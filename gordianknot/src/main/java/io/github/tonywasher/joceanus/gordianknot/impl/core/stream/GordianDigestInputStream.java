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
import io.github.tonywasher.joceanus.gordianknot.api.digest.GordianDigest;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianDataException;
import org.bouncycastle.util.Arrays;

import java.io.InputStream;

/**
 * Input stream Digest implementation.
 */
class GordianDigestInputStream
        extends GordianInputStream {
    /**
     * The digest.
     */
    private final GordianDigest theDigest;

    /**
     * The expected result.
     */
    private final byte[] theExpected;

    /**
     * The MacStream.
     */
    private final GordianMacInputStream theMacStream;

    /**
     * Constructor.
     *
     * @param pDigest    the digest
     * @param pExpected  the expected result
     * @param pInput     the underlying input stream
     * @param pMacStream the MacStream
     * @throws GordianException on error
     */
    GordianDigestInputStream(final GordianDigest pDigest,
                             final byte[] pExpected,
                             final InputStream pInput,
                             final GordianMacInputStream pMacStream) throws GordianException {
        /* Initialise underlying class */
        super(pInput);

        /* Store parameters */
        theDigest = pDigest;
        theExpected = Arrays.copyOf(pExpected, pExpected.length);
        theMacStream = pMacStream;

        /* Create processed buffer */
        setProcessedBuffer(new GordianDigestBuffer(this));

        /* Report the expected Digest to the Mac */
        theMacStream.setExpectedDigest(theExpected);
    }

    /**
     * Obtain the Digest.
     *
     * @return the digest
     */
    public GordianDigest getDigest() {
        return theDigest;
    }

    /**
     * Check result.
     *
     * @throws GordianException on error
     */
    void checkResult() throws GordianException {
        /* Calculate digest */
        final byte[] myResult = theDigest.finish();

        /* Check valid digest */
        if (!Arrays.areEqual(myResult, theExpected)) {
            throw new GordianDataException("Invalid Digest");
        }
    }

    /**
     * Buffer to hold the processed data prior to returning it to the caller.
     */
    private static final class GordianDigestBuffer
            extends GordianProcessedBuffer {
        /**
         * The InputStream.
         */
        private final GordianDigestInputStream theStream;

        /**
         * The digest.
         */
        private final GordianDigest theDigest;

        /**
         * Constructor.
         *
         * @param pStream the input stream
         */
        GordianDigestBuffer(final GordianDigestInputStream pStream) {
            theStream = pStream;
            theDigest = theStream.getDigest();
        }

        @Override
        public int processBytes(final byte[] pBuffer,
                                final int pLength) throws GordianException {
            /* Initialise variables */
            final int iLength = pLength;

            /* If we have EOF from the input stream */
            if (iLength == -1) {
                /* Record the fact and reset the read length to zero */
                setEOFSeen();
                theStream.checkResult();
                return -1;
            }

            /* Update the digest */
            theDigest.update(pBuffer, 0, iLength);

            /* Set up buffer variables */
            setBuffer(pBuffer, iLength);
            return iLength;
        }
    }
}
