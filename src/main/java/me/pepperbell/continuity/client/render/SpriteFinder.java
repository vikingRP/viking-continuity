package me.pepperbell.continuity.client.render;

import java.util.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public final class SpriteFinder {
  private static final int GRID = 128;
  private final List<TextureAtlasSprite>[] cells;
  private final TextureAtlasSprite missing;

  @SuppressWarnings("unchecked")
  public SpriteFinder(Collection<TextureAtlasSprite> sprites, TextureAtlasSprite missing) {
    this.missing = missing;
    cells = new List[GRID * GRID];
    for (TextureAtlasSprite s : sprites)
      for (int y = index(s.getV0()); y <= index(s.getV1()); y++)
        for (int x = index(s.getU0()); x <= index(s.getU1()); x++) {
          int i = y * GRID + x;
          if (cells[i] == null) cells[i] = new ArrayList<>();
          cells[i].add(s);
        }
  }

  private static int index(float n) {
    return Math.max(0, Math.min(GRID - 1, (int) (n * GRID)));
  }

  public TextureAtlasSprite find(QuadView q) {
    float u = 0, v = 0;
    for (int i = 0; i < 4; i++) {
      u += q.u(i) * .25f;
      v += q.v(i) * .25f;
    }
    List<TextureAtlasSprite> c = cells[index(v) * GRID + index(u)];
    if (c != null)
      for (TextureAtlasSprite s : c)
        if (u >= s.getU0() && u < s.getU1() && v >= s.getV0() && v < s.getV1()) return s;
    return q.sprite != null ? q.sprite : missing;
  }
}
