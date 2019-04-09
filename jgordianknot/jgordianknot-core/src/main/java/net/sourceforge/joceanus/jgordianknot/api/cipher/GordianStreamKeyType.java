/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2019 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.api.cipher;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyLengths;

/**
 * Stream Key Type.
 */
public enum GordianStreamKeyType {
    /**
     * XSalsa20.
     */
    XSALSA20(GordianLength.LEN_192),

    /**
     * Salsa20.
     */
    SALSA20(GordianLength.LEN_64),

    /**
     * HC.
     */
    HC(GordianLength.LEN_128, GordianLength.LEN_256),

    /**
     * ChaCha.
     */
    CHACHA(GordianLength.LEN_64, GordianLength.LEN_96),

    /**
     * XChaCha.
     */
    XCHACHA20(GordianLength.LEN_192),

    /**
     * VMPC.
     */
    VMPC(GordianLength.LEN_128, GordianLength.LEN_256),

    /**
     * ISAAC.
     */
    ISAAC(null),

    /**
     * RC4.
     */
    RC4(null),

    /**
     * Grain.
     */
    GRAIN(GordianLength.LEN_96),

    /**
     * Sosemanuk.
     */
    SOSEMANUK(GordianLength.LEN_128),

    /**
     * Rabbit.
     */
    RABBIT(GordianLength.LEN_64),

    /**
     * Snow3G.
     */
    SNOW3G(GordianLength.LEN_128),

    /**
     * Zuc.
     */
    ZUC(GordianLength.LEN_128, GordianLength.LEN_184);

    /**
     * The IV Length.
     */
    private final GordianLength theShortIVLen;

    /**
     * The long IV Length.
     */
    private final GordianLength theLongIVLen;

    /**
     * The String name.
     */
    private String theName;

    /**
     * Constructor.
     * @param pIVLen the IV length
     */
    GordianStreamKeyType(final GordianLength pIVLen) {
        this(pIVLen, pIVLen);
    }

    /**
     * Constructor.
     * @param pShortIVLen the short IV length
     * @param pLongIVLen the short IV length
     */
    GordianStreamKeyType(final GordianLength pShortIVLen,
                         final GordianLength pLongIVLen) {
        theShortIVLen = pShortIVLen;
        theLongIVLen = pLongIVLen;
    }

    /**
     * Obtain the IV Length.
     * @param pKeyLen the keyLength
     * @return the IV length.
     */
    public int getIVLength(final GordianLength pKeyLen) {
        switch (this) {
            case ISAAC:
            case RC4:
                return 0;
            case VMPC:
                return pKeyLen.getByteLength();
            case HC:
            case ZUC:
            case CHACHA:
                return GordianLength.LEN_128 == pKeyLen
                       ? theShortIVLen.getByteLength()
                       : theLongIVLen.getByteLength();
            case XSALSA20:
            case SALSA20:
            case XCHACHA20:
            case GRAIN:
            case SOSEMANUK:
            case RABBIT:
            case SNOW3G:
            default:
                return theShortIVLen.getByteLength();
        }
    }

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* Load the name */
            theName = GordianCipherResource.getKeyForStream(this).getValue();
        }

        /* return the name */
        return theName;
    }

    /**
     * Is this KeyType valid for keyLength?
     * @param pKeyLen the keyLength
     * @return true/false
     */
    public boolean validForKeyLength(final GordianLength pKeyLen) {
        /* Reject unsupported keyLengths */
        if (!GordianKeyLengths.isSupportedLength(pKeyLen)) {
            return false;
        }

        /* Switch on keyType */
        switch (this) {
            case XSALSA20:
            case XCHACHA20:
                return GordianLength.LEN_256 == pKeyLen;
            case GRAIN:
            case RABBIT:
            case SNOW3G:
                return GordianLength.LEN_128 == pKeyLen;
            case HC:
            case CHACHA:
            case SALSA20:
            case SOSEMANUK:
            case ZUC:
                return GordianLength.LEN_128 == pKeyLen
                        || GordianLength.LEN_256 == pKeyLen;
            default:
                return true;
        }
    }

    /**
     * Is this KeyType valid for largeData?
     * @return true/false
     */
    public boolean supportsLargeData() {
        switch (this) {
            case SNOW3G:
            case ZUC:
                return false;
            default:
                return true;
        }
    }
}

