package de.cdietze.tictactoe.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Position {
    public static final int FIELD_COUNT = 9;

    public static int toIndex(int x, int y) {
        return x + y * 3;
    }

    public enum FieldType {
        EMPTY(' '), X('X'), O('O');
        public final char representation;

        FieldType(char representation) {
            this.representation = representation;
        }
    }

    private final List<FieldType> fields = new ArrayList<>();

    public Position() {
        fields.addAll(Collections.nCopies(FIELD_COUNT, FieldType.EMPTY));
    }

    public FieldType fieldType(int index) {
        return fields.get(index);
    }

    public boolean isXToMove() {
        int xCount = 0, oCount = 0;
        for (FieldType field : fields) {
            if (field == FieldType.X) xCount++;
            else if (field == FieldType.O) oCount++;
        }
        return xCount <= oCount;
    }
}
