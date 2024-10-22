package emu.grasscutter.data.excels;

import emu.grasscutter.data.GameResource;
import emu.grasscutter.data.ResourceType;
import lombok.Getter;
import java.util.List;

@ResourceType(name = "CoopPointExcelConfigData.json")
@Getter
public class CoopPointData extends GameResource {
	@Getter(onMethod = @__(@Override))
	private int id;
	private int chapterId;
	private String type;
	private int acceptQuest;
	private List<Integer> postPointList;
	private int pointPosId;
}
