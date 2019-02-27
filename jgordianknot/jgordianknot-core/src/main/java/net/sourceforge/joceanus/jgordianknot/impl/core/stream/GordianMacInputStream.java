/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2019 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.sourceforge.joceanus.jgordianknot.impl.core.stream;

import java.io.InputStream;

import org.bouncycastle.util.Arrays;

import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMac;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Input stream MAC implementation.
 */
class GordianMacInputStream
        extends GordianInputStream {
    /**
     * The MAC.
     */
    private final GordianMac theMac;

    /**
     * The expected result.
     */
    private final byte[] theExpected;

    /**
     * The expected digest.
     */
    private byte[] theDigest;

    /**
     * Constructor.
     * @param pMac the MAC
     * @param pExpected the expected result
     * @param pInput the underlying input stream
     */
    GordianMacInputStream(final GordianMac pMac,
                          final byte[] pExpected,
                          final InputStream pInput) {
        /* Initialise underlying class */
        super(pInput);

        /* Store parameters */
        theMac = pMac;
        theExpected = Arrays.copyOf(pExpected, pExpected.length);

        /* Create processed buffer */
        setProcessedBuffer(new GordianMacBuffer(this));
    }

    /**
     * Obtain the Mac.
     * @return the Mac
     */
    public GordianMac getMac() {
        return theMac;
    }

    /**
     * Set the expected digest.
     * @param pExpected the expected digest
     */
    void setExpectedDigest(final byte[] pExpected) {
        theDigest = pExpected;
    }

    /**
     * Check result.
     * @throws OceanusException on error
     */
    void checkResult() throws OceanusException {
        /* Record the digest */
        theMac.update(theDigest);

        /* Calculate MAC */
        final byte[] myResult = theMac.finish();

        /* Check valid MAC */
        if (!Arrays.areEqual(myResult, theExpected)) {
            throw new GordianDataException("Invalid MAC");
        }
    }

    /**
     * Buffer to hold the processed data prior to returning it to the caller.
     */
    private static final class GordianMacBuffer
            extends GordianProcessedBuffer {
        /**
         * The InputStream.
         */
        private final GordianMacInputStream theStream;

        /**
         * The MAC.
         */
        private final GordianMac theMac;

        /**
         * Constructor.
         * @param pStream the input stream
         */
        GordianMacBuffer(final GordianMacInputStream pStream) {
            theStream = pStream;
            theMac = theStream.getMac();
        }

        @Override
        public int processBytes(final byte[] pBuffer,
                                final int pLength) throws OceanusException {
            /* If we have EOF from the input stream */
            if (pLength == -1) {
                /* Record the fact and reset the read length to zero */
                setEOFSeen();
                theStream.checkResult();
                return -1;
            }

            /* Update the MAC */
            theMac.update(pBuffer, 0, pLength);

            /* Set up buffer variables */
            setBuffer(pBuffer, pLength);
            return pLength;
        }
    }
}
