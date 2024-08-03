package emu.grasscutter.game;

import emu.grasscutter.Grasscutter;
import org.anime_game_servers.game_data_models.data.general.LogicType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.function.BooleanSupplier;

public class LogicTypeUtils {
    public static boolean calculate(LogicType logicType, int[] progress) {
        if (progress.length == 0) {
            return true;
        }

        if (logicType == null) {
            return progress[0] == 1;
        }

        switch (logicType) {
            case LOGIC_AND -> {
                return Arrays.stream(progress).allMatch(i -> i == 1);
            }
            case LOGIC_OR -> {
                return Arrays.stream(progress).anyMatch(i -> i == 1);
            }
            case LOGIC_NOT -> {
                return Arrays.stream(progress).noneMatch(i -> i == 1);
            }
            case LOGIC_A_AND_ETCOR -> {
                return progress[0] == 1 && Arrays.stream(progress).skip(1).anyMatch(i -> i == 1);
            }
            case LOGIC_A_AND_B_AND_ETCOR -> {
                return progress[0] == 1 && progress[1] == 1 && Arrays.stream(progress).skip(2).anyMatch(i -> i == 1);
            }
            case LOGIC_A_OR_ETCAND -> {
                return progress[0] == 1 || Arrays.stream(progress).skip(1).allMatch(i -> i == 1);
            }
            case LOGIC_A_OR_B_OR_ETCAND -> {
                return progress[0] == 1 || progress[1] == 1 || Arrays.stream(progress).skip(2).allMatch(i -> i == 1);
            }
            case LOGIC_A_AND_B_OR_ETCAND -> {
                return progress[0] == 1 && progress[1] == 1 || Arrays.stream(progress).skip(2).allMatch(i -> i == 1);
            }
            default -> {
                return Arrays.stream(progress).anyMatch(i -> i == 1);
            }
        }
    }

    /**
     * Apply logic type to all predicates
     *
     * @param logicType  type of logic that should be applied to predicates
     * @param predicates list of predicates for which logicType will be applied
     * @return result of applying logicType to predicates
     */
    public static boolean calculate(@NotNull LogicType logicType, List<BooleanSupplier> predicates) {
        switch (logicType) {
            case LOGIC_AND -> {
                return predicates.stream().allMatch(BooleanSupplier::getAsBoolean);
            }
            case LOGIC_OR -> {
                return predicates.stream().anyMatch(BooleanSupplier::getAsBoolean);
            }
            default -> {
                Grasscutter.getLogger().error("Unimplemented logic operation was called");
                return false;
            }
        }
    }
}
