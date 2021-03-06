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
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

import java.util.LinkedList;
import java.util.List;
import java.util.Timer;

public class ApplicationController {

  @FXML
  public Label messageLabel;
  public Label delayMessageLabel;

  @FXML
  private GridPane graphPane;

  @FXML
  private ChoiceBox<String> algorithmBox;

  @FXML
  private Slider delaySlider;

  private long delay = 1L;

  private Algorithm algorithm;

  @FXML
  protected void initialize() {
    algorithm = new FloodAlgorithm(this);
    delayMessageLabel.setText("Delay: " + delay + "ms");

    createGraph();

    delaySlider.valueProperty().addListener((observable, oldValue, newValue) -> {
      this.delay = newValue.intValue() + 1;
      delayMessageLabel.setText("Delay: " + this.delay + "ms");
    });

    algorithmBox.setValue("Flood");
    algorithmBox.setItems(FXCollections.observableArrayList("Flood", "A*"));
  }


  @FXML
  private void runAlgorithm(ActionEvent e) {
    switch (algorithmBox.getValue()){
      case "A*": {
        algorithm = new AStarAlgorithm(this);
        break;
      }
      case "Flood":
      default:{
        algorithm = new FloodAlgorithm(this);
        break;
      }
    }

    algorithm.reset();
    new Timer().schedule(algorithm.loop(), 0L, this.delay);
  }

  @FXML
  private void reset(ActionEvent event){
    algorithm.reset();
  }

  private void createGraph(){
    for (int x = 0; x < 200; x++) {
      for (int y = 0; y < 100; y++) {
        Rectangle rectangle = new Rectangle();
        Block block = new Block(rectangle, x, y);
        rectangle.setId("empty");
        rectangle.setWidth(20);
        rectangle.setHeight(20);

        rectangle.setOnMouseClicked(event -> {
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

        rectangle.setStroke(Color.BLACK);
        rectangle.setStrokeType(StrokeType.INSIDE);
        rectangle.setStrokeWidth(.5);

        graphPane.add(rectangle, x, y);
        BlockManager.registerBlock(block);
      }
    }
  }

  @FXML
  public void random(ActionEvent event) {
    List<Block> blocks = new LinkedList<>(BlockManager.findBlocksByState(BlockState.EMPTY));
    algorithm.reset();
    blocks.addAll(BlockManager.findBlocksByState(BlockState.BARRIER));
    System.out.println(Math.random());
    blocks.forEach(block -> {
      double random = Math.random();
      if (random <= .3){
        block.setState(BlockState.BARRIER);
      }else {
        block.setState(BlockState.EMPTY);
      }
    });
  }

}
