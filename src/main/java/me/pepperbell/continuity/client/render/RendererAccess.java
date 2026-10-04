package me.pepperbell.continuity.client.render;

public enum RendererAccess {
  INSTANCE;

  public RendererAccess getRenderer() {
    return this;
  }

  public MeshBuilder meshBuilder() {
    return new MeshBuilder();
  }

  public MaterialFinder materialFinder() {
    return new MaterialFinder();
  }
}
