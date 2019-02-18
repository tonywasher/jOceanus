/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2019 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.junit;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAgreementFactory;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAgreementSpec;
import net.sourceforge.joceanus.jgordianknot.api.asym.GordianAsymKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.asym.GordianAsymKeyType;
import net.sourceforge.joceanus.jgordianknot.api.encrypt.GordianEncryptorFactory;
import net.sourceforge.joceanus.jgordianknot.api.encrypt.GordianEncryptorSpec;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianAsymFactory;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactoryType;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianParameters;
import net.sourceforge.joceanus.jgordianknot.api.impl.GordianGenerator;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.api.sign.GordianSignatureFactory;
import net.sourceforge.joceanus.jgordianknot.api.sign.GordianSignatureSpec;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Security Test suite - Test Asymmetric functionality.
 */
public class AsymmetricTest {
    /**
     * The single keyType to test.
     */
    static final GordianAsymKeyType theKeyType = GordianAsymKeyType.RSA;

    /**
     * Do we process all specs.
     */
    static final boolean allSpecs = false;

    /**
     * The factories.
     */
    static GordianFactory BCFACTORY;
    static GordianFactory JCAFACTORY;

    @BeforeAll
    static void createSecurityFactories() throws OceanusException {
        BCFACTORY = GordianGenerator.createFactory(new GordianParameters(GordianFactoryType.BC));
        JCAFACTORY = GordianGenerator.createFactory(new GordianParameters(GordianFactoryType.JCA));
    }

    /**
     * Factory and KeySpec definition.
     */
    static class FactoryKeySpec {
        private final GordianFactory theFactory;
        private final GordianAsymKeySpec theKeySpec;
        private final List<FactorySignature> theSignatures;
        private final List<FactoryAgreement> theAgreements;
        private final List<FactoryEncryptor> theEncryptors;
        private GordianKeyPair theKeyPair;

        /**
         * Constructor.
         * @param pFactory the factory
         * @param pKeySpec the keySpec
         */
        FactoryKeySpec(final GordianFactory pFactory,
                       final GordianAsymKeySpec pKeySpec) {
            theFactory = pFactory;
            theKeySpec = pKeySpec;
            theSignatures = new ArrayList<>();
            theAgreements = new ArrayList<>();
            theEncryptors = new ArrayList<>();
        }

        /**
         * Obtain (or create) the keyPair for the FactoryKeySpec
         * @return the keyPair
         * @throws OceanusException on error
         */
        GordianKeyPair getKeyPair() throws OceanusException {
            /* Return keyPair if it exists */
            if (theKeyPair != null) {
                return theKeyPair;
            }

            /* Synchronize access */
            synchronized (this) {
                /* Check for race condition */
                if (theKeyPair != null) {
                    return theKeyPair;
                }

                /* Generate the keyPair */
                GordianAsymFactory myFactory = theFactory.getAsymmetricFactory();
                GordianKeyPairGenerator myGenerator = myFactory.getKeyPairGenerator(theKeySpec);
                theKeyPair = myGenerator.generateKeyPair();
                return theKeyPair;
            }
        }

        @Override
        public boolean equals(final Object pThat) {
            if (this == pThat) {
                return true;
            }
            if (pThat == null) {
                return false;
            }
            if (!(pThat instanceof FactoryKeySpec)) {
                return false;
            }
            FactoryKeySpec myThat = (FactoryKeySpec) pThat;
            return theFactory.getFactoryType() == myThat.theFactory.getFactoryType()
                    && theKeySpec.equals(myThat.theKeySpec);
        }

        @Override
        public int hashCode() {
            return theFactory.getFactoryType().hashCode() + theKeySpec.hashCode();
        }

        @Override
        public String toString() {
            return theKeySpec.toString();
        }
    }

    /**
     * FactoryKeySpec and signature definition.
     */
    static class FactorySignature {
        private final FactoryKeySpec theKeySpec;
        private final GordianSignatureSpec theSignSpec;

        /**
         * Constructor.
         * @param pKeySpec the keySpec
         * @param pSignSpec the signatureSpec
         */
        FactorySignature(final FactoryKeySpec pKeySpec,
                         final GordianSignatureSpec pSignSpec) {
            theKeySpec = pKeySpec;
            theSignSpec = pSignSpec;
        }

        @Override
        public String toString() {
            return theSignSpec.toString();
        }
    }

    /**
     * FactoryKeySpec and agreement definition.
     */
    static class FactoryAgreement {
        private final FactoryKeySpec theKeySpec;
        private final GordianAgreementSpec theAgreeSpec;

        /**
         * Constructor.
         * @param pKeySpec the keySpec
         * @param pAgreeSpec the agreementSpec
         */
        FactoryAgreement(final FactoryKeySpec pKeySpec,
                         final GordianAgreementSpec pAgreeSpec) {
            theKeySpec = pKeySpec;
            theAgreeSpec = pAgreeSpec;
        }

        @Override
        public String toString() {
            return theAgreeSpec.toString();
        }
    }

    /**
     * FactoryKeySpec and encryptor definition.
     */
    static class FactoryEncryptor {
        private final FactoryKeySpec theKeySpec;
        private final GordianEncryptorSpec theEncryptSpec;

        /**
         * Constructor.
         * @param pKeySpec the keySpec
         * @param pEncryptSpec the encryptorSpec
         */
        FactoryEncryptor(final FactoryKeySpec pKeySpec,
                         final GordianEncryptorSpec pEncryptSpec) {
            theKeySpec = pKeySpec;
            theEncryptSpec = pEncryptSpec;
        }

        @Override
        public String toString() {
            return theEncryptSpec.toString();
        }
    }

    /**
     * Obtain the list of KeySpecs to test for a given factory.
     * @param pFactory the factory
     * @return the list
     */
    static List<FactoryKeySpec> keySpecProvider(final GordianFactory pFactory) {
        /* Loop through the possible keyTypes */
        final List<FactoryKeySpec> myResult = new ArrayList<>();
        for (GordianAsymKeyType myKeyType : GordianAsymKeyType.values()) {
            /* If we are testing a single keyType, make sure this is the right one */
            if (theKeyType != null
                    && !myKeyType.equals(theKeyType)) {
                continue;
            }

            /* Add all Specs for this keyType */
            myResult.addAll(keySpecProvider(pFactory, myKeyType));
        }

        /* Return the result */
        return myResult;
    }

    /**
     * Obtain the list of KeySpecs to test for a given factory.
     * @param pFactory the factory
     * @param pKeyType the keyType
     * @return the list
     */
    static List<FactoryKeySpec> keySpecProvider(final GordianFactory pFactory,
                                                final GordianAsymKeyType pKeyType) {
        /* Loop through all the possible specs for this keyType */
        final List<FactoryKeySpec> myResult = new ArrayList<>();
        final GordianAsymFactory myFactory = pFactory.getAsymmetricFactory();
        List<GordianAsymKeySpec> mySpecs = myFactory.listAllSupportedAsymSpecs(pKeyType);
        for (GordianAsymKeySpec myKeySpec : mySpecs) {
            /* Add the keySpec */
            myResult.add(new FactoryKeySpec(pFactory, myKeySpec));

            /* If we are only testing one keySpec per type, break the loop */
            if (!allSpecs) {
                break;
            }
        }

        /* Return the result */
        return myResult;
    }

    /**
     * Update the list of Signatures to test.
     * @param pKeySpec the keySpec
     */
    static void signatureProvider(final FactoryKeySpec pKeySpec) throws OceanusException {
        /* Access the list */
        List<FactorySignature> myResult = pKeySpec.theSignatures;

        /* Access the list of possible signatures */
        final GordianAsymFactory myFactory = pKeySpec.theFactory.getAsymmetricFactory();
        final GordianSignatureFactory mySignFactory = myFactory.getSignatureFactory();
        final List<GordianSignatureSpec> mySignSpecs = mySignFactory.listAllSupportedSignatures(pKeySpec.theKeySpec.getKeyType());

        /* Skip key if there are no possible signatures */
        if (mySignSpecs.isEmpty()) {
            return;
        }

        /* Access keyPair and loop through the possible signatures */
        final GordianKeyPair myKeyPair = pKeySpec.getKeyPair();
        for (GordianSignatureSpec mySign : mySignSpecs) {
            /* Add the signature if it is supported */
            if (mySignFactory.validSignatureSpecForKeyPair(myKeyPair, mySign)) {
                myResult.add(new FactorySignature(pKeySpec, mySign));
            }
        }
    }

    /**
     * Update the list of Agreements to test.
     * @param pKeySpec the keySpec
     */
    static void agreementProvider(final FactoryKeySpec pKeySpec) throws OceanusException {
        /* Access the list */
        List<FactoryAgreement> myResult = pKeySpec.theAgreements;

        /* Access the list of possible agreements */
        final GordianAsymFactory myFactory = pKeySpec.theFactory.getAsymmetricFactory();
        final GordianAgreementFactory myAgreeFactory = myFactory.getAgreementFactory();
        final List<GordianAgreementSpec> myAgreeSpecs = myAgreeFactory.listAllSupportedAgreements(pKeySpec.theKeySpec.getKeyType());

        /* Skip key if there are no possible agreements */
        if (myAgreeSpecs.isEmpty()) {
            return;
        }

        /* Access keyPair and loop through the possible agreements */
        final GordianKeyPair myKeyPair = pKeySpec.getKeyPair();
        for (GordianAgreementSpec myAgree : myAgreeSpecs) {
            /* Add the agreement if it is supported */
            if (myAgreeFactory.validAgreementSpecForKeyPair(myKeyPair, myAgree)) {
                myResult.add(new FactoryAgreement(pKeySpec, myAgree));
            }
        }
    }

    /**
     * Update the list of Encryptors to test.
     * @param pKeySpec the keySpec
     */
    static void encryptorProvider(final FactoryKeySpec pKeySpec) throws OceanusException {
        /* Access the list */
        List<FactoryEncryptor> myResult = pKeySpec.theEncryptors;

        /* Access the list of possible encryptors */
        final GordianAsymFactory myFactory = pKeySpec.theFactory.getAsymmetricFactory();
        final GordianEncryptorFactory myEncryptFactory = myFactory.getEncryptorFactory();
        final List<GordianEncryptorSpec> mySpecs = myEncryptFactory.listAllSupportedEncryptors(pKeySpec.theKeySpec.getKeyType());

        /* Skip key if there are no possible encryptors */
        if (mySpecs.isEmpty()) {
            return;
        }

        /* Access keyPair and loop through the possible encryptors */
        final GordianKeyPair myKeyPair = pKeySpec.getKeyPair();
        for (GordianEncryptorSpec myEncrypt : mySpecs) {
            /* Add the encryptor if it is supported */
            if (myEncryptFactory.validEncryptorSpecForKeyPair(myKeyPair, myEncrypt)) {
                myResult.add(new FactoryEncryptor(pKeySpec, myEncrypt));
            }
        }
    }

    /**
     * Create the asymmetric test suite.
     * @return the test stream
     * @throws OceanusException on error
     */
    @TestFactory
    Stream<DynamicNode> asymmetricTests() throws OceanusException {
        /* Create an empty stream */
        Stream<DynamicNode> myStream = Stream.empty();

        /* Create tests */
        final Stream<DynamicNode> myBC = asymmetricTests(BCFACTORY);
        final Stream<DynamicNode> myJCA = asymmetricTests(JCAFACTORY);
        return Stream.concat(myBC, myJCA);
    }

    /**
     * Create the asymmetric test suite for a factory.
     * @param pFactory the factory
     * @return the test stream
     * @throws OceanusException on error
     */
    Stream<DynamicNode> asymmetricTests(final GordianFactory pFactory) throws OceanusException {
        /* Create an empty stream */
        Stream<DynamicNode> myStream = Stream.empty();

        /* Loop through the possible keySpecs */
        for (final FactoryKeySpec myKeySpec :  keySpecProvider(pFactory)) {
            /* Create an empty stream */
            Stream<DynamicNode> myKeyStream = Stream.empty();

            /* Add signature Tests */
            signatureProvider(myKeySpec);
            if (!myKeySpec.theSignatures.isEmpty()) {
                Stream<DynamicNode> myTests = myKeySpec.theSignatures.stream().map(x -> DynamicTest.dynamicTest(x.toString(), () -> testSignature(x)));
                myTests = Stream.of(DynamicContainer.dynamicContainer("Signatures", myTests));
                myKeyStream = Stream.concat(myKeyStream, myTests);
            }

            /* Add agreement Tests */
            agreementProvider(myKeySpec);
            if (!myKeySpec.theAgreements.isEmpty()) {
                Stream<DynamicNode> myTests = myKeySpec.theAgreements.stream().map(x -> DynamicTest.dynamicTest(x.toString(), () -> testAgreement(x)));
                myTests = Stream.of(DynamicContainer.dynamicContainer("Agreements", myTests));
                myKeyStream = Stream.concat(myKeyStream, myTests);
            }

            /* Add encryptor Tests */
            encryptorProvider(myKeySpec);
            if (!myKeySpec.theEncryptors.isEmpty()) {
                Stream<DynamicNode> myTests = myKeySpec.theEncryptors.stream().map(x -> DynamicTest.dynamicTest(x.toString(), () -> testEncryptor(x)));
                myTests = Stream.of(DynamicContainer.dynamicContainer("Encryptors", myTests));
                myKeyStream = Stream.concat(myKeyStream, myTests);
            }

            /* Add the stream */
            myStream = Stream.of(DynamicContainer.dynamicContainer(myKeySpec.theKeySpec.toString(), myKeyStream));
        }

        /* Return the stream */
        myStream = Stream.of(DynamicContainer.dynamicContainer(pFactory.getFactoryType().toString(), myStream));
        return myStream;
    }

    void testSignature(final FactorySignature pSignSpec) {
    }

    void testAgreement(final FactoryAgreement pAgreeSpec) {
    }

    void testEncryptor(final FactoryEncryptor pEncryptSpec) {
    }
}
