package net.ltxprogrammer.changed;

import net.ltxprogrammer.changed.client.ChangedClient;
import net.ltxprogrammer.changed.client.EventHandlerClient;
import net.ltxprogrammer.changed.client.RecipeCategories;
import net.ltxprogrammer.changed.client.latexparticles.LatexParticleType;
import net.ltxprogrammer.changed.data.BuiltinRepositorySource;
import net.ltxprogrammer.changed.entity.HairStyle;
import net.ltxprogrammer.changed.entity.PlayerMover;
import net.ltxprogrammer.changed.extension.ChangedCompatibility;
import net.ltxprogrammer.changed.entity.AccessoryEntities;
import net.ltxprogrammer.changed.init.*;
import net.ltxprogrammer.changed.network.ChangedPackets;
import net.ltxprogrammer.changed.network.packet.ChangedPacket;
import net.ltxprogrammer.changed.util.PatreonBenefits;
import net.ltxprogrammer.changed.world.ChangedDataFixer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.ComposterBlock;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.IEventBusInvokeDispatcher;
import net.neoforged.fml.DistExecutor;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.IModBusEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.neoforge.network.NetworkEvent;
import net.neoforged.neoforge.network.NetworkRegistry;
import net.neoforged.neoforge.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Mod(Changed.MODID)
public class Changed {
    private static Changed instance;
    public static Changed getInstance() { return instance; }

    public static final String MODID = "changed";

    public static final Logger LOGGER = LogManager.getLogger(Changed.class);
    public static EventHandlerClient eventHandlerClient;
    public static ChangedConfig config;
    public static ChangedDataFixer dataFixer;

    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel PACKET_HANDLER = NetworkRegistry.newSimpleChannel(modResource(MODID), () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
    private static final ChangedPackets PACKETS = new ChangedPackets(PACKET_HANDLER);
    private static int messageID = 0;

    /**
     * This function is split out of the main function as a request by mod extension devs
     */
    private void registerLoadingEventListeners(IEventBus eventBus) {
        eventBus.addListener(this::commonSetup);
        eventBus.addListener(this::clientSetup);
        eventBus.addListener(this::customPacks);
    }

    public Changed() {
        config = new ChangedConfig(ModLoadingContext.get());

        registerLoadingEventListeners(FMLJavaModLoadingContext.get().getModEventBus());

        addEventListener(this::dataListeners);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> this::registerClientEventListeners);

        PACKETS.registerPackets();

        instance = this;

        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        HairStyle.REGISTRY.register(modEventBus);
        ChangedAbilities.REGISTRY.register(modEventBus);
        PlayerMover.REGISTRY.register(modEventBus);
        LatexParticleType.REGISTRY.register(modEventBus);

        ChangedAttributes.REGISTRY.register(modEventBus);
        ChangedEnchantments.REGISTRY.register(modEventBus);
        ChangedRecipeSerializers.REGISTRY.register(modEventBus);
        ChangedStructureSets.REGISTRY.register(modEventBus);
        ChangedStructures.CONFIGURED_REGISTRY.register(modEventBus);
        ChangedStructures.REGISTRY.register(modEventBus);
        ChangedStructurePieceTypes.REGISTRY.register(modEventBus);
        ChangedFeatures.REGISTRY.register(modEventBus);
        ChangedBiomes.REGISTRY.register(modEventBus);
        ChangedBlockEntities.REGISTRY.register(modEventBus);
        ChangedFluids.REGISTRY.register(modEventBus);
        ChangedItems.REGISTRY.register(modEventBus);
        ChangedBlockStateProviders.REGISTRY.register(modEventBus);
        ChangedBlocks.REGISTRY.register(modEventBus);
        ChangedTransfurVariants.REGISTRY.register(modEventBus);
        ChangedEntities.REGISTRY.register(modEventBus);
        ChangedAnimationEvents.REGISTRY.register(modEventBus);
        ChangedAccessorySlots.REGISTRY.register(modEventBus);

        // Our DFU references the above registries, so they need to be initialized before the DFU is created
        dataFixer = new ChangedDataFixer();
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            if (!config.common.downloadPatreonContent.get()) {
                Changed.LOGGER.info("Patreon benefits is disabled on this client. Patrons will not receive benefits visible to this client.");
                return;
            }

            try {
                PatreonBenefits.loadBenefits();
                PatreonBenefits.UPDATE_CHECKER.start();
            } catch (Exception ex) {
                Changed.LOGGER.error("Failed to load Patreon Benefits. Patrons will not receive benefits visible to this client.", ex);
            }
        });
        event.enqueueWork(() -> {
            ComposterBlock.COMPOSTABLES.put(ChangedBlocks.ORANGE_TREE_LEAVES.get().asItem(), 0.3F);
            ComposterBlock.COMPOSTABLES.put(ChangedBlocks.ORANGE_TREE_SAPLING.get().asItem(), 0.3F);
            ComposterBlock.COMPOSTABLES.put(ChangedItems.ORANGE.get(), 0.65F);
        });
    }

    private void registerClientEventListeners() {
        neoforged.EVENT_BUS.register(eventHandlerClient = new EventHandlerClient());
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(RecipeCategories::registerCategories);
        ChangedClient.registerEventListeners();
    }

    private void dataListeners(final AddReloadListenerEvent event) {
        event.addListener(ChangedFusions.INSTANCE);
        event.addListener(AccessoryEntities.INSTANCE);
        ChangedCompatibility.addDataListeners(event);
    }

    private void customPacks(final AddPackFindersEvent event) {
        try {
            switch (event.getPackType()) {
                case CLIENT_RESOURCES, SERVER_DATA ->
                        event.addRepositorySource(new BuiltinRepositorySource(event.getPackType(), MODID));
                default -> {}
            }
        } catch (Exception ex) {
            LOGGER.error(ex);
        }
    }

    private static <T> void addNetworkMessage(Class<T> messageType, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder,
                                             BiConsumer<T, Supplier<NetworkEvent.Context>> messageConsumer) {
        PACKET_HANDLER.registerMessage(messageID++, messageType, encoder, decoder, messageConsumer);
    }

    private static <T extends ChangedPacket> void addNetworkMessage(Class<T> messageType, Function<FriendlyByteBuf, T> ctor) {
        addNetworkMessage(messageType, T::write, ctor, T::handle);
    }

    public static ResourceLocation modResource(String path) {
        return new ResourceLocation(MODID, path);
    }
    public static String modResourceStr(String path) {
        return MODID + ":" + path;
    }

    public static <T extends Event & IModBusEvent> void addLoadingEventListener(Consumer<T> listener) {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(listener);
    }

    public static <T extends Event & IModBusEvent> boolean postModLoadingEvent(T event) {
        return FMLJavaModLoadingContext.get().getModEventBus().post(event);
    }

    public static <T extends Event & IModBusEvent> boolean postModLoadingEvent(T event, IEventBusInvokeDispatcher dispatcher) {
        return FMLJavaModLoadingContext.get().getModEventBus().post(event, dispatcher);
    }

    public static <T extends Event> void addEventListener(Consumer<T> listener) {
        neoforged.EVENT_BUS.addListener(listener);
    }

    public static <T extends Event> boolean postModEvent(T event) {
        return neoforged.EVENT_BUS.post(event);
    }

    public static <T extends Event> boolean postModEvent(T event, IEventBusInvokeDispatcher dispatcher) {
        return neoforged.EVENT_BUS.post(event, dispatcher);
    }
}
