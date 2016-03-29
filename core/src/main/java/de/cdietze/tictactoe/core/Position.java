package de.cdietze.tictactoe.core;

public class Position {
    public static final int FIELD_COUNT = 9;

    public static int toIndex(int x, int y) {
        return x + y * 3;
    }
}
