package com.snake;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameLogic {
    public static final int COLS = 30;
    public static final int ROWS = 30;

    private static final String VOWELS = "AEIOU";
    private static final String CONSONANTS = "BCDFGHJKLMNPQRSTVWXYZ";
    private static final int MIN_VOWELS = 2;
    private static final int MIN_CONSONANTS = 6;

    private final Random random = new Random();
    private final List<LetterItem> items = new ArrayList<>();
    private Snake snake;
    private int score;
    private int highScore;
    private boolean gameOver;

    public GameLogic() {
        reset();
    }

    public void reset() {
        snake = new Snake(new Cell(COLS / 2, ROWS / 2));
        items.clear();
        score = 0;
        gameOver = false;
        refill();
    }

    public void tick() {
        if (gameOver) return;

        Cell next = snake.nextHead();

        if (next.x() < 0 || next.x() >= COLS || next.y() < 0 || next.y() >= ROWS) {
            endGame();
            return;
        }

        boolean grow = false;
        LetterItem hit = itemAt(next);
        if (hit != null) {
            if (!hit.vowel()) {      // ate a consonant
                endGame();
                return;
            }
            grow = true;             // ate a vowel
            score += 10;
            items.remove(hit);
        }

        if (snake.hitsSelf(next, grow)) {
            endGame();
            return;
        }

        snake.move(next, grow);
        refill();
    }

    private void endGame() {
        gameOver = true;
        highScore = Math.max(highScore, score);
    }

    private void refill() {
        while (count(true) < MIN_VOWELS) spawn(true);
        while (count(false) < MIN_CONSONANTS) spawn(false);
    }

    private int count(boolean vowel) {
        int n = 0;
        for (LetterItem item : items) {
            if (item.vowel() == vowel) n++;
        }
        return n;
    }

    private void spawn(boolean vowel) {
        Cell c;
        do {
            c = new Cell(random.nextInt(COLS), random.nextInt(ROWS));
        } while (snake.occupies(c) || itemAt(c) != null);

        String pool = vowel ? VOWELS : CONSONANTS;
        items.add(new LetterItem(c, pool.charAt(random.nextInt(pool.length())), vowel));
    }

    private LetterItem itemAt(Cell c) {
        for (LetterItem item : items) {
            if (item.cell().equals(c)) return item;
        }
        return null;
    }

    public Snake snake() { return snake; }
    public List<LetterItem> items() { return items; }
    public int score() { return score; }
    public int highScore() { return highScore; }
    public boolean isGameOver() { return gameOver; }
}
