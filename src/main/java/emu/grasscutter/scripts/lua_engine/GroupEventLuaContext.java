package emu.grasscutter.scripts.lua_engine;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.scripts.SceneScriptManager;
import lombok.Getter;
import org.anime_game_servers.gi_lua.models.ScriptArgs;
import org.anime_game_servers.gi_lua.models.scene.group.SceneGroup;
import org.anime_game_servers.gi_lua.script_lib.ScriptLibGroupHandlerProvider;
import org.anime_game_servers.gi_lua.script_lib.ScriptLibHandler;
import org.anime_game_servers.lua.engine.LuaEngine;
import org.jetbrains.annotations.NotNull;

public class GroupEventLuaContext implements org.anime_game_servers.gi_lua.script_lib.GroupEventLuaContext {
    @Getter
    final private SceneGroup groupInstance;
    @Getter
    final private ScriptArgs args;

    public int target_entity_id;
    public int source_entity_id;

    final private SceneScriptManager scriptManager;

    @Getter(onMethod = @__(@Override))
    final private LuaEngine engine;

    public GroupEventLuaContext(LuaEngine engine, SceneGroup groupInstance, ScriptArgs args, SceneScriptManager scriptManager) {
        this.groupInstance = groupInstance;
        this.args = args;
        this.scriptManager = scriptManager;
        this.engine = engine;
        this.target_entity_id = args.getTargetEntityId();
        this.source_entity_id = args.getSourceEntityId();
    }

    public SceneGroup getCurrentGroup() {
        return groupInstance;
    }

    public SceneScriptManager getSceneScriptManager() {
        return scriptManager;
    }

    @Override
    public ScriptLibHandler getScriptLibHandler() {
        return Grasscutter.getGameServer().getScriptSystem().getScriptLibHandler();
    }

    @NotNull
    @Override
    public ScriptLibGroupHandlerProvider getScriptLibHandlerProvider() {
        return Grasscutter.getGameServer().getScriptSystem().getScriptLibGroupHandlerProvider();
    }

    @Override
    public int getSourceEntityId() {
        return args.getSourceEntityId();
    }

    @Override
    public int getTargetEntityId() {
        return args.getTargetEntityId();
    }


    @Override
    public int getUid() {
        return args.getUid();
    }

    /*@Override
    public int source_entity_id() {
        return args.source_eid;
    }

    @Override
    public int target_entity_id() {
        return args.target_eid;
    }

    @Override
    public int uid() {
        return args.uid;
    }*/
}
