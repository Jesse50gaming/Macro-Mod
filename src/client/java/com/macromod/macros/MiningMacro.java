package com.macromod.macros;

import java.lang.reflect.Array;
import java.util.ArrayList;

import com.macromod.MacroMod;
import com.macromod.Utils;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.MovementType;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class MiningMacro {

    private static BlockPos targetBlockPos = null;
    private static Direction targetBlockSide = null;
    private static MiningMacroStates currentState = MiningMacroStates.IDLE;

    private static MinecraftClient client;

    private static int moveCounter = 0;
    private static final int ticksOfForwardMovement =5; 

    static ArrayList<BlockPos> detectedOres = new ArrayList<>();

    public enum MiningMacroStates {
        IDLE,
        MINING_TOP_BLOCK,
        MINING_BOTTOM_BLOCK,
        MINE_ORES,
        MOVE_FOWARD,
        SCAN_ORES
    }

    public static void start(MinecraftClient c) {
        client = c;

        if (client.crosshairTarget == null) return;
        if (client.crosshairTarget.getType() != HitResult.Type.BLOCK) return;

        BlockHitResult hit = (BlockHitResult) client.crosshairTarget;
        BlockPos pos = hit.getBlockPos();
        Direction side = hit.getSide();

        if (client.player.getHorizontalFacing() == Direction.NORTH) {
            targetBlockPos = pos.add(0, -1, 1);
        } else if (client.player.getHorizontalFacing() == Direction.SOUTH) {
            targetBlockPos = pos.add(0, -1, -1);
        } else if (client.player.getHorizontalFacing() == Direction.WEST) {
            targetBlockPos = pos.add(1, -1, 0);
        } else if (client.player.getHorizontalFacing() == Direction.EAST) {
            targetBlockPos = pos.add(-1, -1, 0);
        } else {
            targetBlockPos = pos.add(0, -1, 0);
        }

        
        targetBlockSide = side;

        updateState(MiningMacroStates.MINING_TOP_BLOCK);
    }


    


    public static void update() {
        
        switch (currentState) {
            case IDLE:
                // Do nothing
                break;
            case MINING_TOP_BLOCK:
                updateMineTopBlock();
                break;
            case MINING_BOTTOM_BLOCK:
                updateMineBottomBlock();
                break;
            case MINE_ORES:
                updateMineOres();
                break;
            case MOVE_FOWARD:
                updateMoveForward();
                break;
            case SCAN_ORES:
                updateScanOres();
                break;
        }

    }

    private static void updateState(MiningMacroStates newState) {
       System.out.println("Transitioning to state: " + newState);

       switch (newState) {
           case IDLE:
               // Do nothing
               break;
           case MINING_TOP_BLOCK:
               initMineTopBlock();
               break;
           case MINING_BOTTOM_BLOCK:
               initMineBottomBlock();
               break;
           case MINE_ORES:
               initMineOres();
               break;
           case MOVE_FOWARD:
               initMoveForward();
               break;
            case SCAN_ORES:
                initScanOres();
                break;
       }

       currentState = newState;
    }

    private static void updateMineTopBlock() {
        client.interactionManager.updateBlockBreakingProgress(targetBlockPos, targetBlockSide);

        if (client.world.isAir(targetBlockPos)) {
            
            updateState(MiningMacroStates.MINING_BOTTOM_BLOCK);
        }
    }

    

    private static void initMineTopBlock() {
        if (client.player.getHorizontalFacing() == Direction.NORTH) {
            setTargetBlock(targetBlockPos.add(0, 1, -1));
        } else if (client.player.getHorizontalFacing() == Direction.SOUTH) {
            setTargetBlock(targetBlockPos.add(0, 1, 1));
        } else if (client.player.getHorizontalFacing() == Direction.WEST) {
            setTargetBlock(targetBlockPos.add(-1, 1, 0));
        } else if (client.player.getHorizontalFacing() == Direction.EAST) {
            setTargetBlock(targetBlockPos.add(1, 1, 0));
        } else {
            setTargetBlock(targetBlockPos.add(0, 1, 0));
        }
        
        
        startMiningBlock();
    }

    

    private static void startMiningBlock() {
        updateMiningSide(targetBlockPos);
        client.interactionManager.attackBlock(targetBlockPos, targetBlockSide);
        client.player.swingHand(Hand.MAIN_HAND);
    }

    private static void initMineBottomBlock() {
        setTargetBlock(targetBlockPos.down(1));
        startMiningBlock();
    }

    private static void setTargetBlock(BlockPos pos) {
        targetBlockPos = pos;
        Utils.lookAtBlock(client, pos);
    }

    private static void updateMineBottomBlock() {
        client.interactionManager.updateBlockBreakingProgress(targetBlockPos, targetBlockSide);

        
        if (client.world.isAir(targetBlockPos)) {
            
            updateState(MiningMacroStates.SCAN_ORES);
            
        }
    }

    private static void initMoveForward() {
        moveCounter = 0;
        client.options.forwardKey.setPressed(true);

    }

    private static void updateMoveForward() {
        moveCounter++;
        MacroMod.LOGGER.info("Move Counter: " + moveCounter);
        if (moveCounter >= ticksOfForwardMovement) { 
            moveCounter = 0;
            client.options.forwardKey.setPressed(false);
            updateState(MiningMacroStates.MINING_TOP_BLOCK);
        }
    }

    private static void initScanOres() {
        detectedOres = detectOres();
       
    }

    private static void updateScanOres() {
        if (detectedOres.isEmpty()) {
            updateState(MiningMacroStates.MOVE_FOWARD);
            return;
        }
        updateState(MiningMacroStates.MINE_ORES);
    }

    private static void initMineOres() {
        if (detectedOres.isEmpty()) {
            updateState(MiningMacroStates.MOVE_FOWARD);
            return;
        }
        setTargetBlock(detectedOres.get(0));
        startMiningBlock();
    }

    private static ArrayList<BlockPos> detectOres() {
        ArrayList<BlockPos> orePositions = new ArrayList<>();

        BlockPos playerPos = client.player.getBlockPos();

        
        //central -- to make sure to get above and below
        for (int z = -1; z <=2; z++) {
            BlockPos pos = playerPos.add(0, 0, z);
            Block block = client.world.getBlockState(pos).getBlock();
            if (isOre(block)) {
                orePositions.add(pos);
            }
        }
        // cross -> +
        for (int z = 0; z <=2; z++) {
            for (int x = -1; x <=1; x++) {
                BlockPos pos = playerPos.add(x, 0, z);
                Block block = client.world.getBlockState(pos).getBlock();
                if (isOre(block)) {
                    orePositions.add(pos);
                }
            }
            for (int y = -1; y <=1; y++) {
                BlockPos pos = playerPos.add(0, y, z);
                Block block = client.world.getBlockState(pos).getBlock();
                if (isOre(block)) {
                    orePositions.add(pos);
                }
            }
            
        }

 
        return orePositions;
    }

    private static boolean isOre(Block block) {
        return block == Blocks.COAL_ORE || block == Blocks.DEEPSLATE_COAL_ORE || block == Blocks.IRON_ORE || block == Blocks.DEEPSLATE_IRON_ORE ||  
        block == Blocks.GOLD_ORE || block == Blocks.DEEPSLATE_GOLD_ORE || block == Blocks.DIAMOND_ORE || block == Blocks.DEEPSLATE_DIAMOND_ORE || 
        block == Blocks.REDSTONE_ORE || block == Blocks.DEEPSLATE_REDSTONE_ORE || block == Blocks.LAPIS_ORE || block == Blocks.DEEPSLATE_LAPIS_ORE || 
        block == Blocks.EMERALD_ORE || block == Blocks.DEEPSLATE_EMERALD_ORE;
    }


    private static void updateMineOres() {
        client.interactionManager.updateBlockBreakingProgress(targetBlockPos, targetBlockSide);

        if (client.world.isAir(targetBlockPos)) {
            if (detectedOres.size() > 1) {
                detectedOres.remove(0);
                updateState(MiningMacroStates.MINE_ORES);
            } else {
                updateState(MiningMacroStates.MOVE_FOWARD); //TODO if it is beneath the player they will have to jump
            }
        } 
    }


    private static void updateMiningSide(BlockPos pos) {
        Vec3d eyes = client.player.getEyePos();
        Vec3d center = Vec3d.ofCenter(pos);
        Direction dir = Direction.getFacing(
            center.x - eyes.x,
            center.y - eyes.y,
            center.z - eyes.z
        );
        targetBlockSide = dir;
        
    }


}

