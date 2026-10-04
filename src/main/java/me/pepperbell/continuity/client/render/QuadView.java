package me.pepperbell.continuity.client.render;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;

public class QuadView {
  protected int[] vertices = new int[32];
  protected int tint = -1, tag;
  protected Direction nominal = Direction.UP, cull;
  protected boolean shade = true, ao = true;
  protected RenderMaterial material = RenderMaterial.DEFAULT;
  protected TextureAtlasSprite sprite;
  protected RenderType defaultLayer;

  public float posByIndex(int v, int axis) {
    return Float.intBitsToFloat(vertices[v * 8 + axis]);
  }

  public float x(int v) {
    return posByIndex(v, 0);
  }

  public float y(int v) {
    return posByIndex(v, 1);
  }

  public float z(int v) {
    return posByIndex(v, 2);
  }

  public float u(int v) {
    return Float.intBitsToFloat(vertices[v * 8 + 4]);
  }

  public float v(int v) {
    return Float.intBitsToFloat(vertices[v * 8 + 5]);
  }

  protected static int swapRedBlue(int c) {
    return (c & 0xff00ff00) | ((c & 0xff) << 16) | ((c >>> 16) & 0xff);
  }

  public int color(int v) {
    return swapRedBlue(vertices[v * 8 + 3]);
  }

  public int lightmap(int v) {
    return vertices[v * 8 + 6];
  }

  public boolean hasNormal(int v) {
    return vertices[v * 8 + 7] != 0;
  }

  private float n(int v, int a) {
    return (byte) (vertices[v * 8 + 7] >>> a * 8) / 127f;
  }

  public float nx(int v) {
    return n(v, 0);
  }

  public float ny(int v) {
    return n(v, 1);
  }

  public float nz(int v) {
    return n(v, 2);
  }

  public Direction lightFace() {
    return nominal;
  }

  public Direction nominalFace() {
    return nominal;
  }

  public Direction cullFace() {
    return cull;
  }

  public RenderMaterial material() {
    return material;
  }

  public int colorIndex() {
    return tint;
  }

  public int tag() {
    return tag;
  }
}
