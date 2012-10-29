/*******************************************************************************
 * jGordianKnot: Security Suite
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
package net.sourceforge.jOceanus.jGordianKnot;

import net.sourceforge.jOceanus.jDataManager.DataConverter;

import org.bouncycastle.util.Arrays;

/**
 * Simple iteration class that provides a counter housed as a byte array. The default counter is 4 bytes long
 * but any length can be requested.
 * @author Tony Washer
 */
public class IterationCounter {
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
     * get buffer pointer.
     * @return the buffer
     */
    protected byte[] getBuffer() {
        return theBuffer;
    }

    /**
     * Constructor.
     */
    protected IterationCounter() {
        /* use default length */
        this(COUNTER_LEN);
    }

    /**
     * Constructor.
     * @param pLen the length of the counter
     */
    protected IterationCounter(final int pLen) {
        /* initialise the buffer */
        theBuffer = new byte[pLen];
        theLength = pLen;
        Arrays.fill(theBuffer, (byte) 0);
    }

    /**
     * Iterate.
     * @return the buffer
     */
    protected byte[] iterate() {
        /* Loop through the bytes */
        for (int i = theLength - 1; i >= 0; i--) {
            /* Increment the element */
            if (++theBuffer[i] != 0) {
                break;
            }
        }

        /* Return the buffer */
        return theBuffer;
    }
}
