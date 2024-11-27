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
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianLogicException;
import net.sourceforge.joceanus.oceanus.OceanusException;

import java.util.ArrayList;
import java.util.List;

/**
 * MultiHKDF.
 */
public class GordianHKDFMulti {
    /**
     * The primary HKDF Engine.
     */
    private final GordianHKDFEngine thePrimary;

    /**
     * The list of HKDF Engines.
     */
    private final List<GordianHKDFEngine> theEngines;

    /**
     * Constructor.
     * @param pFactory the security factory
     * @param pPrimary the primary digestSpecs
     * @param pSecondaries the secondary digestSpecs
     * @throws OceanusException on error
     */
    public GordianHKDFMulti(final GordianFactory pFactory,
                            final GordianDigestSpec pPrimary,
                            final GordianDigestSpec... pSecondaries) throws OceanusException {
        /* Create the list */
        theEngines  = new ArrayList<>();

        /* Must be at least two engines */
        if (pSecondaries.length < 1) {
            throw new GordianLogicException("Must be at least two engines");
        }

        /* Allocate primary engine */
        thePrimary = new GordianHKDFEngine(pFactory, pPrimary);

        /* Allocate the secondary engines */
        for (final GordianDigestSpec mySpec : pSecondaries) {
            theEngines.add(new GordianHKDFEngine(pFactory, mySpec));
        }
    }

    /**
     * Set mode to extractOnly.
     * @return the engine
     */
    public GordianHKDFMulti extractOnly() {
        /* Configure the primary */
        thePrimary.extractOnly();

        /* Share parameters with secondaries */
        shareParameters();

        /* return the engine */
        return this;
    }

    /**
     * Set mode to expandOnly.
     * @param pPRK the pseudo-random key
     * @param pLength the length
     * @return the engine
     */
    public GordianHKDFMulti expandOnly(final byte[] pPRK,
                                       final int pLength) {
        /* Configure the primary */
        thePrimary.expandOnly(pPRK, pLength);

        /* Share parameters with secondaries */
        shareParameters();

        /* return the engine */
        return this;
    }

    /**
     * Set mode to extractThenExpand.
     * @param pLength the length
     * @return the engine
     */
    public GordianHKDFMulti extractThenExpand(final int pLength) {
        /* Configure the primary */
        thePrimary.extractThenExpand(pLength);

        /* Share parameters with secondaries */
        shareParameters();

        /* return the engine */
        return this;
    }

    /**
     * Share primary parameters with secondaries.
     */
    private void shareParameters() {
        /* Loop through the secondaries */
        for (final GordianHKDFEngine myEngine : theEngines) {
            myEngine.shareParameters(thePrimary);
        }
    }

    /**
     * Add iKM.
     * @param pIKM the initial keying material
     * @return the engine
     */
    public GordianHKDFMulti withIKM(final byte[] pIKM) {
        thePrimary.withIKM(pIKM);
        return this;
    }

    /**
     * Add salt.
     * @param pSalt the salt
     * @return the engine
     */
    public GordianHKDFMulti withSalt(final byte[] pSalt) {
        thePrimary.withSalt(pSalt);
        return this;
    }

    /**
     * Add info.
     * @param pInfo the info
     * @return the engine
     */
    public GordianHKDFMulti withInfo(final byte[] pInfo) {
        thePrimary.withInfo(pInfo);
        return this;
    }

    /**
     * Derive bytes.
     * @return the derived bytes
     * @throws OceanusException on error
     */
    public byte[] deriveBytes() throws OceanusException {
        /* Create the primary output */
        final byte[] myOutput = thePrimary.deriveBytes();

        /* Loop through the secondaries */
        for (final GordianHKDFEngine myEngine : theEngines) {
            final byte[] mySecondary = myEngine.deriveBytes();
            for (int i = 0; i < myOutput.length; i++) {
                myOutput[i] ^= mySecondary[i];
            }
        }

        /* Return the bytes */
        return myOutput;
    }
}
