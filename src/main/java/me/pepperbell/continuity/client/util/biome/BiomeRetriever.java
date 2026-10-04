package me.pepperbell.continuity.client.util.biome;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.Nullable;

public final class BiomeRetriever {
  @Nullable
  public static Biome getBiome(BlockAndTintGetter view, BlockPos pos) {
    if (view instanceof net.minecraft.world.level.LevelReader level)
      return level.getBiome(pos).value();
    var level = net.minecraft.client.Minecraft.getInstance().level;
    return level == null ? null : level.getBiome(pos).value();
  }
}
