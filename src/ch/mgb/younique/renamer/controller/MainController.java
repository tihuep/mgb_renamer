package ch.mgb.younique.renamer.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    public Button mainBtnCancel;
    public Button mainBtnExcelSelect;
    public Button mainBtnDirSelect;
    public Button mainBtnGo;
    public Label mainLabelExcelSelect;
    public Label mainLabelDirSelect;
    public Label mainErrorDisplay;

    private File selectedExcelFile = null;
    private File selectedImgDirectory = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        setButtonHandlers();
    }

    private void setButtonHandlers(){
        this.mainBtnCancel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Stage stage = (Stage) mainBtnCancel.getScene().getWindow();
                stage.close();
            }
        });

        this.mainBtnExcelSelect.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                FileChooser fileChooser = new FileChooser();
                FileChooser.ExtensionFilter extFilter =
                        new FileChooser.ExtensionFilter("Excel files (*.xlsx)", "*.xlsx");
                fileChooser.getExtensionFilters().add(extFilter);
                File selectedFile = fileChooser.showOpenDialog(mainBtnExcelSelect.getScene().getWindow());
                if (selectedFile != null) {
                    mainLabelExcelSelect.setText(selectedFile.getName());
                    selectedExcelFile = selectedFile;
                }
            }
        });

        this.mainBtnDirSelect.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                DirectoryChooser directoryChooser = new DirectoryChooser();
                File selectedDirectory = directoryChooser.showDialog(mainBtnDirSelect.getScene().getWindow());
                if (selectedDirectory != null) {
                    mainLabelDirSelect.setText(selectedDirectory.getPath());
                    selectedImgDirectory = selectedDirectory;
                }
            }
        });

        this.mainBtnGo.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (selectedExcelFile != null && selectedImgDirectory != null){
                    System.out.println("GO!\n-Excel: " + selectedExcelFile.getPath() + "\n-Dir: " + selectedImgDirectory.getPath());
                    try {
                        Stage secondaryStage = new Stage();
                        Parent root = FXMLLoader.load(getClass().getResource("../view/ErrorView.fxml"));
                        secondaryStage.setTitle("Renamer");
                        secondaryStage.setScene(new Scene(root, 400, 200));
                        secondaryStage.setResizable(false);
                        secondaryStage.show();

                        Stage stage = (Stage) mainBtnGo.getScene().getWindow();
                        stage.close();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }else {
                    System.out.println("Please finish selection!");
                    if (selectedExcelFile == null && selectedImgDirectory != null){
                        mainErrorDisplay.setText("Bitte wählen Sie ein Excel-File aus!");
                    }else if (selectedExcelFile != null && selectedImgDirectory == null){
                        mainErrorDisplay.setText("Bitte wählen Sie ein Verzeichnis aus!");
                    }else if (selectedExcelFile == null && selectedImgDirectory == null){
                        mainErrorDisplay.setText("Bitte wählen Sie ein Excel-File und ein Verzeichnis aus!");
                    }
                }
            }
        });
    }
}
