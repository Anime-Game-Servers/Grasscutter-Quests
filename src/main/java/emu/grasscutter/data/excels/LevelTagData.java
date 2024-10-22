package emu.grasscutter.data.excels;

import com.google.gson.annotations.SerializedName;
import emu.grasscutter.data.GameResource;
import emu.grasscutter.data.ResourceType;
import lombok.Getter;

@ResourceType(name = "LevelTagExcelConfigData.json")
@Getter
public class LevelTagData extends GameResource {
    @Getter(onMethod = @__(@Override)) @SerializedName("ID")
    private int id;
    private String levelTagName;
    private int sceneId;
    private int[] addSceneTagIdList;
    private int[] removeSceneTagIdList;
    private boolean levelTagCanFixTime;
    private float levelTagFixedEnviroTime;
    private int[] loadDynamicGroupList;
}
