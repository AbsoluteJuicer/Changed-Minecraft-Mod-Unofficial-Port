package net.ltxprogrammer.changed;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import net.ltxprogrammer.changed.client.gui.TransfurProgressOverlay;
import net.ltxprogrammer.changed.data.RegistryElementPredicate;
import net.ltxprogrammer.changed.entity.BasicPlayerInfo;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.ConfigTracker;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.ForgeRegistry;
import net.neoforged.neoforge.registries.GameData;
import net.neoforged.server.ServerLifecycleHooks;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class ChangedConfig {
    private interface AdditionalData {
        String getName();
        void load(CompoundTag tag);
        void save(CompoundTag tag);
    }

    private static final Logger LOGGER = LogManager.getLogger();
    static final Marker CONFIG = MarkerManager.getMarker("CONFIG");

    public static class Common {
        public final ModConfigSpec.ConfigValue<Boolean> downloadPatreonContent;
        public final ModConfigSpec.ConfigValue<String> githubDomain;
        public final ModConfigSpec.ConfigValue<Boolean> displayPatronage;

        public Common(ModConfigSpec.Builder builder) {
            builder.comment("Choose whether this instance will download patreon content. If disabled: this instance will not recognize patrons' rank or special form.");
            downloadPatreonContent = builder.define("downloadPatreonContent", true);
            builder.comment("Choose your domain. Let's you specify a mirror domain if your ISP blocks github (for whatever reason).");
            githubDomain = builder.define("githubDomain", "raw.githubusercontent.com");
            builder.comment("Compatibility is weird, you can disable displaying player's patronage to Changed:MC here.");
            displayPatronage = builder.define("displayPatronage", true);
        }
    }

    public static class Client implements AdditionalData {
        public final ModConfigSpec.ConfigValue<Boolean> showContentWarning;
        public final ModConfigSpec.ConfigValue<Boolean> useGoopyInventory;
        public final ModConfigSpec.ConfigValue<Boolean> useGoopyHearts;
        public final ModConfigSpec.ConfigValue<Boolean> useBlinkingEyes;
        public final ModConfigSpec.ConfigValue<Boolean> cacheGeneratedTextures;
        public final ModConfigSpec.ConfigValue<Boolean> memCacheBaseImages;
        public final ModConfigSpec.ConfigValue<Boolean> generateUniqueTexturesForAllBlocks;
        public final ModConfigSpec.ConfigValue<Boolean> fastAndCheapLatexBlocks;
        public final ModConfigSpec.ConfigValue<TransfurProgressOverlay.Position> transfurMeterPosition;
        public final ModConfigSpec.ConfigValue<Boolean> renderDripParticlesWithNormal;

        public final BasicPlayerInfo basicPlayerInfo = new BasicPlayerInfo();

        public Client(ModConfigSpec.Builder builder) {
            builder.comment("Show content warning on launch (Disables automatically)");
            showContentWarning = builder.define("showContentWarning", true);
            builder.comment("Enable/disable the gooey inventory");
            useGoopyInventory = builder.define("useGooeyInventory", true);
            builder.comment("Enable/disable the gooey hearts");
            useGoopyHearts = builder.define("useGoopyHearts", true);
            builder.comment("Enable/disable compatible models to blink intermittently");
            useBlinkingEyes = builder.define("useBlinkingEyes", false);
            builder.comment("Caching generated latex covering textures will decrease load time, but will disable recreating the cache if you change resource packs. This cache is stored on your disk. It's recommended to enable this while loading large modpacks");
            cacheGeneratedTextures = builder.define("cacheGeneratedTextures", true);
            builder.comment("While generating textures, the generator will store all used block textures in memory until all textures are generated. It's recommended to disable this while loading large modpacks");
            memCacheBaseImages = builder.define("memCacheBaseImages", true);
            builder.comment("Large modpacks will eat up all your memory if unique textures are generated for every block, this will apply a generic texture for all cube like models");
            generateUniqueTexturesForAllBlocks = builder.define("generateUniqueTexturesForAllBlocks", true);
            builder.comment("Got a lot of mods? Unique model generation will be limited to minecraft and changed");
            fastAndCheapLatexBlocks = builder.define("fastAndCheapLatexBlocks", false);
            builder.comment("Specify the location of the transfur meter");
            transfurMeterPosition = builder.defineEnum("transfurMeterPosition", TransfurProgressOverlay.Position.BOTTOM_LEFT);
            builder.comment("Disable this to test fix for black drip particles (https://github.com/LtxProgrammer/Changed-Minecraft-Mod/issues/1291)");
            renderDripParticlesWithNormal = builder.define("renderDripParticlesWithNormal", true);
        }

        @Override
        public String getName() {
            return "client";
        }

        @Override
        public void load(CompoundTag tag) {
            basicPlayerInfo.load(tag.getCompound("basicPlayerInfo"));
        }

        @Override
        public void save(CompoundTag tag) {
            var bpi = new CompoundTag();
            basicPlayerInfo.save(bpi);
            tag.put("basicPlayerInfo", bpi);
        }
    }

    public static class Server {
        public final ModConfigSpec.ConfigValue<Boolean> showTFNametags;
        public final ModConfigSpec.ConfigValue<List<? extends String>> blacklistCoverBlocks;
        public final ModConfigSpec.ConfigValue<List<? extends String>> whitelistCoverBlocks;
        public final ModConfigSpec.ConfigValue<Boolean> playerControllingAbilities;
        public final ModConfigSpec.ConfigValue<Boolean> isGrabEnabled;
        public final ModConfigSpec.ConfigValue<Double> bpiSizeTolerance;

        public Server(ModConfigSpec.Builder builder) {
            builder.comment("Should transfurred players have a nametag");
            showTFNametags = builder.define("showTFNametags", true);
            builder.comment("Blacklist any blocks from being covered. Acceptable formats: \"@modid\", \"#tag\", \"modid:block_id\"");
            blacklistCoverBlocks = builder.defineList("blacklistCoverBlocks", List::of, RegistryElementPredicate::isValidSyntax);
            builder.comment("Overrides any matches found in blacklistCoverBlocks. If the blacklist is empty, any blocks not in this list will not cover");
            whitelistCoverBlocks = builder.defineList("whitelistCoverBlocks", List::of, RegistryElementPredicate::isValidSyntax);
            builder.comment("Can latex abilities (hypno, siren) affect players.");
            playerControllingAbilities = builder.define("playerControllingAbilities", true);
            builder.comment("Can latexes use the grab ability on players.");
            isGrabEnabled = builder.define("isGrabEnabled", true);
            builder.comment("Acceptable model scaling through BPI (Default: +/- 5%)");
            bpiSizeTolerance = builder.defineInRange("bpiSizeTolerance", 0.05, 0.01, 0.95);
        }

        public Stream<RegistryElementPredicate<Block>> getBlacklistedCoverBlocks() {
            return blacklistCoverBlocks.get().stream().map(s -> RegistryElementPredicate.parseString(NeoForgeRegistries.BLOCKS, s));
        }

        public Stream<RegistryElementPredicate<Block>> getWhitelistedCoverBlocks() {
            return whitelistCoverBlocks.get().stream().map(s -> RegistryElementPredicate.parseString(NeoForgeRegistries.BLOCKS, s));
        }

        public boolean canBlockBeCovered(Block block) {
            if (!whitelistCoverBlocks.get().isEmpty() && getWhitelistedCoverBlocks().anyMatch(pred -> pred.test(block)))
                return true;
            if (!blacklistCoverBlocks.get().isEmpty() && getBlacklistedCoverBlocks().anyMatch(pred -> pred.test(block)))
                return false;
            return !blacklistCoverBlocks.get().isEmpty() || whitelistCoverBlocks.get().isEmpty();
        }
    }

    private final Pair<Common, ModConfigSpec> commonPair;
    private final Pair<Client, ModConfigSpec> clientPair;
    private final Pair<Server, ModConfigSpec> serverPair;
    private final List<AdditionalData> additionalDataList = new ArrayList<>();
    public final Common common;
    public final Client client;
    public final Server server;

    private static void earlyLoad(ModConfig.Type type, Pair<?, ModConfigSpec> specPair) {
        for (var config : ConfigTracker.INSTANCE.configSets().get(type)) {
            if (config.getSpec() == specPair.getRight()) {
                LOGGER.debug(CONFIG, "Early loading config file type {} at {} for {}", config.getType(), config.getFileName(), config.getModId());
                final CommentedFileConfig configData = config.getHandler().reader(FMLPaths.CONFIGDIR.get()).apply(config);
                specPair.getRight().acceptConfig(configData);
            }
        }
    }

    public void saveAdditionalData() {
        additionalDataList.forEach(data -> {
            var path = FMLPaths.CONFIGDIR.get().resolve(Changed.MODID).resolve(data.getName() + ".nbt");
            try {
                var tag = new CompoundTag();
                data.save(tag);
                path.getParent().toFile().mkdirs();
                NbtIo.writeCompressed(tag, path);
            } catch (IOException ex) {
                Changed.LOGGER.error("Failed to write data for \"{}\"", data.getName());
                ex.printStackTrace();
            }
        });
    }

    public void updateAdditionalData() {
        additionalDataList.forEach(data -> {
            var path = FMLPaths.CONFIGDIR.get().resolve(Changed.MODID).resolve(data.getName() + ".nbt");
            try {
                var tag = NbtIo.readCompressed(path, NbtAccounter.unlimitedHeap());
                data.load(tag);
            } catch (IOException e) {
                Changed.LOGGER.warn("Data file missing or corrupted for \"{}\", initializing on defaults", data.getName());

                try {
                    var tag = new CompoundTag();
                    data.save(tag);
                    path.getParent().toFile().mkdirs();
                    NbtIo.writeCompressed(tag, path);
                } catch (IOException ex) {
                    Changed.LOGGER.error("Failed to write defaults for \"{}\"", data.getName());
                    ex.printStackTrace();
                }
            }
        });
    }

    public ChangedConfig(ModLoadingContext context) {
        commonPair = new ModConfigSpec.Builder()
                .configure(Common::new);
        clientPair = new ModConfigSpec.Builder()
                .configure(Client::new);
        serverPair = new ModConfigSpec.Builder()
                .configure(Server::new);

        additionalDataList.add(clientPair.getLeft());

        context.registerConfig(ModConfig.Type.COMMON, commonPair.getRight());
        context.registerConfig(ModConfig.Type.CLIENT, clientPair.getRight());
        context.registerConfig(ModConfig.Type.SERVER, serverPair.getRight());
        common = commonPair.getLeft();
        client = clientPair.getLeft();
        server = serverPair.getLeft();

        // Early load client config
        earlyLoad(ModConfig.Type.CLIENT, clientPair);

        updateAdditionalData();
    }
}
