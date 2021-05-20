package ch.mgb.younique.renamer;

import ch.mgb.younique.renamer.model.RenamerModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        renamerModel.mainRoot = FXMLLoader.load(getClass().getResource("view/MainView.fxml"));
        //Parent root = FXMLLoader.load(getClass().getResource("view/MainView.fxml"));
        primaryStage.setTitle("Renamer");
        primaryStage.setScene(new Scene(renamerModel.mainRoot, 600, 400));
        primaryStage.setResizable(false);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

    public static RenamerModel renamerModel = new RenamerModel();
}
