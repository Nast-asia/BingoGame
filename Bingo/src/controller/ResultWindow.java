package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

public class ResultWindow {
    @FXML
    private Text textResult;

    @FXML
    private Button againButton;

    @FXML
    private Button exitButton;


    // Действия, которые выполняются при открытии окна с результатом
    @FXML
    void initialize() {
        againButton.setOnAction(event -> {
            Window window = againButton.getScene().getWindow();
            window.hide();
            window.fireEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSE_REQUEST));
        });

        exitButton.setOnAction(event -> {
            System.exit(0);
        });
    }

    public void setResultText(String text) {
        textResult.setText(text);
    }
}