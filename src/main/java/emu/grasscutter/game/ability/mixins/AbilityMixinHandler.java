package emu.grasscutter.game.ability.mixins;

import emu.grasscutter.data.binout.AbilityMixinData;
import emu.grasscutter.game.ability.Ability;

public abstract class AbilityMixinHandler {

    public abstract boolean execute(Ability ability, AbilityMixinData mixinData, byte[] abilityData);

}
