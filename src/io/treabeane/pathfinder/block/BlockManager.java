package io.treabeane.pathfinder.block;

import java.util.*;

public class BlockManager {

  private static final Set<Block> blockSet = new LinkedHashSet<>();

  public static Set<Block> getBlockSet() {
    return blockSet;
  }

  public static void registerBlock(Block block){
    getBlockSet().add(block);
  }

  public static Set<Block> findBlocksByState(BlockState state){
    Set<Block> blockSet = new HashSet<>();
    getBlockSet().stream().filter(block -> block.getState().equals(state)).forEach(blockSet::add);
    return blockSet;
  }

  public static Set<Block> getNeighbor(Block block, boolean straight){
    Set<Block> adjacent = new HashSet<>();

    if (straight) {
      BlockManager.findBlock(block.getX() + 1, block.getY()).ifPresent(adjacent::add);
      BlockManager.findBlock(block.getX() - 1, block.getY()).ifPresent(adjacent::add);
      BlockManager.findBlock(block.getX(), block.getY() + 1).ifPresent(adjacent::add);
      BlockManager.findBlock(block.getX(), block.getY() - 1).ifPresent(adjacent::add);
    } else  {
      BlockManager.findBlock(block.getX() + 1, block.getY() + 1).ifPresent(adjacent::add);
      BlockManager.findBlock(block.getX() - 1, block.getY() + 1).ifPresent(adjacent::add);
      BlockManager.findBlock(block.getX() + 1, block.getY() - 1).ifPresent(adjacent::add);
      BlockManager.findBlock(block.getX() - 1, block.getY() - 1).ifPresent(adjacent::add);
    }

    return adjacent;
  }

  public static Set<Block> getAllNeighbor(Block block){
    Set<Block> adjacent = new HashSet<>();

    adjacent.addAll(getNeighbor(block, true));
    adjacent.addAll(getNeighbor(block, false));

    return adjacent;
  }

  private static Optional<Block> findBlock(double x, double y){
    return blockSet.stream().filter(block -> block.getX() == x && block.getY() == y).findFirst();
  }

}
