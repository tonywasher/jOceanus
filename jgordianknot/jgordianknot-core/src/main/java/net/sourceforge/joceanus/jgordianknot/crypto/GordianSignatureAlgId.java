/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2018 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.crypto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.asn1.gm.GMObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.RSASSAPSSparams;
import org.bouncycastle.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;

import net.sourceforge.joceanus.jgordianknot.GordianDataException;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * OID Manager for Signatures.
 */
public class GordianSignatureAlgId {
    /**
     *  Base our signatures off bouncyCastle.
     */
    private static final ASN1ObjectIdentifier BASEOID = BCObjectIdentifiers.bc.branch("100");

    /**
     * Map of SignatureSpec to Identifier.
     */
    private final Map<GordianSignatureSpec, AlgorithmIdentifier> theSpecMap;

    /**
     * Map of Identifier to SignatureSpec.
     */
    private final Map<AlgorithmIdentifier, GordianSignatureSpec> theIdentifierMap;

    /**
     *  The predicate for valid for SignatureSpecs.
     */
    private final Predicate<GordianSignatureSpec> thePredicate;

    /**
     *  The list of possible digestSpecs.
     */
    private final List<GordianDigestSpec> theDigests;

    /**
     * Constructor.
     * @param pFactory the factory
     */
    protected GordianSignatureAlgId(final GordianFactory pFactory) {
        /* Create the maps */
        theSpecMap = new HashMap<>();
        theIdentifierMap = new HashMap<>();

        /* Access the predicate and digests */
        thePredicate = pFactory.supportedSignatureSpec();
        theDigests = GordianDigestSpec.listAll();

        /* Populate with the public standards */
        addRSASignatures();
        addDSASignatures();
        addECSignatures();

        /* Loop through the possible AsymKeys */
        for (GordianAsymKeyType myKeyType : GordianAsymKeyType.values()) {
            addSignatures(myKeyType);
        }

        /* Clear out the Digest list */
        theDigests.clear();
    }

    /**
     * Obtain Identifier for SignatureSpec.
     * @param pSpec the signatureSpec.
     * @return the Identifier
     */
    public AlgorithmIdentifier getIdentifierForSpec(final GordianSignatureSpec pSpec) {
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
        addToMaps(GordianSignatureSpec.sm2(),
                  new AlgorithmIdentifier(GMObjectIdentifiers.sm2sign_with_sm3));
        addToMaps(GordianSignatureSpec.gost2012(GordianLength.LEN_256),
                  new AlgorithmIdentifier(RosstandartObjectIdentifiers.id_tc26_signwithdigest_gost_3410_12_256));
        addToMaps(GordianSignatureSpec.gost2012(GordianLength.LEN_512),
                  new AlgorithmIdentifier(RosstandartObjectIdentifiers.id_tc26_signwithdigest_gost_3410_12_512));
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
        for (GordianSignatureType mySigType : pKeyType.getSupportedSignatures()) {
            addSignatures(pKeyType, mySigType);
        }
    }

    /**
     * Create Identifiers for all supported DigestSpecs.
     * @param pKeyType the keyType
     * @param pSigType the signatureType
     */
    private void addSignatures(final GordianAsymKeyType pKeyType,
                               final GordianSignatureType pSigType) {
         for (GordianDigestSpec mySpec : theDigests) {
            /* Create the corresponding signatureSpec */
            final GordianSignatureSpec mySign = new GordianSignatureSpec(pKeyType, pSigType, mySpec);

            /* If the signature is supported and not already known */
            if (thePredicate.test(mySign)
                && !theSpecMap.containsKey(mySign)) {
                addSignature(mySign);
            }
        }
    }

    /**
     * Create Identifier for a signatureSpec.
     * @param pSigSpec the signatureSpec
     */
    private void addSignature(final GordianSignatureSpec pSigSpec) {
        /* Create a branch for signatures based on the AsymKeyType */
        final GordianAsymKeyType myKeyType = pSigSpec.getAsymKeyType();
        ASN1ObjectIdentifier myId = BASEOID.branch(Integer.toString(myKeyType.ordinal() + 1));

        /* Create a branch for signatures based on the SignatureType */
        final GordianSignatureType mySigType = pSigSpec.getSignatureType();
        myId = myId.branch(Integer.toString(mySigType.ordinal() + 1));

        /* Create a branch for digest based on the DigestType/Length/State */
        final GordianDigestSpec myDigestSpec = pSigSpec.getDigestSpec();
        myId = myId.branch(Integer.toString(myDigestSpec.getDigestType().ordinal() + 1));
        myId = myId.branch(Integer.toString(myDigestSpec.getDigestLength().ordinal() + 1));

        /* Add an additional branch if there is a stateLength */
        final GordianLength myState = myDigestSpec.getStateLength();
        if (myState != null) {
            myId = myId.branch(Integer.toString(myState.ordinal() + 1));
        }

        /* Add the id to the maps */
        addToMaps(pSigSpec,  new AlgorithmIdentifier(myId));
    }
}
