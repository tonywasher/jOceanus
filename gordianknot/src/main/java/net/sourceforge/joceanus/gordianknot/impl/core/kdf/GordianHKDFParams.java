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
     * The salts.
     */
    private final List<byte[]> theSalts;

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
     * The expand length.
     */
    private final int theLength;

    /**
     * Constructor.
     * @param pMode theMode
     * @param pPRK the pseudo-random key
     * @param pLength the length
     */
    private GordianHKDFParams(final GordianHKDFMode pMode,
                              final byte[] pPRK,
                              final int pLength) {
        /* Store parameters */
        theMode = pMode;
        thePRK = pPRK == null ? null : pPRK.clone();
        theLength = pLength;
        theIKMs = new ArrayList<>();
        theSalts = new ArrayList<>();
        theInfo = new ArrayList<>();

        /* Check salt */
        //if (pMode.doExtract()
        //        && (theSalt == null || pSalt.length == 0)) {
        //    throw new IllegalArgumentException("Salt must be non-null and non-zero length for extract");
        //}

        /* Check prk */
        if (GordianHKDFMode.EXPAND.equals(theMode)
                && (thePRK == null || pPRK.length == 0)) {
            throw new IllegalArgumentException("PRK must be non-null and non-zero length for expandOnly");
        }
    }

    /**
     * Create an extractOnly parameters.
     * @return an extractOnly parameters
     */
    static GordianHKDFParams extractOnly() {
        return new GordianHKDFParams(GordianHKDFMode.EXTRACT, null, 0);
    }

    /**
     * Create an expandOnly parameters.
     * @param pPRK the pseudo-random key
     * @param pLength the length
     * @return an expandOnly parameters
     */
    static GordianHKDFParams expandOnly(final byte[] pPRK,
                                        final int pLength) {
        return new GordianHKDFParams(GordianHKDFMode.EXPAND, pPRK, pLength);
    }

    /**
     * Create an extractThenExpand parameters.
     * @param pLength the length
     * @return an extractThenExpand parameters
     */
    static GordianHKDFParams extractThenExpand(final int pLength) {
        return new GordianHKDFParams(GordianHKDFMode.EXTRACTTHENEXPAND, null, pLength);
    }

    /**
     * Obtain the mode.
     * @return the mode
     */
    GordianHKDFMode getMode() {
        return theMode;
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
    GordianHKDFParams withIKM(final byte[] pIKM) {
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
     * Add salt.
     * @param pSalt the salt
     * @return the parameters
     */
    GordianHKDFParams withSalt(final byte[] pSalt) {
        if (pSalt != null && pSalt.length > 0) {
            theSalts.add(pSalt.clone());
        }
        return this;
    }

    /**
     * Obtain the saltIterator.
     * @return the iterator.
     */
    Iterator<byte[]> saltIterator() {
        return theSalts.iterator();
    }

    /**
     * Add info.
     * @param pInfo the info
     * @return the parameters
     */
    GordianHKDFParams withInfo(final byte[] pInfo) {
        if (pInfo != null && pInfo.length > 0) {
            theInfo.add(pInfo.clone());
        }
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
