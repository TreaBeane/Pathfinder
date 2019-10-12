package io.treabeane.pathfinder;

import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;

import javax.xml.soap.Node;
import java.util.Timer;
import java.util.TimerTask;

public abstract class Algorithm {
  
  private final Pane noPathPane;

  public Algorithm(Pane noPathPane) {
    this.noPathPane = noPathPane;
  }

  public abstract TimerTask loop();

  public abstract void finish();

  public Pane getNoPathPane() {
    return noPathPane;
  }

  public abstract void reset();



}
