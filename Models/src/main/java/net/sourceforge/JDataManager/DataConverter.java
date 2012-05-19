/*******************************************************************************
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import net.sourceforge.JDataManager.ModelException.ExceptionClass;

public class DataConverter {
    /**
     * Byte encoding
     */
    private final static String ENCODING = "UTF-8";

    /**
     * format a byte array as a hexadecimal string
     * @param pBytes the byte array
     * @return the string
     */
    public static String bytesToHexString(byte[] pBytes) {
        int myInt;
        int myDigit;
        char myChar;
        StringBuilder myValue;

        /* Allocate the string builder */
        myValue = new StringBuilder(2 * pBytes.length);

        /* For each byte in the value */
        for (Byte b : pBytes) {
            /* Access the byte as an unsigned integer */
            myInt = (int) b;
            if (myInt < 0)
                myInt += 256;

            /* Access the high digit */
            myDigit = myInt / 16;
            myChar = (char) ((myDigit > 9) ? ('a' + (myDigit - 10)) : ('0' + myDigit));

            /* Add it to the value string */
            myValue.append(myChar);

            /* Access the low digit */
            myDigit = myInt % 16;
            myChar = (char) ((myDigit > 9) ? ('a' + (myDigit - 10)) : ('0' + myDigit));

            /* Add it to the value string */
            myValue.append(myChar);
        }

        /* Return the string */
        return myValue.toString();
    }

    /**
     * format a long as a hexadecimal string
     * @param pValue the long value
     * @return the string
     */
    public static String longToHexString(long pValue) {
        int myDigit;
        char myChar;
        long myLong;
        StringBuilder myValue;

        /* Access the long value */
        myLong = pValue;

        /* Allocate the string builder */
        myValue = new StringBuilder();

        /* handle negative values */
        boolean isNegative = (myLong < 0);
        if (isNegative)
            myLong = -myLong;

        /* Special case for zero */
        if (myLong == 0)
            myValue.append('0');

        /* else need to loop through the digits */
        else {
            /* While we have digits to format */
            while (myLong > 0) {
                /* Access the digit and move to next one */
                myDigit = (int) (myLong % 16);
                myChar = (char) ((myDigit > 9) ? ('a' + (myDigit - 10)) : ('0' + myDigit));
                myValue.insert(0, myChar);
                myLong >>= 4;
            }

            /* Reinstate negative sign */
            if (isNegative)
                myValue.insert(0, '-');
        }

        /* Return the string */
        return myValue.toString();
    }

    /**
     * parse a byte array from a hexadecimal string
     * @param pHexString the hex string
     * @return the bytes
     * @throws ModelException
     */
    public static byte[] hexStringToBytes(String pHexString) throws ModelException {
        byte[] myByteValue;
        char myChar;
        int myInt;
        int myLen;

        /* Access the length of the hex string */
        myLen = pHexString.length();

        /* Check that it has an even length */
        if ((myLen % 2) != 0)
            throw new ModelException(ExceptionClass.DATA, "Invalid HexString Length: " + pHexString);

        /* Allocate the new bytes array */
        myByteValue = new byte[myLen / 2];

        /* Loop through the string */
        for (int i = 0; i < myLen; i += 2) {
            /* Access the top level byte */
            myChar = pHexString.charAt(i);
            myInt = 16 * ((myChar >= 'a') ? 10 + (myChar - 'a') : (myChar - '0'));

            /* Check that the char is a valid hex digit */
            if (!Character.isDigit(myChar) && ((myChar < 'a') || (myChar > 'f')))
                throw new ModelException(ExceptionClass.DATA, "Non Hexadecimal Value: " + pHexString);

            /* Access the second byte */
            myChar = pHexString.charAt(i + 1);
            myInt += ((myChar >= 'a') ? 10 + (myChar - 'a') : (myChar - '0'));

            /* Check that the char is a valid hex digit */
            if (!Character.isDigit(myChar) && ((myChar < 'a') || (myChar > 'f')))
                throw new ModelException(ExceptionClass.DATA, "Non Hexadecimal Value: " + pHexString);

            /* Convert to byte and store */
            if (myInt > 127)
                myInt -= 256;
            myByteValue[i / 2] = (byte) myInt;
        }

        /* Return the bytes */
        return myByteValue;
    }

    /**
     * parse a long from a hexadecimal string
     * @param pHexString the hex string
     * @return the bytes
     * @throws ModelException
     */
    public static long hexStringToLong(String pHexString) throws ModelException {
        int myLen;
        char myChar;
        long myValue = 0;
        String myHexString = pHexString;

        /* Access the length of the hex string */
        myLen = myHexString.length();

        /* handle negative values */
        boolean isNegative = ((myLen > 0) && (myHexString.charAt(0) == '-'));
        if (isNegative) {
            myHexString = myHexString.substring(1);
            myLen--;
        }

        /* Check that it has an even length */
        if ((myLen % 2) != 0)
            throw new ModelException(ExceptionClass.DATA, "Invalid HexString Length: " + pHexString);

        /* Loop through the string */
        for (int i = 0; i < myLen; i++) {
            /* Access the next character */
            myChar = myHexString.charAt(i);

            /* Check that the char is a valid hex digit */
            if (!Character.isDigit(myChar) && ((myChar < 'a') || (myChar > 'f')))
                throw new ModelException(ExceptionClass.DATA, "Non Hexadecimal Value: " + myHexString);

            /* Add into the value */
            myValue <<= 4;
            myValue += ((myChar >= 'a') ? 10 + (myChar - 'a') : (myChar - '0'));
        }

        /* Reinstate negative values */
        if (isNegative)
            myValue = -myValue;

        /* Return the value */
        return myValue;
    }

    /**
     * Convert character array to byte array
     * @param pChars the character array
     * @return the byte array
     * @throws ModelException
     */
    public static byte[] charsToByteArray(char[] pChars) throws ModelException {
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
        } catch (Exception e) {
            throw new ModelException(ExceptionClass.DATA, "Unable to convert character array to bytes");
        }
    }

    /**
     * Convert byte array to character array
     * @param pBytes the byte array
     * @return the character array
     * @throws ModelException
     */
    public static char[] bytesToCharArray(byte[] pBytes) throws ModelException {
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
        } catch (Exception e) {
            throw new ModelException(ExceptionClass.DATA, "Unable to convert byte array to characters");
        }
    }

    /**
     * parse a long from a byte array
     * @param pBytes the eight byte array holding the long
     * @return the long value
     */
    public static long byteArrayToLong(byte[] pBytes) {
        int myByte;
        long myValue = 0;

        /* Loop through the bytes */
        for (int i = 0; i < 8; i++) {
            /* Access the next byte as an unsigned integer */
            myByte = pBytes[i];
            myByte &= 255;

            /* Add in to value */
            myValue <<= 8;
            myValue += myByte;
        }

        /* Return the value */
        return myValue;
    }

    /**
     * build a byte array from a long
     * @param pValue the long value to convert
     * @return the byte array
     */
    public static byte[] longToByteArray(long pValue) {
        byte myByte;
        byte[] myBytes = new byte[8];
        long myValue = pValue;

        /* Loop through the bytes */
        for (int i = 8; i > 0; i--) {
            /* Access the next byte as an unsigned integer */
            myByte = (byte) (myValue & 255);
            myBytes[i - 1] = myByte;

            /* Adjust value */
            myValue >>= 8;
        }

        /* Return the value */
        return myBytes;
    }

    /**
     * parse a long from a byte array
     * @param pBytes the eight byte array holding the long
     * @return the long value
     */
    public static int byteArrayToInteger(byte[] pBytes) {
        int myByte;
        int myValue = 0;

        /* Loop through the bytes */
        for (int i = 0; i < 4; i++) {
            /* Access the next byte as an unsigned integer */
            myByte = pBytes[i];
            myByte &= 255;

            /* Add in to value */
            myValue <<= 8;
            myValue += myByte;
        }

        /* Return the value */
        return myValue;
    }

    /**
     * build a byte array from an integer
     * @param pValue the integer value to convert
     * @return the byte array
     */
    public static byte[] integerToByteArray(int pValue) {
        byte myByte;
        byte[] myBytes = new byte[4];
        int myValue = pValue;

        /* Loop through the bytes */
        for (int i = 4; i > 0; i--) {
            /* Access the next byte as an unsigned integer */
            myByte = (byte) (myValue & 255);
            myBytes[i - 1] = myByte;

            /* Adjust value */
            myValue >>= 8;
        }

        /* Return the value */
        return myBytes;
    }

    /**
     * get Bytes from String
     * @param pInput the bytes to obtain the string from
     * @return the bytes representing the bytes
     * @throws ModelException
     */
    public static String byteArrayToString(byte[] pInput) throws ModelException {
        try {
            return new String(pInput, ENCODING);
        } catch (Exception e) {
            throw new ModelException(ExceptionClass.DATA, "Failed to convert bytes to string", e);
        }
    }

    /**
     * get Bytes from String
     * @param pInput the string to obtain the bytes from
     * @return the bytes representing the string
     */
    public static byte[] stringToByteArray(String pInput) {
        try {
            return pInput.getBytes(ENCODING);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Remove a directory and all of its contents
     * @param pDir the directory to remove
     * @return success/failure
     */
    public static boolean removeDirectory(File pDir) {
        /* Clear the directory */
        if (!clearDirectory(pDir))
            return false;

        /* Delete the directory itself */
        return (!pDir.exists()) || (pDir.delete());
    }

    /**
     * Clear a directory of all of its contents
     * @param pDir the directory to clear
     * @return success/failure
     */
    public static boolean clearDirectory(File pDir) {
        /* Handle trivial operations */
        if (pDir == null)
            return true;
        if (!pDir.exists())
            return true;
        if (!pDir.isDirectory())
            return false;

        /* Loop through all items */
        for (File myFile : pDir.listFiles()) {
            /* If the file is a directory */
            if (myFile.isDirectory()) {
                /* Remove it recursively */
                if (!removeDirectory(myFile))
                    return false;
            }

            /* else remove the file */
            else if (!myFile.delete())
                return false;
        }

        /* All cleared */
        return true;
    }
}
