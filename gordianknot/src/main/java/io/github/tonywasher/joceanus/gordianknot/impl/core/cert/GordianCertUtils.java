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

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;
import io.github.tonywasher.joceanus.gordianknot.api.cert.GordianKeyPairPurpose;
import io.github.tonywasher.joceanus.gordianknot.api.cert.GordianKeyPairUsage;
import io.github.tonywasher.joceanus.gordianknot.api.cert.GordianKeyPairUse;
import io.github.tonywasher.joceanus.gordianknot.api.digest.GordianDigest;
import io.github.tonywasher.joceanus.gordianknot.api.digest.GordianDigestFactory;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianDigestSpec;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianDigestSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.exc.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.exc.GordianIOException;
import io.github.tonywasher.joceanus.gordianknot.api.factory.GordianFactory;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;
import org.bouncycastle.asn1.x509.KeyPurposeId;
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
        return GordianCoreKeyPairUsage.fromExtensions(pExtensions);
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
            final GordianCoreKeyPairUsage myUsage = (GordianCoreKeyPairUsage) pUsage;
            myGenerator.addExtension(Extension.keyUsage, true, myUsage.getKeyPairUsage());
            myGenerator.addExtension(Extension.subjectKeyIdentifier, true, pSubjectId);
            if (pIssuerId != null) {
                myGenerator.addExtension(Extension.authorityKeyIdentifier, true, pIssuerId);
            }
            if (myUsage.hasPurposes()) {
                myGenerator.addExtension(Extension.extendedKeyUsage, false, myUsage.getKeyPairPurpose());
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
            final GordianCoreKeyPairUsage myUsage = (GordianCoreKeyPairUsage) pUsage;
            myGenerator.addExtension(Extension.keyUsage, true, myUsage.getKeyPairUsage());
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
        final GordianDigestFactory myDigests = pFactory.getDigestFactory();
        final GordianDigestSpecBuilder myBuilder = myDigests.newDigestSpecBuilder();
        final GordianDigestSpec mySpec = myBuilder.sha3(GordianLength.LEN_256);
        final GordianDigest myDigest = myDigests.createDigest(mySpec);
        myDigest.update(pEncodedPublicKey);

        /* Create the keyId */
        return myDigest.finish();
    }

    /**
     * Obtain the ID for the usage id.
     *
     * @param pUsage the purpose
     * @return the id
     */
    private int getOIDforUsage(final GordianKeyPairUse pUsage) {
        return switch (pUsage) {
            case CERTIFICATE -> KeyUsage.keyCertSign;
            case CRLSIGN -> KeyUsage.cRLSign;
            case SIGNATURE -> KeyUsage.digitalSignature;
            case NONREPUDIATION -> KeyUsage.nonRepudiation;
            case AGREEMENT -> KeyUsage.keyAgreement;
            case KEYENCRYPT -> KeyUsage.keyEncipherment;
            case DATAENCRYPT -> KeyUsage.dataEncipherment;
            case ENCRYPTONLY -> KeyUsage.encipherOnly;
            case DECRYPTONLY -> KeyUsage.decipherOnly;
        };
    }

    /**
     * Obtain the OID for the purpose id.
     *
     * @param pPurpose the purpose
     * @return the OID
     */
    private KeyPurposeId getOIDforPurpose(final GordianKeyPairPurpose pPurpose) {
        return switch (pPurpose) {
            case SERVERAUTH -> KeyPurposeId.id_kp_serverAuth;
            case CLIENTAUTH -> KeyPurposeId.id_kp_clientAuth;
            case CODESIGN -> KeyPurposeId.id_kp_codeSigning;
            case EMAILPROTECT -> KeyPurposeId.id_kp_emailProtection;
            case TIMESTAMP -> KeyPurposeId.id_kp_timeStamping;
            case OCSPSIGN -> KeyPurposeId.id_kp_OCSPSigning;
            case DVCS -> KeyPurposeId.id_kp_dvcs;
            case SBGPCERT -> KeyPurposeId.id_kp_sbgpCertAAServerAuth;
            case SCVPRESPONDER -> KeyPurposeId.id_kp_scvp_responder;
            case EAPOVERPPP -> KeyPurposeId.id_kp_eapOverPPP;
            case EAPOVERLAN -> KeyPurposeId.id_kp_eapOverLAN;
            case SCVPSERVER -> KeyPurposeId.id_kp_scvpServer;
            case SCVPCLIENT -> KeyPurposeId.id_kp_scvpClient;
            case IPSECIKE -> KeyPurposeId.id_kp_ipsecIKE;
            case SECURESHELLCLIENT -> KeyPurposeId.id_kp_secureShellClient;
            case SECURESHELLSERVER -> KeyPurposeId.id_kp_secureShellServer;
            case CAPWAPAC -> KeyPurposeId.id_kp_capwapAC;
            case CAPWAPWTP -> KeyPurposeId.id_kp_capwapWTP;
            case CMCRA -> KeyPurposeId.id_kp_cmcRA;
            case CMCARCHIVE -> KeyPurposeId.id_kp_cmcArchive;
            case CMCCA -> KeyPurposeId.id_kp_cmcCA;
            case CMKGA -> KeyPurposeId.id_kp_cmKGA;
            case BUNDLESECURITY -> KeyPurposeId.id_kp_bundleSecurity;
            case DOCSIGN -> KeyPurposeId.id_kp_documentSigning;
            case JWT -> KeyPurposeId.id_kp_jwt;
            case HTTPCONTENT -> KeyPurposeId.id_kp_httpContentEncrypt;
            case OAUTHACCESSTOKEN -> KeyPurposeId.id_kp_oauthAccessTokenSigning;
            case IMURI -> KeyPurposeId.id_kp_imUri;
            case CONFIGSIGN -> KeyPurposeId.id_kp_configSigning;
            case TRUSTANCHORCONFIGSIGN -> KeyPurposeId.id_kp_trustAnchorConfigSigning;
            case UPDATEPACKAGE -> KeyPurposeId.id_kp_updatePackageSigning;
            case SAFETYCOMMS -> KeyPurposeId.id_kp_safetyCommunication;
        };
    }

    /**
     * Obtain the purpose for the OID.
     *
     * @param pOID the oid
     * @return the purpose or null
     */
    private GordianKeyPairPurpose getOIDforPurpose(final KeyPurposeId pOID) {
        for (GordianKeyPairPurpose myPurpose : GordianKeyPairPurpose.values()) {
            if (getOIDforPurpose(myPurpose).equals(pOID)) {
                return myPurpose;
            }
        }
        return null;
    }
}
