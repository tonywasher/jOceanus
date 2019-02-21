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

import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.newdigests.CubeHashDigest;
import org.bouncycastle.crypto.newdigests.GroestlDigest;
import org.bouncycastle.crypto.newdigests.JHDigest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
     * Test the Digests against the results.
     * @param pDigest the digest to test.
     * @param pExpected the expected results
     * @throws OceanusException on error
     */
    static void testDigestStrings(final Digest pDigest,
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
     * Groestl224.
     */
    public static class Groestl224Test extends DigestTest {
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

        @Test
        public void testDigests() throws OceanusException {
            testDigestStrings(new GroestlDigest(224), EXPECTED);
        }
    }

    /**
     * Groestl256.
     */
    public static class Groestl256Test extends DigestTest {
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

        @Test
        public void testDigests() throws OceanusException {
            testDigestStrings(new GroestlDigest(256), EXPECTED);
        }
    }

    /**
     * Groestl384.
     */
    public static class Groestl384Test extends DigestTest {
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

        @Test
        public void testDigests() throws OceanusException {
            testDigestStrings(new GroestlDigest(384), EXPECTED);
        }
    }

    /**
     * Groestl512.
     */
    public static class Groestl512Test extends DigestTest {
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

        @Test
        public void testDigests() throws OceanusException {
            testDigestStrings(new GroestlDigest(512), EXPECTED);
        }
    }

    /**
     * JH224.
     */
    public static class JH224Test extends DigestTest {
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

        @Test
        public void testDigests() throws OceanusException {
            testDigestStrings(new JHDigest(224), EXPECTED);
        }
    }

    /**
     * JH256.
     */
    public static class JH256Test extends DigestTest {
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

        @Test
        public void testDigests() throws OceanusException {
            testDigestStrings(new JHDigest(256), EXPECTED);
        }
    }

    /**
     * JH384.
     */
    public static class JH384Test extends DigestTest {
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

        @Test
        public void testDigests() throws OceanusException {
            testDigestStrings(new JHDigest(384), EXPECTED);
        }
    }

    /**
     * JH512.
     */
    public static class JH512Test extends DigestTest {
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

        @Test
        public void testDigests() throws OceanusException {
            testDigestStrings(new JHDigest(512), EXPECTED);
        }
    }

    /**
     * CubeHash224.
     */
    public static class CubeHash224Test extends DigestTest {
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

        @Test
        public void testDigests() throws OceanusException {
            testDigestStrings(new CubeHashDigest(224), EXPECTED);
        }
    }

    /**
     * CubeHash256.
     */
    public static class CubeHash256Test extends DigestTest {
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

        @Test
        public void testDigests() throws OceanusException {
            testDigestStrings(new CubeHashDigest(256), EXPECTED);
        }
    }

    /**
     * CubeHash384.
     */
    public static class CubeHash384Test extends DigestTest {
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

        @Test
        public void testDigests() throws OceanusException {
            testDigestStrings(new CubeHashDigest(384), EXPECTED);
        }
    }

    /**
     * CubeHash512.
     */
    public static class CubeHash512Test extends DigestTest {
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

        @Test
        public void testDigests() throws OceanusException {
            testDigestStrings(new CubeHashDigest(512), EXPECTED);
        }
    }
}
