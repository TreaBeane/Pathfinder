package io.treabeane.pathfinder.algorithm;

import io.treabeane.pathfinder.ApplicationController;
import io.treabeane.pathfinder.block.Block;
import io.treabeane.pathfinder.block.BlockManager;
import io.treabeane.pathfinder.block.BlockState;
import javafx.application.Platform;
import javafx.scene.control.Label;

import java.util.*;

public class AStarAlgorithm extends Algorithm {

  private final int STRAIGHT_COST = 10;
  private final int DIAGONAL_COST = 14;

  private Map<Block, Integer> gCost = new LinkedHashMap<>();
  private Map<Block, Integer> hCost = new LinkedHashMap<>();

  private Queue<Block> openQueue = new PriorityQueue<>(255, Comparator.comparingInt(this::getFCost));
  private List<Block> closeList = new LinkedList<>();

  private long timeAtMillis;

  public AStarAlgorithm(ApplicationController controller) {
    super(controller);
  }

  @Override
  public TimerTask loop() {
    timeAtMillis = System.currentTimeMillis();
    Optional<Block> optionalStartBlock = BlockManager.findBlocksByState(BlockState.START).stream().findFirst();
    Optional<Block> optionalFinishBlock =  BlockManager.findBlocksByState(BlockState.FINISH).stream().findFirst();

    if (optionalStartBlock.isPresent() && optionalFinishBlock.isPresent()){
      Block startBlock = optionalStartBlock.get();
      Block finishBlock = optionalFinishBlock.get();

      setGCost(startBlock, 0);
      setHCost(startBlock, calculateHCost(startBlock, finishBlock, 1));

      openQueue.offer(startBlock);

      return new TimerTask() {
        public void run() {
          if (!openQueue.isEmpty()) {
            Block target = openQueue.poll();

            if (target.getState().equals(BlockState.FINISH)){
              finish();
              this.cancel();
              return;
            }

//            Searching Non-Diagonal Blocks
            BlockManager.getNeighbor(target, true).forEach(block -> {
              if (openQueue.contains(block) || closeList.contains(block)){
                return;
              }

              if (block.getState().equals(BlockState.EMPTY) || block.getState().equals(BlockState.FINISH)){
                block.setParentBlock(target);
                setGCost(block, getGCost(block.getParentBlock()) + STRAIGHT_COST);
                setHCost(block, calculateHCost(block, finishBlock, STRAIGHT_COST));

                openQueue.add(block);
              }
            });

//            Searching Diagonal Blocks
            BlockManager.getNeighbor(target, false).forEach(block -> {
              if (openQueue.contains(block) || closeList.contains(block)){
                return;
              }

              if (block.getState().equals(BlockState.EMPTY) || block.getState().equals(BlockState.FINISH)){
                block.setParentBlock(target);
                setGCost(block, getGCost(block.getParentBlock()) + DIAGONAL_COST);
                setHCost(block, calculateHCost(block, finishBlock, DIAGONAL_COST));

                openQueue.add(block);
              }
            });

            closeList.add(target);
          }

          openQueue.forEach(block -> {
            if (block.getState().equals(BlockState.START) || block.getState().equals(BlockState.FINISH)){
              return;
            }
            block.setState(BlockState.SEARCHING);
          });

          closeList.forEach(block -> {
            if (block.getState().equals(BlockState.START) || block.getState().equals(BlockState.FINISH)){
              return;
            }
            block.setState(BlockState.SEARCHED);
          });
        }
      };
    }
    return null;
  }


  @Override
  public void finish() {
    long finishTimeAtMillis = System.currentTimeMillis() - timeAtMillis;
    Optional<Block> optionalFinishBlock =  BlockManager.findBlocksByState(BlockState.FINISH).stream().findFirst();

    Queue<Block> blockQueue = new LinkedList<>();
    optionalFinishBlock.ifPresent(block -> blockQueue.offer(block.getParentBlock()));

    while (!blockQueue.isEmpty()){
      Block pathBlock = blockQueue.poll();

      if (!pathBlock.getState().equals(BlockState.START)) {
        pathBlock.setState(BlockState.FOUND);
        blockQueue.offer(pathBlock.getParentBlock());
      }
    }

    Platform.runLater(() -> getController().messageLabel.setText(String.format("Finished in %s ms", (finishTimeAtMillis))));
  }

  @Override
  public void reset() {
    gCost.clear();
    hCost.clear();
    openQueue.clear();
    closeList.clear();

    Platform.runLater(() -> getController().messageLabel.setText(""));

    loop().cancel();
    BlockManager.getBlockSet().stream().filter(block -> block.getState().isSearchable()).forEach(block -> {
      if (!block.getState().equals(BlockState.FINISH) && !block.getState().equals(BlockState.START)) {
        block.setState(BlockState.EMPTY);
      }
    });

  }

  private void setGCost(Block block, int cost){
    gCost.put(block, cost);
  }

  private int getGCost(Block block){
    return gCost.getOrDefault(block, -1);
  }

  private void setHCost(Block block, int cost){
    hCost.put(block, cost);
  }

  private int getHCost(Block block){
    return hCost.getOrDefault(block, -1);
  }

  private int calculateHCost(Block block, Block finishBlock, int cost){
    int bX = block.getX();
    int bY = block.getY();

    int fX = finishBlock.getX();
    int fY = finishBlock.getY();

    return cost * (Math.abs(bX - fX) + Math.abs(bY - fY));
  }

  private int getFCost(Block block){
    return getGCost(block) + getHCost(block);
  }

}
