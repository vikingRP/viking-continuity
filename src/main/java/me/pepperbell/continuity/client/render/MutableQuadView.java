package me.pepperbell.continuity.client.render;

import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;

public class MutableQuadView extends QuadView {
  public MutableQuadView copyFrom(QuadView q) {
    vertices = q.vertices.clone();
    tint = q.tint;
    tag = q.tag;
    nominal = q.nominal;
    cull = q.cull;
    shade = q.shade;
    ao = q.ao;
    material = q.material;
    sprite = q.sprite;
    defaultLayer = q.defaultLayer;
    return this;
  }

  public MutableQuadView fromBaked(BakedQuad q, Direction side, RenderType layer) {
    vertices = q.getVertices().clone();
    tint = q.getTintIndex();
    nominal = q.getDirection();
    cull = side;
    shade = q.isShade();
    ao = q.hasAmbientOcclusion();
    material = RenderMaterial.DEFAULT;
    sprite = q.getSprite();
    defaultLayer = layer;
    return this;
  }

  public MutableQuadView pos(int v, float x, float y, float z) {
    vertices[v * 8] = Float.floatToRawIntBits(x);
    vertices[v * 8 + 1] = Float.floatToRawIntBits(y);
    vertices[v * 8 + 2] = Float.floatToRawIntBits(z);
    return this;
  }

  public MutableQuadView uv(int v, float u, float w) {
    vertices[v * 8 + 4] = Float.floatToRawIntBits(u);
    vertices[v * 8 + 5] = Float.floatToRawIntBits(w);
    return this;
  }

  public MutableQuadView color(int v, int c) {
    vertices[v * 8 + 3] = swapRedBlue(c);
    return this;
  }

  public MutableQuadView color(int a, int b, int c, int d) {
    return color(0, a).color(1, b).color(2, c).color(3, d);
  }

  public MutableQuadView lightmap(int v, int n) {
    vertices[v * 8 + 6] = n;
    return this;
  }

  public MutableQuadView normal(int v, float x, float y, float z) {
    vertices[v * 8 + 7] =
        ((int) (x * 127) & 255) | (((int) (y * 127) & 255) << 8) | (((int) (z * 127) & 255) << 16);
    return this;
  }

  public MutableQuadView material(RenderMaterial m) {
    material = m;
    return this;
  }

  public MutableQuadView cullFace(Direction d) {
    cull = d;
    return this;
  }

  public MutableQuadView nominalFace(Direction d) {
    nominal = d;
    return this;
  }

  public MutableQuadView colorIndex(int i) {
    tint = i;
    return this;
  }

  public MutableQuadView tag(int i) {
    tag = i;
    return this;
  }

  public BakedQuad bake() {
    int[] out = vertices.clone();
    if (material.emissive()) for (int i = 0; i < 4; i++) out[i * 8 + 6] = LightTexture.FULL_BRIGHT;
    TextureAtlasSprite s =
        me.pepperbell.continuity.client.util.RenderUtil.getSpriteFinder().find(this);
    return new BakedQuad(
        out,
        tint,
        nominal,
        s,
        shade && !material.disableDiffuse(),
        ao && material.ambientOcclusion() != TriState.FALSE);
  }
}
