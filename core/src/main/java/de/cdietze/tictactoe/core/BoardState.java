package de.cdietze.tictactoe.core;

import react.Value;
import react.ValueView;

import java.util.ArrayList;
import java.util.List;

public class BoardState {

    public enum FieldType {
        EMPTY(' '), X('X'), O('O');
        public final char representation;

        FieldType(char representation) {
            this.representation = representation;
        }
    }

    private final List<Value<FieldType>> fields = new ArrayList<>();
    public final Value<Boolean> isXToMove = Value.create(true);

    public BoardState() {
        for (int i = 0; i < Position.FIELD_COUNT; ++i) {
            fields.add(Value.create(FieldType.EMPTY));
        }
    }

    public ValueView<FieldType> fieldValue(int fieldIndex) {
        return fields.get(fieldIndex);
    }

    public void tryToMark(int fieldIndex) {
        if (fields.get(fieldIndex).get() != FieldType.EMPTY) return;
        fields.get(fieldIndex).update(isXToMove.get() ? FieldType.X : FieldType.O);
        isXToMove.update(!isXToMove.get());
    }
}
