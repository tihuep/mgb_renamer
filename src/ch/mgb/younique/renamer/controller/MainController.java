package ch.mgb.younique.renamer.controller;

import ch.mgb.younique.renamer.Main;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
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
        mainInpColA.setText("A");
        mainInpColB.setText("B");;
        mainInpRowStart.setText("2");
        mainInpRowEnd.setText("100");
        mainLabelExcelSelect.setText(Main.renamerModel.selectedExcelFile != null ? Main.renamerModel.selectedExcelFile.getName() : "");
        mainLabelDirSelect.setText(Main.renamerModel.selectedImgDirectory != null ? Main.renamerModel.selectedImgDirectory.getName() : "");
        setButtonHandlers();

        //prevents digit input into column fields
        mainInpColA.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (mainInpColA.getText().matches(".*\\d+.*")){
                    mainInpColA.setText(mainInpColA.getText().replaceAll("\\d",""));
                }
            }
        });
        mainInpColB.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (mainInpColB.getText().matches(".*\\d+.*")){
                    mainInpColB.setText(mainInpColB.getText().replaceAll("\\d",""));
                }
            }
        });

        //prevents non-digit input into row fields
        mainInpRowStart.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (mainInpRowStart.getText().matches(".*\\D+.*")){
                    mainInpRowStart.setText(mainInpRowStart.getText().replaceAll("\\D",""));
                }
            }
        });
        mainInpRowEnd.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (mainInpRowEnd.getText().matches(".*\\D+.*")){
                    mainInpRowEnd.setText(mainInpRowEnd.getText().replaceAll("\\D",""));
                }
            }
        });
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
                    Main.renamerModel.selectedExcelFile = selectedFile;

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
                    mainLabelDirSelect.setText(selectedDirectory.getName());
                    Main.renamerModel.selectedImgDirectory = selectedDirectory;
                }
            }
        });

        this.mainBtnGo.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (Main.renamerModel.selectedExcelFile != null && Main.renamerModel.selectedImgDirectory != null){

                    String errorMessageFooter = "\nBitte versuchen Sie es erneut!";
                    try {
                        readExcelParams();
                        int notRenamedCount = Main.renamerModel.startRenaming(Main.renamerModel.selectedExcelFile, Main.renamerModel.selectedImgDirectory);

                        Main.renamerModel.conclusionMessage = "Die Bilder konnten erfolgreich umbenannt werden.";
                        Main.renamerModel.conclusionMessage = notRenamedCount != 0 ? "Alle bis auf " + notRenamedCount + " Bilder konnten erfolgreich \numbenannt werden." : "Alle Bilder konnten erfolgreich umbenannt werden.";
                    }catch (IOException ioException){
                        Main.renamerModel.conclusionMessage = "Ein unbekannter Fehler ist aufgetreten." + errorMessageFooter;
                    }catch (Exception exception) {
                        Main.renamerModel.conclusionMessage = exception.getMessage() + errorMessageFooter;
                    }
                    Stage secondaryStage = new Stage();

                    Parent root = createConclusionRoot();

                    secondaryStage.setTitle("Renamer");
                    secondaryStage.setScene(new Scene(root, 500, 200));
                    secondaryStage.setResizable(false);
                    secondaryStage.show();
                }else {
                    if (Main.renamerModel.selectedExcelFile == null && Main.renamerModel.selectedImgDirectory != null){
                        displayError("Bitte wählen Sie ein Excel-File aus!");
                    }else if (Main.renamerModel.selectedExcelFile != null && Main.renamerModel.selectedImgDirectory == null){
                        displayError("Bitte wählen Sie ein Verzeichnis aus!");
                    }else if (Main.renamerModel.selectedExcelFile == null && Main.renamerModel.selectedImgDirectory == null){
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
                Main.renamerModel.excelColA = 0;
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
                Main.renamerModel.excelColB = 0;
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

    private Parent createConclusionRoot(){
        Label conclusionTitle = new Label("Artikel Renamer");
        conclusionTitle.setFont(new Font("size", 20));

        Label errorLabelDisplay = new Label(Main.renamerModel.conclusionMessage);
        errorLabelDisplay.setFont(new Font("size", 15));

        Button errorBtnClose = new Button("Schliessen");
        errorBtnClose.setFont(new Font("size", 13));

        errorBtnClose.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Stage stage = (Stage) errorBtnClose.getScene().getWindow();
                stage.close();
            }
        });

        HBox separator = new HBox();
        separator.setMinHeight(20);
        separator.setMaxHeight(20);

        HBox separator1 = new HBox();
        separator1.setMinHeight(20);
        separator1.setMaxHeight(20);

        HBox separator2 = new HBox();
        separator2.setMinHeight(20);
        separator2.setMaxHeight(20);

        HBox horizontalSeparator = new HBox();
        horizontalSeparator.setMinWidth(20);
        horizontalSeparator.setMinWidth(20);

        Parent root = new HBox(horizontalSeparator, new VBox(separator, conclusionTitle, separator1, errorLabelDisplay, separator2, errorBtnClose));
        return root;
    }
}
