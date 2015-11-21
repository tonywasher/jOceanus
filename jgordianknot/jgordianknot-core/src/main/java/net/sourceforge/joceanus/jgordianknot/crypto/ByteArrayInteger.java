/*******************************************************************************
 * jGordianKnot: Security Suite
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
package net.sourceforge.joceanus.jgordianknot.crypto;

import org.bouncycastle.util.Arrays;

import net.sourceforge.joceanus.jtethys.DataConverter;

/**
 * Simple class that handles a byte array as an integer housing an integer of max value 2
 * <sup>8n</sup> where n is the length of the byte array.
 */
public final class ByteArrayInteger {
    /**
     * The default counter length.
     */
    private static final int COUNTER_LEN = DataConverter.BYTES_INTEGER;

    /**
     * The counter length.
     */
    private final int theLength;

    /**
     * The buffer.
     */
    private final byte[] theBuffer;

    /**
     * Constructor.
     */
    public ByteArrayInteger() {
        /* use default length */
        this(COUNTER_LEN);
    }

    /**
     * Constructor.
     * @param pLen the length of the counter
     */
    public ByteArrayInteger(final int pLen) {
        /* initialise the buffer */
        theBuffer = new byte[pLen];
        theLength = pLen;
        reset();
    }

    /**
     * Constructor.
     * @param pSource the source byte array
     */
    public ByteArrayInteger(final byte[] pSource) {
        /* initialise the buffer */
        theLength = pSource.length;
        theBuffer = new byte[theLength];
        System.arraycopy(pSource, 0, theBuffer, 0, theLength);
    }

    /**
     * get buffer pointer.
     * @return the buffer
     */
    public byte[] getBuffer() {
        return theBuffer;
    }

    /**
     * Reset counter to zero.
     */
    public void reset() {
        Arrays.fill(theBuffer, (byte) 0);
    }

    /**
     * Compare counter limit.
     * @param pLimit the limit to check against
     * @return true/false the limit has been reached
     */
    public boolean compareLimit(final long pLimit) {
        /* Check that we are long length */
        if (theLength != DataConverter.BYTES_LONG) {
            return false;
        }

        /* Calculate existing value */
        long myVal = 0;
        for (int i = 0; i < theLength; i++) {
            myVal <<= DataConverter.NYBBLE_SHIFT;
            myVal += theBuffer[i]
                     & DataConverter.BYTE_MASK;
        }

        /* Determine whether we have reached the limit */
        return myVal >= pLimit;
    }

    /**
     * Iterate.
     * @return the buffer
     */
    public byte[] iterate() {
        /* Loop through the bytes */
        for (int i = theLength - 1; i >= 0; i--) {
            /* Increment the element */
            byte myByte = theBuffer[i];
            theBuffer[i] = ++myByte;
            if (myByte != 0) {
                break;
            }
        }

        /* Return the buffer */
        return theBuffer;
    }

    /**
     * Add a byte array as a counter.
     * @param pAdjust the array to add.
     * @return the buffer
     */
    public byte[] addTo(final byte[] pAdjust) {
        /* Access length of adjusting array */
        int myLength = Math.min(pAdjust.length, theLength);

        /* Loop through the adjustment bytes */
        boolean doCarry = false;
        int myOffset = 1;
        while (myOffset <= myLength) {
            /* Calculate sum at offset */
            int myNext = (theBuffer[theLength
                                    - myOffset] & DataConverter.BYTE_MASK)
                         + (pAdjust[myLength
                                    - myOffset] & DataConverter.BYTE_MASK);
            if (doCarry) {
                myNext++;
            }

            /* Store result and adjust offset */
            theBuffer[theLength
                      - myOffset] = (byte) myNext;
            myOffset++;

            /* Determine the carry */
            doCarry = (myNext & ~DataConverter.BYTE_MASK) != 0;
        }

        /* Adjust remaining bytes for carry */
        if (doCarry) {
            while (myOffset <= theLength) {
                /* Increment the element */
                byte myByte = theBuffer[theLength
                                        - myOffset];
                theBuffer[theLength
                          - myOffset] = ++myByte;
                if (myByte != 0) {
                    break;
                }

                /* Adjust offset */
                myOffset++;
            }
        }

        /* Return the buffer */
        return theBuffer;
    }
}
