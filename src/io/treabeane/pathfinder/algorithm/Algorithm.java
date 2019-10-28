package io.treabeane.pathfinder.algorithm;

import io.treabeane.pathfinder.ApplicationController;

import java.util.TimerTask;

public abstract class Algorithm {

  private final ApplicationController controller;

  public Algorithm(ApplicationController controller) {
    this.controller = controller;
  }

  public abstract TimerTask loop();

  public abstract void finish();

  public abstract void reset();

  ApplicationController getController() {
    return controller;
  }
}
