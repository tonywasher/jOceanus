/*
 * GordianKnot: Security Suite
 * Copyright 2026. Tony Washer
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

package io.github.tonywasher.joceanus.gordianknot.impl.core.certgateway;

import io.github.tonywasher.joceanus.gordianknot.api.cert.GordianCertificate;
import io.github.tonywasher.joceanus.gordianknot.api.certgateway.GordianCertGatewayStatus.GordianCertFailure;
import io.github.tonywasher.joceanus.gordianknot.api.certgateway.GordianCertGatewayStatus.GordianCertStatus;
import io.github.tonywasher.joceanus.gordianknot.api.exc.GordianException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.cert.GordianCoreCertificate;
import io.github.tonywasher.joceanus.gordianknot.impl.core.certgateway.GordianCertGatewayEncryptor.GordianCertGatewayResult;
import org.bouncycastle.asn1.BERSet;
import org.bouncycastle.asn1.cms.EncryptedContentInfo;
import org.bouncycastle.asn1.cms.EnvelopedData;
import org.bouncycastle.asn1.x509.Certificate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Response Control.
 */
public class GordianCertGatewayResponseState {
    /**
     * The requestId.
     */
    private final int theRequestId;

    /**
     * The responseId.
     */
    private final int theResponseId;

    /**
     * The encryptor.
     */
    private final GordianCertGatewayEncryptor theEncryptor;

    /**
     * The certificateChain.
     */
    private final List<Certificate> theCertificateChain;

    /**
     * Does the response need encryption?
     */
    private final boolean needsEncryption;

    /**
     * The status.
     */
    private GordianCertStatus theStatus;

    /**
     * The failure code.
     */
    private GordianCertFailure theFailure;

    /**
     * The certificates.
     */
    private GordianCoreCertificate theCertificate;

    /**
     * The encrypted certificate.
     */
    private EnvelopedData theEncrypted;

    /**
     * Constructor.
     *
     * @param pRequest    the request
     * @param pEncryptor  the encryptor
     * @param pResponseId the responseId
     */
    GordianCertGatewayResponseState(final GordianCertGatewayRequestASN1 pRequest,
                                    final GordianCertGatewayEncryptor pEncryptor,
                                    final int pResponseId) {
        theRequestId = pRequest.getRequestId();
        theEncryptor = pEncryptor;
        theResponseId = pResponseId;
        theStatus = GordianCertStatus.ACCEPTED;
        theCertificateChain = new ArrayList<>();
        needsEncryption = GordianCertGatewayParser.requiresEncryption(pRequest.getCertificateRequest());
    }

    /**
     * Is the request successful?
     *
     * @return true/false
     */
    boolean isSuccess() {
        return theFailure == null;
    }

    /**
     * Set the status code.
     *
     * @param pStatus the status code
     */
    void setStatus(final GordianCertStatus pStatus) {
        theStatus = pStatus;
    }

    /**
     * Set the failure code.
     *
     * @param pFailure the failure code
     */
    void setFailure(final GordianCertFailure pFailure) {
        theFailure = pFailure;
    }

    /**
     * Create response ASN1.
     *
     * @return the response message
     */
    GordianCertGatewayResponseASN1 buildResponse() {
        /* Handle failure case */
        if (theFailure != null) {
            return GordianCertGatewayResponseASN1.createResponse(theRequestId, theFailure);
        }

        /* Handle success case */
        final Object myCert = theEncrypted != null ? theEncrypted : theCertificate.getCertificate();
        return GordianCertGatewayResponseASN1.createResponse(theRequestId, theResponseId, theStatus, myCert, theCertificateChain);
    }

    /**
     * Encrypt certificate.
     */
    private void encryptCertificate() {
        /* Protect against exceptions */
        try {
            /* Prepare for encryption */
            final GordianCertGatewayResult myResult = theEncryptor.prepareForEncryption(theCertificate);

            /* Create the encrypted content */
            final EncryptedContentInfo myInfo = GordianCertGatewayEncryptor.buildEncryptedContentInfo(myResult.keySet(), theCertificate);
            theEncrypted = new EnvelopedData(null, new BERSet(myResult.recipient()), myInfo, (BERSet) null);

            /* Note the failure */
        } catch (GordianException e) {
            theFailure = GordianCertFailure.SYSTEMFAIL;
        }
    }

    /**
     * Set the certificate chain.
     *
     * @param pChain the certificate chain
     */
    void setCertificates(final List<GordianCertificate> pChain) {
        /* Extract first element in chain */
        final Iterator<GordianCertificate> myIterator = pChain.iterator();
        theCertificate = (GordianCoreCertificate) myIterator.next();

        /* Store subsequent details */
        theCertificateChain.clear();
        while (myIterator.hasNext()) {
            theCertificateChain.add(((GordianCoreCertificate) myIterator.next()).getCertificate());
        }

        /* Encrypt certificate if necessary */
        if (needsEncryption) {
            encryptCertificate();
        }
    }
}
