package emu.grasscutter.game.dungeons.challenge.factory;

import emu.grasscutter.game.dungeons.challenge.WorldChallenge;
import emu.grasscutter.game.dungeons.challenge.enums.ChallengeType;
import emu.grasscutter.game.dungeons.challenge.trigger.DamageMonsterOrShieldCountTrigger;
import emu.grasscutter.game.world.Scene;
import emu.grasscutter.scripts.data.SceneGroup;

import java.util.List;

import static emu.grasscutter.game.dungeons.challenge.enums.ChallengeType.CHALLENGE_MONSTER_DAMAGE_COUNT;
import static emu.grasscutter.game.dungeons.challenge.enums.ChallengeType.CHALLENGE_SHEILD_ABSORB_DAMAGE_COUNT;

public class DamageMonsterOrShieldCountChallengeFactoryHandler implements ChallengeFactoryHandler{
    @Override
    public boolean isThisType(ChallengeType challengeType) {
        return challengeType == CHALLENGE_MONSTER_DAMAGE_COUNT
            || challengeType == CHALLENGE_SHEILD_ABSORB_DAMAGE_COUNT;
    }

    /**
     * Build a new challenge
     * @param params: [damageCount, unused1, unused2, unused3, successCount, failCount] or
     *              [absorbDamage, unused1, unused2, unused3, successCount, failCount]
     */
    @Override
    public WorldChallenge build(List<Integer> indices, List<Integer> params, Scene scene, SceneGroup group) {
        return new WorldChallenge(
            scene, group,
            indices,
            List.of(params.get(0)), 0, params.get(0), // parameters, time limit, goal
            List.of(new DamageMonsterOrShieldCountTrigger(1)),
            params.get(params.size() - 2), params.get(params.size() - 1) // success count, fail count
        );
    }
}