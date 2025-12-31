package com.macromod;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class Utils {

    public static void lookAtBlock(MinecraftClient client, BlockPos pos) {
        if (client.player == null) return;

        PlayerEntity player = client.player;

        Vec3d eyePos = player.getEyePos();
        Vec3d target = Vec3d.ofCenter(pos);

        double dx = target.x - eyePos.x;
        double dy = target.y - eyePos.y;
        double dz = target.z - eyePos.z;

        double distanceXZ = Math.sqrt(dx * dx + dz * dz);

        float yaw = (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0F);
        float pitch = (float) (-Math.toDegrees(Math.atan2(dy, distanceXZ)));

        player.setYaw(yaw);
        player.setPitch(pitch);
    }

}
