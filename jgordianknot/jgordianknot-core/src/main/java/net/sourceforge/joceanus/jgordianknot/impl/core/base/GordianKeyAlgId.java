/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2022 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.impl.core.base;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.nsri.NSRIObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.bouncycastle.asn1.ua.UAObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianCipherFactory;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianStreamKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianStreamKeyType;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeyType;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestSpec;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyLengths;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacFactory;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacSpec;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacType;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianSipHashSpec;

/**
 * Mappings from EncodedId to KeySpec.
 */
public class GordianKeyAlgId {
    /**
     * CipherOID branch.
     */
    private static final ASN1ObjectIdentifier KEYOID = GordianASN1Util.SYMOID.branch("2");

    /**
     * SymKeysOID branch.
     */
    private static final ASN1ObjectIdentifier SYMKEYOID = KEYOID.branch("1");

    /**
     * StreamKeysOID branch.
     */
    private static final ASN1ObjectIdentifier STREAMKEYOID = KEYOID.branch("2");

    /**
     * MacOID branch.
     */
    private static final ASN1ObjectIdentifier MACOID = GordianASN1Util.SYMOID.branch("3");

    /**
     * Map of KeySpec to Identifier.
     */
    private final Map<GordianKeySpec, AlgorithmIdentifier> theSpecMap;

    /**
     * Map of Identifier to KeySpec.
     */
    private final Map<AlgorithmIdentifier, GordianKeySpec> theIdentifierMap;

    /**
     * Constructor.
     *
     * @param pFactory the factory
     */
    GordianKeyAlgId(final GordianCoreFactory pFactory) {
        /* Create the maps */
        theSpecMap = new HashMap<>();
        theIdentifierMap = new HashMap<>();

        /* Access the factories  */
        final GordianCipherFactory myCipherFactory = pFactory.getCipherFactory();
        final GordianMacFactory myMacFactory = pFactory.getMacFactory();

        /* Populate with the public standards */
        addWellKnownMacs();

        /* For each KeyLength */
        final Iterator<GordianLength> myIterator = GordianKeyLengths.iterator();
        while (myIterator.hasNext()) {
            final GordianLength myKeyLen = myIterator.next();

            /* Loop through the possible SymKeySpecs */
            for (GordianSymKeySpec mySpec : myCipherFactory.listAllSupportedSymKeySpecs(myKeyLen)) {
                /* Add any non-standard symKeys */
                ensureSymKey(mySpec);
            }

            /* Loop through the possible StreamKeySpecs */
            for (GordianStreamKeySpec mySpec : myCipherFactory.listAllSupportedStreamKeySpecs(myKeyLen)) {
                /* Add any non-standard streamKeys */
                ensureStreamKey(mySpec);
            }

            /* Loop through the possible MacSpecs */
            for (GordianMacSpec mySpec : myMacFactory.listAllSupportedSpecs(myKeyLen)) {
                /* Add any non-standard macs */
                ensureMac(mySpec);
            }
        }
    }

    /**
     * Obtain Identifier for KeySpec.
     *
     * @param pSpec the keySpec.
     * @return the Identifier
     */
    public AlgorithmIdentifier getIdentifierForSpec(final GordianKeySpec pSpec) {
        return theSpecMap.get(pSpec);
    }

    /**
     * Obtain symKeySpec for Identifier.
     *
     * @param pIdentifier the identifier.
     * @return the keySpec (or null if not found)
     */
    public GordianKeySpec getKeySpecForIdentifier(final AlgorithmIdentifier pIdentifier) {
        return theIdentifierMap.get(pIdentifier);
    }

    /**
     * Add symKeySpec to map if supported and not already present.
     *
     * @param pSpec the symKeySpec
     */
    private void ensureSymKey(final GordianSymKeySpec pSpec) {
        /* If the key is not already known */
        if (!theSpecMap.containsKey(pSpec)) {
            addSymKey(pSpec);
        }
    }

    /**
     * Create Identifier for a symKeySpec.
     *
     * @param pSpec the keySpec
     */
    private void addSymKey(final GordianSymKeySpec pSpec) {
        /* Build SymKey id */
        final ASN1ObjectIdentifier myId = appendSymKeyOID(SYMKEYOID, true, pSpec);

        /* Add the spec to the maps */
        addToMaps(pSpec, new AlgorithmIdentifier(myId, DERNull.INSTANCE));
    }

    /**
     * Append Identifier for a symKeySpec.
     * @param pBaseOID the base OID
     * @param pAddLength add length to id true/false
     * @param pSpec the keySpec
     * @return the resulting OID
     */
    public static ASN1ObjectIdentifier appendSymKeyOID(final ASN1ObjectIdentifier pBaseOID,
                                                       final boolean pAddLength,
                                                       final GordianSymKeySpec pSpec) {
        /* Create a branch based on the KeyType */
        final GordianSymKeyType myType = pSpec.getSymKeyType();
        ASN1ObjectIdentifier myId = pBaseOID.branch(Integer.toString(myType.ordinal() + 1));

        /* Add blockLength */
        final GordianLength myBlockLength = pSpec.getBlockLength();
        myId = myId.branch(Integer.toString(myBlockLength.ordinal() + 1));

        /* Add length if required */
        if (pAddLength) {
            final GordianLength myKeyLength = pSpec.getKeyLength();
            myId = myId.branch(Integer.toString(myKeyLength.ordinal() + 1));
        }

        /* Return the id */
        return myId;
    }

    /**
     * Add keySpec to maps.
     * @param pSpec the keySpec
     * @param pIdentifier the identifier
     */
    private void addToMaps(final GordianKeySpec pSpec,
                           final AlgorithmIdentifier pIdentifier) {
        theSpecMap.put(pSpec, pIdentifier);
        theIdentifierMap.put(pIdentifier, pSpec);
    }

    /**
     * Add streamKeySpec to map if supported and not already present.
     *
     * @param pSpec the streamKeySpec
     */
    private void ensureStreamKey(final GordianStreamKeySpec pSpec) {
        /* If the key is not already known */
        if (!theSpecMap.containsKey(pSpec)) {
            addStreamKey(pSpec);
        }
    }

    /**
     * Create Identifier for a streamKeySpec.
     *
     * @param pSpec the keySpec
     */
    private void addStreamKey(final GordianStreamKeySpec pSpec) {
        /* Build StreamKey id */
        final ASN1ObjectIdentifier myId = appendStreamKeyOID(STREAMKEYOID, pSpec);

        /* Add the spec to the maps */
        addToMaps(pSpec, new AlgorithmIdentifier(myId, DERNull.INSTANCE));
    }

    /**
     * Append Identifier for a streamKeySpec.
     * @param pBaseOID the base OID
     * @param pSpec the keySpec
     * @return the resulting OID
     */
    public static ASN1ObjectIdentifier appendStreamKeyOID(final ASN1ObjectIdentifier pBaseOID,
                                                          final GordianStreamKeySpec pSpec) {
        /* Create a branch based on the KeyType */
        final GordianStreamKeyType myType = pSpec.getStreamKeyType();
        ASN1ObjectIdentifier myId = pBaseOID.branch(Integer.toString(myType.ordinal() + 1));

        /* Add keyLength */
        final GordianLength myKeyLength = pSpec.getKeyLength();
        myId = myId.branch(Integer.toString(myKeyLength.ordinal() + 1));

        /* Add subKeyType if required */
        if (pSpec.getSubKeyType() != null) {
            myId = myId.branch(Integer.toString(((Enum<?>) pSpec.getSubKeyType()).ordinal() + 1));
        }

        /* Return the id */
        return myId;
    }

    /**
     * Add well-known macs.
     */
    private void addWellKnownMacs() {
        addToMaps(GordianMacSpec.hMac(GordianDigestSpec.gost()), new AlgorithmIdentifier(CryptoProObjectIdentifiers.gostR3411Hmac, DERNull.INSTANCE));
        addToMaps(GordianMacSpec.hMac(GordianDigestSpec.streebog(GordianLength.LEN_256)),
                new AlgorithmIdentifier(RosstandartObjectIdentifiers.id_tc26_hmac_gost_3411_12_256, DERNull.INSTANCE));
        addToMaps(GordianMacSpec.hMac(GordianDigestSpec.streebog(GordianLength.LEN_512)),
                new AlgorithmIdentifier(RosstandartObjectIdentifiers.id_tc26_hmac_gost_3411_12_512, DERNull.INSTANCE));
        addToMaps(GordianMacSpec.hMac(GordianDigestSpec.sha1()), new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA1, DERNull.INSTANCE));
        addToMaps(GordianMacSpec.hMac(GordianDigestSpec.sha2(GordianLength.LEN_224)), new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA224, DERNull.INSTANCE));
        addToMaps(GordianMacSpec.hMac(GordianDigestSpec.sha2(GordianLength.LEN_256)), new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA256, DERNull.INSTANCE));
        addToMaps(GordianMacSpec.hMac(GordianDigestSpec.sha2(GordianLength.LEN_384)), new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA384, DERNull.INSTANCE));
        addToMaps(GordianMacSpec.hMac(GordianDigestSpec.sha2(GordianLength.LEN_512)), new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA512, DERNull.INSTANCE));
        addToMaps(GordianMacSpec.hMac(GordianDigestSpec.sha3(GordianLength.LEN_224)), new AlgorithmIdentifier(NISTObjectIdentifiers.id_hmacWithSHA3_224, DERNull.INSTANCE));
        addToMaps(GordianMacSpec.hMac(GordianDigestSpec.sha3(GordianLength.LEN_256)), new AlgorithmIdentifier(NISTObjectIdentifiers.id_hmacWithSHA3_256, DERNull.INSTANCE));
        addToMaps(GordianMacSpec.hMac(GordianDigestSpec.sha3(GordianLength.LEN_384)), new AlgorithmIdentifier(NISTObjectIdentifiers.id_hmacWithSHA3_384, DERNull.INSTANCE));
        addToMaps(GordianMacSpec.hMac(GordianDigestSpec.sha3(GordianLength.LEN_512)), new AlgorithmIdentifier(NISTObjectIdentifiers.id_hmacWithSHA3_512, DERNull.INSTANCE));
        addToMaps(GordianMacSpec.cMac(GordianSymKeySpec.aria(GordianLength.LEN_128)), new AlgorithmIdentifier(NSRIObjectIdentifiers.id_aria128_cmac, DERNull.INSTANCE));
        addToMaps(GordianMacSpec.cMac(GordianSymKeySpec.aria(GordianLength.LEN_192)), new AlgorithmIdentifier(NSRIObjectIdentifiers.id_aria192_cmac, DERNull.INSTANCE));
        addToMaps(GordianMacSpec.cMac(GordianSymKeySpec.aria(GordianLength.LEN_256)), new AlgorithmIdentifier(NSRIObjectIdentifiers.id_aria256_cmac, DERNull.INSTANCE));
        addToMaps(GordianMacSpec.kupynaMac(GordianLength.LEN_256, GordianLength.LEN_256), new AlgorithmIdentifier(UAObjectIdentifiers.dstu7564mac_256, DERNull.INSTANCE));
        addToMaps(GordianMacSpec.kupynaMac(GordianLength.LEN_256, GordianLength.LEN_384), new AlgorithmIdentifier(UAObjectIdentifiers.dstu7564mac_384, DERNull.INSTANCE));
        addToMaps(GordianMacSpec.kupynaMac(GordianLength.LEN_256, GordianLength.LEN_512), new AlgorithmIdentifier(UAObjectIdentifiers.dstu7564mac_512, DERNull.INSTANCE));
    }

    /**
     * Add macSpec to map if supported and not already present.
     *
     * @param pSpec the macSpec
     */
    private void ensureMac(final GordianMacSpec pSpec) {
        /* If the mac is not already known */
        if (!theSpecMap.containsKey(pSpec)) {
            addMac(pSpec);
        }
    }

    /**
     * Create Identifier for a macSpec.
     *
     * @param pSpec the macSpec
     */
    private void addMac(final GordianMacSpec pSpec) {
        /* Create a branch for mac based on the MacType */
        final GordianMacType myType = pSpec.getMacType();
        ASN1ObjectIdentifier myId = MACOID.branch(Integer.toString(myType.ordinal() + 1));
        myId = myId.branch(Integer.toString(pSpec.getKeyLength().ordinal() + 1));

        /* Obtain the subSpec */
        final Object mySubSpec = pSpec.getSubSpec();
        if (mySubSpec instanceof GordianDigestSpec) {
            myId = GordianDigestAlgId.appendDigestOID(myId, (GordianDigestSpec) mySubSpec);
        } else if (mySubSpec instanceof GordianSymKeySpec) {
            myId = GordianKeyAlgId.appendSymKeyOID(myId, false, (GordianSymKeySpec) mySubSpec);
        } else if (mySubSpec instanceof GordianLength) {
            myId = myId.branch(Integer.toString(((GordianLength) mySubSpec).ordinal() + 1));
        } else if (mySubSpec instanceof GordianSipHashSpec) {
            myId = myId.branch(Integer.toString(((GordianSipHashSpec) mySubSpec).ordinal() + 1));
        }

        /* Add the spec to the maps */
        addToMaps(pSpec, new AlgorithmIdentifier(myId, DERNull.INSTANCE));
    }
}
