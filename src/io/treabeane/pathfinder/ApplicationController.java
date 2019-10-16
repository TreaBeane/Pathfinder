package io.treabeane.pathfinder;

import io.treabeane.pathfinder.algorithm.AStarAlgorithm;
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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

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

    createGraph();

    speedSlider.valueProperty().addListener((observable, oldValue, newValue) -> this.speed = newValue.intValue() + 1);

    algorithmBox.setValue("Flood");
    algorithmBox.setItems(FXCollections.observableArrayList("Flood", "A*"));
  }


  @FXML
  private void runAlgorithm(ActionEvent e) {
    switch (algorithmBox.getValue()){
      case "A*": {
        algorithm = new AStarAlgorithm(noPathPane);
        break;
      }
      case "Flood":
      default:{
        algorithm = new FloodAlgorithm(noPathPane);
        break;
      }
    }

    algorithm.reset();
    new Timer().schedule(algorithm.loop(), 0L, this.speed);
  }

  @FXML
  private void reset(ActionEvent event){
    algorithm.reset();
  }

  private void createGraph(){
    for (int x = 0; x < 200; x++) {
      for (int y = 0; y < 100; y++) {
        Rectangle pane = new Rectangle();
        Block block = new Block(pane, x, y);
        pane.setId("empty");
        pane.setWidth(20);
        pane.setHeight(20);

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

        graphPane.setOnMouseEntered(event -> {
          if (block.getState().equals(BlockState.EMPTY)) {
            block.setState(BlockState.BARRIER);
            block.getPane().setFill(Color.RED);
          } else {
            block.setState(BlockState.EMPTY);
          }
        });

        graphPane.add(pane, x, y);
        BlockManager.registerBlock(block);
      }
    }

    graphPane.setOnScroll(event -> {
      double zoomFactor = 1.05;
      double deltaY = event.getDeltaY();
      if (deltaY < 0){
        zoomFactor = 2.0 - zoomFactor;
      }

      double finalZoomFactor = zoomFactor;
      BlockManager.getBlockSet().forEach(block -> {
        if (block.getPane().getWidth() <= 5 || block.getPane().getWidth() >= 50){
          return;
        }
        block.getPane().setWidth(block.getPane().getWidth() * finalZoomFactor);
        block.getPane().setHeight(block.getPane().getHeight() * finalZoomFactor);
      });
    });
  }
}
