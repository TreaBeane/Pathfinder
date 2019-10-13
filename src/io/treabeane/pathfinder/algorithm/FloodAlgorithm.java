package io.treabeane.pathfinder.algorithm;

import io.treabeane.pathfinder.block.Block;
import io.treabeane.pathfinder.block.BlockManager;
import io.treabeane.pathfinder.block.BlockState;
import javafx.scene.layout.Pane;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class FloodAlgorithm extends Algorithm {

  AtomicBoolean running = new AtomicBoolean(true);
  Map<Block, Integer> blockValue = new HashMap<>();
  Set<Block> searching = new HashSet<>();
  Set<Block> searched = new HashSet<>();
  Queue<Block> blockQueue = new LinkedList<>();

  int count = 0;

  public FloodAlgorithm(Pane noPathPane) {
    super(noPathPane);
  }

  @Override
  public TimerTask loop() {
    BlockManager.findBlocksByState(BlockState.FINISH).stream().findFirst().ifPresent(finishBlock -> {
      blockQueue.add(finishBlock);
      blockValue.put(finishBlock, 0);
    });

    return new TimerTask() {
      public void run() {
//    Add blocks adjacent to finish block to block queue
        int finalCount = 0;
          BlockManager.findBlocksByState(BlockState.SEARCHING)
                  .forEach(block -> block.setState(BlockState.SEARCHED));
          Block current = blockQueue.poll();

          if (current == null){
            running.set(false);
            this.cancel();
            finish();
            return;
          }

          searching.clear();
          searching.addAll(BlockManager.getAdjacentBlocks(current, false));

          int finalCount1 = count++;
          List<Block> blocks = searching.stream().filter(block -> block.getState().isSearchable()).collect(Collectors.toList());

          blocks.forEach(block -> {
            if (searched.contains(block))return;

            if (block.getState().equals(BlockState.START)){
              running.set(false);
              this.cancel();
              finish();
              return;
            }
            blockValue.putIfAbsent(block, finalCount1);

            if (block.getState().equals(BlockState.EMPTY)) {
              block.setState(BlockState.SEARCHING);
            }

            blockQueue.add(block);

            //block.setTag(String.valueOf(blockValue.getOrDefault(block, -1)));
            searched.add(block);
          });
        }
    };
  }

  @Override
  public void finish() {
    BlockManager.findBlocksByState(BlockState.START).stream().findFirst().ifPresent(start -> {
      Block current = start;
      while (true) {
        List<Block> blockSet = BlockManager.getAdjacentBlocks(current, false).stream()
                .filter(blockValue::containsKey)
                .filter(block -> block.getState().isSearchable() && !block.getState().equals(BlockState.EMPTY))
                .sorted(Comparator.comparingInt(blockValue::get)).collect(Collectors.toCollection(LinkedList::new));

        if (blockSet.isEmpty()){
          System.out.println("There is no path.");
          getNoPathPane().setVisible(true);
          return;
        }

        current = blockSet.get(0);

//        System.out.println(String.format("Block: %s Block Value: %s", current.getState(), blockValue.getOrDefault(current, -1)));

        if (current.getState().equals(BlockState.FINISH)){
          return;
        }

        current.setState(BlockState.FOUND);
        blockSet.clear();

        BlockManager.findBlocksByState(BlockState.SEARCHING).forEach(block -> {
          block.setState(BlockState.EMPTY);
          block.setTag("");
        });
      }
    });
  }

  @Override
  public void reset() {
    blockValue.clear();
    searching.clear();
    searched.clear();
    blockQueue.clear();

    getNoPathPane().setVisible(false);
    loop().cancel();
    BlockManager.getBlockSet().stream().filter(block -> block.getState().isSearchable()).forEach(block -> {
      if (!block.getState().equals(BlockState.FINISH) && !block.getState().equals(BlockState.START)) {
        block.setState(BlockState.EMPTY);
      }
      block.setTag("");
    });
  }
}
