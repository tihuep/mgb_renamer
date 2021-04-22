package ch.mgb.younique.renamer.controller;

import ch.mgb.younique.renamer.Main;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
import java.util.ResourceBundle;

public class ErrorController implements Initializable {

    @FXML
    public Button errorBtnBack;
    public Button errorBtnClose;
    public Label errorLabelDisplay;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        errorLabelDisplay.setText(Main.renamerModel.errorMessage);
        setButtonHandler();
    }

    private void setButtonHandler(){
        errorBtnClose.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Stage stage = (Stage) errorBtnClose.getScene().getWindow();
                stage.close();
            }
        });

        errorBtnBack.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {

                try {
                    Parent root = FXMLLoader.load(getClass().getResource("../view/MainView.fxml"));
                    Stage primaryStage = new Stage();
                    primaryStage.setTitle("Renamer");
                    primaryStage.setScene(new Scene(root, 600, 400));
                    primaryStage.setResizable(false);
                    primaryStage.show();
                }catch (IOException e){
                    e.printStackTrace();
                }

                Stage stage = (Stage) errorBtnBack.getScene().getWindow();
                stage.close();
            }
        });
    }
}
