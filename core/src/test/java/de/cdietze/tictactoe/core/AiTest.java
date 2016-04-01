package de.cdietze.tictactoe.core;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

public class AiTest {

    public static final String EMPTY_STATE_STRING = "...\n...\n...";

    @Test
    public void testStateToString() {
        assertEquals(EMPTY_STATE_STRING, Ai.stateToString(Ai.emptyState));
    }

    @Test
    public void testStringToState() {
        assertEquals(Ai.emptyState, Ai.stringToState(EMPTY_STATE_STRING));
    }

    @Test
    public void testGetFieldForEmptyState() {
        int state = Ai.emptyState;
        for (int i = 0; i < 9; i++) {
            assertEquals(Ai.Field.EMPTY, Ai.getField(state, i));
        }
    }

    @Test
    public void testGetSetX() {
        int state = Ai.emptyState;
        for (int i = 0; i < 9; i++) {
            assertEquals(Ai.Field.X, Ai.getField(Ai.setField(state, i, true), i));
        }
    }

    @Test
    public void testGetSetO() {
        int state = Ai.emptyState;
        for (int i = 0; i < 9; i++) {
            assertEquals(Ai.Field.O, Ai.getField(Ai.setField(state, i, false), i));
        }
    }

    @Test
    public void testGetFieldOnEmptyState() {
        for (int i = 0; i < 9; i++) {
            assertEquals(Ai.Field.EMPTY, Ai.getField(Ai.emptyState, i));
        }
    }

    @Test
    public void testGetFieldOnFilledState() {
        int state = Ai.stringToState(".XO\nXO.\nO.X");
        assertEquals(Ai.Field.EMPTY, Ai.getField(state, 0));
        assertEquals(Ai.Field.X, Ai.getField(state, 1));
        assertEquals(Ai.Field.O, Ai.getField(state, 2));
        assertEquals(Ai.Field.X, Ai.getField(state, 3));
        assertEquals(Ai.Field.O, Ai.getField(state, 4));
        assertEquals(Ai.Field.EMPTY, Ai.getField(state, 5));
        assertEquals(Ai.Field.O, Ai.getField(state, 6));
        assertEquals(Ai.Field.EMPTY, Ai.getField(state, 7));
        assertEquals(Ai.Field.X, Ai.getField(state, 8));
    }

    @Test
    public void hasXWon() {
        assertEquals(false, Ai.hasXWon(Ai.emptyState));
        assertEquals(true, Ai.hasXWon(Ai.stringToState("XXX......")));
        assertEquals(false, Ai.hasXWon(Ai.stringToState(".XXX.....")));
        assertEquals(true, Ai.hasXWon(Ai.stringToState("X..X..X..")));
        assertEquals(true, Ai.hasXWon(Ai.stringToState("X...X...X")));
    }

    @Test
    public void hasOWon() {
        assertEquals(false, Ai.hasOWon(Ai.emptyState));
        assertEquals(true, Ai.hasOWon(Ai.stringToState("OOO......")));
        assertEquals(false, Ai.hasOWon(Ai.stringToState(".OOO.....")));
        assertEquals(true, Ai.hasOWon(Ai.stringToState("O..O..O..")));
        assertEquals(true, Ai.hasOWon(Ai.stringToState("O...O...O")));
    }

    @Test
    public void testIsDraw() {
        assertEquals(false, Ai.isDraw(Ai.emptyState));
        assertEquals(false, Ai.isDraw(Ai.stringToState("XXXOOOXX.")));
        assertEquals(true, Ai.isDraw(Ai.stringToState("XXXOOOXXX")));
    }

    @Test
    public void testIsXToMove() {
        assertEquals(true, Ai.isXToMove(Ai.emptyState));
        assertEquals(false, Ai.isXToMove(Ai.stringToState("X........")));
        assertEquals(true, Ai.isXToMove(Ai.stringToState("XO.......")));
    }

    @Test
    public void testEvalRating() {
        assertThat(Ai.eval(Ai.stringToState("XOXXOOOXX")).rating).isZero();
        assertThat(Ai.eval(Ai.stringToState("XO.XO.X..")).rating).isLessThan(0);
        assertThat(Ai.eval(Ai.stringToState("O.XOXXO..")).rating).isLessThan(0);
    }

    @Test
    public void shouldWinInOneWithX() {
        assertEquals(Ai.index(0, 2), Ai.eval(Ai.stringToState("XO.XO....")).bestMoveIndex);
    }

    @Test
    public void winInOneWithO() {
        assertEquals(Ai.index(1, 2), Ai.eval(Ai.stringToState("XO.XO...X")).bestMoveIndex);
    }

    @Test
    public void shouldPreventInOneWithX() {
        assertEquals(Ai.index(0, 2), Ai.eval(Ai.stringToState("OXXO.....")).bestMoveIndex);
    }

    @Test
    public void shouldPreventInOneWithO() {
        assertEquals(Ai.index(0, 2), Ai.eval(Ai.stringToState("XO.|X..|...")).bestMoveIndex);
    }
}
