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
package net.sourceforge.joceanus.jgordianknot.impl.core.cipher;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.gm.GMObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.nsri.NSRIObjectIdentifiers;
import org.bouncycastle.asn1.ntt.NTTObjectIdentifiers;
import org.bouncycastle.asn1.ua.UAObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianCipherFactory;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianCipherMode;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianPadding;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianStreamCipherSpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianStreamKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymCipherSpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeyType;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyLengths;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;

/**
 * Mappings from EncodedId to CipherSpec.
 */
public class GordianCipherAlgId {
    /**
     * Base our ciphers off bouncyCastle.
     */
    private static final ASN1ObjectIdentifier CIPHEROID = GordianCoreFactory.BASEOID.branch("11");

    /**
     * Map of SymCipherSpec to Identifier.
     */
    private final Map<GordianSymCipherSpec, AlgorithmIdentifier> theSymSpecMap;

    /**
     * Map of Identifier to SymCipherSpec.
     */
    private final Map<AlgorithmIdentifier, GordianSymCipherSpec> theSymIdentifierMap;

    /**
     * Map of StreamCipherSpec to Identifier.
     */
    private final Map<GordianStreamCipherSpec, AlgorithmIdentifier> theStreamSpecMap;

    /**
     * Map of Identifier to StreamCipherSpec.
     */
    private final Map<AlgorithmIdentifier, GordianStreamCipherSpec> theStreamIdentifierMap;

    /**
     * Constructor.
     *
     * @param pFactory the factory
     */
    GordianCipherAlgId(final GordianCoreFactory pFactory) {
        /* Create the maps */
        theSymSpecMap = new HashMap<>();
        theSymIdentifierMap = new HashMap<>();
        theStreamSpecMap = new HashMap<>();
        theStreamIdentifierMap = new HashMap<>();

        /* Access the cipherFactory  */
        final GordianCipherFactory myFactory = pFactory.getCipherFactory();

        /* Populate with the public standards */
        addWellKnownCiphers128();
        addWellKnownCiphers192();
        addWellKnownCiphers256();

        /* For each KeyLength */
        final Iterator<GordianLength> myIterator = GordianKeyLengths.iterator();
        while (myIterator.hasNext()) {
            final GordianLength myKeyLen = myIterator.next();

            /* Loop through the possible SymKeySpecs */
            for (GordianSymKeySpec mySymKey : myFactory.listAllSupportedSymKeySpecs(myKeyLen)) {
                /* Loop through the possible non-AAD CipherSpecs */
                for (GordianSymCipherSpec mySpec : myFactory.listAllSupportedSymCipherSpecs(mySymKey, false)) {
                    /* Add any non-standard symCiphers */
                    ensureSymCipher(mySpec);
                }

                /* Loop through the possible AAD CipherSpecs */
                for (GordianSymCipherSpec mySpec : myFactory.listAllSupportedSymCipherSpecs(mySymKey, true)) {
                    /* Add any non-standard AAD symCiphers */
                    ensureSymCipher(mySpec);
                }
            }

            /* Loop through the possible StreamKeySpecs */
            for (GordianStreamKeySpec mySpec : myFactory.listAllSupportedStreamKeySpecs(myKeyLen)) {
                /* Add any non-standard streamCiphers */
                ensureStreamCipher(GordianStreamCipherSpec.stream(mySpec));
            }
        }
    }

    /**
     * Obtain Identifier for symCipherSpec.
     *
     * @param pSpec the cipherSpec.
     * @return the Identifier
     */
    public AlgorithmIdentifier getIdentifierForSpec(final GordianSymCipherSpec pSpec) {
        return theSymSpecMap.get(pSpec);
    }

    /**
     * Obtain Identifier for streamCipherSpec.
     *
     * @param pSpec the cipherSpec.
     * @return the Identifier
     */
    public AlgorithmIdentifier getIdentifierForSpec(final GordianStreamCipherSpec pSpec) {
        return theStreamSpecMap.get(pSpec);
    }

    /**
     * Obtain symCipherSpec for Identifier.
     *
     * @param pIdentifier the identifier.
     * @return the cipherSpec (or null if not found)
     */
    GordianSymCipherSpec getSymSpecForIdentifier(final AlgorithmIdentifier pIdentifier) {
        return theSymIdentifierMap.get(pIdentifier);
    }

    /**
     * Obtain symCipherSpec for Identifier.
     *
     * @param pIdentifier the identifier.
     * @return the cipherSpec (or null if not found)
     */
    GordianStreamCipherSpec getStreamSpecForIdentifier(final AlgorithmIdentifier pIdentifier) {
        return theStreamIdentifierMap.get(pIdentifier);
    }

    /**
     * Add WellKnown Ciphers for 128bit keys.
     */
    private void addWellKnownCiphers128() {
        addToSymMaps(GordianSymCipherSpec.ecb(GordianSymKeySpec.sm4(), GordianPadding.NONE), new AlgorithmIdentifier(GMObjectIdentifiers.sms4_ecb));
        addToSymMaps(GordianSymCipherSpec.cbc(GordianSymKeySpec.sm4(), GordianPadding.NONE), new AlgorithmIdentifier(GMObjectIdentifiers.sms4_cbc));
        addToSymMaps(GordianSymCipherSpec.sic(GordianSymKeySpec.sm4()), new AlgorithmIdentifier(GMObjectIdentifiers.sms4_ctr));
        addToSymMaps(GordianSymCipherSpec.ofb(GordianSymKeySpec.sm4()), new AlgorithmIdentifier(GMObjectIdentifiers.sms4_ofb128));
        addToSymMaps(GordianSymCipherSpec.cfb(GordianSymKeySpec.sm4()), new AlgorithmIdentifier(GMObjectIdentifiers.sms4_cfb8));
        addToSymMaps(GordianSymCipherSpec.ccm(GordianSymKeySpec.sm4()), new AlgorithmIdentifier(GMObjectIdentifiers.sms4_ccm));
        addToSymMaps(GordianSymCipherSpec.gcm(GordianSymKeySpec.sm4()), new AlgorithmIdentifier(GMObjectIdentifiers.sms4_gcm));
        addToSymMaps(GordianSymCipherSpec.ocb(GordianSymKeySpec.sm4()), new AlgorithmIdentifier(GMObjectIdentifiers.sms4_ocb));
        addToSymMaps(GordianSymCipherSpec.ecb(GordianSymKeySpec.aes(GordianLength.LEN_128), GordianPadding.NONE),
                new AlgorithmIdentifier(NISTObjectIdentifiers.id_aes128_ECB));
        addToSymMaps(GordianSymCipherSpec.cbc(GordianSymKeySpec.aes(GordianLength.LEN_128), GordianPadding.NONE),
                new AlgorithmIdentifier(NISTObjectIdentifiers.id_aes128_CBC));
        addToSymMaps(GordianSymCipherSpec.ofb(GordianSymKeySpec.aes(GordianLength.LEN_128)),
                new AlgorithmIdentifier(NISTObjectIdentifiers.id_aes128_OFB));
        addToSymMaps(GordianSymCipherSpec.cfb(GordianSymKeySpec.aes(GordianLength.LEN_128)),
                new AlgorithmIdentifier(NISTObjectIdentifiers.id_aes128_CFB));
        addToSymMaps(GordianSymCipherSpec.ccm(GordianSymKeySpec.aes(GordianLength.LEN_128)),
                new AlgorithmIdentifier(NISTObjectIdentifiers.id_aes128_CCM));
        addToSymMaps(GordianSymCipherSpec.gcm(GordianSymKeySpec.aes(GordianLength.LEN_128)),
                new AlgorithmIdentifier(NISTObjectIdentifiers.id_aes128_GCM));
        addToSymMaps(GordianSymCipherSpec.ecb(GordianSymKeySpec.aria(GordianLength.LEN_128), GordianPadding.NONE),
                new AlgorithmIdentifier(NSRIObjectIdentifiers.id_aria128_ecb));
        addToSymMaps(GordianSymCipherSpec.cbc(GordianSymKeySpec.aria(GordianLength.LEN_128), GordianPadding.NONE),
                new AlgorithmIdentifier(NSRIObjectIdentifiers.id_aria128_cbc));
        addToSymMaps(GordianSymCipherSpec.sic(GordianSymKeySpec.aria(GordianLength.LEN_128)),
                new AlgorithmIdentifier(NSRIObjectIdentifiers.id_aria128_ctr));
        addToSymMaps(GordianSymCipherSpec.ofb(GordianSymKeySpec.aria(GordianLength.LEN_128)),
                new AlgorithmIdentifier(NSRIObjectIdentifiers.id_aria128_ofb));
        addToSymMaps(GordianSymCipherSpec.cfb(GordianSymKeySpec.aria(GordianLength.LEN_128)),
                new AlgorithmIdentifier(NSRIObjectIdentifiers.id_aria128_cfb));
        addToSymMaps(GordianSymCipherSpec.ccm(GordianSymKeySpec.aria(GordianLength.LEN_128)),
                new AlgorithmIdentifier(NSRIObjectIdentifiers.id_aria128_ccm));
        addToSymMaps(GordianSymCipherSpec.gcm(GordianSymKeySpec.aria(GordianLength.LEN_128)),
                new AlgorithmIdentifier(NSRIObjectIdentifiers.id_aria128_gcm));
        addToSymMaps(GordianSymCipherSpec.cbc(GordianSymKeySpec.camellia(GordianLength.LEN_128), GordianPadding.NONE),
                new AlgorithmIdentifier(NTTObjectIdentifiers.id_camellia128_cbc));
        addToSymMaps(GordianSymCipherSpec.ecb(GordianSymKeySpec.kalyna(GordianLength.LEN_128), GordianPadding.NONE),
                new AlgorithmIdentifier(UAObjectIdentifiers.dstu7624ecb_128));
        addToSymMaps(GordianSymCipherSpec.cbc(GordianSymKeySpec.kalyna(GordianLength.LEN_128), GordianPadding.NONE),
                new AlgorithmIdentifier(UAObjectIdentifiers.dstu7624cbc_128));
        addToSymMaps(GordianSymCipherSpec.sic(GordianSymKeySpec.kalyna(GordianLength.LEN_128, GordianLength.LEN_128)),
                new AlgorithmIdentifier(UAObjectIdentifiers.dstu7624ctr_128));
        addToSymMaps(GordianSymCipherSpec.ofb(GordianSymKeySpec.kalyna(GordianLength.LEN_128, GordianLength.LEN_128)),
                new AlgorithmIdentifier(UAObjectIdentifiers.dstu7624ofb_128));
        addToSymMaps(GordianSymCipherSpec.cfb(GordianSymKeySpec.kalyna(GordianLength.LEN_128, GordianLength.LEN_128)),
                new AlgorithmIdentifier(UAObjectIdentifiers.dstu7624cfb_128));
        addToSymMaps(GordianSymCipherSpec.ccm(GordianSymKeySpec.kalyna(GordianLength.LEN_128, GordianLength.LEN_128)),
                new AlgorithmIdentifier(UAObjectIdentifiers.dstu7624ccm_128));
        addToSymMaps(GordianSymCipherSpec.gcm(GordianSymKeySpec.kalyna(GordianLength.LEN_128, GordianLength.LEN_128)),
                new AlgorithmIdentifier(UAObjectIdentifiers.dstu7624gmac_128));
    }

    /**
     * Add WellKnown ciphers for 192bit keys.
     */
    private void addWellKnownCiphers192() {
        addToSymMaps(GordianSymCipherSpec.ecb(GordianSymKeySpec.aes(GordianLength.LEN_192), GordianPadding.NONE),
                new AlgorithmIdentifier(NISTObjectIdentifiers.id_aes192_ECB));
        addToSymMaps(GordianSymCipherSpec.cbc(GordianSymKeySpec.aes(GordianLength.LEN_192), GordianPadding.NONE),
                new AlgorithmIdentifier(NISTObjectIdentifiers.id_aes192_CBC));
        addToSymMaps(GordianSymCipherSpec.ofb(GordianSymKeySpec.aes(GordianLength.LEN_192)),
                new AlgorithmIdentifier(NISTObjectIdentifiers.id_aes192_OFB));
        addToSymMaps(GordianSymCipherSpec.cfb(GordianSymKeySpec.aes(GordianLength.LEN_192)),
                new AlgorithmIdentifier(NISTObjectIdentifiers.id_aes192_CFB));
        addToSymMaps(GordianSymCipherSpec.ccm(GordianSymKeySpec.aes(GordianLength.LEN_192)),
                new AlgorithmIdentifier(NISTObjectIdentifiers.id_aes192_CCM));
        addToSymMaps(GordianSymCipherSpec.gcm(GordianSymKeySpec.aes(GordianLength.LEN_192)),
                new AlgorithmIdentifier(NISTObjectIdentifiers.id_aes192_GCM));
        addToSymMaps(GordianSymCipherSpec.ecb(GordianSymKeySpec.aria(GordianLength.LEN_192), GordianPadding.NONE),
                new AlgorithmIdentifier(NSRIObjectIdentifiers.id_aria192_ecb));
        addToSymMaps(GordianSymCipherSpec.cbc(GordianSymKeySpec.aria(GordianLength.LEN_192), GordianPadding.NONE),
                new AlgorithmIdentifier(NSRIObjectIdentifiers.id_aria192_cbc));
        addToSymMaps(GordianSymCipherSpec.sic(GordianSymKeySpec.aria(GordianLength.LEN_192)),
                new AlgorithmIdentifier(NSRIObjectIdentifiers.id_aria192_ctr));
        addToSymMaps(GordianSymCipherSpec.ofb(GordianSymKeySpec.aria(GordianLength.LEN_192)),
                new AlgorithmIdentifier(NSRIObjectIdentifiers.id_aria192_ofb));
        addToSymMaps(GordianSymCipherSpec.cfb(GordianSymKeySpec.aria(GordianLength.LEN_192)),
                new AlgorithmIdentifier(NSRIObjectIdentifiers.id_aria192_cfb));
        addToSymMaps(GordianSymCipherSpec.ccm(GordianSymKeySpec.aria(GordianLength.LEN_192)),
                new AlgorithmIdentifier(NSRIObjectIdentifiers.id_aria192_ccm));
        addToSymMaps(GordianSymCipherSpec.gcm(GordianSymKeySpec.aria(GordianLength.LEN_192)),
                new AlgorithmIdentifier(NSRIObjectIdentifiers.id_aria192_gcm));
        addToSymMaps(GordianSymCipherSpec.cbc(GordianSymKeySpec.camellia(GordianLength.LEN_192), GordianPadding.NONE),
                new AlgorithmIdentifier(NTTObjectIdentifiers.id_camellia192_cbc));
    }


    /**
     * Add WellKnown Ciphers for 256bit keys.
     */
    private void addWellKnownCiphers256() {
        addToSymMaps(GordianSymCipherSpec.ecb(GordianSymKeySpec.aes(GordianLength.LEN_256), GordianPadding.NONE),
                new AlgorithmIdentifier(NISTObjectIdentifiers.id_aes256_ECB));
        addToSymMaps(GordianSymCipherSpec.cbc(GordianSymKeySpec.aes(GordianLength.LEN_256), GordianPadding.NONE),
                new AlgorithmIdentifier(NISTObjectIdentifiers.id_aes256_CBC));
        addToSymMaps(GordianSymCipherSpec.ofb(GordianSymKeySpec.aes(GordianLength.LEN_256)),
                new AlgorithmIdentifier(NISTObjectIdentifiers.id_aes256_OFB));
        addToSymMaps(GordianSymCipherSpec.cfb(GordianSymKeySpec.aes(GordianLength.LEN_256)),
                new AlgorithmIdentifier(NISTObjectIdentifiers.id_aes256_CFB));
        addToSymMaps(GordianSymCipherSpec.ccm(GordianSymKeySpec.aes(GordianLength.LEN_256)),
                new AlgorithmIdentifier(NISTObjectIdentifiers.id_aes256_CCM));
        addToSymMaps(GordianSymCipherSpec.gcm(GordianSymKeySpec.aes(GordianLength.LEN_256)),
                new AlgorithmIdentifier(NISTObjectIdentifiers.id_aes256_GCM));
        addToSymMaps(GordianSymCipherSpec.ecb(GordianSymKeySpec.aria(GordianLength.LEN_256), GordianPadding.NONE),
                new AlgorithmIdentifier(NSRIObjectIdentifiers.id_aria256_ecb));
        addToSymMaps(GordianSymCipherSpec.cbc(GordianSymKeySpec.aria(GordianLength.LEN_256), GordianPadding.NONE),
                new AlgorithmIdentifier(NSRIObjectIdentifiers.id_aria256_cbc));
        addToSymMaps(GordianSymCipherSpec.sic(GordianSymKeySpec.aria(GordianLength.LEN_256)),
                new AlgorithmIdentifier(NSRIObjectIdentifiers.id_aria256_ctr));
        addToSymMaps(GordianSymCipherSpec.ofb(GordianSymKeySpec.aria(GordianLength.LEN_256)),
                new AlgorithmIdentifier(NSRIObjectIdentifiers.id_aria256_ofb));
        addToSymMaps(GordianSymCipherSpec.cfb(GordianSymKeySpec.aria(GordianLength.LEN_256)),
                new AlgorithmIdentifier(NSRIObjectIdentifiers.id_aria256_cfb));
        addToSymMaps(GordianSymCipherSpec.ccm(GordianSymKeySpec.aria(GordianLength.LEN_256)),
                new AlgorithmIdentifier(NSRIObjectIdentifiers.id_aria256_ccm));
        addToSymMaps(GordianSymCipherSpec.gcm(GordianSymKeySpec.aria(GordianLength.LEN_256)),
                new AlgorithmIdentifier(NSRIObjectIdentifiers.id_aria256_gcm));
        addToSymMaps(GordianSymCipherSpec.cbc(GordianSymKeySpec.camellia(GordianLength.LEN_256), GordianPadding.NONE),
                new AlgorithmIdentifier(NTTObjectIdentifiers.id_camellia256_cbc));
        addToSymMaps(GordianSymCipherSpec.ecb(GordianSymKeySpec.kalyna(GordianLength.LEN_256, GordianLength.LEN_256), GordianPadding.NONE),
                new AlgorithmIdentifier(UAObjectIdentifiers.dstu7624ecb_256));
        addToSymMaps(GordianSymCipherSpec.cbc(GordianSymKeySpec.kalyna(GordianLength.LEN_256, GordianLength.LEN_256), GordianPadding.NONE),
                new AlgorithmIdentifier(UAObjectIdentifiers.dstu7624cbc_256));
        addToSymMaps(GordianSymCipherSpec.sic(GordianSymKeySpec.kalyna(GordianLength.LEN_256, GordianLength.LEN_256)),
                new AlgorithmIdentifier(UAObjectIdentifiers.dstu7624ctr_256));
        addToSymMaps(GordianSymCipherSpec.ofb(GordianSymKeySpec.kalyna(GordianLength.LEN_256, GordianLength.LEN_256)),
                new AlgorithmIdentifier(UAObjectIdentifiers.dstu7624ofb_256));
        addToSymMaps(GordianSymCipherSpec.cfb(GordianSymKeySpec.kalyna(GordianLength.LEN_256, GordianLength.LEN_256)),
                new AlgorithmIdentifier(UAObjectIdentifiers.dstu7624cfb_256));
        addToSymMaps(GordianSymCipherSpec.ccm(GordianSymKeySpec.kalyna(GordianLength.LEN_256, GordianLength.LEN_256)),
                new AlgorithmIdentifier(UAObjectIdentifiers.dstu7624ccm_256));
        addToSymMaps(GordianSymCipherSpec.gcm(GordianSymKeySpec.kalyna(GordianLength.LEN_256, GordianLength.LEN_256)),
                new AlgorithmIdentifier(UAObjectIdentifiers.dstu7624gmac_256));
    }

    /**
     * Add symCipherSpec to map if supported and not already present.
     *
     * @param pSpec the symCipherSpec
     */
    private void ensureSymCipher(final GordianSymCipherSpec pSpec) {
        /* If the cipher is not already known */
        if (!theSymSpecMap.containsKey(pSpec)) {
            addSymCipher(pSpec);
        }
    }

    /**
     * Create Identifier for a symCipherSpec.
     *
     * @param pSpec the cipherSpec
     */
    private void addSymCipher(final GordianSymCipherSpec pSpec) {
        /* Build SymKey id */
        ASN1ObjectIdentifier myId = appendSymKeyOID(CIPHEROID.branch("1"), true, pSpec.getKeyType());

        /* Add mode */
        final GordianCipherMode myMode = pSpec.getCipherMode();
        myId = myId.branch(Integer.toString(myMode.ordinal() + 1));

        /* Add padding if necessary */
        if (myMode.hasPadding()) {
            myId = myId.branch(Integer.toString(pSpec.getPadding().ordinal() + 1));
        }

        /* Add the spec to the maps */
        addToSymMaps(pSpec, new AlgorithmIdentifier(myId));
    }

    /**
     * Append Identifier for a symCipherSpec.
     * @param pBaseOID the base OID
     * @param pAddLength add length to id true/false
     * @param pSpec the cipherSpec
     * @return the resulting OID
     */
    public static ASN1ObjectIdentifier appendSymKeyOID(final ASN1ObjectIdentifier pBaseOID,
                                                       final boolean pAddLength,
                                                       final GordianSymKeySpec pSpec) {
        /* Create a branch for cipher based on the KeyType */
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
     * Add streamCipherSpec to map if supported and not already present.
     *
     * @param pSpec the streamCipherSpec
     */
    private void ensureStreamCipher(final GordianStreamCipherSpec pSpec) {
        /* If the cipher is not already known */
        if (!theStreamSpecMap.containsKey(pSpec)) {
            addStreamCipher(pSpec);
        }
    }

    /**
     * Create Identifier for a cipherSpec.
     *
     * @param pSpec the cipherSpec
     */
    private void addStreamCipher(final GordianStreamCipherSpec pSpec) {
        /* Create a branch for cipher based on the KeyType */
        final GordianStreamKeySpec mySpec = pSpec.getKeyType();
        ASN1ObjectIdentifier myId = CIPHEROID.branch("2");
        myId = myId.branch(Integer.toString(mySpec.getKeyLength().ordinal() + 1));
        myId = myId.branch(Integer.toString(mySpec.getStreamKeyType().ordinal() + 1));

        /* Add the spec to the maps */
        addToStreamMaps(pSpec, new AlgorithmIdentifier(myId));
    }

    /**
     * Add spec to maps.
     * @param pSpec the cipherSpec
     * @param pIdentifier the identifier
     */
    private void addToSymMaps(final GordianSymCipherSpec pSpec,
                              final AlgorithmIdentifier pIdentifier) {
        theSymSpecMap.put(pSpec, pIdentifier);
        theSymIdentifierMap.put(pIdentifier, pSpec);
    }

    /**
     * Add spec to maps.
     * @param pSpec the cipherSpec
     * @param pIdentifier the identifier
     */
    private void addToStreamMaps(final GordianStreamCipherSpec pSpec,
                                 final AlgorithmIdentifier pIdentifier) {
        theStreamSpecMap.put(pSpec, pIdentifier);
        theStreamIdentifierMap.put(pIdentifier, pSpec);
    }
}
