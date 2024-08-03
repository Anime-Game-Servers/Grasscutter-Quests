package emu.grasscutter.game.activity.condition;

import emu.grasscutter.Grasscutter;
import org.anime_game_servers.game_data_models.data.activity.ActivityCondData;
import org.anime_game_servers.game_data_models.data.activity.ActivityCondition;

import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Class that used for scanning classpath, picking up all activity conditions (for NewActivityCondExcelConfigData.json {@link ActivityCondData})
 * and saving them to map. Check for more info {@link ActivityConditionHandler}
 */
public class AllActivityConditionBuilder {

    /**
     * Build activity conditions handlers
     *
     * @return map containing all condition handlers for NewActivityCondExcelConfigData.json
     */
    static public Map<ActivityCondition, ActivityConditionBaseHandler> buildActivityConditions() {
        return new AllActivityConditionBuilder().initActivityConditions();
    }

    private Map<ActivityCondition, ActivityConditionBaseHandler> initActivityConditions() {
        return Grasscutter.reflector.getTypesAnnotatedWith(ActivityConditionHandler.class).stream()
            .map(this::newInstance)
            .map(h -> new AbstractMap.SimpleEntry<>(extractActionType(h), h))
            .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
    }

    private ActivityCondition extractActionType(ActivityConditionBaseHandler e) {
        ActivityConditionHandler condition = e.getClass().getAnnotation(ActivityConditionHandler.class);
        if (condition == null) {
            Grasscutter.getLogger().error("Failed to read command type for class {}", e.getClass().getName());
            return null;
        }

        return condition.value();
    }

    private ActivityConditionBaseHandler newInstance(Class<?> clazz) {
        try {
            Object result = clazz.getDeclaredConstructor().newInstance();
            if (result instanceof ActivityConditionBaseHandler) {
                return (ActivityConditionBaseHandler) result;
            }
            Grasscutter.getLogger().error("Failed to initiate activity condition: {}, object have wrong type", clazz.getName());
        } catch (Exception e) {
            String message = String.format("Failed to initiate activity condition: %s, %s", clazz.getName(), e.getMessage());
            Grasscutter.getLogger().error(message, e);
        }
        return null;
    }


}
