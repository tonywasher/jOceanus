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
package net.sourceforge.joceanus.gordianknot.impl.core.base;

import net.sourceforge.joceanus.gordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.gordianknot.api.digest.GordianDigestFactory;
import net.sourceforge.joceanus.gordianknot.api.digest.GordianDigestSpec;
import net.sourceforge.joceanus.gordianknot.api.digest.GordianDigestSpecBuilder;
import net.sourceforge.joceanus.gordianknot.api.digest.GordianDigestSubSpec.GordianDigestState;
import net.sourceforge.joceanus.gordianknot.api.digest.GordianDigestType;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.gm.GMObjectIdentifiers;
import org.bouncycastle.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.asn1.ua.UAObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

import java.util.HashMap;
import java.util.Map;

/**
 * Mappings from EncodedId to DigestSpec.
 */
public class GordianDigestAlgId {
    /**
     * DigestOID branch.
     */
    private static final ASN1ObjectIdentifier DIGESTOID = GordianASN1Util.SYMOID.branch("1");

    /**
     * Map of DigestSpec to Identifier.
     */
    private final Map<GordianDigestSpec, AlgorithmIdentifier> theSpecMap;

    /**
     * Map of Identifier to DigestSpec.
     */
    private final Map<AlgorithmIdentifier, GordianDigestSpec> theIdentifierMap;

    /**
     * Constructor.
     *
     * @param pFactory the factory
     */
    GordianDigestAlgId(final GordianCoreFactory pFactory) {
        /* Create the maps */
        theSpecMap = new HashMap<>();
        theIdentifierMap = new HashMap<>();

        /* Access the digestFactory  */
        final GordianDigestFactory myFactory = pFactory.getDigestFactory();

        /* Populate with the public standards */
        addSHADigests();
        addBlakeDigests();
        addGOSTDigests();
        addSundryDigests();

        /* Loop through the possible DigestSpecs */
        for (GordianDigestSpec mySpec : myFactory.listAllSupportedSpecs()) {
            /* Add any non-standard digests */
            ensureDigest(mySpec);
        }
    }

    /**
     * Obtain Identifier for DigestSpec.
     *
     * @param pSpec the digestSpec.
     * @return the Identifier
     */
    public AlgorithmIdentifier getIdentifierForSpec(final GordianDigestSpec pSpec) {
        return theSpecMap.get(pSpec);
    }

    /**
     * Obtain DigestSpec for Identifier.
     *
     * @param pIdentifier the identifier.
     * @return the digestSpec (or null if not found)
     */
    public GordianDigestSpec getSpecForIdentifier(final AlgorithmIdentifier pIdentifier) {
        return theIdentifierMap.get(pIdentifier);
    }

    /**
     * Add SHA digests.
     */
    private void addSHADigests() {
        addToMaps(GordianDigestSpecBuilder.sha1(), new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1));
        addToMaps(GordianDigestSpecBuilder.sha2(GordianLength.LEN_224), new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha224, DERNull.INSTANCE));
        addToMaps(GordianDigestSpecBuilder.sha2(GordianLength.LEN_256), new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256, DERNull.INSTANCE));
        addToMaps(GordianDigestSpecBuilder.sha2(GordianLength.LEN_384), new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha384, DERNull.INSTANCE));
        addToMaps(GordianDigestSpecBuilder.sha2(GordianLength.LEN_512), new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha512, DERNull.INSTANCE));
        addToMaps(GordianDigestSpecBuilder.sha2(GordianDigestState.STATE512, GordianLength.LEN_224), new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha512_224, DERNull.INSTANCE));
        addToMaps(GordianDigestSpecBuilder.sha2(GordianDigestState.STATE512, GordianLength.LEN_256), new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha512_256, DERNull.INSTANCE));
        addToMaps(GordianDigestSpecBuilder.sha3(GordianLength.LEN_224), new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha3_224, DERNull.INSTANCE));
        addToMaps(GordianDigestSpecBuilder.sha3(GordianLength.LEN_256), new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha3_256, DERNull.INSTANCE));
        addToMaps(GordianDigestSpecBuilder.sha3(GordianLength.LEN_384), new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha3_384, DERNull.INSTANCE));
        addToMaps(GordianDigestSpecBuilder.sha3(GordianLength.LEN_512), new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha3_512, DERNull.INSTANCE));
        addToMaps(GordianDigestSpecBuilder.shake128(), new AlgorithmIdentifier(NISTObjectIdentifiers.id_shake128, DERNull.INSTANCE));
        addToMaps(GordianDigestSpecBuilder.shake256(), new AlgorithmIdentifier(NISTObjectIdentifiers.id_shake256, DERNull.INSTANCE));
    }

    /**
     * Add Blake digests.
     */
    private void addBlakeDigests() {
        addToMaps(GordianDigestSpecBuilder.blake2s(GordianLength.LEN_128), new AlgorithmIdentifier(MiscObjectIdentifiers.id_blake2s128, DERNull.INSTANCE));
        addToMaps(GordianDigestSpecBuilder.blake2b(GordianLength.LEN_160), new AlgorithmIdentifier(MiscObjectIdentifiers.id_blake2b160, DERNull.INSTANCE));
        addToMaps(GordianDigestSpecBuilder.blake2s(GordianLength.LEN_160), new AlgorithmIdentifier(MiscObjectIdentifiers.id_blake2s160, DERNull.INSTANCE));
        addToMaps(GordianDigestSpecBuilder.blake2s(GordianLength.LEN_224), new AlgorithmIdentifier(MiscObjectIdentifiers.id_blake2s224, DERNull.INSTANCE));
        addToMaps(GordianDigestSpecBuilder.blake2b(GordianLength.LEN_256), new AlgorithmIdentifier(MiscObjectIdentifiers.id_blake2b256, DERNull.INSTANCE));
        addToMaps(GordianDigestSpecBuilder.blake2s(GordianLength.LEN_256), new AlgorithmIdentifier(MiscObjectIdentifiers.id_blake2s256, DERNull.INSTANCE));
        addToMaps(GordianDigestSpecBuilder.blake2b(GordianLength.LEN_384), new AlgorithmIdentifier(MiscObjectIdentifiers.id_blake2b384, DERNull.INSTANCE));
        addToMaps(GordianDigestSpecBuilder.blake2b(GordianLength.LEN_512), new AlgorithmIdentifier(MiscObjectIdentifiers.id_blake2b512, DERNull.INSTANCE));
    }

    /**
     * Add GOST digests.
     */
    private void addGOSTDigests() {
        addToMaps(GordianDigestSpecBuilder.gost(), new AlgorithmIdentifier(CryptoProObjectIdentifiers.gostR3411));
        addToMaps(GordianDigestSpecBuilder.streebog(GordianLength.LEN_256), new AlgorithmIdentifier(RosstandartObjectIdentifiers.id_tc26_gost_3411_12_256, DERNull.INSTANCE));
        addToMaps(GordianDigestSpecBuilder.streebog(GordianLength.LEN_512), new AlgorithmIdentifier(RosstandartObjectIdentifiers.id_tc26_gost_3411_12_512, DERNull.INSTANCE));
        addToMaps(GordianDigestSpecBuilder.kupyna(GordianLength.LEN_256), new AlgorithmIdentifier(UAObjectIdentifiers.dstu7564digest_256, DERNull.INSTANCE));
        addToMaps(GordianDigestSpecBuilder.kupyna(GordianLength.LEN_384), new AlgorithmIdentifier(UAObjectIdentifiers.dstu7564digest_384, DERNull.INSTANCE));
        addToMaps(GordianDigestSpecBuilder.kupyna(GordianLength.LEN_512), new AlgorithmIdentifier(UAObjectIdentifiers.dstu7564digest_512, DERNull.INSTANCE));
    }

    /**
     * Add Sundry digests.
     */
    private void addSundryDigests() {
        addToMaps(GordianDigestSpecBuilder.md2(), new AlgorithmIdentifier(PKCSObjectIdentifiers.md2, DERNull.INSTANCE));
        addToMaps(GordianDigestSpecBuilder.md4(), new AlgorithmIdentifier(PKCSObjectIdentifiers.md4, DERNull.INSTANCE));
        addToMaps(GordianDigestSpecBuilder.md5(), new AlgorithmIdentifier(PKCSObjectIdentifiers.md5, DERNull.INSTANCE));
        addToMaps(GordianDigestSpecBuilder.sm3(), new AlgorithmIdentifier(GMObjectIdentifiers.sm3, DERNull.INSTANCE));
        addToMaps(GordianDigestSpecBuilder.whirlpool(), new AlgorithmIdentifier(new ASN1ObjectIdentifier("1.0.10118.3.0.55"), DERNull.INSTANCE));
        addToMaps(GordianDigestSpecBuilder.tiger(), new AlgorithmIdentifier(new ASN1ObjectIdentifier("1.3.6.1.4.1.11591.12.2"), DERNull.INSTANCE));
        addToMaps(GordianDigestSpecBuilder.ripemd(GordianLength.LEN_128), new AlgorithmIdentifier(TeleTrusTObjectIdentifiers.ripemd128, DERNull.INSTANCE));
        addToMaps(GordianDigestSpecBuilder.ripemd(GordianLength.LEN_160), new AlgorithmIdentifier(TeleTrusTObjectIdentifiers.ripemd160, DERNull.INSTANCE));
        addToMaps(GordianDigestSpecBuilder.ripemd(GordianLength.LEN_256), new AlgorithmIdentifier(TeleTrusTObjectIdentifiers.ripemd256, DERNull.INSTANCE));
    }

    /**
     * Add digestSpec to map if supported and not already present.
     *
     * @param pSpec the digestSpec
     */
    private void ensureDigest(final GordianDigestSpec pSpec) {
        /* If the digest is not already known */
        if (!theSpecMap.containsKey(pSpec)) {
            addDigest(pSpec);
        }
    }

    /**
     * Create Identifier for a digestSpec.
     *
     * @param pSpec the digestSpec
     */
    private void addDigest(final GordianDigestSpec pSpec) {
        final ASN1ObjectIdentifier myId = appendDigestOID(DIGESTOID, pSpec);
        addToMaps(pSpec, new AlgorithmIdentifier(myId, DERNull.INSTANCE));
    }

    /**
     * Append Identifier for a digestSpec.
     * @param pBaseOID the base OID
     * @param pSpec the digestSpec
     * @return the resulting OID
     */
    public static ASN1ObjectIdentifier appendDigestOID(final ASN1ObjectIdentifier pBaseOID,
                                                       final GordianDigestSpec pSpec) {
        /* Create a branch for digest based on the DigestType */
        final GordianDigestType myType = pSpec.getDigestType();
        ASN1ObjectIdentifier myId = pBaseOID.branch(Integer.toString(myType.ordinal() + 1));

        /* Adjust for subSpec */
        final GordianDigestState myState = pSpec.getDigestState();
        if (myState != null) {
            myId = myId.branch("1").branch(Integer.toString(myState.ordinal() + 1));
        } else {
            myId = myId.branch("2");
        }

        /* Add Xof indication */
        myId = myId.branch(Boolean.TRUE.equals(pSpec.isXofMode()) ? "1" : "2");

        /* Add length */
        return myId.branch(Integer.toString(pSpec.getDigestLength().ordinal() + 1));
    }

    /**
     * Add spec to maps.
     * @param pSpec the digestSpec
     * @param pIdentifier the identifier
     */
    private void addToMaps(final GordianDigestSpec pSpec,
                           final AlgorithmIdentifier pIdentifier) {
        theSpecMap.put(pSpec, pIdentifier);
        theIdentifierMap.put(pIdentifier, pSpec);
    }
}
