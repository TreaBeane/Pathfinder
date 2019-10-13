package io.treabeane.pathfinder;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    Parent root = FXMLLoader.load(getClass().getResource("fxml/application.fxml"));
    primaryStage.setTitle("PathFinder");
    primaryStage.setScene(new Scene(root, 720, 480));
    primaryStage.getScene().getStylesheets().add(getClass().getResource("styles/style.css").toExternalForm());
    primaryStage.setResizable(false);
    primaryStage.show();
  }

}
