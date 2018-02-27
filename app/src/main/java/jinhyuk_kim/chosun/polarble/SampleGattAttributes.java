package jinhyuk_kim.chosun.polarble;

import java.util.HashMap;

/**
 * This class includes a small subset of standard GATT attributes for demonstration purposes.
 */
public class SampleGattAttributes {
    private static HashMap<String, String> attributes = new HashMap();
    public static String HEART_RATE_MEASUREMENT = "00002a37-0000-1000-8000-00805f9b34fb";
    //public static String CLICKER_INDICATE_CHARACTERISTIC = "00002a1c-0000-1000-8000-00805f9b34fb";
    public static String CLICKER_INDICATE_CHARACTERISTIC = "df342b03-53f9-43b4-acb6-62a63ca0615a";
    public static String CLICKER_WRITE_CHARACTERISTIC = "e4c937b3-7f6d-41f9-b997-40c561f4453b";
    
    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
    
    public static String BATTERY_SERVICE_UUID = "0000180F-0000-1000-8000-00805f9b34fb";
    public static String BATTERY_LEVEL_UUID = "00002a19-0000-1000-8000-00805f9b34fb";

    //public static String HEART_RATE_MEASUREMENT = "2A37";
    
    static {
        // Sample Services.
        attributes.put("0000180d-0000-1000-8000-00805f9b34fb", "Heart Rate Service");
        attributes.put("0000180a-0000-1000-8000-00805f9b34fb", "Device Information Service");
        // Sample Characteristics.
        attributes.put(HEART_RATE_MEASUREMENT, "Heart Rate Measurement");
        attributes.put("00002a29-0000-1000-8000-00805f9b34fb", "Manufacturer Name String");
    }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }
}
