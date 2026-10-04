package me.pepperbell.continuity.smoke;

import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.AccessibilityOnboardingScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.*;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.world.level.ColorResolver;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.client.model.data.*;
import me.pepperbell.continuity.client.render.ForwardingBakedModel;

@Mod.EventBusSubscriber(modid="continuity",value=Dist.CLIENT)
public final class NativeSmoke {
    private static boolean checked, reloaded;
    private static boolean reported;
    @SubscribeEvent public static void render(TickEvent.RenderTickEvent event) {
        var mc=Minecraft.getInstance();
        if(!reported && mc.getOverlay()==null) { reported=true; System.out.println("CONTINUITY_SMOKE_SCREEN "+mc.screen); }
        if(mc.getOverlay()==null && mc.getModelManager().getMissingModel()!=null) tick(new TickEvent.ClientTickEvent(TickEvent.Phase.END));
    }
    @SubscribeEvent public static void tick(TickEvent.ClientTickEvent event) {
        var mc=Minecraft.getInstance();
        if(checked||event.phase!=TickEvent.Phase.END||mc.getOverlay()!=null||mc.getModelManager().getMissingModel()==null) return;
        checked=true;
        try {
            if(net.minecraftforge.fml.ModList.get().isLoaded("fabric_api")||net.minecraftforge.fml.ModList.get().isLoaded("connectormod")) throw new AssertionError("External API in native smoke run");
            var state=Blocks.GLASS.defaultBlockState(); var model=mc.getBlockRenderer().getBlockModel(state);
            require(model instanceof ForwardingBakedModel,"Native CTM wrapper missing");
            Set<String> sprites=new HashSet<>();
            for(int mask=0;mask<256;mask++) {
                var view=new TestView(state,mask); var data=model.getModelData(view,BlockPos.ZERO,state,ModelData.EMPTY);
                var quads=model.getQuads(state,Direction.UP,RandomSource.create(42),data,RenderType.cutout());
                require(quads.size()==1,"Glass quad count="+quads.size());
                sprites.add(quads.get(0).getSprite().contents().name().toString());
            }
            require(sprites.size()==47,"Expected all 47 CTM cases, got "+sprites.size());
            var emissive=Blocks.GOLD_BLOCK.defaultBlockState(); var em=mc.getBlockRenderer().getBlockModel(emissive);
            var data=em.getModelData(new TestView(emissive,0),BlockPos.ZERO,emissive,ModelData.EMPTY);
            var lit=em.getQuads(emissive,Direction.UP,RandomSource.create(42),data,RenderType.cutoutMipped());
            require(lit.size()==1,"Emissive overlay missing");
            require(lit.get(0).getSprite().contents().name().getPath().endsWith("_e"),"Wrong emissive sprite");
            for(int i=0;i<4;i++) require(lit.get(0).getVertices()[i*8+6]==LightTexture.FULL_BRIGHT,"Emissive light missing");
            var item=mc.getItemRenderer().getItemModelShaper().getItemModel(new net.minecraft.world.item.ItemStack(Blocks.GOLD_BLOCK));
            var iq=item.getQuads(null,Direction.UP,RandomSource.create(42));
            require(iq.size()==2,"Item emissive overlay missing: "+iq.size());
            int methods=0;
            for(Block block:List.of(Blocks.IRON_BLOCK,Blocks.COPPER_BLOCK,Blocks.LAPIS_BLOCK,Blocks.EMERALD_BLOCK,Blocks.DIAMOND_BLOCK,Blocks.REDSTONE_BLOCK,Blocks.QUARTZ_BLOCK,Blocks.COAL_BLOCK)) {
                var bs=block.defaultBlockState(); var bm=mc.getBlockRenderer().getBlockModel(bs); Set<String> tiles=new HashSet<>(); int max=0;
                for(int mask=0;mask<256;mask++) {
                    var md=bm.getModelData(new TestView(bs,mask),BlockPos.ZERO,bs,ModelData.EMPTY);
                    var qs=bm.getQuads(bs,Direction.UP,RandomSource.create(mask),md,RenderType.solid());
                    require(!qs.isEmpty(),"Lost geometry for "+block+" mask="+mask); max=Math.max(max,qs.size());
                    for(var q:qs) { var id=q.getSprite().contents().name(); require(id.getPath().contains("continuity_reserved"),"Unprocessed fixture "+block+": "+id); tiles.add(id.toString()); }
                }
                if(block==Blocks.IRON_BLOCK) require(max>1,"Compact CTM never split geometry");
                if(block==Blocks.COPPER_BLOCK||block==Blocks.LAPIS_BLOCK) require(tiles.size()==4,"Missing directional variants for "+block+": "+tiles.size());
                methods++;
            }
            var stone=Blocks.STONE.defaultBlockState(); var sm=mc.getBlockRenderer().getBlockModel(stone);
            var sd=sm.getModelData(new TestView(stone,0),BlockPos.ZERO,stone,ModelData.EMPTY);
            var overlay=sm.getQuads(stone,Direction.UP,RandomSource.create(42),sd,RenderType.cutout());
            require(overlay.size()==1,"Overlay must occur once in cutout layer");
            require(overlay.get(0).getVertices()[3]==0xff563412,"Overlay tint must use native ABGR");
            require(sm.getQuads(stone,Direction.UP,RandomSource.create(42),sd,RenderType.solid()).size()==1,"Base layer duplicated overlay");
            var custom=Blocks.BLACK_CONCRETE.defaultBlockState(); var cm=mc.getBlockRenderer().getBlockModel(custom);
            var cd=cm.getModelData(new TestView(custom,0),BlockPos.ZERO,custom,ModelData.EMPTY);
            require(cm.getQuads(custom,Direction.UP,RandomSource.create(42),cd,RenderType.translucent()).size()==1,"Custom block layer missing");
            require(cm.getQuads(custom,Direction.UP,RandomSource.create(42),cd,RenderType.solid()).isEmpty(),"Custom block layer duplicated original");
            System.out.println("CONTINUITY_NATIVE_SMOKE_OK stage="+(reloaded?"reload":"startup")+" ctm_variants="+sprites.size()+" emissive_block=true emissive_item=true external_apis=0");
            System.out.println("CONTINUITY_NATIVE_METHODS_OK methods="+methods+" compact_split=true overlay_layers=true overlay_tint=true");
            if(!reloaded) { reloaded=true; mc.reloadResourcePacks().thenRun(()->mc.execute(()->checked=false)); }
            else mc.stop();
        } catch(Throwable error) { error.printStackTrace(); System.out.println("CONTINUITY_NATIVE_SMOKE_FAILED"); mc.stop(); }
    }
    private static void require(boolean condition,String message) { if(!condition) throw new AssertionError(message); }
    private static final class TestView implements BlockAndTintGetter {
        private final BlockState state; private final int mask;
        TestView(BlockState state,int mask) { this.state=state; this.mask=mask; }
        public BlockState getBlockState(BlockPos p) {
            if(p.equals(BlockPos.ZERO)) return state;
            int bit=0; for(int z=-1;z<=1;z++) for(int x=-1;x<=1;x++) {
                if(x==0&&z==0) continue;
                if(p.getX()==x&&p.getY()==0&&p.getZ()==z) return (mask&(1<<bit))!=0?state:Blocks.AIR.defaultBlockState(); bit++;
            } return Blocks.AIR.defaultBlockState();
        }
        public BlockEntity getBlockEntity(BlockPos p) { return null; }
        public FluidState getFluidState(BlockPos p) { return getBlockState(p).getFluidState(); }
        public int getHeight() { return 384; } public int getMinBuildHeight() { return -64; }
        public float getShade(Direction d,boolean shade) { return 1; }
        public LevelLightEngine getLightEngine() { return null; }
        public int getBlockTint(BlockPos p,ColorResolver resolver) { return 0x123456; }
    }
}
