/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.gordianknot.impl.core.kdf;

import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.digest.GordianDigest;
import net.sourceforge.joceanus.gordianknot.api.digest.GordianDigestFactory;
import net.sourceforge.joceanus.gordianknot.api.digest.GordianDigestSpec;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.gordianknot.api.mac.GordianMac;
import net.sourceforge.joceanus.gordianknot.api.mac.GordianMacFactory;
import net.sourceforge.joceanus.gordianknot.api.mac.GordianMacSpec;
import net.sourceforge.joceanus.gordianknot.api.mac.GordianMacSpecBuilder;

import java.util.Arrays;
import java.util.Iterator;

/**
 * HKDF functions.
 */
public class GordianHKDFEngine {
    /**
     * The Digest.
     */
    private final GordianDigest theDigest;

    /**
     * The HMac.
     */
    private final GordianMac theHMac;

    /**
     * Is this a primary engine?
     */
    private boolean isPrimary;

    /**
     * Constructor.
     * @param pFactory the security factory
     * @param pDigestSpec the digestSpec
     * @throws GordianException on error
     */
    public GordianHKDFEngine(final GordianFactory pFactory,
                             final GordianDigestSpec pDigestSpec) throws GordianException {
        /* Create the digest */
        final GordianDigestFactory myDigestFactory = pFactory.getDigestFactory();
        theDigest = myDigestFactory.createDigest(pDigestSpec);

        /* Create the hMac */
        final GordianMacFactory myMacFactory = pFactory.getMacFactory();
        final GordianMacSpec myMacSpec = GordianMacSpecBuilder.hMac(pDigestSpec);
        theHMac = myMacFactory.createMac(myMacSpec);
    }

    /**
     * Derive bytes.
     * @param pParams the parameters
     * @return the derived bytes
     * @throws GordianException on error
     */
    public byte[] deriveBytes(final GordianHKDFParams pParams) throws GordianException {
        /* Check parameters */
        if (pParams == null) {
            throw new IllegalStateException("Null HKDF parameters");
        }

        /* Determine the mode */
        final GordianHKDFMode myMode = pParams.getMode();
        byte[] myOutput = null;

        /* If we should extract the information */
        if (myMode.doExtract()) {
            myOutput = extractKeyingMaterial(pParams.saltIterator(), pParams.ikmIterator());
        }

        /* If we should expand the information */
        if (myMode.doExpand()) {
            /* Save the intermediate value */
            final byte[] myIntermediate = myOutput;

            /* Determine PRK and expand it */
            final byte[] myPRK = myOutput == null ? pParams.getPRK() : myOutput;
            myOutput = expandKeyingMaterial(pParams, myPRK);

            /* Clear intermediate result */
            if (myIntermediate != null) {
                Arrays.fill(myIntermediate, (byte) 0);
            }
        }

        /* Return the result */
        return myOutput;
    }

    /**
     * Extract keying material.
     * @param saltIterator the iterator over the salts
     * @param ikmIterator the iterator over the initial keying material
     * @return the extracted material
     * @throws GordianException on error
     */
    private byte[] extractKeyingMaterial(final Iterator<byte[]> saltIterator,
                                         final Iterator<byte[]> ikmIterator) throws GordianException {
        /* Determine the key */
        while (saltIterator.hasNext()) {
            theDigest.update(saltIterator.next());
        }
        theHMac.initKeyBytes(theDigest.finish());

        /* Extract the keying material */
        while (ikmIterator.hasNext()) {
            theHMac.update(ikmIterator.next());
        }
        return theHMac.finish();
    }

    /**
     * Expand the pseudo-random key.
     * @param pParams the parameters
     * @param pPRK the pseudo-random key
     * @return the expanded material
     * @throws GordianException on error
     */
    private byte[] expandKeyingMaterial(final GordianHKDFParams pParams,
                                        final byte[] pPRK) throws GordianException {
        /* Initialise the HMac */
        theHMac.initKeyBytes(pPRK);

        /* Allocate the output buffer */
        int myLenRemaining = pParams.getLength();
        final byte[] myOutput = new byte[myLenRemaining];
        final int myHashLen = theHMac.getMacSize();

        /* Initialise variables */
        byte[] myInput = null;
        int myOffset = 0;
        byte myCounter = 0;

        /* Loop while we have more data to obtain */
        while (myLenRemaining > 0) {
            /* Update with the results of the last loop */
            if (myInput != null) {
                theHMac.update(myInput);
                Arrays.fill(myInput, (byte) 0);
            }

            /* Update with the info */
            final Iterator<byte[]> myIterator = pParams.infoIterator();
            while (myIterator.hasNext()) {
                theHMac.update(myIterator.next());
            }

            /* Update with the counter */
            theHMac.update(myCounter++);

            /* Calculate the hash */
            myInput = theHMac.finish();

            /* Output the required bytes */
            final int myLenToCopy = Math.min(myLenRemaining, myHashLen);
            System.arraycopy(myInput, 0, myOutput, myOffset, myLenToCopy);
            myOffset += myLenToCopy;
            myLenRemaining -= myLenToCopy;
        }

        /* Clear final intermediate results */
        if (myInput != null) {
            Arrays.fill(myInput, (byte) 0);
        }

        /* Return the expanded key */
        return myOutput;
    }
}
