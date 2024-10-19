package emu.grasscutter.game.activity;

import org.anime_game_servers.game_data_models.data.activity.ActivityType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface GameActivity {
    ActivityType value();
}
