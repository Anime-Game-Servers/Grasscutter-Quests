package emu.grasscutter.game.ability;

import java.util.HashMap;
import java.util.Map;

import emu.grasscutter.data.GameData;
import emu.grasscutter.data.binout.AbilityData;
import emu.grasscutter.game.entity.GameEntity;
import emu.grasscutter.game.player.Player;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import lombok.Getter;
import org.anime_game_servers.multi_proto.gi.messages.general.ability.AbilityString;

import javax.annotation.Nullable;

public class Ability {
    @Getter private AbilityData data;
    @Getter private GameEntity owner;
    @Getter private Player playerOwner;

    @Getter private AbilityManager manager;

    @Getter private Map<String, AbilityModifierController> modifiers = new HashMap<>();
    @Getter private Object2FloatMap<String> abilitySpecials = new Object2FloatOpenHashMap<String>();

    @Getter private static Map<String, Object2FloatMap<String>> abilitySpecialsModified = new HashMap<String, Object2FloatMap<String>>();

    @Getter private int hash;

    public Ability(AbilityData data, GameEntity owner, Player playerOwner) {
        this.data = data;
        this.owner = owner;
        this.manager = owner.getWorld().getHost().getAbilityManager();
        if(this.data.abilitySpecials != null)
            for(var entry : this.data.abilitySpecials.entrySet())
                abilitySpecials.put(entry.getKey(), entry.getValue().floatValue());
        //if(abilitySpecialsModified.containsKey(this.data.abilityName)) {//Modify talent data
        //    abilitySpecials.putAll(abilitySpecialsModified.get(this.data.abilityName));
        //}

        this.playerOwner = playerOwner;

        hash = AbilityHash(data.abilityName);

        data.initialize();
    }

    public static int AbilityHash(String str)
    {
        long hash = 0;
        char[] asCharArray = str.toCharArray();
        for (int i = 0; i < str.length(); i++)
        {
            hash = ((asCharArray[i] + 131 * hash) & 0xFFFFFFFF);
        }
        return (int)hash;
    }

    @Nullable
    public static String getAbilityName(AbilityString abString) {
        if(abString.getType() instanceof AbilityString.Type.Str abStr){
            return abStr.getValue();
        }if(abString.getType() instanceof AbilityString.Type.Hash abHash){
            return GameData.getAbilityHashes().get((int)abHash.getValue());
        }
        return null;
    }
}
