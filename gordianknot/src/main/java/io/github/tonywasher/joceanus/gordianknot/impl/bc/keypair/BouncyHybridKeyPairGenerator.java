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

package io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPair;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPairFactory;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPairGenerator;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianEdwardsSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianKeyPairSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianKeyPairType;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyEdDSAKeyPair.BouncyEd25519PrivateKey;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyEdDSAKeyPair.BouncyEd448PrivateKey;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyKeyPair.BouncyPublicKey;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianDataException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianIOException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.keypair.GordianCoreKeyPairGenerator;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreKeyPairSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianHybridSpec;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed448PrivateKeyParameters;
import org.bouncycastle.util.Arrays;

import java.io.IOException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * Hybrid kyPair Generator.
 */
public class BouncyHybridKeyPairGenerator
        extends GordianCoreKeyPairGenerator {
    /**
     * The subSpec.
     */
    private final GordianHybridSpec theHybridSpec;

    /**
     * The primary generator.
     */
    private final GordianKeyPairGenerator thePrimaryGenerator;

    /**
     * The primary generators.
     */
    private final GordianKeyPairGenerator theTradGenerator;

    /**
     * Constructor.
     *
     * @param pFactory the asymFactory.
     * @param pSpec    the keyPairSetSpec.
     * @throws GordianException on error
     */
    BouncyHybridKeyPairGenerator(final GordianBaseFactory pFactory,
                                 final GordianKeyPairSpec pSpec) throws GordianException {
        /* Store the spec. */
        super(pFactory, pSpec);
        theHybridSpec = ((GordianCoreKeyPairSpec) pSpec).getHybridSpec();

        /* Access generators */
        final GordianKeyPairFactory myFactory = pFactory.getAsyncFactory().getKeyPairFactory();
        thePrimaryGenerator = myFactory.getKeyPairGenerator(theHybridSpec.getPrimaryKeyPairSpec());
        theTradGenerator = myFactory.getKeyPairGenerator(theHybridSpec.getTraditionalKeyPairSpec());
    }

    @Override
    public BouncyHybridKeyPair generateKeyPair() {
        /* Create the keyPairs */
        final BouncyKeyPair myPrimary = (BouncyKeyPair) thePrimaryGenerator.generateKeyPair();
        final BouncyKeyPair myTraditional = (BouncyKeyPair) theTradGenerator.generateKeyPair();

        /* Return the hybrid keyPair */
        return new BouncyHybridKeyPair(getKeySpec(), myPrimary, myTraditional);
    }

    @Override
    public X509EncodedKeySpec getX509Encoding(final GordianKeyPair pKeyPair) throws GordianException {
        /* Protect against exceptions */
        try {
            /* Check the keyPair type and keySpecs */
            BouncyHybridKeyPair.checkKeyPair(pKeyPair, getKeySpec());

            /* Access the hybrid keyPair */
            final BouncyHybridKeyPair myPair = (BouncyHybridKeyPair) pKeyPair;

            /* Access the keyPairs */
            final GordianKeyPair myPrimary = myPair.getPrimary();
            final GordianKeyPair myTraditional = myPair.getTraditional();

            /* Obtain the bytes of the Primary key */
            final X509EncodedKeySpec myPrimeKeySpec = thePrimaryGenerator.getX509Encoding(myPrimary);
            final SubjectPublicKeyInfo myPrimeInfo = SubjectPublicKeyInfo.getInstance(myPrimeKeySpec.getEncoded());
            final byte[] myPrimeBytes = myPrimeInfo.getPublicKeyData().getOctets();

            /* Obtain the bytes of the traditional key */
            final X509EncodedKeySpec myTradKeySpec = theTradGenerator.getX509Encoding(myTraditional);
            final SubjectPublicKeyInfo myTradInfo = SubjectPublicKeyInfo.getInstance(myTradKeySpec.getEncoded());
            final byte[] myTradBytes = myTradInfo.getPublicKeyData().getOctets();

            /* Build the x509 encoding */
            final byte[] myBytes = Arrays.concatenate(myPrimeBytes, myTradBytes);
            final AlgorithmIdentifier myId = new AlgorithmIdentifier(theHybridSpec.getIdentifier());
            final SubjectPublicKeyInfo myInfo = new SubjectPublicKeyInfo(myId, myBytes);
            return new X509EncodedKeySpec(myInfo.getEncoded());

        } catch (IOException e) {
            throw new GordianIOException("Failed to derive keySpec", e);
        }
    }

    @Override
    public PKCS8EncodedKeySpec getPKCS8Encoding(final GordianKeyPair pKeyPair) throws GordianException {
        /* Protect against exceptions */
        try {
            /* Check the keyPair type and keySpecs */
            BouncyHybridKeyPair.checkKeyPair(pKeyPair, getKeySpec());

            /* Access the hybrid keyPair */
            final BouncyHybridKeyPair myPair = (BouncyHybridKeyPair) pKeyPair;

            /* Obtain the bytes of the Primary key */
            final byte[] myPrimeBytes = myPair.getPrimarySeed();

            /* Obtain the bytes of the traditional key */
            final BouncyKeyPair myTraditional = myPair.getTraditional();
            final byte[] myTradBytes = getTraditionalPrivateKeyBytes(myTraditional);

            /* Build the x509 encoding */
            final byte[] myBytes = Arrays.concatenate(myPrimeBytes, myTradBytes);
            final AlgorithmIdentifier myId = new AlgorithmIdentifier(theHybridSpec.getIdentifier());
            final PrivateKeyInfo myInfo = new PrivateKeyInfo(myId, myBytes);
            return new PKCS8EncodedKeySpec(myInfo.getEncoded());

        } catch (IOException e) {
            throw new GordianIOException("Failed to derive keySpec", e);
        }
    }

    /**
     * Obtain the traditional keyBytes.
     *
     * @param pTraditional the traditional keyPair
     * @return the bytes
     * @throws GordianException on error
     */
    private byte[] getTraditionalPrivateKeyBytes(final BouncyKeyPair pTraditional) throws GordianException {
        /* Handle EdDSA specially */
        if (pTraditional.getKeyPairSpec().getKeyPairType().equals(GordianKeyPairType.EDDSA)) {
            final Object mySubSpec = pTraditional.getKeyPairSpec().getSubSpec();
            if (GordianEdwardsSpec.CURVE25519.equals(mySubSpec)) {
                final Ed25519PrivateKeyParameters myParams = (Ed25519PrivateKeyParameters) pTraditional.getPrivateKey().getPrivateKey();
                return myParams.getEncoded();
            } else {
                final Ed448PrivateKeyParameters myParams = (Ed448PrivateKeyParameters) pTraditional.getPrivateKey().getPrivateKey();
                return myParams.getEncoded();
            }
        }

        /* Handle normally */
        final PKCS8EncodedKeySpec myTradKeySpec = theTradGenerator.getPKCS8Encoding(pTraditional);
        final PrivateKeyInfo myTradInfo = PrivateKeyInfo.getInstance(myTradKeySpec.getEncoded());
        return myTradInfo.getPrivateKey().getOctets();
    }

    @Override
    public BouncyHybridKeyPair deriveKeyPair(final X509EncodedKeySpec pPublicKey,
                                             final PKCS8EncodedKeySpec pPrivateKey) throws GordianException {
        /* Protect against exceptions */
        try {
            /* Check the keySpecs */
            checkKeySpec(pPublicKey);
            checkKeySpec(pPrivateKey);

            /* Access the encodings */
            final PrivateKeyInfo myPrivInfo = PrivateKeyInfo.getInstance(pPrivateKey.getEncoded());
            final byte[] myPrivateBytes = myPrivInfo.getPrivateKey().getOctets();
            final SubjectPublicKeyInfo myPubInfo = SubjectPublicKeyInfo.getInstance(pPublicKey.getEncoded());
            final byte[] myPublicBytes = myPubInfo.getPublicKeyData().getOctets();

            /* Check minimum lengths */
            final int myPrivSeedLength = theHybridSpec.getPrivateSeedLength();
            final int myPubSeedLength = theHybridSpec.getPublicSeedLength();
            if (myPrivateBytes.length < myPrivSeedLength) {
                throw new GordianDataException("Private key length too short");
            }
            if (myPublicBytes.length < myPubSeedLength) {
                throw new GordianDataException("Public key length too short");
            }

            /* Derive the primary keyPair */
            final byte[] myPrimePrivBytes = Arrays.copyOfRange(myPrivateBytes, 0, myPrivSeedLength);
            final PrivateKeyInfo myPrimePrivInfo = new PrivateKeyInfo(theHybridSpec.getPrimaryIdentifier(), myPrimePrivBytes);
            final PKCS8EncodedKeySpec myPrimePrivSpec = new PKCS8EncodedKeySpec(myPrimePrivInfo.getEncoded());
            final byte[] myPrimePubBytes = Arrays.copyOfRange(myPublicBytes, 0, myPubSeedLength);
            final SubjectPublicKeyInfo myPrimePubInfo = new SubjectPublicKeyInfo(theHybridSpec.getPrimaryIdentifier(), myPrimePubBytes);
            final X509EncodedKeySpec myPrimePubSpec = new X509EncodedKeySpec(myPrimePubInfo.getEncoded());
            final BouncyKeyPair myPrimePair = (BouncyKeyPair) thePrimaryGenerator.deriveKeyPair(myPrimePubSpec, myPrimePrivSpec);

            /* Derive the secondary keyPair */
            final byte[] myTradPrivateBytes = Arrays.copyOfRange(myPrivateBytes, myPrivSeedLength, myPrivateBytes.length);
            final byte[] myTradPubBytes = Arrays.copyOfRange(myPublicBytes, myPubSeedLength, myPublicBytes.length);
            final BouncyKeyPair myTradPair = deriveTraditionalKeyPair(myTradPubBytes, myTradPrivateBytes);

            /* Return the hybrid pair */
            return new BouncyHybridKeyPair(getKeySpec(), myPrimePair, myTradPair);

        } catch (IOException e) {
            throw new GordianIOException("Failed to parse keySpec", e);
        }
    }

    /**
     * Obtain the traditional keyPair.
     *
     * @param pPublicBytes  the publicKeyBytes
     * @param pPrivateBytes the privateKeyBytes
     * @return the keyPair
     * @throws GordianException on error
     */
    private BouncyKeyPair deriveTraditionalKeyPair(final byte[] pPublicBytes,
                                                   final byte[] pPrivateBytes) throws GordianException {
        /* Handle EdDSA specially */
        final GordianKeyPairSpec mySpec = theHybridSpec.getTraditionalKeyPairSpec();
        final Object mySubSpec = mySpec.getSubSpec();
        if (mySpec.getKeyPairType().equals(GordianKeyPairType.EDDSA)) {
            if (GordianEdwardsSpec.CURVE25519.equals(mySubSpec)) {
                final Ed25519PrivateKeyParameters myParams = new Ed25519PrivateKeyParameters(pPrivateBytes);
                final BouncyEd25519PrivateKey myPrivateKey = new BouncyEd25519PrivateKey(mySpec, myParams);
                return new BouncyKeyPair(deriveTraditionalKey(pPublicBytes), myPrivateKey);
            } else {
                final Ed448PrivateKeyParameters myParams = new Ed448PrivateKeyParameters(pPrivateBytes);
                final BouncyEd448PrivateKey myPrivateKey = new BouncyEd448PrivateKey(mySpec, myParams);
                return new BouncyKeyPair(deriveTraditionalKey(pPublicBytes), myPrivateKey);
            }
        }

        /* Protect against exceptions */
        try {
            /* Derive the secondary keyPair */
            final PrivateKeyInfo myTradPrivInfo = new PrivateKeyInfo(theHybridSpec.getSecondaryIdentifier(), pPrivateBytes);
            final PKCS8EncodedKeySpec myTradPrivSpec = new PKCS8EncodedKeySpec(myTradPrivInfo.getEncoded());
            final SubjectPublicKeyInfo myTradPubInfo = new SubjectPublicKeyInfo(theHybridSpec.getSecondaryIdentifier(), pPublicBytes);
            final X509EncodedKeySpec myTradPubSpec = new X509EncodedKeySpec(myTradPubInfo.getEncoded());
            return (BouncyKeyPair) theTradGenerator.deriveKeyPair(myTradPubSpec, myTradPrivSpec);
        } catch (IOException e) {
            throw new GordianIOException("Failed to derive publicKeySpec", e);
        }
    }

    /**
     * Obtain the traditional publicOnly keyPair.
     *
     * @param pPublicBytes the publicKeyBytes
     * @return the public key
     * @throws GordianException on error
     */
    private BouncyPublicKey<?> deriveTraditionalKey(final byte[] pPublicBytes) throws GordianException {
        /* Protect against exceptions */
        try {
            /* Derive the traditional publicKeyPair */
            final SubjectPublicKeyInfo myTradInfo = new SubjectPublicKeyInfo(theHybridSpec.getSecondaryIdentifier(), pPublicBytes);
            final X509EncodedKeySpec myTradSpec = new X509EncodedKeySpec(myTradInfo.getEncoded());
            final BouncyKeyPair myTradPair = (BouncyKeyPair) theTradGenerator.derivePublicOnlyKeyPair(myTradSpec);
            return myTradPair.getPublicKey();
        } catch (IOException e) {
            throw new GordianIOException("Failed to derive publicKeySpec", e);
        }
    }

    @Override
    public GordianKeyPair derivePublicOnlyKeyPair(final X509EncodedKeySpec pPublicKeySpec) throws GordianException {
        /* Protect against exceptions */
        try {
            /* Check the keySpec */
            checkKeySpec(pPublicKeySpec);

            /* Access the encoding */
            final SubjectPublicKeyInfo myInfo = SubjectPublicKeyInfo.getInstance(pPublicKeySpec.getEncoded());
            final byte[] myBytes = myInfo.getPublicKeyData().getOctets();

            /* Check minimum lengths */
            final int myPubSeedLength = theHybridSpec.getPublicSeedLength();
            if (myBytes.length < myPubSeedLength) {
                throw new GordianDataException("Public key length too short");
            }

            /* Derive the primary keyPair */
            final byte[] myPrimeBytes = Arrays.copyOfRange(myBytes, 0, myPubSeedLength);
            final SubjectPublicKeyInfo myPrimeInfo = new SubjectPublicKeyInfo(theHybridSpec.getPrimaryIdentifier(), myPrimeBytes);
            final X509EncodedKeySpec myPrimeSpec = new X509EncodedKeySpec(myPrimeInfo.getEncoded());
            final BouncyKeyPair myPrimePair = (BouncyKeyPair) thePrimaryGenerator.derivePublicOnlyKeyPair(myPrimeSpec);

            /* Derive the secondary keyPair */
            final byte[] myTradBytes = Arrays.copyOfRange(myBytes, myPubSeedLength, myBytes.length);
            final SubjectPublicKeyInfo myTradInfo = new SubjectPublicKeyInfo(theHybridSpec.getSecondaryIdentifier(), myTradBytes);
            final X509EncodedKeySpec myTradSpec = new X509EncodedKeySpec(myTradInfo.getEncoded());
            final BouncyKeyPair myTradPair = (BouncyKeyPair) theTradGenerator.derivePublicOnlyKeyPair(myTradSpec);

            /* Return the hybrid pair */
            return new BouncyHybridKeyPair(getKeySpec(), myPrimePair, myTradPair);

        } catch (IOException e) {
            throw new GordianIOException("Failed to parse keySpec", e);
        }
    }
}
