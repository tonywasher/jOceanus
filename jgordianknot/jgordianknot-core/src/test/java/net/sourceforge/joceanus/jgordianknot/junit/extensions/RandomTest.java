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
package net.sourceforge.joceanus.jgordianknot.junit.extensions;

import java.util.stream.Stream;

import org.bouncycastle.crypto.prng.EntropySource;
import org.bouncycastle.crypto.prng.EntropySourceProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianCipherFactory;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianPadding;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymCipher;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymCipherSpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigest;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestFactory;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestSpec;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactoryType;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianParameters;
import net.sourceforge.joceanus.jgordianknot.api.impl.GordianGenerator;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMac;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacFactory;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacSpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.cipher.GordianCoreCipher;
import net.sourceforge.joceanus.jgordianknot.impl.core.random.GordianDRBGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.core.random.GordianSP800CTRDRBG;
import net.sourceforge.joceanus.jgordianknot.impl.core.random.GordianSP800HMacDRBG;
import net.sourceforge.joceanus.jgordianknot.impl.core.random.GordianSP800HashDRBG;
import net.sourceforge.joceanus.jgordianknot.impl.core.random.GordianX931CipherDRBG;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.TethysDataConverter;

/**
 * GordianKnot DRBG testCases.
 */
public class RandomTest {
    /**
     * The standard test name.
     */
    private static final String STANDARD = "Standard";

    /**
     * The resistant test name.
     */
    private static final String RESISTANT = "Resistant";

    /**
     * The personalised test name.
     */
    private static final String PERSONALISED = "Personalised";

    /**
     * The additional test name.
     */
    private static final String ADDITIONAL = "Additional";

    /**
     * The factories.
     */
    private static GordianFactory BCFACTORY;

    /**
     * TestCase.
     */
    private static class GordianTestCase {
        /**
         * The additional input (if available).
         */
        private final String theAdditional;

        /**
         * The expected output
         */
        private final String theExpected;

        /**
         * Constructor.
         * @param pExpected the expected output
         */
        GordianTestCase(final String pExpected) {
            this(null, pExpected);
        }

        /**
         * Constructor.
         * @param pAdditional the additional input
         * @param pExpected the expected output
         */
        GordianTestCase(final String pAdditional,
                        final String pExpected) {
            theAdditional = pAdditional;
            theExpected = pExpected;
        }
    }

    /**
     * DRBGInit.
     */
    private static class GordianDRBGInit {
        /**
         * Is the Test Case prediction resistant?
         */
        private final boolean predictionResistant;

        /**
         * The Nonce.
         */
        private final String theNonce;

        /**
         * The entropy.
         */
        private final String theEntropy;

        /**
         * The personalisation.
         */
        private final String thePersonal;

        /**
         * Constructor.
         * @param pResistant is the entropy prediction resistant?
         * @param pNonce the nonce
         * @param pEntropy the entropy bytes
         */
        GordianDRBGInit(final boolean pResistant,
                        final String pNonce,
                        final String pEntropy) {
            this(pResistant, pNonce, pEntropy, null);
        }

        /**
         * Constructor.
         * @param pResistant is the entropy prediction resistant?
         * @param pNonce the nonce
         * @param pEntropy the entropy bytes
         * @param pPersonal the personalisation
         */
        GordianDRBGInit(final boolean pResistant,
                        final String pNonce,
                        final String pEntropy,
                        final String pPersonal) {
            predictionResistant = pResistant;
            theNonce = pNonce;
            theEntropy = pEntropy;
            thePersonal = pPersonal;
        }
    }

    /**
     * Entropy Source Provider.
     */
    public class GordianEntropySourceProvider
            implements EntropySourceProvider {
        /**
         * The entropy data.
         */
        private final byte[] theData;

        /**
         * Is the source prediction resistant?
         */
        private final boolean predictionResistant;

        /**
         * Constructor.
         * @param pData the entropy data
         * @param pResistant is the entropy prediction resistant?
         * @throws OceanusException on error
         */
        GordianEntropySourceProvider(final String pData,
                                     final boolean pResistant) throws OceanusException {
            theData = TethysDataConverter.hexStringToBytes(pData);
            predictionResistant = pResistant;
        }

        @Override
        public EntropySource get(final int bitsRequired) {
            return new GordianEntropySource(theData, bitsRequired, predictionResistant);
         }
    }

    /**
     * Entropy Source Provider.
     */
    public class GordianEntropySource
            implements EntropySource {
        /**
         * The entropy data.
         */
        private final byte[] data;

        /**
         * The bitsRequired each time.
         */
        private final int bitsRequired;

        /**
         * Is the source prediction resistant?
         */
        private final boolean predictionResistant;

        /**
         * The current index.
         */
        private int theIndex;

        /**
         * Constructor.
         * @param pData the entropy data
         * @param pRequired the number of bits required each time
         * @param pResistant is the entropy prediction resistant?
         */
        GordianEntropySource(final byte[] pData,
                             final int pRequired,
                             final boolean pResistant) {
            data = pData;
            bitsRequired = pRequired;
            predictionResistant = pResistant;
        }

        @Override
        public boolean isPredictionResistant() {
            return predictionResistant;
        }

        @Override
        public byte[] getEntropy() {
            /* Create the required entropy */
            byte[] rv = new byte[bitsRequired / 8];
            System.arraycopy(data, theIndex, rv, 0, rv.length);

            /* Adjust index and return */
            theIndex += bitsRequired / 8;
            return rv;
        }

        @Override
        public int entropySize() {
           return bitsRequired;
        }
    }

    /**
     * Initialise Factories.
     * @throws OceanusException on error
     */
    @BeforeAll
    public static void createSecurityFactories() throws OceanusException {
        BCFACTORY = GordianGenerator.createFactory(new GordianParameters(GordianFactoryType.BC));
    }

    /**
     * Create the drbg test suite.
     * @return the test stream
     * @throws OceanusException on error
     */
    @TestFactory
    public Stream<DynamicNode> drbgTests() throws OceanusException {
        /* Create tests */
        DynamicNode myHash = DynamicContainer.dynamicContainer("Hash", Stream.of(
                testSHA1HashDRBG(),
                testSHA512HashDRBG()
        ));
        DynamicNode myHMac = DynamicContainer.dynamicContainer("HMac", Stream.of(
                testSHA1HMacDRBG(),
                testSHA512HMacDRBG()
        ));
        DynamicNode myCTR = DynamicContainer.dynamicContainer("CTR", Stream.of(
                testAES128CtrDRBG(),
                testAES256CtrDRBG()
        ));
        DynamicNode myX931 = DynamicContainer.dynamicContainer("X931", Stream.of(
                testAES128X931DRBG()
        ));
        return Stream.of(myHash, myHMac, myCTR, myX931);
    }

    /**
     * Test random.
     * @param pRandom the random generator
     * @param pResistant is the generator prediction resistant?
     * @param pTestCase the testcase
     * @throws OceanusException on error
     */
    private void testRandom(final GordianDRBGenerator pRandom,
                            final boolean pResistant,
                            final GordianTestCase pTestCase) throws OceanusException {
        /* Access the expected bytes */
        final byte[] myExpected = TethysDataConverter.hexStringToBytes(pTestCase.theExpected);
        final byte[] myActual = new byte[myExpected.length];

        /* Access the additional input */
        final String myAdditional = pTestCase.theAdditional;
        final byte[] myXtra = myAdditional == null ? null : TethysDataConverter.hexStringToBytes(myAdditional);

        /* Generate the bytes */
        pRandom.generate(myActual, myXtra, pResistant);
        Assertions.assertArrayEquals(myExpected, myActual, "Incorrect output");
    }

    /**
     * Test SP800Hash.
     * @param pDigest the Digest
     * @param pInit the initialisation
     * @param pTestCases the testCases
     * @throws OceanusException on error
     */
    private void testHashDRBG(final GordianDigest pDigest,
                              final GordianDRBGInit pInit,
                              final GordianTestCase[] pTestCases) throws OceanusException {
        /* Access the nonce and personalisation */
        final byte[] myNonce = TethysDataConverter.hexStringToBytes(pInit.theNonce);
        final String myPers = pInit.thePersonal;
        final byte[] myPersonal = myPers == null ? null : TethysDataConverter.hexStringToBytes(myPers);

        /* Create the entropy source provider */
        final EntropySourceProvider myEntropy = new GordianEntropySourceProvider(pInit.theEntropy, true);

        /* Calculate the entropyLength */
        final int myEntropyLength = pDigest.getDigestSize() > GordianLength.LEN_256.getByteLength()
                        ? GordianSP800HashDRBG.LONG_SEED_LENGTH
                        : GordianSP800HashDRBG.SHORT_SEED_LENGTH;

        /* Create the provider */
        final GordianSP800HashDRBG myProvider = new GordianSP800HashDRBG(pDigest, myEntropy.get(myEntropyLength), myPersonal, myNonce);

        /* Run the testCases */
        for(GordianTestCase myCase : pTestCases) {
            testRandom(myProvider, pInit.predictionResistant, myCase);
        }
    }

    /**
     * Test SP800 HMac.
     * @param pMac the HMac.
     * @param pInit the initialisation
     * @param pTestCases the testCases
     * @throws OceanusException on error
     */
    private void testHMacDRBG(final GordianMac pMac,
                              final GordianDRBGInit pInit,
                              final GordianTestCase[] pTestCases) throws OceanusException {
        /* Access the nonce and personalisation */
        final byte[] myNonce = TethysDataConverter.hexStringToBytes(pInit.theNonce);
        final String myPers = pInit.thePersonal;
        final byte[] myPersonal = myPers == null ? null : TethysDataConverter.hexStringToBytes(myPers);

        /* Create the entropy source provider */
        final EntropySourceProvider myEntropy = new GordianEntropySourceProvider(pInit.theEntropy, true);

        /* Calculate the entropyLength */
        final int myEntropyLength = pMac.getMacSize() > GordianLength.LEN_256.getByteLength()
                                    ? GordianSP800HashDRBG.LONG_SEED_LENGTH
                                    : GordianSP800HashDRBG.SHORT_SEED_LENGTH;

        /* Create the provider */
        final GordianSP800HMacDRBG myProvider = new GordianSP800HMacDRBG(pMac, myEntropy.get(myEntropyLength), myPersonal, myNonce);

        /* Run the testCases */
        for(GordianTestCase myCase : pTestCases) {
            testRandom(myProvider, pInit.predictionResistant, myCase);
        }
    }

    /**
     * Test SP800 CTRCipher.
     * @param pCipher the Cipher.
     * @param pKeySize the keySize
     * @param pInit the initialisation
     * @param pTestCases the testCases
     * @throws OceanusException on error
     */
    private void testCTRCipherDRBG(final GordianCoreCipher<GordianSymKeySpec> pCipher,
                                   final GordianLength pKeySize,
                                   final GordianDRBGInit pInit,
                                   final GordianTestCase[] pTestCases) throws OceanusException {
        /* Access the nonce and personalisation */
        final byte[] myNonce = TethysDataConverter.hexStringToBytes(pInit.theNonce);
        final String myPers = pInit.thePersonal;
        final byte[] myPersonal = myPers == null ? null : TethysDataConverter.hexStringToBytes(myPers);

        /* Create the entropy source provider */
        final EntropySourceProvider myEntropy = new GordianEntropySourceProvider(pInit.theEntropy, true);

        /* Calculate the entropyLength */
        final int myEntropyLength = pKeySize.getLength() + pCipher.getKeyType().getBlockLength().getLength();

        /* Create the provider */
        final GordianSP800CTRDRBG myProvider = new GordianSP800CTRDRBG(pCipher, myEntropy.get(myEntropyLength), myPersonal, myNonce);

        /* Run the testCases */
        for(GordianTestCase myCase : pTestCases) {
            testRandom(myProvider, pInit.predictionResistant, myCase);
        }
    }

    /**
     * Test SP800 CTRCipher.
     * @param pCipher the Cipher.
     * @param pKey the key
     * @param pInit the initialisation
     * @param pTestCases the testCases
     * @throws OceanusException on error
     */
    private void testX931CipherDRBG(final GordianCoreCipher<GordianSymKeySpec> pCipher,
                                    final String pKey,
                                    final GordianDRBGInit pInit,
                                    final GordianTestCase[] pTestCases) throws OceanusException {
        /* Access the nonce and key */
        final byte[] myNonce = TethysDataConverter.hexStringToBytes(pInit.theNonce);
        final byte[] myKey = TethysDataConverter.hexStringToBytes(pKey);

        /* Create the entropy source provider */
        final EntropySourceProvider myEntropy = new GordianEntropySourceProvider(pInit.theEntropy, true);

        /* Calculate the entropyLength */
        final int myEntropyLength = pCipher.getKeyType().getBlockLength().getLength();

        /* Initialise the cipher */
        pCipher.initCipher(myKey);

        /* Create the provider */
        final GordianX931CipherDRBG myProvider = new GordianX931CipherDRBG((GordianSymCipher)pCipher, myEntropy.get(myEntropyLength), myNonce);

        /* Run the testCases */
        for(GordianTestCase myCase : pTestCases) {
            testRandom(myProvider, pInit.predictionResistant, myCase);
        }
    }

    /* The SHA1 Constants */
    private static final String sha1Entropy = "000102030405060708090A0B0C0D0E0F101112131415161718191A1B1C1D1E1F202122232425262728292A2B2C2D2E2F30313233343536"
            + "808182838485868788898A8B8C8D8E8F909192939495969798999A9B9C9D9E9FA0A1A2A3A4A5A6A7A8A9AAABACADAEAFB0B1B2B3B4B5B6"
            + "C0C1C2C3C4C5C6C7C8C9CACBCCCDCECFD0D1D2D3D4D5D6D7D8D9DADBDCDDDEDFE0E1E2E3E4E5E6E7E8E9EAEBECEDEEEFF0F1F2F3F4F5F6";
    private static final String sha1Nonce = "2021222324";
    private static final String sha1Personal = "404142434445464748494A4B4C4D4E4F505152535455565758595A5B5C5D5E5F606162636465666768696A6B6C6D6E6F70717273747576";
    private static final String sha1Add1 = "606162636465666768696A6B6C6D6E6F707172737475767778797A7B7C7D7E7F808182838485868788898A8B8C8D8E8F90919293949596";
    private static final String sha1Add2 = "A0A1A2A3A4A5A6A7A8A9AAABACADAEAFB0B1B2B3B4B5B6B7B8B9BABBBCBDBEBFC0C1C2C3C4C5C6C7C8C9CACBCCCDCECFD0D1D2D3D4D5D6";

    /**
     * Create Sha1 Hash DRBG Test.
     * @return the test
     * @throws OceanusException on error
     */
    private DynamicNode testSHA1HashDRBG() throws OceanusException {
        /* The SHA1 Init */
        final GordianDRBGInit myInitF =  new GordianDRBGInit(false, sha1Nonce, sha1Entropy);
        final GordianDRBGInit myInitFP =  new GordianDRBGInit(false, sha1Nonce, sha1Entropy, sha1Personal);
        final GordianDRBGInit myInitT =  new GordianDRBGInit(true, sha1Nonce, sha1Entropy);
        final GordianDRBGInit myInitTP =  new GordianDRBGInit(true, sha1Nonce, sha1Entropy, sha1Personal);

        /* The Expected results */
        GordianTestCase[]  myTestsF = new GordianTestCase[] {
                new GordianTestCase("9F7CFF1ECA23E750F66326969F11800F12088BA68E441D15D888B3FE12BF66FE057494F4546DE2F1"),
                new GordianTestCase("B77AA5C0CD55BBCEED7574AF223AFD988C7EEC8EFF4A94E5E89D26A04F58FA79F5E0D3702D7A9A6A")
                };
        GordianTestCase[]  myTestsFP = new GordianTestCase[] {
                new GordianTestCase("AB438BD3B01A0AF85CFEE29F7D7B71621C4908B909124D430E7B406FB1086EA994C582E0D656D989"),
                new GordianTestCase("29D9098F987E7005314A0F51B3DD2B8122F4AED706735DE6AD5DDBF223177C1E5F3AEBC52FAB90B9")
        };
        GordianTestCase[]  myTestsFA = new GordianTestCase[] {
                new GordianTestCase(sha1Add1,"E76B4EDD5C865BC8AFD809A59B69B429AC7F4352A579BCF3F75E56249A3491F87C3CA6848B0FAB25"),
                new GordianTestCase(sha1Add2,"6577B6B4F87A93240B199FE51A3B335313683103DECE171E3256FB7E803586CA4E45DD242EB01F70")
        };
        GordianTestCase[]  myTestsT = new GordianTestCase[] {
                new GordianTestCase("56EF4913373994D5539F4D7D17AFE7448CDF5E72416CC6A71A340059FA0D5AE526B23250C46C0944"),
                new GordianTestCase("575B37A2739814F966C63B60A2C4F149CA9ACC84FC4B25493289B085C67B2E30F5F0B99A2C349E2A")
        };
        GordianTestCase[]  myTestsTP = new GordianTestCase[] {
                new GordianTestCase("532CA1165DCFF21C55592687639884AF4BC4B057DF8F41DE653AB44E2ADEC7C9303E75ABE277EDBF"),
                new GordianTestCase("73C2C67C696D686D0C4DBCEB5C2AF7DDF6F020B6874FAE4390F102117ECAAFF54418529A367005A0")
        };
        GordianTestCase[]  myTestsTA = new GordianTestCase[] {
                new GordianTestCase(sha1Add1,"183C242A1430E46C4ED70B4DBE1BF9AB0AB8721CDCA2A2D1820AD6F6C956858543B2AA191D8D1287"),
                new GordianTestCase(sha1Add2,"F196F9BD021C745CBD5AC7BFCE48EAAF0D0E7C091FBF436940E63A198EE770D9A4F0718669AF2BC9")
        };

        /* Create the digest */
        final GordianDigestFactory myFactory = BCFACTORY.getDigestFactory();
        final GordianDigest myDigest = myFactory.createDigest(GordianDigestSpec.sha1());

        /* Create a standard stream */
        Stream<DynamicNode> myStandard = Stream.of(DynamicTest.dynamicTest(STANDARD,
                () -> testHashDRBG(myDigest, myInitF, myTestsF)));
        myStandard = Stream.concat(myStandard, Stream.of(DynamicTest.dynamicTest(PERSONALISED,
                () -> testHashDRBG(myDigest, myInitFP, myTestsFP))));
        myStandard = Stream.concat(myStandard, Stream.of(DynamicTest.dynamicTest(ADDITIONAL,
                () -> testHashDRBG(myDigest, myInitF, myTestsFA))));
        myStandard = Stream.of(DynamicContainer.dynamicContainer(STANDARD, myStandard));

        /* Create a resistant stream */
        Stream<DynamicNode> myResistant = Stream.of(DynamicTest.dynamicTest(STANDARD,
                () -> testHashDRBG(myDigest, myInitT, myTestsT)));
        myResistant = Stream.concat(myResistant, Stream.of(DynamicTest.dynamicTest(PERSONALISED,
                () -> testHashDRBG(myDigest, myInitTP, myTestsTP))));
        myResistant = Stream.concat(myResistant, Stream.of(DynamicTest.dynamicTest(ADDITIONAL,
                () -> testHashDRBG(myDigest, myInitT, myTestsTA))));
        myResistant = Stream.of(DynamicContainer.dynamicContainer(RESISTANT, myResistant));

        /* Return the test */
        return DynamicContainer.dynamicContainer("Sha1", Stream.concat(myStandard, myResistant));
    }

    /**
     * Create Sha1 HMac DRBG test .
     * @return the test
     * @throws OceanusException on error
     */
    private DynamicNode testSHA1HMacDRBG() throws OceanusException {
        /* The SHA1 Init */
        final GordianDRBGInit myInitF =  new GordianDRBGInit(false, sha1Nonce, sha1Entropy);
        final GordianDRBGInit myInitFP =  new GordianDRBGInit(false, sha1Nonce, sha1Entropy, sha1Personal);
        final GordianDRBGInit myInitT =  new GordianDRBGInit(true, sha1Nonce, sha1Entropy);
        final GordianDRBGInit myInitTP =  new GordianDRBGInit(true, sha1Nonce, sha1Entropy, sha1Personal);

        /* The Expected results */
        GordianTestCase[]  myTestsF = new GordianTestCase[] {
                new GordianTestCase("5A7D3B449F481CB38DF79AD2B1FCC01E57F8135E8C0B22CD0630BFB0127FB5408C8EFC17A929896E"),
                new GordianTestCase("82cf772ec3e84b00fc74f5df104efbfb2428554e9ce367d03aeade37827fa8e9cb6a08196115d948")
        };
        GordianTestCase[]  myTestsFP = new GordianTestCase[] {
                new GordianTestCase("B3BD05246CBA12A64735A4E3FDE599BC1BE30F439BD060208EEA7D71F9D123DF47B3CE069D98EDE6"),
                new GordianTestCase("B5DADA380E2872DF935BCA55B882C8C9376902AB639765472B71ACEBE2EA8B1B6B49629CB67317E0")
        };
        GordianTestCase[]  myTestsFA = new GordianTestCase[] {
                new GordianTestCase(sha1Add1,"C7AAAC583C6EF6300714C2CC5D06C148CFFB40449AD0BB26FAC0497B5C57E161E36681BCC930CE80"),
                new GordianTestCase(sha1Add2,"6EBD2B7B5E0A2AD7A24B1BF9A1DBA47D43271719B9C37B7FE81BA94045A14A7CB514B446666EA5A7")
        };
        GordianTestCase[]  myTestsT = new GordianTestCase[] {
                new GordianTestCase("FEC4597F06A3A8CC8529D59557B9E661053809C0BC0EFC282ABD87605CC90CBA9B8633DCB1DAE02E"),
                new GordianTestCase("84ADD5E2D2041C01723A4DE4335B13EFDF16B0E51A0AD39BD15E862E644F31E4A2D7D843E57C5968")
        };
        GordianTestCase[]  myTestsTP = new GordianTestCase[] {
                new GordianTestCase("6C37FDD729AA40F80BC6AB08CA7CC649794F6998B57081E4220F22C5C283E2C91B8E305AB869C625"),
                new GordianTestCase("CAF57DCFEA393B9236BF691FA456FEA7FDF1DF8361482CA54D5FA723F4C88B4FA504BF03277FA783")
        };
        GordianTestCase[]  myTestsTA = new GordianTestCase[] {
                new GordianTestCase(sha1Add1,"A1BA8FA58BB5013F43F7B6ED52B4539FA16DC77957AEE815B9C07004C7E992EB8C7E591964AFEEA2"),
                new GordianTestCase(sha1Add2,"84264A73A818C95C2F424B37D3CC990B046FB50C2DC64A164211889A010F2471A0912FFEA1BF0195")
        };

        /* Create the mac */
        final GordianMacFactory myFactory = BCFACTORY.getMacFactory();
        final GordianMac myMac = myFactory.createMac(GordianMacSpec.hMac(GordianDigestSpec.sha1()));

        /* Create a standard stream */
        Stream<DynamicNode> myStandard = Stream.of(DynamicTest.dynamicTest(STANDARD,
                () -> testHMacDRBG(myMac, myInitF, myTestsF)));
        myStandard = Stream.concat(myStandard, Stream.of(DynamicTest.dynamicTest(PERSONALISED,
                () -> testHMacDRBG(myMac, myInitFP, myTestsFP))));
        myStandard = Stream.concat(myStandard, Stream.of(DynamicTest.dynamicTest(ADDITIONAL,
                () -> testHMacDRBG(myMac, myInitF, myTestsFA))));
        myStandard = Stream.of(DynamicContainer.dynamicContainer(STANDARD, myStandard));

        /* Create a resistant stream */
        Stream<DynamicNode> myResistant = Stream.of(DynamicTest.dynamicTest(STANDARD,
                () -> testHMacDRBG(myMac, myInitT, myTestsT)));
        myResistant = Stream.concat(myResistant, Stream.of(DynamicTest.dynamicTest(PERSONALISED,
                () -> testHMacDRBG(myMac, myInitTP, myTestsTP))));
        myResistant = Stream.concat(myResistant, Stream.of(DynamicTest.dynamicTest(ADDITIONAL,
                () -> testHMacDRBG(myMac, myInitT, myTestsTA))));
        myResistant = Stream.of(DynamicContainer.dynamicContainer(RESISTANT, myResistant));

        /* Return the test */
        return DynamicContainer.dynamicContainer("Sha1", Stream.concat(myStandard, myResistant));
    }

    /* The SHA512 Constants */
    private static final String sha512Entropy = "000102030405060708090A0B0C0D0E" +
            "0F101112131415161718191A1B1C1D1E1F20212223242526" +
            "2728292A2B2C2D2E2F303132333435363738393A3B3C3D3E" +
            "3F404142434445464748494A4B4C4D4E4F50515253545556" +
            "5758595A5B5C5D5E5F606162636465666768696A6B6C6D6E" +
            "808182838485868788898A8B8C8D8E" +
            "8F909192939495969798999A9B9C9D9E9FA0A1A2A3A4A5A6" +
            "A7A8A9AAABACADAEAFB0B1B2B3B4B5B6B7B8B9BABBBCBDBE" +
            "BFC0C1C2C3C4C5C6C7C8C9CACBCCCDCECFD0D1D2D3D4D5D6" +
            "D7D8D9DADBDCDDDEDFE0E1E2E3E4E5E6E7E8E9EAEBECEDEE" +
            "C0C1C2C3C4C5C6C7C8C9CACBCCCDCE" +
            "CFD0D1D2D3D4D5D6D7D8D9DADBDCDDDEDFE0E1E2E3E4E5E6" +
            "E7E8E9EAEBECEDEEEFF0F1F2F3F4F5F6F7F8F9FAFBFCFDFE" +
            "FF000102030405060708090A0B0C0D0E0F10111213141516" +
            "1718191A1B1C1D1E1F202122232425262728292A2B2C2D2E";
    private static final String sha512Nonce = "202122232425262728292A2B2C2D2E2F";
    private static final String sha512Personal = "404142434445464748494A4B4C4D4E" +
            "4F505152535455565758595A5B5C5D5E5F60616263646566" +
            "6768696A6B6C6D6E6F707172737475767778797A7B7C7D7E" +
            "7F808182838485868788898A8B8C8D8E8F90919293949596" +
            "9798999A9B9C9D9E9FA0A1A2A3A4A5A6A7A8A9AAABACADAE";
    private static final String sha512Add1 = "606162636465666768696A6B6C6D6E" +
            "6F707172737475767778797A7B7C7D7E7F80818283848586" +
            "8788898A8B8C8D8E8F909192939495969798999A9B9C9D9E" +
            "9FA0A1A2A3A4A5A6A7A8A9AAABACADAEAFB0B1B2B3B4B5B6" +
            "B7B8B9BABBBCBDBEBFC0C1C2C3C4C5C6C7C8C9CACBCCCDCE";
    private static final String sha512Add2 = "A0A1A2A3A4A5A6A7A8A9AAABACADAE" +
            "AFB0B1B2B3B4B5B6B7B8B9BABBBCBDBEBFC0C1C2C3C4C5C6" +
            "C7C8C9CACBCCCDCECFD0D1D2D3D4D5D6D7D8D9DADBDCDDDE" +
            "DFE0E1E2E3E4E5E6E7E8E9EAEBECEDEEEFF0F1F2F3F4F5F6" +
            "F7F8F9FAFBFCFDFEFF000102030405060708090A0B0C0D0E";

    /**
     * Create Sha512 Hash DRBG test.
     * @return the test
     * @throws OceanusException on error
     */
    private DynamicNode testSHA512HashDRBG() throws OceanusException {
        /* The SHA512 Init */
        final GordianDRBGInit myInitFP =  new GordianDRBGInit(false, sha512Nonce, sha512Entropy, sha512Personal);
        final GordianDRBGInit myInitT =  new GordianDRBGInit(true, sha512Nonce, sha512Entropy);
        final GordianDRBGInit myInitTP =  new GordianDRBGInit(true, sha512Nonce, sha512Entropy, sha512Personal);

        /* The Expected results */
        GordianTestCase[]  myTestsFPA = new GordianTestCase[] {
                new GordianTestCase(sha512Add1, "DA126CF95C6BF97E" +
                        "2F731F2137A907ACC70FD7AC9EBACD1C6E31C74029B052E3" +
                        "AABC48F3B00993F2B2381F7650A55322A968C86E05DE88E6" +
                        "367F6EF89A601DB4342E9086C7AC13B5E56C32E9E668040B" +
                        "73847893C5BFD38A1CF44F348B4EEE4CD68ADB7E7B8C837F" +
                        "19BC4F902761F7CFF24AB1D704FD11C4E929D8553753B55D"),
                new GordianTestCase(sha512Add2,"400B977CE8A2BB6A" +
                        "84C6FD1CF901459685ABF5408CFF4588CEDF52E2D2DC300A" +
                        "A9B4FAED8CD0161C2172B1FD269253195883D6EBF21020F2" +
                        "C20E5F2C81AE60C8595B834A229B1F5B726C1125717E6207" +
                        "8886EF38E61E32707AD5F8116C6393DFB6E7C7AE0E8E92BB" +
                        "D7E0C3D04BBA02F5169F2F569A58158915FEE4C9D28D45DB")
        };
        GordianTestCase[]  myTestsT = new GordianTestCase[] {
                new GordianTestCase("F93CA6855590A77F" +
                        "07354097E90E026648B6115DF008FFEDBD9D9811F54E8286" +
                        "EF00FDD6BA1E58DF2535E3FBDD9A9BA3754A97F36EE83322" +
                        "1582060A1F37FCE4EE8826636B28EAD589593F4CA8B64738" +
                        "8F24EB3F0A34796968D21BDEE6F81FD5DF93536F935937B8" +
                        "025EC8CBF57DDB0C61F2E41463CC1516D657DA2829C6BF90"),
                new GordianTestCase("4817618F48C60FB1" +
                        "CE5BFBDA0CAF4591882A31F6EE3FE0F78779992A06EC60F3" +
                        "7FB9A8D6108C231F0A927754B0599FA4FA27A4E25E065EF0" +
                        "3085B892979DC0E7A1080883CAEBFDFD3665A8F2D061C521" +
                        "F7D6E3DA2AF8B97B6B43B6EC831AF515070A83BBB9AC95ED" +
                        "4EF49B756A2377A5F0833D847E27A88DDB0C2CE4AD782E7B")
        };
        GordianTestCase[]  myTestsTP = new GordianTestCase[] {
                new GordianTestCase("22EB93A67911DA73" +
                        "85D9180C78127DE1A04FF713114C07C9C615F7CC5EF72744" +
                        "A2DDCD7C3CB85E65DED8EF5F240FBDCBEBBDE2BAAC8ECF7D" +
                        "CBC8AC333E54607AD41DC495D83DF72A05EF55B127C1441C" +
                        "9A0EFFDA2C7954DB6C2D04342EB812E5E0B11D6C395F41ED" +
                        "A2702ECE5BA479E2DFA18F953097492636C12FE30CE5C968"),
                new GordianTestCase("E66698CFBF1B3F2E" +
                        "919C03036E584EAA81CF1C6666240AF05F70637043733954" +
                        "D8A1E5A66A04C53C6900FDC145D4A3A80A31F5868ACE9AC9" +
                        "4E14E2051F624A05EEA1F8B684AA5410BCE315E76EA07C71" +
                        "5D6F34731320FF0DCF78D795E6EFA2DF92B98BE636CDFBA2" +
                        "9008DD392112AEC202F2E481CB9D83F987FEA69CD1B368BB")
        };
        GordianTestCase[]  myTestsTA = new GordianTestCase[] {
                new GordianTestCase(sha512Add1,"0455DD4AD7DBACB2" +
                        "410BE58DF7248D765A4547ABAEE1743B0BCAD37EBD06DA7C" +
                        "F7CE5E2216E525327E9E2005EBEF2CE53BD733B18128627D" +
                        "3FD6153089373AF2606A1584646A0EA488BFEF45228699A0" +
                        "89CEA8AEC44502D86D9591F3552C688B7F7B45FCB0C3C2B9" +
                        "43C1CD8A6FC63DF4D81C3DA543C9CF2843855EA84E4F959C"),
                new GordianTestCase(sha512Add2,"C047D46D7F614E4E" +
                        "4A7952C79A451F8F7ACA379967E2977C401C626A2ED70D74" +
                        "A63660579A354115BC8C8C8CC3AEA3050686A0CFCDB6FA9C" +
                        "F78D4C2165BAF851C6F9B1CD16A2E14C15C6DAAC56C16E75" +
                        "FC84A14D58B41622E88B0F1B1995587FD8BAA999CBA98025" +
                        "4C8AB9A9691DF7B84D88B639A9A3106DEABEB63748B99C09")
        };
        GordianTestCase[]  myTestsTPA = new GordianTestCase[] {
                new GordianTestCase(sha512Add1,"7596A76372308BD5" +
                        "A5613439934678B35521A94D81ABFE63A21ACF61ABB88B61" +
                        "E86A12C37F308F2BBBE32BE4B38D03AE808386494D70EF52" +
                        "E9E1365DD18B7784CAB826F31D47579E4D57F69D8BF3152B" +
                        "95741946CEBE58571DF58ED39980D9AF44E69F01E8989759" +
                        "8E40171101A0E3302838E0AD9E849C01988993CF9F6E5263"),
                new GordianTestCase(sha512Add2,"DBE5EE36FCD85301" +
                        "303E1C3617C1AC5E23C08885D0BEFAAD0C85A0D89F85B9F1" +
                        "6ECE3D88A24EB96504F2F13EFA7049621782F5DE2C416A0D" +
                        "294CCFE53545C4E309C48E1E285A2B829A574B72B3C2FBE1" +
                        "34D01E3706B486F2401B9820E17298A342666918E15B8462" +
                        "87F8C5AF2D96B20FAF3D0BB392E15F4A06CDB0DECD1B6AD7")
        };

        /* Build the digest */
        final GordianDigestFactory myFactory = BCFACTORY.getDigestFactory();
        final GordianDigest myDigest = myFactory.createDigest(GordianDigestSpec.sha2(GordianLength.LEN_512));

        /* Create a standard stream */
        Stream<DynamicNode> myStandard = Stream.of(DynamicTest.dynamicTest(PERSONALISED + ADDITIONAL,
                () -> testHashDRBG(myDigest, myInitFP, myTestsFPA)));
        myStandard = Stream.of(DynamicContainer.dynamicContainer(STANDARD, myStandard));

        /* Create a resistant stream */
        Stream<DynamicNode> myResistant = Stream.of(DynamicTest.dynamicTest(STANDARD,
                () -> testHashDRBG(myDigest, myInitT, myTestsT)));
        myResistant = Stream.concat(myResistant, Stream.of(DynamicTest.dynamicTest(PERSONALISED,
                () -> testHashDRBG(myDigest, myInitTP, myTestsTP))));
        myResistant = Stream.concat(myResistant, Stream.of(DynamicTest.dynamicTest(ADDITIONAL,
                () -> testHashDRBG(myDigest, myInitT, myTestsTA))));
        myResistant = Stream.concat(myResistant, Stream.of(DynamicTest.dynamicTest(PERSONALISED + ADDITIONAL,
                () -> testHashDRBG(myDigest, myInitTP, myTestsTPA))));
        myResistant = Stream.of(DynamicContainer.dynamicContainer(RESISTANT, myResistant));

        /* Return the test */
        return DynamicContainer.dynamicContainer("Sha512", Stream.concat(myStandard, myResistant));
    }

    /**
     * Create Sha512 HMac DRBG test.
     * @return the test
     * @throws OceanusException on error
     */
    private DynamicNode testSHA512HMacDRBG() throws OceanusException {
        /* The SHA512 Init */
        final GordianDRBGInit myInitFP =  new GordianDRBGInit(false, sha512Nonce, sha512Entropy, sha512Personal);
        final GordianDRBGInit myInitT =  new GordianDRBGInit(true, sha512Nonce, sha512Entropy);
        final GordianDRBGInit myInitTP =  new GordianDRBGInit(true, sha512Nonce, sha512Entropy, sha512Personal);

        /* The Expected results */
        GordianTestCase[]  myTestsFP = new GordianTestCase[] {
                new GordianTestCase("2A5FF6520C20F66E" +
                        "D5EA431BD4AEAC58F975EEC9A015137D5C94B73AA09CB8B5" +
                        "9D611DDEECEB34A52BB999424009EB9EAC5353F92A6699D2" +
                        "0A02164EEBBC6492941E10426323898465DFD731C7E04730" +
                        "60A5AA8973841FDF3446FB6E72A58DA8BDA2A57A36F3DD98" +
                        "6DF85C8A5C6FF31CDE660BF8A841B21DD6AA9D3AC356B87B"),
                new GordianTestCase("0EDC8D7D7CEEC7FE" +
                        "36333FB30C0A9A4B27AA0BECBF075568B006C1C3693B1C29" +
                        "0F84769C213F98EB5880909EDF068FDA6BFC43503987BBBD" +
                        "4FC23AFBE982FE4B4B007910CC4874EEC217405421C8D8A1" +
                        "BA87EC684D0AF9A6101D9DB787AE82C3A6A25ED478DF1B12" +
                        "212CEC325466F3AC7C48A56166DD0B119C8673A1A9D54F67")
        };
        GordianTestCase[]  myTestsFPA = new GordianTestCase[] {
                new GordianTestCase(sha512Add1, "7AE31A2DEC31075F" +
                        "E5972660C16D22ECC0D415C5693001BE5A468B590BC1AE2C" +
                        "43F647F8D681AEEA0D87B79B0B4E5D089CA2C9D327534234" +
                        "0254E6B04690D77A71A294DA9568479EEF8BB2A2110F18B6" +
                        "22F60F35235DE0E8F9D7E98105D84AA24AF0757AF005DFD5" +
                        "2FA51DE3F44FCE0C5F3A27FCE8B0F6E4A3F7C7B53CE34A3D"),
                new GordianTestCase(sha512Add2,"D83A8084630F286D" +
                        "A4DB49B9F6F608C8993F7F1397EA0D6F4A72CF3EF2733A11" +
                        "AB823C29F2EBDEC3EDE962F93D920A1DB59C84E1E879C29F" +
                        "5F9995FC3A6A3AF9B587CA7C13EA197D423E81E1D6469942" +
                        "B6E2CA83A97E91F6B298266AC148A1809776C26AF5E239A5" +
                        "5A2BEB9E752203A694E1F3FE2B3E6A0C9C314421CDB55FBD")
        };
        GordianTestCase[]  myTestsT = new GordianTestCase[] {
                new GordianTestCase("28FD6060C4F35F4D" +
                        "317AB2060EE32019E0DAA330F3F5650BBCA57CB67EE6AF1C" +
                        "6F25D1B01F3601EDA85DC2ED29A9B2BA4C85CF491CE7185F" +
                        "1A2BD9378AE3C655BD1CEC2EE108AE7FC382989F6D4FEA8A" +
                        "B01499697C2F07945CE02C5ED617D04287FEAF3BA638A4CE" +
                        "F3BB6B827E40AF16279580FCF1FDAD830930F7FDE341E2AF"),
                new GordianTestCase("C0B1601AFE39338B" +
                        "58DC2BE7C256AEBE3C21C5A939BEEC7E97B3528AC420F0C6" +
                        "341847187666E0FF578A8EB0A37809F877365A28DF2FA0F0" +
                        "6354A6F02496747369375B9A9D6B756FDC4A8FB308E08256" +
                        "9D79A85BB960F747256626389A3B45B0ABE7ECBC39D5CD7B" +
                        "2C18DF2E5FDE8C9B8D43474C54B6F9839468445929B438C7")
        };
        GordianTestCase[]  myTestsTP = new GordianTestCase[] {
                new GordianTestCase("AAE4DC3C9ECC74D9" +
                        "061DD527117EF3D29E1E52B26853C539D6CA797E8DA3D0BB" +
                        "171D8E30B8B194D8C28F7F6BE3B986B88506DC6A01B294A7" +
                        "165DD1C3470F7BE7B396AA0DB7D50C4051E7C7E1C8A7D21A" +
                        "2B5878C0BCB163CAA79366E7A1162FDC88429616CD3E6977" +
                        "8D327520A6BBBF71D8AA2E03EC4A9DAA0E77CF93E1EE30D2"),
                new GordianTestCase("129FF6D31A23FFBC" +
                        "870632B35EE477C2280DDD2ECDABEDB900C78418BE2D243B" +
                        "B9D8E5093ECE7B6BF48638D8F704D134ADDEB7F4E9D5C142" +
                        "CD05683E72B516486AF24AEC15D61E81E270DD4EBED91B62" +
                        "12EB8896A6250D5C8BC3A4A12F7E3068FBDF856F47EB23D3" +
                        "79F82C1EBCD1585FB260B9C0C42625FBCEE68CAD773CD5B1")
        };
        GordianTestCase[]  myTestsTA = new GordianTestCase[] {
                new GordianTestCase(sha512Add1,"72691D2103FB567C" +
                        "CD30370715B36666F63430087B1C688281CA0974DB456BDB" +
                        "A7EB5C48CFF62EA05F9508F3B530CE995A272B11EC079C13" +
                        "923EEF8E011A93C19B58CC6716BC7CB8BD886CAA60C14D85" +
                        "C023348BD77738C475D6C7E1D9BFF4B12C43D8CC73F838DC" +
                        "4F8BD476CF8328EEB71B3D873D6B7B859C9B21065638FF95"),
                new GordianTestCase(sha512Add2,"8570DA3D47E1E160" +
                        "5CF3E44B8D328B995EFC64107B6292D1B1036B5F88CE3160" +
                        "2F12BEB71D801C0942E7C0864B3DB67A9356DB203490D881" +
                        "24FE86BCE38AC2269B4FDA6ABAA884039DF80A0336A24D79" +
                        "1EB3067C8F5F0CF0F18DD73B66A7B316FB19E02835CC6293" +
                        "65FCD1D3BE640178ED9093B91B36E1D68135F2785BFF505C")
        };
        GordianTestCase[]  myTestsTPA = new GordianTestCase[] {
                new GordianTestCase(sha512Add1,"B8E827652175E6E0" +
                        "6E513C7BE94B5810C14ED94AD903647940CAEB7EE014C848" +
                        "8DCBBE6D4D6616D06656A3DC707CDAC4F02EE6D8408C065F" +
                        "CB068C0760DA47C5D60E5D70D09DC3929B6979615D117F7B" +
                        "EDCC661A98514B3A1F55B2CBABDCA59F11823E4838065F1F" +
                        "8431CBF28A577738234AF3F188C7190CC19739E72E9BBFFF"),
                new GordianTestCase(sha512Add2,"7ED41B9CFDC8C256" +
                        "83BBB4C553CC2DC61F690E62ABC9F038A16B8C519690CABE" +
                        "BD1B5C196C57CF759BB9871BE0C163A57315EA96F615136D" +
                        "064572F09F26D659D24211F9610FFCDFFDA8CE23FFA96735" +
                        "7595182660877766035EED800B05364CE324A75EB63FD9B3" +
                        "EED956D147480B1D0A42DF8AA990BB628666F6F61D60CBE2")
        };

        /* Create the mac */
        final GordianMacFactory myFactory = BCFACTORY.getMacFactory();
        final GordianMac myMac = myFactory.createMac(GordianMacSpec.hMac(GordianDigestSpec.sha2(GordianLength.LEN_512)));

        /* Create a standard stream */
        Stream<DynamicNode> myStandard = Stream.of(DynamicTest.dynamicTest(PERSONALISED,
                () -> testHMacDRBG(myMac, myInitFP, myTestsFP)));
        myStandard = Stream.concat(myStandard, Stream.of(DynamicTest.dynamicTest(PERSONALISED + ADDITIONAL,
                () -> testHMacDRBG(myMac, myInitFP, myTestsFPA))));
        myStandard = Stream.of(DynamicContainer.dynamicContainer(STANDARD, myStandard));

        /* Create a resistant stream */
        Stream<DynamicNode> myResistant = Stream.of(DynamicTest.dynamicTest(STANDARD,
                () -> testHMacDRBG(myMac, myInitT, myTestsT)));
        myResistant = Stream.concat(myResistant, Stream.of(DynamicTest.dynamicTest(PERSONALISED,
                () -> testHMacDRBG(myMac, myInitTP, myTestsTP))));
        myResistant = Stream.concat(myResistant, Stream.of(DynamicTest.dynamicTest(ADDITIONAL,
                () -> testHMacDRBG(myMac, myInitT, myTestsTA))));
        myResistant = Stream.concat(myResistant, Stream.of(DynamicTest.dynamicTest(PERSONALISED + ADDITIONAL,
                () -> testHMacDRBG(myMac, myInitTP, myTestsTPA))));
        myResistant = Stream.of(DynamicContainer.dynamicContainer(RESISTANT, myResistant));

        /* Return the test */
        return DynamicContainer.dynamicContainer("Sha512", Stream.concat(myStandard, myResistant));
    }

    /* The AES 128 Constants */
    private static final String aes128Entropy = "0001020304050607"+
            "08090A0B0C0D0E0F101112131415161718191A1B1C1D1E1F"+
            "8081828384858687"+
            "88898A8B8C8D8E8F909192939495969798999A9B9C9D9E9F"+
            "C0C1C2C3C4C5C6C7"+
            "C8C9CACBCCCDCECFD0D1D2D3D4D5D6D7D8D9DADBDCDDDEDF";
    private static final String aes128Nonce = "2021222324252627";
    private static final String aes128Personal = "404142434445464748494A4B4C4D4E4F505152535455565758595A5B5C5D5E5F";
    private static final String aes128Add1 = "606162636465666768696A6B6C6D6E6F707172737475767778797A7B7C7D7E7F";
    private static final String aes128Add2 = "A0A1A2A3A4A5A6A7A8A9AAABACADAEAFB0B1B2B3B4B5B6B7B8B9BABBBCBDBEBF";

    /**
     * Create aes128 Ctr DRBG test.
     * @return the test
     * @throws OceanusException on error
     */
    private DynamicNode testAES128CtrDRBG() throws OceanusException {
        /* The AES128 Init */
        final GordianDRBGInit myInitF =  new GordianDRBGInit(false, aes128Nonce, aes128Entropy);
        final GordianDRBGInit myInitFP =  new GordianDRBGInit(false, aes128Nonce, aes128Entropy, aes128Personal);
        final GordianDRBGInit myInitT =  new GordianDRBGInit(true, aes128Nonce, aes128Entropy);
        final GordianDRBGInit myInitTP =  new GordianDRBGInit(true, aes128Nonce, aes128Entropy, aes128Personal);

        /* The Expected results */
        GordianTestCase[]  myTestsF = new GordianTestCase[] {
                new GordianTestCase("8CF59C8CF6888B96EB1C1E3E79D82387AF08A9E5FF75E23F1FBCD4559B6B997E"),
                new GordianTestCase("69CDEF912C692D61B1DA4C05146B52EB7B8849BD87937835328254EC25A9180E")
        };
        GordianTestCase[]  myTestsFP = new GordianTestCase[] {
                new GordianTestCase("18FDEFBDC43D7A36D5D6D862205765D1D701C9F237007030DF1B8E70EE4EEE29"),
                new GordianTestCase("9888F1D38BB1CCE31B363AA1BD9B39616876C30DEE1FF0B7BD8C4C441715C833")
        };
        GordianTestCase[]  myTestsFA = new GordianTestCase[] {
                new GordianTestCase(aes128Add1,"E8C74A4B7BFFB53BEB80E78CA86BB6DF70E2032AEB473E0DD54D2339CEFCE9D0"),
                new GordianTestCase(aes128Add2,"26B3F823B4DBAFC23B141375E10B3AEB7A0B5DEF1C7D760B6F827D01ECD17AC7")
        };
        GordianTestCase[]  myTestsT = new GordianTestCase[] {
                new GordianTestCase("BFF4B85D68C84529F24F69F9ACF1756E29BA648DDEB825C225FA32BA490EF4A9"),
                new GordianTestCase("9BD2635137A52AF7D0FCBEFEFB97EA93A0F4C438BD98956C0DACB04F15EE25B3")
        };
        GordianTestCase[]  myTestsTP = new GordianTestCase[] {
                new GordianTestCase("F324104E2FA14F79D8AA60DF06B93B3BC157324958F0A7EE1E193677A70E0250"),
                new GordianTestCase("78F4C840134F40DC001BFAD3A90B5EF4DEBDBFAC3CFDF0CD69A89DC4FD34713F")
        };
        GordianTestCase[]  myTestsTA = new GordianTestCase[] {
                new GordianTestCase(aes128Add1,"4573AC8BBB33D7CC4DBEF3EEDF6EAE748B536C3A1082CEE4948CDB51C83A7F9C"),
                new GordianTestCase(aes128Add2,"99C628CDD87BD8C2F1FE443AA7F761DA16886436326323354DA6311FFF5BC678")
        };

        /* Create the cipher */
        final GordianCipherFactory myFactory = BCFACTORY.getCipherFactory();
        final GordianSymCipherSpec mySpec = GordianSymCipherSpec.ecb(GordianSymKeySpec.aes(GordianLength.LEN_128), GordianPadding.NONE);
        final GordianCoreCipher<GordianSymKeySpec> myCipher = (GordianCoreCipher<GordianSymKeySpec>) myFactory.createSymKeyCipher(mySpec);

        /* Create a standard stream */
        Stream<DynamicNode> myStandard = Stream.of(DynamicTest.dynamicTest(STANDARD,
                () -> testCTRCipherDRBG(myCipher, GordianLength.LEN_128, myInitF, myTestsF)));
        myStandard = Stream.concat(myStandard, Stream.of(DynamicTest.dynamicTest(PERSONALISED,
                () -> testCTRCipherDRBG(myCipher, GordianLength.LEN_128, myInitFP, myTestsFP))));
        myStandard = Stream.concat(myStandard, Stream.of(DynamicTest.dynamicTest(ADDITIONAL
                , () -> testCTRCipherDRBG(myCipher, GordianLength.LEN_128, myInitF, myTestsFA))));
        myStandard = Stream.of(DynamicContainer.dynamicContainer(STANDARD, myStandard));

        /* Create a resistant stream */
        Stream<DynamicNode> myResistant = Stream.of(DynamicTest.dynamicTest(STANDARD,
                () -> testCTRCipherDRBG(myCipher, GordianLength.LEN_128, myInitT, myTestsT)));
        myResistant = Stream.concat(myResistant, Stream.of(DynamicTest.dynamicTest(PERSONALISED,
                () -> testCTRCipherDRBG(myCipher, GordianLength.LEN_128, myInitTP, myTestsTP))));
        myResistant = Stream.concat(myResistant, Stream.of(DynamicTest.dynamicTest(ADDITIONAL,
                () -> testCTRCipherDRBG(myCipher, GordianLength.LEN_128, myInitT, myTestsTA))));
        myResistant = Stream.of(DynamicContainer.dynamicContainer(RESISTANT, myResistant));

        /* Return the test */
        return DynamicContainer.dynamicContainer("aes128", Stream.concat(myStandard, myResistant));
    }

    /**
     * Create aes128 X931 DRBG test.
     * @return the test
     * @throws OceanusException on error
     */
    private DynamicNode testAES128X931DRBG() throws OceanusException {
        /* The AES128 Init */
        final GordianDRBGInit myInitF =  new GordianDRBGInit(false, "259e67249288597a4d61e7c0e690afae", "35cc0ea481fc8a4f5f05c7d4667233b2");

        /* The Expected results */
        GordianTestCase[]  myTestsF = new GordianTestCase[] {
                new GordianTestCase("15f013af5a8e9df9a8e37500edaeac43"),
                new GordianTestCase("a9d74bb1c90a222adc398546d64879cf"),
                new GordianTestCase("0379e404042d58180764fb9e6c5d94bb"),
                new GordianTestCase("3c74603e036d28c79947ffb56fee4e51"),
                new GordianTestCase("e872101a4df81ebbe1e632fc87195d52")
        };

        /* Run the tests */
        final GordianCipherFactory myFactory = BCFACTORY.getCipherFactory();
        final GordianSymCipherSpec mySpec = GordianSymCipherSpec.ecb(GordianSymKeySpec.aes(GordianLength.LEN_128), GordianPadding.NONE);
        final GordianCoreCipher<GordianSymKeySpec> myCipher = (GordianCoreCipher<GordianSymKeySpec>) myFactory.createSymKeyCipher(mySpec);
        testX931CipherDRBG(myCipher, "f7d36762b9915f1ed585eb8e91700eb2", myInitF, myTestsF);

        /* Create a standard stream */
        Stream<DynamicNode> myStandard = Stream.of(DynamicTest.dynamicTest(STANDARD,
                () -> testX931CipherDRBG(myCipher, "f7d36762b9915f1ed585eb8e91700eb2", myInitF, myTestsF)));

        /* Return the test */
        return DynamicContainer.dynamicContainer("aes128", myStandard);
    }

    /* The AES 256 Constants */
    private static final String aes256Entropy = "000102030405060708090A0B0C0D0E0F1011121314151617" +
            "18191A1B1C1D1E1F202122232425262728292A2B2C2D2E2F" +
            "808182838485868788898A8B8C8D8E8F9091929394959697" +
            "98999A9B9C9D9E9FA0A1A2A3A4A5A6A7A8A9AAABACADAEAF" +
            "C0C1C2C3C4C5C6C7C8C9CACBCCCDCECFD0D1D2D3D4D5D6D7" +
            "D8D9DADBDCDDDEDFE0E1E2E3E4E5E6E7E8E9EAEBECEDEEEF";
    private static final String aes256Nonce = "202122232425262728292A2B2C2D2E2F";
    private static final String aes256Personal = "404142434445464748494A4B4C4D4E4F505152535455565758595A5B5C5D5E5F606162636465666768696A6B6C6D6E6F";
    private static final String aes256Add1 = "606162636465666768696A6B6C6D6E6F707172737475767778797A7B7C7D7E7F808182838485868788898A8B8C8D8E8F";
    private static final String aes256Add2 = "A0A1A2A3A4A5A6A7A8A9AAABACADAEAFB0B1B2B3B4B5B6B7B8B9BABBBCBDBEBFC0C1C2C3C4C5C6C7C8C9CACBCCCDCECF";

    /**
     * Create aes128 Ctr DRBG test.
     * @return the test
     * @throws OceanusException on error
     */
    private DynamicNode testAES256CtrDRBG() throws OceanusException {
        /* The AES256 Init */
        final GordianDRBGInit myInitF =  new GordianDRBGInit(false, aes256Nonce, aes256Entropy);
        final GordianDRBGInit myInitFP =  new GordianDRBGInit(false, aes256Nonce, aes256Entropy, aes256Personal);
        final GordianDRBGInit myInitT =  new GordianDRBGInit(true, aes256Nonce, aes256Entropy);
        final GordianDRBGInit myInitTP =  new GordianDRBGInit(true, aes256Nonce, aes256Entropy, aes256Personal);

        /* The Expected results */
        GordianTestCase[]  myTestsFPA = new GordianTestCase[] {
                new GordianTestCase(aes256Add1,"47111E146562E9AA2FB2A1B095D37A8165AF8FC7CA611D632BE7D4C145C83900"),
                new GordianTestCase(aes256Add2,"98A28E3B1BA363C9DAF0F6887A1CF52B833D3354D77A7C10837DD63DD2E645F8")
        };
        GordianTestCase[]  myTestsTP = new GordianTestCase[] {
                new GordianTestCase("1A2E3FEE9056E98D375525FDC2B63B95B47CE51FCF594D804BD5A17F2E01139B"),
                new GordianTestCase("601F95384F0D85946301D1EACE8F645A825CE38F1E2565B0C0C439448E9CA8AC")
        };
        GordianTestCase[]  myTestsTA = new GordianTestCase[] {
                new GordianTestCase(aes256Add1,"71BB3F9C9CEAF4E6C92A83EB4C7225010EE150AC75E23F5F77AD5073EF24D88A"),
                new GordianTestCase(aes256Add2,"386DEBBBF091BBF0502957B0329938FB836B82E594A2F5FDD5EB28D4E35528F4")
        };
        GordianTestCase[]  myTestsTPA = new GordianTestCase[] {
                new GordianTestCase(aes256Add1,"EAE6BCE781807E524D26605EA198077932D01EEB445B9AC6C5D99C101D29F46E"),
                new GordianTestCase(aes256Add2,"738E99C95AF59519AAD37FF3D5180986ADEBAB6E95836725097E50A8D1D0BD28")
        };
        GordianTestCase[]  myTestsTPA2 = new GordianTestCase[] {
                new GordianTestCase(aes256Add1,"eae6bce781807e524d26605ea198077932d01eeb445b9ac6c5d99c101d29f46e30b27377"),
                new GordianTestCase(aes256Add2,"ec51b55b49904c3ff9e13939f1cf27398993e1b3acb2b0be0be8761261428f0aa8ba2657")
        };

        /* Create the cipher */
        final GordianCipherFactory myFactory = BCFACTORY.getCipherFactory();
        final GordianSymCipherSpec mySpec = GordianSymCipherSpec.ecb(GordianSymKeySpec.aes(GordianLength.LEN_256), GordianPadding.NONE);
        final GordianCoreCipher<GordianSymKeySpec> myCipher = (GordianCoreCipher<GordianSymKeySpec>) myFactory.createSymKeyCipher(mySpec);

        /* Create a standard stream */
        Stream<DynamicNode> myStandard = Stream.of(DynamicTest.dynamicTest(PERSONALISED + ADDITIONAL,
                () -> testCTRCipherDRBG(myCipher, GordianLength.LEN_256, myInitFP, myTestsFPA)));
        myStandard = Stream.of(DynamicContainer.dynamicContainer(STANDARD, myStandard));

        /* Create a resistant stream */
        Stream<DynamicNode> myResistant = Stream.of(DynamicTest.dynamicTest(PERSONALISED,
                () -> testCTRCipherDRBG(myCipher, GordianLength.LEN_256, myInitTP, myTestsTP)));
        myResistant = Stream.concat(myResistant, Stream.of(DynamicTest.dynamicTest(ADDITIONAL,
                () -> testCTRCipherDRBG(myCipher, GordianLength.LEN_256, myInitT, myTestsTA))));
        myResistant = Stream.concat(myResistant, Stream.of(DynamicTest.dynamicTest(PERSONALISED + ADDITIONAL,
                () -> testCTRCipherDRBG(myCipher, GordianLength.LEN_256, myInitTP, myTestsTPA))));
        myResistant = Stream.concat(myResistant, Stream.of(DynamicTest.dynamicTest(PERSONALISED + ADDITIONAL + 2,
                () -> testCTRCipherDRBG(myCipher, GordianLength.LEN_256, myInitTP, myTestsTPA2))));
        myResistant = Stream.of(DynamicContainer.dynamicContainer(RESISTANT, myResistant));

        /* Return the test */
        return DynamicContainer.dynamicContainer("aes256", Stream.concat(myStandard, myResistant));
    }
}
