package sample;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.HashMap;
import java.util.Map;

public class Util {

    public static final HashMap<Integer, Color> COLORS = new HashMap<Integer, Color>() {{
        put(0, Color.valueOf("#CCC0B4"));
        put(2, Color.valueOf("#EEE4DA"));
        put(4, Color.valueOf("#EDE1C9"));
        put(8, Color.valueOf("#F3B37C"));
        put(16, Color.valueOf("#F69865"));
        put(32, Color.valueOf("#F87F5F"));
        put(64, Color.valueOf("#F9623B"));
        put(128, Color.valueOf("#ECD279"));
        put(256, Color.valueOf("#ECCF69"));
        put(512, Color.valueOf("#ECCB5A"));
        put(1024, Color.valueOf("#ECC84B"));
        put(2048, Color.valueOf("#ECC63E"));
        put(4096, Color.valueOf("#F76772"));
        put(8192, Color.valueOf("#F44D5F"));
        put(8192*2, Color.valueOf("#EE463C"));
        put(8192*4, Color.valueOf("#72B3DC"));
    }};

    public static Color fontColor(int value) {
        if(value == 2 || value == 4) return Color.valueOf("#776E65");
        else return Color.valueOf("#FFF4F3");
    }

    public static Font font(int value, int size) {
        switch (size) {
            case 3: {
                if(value <= 64) return new Font(60);
                else return new Font(45);
            }
            case 4: {
                if(value <= 64) return new Font(50);
                else if(value >= 128 && value <= 512) return new Font(45);
                else return new Font(40);
            }
            case 5: {
                if(value <= 64) return new Font(45);
                else if(value >= 128 && value <= 512) return new Font(35);
                else if(value >= 1024 && value <= 8192) return new Font(25);
                else return new Font(20);
            }
            case 6: {
                if(value <= 64) return new Font(40);
                else if(value >= 128 && value <= 512) return new Font(35);
                else if(value >= 1024 && value <= 8192) return new Font(25);
                else return new Font(20);
            }
            case 8: {
                if(value <= 64) return new Font(32);
                else if(value >= 128 && value <= 512) return new Font(25);
                else if(value >= 1024 && value <= 8192) return new Font(18);
                else return new Font(14);
            }
        }
        return null;
    }

}
