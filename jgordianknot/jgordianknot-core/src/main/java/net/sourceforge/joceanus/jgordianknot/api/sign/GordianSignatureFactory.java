/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2019 Tony Washer
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

import net.sourceforge.joceanus.jgordianknot.api.asym.GordianAsymKeyType;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyPair;
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
    GordianSignature createSigner(GordianSignatureSpec pSignatureSpec) throws OceanusException;

    /**
     * Obtain predicate for signatures.
     * @return the predicate
     */
    Predicate<GordianSignatureSpec> supportedSignatureSpecs();

    /**
     * Obtain a list of supported signatureSpecs.
     * @param pKeyType the keyType
     * @return the list of supported signatureSpecs.
     */
    default List<GordianSignatureSpec> listAllSupportedSignatureSpecs(final GordianAsymKeyType pKeyType) {
        return GordianSignatureSpec.listPossibleSignatures(pKeyType)
                .stream()
                .filter(supportedSignatureSpecs())
                .collect(Collectors.toList());
    }

    /**
     * Check SignatureSpec and KeyPair combination.
     * @param pKeyPair the keyPair
     * @param pSignSpec the signSpec
     * @return true/false
     */
    boolean validSignatureSpecForKeyPair(GordianKeyPair pKeyPair,
                                         GordianSignatureSpec pSignSpec);

    /**
     * Obtain a list of supported signatureSpecs.
     * @param pKeyPair the keyPair
     * @return the list of supported signatureSpecs.
     */
    default List<GordianSignatureSpec> listAllSupportedSignatureSpecs(final GordianKeyPair pKeyPair) {
        return GordianSignatureSpec.listPossibleSignatures(pKeyPair.getKeySpec().getKeyType())
                .stream()
                .filter(s -> validSignatureSpecForKeyPair(pKeyPair, s))
                .collect(Collectors.toList());
    }
}
