/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2021 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.impl.core.keypair;

import java.util.Arrays;

import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAgreementFactory;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianKDFType;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianKeyPairAgreementSpec;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianKeyPairAnonymousAgreement;
import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestSpec;
import net.sourceforge.joceanus.jgordianknot.api.encrypt.GordianEncryptorFactory;
import net.sourceforge.joceanus.jgordianknot.api.encrypt.GordianEncryptorSpec;
import net.sourceforge.joceanus.jgordianknot.api.encrypt.GordianKeyPairEncryptor;
import net.sourceforge.joceanus.jgordianknot.api.encrypt.GordianMcElieceEncryptionType;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairSpec;
import net.sourceforge.joceanus.jgordianknot.api.sign.GordianKeyPairSignature;
import net.sourceforge.joceanus.jgordianknot.api.sign.GordianSignatureFactory;
import net.sourceforge.joceanus.jgordianknot.api.sign.GordianSignatureSpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianLogicException;
import net.sourceforge.joceanus.jtethys.OceanusException;

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
     * @throws OceanusException on error
     */
    public static void checkValidity(final GordianCoreFactory pFactory,
                                     final GordianKeyPair pKeyPair) throws OceanusException {
        final Object myCheck = getValidityCheck(pKeyPair);
        if (myCheck instanceof GordianSignatureSpec) {
            checkValidity(pFactory, pKeyPair, (GordianSignatureSpec) myCheck);
        } else if (myCheck instanceof GordianEncryptorSpec) {
            checkValidity(pFactory, pKeyPair, (GordianEncryptorSpec) myCheck);
        } else if (myCheck instanceof GordianKeyPairAgreementSpec) {
            checkValidity(pFactory, pKeyPair, (GordianKeyPairAgreementSpec) myCheck);
        } else {
            throw new GordianLogicException("Unexpected keyPairType");
        }
    }

    /**
     * Check keyPair validity via signature.
     * @param pFactory the factory
     * @param pKeyPair the keyPair
     * @param pSignSpec the signature spec
     * @throws OceanusException on error
     */
    private static void checkValidity(final GordianCoreFactory pFactory,
                                      final GordianKeyPair pKeyPair,
                                      final GordianSignatureSpec pSignSpec) throws OceanusException {
        /* Use default personalisation as the data to sign */
        final byte[] myData = pFactory.getRandomSource().defaultPersonalisation();

        /* Create signer */
        final GordianSignatureFactory mySigns = pFactory.getKeyPairFactory().getSignatureFactory();
        final GordianKeyPairSignature mySigner = mySigns.createKeyPairSigner(pSignSpec);

        /* Create signature */
        mySigner.initForSigning(pKeyPair);
        mySigner.update(myData);
        final byte[] mySignature = mySigner.sign();

        /* Validate signature */
        mySigner.initForVerify(pKeyPair);
        mySigner.update(myData);
        if (!mySigner.verify(mySignature)) {
            throw new GordianDataException(ERRORMSG);
        }
    }

    /**
     * Check keyPair validity via encryption.
     * @param pFactory the factory
     * @param pKeyPair the keyPair
     * @param pEncryptSpec the encryption spec
     * @throws OceanusException on error
     */
    private static void checkValidity(final GordianCoreFactory pFactory,
                                      final GordianKeyPair pKeyPair,
                                      final GordianEncryptorSpec pEncryptSpec) throws OceanusException {
        /* Use default personalisation as the data to encrypt */
        final byte[] myData = pFactory.getRandomSource().defaultPersonalisation();

        /* Create encryptor */
        final GordianEncryptorFactory myEncrypts = pFactory.getKeyPairFactory().getEncryptorFactory();
        final GordianKeyPairEncryptor myEncryptor = myEncrypts.createKeyPairEncryptor(pEncryptSpec);

        /* Encrypt data */
        myEncryptor.initForEncrypt(pKeyPair);
        final byte[] myEncrypted = myEncryptor.encrypt(myData);

        /* decrypt encrypted data */
        myEncryptor.initForDecrypt(pKeyPair);
        final byte[] myResult = myEncryptor.decrypt(myEncrypted);

        /* Check that we have arrived back at the original data */
        if (!Arrays.equals(myData, myResult)) {
            throw new GordianDataException(ERRORMSG);
        }
    }

    /**
     * Check keyPair validity via agreement.
     * @param pFactory the factory
     * @param pKeyPair the keyPair
     * @param pAgreeSpec the agreementSpec
     * @throws OceanusException on error
     */
    private static void checkValidity(final GordianCoreFactory pFactory,
                                      final GordianKeyPair pKeyPair,
                                      final GordianKeyPairAgreementSpec pAgreeSpec) throws OceanusException {
        /* Create agreement on client side */
        final GordianAgreementFactory myAgrees = pFactory.getKeyPairFactory().getAgreementFactory();
        GordianKeyPairAnonymousAgreement myAgreement
                = (GordianKeyPairAnonymousAgreement) myAgrees.createKeyPairAgreement(pAgreeSpec);
        final byte[] myHello = myAgreement.createClientHello(pKeyPair);
        final byte[] myClient = (byte[]) myAgreement.getResult();

        /* Accept agreement on server side */
        /* We have to use a new agreement due to bug in JCA NewHope support */
        myAgreement = (GordianKeyPairAnonymousAgreement) myAgrees.createKeyPairAgreement(pAgreeSpec);
        myAgreement.acceptClientHello(pKeyPair, myHello);
        final byte[] myServer = (byte[]) myAgreement.getResult();

        /* Check that we have the same result at either end */
        if (!Arrays.equals(myClient, myServer)) {
            throw new GordianDataException(ERRORMSG);
        }
    }

    /**
     * Obtain validity check for keyPair.
     * @param pKeyPair the keyPair
     * @return the validity check
     */
    private static Object getValidityCheck(final GordianKeyPair pKeyPair) {
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
            case RAINBOW:
            case SPHINCS:
            case QTESLA:
            case XMSS:
            case LMS:
                return GordianSignatureSpec.defaultForKey(mySpec);
            case ELGAMAL:
                return GordianEncryptorSpec.elGamal(GordianDigestSpec.sha2(GordianLength.LEN_512));
            case MCELIECE:
                return mySpec.getMcElieceKeySpec().isCCA2()
                        ? GordianEncryptorSpec.mcEliece(GordianMcElieceEncryptionType.FUJISAKI)
                        : GordianEncryptorSpec.mcEliece(GordianMcElieceEncryptionType.STANDARD);
            case DH:
                return GordianKeyPairAgreementSpec.dhAnon(GordianKDFType.SHA256KDF);
            case XDH:
                return mySpec.getEdwardsElliptic().is25519()
                        ? GordianKeyPairAgreementSpec.xdhAnon(GordianKDFType.SHA256KDF)
                        : GordianKeyPairAgreementSpec.xdhAnon(GordianKDFType.SHA512KDF);
            case NEWHOPE:
                return GordianKeyPairAgreementSpec.newHope(GordianKDFType.SHA256KDF);
            default:
                return null;
        }
    }
}