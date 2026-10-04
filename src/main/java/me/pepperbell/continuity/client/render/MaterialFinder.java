package me.pepperbell.continuity.client.render;

public class MaterialFinder {
  private BlendMode blend;
  private boolean emissive, diffuse;
  private TriState ao;

  public MaterialFinder() {
    clear();
  }

  public MaterialFinder clear() {
    blend = BlendMode.DEFAULT;
    emissive = diffuse = false;
    ao = TriState.DEFAULT;
    return this;
  }

  public MaterialFinder copyFrom(RenderMaterial m) {
    blend = m.blendMode();
    emissive = m.emissive();
    diffuse = m.disableDiffuse();
    ao = m.ambientOcclusion();
    return this;
  }

  public MaterialFinder blendMode(BlendMode b) {
    blend = b;
    return this;
  }

  public MaterialFinder emissive(boolean b) {
    emissive = b;
    return this;
  }

  public MaterialFinder disableDiffuse(boolean b) {
    diffuse = b;
    return this;
  }

  public MaterialFinder ambientOcclusion(TriState b) {
    ao = b;
    return this;
  }

  public RenderMaterial find() {
    return new RenderMaterial(blend, emissive, diffuse, ao);
  }
}
