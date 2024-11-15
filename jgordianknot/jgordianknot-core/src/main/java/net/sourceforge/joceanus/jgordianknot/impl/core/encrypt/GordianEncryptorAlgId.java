/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.impl.core.encrypt;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestSpec;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestSpecBuilder;
import net.sourceforge.joceanus.jgordianknot.api.encrypt.GordianEncryptorFactory;
import net.sourceforge.joceanus.jgordianknot.api.encrypt.GordianEncryptorSpec;
import net.sourceforge.joceanus.jgordianknot.api.encrypt.GordianEncryptorSpecBuilder;
import net.sourceforge.joceanus.jgordianknot.api.encrypt.GordianSM2EncryptionSpec;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairType;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianASN1Util;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDigestAlgId;
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
    private final Map<GordianEncryptorSpec, AlgorithmIdentifier> theSpecMap;

    /**
     * Map of Identifier to EncryptorSpec.
     */
    private final Map<AlgorithmIdentifier, GordianEncryptorSpec> theIdentifierMap;

    /**
     * The factory.
     */
    private final GordianEncryptorFactory theFactory;

    /**
     * Constructor.
     *
     * @param pFactory the factory
     */
    GordianEncryptorAlgId(final GordianCoreFactory pFactory) {
        /* Create the maps */
        theSpecMap = new HashMap<>();
        theIdentifierMap = new HashMap<>();

        /* Access the encryptorFactory  */
        theFactory = pFactory.getKeyPairFactory().getEncryptorFactory();

        /* Populate with the public standards */
        addWellKnownEncryptors();

        /* Loop through the possible AsymKeys */
        for (GordianKeyPairType myKeyType : GordianKeyPairType.values()) {
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
    public AlgorithmIdentifier getIdentifierForSpec(final GordianEncryptorSpec pSpec) {
        /* Handle Composite keyPairs specially */
        if (pSpec.getKeyPairType() == GordianKeyPairType.COMPOSITE) {
            final Iterator<GordianEncryptorSpec> myIterator = pSpec.encryptorSpecIterator();
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
    public GordianEncryptorSpec getSpecForIdentifier(final AlgorithmIdentifier pIdentifier) {
        /* Handle Composite keyPairs specially */
        if (MiscObjectIdentifiers.id_alg_composite.equals(pIdentifier.getAlgorithm())) {
            final List<GordianEncryptorSpec> myList = new ArrayList<>();
            final ASN1Sequence myAlgs = ASN1Sequence.getInstance(pIdentifier.getParameters());
            final Enumeration<?> en = myAlgs.getObjects();
            while (en.hasMoreElements()) {
                myList.add(getSpecForIdentifier(AlgorithmIdentifier.getInstance(en.nextElement())));
            }
            return GordianEncryptorSpecBuilder.composite(myList);
        }
        return theIdentifierMap.get(pIdentifier);
    }

    /**
     * Add WellKnown encryptors.
     */
    private void addWellKnownEncryptors() {
        addToMaps(GordianEncryptorSpecBuilder.sm2(GordianSM2EncryptionSpec.c1c2c3(GordianDigestSpecBuilder.sm3())),
                new AlgorithmIdentifier(GMObjectIdentifiers.sm2encrypt_with_sm3, DERNull.INSTANCE));
        addToMaps(GordianEncryptorSpecBuilder.sm2(GordianSM2EncryptionSpec.c1c2c3(GordianDigestSpecBuilder.sha2(GordianLength.LEN_224))),
                new AlgorithmIdentifier(GMObjectIdentifiers.sm2encrypt_with_sha224, DERNull.INSTANCE));
        addToMaps(GordianEncryptorSpecBuilder.sm2(GordianSM2EncryptionSpec.c1c2c3(GordianDigestSpecBuilder.sha2(GordianLength.LEN_256))),
                new AlgorithmIdentifier(GMObjectIdentifiers.sm2encrypt_with_sha256, DERNull.INSTANCE));
        addToMaps(GordianEncryptorSpecBuilder.sm2(GordianSM2EncryptionSpec.c1c2c3(GordianDigestSpecBuilder.sha2(GordianLength.LEN_384))),
                new AlgorithmIdentifier(GMObjectIdentifiers.sm2encrypt_with_sha384, DERNull.INSTANCE));
        addToMaps(GordianEncryptorSpecBuilder.sm2(GordianSM2EncryptionSpec.c1c2c3(GordianDigestSpecBuilder.sha2(GordianLength.LEN_512))),
                new AlgorithmIdentifier(GMObjectIdentifiers.sm2encrypt_with_sha512, DERNull.INSTANCE));
        addToMaps(GordianEncryptorSpecBuilder.sm2(GordianSM2EncryptionSpec.c1c2c3(GordianDigestSpecBuilder.whirlpool())),
                new AlgorithmIdentifier(GMObjectIdentifiers.sm2encrypt_with_whirlpool, DERNull.INSTANCE));
        addToMaps(GordianEncryptorSpecBuilder.sm2(GordianSM2EncryptionSpec.c1c2c3(GordianDigestSpecBuilder.blake2s(GordianLength.LEN_256))),
                new AlgorithmIdentifier(GMObjectIdentifiers.sm2encrypt_with_blake2s256, DERNull.INSTANCE));
        addToMaps(GordianEncryptorSpecBuilder.sm2(GordianSM2EncryptionSpec.c1c2c3(GordianDigestSpecBuilder.blake2b(GordianLength.LEN_512))),
                new AlgorithmIdentifier(GMObjectIdentifiers.sm2encrypt_with_blake2b512, DERNull.INSTANCE));
    }

    /**
     * Create Identifiers for all valid EncryptorTypes.
     * @param pKeyType the keyType
     */
    private void addEncryptors(final GordianKeyPairType pKeyType) {
        for (GordianEncryptorSpec mySpec : theFactory.listAllSupportedEncryptors(pKeyType)) {
            ensureEncryptor(mySpec);
        }
    }

    /**
     * Add encryptorSpec to map if supported and not already present.
     * @param pSpec the encryptorSpec
     */
    private void ensureEncryptor(final GordianEncryptorSpec pSpec) {
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
    private void addEncryptor(final GordianEncryptorSpec pSpec) {
        /* Create a branch for encryptor based on the keyType */
        final GordianKeyPairType myKeyType = pSpec.getKeyPairType();
        ASN1ObjectIdentifier myId = ENCRYPTOID.branch(Integer.toString(myKeyType.ordinal() + 1));

        /* Obtain the encryptor */
        final Object myEncryptor = pSpec.getEncryptorType();
        if (myEncryptor instanceof GordianDigestSpec) {
            myId = GordianDigestAlgId.appendDigestOID(myId.branch("2"), (GordianDigestSpec) myEncryptor);
        } else if (myEncryptor instanceof GordianSM2EncryptionSpec) {
            final GordianSM2EncryptionSpec mySpec = (GordianSM2EncryptionSpec) myEncryptor;
            myId = myId.branch("4").branch(Integer.toString(mySpec.getEncryptionType().ordinal() + 1));
            myId = GordianDigestAlgId.appendDigestOID(myId, mySpec.getDigestSpec());
        } else {
            myId = myId.branch("1");
        }

        /* Add the spec to the maps */
        addToMaps(pSpec, new AlgorithmIdentifier(myId, DERNull.INSTANCE));
    }

    /**
     * Add encryptor to maps.
     * @param pSpec the encryptorSpec
     * @param pIdentifier the identifier
     */
    private void addToMaps(final GordianEncryptorSpec pSpec,
                           final AlgorithmIdentifier pIdentifier) {
        theSpecMap.put(pSpec, pIdentifier);
        theIdentifierMap.put(pIdentifier, pSpec);
    }
}
