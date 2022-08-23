package easyXO;

import java.util.Objects;


/**
 * Coordinate representing class
 */
class Cord {

    public final int x;
    public final int y;

    Cord(int x, int y) {
        this.x = --x;
        this.y = --y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cord cord = (Cord) o;
        return x == cord.x && y == cord.y;
    }

}
