package emu.grasscutter.data.excels;

import com.google.gson.annotations.SerializedName;
import emu.grasscutter.data.GameResource;
import emu.grasscutter.data.ResourceType;
import emu.grasscutter.game.props.ElementType;
import lombok.Getter;

@ResourceType(name = "WorldAreaConfigData.json")
public class WorldAreaData extends GameResource {
    private int ID;
    @Getter @SerializedName("AreaID1")
    private int areaID1;
    @Getter @SerializedName("AreaID2")
    private int areaID2;
    @Getter @SerializedName("SceneID")
    private int sceneID;
    @Getter
    private ElementType elementType;

    @Override
    public int getId() {
        return (this.areaID2 << 16) + this.areaID1;
    }
}
