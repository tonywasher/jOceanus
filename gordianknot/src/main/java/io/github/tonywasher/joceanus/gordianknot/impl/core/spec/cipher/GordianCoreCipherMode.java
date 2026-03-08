/*
 * GordianKnot: Security Suite
 * Copyright 2026. Tony Washer
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
 */

package io.github.tonywasher.joceanus.gordianknot.impl.core.spec.cipher;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianCipherMode;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianSymKeySpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianSymKeyType;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

/**
 * Cipher Modes. Available algorithms.
 */
public final class GordianCoreCipherMode {
    /**
     * The modeMap.
     */
    private static final Map<GordianCipherMode, GordianCoreCipherMode> MODEMAP = newModeMap();

    /**
     * The CipherMode.
     */
    private final GordianCipherMode theMode;

    /**
     * Constructor.
     *
     * @param pMode the mode
     */
    private GordianCoreCipherMode(final GordianCipherMode pMode) {
        theMode = pMode;
    }

    /**
     * Obtain the mode.
     *
     * @return the mode
     */
    public GordianCipherMode getMode() {
        return theMode;
    }

    /**
     * Does the mode require padding?
     *
     * @return true/false
     */
    public boolean hasPadding() {
        switch (theMode) {
            case ECB:
            case CBC:
            case G3413CBC:
                return true;
            default:
                return false;
        }
    }

    /**
     * Is this an AAD mode?
     *
     * @return true/false
     */
    public boolean isAAD() {
        switch (theMode) {
            case CCM:
            case GCM:
            case EAX:
            case OCB:
            case KCCM:
            case KGCM:
            case GCMSIV:
                return true;
            default:
                return false;
        }
    }

    /**
     * Can we work on a short block?
     *
     * @return true/false
     */
    public boolean allowShortBlock() {
        switch (theMode) {
            case CCM:
            case GCM:
            case OCB:
            case SIC:
                return false;
            default:
                return true;
        }
    }

    /**
     * Does the mode require a standard block?
     *
     * @return true/false
     */
    public boolean needsStdBlock() {
        switch (theMode) {
            case CCM:
            case GCM:
            case GCMSIV:
            case OCB:
                return true;
            default:
                return false;
        }
    }

    /**
     * Is this mode valid for the symKeySpec?
     *
     * @param pKeySpec the keySpec
     * @return true/false
     */
    public boolean validForSymKey(final GordianSymKeySpec pKeySpec) {
        final GordianSymKeyType myKeyType = pKeySpec.getSymKeyType();
        final GordianLength myKeyLen = pKeySpec.getKeyLength();
        switch (theMode) {
            case G3413OFB:
            case G3413CFB:
            case G3413CBC:
            case G3413CTR:
                return GordianSymKeyType.KUZNYECHIK.equals(myKeyType);
            case GOFB:
            case GCFB:
                return GordianSymKeyType.GOST.equals(myKeyType);
            case KCTR:
            case KGCM:
            case KCCM:
                return GordianSymKeyType.KALYNA.equals(myKeyType);
            case GCMSIV:
                return GordianLength.LEN_128.equals(myKeyLen)
                        || GordianLength.LEN_256.equals(myKeyLen);
            default:
                return true;
        }
    }

    /**
     * Does the mode need an IV?
     *
     * @return true/false
     */
    public boolean needsIV() {
        return theMode != GordianCipherMode.ECB;
    }

    /**
     * Needs re-initialisation after final.
     *
     * @return true/false
     */
    public boolean needsReInitialisation() {
        return theMode == GordianCipherMode.GCM;
    }

    @Override
    public String toString() {
        return theMode.toString();
    }

    @Override
    public boolean equals(final Object pThat) {
        /* Handle the trivial cases */
        if (this == pThat) {
            return true;
        }
        if (pThat == null) {
            return false;
        }

        /* Check subFields */
        return pThat instanceof GordianCoreCipherMode myThat
                && theMode == myThat.getMode();
    }

    @Override
    public int hashCode() {
        return theMode.hashCode();
    }

    /**
     * Build the mode map.
     *
     * @return the mode map
     */
    private static Map<GordianCipherMode, GordianCoreCipherMode> newModeMap() {
        final Map<GordianCipherMode, GordianCoreCipherMode> myMap = new EnumMap<>(GordianCipherMode.class);
        for (GordianCipherMode myMode : GordianCipherMode.values()) {
            myMap.put(myMode, new GordianCoreCipherMode(myMode));
        }
        return myMap;
    }

    /**
     * Obtain the core mode.
     *
     * @param pMode the base mode
     * @return the core mode
     */
    public static GordianCoreCipherMode mapCoreMode(final GordianCipherMode pMode) {
        return MODEMAP.get(pMode);
    }

    /**
     * Obtain the values.
     *
     * @return the values
     */
    public static Collection<GordianCoreCipherMode> values() {
        return MODEMAP.values();
    }
}
