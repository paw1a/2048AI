package sample;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Random;

public class GameController {

    private int size;
    private int tileSize;
    private int tileOffset;

    public Pane pane;
    public Label scoreLabel;
    private Tile[][] tiles;

    private boolean isPressed = false;
    private AI ai;
    private int gameScore = 0;
    private int gameMode;

    public void init(int size, int gameMode) {
        this.size = size;
        this.gameMode = gameMode;
        tileOffset = 14 - size;
        tileSize = (400 - (tileOffset*(size+1))) / size;

        tiles = new Tile[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                Rectangle rectangle = new Rectangle();
                rectangle.setTranslateX((j+1)*tileOffset + j*tileSize);
                rectangle.setTranslateY((i+1)*tileOffset + i*tileSize);
                rectangle.setWidth(tileSize);
                rectangle.setHeight(tileSize);
                rectangle.setFill(Util.COLORS.get(0));
                rectangle.setArcWidth(10);
                rectangle.setArcHeight(10);
                pane.getChildren().add(rectangle);
            }
        }

        generateTile();
        generateTile();

        if(gameMode == 1) {
            ai = new AI(size);
            makeMove();
        }
    }

    private void makeMove() {
        int [][] field = getField();

        Direction direction = null;
        try {
            int depth = 11-size;
            direction = ai.rootMinimax(depth, field);
        } catch (GameOverException e) {
            try {
                Thread.sleep(7000);
                ((Stage) pane.getScene().getWindow())
                        .setScene(new Scene(FXMLLoader.load(getClass().getResource("/menu.fxml")), 500, 600));
            } catch (InterruptedException | IOException interruptedException) {}
        }

        Transition transition = null;
        switch (direction) {
            case LEFT: transition = moveLeft(); break;
            case DOWN: transition = moveDown(); break;
            case UP: transition = moveUp(); break;
            case RIGHT: transition = moveRight(); break;
        }
        for (int i = 0; i < pane.getChildren().size(); i++) {
            if(pane.getChildren().get(i) instanceof Tile)
                ((Tile) pane.getChildren().get(i)).isJustChanged = false;
        }

        int[][] current = getField();
        boolean b = false;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (field[i][j] != current[i][j]) { b = true;break; }
            }
        }

        boolean finalB = b;
        transition.setOnFinished(event -> {
            if(finalB) generateTile();
            Transition pause = new PauseTransition(Duration.millis(10));
            pause.setOnFinished(event1 -> makeMove());
            pause.play();
        });
        transition.play();
    }

    @FXML
    private void handleOnKeyReleased(KeyEvent event) {
        isPressed = false;
    }

    @FXML
    private void handleOnKeyPressed(KeyEvent event) {
        if(!isPressed && gameMode == 2) {
            int [][] previous = getField();

            Transition transition = null;
            if (event.getCode() == KeyCode.RIGHT) transition = moveRight();
            if (event.getCode() == KeyCode.LEFT) transition = moveLeft();
            if (event.getCode() == KeyCode.UP) transition = moveUp();
            if (event.getCode() == KeyCode.DOWN) transition = moveDown();

            isPressed = true;
            for (int i = 0; i < pane.getChildren().size(); i++) {
                if(pane.getChildren().get(i) instanceof Tile)
                    ((Tile) pane.getChildren().get(i)).isJustChanged = false;
            }

            int[][] current = getField();
            boolean b = false;
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    if (previous[i][j] != current[i][j]) { b = true;break; }
                }
            }

            boolean finalB = b;
            if(transition != null) {
                transition.setOnFinished(event1 -> {
                    if(finalB) generateTile();
                });
                transition.play();
            }
        }
    }

    private Transition moveLeft() {
        ParallelTransition par = new ParallelTransition();
        for (int i = 0; i < size; i++) {
            for (int j = 1; j < size; j++) {
                if(tiles[i][j] != null &&
                        (tiles[i][j-1] == null || tiles[i][j-1].value == tiles[i][j].value)) {
                    SequentialTransition seq = new SequentialTransition();
                    Tile tile = tiles[i][j];
                    boolean b = false;
                    int x = j;
                    for (int k = j; k > 0; k--) {
                        if (tiles[i][k - 1] == null) {
                            tiles[i][k - 1] = tiles[i][k];
                            tiles[i][k] = null;
                            x--;
                        } else if(tiles[i][k-1].isJustChanged) {
                            k = 0;
                        } else if (!b && tiles[i][k - 1].value == tiles[i][k].value) {
                            tiles[i][k - 1].value *= 2;
                            tiles[i][k-1].isJustChanged = true;
                            gameScore += tiles[i][k-1].value;
                            Timeline fade = changeTile(tiles[i][k-1], tiles[i][k-1].value);
                            seq.getChildren().add(fade);
                            x--;
                            tiles[i][k] = null;
                            k = 0;
                            b = true;
                        }
                    }

                    Timeline translate = translate(tile, x, i);
                    if(b) {
                        translate.setOnFinished(event -> {
                            pane.getChildren().remove(tile);
                        });
                    }
                    seq.getChildren().add(0, translate);
                    par.getChildren().add(seq);
                }
            }
        }
        return par;
    }

    private Transition moveRight() {
        ParallelTransition par = new ParallelTransition();
        for (int i = 0; i < size; i++) {
            for (int j = size-2; j >= 0; j--) {
                if(tiles[i][j] != null &&
                        (tiles[i][j+1] == null || tiles[i][j+1].value == tiles[i][j].value)) {
                    SequentialTransition seq = new SequentialTransition();
                    Tile tile = tiles[i][j];
                    boolean b = false;
                    int x = j;
                    for (int k = j; k < size-1; k++) {
                        if (tiles[i][k + 1] == null) {
                            tiles[i][k + 1] = tiles[i][k];
                            tiles[i][k] = null;
                            x++;
                        } else if(tiles[i][k+1].isJustChanged) {
                            k = size;
                        } else if (!b && tiles[i][k + 1].value == tiles[i][k].value) {
                            tiles[i][k + 1].value *= 2;
                            tiles[i][k+1].isJustChanged = true;
                            gameScore += tiles[i][k+1].value;
                            Timeline fade = changeTile(tiles[i][k+1], tiles[i][k+1].value);
                            seq.getChildren().add(fade);
                            x++;
                            tiles[i][k] = null;
                            k = size;
                            b = true;
                        }
                    }

                    Timeline translate = translate(tile, x, i);
                    if(b) {
                        translate.setOnFinished(event -> {
                            pane.getChildren().remove(tile);
                        });
                    }
                    seq.getChildren().add(0, translate);
                    par.getChildren().add(seq);
                }
            }
        }
        return par;
    }

    private Transition moveUp() {
        ParallelTransition par = new ParallelTransition();
        for (int i = 0; i < size; i++) {
            for (int j = 1; j < size; j++) {
                if(tiles[j][i] != null &&
                        (tiles[j-1][i] == null || tiles[j-1][i].value == tiles[j][i].value)) {
                    SequentialTransition seq = new SequentialTransition();
                    Tile tile = tiles[j][i];
                    boolean b = false;
                    int y = j;
                    for (int k = j; k > 0; k--) {
                        if (tiles[k-1][i] == null) {
                            tiles[k-1][i] = tiles[k][i];
                            tiles[k][i] = null;
                            y--;
                        } else if(tiles[k-1][i].isJustChanged) {
                            k = 0;
                        } else if (!b && tiles[k-1][i].value == tiles[k][i].value) {
                            tiles[k-1][i].value *= 2;
                            tiles[k-1][i].isJustChanged = true;
                            gameScore += tiles[k-1][i].value;
                            Timeline fade = changeTile(tiles[k-1][i], tiles[k-1][i].value);
                            seq.getChildren().add(fade);
                            y--;
                            tiles[k][i] = null;
                            k = 0;
                            b = true;
                        }
                    }

                    Timeline translate = translate(tile, i, y);
                    if(b) {
                        translate.setOnFinished(event -> {
                            pane.getChildren().remove(tile);
                        });
                    }
                    seq.getChildren().add(0, translate);
                    par.getChildren().add(seq);
                }
            }
        }
        return par;
    }

    private Transition moveDown() {
        ParallelTransition par = new ParallelTransition();
        for (int i = 0; i < size; i++) {
            for (int j = size-2; j >= 0; j--) {
                if(tiles[j][i] != null &&
                        (tiles[j+1][i] == null || tiles[j+1][i].value == tiles[j][i].value)) {
                    SequentialTransition seq = new SequentialTransition();
                    Tile tile = tiles[j][i];
                    boolean b = false;
                    int y = j;
                    for (int k = j; k < size-1; k++) {
                        if (tiles[k+1][i] == null) {
                            tiles[k+1][i] = tiles[k][i];
                            tiles[k][i] = null;
                            y++;
                        } else if(tiles[k+1][i].isJustChanged) {
                            k = size;
                        } else if (!b && tiles[k+1][i].value == tiles[k][i].value) {
                            tiles[k+1][i].value *= 2;
                            tiles[k+1][i].isJustChanged = true;
                            gameScore += tiles[k+1][i].value;
                            Timeline fade = changeTile(tiles[k+1][i], tiles[k+1][i].value);
                            seq.getChildren().add(fade);
                            y++;
                            tiles[k][i] = null;
                            k = size;
                            b = true;
                        }
                    }

                    Timeline translate = translate(tile, i, y);
                    if(b) {
                        translate.setOnFinished(event -> {
                            pane.getChildren().remove(tile);
                        });
                    }
                    seq.getChildren().add(0, translate);
                    par.getChildren().add(seq);
                }
            }
        }
        return par;
    }

    private Timeline translate(Group group, int x, int y) {
        return new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(group.translateXProperty(), group.getTranslateX())),
                new KeyFrame(Duration.millis(100), new KeyValue(group.translateXProperty(), (x+1)*tileOffset + x*tileSize)),
                new KeyFrame(Duration.ZERO, new KeyValue(group.translateYProperty(), group.getTranslateY())),
                new KeyFrame(Duration.millis(100), new KeyValue(group.translateYProperty(), (y+1)*tileOffset + y*tileSize))
        );
    }

    private Timeline changeTile(Tile tile, int value) {
        return new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(tile.rectangle.fillProperty(), tile.rectangle.getFill())),
                new KeyFrame(Duration.millis(50), new KeyValue(tile.rectangle.fillProperty(), Util.COLORS.get(value))),
                new KeyFrame(Duration.ZERO, new KeyValue(tile.label.textProperty(), tile.label.getText())),
                new KeyFrame(Duration.millis(20), new KeyValue(tile.label.textProperty(), String.valueOf(value))),
                new KeyFrame(Duration.ZERO, new KeyValue(tile.label.textFillProperty(), tile.label.getTextFill())),
                new KeyFrame(Duration.millis(20), new KeyValue(tile.label.textFillProperty(), Util.fontColor(value))),
                new KeyFrame(Duration.ZERO, new KeyValue(scoreLabel.textProperty(), scoreLabel.getText())),
                new KeyFrame(Duration.millis(20), new KeyValue(scoreLabel.textProperty(), "Score\n" + gameScore)),
                new KeyFrame(Duration.ZERO, new KeyValue(tile.label.fontProperty(), tile.label.getFont())),
                new KeyFrame(Duration.millis(20), new KeyValue(tile.label.fontProperty(), Util.font(value, size))),
                new KeyFrame(Duration.ZERO, new KeyValue(tile.rectangle.scaleXProperty(), 1)),
                new KeyFrame(Duration.millis(50), new KeyValue(tile.rectangle.scaleXProperty(), 1.2)),
                new KeyFrame(Duration.millis(100), new KeyValue(tile.rectangle.scaleXProperty(), 1)),
                new KeyFrame(Duration.ZERO, new KeyValue(tile.rectangle.scaleYProperty(), 1)),
                new KeyFrame(Duration.millis(50), new KeyValue(tile.rectangle.scaleYProperty(), 1.2)),
                new KeyFrame(Duration.millis(100), new KeyValue(tile.rectangle.scaleYProperty(), 1))
        );
    }

    private void generateTile() {
        int tilePos;
        do {
            tilePos = new Random().nextInt(size*size);
        } while (tiles[tilePos/size][tilePos%size] != null);
        int x = tilePos%size;
        int y = tilePos/size;

        int value = Math.random() < 0.9 ? 2 : 4;
        tiles[y][x] = new Tile(x, y, value, size);
        changeTile(tiles[y][x], value).play();

        pane.getChildren().add(tiles[y][x]);
    }

    private int[][] getField() {
        int[][] a = new int[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if(tiles[i][j] != null) a[i][j] = tiles[i][j].value;
                else a[i][j] = 0;
            }
        }
        return a;
    }

}
