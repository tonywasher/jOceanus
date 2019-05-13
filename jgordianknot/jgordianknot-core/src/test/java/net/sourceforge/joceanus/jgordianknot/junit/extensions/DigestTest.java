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

import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.ext.digests.Blake2b;
import org.bouncycastle.crypto.ext.digests.Blake2s;
import org.bouncycastle.crypto.ext.digests.CubeHashDigest;
import org.bouncycastle.crypto.ext.digests.GroestlDigest;
import org.bouncycastle.crypto.ext.digests.JHDigest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.TethysDataConverter;

/**
 * Digest Tests.
 */
public class DigestTest {
    /**
     * The test inputs.
    */
    private static final String[] INPUTS = {
                "",
                "a",
                "abc",
                "message digest",
                "abcdefghijklmnopqrstuvwxyz",
                "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789",
                "12345678901234567890123456789012345678901234567890123456789012345678901234567890"
    };

    /**
     * Create the blockCipher test suite.
     * @return the test stream
     * @throws OceanusException on error
     */
    @TestFactory
    public Stream<DynamicNode> digestTests() throws OceanusException {
        /* Create tests */
        return Stream.of(DynamicContainer.dynamicContainer("Digests", Stream.of(
                DynamicContainer.dynamicContainer("Groestl", Stream.of(
                        DynamicTest.dynamicTest("224", () -> new Groestl224Test().checkDigests()),
                        DynamicTest.dynamicTest("256", () -> new Groestl256Test().checkDigests()),
                        DynamicTest.dynamicTest("384", () -> new Groestl384Test().checkDigests()),
                        DynamicTest.dynamicTest("512", () -> new Groestl512Test().checkDigests())
                )),
                DynamicContainer.dynamicContainer("JH", Stream.of(
                        DynamicTest.dynamicTest("224", () -> new JH224Test().checkDigests()),
                        DynamicTest.dynamicTest("256", () -> new JH256Test().checkDigests()),
                        DynamicTest.dynamicTest("384", () -> new JH384Test().checkDigests()),
                        DynamicTest.dynamicTest("512", () -> new JH512Test().checkDigests())
                )),
                DynamicContainer.dynamicContainer("CubeHash", Stream.of(
                        DynamicTest.dynamicTest("224", () -> new CubeHash224Test().checkDigests()),
                        DynamicTest.dynamicTest("256", () -> new CubeHash256Test().checkDigests()),
                        DynamicTest.dynamicTest("384", () -> new CubeHash384Test().checkDigests()),
                        DynamicTest.dynamicTest("512", () -> new CubeHash512Test().checkDigests())
                )),
                DynamicContainer.dynamicContainer("Blake2b", Stream.of(
                        DynamicTest.dynamicTest("224", () -> new Blake2b224Test().checkDigests()),
                        DynamicTest.dynamicTest("256", () -> new Blake2b256Test().checkDigests()),
                        DynamicTest.dynamicTest("384", () -> new Blake2b384Test().checkDigests()),
                        DynamicTest.dynamicTest("512", () -> new Blake2b512Test().checkDigests())
                )),
                DynamicContainer.dynamicContainer("Blake2s", Stream.of(
                        DynamicTest.dynamicTest("128", () -> new Blake2s128Test().checkDigests()),
                        DynamicTest.dynamicTest("160", () -> new Blake2s160Test().checkDigests()),
                        DynamicTest.dynamicTest("224", () -> new Blake2s224Test().checkDigests()),
                        DynamicTest.dynamicTest("256", () -> new Blake2s256Test().checkDigests())
                ))
        )));
    }

    /**
     * Test the Digests against the results.
     * @param pDigest the digest to test.
     * @param pExpected the expected results
     * @throws OceanusException on error
     */
    static void checkDigestStrings(final Digest pDigest,
                                   final String[] pExpected) throws OceanusException {
        /* Check the array */
        Assertions.assertEquals(INPUTS.length, pExpected.length, "Expected results must have same dimensions as Inputs");

        /* Create the output buffer */
        final byte[] myOutput = new byte[pDigest.getDigestSize()];

        /* Loop through the input strings */
        for(int i=0; i < INPUTS.length; i++) {
            /* Create the hash */
            final String myInput = INPUTS[i];
            pDigest.update(myInput.getBytes(), 0, myInput.length());
            pDigest.doFinal(myOutput, 0);

            /* Check the hash */
            final byte[] myExpected = TethysDataConverter.hexStringToBytes(pExpected[i]);
            Assertions.assertArrayEquals(myExpected, myOutput, "Result mismatch");
        }
    }

    /**
     * Print the Digests.
     * @param pDigest the digest to test.
     * @throws OceanusException on error
     */
    static void printDigestStrings(final Digest pDigest) throws OceanusException {
        /* Create the output buffer */
        final byte[] myOutput = new byte[pDigest.getDigestSize()];

        /* Loop through the input strings */
        for(int i=0; i < INPUTS.length; i++) {
            /* Create the hash */
            final String myInput = INPUTS[i];
            pDigest.update(myInput.getBytes(), 0, myInput.length());
            pDigest.doFinal(myOutput, 0);

            /* Check the hash */
            final String myHash = TethysDataConverter.bytesToHexString(myOutput);
            System.out.println(myHash);
        }
    }

    /**
     * Groestl224.
     */
    static class Groestl224Test {
        /**
         * Expected results.
         */
        private static final String[] EXPECTED = {
                "f2e180fb5947be964cd584e22e496242c6a329c577fc4ce8c36d34c3",
                "2dfa5bd326c23c451b1202d99e6cee98a98c45927e1a31077f538712",
                "ed7bb299331c99ee485d49c22d368f05d9158f2055b9605676786f43",
                "e7c16558992711d5736c71d27c943b5b233d485ba923fd26cd6e33a3",
                "9ee8ca59e9ab4cba339ad91c7dffd33e6b694d8b1b83b1b502612b2d",
                "788a94f5a8ddf8ed66539978be578873a2c0209882f818680300b589",
                "c8a3e7274d599900ae673419683c3626a2e49ed57308ed2687508bef"
        };

        /**
         * Test digests.
         * @throws OceanusException on error
         */
        void checkDigests() throws OceanusException {
            checkDigestStrings(new GroestlDigest(224), EXPECTED);
        }
    }

    /**
     * Groestl256.
     */
    static class Groestl256Test {
        /**
         * Expected results.
         */
        private static final String[] EXPECTED = {
                "1a52d11d550039be16107f9c58db9ebcc417f16f736adb2502567119f0083467",
                "3645c245bb31223ad93c80885b719aa40b4bed0a9d9d6e7c11fe99e59ca350b5",
                "f3c1bb19c048801326a7efbcf16e3d7887446249829c379e1840d1a3a1e7d4d2",
                "3fc49ee11a0ffec8b42ed3e4a81c3b1e014bb1747e2ca274eceb8954f693f6ae",
                "113f70bfdbca1fad4de646b3ef7331c55c0c9f727c31cab3871eb117a8cdabb2",
                "646585bdd431960deac99250b29fb59183b4dda335e06259abb96473189eb070",
                "2679d98913bee62e57fdbdde97ddb328373548c6b24fc587cc3d08f2a02a529c"
        };

        /**
         * Test digests.
         * @throws OceanusException on error
         */
        void checkDigests() throws OceanusException {
            checkDigestStrings(new GroestlDigest(256), EXPECTED);
        }
    }

    /**
     * Groestl384.
     */
    static class Groestl384Test {
        /**
         * Expected results.
         */
        private static final String[] EXPECTED = {
                "ac353c1095ace21439251007862d6c62f829ddbe6de4f78e68d310a9205a736d8b11d99bffe448f57a1cfa2934f044a5",
                "13fce7bd9fc69b67cc12c77e765a0a97794c585f89df39fbff32408e060d7d9225c7e80fd87da647686888bda896c342",
                "32c39f82ab41ee4fdb1582f83dde41089d47b904988b1a9a647553cb1a502cf07df7eb1e11dc3d66bec096a39a790336",
                "921099fa694dd442ce784152abcb5658d3fc93fce89ab592c2b5cf063485e6de40dd1dc174de28f6d98ba960cec6f784",
                "af3607759915be17cb74ccd97f6302776cd5c98b18623e74b70e2ba0022cfabd3a0f243d638c59ad673cc7d98d817c06",
                "8100f9a33deca18c56184da6b618587b3f464aea02fa86023bf9bda7fd4256d4a43229a4a622a5824faaf86919022839",
                "1c446cd70a6de52c9db386f5305aae029fe5a4120bc6230b7cd3a5e1ef1949cc8e6d2548c24cd7347b5ba512628a62f6"
        };

        /**
         * Test digests.
         * @throws OceanusException on error
         */
        void checkDigests() throws OceanusException {
            checkDigestStrings(new GroestlDigest(384), EXPECTED);
        }
    }

    /**
     * Groestl512.
     */
    static class Groestl512Test {
        /**
         * Expected results.
         */
        private static final String[] EXPECTED = {
                "6d3ad29d279110eef3adbd66de2a0345a77baede1557f5d099fce0c03d6dc2ba8e6d4a6633dfbd66053c20faa87d1a11f39a7fbe4a6c2f009801370308fc4ad8",
                "9ef345a835ee35d6d0d462ce45f722d84b5ca41fde9c81a98a22cfb4f7425720511b03a258cdc055bf8e9179dc9bdb5d88bed906c71125d4cf0cd39d3d7bebc7",
                "70e1c68c60df3b655339d67dc291cc3f1dde4ef343f11b23fdd44957693815a75a8339c682fc28322513fd1f283c18e53cff2b264e06bf83a2f0ac8c1f6fbff6",
                "3b39d13419dc993f679ca50e068c25a4e9fdd7d90fb540b8d6378fc116497cdfffec0de583af852bcbb69674fbfc2e7387721a5b6ea26ba6b68692d4c7ff0b5e",
                "fe637169445eeacbd53763ef48cec130d7bd2a12425dd80a6410ef13d0cd5d2b98cc91b714f8d0ba637d6e872cae046c271f0e22f2a1eff46a2d2d5449ffec74",
                "21fb8c769a51def37bfe4fe53b6d1f3b69bf1f766f5a6c4cbd56b3ef43fb3be5e53612a9906cd11ce5bd5d95b319a225c5067481092f4238b2a53f35b85e61a6",
                "862849fd911852cd54beefa88759db4cead0ef8e36aaf15398303c5c4cbc016d9b4c42b32081cbdcba710d2693e7663d244fae116ec29ffb40168baf44f944e7"
        };

        /**
         * Test digests.
         * @throws OceanusException on error
         */
        void checkDigests() throws OceanusException {
            checkDigestStrings(new GroestlDigest(512), EXPECTED);
        }
    }

    /**
     * JH224.
     */
    static class JH224Test {
        /**
         * Expected results.
         */
        private static final String[] EXPECTED = {
                "2c99df889b019309051c60fecc2bd285a774940e43175b76b2626630",
                "e715f969fb61b203a97e494aab92d91a9cec52f0933436b0d63bf722",
                "21e88480ebb76dd51a984d52e97fa0da620f885b94a172320131ab54",
                "c7f18f837a7fa5d8b8aac70488787969b9ccad952b0308ed99fd49ba",
                "8f4a448b971e639f7a05ae52d9c3ae25b5dbb4f348963462b4d6f394",
                "47072b35bd1f6ae2b10635f13da3b09230470f77db0dbf6e96dd4d1b",
                "c2b1967e635bd55b6a4d36f863ac4a877be302251d68692873007281"
        };

        /**
         * Test digests.
         * @throws OceanusException on error
         */
        void checkDigests() throws OceanusException {
            checkDigestStrings(new JHDigest(224), EXPECTED);
        }
    }

    /**
     * JH256.
     */
    static class JH256Test {
        /**
         * Expected results.
         */
        private static final String[] EXPECTED = {
                "46e64619c18bb0a92a5e87185a47eef83ca747b8fcc8e1412921357e326df434",
                "d52c0c130a1bc0ae5136375637a52773e150c71efe1c968df8956f6745b05386",
                "924bc82f24a76d519d4f69493da7fa70dc88bdb6016b6d1cc1dcf7def15e9cdd",
                "2821ad727035e451aa91e6fc3dd781a284fd47c55f693df53f9b5099d6528ee1",
                "c392a84988b82fb5b745b7174e9f808b38a14dc00b34250775fa31dd58ab053d",
                "9da41ae486eafd1e1b546a63e4679ee551d0d9e7bbc3f88395843fb76d066ef8",
                "fc4214867025a8af94c614353b3553b10e561ae749fc18c40e5fd44a7a4ecd1b"
        };

        /**
         * Test digests.
         * @throws OceanusException on error
         */
        void checkDigests() throws OceanusException {
            checkDigestStrings(new JHDigest(256), EXPECTED);
        }
    }

    /**
     * JH384.
     */
    static class JH384Test {
        /**
         * Expected results.
         */
        private static final String[] EXPECTED = {
                "2fe5f71b1b3290d3c017fb3c1a4d02a5cbeb03a0476481e25082434a881994b0ff99e078d2c16b105ad069b569315328",
                "77de897ca4fd5dadfbcbd1d8d4ea3c3c1426855e38661325853e92b069f3fe156729f6bbb9a5892c7c18a77f1cb9d0bb",
                "fc41b2b33438dc818a6ef99dd86f2c02a9c42ade5d0d3422f0cdd2289d50b6472c59798e569a0faec4c632e3340d1442",
                "bea0f130acfe76725af9019d7530c93171dde8a4f1d150d6a9daa70724f7c40806ad63781b61614e66d637ed12f62ac4",
                "8aa48c3ee261534441d91ffd0b647638640ea5c7473dd6a823456e0d96cb0219528492862f7b684d47fcfd5c59c6df65",
                "8f78aba5799992b6e5be31d05d0e7e327ea704b71fa41ef53c3a050098638b2525e560da5890c96ca046a02507e90d1a",
                "6f73d9b9b8ed362f8180fb26020725b40bd6ca75b3b947405f26c4c37a885ce028876dc42e379d2faf6146fed3ea0e42"
        };

        /**
         * Test digests.
         * @throws OceanusException on error
         */
        void checkDigests() throws OceanusException {
            checkDigestStrings(new JHDigest(384), EXPECTED);
        }
    }

    /**
     * JH512.
     */
    static class JH512Test {
        /**
         * Expected results.
         */
        private static final String[] EXPECTED = {
                "90ecf2f76f9d2c8017d979ad5ab96b87d58fc8fc4b83060f3f900774faa2c8fabe69c5f4ff1ec2b61d6b316941cedee117fb04b1f4c5bc1b919ae841c50eec4f",
                "f12c87e986daff17c481c81a99a39b603ca6bafcd320c5735523b97cb9a26f7681bad62ffad9aad0e21160a05f773fb0d1434ca4cbcb0483f480a171ada1561b",
                "a05eab9c641cb901107d9880bcdf0eedb19b0073188896365921bd200225d9176cf136e7af90d67bdb05dfa3037e48b757d23a905b2270db67255b9eca982973",
                "47aaa139002108d9e36d8f3f99a5515766187253ba3896fd07a56b9539299595389e7d6abf3717ca773b58a2f5613f382ab50dd2688aa87de10853c3ffa7766d",
                "8735238cc6ac144c2639f5024cfb8706bed077094d4c5f9bde87275cc1eb68972b1cb7e2a01e80f26fcf0242a540a0e9ff515ed3dc54de308c624c134e9ebe3d",
                "4194bc44eec13b905b3d986d4c92baac790d672560705f57c5c94e8f59fea1ebae4dcdeafd9fbad383665f59690d609cf644cadf41961ec3556c8327cfd2eca2",
                "bafb8e710b35eabeb1a48220c4b0987c2c985b6e73b7b31d164bfb9d67c94d99d7bc43b474a25e647cd6cc36334b6a00a5f2a85fae74907fd2885c6168132fe7"
        };

        /**
         * Test digests.
         * @throws OceanusException on error
         */
        void checkDigests() throws OceanusException {
            checkDigestStrings(new JHDigest(512), EXPECTED);
        }
    }

    /**
     * CubeHash224.
     */
    static class CubeHash224Test {
        /**
         * Expected results.
         */
        private static final String[] EXPECTED = {
                "4d841199a71b60279dd4da3fd1efbedf671716f6d1c4e2fdbfc0a879",
                "5f0122e539e241ad5aaf42fe5150468c40e3670a3bf8d6dda15bcc98",
                "f5c18c49e9e1236bed4065da8fc95cafc44f35d37ac05f8d4f06961d",
                "9719cf78ea33ea70bde332f93d10bc5e71de05e0dbc3da80f45b99e1",
                "b4de8505a5ad631db28ec031c9551c6d41863f5c9ab12d12a5477978",
                "40ac16e99b72813241dcb1969c4fbd83b805162a5cac46a68d9430cf",
                "cec6fe7c403e12d2740b0e6011c6066da2b9fad6a17b07b40d207155"
        };

        /**
         * Test digests.
         * @throws OceanusException on error
         */
        void checkDigests() throws OceanusException {
            checkDigestStrings(new CubeHashDigest(224), EXPECTED);
        }
    }

    /**
     * CubeHash256.
     */
    static class CubeHash256Test {
        /**
         * Expected results.
         */
        private static final String[] EXPECTED = {
                "67dfa7b6b3cb27c58c19db1d7bbb7c4596913e25f228ddfb9910ddf3c5cad2eb",
                "22e30986ffa6beb826ae34b5960ec6388e146aa92454985d7cf064a49f010fec",
                "0bff398cba8200a6914e740b3b092e46e9658bf84fb5921b29b346ab34294238",
                "aea2a33310a88d0446fb8c308f2fbc4194bfaea3044e3ac90f9420438f314ec7",
                "947faf774cfec74fe5f9e59e676afd0f026774959d132541e78df7b510a4cc1e",
                "de7540f2e1c04f7de29729441f2a1ad4c28b25dfeeb11aea7ba0d8de19b78339",
                "cf750745b502b313459a9c7b0a850da20cf092bf85b70a02875049a9607bda4a"
        };

        /**
         * Test digests.
         * @throws OceanusException on error
         */
        void checkDigests() throws OceanusException {
            checkDigestStrings(new CubeHashDigest(256), EXPECTED);
        }
    }

    /**
     * CubeHash384.
     */
    static class CubeHash384Test {
        /**
         * Expected results.
         */
        private static final String[] EXPECTED = {
                "05442e0edbc4efceed1eda27115a4a4d4cd6adb865f787b5e83a62ec4642b9e639040db0b410c73f19767319ad6f82bf",
                "ae72fd99ff8e391ff5a39d7a1352ae64191bcc890076543835ae014656f2abfc027a0cfbeac387bcdb91627718c3a91f",
                "409a451205d22bb010381fb85567d04c6d485b726d35465c8347def3cb8c5fb380c2741f924c446e5c38c0c3f8257bb2",
                "743a670038507ce0449764c6b617facc0b2da21a927a699df828c47b47d87eea8e10a9b15ac1d19a4d7f6e6c2977f98f",
                "a2ee6a7ef7b9865a3c4b361af87acbd3525555036dfb088d3230113d9c9b79bb92a34e0a1df39dd682709b82864f69b6",
                "df867f1ea8d793c388864ce225dab35c387bbf8c58fa8e998f56f7e0cb9c045eb3e633ab309b998c75b746b8f5fa93bd",
                "d6cb0ecbe1f10dbf50cf04df376540c220f8278a458d6357c01051605da9818f7df91d829230d1ae327f3bafcfcfcaa9"
        };

        /**
         * Test digests.
         * @throws OceanusException on error
         */
        void checkDigests() throws OceanusException {
            checkDigestStrings(new CubeHashDigest(384), EXPECTED);
        }
    }

    /**
     * CubeHash512.
     */
    static class CubeHash512Test {
        /**
         * Expected results.
         */
        private static final String[] EXPECTED = {
                "37045cca405ee6fbdf815ed8b57c971bb78dafb58f3ef676c977a716f66dbd8f376fef59d2e0687cf5608c5dad53ba42c8456269f3f3bcfb27d9b75caaa26e11",
                "edab0e685bb06bd7032d78a837b0a6e53b85a01787d505c3e56461ee9a27ad3c7fb5a02942a46147168646e0b8f4d2c636a69d70472368037e3a852706ea2e57",
                "f6c085ffde5374ef3ddc42b2a56a793b5371e23cd05b60c79106851d8c0f219e2d24e4c5f5d73b647efdb145b12ffd7005f913386c4d22627c9b4e75586ab490",
                "c5dc45bbc711594ff3bb4329487f4fcf3eb530c08ad6e2698d0c5373d3f9b977e111eea58d686266e25190c7c816dff6f62479d4fd2bc01cae28988ac53ce30e",
                "5540b7ad4c469184088fbe360207443fb0005bb7c948e6cfa550c15469d2ff4cbf3172c7344fb6ab4b98b86fc461bba6db26a664f1a81ea21fb13e78303f556e",
                "4b09926453ed768fea54bd2c829a9ae6105aa05ba8df4cde45d46725f26fb7edc4f8e94f294283a8cb451da0e573eb9f03db0327fdaa12440140dca61712685e",
                "48e34719a74c380fbeaf6f9914a3d84570bee32f9284f919459f12eb3360bf9c632663daa154455f79ec95db11ba1d3c23e9d9f7d12a59dcdb7c464c52965d5d"
        };

        void checkDigests() throws OceanusException {
            checkDigestStrings(new CubeHashDigest(512), EXPECTED);
        }
    }

    /**
     * Blake2b224.
     */
    static class Blake2b224Test {
        /**
         * Expected results.
         */
        private static final String[] EXPECTED = {
                "836cc68931c2e4e3e838602eca1902591d216837bafddfe6f0c8cb07",
                "c05d5ea0257c7a4604122b8e99a0093f89d0797ef06a7f0af65a3560",
                "9bd237b02a29e43bdd6738afa5b53ff0eee178d6210b618e4511aec8",
                "f305a410b733771b7c5c8ad1041e356ff1da48c51792dfe319ba286b",
                "7a04e26d7180b9c5e494558dab986f7e8243891a4bb50c45201a16c9",
                "6116b617e7e51e0032dbd2b3db8b6004b15bda4916b19f2737d95e43",
                "d353573a176b7034a9b3a1fa63b4a6fa0eb7bfb86ebfd1be9aaf0f58"
        };

        void checkDigests() throws OceanusException {
            checkDigestStrings(new Blake2b(224), EXPECTED);
        }
    }

    /**
     * Blake2b256.
     */
    static class Blake2b256Test {
        /**
         * Expected results.
         */
        private static final String[] EXPECTED = {
                "0e5751c026e543b2e8ab2eb06099daa1d1e5df47778f7787faab45cdf12fe3a8",
                "8928aae63c84d87ea098564d1e03ad813f107add474e56aedd286349c0c03ea4",
                "bddd813c634239723171ef3fee98579b94964e3bb1cb3e427262c8c068d52319",
                "31a65b562925c6ffefdafa0ad830f4e33eff148856c2b4754de273814adf8b85",
                "117ad6b940f5e8292c007d9c7e7350cd33cf85b5887e8da71c7957830f536e7c",
                "63f74bf0df57c4fd10f949edbe1cb7f6e374ecab882616381d6d999fda748b93",
                "a4705bbca1ae2e7a5d184a403a15f36c31c7e567adeae33f0f3e2f3ca9958198"
        };

        void checkDigests() throws OceanusException {
            checkDigestStrings(new Blake2b(256), EXPECTED);
        }
    }

    /**
     * Blake2b384.
     */
    static class Blake2b384Test {
        /**
         * Expected results.
         */
        private static final String[] EXPECTED = {
                "b32811423377f52d7862286ee1a72ee540524380fda1724a6f25d7978c6fd3244a6caf0498812673c5e05ef583825100",
                "7d40de16ff771d4595bf70cbda0c4ea0a066a6046fa73d34471cd4d93d827d7c94c29399c50de86983af1ec61d5dcef0",
                "6f56a82c8e7ef526dfe182eb5212f7db9df1317e57815dbda46083fc30f54ee6c66ba83be64b302d7cba6ce15bb556f4",
                "44c3965bd8f02ed299ad52ffb5bba7c448df242073c5520dc091a0cc55d024cdd51569c339d0bf2b6cd746708683a0ef",
                "5cad60ce23b9dc62eabdd149a16307ef916e0637506fa10cf8c688430da6c978a0cb7857fd138977bd281e8cfd5bfd1f",
                "b4975ee19a4f559e3d3497df0db1e5c6b79988b7d7e85c1f064ceaa72a418c484e4418b775c77af8d2651872547c8e9f",
                "1ce12d72189f06f1b95c16f4bf7e0685519bc1065eae2efd015a31db13bd123ea8f8bf83a8682ad29e3828a0a0af299c"
        };

        void checkDigests() throws OceanusException {
            checkDigestStrings(new Blake2b(384), EXPECTED);
        }
    }

    /**
     * Blake2b512.
     */
    static class Blake2b512Test {
        /**
         * Expected results.
         */
        private static final String[] EXPECTED = {
                "786a02f742015903c6c6fd852552d272912f4740e15847618a86e217f71f5419d25e1031afee585313896444934eb04b903a685b1448b755d56f701afe9be2ce",
                "333fcb4ee1aa7c115355ec66ceac917c8bfd815bf7587d325aec1864edd24e34d5abe2c6b1b5ee3face62fed78dbef802f2a85cb91d455a8f5249d330853cb3c",
                "ba80a53f981c4d0d6a2797b69f12f6e94c212f14685ac4b74b12bb6fdbffa2d17d87c5392aab792dc252d5de4533cc9518d38aa8dbf1925ab92386edd4009923",
                "3c26ce487b1c0f062363afa3c675ebdbf5f4ef9bdc022cfbef91e3111cdc283840d8331fc30a8a0906cff4bcdbcd230c61aaec60fdfad457ed96b709a382359a",
                "c68ede143e416eb7b4aaae0d8e48e55dd529eafed10b1df1a61416953a2b0a5666c761e7d412e6709e31ffe221b7a7a73908cb95a4d120b8b090a87d1fbedb4c",
                "99964802e5c25e703722905d3fb80046b6bca698ca9e2cc7e49b4fe1fa087c2edf0312dfbb275cf250a1e542fd5dc2edd313f9c491127c2e8c0c9b24168e2d50",
                "686f41ec5afff6e87e1f076f542aa466466ff5fbde162c48481ba48a748d842799f5b30f5b67fc684771b33b994206d05cc310f31914edd7b97e41860d77d282"
        };

        void checkDigests() throws OceanusException {
            checkDigestStrings(new Blake2b(512), EXPECTED);
        }
    }

    /**
     * Blake2s128.
     */
    static class Blake2s128Test {
        /**
         * Expected results.
         */
        private static final String[] EXPECTED = {
                "64550d6ffe2c0a01a14aba1eade0200c",
                "854b9e9ba49bfd9457d4c3bf96e42523",
                "aa4938119b1dc7b87cbad0ffd200d0ae",
                "a120dbd782f5e524252ba9e77e69301b",
                "6b5da6a19a600add9fada4c0b95bf6c9",
                "ae8812ea7e3507014d764e3d1f57387e",
                "d0b88b4a58efa805a1f7642865edd050"
        };

        void checkDigests() throws OceanusException {
            checkDigestStrings(new Blake2s(128), EXPECTED);
        }
    }

    /**
     * Blake2s160.
     */
    static class Blake2s160Test {
        /**
         * Expected results.
         */
        private static final String[] EXPECTED = {
                "354c9c33f735962418bdacb9479873429c34916f",
                "d9cd2bec1a24404b6588a55b191c7833d630bad8",
                "5ae3b99be29b01834c3b508521ede60438f8de17",
                "bfba9d03326c0ba30fd98d8aad4ad43593c22127",
                "d1dcf102967d7cd98323ee5208fa034f073fac8f",
                "7bc244b9ef68e6901838213fe5dce91bc8f2195b",
                "b81de34029b3fa85ac71db064d25a687377d27f5"
        };

        void checkDigests() throws OceanusException {
            checkDigestStrings(new Blake2s(160), EXPECTED);
        }
    }

    /**
     * Blake2s224.
     */
    static class Blake2s224Test {
        /**
         * Expected results.
         */
        private static final String[] EXPECTED = {
                "1fa1291e65248b37b3433475b2a0dd63d54a11ecc4e3e034e7bc1ef4",
                "726ab9ea46d69ae3b4440d02255ab73b256df1afb5587fb38b92512e",
                "0b033fc226df7abde29f67a05d3dc62cf271ef3dfea4d387407fbd55",
                "ec7ac253d128c17e42fe2cfae74209e14f5b8bb57b1d26075b153a4e",
                "8de6b28a9536f23725d9de3953de02ac58143fd4719adf2e11fb8a23",
                "c4577776a9a8e0c83666bfe8c077ca9ae0271daf5e7433d965a5f787",
                "9144fe48a93c7139b4f1b92ec3c87ee8d184e665eeeaf096b6218d51"
        };

        void checkDigests() throws OceanusException {
            checkDigestStrings(new Blake2s(224), EXPECTED);
        }
    }

    /**
     * Blake2s256.
     */
    static class Blake2s256Test {
        /**
         * Expected results.
         */
        private static final String[] EXPECTED = {
                "69217a3079908094e11121d042354a7c1f55b6482ca1a51e1b250dfd1ed0eef9",
                "4a0d129873403037c2cd9b9048203687f6233fb6738956e0349bd4320fec3e90",
                "508c5e8c327c14e2e1a72ba34eeb452f37458b209ed63a294d999b4c86675982",
                "fa10ab775acf89b7d3c8a6e823d586f6b67bdbac4ce207fe145b7d3ac25cd28c",
                "bdf88eb1f86a0cdf0e840ba88fa118508369df186c7355b4b16cf79fa2710a12",
                "c75439ea17e1de6fa4510c335dc3d3f343e6f9e1ce2773e25b4174f1df8b119b",
                "fdaedb290a0d5af9870864fec2e090200989dc9cd53a3c092129e8535e8b4f66"
        };

        void checkDigests() throws OceanusException {
            checkDigestStrings(new Blake2s(256), EXPECTED);
        }
    }
}
