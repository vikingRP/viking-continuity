package me.pepperbell.continuity.client;

import me.pepperbell.continuity.client.config.*;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.*;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.resource.PathPackResources;

public final class ForgeClient {
  public static void init() {
    new ContinuityClient().onInitializeClient();
    var bus = FMLJavaModLoadingContext.get().getModEventBus();
    bus.addListener(ForgeClient::packs);
    bus.addListener(
        (net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent e) ->
            e.enqueueWork(
                () ->
                    me.pepperbell.continuity.impl.client.ProcessingDataKeyRegistryImpl.INSTANCE
                        .freeze()));
    ModLoadingContext.get()
        .registerExtensionPoint(
            ConfigScreenHandler.ConfigScreenFactory.class,
            () ->
                new ConfigScreenHandler.ConfigScreenFactory(
                    (mc, parent) -> new ContinuityConfigScreen(parent, ContinuityConfig.INSTANCE)));
  }

  private static void packs(AddPackFindersEvent e) {
    if (e.getPackType() != PackType.CLIENT_RESOURCES) return;
    var mod = ModList.get().getModFileById("continuity").getFile();
    for (String name : new String[] {"default", "glass_pane_culling_fix"}) {
      String id = "continuity/" + name;
      var path = mod.findResource("resourcepacks", name);
      e.addRepositorySource(
          consumer -> {
            Pack p =
                Pack.readMetaAndCreate(
                    id,
                    Component.translatable("resourcePack.continuity." + name + ".name"),
                    false,
                    x -> new PathPackResources(id, true, path),
                    PackType.CLIENT_RESOURCES,
                    Pack.Position.TOP,
                    PackSource.BUILT_IN);
            if (p != null) consumer.accept(p);
          });
    }
  }
}
