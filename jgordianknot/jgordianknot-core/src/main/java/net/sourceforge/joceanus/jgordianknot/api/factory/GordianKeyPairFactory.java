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
package net.sourceforge.joceanus.jgordianknot.api.factory;

import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAgreementFactory;
import net.sourceforge.joceanus.jgordianknot.api.encrypt.GordianEncryptorFactory;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairSpec;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairType;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreFactory;
import net.sourceforge.joceanus.jgordianknot.api.sign.GordianSignatureFactory;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * KeyPair Factory API.
 */
public interface GordianKeyPairFactory {
    /**
     * Obtain keyPair generator.
     * @param pKeySpec the keySpec
     * @return the generator
     * @throws OceanusException on error
     */
    GordianKeyPairGenerator getKeyPairGenerator(GordianKeyPairSpec pKeySpec) throws OceanusException;

    /**
     * Determine KeySpec from PKCS8EncodedKeySpec.
     * @param pEncoded the encodedKeySpec
     * @return the keySpec
     * @throws OceanusException on error
     */
    GordianKeyPairSpec determineKeyPairSpec(PKCS8EncodedKeySpec pEncoded) throws OceanusException;

    /**
     * Determine KeySpec from X509EncodedKeySpec.
     * @param pEncoded the encodedKeySpec
     * @return the keySpec
     * @throws OceanusException on error
     */
    GordianKeyPairSpec determineKeyPairSpec(X509EncodedKeySpec pEncoded) throws OceanusException;

    /**
     * Obtain the underlying Factory.
     * @return the factory
     */
    GordianFactory getFactory();

    /**
     * Obtain the signatureFactory.
     * @return the signature factory
     */
    GordianSignatureFactory getSignatureFactory();

    /**
     * Obtain the agreementFactory.
     * @return the agreement factory
     */
    GordianAgreementFactory getAgreementFactory();

    /**
     * Obtain the encryptorFactory.
     * @return the encryptor factory
     */
    GordianEncryptorFactory getEncryptorFactory();

    /**
     * Obtain the keyStore Factory.
     * @return the keyStore factory
     */
    GordianKeyStoreFactory getKeyStoreFactory();

    /**
     * Obtain predicate for keyPairSpecs.
     * @return the predicate
     */
    Predicate<GordianKeyPairSpec> supportedKeyPairSpecs();

    /**
     * Obtain a list of supported keyPairSpecs.
     * @return the list of supported keyPairSpecs.
     */
    default List<GordianKeyPairSpec> listAllSupportedKeyPairSpecs() {
        return GordianKeyPairSpec.listPossibleKeySpecs()
                .stream()
                .filter(supportedKeyPairSpecs())
                .collect(Collectors.toList());
    }

    /**
     * Obtain a list of supported KeyPairSpecs for a KeyPairType.
     * @param pKeyPairType the keyPairType
     * @return the list of supported asymKeySpecs.
     */
    default List<GordianKeyPairSpec> listAllSupportedKeyPairSpecs(final GordianKeyPairType pKeyPairType) {
        return GordianKeyPairSpec.listPossibleKeySpecs()
                .stream()
                .filter(s -> pKeyPairType.equals(s.getKeyPairType()))
                .filter(supportedKeyPairSpecs())
                .collect(Collectors.toList());
    }
}
