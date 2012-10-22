/*******************************************************************************
 * JDataManager: Java Data Manager
 * Copyright 2012 Tony Washer
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
package net.sourceforge.JDataManager;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;

import net.sourceforge.JDataManager.JDataException.ExceptionClass;

/**
 * Data Conversion utility functions.
 * @author Tony Washer
 */
public final class DataConverter {
    /**
     * Byte encoding.
     */
    private static final String ENCODING = "UTF-8";

    /**
     * Hexadecimal Radix.
     */
    public static final int HEX_RADIX = 16;

    /**
     * Number of bytes in a long.
     */
    public static final int BYTES_LONG = 8;

    /**
     * Number of bytes in an integer.
     */
    public static final int BYTES_INTEGER = 4;

    /**
     * Byte shift.
     */
    public static final int BYTE_SHIFT = 8;

    /**
     * Byte mask.
     */
    public static final int BYTE_MASK = 0xFF;

    /**
     * Color mask.
     */
    public static final int COLOR_MASK = 0x00FFFFFF;

    /**
     * Nybble shift.
     */
    public static final int NYBBLE_SHIFT = 4;

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
    private DataConverter() {
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
            myDigit = myInt & NYBBLE_MASK;
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
        boolean isNegative = (myLong < 0);
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
     * format a colour as a hexadecimal string.
     * @param pValue the long value
     * @return the string
     */
    public static String colorToHexString(final Color pValue) {
        /* Access the RGB value */
        int myValue = pValue.getRGB();
        myValue &= COLOR_MASK;

        /* Allocate the string builder */
        StringBuilder myBuilder = new StringBuilder();

        /* While we have digits to format */
        while (myValue > 0) {
            /* Access the digit and move to next one */
            int myDigit = (int) (myValue & NYBBLE_MASK);
            char myChar = Character.forDigit(myDigit, HEX_RADIX);
            myBuilder.insert(0, myChar);
            myValue >>>= NYBBLE_SHIFT;
        }

        /* Add zeros to front if less than 6 digits */
        while (myBuilder.length() < RGB_LENGTH) {
            myBuilder.insert(0, '0');
        }

        /* Insert a # sign */
        myBuilder.insert(0, '#');

        /* Return the string */
        return myBuilder.toString();
    }

    /**
     * parse a byte array from a hexadecimal string.
     * @param pHexString the hex string
     * @return the bytes
     * @throws JDataException on error
     */
    public static byte[] hexStringToBytes(final String pHexString) throws JDataException {
        /* Access the length of the hex string */
        int myLen = pHexString.length();

        /* Check that it has an even length */
        if ((myLen % 2) != 0) {
            throw new JDataException(ExceptionClass.DATA, "Invalid HexString Length: " + pHexString);
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
                throw new JDataException(ExceptionClass.DATA, "Non Hexadecimal Value: " + pHexString);
            }

            /* Initialise result */
            int myInt = myDigit << NYBBLE_SHIFT;

            /* Access the second byte */
            myChar = pHexString.charAt(i + 1);
            myDigit = Character.digit(myChar, HEX_RADIX);

            /* Check that the char is a valid hex digit */
            if (myDigit < 0) {
                throw new JDataException(ExceptionClass.DATA, "Non Hexadecimal Value: " + pHexString);
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
     * @throws JDataException on error
     */
    public static long hexStringToLong(final String pHexString) throws JDataException {
        /* Access the length of the hex string */
        String myHexString = pHexString;
        int myLen = myHexString.length();
        long myValue = 0;

        /* handle negative values */
        boolean isNegative = ((myLen > 0) && (myHexString.charAt(0) == '-'));
        if (isNegative) {
            myHexString = myHexString.substring(1);
            myLen--;
        }

        /* Check that it has an even length */
        if ((myLen % 2) != 0) {
            throw new JDataException(ExceptionClass.DATA, "Invalid HexString Length: " + pHexString);
        }

        /* Loop through the string */
        for (int i = 0; i < myLen; i++) {
            /* Access the next character */
            char myChar = myHexString.charAt(i);
            int myDigit = Character.digit(myChar, HEX_RADIX);

            /* Check that the char is a valid hex digit */
            if (myDigit < 0) {
                throw new JDataException(ExceptionClass.DATA, "Non Hexadecimal Value: " + pHexString);
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
     * @throws JDataException on error
     */
    public static byte[] charsToByteArray(final char[] pChars) throws JDataException {
        /* protect against exceptions */
        try {
            /* Transform the character array to a byte array */
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            OutputStreamWriter out = new OutputStreamWriter(baos, ENCODING);
            for (int ch : pChars) {
                out.write(ch);
            }
            out.flush();
            return baos.toByteArray();
        } catch (IOException e) {
            throw new JDataException(ExceptionClass.DATA, e.getMessage(), e);
        }
    }

    /**
     * Convert byte array to character array.
     * @param pBytes the byte array
     * @return the character array
     * @throws JDataException on error
     */
    public static char[] bytesToCharArray(final byte[] pBytes) throws JDataException {
        /* protect against exceptions */
        try {
            /* Allocate the character array allowing for one character per byte */
            char[] myArray = new char[pBytes.length];
            int myLen;

            /* Transform the byte array to a character array */
            ByteArrayInputStream bais = new ByteArrayInputStream(pBytes);
            InputStreamReader in = new InputStreamReader(bais, ENCODING);
            myLen = in.read(myArray);

            /* Cut down the array to the actual length */
            myArray = java.util.Arrays.copyOf(myArray, myLen);

            /* Return the array */
            return myArray;
        } catch (IOException e) {
            throw new JDataException(ExceptionClass.DATA, e.getMessage(), e);
        }
    }

    /**
     * parse a long from a byte array.
     * @param pBytes the eight byte array holding the long
     * @return the long value
     */
    public static long byteArrayToLong(final byte[] pBytes) {
        int myByte;
        long myValue = 0;

        /* Loop through the bytes */
        for (int i = 0; i < BYTES_LONG; i++) {
            /* Access the next byte as an unsigned integer */
            myByte = pBytes[i];
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
        byte myByte;
        byte[] myBytes = new byte[BYTES_LONG];
        long myValue = pValue;

        /* Loop through the bytes */
        for (int i = BYTES_LONG; i > 0; i--) {
            /* Access the next byte as an unsigned integer */
            myByte = (byte) (myValue & BYTE_MASK);
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
        int myByte;
        int myValue = 0;

        /* Loop through the bytes */
        for (int i = 0; i < BYTES_INTEGER; i++) {
            /* Access the next byte as an unsigned integer */
            myByte = pBytes[i];
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
        byte myByte;
        byte[] myBytes = new byte[BYTES_INTEGER];
        int myValue = pValue;

        /* Loop through the bytes */
        for (int i = BYTES_INTEGER; i > 0; i--) {
            /* Access the next byte as an unsigned integer */
            myByte = (byte) (myValue & BYTE_MASK);
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
     * @throws JDataException on error
     */
    public static String byteArrayToString(final byte[] pInput) throws JDataException {
        try {
            return new String(pInput, ENCODING);
        } catch (IOException e) {
            throw new JDataException(ExceptionClass.DATA, e.getMessage(), e);
        }
    }

    /**
     * get Bytes from String.
     * @param pInput the string to obtain the bytes from
     * @return the bytes representing the string
     * @throws JDataException on error
     */
    public static byte[] stringToByteArray(final String pInput) throws JDataException {
        try {
            return pInput.getBytes(ENCODING);
        } catch (IOException e) {
            throw new JDataException(ExceptionClass.DATA, e.getMessage(), e);
        }
    }

    /**
     * Simple function to combine hashes. Hashes are simply XORed together.
     * @param pFirst the first Hash
     * @param pSecond the second Hash
     * @return the combined hash
     * @throws JDataException on error
     */
    public static byte[] combineHashes(final byte[] pFirst,
                                       final byte[] pSecond) throws JDataException {
        byte[] myTarget = pSecond;
        byte[] mySource = pFirst;
        int myLen;
        int i;

        /* Handle nulls */
        if (pFirst == null) {
            return pSecond;
        }
        if (pSecond == null) {
            return pFirst;
        }

        /* If the target is smaller than the source */
        if (myTarget.length < mySource.length) {
            /* Reverse the order to make use of all bits */
            myTarget = pFirst;
            mySource = pSecond;
        }

        /* Allocate the target as a copy of the input */
        myTarget = Arrays.copyOf(myTarget, myTarget.length);

        /* Determine length of operation */
        myLen = mySource.length;

        /* Loop through the array bytes */
        for (i = 0; i < myTarget.length; i++) {
            /* Combine the bytes */
            myTarget[i] ^= mySource[i % myLen];
        }

        /* return the array */
        return myTarget;
    }
}
