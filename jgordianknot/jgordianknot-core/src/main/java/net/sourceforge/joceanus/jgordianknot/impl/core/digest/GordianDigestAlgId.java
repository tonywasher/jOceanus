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
package net.sourceforge.joceanus.jgordianknot.impl.core.digest;

import java.util.HashMap;
import java.util.Map;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
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

import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestFactory;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestSpec;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestType;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Mappings from EncodedId to DigestSpec.
 */
public class GordianDigestAlgId {
    /**
     * Base our digests off bouncyCastle.
     */
    private static final ASN1ObjectIdentifier DIGESTOID = GordianCoreFactory.BASEOID.branch("1");

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
        addToMaps(GordianDigestSpec.sha1(), new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1));
        addToMaps(GordianDigestSpec.sha2(GordianLength.LEN_224), new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha224));
        addToMaps(GordianDigestSpec.sha2(GordianLength.LEN_256), new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256));
        addToMaps(GordianDigestSpec.sha2(GordianLength.LEN_384), new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha384));
        addToMaps(GordianDigestSpec.sha2(GordianLength.LEN_512), new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha512));
        addToMaps(GordianDigestSpec.sha2Alt(GordianLength.LEN_224), new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha512_224));
        addToMaps(GordianDigestSpec.sha2Alt(GordianLength.LEN_256), new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha512_256));
        addToMaps(GordianDigestSpec.sha3(GordianLength.LEN_224), new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha3_224));
        addToMaps(GordianDigestSpec.sha3(GordianLength.LEN_256), new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha3_256));
        addToMaps(GordianDigestSpec.sha3(GordianLength.LEN_384), new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha3_384));
        addToMaps(GordianDigestSpec.sha3(GordianLength.LEN_512), new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha3_512));
        addToMaps(GordianDigestSpec.shake(GordianLength.LEN_128), new AlgorithmIdentifier(NISTObjectIdentifiers.id_shake128));
        addToMaps(GordianDigestSpec.shake(GordianLength.LEN_256), new AlgorithmIdentifier(NISTObjectIdentifiers.id_shake256));
    }

    /**
     * Add Blake digests.
     */
    private void addBlakeDigests() {
        addToMaps(GordianDigestSpec.blake(GordianLength.LEN_128), new AlgorithmIdentifier(MiscObjectIdentifiers.id_blake2s128));
        addToMaps(GordianDigestSpec.blake(GordianLength.LEN_160), new AlgorithmIdentifier(MiscObjectIdentifiers.id_blake2b160));
        addToMaps(GordianDigestSpec.blakeAlt(GordianLength.LEN_160), new AlgorithmIdentifier(MiscObjectIdentifiers.id_blake2s160));
        addToMaps(GordianDigestSpec.blake(GordianLength.LEN_224), new AlgorithmIdentifier(MiscObjectIdentifiers.id_blake2s224));
        addToMaps(GordianDigestSpec.blake(GordianLength.LEN_256), new AlgorithmIdentifier(MiscObjectIdentifiers.id_blake2b256));
        addToMaps(GordianDigestSpec.blakeAlt(GordianLength.LEN_256), new AlgorithmIdentifier(MiscObjectIdentifiers.id_blake2s256));
        addToMaps(GordianDigestSpec.blake(GordianLength.LEN_384), new AlgorithmIdentifier(MiscObjectIdentifiers.id_blake2b384));
        addToMaps(GordianDigestSpec.blake(GordianLength.LEN_512), new AlgorithmIdentifier(MiscObjectIdentifiers.id_blake2b512));
    }

    /**
     * Add GOST digests.
     */
    private void addGOSTDigests() {
        addToMaps(GordianDigestSpec.gost(), new AlgorithmIdentifier(CryptoProObjectIdentifiers.gostR3411));
        addToMaps(GordianDigestSpec.streebog(GordianLength.LEN_256), new AlgorithmIdentifier(RosstandartObjectIdentifiers.id_tc26_gost_3411_12_256));
        addToMaps(GordianDigestSpec.streebog(GordianLength.LEN_512), new AlgorithmIdentifier(RosstandartObjectIdentifiers.id_tc26_gost_3411_12_512));
        addToMaps(GordianDigestSpec.kupyna(GordianLength.LEN_256), new AlgorithmIdentifier(UAObjectIdentifiers.dstu7564digest_256));
        addToMaps(GordianDigestSpec.kupyna(GordianLength.LEN_384), new AlgorithmIdentifier(UAObjectIdentifiers.dstu7564digest_384));
        addToMaps(GordianDigestSpec.kupyna(GordianLength.LEN_512), new AlgorithmIdentifier(UAObjectIdentifiers.dstu7564digest_512));
    }

    /**
     * Add Sundry digests.
     */
    private void addSundryDigests() {
        addToMaps(GordianDigestSpec.md2(), new AlgorithmIdentifier(PKCSObjectIdentifiers.md2));
        addToMaps(GordianDigestSpec.md4(), new AlgorithmIdentifier(PKCSObjectIdentifiers.md4));
        addToMaps(GordianDigestSpec.md5(), new AlgorithmIdentifier(PKCSObjectIdentifiers.md5));
        addToMaps(GordianDigestSpec.sm3(), new AlgorithmIdentifier(GMObjectIdentifiers.sm3));
        addToMaps(GordianDigestSpec.whirlpool(), new AlgorithmIdentifier(new ASN1ObjectIdentifier("1.0.10118.3.0.55")));
        addToMaps(GordianDigestSpec.tiger(), new AlgorithmIdentifier(new ASN1ObjectIdentifier("1.3.6.1.4.1.11591.12.2")));
        addToMaps(GordianDigestSpec.ripemd(GordianLength.LEN_128), new AlgorithmIdentifier(TeleTrusTObjectIdentifiers.ripemd128));
        addToMaps(GordianDigestSpec.ripemd(GordianLength.LEN_160), new AlgorithmIdentifier(TeleTrusTObjectIdentifiers.ripemd160));
        addToMaps(GordianDigestSpec.ripemd(GordianLength.LEN_256), new AlgorithmIdentifier(TeleTrusTObjectIdentifiers.ripemd256));
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
        addToMaps(pSpec, new AlgorithmIdentifier(myId));
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

        /* Determine stateLength */
        final GordianLength myState = pSpec.getStateLength();
        myId = myState == null
               ? myId.branch("1")
               : myId.branch(Integer.toString(myState.ordinal() + 2));

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
