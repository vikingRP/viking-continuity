package me.pepperbell.continuity.smoke;

@net.minecraftforge.fml.common.Mod("continuity_native_smoke")
public final class ProductionSmoke {
    public ProductionSmoke() {
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(NativeSmoke.class);
    }
}
