package io.treabeane.pathfinder;

import io.treabeane.pathfinder.algorithm.Algorithm;
import io.treabeane.pathfinder.algorithm.FloodAlgorithm;
import io.treabeane.pathfinder.block.Block;
import io.treabeane.pathfinder.block.BlockManager;
import io.treabeane.pathfinder.block.BlockState;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.util.*;

public class ApplicationController {

  @FXML
  public GridPane noPathPane;

  @FXML
  private GridPane graphPane;

  @FXML
  private ChoiceBox<String> algorithmBox;

  @FXML
  private Slider speedSlider;

  private long speed = 1L;

  private Algorithm algorithm;

  @FXML
  protected void initialize() {
    noPathPane.setVisible(false);
    algorithm = new FloodAlgorithm(noPathPane);
    createGraph();

    speedSlider.valueProperty().addListener((observable, oldValue, newValue) -> this.speed = newValue.intValue() + 1);

    algorithmBox.setValue("Flood");
    algorithmBox.setItems(FXCollections.observableArrayList("Flood", "A*"));
  }


  @FXML
  private void runAlgorithm(ActionEvent e) {
    algorithm.reset();
    new Timer().schedule(algorithm.loop(), 0L, this.speed);
  }

  @FXML
  private void reset(ActionEvent event){
    algorithm.reset();
  }

  private void createGraph(){
    for (int x = 0; x < 23; x++) {
      for (int y = 0; y < 10; y++) {
        AnchorPane pane = new AnchorPane();
        Block block = new Block(pane, x, y);
        pane.setId("empty");
        pane.setMinSize(30, 30);
        pane.setMaxSize(30, 30);

        {
          Label label = new Label("");
          pane.getChildren().add(label);
        }

        pane.setOnMouseClicked(event -> {
          if (block.getState().equals(BlockState.EMPTY) && BlockManager.findBlocksByState(BlockState.START).isEmpty()) {
            block.setState(BlockState.START);
          } else if (block.getState().equals(BlockState.START) || BlockManager.findBlocksByState(BlockState.FINISH).isEmpty()) {
            block.setState(BlockState.FINISH);
          } else if (block.getState().equals(BlockState.EMPTY)) {
            block.setState(BlockState.BARRIER);
          } else {
            block.setState(BlockState.EMPTY);
          }
        });

        graphPane.add(pane, x, y);
        BlockManager.registerBlock(block);
      }
    }
  }
}
