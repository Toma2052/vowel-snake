package com.snake;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

public class App extends Application {
    private static final int CELL = 20;
    private static final int WIDTH = GameLogic.COLS * CELL;
    private static final int HEIGHT = GameLogic.ROWS * CELL;

    private final GameLogic game = new GameLogic();
    private Timeline loop;
    private boolean paused;

    @Override
    public void start(Stage stage) {
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        loop = new Timeline(new KeyFrame(Duration.millis(140), e -> {
            game.tick();
            draw(gc);
        }));
        loop.setCycleCount(Timeline.INDEFINITE);

        Scene scene = new Scene(new StackPane(canvas));
        scene.setOnKeyPressed(e -> {
            KeyCode k = e.getCode();
            switch (k) {
                case UP, W -> game.snake().turn(Direction.UP);
                case DOWN, S -> game.snake().turn(Direction.DOWN);
                case LEFT, A -> game.snake().turn(Direction.LEFT);
                case RIGHT, D -> game.snake().turn(Direction.RIGHT);
                case SPACE -> togglePause(gc);
                case R -> {
                    game.reset();
                    paused = false;
                    loop.play();
                    draw(gc);
                }
                default -> { }
            }
        });

        stage.setTitle("Vowel Snake");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        draw(gc);
        loop.play();
    }

    private void togglePause(GraphicsContext gc) {
        if (game.isGameOver()) return;
        paused = !paused;
        if (paused) loop.pause(); else loop.play();
        draw(gc);
    }

    private void draw(GraphicsContext gc) {
        gc.setFill(Color.web("#1e1e2e"));
        gc.fillRect(0, 0, WIDTH, HEIGHT);

        // letters
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFont(Font.font("Monospaced", FontWeight.BOLD, 15));
        for (LetterItem item : game.items()) {
            double x = item.cell().x() * CELL;
            double y = item.cell().y() * CELL;
            gc.setFill(Color.web("#45475a"));
            gc.fillRoundRect(x + 1, y + 1, CELL - 2, CELL - 2, 6, 6);
            gc.setFill(Color.web("#f9e2af"));
            gc.fillText(String.valueOf(item.letter()), x + CELL / 2.0, y + CELL - 5);
        }

        // snake
        boolean head = true;
        for (Cell c : game.snake().cells()) {
            gc.setFill(head ? Color.web("#40a02b") : Color.web("#a6e3a1"));
            gc.fillRoundRect(c.x() * CELL + 1, c.y() * CELL + 1, CELL - 2, CELL - 2, 8, 8);
            head = false;
        }

        // HUD
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("SansSerif", FontWeight.BOLD, 14));
        gc.setTextAlign(TextAlignment.LEFT);
        gc.fillText("Score: " + game.score() + "   Best: " + game.highScore(), 8, 18);

        if (game.isGameOver()) {
            overlay(gc, "GAME OVER", "Press R to restart");
        } else if (paused) {
            overlay(gc, "PAUSED", "Press SPACE to resume");
        }
    }

    private void overlay(GraphicsContext gc, String title, String sub) {
        gc.setFill(Color.rgb(0, 0, 0, 0.6));
        gc.fillRect(0, 0, WIDTH, HEIGHT);
        gc.setFill(Color.WHITE);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFont(Font.font("SansSerif", FontWeight.BOLD, 40));
        gc.fillText(title, WIDTH / 2.0, HEIGHT / 2.0);
        gc.setFont(Font.font("SansSerif", 18));
        gc.fillText(sub, WIDTH / 2.0, HEIGHT / 2.0 + 35);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
