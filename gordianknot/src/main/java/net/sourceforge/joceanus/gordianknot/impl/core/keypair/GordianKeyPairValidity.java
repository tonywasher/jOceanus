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
package net.sourceforge.joceanus.gordianknot.impl.core.keypair;

import net.sourceforge.joceanus.gordianknot.api.agree.GordianAgreementSpec;
import net.sourceforge.joceanus.gordianknot.api.agree.GordianAgreementSpecBuilder;
import net.sourceforge.joceanus.gordianknot.api.agree.GordianAgreementKDF;
import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.gordianknot.api.cert.GordianCertificate;
import net.sourceforge.joceanus.gordianknot.api.cert.GordianKeyPairUsage;
import net.sourceforge.joceanus.gordianknot.api.cert.GordianKeyPairUse;
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
import net.sourceforge.joceanus.gordianknot.api.agree.GordianAgreement;
import net.sourceforge.joceanus.gordianknot.api.agree.GordianAgreementFactory;
import net.sourceforge.joceanus.gordianknot.api.agree.GordianAgreementParams;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianDataException;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianLogicException;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;

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
     * Server X500Name.
     */
    private static final X500Name SERVER = new X500NameBuilder(BCStyle.INSTANCE).addRDN(BCStyle.CN, "Server").build();

    /**
     * Private constructor.
     */
    private GordianKeyPairValidity() {
    }

    /**
     * Check keyPair validity.
     *
     * @param pFactory the factory
     * @param pKeyPair the keyPair
     * @throws GordianException on error
     */
    public static void checkValidity(final GordianBaseFactory pFactory,
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
     *
     * @param pFactory  the factory
     * @param pKeyPair  the keyPair
     * @param pSignSpec the signature spec
     * @throws GordianException on error
     */
    private static void checkValidity(final GordianBaseFactory pFactory,
                                      final GordianKeyPair pKeyPair,
                                      final GordianSignatureSpec pSignSpec) throws GordianException {
        /* Use default personalisation as the data to sign */
        final byte[] myData = pFactory.getRandomSource().defaultPersonalisation();

        /* Create signer */
        final GordianSignatureFactory mySigns = pFactory.getAsyncFactory().getSignatureFactory();
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
     *
     * @param pFactory     the factory
     * @param pKeyPair     the keyPair
     * @param pEncryptSpec the encryption spec
     * @throws GordianException on error
     */
    private static void checkValidity(final GordianBaseFactory pFactory,
                                      final GordianKeyPair pKeyPair,
                                      final GordianEncryptorSpec pEncryptSpec) throws GordianException {
        /* Use default personalisation as the data to encrypt */
        final byte[] myData = pFactory.getRandomSource().defaultPersonalisation();

        /* Create encryptor */
        final GordianEncryptorFactory myEncrypts = pFactory.getAsyncFactory().getEncryptorFactory();
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
     *
     * @param pFactory   the factory
     * @param pKeyPair   the keyPair
     * @param pAgreeSpec the agreementSpec
     * @throws GordianException on error
     */
    private static void checkValidity(final GordianBaseFactory pFactory,
                                      final GordianKeyPair pKeyPair,
                                      final GordianAgreementSpec pAgreeSpec) throws GordianException {
        /* Create agreement on client side */
        final GordianAgreementFactory myAgrees = pFactory.getAsyncFactory().getAgreementFactory();
        final GordianCertificate myCert = myAgrees.newMiniCertificate(SERVER, pKeyPair, new GordianKeyPairUsage(GordianKeyPairUse.AGREEMENT));
        GordianAgreementParams myParams = myAgrees.newAgreementParams(pAgreeSpec, GordianLength.LEN_256.getByteLength())
                .setServerCertificate(myCert);
        GordianAgreement myAgreement = myAgrees.createAgreement(myParams);
        final byte[] myHello = myAgreement.nextMessage();
        final byte[] myClient = (byte[]) myAgreement.getResult();

        /* Accept agreement on server side */
        myAgreement = myAgrees.parseAgreementMessage(myHello);
        myParams = myAgreement.getAgreementParams().setServerCertificate(myCert);
        myAgreement.updateParams(myParams);
        final byte[] myServer = (byte[]) myAgreement.getResult();

        /* Check that we have the same result at either end */
        if (!Arrays.equals(myClient, myServer)) {
            throw new GordianDataException(ERRORMSG + " " + pAgreeSpec);
        }
    }

    /**
     * Obtain validity check for keyPair.
     *
     * @param pFactory the factory
     * @param pKeyPair the keyPair
     * @return the validity check
     */
    private static Object getValidityCheck(final GordianBaseFactory pFactory,
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
                return pFactory.getAsyncFactory().getSignatureFactory().defaultForKeyPair(mySpec);
            case ELGAMAL:
                return GordianEncryptorSpecBuilder.elGamal(GordianDigestSpecBuilder.sha2(GordianLength.LEN_512));
            case DH:
                return GordianAgreementSpecBuilder.anon(mySpec, GordianAgreementKDF.SHA256KDF);
            case XDH:
                return mySpec.getEdwardsElliptic().is25519()
                        ? GordianAgreementSpecBuilder.anon(mySpec, GordianAgreementKDF.SHA256KDF)
                        : GordianAgreementSpecBuilder.anon(mySpec, GordianAgreementKDF.SHA512KDF);
            case CMCE:
            case FRODO:
            case SABER:
            case MLKEM:
            case HQC:
            case BIKE:
            case NTRU:
            case NTRUPRIME:
            case NEWHOPE:
                return GordianAgreementSpecBuilder.kem(mySpec, GordianAgreementKDF.NONE);
            default:
                return null;
        }
    }
}
