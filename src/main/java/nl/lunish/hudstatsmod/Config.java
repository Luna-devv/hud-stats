package nl.lunish.hudstatsmod;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = HUDStatsMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class Config {

    public enum Position {
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT
    }

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.EnumValue<Position> POSITION = BUILDER
            .comment("Choose the position for the stats HUD")
            .translation("lunahudstats.configuration.hud_position_name")
            .defineEnum("hudPosition", Position.TOP_LEFT);

    static final ModConfigSpec SPEC = BUILDER.build();

    public static Position hudPosition;

    private static boolean validateItemName(final Object obj) {
        return obj instanceof String itemName && BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(itemName));
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        hudPosition = POSITION.get();
    }
}
