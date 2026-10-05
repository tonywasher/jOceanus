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
import io.github.tonywasher.joceanus.gordianknot.api.exc.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.keyset.GordianKeySet;
import io.github.tonywasher.joceanus.gordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStorePair;
import org.bouncycastle.asn1.cms.EncryptedContentInfo;
import org.bouncycastle.asn1.cms.EnvelopedData;
import org.bouncycastle.asn1.cms.KeyTransRecipientInfo;
import org.bouncycastle.asn1.cms.RecipientInfo;
import org.bouncycastle.asn1.x509.Certificate;

import java.util.ArrayList;
import java.util.List;

/**
 * Confirm Control.
 */
public class GordianCertGatewayConfirmState {
    /**
     * The responseId.
     */
    private final int theResponseId;

    /**
     * The encryptor.
     */
    private final GordianCertGatewayEncryptor theEncryptor;

    /**
     * The failure code.
     */
    private GordianCertFailure theFailure;

    /**
     * The encrypted certificate.
     */
    private final EnvelopedData theEncrypted;

    /**
     * The certificates.
     */
    private final List<Certificate> theCertificates;

    /**
     * The certificate.
     */
    private Certificate theCertificate;

    /**
     * The digestCode.
     */
    private byte[] theDigest;

    /**
     * Constructor.
     *
     * @param pResponse  the response
     * @param pEncryptor the encryptor
     */
    GordianCertGatewayConfirmState(final GordianCertGatewayResponseASN1 pResponse,
                                   final GordianCertGatewayEncryptor pEncryptor) throws GordianException {
        theResponseId = pResponse.getResponseId();
        theEncryptor = pEncryptor;
        theCertificates = pResponse.getTheCertificateChain();
        theEncrypted = pResponse.getEncrypted();
        theCertificate = pResponse.getCertificate();
    }

    /**
     * Is the confirmation successful?
     *
     * @return true/false
     */
    boolean isSuccess() {
        return theFailure == null;
    }

    /**
     * Does the confirmation need a digest response?
     *
     * @return true/false
     */
    boolean needsDigest() {
        return theEncrypted != null;
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
     * Set the digest code.
     *
     * @param pDigestCode the digest code
     */
    void setDigest(final byte[] pDigestCode) {
        theDigest = pDigestCode;
    }

    /**
     * Obtain the certificate chain
     *
     * @param pKeyPair the keyPair for decryption
     * @return the chain (or null)
     */
    List<GordianCertificate> getChain(final GordianKeyStorePair pKeyPair) {
        /* Create the chain */
        final List<GordianCertificate> myChain = new ArrayList<>();

        /* Protect against exceptions */
        try {
            /* Decrypt the certificate if required */
            if (theCertificate == null) {
                decryptCertificate(pKeyPair);
            }

            /* Store the details */
            myChain.add(theEncryptor.convertCertificate(theCertificate));
            for (Certificate myCert : theCertificates) {
                myChain.add(theEncryptor.convertCertificate(myCert));
            }

        } catch (GordianException e) {
            theFailure = GordianCertFailure.SYSTEMFAIL;
        }

        /* Return the chain */
        return myChain;
    }

    /**
     * Create confirm ASN1.
     *
     * @return the confirmation message
     */
    GordianCertGatewayConfirmASN1 buildConfirm() {
        /* Handle failure case */
        if (theFailure != null) {
            return GordianCertGatewayConfirmASN1.createCertConfirm(theResponseId, theFailure);
        }

        /* Handle success case */
        return GordianCertGatewayConfirmASN1.createCertConfirm(theResponseId, theDigest);
    }

    /**
     * Resolve certificate.
     *
     * @param pKeyPair the keyPair
     * @throws GordianException on error
     */
    private void decryptCertificate(final GordianKeyStorePair pKeyPair) throws GordianException {
        /* Derive the KeySet */
        final RecipientInfo myRecipient = RecipientInfo.getInstance(theEncrypted.getRecipientInfos().getObjectAt(0));
        final KeyTransRecipientInfo myRecInfo = (KeyTransRecipientInfo) myRecipient.getInfo();
        final GordianKeySet myKeySet = theEncryptor.deriveKeySetFromRecInfo(myRecInfo,
                pKeyPair.getCertificateChain().getFirst(), pKeyPair.getKeyPair());

        /* Decrypt the certificate */
        final EncryptedContentInfo myInfo = theEncrypted.getEncryptedContentInfo();
        final byte[] myEncoded = myKeySet.decryptBytes(myInfo.getEncryptedContent().getOctets());
        theCertificate = Certificate.getInstance(myEncoded);
    }
}
