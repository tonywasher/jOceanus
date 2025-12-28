/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2025 Tony Washer
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

import net.sourceforge.joceanus.gordianknot.api.agree.GordianAgreementSpec;
import net.sourceforge.joceanus.gordianknot.api.cert.GordianCertificate;
import net.sourceforge.joceanus.gordianknot.api.sign.GordianSignatureSpec;
import net.sourceforge.joceanus.gordianknot.api.xagree.GordianXAgreementStatus;

/**
 * Agreement State.
 */
public class GordianXCoreAgreementState {
    /**
     * The Spec.
     */
    private final GordianAgreementSpec theSpec;

    /**
     * The status.
     */
    private GordianXAgreementStatus theStatus;

    /**
     * The resultType.
     */
    private Object theResultType;

    /**
     * The result.
     */
    private Object theResult;

    /**
     * The clientState.
     */
    private final GordianXCoreAgreementParticipant theClient;

    /**
     * The serverState.
     */
    private final GordianXCoreAgreementParticipant theServer;

    /**
     * The encapsulated.
     */
    private byte[] theEncapsulated;

    /**
     * Signer Certificate.
     */
    private GordianCertificate theSignerCertificate;

    /**
     * The signatureSpec.
     */
    private GordianSignatureSpec theSignSpec;

    /**
     * Constructor.
     * @param pSpec the agreementSpec
     */
    GordianXCoreAgreementState(final GordianAgreementSpec pSpec) {
        /* Store parameters */
        theSpec = pSpec;

        /* Create participants */
        theClient = new GordianXCoreAgreementParticipant();
        theServer = new GordianXCoreAgreementParticipant();
    }

    /**
     * Obtain the Agreement Spec.
     * @return the agreementSpec
     */
    public GordianAgreementSpec getSpec() {
        return theSpec;
    }

    /**
     * Obtain the Status.
     * @return the status
     */
    GordianXAgreementStatus getStatus() {
        return theStatus;
    }

    /**
     * Set the status.
     * @param pStatus the status
     * @return the state
     */
    GordianXCoreAgreementState setStatus(final GordianXAgreementStatus pStatus) {
        theStatus = pStatus;
        return this;
    }

    /**
     * Obtain the ResultType.
     * @return the resultType
     */
    Object getResultType() {
        return theResultType;
    }

    /**
     * Set the resultType.
     * @param pResultType the resultType
     * @return the state
     */
    GordianXCoreAgreementState setResultType(final Object pResultType) {
        theResultType = pResultType;
        return this;
    }

    /**
     * Obtain the Result.
     * @return the result
     */
    Object getResult() {
        return theResult;
    }

    /**
     * Set the result.
     * @param pResult the result
     * @return the state
     */
    GordianXCoreAgreementState setResult(final Object pResult) {
        theResult = pResult;
        return this;
    }

    /**
     * Obtain the clientState.
     * @return the state
     */
    public GordianXCoreAgreementParticipant getClient() {
        return theClient;
    }

    /**
     * Obtain the serverState.
     * @return the state
     */
    public GordianXCoreAgreementParticipant getServer() {
        return theServer;
    }

    /**
     * Obtain the encapsulated.
     * @return the encapsulated
     */
    public byte[] getEncapsulated() {
        return theEncapsulated;
    }

    /**
     * Set the encapsulated.
     * @param pEncapsulated the encapsulated
     * @return the state
     */
    public GordianXCoreAgreementState setEncapsulated(final byte[] pEncapsulated) {
        theEncapsulated = pEncapsulated;
        return this;
    }

    /**
     * Obtain the signer certificate.
     * @return the certificate
     */
    GordianCertificate getSignerCertificate() {
        return theSignerCertificate;
    }

    /**
     * Set the signer certificate.
     * @param pCertificate the certificate
     */
    GordianXCoreAgreementState setSignerCertificate(final GordianCertificate pCertificate) {
        theSignerCertificate = pCertificate;
        return this;
    }

    /**
     * Obtain the signatureSpec.
     * @return the signatureSpec
     */
    GordianSignatureSpec getSignSpec() {
        return theSignSpec;
    }

    /**
     * Set the signatureSpec.
     * @param pSignSpec the signatureSpec
     * @return the state
     */
    GordianXCoreAgreementState setSignSpec(final GordianSignatureSpec pSignSpec) {
        theSignSpec = pSignSpec;
        return this;
    }
}
