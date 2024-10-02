package nl.lunish.hudstatsmod;

public class Utils {
    public String ticksToHumanTime(int ticks) {
        int seconds = ticks / 20;
        int minutes = seconds / 60;
        int hours = minutes / 60;
        int days = hours / 24;

        seconds = seconds % 60;
        minutes = minutes % 60;
        hours = hours % 24;

        String text = "Playtime: ";

        if (days > 0) {
            text += String.format("%1d day%s, ", days, days == 1 ? "" : "s");
        }

        text += String.format("%02d:%02d:%02d", hours, minutes, seconds);
        return text;
    }
}
