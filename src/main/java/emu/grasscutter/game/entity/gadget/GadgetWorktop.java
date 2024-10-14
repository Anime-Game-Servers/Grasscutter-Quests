package emu.grasscutter.game.entity.gadget;

import java.util.Arrays;

import emu.grasscutter.game.entity.EntityGadget;
import emu.grasscutter.game.entity.gadget.worktop.WorktopWorktopOptionHandler;
import emu.grasscutter.game.player.Player;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import lombok.val;
import org.anime_game_servers.multi_proto.gi.messages.gadget.GadgetInteractReq;
import org.anime_game_servers.multi_proto.gi.messages.gadget.SelectWorktopOptionReq;
import org.anime_game_servers.multi_proto.gi.messages.scene.entity.SceneGadgetInfo;
import org.anime_game_servers.multi_proto.gi.messages.scene.entity.WorktopInfo;

public class GadgetWorktop extends GadgetContent {
    private IntSet worktopOptions;
    private WorktopWorktopOptionHandler handler;

    public GadgetWorktop(EntityGadget gadget) {
        super(gadget);
    }

    public IntSet getWorktopOptions() {
        if (this.worktopOptions == null) {
            this.worktopOptions = new IntOpenHashSet();
        }
        return worktopOptions;
    }

    public void addWorktopOptions(int[] options) {
        if (this.worktopOptions == null) {
            this.worktopOptions = new IntOpenHashSet();
        }
        Arrays.stream(options).forEach(this.worktopOptions::add);
    }

    public void removeWorktopOption(int option) {
        if (this.worktopOptions == null) {
            return;
        }
        this.worktopOptions.remove(option);
    }

    public boolean onInteract(Player player, GadgetInteractReq req) {
        return false;
    }

    public void onBuildProto(SceneGadgetInfo gadgetInfo) {
        if (this.worktopOptions == null) {
            return;
        }

        val worktop = new WorktopInfo(this.getWorktopOptions().stream().toList());

        gadgetInfo.setContent(new SceneGadgetInfo.Content.Worktop(worktop));
    }

    public void setOnSelectWorktopOptionEvent(WorktopWorktopOptionHandler handler) {
        this.handler = handler;
    }
    public boolean onSelectWorktopOption(SelectWorktopOptionReq req) {
        if (this.handler != null) {
            this.handler.onSelectWorktopOption(this, req.getOptionId());
        }
        return false;
    }

}
