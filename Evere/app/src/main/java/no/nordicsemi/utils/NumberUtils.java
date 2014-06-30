package no.nordicsemi.utils;

import java.math.BigInteger;

public class NumberUtils {

    public static byte[] stringNumberToByteArray(String number, int radix, int size) {
        byte[] array = new BigInteger(number, radix).toByteArray();
        byte[] sizedArray = new byte[size];
        System.arraycopy(array, 0, sizedArray, 0, Math.min(size, array.length));
        return sizedArray;
    }
}
