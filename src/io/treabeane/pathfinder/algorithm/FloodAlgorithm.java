package io.treabeane.pathfinder.algorithm;

import io.treabeane.pathfinder.ApplicationController;
import io.treabeane.pathfinder.block.Block;
import io.treabeane.pathfinder.block.BlockManager;
import io.treabeane.pathfinder.block.BlockState;
import javafx.application.Platform;
import javafx.scene.control.Label;

import java.util.*;

public class FloodAlgorithm extends Algorithm {

  private Map<Block, Integer> blockCost = new HashMap<>();
  private Queue<Block> openQueue = new PriorityQueue<>(255, Comparator.comparingInt(this::getBlockValue));
  private List<Block> closeList = new LinkedList<>();

  private long timeAtMillis;

  public FloodAlgorithm(ApplicationController controller) {
    super(controller);
  }

  @Override
  public TimerTask loop() {
    timeAtMillis = System.currentTimeMillis();
    Optional<Block> optionalStartBlock = BlockManager.findBlocksByState(BlockState.START).stream().findFirst();

    if (optionalStartBlock.isPresent()){
      Block startBlock = optionalStartBlock.get();
      setBlockCost(startBlock, 0);

      openQueue.addAll(BlockManager.getNeighbor(startBlock, true));

      return new TimerTask() {
        public void run() {
          if (!openQueue.isEmpty()){
            Block target = openQueue.poll();

            if (target.getState().equals(BlockState.FINISH)){
              finish();
              this.cancel();
              return;
            }

            BlockManager.getAllNeighbor(target).forEach(block -> {
              if (openQueue.contains(block) || closeList.contains(block)){
                return;
              }

              if (block.getState().equals(BlockState.EMPTY) || block.getState().equals(BlockState.FINISH)){
                block.setParentBlock(target);
                setBlockCost(block, getBlockValue(block.getParentBlock()) + 1);

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
    Platform.runLater(() -> getController().messageLabel.setText(String.format("Finished in %s ms", (finishTimeAtMillis))));

    Optional<Block> optionalFinishBlock =  BlockManager.findBlocksByState(BlockState.FINISH).stream().findFirst();

    Queue<Block> blockQueue = new LinkedList<>();
    optionalFinishBlock.ifPresent(block -> blockQueue.offer(block.getParentBlock()));

    while (!blockQueue.isEmpty()){
      Block pathBlock = blockQueue.poll();

      if (pathBlock == null)return;

      if (!pathBlock.getState().equals(BlockState.START)) {
        pathBlock.setState(BlockState.FOUND);
        blockQueue.offer(pathBlock.getParentBlock());
      }
    }

  }

  @Override
  public void reset() {
    blockCost.clear();
    openQueue.clear();
    closeList.clear();

    Platform.runLater(() -> getController().messageLabel.setText(""));

    if (loop() != null) {
      loop().cancel();
    }
    BlockManager.getBlockSet().stream().filter(block -> block.getState().isSearchable()).forEach(block -> {
      if (!block.getState().equals(BlockState.FINISH) && !block.getState().equals(BlockState.START)) {
        block.setState(BlockState.EMPTY);
      }
    });
  }

  private void setBlockCost(Block block, int cost) {
    blockCost.put(block, cost);
  }

  private int getBlockValue(Block block) {
    return blockCost.getOrDefault(block, 0);
  }
}
