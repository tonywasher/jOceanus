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
package net.sourceforge.joceanus.gordianknot.api.sign;

import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairSpec;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairType;

import java.util.List;
import java.util.function.Predicate;

/**
 * GordianKnot SignatureFactory API.
 */
public interface GordianSignatureFactory {
    /**
     * Create signer.
     * @param pSignatureSpec the signatureSpec
     * @return the signer
     * @throws GordianException on error
     */
    GordianSignature createSigner(GordianSignatureSpec pSignatureSpec) throws GordianException;

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
        return listPossibleSignatures(pKeyPairType)
                .stream()
                .filter(supportedKeyPairSignatures())
                .toList();
    }

    /**
     * Check SignatureSpec and KeyPair combination.
     * @param pKeyPair the keyPair
     * @param pSignSpec the signSpec
     * @return true/false
     */
    default boolean validSignatureSpecForKeyPair(final GordianKeyPair pKeyPair,
                                                 final GordianSignatureSpec pSignSpec) {
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
    List<GordianSignatureSpec> listAllSupportedSignatures(GordianKeyPair pKeyPair);

    /**
     * Obtain a list of supported signatureSpecs.
     * @param pKeySpec the keySpec
     * @return the list of supported signatureSpecs.
     */
    List<GordianSignatureSpec> listAllSupportedSignatures(GordianKeyPairSpec pKeySpec);

    /**
     * Obtain a list of all possible signatures for the keyType.
     * @param pKeyType the keyType
     * @return the list
     */
    List<GordianSignatureSpec> listPossibleSignatures(GordianKeyPairType pKeyType);

    /**
     * Create default signatureSpec for keyPair.
     * @param pKeySpec the keyPairSpec
     * @return the SignatureSpec
     */
    GordianSignatureSpec defaultForKeyPair(GordianKeyPairSpec pKeySpec);
}
