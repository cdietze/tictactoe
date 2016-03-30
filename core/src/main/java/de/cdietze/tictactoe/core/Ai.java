package de.cdietze.tictactoe.core;

import com.google.common.base.Optional;
import com.google.common.primitives.Ints;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static de.cdietze.tictactoe.core.Position.toIndex;

public final class Ai {

    public static final int emptyState = 0;
    private static final int NINE_BITS = (1 << 9) - 1;
    private static final int[] LINE_MASKS = buildLines();

    private Ai() {
    }

    public static int index(int x, int y) {
        return x + y * 3;
    }

    public static Field getField(int state, int index) {
        if (hasMask(state, toMask(index))) return Field.X;
        if (hasMask(state, toOMask(index))) return Field.O;
        return Field.EMPTY;
    }

    /**
     * We can only assign empty fields. No taking back.
     */
    public static int setField(int state, int index, boolean isX) {
        return isX ? (state | toMask(index)) : (state | toOMask(index));
    }

    public static EvalResult eval(int state) {
        return eval(state, isXToMove(state), 0);
    }

    public static EvalResult eval(int state, boolean isXToMove, int depth) {
        if (hasXWon(state)) return EvalResult.create(EvalResult.X_WINS);
        if (hasOWon(state)) return EvalResult.create(EvalResult.O_WINS);
        if (isDraw(state)) return EvalResult.create(EvalResult.DRAW);
        int occupiedFieldMask = (state >> 9) | state;
        EvalResult bestResult = null;
        for (int i = 0; i < 9; i++) {
            if (isBitSet(occupiedFieldMask, i)) continue;
            int newState = setField(state, i, isXToMove);
            EvalResult result = eval(newState, !isXToMove, depth + 1);
            int resultRating = result.rating;
            if (!isXToMove) resultRating *= -1;
            final int depthAdjustment = 1;
            resultRating -= depthAdjustment;
            if (bestResult == null || resultRating >= bestResult.rating) {
                bestResult = EvalResult.create(resultRating, i);
            }
        }
        return checkNotNull(bestResult, "No valid move found");
    }

    public static boolean isXToMove(int state) {
        int xCount = Integer.bitCount(state & NINE_BITS);
        int oCount = Integer.bitCount(state & (NINE_BITS << 9));
        return xCount <= oCount;
    }

    public static boolean hasXWon(int state) {
        for (int lineMask : LINE_MASKS) {
            if (hasMask(state, lineMask)) return true;
        }
        return false;
    }

    public static boolean hasOWon(int state) {
        for (int lineMask : LINE_MASKS) {
            if (hasMask(state, lineMask << 9)) return true;
        }
        return false;
    }

    public static boolean isDraw(int state) {
        int occupiedFieldMask = (state >> 9) | state;
        return (NINE_BITS & occupiedFieldMask) == NINE_BITS;
    }

    public static String stateToString(int state) {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                Field field = getField(state, index(x, y));
                sb.append(field.representation);
            }
            if (y < 2) sb.append('\n');
        }
        return sb.toString();
    }

    public static int stringToState(String s) {
        int state = 0;
        int fieldIndex = 0;
        for (int charIndex = 0; charIndex < s.length(); charIndex++) {
            char c = s.charAt(charIndex);
            Optional<Field> field = Field.valueOf(c);
            if (!field.isPresent()) continue;
            if (field.get() != Field.EMPTY) {
                state = setField(state, fieldIndex, field.get() == Field.X);
            }
            fieldIndex++;
        }
        return state;
    }

    private static boolean isBitSet(int i, int pos) {
        return (i & (1 << pos)) != 0;
    }

    private static int setBit(int i, int pos) {
        return i | (1 << pos);
    }

    private static int unsetBit(int i, int pos) {
        return i & ~(1 << pos);
    }

    private static int toMask(int index) {
        return 1 << index;
    }

    private static int toOMask(int index) {
        return 1 << (index + 9);
    }

    private static boolean hasMask(int state, int mask) {
        return (state & mask) == mask;
    }

    @SuppressWarnings("Duplicates")
    private static int[] buildLines() {
        List<Integer> list = new ArrayList<>();
        for (int y = 0; y < 3; y++) {
            int row = 0;
            row |= toMask(toIndex(0, y));
            row |= toMask(toIndex(1, y));
            row |= toMask(toIndex(2, y));
            list.add(row);
        }
        for (int x = 0; x < 3; x++) {
            int col = 0;
            col |= toMask(toIndex(x, 0));
            col |= toMask(toIndex(x, 1));
            col |= toMask(toIndex(x, 2));
            list.add(col);
        }
        int diag1 = 0;
        diag1 |= toMask(toIndex(0, 0));
        diag1 |= toMask(toIndex(1, 1));
        diag1 |= toMask(toIndex(2, 2));
        list.add(diag1);
        int diag2 = 0;
        diag2 |= toMask(toIndex(0, 2));
        diag2 |= toMask(toIndex(1, 1));
        diag2 |= toMask(toIndex(2, 0));
        list.add(diag2);
        return Ints.toArray(list);
    }

    public enum Field {
        EMPTY('.'), X('X'), O('O');
        public final char representation;

        Field(char representation) {
            this.representation = representation;
        }

        public static Optional<Field> valueOf(char c) {
            for (Field field : values()) {
                if (c == field.representation) return Optional.of(field);
            }
            return Optional.absent();
        }
    }

    public static class EvalResult {
        public final static int DRAW = 0;
        public final static int X_WINS = 100;
        public final static int O_WINS = -100;

        public final int rating;
        public final int bestMoveIndex;

        private EvalResult(int rating, int bestMoveIndex) {
            this.rating = rating;
            this.bestMoveIndex = bestMoveIndex;
        }

        public static EvalResult create(int rating, int bestMoveIndex) {
            return new EvalResult(rating, bestMoveIndex);
        }

        public static EvalResult create(int rating) {
            return new EvalResult(rating, -1);
        }

        public EvalResult negate() {
            return create(-rating, bestMoveIndex);
        }

        @Override
        public String toString() {
            return "EvalResult{" +
                    "rating=" + rating +
                    ", bestMoveIndex=" + bestMoveIndex +
                    '}';
        }
    }
}
