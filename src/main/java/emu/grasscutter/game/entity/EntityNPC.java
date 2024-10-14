package emu.grasscutter.game.entity;

import emu.grasscutter.game.props.EntityIdType;
import emu.grasscutter.game.world.Scene;
import emu.grasscutter.utils.Position;
import it.unimi.dsi.fastutil.ints.Int2FloatMap;
import lombok.Getter;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.general.Vector;
import org.anime_game_servers.multi_proto.gi.messages.general.ability.AbilitySyncStateInfo;
import org.anime_game_servers.multi_proto.gi.messages.scene.entity.*;
import org.anime_game_servers.gi_lua.models.scene.group.SceneNPC;

import java.util.List;

public class EntityNPC extends GameEntity {
    @Getter(onMethod = @__(@Override))
    private final Position position;
    @Getter(onMethod = @__(@Override))
    private final Position rotation;
    private final SceneNPC metaNpc;
    @Getter private final int suiteId;

    public EntityNPC(Scene scene, SceneNPC metaNPC, int blockId, int suiteId) {
        super(scene);
        this.id = getScene().getWorld().getNextEntityId(EntityIdType.NPC);
        setConfigId(metaNPC.getConfigId());
        setGroupId(metaNPC.getGroupId());
        setBlockId(blockId);
        this.suiteId = suiteId;
        this.position = new Position(metaNPC.getPos());
        this.rotation = new Position(metaNPC.getRot());
        this.metaNpc = metaNPC;

    }

    @Override
    public int getEntityTypeId() {
        return metaNpc.getNpc_id();
    }

    @Override
    public Int2FloatMap getFightProperties() {
        return null;
    }

    @Override
    public SceneEntityInfo toProto() {
        val protoBornPos = getPosition().toProto();
        val protoPos = getPosition().toProto();
        val protoRot = getRotation().toProto();
        val aiInfo = new SceneEntityAiInfo(true, protoBornPos);
        val authority = new EntityAuthorityInfo(new AbilitySyncStateInfo(), new EntityRendererChangedInfo(), aiInfo, protoBornPos);

        val entityInfo = new SceneEntityInfo(ProtEntityType.PROT_ENTITY_NPC, getId());
        entityInfo.setMotionInfo(new MotionInfo(protoPos, protoRot, new Vector()));
        entityInfo.setAnimatorParaList(List.of(new AnimatorParameterValueInfoPair()));
        entityInfo.setEntityClientData(new EntityClientData());
        entityInfo.setEntityAuthorityInfo(authority);
        entityInfo.setLifeState(1);


        val npc = new SceneNpcInfo(metaNpc.getNpc_id());
        npc.setBlockId(getBlockId());
        entityInfo.setEntity(new SceneEntityInfo.Entity.Npc(npc));

        return entityInfo;
    }
}
