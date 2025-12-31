package com.macromod.macros;

import com.jcraft.jorbis.Block;
import com.macromod.Utils;

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

    public enum MiningMacroStates {
        IDLE,
        MINING_TOP_BLOCK,
        MINING_BOTTOM_BLOCK,
        SCAN_ORES,
        MINE_ORES,
        MOVE_FOWARD
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
            case SCAN_ORES:
                // Logic to scan for ores
                break;
            case MINE_ORES:
                // Logic to mine detected ores
                break;
            case MOVE_FOWARD:
                updateMoveForward();
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
           case SCAN_ORES:
               // Logic to initialize scanning for ores
               break;
           case MINE_ORES:
               // Logic to initialize mining detected ores
               break;
           case MOVE_FOWARD:
               initMoveForward();
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
            
            updateState(MiningMacroStates.MOVE_FOWARD);
            
        }
    }

    private static void initMoveForward() {
        client.options.forwardKey.setPressed(true);

    }

    private static void updateMoveForward() {
        moveCounter++;

        if (moveCounter >= ticksOfForwardMovement) { 
            moveCounter = 0;
            client.options.forwardKey.setPressed(false);
            updateState(MiningMacroStates.MINING_TOP_BLOCK);
        }
    }


}

