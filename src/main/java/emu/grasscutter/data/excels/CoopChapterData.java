package emu.grasscutter.data.excels;

import com.google.gson.annotations.SerializedName;
import emu.grasscutter.data.GameResource;
import emu.grasscutter.data.ResourceType;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import java.util.List;

@ResourceType(name = "CoopChapterExcelConfigData.json")
@Getter
public class CoopChapterData extends GameResource {
	@Getter(onMethod = @__(@Override))
	private int id;
	private int avatarId;
	List<CoopCondition> unlockCond;
	private int confidenceValue;

	@Data
	@Getter
	public static class CoopCondition {
		@SerializedName(
				value = "_condType",
				alternate = {"condType"})
		private String type = "COOP_COND_NONE";

		@SerializedName(
				value = "_args",
				alternate = {"args"})
		private int[] args;
	}
}
