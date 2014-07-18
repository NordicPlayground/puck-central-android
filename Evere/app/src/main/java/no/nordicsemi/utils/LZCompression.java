package no.nordicsemi.utils;

import java.util.ArrayList;

/* ported from http://bcl.comli.eu/ */
public class LZCompression {
    final static int LZ_MAX_OFFSET = 100000;

    public static byte[] compress(byte[] input) {
        int[] histogram = new int[256];
        ArrayList<Byte> output = new ArrayList<>();

        /* Create histogram */
        for(int i = 0; i < input.length; i++) {
            histogram[input[i] & 0xff]++;
        }

        /* Find the least common byte, and use it as the marker symbol */
        byte marker = 0;
        for(int i = 0; i < 256; i++) {
            if(histogram[i] < histogram[marker]) {
                marker = (byte) i;
            }
        }

        /* Remember the marker symbol for the decoder */
        output.add(marker);

        /* Start of compression */
        int inputPosition = 0;

        /* Main compression loop */
        int bytesLeft = input.length;
        do {
            /* Determine most distant position */
            int maxOffset = Math.min(LZ_MAX_OFFSET, inputPosition);

            /* Get pointer to current position */
            int currentPosition = inputPosition;

            /* Search history window for maximum length string match */
            int bestLength = 3;
            int bestOffset = 0;
            for(int offset = 3; offset <= maxOffset ; offset++) {

                /* Get pointer to candidate string */
                int candidateStringIndex = currentPosition - offset;

                /* Quickly determine if this is a candidate (for speed) */
                if((input[candidateStringIndex] == input[currentPosition]) &&
                   (input[candidateStringIndex + bestLength] == input[currentPosition + bestLength])) {

                    /* Determine maximum length for this offset */
                    int maxLength = (bytesLeft <= offset ? bytesLeft - 1: offset);

                    /* Count maximum length match at this offset */
                    int length = stringCompare(input, candidateStringIndex, currentPosition, 0, maxLength);

                    /* Better match than any previous match? */
                    if(length > bestLength) {
                        bestLength = length;
                        bestOffset = offset;
                    }
                }
            }

            /* Was there a good enough match? */
            if((bestLength>= 8) ||
               ((bestLength == 4) && (bestOffset <= 0x0000007f)) ||
               ((bestLength == 5) && (bestOffset <= 0x00003fff)) ||
               ((bestLength == 6) && (bestOffset <= 0x001fffff)) ||
               ((bestLength == 7) && (bestOffset <= 0x0fffffff)) ) {
                output.add(marker);
                writeVariableSize(bestLength, output);
                writeVariableSize(bestOffset, output);
                inputPosition += bestLength;
                bytesLeft -= bestLength;
            } else {
                /* Output single byte (or two bytes if marker byte) */
                byte symbol = input[inputPosition++];
                output.add(symbol);
                if(symbol == marker) {
                    output.add((byte) 0);
                }
                bytesLeft--;
            }
        } while(bytesLeft > 3);

        /* Dump remaining bytes, if any */
        while(inputPosition < input.length) {
            if(input[inputPosition] == marker) {
                output.add(marker);
                output.add((byte) 0);
            } else {
                output.add(input[inputPosition]);
            }
            inputPosition++;
        }

        byte[] result = new byte[output.size()];
        for(int i = 0; i < result.length; i++) {
            result[i] = output.get(i);
        }
        return result;
    }

    private static int stringCompare(byte[]input, int string1Offset, int string2Offset, int minLength, int maxLength) {
        int len = minLength;
        while((len < maxLength) && (input[string1Offset + len] == input[string2Offset + len])) {
            len++;
        }
        return len;
    }

    private static void writeVariableSize(int x, ArrayList<Byte> output) {

        /* Determine number of bytes needed to store the number x */
        int y = x >> 3;
        int num_bytes;
        for(num_bytes = 5; num_bytes >= 2; -- num_bytes ) {
            if((y & 0xfe000000) != 0) {
                break;
            }
            y <<= 7;
        }

        /* Write all bytes, seven bits in each, with
         * 8:th bit set for all  but the last byte. */
        for(int i = num_bytes - 1; i >= 0; i--) {
            byte b = (byte) ((x >> (i*7)) & 0x0000007f);
            if(i > 0) {
                b |= 0x00000080;
            }
            output.add(b);
        }
    }
}

