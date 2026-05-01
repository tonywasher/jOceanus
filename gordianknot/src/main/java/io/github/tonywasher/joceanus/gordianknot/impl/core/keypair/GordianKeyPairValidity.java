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
package io.github.tonywasher.joceanus.gordianknot.impl.core.keypair;

import io.github.tonywasher.joceanus.gordianknot.api.agree.GordianAgreement;
import io.github.tonywasher.joceanus.gordianknot.api.agree.GordianAgreementFactory;
import io.github.tonywasher.joceanus.gordianknot.api.agree.GordianAgreementParams;
import io.github.tonywasher.joceanus.gordianknot.api.agree.spec.GordianAgreementKDF;
import io.github.tonywasher.joceanus.gordianknot.api.agree.spec.GordianAgreementSpec;
import io.github.tonywasher.joceanus.gordianknot.api.agree.spec.GordianAgreementSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;
import io.github.tonywasher.joceanus.gordianknot.api.cert.GordianCertificate;
import io.github.tonywasher.joceanus.gordianknot.api.cert.GordianKeyPairUsage;
import io.github.tonywasher.joceanus.gordianknot.api.cert.GordianKeyPairUse;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianDigestSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.encrypt.GordianEncryptor;
import io.github.tonywasher.joceanus.gordianknot.api.encrypt.GordianEncryptorFactory;
import io.github.tonywasher.joceanus.gordianknot.api.encrypt.spec.GordianEncryptorSpec;
import io.github.tonywasher.joceanus.gordianknot.api.encrypt.spec.GordianEncryptorSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPair;
import io.github.tonywasher.joceanus.gordianknot.api.sign.GordianNewSignParams;
import io.github.tonywasher.joceanus.gordianknot.api.sign.GordianNewSignParamsBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.sign.GordianSignature;
import io.github.tonywasher.joceanus.gordianknot.api.sign.GordianSignatureFactory;
import io.github.tonywasher.joceanus.gordianknot.api.sign.spec.GordianSignatureSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianDataException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianLogicException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.agree.GordianCoreAgreementSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.digest.GordianCoreDigestSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.encrypt.GordianCoreEncryptorSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreKeyPairSpec;
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
        switch (myCheck) {
            case GordianSignatureSpec mySpec -> checkValidity(pFactory, pKeyPair, mySpec);
            case GordianEncryptorSpec mySpec -> checkValidity(pFactory, pKeyPair, mySpec);

            case GordianAgreementSpec mySpec -> checkValidity(pFactory, pKeyPair, mySpec);
            default -> throw new GordianLogicException("Unexpected keyPairType");
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
        final GordianNewSignParamsBuilder myBuilder = mySigns.newSignParamsBuilder();
        final GordianNewSignParams myParams = myBuilder.keyPair(pKeyPair);
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
     * @throws GordianException on error
     */
    private static Object getValidityCheck(final GordianBaseFactory pFactory,
                                           final GordianKeyPair pKeyPair) throws GordianException {
        /* Switch on keyType */
        final GordianDigestSpecBuilder myBuilder = GordianCoreDigestSpecBuilder.newInstance();
        final GordianEncryptorSpecBuilder myEncBuilder = GordianCoreEncryptorSpecBuilder.newInstance();
        final GordianAgreementSpecBuilder myAgreeBuilder = GordianCoreAgreementSpecBuilder.newInstance();
        final GordianCoreKeyPairSpec mySpec = (GordianCoreKeyPairSpec) pKeyPair.getKeyPairSpec();
        switch (mySpec.getKeyPairType()) {
            case RSA:
            case DSA:
            case EDDSA:
            case EC:
            case GOST:
            case DSTU:
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
                return myEncBuilder.elGamal(myBuilder.sha2(GordianLength.LEN_256));
            case DH:
                return myAgreeBuilder.anon(mySpec, GordianAgreementKDF.SHA256KDF);
            case XDH:
                return mySpec.getEdwardsSpec().is25519()
                        ? myAgreeBuilder.anon(mySpec, GordianAgreementKDF.SHA256KDF)
                        : myAgreeBuilder.anon(mySpec, GordianAgreementKDF.SHA512KDF);
            case CMCE:
            case FRODO:
            case SABER:
            case MLKEM:
            case HQC:
            case BIKE:
            case NTRU:
            case NTRUPLUS:
            case NTRUPRIME:
            case NEWHOPE:
                return myAgreeBuilder.kem(mySpec, GordianAgreementKDF.NONE);
            default:
                throw new GordianDataException("No validity check found for :" + mySpec.getKeyPairType());
        }
    }
}
