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
        return switch (pKeyType) {
            case SALSA20 -> GordianSalsa20Key.STD;
            case CHACHA20 -> GordianChaCha20Key.STD;
            case VMPC -> GordianVMPCKey.STD;
            case SKEINXOF -> GordianSkeinXofKey.STATE1024;
            case BLAKE2XOF -> GordianBlakeXofKey.BLAKE2XB;
            case ELEPHANT -> GordianElephantKey.ELEPHANT160;
            case ISAP -> GordianISAPKey.ISAPA128;
            case ROMULUS -> GordianRomulusKey.ROMULUS_M;
            case SPARKLE -> GordianSparkleKey.SPARKLE128_128;
            default -> null;
        };
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
        return switch (pKeyType) {
            case BLAKE2XOF -> toBlake2String(pSubType);
            case ELEPHANT -> toElephantString(pSubType);
            case ISAP -> toISAPString(pSubType);
            case ROMULUS -> toRomulusString(pSubType);
            case SPARKLE -> toSparkleString(pSubType);
            default -> throw new IllegalArgumentException();
        };
    }

    /**
     * Obtain required salsaIVLength.
     *
     * @param pType the type
     * @return the ivLength
     */
    static GordianLength requiredSalsaIVLength(final GordianSalsa20Key pType) {
        return switch (pType) {
            case STD -> GordianLength.LEN_64;
            default -> GordianLength.LEN_192;
        };
    }

    /**
     * Obtain required chachaIVLength.
     *
     * @param pType the type
     * @return the ivLength
     */
    static GordianLength requiredChaChaIVLength(final GordianChaCha20Key pType) {
        return switch (pType) {
            case STD -> GordianLength.LEN_64;
            case ISO7539 -> GordianLength.LEN_96;
            default -> GordianLength.LEN_192;
        };
    }

    /**
     * Obtain length for SkeinXof subKeyType.
     *
     * @param pKey the key
     * @return the length
     */
    public static GordianLength getLengthForSkeinXofKey(final GordianSkeinXofKey pKey) {
        return switch (pKey) {
            case STATE256 -> GordianLength.LEN_256;
            case STATE512 -> GordianLength.LEN_512;
            case STATE1024 -> GordianLength.LEN_1024;
            default -> throw new IllegalArgumentException();
        };
    }

    /**
     * Obtain SkeinXof subKeyType for stateLength.
     *
     * @param pLength the length
     * @return the subKeyType
     */
    public static GordianSkeinXofKey getSkeinXofKeyForLength(final GordianLength pLength) {
        return switch (pLength) {
            case LEN_256 -> GordianSkeinXofKey.STATE256;
            case LEN_512 -> GordianSkeinXofKey.STATE512;
            case LEN_1024 -> GordianSkeinXofKey.STATE1024;
            default -> throw new IllegalArgumentException();
        };
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
        return switch (pType) {
            case BLAKE2XS -> GordianLength.LEN_64;
            default -> GordianLength.LEN_128;
        };
    }

    /**
     * Obtain the Elephant name.
     *
     * @param pType the type
     * @return the name
     */
    private static String toElephantString(final GordianStreamKeySubType pType) {
        final String myBase = GordianStreamKeyType.ELEPHANT.toString();
        return switch ((GordianElephantKey) pType) {
            case ELEPHANT160 -> myBase + "160";
            case ELEPHANT176 -> myBase + "176";
            default -> myBase + "200";
        };
    }

    /**
     * Obtain the elephant parameters.
     *
     * @param pType the type
     * @return the parameters
     */
    public static ElephantParameters getParameters(final GordianElephantKey pType) {
        return switch (pType) {
            case ELEPHANT160 -> ElephantParameters.elephant160;
            case ELEPHANT176 -> ElephantParameters.elephant176;
            default -> ElephantParameters.elephant200;
        };
    }

    /**
     * Obtain the ISAP name.
     *
     * @param pType the type
     * @return the name
     */
    private static String toISAPString(final GordianStreamKeySubType pType) {
        final String myBase = GordianStreamKeyType.ISAP.toString();
        return switch ((GordianISAPKey) pType) {
            case ISAPA128 -> myBase + "A128";
            case ISAPA128A -> myBase + "A128A";
            case ISAPK128 -> myBase + "K128";
            default -> myBase + "K128A";
        };
    }

    /**
     * Obtain the ISAP type.
     *
     * @param pType the type
     * @return the type
     */
    public static IsapType getISAPType(final GordianISAPKey pType) {
        return switch (pType) {
            case ISAPA128 -> IsapType.ISAP_A_128;
            case ISAPA128A -> IsapType.ISAP_A_128A;
            case ISAPK128 -> IsapType.ISAP_K_128;
            default -> IsapType.ISAP_K_128A;
        };
    }

    /**
     * Obtain the romulus name.
     *
     * @param pType the type
     * @return the name
     */
    private static String toRomulusString(final GordianStreamKeySubType pType) {
        final String myBase = GordianStreamKeyType.ROMULUS.toString();
        return switch ((GordianRomulusKey) pType) {
            case ROMULUS_M -> myBase + "-M";
            case ROMULUS_N -> myBase + "-N";
            default -> myBase + "-T";
        };
    }

    /**
     * Obtain the RomulusParameters.
     *
     * @param pType the type
     * @return the parameters
     */
    public static RomulusParameters getParameters(final GordianRomulusKey pType) {
        return switch (pType) {
            case ROMULUS_M -> RomulusParameters.RomulusM;
            case ROMULUS_N -> RomulusParameters.RomulusN;
            default -> RomulusParameters.RomulusT;
        };
    }

    /**
     * Obtain the sparkle name.
     *
     * @param pType the type
     * @return the name
     */
    private static String toSparkleString(final GordianStreamKeySubType pType) {
        final String myBase = GordianStreamKeyType.SPARKLE.toString();
        return switch ((GordianSparkleKey) pType) {
            case SPARKLE128_128 -> myBase + "128_128";
            case SPARKLE256_128 -> myBase + "256_128";
            case SPARKLE192_192 -> myBase + "192_192";
            default -> myBase + "256_256";
        };
    }

    /**
     * Obtain required sparkleKeyLength.
     *
     * @param pType the type
     * @return the keyLength
     */
    static GordianLength requiredSparkleKeyLength(final GordianSparkleKey pType) {
        return switch (pType) {
            case SPARKLE128_128, SPARKLE256_128 -> GordianLength.LEN_128;
            case SPARKLE192_192 -> GordianLength.LEN_192;
            default -> GordianLength.LEN_256;
        };
    }

    /**
     * Obtain required sparkleIVLength.
     *
     * @param pType the type
     * @return the ivLength
     */
    static GordianLength requiredSparkleIVLength(final GordianSparkleKey pType) {
        return switch (pType) {
            case SPARKLE128_128 -> GordianLength.LEN_128;
            case SPARKLE192_192 -> GordianLength.LEN_192;
            default -> GordianLength.LEN_256;
        };
    }

    /**
     * Obtain the Sparkle parameters.
     *
     * @param pType the type
     * @return the parameters
     */
    public static SparkleParameters getParameters(final GordianSparkleKey pType) {
        return switch (pType) {
            case SPARKLE128_128 -> SparkleParameters.SCHWAEMM128_128;
            case SPARKLE256_128 -> SparkleParameters.SCHWAEMM256_128;
            case SPARKLE192_192 -> SparkleParameters.SCHWAEMM192_192;
            default -> SparkleParameters.SCHWAEMM256_256;
        };
    }
}
