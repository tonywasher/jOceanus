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
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianKeyPairSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianKeyPairSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianIOException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.keypair.GordianCoreKeyPairGenerator;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianHybridSpec;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
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
        theHybridSpec = (GordianHybridSpec) pSpec.getSubSpec();

        /* Access generators */
        final GordianKeyPairFactory myFactory = pFactory.getAsyncFactory().getKeyPairFactory();
        final GordianKeyPairSpecBuilder myBuilder = myFactory.newKeyPairSpecBuilder();
        thePrimaryGenerator = myFactory.getKeyPairGenerator(theHybridSpec.getPrimaryKeyPairSpec(myBuilder));
        theTradGenerator = myFactory.getKeyPairGenerator(theHybridSpec.getTraditionalKeyPairSpec(myBuilder));
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

            /* Access the keyPairs */
            final GordianKeyPair myTraditional = myPair.getTraditional();

            /* Obtain the bytes of the Primary key */
            final byte[] myPrimeBytes = myPair.getPrimarySeed();

            /* Obtain the bytes of the traditional key */
            final X509EncodedKeySpec myTradKeySpec = theTradGenerator.getX509Encoding(myTraditional);
            final PrivateKeyInfo myTradInfo = PrivateKeyInfo.getInstance(myTradKeySpec.getEncoded());
            final byte[] myTradBytes = myTradInfo.getPrivateKey().getOctets();

            /* Build the x509 encoding */
            final byte[] myBytes = Arrays.concatenate(myPrimeBytes, myTradBytes);
            final AlgorithmIdentifier myId = new AlgorithmIdentifier(theHybridSpec.getIdentifier());
            final PrivateKeyInfo myInfo = new PrivateKeyInfo(myId, myBytes);
            return new PKCS8EncodedKeySpec(myInfo.getEncoded());

        } catch (IOException e) {
            throw new GordianIOException("Failed to derive keySpec", e);
        }
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

            /* Derive the primary keyPair */
            final byte[] myPrimePrivBytes = Arrays.copyOfRange(myPrivateBytes, 0, theHybridSpec.getPrivateSeedLength());
            final PrivateKeyInfo myPrimePrivInfo = new PrivateKeyInfo(theHybridSpec.getPrimaryIdentifier(), myPrimePrivBytes);
            final PKCS8EncodedKeySpec myPrimePrivSpec = new PKCS8EncodedKeySpec(myPrimePrivInfo.getEncoded());
            final byte[] myPQPubBytes = Arrays.copyOfRange(myPublicBytes, 0, theHybridSpec.getPublicSeedLength());
            final SubjectPublicKeyInfo myPQPubInfo = new SubjectPublicKeyInfo(theHybridSpec.getPrimaryIdentifier(), myPQPubBytes);
            final X509EncodedKeySpec myPQPubSpec = new X509EncodedKeySpec(myPQPubInfo.getEncoded());
            final BouncyKeyPair myPrimePair = (BouncyKeyPair) thePrimaryGenerator.deriveKeyPair(myPQPubSpec, myPrimePrivSpec);

            /* Derive the secondary keyPair */
            final byte[] myTradPrivBytes = Arrays.copyOfRange(myPrivateBytes, 0, theHybridSpec.getPrivateSeedLength());
            final PrivateKeyInfo myTradPrivInfo = new PrivateKeyInfo(theHybridSpec.getPrimaryIdentifier(), myTradPrivBytes);
            final PKCS8EncodedKeySpec myTradPrivSpec = new PKCS8EncodedKeySpec(myTradPrivInfo.getEncoded());
            final byte[] myTradPubBytes = Arrays.copyOfRange(myPublicBytes, theHybridSpec.getPublicSeedLength(), myPublicBytes.length);
            final SubjectPublicKeyInfo myTradPubInfo = new SubjectPublicKeyInfo(theHybridSpec.getSecondaryIdentifier(), myTradPubBytes);
            final X509EncodedKeySpec myTradPubSpec = new X509EncodedKeySpec(myTradPubInfo.getEncoded());
            final BouncyKeyPair myTradPair = (BouncyKeyPair) theTradGenerator.deriveKeyPair(myTradPubSpec, myTradPrivSpec);

            /* Return the hybrid pair */
            return new BouncyHybridKeyPair(getKeySpec(), myPrimePair, myTradPair);

        } catch (IOException e) {
            throw new GordianIOException("Failed to parse keySpec", e);
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

            /* Derive the primary keyPair */
            final byte[] myPrimeBytes = Arrays.copyOfRange(myBytes, 0, theHybridSpec.getPublicSeedLength());
            final SubjectPublicKeyInfo myPrimeInfo = new SubjectPublicKeyInfo(theHybridSpec.getPrimaryIdentifier(), myPrimeBytes);
            final X509EncodedKeySpec myPrimeSpec = new X509EncodedKeySpec(myPrimeInfo.getEncoded());
            final BouncyKeyPair myPrimePair = (BouncyKeyPair) thePrimaryGenerator.derivePublicOnlyKeyPair(myPrimeSpec);

            /* Derive the secondary keyPair */
            final byte[] myTradBytes = Arrays.copyOfRange(myBytes, theHybridSpec.getPublicSeedLength(), myBytes.length);
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
