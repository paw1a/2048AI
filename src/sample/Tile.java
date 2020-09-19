package sample;

import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

public class Tile extends Group {

    public Rectangle rectangle;
    public Label label;
    public int value;
    public boolean isJustChanged = false;

    public Tile(int x, int y, int value, int size) {
        super();
        this.value = value;
        int tileOffset = 14 - size;
        int tileSize = (400 - (tileOffset * (size + 1))) / size;

        rectangle = new Rectangle();
        rectangle.setFill(Util.COLORS.get(value));
        rectangle.setWidth(tileSize);
        rectangle.setHeight(tileSize);
        rectangle.setArcWidth(10);
        rectangle.setArcHeight(10);

        label = new Label(String.valueOf(value));
        label.setFont(Util.font(value, size));
        label.setTextFill(Util.fontColor(value));
        label.setPrefSize(tileSize, tileSize);
        label.setAlignment(Pos.CENTER);

        this.getChildren().addAll(rectangle, label);
        this.setTranslateX((x+1)* tileOffset + x* tileSize);
        this.setTranslateY((y+1)* tileOffset + y* tileSize);
    }
}
