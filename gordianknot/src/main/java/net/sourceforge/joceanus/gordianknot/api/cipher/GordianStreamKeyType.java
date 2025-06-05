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
package net.sourceforge.joceanus.gordianknot.api.cipher;

import net.sourceforge.joceanus.gordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.gordianknot.api.key.GordianKeyLengths;

/**
 * Stream Key Type.
 */
public enum GordianStreamKeyType {
    /**
     * Salsa20.
     */
    SALSA20,

    /**
     * HC.
     */
    HC,

    /**
     * ChaCha20.
     */
    CHACHA20,

    /**
     * VMPC.
     */
    VMPC,

    /**
     * ISAAC.
     */
    ISAAC,

    /**
     * RC4.
     */
    RC4,

    /**
     * Grain.
     */
    GRAIN,

    /**
     * Sosemanuk.
     */
    SOSEMANUK,

    /**
     * Rabbit.
     */
    RABBIT,

    /**
     * Snow3G.
     */
    SNOW3G,

    /**
     * Zuc.
     */
    ZUC,

    /**
     * SkeinXof.
     */
    SKEINXOF,

    /**
     * Blake2Xof.
     */
    BLAKE2XOF,

    /**
     * Blake3Xof.
     */
    BLAKE3XOF,

    /**
     * Ascon.
     */
    ASCON,

    /**
     * Elephant.
     */
    ELEPHANT,

    /**
     * ISAP.
     */
    ISAP,

    /**
     * PhotonBeetle.
     */
    PHOTONBEETLE,

    /**
     * Romulus.
     */
    ROMULUS,

    /**
     * Sparkle.
     */
    SPARKLE,

    /**
     * Xoodyak.
     */
    XOODYAK;

    /**
     * The String name.
     */
    private String theName;

    /**
     * Does the keyType need a subKeyType?
     * @return true/false.
     */
    public boolean needsSubKeyType() {
        switch (this) {
            case CHACHA20:
            case SALSA20:
            case VMPC:
            case SKEINXOF:
            case BLAKE2XOF:
            case ELEPHANT:
            case ISAP:
            case ROMULUS:
            case SPARKLE:
                return true;
            default:
                return false;
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
            case GRAIN:
            case RABBIT:
            case SNOW3G:
            case ASCON:
            case ELEPHANT:
            case ISAP:
            case PHOTONBEETLE:
            case ROMULUS:
            case XOODYAK:
                 return GordianLength.LEN_128 == pKeyLen;
            case HC:
            case CHACHA20:
            case SALSA20:
            case SOSEMANUK:
            case ZUC:
                return GordianLength.LEN_128 == pKeyLen
                        || GordianLength.LEN_256 == pKeyLen;
            case BLAKE2XOF:
                return GordianLength.LEN_1024 != pKeyLen;
            case BLAKE3XOF:
                return GordianLength.LEN_256 == pKeyLen;
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

    /**
     * Does the keyType need reInit after finish?
     * @return true/false.
     */
    public boolean needsReInit() {
        switch (this) {
            case ASCON:
            case ELEPHANT:
            case ISAP:
            case PHOTONBEETLE:
            case SPARKLE:
            case XOODYAK:
                return true;
            default:
                return false;
        }
    }
}
