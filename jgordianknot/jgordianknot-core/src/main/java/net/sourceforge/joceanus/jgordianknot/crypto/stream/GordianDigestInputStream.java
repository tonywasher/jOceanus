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
package net.sourceforge.joceanus.jgordianknot.crypto.stream;

import java.io.InputStream;

import org.bouncycastle.util.Arrays;

import net.sourceforge.joceanus.jgordianknot.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianDigest;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Input stream Digest implementation.
 */
public class GordianDigestInputStream
        extends GordianInputStream {
    /**
     * The digest.
     */
    private final GordianDigest theDigest;

    /**
     * The expected result.
     */
    private byte[] theExpected;

    /**
     * Constructor.
     * @param pDigest the digest
     * @param pExpected the expected result
     * @param pInput the underlying input stream
     */
    protected GordianDigestInputStream(final GordianDigest pDigest,
                                       final byte[] pExpected,
                                       final InputStream pInput) {
        /* Initialise underlying class */
        super(pInput);

        /* Store parameters */
        theDigest = pDigest;
        theExpected = pExpected;

        /* Create processed buffer */
        setProcessedBuffer(new DigestBuffer(this));
    }

    /**
     * Obtain the Digest.
     * @return the digest
     */
    public GordianDigest getDigest() {
        return theDigest;
    }

    /**
     * Check result.
     * @throws OceanusException on error
     */
    private void checkResult() throws OceanusException {
        /* Calculate digest */
        byte[] myResult = theDigest.finish();

        /* Check valid MAC */
        if (!Arrays.areEqual(myResult, theExpected)) {
            throw new GordianDataException("Invalid Digest");
        }
    }

    /**
     * Buffer to hold the processed data prior to returning it to the caller.
     */
    private static final class DigestBuffer
            extends ProcessedBuffer {
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
         * @param pStream the input stream
         */
        private DigestBuffer(final GordianDigestInputStream pStream) {
            theStream = pStream;
            theDigest = theStream.getDigest();
        }

        @Override
        public int processBytes(final byte[] pBuffer,
                                final int pLength) throws OceanusException {
            /* Initialise variables */
            int iLength = pLength;

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