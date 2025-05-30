package nl.jqno.equalsverifier_testhelpers.types;

import java.util.Objects;

public class GetClassPoint {

    private final int x;
    private final int y;

    public GetClassPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != getClass()) {
            return false;
        }
        GetClassPoint p = (GetClassPoint) obj;
        return p.x == x && p.y == y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
