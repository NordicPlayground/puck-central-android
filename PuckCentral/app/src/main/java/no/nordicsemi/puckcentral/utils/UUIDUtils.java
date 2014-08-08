package no.nordicsemi.puckcentral.utils;

import java.util.UUID;

public class UUIDUtils {

    public static UUID stringToUUID(String string){
        long firstLong = 0;
        long secondLong = 0;
        for(int i = 0;i < 8; i++) {
            firstLong <<= 8;
            firstLong |= string.charAt(i);
        }
        for(int i = 8;i < string.length(); i++) {
            secondLong <<= 8;
            secondLong |= string.charAt(i);
        }
        return new UUID(firstLong, secondLong);
    }
}
