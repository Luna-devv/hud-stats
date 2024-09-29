package nl.lunish.hudstatsmod;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;
import net.minecraft.stats.Stats;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.slf4j.Logger;

@Mod(HUDStatsMod.MOD_ID)
public class HUDStatsMod {
    public static final String MOD_ID = "lunashudstats";
    private static final Logger LOGGER = LogUtils.getLogger();

    private int playTimeTicks = 0;
    private boolean requestedStats = false;

    public HUDStatsMod() {
        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onWorldJoin(PlayerEvent.PlayerLoggedInEvent event) {
        playTimeTicks = 0;
        requestedStats = false;
    }

    @SubscribeEvent
    public void onTick(ClientTickEvent.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        if (!requestedStats) {
            ClientPacketListener connection = mc.getConnection();
            if (connection == null) return;

            ServerboundClientCommandPacket statsRequestPacket = new ServerboundClientCommandPacket(ServerboundClientCommandPacket.Action.REQUEST_STATS);
            connection.send(statsRequestPacket);

            requestedStats = true;
        }

        if (playTimeTicks == 0) {
            playTimeTicks = mc.player.getStats().getValue(Stats.CUSTOM.get(Stats.PLAY_TIME));
            return;
        }

        playTimeTicks++;
    }

    @SubscribeEvent
    public void renderGameOverlayEvent(RenderGuiLayerEvent.Post event) {
        final var mc = Minecraft.getInstance();
        if (mc.player == null) return;

        int seconds = playTimeTicks / 20;
        int minutes = seconds / 60;
        int hours = minutes / 60;
        int days = hours / 24;

        seconds = seconds % 60;
        minutes = minutes % 60;
        hours = hours % 24;

        var gui = event.getGuiGraphics();

        String playTime = "";

        if (days > 0) {
            playTime += String.format("%1d day%s", days, days == 1 ? ", " : "s, ");
        }

        playTime += String.format("%02d:%02d:%02d", hours, minutes, seconds);

        gui.drawString(mc.font, "Playtime: " + playTime, 10, 10, 0xffffff);
    }

}