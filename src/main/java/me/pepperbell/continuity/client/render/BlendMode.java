package me.pepperbell.continuity.client.render;

import net.minecraft.client.renderer.RenderType;

public enum BlendMode {
  DEFAULT,
  SOLID,
  CUTOUT_MIPPED,
  CUTOUT,
  TRANSLUCENT;

  public RenderType layer() {
    return switch (this) {
      case SOLID -> RenderType.solid();
      case CUTOUT_MIPPED -> RenderType.cutoutMipped();
      case CUTOUT -> RenderType.cutout();
      case TRANSLUCENT -> RenderType.translucent();
      default -> null;
    };
  }
}
