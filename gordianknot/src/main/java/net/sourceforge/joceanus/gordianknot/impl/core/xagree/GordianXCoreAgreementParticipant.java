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
package net.sourceforge.joceanus.gordianknot.impl.core.xagree;

import net.sourceforge.joceanus.gordianknot.api.cert.GordianCertificate;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPair;

import java.security.spec.X509EncodedKeySpec;

/**
 * Participant state.
 */
public class GordianXCoreAgreementParticipant {
    /**
     * Certificate.
     */
    private GordianCertificate theCertificate;

    /**
     * KeyPair.
     */
    private GordianKeyPair theKeyPair;

    /**
     * Ephemeral KeyPair.
     */
    private GordianKeyPair theEphemeral;

    /**
     * The Ephemeral keySpec.
     */
    private X509EncodedKeySpec theKeySpec;

    /**
     * InitVector.
     */
    private byte[] theInitVector;

    /**
     * Id.
     */
    private Long theId;

    /**
     * Confirm.
     */
    private byte[] theConfirm;

    /**
     * Obtain the certificate.
     * @return the certificate
     */
    GordianCertificate getCertificate() {
        return theCertificate;
    }

    /**
     * Set the certificate.
     * @param pCertificate the certificate
     * @return the state
     */
    GordianXCoreAgreementParticipant setCertificate(final GordianCertificate pCertificate) {
        theCertificate = pCertificate;
        theKeyPair = theCertificate == null ? null : theCertificate.getKeyPair();
        return this;
    }

    /**
     * Obtain the keyPair.
     * @return the keyPair
     */
    public GordianKeyPair getKeyPair() {
        return theKeyPair;
    }

    /**
     * Set the keyPair.
     * @param pKeyPair the keyPair
     * @return the state
     */
    GordianXCoreAgreementParticipant setKeyPair(final GordianKeyPair pKeyPair) {
        theKeyPair = pKeyPair;
        return this;
    }

    /**
     * Obtain the ephemeral keyPair.
     * @return the keyPair
     */
    public GordianKeyPair getEphemeralKeyPair() {
        return theEphemeral;
    }

    /**
     * Set the ephemeral keyPair.
     * @param pKeyPair the keyPair
     * @return the state
     */
    GordianXCoreAgreementParticipant setEphemeralKeyPair(final GordianKeyPair pKeyPair) {
        theEphemeral = pKeyPair;
        return this;
    }

    /**
     * Obtain the ephemeral keySpec.
     * @return the keySpec
     */
    X509EncodedKeySpec getEphemeralKeySpec() {
        return theKeySpec;
    }

    /**
     * Set the ephemeral keySpec.
     * @param pKeySpec the keySpec
     * @return the state
     */
    GordianXCoreAgreementParticipant setEphemeralKeySpec(final X509EncodedKeySpec pKeySpec) {
        theKeySpec = pKeySpec;
        return this;
    }

    /**
     * Copy ephemeral keyPair to main keyPair.
     */
    void copyEphemeral() {
        theKeyPair = theEphemeral;
     }

    /**
     * Obtain the certificate.
     * @return the certificate
     */
    byte[] getInitVector() {
        return theInitVector;
    }

    /**
     * Set the initVector.
     * @param pInitVector the initVector
     * @return the state
     */
    GordianXCoreAgreementParticipant setInitVector(final byte[] pInitVector) {
        theInitVector = pInitVector;
        return this;
    }

    /**
     * Obtain the id.
     * @return the id
     */
    Long getId() {
        return theId;
    }

    /**
     * Set the id.
     * @param pId the id
     * @return the state
     */
    GordianXCoreAgreementParticipant setId(final Long pId) {
        theId = pId;
        return this;
    }

    /**
     * Obtain the confirmation.
     * @return the confirmation
     */
    public byte[] getConfirm() {
        return theConfirm;
    }

    /**
     * Set the confirmation.
     * @param pConfirm the confirmation
     * @return the state
     */
    GordianXCoreAgreementParticipant setConfirm(final byte[] pConfirm) {
        theConfirm = pConfirm;
        return this;
    }
}
