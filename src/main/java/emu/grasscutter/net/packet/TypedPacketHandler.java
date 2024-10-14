package emu.grasscutter.net.packet;

import emu.grasscutter.server.game.GameSession;
import lombok.val;
import org.anime_game_servers.core.base.Version;
import org.anime_game_servers.multi_proto.core.interfaces.ProtoModel;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class TypedPacketHandler<T extends ProtoModel> extends PacketHandler {
    final private Method parseMethod;

    @Nullable
    public static Class<?> getStaticClass(Class<? extends TypedPacketHandler> handlerClass) {
        if(!TypedPacketHandler.class.isAssignableFrom(handlerClass)){
            return null;
        }
        Type superClassType = handlerClass.getGenericSuperclass();
        if(superClassType == null || !(superClassType instanceof ParameterizedType)){
            return null;
        }
        ParameterizedType superClass = (ParameterizedType) superClassType;

        if(superClass.getActualTypeArguments().length == 0){
            return null;
        }
        return (Class<?>) superClass.getActualTypeArguments()[0];
    }

    public TypedPacketHandler() {
        Class<?> modelClass = getStaticClass(getClass());
        if(modelClass == null){
            throw new RuntimeException("Could not find model class for " + getClass().getName());
        }
        try {
            parseMethod = modelClass.getMethod("parseBy", byte[].class, Version.class);
            if(!parseMethod.getReturnType().isAssignableFrom(modelClass))
                throw new RuntimeException("parseBy method does not return " + modelClass.getName());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void handle(GameSession session, byte[] header, byte[] payload) throws Exception {
        val model = (T) parseMethod.invoke(null, payload, session.getVersion());
        handle(session, header, model);
    }

    public abstract void handle(GameSession session, byte[] header, T payload) throws Exception;
}
