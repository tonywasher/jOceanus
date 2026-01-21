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
package io.github.tonywasher.joceanus.gordianknot.impl.core.agree;

import io.github.tonywasher.joceanus.gordianknot.api.agree.GordianAgreementSpec;
import io.github.tonywasher.joceanus.gordianknot.api.agree.GordianAgreementStatus;
import io.github.tonywasher.joceanus.gordianknot.api.cert.GordianCertificate;
import io.github.tonywasher.joceanus.gordianknot.api.sign.GordianSignatureSpec;

/**
 * Agreement State.
 */
public class GordianCoreAgreementState {
    /**
     * The Spec.
     */
    private final GordianAgreementSpec theSpec;

    /**
     * The status.
     */
    private GordianAgreementStatus theStatus;

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
    private final GordianCoreAgreementParticipant theClient;

    /**
     * The serverState.
     */
    private final GordianCoreAgreementParticipant theServer;

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
     * The additional data.
     */
    private byte[] theAdditional;

    /**
     * Constructor.
     *
     * @param pSpec the agreementSpec
     */
    GordianCoreAgreementState(final GordianAgreementSpec pSpec) {
        /* Store parameters */
        theSpec = pSpec;

        /* Create participants */
        theClient = new GordianCoreAgreementParticipant();
        theServer = new GordianCoreAgreementParticipant();
    }

    /**
     * Obtain the Agreement Spec.
     *
     * @return the agreementSpec
     */
    public GordianAgreementSpec getSpec() {
        return theSpec;
    }

    /**
     * Obtain the Status.
     *
     * @return the status
     */
    GordianAgreementStatus getStatus() {
        return theStatus;
    }

    /**
     * Set the status.
     *
     * @param pStatus the status
     * @return the state
     */
    GordianCoreAgreementState setStatus(final GordianAgreementStatus pStatus) {
        theStatus = pStatus;
        return this;
    }

    /**
     * Obtain the ResultType.
     *
     * @return the resultType
     */
    Object getResultType() {
        return theResultType;
    }

    /**
     * Set the resultType.
     *
     * @param pResultType the resultType
     * @return the state
     */
    GordianCoreAgreementState setResultType(final Object pResultType) {
        theResultType = pResultType;
        return this;
    }

    /**
     * Obtain the Result.
     *
     * @return the result
     */
    Object getResult() {
        return theResult;
    }

    /**
     * Set the result.
     *
     * @param pResult the result
     * @return the state
     */
    GordianCoreAgreementState setResult(final Object pResult) {
        theResult = pResult;
        return this;
    }

    /**
     * Obtain the clientState.
     *
     * @return the state
     */
    public GordianCoreAgreementParticipant getClient() {
        return theClient;
    }

    /**
     * Obtain the serverState.
     *
     * @return the state
     */
    public GordianCoreAgreementParticipant getServer() {
        return theServer;
    }

    /**
     * Obtain the encapsulated.
     *
     * @return the encapsulated
     */
    public byte[] getEncapsulated() {
        return theEncapsulated;
    }

    /**
     * Set the encapsulated.
     *
     * @param pEncapsulated the encapsulated
     * @return the state
     */
    public GordianCoreAgreementState setEncapsulated(final byte[] pEncapsulated) {
        theEncapsulated = pEncapsulated;
        return this;
    }

    /**
     * Obtain the signer certificate.
     *
     * @return the certificate
     */
    GordianCertificate getSignerCertificate() {
        return theSignerCertificate;
    }

    /**
     * Set the signer certificate.
     *
     * @param pCertificate the certificate
     * @return the state
     */
    GordianCoreAgreementState setSignerCertificate(final GordianCertificate pCertificate) {
        theSignerCertificate = pCertificate;
        return this;
    }

    /**
     * Obtain the signatureSpec.
     *
     * @return the signatureSpec
     */
    GordianSignatureSpec getSignSpec() {
        return theSignSpec;
    }

    /**
     * Set the signatureSpec.
     *
     * @param pSignSpec the signatureSpec
     * @return the state
     */
    GordianCoreAgreementState setSignSpec(final GordianSignatureSpec pSignSpec) {
        theSignSpec = pSignSpec;
        return this;
    }

    /**
     * Obtain the additional Data.
     *
     * @return the data
     */
    public byte[] getAdditionalData() {
        return theAdditional;
    }

    /**
     * Set the encapsulated.
     *
     * @param pAdditional the encapsulated
     * @return the state
     */
    public GordianCoreAgreementState setAdditionalData(final byte[] pAdditional) {
        theAdditional = pAdditional;
        return this;
    }
}
