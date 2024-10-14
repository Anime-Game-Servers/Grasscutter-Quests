package emu.grasscutter.server.packet.send;

import emu.grasscutter.net.packet.BaseTypedPacket;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.item.cooking.CookRecipeData;
import org.anime_game_servers.multi_proto.gi.messages.item.cooking.CookRecipeDataNotify;

public class PacketCookRecipeDataNotify extends BaseTypedPacket<CookRecipeDataNotify> {
    public PacketCookRecipeDataNotify(CookRecipeData recipe) {
        super(new CookRecipeDataNotify());
        proto.setRecipeData(recipe);
    }

    public PacketCookRecipeDataNotify(int recipeId) {
        this(recipeId, 0);
    }

    public PacketCookRecipeDataNotify(int recipeId, int proficiency) {
        super(new CookRecipeDataNotify());
        val cookRecipeData = new CookRecipeData();
        cookRecipeData.setRecipeId(recipeId);
        cookRecipeData.setProficiency(proficiency);
        proto.setRecipeData(cookRecipeData);
    }
}
