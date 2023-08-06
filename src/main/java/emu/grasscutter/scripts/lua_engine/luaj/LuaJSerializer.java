package emu.grasscutter.scripts.lua_engine.luaj;

import emu.grasscutter.Grasscutter;
import emu.grasscutter.scripts.ScriptUtils;
import emu.grasscutter.scripts.lua_engine.Serializer;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class LuaJSerializer extends Serializer {


    @Override
    public <T> List<T> toList(Class<T> type, Object obj) {
        return serializeList(type, (LuaTable) obj);
    }

    @Override
    public <T> T toObject(Class<T> type, Object obj) {
        return serialize(type, null, (LuaTable) obj);
    }

    @Override
    public <T> Map<String, T> toMap(Class<T> type, Object obj) {
        return serializeMap(type, (LuaTable) obj);
    }

    @Nullable
    private <T> T valueToType(Class<T> type, LuaValue keyValue) {
        T object = null;

        if (keyValue.istable()) {
            object = serialize(type, null, keyValue.checktable());
        } else if (keyValue.isint()) {
            object = (T) (Integer) keyValue.toint();
        } else if (keyValue.isnumber()) {
            object = (T) (Float) keyValue.tofloat(); // terrible...
        } else if (keyValue.isstring()) {
            object = (T) keyValue.tojstring();
        } else if (keyValue.isboolean()) {
            object = (T) (Boolean) keyValue.toboolean();
        } else {
            object = (T) keyValue;
        }
        if (object == null) {
            Grasscutter.getLogger().warn("Can't serialize value: {} to type: {}", keyValue, type);
        }
        return object;
    }

    private <T> Map<String, T> serializeMap(Class<T> type, LuaTable table) {
        Map<String, T> map = new HashMap<>();

        if (table == null) {
            return map;
        }

        try {
            LuaValue[] keys = table.keys();
            for (LuaValue k : keys) {
                try {
                    LuaValue keyValue = table.get(k);
                    T object = valueToType(type, keyValue);

                    if (object != null) {
                        map.put(String.valueOf(k), object);
                    }
                } catch (Exception ex) {

                }
            }
        } catch (Exception e) {
            Grasscutter.getLogger().error("Exception while serializing map", e);
        }

        return map;
    }

    public <T> List<T> serializeList(Class<T> type, LuaTable table) {
        List<T> list = new ArrayList<>();

        if (table == null) {
            return list;
        }

        try {
            LuaValue[] keys = table.keys();
            for (LuaValue k : keys) {
                try {
                    LuaValue keyValue = table.get(k);

                    T object = valueToType(type, keyValue);

                    if (object != null) {
                        list.add(object);
                    }
                } catch (Exception ex) {

                }
            }
        } catch (Exception e) {
            Grasscutter.getLogger().error("Exception while serializing list", e);
        }

        return list;
    }

    private Class<?> getListType(Class<?> type, @Nullable Field field) {
        if (field == null) {
            return type.getTypeParameters()[0].getClass();
        }
        Type fieldType = field.getGenericType();
        if (fieldType instanceof ParameterizedType) {
            return (Class<?>) ((ParameterizedType) fieldType).getActualTypeArguments()[0];
        }

        return null;
    }

    public <T> T serialize(Class<T> type, @Nullable Field field, LuaTable table) {
        T object = null;

        if (type == List.class) {
            try {
                Class<?> listType = getListType(type, field);
                return (T) serializeList(listType, table);
            } catch (Exception e) {
                Grasscutter.getLogger().error("Exception while serializing {}", type.getName(), e);
                return null;
            }
        }

        try {
            if (!methodAccessCache.containsKey(type)) {
                cacheType(type);
            }
            var methodAccess = methodAccessCache.get(type);
            var fieldMetaMap = fieldMetaCache.get(type);

            object = (T) constructorCache.get(type).newInstance();

            if (table == null) {
                return object;
            }

            LuaValue[] keys = table.keys();
            for (LuaValue k : keys) {
                try {
                    var keyName = k.checkjstring();
                    if (!fieldMetaMap.containsKey(keyName)) {
                        continue;
                    }
                    var fieldMeta = fieldMetaMap.get(keyName);
                    LuaValue keyValue = table.get(k);

                    if (keyValue.istable()) {
                        methodAccess.invoke(object, fieldMeta.getIndex(), serialize(fieldMeta.getType(), fieldMeta.getField(), keyValue.checktable()));
                    } else if (fieldMeta.getType().equals(float.class)) {
                        methodAccess.invoke(object, fieldMeta.getIndex(), keyValue.tofloat());
                    } else if (fieldMeta.getType().equals(int.class)) {
                        methodAccess.invoke(object, fieldMeta.getIndex(), keyValue.toint());
                    } else if (fieldMeta.getType().equals(String.class)) {
                        methodAccess.invoke(object, fieldMeta.getIndex(), keyValue.tojstring());
                    } else if (fieldMeta.getType().equals(boolean.class)) {
                        methodAccess.invoke(object, fieldMeta.getIndex(), keyValue.toboolean());
                    } else {
                        methodAccess.invoke(object, fieldMeta.getIndex(), keyValue.tojstring());
                    }
                } catch (Exception ex) {
                    Grasscutter.getLogger().error("Exception serializing", ex);
                }
            }
        } catch (Exception e) {
            Grasscutter.getLogger().info(ScriptUtils.toMap(table).toString());
            Grasscutter.getLogger().error("Exception while serializing {}", type.getName(), e);
        }

        return object;
    }
}