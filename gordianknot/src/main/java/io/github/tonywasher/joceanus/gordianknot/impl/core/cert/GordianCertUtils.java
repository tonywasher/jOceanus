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

package io.github.tonywasher.joceanus.gordianknot.impl.core.cert;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;
import io.github.tonywasher.joceanus.gordianknot.api.cert.GordianKeyPairUsage;
import io.github.tonywasher.joceanus.gordianknot.api.cert.GordianKeyPairUse;
import io.github.tonywasher.joceanus.gordianknot.api.digest.GordianDigest;
import io.github.tonywasher.joceanus.gordianknot.api.digest.GordianDigestFactory;
import io.github.tonywasher.joceanus.gordianknot.api.digest.GordianDigestSpec;
import io.github.tonywasher.joceanus.gordianknot.api.digest.GordianDigestSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.factory.GordianFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianIOException;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.TBSCertificate;

import java.io.IOException;
import java.math.BigInteger;

/**
 * Certificate utilities.
 */
public final class GordianCertUtils {
    /**
     * Private constructor.
     */
    private GordianCertUtils() {
    }

    /**
     * Create New Serial#.
     *
     * @return the new serial#
     */
    static BigInteger newSerialNo() {
        final long myNow = System.currentTimeMillis();
        return BigInteger.valueOf(myNow);
    }

    /**
     * Determine usage.
     *
     * @param pExtensions the extensions.
     * @return the usage
     */
    public static GordianKeyPairUsage determineUsage(final Extensions pExtensions) {
        /* Access details */
        final KeyUsage myUsage = KeyUsage.fromExtensions(pExtensions);
        final BasicConstraints myConstraint = BasicConstraints.fromExtensions(pExtensions);
        final GordianKeyPairUsage myResult = new GordianKeyPairUsage();

        /* Check for CERTIFICATE */
        final boolean isCA = myConstraint != null && myConstraint.isCA();
        if (isCA && checkUsage(myUsage, KeyUsage.keyCertSign)) {
            myResult.addUse(GordianKeyPairUse.CERTIFICATE);
        }

        /* Check for signer. */
        if (checkUsage(myUsage, KeyUsage.digitalSignature)) {
            myResult.addUse(GordianKeyPairUse.SIGNATURE);
        }

        /* Check for nonRepudiation. */
        if (checkUsage(myUsage, KeyUsage.nonRepudiation)) {
            myResult.addUse(GordianKeyPairUse.NONREPUDIATION);
        }

        /* Check for keyAgreement. */
        if (checkUsage(myUsage, KeyUsage.keyAgreement)) {
            myResult.addUse(GordianKeyPairUse.AGREEMENT);
        }

        /* Check for keyEncryption. */
        if (checkUsage(myUsage, KeyUsage.keyEncipherment)) {
            myResult.addUse(GordianKeyPairUse.KEYENCRYPT);
        }

        /* Check for dataEncryption. */
        if (checkUsage(myUsage, KeyUsage.dataEncipherment)) {
            myResult.addUse(GordianKeyPairUse.DATAENCRYPT);
        }

        /* Check for encipherOnly. */
        if (checkUsage(myUsage, KeyUsage.encipherOnly)) {
            myResult.addUse(GordianKeyPairUse.ENCRYPTONLY);
        }

        /* Check for decipherOnly. */
        if (checkUsage(myUsage, KeyUsage.decipherOnly)) {
            myResult.addUse(GordianKeyPairUse.DECRYPTONLY);
        }

        /* Return the result */
        return myResult;
    }

    /**
     * Determine subjectId.
     *
     * @param pCertificate the certificate.
     * @return the id
     */
    static byte[] determineSubjectId(final TBSCertificate pCertificate) {
        /* Access details */
        final Extensions myExtensions = pCertificate.getExtensions();
        final Extension mySubjectId = myExtensions.getExtension(Extension.subjectKeyIdentifier);
        if (mySubjectId != null) {
            return mySubjectId.getExtnValue().getOctets();
        }
        final ASN1BitString myId = pCertificate.getSubjectUniqueId();
        return myId == null ? null : myId.getOctets();
    }

    /**
     * Determine issuerId.
     *
     * @param pCertificate the certificate.
     * @return the id
     */
    static byte[] determineIssuerId(final TBSCertificate pCertificate) {
        /* Access details */
        final Extensions myExtensions = pCertificate.getExtensions();
        final Extension myIssuerId = myExtensions.getExtension(Extension.authorityKeyIdentifier);
        if (myIssuerId != null) {
            return myIssuerId.getExtnValue().getOctets();
        }
        final ASN1BitString myId = pCertificate.getIssuerUniqueId();
        return myId == null ? null : myId.getOctets();
    }

    /**
     * Check for usage.
     *
     * @param pUsage    the usage control
     * @param pRequired the required usage
     * @return true/false
     */
    private static boolean checkUsage(final KeyUsage pUsage,
                                      final int pRequired) {
        return pUsage == null || pUsage.hasUsages(pRequired);
    }

    /**
     * Create extensions for tbsCertificate.
     *
     * @param pCAStatus  the CA status
     * @param pUsage     the keyPair usage
     * @param pSubjectId the subjectId
     * @param pIssuerId  the issuerId (or null)
     * @return the extensions
     * @throws GordianException on error
     */
    static Extensions createExtensions(final GordianCAStatus pCAStatus,
                                       final GordianKeyPairUsage pUsage,
                                       final byte[] pSubjectId,
                                       final byte[] pIssuerId) throws GordianException {
        /* Protect against exceptions */
        try {
            /* Create extensions for the certificate */
            final ExtensionsGenerator myGenerator = new ExtensionsGenerator();
            myGenerator.addExtension(Extension.keyUsage, true, pUsage.getKeyUsage());
            myGenerator.addExtension(Extension.subjectKeyIdentifier, true, pSubjectId);
            if (pIssuerId != null) {
                myGenerator.addExtension(Extension.authorityKeyIdentifier, true, pIssuerId);
            }
            pCAStatus.createExtensions(myGenerator);
            return myGenerator.generate();

        } catch (IOException e) {
            throw new GordianIOException("Failed to create extensions", e);
        }
    }

    /**
     * Create extensions.
     *
     * @param pUsage the usage
     * @return the extensions
     * @throws GordianException on error
     */
    static Extensions createExtensions(final GordianKeyPairUsage pUsage) throws GordianException {
        /* Protect against exceptions */
        try {
            final ExtensionsGenerator myGenerator = new ExtensionsGenerator();
            myGenerator.addExtension(Extension.keyUsage, true, pUsage.getKeyUsage());
            return myGenerator.generate();
        } catch (IOException e) {
            throw new GordianIOException("Failed to create extensions", e);
        }
    }

    /**
     * Create the keyId.
     *
     * @param pFactory          the factory
     * @param pEncodedPublicKey the publicKey
     * @return the keyId
     * @throws GordianException on error
     */
    static byte[] createKeyId(final GordianFactory pFactory,
                              final byte[] pEncodedPublicKey) throws GordianException {
        /* Build the hash */
        final GordianDigestSpec mySpec = GordianDigestSpecBuilder.sha3(GordianLength.LEN_256);
        final GordianDigestFactory myDigests = pFactory.getDigestFactory();
        final GordianDigest myDigest = myDigests.createDigest(mySpec);
        myDigest.update(pEncodedPublicKey);

        /* Create the keyId */
        return myDigest.finish();
    }
}
