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
package net.sourceforge.joceanus.jgordianknot.impl.core.kdf;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * HKDF parameters.
 */
public final class GordianHKDFParams {
    /**
     * The initial keying material.
     */
    private final List<byte[]> theIKMs;

    /**
     * The pseudo-random key.
     */
    private final byte[] thePRK;

    /**
     * The expanding info.
     */
    private final List<byte[]> theInfo;

    /**
     * The mode.
     */
    private final GordianHKDFMode theMode;

    /**
     * The salt.
     */
    private final byte[] theSalt;

    /**
     * The expand length.
     */
    private final int theLength;

    /**
     * Constructor.
     * @param pMode theMode
     * @param pSalt the salt
     * @param pPRK the pseudo-random key
     * @param pLength the length
     */
    private GordianHKDFParams(final GordianHKDFMode pMode,
                              final byte[] pSalt,
                              final byte[] pPRK,
                              final int pLength) {
        /* Store parameters */
        theMode = pMode;
        theSalt = pSalt == null ? null : pSalt.clone();
        thePRK = pPRK == null ? null : pPRK.clone();
        theLength = pLength;
        theIKMs = new ArrayList<>();
        theInfo = new ArrayList<>();

        /* Check salt */
        if (pMode.doExtract()
                && (theSalt == null || pSalt.length == 0)) {
            throw new IllegalArgumentException("Salt must be non-null and non-zero length for extract");
        }

        /* Check prk */
        if (GordianHKDFMode.EXPAND.equals(theMode)
                && (thePRK == null || pPRK.length == 0)) {
            throw new IllegalArgumentException("PRK must be non-null and non-zero length for expandOnly");
        }
    }

    /**
     * Create an extractOnly parameters.
     * @param pSalt the salt
     * @return an extractOnly parameters
     */
    static GordianHKDFParams extractOnly(final byte[] pSalt) {
        return new GordianHKDFParams(GordianHKDFMode.EXTRACT, pSalt, null, 0);
    }

    /**
     * Create an expandOnly parameters.
     * @param pPRK the pseudo-random key
     * @param pLength the length
     * @return an expandOnly parameters
     */
    static GordianHKDFParams expandOnly(final byte[] pPRK,
                                        final int pLength) {
        return new GordianHKDFParams(GordianHKDFMode.EXPAND, null, pPRK, pLength);
    }

    /**
     * Create an extractThenExpand parameters.
     * @param pSalt the salt
     * @param pLength the length
     * @return an extractThenExpand parameters
     */
    static GordianHKDFParams extractThenExpand(final byte[] pSalt,
                                               final int pLength) {
        return new GordianHKDFParams(GordianHKDFMode.EXTRACTTHENEXPAND, pSalt, null, pLength);
    }

    /**
     * Obtain the mode.
     * @return the mode
     */
    GordianHKDFMode getMode() {
        return theMode;
    }

    /**
     * Obtain the salt.
     * @return the salt
     */
    byte[] getSalt() {
        return theSalt;
    }

    /**
     * Obtain the PRK.
     * @return the pseudo-random key
     */
    byte[] getPRK() {
        return thePRK;
    }

    /**
     * Add initial keying material.
     * @param pIKM the initial keying material
     * @return the parameters
     */
    GordianHKDFParams addIKM(final byte[] pIKM) {
        if (pIKM != null && pIKM.length > 0) {
            theIKMs.add(pIKM.clone());
        }
        return this;
    }

    /**
     * Obtain the ikmIterator.
     * @return the iterator.
     */
    Iterator<byte[]> ikmIterator() {
        return theIKMs.iterator();
    }

    /**
     * Add info.
     * @param pInfo the info
     * @return the parameters
     */
    GordianHKDFParams addInfo(final byte[] pInfo) {
        if (pInfo != null && pInfo.length > 0) {
            theInfo.add(pInfo.clone());
        }
        return this;
    }

    /**
     * Clear the info list.
     * @return the parameters
     */
    GordianHKDFParams clearInfo() {
        theInfo.clear();
        return this;
    }

    /**
     * Obtain the infoIterator.
     * @return the iterator.
     */
    Iterator<byte[]> infoIterator() {
        return theInfo.iterator();
    }

    /**
     * Obtain the length.
     * @return the length
     */
    int getLength() {
        if (theLength <= 0) {
            throw new IllegalArgumentException("Length must be greater than zero");
        }
        return theLength;
    }
}
