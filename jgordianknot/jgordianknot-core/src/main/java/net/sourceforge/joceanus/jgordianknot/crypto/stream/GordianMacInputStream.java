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
 * $URL: http://localhost/svn/Finance/jOceanus/trunk/jgordianknot/src/main/java/net/sourceforge/joceanus/jgordianknot/zip/EncryptionOutputStream.java $
 * $Revision: 570 $
 * $Author: Tony $
 * $Date: 2015-02-14 06:54:38 +0000 (Sat, 14 Feb 2015) $
 ******************************************************************************/
package net.sourceforge.joceanus.jgordianknot.crypto.stream;

import java.io.InputStream;

import org.bouncycastle.util.Arrays;

import net.sourceforge.joceanus.jgordianknot.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianMac;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Input stream MAC implementation.
 */
public class GordianMacInputStream
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
     * Constructor.
     * @param pMac the MAC
     * @param pExpected the expected result
     * @param pInput the underlying input stream
     */
    protected GordianMacInputStream(final GordianMac pMac,
                                    final byte[] pExpected,
                                    final InputStream pInput) {
        /* Initialise underlying class */
        super(pInput);

        /* Store parameters */
        theMac = pMac;
        theExpected = pExpected;

        /* Create processed buffer */
        setProcessedBuffer(new MacBuffer(this));
    }

    /**
     * Obtain the Mac.
     * @return the Mac
     */
    public GordianMac getMac() {
        return theMac;
    }

    /**
     * Check result.
     * @throws JOceanusException on error
     */
    private void checkResult() throws JOceanusException {
        /* Calculate MAC */
        byte[] myResult = theMac.finish();

        /* Check valid MAC */
        if (!Arrays.areEqual(myResult, theExpected)) {
            throw new GordianDataException("Invalid MAC");
        }
    }

    /**
     * Buffer to hold the processed data prior to returning it to the caller.
     */
    private static final class MacBuffer
            extends ProcessedBuffer {
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
        private MacBuffer(final GordianMacInputStream pStream) {
            theStream = pStream;
            theMac = theStream.getMac();
        }

        @Override
        public int processBytes(final byte[] pBuffer,
                                final int pLength) throws JOceanusException {
            /* Initialise variables */
            int iLength = pLength;

            /* If we have EOF from the input stream */
            if (iLength == -1) {
                /* Record the fact and reset the read length to zero */
                setEOFSeen();
                theStream.checkResult();
                return -1;
            }

            /* Update the MAC */
            theMac.update(pBuffer, 0, iLength);

            /* Set up buffer variables */
            setBuffer(pBuffer, iLength);
            return iLength;
        }
    }
}
