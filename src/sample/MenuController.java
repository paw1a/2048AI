package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class MenuController implements Initializable {

    public Button next;
    public Label size;
    public Label mode;

    private List<String> sizeList;
    private List<String> modeList;
    private int currentMode;
    private int currentSize;

    @FXML
    public void startGame() throws IOException {
        Scene scene = next.getScene();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/game.fxml"));
        Parent root = loader.load();
        GameController gameController = loader.getController();
        gameController.init(Integer.parseInt(String.valueOf(size.getText().charAt(0))),
                mode.getText().equals("AI") ? 1 : 2);

        ((Stage)scene.getWindow()).setScene(new Scene(root, 500, 600));
    }

    @FXML
    public void next() {
        if(++currentSize > 4) currentSize--;
        size.setText(sizeList.get(currentSize));
    }

    @FXML
    public void previous() {
        if(--currentSize < 0) currentSize = 0;
        size.setText(sizeList.get(currentSize));
    }

    @FXML
    public void nextMode() {
        if(++currentMode > 1) currentMode--;
        mode.setText(modeList.get(currentMode));
    }

    @FXML
    public void previousMode() {
        if(--currentMode < 0) currentMode = 0;
        mode.setText(modeList.get(currentMode));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        sizeList = Arrays.asList("3x3", "4x4", "5x5", "6x6", "8x8");
        modeList = Arrays.asList("PLAYER", "AI");
        currentSize = 1;
    }
}
