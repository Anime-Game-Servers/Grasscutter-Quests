package emu.grasscutter.server.packet.recv;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.game.entity.GameEntity;
import emu.grasscutter.game.player.Player;
import emu.grasscutter.game.props.FightProperty;
import emu.grasscutter.net.packet.TypedPacketHandler;
import emu.grasscutter.server.event.entity.EntityMoveEvent;
import emu.grasscutter.server.game.GameSession;
import emu.grasscutter.server.packet.send.PacketEntityFightPropUpdateNotify;
import emu.grasscutter.utils.Position;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.battle.CombatInvocationsNotify;
import org.anime_game_servers.multi_proto.gi.messages.battle.CombatInvokeEntry;
import org.anime_game_servers.multi_proto.gi.messages.battle.event.EvtAnimatorParameterInfo;
import org.anime_game_servers.multi_proto.gi.messages.battle.event.EvtBeingHitInfo;
import org.anime_game_servers.multi_proto.gi.messages.general.PlayerDieType;
import org.anime_game_servers.multi_proto.gi.messages.general.entity.EntityMoveInfo;
import org.anime_game_servers.multi_proto.gi.messages.scene.entity.MotionState;

public class HandlerCombatInvocationsNotify extends TypedPacketHandler<CombatInvocationsNotify> {

    private float cachedLandingSpeed = 0;
    private long cachedLandingTimeMillisecond = 0;
    private boolean monitorLandingEvent = false;

    @Override
    public void handle(GameSession session, byte[] header, CombatInvocationsNotify notif) throws Exception {
        for (CombatInvokeEntry entry : notif.getInvokeList()) {
            // Handle combat invoke
            switch (entry.getArgumentType()) {
                case COMBAT_EVT_BEING_HIT -> {
                    val hitInfo = EvtBeingHitInfo.parseBy(entry.getCombatData(), session.getVersion());
                    val attackResult = hitInfo.getAttackResult();
                    Player player = session.getPlayer();

                    // Check if the player is invulnerable.
                    if (
                        attackResult.getAttackerId() != player.getTeamManager().getCurrentAvatarEntity().getId() &&
                            player.getAbilityManager().isAbilityInvulnerable()
                    ) break;

                    // Handle damage
                    player.getAttackResults().add(attackResult);
                    player.getEnergyManager().handleAttackHit(hitInfo);
                }
                case ENTITY_MOVE -> {
                    // Handle movement
                    val moveInfo = EntityMoveInfo.parseBy(entry.getCombatData(), session.getVersion());
                    val scene = session.getPlayer().getScene();
                    GameEntity entity = scene != null ? scene.getEntityById(moveInfo.getEntityId()) : null;
                    if (entity != null) {
                        // Move player
                        val motionInfo = moveInfo.getMotionInfo();
                        val motionState = motionInfo.getState();

                        val position = motionInfo.getPos() != null ? new Position(motionInfo.getPos()) : null;
                        val rotation = motionInfo.getRot() != null ? new Position(motionInfo.getRot()) : null;
                        val speed = motionInfo.getSpeed() != null ? new Position(motionInfo.getSpeed()) : null;

                        // Call entity move event.
                        EntityMoveEvent event = new EntityMoveEvent(entity, position, rotation, speed, motionState);
                        event.call();

                        if(position != null) {
                            entity.move(event.getPosition(), event.getRotation());
                        }
                        entity.setLastMoveSceneTimeMs(moveInfo.getSceneTime());
                        entity.setLastMoveReliableSeq(moveInfo.getReliableSeq());
                        if(motionState!= null)
                            entity.setMotionState(motionState);

                        session.getPlayer().getStaminaManager().handleCombatInvocationsNotify(session, moveInfo, entity);

                        // TODO: handle MOTION_FIGHT landing which has a different damage factor
                        // 		Also, for plunge attacks, LAND_SPEED is always -30 and is not useful.
                        //  	May need the height when starting plunge attack.

                        // MOTION_LAND_SPEED and MOTION_FALL_ON_GROUND arrive in different packets.
                        // Cache land speed for later use.
                        if (motionState == MotionState.MOTION_LAND_SPEED) {
                            cachedLandingSpeed = motionInfo.getSpeed().getY();
                            cachedLandingTimeMillisecond = System.currentTimeMillis();
                            monitorLandingEvent = true;
                        }
                        if (monitorLandingEvent) {
                            if (motionState == MotionState.MOTION_FALL_ON_GROUND) {
                                monitorLandingEvent = false;
                                handleFallOnGround(session, entity, motionState);
                            }
                        }

                        // MOTION_STATE_NOTIFY = Dont send to other players
                        if (motionState == MotionState.MOTION_NOTIFY) {
                            continue;
                        }
                    }
                }
                case COMBAT_ANIMATOR_PARAMETER_CHANGED -> {
                    val paramInfo = EvtAnimatorParameterInfo.parseBy(entry.getCombatData(), session.getVersion());
                    if (paramInfo.isServerCache()) {
                        paramInfo.setServerCache(false);
                        entry.setCombatData(paramInfo.encodeToByteArray(session.getVersion()));
                    }
                }
                default -> {
                    Grasscutter.getLogger().debug("Unhandled type: " + entry.getArgumentType().name());
                }
            }

            session.getPlayer().getCombatInvokeHandler().addEntry(entry.getForwardType(), entry);
        }
    }

    private void handleFallOnGround(GameSession session, GameEntity entity, MotionState motionState) {
        if (session.getPlayer().inGodmode()) {
            return;
        }
        // People have reported that after plunge attack (client sends a FIGHT instead of FALL_ON_GROUND) they will die
        // 		if they talk to an NPC (this is when the client sends a FALL_ON_GROUND) without jumping again.
        // A dirty patch: if not received immediately after MOTION_LAND_SPEED, discard this packet.
        // 200ms seems to be a reasonable delay.
        int maxDelay = 200;
        long actualDelay = System.currentTimeMillis() - cachedLandingTimeMillisecond;
        Grasscutter.getLogger().trace("MOTION_FALL_ON_GROUND received after " + actualDelay + "/" + maxDelay + "ms." + (actualDelay > maxDelay ? " Discard" : ""));
        if (actualDelay > maxDelay) {
            return;
        }
        float currentHP = entity.getFightProperty(FightProperty.FIGHT_PROP_CUR_HP);
        float maxHP = entity.getFightProperty(FightProperty.FIGHT_PROP_MAX_HP);
        float damageFactor = 0;
        if (cachedLandingSpeed < -23.5) {
            damageFactor = 0.33f;
        }
        if (cachedLandingSpeed < -25) {
            damageFactor = 0.5f;
        }
        if (cachedLandingSpeed < -26.5) {
            damageFactor = 0.66f;
        }
        if (cachedLandingSpeed < -28) {
            damageFactor = 1f;
        }
        float damage = maxHP * damageFactor;
        float newHP = currentHP - damage;
        if (newHP < 0) {
            newHP = 0;
        }
        if (damageFactor > 0) {
            Grasscutter.getLogger().debug(currentHP + "/" + maxHP + "\tLandingSpeed: " + cachedLandingSpeed +
                    "\tDamageFactor: " + damageFactor + "\tDamage: " + damage + "\tNewHP: " + newHP);
        } else {
            Grasscutter.getLogger().trace(currentHP + "/" + maxHP + "\tLandingSpeed: 0\tNo damage");
        }
        entity.setFightProperty(FightProperty.FIGHT_PROP_CUR_HP, newHP);
        entity.getWorld().broadcastPacket(new PacketEntityFightPropUpdateNotify(entity, FightProperty.FIGHT_PROP_CUR_HP));
        if (newHP == 0) {
            session.getPlayer().getStaminaManager().killAvatar(session, entity, PlayerDieType.PLAYER_DIE_FALL);
        }
        cachedLandingSpeed = 0;
    }
}
