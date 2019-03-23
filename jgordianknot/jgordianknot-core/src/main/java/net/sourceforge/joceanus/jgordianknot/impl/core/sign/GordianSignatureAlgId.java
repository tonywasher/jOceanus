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
package net.sourceforge.joceanus.jgordianknot.impl.core.sign;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.edec.EdECObjectIdentifiers;
import org.bouncycastle.asn1.gm.GMObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.RSASSAPSSparams;
import org.bouncycastle.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.pqc.asn1.PQCObjectIdentifiers;

import net.sourceforge.joceanus.jgordianknot.api.asym.GordianAsymKeyType;
import net.sourceforge.joceanus.jgordianknot.api.asym.GordianQTESLAKeyType;
import net.sourceforge.joceanus.jgordianknot.api.asym.GordianSPHINCSKeyType;
import net.sourceforge.joceanus.jgordianknot.api.asym.GordianXMSSKeyType;
import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestSpec;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.api.sign.GordianSignatureFactory;
import net.sourceforge.joceanus.jgordianknot.api.sign.GordianSignatureSpec;
import net.sourceforge.joceanus.jgordianknot.api.sign.GordianSignatureType;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * OID Manager for Signatures.
 */
public class GordianSignatureAlgId {
    /**
     *  Base our signatures off bouncyCastle.
     */
    private static final ASN1ObjectIdentifier SIGOID = GordianCoreFactory.BASEOID.branch("20");

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
    GordianSignatureAlgId(final GordianCoreFactory pFactory) {
        /* Create the maps */
        theSpecMap = new HashMap<>();
        theSpecSubTypeMap = new HashMap<>();
        theIdentifierMap = new HashMap<>();

        /* Access the asymFactory and digests */
        theFactory = pFactory.getAsymmetricFactory().getSignatureFactory();

        /* Populate with the public standards */
        addRSASignatures();
        addDSASignatures();
        addECSignatures();
        addSM2Signatures();
        addGOSTSignatures();
        addEdDSASignatures();
        addPostQuantumSignatures();

        /* Loop through the possible AsymKeys */
        for (GordianAsymKeyType myKeyType : GordianAsymKeyType.values()) {
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
    public AlgorithmIdentifier getIdentifierForSpecAndKeyPair(final GordianSignatureSpec pSpec,
                                                              final GordianKeyPair pKeyPair) {
        /* If we need to use the subType */
        if (pSpec.getAsymKeyType().subTypeForSignatures()) {
            /* Look up in the subKey map */
            final Map<Object, AlgorithmIdentifier> myMap = theSpecSubTypeMap.get(pSpec);
            return myMap == null
                   ? null
                   : myMap.get(pKeyPair.getKeySpec().getSubKeyType());
        }

        /* Look up in the standard map */
        return theSpecMap.get(pSpec);
    }

    /**
     * Obtain SignatureSpec for Identifier.
     * @param pIdentifier the identifier.
     * @return the signatureSpec
     * @throws OceanusException on error
     */
    public GordianSignatureSpec getSpecForIdentifier(final AlgorithmIdentifier pIdentifier) throws OceanusException {
        final GordianSignatureSpec mySpec = theIdentifierMap.get(pIdentifier);
        if (mySpec == null) {
            throw new GordianDataException("Invalid identifier " + pIdentifier);
        }
        return mySpec;
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
        addToMaps(GordianSignatureSpec.rsa(GordianSignatureType.PSS, GordianDigestSpec.sha1()),
                new AlgorithmIdentifier(PKCSObjectIdentifiers.id_RSASSA_PSS,
                        createPSSParams(OIWObjectIdentifiers.idSHA1, GordianLength.LEN_160)));
        addToMaps(GordianSignatureSpec.rsa(GordianSignatureType.PSS, GordianDigestSpec.sha2(GordianLength.LEN_224)),
                new AlgorithmIdentifier(PKCSObjectIdentifiers.id_RSASSA_PSS,
                        createPSSParams(NISTObjectIdentifiers.id_sha224, GordianLength.LEN_224)));
        addToMaps(GordianSignatureSpec.rsa(GordianSignatureType.PSS, GordianDigestSpec.sha2(GordianLength.LEN_256)),
                new AlgorithmIdentifier(PKCSObjectIdentifiers.id_RSASSA_PSS,
                        createPSSParams(NISTObjectIdentifiers.id_sha256, GordianLength.LEN_256)));
        addToMaps(GordianSignatureSpec.rsa(GordianSignatureType.PSS, GordianDigestSpec.sha2(GordianLength.LEN_384)),
                new AlgorithmIdentifier(PKCSObjectIdentifiers.id_RSASSA_PSS,
                        createPSSParams(NISTObjectIdentifiers.id_sha384, GordianLength.LEN_384)));
        addToMaps(GordianSignatureSpec.rsa(GordianSignatureType.PSS, GordianDigestSpec.sha2(GordianLength.LEN_512)),
                new AlgorithmIdentifier(PKCSObjectIdentifiers.id_RSASSA_PSS,
                        createPSSParams(NISTObjectIdentifiers.id_sha512, GordianLength.LEN_512)));
        addToMaps(GordianSignatureSpec.rsa(GordianSignatureType.PSS, GordianDigestSpec.sha3(GordianLength.LEN_224)),
                new AlgorithmIdentifier(PKCSObjectIdentifiers.id_RSASSA_PSS,
                        createPSSParams(NISTObjectIdentifiers.id_sha3_224, GordianLength.LEN_224)));
        addToMaps(GordianSignatureSpec.rsa(GordianSignatureType.PSS, GordianDigestSpec.sha3(GordianLength.LEN_256)),
                new AlgorithmIdentifier(PKCSObjectIdentifiers.id_RSASSA_PSS,
                        createPSSParams(NISTObjectIdentifiers.id_sha3_256, GordianLength.LEN_256)));
        addToMaps(GordianSignatureSpec.rsa(GordianSignatureType.PSS, GordianDigestSpec.sha3(GordianLength.LEN_384)),
                new AlgorithmIdentifier(PKCSObjectIdentifiers.id_RSASSA_PSS,
                        createPSSParams(NISTObjectIdentifiers.id_sha3_384, GordianLength.LEN_384)));
        addToMaps(GordianSignatureSpec.rsa(GordianSignatureType.PSS, GordianDigestSpec.sha3(GordianLength.LEN_512)),
                new AlgorithmIdentifier(PKCSObjectIdentifiers.id_RSASSA_PSS,
                        createPSSParams(NISTObjectIdentifiers.id_sha3_512, GordianLength.LEN_512)));
    }

    /**
     * Add DSA signatures.
     */
    private void addDSASignatures() {
        addToMaps(GordianSignatureSpec.dsa(GordianSignatureType.DSA, GordianDigestSpec.sha1()),
                new AlgorithmIdentifier(X9ObjectIdentifiers.id_dsa_with_sha1));
        addToMaps(GordianSignatureSpec.dsa(GordianSignatureType.DSA, GordianDigestSpec.sha2(GordianLength.LEN_224)),
                new AlgorithmIdentifier(NISTObjectIdentifiers.dsa_with_sha224));
        addToMaps(GordianSignatureSpec.dsa(GordianSignatureType.DSA, GordianDigestSpec.sha2(GordianLength.LEN_256)),
                new AlgorithmIdentifier(NISTObjectIdentifiers.dsa_with_sha256));
        addToMaps(GordianSignatureSpec.dsa(GordianSignatureType.DSA, GordianDigestSpec.sha2(GordianLength.LEN_384)),
                new AlgorithmIdentifier(NISTObjectIdentifiers.dsa_with_sha384));
        addToMaps(GordianSignatureSpec.dsa(GordianSignatureType.DSA, GordianDigestSpec.sha2(GordianLength.LEN_512)),
                new AlgorithmIdentifier(NISTObjectIdentifiers.dsa_with_sha512));
        addToMaps(GordianSignatureSpec.dsa(GordianSignatureType.DSA, GordianDigestSpec.sha3(GordianLength.LEN_224)),
                new AlgorithmIdentifier(NISTObjectIdentifiers.id_dsa_with_sha3_224));
        addToMaps(GordianSignatureSpec.dsa(GordianSignatureType.DSA, GordianDigestSpec.sha3(GordianLength.LEN_256)),
                new AlgorithmIdentifier(NISTObjectIdentifiers.id_dsa_with_sha3_256));
        addToMaps(GordianSignatureSpec.dsa(GordianSignatureType.DSA, GordianDigestSpec.sha3(GordianLength.LEN_384)),
                new AlgorithmIdentifier(NISTObjectIdentifiers.id_dsa_with_sha3_384));
        addToMaps(GordianSignatureSpec.dsa(GordianSignatureType.DSA, GordianDigestSpec.sha3(GordianLength.LEN_512)),
                new AlgorithmIdentifier(NISTObjectIdentifiers.id_dsa_with_sha3_512));
    }

    /**
     * Add EC signatures.
     */
    private void addECSignatures() {
        addToMaps(GordianSignatureSpec.ec(GordianSignatureType.DSA, GordianDigestSpec.sha1()),
                new AlgorithmIdentifier(X9ObjectIdentifiers.ecdsa_with_SHA1));
        addToMaps(GordianSignatureSpec.ec(GordianSignatureType.DSA, GordianDigestSpec.sha2(GordianLength.LEN_224)),
                new AlgorithmIdentifier(X9ObjectIdentifiers.ecdsa_with_SHA224));
        addToMaps(GordianSignatureSpec.ec(GordianSignatureType.DSA, GordianDigestSpec.sha2(GordianLength.LEN_256)),
                new AlgorithmIdentifier(X9ObjectIdentifiers.ecdsa_with_SHA256));
        addToMaps(GordianSignatureSpec.ec(GordianSignatureType.DSA, GordianDigestSpec.sha2(GordianLength.LEN_384)),
                new AlgorithmIdentifier(X9ObjectIdentifiers.ecdsa_with_SHA384));
        addToMaps(GordianSignatureSpec.ec(GordianSignatureType.DSA, GordianDigestSpec.sha2(GordianLength.LEN_512)),
                new AlgorithmIdentifier(X9ObjectIdentifiers.ecdsa_with_SHA512));
        addToMaps(GordianSignatureSpec.ec(GordianSignatureType.DSA, GordianDigestSpec.sha3(GordianLength.LEN_224)),
                new AlgorithmIdentifier(NISTObjectIdentifiers.id_ecdsa_with_sha3_224));
        addToMaps(GordianSignatureSpec.ec(GordianSignatureType.DSA, GordianDigestSpec.sha3(GordianLength.LEN_256)),
                new AlgorithmIdentifier(NISTObjectIdentifiers.id_ecdsa_with_sha3_256));
        addToMaps(GordianSignatureSpec.ec(GordianSignatureType.DSA, GordianDigestSpec.sha3(GordianLength.LEN_384)),
                new AlgorithmIdentifier(NISTObjectIdentifiers.id_ecdsa_with_sha3_384));
        addToMaps(GordianSignatureSpec.ec(GordianSignatureType.DSA, GordianDigestSpec.sha3(GordianLength.LEN_512)),
                new AlgorithmIdentifier(NISTObjectIdentifiers.id_ecdsa_with_sha3_512));
    }

    /**
     * Add SM2 signatures.
     */
    private void addSM2Signatures() {
        addToMaps(GordianSignatureSpec.sm2(),
                new AlgorithmIdentifier(GMObjectIdentifiers.sm2sign_with_sm3));
    }

    /**
     * Add GOST signatures.
     */
    private void addGOSTSignatures() {
        addToMaps(GordianSignatureSpec.gost2012(GordianLength.LEN_256),
                new AlgorithmIdentifier(RosstandartObjectIdentifiers.id_tc26_signwithdigest_gost_3410_12_256));
        addToMaps(GordianSignatureSpec.gost2012(GordianLength.LEN_512),
                new AlgorithmIdentifier(RosstandartObjectIdentifiers.id_tc26_signwithdigest_gost_3410_12_512));
    }

    /**
     * Add EdDSA signatures.
     */
    private void addEdDSASignatures() {
        addToMaps(GordianSignatureSpec.ed25519(),
                new AlgorithmIdentifier(EdECObjectIdentifiers.id_Ed25519));
        addToMaps(GordianSignatureSpec.ed448(),
                new AlgorithmIdentifier(EdECObjectIdentifiers.id_Ed448));
    }

    /**
     * Add postQuantum signatures.
     */
    private void addPostQuantumSignatures() {
        addToMaps(GordianSignatureSpec.rainbow(GordianDigestSpec.sha1()),
                new AlgorithmIdentifier(PQCObjectIdentifiers.rainbowWithSha1));
        addToMaps(GordianSignatureSpec.rainbow(GordianDigestSpec.sha2(GordianLength.LEN_224)),
                new AlgorithmIdentifier(PQCObjectIdentifiers.rainbowWithSha224));
        addToMaps(GordianSignatureSpec.rainbow(GordianDigestSpec.sha2(GordianLength.LEN_256)),
                new AlgorithmIdentifier(PQCObjectIdentifiers.rainbowWithSha256));
        addToMaps(GordianSignatureSpec.rainbow(GordianDigestSpec.sha2(GordianLength.LEN_384)),
                new AlgorithmIdentifier(PQCObjectIdentifiers.rainbowWithSha384));
        addToMaps(GordianSignatureSpec.rainbow(GordianDigestSpec.sha2(GordianLength.LEN_512)),
                new AlgorithmIdentifier(PQCObjectIdentifiers.rainbowWithSha512));

        /* Note that we have multiple signatures oids per spec */
        addToMaps(GordianSignatureSpec.qTESLA(), GordianQTESLAKeyType.HEURISTIC_I,
                new AlgorithmIdentifier(PQCObjectIdentifiers.qTESLA_I));
        addToMaps(GordianSignatureSpec.qTESLA(), GordianQTESLAKeyType.HEURISTIC_III_SIZE,
                new AlgorithmIdentifier(PQCObjectIdentifiers.qTESLA_III_size));
        addToMaps(GordianSignatureSpec.qTESLA(), GordianQTESLAKeyType.HEURISTIC_III_SPEED,
                new AlgorithmIdentifier(PQCObjectIdentifiers.qTESLA_III_speed));
        addToMaps(GordianSignatureSpec.qTESLA(), GordianQTESLAKeyType.PROVABLY_SECURE_I,
                new AlgorithmIdentifier(PQCObjectIdentifiers.qTESLA_p_I));
        addToMaps(GordianSignatureSpec.qTESLA(), GordianQTESLAKeyType.PROVABLY_SECURE_III,
                new AlgorithmIdentifier(PQCObjectIdentifiers.qTESLA_p_III));
        addToMaps(GordianSignatureSpec.sphincs(), GordianSPHINCSKeyType.SHA2,
                new AlgorithmIdentifier(PQCObjectIdentifiers.sphincs256_with_SHA512));
        addToMaps(GordianSignatureSpec.sphincs(), GordianSPHINCSKeyType.SHA3,
                new AlgorithmIdentifier(PQCObjectIdentifiers.sphincs256_with_SHA3_512));
        addToMaps(GordianSignatureSpec.xmss(), GordianXMSSKeyType.SHA256,
                new AlgorithmIdentifier(PQCObjectIdentifiers.xmss_SHA256));
        addToMaps(GordianSignatureSpec.xmssph(), GordianXMSSKeyType.SHA256,
                new AlgorithmIdentifier(PQCObjectIdentifiers.xmss_SHA256ph));
        addToMaps(GordianSignatureSpec.xmss(), GordianXMSSKeyType.SHA512,
                new AlgorithmIdentifier(PQCObjectIdentifiers.xmss_SHA512));
        addToMaps(GordianSignatureSpec.xmssph(), GordianXMSSKeyType.SHA512,
                new AlgorithmIdentifier(PQCObjectIdentifiers.xmss_SHA512ph));
        addToMaps(GordianSignatureSpec.xmss(), GordianXMSSKeyType.SHAKE128,
                new AlgorithmIdentifier(PQCObjectIdentifiers.xmss_SHAKE128));
        addToMaps(GordianSignatureSpec.xmssph(), GordianXMSSKeyType.SHAKE128,
                new AlgorithmIdentifier(PQCObjectIdentifiers.xmss_SHAKE128ph));
        addToMaps(GordianSignatureSpec.xmss(), GordianXMSSKeyType.SHAKE256,
                new AlgorithmIdentifier(PQCObjectIdentifiers.xmss_SHAKE256));
        addToMaps(GordianSignatureSpec.xmssph(), GordianXMSSKeyType.SHAKE256,
                new AlgorithmIdentifier(PQCObjectIdentifiers.xmss_SHAKE256ph));
        addToMaps(GordianSignatureSpec.xmssmt(), GordianXMSSKeyType.SHA256,
                new AlgorithmIdentifier(PQCObjectIdentifiers.xmss_mt_SHA256));
        addToMaps(GordianSignatureSpec.xmssmtph(), GordianXMSSKeyType.SHA256,
                new AlgorithmIdentifier(PQCObjectIdentifiers.xmss_mt_SHA256ph));
        addToMaps(GordianSignatureSpec.xmssmt(), GordianXMSSKeyType.SHA512,
                new AlgorithmIdentifier(PQCObjectIdentifiers.xmss_mt_SHA512));
        addToMaps(GordianSignatureSpec.xmssmtph(), GordianXMSSKeyType.SHA512,
                new AlgorithmIdentifier(PQCObjectIdentifiers.xmss_mt_SHA512ph));
        addToMaps(GordianSignatureSpec.xmssmt(), GordianXMSSKeyType.SHAKE128,
                new AlgorithmIdentifier(PQCObjectIdentifiers.xmss_mt_SHAKE128));
        addToMaps(GordianSignatureSpec.xmssmtph(), GordianXMSSKeyType.SHAKE128,
                new AlgorithmIdentifier(PQCObjectIdentifiers.xmss_mt_SHAKE128ph));
        addToMaps(GordianSignatureSpec.xmssmt(), GordianXMSSKeyType.SHAKE256,
                new AlgorithmIdentifier(PQCObjectIdentifiers.xmss_mt_SHAKE256));
        addToMaps(GordianSignatureSpec.xmssmtph(), GordianXMSSKeyType.SHAKE256,
                new AlgorithmIdentifier(PQCObjectIdentifiers.xmss_mt_SHAKE256ph));
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
     * Create Identifiers for all valid SignatureTypes/DigestSpecs.
     * @param pKeyType the keyType
     */
    private void addSignatures(final GordianAsymKeyType pKeyType) {
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
        final GordianAsymKeyType myKeyType = pSigSpec.getAsymKeyType();
        ASN1ObjectIdentifier myId = SIGOID.branch(Integer.toString(myKeyType.ordinal() + 1));

        /* Create a branch for signatures based on the SignatureType */
        final GordianSignatureType mySigType = pSigSpec.getSignatureType();
        myId = myId.branch(Integer.toString(mySigType.ordinal() + 1));

        /* If we have a digestSpec */
        if (!myKeyType.nullDigestForSignatures()) {
            /* Create a branch for digest based on the DigestType/Length/State */
            final GordianDigestSpec myDigestSpec = pSigSpec.getDigestSpec();
            myId = myId.branch(Integer.toString(myDigestSpec.getDigestType().ordinal() + 1));
            myId = myId.branch(Integer.toString(myDigestSpec.getDigestLength().ordinal() + 1));

            /* Add an additional branch if there is a stateLength */
            final GordianLength myState = myDigestSpec.getStateLength();
            if (myState != null) {
                myId = myId.branch(Integer.toString(myState.ordinal() + 1));
            }
        }

        /* Add the id to the maps */
        addToMaps(pSigSpec,  new AlgorithmIdentifier(myId));
    }
}
