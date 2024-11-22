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
        /* Create the hMac */
        final GordianMacFactory myFactory = pFactory.getMacFactory();
        final GordianMacSpec myMacSpec = GordianMacSpecBuilder.hMac(pDigestSpec);
        theHMac = myFactory.createMac(myMacSpec);
    }

    /**
     * Set mode to extractOnly.
     * @param pSalt the salt
     * @return the engine
     */
    public GordianHKDFEngine extractOnly(final byte[] pSalt) {
        theParams = GordianHKDFParams.extractOnly(pSalt);
        return this;
    }

    /**
     * Set mode to extractOnly.
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
     * @param pSalt the salt
     * @param pLength the length
     * @return the engine
     */
    public GordianHKDFEngine extractThenExpand(final byte[] pSalt,
                                               final int pLength) {
        theParams = GordianHKDFParams.extractThenExpand(pSalt, pLength);
        return this;
    }

    /**
     * Add iKM.
     * @param pIKM the initial keying material
     * @return the engine
     */
    public GordianHKDFEngine addIKM(final byte[] pIKM) {
        checkParams();
        theParams.addIKM(pIKM);
        return this;
    }

    /**
     * Add info.
     * @param pInfo the info
     * @return the engine
     */
    public GordianHKDFEngine addInfo(final byte[] pInfo) {
        checkParams();
        theParams.addInfo(pInfo);
        return this;
    }

    /**
     * Clear the info list.
     * @return the engine
     */
    public GordianHKDFEngine clearInfo() {
        checkParams();
        theParams.clearInfo();
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
            myOutput = extractKeyingMaterial(theParams.ikmIterator());
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
     * @param ikmIterator the iterator over the initial keying material
     * @return the extracted material
     * @throws OceanusException on error
     */
    private byte[] extractKeyingMaterial(final Iterator<byte[]> ikmIterator) throws OceanusException {
        /* Extract the keying material */
        theHMac.initKeyBytes(theParams.getSalt());
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
