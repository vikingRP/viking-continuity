package me.pepperbell.continuity.client.render;

public record RenderMaterial(
    BlendMode blendMode, boolean emissive, boolean disableDiffuse, TriState ambientOcclusion) {
  public static final RenderMaterial DEFAULT =
      new RenderMaterial(BlendMode.DEFAULT, false, false, TriState.DEFAULT);
}
