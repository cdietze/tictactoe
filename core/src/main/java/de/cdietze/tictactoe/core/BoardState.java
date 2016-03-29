package de.cdietze.tictactoe.core;

import com.google.common.collect.ImmutableList;
import react.Value;
import react.ValueView;

import java.util.ArrayList;
import java.util.List;

import static de.cdietze.tictactoe.core.Position.toIndex;

public class BoardState {

    public enum FieldType {
        EMPTY(' '), X('X'), O('O');
        public final char representation;

        FieldType(char representation) {
            this.representation = representation;
        }
    }

    public enum GameState {
        RUNNING, DRAW, X_WON, O_WON;
    }

    private final List<Value<FieldType>> fields = new ArrayList<>();
    public final Value<Boolean> isXToMove = Value.create(true);
    public final Value<GameState> gameState = Value.create(GameState.RUNNING);

    public BoardState() {
        for (int i = 0; i < Position.FIELD_COUNT; ++i) {
            fields.add(Value.create(FieldType.EMPTY));
        }
    }

    public ValueView<FieldType> fieldValue(int fieldIndex) {
        return fields.get(fieldIndex);
    }

    public void tryToMark(int fieldIndex) {
        if (gameState.get() != GameState.RUNNING) return;
        if (fields.get(fieldIndex).get() != FieldType.EMPTY) return;
        fields.get(fieldIndex).update(isXToMove.get() ? FieldType.X : FieldType.O);
        isXToMove.update(!isXToMove.get());
        gameState.update(calcGameState());
    }

    private static int toMask(int index) {
        return 1 << index;
    }

    private GameState calcGameState() {
        int xMask = 0, oMask = 0, emptyMask = 0;
        for (int fieldIndex = 0; fieldIndex < fields.size(); fieldIndex++) {
            int fieldMask = toMask(fieldIndex);
            FieldType fieldType = fields.get(fieldIndex).get();
            switch (fieldType) {
                case EMPTY:
                    emptyMask |= fieldMask;
                    break;
                case X:
                    xMask |= fieldMask;
                    break;
                case O:
                    oMask |= fieldMask;
                    break;
            }
        }
        for (int line : lines) {
            if ((line & xMask) == line) return GameState.X_WON;
            if ((line & oMask) == line) return GameState.O_WON;
        }
        if (emptyMask == 0) return GameState.DRAW;
        return GameState.RUNNING;
    }

    private static final List<Integer> lines = buildLines();

    @SuppressWarnings("Duplicates")
    private static List<Integer> buildLines() {
        ImmutableList.Builder<Integer> builder = ImmutableList.builder();
        for (int y = 0; y < 3; y++) {
            int row = 0;
            row |= toMask(toIndex(0, y));
            row |= toMask(toIndex(1, y));
            row |= toMask(toIndex(2, y));
            builder.add(row);
        }
        for (int x = 0; x < 3; x++) {
            int col = 0;
            col |= toMask(toIndex(x, 0));
            col |= toMask(toIndex(x, 1));
            col |= toMask(toIndex(x, 2));
            builder.add(col);
        }
        int diag1 = 0;
        diag1 |= toMask(toIndex(0, 0));
        diag1 |= toMask(toIndex(1, 1));
        diag1 |= toMask(toIndex(2, 2));
        builder.add(diag1);
        int diag2 = 0;
        diag2 |= toMask(toIndex(0, 2));
        diag2 |= toMask(toIndex(1, 1));
        diag2 |= toMask(toIndex(2, 0));
        builder.add(diag2);
        return builder.build();
    }
}
