package emu.grasscutter.database.codec;

import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.scene.map.MapMarkPointType;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class MapMarkPointCodec implements Codec<MapMarkPointType> {
    @Override
    public void encode(BsonWriter bsonWriter, MapMarkPointType mapMarkPointType, EncoderContext encoderContext) {
        bsonWriter.writeString(mapMarkPointType.name());
    }

    @Override
    public MapMarkPointType decode(BsonReader bsonReader, DecoderContext decoderContext) {
        try {
            return MapMarkPointType.valueOf(bsonReader.readString().replace("MAP_MARK_POINT_TYPE_", ""));
        } catch (IllegalArgumentException e) {
            return MapMarkPointType.UNRECOGNISED;
        }
    }

    @Override
    public Class<MapMarkPointType> getEncoderClass() {
        return MapMarkPointType.class;
    }
}
