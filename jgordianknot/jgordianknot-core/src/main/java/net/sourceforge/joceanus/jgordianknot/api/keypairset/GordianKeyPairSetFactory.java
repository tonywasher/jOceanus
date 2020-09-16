/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2020 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.api.keypairset;

import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * KeyPairSetFactory.
 */
public interface GordianKeyPairSetFactory {
    /**
     * Obtain keyPairSet generator.
     * @param pKeyPairSetSpec the keyPairSetSpec
     * @return the generator
     * @throws OceanusException on error
     */
    GordianKeyPairSetGenerator getKeyPairSetGenerator(GordianKeyPairSetSpec pKeyPairSetSpec) throws OceanusException;

    /**
     * create keyPairSetAgreement.
     * @param pAgreementSpec the keyPairSetSpec
     * @return the encryptor
     * @throws OceanusException on error
     */
    GordianKeyPairSetAgreement createAgreement(GordianKeyPairSetAgreementSpec pAgreementSpec) throws OceanusException;

    /**
     * Create Agreement for clientHello message.
     * @param pClientHello the clientHello message
     * @return the Agreement
     * @throws OceanusException on error
     */
    GordianKeyPairSetAgreement createAgreement(byte[] pClientHello) throws OceanusException;

    /**
     * create keyPairSetEncryptor.
     * @param pKeyPairSetSpec the keyPairSetSpec
     * @return the encryptor
     * @throws OceanusException on error
     */
    GordianKeyPairSetEncryptor createEncryptor(GordianKeyPairSetSpec pKeyPairSetSpec) throws OceanusException;

    /**
     * create keyPairSetSigner.
     * @param pKeyPairSetSpec the keyPairSetSpec
     * @return the encryptor
     * @throws OceanusException on error
     */
    GordianKeyPairSetSignature createSigner(GordianKeyPairSetSpec pKeyPairSetSpec) throws OceanusException;

    /**
     * Determine KeyPairSetSpec from PKCS8EncodedKeySpec.
     * @param pEncoded the encodedKeySpec
     * @return the keyPairSetSpec
     * @throws OceanusException on error
     */
    GordianKeyPairSetSpec determineKeyPairSetSpec(PKCS8EncodedKeySpec pEncoded) throws OceanusException;

    /**
     * Determine KeyPairSetSpec from X509EncodedKeySpec.
     * @param pEncoded the encodedKeySpec
     * @return the keyPairSetSpec
     * @throws OceanusException on error
     */
    GordianKeyPairSetSpec determineKeyPairSetSpec(X509EncodedKeySpec pEncoded) throws OceanusException;

    /**
     * Obtain predicate for keyPairSetSpecs.
     * @return the predicate
     */
    Predicate<GordianKeyPairSetSpec> supportedKeyPairSetSpecs();

    /**
     * Obtain a list of supported keyPairSetSpecs.
     * @return the list of supported keyPairSetSpecs.
     */
    default List<GordianKeyPairSetSpec> listAllSupportedKeyPairSetSpecs() {
        return EnumSet.allOf(GordianKeyPairSetSpec.class)
                .stream()
                .filter(supportedKeyPairSetSpecs())
                .collect(Collectors.toList());
    }
}
