package emu.grasscutter.database.codec;

import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.scene.map.MapMarkPointType;
import org.bson.codecs.Codec;

import java.util.Map;

public class CodecProvider implements org.bson.codecs.configuration.CodecProvider {
    Map<Class<?>, Codec<?>> codecMap;

    public CodecProvider(){
        codecMap = new java.util.HashMap<>();
        codecMap.put(MapMarkPointType.class, new MapMarkPointCodec());
    }

    public <T> org.bson.codecs.Codec<T> get(Class<T> clazz, org.bson.codecs.configuration.CodecRegistry registry) {
        val codec = codecMap.get(clazz);
        return (Codec<T>) codec;
    }
}
