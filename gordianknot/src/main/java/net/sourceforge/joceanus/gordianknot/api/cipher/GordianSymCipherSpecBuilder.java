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
package net.sourceforge.joceanus.gordianknot.api.cipher;

/**
 * The SymCipherSpec Builder class.
 */
public final class GordianSymCipherSpecBuilder {
    /**
     * Private constructor.
     */
    private GordianSymCipherSpecBuilder() {
    }

    /**
     * Create an ECB symKey cipherSpec.
     * @param pKeySpec the keySpec
     * @param pPadding the padding
     * @return the cipherSpec
     */
    public static GordianSymCipherSpec ecb(final GordianSymKeySpec pKeySpec,
                                           final GordianPadding pPadding) {
        return new GordianSymCipherSpec(pKeySpec, GordianCipherMode.ECB, pPadding);
    }

    /**
     * Create a CBC symKey cipherSpec.
     * @param pKeySpec the keySpec
     * @param pPadding the padding
     * @return the cipherSpec
     */
    public static GordianSymCipherSpec cbc(final GordianSymKeySpec pKeySpec,
                                           final GordianPadding pPadding) {
        return new GordianSymCipherSpec(pKeySpec, GordianCipherMode.CBC, pPadding);
    }

    /**
     * Create a CFB symKey cipherSpec.
     * @param pKeySpec the keySpec
     * @return the cipherSpec
     */
    public static GordianSymCipherSpec cfb(final GordianSymKeySpec pKeySpec) {
        return new GordianSymCipherSpec(pKeySpec, GordianCipherMode.CFB, GordianPadding.NONE);
    }

    /**
     * Create a GCFB symKey cipherSpec.
     * @param pKeySpec the keySpec
     * @return the cipherSpec
     */
    public static GordianSymCipherSpec gcfb(final GordianSymKeySpec pKeySpec) {
        return new GordianSymCipherSpec(pKeySpec, GordianCipherMode.GCFB, GordianPadding.NONE);
    }

    /**
     * Create a OFB symKey cipherSpec.
     * @param pKeySpec the keySpec
     * @return the cipherSpec
     */
    public static GordianSymCipherSpec ofb(final GordianSymKeySpec pKeySpec) {
        return new GordianSymCipherSpec(pKeySpec, GordianCipherMode.OFB, GordianPadding.NONE);
    }

    /**
     * Create a GOFB symKey cipherSpec.
     * @param pKeySpec the keySpec
     * @return the cipherSpec
     */
    public static GordianSymCipherSpec gofb(final GordianSymKeySpec pKeySpec) {
        return new GordianSymCipherSpec(pKeySpec, GordianCipherMode.GOFB, GordianPadding.NONE);
    }

    /**
     * Create a SIC symKey cipherSpec.
     * @param pKeySpec the keySpec
     * @return the cipherSpec
     */
    public static GordianSymCipherSpec sic(final GordianSymKeySpec pKeySpec) {
        return new GordianSymCipherSpec(pKeySpec, GordianCipherMode.SIC, GordianPadding.NONE);
    }

    /**
     * Create a KCTR symKey cipherSpec.
     * @param pKeySpec the keySpec
     * @return the cipherSpec
     */
    public static GordianSymCipherSpec kctr(final GordianSymKeySpec pKeySpec) {
        return new GordianSymCipherSpec(pKeySpec, GordianCipherMode.KCTR, GordianPadding.NONE);
    }

    /**
     * Create a CCM symKey cipherSpec.
     * @param pKeySpec the keySpec
     * @return the cipherSpec
     */
    public static GordianSymCipherSpec ccm(final GordianSymKeySpec pKeySpec) {
        return new GordianSymCipherSpec(pKeySpec, GordianCipherMode.CCM, GordianPadding.NONE);
    }

    /**
     * Create a KCCM symKey cipherSpec.
     * @param pKeySpec the keySpec
     * @return the cipherSpec
     */
    public static GordianSymCipherSpec kccm(final GordianSymKeySpec pKeySpec) {
        return new GordianSymCipherSpec(pKeySpec, GordianCipherMode.KCCM, GordianPadding.NONE);
    }

    /**
     * Create a GCM symKey cipherSpec.
     * @param pKeySpec the keySpec
     * @return the cipherSpec
     */
    public static GordianSymCipherSpec gcm(final GordianSymKeySpec pKeySpec) {
        return new GordianSymCipherSpec(pKeySpec, GordianCipherMode.GCM, GordianPadding.NONE);
    }

    /**
     * Create a KGCM symKey cipherSpec.
     * @param pKeySpec the keySpec
     * @return the cipherSpec
     */
    public static GordianSymCipherSpec kgcm(final GordianSymKeySpec pKeySpec) {
        return new GordianSymCipherSpec(pKeySpec, GordianCipherMode.KGCM, GordianPadding.NONE);
    }

    /**
     * Create an EAX symKey cipherSpec.
     * @param pKeySpec the keySpec
     * @return the cipherSpec
     */
    public static GordianSymCipherSpec eax(final GordianSymKeySpec pKeySpec) {
        return new GordianSymCipherSpec(pKeySpec, GordianCipherMode.EAX, GordianPadding.NONE);
    }

    /**
     * Create an OCB symKey cipherSpec.
     * @param pKeySpec the keySpec
     * @return the cipherSpec
     */
    public static GordianSymCipherSpec ocb(final GordianSymKeySpec pKeySpec) {
        return new GordianSymCipherSpec(pKeySpec, GordianCipherMode.OCB, GordianPadding.NONE);
    }
}
