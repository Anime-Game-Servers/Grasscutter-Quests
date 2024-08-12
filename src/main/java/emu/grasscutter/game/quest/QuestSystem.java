package emu.grasscutter.game.quest;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.Loggers;
import emu.grasscutter.data.common.quest.SubQuestData.*;
import emu.grasscutter.data.common.quest.SubQuestData;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.quest.conditions.BaseCondition;
import emu.grasscutter.game.quest.content.BaseContent;
import emu.grasscutter.game.quest.enums.LogicType;
import emu.grasscutter.game.quest.enums.QuestContent;
import emu.grasscutter.game.quest.handlers.QuestExecHandler;
import emu.grasscutter.server.game.BaseGameSystem;
import emu.grasscutter.server.game.GameServer;
import emu.grasscutter.server.packet.send.PacketQuestProgressUpdateNotify;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;
import lombok.val;
import org.slf4j.Logger;

import java.util.List;

public class QuestSystem extends BaseGameSystem {
    @Getter
    private static final Logger logger = Loggers.getQuestSystem();
    private final Int2ObjectMap<BaseCondition> condHandlers;
    private final Int2ObjectMap<BaseContent> contHandlers;
    private final Int2ObjectMap<QuestExecHandler> execHandlers;

    public QuestSystem(GameServer server) {
        super(server);

        this.condHandlers = new Int2ObjectOpenHashMap<>();
        this.contHandlers = new Int2ObjectOpenHashMap<>();
        this.execHandlers = new Int2ObjectOpenHashMap<>();

        this.registerHandlers();
    }

    public void registerHandlers() {
        this.registerHandlers(this.condHandlers, "emu.grasscutter.game.quest.conditions", BaseCondition.class);
        this.registerHandlers(this.contHandlers, "emu.grasscutter.game.quest.content", BaseContent.class);
        this.registerHandlers(this.execHandlers, "emu.grasscutter.game.quest.exec", QuestExecHandler.class);
    }

    public <T> void registerHandlers(Int2ObjectMap<T> map, String packageName, Class<T> clazz) {
        var handlerClasses = Grasscutter.reflector.getSubTypesOf(clazz);

        for (var obj : handlerClasses) {
            this.registerPacketHandler(map, obj);
        }
    }

    public <T> void registerPacketHandler(Int2ObjectMap<T> map, Class<? extends T> handlerClass) {
        try {
            int value;
            if (handlerClass.isAnnotationPresent(QuestValueExec.class)) {
                QuestValueExec opcode = handlerClass.getAnnotation(QuestValueExec.class);
                value = opcode.value().getValue();
            } else if (handlerClass.isAnnotationPresent(QuestValueContent.class)) {
                QuestValueContent opcode = handlerClass.getAnnotation(QuestValueContent.class);
                value = opcode.value().getValue();
            } else if (handlerClass.isAnnotationPresent(QuestValueCond.class)) {
                QuestValueCond opcode = handlerClass.getAnnotation(QuestValueCond.class);
                value = opcode.value().getValue();
            } else {
                return;
            }

            if (value <= 0) {
                return;
            }

            map.put(value, handlerClass.getDeclaredConstructor().newInstance());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // TODO make cleaner

    public boolean triggerCondition(Player owner, SubQuestData questData, QuestAcceptCondition condition, String paramStr, int... params) {
        BaseCondition handler = condHandlers.get(condition.getType().getValue());

        if (handler == null || questData == null) {
            getLogger().debug("Could not trigger condition {} at {}", condition.getType().getValue(), questData);
            return false;
        }

        return handler.execute(owner, questData, condition, paramStr, params);
    }

    private BaseContent getContentHandler(QuestContent type, SubQuestData questData){
        val handler = contHandlers.get(type.getValue());
        if(handler == null){
            getLogger().debug("Could not get handler for content {} in quest {}", type.getValue(), questData);
            return contHandlers.get(QuestContent.QUEST_CONTENT_UNKNOWN.getValue());
        }
        return handler;
    }

    public boolean initialCheckContent(GameQuest quest, int[] curProgress,
                                       List<QuestContentCondition> conditions, LogicType logicType, boolean shouldReset) {
        val owner = quest.getOwner();
        var changed = false;
        int[] finished = new int[conditions.size()];
        for (int i = 0; i < conditions.size(); i++) {
            val condition = conditions.get(i);

            BaseContent handler = getContentHandler(condition.getType(), quest.getQuestData());

            val startingProgress = curProgress[i];
            int result = handler.initialCheck(quest, quest.getQuestData(), condition);
            if (shouldReset) curProgress[i] = result;
            if (startingProgress != result) {
                changed = true;
            }
            finished[i] = handler.checkProgress(quest, condition, result) ? 1 : 0;
        }
        if (changed) {
            owner.getSession().send(new PacketQuestProgressUpdateNotify(quest));
        }
        return LogicType.calculate(logicType, finished);
    }

    public boolean checkAndUpdateContent(GameQuest quest, int[] curProgress,
                                          List<QuestContentCondition> conditions, LogicType logicType,
                                          QuestContent condType, String paramStr, int... params){
        val owner = quest.getOwner();
        var changed = false;
        int[] finished = new int[conditions.size()];
        for (int i = 0; i < conditions.size(); i++) {
            val condition = conditions.get(i);

            BaseContent handler = getContentHandler(condition.getType(), quest.getQuestData());
            // only update progress if its actually affected by the current event
            if(handler.isEvent(quest.getQuestData(), condition, condType, paramStr, params)){
                val startingProgress = curProgress[i];
                int result = handler.updateProgress(quest, startingProgress, condition, paramStr, params);
                curProgress[i] = result;
                if (startingProgress != result) {
                    changed = true;
                }
            }
            finished[i] = handler.checkProgress(quest, condition, curProgress[i]) ? 1 : 0;
        }
        if(changed) {
            owner.getSession().send(new PacketQuestProgressUpdateNotify(quest));
            quest.save();
        }
        return LogicType.calculate(logicType, finished);
    }

    public void triggerExec(GameQuest quest, QuestExecParam execParam, String... params) {
        if(execParam.getType() == null) {
            getLogger().error("execParam.getType() is null {}", quest.getSubQuestId());
            return;
        }
        QuestExecHandler handler = execHandlers.get(execParam.getType().getValue());

        if (handler == null || quest.getQuestData() == null) {
            getLogger().debug("Could not trigger exec {} at {}", execParam.getType().getValue(), quest.getQuestData());
            return;
        }

        QuestManager.eventExecutor.submit(() -> {
            if (!handler.execute(quest, execParam, params)) {
                getLogger().debug("exec trigger failed {} at {}", execParam.getType().getValue(), quest.getQuestData());
            }
        });
    }
}
