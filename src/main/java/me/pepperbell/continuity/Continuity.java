package me.pepperbell.continuity;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;

@Mod("continuity")
public final class Continuity {
  public Continuity() {
    DistExecutor.safeRunWhenOn(
        Dist.CLIENT, () -> me.pepperbell.continuity.client.ForgeClient::init);
  }
}
