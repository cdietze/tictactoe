package de.cdietze.tictactoe.core;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AiTest {

    public static final String EMPTY_STATE_STRING = "...\n...\n...";

    @Test
    public void testStateToString() {
        assertThat(Ai.stateToString(Ai.emptyState)).isEqualTo(EMPTY_STATE_STRING);
    }

    @Test
    public void testStringToState() {
        assertThat(Ai.stringToState(EMPTY_STATE_STRING)).isEqualTo(Ai.emptyState);
    }

    @Test
    public void testGetFieldForEmptyState() {
        int state = Ai.emptyState;
        for (int i = 0; i < 9; i++) {
            assertThat(Ai.getField(state, i)).isEqualTo(Ai.Field.EMPTY);
        }
    }

    @Test
    public void testGetSetX() {
        int state = Ai.emptyState;
        for (int i = 0; i < 9; i++) {
            assertThat(Ai.getField(Ai.setField(state, i, true), i)).isEqualTo(Ai.Field.X);
        }
    }

    @Test
    public void testGetSetO() {
        int state = Ai.emptyState;
        for (int i = 0; i < 9; i++) {
            assertThat(Ai.getField(Ai.setField(state, i, false), i)).isEqualTo(Ai.Field.O);
        }
    }

    @Test
    public void testGetFieldOnEmptyState() {
        for (int i = 0; i < 9; i++) {
            assertThat(Ai.getField(Ai.emptyState, i)).isEqualTo(Ai.Field.EMPTY);
        }
    }

    @Test
    public void testGetFieldOnFilledState() {
        int state = Ai.stringToState(".XO\nXO.\nO.X");
        assertThat(Ai.getField(state, 0)).isEqualTo(Ai.Field.EMPTY);
        assertThat(Ai.getField(state, 1)).isEqualTo(Ai.Field.X);
        assertThat(Ai.getField(state, 2)).isEqualTo(Ai.Field.O);
        assertThat(Ai.getField(state, 3)).isEqualTo(Ai.Field.X);
        assertThat(Ai.getField(state, 4)).isEqualTo(Ai.Field.O);
        assertThat(Ai.getField(state, 5)).isEqualTo(Ai.Field.EMPTY);
        assertThat(Ai.getField(state, 6)).isEqualTo(Ai.Field.O);
        assertThat(Ai.getField(state, 7)).isEqualTo(Ai.Field.EMPTY);
        assertThat(Ai.getField(state, 8)).isEqualTo(Ai.Field.X);
    }

    @Test
    public void hasXWon() {
        assertThat(Ai.hasXWon(Ai.emptyState)).isFalse();
        assertThat(Ai.hasXWon(Ai.stringToState("XXX......"))).isTrue();
        assertThat(Ai.hasXWon(Ai.stringToState(".XXX....."))).isFalse();
        assertThat(Ai.hasXWon(Ai.stringToState("X..X..X.."))).isTrue();
        assertThat(Ai.hasXWon(Ai.stringToState("X...X...X"))).isTrue();
    }

    @Test
    public void hasOWon() {
        assertThat(Ai.hasOWon(Ai.emptyState)).isFalse();
        assertThat(Ai.hasOWon(Ai.stringToState("OOO......"))).isTrue();
        assertThat(Ai.hasOWon(Ai.stringToState(".OOO....."))).isFalse();
        assertThat(Ai.hasOWon(Ai.stringToState("O..O..O.."))).isTrue();
        assertThat(Ai.hasOWon(Ai.stringToState("O...O...O"))).isTrue();
    }

    @Test
    public void testIsDraw() {
        assertThat(Ai.isDraw(Ai.emptyState)).isFalse();
        assertThat(Ai.isDraw(Ai.stringToState("XXXOOOXX."))).isFalse();
        assertThat(Ai.isDraw(Ai.stringToState("XXXOOOXXX"))).isTrue();
    }

    @Test
    public void testIsXToMove() {
        assertThat(Ai.isXToMove(Ai.emptyState)).isTrue();
        assertThat(Ai.isXToMove(Ai.stringToState("X........"))).isFalse();
        assertThat(Ai.isXToMove(Ai.stringToState("XO......."))).isTrue();
    }

    @Test
    public void testEvalRating() {
        assertThat(Ai.eval(Ai.stringToState("XOXXOOOXX")).rating).isZero();
        assertThat(Ai.eval(Ai.stringToState("XO.XO.X..")).rating).isLessThan(0);
        assertThat(Ai.eval(Ai.stringToState("O.XOXXO..")).rating).isLessThan(0);
    }

    @Test
    public void shouldWinInOneWithX() {
        assertThat(Ai.eval(Ai.stringToState("XO.XO....")).bestMoveIndex).isEqualTo(Ai.index(0, 2));
    }

    @Test
    public void winInOneWithO() {
        assertThat(Ai.eval(Ai.stringToState("XO.XO...X")).bestMoveIndex).isEqualTo(Ai.index(1, 2));
    }

    @Test
    public void shouldPreventInOneWithX() {
        assertThat(Ai.eval(Ai.stringToState("OXXO.....")).bestMoveIndex).isEqualTo(Ai.index(0, 2));
    }

    @Test
    public void shouldPreventInOneWithO() {
        assertThat(Ai.eval(Ai.stringToState("XO.|X..|...")).bestMoveIndex).isEqualTo(Ai.index(0, 2));
    }
}
