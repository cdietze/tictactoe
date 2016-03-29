package de.cdietze.tictactoe.core.util;

import de.cdietze.tictactoe.core.MainGame;
import playn.core.Game;
import tripleplay.anim.Animator;
import tripleplay.game.ScreenStack;

public class MyScreen extends ScreenStack.UIScreen {
    public final MainGame game;
    /**
     * An animator that's updated on every update(), thus can be used for game logic. This supplements {@link
     * tripleplay.ui.Interface#anim} which is updated on paint().
     */
    public final Animator updateAnim = new Animator();

    public MyScreen(MainGame game) {
        super(game.plat);
        this.game = game;
        update.connect(updateAnim.onPaint);
    }

    @Override
    public Game game() {
        return game;
    }
}
