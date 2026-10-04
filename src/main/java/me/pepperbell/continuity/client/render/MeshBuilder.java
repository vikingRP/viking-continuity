package me.pepperbell.continuity.client.render;

import java.util.ArrayList;
import java.util.List;

public class MeshBuilder {
  private final List<MutableQuadView> quads = new ArrayList<>();
  private final QuadEmitter emitter = new QuadEmitter(quads::add);

  public QuadEmitter getEmitter() {
    return emitter;
  }

  public Mesh build() {
    Mesh m = new Mesh(List.copyOf(quads));
    quads.clear();
    return m;
  }
}
