package emu.grasscutter.data.excels;

import com.google.gson.annotations.SerializedName;
import emu.grasscutter.data.GameResource;
import emu.grasscutter.data.ResourceType;
import lombok.Getter;

@ResourceType(name = "LevelTagGroupsExcelConfigData.json")
@Getter
public class LevelTagGroupsData extends GameResource {
    @Getter(onMethod = @__(@Override)) @SerializedName("ID")
    private int id;
    private LevelTagGroup[] levelTagGroupList;
    private int[] initialLevelTagIdList;
    private int changeCd;

    @Getter
    public static class LevelTagGroup {
        private int[] levelTagIdList;
    }
}
