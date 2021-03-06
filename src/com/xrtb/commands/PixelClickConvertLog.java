package com.xrtb.commands;

import com.xrtb.exchanges.google.GoogleWinObject;
import com.xrtb.exchanges.google.OpenRTB;
import com.xrtb.pojo.BidRequest;

/**
 * Base class for logging pixel loads, clicks and conversions.
 * 
 * @author Ben M. Faul
 *
 */
public class PixelClickConvertLog {
	public String instance;
	public String payload;
	public double lat;
	public double lon;
	public double price;
	public long time;
	public int type;
	public String ad_id;
	public String creative_id;
	public String bid_id;
	public boolean debug = false;
	public String exchange;
	public static final int PIXEL = 0;
	public static final int CLICK = 1;
	public static final int CONVERT = 2;
	

	public static void main(String[] args) {
		PixelClickConvertLog x = new PixelClickConvertLog();
		x.create("//pixel/citenko/4/3/25c40279-dd90-4caa-afc9-d0474705e0d1/0.0425/32.83/-83.65");

	}

	/**
	 * Default constructor
	 */
	public PixelClickConvertLog() {

	}

	/**
	 * Create the log from a chunk of text
	 * 
	 * @param data
	 *            String. The data to use.
	 */
	public void create(String data) {
		time = System.currentTimeMillis();
		if (data.contains("redirect")) {
			doClick(data);
			return;
		}
		
		doClick(payload);
		type = PIXEL;
	}

	/**
	 * Process a click
	 * 
	 * @param payload
	 *            String. The data to turn into the log message.
	 */
	void doClick(String payload) {
		this.payload = payload;
		String[] parts = payload.split("/");

		for (int i = 0; i < parts.length; i++) {
			if (parts[i].indexOf("=") > -1) {
				String[] items = parts[i].split("=");
				switch (items[0]) {
				case "lat":
					try {
						lat = Double.parseDouble(items[1]);
					} catch (Exception error) {
						lat = 0;
					}
					break;
				case "lon":
					try {
						lon = Double.parseDouble(items[1]);
					} catch (Exception error) {
						lon = 0;
					}
					break;
				case "price":
					if (exchange.equals(OpenRTB.GOOGLE)) {
						try {
							price = GoogleWinObject.decrypt(items[1], System.currentTimeMillis());
							price /= 1000;
						} catch (Exception e) {
							e.printStackTrace();
							price = 0;
						}
					} else {	
						try {
							price = Double.parseDouble(items[1]);
						} catch (Exception error) {
							System.err.println("Error in price for: " + payload);
							price = 0;
						}
					}
					break;
				case "bid_id":
					bid_id = items[1];
					break;
				case "ad_id":
					ad_id = items[1];
					break;
				case "creative_id":
					creative_id = items[1];
					break;
				case "exchange":
					exchange = items[1];
					break;
				}
			}
		}
		type = CLICK;
		time = System.currentTimeMillis();
	}
}
