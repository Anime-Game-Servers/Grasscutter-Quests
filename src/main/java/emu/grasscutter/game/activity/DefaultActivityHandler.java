package emu.grasscutter.game.activity;

import emu.grasscutter.game.props.ActivityType;
import org.anime_game_servers.multi_proto.gi.messages.activity.general.ActivityInfo;

@GameActivity(ActivityType.NONE)
public class DefaultActivityHandler extends ActivityHandler{
    @Override
    public void onProtoBuild(PlayerActivityData playerActivityData, ActivityInfo activityInfo) {

    }

    @Override
    public void onInitPlayerActivityData(PlayerActivityData playerActivityData) {

    }
}
