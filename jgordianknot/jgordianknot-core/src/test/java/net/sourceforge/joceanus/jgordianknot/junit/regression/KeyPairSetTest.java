/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2020 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.junit.regression;

import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.EnumSet;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAgreementSpec;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianKDFType;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactoryType;
import net.sourceforge.joceanus.jgordianknot.api.keypairset.GordianKeyPairSet;
import net.sourceforge.joceanus.jgordianknot.api.keypairset.GordianKeyPairSetGenerator;
import net.sourceforge.joceanus.jgordianknot.api.keypairset.GordianKeyPairSetSpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.keypair.GordianCoreAsymFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.keypairset.GordianCoreKeyPairSetFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.keypairset.GordianKeyPairSetAnonymousAgreement;
import net.sourceforge.joceanus.jgordianknot.impl.core.keypairset.GordianKeyPairSetEncryptor;
import net.sourceforge.joceanus.jgordianknot.impl.core.keypairset.GordianKeyPairSetHandshakeAgreement;
import net.sourceforge.joceanus.jgordianknot.impl.core.keypairset.GordianKeyPairSetSignedAgreement;
import net.sourceforge.joceanus.jgordianknot.impl.core.keypairset.GordianKeyPairSetSigner;
import net.sourceforge.joceanus.jgordianknot.util.GordianGenerator;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * KeyPairSet Tests.
 */
public class KeyPairSetTest {
    /**
     * Create the zipFile test suite.
     * @return the test stream
     * @throws OceanusException on error
     */
    @TestFactory
    public Stream<DynamicNode> keyPairSetTests() throws OceanusException {
        /* Create tests */
        Stream<DynamicNode> myStream = keyPairSetTests(GordianFactoryType.BC);
        return Stream.concat(myStream, keyPairSetTests(GordianFactoryType.JCA));
    }

    /**
     * Create the keyPairSet test suite for a factory.
     * @param pType the factoryType
     * @return the test stream
     * @throws OceanusException on error
     */
    private Stream<DynamicNode> keyPairSetTests(final GordianFactoryType pType) throws OceanusException {
        /* Create the factory */
        final GordianFactory myFactory = GordianGenerator.createFactory(pType);

        /* Return the stream */
        final String myName = pType.toString();
        return Stream.of(DynamicContainer.dynamicContainer(myName, Stream.of(
                DynamicContainer.dynamicContainer("Signatures", signatureTests(myFactory)),
                DynamicContainer.dynamicContainer("Encryptors", encryptionTests(myFactory)),
                DynamicContainer.dynamicContainer("Agreements", agreementTests(myFactory))
        )));
    }

    /**
     * Create the signature test suite for a factory.
     * @param pFactory the factory
     * @return the test stream
      */
    private Stream<DynamicNode> signatureTests(final GordianFactory pFactory) {
        /* Return the stream */
        return EnumSet.allOf(GordianKeyPairSetSpec.class)
                .stream()
                .filter(GordianKeyPairSetSpec::canSign)
                .map(s -> DynamicTest.dynamicTest(s.toString(), () -> signatureTest(pFactory, s))
        );
    }

    /**
     * Create the encryption test suite for a factory.
     * @param pFactory the factory
     * @return the test stream
     */
    private Stream<DynamicNode> encryptionTests(final GordianFactory pFactory) {
        /* Return the stream */
        return EnumSet.allOf(GordianKeyPairSetSpec.class)
                .stream()
                .filter(GordianKeyPairSetSpec::canEncrypt)
                .map(s -> DynamicTest.dynamicTest(s.toString(), () -> encryptionTest(pFactory, s))
                );
    }

    /**
     * Create the agreement test suite for a factory.
     * @param pFactory the factory
     * @return the test stream
     */
    private Stream<DynamicNode> agreementTests(final GordianFactory pFactory) {
        /* Return the stream */
        return EnumSet.allOf(GordianKeyPairSetSpec.class)
                .stream()
                .filter(GordianKeyPairSetSpec::canAgree)
                .map(s -> DynamicContainer.dynamicContainer(s.toString(), agreementTests(pFactory, s))
                );
    }

    /**
     * Create the encryption test suite for a factory.
     * @param pFactory the factory
     * @param pSpec the keyPairSet spec
     * @return the test stream
     */
    private Stream<DynamicNode> agreementTests(final GordianFactory pFactory,
                                               final GordianKeyPairSetSpec pSpec) {
        /* Return the stream */
        return Stream.of(
                DynamicTest.dynamicTest("anonymous", () -> agreementAnonTest(pFactory, pSpec)),
                DynamicTest.dynamicTest("signed", () -> agreementSignedTest(pFactory, pSpec)),
                DynamicTest.dynamicTest("handshake", () -> agreementHandshakeTest(pFactory, pSpec, Boolean.FALSE)),
                DynamicTest.dynamicTest("confirm", () -> agreementHandshakeTest(pFactory, pSpec, Boolean.TRUE))
        );
    }

    /**
     * Perform the signature tests for a factory.
     * @param pFactory the factory
     * @param pSpec the keyPairSet spec
     * @throws OceanusException on error
     */
    private void signatureTest(final GordianFactory pFactory,
                               final GordianKeyPairSetSpec pSpec) throws OceanusException {
        /* Create the keyPair */
        final GordianCoreAsymFactory myAsymFactory = (GordianCoreAsymFactory) pFactory.getAsymmetricFactory();
        final GordianCoreKeyPairSetFactory myFactory = (GordianCoreKeyPairSetFactory) myAsymFactory.getKeyPairSetFactory();
        final GordianKeyPairSetGenerator myGenerator = myFactory.getKeyPairSetGenerator(pSpec);
        final GordianKeyPairSet mySet = myGenerator.generateKeyPairSet();

        /* Check external transformations */
        final X509EncodedKeySpec myPublic = myGenerator.getX509Encoding(mySet);
        final PKCS8EncodedKeySpec myPrivate = myGenerator.getPKCS8Encoding(mySet);
        final GordianKeyPairSet myDerived = myGenerator.deriveKeyPairSet(myPublic, myPrivate);
        Assertions.assertEquals(mySet, myDerived);

        /* Create signature */
        final GordianKeyPairSetSigner mySigner = myFactory.getKeyPairSetSigner(pSpec);
        mySigner.initForSigning(mySet);
        mySigner.update(myPublic.getEncoded(), 0, myPublic.getEncoded().length);
        mySigner.update(myPrivate.getEncoded(), 0, myPrivate.getEncoded().length);
        final byte[] mySign = mySigner.sign();

        /* Verify signature */
        mySigner.initForVerify(mySet);
        mySigner.update(myPublic.getEncoded(), 0, myPublic.getEncoded().length);
        mySigner.update(myPrivate.getEncoded(), 0, myPrivate.getEncoded().length);
        Assertions.assertTrue(mySigner.verify(mySign));
    }

    /**
     * Perform the encryption tests for a factory.
     * @param pFactory the factory
     * @param pSpec the keyPairSet spec
     * @throws OceanusException on error
     */
    private void encryptionTest(final GordianFactory pFactory,
                                final GordianKeyPairSetSpec pSpec) throws OceanusException {
        /* Create the keyPair */
        final GordianCoreAsymFactory myAsymFactory = (GordianCoreAsymFactory) pFactory.getAsymmetricFactory();
        final GordianCoreKeyPairSetFactory myFactory = (GordianCoreKeyPairSetFactory) myAsymFactory.getKeyPairSetFactory();
        final GordianKeyPairSetGenerator myGenerator = myFactory.getKeyPairSetGenerator(pSpec);
        final GordianKeyPairSet mySet = myGenerator.generateKeyPairSet();

        /* Check external transformations */
        final X509EncodedKeySpec myPublic = myGenerator.getX509Encoding(mySet);
        final PKCS8EncodedKeySpec myPrivate = myGenerator.getPKCS8Encoding(mySet);
        final GordianKeyPairSet myDerived = myGenerator.deriveKeyPairSet(myPublic, myPrivate);
        Assertions.assertEquals(mySet, myDerived);

        /* Create encryption */
        final GordianKeyPairSetEncryptor myEncrypt = myFactory.getKeyPairSetEncryptor(pSpec);
        myEncrypt.initForEncrypt(mySet);
        final byte[] myData = myEncrypt.encrypt(myPublic.getEncoded());

        /* decrypt data */
        myEncrypt.initForDecrypt(mySet);
        final byte[] myResult = myEncrypt.decrypt(myData);
        Assertions.assertArrayEquals(myResult, myPublic.getEncoded());
    }

    /**
     * Perform the agreement tests for a factory.
     * @param pFactory the factory
     * @param pSpec the keyPairSet spec
     * @throws OceanusException on error
     */
    private void agreementAnonTest(final GordianFactory pFactory,
                                   final GordianKeyPairSetSpec pSpec) throws OceanusException {
        /* Create the keyPair */
        final GordianCoreAsymFactory myAsymFactory = (GordianCoreAsymFactory) pFactory.getAsymmetricFactory();
        final GordianCoreKeyPairSetFactory myFactory = (GordianCoreKeyPairSetFactory) myAsymFactory.getKeyPairSetFactory();
        final GordianKeyPairSetGenerator myGenerator = myFactory.getKeyPairSetGenerator(pSpec);
        final GordianKeyPairSet mySet = myGenerator.generateKeyPairSet();

        /* Check external transformations */
        final X509EncodedKeySpec myPublic = myGenerator.getX509Encoding(mySet);
        final PKCS8EncodedKeySpec myPrivate = myGenerator.getPKCS8Encoding(mySet);
        final GordianKeyPairSet myDerived = myGenerator.deriveKeyPairSet(myPublic, myPrivate);
        if (GordianFactoryType.BC.equals(pFactory.getFactoryType())) {
            Assertions.assertEquals(mySet, myDerived);
        }

        /* Create clientHello */
        final GordianKDFType myKDFType = getKDFTypeForKeyPairSetSpec(pSpec);
        final GordianAgreementSpec myAgreeSpec = GordianAgreementSpec.dhAnon(myKDFType);
        final GordianKeyPairSetAnonymousAgreement myClient = (GordianKeyPairSetAnonymousAgreement) myFactory.getKeyPairSetAgreement(myAgreeSpec);
        final byte[] myClientHello = myClient.createClientHello(mySet);
        final byte[] myResult = (byte[]) myClient.getResult();

        /* accept clientHello */
        final GordianKeyPairSetAnonymousAgreement myServer = (GordianKeyPairSetAnonymousAgreement) myFactory.getKeyPairSetAgreement(myAgreeSpec);
        myServer.acceptClientHello(mySet, myClientHello);
        Assertions.assertArrayEquals(myResult, (byte[]) myServer.getResult());
    }

    /**
     * Perform the agreement tests for a factory.
     * @param pFactory the factory
     * @param pSpec the keyPairSet spec
     * @throws OceanusException on error
     */
    private void agreementSignedTest(final GordianFactory pFactory,
                                     final GordianKeyPairSetSpec pSpec) throws OceanusException {
        /* Create the keyPair */
        final GordianCoreAsymFactory myAsymFactory = (GordianCoreAsymFactory) pFactory.getAsymmetricFactory();
        final GordianCoreKeyPairSetFactory myFactory = (GordianCoreKeyPairSetFactory) myAsymFactory.getKeyPairSetFactory();
        final GordianKeyPairSetGenerator myGenerator = myFactory.getKeyPairSetGenerator(GordianKeyPairSetSpec.SIGNLO);
        final GordianKeyPairSet mySet = myGenerator.generateKeyPairSet();

        /* Check external transformations */
        final X509EncodedKeySpec myPublic = myGenerator.getX509Encoding(mySet);
        final PKCS8EncodedKeySpec myPrivate = myGenerator.getPKCS8Encoding(mySet);
        final GordianKeyPairSet myDerived = myGenerator.deriveKeyPairSet(myPublic, myPrivate);
        Assertions.assertEquals(mySet, myDerived);

        /* Create clientHello */
        final GordianKDFType myKDFType = getKDFTypeForKeyPairSetSpec(pSpec);
        final GordianAgreementSpec myAgreeSpec = GordianAgreementSpec.dhSigned(myKDFType);
        final GordianKeyPairSetSignedAgreement myClient = (GordianKeyPairSetSignedAgreement) myFactory.getKeyPairSetAgreement(myAgreeSpec);
        final byte[] myClientHello = myClient.createClientHello(pSpec);

        /* accept clientHello */
        final GordianKeyPairSetSignedAgreement myServer = (GordianKeyPairSetSignedAgreement) myFactory.getKeyPairSetAgreement(myAgreeSpec);
        final byte[] myServerHello = myServer.acceptClientHello(mySet, myClientHello);

        /* accept serverHello */
        myClient.acceptServerHello(mySet, myServerHello);
        final byte[] myResult = (byte[]) myClient.getResult();
        Assertions.assertArrayEquals(myResult, (byte[]) myServer.getResult());
    }

    /**
     * Perform the agreement tests for a factory.
     * @param pFactory the factory
     * @param pSpec the keyPairSet spec
     * @param pConfirm with Confirm? true/false
     * @throws OceanusException on error
     */
    private void agreementHandshakeTest(final GordianFactory pFactory,
                                        final GordianKeyPairSetSpec pSpec,
                                        final Boolean pConfirm) throws OceanusException {
        /* Create the keyPair */
        final GordianCoreAsymFactory myAsymFactory = (GordianCoreAsymFactory) pFactory.getAsymmetricFactory();
        final GordianCoreKeyPairSetFactory myFactory = (GordianCoreKeyPairSetFactory) myAsymFactory.getKeyPairSetFactory();
        final GordianKeyPairSetGenerator myGenerator = myFactory.getKeyPairSetGenerator(pSpec);
        final GordianKeyPairSet myClient = myGenerator.generateKeyPairSet();
        final GordianKeyPairSet myServer = myGenerator.generateKeyPairSet();

        /* Check external transformations */
        final X509EncodedKeySpec myPublic = myGenerator.getX509Encoding(myClient);
        final PKCS8EncodedKeySpec myPrivate = myGenerator.getPKCS8Encoding(myClient);
        final GordianKeyPairSet myDerived = myGenerator.deriveKeyPairSet(myPublic, myPrivate);
        if (GordianFactoryType.BC.equals(pFactory.getFactoryType())) {
            Assertions.assertEquals(myClient, myDerived);
        }

        /* Create clientHello */
        final GordianKDFType myKDFType = getKDFTypeForKeyPairSetSpec(pSpec);
        final GordianAgreementSpec myAgreeSpec = GordianAgreementSpec.dhUnifiedConfirm(myKDFType, pConfirm);
        final GordianKeyPairSetHandshakeAgreement myClientAgree = (GordianKeyPairSetHandshakeAgreement) myFactory.getKeyPairSetAgreement(myAgreeSpec);
        final byte[] myClientHello = myClientAgree.createClientHello(myClient);

        /* accept clientHello */
        final GordianKeyPairSetHandshakeAgreement myServerAgree = (GordianKeyPairSetHandshakeAgreement) myFactory.getKeyPairSetAgreement(myAgreeSpec);
        final byte[] myServerHello = myServerAgree.acceptClientHello(myClient, myServer, myClientHello);

        /* accept serverHello */
        final byte[] myClientConfirm = myClientAgree.acceptServerHello(myServer, myServerHello);
        final byte[] myResult = (byte[]) myClientAgree.getResult();

        /* accept clientConfirm */
        if (myClientConfirm != null) {
            myServerAgree.acceptClientConfirm(myClientConfirm);
        }
        Assertions.assertArrayEquals(myResult, (byte[]) myServerAgree.getResult());
    }

    /**
     * Obtain the KDF for the KeyPairSetSpec.
     * @param pSpec the keyPairSetSpec
     * @return the KDFType.
     */
    private static GordianKDFType getKDFTypeForKeyPairSetSpec(final GordianKeyPairSetSpec pSpec) {
        return GordianKeyPairSetSpec.AGREEHI.equals(pSpec)
            ? GordianKDFType.SHA512KDF
            : GordianKDFType.SHA256KDF;
    }
}
