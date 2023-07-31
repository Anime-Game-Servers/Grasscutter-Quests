package emu.grasscutter.data.excels;

import java.util.Calendar;

import emu.grasscutter.data.GameResource;
import emu.grasscutter.data.ResourceType;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;

@ResourceType(name = "DailyDungeonConfigData.json")
public class DailyDungeonData extends GameResource {
    @Getter(onMethod = @__(@Override))
    int id;
    int[] monday;
    int[] tuesday;
    int[] wednesday;
    int[] thursday;
    int[] friday;
    int[] saturday;
    int[] sunday;

    private static final int[] empty = new int[0];
    private final Int2ObjectMap<int[]> map;

    public DailyDungeonData() {
        this.map = new Int2ObjectOpenHashMap<>();
    }

    public int[] getDungeonsByDay(int day) {
        return map.getOrDefault(day, empty);
    }

    @Override
    public void onLoad() {
        map.put(Calendar.MONDAY, monday);
        map.put(Calendar.TUESDAY, tuesday);
        map.put(Calendar.WEDNESDAY, wednesday);
        map.put(Calendar.THURSDAY, thursday);
        map.put(Calendar.FRIDAY, friday);
        map.put(Calendar.SATURDAY, saturday);
        map.put(Calendar.SUNDAY, sunday);
    }
}
