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
package io.github.tonywasher.joceanus.gordianknot.impl.core.encrypt;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianNewDigestSpec;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianNewDigestSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.encrypt.GordianEncryptorFactory;
import io.github.tonywasher.joceanus.gordianknot.api.encrypt.spec.GordianNewEncryptorSpec;
import io.github.tonywasher.joceanus.gordianknot.api.encrypt.spec.GordianNewEncryptorSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.encrypt.spec.GordianNewSM2EncryptionType;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewKeyPairType;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianASN1Util;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.digest.GordianCoreDigestAlgId;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.digest.GordianCoreDigestSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.encrypt.GordianCoreEncryptorSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.encrypt.GordianCoreEncryptorSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.encrypt.GordianCoreSM2EncryptionSpec;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.gm.GMObjectIdentifiers;
import org.bouncycastle.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Mappings from EncodedId to EncryptorSpec.
 */
public class GordianEncryptorAlgId {
    /**
     * EncryptorOID branch.
     */
    private static final ASN1ObjectIdentifier ENCRYPTOID = GordianASN1Util.ASYMOID.branch("3");

    /**
     * Map of EncryptorSpec to Identifier.
     */
    private final Map<GordianNewEncryptorSpec, AlgorithmIdentifier> theSpecMap;

    /**
     * Map of Identifier to EncryptorSpec.
     */
    private final Map<AlgorithmIdentifier, GordianNewEncryptorSpec> theIdentifierMap;

    /**
     * The factory.
     */
    private final GordianEncryptorFactory theFactory;

    /**
     * Constructor.
     *
     * @param pFactory the factory
     */
    GordianEncryptorAlgId(final GordianBaseFactory pFactory) {
        /* Create the maps */
        theSpecMap = new HashMap<>();
        theIdentifierMap = new HashMap<>();

        /* Access the encryptorFactory  */
        theFactory = pFactory.getAsyncFactory().getEncryptorFactory();

        /* Populate with the public standards */
        addWellKnownEncryptors();

        /* Loop through the possible AsymKeys */
        for (GordianNewKeyPairType myKeyType : GordianNewKeyPairType.values()) {
            /* Add any non-standard encryptorSpecs */
            addEncryptors(myKeyType);
        }
    }

    /**
     * Obtain Identifier for EncryptorSpec.
     *
     * @param pSpec the encryptorSpec.
     * @return the Identifier
     */
    public AlgorithmIdentifier getIdentifierForSpec(final GordianNewEncryptorSpec pSpec) {
        /* Handle Composite keyPairs specially */
        if (pSpec.getKeyPairType() == GordianNewKeyPairType.COMPOSITE) {
            final GordianCoreEncryptorSpec mySpec = (GordianCoreEncryptorSpec) pSpec;
            final Iterator<GordianNewEncryptorSpec> myIterator = mySpec.encryptorSpecIterator();
            final ASN1EncodableVector ks = new ASN1EncodableVector();
            while (myIterator.hasNext()) {
                ks.add(getIdentifierForSpec(myIterator.next()));
            }
            return new AlgorithmIdentifier(MiscObjectIdentifiers.id_alg_composite, new DERSequence(ks));
        }
        return theSpecMap.get(pSpec);
    }

    /**
     * Obtain EncryptorSpec for Identifier.
     *
     * @param pIdentifier the identifier.
     * @return the encryptorSpec (or null if not found)
     */
    public GordianNewEncryptorSpec getSpecForIdentifier(final AlgorithmIdentifier pIdentifier) {
        /* Handle Composite keyPairs specially */
        if (MiscObjectIdentifiers.id_alg_composite.equals(pIdentifier.getAlgorithm())) {
            final GordianNewEncryptorSpecBuilder myBuilder = GordianCoreEncryptorSpecBuilder.newInstance();
            final List<GordianNewEncryptorSpec> myList = new ArrayList<>();
            final ASN1Sequence myAlgs = ASN1Sequence.getInstance(pIdentifier.getParameters());
            final Enumeration<?> en = myAlgs.getObjects();
            while (en.hasMoreElements()) {
                myList.add(getSpecForIdentifier(AlgorithmIdentifier.getInstance(en.nextElement())));
            }
            return myBuilder.composite(myList);
        }
        return theIdentifierMap.get(pIdentifier);
    }

    /**
     * Add WellKnown encryptors.
     */
    private void addWellKnownEncryptors() {
        final GordianNewEncryptorSpecBuilder myEncBuilder = GordianCoreEncryptorSpecBuilder.newInstance();
        final GordianNewDigestSpecBuilder myDigestBuilder = GordianCoreDigestSpecBuilder.newInstance();
        addToMaps(myEncBuilder.sm2(GordianNewSM2EncryptionType.C1C2C3, myDigestBuilder.sm3()),
                new AlgorithmIdentifier(GMObjectIdentifiers.sm2encrypt_with_sm3, DERNull.INSTANCE));
        addToMaps(myEncBuilder.sm2(GordianNewSM2EncryptionType.C1C2C3, myDigestBuilder.sha2(GordianLength.LEN_224)),
                new AlgorithmIdentifier(GMObjectIdentifiers.sm2encrypt_with_sha224, DERNull.INSTANCE));
        addToMaps(myEncBuilder.sm2(GordianNewSM2EncryptionType.C1C2C3, myDigestBuilder.sha2(GordianLength.LEN_256)),
                new AlgorithmIdentifier(GMObjectIdentifiers.sm2encrypt_with_sha256, DERNull.INSTANCE));
        addToMaps(myEncBuilder.sm2(GordianNewSM2EncryptionType.C1C2C3, myDigestBuilder.sha2(GordianLength.LEN_384)),
                new AlgorithmIdentifier(GMObjectIdentifiers.sm2encrypt_with_sha384, DERNull.INSTANCE));
        addToMaps(myEncBuilder.sm2(GordianNewSM2EncryptionType.C1C2C3, myDigestBuilder.sha2(GordianLength.LEN_512)),
                new AlgorithmIdentifier(GMObjectIdentifiers.sm2encrypt_with_sha512, DERNull.INSTANCE));
        addToMaps(myEncBuilder.sm2(GordianNewSM2EncryptionType.C1C2C3, myDigestBuilder.whirlpool()),
                new AlgorithmIdentifier(GMObjectIdentifiers.sm2encrypt_with_whirlpool, DERNull.INSTANCE));
        addToMaps(myEncBuilder.sm2(GordianNewSM2EncryptionType.C1C2C3, myDigestBuilder.blake2s(GordianLength.LEN_256)),
                new AlgorithmIdentifier(GMObjectIdentifiers.sm2encrypt_with_blake2s256, DERNull.INSTANCE));
        addToMaps(myEncBuilder.sm2(GordianNewSM2EncryptionType.C1C2C3, myDigestBuilder.blake2b(GordianLength.LEN_512)),
                new AlgorithmIdentifier(GMObjectIdentifiers.sm2encrypt_with_blake2b512, DERNull.INSTANCE));
    }

    /**
     * Create Identifiers for all valid EncryptorTypes.
     *
     * @param pKeyType the keyType
     */
    private void addEncryptors(final GordianNewKeyPairType pKeyType) {
        for (GordianNewEncryptorSpec mySpec : theFactory.listAllSupportedEncryptors(pKeyType)) {
            ensureEncryptor(mySpec);
        }
    }

    /**
     * Add encryptorSpec to map if supported and not already present.
     *
     * @param pSpec the encryptorSpec
     */
    private void ensureEncryptor(final GordianNewEncryptorSpec pSpec) {
        /* If the encryptor is not already known */
        if (!theSpecMap.containsKey(pSpec)) {
            addEncryptor(pSpec);
        }
    }

    /**
     * Create Identifier for an encryptorSpec.
     *
     * @param pSpec the macSpec
     */
    private void addEncryptor(final GordianNewEncryptorSpec pSpec) {
        /* Create a branch for encryptor based on the keyType */
        final GordianNewKeyPairType myKeyType = pSpec.getKeyPairType();
        ASN1ObjectIdentifier myId = ENCRYPTOID.branch(Integer.toString(myKeyType.ordinal() + 1));

        /* Obtain the encryptor */
        final Object myEncryptor = pSpec.getEncryptorType();
        if (myEncryptor instanceof GordianNewDigestSpec mySpec) {
            myId = GordianCoreDigestAlgId.appendDigestOID(myId.branch("2"), mySpec);
        } else if (myEncryptor instanceof GordianCoreSM2EncryptionSpec mySpec) {
            myId = myId.branch("4").branch(Integer.toString(mySpec.getEncryptionType().ordinal() + 1));
            myId = GordianCoreDigestAlgId.appendDigestOID(myId, mySpec.getDigestSpec());
        } else {
            myId = myId.branch("1");
        }

        /* Add the spec to the maps */
        addToMaps(pSpec, new AlgorithmIdentifier(myId, DERNull.INSTANCE));
    }

    /**
     * Add encryptor to maps.
     *
     * @param pSpec       the encryptorSpec
     * @param pIdentifier the identifier
     */
    private void addToMaps(final GordianNewEncryptorSpec pSpec,
                           final AlgorithmIdentifier pIdentifier) {
        theSpecMap.put(pSpec, pIdentifier);
        theIdentifierMap.put(pIdentifier, pSpec);
    }
}
