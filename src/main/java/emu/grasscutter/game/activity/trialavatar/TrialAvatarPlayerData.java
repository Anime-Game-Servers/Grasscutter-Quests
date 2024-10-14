package emu.grasscutter.game.activity.trialavatar;

import emu.grasscutter.data.GameData;
import emu.grasscutter.data.common.BaseTrialActivityData;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.activity.trial.TrialAvatarActivityDetailInfo;
import org.anime_game_servers.multi_proto.gi.messages.activity.trial.TrialAvatarActivityRewardDetailInfo;

import java.util.List;
import java.util.stream.*;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder(builderMethodName = "of")
public class TrialAvatarPlayerData {
    List<RewardInfoItem> rewardInfoList;

    private static BaseTrialActivityData getActivityData(int scheduleId){
        // prefer custom data over official data
        return GameData.getTrialAvatarActivityCustomData().isEmpty() ? GameData.getTrialAvatarActivityDataMap().get(scheduleId)
            : GameData.getTrialAvatarActivityCustomData().get(scheduleId);
    }

    public static List<Integer> getAvatarIdList(int scheduleId) {
        val activityData = getActivityData(scheduleId);
        return activityData != null ? activityData.getAvatarIndexIdList() : List.of();
    }

    public static List<Integer> getRewardIdList(int scheduleId) {
        val activityData = getActivityData(scheduleId);
        return activityData != null ? activityData.getRewardIdList() : List.of();
    }

    public static TrialAvatarPlayerData create(int scheduleId) {
        List<Integer> avatarIds = getAvatarIdList(scheduleId);
        List<Integer> rewardIds = getRewardIdList(scheduleId);
        return TrialAvatarPlayerData.of()
            .rewardInfoList(IntStream.range(0, avatarIds.size())
                .filter(i -> avatarIds.get(i) > 0 && rewardIds.get(i) > 0)
                .mapToObj(i -> RewardInfoItem.create(
                    avatarIds.get(i),
                    rewardIds.get(i)))
                .collect(Collectors.toList()))
            .build();
    }

    public TrialAvatarActivityDetailInfo toProto() {
        return new TrialAvatarActivityDetailInfo(getRewardInfoList().stream()
            .map(RewardInfoItem::toProto)
            .toList());
    }

    public RewardInfoItem getRewardInfo(int trialAvatarIndexId) {
        return getRewardInfoList().stream().filter(x -> x.getTrialAvatarIndexId() == trialAvatarIndexId)
            .findFirst().orElse(null);
    }

    @Data
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Builder(builderMethodName = "of")
    public static class RewardInfoItem {
        int trialAvatarIndexId;
        int rewardId;
        boolean passedDungeon;
        boolean receivedReward;

        public static RewardInfoItem create(int trialAvatarIndexId, int rewardId) {
            return RewardInfoItem.of()
                .trialAvatarIndexId(trialAvatarIndexId)
                .rewardId(rewardId)
                .passedDungeon(false)
                .receivedReward(false)
                .build();
        }

        public TrialAvatarActivityRewardDetailInfo toProto() {
            val proto = new TrialAvatarActivityRewardDetailInfo();
            proto.setTrialAvatarIndexId(getTrialAvatarIndexId());
            proto.setRewardId(getRewardId());
            proto.setPassedDungeon(isPassedDungeon());
            proto.setReceivedReward(isReceivedReward());
            return proto;
        }
    }
}


