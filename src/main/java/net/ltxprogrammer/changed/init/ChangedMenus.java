package net.ltxprogrammer.changed.init;

import net.ltxprogrammer.changed.world.inventory.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.event.RegistryEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.network.IContainerFactory;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ChangedMenus {
    private static final List<MenuType<?>> REGISTRY = new ArrayList<>();
    public static final MenuType<AbilityRadialMenu> ABILITY_RADIAL = register("ability_radial", AbilityRadialMenu::new);
    public static final MenuType<HairStyleRadialMenu> HAIRSTYLE_RADIAL = register("hairstyle_radial", HairStyleRadialMenu::new);
    public static final MenuType<SpecialStateRadialMenu> SPECIAL_RADIAL = register("special_radial", SpecialStateRadialMenu::new);
    public static final MenuType<ComputerMenu> COMPUTER = register("computer", ComputerMenu::new);
    public static final MenuType<InfuserMenu> INFUSER = register("infuser", InfuserMenu::new);
    public static final MenuType<PurifierMenu> PURIFIER = register("purifier", PurifierMenu::new);
    public static final MenuType<KeypadMenu> KEYPAD = register("keypad", KeypadMenu::new);
    public static final MenuType<ClipboardMenu> CLIPBOARD = register("clipboard", ClipboardMenu::new);
    public static final MenuType<NoteMenu> NOTE = register("note", NoteMenu::new);
    public static final MenuType<StasisChamberMenu> STASIS_CHAMBER = register("stasis_chamber", StasisChamberMenu::new);
    public static final MenuType<AccessoryAccessMenu> ACCESSORY_ACCESS = register("accessory_access", AccessoryAccessMenu::new);

    private static <T extends AbstractContainerMenu> MenuType<T> register(String name, IContainerFactory<T> containerFactory) {
        MenuType<T> menuType = new MenuType<T>(containerFactory);
        menuType.setRegistryName(name);
        REGISTRY.add(menuType);
        return menuType;
    }

    @SubscribeEvent
    public static void registerContainers(RegistryEvent.Register<MenuType<?>> event) {
        event.getRegistry().registerAll(REGISTRY.toArray(new MenuType[0]));
    }
}
