/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2024 Tony Washer
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

import net.sourceforge.joceanus.gordianknot.api.digest.GordianDigest;
import net.sourceforge.joceanus.gordianknot.api.digest.GordianDigestFactory;
import net.sourceforge.joceanus.gordianknot.api.digest.GordianDigestSpec;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.gordianknot.api.mac.GordianMac;
import net.sourceforge.joceanus.gordianknot.api.mac.GordianMacFactory;
import net.sourceforge.joceanus.gordianknot.api.mac.GordianMacSpec;
import net.sourceforge.joceanus.gordianknot.api.mac.GordianMacSpecBuilder;
import net.sourceforge.joceanus.oceanus.OceanusException;

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
     * The Parameters.
     */
    private GordianHKDFParams theParams;

    /**
     * Constructor.
     * @param pFactory the security factory
     * @param pDigestSpec the digestSpec
     * @throws OceanusException on error
     */
    public GordianHKDFEngine(final GordianFactory pFactory,
                             final GordianDigestSpec pDigestSpec) throws OceanusException {
        /* Create the digest */
        final GordianDigestFactory myDigestFactory = pFactory.getDigestFactory();
        theDigest = myDigestFactory.createDigest(pDigestSpec);

        /* Create the hMac */
        final GordianMacFactory myMacFactory = pFactory.getMacFactory();
        final GordianMacSpec myMacSpec = GordianMacSpecBuilder.hMac(pDigestSpec);
        theHMac = myMacFactory.createMac(myMacSpec);
    }

    /**
     * Set mode to extractOnly.
     * @return the engine
     */
    public GordianHKDFEngine extractOnly() {
        theParams = GordianHKDFParams.extractOnly();
        return this;
    }

    /**
     * Set mode to expandOnly.
     * @param pPRK the pseudo-random key
     * @param pLength the length
     * @return the engine
     */
    public GordianHKDFEngine expandOnly(final byte[] pPRK,
                                        final int pLength) {
        theParams = GordianHKDFParams.expandOnly(pPRK, pLength);
        return this;
    }

    /**
     * Set mode to extractThenExpand.
     * @param pLength the length
     * @return the engine
     */
    public GordianHKDFEngine extractThenExpand(final int pLength) {
        theParams = GordianHKDFParams.extractThenExpand(pLength);
        return this;
    }

    /**
     * Add iKM.
     * @param pIKM the initial keying material
     * @return the engine
     */
    public GordianHKDFEngine withIKM(final byte[] pIKM) {
        checkParams();
        theParams.withIKM(pIKM);
        return this;
    }

    /**
     * Add salt.
     * @param pSalt the salt
     * @return the engine
     */
    public GordianHKDFEngine withSalt(final byte[] pSalt) {
        checkParams();
        theParams.withIKM(pSalt);
        return this;
    }

    /**
     * Add info.
     * @param pInfo the info
     * @return the engine
     */
    public GordianHKDFEngine withInfo(final byte[] pInfo) {
        checkParams();
        theParams.withInfo(pInfo);
        return this;
    }

    /**
     * Check that we have valid parameters.
     */
    private void checkParams() {
         if (theParams == null) {
             throw new IllegalStateException("HKDF parameters not set");
         }
    }

    /**
     * Share parameters.
     * @param pPrimary the primary engine
     */
    void shareParameters(final GordianHKDFEngine pPrimary) {
        theParams = pPrimary.theParams;
    }

    /**
     * Derive bytes.
     * @return the derived bytes
     * @throws OceanusException on error
     */
    public byte[] deriveBytes() throws OceanusException {
        /* Determine the mode */
        checkParams();
        final GordianHKDFMode myMode = theParams.getMode();
        byte[] myOutput = null;

        /* If we should extract the information */
        if (myMode.doExtract()) {
            myOutput = extractKeyingMaterial(theParams.saltIterator(), theParams.ikmIterator());
        }

        /* If we should expand the information */
        if (myMode.doExpand()) {
            /* Determine PRK and expand it */
            final byte[] myPRK = myOutput == null ? theParams.getPRK() : myOutput;
            myOutput = expandKeyingMaterial(myPRK);
        }

        /* Return the result */
        return myOutput;
    }

    /**
     * Extract keying material.
     * @param saltIterator the iterator over the salts
     * @param ikmIterator the iterator over the initial keying material
     * @return the extracted material
     * @throws OceanusException on error
     */
    private byte[] extractKeyingMaterial(final Iterator<byte[]> saltIterator,
                                         final Iterator<byte[]> ikmIterator) throws OceanusException {
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
     * Expane the pseudo-random key.
     * @param pPRK the pseudo-random key
     * @return the expanded material
     * @throws OceanusException on error
     */
    private byte[] expandKeyingMaterial(final byte[] pPRK) throws OceanusException {
        /* Initialise the HMac */
        theHMac.initKeyBytes(pPRK);

        /* Allocate the output buffer */
        int myLenRemaining = theParams.getLength();
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
            }

            /* Update with the info */
            final Iterator<byte[]> myIterator = theParams.infoIterator();
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

        /* Return the expanded key */
        return myOutput;
    }
}
