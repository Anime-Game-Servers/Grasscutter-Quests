package emu.grasscutter.game.activity.condition;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.data.excels.ActivityCondExcelConfigData;
import emu.grasscutter.game.activity.ActivityConfigItem;
import emu.grasscutter.game.activity.PlayerActivityData;
import emu.grasscutter.game.activity.condition.all.UnknownActivityConditionHandler;
import emu.grasscutter.game.quest.enums.LogicType;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;

import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;

import static emu.grasscutter.Grasscutter.getLogger;

public class BasicActivityConditionExecutor implements ActivityConditionExecutor {

    private final Map<Integer, ActivityConfigItem> activityConfigItemMap;
    private final Int2ObjectMap<ActivityCondExcelConfigData> activityConditions;

    private final Int2ObjectMap<PlayerActivityData> playerActivityDataByActivityCondId;
    private final Map<ActivityConditions, ActivityConditionBaseHandler> activityConditionsHandlers;

    private static final UnknownActivityConditionHandler UNKNOWN_CONDITION_HANDLER = new UnknownActivityConditionHandler();

    public BasicActivityConditionExecutor(Map<Integer, ActivityConfigItem> activityConfigItemMap,
                                          Int2ObjectMap<ActivityCondExcelConfigData> activityConditions,
                                          Int2ObjectMap<PlayerActivityData> playerActivityDataByActivityCondId,
                                          Map<ActivityConditions, ActivityConditionBaseHandler> activityConditionsHandlers) {
        this.activityConfigItemMap = activityConfigItemMap;
        this.activityConditions = activityConditions;
        this.playerActivityDataByActivityCondId = playerActivityDataByActivityCondId;
        this.activityConditionsHandlers = activityConditionsHandlers;
    }

    @Override
    public List<Integer> getMeetActivitiesConditions(List<Integer> condIds) {
        return condIds.stream()
            .filter(this::meetsCondition)
            .collect(Collectors.toList());
    }

    @Override
    public boolean meetsCondition(int activityCondId) {
        ActivityCondExcelConfigData condData = activityConditions.get(activityCondId);

        if (condData == null) {
            getLogger().error("Could not find condition for activity with id = {}", activityCondId);
            return false;
        }

        LogicType condComb = condData.getCondComb();
        if (condComb == null) {
            condComb = LogicType.LOGIC_AND;
        }

        PlayerActivityData activity = playerActivityDataByActivityCondId.get(activityCondId);
        ActivityConfigItem activityConfig = activityConfigItemMap.get(activity.getActivityId());
        List<BooleanSupplier> predicates = condData.getCond()
            .stream()
            .map(c -> (BooleanSupplier) () ->
                activityConditionsHandlers
                    .getOrDefault(c.getType(), UNKNOWN_CONDITION_HANDLER).execute(activity, activityConfig, c.paramArray()))
            .collect(Collectors.toList());

        return LogicType.calculate(condComb, predicates);
    }
}
