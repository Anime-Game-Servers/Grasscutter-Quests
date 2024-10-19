package emu.grasscutter.data.excels;

import emu.grasscutter.data.GameData;
import emu.grasscutter.data.GameResource;
import emu.grasscutter.data.ResourceType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.anime_game_servers.game_data_models.data.city.CityData;

import javax.annotation.Nullable;
import java.util.List;

@ResourceType(name = "InvestigationMonsterConfigData.json", loadPriority = ResourceType.LoadPriority.LOW)
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InvestigationMonsterData extends GameResource {
    @Getter(onMethod = @__(@Override))
    int id;
    int cityId;
    List<Integer> monsterIdList;
    List<Integer> groupIdList;
    int rewardPreviewId;
    String mapMarkCreateType;
    String monsterCategory;

    @Nullable
    public CityData getCityData(){
        return GameData.getCityDataMap().get(cityId);
    }
}
