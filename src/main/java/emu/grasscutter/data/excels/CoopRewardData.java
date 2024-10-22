package emu.grasscutter.data.excels;

import emu.grasscutter.data.GameResource;
import emu.grasscutter.data.ResourceType;
import lombok.Getter;

@ResourceType(name = "CoopRewardExcelConfigData.json")
@Getter
public class CoopRewardData extends GameResource {
	@Getter(onMethod = @__(@Override))
	private int id;
	private int chapterId;
}
