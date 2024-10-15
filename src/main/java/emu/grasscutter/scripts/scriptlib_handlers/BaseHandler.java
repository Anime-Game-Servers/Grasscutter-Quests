package emu.grasscutter.scripts.scriptlib_handlers;

import emu.grasscutter.Loggers;
import emu.grasscutter.scripts.lua_engine.GroupEventLuaContext;
import lombok.Getter;
import lombok.val;
import org.anime_game_servers.gi_lua.models.scene.group.SceneGroup;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.Set;

import static java.lang.StackWalker.Option.RETAIN_CLASS_REFERENCE;

public class BaseHandler {
    @Getter
    private static final Logger logger = Loggers.getScriptSystem();

    protected int handleUnimplemented(Object... args){
        val methodName = StackWalker.getInstance(Set.of(RETAIN_CLASS_REFERENCE)).walk(s -> s.limit(2).toList().get(1)).getMethodName();
        logger.warn("[LUA] Call unimplemented {} with {}", methodName, Arrays.toString(args));
        return 0;
    }

    protected SceneGroup getGroupOrCurrent(GroupEventLuaContext context, int groupId){
        return groupId!=0 ? context.getSceneScriptManager().getGroupById(groupId) : context.getCurrentGroup();
    }
    protected int getGroupIdOrCurrentId(GroupEventLuaContext context, int groupId){
        return groupId!=0 ? groupId : context.getCurrentGroup().getGroupInfo().getId();
    }
}
