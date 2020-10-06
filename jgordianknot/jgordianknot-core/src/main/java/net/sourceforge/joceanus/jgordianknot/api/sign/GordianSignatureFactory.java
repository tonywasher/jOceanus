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
package net.sourceforge.joceanus.jgordianknot.api.sign;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairSpec;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairType;
import net.sourceforge.joceanus.jgordianknot.api.keypairset.GordianKeyPairSetSpec;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * GordianKnot SignatureFactory API.
 */
public interface GordianSignatureFactory {
    /**
     * Create signer.
     * @param pSignatureSpec the signatureSpec
     * @return the signer
     * @throws OceanusException on error
     */
    GordianKeyPairSignature createKeyPairSigner(GordianSignatureSpec pSignatureSpec) throws OceanusException;

    /**
     * create keyPairSetSigner.
     * @param pKeyPairSetSpec the keyPairSetSpec
     * @return the encryptor
     * @throws OceanusException on error
     */
    GordianKeyPairSetSignature createKeyPairSetSigner(GordianKeyPairSetSpec pKeyPairSetSpec) throws OceanusException;

    /**
     * Obtain predicate for signatures.
     * @return the predicate
     */
    Predicate<GordianSignatureSpec> supportedKeyPairSignatures();

    /**
     * Obtain a list of supported signatures.
     * @param pKeyPairType the keyPairType
     * @return the list of supported signatureSpecs.
     */
    default List<GordianSignatureSpec> listAllSupportedSignatures(final GordianKeyPairType pKeyPairType) {
        return GordianSignatureSpec.listPossibleSignatures(pKeyPairType)
                .stream()
                .filter(supportedKeyPairSignatures())
                .collect(Collectors.toList());
    }

    /**
     * Check SignatureSpec and KeyPair combination.
     * @param pKeyPair the keyPair
     * @param pSignSpec the signSpec
     * @return true/false
     */
    default boolean validSignatureSpecForKeyPair(GordianKeyPair pKeyPair,
                                                 GordianSignatureSpec pSignSpec) {
        return validSignatureSpecForKeyPairSpec(pKeyPair.getKeyPairSpec(), pSignSpec);
    }

    /**
     * Check SignatureSpec and KeyPairSpec combination.
     * @param pKeyPairSpec the keyPairSpec
     * @param pSignSpec the signSpec
     * @return true/false
     */
    boolean validSignatureSpecForKeyPairSpec(GordianKeyPairSpec pKeyPairSpec,
                                             GordianSignatureSpec pSignSpec);

    /**
     * Obtain a list of supported signatureSpecs.
     * @param pKeyPair the keyPair
     * @return the list of supported signatureSpecs.
     */
    default List<GordianSignatureSpec> listAllSupportedSignatures(final GordianKeyPair pKeyPair) {
        return listAllSupportedSignatures(pKeyPair.getKeyPairSpec());
    }

    /**
     * Obtain a list of supported signatureSpecs.
     * @param pKeySpec the keySpec
     * @return the list of supported signatureSpecs.
     */
    default List<GordianSignatureSpec> listAllSupportedSignatures(final GordianKeyPairSpec pKeySpec) {
        return GordianSignatureSpec.listPossibleSignatures(pKeySpec.getKeyPairType())
                .stream()
                .filter(s -> validSignatureSpecForKeyPairSpec(pKeySpec, s))
                .collect(Collectors.toList());
    }
}
