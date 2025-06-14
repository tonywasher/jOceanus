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
package net.sourceforge.joceanus.gordianknot.impl.core.keypair;

import net.sourceforge.joceanus.gordianknot.api.agree.GordianAgreementFactory;
import net.sourceforge.joceanus.gordianknot.api.agree.GordianAgreementSpec;
import net.sourceforge.joceanus.gordianknot.api.agree.GordianAgreementSpecBuilder;
import net.sourceforge.joceanus.gordianknot.api.agree.GordianAnonymousAgreement;
import net.sourceforge.joceanus.gordianknot.api.agree.GordianKDFType;
import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.gordianknot.api.digest.GordianDigestSpecBuilder;
import net.sourceforge.joceanus.gordianknot.api.encrypt.GordianEncryptor;
import net.sourceforge.joceanus.gordianknot.api.encrypt.GordianEncryptorFactory;
import net.sourceforge.joceanus.gordianknot.api.encrypt.GordianEncryptorSpec;
import net.sourceforge.joceanus.gordianknot.api.encrypt.GordianEncryptorSpecBuilder;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairSpec;
import net.sourceforge.joceanus.gordianknot.api.sign.GordianSignParams;
import net.sourceforge.joceanus.gordianknot.api.sign.GordianSignature;
import net.sourceforge.joceanus.gordianknot.api.sign.GordianSignatureFactory;
import net.sourceforge.joceanus.gordianknot.api.sign.GordianSignatureSpec;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianDataException;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianLogicException;

import java.util.Arrays;

/**
 * KeyPair validity checking.
 */
public final class GordianKeyPairValidity {
    /**
     * Error message.
     */
    private static final String ERRORMSG = "Mismatch on public/private key";

    /**
     * Private constructor.
     */
    private GordianKeyPairValidity() {
    }

    /**
     * Check keyPair validity.
     * @param pFactory the factory
     * @param pKeyPair the keyPair
     * @throws GordianException on error
     */
    public static void checkValidity(final GordianCoreFactory pFactory,
                                     final GordianKeyPair pKeyPair) throws GordianException {
        final Object myCheck = getValidityCheck(pFactory, pKeyPair);
        if (myCheck instanceof GordianSignatureSpec mySpec) {
            checkValidity(pFactory, pKeyPair, mySpec);
        } else if (myCheck instanceof GordianEncryptorSpec mySpec) {
            checkValidity(pFactory, pKeyPair, mySpec);
        } else if (myCheck instanceof GordianAgreementSpec mySpec) {
            checkValidity(pFactory, pKeyPair, mySpec);
        } else {
            throw new GordianLogicException("Unexpected keyPairType");
        }
    }

    /**
     * Check keyPair validity via signature.
     * @param pFactory the factory
     * @param pKeyPair the keyPair
     * @param pSignSpec the signature spec
     * @throws GordianException on error
     */
    private static void checkValidity(final GordianCoreFactory pFactory,
                                      final GordianKeyPair pKeyPair,
                                      final GordianSignatureSpec pSignSpec) throws GordianException {
        /* Use default personalisation as the data to sign */
        final byte[] myData = pFactory.getRandomSource().defaultPersonalisation();

        /* Create signer */
        final GordianSignatureFactory mySigns = pFactory.getKeyPairFactory().getSignatureFactory();
        final GordianSignature mySigner = mySigns.createSigner(pSignSpec);

        /* Create signature */
        final GordianSignParams myParams = GordianSignParams.keyPair(pKeyPair);
        mySigner.initForSigning(myParams);
        mySigner.update(myData);
        final byte[] mySignature = mySigner.sign();

        /* Validate signature */
        mySigner.initForVerify(myParams);
        mySigner.update(myData);
        if (!mySigner.verify(mySignature)) {
            throw new GordianDataException(ERRORMSG + " " + pSignSpec);
        }
    }

    /**
     * Check keyPair validity via encryption.
     * @param pFactory the factory
     * @param pKeyPair the keyPair
     * @param pEncryptSpec the encryption spec
     * @throws GordianException on error
     */
    private static void checkValidity(final GordianCoreFactory pFactory,
                                      final GordianKeyPair pKeyPair,
                                      final GordianEncryptorSpec pEncryptSpec) throws GordianException {
        /* Use default personalisation as the data to encrypt */
        final byte[] myData = pFactory.getRandomSource().defaultPersonalisation();

        /* Create encryptor */
        final GordianEncryptorFactory myEncrypts = pFactory.getKeyPairFactory().getEncryptorFactory();
        final GordianEncryptor myEncryptor = myEncrypts.createEncryptor(pEncryptSpec);

        /* Encrypt data */
        myEncryptor.initForEncrypt(pKeyPair);
        final byte[] myEncrypted = myEncryptor.encrypt(myData);

        /* decrypt encrypted data */
        myEncryptor.initForDecrypt(pKeyPair);
        final byte[] myResult = myEncryptor.decrypt(myEncrypted);

        /* Check that we have arrived back at the original data */
        if (!Arrays.equals(myData, myResult)) {
            throw new GordianDataException(ERRORMSG + " " + pEncryptSpec);
        }
    }

    /**
     * Check keyPair validity via agreement.
     * @param pFactory the factory
     * @param pKeyPair the keyPair
     * @param pAgreeSpec the agreementSpec
     * @throws GordianException on error
     */
    private static void checkValidity(final GordianCoreFactory pFactory,
                                      final GordianKeyPair pKeyPair,
                                      final GordianAgreementSpec pAgreeSpec) throws GordianException {
        /* Create agreement on client side */
        final GordianAgreementFactory myAgrees = pFactory.getKeyPairFactory().getAgreementFactory();
        GordianAnonymousAgreement myAgreement
                = (GordianAnonymousAgreement) myAgrees.createAgreement(pAgreeSpec);
        final byte[] myHello = myAgreement.createClientHello(pKeyPair);
        final byte[] myClient = (byte[]) myAgreement.getResult();

        /* Accept agreement on server side */
        /* We have to use a new agreement due to bug in JCA NewHope support */
        myAgreement = (GordianAnonymousAgreement) myAgrees.createAgreement(pAgreeSpec);
        myAgreement.acceptClientHello(pKeyPair, myHello);
        final byte[] myServer = (byte[]) myAgreement.getResult();

        /* Check that we have the same result at either end */
        if (!Arrays.equals(myClient, myServer)) {
            throw new GordianDataException(ERRORMSG + " " + pAgreeSpec);
        }
    }

    /**
     * Obtain validity check for keyPair.
     * @param pFactory the factory
     * @param pKeyPair the keyPair
     * @return the validity check
     */
    private static Object getValidityCheck(final GordianCoreFactory pFactory,
                                           final GordianKeyPair pKeyPair) {
        /* Switch on keyType */
        final GordianKeyPairSpec mySpec = pKeyPair.getKeyPairSpec();
        switch (mySpec.getKeyPairType()) {
            case RSA:
            case DSA:
            case EDDSA:
            case EC:
            case GOST2012:
            case DSTU4145:
            case SM2:
            case SLHDSA:
            case MLDSA:
            case FALCON:
            case MAYO:
            case SNOVA:
            case PICNIC:
            case XMSS:
            case LMS:
                return pFactory.getKeyPairFactory().getSignatureFactory().defaultForKeyPair(mySpec);
            case ELGAMAL:
                return GordianEncryptorSpecBuilder.elGamal(GordianDigestSpecBuilder.sha2(GordianLength.LEN_512));
            case DH:
                return GordianAgreementSpecBuilder.anon(mySpec, GordianKDFType.SHA256KDF);
            case XDH:
                return mySpec.getEdwardsElliptic().is25519()
                        ? GordianAgreementSpecBuilder.anon(mySpec, GordianKDFType.SHA256KDF)
                        : GordianAgreementSpecBuilder.anon(mySpec, GordianKDFType.SHA512KDF);
            case NEWHOPE:
            case CMCE:
            case FRODO:
            case SABER:
            case MLKEM:
            case HQC:
            case BIKE:
            case NTRU:
            case NTRUPRIME:
                return GordianAgreementSpecBuilder.kem(mySpec, GordianKDFType.NONE);
            default:
                return null;
        }
    }
}
