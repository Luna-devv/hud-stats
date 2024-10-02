package nl.lunish.hudstatsmod;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;
import net.minecraft.stats.Stats;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.slf4j.Logger;
import net.neoforged.fml.ModContainer;

@Mod(HUDStatsMod.MOD_ID)
public class HUDStatsMod {
    public static final String MOD_ID = "lunashudstats";
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Utils UTILS = new Utils();
    private static final Render RENDER = new Render();

    private int playTimeTicks = 0;
    private boolean requestedStats = false;

    public HUDStatsMod(ModContainer modContainer) {
        NeoForge.EVENT_BUS.register(this);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    public void onWorldJoin(PlayerEvent.PlayerLoggedInEvent event) {
        playTimeTicks = 0;
        requestedStats = false;
    }

    @SubscribeEvent
    public void onTick(ClientTickEvent.Pre event) {
        final var mc = Minecraft.getInstance();
        if (mc.player == null) return;

        if (!requestedStats) {
            final var connection = mc.getConnection();
            if (connection == null) return;

            final var statsRequestPacket = new ServerboundClientCommandPacket(ServerboundClientCommandPacket.Action.REQUEST_STATS);
            connection.send(statsRequestPacket);

            requestedStats = true;
        }

        if (playTimeTicks == 0) {
            playTimeTicks = mc.player.getStats().getValue(Stats.CUSTOM.get(Stats.PLAY_TIME));
            return;
        }

        if (mc.isPaused()) return;
        playTimeTicks++;
    }

    @SubscribeEvent
    public void renderGameOverlayEvent(RenderGuiLayerEvent.Post event) {
        final var mc = Minecraft.getInstance();
        if (mc.player == null || mc.getDebugOverlay().showDebugScreen() || mc.options.hideGui) return;

        final var gui = event.getGuiGraphics();

        final var playtime = UTILS.ticksToHumanTime(playTimeTicks);
        RENDER.drawGui(gui, playtime);
    }

}