package me.pepperbell.continuity.client.render;

import java.util.*;
import java.util.function.Supplier;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.*;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.*;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.data.*;

public class ForwardingBakedModel implements BakedModel {
  protected BakedModel wrapped;

  public ForwardingBakedModel() {}

  public ForwardingBakedModel(BakedModel wrapped) {
    this.wrapped = wrapped;
  }

  private record WorldView(BlockAndTintGetter view, BlockPos pos) {}

  private static final ModelProperty<WorldView> WORLD = new ModelProperty<>();

  public boolean isVanillaAdapter() {
    return true;
  }

  public void emitBlockQuads(
      BlockAndTintGetter view,
      BlockState state,
      BlockPos pos,
      Supplier<RandomSource> random,
      RenderContext context) {
    if (wrapped instanceof ForwardingBakedModel m)
      m.emitBlockQuads(view, state, pos, random, context);
    else context.emit(wrapped, state, random.get());
  }

  public void emitItemQuads(ItemStack stack, Supplier<RandomSource> random, RenderContext context) {
    if (wrapped instanceof ForwardingBakedModel m) m.emitItemQuads(stack, random, context);
    else context.emit(wrapped, null, random.get());
  }

  @Override
  public ModelData getModelData(
      BlockAndTintGetter view, BlockPos pos, BlockState state, ModelData data) {
    return wrapped
        .getModelData(view, pos, state, data)
        .derive()
        .with(WORLD, new WorldView(view, pos.immutable()))
        .build();
  }

  @Override
  public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource random) {
    return getQuads(state, side, random, ModelData.EMPTY, null);
  }

  @Override
  public List<BakedQuad> getQuads(
      BlockState state, Direction side, RandomSource random, ModelData data, RenderType layer) {
    WorldView world = data.get(WORLD);
    if (state != null && world == null) return wrapped.getQuads(state, side, random, data, layer);
    RenderContext context =
        new RenderContext(
            side,
            layer,
            data,
            world == null ? null : world.view,
            world == null ? null : world.pos,
            state);
    if (world != null) emitBlockQuads(world.view, state, world.pos, () -> random, context);
    else emitItemQuads(ItemStack.EMPTY, () -> random, context);
    return context.result();
  }

  @Override
  public ChunkRenderTypeSet getRenderTypes(BlockState s, RandomSource r, ModelData d) {
    return ChunkRenderTypeSet.union(
        wrapped.getRenderTypes(s, r, d),
        ChunkRenderTypeSet.of(
            RenderType.solid(),
            RenderType.cutoutMipped(),
            RenderType.cutout(),
            RenderType.translucent()));
  }

  @Override
  public List<RenderType> getRenderTypes(ItemStack stack, boolean fabulous) {
    return wrapped.getRenderTypes(stack, fabulous);
  }

  @Override
  public List<BakedModel> getRenderPasses(ItemStack stack, boolean fabulous) {
    return List.of(this);
  }

  @Override
  public boolean useAmbientOcclusion() {
    return wrapped.useAmbientOcclusion();
  }

  @Override
  public boolean useAmbientOcclusion(BlockState state) {
    return wrapped.useAmbientOcclusion(state);
  }

  @Override
  public boolean useAmbientOcclusion(BlockState state, RenderType layer) {
    return wrapped.useAmbientOcclusion(state, layer);
  }

  @Override
  public boolean isGui3d() {
    return wrapped.isGui3d();
  }

  @Override
  public boolean usesBlockLight() {
    return wrapped.usesBlockLight();
  }

  @Override
  public boolean isCustomRenderer() {
    return wrapped.isCustomRenderer();
  }

  @Override
  public TextureAtlasSprite getParticleIcon() {
    return wrapped.getParticleIcon();
  }

  @Override
  public TextureAtlasSprite getParticleIcon(ModelData data) {
    return wrapped.getParticleIcon(data);
  }

  @Override
  public ItemTransforms getTransforms() {
    return wrapped.getTransforms();
  }

  @Override
  public ItemOverrides getOverrides() {
    return wrapped.getOverrides();
  }

  @Override
  public BakedModel applyTransform(
      ItemDisplayContext type, com.mojang.blaze3d.vertex.PoseStack pose, boolean left) {
    wrapped.applyTransform(type, pose, left);
    return this;
  }
}
