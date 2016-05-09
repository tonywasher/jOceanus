/*******************************************************************************
 * jTethys: Java Utilities
 * Copyright 2012,2014 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jtethys;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Data Conversion utility functions.
 * @author Tony Washer
 */
public final class TethysDataConverter {
    /**
     * Invalid hexadecimal length string.
     */
    private static final String ERROR_HEXLEN = "Invalid HexString Length: ";

    /**
     * Invalid hexadecimal error string.
     */
    private static final String ERROR_HEXDIGIT = "Non Hexadecimal Value: ";

    /**
     * Base64 Encoding array.
     */
    private static final char[] BASE64_ENCODE = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();

    /**
     * Base64 Decoding array.
     */
    private static final int[] BASE64_DECODE = new int[BASE64_ENCODE.length << 1];

    static {
        for (int i = 0; i < BASE64_ENCODE.length; i++) {
            BASE64_DECODE[BASE64_ENCODE[i]] = i;
        }
    }

    /**
     * Base64 triplet size.
     */
    private static final int BASE64_TRIPLE = 3;

    /**
     * Base64 padding character.
     */
    private static final char BASE64_PAD = '=';

    /**
     * Base64 shift 1.
     */
    private static final int BASE64_SHIFT1 = 2;

    /**
     * Base64 shift 2.
     */
    private static final int BASE64_SHIFT2 = 4;

    /**
     * Base64 shift 3.
     */
    private static final int BASE64_SHIFT3 = 6;

    /**
     * Hexadecimal Radix.
     */
    public static final int HEX_RADIX = 16;

    /**
     * Number of bytes in a long.
     */
    public static final int BYTES_LONG = Long.SIZE
                                         / Byte.SIZE;

    /**
     * Number of bytes in an integer.
     */
    public static final int BYTES_INTEGER = Integer.SIZE
                                            / Byte.SIZE;

    /**
     * Byte shift.
     */
    public static final int BYTE_SHIFT = Byte.SIZE;

    /**
     * Byte mask.
     */
    public static final int BYTE_MASK = 0xFF;

    /**
     * Base64 mask.
     */
    public static final int BASE64_MASK = 0x3F;

    /**
     * Color mask.
     */
    public static final int COLOR_MASK = 0x00FFFFFF;

    /**
     * Nybble shift.
     */
    public static final int NYBBLE_SHIFT = Byte.SIZE >> 1;

    /**
     * Nybble mask.
     */
    public static final int NYBBLE_MASK = 0xF;

    /**
     * RGB colour length.
     */
    public static final int RGB_LENGTH = 6;

    /**
     * Private constructor to avoid instantiation.
     */
    private TethysDataConverter() {
    }

    /**
     * format a byte array as a hexadecimal string.
     * @param pBytes the byte array
     * @return the string
     */
    public static String bytesToHexString(final byte[] pBytes) {
        /* Allocate the string builder */
        StringBuilder myValue = new StringBuilder(2 * pBytes.length);

        /* For each byte in the value */
        for (Byte b : pBytes) {
            /* Access the byte as an unsigned integer */
            int myInt = (int) b;
            if (myInt < 0) {
                myInt += BYTE_MASK + 1;
            }

            /* Access the high nybble */
            int myDigit = myInt >>> NYBBLE_SHIFT;
            char myChar = Character.forDigit(myDigit, HEX_RADIX);

            /* Add it to the value string */
            myValue.append(myChar);

            /* Access the low digit */
            myDigit = myInt
                      & NYBBLE_MASK;
            myChar = Character.forDigit(myDigit, HEX_RADIX);

            /* Add it to the value string */
            myValue.append(myChar);
        }

        /* Return the string */
        return myValue.toString();
    }

    /**
     * format a long as a hexadecimal string.
     * @param pValue the long value
     * @return the string
     */
    public static String longToHexString(final long pValue) {
        /* Access the long value */
        long myLong = pValue;

        /* Allocate the string builder */
        StringBuilder myValue = new StringBuilder();

        /* handle negative values */
        boolean isNegative = myLong < 0;
        if (isNegative) {
            myLong = -myLong;
        }

        /* Special case for zero */
        if (myLong == 0) {
            myValue.append("00");

            /* else need to loop through the digits */
        } else {
            /* While we have digits to format */
            while (myLong > 0) {
                /* Access the digit and move to next one */
                int myDigit = (int) (myLong & NYBBLE_MASK);
                char myChar = Character.forDigit(myDigit, HEX_RADIX);
                myValue.insert(0, myChar);
                myLong >>>= NYBBLE_SHIFT;
            }

            /* If we are odd length prefix a zero */
            if ((myValue.length() & 1) != 0) {
                myValue.insert(0, '0');
            }

            /* Reinstate negative sign */
            if (isNegative) {
                myValue.insert(0, '-');
            }
        }

        /* Return the string */
        return myValue.toString();
    }

    /**
     * parse a byte array from a hexadecimal string.
     * @param pHexString the hex string
     * @return the bytes
     * @throws OceanusException on error
     */
    public static byte[] hexStringToBytes(final String pHexString) throws OceanusException {
        /* Access the length of the hex string */
        int myLen = pHexString.length();

        /* Check that it has an even length */
        if ((myLen % 2) != 0) {
            throw new TethysDataException(ERROR_HEXLEN
                                          + pHexString);
        }

        /* Allocate the new bytes array */
        byte[] myByteValue = new byte[myLen / 2];

        /* Loop through the string */
        for (int i = 0; i < myLen; i += 2) {
            /* Access the top level byte */
            char myChar = pHexString.charAt(i);
            int myDigit = Character.digit(myChar, HEX_RADIX);

            /* Check that the char is a valid hex digit */
            if (myDigit < 0) {
                throw new TethysDataException(ERROR_HEXDIGIT
                                              + pHexString);
            }

            /* Initialise result */
            int myInt = myDigit << NYBBLE_SHIFT;

            /* Access the second byte */
            myChar = pHexString.charAt(i + 1);
            myDigit = Character.digit(myChar, HEX_RADIX);

            /* Check that the char is a valid hex digit */
            if (myDigit < 0) {
                throw new TethysDataException(ERROR_HEXDIGIT
                                              + pHexString);
            }

            /* Add into result */
            myInt += myDigit;

            /* Convert to byte and store */
            if (myInt > Byte.MAX_VALUE) {
                myInt -= BYTE_MASK + 1;
            }
            myByteValue[i / 2] = (byte) myInt;
        }

        /* Return the bytes */
        return myByteValue;
    }

    /**
     * parse a long from a hexadecimal string.
     * @param pHexString the hex string
     * @return the bytes
     * @throws OceanusException on error
     */
    public static long hexStringToLong(final String pHexString) throws OceanusException {
        /* Access the length of the hex string */
        String myHexString = pHexString;
        int myLen = myHexString.length();
        long myValue = 0;

        /* handle negative values */
        boolean isNegative = (myLen > 0)
                             && (myHexString.charAt(0) == '-');
        if (isNegative) {
            myHexString = myHexString.substring(1);
            myLen--;
        }

        /* Check that it has an even length */
        if ((myLen % 2) != 0) {
            throw new TethysDataException(ERROR_HEXLEN
                                          + pHexString);
        }

        /* Loop through the string */
        for (int i = 0; i < myLen; i++) {
            /* Access the next character */
            char myChar = myHexString.charAt(i);
            int myDigit = Character.digit(myChar, HEX_RADIX);

            /* Check that the char is a valid hex digit */
            if (myDigit < 0) {
                throw new TethysDataException(ERROR_HEXDIGIT
                                              + pHexString);
            }

            /* Add into the value */
            myValue <<= NYBBLE_SHIFT;
            myValue += myDigit;
        }

        /* Reinstate negative values */
        if (isNegative) {
            myValue = -myValue;
        }

        /* Return the value */
        return myValue;
    }

    /**
     * Convert character array to byte array.
     * @param pChars the character array
     * @return the byte array
     * @throws OceanusException on error
     */
    public static byte[] charsToByteArray(final char[] pChars) throws OceanusException {
        /* protect against exceptions */
        try {
            /* Transform the character array to a byte array */
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            OutputStreamWriter out = new OutputStreamWriter(baos, StandardCharsets.UTF_8);
            out.write(pChars, 0, pChars.length);
            out.flush();
            return baos.toByteArray();
        } catch (IOException e) {
            throw new TethysDataException(e.getMessage(), e);
        }
    }

    /**
     * Convert byte array to character array.
     * @param pBytes the byte array
     * @return the character array
     * @throws OceanusException on error
     */
    public static char[] bytesToCharArray(final byte[] pBytes) throws OceanusException {
        /* protect against exceptions */
        try {
            /* Allocate the character array allowing for one character per byte */
            char[] myArray = new char[pBytes.length];
            int myLen;

            /* Transform the byte array to a character array */
            ByteArrayInputStream bais = new ByteArrayInputStream(pBytes);
            InputStreamReader in = new InputStreamReader(bais, StandardCharsets.UTF_8);
            myLen = in.read(myArray);

            /* Cut down the array to the actual length */
            myArray = Arrays.copyOf(myArray, myLen);

            /* Return the array */
            return myArray;
        } catch (IOException e) {
            throw new TethysDataException(e.getMessage(), e);
        }
    }

    /**
     * parse a long from a byte array.
     * @param pBytes the eight byte array holding the long
     * @return the long value
     */
    public static long byteArrayToLong(final byte[] pBytes) {
        /* Loop through the bytes */
        long myValue = 0;
        for (int i = 0; i < BYTES_LONG; i++) {
            /* Access the next byte as an unsigned integer */
            int myByte = pBytes[i];
            myByte &= BYTE_MASK;

            /* Add in to value */
            myValue <<= BYTE_SHIFT;
            myValue += myByte;
        }

        /* Return the value */
        return myValue;
    }

    /**
     * build a byte array from a long.
     * @param pValue the long value to convert
     * @return the byte array
     */
    public static byte[] longToByteArray(final long pValue) {
        /* Loop through the bytes */
        long myValue = pValue;
        byte[] myBytes = new byte[BYTES_LONG];
        for (int i = BYTES_LONG; i > 0; i--) {
            /* Store the next byte */
            byte myByte = (byte) (myValue & BYTE_MASK);
            myBytes[i - 1] = myByte;

            /* Adjust value */
            myValue >>= BYTE_SHIFT;
        }

        /* Return the value */
        return myBytes;
    }

    /**
     * parse an integer from a byte array.
     * @param pBytes the four byte array holding the integer
     * @return the integer value
     */
    public static int byteArrayToInteger(final byte[] pBytes) {
        /* Loop through the bytes */
        int myValue = 0;
        for (int i = 0; i < BYTES_INTEGER; i++) {
            /* Access the next byte as an unsigned integer */
            int myByte = pBytes[i];
            myByte &= BYTE_MASK;

            /* Add in to value */
            myValue <<= BYTE_SHIFT;
            myValue += myByte;
        }

        /* Return the value */
        return myValue;
    }

    /**
     * build a byte array from an integer.
     * @param pValue the integer value to convert
     * @return the byte array
     */
    public static byte[] integerToByteArray(final int pValue) {
        /* Loop through the bytes */
        byte[] myBytes = new byte[BYTES_INTEGER];
        int myValue = pValue;
        for (int i = BYTES_INTEGER; i > 0; i--) {
            /* Store the next byte */
            byte myByte = (byte) (myValue & BYTE_MASK);
            myBytes[i - 1] = myByte;

            /* Adjust value */
            myValue >>= BYTE_SHIFT;
        }

        /* Return the value */
        return myBytes;
    }

    /**
     * get Bytes from String.
     * @param pInput the bytes to obtain the string from
     * @return the bytes representing the bytes
     */
    public static String byteArrayToString(final byte[] pInput) {
        return new String(pInput, StandardCharsets.UTF_8);
    }

    /**
     * get Bytes from String.
     * @param pInput the string to obtain the bytes from
     * @return the bytes representing the string
     */
    public static byte[] stringToByteArray(final String pInput) {
        return pInput.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Simple function to combine hashes. Hashes are simply XORed together.
     * @param pFirst the first Hash
     * @param pSecond the second Hash
     * @return the combined hash
     * @throws OceanusException on error
     */
    public static byte[] combineHashes(final byte[] pFirst,
                                       final byte[] pSecond) throws OceanusException {
        /* Handle nulls */
        if (pFirst == null) {
            return pSecond;
        }
        if (pSecond == null) {
            return pFirst;
        }

        /* If the target is smaller than the source */
        byte[] myTarget = pSecond;
        byte[] mySource = pFirst;
        if (myTarget.length < mySource.length) {
            /* Reverse the order to make use of all bits */
            myTarget = pFirst;
            mySource = pSecond;
        }

        /* Allocate the target as a copy of the input */
        myTarget = Arrays.copyOf(myTarget, myTarget.length);

        /* Determine length of operation */
        int myLen = mySource.length;

        /* Loop through the array bytes */
        for (int i = 0; i < myTarget.length; i++) {
            /* Combine the bytes */
            myTarget[i] ^= mySource[i
                                    % myLen];
        }

        /* return the array */
        return myTarget;
    }

    /**
     * Simple function to build a hash result.
     * @param pResult the result Hash
     * @param pHash the calculated Hash
     * @throws OceanusException on error
     */
    public static void buildHashResult(final byte[] pResult,
                                       final byte[] pHash) throws OceanusException {
        /* If the target is smaller than the source */
        int myLen = pResult.length;
        if (myLen != pHash.length) {
            throw new TethysDataException("Hashes are different lengths");
        }
        /* Loop through the array bytes */
        for (int i = 0; i < myLen; i++) {
            /* Combine the bytes */
            pResult[i] ^= pHash[i];
        }
    }

    /**
     * Convert a byte array to a Base64 string.
     * @param pBytes the byte array (not null)
     * @return the translated Base64 string (not null)
     */
    public static String byteArrayToBase64(final byte[] pBytes) {
        /* Determine input length and allocate output buffer */
        int myLen = pBytes.length;
        StringBuilder myBuilder = new StringBuilder(myLen << 1);
        byte[] myTriplet = new byte[BASE64_TRIPLE];

        /* Loop through the input bytes */
        int myIn = 0;
        while (myIn < myLen) {
            /* Access input triplet */
            myTriplet[0] = pBytes[myIn++];
            myTriplet[1] = (myIn < myLen)
                                          ? pBytes[myIn++]
                                          : 0;
            myTriplet[2] = (myIn < myLen)
                                          ? pBytes[myIn++]
                                          : 0;

            /* Convert to base64 */
            myBuilder.append(BASE64_ENCODE[(myTriplet[0] >> BASE64_SHIFT1)
                                           & BASE64_MASK]);
            myBuilder.append(BASE64_ENCODE[((myTriplet[0] << BASE64_SHIFT2) | ((myTriplet[1] & BYTE_MASK) >> BASE64_SHIFT2))
                                           & BASE64_MASK]);
            myBuilder.append(BASE64_ENCODE[((myTriplet[1] << BASE64_SHIFT1) | ((myTriplet[2] & BYTE_MASK) >> BASE64_SHIFT3))
                                           & BASE64_MASK]);
            myBuilder.append(BASE64_ENCODE[myTriplet[2]
                                           & BASE64_MASK]);
        }

        /* Handle short input */
        int myXtra = myLen
                     % myTriplet.length;
        if (myXtra > 0) {
            /* Determine padding length */
            myXtra = myTriplet.length
                     - myXtra;

            /* Remove redundant characters */
            myBuilder.setLength(myBuilder.length()
                                - myXtra);

            /* Replace with padding character */
            while (myXtra-- > 0) {
                myBuilder.append(BASE64_PAD);
            }
        }

        /* Convert chars to string */
        return myBuilder.toString();
    }

    /**
     * Convert a Base64 string into a byte array.
     * @param pBase64 the Base64 string (not null)
     * @return the byte array (not null)
     */
    public static byte[] base64ToByteArray(final String pBase64) {
        /* Access input as chars */
        char[] myBase64 = pBase64.toCharArray();
        int myLen = myBase64.length;

        /* Determine number of padding bytes */
        int myNumPadding = 0;
        if (myBase64[myLen - 1] == BASE64_PAD) {
            myNumPadding++;
            if (myBase64[myLen - 2] == BASE64_PAD) {
                myNumPadding++;
            }
        }

        /* Allocate the output buffer and index */
        int myOutLen = ((myLen * BASE64_TRIPLE) >> 2)
                       - myNumPadding;
        byte[] myOutput = new byte[myOutLen];

        /* Loop through the base64 input */
        int myIn = 0;
        int myOut = 0;
        while (myOut < myOutLen) {
            /* Build first byte */
            int c0 = BASE64_DECODE[myBase64[myIn++]];
            int c1 = BASE64_DECODE[myBase64[myIn++]];
            myOutput[myOut++] = (byte) (((c0 << BASE64_SHIFT1) | (c1 >> BASE64_SHIFT2)) & BYTE_MASK);

            /* Build second byte */
            if (myOut < myOutLen) {
                int c2 = BASE64_DECODE[myBase64[myIn++]];
                myOutput[myOut++] = (byte) (((c1 << BASE64_SHIFT2) | (c2 >> BASE64_SHIFT1)) & BYTE_MASK);

                /* Build third byte */
                if (myOut < myOutLen) {
                    int c3 = BASE64_DECODE[myBase64[myIn++]];
                    myOutput[myOut++] = (byte) (((c2 << BASE64_SHIFT3) | c3) & BYTE_MASK);
                }
            }
        }
        return myOutput;
    }
}
