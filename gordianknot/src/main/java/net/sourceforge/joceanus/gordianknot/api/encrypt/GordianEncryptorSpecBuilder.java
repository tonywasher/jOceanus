/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012-2026 Tony Washer
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
package net.sourceforge.joceanus.gordianknot.api.encrypt;

import java.util.Arrays;
import java.util.List;

import net.sourceforge.joceanus.gordianknot.api.digest.GordianDigestSpec;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairType;

/**
 * Asymmetric Encryption Specification Builder.
 */
public final class GordianEncryptorSpecBuilder {
    /**
     * Private constructor.
     */
    private GordianEncryptorSpecBuilder() {
    }

    /**
     * Create RSA Encryptor.
     * @param pSpec the digestSpec
     * @return the encryptorSpec
     */
    public static GordianEncryptorSpec rsa(final GordianDigestSpec pSpec) {
        return new GordianEncryptorSpec(GordianKeyPairType.RSA, pSpec);
    }

    /**
     * Create ElGamal Encryptor.
     * @param pSpec the digestSpec
     * @return the encryptorSpec
     */
    public static GordianEncryptorSpec elGamal(final GordianDigestSpec pSpec) {
        return new GordianEncryptorSpec(GordianKeyPairType.ELGAMAL, pSpec);
    }

    /**
     * Create EC Encryptor.
     * @return the encryptorSpec
     */
    public static GordianEncryptorSpec ec() {
        return new GordianEncryptorSpec(GordianKeyPairType.EC, null);
    }

    /**
     * Create GOST Encryptor.
     * @return the encryptorSpec
     */
    public static GordianEncryptorSpec gost2012() {
        return new GordianEncryptorSpec(GordianKeyPairType.GOST2012, null);
    }

    /**
     * Create SM2 Encryptor.
     * @return the encryptorSpec
     */
    public static GordianEncryptorSpec sm2() {
        return new GordianEncryptorSpec(GordianKeyPairType.SM2, null);
    }

    /**
     * Create SM2 Encryptor.
     * @param pSpec the sm2EncryptionSpec
     * @return the encryptorSpec
     */
    public static GordianEncryptorSpec sm2(final GordianSM2EncryptionSpec pSpec) {
        return new GordianEncryptorSpec(GordianKeyPairType.SM2, pSpec);
    }

    /**
     * Create CompositeSpec.
     * @param pSpecs the list of encryptorSpecs
     * @return the encryptorSpec
     */
    public static GordianEncryptorSpec composite(final GordianEncryptorSpec... pSpecs) {
        return composite(Arrays.asList(pSpecs));
    }

    /**
     * Create CompositeSpec.
     * @param pSpecs the list of encryptorSpecs
     * @return the encryptorSpec
     */
    public static GordianEncryptorSpec composite(final List<GordianEncryptorSpec> pSpecs) {
        return new GordianEncryptorSpec(GordianKeyPairType.COMPOSITE, pSpecs);
    }
}
