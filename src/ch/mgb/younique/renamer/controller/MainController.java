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
import javafx.scene.control.TextField;
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
    public TextField mainInpColA;
    public TextField mainInpColB;
    public TextField mainInpRowStart;
    public TextField mainInpRowEnd;
    public Label mainLabelExcelSelect;
    public Label mainLabelDirSelect;
    public Label mainErrorDisplay;

    private File selectedExcelFile = null;
    private File selectedImgDirectory = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        mainInpColA.setText("I");
        mainInpColB.setText("A");;
        mainInpRowStart.setText("2");
        mainInpRowEnd.setText("414");
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

                    mainInpColA.setDisable(false);
                    mainInpColB.setDisable(false);
                    mainInpRowStart.setDisable(false);
                    mainInpRowEnd.setDisable(false);
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

                    String errorMessageFooter = "\nBitte versuchen Sie es erneut!";
                    try {
                        readExcelParams();
                        Main.renamerModel.startRenaming(selectedExcelFile, selectedImgDirectory);

                        //TODO

                    }catch (IOException ioException){
                        Main.renamerModel.conclusionMessage = "Ein unbekannter Fehler ist aufgetreten." + errorMessageFooter;
                    }catch (Exception exception){
                        Main.renamerModel.conclusionMessage = exception.getMessage() + errorMessageFooter;
                    }

                    try {
                        Stage secondaryStage = new Stage();
                        Parent root = FXMLLoader.load(getClass().getResource("../view/ConclusionView.fxml"));
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
                        displayError("Bitte wählen Sie ein Excel-File aus!");
                    }else if (selectedExcelFile != null && selectedImgDirectory == null){
                        displayError("Bitte wählen Sie ein Verzeichnis aus!");
                    }else if (selectedExcelFile == null && selectedImgDirectory == null){
                        displayError("Bitte wählen Sie ein Excel-File und ein Verzeichnis aus!");
                    }
                }
            }
        });
    }

    private void displayError(String message) {
        mainErrorDisplay.setText(message);
    }

    private boolean readExcelParams() {
        String colA = mainInpColA.getText();
        String colB = mainInpColB.getText();
        String rowStart = mainInpRowStart.getText();
        String rowEnd = mainInpRowEnd.getText();

        if (colA.matches("^\\D+$") && colB.matches("^\\D+$")){
            if (rowStart.matches("^\\d+$") && rowEnd.matches("^\\d+$")) {

                String str = colA;
                char[] ch  = str.toCharArray();
                for(char c : ch){
                    int temp = (int)c;
                    //https://www.geeksforgeeks.org/check-whether-the-given-character-is-in-upper-case-lower-case-or-non-alphabetic-character/
                    int temp_integer = 64; //for upper case
                    if (c >= 'a' && c <= 'z') {
                        temp_integer = 96; // for lower case
                    }
                    //https://stackoverflow.com/questions/8879714/how-to-get-numeric-position-of-alphabets-in-java
                    if(temp<=122 & temp>=65)
                        Main.renamerModel.excelColA = Main.renamerModel.excelColA * 26 + temp-temp_integer;
                }
                Main.renamerModel.excelColA -= Main.renamerModel.excelColA > 0 ? 1 : 0;

                str = colB;
                ch  = str.toCharArray();
                for(char c : ch){
                    int temp = (int)c;
                    //https://www.geeksforgeeks.org/check-whether-the-given-character-is-in-upper-case-lower-case-or-non-alphabetic-character/
                    int temp_integer = 64; //for upper case
                    if (c >= 'a' && c <= 'z') {
                        temp_integer = 96; // for lower case
                    }
                    //https://stackoverflow.com/questions/8879714/how-to-get-numeric-position-of-alphabets-in-java
                    if(temp<=122 & temp>=65)
                        Main.renamerModel.excelColB = Main.renamerModel.excelColB * 26 + temp-temp_integer;
                }
                Main.renamerModel.excelColB -= Main.renamerModel.excelColB > 0 ? 1 : 0;

                int rowStartInt = Integer.parseInt(rowStart);
                int rowEndInt = Integer.parseInt(rowEnd);
                if (rowStartInt > 0 && rowEndInt > 0) {
                    if (rowStartInt <= rowEndInt){
                        Main.renamerModel.excelRowStart = rowStartInt - 1;
                        Main.renamerModel.excelRowEnd = rowEndInt - 1;
                        return true;
                    }else{
                        displayError("Bitte geben Sie Zahlen über 0 für den Reihenbereich an!");
                    }
                }else {
                    displayError("Bitte geben Sie Zahlen über 0 für den Reihenbereich an!");
                }
            }else {
                displayError("Bitte geben Sie Zahlen für den Reihenbereich an!");
            }
        }else {
            displayError("Bitte geben Sie Buchstaben für die Spaltenauswahl an!");
        }
        return false;
    }
}
