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

import io.github.tonywasher.joceanus.gordianknot.api.cert.GordianCertPurpose;
import io.github.tonywasher.joceanus.gordianknot.api.cert.GordianCertUsage;
import io.github.tonywasher.joceanus.gordianknot.api.cert.GordianCertUse;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.KeyUsage;

import java.util.EnumSet;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Certificate Usage implementation.
 */
public class GordianCoreCertUsage
        implements GordianCertUsage {
    /**
     * The Purpose Map.
     */
    private static final Map<KeyPurposeId, GordianCertPurpose> PURPOSE_MAP = buildPurposeMap();

    /**
     * The Usage set.
     */
    private final EnumSet<GordianCertUse> theUsageSet;

    /**
     * The Usage set.
     */
    private final EnumSet<GordianCertPurpose> thePurposeSet;

    /**
     * Constructor.
     */
    public GordianCoreCertUsage() {
        theUsageSet = EnumSet.noneOf(GordianCertUse.class);
        thePurposeSet = EnumSet.noneOf(GordianCertPurpose.class);
    }

    @Override
    public boolean hasUse(final GordianCertUse pUse) {
        return theUsageSet.contains(pUse);
    }

    @Override
    public Set<GordianCertUse> getUsageSet() {
        return EnumSet.copyOf(theUsageSet);
    }

    @Override
    public boolean hasPurpose(final GordianCertPurpose pPurpose) {
        return thePurposeSet.contains(pPurpose);
    }

    @Override
    public Set<GordianCertPurpose> getPurposeSet() {
        return EnumSet.copyOf(thePurposeSet);
    }

    @Override
    public GordianCoreCertUsage withUse(final GordianCertUse pUse) {
        theUsageSet.add(pUse);
        return this;
    }

    @Override
    public GordianCoreCertUsage withUses(final GordianCertUse... pUses) {
        theUsageSet.addAll(Set.of(pUses));
        return this;
    }

    @Override
    public void removeUse(final GordianCertUse pUse) {
        theUsageSet.remove(pUse);
    }

    @Override
    public GordianCoreCertUsage withPurpose(final GordianCertPurpose pPurpose) {
        thePurposeSet.add(pPurpose);
        return this;
    }

    @Override
    public GordianCoreCertUsage withPurposes(final GordianCertPurpose... pPurposes) {
        thePurposeSet.addAll(Set.of(pPurposes));
        return this;
    }

    @Override
    public void removePurpose(final GordianCertPurpose pPurpose) {
        thePurposeSet.remove(pPurpose);
    }

    /**
     * Do we have purposes?
     *
     * @return true/false
     */
    public boolean hasPurposes() {
        return !thePurposeSet.isEmpty();
    }

    /**
     * Determine usage from Extensions.
     *
     * @param pExtensions the extensions.
     * @return the usage
     */
    public static GordianCoreCertUsage fromExtensions(final Extensions pExtensions) {
        /* Create a new usage */
        final GordianCoreCertUsage myUsage = new GordianCoreCertUsage();

        /* Determine the keyPair Usage */
        final KeyUsage myKeyUsage = KeyUsage.fromExtensions(pExtensions);
        for (GordianCertUse myUse : GordianCertUse.values()) {
            if (myKeyUsage.hasUsages(determineUsage(myUse))) {
                myUsage.withUse(myUse);
            }
        }

        /* Restrict Certificate signing to CA */
        final BasicConstraints myConstraint = BasicConstraints.fromExtensions(pExtensions);
        final boolean isCA = myConstraint != null && myConstraint.isCA();
        if (!isCA && myUsage.hasUse(GordianCertUse.CERTIFICATE)) {
            myUsage.removeUse(GordianCertUse.CERTIFICATE);
        }

        /* Determine the keyPair Purpose */
        final ASN1Sequence myPurposes = ASN1Sequence.getInstance(Extensions.getExtensionValue(pExtensions, Extension.extendedKeyUsage));
        if (myPurposes != null) {
            final Enumeration<?> enPurp = myPurposes.getObjects();
            while (enPurp.hasMoreElements()) {
                final KeyPurposeId myId = KeyPurposeId.getInstance(enPurp.nextElement());
                final GordianCertPurpose myPurpose = getPurposeforOID(myId);
                myUsage.withPurpose(myPurpose);
            }
        }

        /* Return the usage */
        return myUsage;
    }

    /**
     * Obtain the keyPair Usage.
     *
     * @return the keyPair Purpose
     */
    public KeyUsage getKeyPairUsage() {
        int myUsage = 0;
        for (GordianCertUse myUse : theUsageSet) {
            myUsage |= determineUsage(myUse);
        }
        return new KeyUsage(myUsage);
    }

    /**
     * Obtain the keyPair Purpose.
     *
     * @return the keyPair Purpose
     */
    public ASN1Sequence getKeyPairPurpose() {
        /* Create the vector */
        final ASN1EncodableVector v = new ASN1EncodableVector();
        for (GordianCertPurpose myPurpose : thePurposeSet) {
            v.add(determinePurposeId(myPurpose));
        }
        return new DERSequence(v);
    }

    /**
     * Build the keyPair purposeMap.
     *
     * @return the Map
     */
    private static Map<KeyPurposeId, GordianCertPurpose> buildPurposeMap() {
        final Map<KeyPurposeId, GordianCertPurpose> myMap = new HashMap<>();
        for (GordianCertPurpose myPurpose : GordianCertPurpose.values()) {
            final KeyPurposeId myId = determinePurposeId(myPurpose);
            myMap.put(myId, myPurpose);
        }
        return myMap;
    }

    /**
     * Obtain the purpose for the OID.
     *
     * @param pOID the oid
     * @return the purpose or null
     */
    private static GordianCertPurpose getPurposeforOID(final KeyPurposeId pOID) {
        return PURPOSE_MAP.get(pOID);
    }

    /**
     * Obtain the Usage for the use id.
     *
     * @param pUse the use
     * @return the Usage
     */
    private static int determineUsage(final GordianCertUse pUse) {
        return switch (pUse) {
            case CERTIFICATE -> KeyUsage.keyCertSign;
            case SIGNATURE -> KeyUsage.digitalSignature;
            case NONREPUDIATION -> KeyUsage.nonRepudiation;
            case CRLSIGN -> KeyUsage.cRLSign;
            case KEYENCRYPT -> KeyUsage.keyEncipherment;
            case DATAENCRYPT -> KeyUsage.dataEncipherment;
            case AGREEMENT -> KeyUsage.keyAgreement;
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
    private static KeyPurposeId determinePurposeId(final GordianCertPurpose pPurpose) {
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
}
