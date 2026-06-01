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
package io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianKeyPairSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianXMSSSpec.GordianXMSSDigestType;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyKeyPair.BouncyPublicKey;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyKeyPair.BouncyStateAwarePrivateKey;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreKeyPairSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreXMSSSpec;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.crypto.xmss.XMSSKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSKeyPairGenerator;
import org.bouncycastle.pqc.crypto.xmss.XMSSMTKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSMTKeyPairGenerator;
import org.bouncycastle.pqc.crypto.xmss.XMSSMTParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSMTPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSMTPublicKeyParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSPublicKeyParameters;

import java.io.IOException;
import java.util.Arrays;

/**
 * XMSS KeyPair classes.
 */
public final class BouncyXMSSKeyPair {
    /**
     * Private constructor.
     */
    private BouncyXMSSKeyPair() {
    }

    /**
     * Bouncy XMSS PublicKey.
     */
    public static class BouncyXMSSPublicKey
            extends BouncyPublicKey<XMSSPublicKeyParameters> {
        /**
         * Constructor.
         *
         * @param pKeySpec   the keySpec
         * @param pPublicKey the public key
         */
        BouncyXMSSPublicKey(final GordianKeyPairSpec pKeySpec,
                            final XMSSPublicKeyParameters pPublicKey) {
            super(pKeySpec, pPublicKey);
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final XMSSPublicKeyParameters myThis = getPublicKey();
            final XMSSPublicKeyParameters myThat = (XMSSPublicKeyParameters) pThat;

            /* Check equality */
            return compareKeys(myThis, myThat);
        }

        /**
         * CompareKeys.
         *
         * @param pFirst  the first key
         * @param pSecond the second key
         * @return true/false
         */
        private static boolean compareKeys(final XMSSPublicKeyParameters pFirst,
                                           final XMSSPublicKeyParameters pSecond) {
            try {
                return Arrays.equals(pFirst.getEncoded(), pSecond.getEncoded());
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }

    /**
     * Bouncy XMSS PrivateKey.
     */
    public static class BouncyXMSSPrivateKey
            extends BouncyStateAwarePrivateKey<XMSSPrivateKeyParameters> {
        /**
         * Constructor.
         *
         * @param pKeySpec    the keySpec
         * @param pPrivateKey the private key
         */
        BouncyXMSSPrivateKey(final GordianKeyPairSpec pKeySpec,
                             final XMSSPrivateKeyParameters pPrivateKey) {
            super(pKeySpec, pPrivateKey);
        }

        @Override
        public long getUsagesRemaining() {
            return getPrivateKey().getUsagesRemaining();
        }

        @Override
        public BouncyXMSSPrivateKey getKeyShard(final int pNumUsages) {
            return new BouncyXMSSPrivateKey(getKeySpec(), getPrivateKey().extractKeyShard(pNumUsages));
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final XMSSPrivateKeyParameters myThis = getPrivateKey();
            final XMSSPrivateKeyParameters myThat = (XMSSPrivateKeyParameters) pThat;

            /* Check equality */
            return compareKeys(myThis, myThat);
        }

        /**
         * CompareKeys.
         *
         * @param pFirst  the first key
         * @param pSecond the second key
         * @return true/false
         */
        private static boolean compareKeys(final XMSSPrivateKeyParameters pFirst,
                                           final XMSSPrivateKeyParameters pSecond) {
            try {
                return Arrays.equals(pFirst.getEncoded(), pSecond.getEncoded());
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }

    /**
     * BouncyCastle XMSS KeyPair generator.
     */
    public static class BouncyXMSSKeyPairGenerator
            extends BouncyKeyPairGenerator<XMSSPrivateKeyParameters, XMSSPublicKeyParameters> {
        /**
         * Constructor.
         *
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         */
        BouncyXMSSKeyPairGenerator(final GordianBaseFactory pFactory,
                                   final GordianKeyPairSpec pKeySpec) {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Create and initialise the generator */
            final GordianCoreKeyPairSpec myKeySpec = (GordianCoreKeyPairSpec) pKeySpec;
            final GordianCoreXMSSSpec mySpec = myKeySpec.getXMSSSpec();
            final KeyGenerationParameters myParams = new XMSSKeyGenerationParameters(
                    new XMSSParameters(mySpec.getHeight().getHeight(), createDigest(getKeyType())), getRandom());

            /* Create and initialise the generator */
            setGenerator(new XMSSKeyPairGenerator(), myParams);
            setFactorySet(BouncyPqKeyFactorySet.INSTANCE);
        }

        /**
         * Obtain the keyTypeType.
         *
         * @return the keyTypeType
         */
        private GordianXMSSDigestType getKeyType() {
            final GordianCoreKeyPairSpec myKeySpec = (GordianCoreKeyPairSpec) getKeySpec();
            return myKeySpec.getXMSSSpec().getDigestType();
        }

        @Override
        BouncyXMSSPrivateKey newPrivateKey(final AsymmetricKeyParameter pThat) {
            return new BouncyXMSSPrivateKey(getKeySpec(), (XMSSPrivateKeyParameters) pThat);
        }

        @Override
        BouncyXMSSPublicKey newPublicKey(final AsymmetricKeyParameter pThat) {
            return new BouncyXMSSPublicKey(getKeySpec(), (XMSSPublicKeyParameters) pThat);
        }
    }

    /**
     * Create digest for XMSSKeyType.
     *
     * @param pKeyType the key type
     * @return the digest
     */
    static Digest createDigest(final GordianXMSSDigestType pKeyType) {
        return switch (pKeyType) {
            case SHAKE128 -> new SHAKEDigest(GordianLength.LEN_128.getLength());
            case SHAKE256 -> new SHAKEDigest(GordianLength.LEN_256.getLength());
            case SHA256 -> new SHA256Digest();
            default -> new SHA512Digest();
        };
    }

    /**
     * Bouncy XMSSMT PublicKey.
     */
    public static class BouncyXMSSMTPublicKey
            extends BouncyPublicKey<XMSSMTPublicKeyParameters> {
        /**
         * Constructor.
         *
         * @param pKeySpec   the keySpec
         * @param pPublicKey the public key
         */
        BouncyXMSSMTPublicKey(final GordianKeyPairSpec pKeySpec,
                              final XMSSMTPublicKeyParameters pPublicKey) {
            super(pKeySpec, pPublicKey);
        }


        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final XMSSMTPublicKeyParameters myThis = getPublicKey();
            final XMSSMTPublicKeyParameters myThat = (XMSSMTPublicKeyParameters) pThat;

            /* Check equality */
            return compareKeys(myThis, myThat);
        }

        /**
         * CompareKeys.
         *
         * @param pFirst  the first key
         * @param pSecond the second key
         * @return true/false
         */
        private static boolean compareKeys(final XMSSMTPublicKeyParameters pFirst,
                                           final XMSSMTPublicKeyParameters pSecond) {
            try {
                return Arrays.equals(pFirst.getEncoded(), pSecond.getEncoded());
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }

    /**
     * Bouncy XMSSMT PrivateKey.
     */
    public static class BouncyXMSSMTPrivateKey
            extends BouncyStateAwarePrivateKey<XMSSMTPrivateKeyParameters> {
        /**
         * Constructor.
         *
         * @param pKeySpec    the keySpec
         * @param pPrivateKey the private key
         */
        BouncyXMSSMTPrivateKey(final GordianKeyPairSpec pKeySpec,
                               final XMSSMTPrivateKeyParameters pPrivateKey) {
            super(pKeySpec, pPrivateKey);
        }

        @Override
        public long getUsagesRemaining() {
            return getPrivateKey().getUsagesRemaining();
        }

        @Override
        public BouncyXMSSMTPrivateKey getKeyShard(final int pNumUsages) {
            return new BouncyXMSSMTPrivateKey(getKeySpec(), getPrivateKey().extractKeyShard(pNumUsages));
        }

        @Override
        protected boolean matchKey(final AsymmetricKeyParameter pThat) {
            /* Access keys */
            final XMSSMTPrivateKeyParameters myThis = getPrivateKey();
            final XMSSMTPrivateKeyParameters myThat = (XMSSMTPrivateKeyParameters) pThat;

            /* Check equality */
            return compareKeys(myThis, myThat);
        }

        /**
         * CompareKeys.
         *
         * @param pFirst  the first key
         * @param pSecond the second key
         * @return true/false
         */
        private static boolean compareKeys(final XMSSMTPrivateKeyParameters pFirst,
                                           final XMSSMTPrivateKeyParameters pSecond) {
            try {
                return Arrays.equals(pFirst.getEncoded(), pSecond.getEncoded());
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }

    /**
     * BouncyCastle XMSSMT KeyPair generator.
     */
    public static class BouncyXMSSMTKeyPairGenerator
            extends BouncyKeyPairGenerator<XMSSMTPrivateKeyParameters, XMSSMTPublicKeyParameters> {
        /**
         * Constructor.
         *
         * @param pFactory the Security Factory
         * @param pKeySpec the keySpec
         */
        BouncyXMSSMTKeyPairGenerator(final GordianBaseFactory pFactory,
                                     final GordianKeyPairSpec pKeySpec) {
            /* Initialise underlying class */
            super(pFactory, pKeySpec);

            /* Create and initialise the generator */
            final GordianCoreKeyPairSpec myKeySpec = (GordianCoreKeyPairSpec) pKeySpec;
            final GordianCoreXMSSSpec mySpec = myKeySpec.getXMSSSpec();
            final KeyGenerationParameters myParams = new XMSSMTKeyGenerationParameters(
                    new XMSSMTParameters(mySpec.getHeight().getHeight(), mySpec.getLayers().getLayers(),
                            createDigest(getKeyType())), getRandom());

            /* Create and initialise the generator */
            setGenerator(new XMSSMTKeyPairGenerator(), myParams);
            setFactorySet(BouncyPqKeyFactorySet.INSTANCE);
        }

        /**
         * Obtain the digestType.
         *
         * @return the digestType
         */
        private GordianXMSSDigestType getKeyType() {
            final GordianCoreKeyPairSpec myKeySpec = (GordianCoreKeyPairSpec) getKeySpec();
            return myKeySpec.getXMSSSpec().getDigestType();
        }

        @Override
        BouncyXMSSMTPrivateKey newPrivateKey(final AsymmetricKeyParameter pThat) {
            return new BouncyXMSSMTPrivateKey(getKeySpec(), (XMSSMTPrivateKeyParameters) pThat);
        }

        @Override
        BouncyXMSSMTPublicKey newPublicKey(final AsymmetricKeyParameter pThat) {
            return new BouncyXMSSMTPublicKey(getKeySpec(), (XMSSMTPublicKeyParameters) pThat);
        }
    }
}
