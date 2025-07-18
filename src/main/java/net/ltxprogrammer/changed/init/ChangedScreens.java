package net.ltxprogrammer.changed.init;

import net.ltxprogrammer.changed.client.gui.*;
import net.minecraft.client.gui.screens.MenuScreens;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ChangedScreens {
    @SubscribeEvent
    public static void clientLoad(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(ChangedMenus.COMPUTER, ComputerExploreScreen::new);
            MenuScreens.register(ChangedMenus.INFUSER, InfuserScreen::new);
            MenuScreens.register(ChangedMenus.PURIFIER, PurifierScreen::new);
            MenuScreens.register(ChangedMenus.ABILITY_RADIAL, AbilityRadialScreen::new);
            MenuScreens.register(ChangedMenus.HAIRSTYLE_RADIAL, HairStyleRadialScreen::new);
            MenuScreens.register(ChangedMenus.SPECIAL_RADIAL, SpecialStateRadialScreen::new);
            MenuScreens.register(ChangedMenus.KEYPAD, KeypadScreen::new);
            MenuScreens.register(ChangedMenus.CLIPBOARD, ClipboardScreen::new);
            MenuScreens.register(ChangedMenus.NOTE, NoteScreen::new);
            MenuScreens.register(ChangedMenus.STASIS_CHAMBER, StasisChamberScreen::new);
            MenuScreens.register(ChangedMenus.ACCESSORY_ACCESS, AccessoryAccessScreen::new);
        });
    }
}
