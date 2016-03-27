package com.jojen.test;

import java.io.IOException;


import com.jojen.lcd.AdafruitLcdPlate;
import com.jojen.lcd.Lcd;


public class TestLcd {
	
	static final int BUS_NO = 1;
	static final int BUS_ADDRESS = 0x20;

	public static void main(String[] args) throws IOException, InterruptedException {
		Lcd lcd = new AdafruitLcdPlate(BUS_NO, BUS_ADDRESS);
		lcd.write("Hello World!");
		Thread.sleep(5000);
		lcd.shutdown();
	}

}
