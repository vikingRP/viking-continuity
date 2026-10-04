package me.pepperbell.continuity.client.render;

import java.util.List;

public record Mesh(List<MutableQuadView> quads) {
  public void outputTo(QuadEmitter e) {
    for (MutableQuadView q : quads) {
      e.copyFrom(q);
      e.emit();
    }
  }
}
