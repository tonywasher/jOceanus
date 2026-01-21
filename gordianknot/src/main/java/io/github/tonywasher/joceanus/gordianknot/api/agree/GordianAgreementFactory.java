/*
 * GordianKnot: Security Suite
 * Copyright 2012-2026. Tony Washer
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
package io.github.tonywasher.joceanus.gordianknot.api.agree;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.cert.GordianCertificate;
import io.github.tonywasher.joceanus.gordianknot.api.cert.GordianKeyPairUsage;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPair;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPairSpec;
import io.github.tonywasher.joceanus.gordianknot.api.sign.GordianSignatureSpec;
import org.bouncycastle.asn1.x500.X500Name;

import java.util.List;
import java.util.function.Predicate;

/**
 * GordianKnot AgreementFactory API.
 */
public interface GordianAgreementFactory {
    /**
     * Create new AgreementParams.
     *
     * @param pSpec       the agreementSpec
     * @param pResultType the result type
     *                    <dl>
     *                        <dt>GordianFactoryType</dt><dd>To agree a Factory</dd>
     *                        <dt>GordianSymCipherSpec</dt><dd>To agree a symCipher pair</dd>
     *                        <dt>GordianStreamCipherSpec</dt><dd>To agree a streamCipher pair</dd>
     *                        <dt>GordianKeySetSpec</dt><dd>To agree a KeySet</dd>
     *                        <dt>Integer</dt><dd>To agree a defined length byte array</dd>
     *                    </dl>
     * @return the Params
     * @throws GordianException on error
     */
    GordianAgreementParams newAgreementParams(GordianAgreementSpec pSpec,
                                              Object pResultType) throws GordianException;

    /**
     * CreateAgreement.
     *
     * @param pParams the agreementParams
     * @return the Agreement
     * @throws GordianException on error
     */
    GordianAgreement createAgreement(GordianAgreementParams pParams) throws GordianException;

    /**
     * Create/Locate Agreement for incoming message.
     *
     * @param pMessage the incoming message
     * @return the Agreement
     * @throws GordianException on error
     */
    GordianAgreement parseAgreementMessage(byte[] pMessage) throws GordianException;

    /**
     * Declare signer certificate.
     *
     * @param pSigner the certificate
     * @throws GordianException on error
     */
    void setSigner(GordianCertificate pSigner) throws GordianException;

    /**
     * Declare signer certificate and specification.
     *
     * @param pSigner   the certificate
     * @param pSignSpec the signSpec
     * @throws GordianException on error
     */
    void setSigner(GordianCertificate pSigner,
                   GordianSignatureSpec pSignSpec) throws GordianException;

    /**
     * Create new miniCertificate.
     *
     * @param pSubject the subject of the certificate
     * @param pKeyPair the keyPair.
     * @param pUsage   the usage
     * @return the certificate
     * @throws GordianException on error
     */
    GordianCertificate newMiniCertificate(X500Name pSubject,
                                          GordianKeyPair pKeyPair,
                                          GordianKeyPairUsage pUsage) throws GordianException;

    /**
     * Obtain predicate for keyAgreement.
     *
     * @return the predicate
     */
    Predicate<GordianAgreementSpec> supportedAgreements();

    /**
     * Check AgreementSpec and KeyPair combination.
     *
     * @param pKeyPair       the keyPair
     * @param pAgreementSpec the macSpec
     * @return true/false
     */
    default boolean validAgreementSpecForKeyPair(final GordianKeyPair pKeyPair,
                                                 final GordianAgreementSpec pAgreementSpec) {
        return validAgreementSpecForKeyPairSpec(pKeyPair.getKeyPairSpec(), pAgreementSpec);
    }

    /**
     * Check AgreementSpec and KeyPairSpec combination.
     *
     * @param pKeyPairSpec   the keyPairSpec
     * @param pAgreementSpec the agreementSpec
     * @return true/false
     */
    boolean validAgreementSpecForKeyPairSpec(GordianKeyPairSpec pKeyPairSpec,
                                             GordianAgreementSpec pAgreementSpec);

    /**
     * Obtain a list of supported agreementSpecs.
     *
     * @param pKeyPair the keyPair
     * @return the list of supported agreementSpecs.
     */
    List<GordianAgreementSpec> listAllSupportedAgreements(GordianKeyPair pKeyPair);

    /**
     * Obtain a list of supported agreementSpecs.
     *
     * @param pKeyPairSpec the keySpec
     * @return the list of supported agreementSpecs.
     */
    List<GordianAgreementSpec> listAllSupportedAgreements(GordianKeyPairSpec pKeyPairSpec);

    /**
     * Create default agreementSpec for key.
     *
     * @param pKeySpec the keySpec
     * @return the AgreementSpec
     */
    GordianAgreementSpec defaultForKeyPair(GordianKeyPairSpec pKeySpec);
}
