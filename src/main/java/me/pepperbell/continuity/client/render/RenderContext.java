package me.pepperbell.continuity.client.render;

import java.util.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.*;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;

public final class RenderContext {
  public interface QuadTransform {
    boolean transform(MutableQuadView quad);
  }

  private final Deque<QuadTransform> transforms = new ArrayDeque<>();
  private final List<BakedQuad> result = new ArrayList<>();
  private final QuadEmitter emitter = new QuadEmitter(this::accept);
  final Direction side;
  final RenderType layer;
  final ModelData data;
  private final BlockAndTintGetter view;
  private final BlockPos pos;
  private final BlockState state;

  public RenderContext(
      Direction side,
      RenderType layer,
      ModelData data,
      BlockAndTintGetter view,
      BlockPos pos,
      BlockState state) {
    this.side = side;
    this.layer = layer;
    this.data = data;
    this.view = view;
    this.pos = pos;
    this.state = state;
  }

  public void pushTransform(QuadTransform t) {
    transforms.push(t);
  }

  public void popTransform() {
    transforms.pop();
  }

  public QuadEmitter getEmitter() {
    return emitter;
  }

  public List<BakedQuad> result() {
    return result;
  }

  public boolean isFaceCulled(Direction d) {
    return d != null
        && view != null
        && !net.minecraft.world.level.block.Block.shouldRenderFace(
            state, view, pos, d, pos.relative(d));
  }

  private void accept(MutableQuadView q) {
    for (QuadTransform t : transforms) if (!t.transform(q)) return;
    if (q.cullFace() != side && !(side == null && q.defaultLayer == null)) return;
    RenderType desired = q.material().blendMode().layer();
    if (desired == null) desired = q.defaultLayer;
    if (q.material().blendMode() == BlendMode.DEFAULT
        && state != null
        && me.pepperbell.continuity.client.config.ContinuityConfig.INSTANCE.customBlockLayers
            .get()) {
      RenderType custom =
          me.pepperbell.continuity.client.resource.CustomBlockLayers.renderType(state);
      if (custom != null) desired = custom;
    }
    if (desired == null && state != null)
      desired = net.minecraft.client.renderer.ItemBlockRenderTypes.getChunkRenderType(state);
    if (layer != null && desired != layer) return;
    result.add(q.bake());
  }

  public void emit(BakedModel model, BlockState state, RandomSource random) {
    // Ask each source layer once. Filtering happens after CTM/emissive transforms so overlays can
    // change layer.
    if (layer == null || state == null) {
      emitQuads(model.getQuads(state, side, random, data, null), null);
      return;
    }
    long seed = random.nextLong();
    for (RenderType sourceLayer : model.getRenderTypes(state, RandomSource.create(seed), data))
      emitQuads(
          model.getQuads(state, side, RandomSource.create(seed), data, sourceLayer), sourceLayer);
  }

  private void emitQuads(List<BakedQuad> quads, RenderType sourceLayer) {
    for (BakedQuad q : quads) {
      emitter.fromBaked(q, side, sourceLayer);
      emitter.emit();
    }
  }
}
