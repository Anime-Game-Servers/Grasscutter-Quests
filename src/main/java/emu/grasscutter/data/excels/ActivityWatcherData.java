package emu.grasscutter.data.excels;

import emu.grasscutter.data.GameResource;
import emu.grasscutter.data.ResourceType;
import emu.grasscutter.game.props.WatcherTriggerType;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Deprecated
@ResourceType(name = "NewActivityWatcherConfigData.json", loadPriority = ResourceType.LoadPriority.HIGH)
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ActivityWatcherData extends GameResource {
    @Getter(onMethod = @__(@Override))
    int id;
    int rewardID;
    int progress;
    WatcherTrigger triggerConfig;

    @Override
    public void onLoad() {
        triggerConfig.onLoad();
    }

    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class WatcherTrigger{
        String triggerType;
        List<String> paramList;

        transient WatcherTriggerType watcherTriggerType;

        public void onLoad(){
            paramList = paramList.stream().filter(x -> (x != null) && !x.isBlank()).toList();
            watcherTriggerType = WatcherTriggerType.getTypeByName(triggerType);
        }
    }

}
