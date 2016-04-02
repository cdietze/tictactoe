package de.cdietze.tictactoe.core.util;

import com.google.common.collect.Lists;
import de.cdietze.tictactoe.core.MainGame;
import playn.core.Game;
import playn.core.Platform;
import tripleplay.anim.Animator;
import tripleplay.game.ScreenStack;
import tripleplay.ui.Element;
import tripleplay.ui.Layout;
import tripleplay.ui.SimpleStyles;
import tripleplay.ui.layout.AxisLayout;

import java.util.List;

public class MyScreen extends ScreenStack.UIScreen {
    public final MainGame game;
    /**
     * An animator that's updated on every update(), thus can be used for game logic. This supplements {@link
     * tripleplay.ui.Interface#anim} which is updated on paint().
     */
    public final Animator updateAnim = new Animator();
    public final Platform plat;

    float dialogDepth = 100f;
    final List<Dialog> dialogs = Lists.newArrayList();

    public MyScreen(MainGame game) {
        super(game.plat);
        this.game = game;
        this.plat = game.plat;
        update.connect(updateAnim.onPaint);
    }

    @Override
    public Game game() {
        return game;
    }

    /**
     * Returns a dialog which contains the supplied contents.
     */
    public Dialog createDialog(Element<?> dialog) {
        Dialog d = createDialog();
        d.root.add(dialog);
        return d;
    }

    /**
     * Returns a dialog with the supplied root layout.
     */
    public Dialog createDialog(Layout layout) {
        return new Dialog(this, iface.createRoot(layout, SimpleStyles.newSheet(plat.graphics()), layer));
    }

    /**
     * Returns a dialog with a vertical axis layout.
     */
    public Dialog createDialog() {
        return createDialog(AxisLayout.vertical());
    }
}
