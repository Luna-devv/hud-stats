package nl.lunish.hudstatsmod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public class Render {
    public void drawGui(GuiGraphics gui, String text) {
        final var mc = Minecraft.getInstance();

        int x = 10, y = 10;
        gui.drawString(mc.font,  text, x, y, 0xffffff);
    }
}
