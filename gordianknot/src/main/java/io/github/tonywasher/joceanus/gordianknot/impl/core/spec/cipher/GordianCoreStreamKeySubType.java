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
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamKeySubType;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamKeySubType.GordianBlakeXofKey;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamKeySubType.GordianChaCha20Key;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamKeySubType.GordianElephantKey;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamKeySubType.GordianISAPKey;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamKeySubType.GordianRomulusKey;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamKeySubType.GordianSalsa20Key;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamKeySubType.GordianSkeinXofKey;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamKeySubType.GordianSparkleKey;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamKeySubType.GordianVMPCKey;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamKeyType;
import org.bouncycastle.crypto.engines.ElephantEngine.ElephantParameters;
import org.bouncycastle.crypto.engines.ISAPEngine.IsapType;
import org.bouncycastle.crypto.engines.RomulusEngine.RomulusParameters;
import org.bouncycastle.crypto.engines.SparkleEngine.SparkleParameters;

/**
 * Core StreamKey subType.
 */
public final class GordianCoreStreamKeySubType {
    /**
     * Private constructor.
     */
    private GordianCoreStreamKeySubType() {
    }

    /**
     * Default subKeyType.
     *
     * @param pKeyType the keyType
     * @return the default
     */
    static GordianStreamKeySubType defaultSubKeyType(final GordianStreamKeyType pKeyType) {
        /* Switch on keyType */
        switch (pKeyType) {
            case SALSA20:
                return GordianSalsa20Key.STD;
            case CHACHA20:
                return GordianChaCha20Key.STD;
            case VMPC:
                return GordianVMPCKey.STD;
            case SKEINXOF:
                return GordianSkeinXofKey.STATE1024;
            case BLAKE2XOF:
                return GordianBlakeXofKey.BLAKE2XB;
            case ELEPHANT:
                return GordianElephantKey.ELEPHANT160;
            case ISAP:
                return GordianISAPKey.ISAPA128;
            case ROMULUS:
                return GordianRomulusKey.ROMULUS_M;
            case SPARKLE:
                return GordianSparkleKey.SPARKLE128_128;
            default:
                return null;
        }
    }

    /**
     * Obtain the name.
     *
     * @param pKeyType the keyType
     * @param pSubType the subType
     * @return the name
     */
    static String toSubTypeString(final GordianStreamKeyType pKeyType,
                                  final GordianStreamKeySubType pSubType) {
        switch (pKeyType) {
            case BLAKE2XOF:
                return toBlake2String(pSubType);
            case ELEPHANT:
                return toElephantString(pSubType);
            case ISAP:
                return toISAPString(pSubType);
            case ROMULUS:
                return toRomulusString(pSubType);
            case SPARKLE:
                return toSparkleString(pSubType);
            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * Obtain required salsaIVLength.
     *
     * @param pType the type
     * @return the ivLength
     */
    static GordianLength requiredSalsaIVLength(final GordianSalsa20Key pType) {
        switch (pType) {
            case STD:
                return GordianLength.LEN_64;
            case XSALSA:
            default:
                return GordianLength.LEN_192;
        }
    }

    /**
     * Obtain required chachaIVLength.
     *
     * @param pType the type
     * @return the ivLength
     */
    static GordianLength requiredChaChaIVLength(final GordianChaCha20Key pType) {
        switch (pType) {
            case STD:
                return GordianLength.LEN_64;
            case ISO7539:
                return GordianLength.LEN_96;
            case XCHACHA:
            default:
                return GordianLength.LEN_192;
        }
    }

    /**
     * Obtain length for SkeinXof subKeyType.
     *
     * @param pKey the key
     * @return the length
     */
    public static GordianLength getLengthForSkeinXofKey(final GordianSkeinXofKey pKey) {
        switch (pKey) {
            case STATE256:
                return GordianLength.LEN_256;
            case STATE512:
                return GordianLength.LEN_512;
            case STATE1024:
                return GordianLength.LEN_1024;
            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * Obtain SkeinXof subKeyType for stateLength.
     *
     * @param pLength the length
     * @return the subKeyType
     */
    public static GordianSkeinXofKey getSkeinXofKeyForLength(final GordianLength pLength) {
        switch (pLength) {
            case LEN_256:
                return GordianSkeinXofKey.STATE256;
            case LEN_512:
                return GordianSkeinXofKey.STATE512;
            case LEN_1024:
                return GordianSkeinXofKey.STATE1024;
            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * Obtain the Blake2 name.
     *
     * @param pType the type
     * @return the name
     */
    private static String toBlake2String(final GordianStreamKeySubType pType) {
        return pType == GordianBlakeXofKey.BLAKE2XB ? "Blake2Xb" : "Blake2Xs";
    }

    /**
     * Obtain required blakeIVLength.
     *
     * @param pType the type
     * @return the ivLength
     */
    static GordianLength requiredBlakeIVLength(final GordianBlakeXofKey pType) {
        switch (pType) {
            case BLAKE2XS:
                return GordianLength.LEN_64;
            case BLAKE2XB:
            default:
                return GordianLength.LEN_128;
        }
    }

    /**
     * Obtain the Elephant name.
     *
     * @param pType the type
     * @return the name
     */
    private static String toElephantString(final GordianStreamKeySubType pType) {
        final String myBase = GordianStreamKeyType.ELEPHANT.toString();
        switch ((GordianElephantKey) pType) {
            case ELEPHANT160:
                return myBase + "160";
            case ELEPHANT176:
                return myBase + "176";
            case ELEPHANT200:
            default:
                return myBase + "200";
        }
    }

    /**
     * Obtain the elephant parameters.
     *
     * @param pType the type
     * @return the parameters
     */
    public static ElephantParameters getParameters(final GordianElephantKey pType) {
        switch (pType) {
            case ELEPHANT160:
                return ElephantParameters.elephant160;
            case ELEPHANT176:
                return ElephantParameters.elephant176;
            case ELEPHANT200:
            default:
                return ElephantParameters.elephant200;
        }
    }

    /**
     * Obtain the ISAP name.
     *
     * @param pType the type
     * @return the name
     */
    private static String toISAPString(final GordianStreamKeySubType pType) {
        final String myBase = GordianStreamKeyType.ISAP.toString();
        switch ((GordianISAPKey) pType) {
            case ISAPA128:
                return myBase + "A128";
            case ISAPA128A:
                return myBase + "A128A";
            case ISAPK128:
                return myBase + "K128";
            case ISAPK128A:
            default:
                return myBase + "K128A";
        }
    }

    /**
     * Obtain the ISAP type.
     *
     * @param pType the type
     * @return the type
     */
    public static IsapType getISAPType(final GordianISAPKey pType) {
        switch (pType) {
            case ISAPA128:
                return IsapType.ISAP_A_128;
            case ISAPA128A:
                return IsapType.ISAP_A_128A;
            case ISAPK128:
                return IsapType.ISAP_K_128;
            case ISAPK128A:
            default:
                return IsapType.ISAP_K_128A;
        }
    }

    /**
     * Obtain the romulus name.
     *
     * @param pType the type
     * @return the name
     */
    private static String toRomulusString(final GordianStreamKeySubType pType) {
        final String myBase = GordianStreamKeyType.ROMULUS.toString();
        switch ((GordianRomulusKey) pType) {
            case ROMULUS_M:
                return myBase + "-M";
            case ROMULUS_N:
                return myBase + "-N";
            case ROMULUS_T:
            default:
                return myBase + "-T";
        }
    }

    /**
     * Obtain the RomulusParameters.
     *
     * @param pType the type
     * @return the parameters
     */
    public static RomulusParameters getParameters(final GordianRomulusKey pType) {
        switch (pType) {
            case ROMULUS_M:
                return RomulusParameters.RomulusM;
            case ROMULUS_N:
                return RomulusParameters.RomulusN;
            case ROMULUS_T:
            default:
                return RomulusParameters.RomulusT;
        }
    }

    /**
     * Obtain the sparkle name.
     *
     * @param pType the type
     * @return the name
     */
    private static String toSparkleString(final GordianStreamKeySubType pType) {
        final String myBase = GordianStreamKeyType.SPARKLE.toString();
        switch ((GordianSparkleKey) pType) {
            case SPARKLE128_128:
                return myBase + "128_128";
            case SPARKLE256_128:
                return myBase + "256_128";
            case SPARKLE192_192:
                return myBase + "192_192";
            case SPARKLE256_256:
            default:
                return myBase + "256_256";
        }
    }

    /**
     * Obtain required sparkleKeyLength.
     *
     * @param pType the type
     * @return the keyLength
     */
    static GordianLength requiredSparkleKeyLength(final GordianSparkleKey pType) {
        switch (pType) {
            case SPARKLE128_128:
            case SPARKLE256_128:
                return GordianLength.LEN_128;
            case SPARKLE192_192:
                return GordianLength.LEN_192;
            case SPARKLE256_256:
            default:
                return GordianLength.LEN_256;
        }
    }

    /**
     * Obtain required sparkleIVLength.
     *
     * @param pType the type
     * @return the ivLength
     */
    static GordianLength requiredSparkleIVLength(final GordianSparkleKey pType) {
        switch (pType) {
            case SPARKLE128_128:
                return GordianLength.LEN_128;
            case SPARKLE192_192:
                return GordianLength.LEN_192;
            case SPARKLE256_128:
            case SPARKLE256_256:
            default:
                return GordianLength.LEN_256;
        }
    }

    /**
     * Obtain the Sparkle parameters.
     *
     * @param pType the type
     * @return the parameters
     */
    public static SparkleParameters getParameters(final GordianSparkleKey pType) {
        switch (pType) {
            case SPARKLE128_128:
                return SparkleParameters.SCHWAEMM128_128;
            case SPARKLE256_128:
                return SparkleParameters.SCHWAEMM256_128;
            case SPARKLE192_192:
                return SparkleParameters.SCHWAEMM192_192;
            case SPARKLE256_256:
            default:
                return SparkleParameters.SCHWAEMM256_256;
        }
    }
}
