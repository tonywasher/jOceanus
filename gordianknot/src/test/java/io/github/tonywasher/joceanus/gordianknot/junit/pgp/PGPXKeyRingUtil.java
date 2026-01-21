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

package io.github.tonywasher.joceanus.gordianknot.junit.pgp;

import org.bouncycastle.bcpg.ArmoredInputStream;
import org.bouncycastle.bcpg.sig.IssuerFingerprint;
import org.bouncycastle.bcpg.sig.KeyFlags;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPSignature;
import org.bouncycastle.openpgp.PGPSignatureSubpacketVector;
import org.bouncycastle.openpgp.bc.BcPGPPublicKeyRing;
import org.bouncycastle.openpgp.bc.BcPGPSecretKeyRing;
import org.bouncycastle.openpgp.operator.bc.BcPGPContentVerifierBuilderProvider;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;

/**
 * PGP keyRing Utilities.
 */
public class PGPXKeyRingUtil {
    /* Source files location */
    private static final String HOME = System.getProperty("user.home");
    static final String FILEDIR = HOME + "/PGPTest/";

    /* Buffer Size */
    static final int BUFFER_SIZE = 1 << 16;

    /* keyRing suffii */
    static final String PUBLIC_SFX = ".pub.asc";
    static final String SECRET_SFX = ".sec.asc";

    /**
     * Load publicKeyRing.
     *
     * @param pRing the ringType
     * @return the ring
     */
    static BcPGPPublicKeyRing loadPublicKeyRing(final PGPXKeyRing pRing) throws IOException, PGPException {
        try (InputStream myInput = new FileInputStream(PGPXKeyRingUtil.FILEDIR + pRing.obtainFilename() + PUBLIC_SFX);
             BufferedInputStream myBuffered = new BufferedInputStream(myInput);
             ArmoredInputStream myArmored = new ArmoredInputStream(myBuffered)) {
            final BcPGPPublicKeyRing myRing = new BcPGPPublicKeyRing(myArmored);
            checkPublicKeys(myRing.getPublicKey(), myRing.getPublicKeys());
            return myRing;
        }
    }

    /**
     * Load secretKeyRing.
     *
     * @param pRing the ringType
     * @return the ring
     */
    static BcPGPSecretKeyRing loadSecretKeyRing(final PGPXKeyRing pRing) throws IOException, PGPException {
        try (InputStream myInput = new FileInputStream(PGPXKeyRingUtil.FILEDIR + pRing.obtainFilename() + SECRET_SFX);
             BufferedInputStream myBuffered = new BufferedInputStream(myInput);
             ArmoredInputStream myArmored = new ArmoredInputStream(myBuffered)) {
            final BcPGPSecretKeyRing myRing = new BcPGPSecretKeyRing(myArmored);
            checkPublicKeys(myRing.getPublicKey(), myRing.getPublicKeys());
            return myRing;
        }
    }

    /**
     * Check public keys.
     *
     * @param pSigner the signing key
     * @param pKeys   the keys
     */
    static void checkPublicKeys(final PGPPublicKey pSigner,
                                final Iterator<PGPPublicKey> pKeys) throws PGPException {
        /* Check the master key */
        checkMasterKey(pSigner);

        /* Loop through the keys */
        while (pKeys.hasNext()) {
            final PGPPublicKey myKey = pKeys.next();
            if (!myKey.isMasterKey()) {
                checkSubKey(pSigner, myKey);
            }
        }
    }

    /**
     * Check master key.
     *
     * @param pMaster the masterKey
     */
    static void checkMasterKey(final PGPPublicKey pMaster) throws PGPException {
        /* Loop through the userIds */
        Iterator<String> myUserITer = pMaster.getUserIDs();
        final BcPGPContentVerifierBuilderProvider myProvider = new BcPGPContentVerifierBuilderProvider();
        while (myUserITer.hasNext()) {
            /* Access the signatures for this ID and loop through them */
            String myID = myUserITer.next();
            Iterator<PGPSignature> mySigns = pMaster.getSignaturesForID(myID);
            while (mySigns.hasNext()) {
                PGPSignature myS = mySigns.next();

                /* Ignore anything that is not a Positive Certification/Revocation */
                final int mySigType = myS.getSignatureType();
                if (mySigType != PGPSignature.POSITIVE_CERTIFICATION
                        && mySigType != PGPSignature.CERTIFICATION_REVOCATION) {
                    continue;
                }

                /* Check the issuer */
                checkIssuer(pMaster, myS);

                /* Check the certification */
                myS.init(myProvider, pMaster);
                if (!myS.verifyCertification(myID, pMaster)) {
                    throw new IllegalStateException("Invalid self-certification");
                }
            }
        }

        /* Loop through the keySignatures */
        Iterator<PGPSignature> mySigns = pMaster.getKeySignatures();
        while (mySigns.hasNext()) {
            PGPSignature myS = mySigns.next();

            /* Ignore anything that is not a DirectKey/Revocation */
            final int mySigType = myS.getSignatureType();
            if (mySigType != PGPSignature.DIRECT_KEY
                    && mySigType != PGPSignature.KEY_REVOCATION) {
                continue;
            }

            /* Ignore if not self-signed */
            if (myS.getKeyID() != pMaster.getKeyID()) {
                continue;
            }

            /* Check the issuer */
            checkIssuer(pMaster, myS);

            /* Check the certification */
            myS.init(myProvider, pMaster);
            if (!myS.verifyCertification(pMaster)) {
                throw new IllegalStateException("Invalid self-certification");
            }
        }
    }

    /**
     * Check subKey.
     *
     * @param pMaster the masterKey
     * @param pSubKey the subKey
     */
    static void checkSubKey(final PGPPublicKey pMaster,
                            final PGPPublicKey pSubKey) throws PGPException {
        /* Loop through the userIds */
        final BcPGPContentVerifierBuilderProvider myProvider = new BcPGPContentVerifierBuilderProvider();

        /* Access the signatures for this ID and loop through them */
        Iterator<PGPSignature> mySigns = pSubKey.getSignaturesForKeyID(pMaster.getKeyID());
        while (mySigns.hasNext()) {
            PGPSignature myS = mySigns.next();

            /* Ignore anything that is not a SubKey Binding/Revocation/DirectKey/Revocation */
            final int mySigType = myS.getSignatureType();
            if (mySigType != PGPSignature.SUBKEY_BINDING
                    && mySigType != PGPSignature.SUBKEY_REVOCATION
                    && myS.getSignatureType() != PGPSignature.DIRECT_KEY
                    && myS.getSignatureType() != PGPSignature.CERTIFICATION_REVOCATION) {
                continue;
            }

            /* Check the issuer */
            checkIssuer(pMaster, myS);

            /* Check the subKey binding */
            myS.init(myProvider, pMaster);
            if (!myS.verifyCertification(pMaster, pSubKey)) {
                throw new IllegalStateException("Invalid subKey binding");
            }

            /* If this is a signing subKey */
            if ((myS.getHashedSubPackets().getKeyFlags()
                    & KeyFlags.CERTIFY_OTHER + KeyFlags.SIGN_DATA) != 0) {
                /* Loop checkint the embedded signatures for primaryKey binding */
                for (PGPSignature myE : myS.getHashedSubPackets().getEmbeddedSignatures()) {
                    /* Ignore anything that is not a PrimaryKey Binding */
                    if (myE.getSignatureType() != PGPSignature.PRIMARYKEY_BINDING) {
                        continue;
                    }

                    /* Check the issuer */
                    checkIssuer(pSubKey, myE);

                    /* Check the primaryKey binding */
                    myE.init(myProvider, pSubKey);
                    if (!myE.verifyCertification(pMaster, pSubKey)) {
                        throw new IllegalStateException("Invalid primaryKey binding");
                    }
                }
            }
        }
    }

    /**
     * Check signature issuer ids.
     *
     * @param pSigner    the signer
     * @param pSignature the signature
     */
    static void checkIssuer(final PGPPublicKey pSigner,
                            final PGPSignature pSignature) {
        /* Check that the keyId and fingerprint match */
        long myKeyId = pSignature.getUnhashedSubPackets().getIssuerKeyID();
        if (myKeyId != 0 && myKeyId != pSigner.getKeyID()) {
            throw new IllegalStateException("Invalid issuerKeyId");
        }
        IssuerFingerprint myFingerPrint = pSignature.getHashedSubPackets().getIssuerFingerprint();
        if (myFingerPrint != null
                && !Arrays.equals(myFingerPrint.getFingerprint(), pSigner.getFingerprint())) {
            throw new IllegalStateException("Invalid issuerFingerPrint");
        }
    }

    /**
     * Obtain the signature signed by the specified key.
     *
     * @param pKey   the publicKey
     * @param pKeyId the keyId
     * @return the signature
     */
    static PGPSignature obtainKeyIdSignature(final PGPPublicKey pKey,
                                             final long pKeyId) {
        /* Loop through signatures */
        for (Iterator<PGPSignature> sit = pKey.getSignatures(); sit.hasNext(); ) {
            final PGPSignature sig = sit.next();

            /* Check that this is the required signature */
            final PGPSignatureSubpacketVector v = sig.getUnhashedSubPackets();
            if (v != null && v.getIssuerKeyID() == pKeyId
                    && checkSigValidity(sig)) {
                return sig;
            }
        }

        /* No valid signature */
        throw new IllegalArgumentException("Can't find signature for keyId.");
    }

    /**
     * Check key validity.
     *
     * @param pKey the publicKey
     * @param pSig the signature
     * @return valid true/false
     */
    static boolean checkKeyValidity(final PGPPublicKey pKey,
                                    final PGPSignature pSig) {
        /* Access detail */
        final PGPSignatureSubpacketVector v = pSig.getHashedSubPackets();
        final Date myCreate = pKey.getCreationTime();
        final LocalDateTime myExpireTime = myCreate.toInstant().atZone(ZoneId.of("UTC")).toLocalDateTime();
        final long myExpire = v.getKeyExpirationTime();
        return myExpire == 0 || myExpireTime.plusSeconds(myExpire).isAfter(LocalDateTime.now());
    }

    /**
     * Check signature validity.
     *
     * @param pSig the signature
     * @return valid true/false
     */
    private static boolean checkSigValidity(final PGPSignature pSig) {
        /* Access detail */
        final PGPSignatureSubpacketVector v = pSig.getHashedSubPackets();
        final Date myCreate = v.getSignatureCreationTime();
        final LocalDateTime myExpireTime = myCreate.toInstant().atZone(ZoneId.of("UTC")).toLocalDateTime();
        final long myExpire = v.getSignatureExpirationTime();
        return myExpire == 0 || myExpireTime.plusSeconds(myExpire).isAfter(LocalDateTime.now());
    }
}
