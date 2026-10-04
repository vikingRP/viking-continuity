package me.pepperbell.continuity.client.render;

import java.util.function.Consumer;
import net.minecraft.core.Direction;

public class QuadEmitter extends MutableQuadView {
  private final Consumer<MutableQuadView> output;

  public QuadEmitter(Consumer<MutableQuadView> output) {
    this.output = output;
  }

  public QuadEmitter emit() {
    output.accept(new MutableQuadView().copyFrom(this));
    vertices = new int[32];
    tint = -1;
    tag = 0;
    cull = null;
    nominal = Direction.UP;
    shade = ao = true;
    material = RenderMaterial.DEFAULT;
    sprite = null;
    defaultLayer = null;
    return this;
  }

  public QuadEmitter square(
      Direction face, float left, float bottom, float right, float top, float depth) {
    nominal = face;
    cull = depth == 0 ? face : null;
    float[][] corners = {{left, top}, {left, bottom}, {right, bottom}, {right, top}};
    for (int i = 0; i < 4; i++) {
      float a = corners[i][0], b = corners[i][1];
      switch (face) {
        case UP -> pos(i, a, 1 - depth, 1 - b);
        case DOWN -> pos(i, a, depth, b);
        case NORTH -> pos(i, 1 - a, b, depth);
        case SOUTH -> pos(i, a, b, 1 - depth);
        case WEST -> pos(i, depth, b, a);
        case EAST -> pos(i, 1 - depth, b, 1 - a);
      }
      normal(i, face.getStepX(), face.getStepY(), face.getStepZ());
    }
    return this;
  }
}
