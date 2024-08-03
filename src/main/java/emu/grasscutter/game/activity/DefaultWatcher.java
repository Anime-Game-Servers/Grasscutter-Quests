package emu.grasscutter.game.activity;

import static org.anime_game_servers.game_data_models.data.watcher.WatcherTriggerType.TRIGGER_NONE;

@ActivityWatcherType(TRIGGER_NONE)
public class DefaultWatcher extends ActivityWatcher{
    @Override
    protected boolean isMeet(String... param) {
        return false;
    }
}
