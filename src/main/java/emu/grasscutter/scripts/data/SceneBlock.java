package emu.grasscutter.scripts.data;

import com.github.davidmoten.rtreemulti.RTree;
import com.github.davidmoten.rtreemulti.geometry.Geometry;
import com.github.davidmoten.rtreemulti.geometry.Rectangle;
import emu.grasscutter.Grasscutter;
import emu.grasscutter.scripts.SceneIndexManager;
import emu.grasscutter.scripts.ScriptLoader;
import emu.grasscutter.utils.Position;
import lombok.Setter;
import lombok.ToString;
import lombok.val;

import javax.script.Bindings;
import javax.script.CompiledScript;
import javax.script.ScriptException;

import java.util.Map;
import java.util.stream.Collectors;

@ToString
@Setter
public class SceneBlock {
    public int id;
    public Position max;
    public Position min;

    public int sceneId;
    public Map<Integer,SceneGroup> groups;
    public RTree<SceneGroup, Geometry> sceneGroupIndex;

    private transient boolean loaded; // Not an actual variable in the scripts either

    public boolean isLoaded() {
        return this.loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    public boolean contains(Position pos) {
        int range = Grasscutter.getConfig().server.game.loadEntitiesForPlayerRange;
        return 	pos.getX() <= (this.max.getX() + range) && pos.getX() >= (this.min.getX() - range) &&
                pos.getZ() <= (this.max.getZ() + range) && pos.getZ() >= (this.min.getZ() - range);
    }

    public SceneBlock load(int sceneId) {
        if (this.loaded) {
            return this;
        }
        this.sceneId = sceneId;
        this.setLoaded(true);

        val cs = ScriptLoader.getScript("Scene/" + sceneId + "/scene" + sceneId + "_block" + this.id + ".lua");

        if (cs == null) {
            return null;
        }

        // Eval script
        try {
            cs.evaluate();

            // Set groups
            this.groups = cs.getGlobalVariableList("groups", SceneGroup.class).stream()
                    .collect(Collectors.toMap(x -> x.id, y -> y, (a, b) -> a));

            this.groups.values().forEach(g -> g.block_id = this.id);
            this.sceneGroupIndex = SceneIndexManager.buildIndex(3, this.groups.values(), g -> g.pos.toPoint());
        } catch (ScriptException exception) {
            Grasscutter.getLogger().error("An error occurred while loading block " + this.id + " in scene " + sceneId, exception);
        }
        Grasscutter.getLogger().debug("Successfully loaded block {} in scene {}.", this.id, sceneId);
        return this;
    }

    public Rectangle toRectangle() {
        return Rectangle.create(this.min.toXZDoubleArray(), this.max.toXZDoubleArray());
    }
}
