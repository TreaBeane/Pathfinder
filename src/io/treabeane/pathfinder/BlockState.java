package io.treabeane.pathfinder;

public enum BlockState {
  EMPTY("empty", true),
  SEARCHED("searched", true),
  SEARCHING("searching", true),
  START("start", true),
  FINISH("finish", true),
  FOUND("found", true),
  BARRIER("barrier");

  final String id;
  final boolean searchable;

  BlockState(String id) {
    this.id = id;
    this.searchable = false;
  }

  BlockState(String id, boolean searchable){
    this.id = id;
    this.searchable = searchable;
  }

  public String getId() {
    return id;
  }

  public boolean isSearchable() {
    return searchable;
  }
}
