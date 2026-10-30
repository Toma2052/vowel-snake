package com.snake;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

public class Snake {
    private final Deque<Cell> body = new ArrayDeque<>();
    private Direction direction = Direction.RIGHT;
    private Direction pending = Direction.RIGHT;

    public Snake(Cell start) {
        body.addLast(start);
        body.addLast(new Cell(start.x() - 1, start.y()));
        body.addLast(new Cell(start.x() - 2, start.y()));
    }

    public Cell head() {
        return body.peekFirst();
    }

    public Iterable<Cell> cells() {
        return body;
    }

    public boolean occupies(Cell c) {
        return body.contains(c);
    }

    /** Request a turn; reversing straight into yourself is ignored. */
    public void turn(Direction d) {
        if (d != direction.opposite()) {
            pending = d;
        }
    }

    /** Applies the pending turn and returns where the head will go next. */
    public Cell nextHead() {
        direction = pending;
        return new Cell(head().x() + direction.dx, head().y() + direction.dy);
    }

    /** The tail cell moves away this tick unless we are growing, so it is safe to enter. */
    public boolean hitsSelf(Cell next, boolean growing) {
        Iterator<Cell> it = body.iterator();
        int i = 0, size = body.size();
        while (it.hasNext()) {
            Cell c = it.next();
            boolean isTail = (i == size - 1);
            if (!(isTail && !growing) && c.equals(next)) {
                return true;
            }
            i++;
        }
        return false;
    }

    public void move(Cell next, boolean grow) {
        body.addFirst(next);
        if (!grow) {
            body.removeLast();
        }
    }
}
