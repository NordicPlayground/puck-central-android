package no.nordicsemi.puckcentral.utils;

import java.math.BigInteger;

public class NumberUtils {

    public static byte[] stringNumberToByteArray(String number, int radix, int size) {
        byte[] array = new BigInteger(number, radix).toByteArray();
        byte[] sizedArray = new byte[size];
        int signOffset = array[0] == 0 ? 1 : 0; //http://stackoverflow.com/q/4407779
        System.arraycopy(array, signOffset, sizedArray, 0, Math.min(size, array.length));

        return sizedArray;
    }
}
