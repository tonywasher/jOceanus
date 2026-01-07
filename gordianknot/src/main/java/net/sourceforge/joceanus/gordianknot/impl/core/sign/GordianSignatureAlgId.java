/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.gordianknot.impl.core.sign;

import net.sourceforge.joceanus.gordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.gordianknot.api.digest.GordianDigestSpec;
import net.sourceforge.joceanus.gordianknot.api.digest.GordianDigestSpecBuilder;
import net.sourceforge.joceanus.gordianknot.api.digest.GordianDigestSubSpec.GordianDigestState;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianFalconSpec;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairSpecBuilder;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairType;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianMLDSASpec;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianMayoSpec;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianRSAModulus;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianSLHDSASpec;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianSnovaSpec;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianXMSSKeySpec;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianXMSSKeySpec.GordianXMSSDigestType;
import net.sourceforge.joceanus.gordianknot.api.sign.GordianSignatureFactory;
import net.sourceforge.joceanus.gordianknot.api.sign.GordianSignatureSpec;
import net.sourceforge.joceanus.gordianknot.api.sign.GordianSignatureSpecBuilder;
import net.sourceforge.joceanus.gordianknot.api.sign.GordianSignatureType;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianASN1Util;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.keypair.GordianCompositeKeyPair;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.asn1.edec.EdECObjectIdentifiers;
import org.bouncycastle.asn1.gm.GMObjectIdentifiers;
import org.bouncycastle.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.RSASSAPSSparams;
import org.bouncycastle.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * OID Manager for Signatures.
 */
public class GordianSignatureAlgId {
    /**
     *  SignatureOID branch.
     */
    private static final ASN1ObjectIdentifier SIGOID = GordianASN1Util.ASYMOID.branch("1");

    /**
     * Map of SignatureSpec to Identifier.
     */
    private final Map<GordianSignatureSpec, AlgorithmIdentifier> theSpecMap;

    /**
     * Map of SignatureSpec to subTypeMap.
     */
    private final Map<GordianSignatureSpec, Map<Object, AlgorithmIdentifier>> theSpecSubTypeMap;

    /**
     * Map of Identifier to SignatureSpec.
     */
    private final Map<AlgorithmIdentifier, GordianSignatureSpec> theIdentifierMap;

    /**
     * The factory.
     */
    private final GordianSignatureFactory theFactory;

    /**
     * Constructor.
     * @param pFactory the factory
     */
    GordianSignatureAlgId(final GordianBaseFactory pFactory) {
        /* Create the maps */
        theSpecMap = new HashMap<>();
        theSpecSubTypeMap = new HashMap<>();
        theIdentifierMap = new HashMap<>();

        /* Access the asymFactory and digests */
        theFactory = pFactory.getAsyncFactory().getSignatureFactory();

        /* Populate with the public standards */
        addRSASignatures();
        addDSASignatures();
        addECSignatures();
        addSM2Signatures();
        addGOSTSignatures();
        addEdDSASignatures();
        addPostQuantumSignatures();

        /* Loop through the possible AsymKeys */
        for (GordianKeyPairType myKeyType : GordianKeyPairType.values()) {
            /* Ignore keyType if subTypes are used */
            if (myKeyType.subTypeForSignatures()) {
                continue;
            }

            /* Add any non-standard signatureSpecs */
            addSignatures(myKeyType);
        }
    }

    /**
     * Obtain Identifier for SignatureSpec.
     * @param pSpec the signatureSpec.
     * @param pKeyPair the keyPair
     * @return the Identifier
     */
    AlgorithmIdentifier getIdentifierForSpecAndKeyPair(final GordianSignatureSpec pSpec,
                                                       final GordianKeyPair pKeyPair) {
        /* Handle Composite keyPairs specially */
        if (pSpec.getKeyPairType() == GordianKeyPairType.COMPOSITE) {
            final GordianCompositeKeyPair myCompPair = (GordianCompositeKeyPair) pKeyPair;
            final Iterator<GordianKeyPair> pairIterator = myCompPair.iterator();
            final Iterator<GordianSignatureSpec> sigIterator = pSpec.signatureSpecIterator();
            final ASN1EncodableVector ks = new ASN1EncodableVector();
            while (sigIterator.hasNext()) {
                ks.add(getIdentifierForSpecAndKeyPair(sigIterator.next(), pairIterator.next()));
            }
            return new AlgorithmIdentifier(MiscObjectIdentifiers.id_alg_composite, new DERSequence(ks));
        }

        /* If we need to use the subType */
        if (pSpec.getKeyPairType().subTypeForSignatures()) {
            /* Look up in the subKey map */
            final Map<Object, AlgorithmIdentifier> myMap = theSpecSubTypeMap.get(pSpec);
            return myMap == null
                   ? null
                   : myMap.get(pKeyPair.getKeyPairSpec().getSubKeyType());
        }

        /* Look up in the standard map */
        return theSpecMap.get(pSpec);
    }

    /**
     * Obtain SignatureSpec for Identifier.
     * @param pIdentifier the identifier.
     * @return the signatureSpec (or null if not found)
     */
    public GordianSignatureSpec getSpecForIdentifier(final AlgorithmIdentifier pIdentifier) {
        /* Handle Composite keyPairs specially */
        if (MiscObjectIdentifiers.id_alg_composite.equals(pIdentifier.getAlgorithm())) {
            final List<GordianSignatureSpec> myList = new ArrayList<>();
            final ASN1Sequence myAlgs = ASN1Sequence.getInstance(pIdentifier.getParameters());
            final Enumeration<?> en = myAlgs.getObjects();
            while (en.hasMoreElements()) {
                myList.add(getSpecForIdentifier(AlgorithmIdentifier.getInstance(en.nextElement())));
            }
            return GordianSignatureSpecBuilder.composite(myList);
        }
        return theIdentifierMap.get(pIdentifier);
    }

    /**
     * Add pair to maps.
     * @param pSpec the signatureSpec
     * @param pIdentifier the identifier
     */
    private void addToMaps(final GordianSignatureSpec pSpec,
                           final AlgorithmIdentifier pIdentifier) {
        theSpecMap.put(pSpec, pIdentifier);
        theIdentifierMap.put(pIdentifier, pSpec);
    }

    /**
     * Add pair to maps.
     * @param pSpec the signatureSpec
     * @param pSubType the subType
     * @param pIdentifier the identifier
     */
    private void addToMaps(final GordianSignatureSpec pSpec,
                           final Object pSubType,
                           final AlgorithmIdentifier pIdentifier) {
        /* Access the relevant map */
        final Map<Object, AlgorithmIdentifier> myMap = theSpecSubTypeMap.computeIfAbsent(pSpec, p -> new HashMap<>());
        myMap.put(pSubType, pIdentifier);
        theIdentifierMap.put(pIdentifier, pSpec);
    }

    /**
     * Add RSA signatures.
     */
    private void addRSASignatures() {
        addToMaps(GordianSignatureSpecBuilder.rsa(GordianSignatureType.PSSMGF1, GordianDigestSpecBuilder.sha1()),
                new AlgorithmIdentifier(PKCSObjectIdentifiers.id_RSASSA_PSS,
                        createPSSParams(OIWObjectIdentifiers.idSHA1, GordianLength.LEN_160)));
        addToMaps(GordianSignatureSpecBuilder.rsa(GordianSignatureType.PSSMGF1, GordianDigestSpecBuilder.sha2(GordianLength.LEN_224)),
                new AlgorithmIdentifier(PKCSObjectIdentifiers.id_RSASSA_PSS,
                        createPSSParams(NISTObjectIdentifiers.id_sha224, GordianLength.LEN_224)));
        addToMaps(GordianSignatureSpecBuilder.rsa(GordianSignatureType.PSSMGF1, GordianDigestSpecBuilder.sha2(GordianLength.LEN_256)),
                new AlgorithmIdentifier(PKCSObjectIdentifiers.id_RSASSA_PSS,
                        createPSSParams(NISTObjectIdentifiers.id_sha256, GordianLength.LEN_256)));
        addToMaps(GordianSignatureSpecBuilder.rsa(GordianSignatureType.PSSMGF1, GordianDigestSpecBuilder.sha2(GordianLength.LEN_384)),
                new AlgorithmIdentifier(PKCSObjectIdentifiers.id_RSASSA_PSS,
                        createPSSParams(NISTObjectIdentifiers.id_sha384, GordianLength.LEN_384)));
        addToMaps(GordianSignatureSpecBuilder.rsa(GordianSignatureType.PSSMGF1, GordianDigestSpecBuilder.sha2(GordianLength.LEN_512)),
                new AlgorithmIdentifier(PKCSObjectIdentifiers.id_RSASSA_PSS,
                        createPSSParams(NISTObjectIdentifiers.id_sha512, GordianLength.LEN_512)));
        addToMaps(GordianSignatureSpecBuilder.rsa(GordianSignatureType.PSSMGF1, GordianDigestSpecBuilder.sha3(GordianLength.LEN_224)),
                new AlgorithmIdentifier(PKCSObjectIdentifiers.id_RSASSA_PSS,
                        createPSSParams(NISTObjectIdentifiers.id_sha3_224, GordianLength.LEN_224)));
        addToMaps(GordianSignatureSpecBuilder.rsa(GordianSignatureType.PSSMGF1, GordianDigestSpecBuilder.sha3(GordianLength.LEN_256)),
                new AlgorithmIdentifier(PKCSObjectIdentifiers.id_RSASSA_PSS,
                        createPSSParams(NISTObjectIdentifiers.id_sha3_256, GordianLength.LEN_256)));
        addToMaps(GordianSignatureSpecBuilder.rsa(GordianSignatureType.PSSMGF1, GordianDigestSpecBuilder.sha3(GordianLength.LEN_384)),
                new AlgorithmIdentifier(PKCSObjectIdentifiers.id_RSASSA_PSS,
                        createPSSParams(NISTObjectIdentifiers.id_sha3_384, GordianLength.LEN_384)));
        addToMaps(GordianSignatureSpecBuilder.rsa(GordianSignatureType.PSSMGF1, GordianDigestSpecBuilder.sha3(GordianLength.LEN_512)),
                new AlgorithmIdentifier(PKCSObjectIdentifiers.id_RSASSA_PSS,
                        createPSSParams(NISTObjectIdentifiers.id_sha3_512, GordianLength.LEN_512)));

        addPSS128Algorithms(GordianDigestSpecBuilder.sha2(GordianLength.LEN_224),
                            NISTObjectIdentifiers.id_sha224, GordianLength.LEN_224);
        addPSS128Algorithms(GordianDigestSpecBuilder.sha2(GordianLength.LEN_256),
                            NISTObjectIdentifiers.id_sha256, GordianLength.LEN_256);
        addPSS128Algorithms(GordianDigestSpecBuilder.sha2(GordianLength.LEN_384),
                            NISTObjectIdentifiers.id_sha384, GordianLength.LEN_384);
        addPSS128Algorithms(GordianDigestSpecBuilder.sha2(GordianLength.LEN_512),
                            NISTObjectIdentifiers.id_sha512, GordianLength.LEN_512);
        addPSS128Algorithms(GordianDigestSpecBuilder.sha3(GordianLength.LEN_224),
                            NISTObjectIdentifiers.id_sha3_224, GordianLength.LEN_224);
        addPSS128Algorithms(GordianDigestSpecBuilder.sha3(GordianLength.LEN_256),
                            NISTObjectIdentifiers.id_sha3_256, GordianLength.LEN_256);
        addPSS128Algorithms(GordianDigestSpecBuilder.sha3(GordianLength.LEN_384),
                            NISTObjectIdentifiers.id_sha3_384, GordianLength.LEN_384);
        addPSS128Algorithms(GordianDigestSpecBuilder.sha3(GordianLength.LEN_512),
                            NISTObjectIdentifiers.id_sha3_512, GordianLength.LEN_512);
        addPSS128Algorithms(GordianDigestSpecBuilder.shake128(),
                            NISTObjectIdentifiers.id_shake128_len, GordianLength.LEN_256);

        addPSS256Algorithms(GordianDigestSpecBuilder.sha2(GordianLength.LEN_224),
                            NISTObjectIdentifiers.id_sha224, GordianLength.LEN_224);
        addPSS256Algorithms(GordianDigestSpecBuilder.sha2(GordianLength.LEN_256),
                            NISTObjectIdentifiers.id_sha256, GordianLength.LEN_256);
        addPSS256Algorithms(GordianDigestSpecBuilder.sha2(GordianLength.LEN_384),
                            NISTObjectIdentifiers.id_sha384, GordianLength.LEN_384);
        addPSS256Algorithms(GordianDigestSpecBuilder.sha2(GordianLength.LEN_512),
                            NISTObjectIdentifiers.id_sha512, GordianLength.LEN_512);
        addPSS256Algorithms(GordianDigestSpecBuilder.sha3(GordianLength.LEN_224),
                            NISTObjectIdentifiers.id_sha3_224, GordianLength.LEN_224);
        addPSS256Algorithms(GordianDigestSpecBuilder.sha3(GordianLength.LEN_256),
                            NISTObjectIdentifiers.id_sha3_256, GordianLength.LEN_256);
        addPSS256Algorithms(GordianDigestSpecBuilder.sha3(GordianLength.LEN_384),
                            NISTObjectIdentifiers.id_sha3_384, GordianLength.LEN_384);
        addPSS256Algorithms(GordianDigestSpecBuilder.sha3(GordianLength.LEN_512),
                            NISTObjectIdentifiers.id_sha3_512, GordianLength.LEN_512);
        addPSS256Algorithms(GordianDigestSpecBuilder.shake256(),
                            NISTObjectIdentifiers.id_shake256_len, GordianLength.LEN_512);

        addToMaps(GordianSignatureSpecBuilder.rsa(GordianSignatureType.PREHASH, GordianDigestSpecBuilder.md2()),
                new AlgorithmIdentifier(PKCSObjectIdentifiers.md2WithRSAEncryption, DERNull.INSTANCE));
        addToMaps(GordianSignatureSpecBuilder.rsa(GordianSignatureType.PREHASH, GordianDigestSpecBuilder.md4()),
                new AlgorithmIdentifier(PKCSObjectIdentifiers.md4WithRSAEncryption, DERNull.INSTANCE));
        addToMaps(GordianSignatureSpecBuilder.rsa(GordianSignatureType.PREHASH, GordianDigestSpecBuilder.md5()),
                new AlgorithmIdentifier(PKCSObjectIdentifiers.md5WithRSAEncryption, DERNull.INSTANCE));
        addToMaps(GordianSignatureSpecBuilder.rsa(GordianSignatureType.PREHASH, GordianDigestSpecBuilder.sha1()),
                new AlgorithmIdentifier(PKCSObjectIdentifiers.sha1WithRSAEncryption, DERNull.INSTANCE));
        addToMaps(GordianSignatureSpecBuilder.rsa(GordianSignatureType.PREHASH, GordianDigestSpecBuilder.sha2(GordianLength.LEN_224)),
                new AlgorithmIdentifier(PKCSObjectIdentifiers.sha224WithRSAEncryption, DERNull.INSTANCE));
        addToMaps(GordianSignatureSpecBuilder.rsa(GordianSignatureType.PREHASH, GordianDigestSpecBuilder.sha2(GordianLength.LEN_256)),
                new AlgorithmIdentifier(PKCSObjectIdentifiers.sha256WithRSAEncryption, DERNull.INSTANCE));
        addToMaps(GordianSignatureSpecBuilder.rsa(GordianSignatureType.PREHASH, GordianDigestSpecBuilder.sha2(GordianLength.LEN_384)),
                new AlgorithmIdentifier(PKCSObjectIdentifiers.sha384WithRSAEncryption, DERNull.INSTANCE));
        addToMaps(GordianSignatureSpecBuilder.rsa(GordianSignatureType.PREHASH, GordianDigestSpecBuilder.sha2(GordianLength.LEN_512)),
                new AlgorithmIdentifier(PKCSObjectIdentifiers.sha512WithRSAEncryption, DERNull.INSTANCE));
        addToMaps(GordianSignatureSpecBuilder.rsa(GordianSignatureType.PREHASH, GordianDigestSpecBuilder.sha2(GordianDigestState.STATE512, GordianLength.LEN_224)),
                new AlgorithmIdentifier(PKCSObjectIdentifiers.sha512_224WithRSAEncryption, DERNull.INSTANCE));
        addToMaps(GordianSignatureSpecBuilder.rsa(GordianSignatureType.PREHASH, GordianDigestSpecBuilder.sha2(GordianDigestState.STATE512, GordianLength.LEN_256)),
                new AlgorithmIdentifier(PKCSObjectIdentifiers.sha512_256WithRSAEncryption, DERNull.INSTANCE));
        addToMaps(GordianSignatureSpecBuilder.rsa(GordianSignatureType.PREHASH, GordianDigestSpecBuilder.sha3(GordianLength.LEN_224)),
                new AlgorithmIdentifier(NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_224, DERNull.INSTANCE));
        addToMaps(GordianSignatureSpecBuilder.rsa(GordianSignatureType.PREHASH, GordianDigestSpecBuilder.sha3(GordianLength.LEN_256)),
                new AlgorithmIdentifier(NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_256, DERNull.INSTANCE));
        addToMaps(GordianSignatureSpecBuilder.rsa(GordianSignatureType.PREHASH, GordianDigestSpecBuilder.sha3(GordianLength.LEN_384)),
                new AlgorithmIdentifier(NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_384, DERNull.INSTANCE));
        addToMaps(GordianSignatureSpecBuilder.rsa(GordianSignatureType.PREHASH, GordianDigestSpecBuilder.sha3(GordianLength.LEN_512)),
                new AlgorithmIdentifier(NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_512, DERNull.INSTANCE));
        addToMaps(GordianSignatureSpecBuilder.rsa(GordianSignatureType.PREHASH, GordianDigestSpecBuilder.ripemd(GordianLength.LEN_128)),
                new AlgorithmIdentifier(TeleTrusTObjectIdentifiers.rsaSignatureWithripemd128, DERNull.INSTANCE));
        addToMaps(GordianSignatureSpecBuilder.rsa(GordianSignatureType.PREHASH, GordianDigestSpecBuilder.ripemd(GordianLength.LEN_160)),
                new AlgorithmIdentifier(TeleTrusTObjectIdentifiers.rsaSignatureWithripemd160, DERNull.INSTANCE));
        addToMaps(GordianSignatureSpecBuilder.rsa(GordianSignatureType.PREHASH, GordianDigestSpecBuilder.ripemd(GordianLength.LEN_256)),
                new AlgorithmIdentifier(TeleTrusTObjectIdentifiers.rsaSignatureWithripemd256, DERNull.INSTANCE));
    }

    /**
     * Add DSA signatures.
     */
    private void addDSASignatures() {
        addToMaps(GordianSignatureSpecBuilder.dsa(GordianSignatureType.DSA, GordianDigestSpecBuilder.sha1()),
                new AlgorithmIdentifier(X9ObjectIdentifiers.id_dsa_with_sha1, DERNull.INSTANCE));
        addToMaps(GordianSignatureSpecBuilder.dsa(GordianSignatureType.DSA, GordianDigestSpecBuilder.sha2(GordianLength.LEN_224)),
                new AlgorithmIdentifier(NISTObjectIdentifiers.dsa_with_sha224, DERNull.INSTANCE));
        addToMaps(GordianSignatureSpecBuilder.dsa(GordianSignatureType.DSA, GordianDigestSpecBuilder.sha2(GordianLength.LEN_256)),
                new AlgorithmIdentifier(NISTObjectIdentifiers.dsa_with_sha256, DERNull.INSTANCE));
        addToMaps(GordianSignatureSpecBuilder.dsa(GordianSignatureType.DSA, GordianDigestSpecBuilder.sha2(GordianLength.LEN_384)),
                new AlgorithmIdentifier(NISTObjectIdentifiers.dsa_with_sha384, DERNull.INSTANCE));
        addToMaps(GordianSignatureSpecBuilder.dsa(GordianSignatureType.DSA, GordianDigestSpecBuilder.sha2(GordianLength.LEN_512)),
                new AlgorithmIdentifier(NISTObjectIdentifiers.dsa_with_sha512, DERNull.INSTANCE));
        addToMaps(GordianSignatureSpecBuilder.dsa(GordianSignatureType.DSA, GordianDigestSpecBuilder.sha3(GordianLength.LEN_224)),
                new AlgorithmIdentifier(NISTObjectIdentifiers.id_dsa_with_sha3_224, DERNull.INSTANCE));
        addToMaps(GordianSignatureSpecBuilder.dsa(GordianSignatureType.DSA, GordianDigestSpecBuilder.sha3(GordianLength.LEN_256)),
                new AlgorithmIdentifier(NISTObjectIdentifiers.id_dsa_with_sha3_256, DERNull.INSTANCE));
        addToMaps(GordianSignatureSpecBuilder.dsa(GordianSignatureType.DSA, GordianDigestSpecBuilder.sha3(GordianLength.LEN_384)),
                new AlgorithmIdentifier(NISTObjectIdentifiers.id_dsa_with_sha3_384, DERNull.INSTANCE));
        addToMaps(GordianSignatureSpecBuilder.dsa(GordianSignatureType.DSA, GordianDigestSpecBuilder.sha3(GordianLength.LEN_512)),
                new AlgorithmIdentifier(NISTObjectIdentifiers.id_dsa_with_sha3_512, DERNull.INSTANCE));
    }

    /**
     * Add EC signatures.
     */
    private void addECSignatures() {
        addToMaps(GordianSignatureSpecBuilder.ec(GordianSignatureType.DSA, GordianDigestSpecBuilder.sha1()),
                new AlgorithmIdentifier(X9ObjectIdentifiers.ecdsa_with_SHA1, DERNull.INSTANCE));
        addToMaps(GordianSignatureSpecBuilder.ec(GordianSignatureType.DSA, GordianDigestSpecBuilder.sha2(GordianLength.LEN_224)),
                new AlgorithmIdentifier(X9ObjectIdentifiers.ecdsa_with_SHA224, DERNull.INSTANCE));
        addToMaps(GordianSignatureSpecBuilder.ec(GordianSignatureType.DSA, GordianDigestSpecBuilder.sha2(GordianLength.LEN_256)),
                new AlgorithmIdentifier(X9ObjectIdentifiers.ecdsa_with_SHA256, DERNull.INSTANCE));
        addToMaps(GordianSignatureSpecBuilder.ec(GordianSignatureType.DSA, GordianDigestSpecBuilder.sha2(GordianLength.LEN_384)),
                new AlgorithmIdentifier(X9ObjectIdentifiers.ecdsa_with_SHA384, DERNull.INSTANCE));
        addToMaps(GordianSignatureSpecBuilder.ec(GordianSignatureType.DSA, GordianDigestSpecBuilder.sha2(GordianLength.LEN_512)),
                new AlgorithmIdentifier(X9ObjectIdentifiers.ecdsa_with_SHA512, DERNull.INSTANCE));
        addToMaps(GordianSignatureSpecBuilder.ec(GordianSignatureType.DSA, GordianDigestSpecBuilder.sha3(GordianLength.LEN_224)),
                new AlgorithmIdentifier(NISTObjectIdentifiers.id_ecdsa_with_sha3_224, DERNull.INSTANCE));
        addToMaps(GordianSignatureSpecBuilder.ec(GordianSignatureType.DSA, GordianDigestSpecBuilder.sha3(GordianLength.LEN_256)),
                new AlgorithmIdentifier(NISTObjectIdentifiers.id_ecdsa_with_sha3_256, DERNull.INSTANCE));
        addToMaps(GordianSignatureSpecBuilder.ec(GordianSignatureType.DSA, GordianDigestSpecBuilder.sha3(GordianLength.LEN_384)),
                new AlgorithmIdentifier(NISTObjectIdentifiers.id_ecdsa_with_sha3_384, DERNull.INSTANCE));
        addToMaps(GordianSignatureSpecBuilder.ec(GordianSignatureType.DSA, GordianDigestSpecBuilder.sha3(GordianLength.LEN_512)),
                new AlgorithmIdentifier(NISTObjectIdentifiers.id_ecdsa_with_sha3_512, DERNull.INSTANCE));
    }

    /**
     * Add SM2 signatures.
     */
    private void addSM2Signatures() {
        addToMaps(GordianSignatureSpecBuilder.sm2(GordianDigestSpecBuilder.sm3()),
                new AlgorithmIdentifier(GMObjectIdentifiers.sm2sign_with_sm3, DERNull.INSTANCE));
        addToMaps(GordianSignatureSpecBuilder.sm2(GordianDigestSpecBuilder.sha2(GordianLength.LEN_256)),
                new AlgorithmIdentifier(GMObjectIdentifiers.sm2sign_with_sha256, DERNull.INSTANCE));
    }

    /**
     * Add GOST signatures.
     */
    private void addGOSTSignatures() {
        addToMaps(GordianSignatureSpecBuilder.gost2012(GordianLength.LEN_256),
                new AlgorithmIdentifier(RosstandartObjectIdentifiers.id_tc26_signwithdigest_gost_3410_12_256, DERNull.INSTANCE));
        addToMaps(GordianSignatureSpecBuilder.gost2012(GordianLength.LEN_512),
                new AlgorithmIdentifier(RosstandartObjectIdentifiers.id_tc26_signwithdigest_gost_3410_12_512, DERNull.INSTANCE));
    }

    /**
     * Add EdDSA signatures.
     */
    private void addEdDSASignatures() {
        addToMaps(GordianSignatureSpecBuilder.edDSA(), GordianKeyPairSpecBuilder.ed25519(),
                new AlgorithmIdentifier(EdECObjectIdentifiers.id_Ed25519, DERNull.INSTANCE));
        addToMaps(GordianSignatureSpecBuilder.edDSA(), GordianKeyPairSpecBuilder.ed448(),
                new AlgorithmIdentifier(EdECObjectIdentifiers.id_Ed448, DERNull.INSTANCE));
    }

    /**
     * Add postQuantum signatures.
     */
    private void addPostQuantumSignatures() {
        /* Add Picnic signatures */
        addToMaps(GordianSignatureSpecBuilder.picnic(),
                new AlgorithmIdentifier(BCObjectIdentifiers.picnic_signature, DERNull.INSTANCE));
        addToMaps(GordianSignatureSpecBuilder.picnic(GordianDigestSpecBuilder.sha2(GordianLength.LEN_512)),
                new AlgorithmIdentifier(BCObjectIdentifiers.picnic_with_sha512, DERNull.INSTANCE));
        addToMaps(GordianSignatureSpecBuilder.picnic(GordianDigestSpecBuilder.sha3(GordianLength.LEN_512)),
                new AlgorithmIdentifier(BCObjectIdentifiers.picnic_with_sha3_512, DERNull.INSTANCE));
        addToMaps(GordianSignatureSpecBuilder.picnic(GordianDigestSpecBuilder.shake256()),
                new AlgorithmIdentifier(BCObjectIdentifiers.picnic_with_shake256, DERNull.INSTANCE));

        /* Add LMS signatures */
        addToMaps(GordianSignatureSpecBuilder.lms(),
                new AlgorithmIdentifier(PKCSObjectIdentifiers.id_alg_hss_lms_hashsig, DERNull.INSTANCE));

        /* Add signatures that use keyPair id */
        addMLDSASignatures();
        addSLHDSASignatures();
        addFalconSignatures();
        addMayoSignatures();
        addSnovaSignatures();

        /* Add XMSS signatures */
        addXMSSSignatures();
    }

    /**
     * Add SLHDSA signatures.
     */
    private void addMLDSASignatures() {
        for (GordianMLDSASpec mySpec : GordianMLDSASpec.values()) {
            addToMaps(GordianSignatureSpecBuilder.mldsa(), GordianKeyPairSpecBuilder.mldsa(mySpec).getSubKeyType(),
                    new AlgorithmIdentifier(mySpec.getIdentifier(), DERNull.INSTANCE));
        }
    }

    /**
     * Add SLHDSA signatures.
     */
    private void addSLHDSASignatures() {
        for (GordianSLHDSASpec mySpec : GordianSLHDSASpec.values()) {
            addToMaps(GordianSignatureSpecBuilder.slhdsa(), GordianKeyPairSpecBuilder.slhdsa(mySpec).getSubKeyType(),
                    new AlgorithmIdentifier(mySpec.getIdentifier(), DERNull.INSTANCE));
        }
    }

    /**
     * Add Falcon signatures.
     */
    private void addFalconSignatures() {
        for (GordianFalconSpec mySpec : GordianFalconSpec.values()) {
            addToMaps(GordianSignatureSpecBuilder.falcon(), GordianKeyPairSpecBuilder.falcon(mySpec).getSubKeyType(),
                    new AlgorithmIdentifier(mySpec.getIdentifier(), DERNull.INSTANCE));
        }
    }

    /**
     * Add Mayo signatures.
     */
    private void addMayoSignatures() {
        for (GordianMayoSpec mySpec : GordianMayoSpec.values()) {
            addToMaps(GordianSignatureSpecBuilder.mayo(), GordianKeyPairSpecBuilder.mayo(mySpec).getSubKeyType(),
                    new AlgorithmIdentifier(mySpec.getIdentifier(), DERNull.INSTANCE));
        }
    }

    /**
     * Add Snova signatures.
     */
    private void addSnovaSignatures() {
        for (GordianSnovaSpec mySpec : GordianSnovaSpec.values()) {
            addToMaps(GordianSignatureSpecBuilder.snova(), GordianKeyPairSpecBuilder.snova(mySpec).getSubKeyType(),
                    new AlgorithmIdentifier(mySpec.getIdentifier(), DERNull.INSTANCE));
        }
    }

    /**
     * Add XMSS signatures.
     */
    private void addXMSSSignatures() {
        /* List XMSS Sha256 signatures */
        for (GordianXMSSKeySpec mySpec : GordianXMSSKeySpec.listPossibleKeySpecs(GordianXMSSDigestType.SHA256)) {
            /* If this is an XMSSMT spec */
            if (mySpec.isMT()) {
                addToMaps(GordianSignatureSpecBuilder.xmss(), mySpec,
                        new AlgorithmIdentifier(BCObjectIdentifiers.xmss_mt_SHA256, DERNull.INSTANCE));
                addToMaps(GordianSignatureSpecBuilder.xmssph(), mySpec,
                        new AlgorithmIdentifier(BCObjectIdentifiers.xmss_mt_SHA256ph, DERNull.INSTANCE));

            } else {
                addToMaps(GordianSignatureSpecBuilder.xmss(), mySpec,
                        new AlgorithmIdentifier(BCObjectIdentifiers.xmss_SHA256, DERNull.INSTANCE));
                addToMaps(GordianSignatureSpecBuilder.xmssph(), mySpec,
                        new AlgorithmIdentifier(BCObjectIdentifiers.xmss_SHA256ph, DERNull.INSTANCE));
            }
        }

        /* List XMSS Sha512 signatures */
        for (GordianXMSSKeySpec mySpec : GordianXMSSKeySpec.listPossibleKeySpecs(GordianXMSSDigestType.SHA512)) {
            /* If this is an XMSSMT spec */
            if (mySpec.isMT()) {
                addToMaps(GordianSignatureSpecBuilder.xmss(), mySpec,
                        new AlgorithmIdentifier(BCObjectIdentifiers.xmss_mt_SHA512, DERNull.INSTANCE));
                addToMaps(GordianSignatureSpecBuilder.xmssph(), mySpec,
                        new AlgorithmIdentifier(BCObjectIdentifiers.xmss_mt_SHA512ph, DERNull.INSTANCE));

            } else {
                addToMaps(GordianSignatureSpecBuilder.xmss(), mySpec,
                        new AlgorithmIdentifier(BCObjectIdentifiers.xmss_SHA512, DERNull.INSTANCE));
                addToMaps(GordianSignatureSpecBuilder.xmssph(), mySpec,
                        new AlgorithmIdentifier(BCObjectIdentifiers.xmss_SHA512ph, DERNull.INSTANCE));
            }
        }

        /* List XMSS Shake128 signatures */
        for (GordianXMSSKeySpec mySpec : GordianXMSSKeySpec.listPossibleKeySpecs(GordianXMSSDigestType.SHAKE128)) {
            /* If this is an XMSSMT spec */
            if (mySpec.isMT()) {
                addToMaps(GordianSignatureSpecBuilder.xmss(), mySpec,
                        new AlgorithmIdentifier(BCObjectIdentifiers.xmss_mt_SHAKE128, DERNull.INSTANCE));
                addToMaps(GordianSignatureSpecBuilder.xmssph(), mySpec,
                        new AlgorithmIdentifier(BCObjectIdentifiers.xmss_mt_SHAKE128ph, DERNull.INSTANCE));

            } else {
                addToMaps(GordianSignatureSpecBuilder.xmss(), mySpec,
                        new AlgorithmIdentifier(BCObjectIdentifiers.xmss_SHAKE128, DERNull.INSTANCE));
                addToMaps(GordianSignatureSpecBuilder.xmssph(), mySpec,
                        new AlgorithmIdentifier(BCObjectIdentifiers.xmss_SHAKE128ph, DERNull.INSTANCE));
            }
        }

        /* List XMSS Shake128 signatures */
        for (GordianXMSSKeySpec mySpec : GordianXMSSKeySpec.listPossibleKeySpecs(GordianXMSSDigestType.SHAKE256)) {
            /* If this is an XMSSMT spec */
            if (mySpec.isMT()) {
                addToMaps(GordianSignatureSpecBuilder.xmss(), mySpec,
                        new AlgorithmIdentifier(BCObjectIdentifiers.xmss_mt_SHAKE256, DERNull.INSTANCE));
                addToMaps(GordianSignatureSpecBuilder.xmssph(), mySpec,
                        new AlgorithmIdentifier(BCObjectIdentifiers.xmss_mt_SHAKE256ph, DERNull.INSTANCE));

            } else {
                addToMaps(GordianSignatureSpecBuilder.xmss(), mySpec,
                        new AlgorithmIdentifier(BCObjectIdentifiers.xmss_SHAKE256, DERNull.INSTANCE));
                addToMaps(GordianSignatureSpecBuilder.xmssph(), mySpec,
                        new AlgorithmIdentifier(BCObjectIdentifiers.xmss_SHAKE256ph, DERNull.INSTANCE));
            }
        }
    }

    /**
     * Create PSS Parameters.
     * @param pHash the hash algorithmId
     * @param pSaltSize the saltSize
     * @return the params
     */
    private static RSASSAPSSparams createPSSParams(final ASN1ObjectIdentifier pHash,
                                                   final GordianLength pSaltSize) {
        final AlgorithmIdentifier myId = new AlgorithmIdentifier(pHash, DERNull.INSTANCE);
        return new RSASSAPSSparams(myId,
                new AlgorithmIdentifier(PKCSObjectIdentifiers.id_mgf1, myId),
                new ASN1Integer(pSaltSize.getByteLength()),
                new ASN1Integer(1));
    }

    /**
     * Add PSS SHAKE128 Algorithms.
     * @param pSpec the digestSpec
     * @param pHash the hash algorithmId
     * @param pSaltSize the saltSize
     */
    private void addPSS128Algorithms(final GordianDigestSpec pSpec,
                                     final ASN1ObjectIdentifier pHash,
                                     final GordianLength pSaltSize) {
        /* Loop through the RSAModulii */
        for (GordianRSAModulus myModulus : GordianRSAModulus.values()) {
            addToMaps(GordianSignatureSpecBuilder.rsa(GordianSignatureType.PSS128, pSpec),
                    myModulus, new AlgorithmIdentifier(PKCSObjectIdentifiers.id_RSASSA_PSS,
                                                       createPSS128Params(pHash, myModulus, pSaltSize)));
        }
    }

    /**
     * Create PSS SHAKE128 Parameters.
     * @param pHash the hash algorithmId
     * @param pModulus the RSA modulus
     * @param pSaltSize the saltSize
     * @return the params
     */
    private static RSASSAPSSparams createPSS128Params(final ASN1ObjectIdentifier pHash,
                                                      final GordianRSAModulus pModulus,
                                                      final GordianLength pSaltSize) {
        final AlgorithmIdentifier myId = NISTObjectIdentifiers.id_shake128_len.equals(pHash)
                    ? new AlgorithmIdentifier(pHash, new ASN1Integer(GordianLength.LEN_256.getByteLength()))
                    : new AlgorithmIdentifier(pHash, DERNull.INSTANCE);
        final int myShakeLen = GordianLength.LEN_256.getLength();
        final int myLen = (pModulus.getLength() - myShakeLen  - Byte.SIZE) / Byte.SIZE;
        return new RSASSAPSSparams(myId,
                new AlgorithmIdentifier(NISTObjectIdentifiers.id_shake128, new ASN1Integer(myLen)),
                new ASN1Integer(pSaltSize.getByteLength()),
                new ASN1Integer(1));
    }

    /**
     * Add PSS SHAKE256 Algorithms.
     * @param pSpec the digestSpec
     * @param pHash the hash algorithmId
     * @param pSaltSize the saltSize
     */
    private void addPSS256Algorithms(final GordianDigestSpec pSpec,
                                     final ASN1ObjectIdentifier pHash,
                                     final GordianLength pSaltSize) {
        /* Loop through the RSAModulii */
        for (GordianRSAModulus myModulus : GordianRSAModulus.values()) {
            addToMaps(GordianSignatureSpecBuilder.rsa(GordianSignatureType.PSS256, pSpec),
                    myModulus, new AlgorithmIdentifier(PKCSObjectIdentifiers.id_RSASSA_PSS,
                            createPSS256Params(pHash, myModulus, pSaltSize)));
        }
    }

    /**
     * Create PSS SHAKE256 Parameters.
     * @param pHash the hash algorithmId
     * @param pModulus the RSA modulus
     * @param pSaltSize the saltSize
     * @return the params
     */
    private static RSASSAPSSparams createPSS256Params(final ASN1ObjectIdentifier pHash,
                                                      final GordianRSAModulus pModulus,
                                                      final GordianLength pSaltSize) {
        final AlgorithmIdentifier myId = NISTObjectIdentifiers.id_shake256_len.equals(pHash)
                ? new AlgorithmIdentifier(pHash, new ASN1Integer(GordianLength.LEN_512.getByteLength()))
                : new AlgorithmIdentifier(pHash, DERNull.INSTANCE);
        final int myShakeLen = GordianLength.LEN_512.getLength();
        final int myLen = (pModulus.getLength() - myShakeLen  - Byte.SIZE) / Byte.SIZE;
        return new RSASSAPSSparams(myId,
                new AlgorithmIdentifier(NISTObjectIdentifiers.id_shake256, new ASN1Integer(myLen)),
                new ASN1Integer(pSaltSize.getByteLength()),
                new ASN1Integer(1));
    }

    /**
     * Create Identifiers for all valid SignatureTypes/DigestSpecs.
     * @param pKeyType the keyType
     */
    private void addSignatures(final GordianKeyPairType pKeyType) {
        for (GordianSignatureSpec mySpec : theFactory.listAllSupportedSignatures(pKeyType)) {
            ensureSignature(mySpec);
        }
    }

    /**
     * Add sigSpec to map if supported and not already present.
     * @param pSigSpec the signatureSpec
     */
    private void ensureSignature(final GordianSignatureSpec pSigSpec) {
        /* If the signature is not already known */
        if (!theSpecMap.containsKey(pSigSpec)) {
            addSignature(pSigSpec);
        }
    }

    /**
     * Create Identifier for a signatureSpec.
     * @param pSigSpec the signatureSpec
     */
    private void addSignature(final GordianSignatureSpec pSigSpec) {
        /* Create a branch for signatures based on the AsymKeyType */
        final GordianKeyPairType myKeyType = pSigSpec.getKeyPairType();
        ASN1ObjectIdentifier myId = SIGOID.branch(Integer.toString(myKeyType.ordinal() + 1));

        /* Create a branch for signatures based on the SignatureType */
        final GordianSignatureType mySigType = pSigSpec.getSignatureType();
        myId = myId.branch(Integer.toString(mySigType.ordinal() + 1));

        /* If we have a digestSpec */
        if (pSigSpec.getSignatureSpec() instanceof GordianDigestSpec) {
            /* Create a branch for digest based on the DigestType/Length/State */
            final GordianDigestSpec myDigestSpec = pSigSpec.getDigestSpec();
            myId = myId.branch(Integer.toString(myDigestSpec.getDigestType().ordinal() + 1));
            myId = myId.branch(Integer.toString(myDigestSpec.getDigestLength().ordinal() + 1));

            /* Add a branch if there is a state */
            final GordianDigestState myState = myDigestSpec.getDigestState();
            if (myState != null) {
                myId = myId.branch(Integer.toString(myState.ordinal() + 1));
            }

            /* Add Xof indication */
            myId = myId.branch(myDigestSpec.isXofMode() ? "1" : "2");
        }

        /* Add the id to the maps */
        addToMaps(pSigSpec,  new AlgorithmIdentifier(myId, DERNull.INSTANCE));
    }
}
