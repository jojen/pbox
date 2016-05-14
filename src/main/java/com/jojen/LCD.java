package com.jojen;

import com.pi4j.component.lcd.LCDTextAlignment;
import com.pi4j.component.lcd.impl.GpioLcdDisplay;
import com.pi4j.io.gpio.RaspiPin;

/**
 * Created by Jochen on 10.04.2016.
 */
public class LCD {
    private GpioLcdDisplay lcd;
    public final static int LCD_ROWS = 2;
    public final static int LCD_ROW_1 = 0;
    public final static int LCD_ROW_2 = 1;
    public final static int LCD_COLUMNS = 16;

    public static final String KEY_STATION_CAHNGE = "station_change";
    public static final String KEY_MESSAGE = "message";

    LCD() {
        this.lcd = new GpioLcdDisplay(LCD_ROWS,          // number of row supported by LCD
                LCD_COLUMNS,       // number of columns supported by LCD
                RaspiPin.GPIO_29,  // LCD RS pin
                RaspiPin.GPIO_28,  // LCD strobe pin
                RaspiPin.GPIO_27,  // LCD data bit 1
                RaspiPin.GPIO_26,  // LCD data bit 2
                RaspiPin.GPIO_06,  // LCD data bit 3
                RaspiPin.GPIO_05); // LCD data bit 4
    }

    void show(String s) {
        show(s, null, null);
    }




    void show(String s, String s2, String key) {
        if (s != null) {
            lcd.clear();
            if (s.length() > LCD_COLUMNS) {
                s = s.substring(0, 16);
            }

            if (key != null) {
                switch (key) {
                    case KEY_STATION_CAHNGE:
                        lcd.writeln(LCD_ROW_1, "neuer Sender:");
                        lcd.writeln(LCD_ROW_2, s);
                        break;
                    case KEY_MESSAGE:
                        lcd.writeln(LCD_ROW_1, s, LCDTextAlignment.ALIGN_CENTER);
                        break;
                    default:
                        lcd.writeln(LCD_ROW_1, s);
                        lcd.writeln(LCD_ROW_2, s2);
                }
            } else {
                lcd.writeln(LCD_ROW_1, s);
            }
        }

    }
}
