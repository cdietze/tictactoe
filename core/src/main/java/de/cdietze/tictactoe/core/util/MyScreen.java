package de.cdietze.tictactoe.core.util;

import de.cdietze.tictactoe.core.MainGame;
import playn.core.Game;
import playn.core.Platform;
import tripleplay.anim.Animator;
import tripleplay.game.ScreenStack;

public class MyScreen extends ScreenStack.UIScreen {
    public final MainGame game;
    /**
     * An animator that's updated on every update(), thus can be used for game logic. This supplements {@link
     * tripleplay.ui.Interface#anim} which is updated on paint().
     */
    public final Animator updateAnim = new Animator();
    public final Platform plat;

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
}
