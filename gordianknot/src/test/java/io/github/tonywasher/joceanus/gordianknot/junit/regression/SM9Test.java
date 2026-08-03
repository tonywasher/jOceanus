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

package io.github.tonywasher.joceanus.gordianknot.junit.regression;

import io.github.tonywasher.joceanus.gordianknot.api.agree.GordianAgreementFactory;
import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.factory.GordianAsyncFactory;
import io.github.tonywasher.joceanus.gordianknot.api.factory.GordianFactory;
import io.github.tonywasher.joceanus.gordianknot.api.factory.GordianFactoryType;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianIdAwareKeyPair;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPair;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPairFactory;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPairGenerator;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianKeyPairSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianKeyPairSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianSM9Spec.GordianSM9EncryptType;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianSM9Spec.GordianSM9SignType;
import io.github.tonywasher.joceanus.gordianknot.api.sign.GordianSignParams;
import io.github.tonywasher.joceanus.gordianknot.api.sign.GordianSignParamsBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.sign.GordianSignature;
import io.github.tonywasher.joceanus.gordianknot.api.sign.GordianSignatureFactory;
import io.github.tonywasher.joceanus.gordianknot.api.sign.spec.GordianSignatureSpec;
import io.github.tonywasher.joceanus.gordianknot.api.sign.spec.GordianSignatureSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.util.GordianGenerator;

import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Objects;

public class SM9Test {
    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        try {
            /* Create Factory */
            final GordianFactory myFactory = GordianGenerator.createRandomFactory(GordianFactoryType.BC);
            final GordianFactory myJFactory = GordianGenerator.createRandomFactory(GordianFactoryType.JCA);

            /* Create Encrypt keyPair Generator */
            final GordianAsyncFactory myAsyncFactory = myFactory.getAsyncFactory();
            final GordianAsyncFactory myJAsyncFactory = myJFactory.getAsyncFactory();
            final GordianKeyPairFactory myKeyPairs = myAsyncFactory.getKeyPairFactory();
            final GordianSignatureFactory mySigns = myAsyncFactory.getSignatureFactory();
            final GordianAgreementFactory myAgrees = myAsyncFactory.getAgreementFactory();

            /* Create Encryption keyPairs */
            final byte[] myTargetId = "TargetID".getBytes();
            final byte[] myClientId = "ClientID".getBytes();
            final byte[] mySignerId = "SignerID".getBytes();
            final GordianKeyPairSpecBuilder myKPBuilder = myKeyPairs.newKeyPairSpecBuilder();
            final GordianKeyPairSpec myEncMasterSpec = myKPBuilder.sm9(GordianSM9EncryptType.ENCMASTER);
            final GordianKeyPairSpec myEncKEMSpec = myKPBuilder.sm9(GordianSM9EncryptType.ENCRYPT);
            final GordianKeyPairGenerator myEncGenerator = myKeyPairs.getKeyPairGenerator(myEncMasterSpec);
            final GordianIdAwareKeyPair<GordianSM9EncryptType> myEncMasterPair
                    = (GordianIdAwareKeyPair<GordianSM9EncryptType>) myEncGenerator.generateKeyPair();

//            /* Names */
//            final X500Name myTargetName = KeyStoreUtils.buildX500Name(KeyStoreAlias.TARGET);
//            final GordianCertificate myTargetCert = myAgrees.newMiniCertificate(myTargetName, (GordianKeyPair) myEncMasterPair,
//                    new GordianKeyPairUsage(GordianKeyPairUse.AGREEMENT));
//
//            /* Create a KEM agreement */
//            final GordianAgreementSpecBuilder myAgreeBuilder = myAgrees.newAgreementSpecBuilder();
//            final GordianAgreementSpec myKEMAgreeSpec = myAgreeBuilder.kem(myEncMasterSpec, GordianAgreementKDF.NONE);
//            final GordianAgreementParams myKEMClientParams = myAgrees.newAgreementParams(myKEMAgreeSpec, myEncMasterSpec)
//                    .setServerCertificate(myTargetCert).setServerName(myTargetId);
//            final GordianAgreement myKEMClient = myAgrees.createAgreement(myKEMClientParams);
//            final byte[] myKEMClientHello = myKEMClient.nextMessage();
//            final GordianAgreement myKEMServer = myAgrees.parseAgreementMessage(myKEMClientHello);
//            final GordianAgreementParams myKEMServerParams = myKEMServer.getAgreementParams()
//                    .setServerCertificate(myTargetCert);
//            myKEMServer.updateParams(myKEMServerParams);
//            final byte[] myServerHello = myKEMServer.nextMessage();
//            myAgrees.parseAgreementMessage(myServerHello);
//
//            /* Create an Xchg agreement */
//            final GordianAgreementSpec myXchgAgreeSpec = myAgreeBuilder.kem(myEncKEMSpec, GordianAgreementKDF.NONE);
//            final GordianAgreementParams myXchgClientParams = myAgrees.newAgreementParams(myXchgAgreeSpec, myEncMasterSpec)
//                    .setServerCertificate(myTargetCert).setServerName(myTargetId)
//                    .setClientCertificate(myTargetCert).setClientName(myClientId);
//            final GordianAgreement myXchgClient = myAgrees.createAgreement(myXchgClientParams);
//            final byte[] myXchgClientHello = myXchgClient.nextMessage();
//            final GordianAgreement myXchgServer = myAgrees.parseAgreementMessage(myXchgClientHello);
//            final GordianAgreementParams myXchgServerParams = myXchgServer.getAgreementParams()
//                    .setServerCertificate(myTargetCert);
//            myXchgServer.updateParams(myXchgServerParams);
//            final byte[] myXchgServerHello = myXchgServer.nextMessage();
//            myAgrees.parseAgreementMessage(myXchgServerHello);
//
            /* Test signatures */
            testSignatures(myAsyncFactory);
            testSignatures(myJAsyncFactory);

        } catch (GordianException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private static void testSignatures(final GordianAsyncFactory pFactory) {
        try {
            /* Access factories */
            final GordianKeyPairFactory myKeyPairs = pFactory.getKeyPairFactory();
            final GordianSignatureFactory mySigns = pFactory.getSignatureFactory();
            final byte[] mySignerId = "SignerID".getBytes();
            final byte[] myMessage = "ASimpleMessage".getBytes();

            /* Create Signature keyPairs */
            final GordianKeyPairSpecBuilder myKPBuilder = myKeyPairs.newKeyPairSpecBuilder();
            final GordianKeyPairSpec mySigMasterSpec = myKPBuilder.sm9(GordianSM9SignType.SIGNMASTER);
            final GordianKeyPairGenerator mySigGenerator = myKeyPairs.getKeyPairGenerator(mySigMasterSpec);
            final GordianIdAwareKeyPair<GordianSM9SignType> mySigMasterPair = (GordianIdAwareKeyPair<GordianSM9SignType>) mySigGenerator.generateKeyPair();
            final GordianKeyPair mySigPair = mySigMasterPair.newUserKeyPair(GordianSM9SignType.SIGN, mySignerId);

            /* Obtain representations keyPair */
            final X509EncodedKeySpec myX509 = mySigGenerator.getX509Encoding(mySigMasterPair);
            final PKCS8EncodedKeySpec myPKCS8 = mySigGenerator.getPKCS8Encoding(mySigMasterPair);
            final GordianKeyPair myDerived = mySigGenerator.deriveKeyPair(myX509, myPKCS8);
            final boolean isIdentical = Objects.equals(mySigMasterPair, myDerived);

            /* Create a signature */
            final GordianSignatureSpecBuilder mySigBuilder = mySigns.newSignatureSpecBuilder();
            final GordianSignatureSpec mySigSpec = mySigBuilder.sm9();
            final GordianSignature mySigner = mySigns.createSigner(mySigSpec);
            final GordianSignParamsBuilder mySigParamsBuilder = mySigns.newSignParamsBuilder();
            final GordianSignParams mySigParams = mySigParamsBuilder.keyPair(mySigPair);
            mySigner.initForSigning(mySigParams);
            mySigner.update(myMessage);
            final byte[] mySignature = mySigner.sign();
            mySigner.initForVerify(mySigParams);
            mySigner.update(myMessage);
            final boolean myResult = mySigner.verify(mySignature);
            assert myResult;

        } catch (GordianException e) {
            e.printStackTrace();
        }
    }
}
