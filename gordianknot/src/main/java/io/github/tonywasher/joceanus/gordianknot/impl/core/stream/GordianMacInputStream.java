/*
 * GordianKnot: Security Suite
 * Copyright 2012-2026. Tony Washer
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
 */
package io.github.tonywasher.joceanus.gordianknot.impl.core.stream;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.mac.GordianMac;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianDataException;
import org.bouncycastle.util.Arrays;

import java.io.InputStream;

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
     * Have we closed the stream?.
     */
    private boolean haveClosed;

    /**
     * Constructor.
     *
     * @param pMac      the MAC
     * @param pExpected the expected result
     * @param pInput    the underlying input stream
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
     *
     * @return the Mac
     */
    public GordianMac getMac() {
        return theMac;
    }

    /**
     * Set the expected digest.
     *
     * @param pExpected the expected digest
     * @throws GordianException on error
     */
    void setExpectedDigest(final byte[] pExpected) throws GordianException {
        /* Set the expected client */
        theDigest = pExpected;

        /* If we are late reporting the digest, then check result now */
        if (haveClosed) {
            checkResult();
        }
    }

    /**
     * Check result.
     *
     * @throws GordianException on error
     */
    void checkResult() throws GordianException {
        /*
         * If we are reading a small file, we may end up closing the input file before the digest stream is created, and
         *  therefore the digest has not yet been reported. If this is the case, defer processing until the digest is reported
         */
        if (theDigest == null) {
            /* Just note that we have closed and return */
            haveClosed = true;
            return;
        }

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
         *
         * @param pStream the input stream
         */
        GordianMacBuffer(final GordianMacInputStream pStream) {
            theStream = pStream;
            theMac = theStream.getMac();
        }

        @Override
        public int processBytes(final byte[] pBuffer,
                                final int pLength) throws GordianException {
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
