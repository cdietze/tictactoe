package de.cdietze.tictactoe.android;

import playn.android.GameActivity;

import de.cdietze.tictactoe.core.MainGame;

public class MainActivity extends GameActivity {

  @Override public void main () {
    new MainGame(platform());
  }
}
