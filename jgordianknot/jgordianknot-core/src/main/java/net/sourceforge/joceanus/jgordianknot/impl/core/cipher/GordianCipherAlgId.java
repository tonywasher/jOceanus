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
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.gm.GMObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.nsri.NSRIObjectIdentifiers;
import org.bouncycastle.asn1.ntt.NTTObjectIdentifiers;
import org.bouncycastle.asn1.ua.UAObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianCipherFactory;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianCipherMode;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianCipherSpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianPadding;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianStreamCipherSpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianStreamKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymCipherSpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyLengths;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianASN1Util;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianKeyAlgId;

/**
 * Mappings from EncodedId to CipherSpec.
 */
public class GordianCipherAlgId {
    /**
     * CipherOID branch.
     */
    private static final ASN1ObjectIdentifier CIPHEROID = GordianASN1Util.SYMOID.branch("4");

    /**
     * Map of cipherSpec to Identifier.
     */
    private final Map<GordianCipherSpec<?>, AlgorithmIdentifier> theSpecMap;

    /**
     * Map of Identifier to cipherSpec.
     */
    private final Map<AlgorithmIdentifier, GordianCipherSpec<?>> theIdentifierMap;

    /**
     * Constructor.
     *
     * @param pFactory the factory
     */
    GordianCipherAlgId(final GordianCoreFactory pFactory) {
        /* Create the maps */
        theSpecMap = new HashMap<>();
        theIdentifierMap = new HashMap<>();

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
                /* Loop through the possible CipherSpecs */
                for (GordianSymCipherSpec mySpec : myFactory.listAllSupportedSymCipherSpecs(mySymKey)) {
                    /* Add any non-standard symCiphers */
                    ensureSymCipher(mySpec);
                }
            }

            /* Loop through the possible StreamCipherSpecs */
            for (GordianStreamCipherSpec mySpec : myFactory.listAllSupportedStreamCipherSpecs(myKeyLen)) {
                /* Add any non-standard streamCiphers */
                ensureStreamCipher(mySpec);
            }
        }
    }

    /**
     * Obtain Identifier for cipherSpec.
     *
     * @param pSpec the cipherSpec.
     * @return the Identifier
     */
    public AlgorithmIdentifier getIdentifierForSpec(final GordianCipherSpec<?> pSpec) {
        return theSpecMap.get(pSpec);
    }

    /**
     * Obtain symCipherSpec for Identifier.
     *
     * @param pIdentifier the identifier.
     * @return the cipherSpec (or null if not found)
     */
    GordianCipherSpec<?> getCipherSpecForIdentifier(final AlgorithmIdentifier pIdentifier) {
        return theIdentifierMap.get(pIdentifier);
    }

    /**
     * Add WellKnown Ciphers for 128bit keys.
     */
    private void addWellKnownCiphers128() {
        addToMaps(GordianSymCipherSpec.ecb(GordianSymKeySpec.sm4(), GordianPadding.NONE), new AlgorithmIdentifier(GMObjectIdentifiers.sms4_ecb, DERNull.INSTANCE));
        addToMaps(GordianSymCipherSpec.cbc(GordianSymKeySpec.sm4(), GordianPadding.NONE), new AlgorithmIdentifier(GMObjectIdentifiers.sms4_cbc, DERNull.INSTANCE));
        addToMaps(GordianSymCipherSpec.sic(GordianSymKeySpec.sm4()), new AlgorithmIdentifier(GMObjectIdentifiers.sms4_ctr, DERNull.INSTANCE));
        addToMaps(GordianSymCipherSpec.ofb(GordianSymKeySpec.sm4()), new AlgorithmIdentifier(GMObjectIdentifiers.sms4_ofb128, DERNull.INSTANCE));
        addToMaps(GordianSymCipherSpec.cfb(GordianSymKeySpec.sm4()), new AlgorithmIdentifier(GMObjectIdentifiers.sms4_cfb8, DERNull.INSTANCE));
        addToMaps(GordianSymCipherSpec.ccm(GordianSymKeySpec.sm4()), new AlgorithmIdentifier(GMObjectIdentifiers.sms4_ccm, DERNull.INSTANCE));
        addToMaps(GordianSymCipherSpec.gcm(GordianSymKeySpec.sm4()), new AlgorithmIdentifier(GMObjectIdentifiers.sms4_gcm, DERNull.INSTANCE));
        addToMaps(GordianSymCipherSpec.ocb(GordianSymKeySpec.sm4()), new AlgorithmIdentifier(GMObjectIdentifiers.sms4_ocb, DERNull.INSTANCE));
        addToMaps(GordianSymCipherSpec.ecb(GordianSymKeySpec.aes(GordianLength.LEN_128), GordianPadding.NONE),
                new AlgorithmIdentifier(NISTObjectIdentifiers.id_aes128_ECB, DERNull.INSTANCE));
        addToMaps(GordianSymCipherSpec.cbc(GordianSymKeySpec.aes(GordianLength.LEN_128), GordianPadding.NONE),
                new AlgorithmIdentifier(NISTObjectIdentifiers.id_aes128_CBC, DERNull.INSTANCE));
        addToMaps(GordianSymCipherSpec.ofb(GordianSymKeySpec.aes(GordianLength.LEN_128)),
                new AlgorithmIdentifier(NISTObjectIdentifiers.id_aes128_OFB, DERNull.INSTANCE));
        addToMaps(GordianSymCipherSpec.cfb(GordianSymKeySpec.aes(GordianLength.LEN_128)),
                new AlgorithmIdentifier(NISTObjectIdentifiers.id_aes128_CFB, DERNull.INSTANCE));
        addToMaps(GordianSymCipherSpec.ccm(GordianSymKeySpec.aes(GordianLength.LEN_128)),
                new AlgorithmIdentifier(NISTObjectIdentifiers.id_aes128_CCM, DERNull.INSTANCE));
        addToMaps(GordianSymCipherSpec.gcm(GordianSymKeySpec.aes(GordianLength.LEN_128)),
                new AlgorithmIdentifier(NISTObjectIdentifiers.id_aes128_GCM, DERNull.INSTANCE));
        addToMaps(GordianSymCipherSpec.ecb(GordianSymKeySpec.aria(GordianLength.LEN_128), GordianPadding.NONE),
                new AlgorithmIdentifier(NSRIObjectIdentifiers.id_aria128_ecb, DERNull.INSTANCE));
        addToMaps(GordianSymCipherSpec.cbc(GordianSymKeySpec.aria(GordianLength.LEN_128), GordianPadding.NONE),
                new AlgorithmIdentifier(NSRIObjectIdentifiers.id_aria128_cbc, DERNull.INSTANCE));
        addToMaps(GordianSymCipherSpec.sic(GordianSymKeySpec.aria(GordianLength.LEN_128)),
                new AlgorithmIdentifier(NSRIObjectIdentifiers.id_aria128_ctr, DERNull.INSTANCE));
        addToMaps(GordianSymCipherSpec.ofb(GordianSymKeySpec.aria(GordianLength.LEN_128)),
                new AlgorithmIdentifier(NSRIObjectIdentifiers.id_aria128_ofb, DERNull.INSTANCE));
        addToMaps(GordianSymCipherSpec.cfb(GordianSymKeySpec.aria(GordianLength.LEN_128)),
                new AlgorithmIdentifier(NSRIObjectIdentifiers.id_aria128_cfb, DERNull.INSTANCE));
        addToMaps(GordianSymCipherSpec.ccm(GordianSymKeySpec.aria(GordianLength.LEN_128)),
                new AlgorithmIdentifier(NSRIObjectIdentifiers.id_aria128_ccm, DERNull.INSTANCE));
        addToMaps(GordianSymCipherSpec.gcm(GordianSymKeySpec.aria(GordianLength.LEN_128)),
                new AlgorithmIdentifier(NSRIObjectIdentifiers.id_aria128_gcm, DERNull.INSTANCE));
        addToMaps(GordianSymCipherSpec.cbc(GordianSymKeySpec.camellia(GordianLength.LEN_128), GordianPadding.NONE),
                new AlgorithmIdentifier(NTTObjectIdentifiers.id_camellia128_cbc, DERNull.INSTANCE));
        addToMaps(GordianSymCipherSpec.ecb(GordianSymKeySpec.kalyna(GordianLength.LEN_128), GordianPadding.NONE),
                new AlgorithmIdentifier(UAObjectIdentifiers.dstu7624ecb_128, DERNull.INSTANCE));
        addToMaps(GordianSymCipherSpec.cbc(GordianSymKeySpec.kalyna(GordianLength.LEN_128), GordianPadding.NONE),
                new AlgorithmIdentifier(UAObjectIdentifiers.dstu7624cbc_128, DERNull.INSTANCE));
        addToMaps(GordianSymCipherSpec.sic(GordianSymKeySpec.kalyna(GordianLength.LEN_128, GordianLength.LEN_128)),
                new AlgorithmIdentifier(UAObjectIdentifiers.dstu7624ctr_128, DERNull.INSTANCE));
        addToMaps(GordianSymCipherSpec.ofb(GordianSymKeySpec.kalyna(GordianLength.LEN_128, GordianLength.LEN_128)),
                new AlgorithmIdentifier(UAObjectIdentifiers.dstu7624ofb_128, DERNull.INSTANCE));
        addToMaps(GordianSymCipherSpec.cfb(GordianSymKeySpec.kalyna(GordianLength.LEN_128, GordianLength.LEN_128)),
                new AlgorithmIdentifier(UAObjectIdentifiers.dstu7624cfb_128, DERNull.INSTANCE));
        addToMaps(GordianSymCipherSpec.ccm(GordianSymKeySpec.kalyna(GordianLength.LEN_128, GordianLength.LEN_128)),
                new AlgorithmIdentifier(UAObjectIdentifiers.dstu7624ccm_128, DERNull.INSTANCE));
        addToMaps(GordianSymCipherSpec.gcm(GordianSymKeySpec.kalyna(GordianLength.LEN_128, GordianLength.LEN_128)),
                new AlgorithmIdentifier(UAObjectIdentifiers.dstu7624gmac_128, DERNull.INSTANCE));
    }

    /**
     * Add WellKnown ciphers for 192bit keys.
     */
    private void addWellKnownCiphers192() {
        addToMaps(GordianSymCipherSpec.ecb(GordianSymKeySpec.aes(GordianLength.LEN_192), GordianPadding.NONE),
                new AlgorithmIdentifier(NISTObjectIdentifiers.id_aes192_ECB, DERNull.INSTANCE));
        addToMaps(GordianSymCipherSpec.cbc(GordianSymKeySpec.aes(GordianLength.LEN_192), GordianPadding.NONE),
                new AlgorithmIdentifier(NISTObjectIdentifiers.id_aes192_CBC, DERNull.INSTANCE));
        addToMaps(GordianSymCipherSpec.ofb(GordianSymKeySpec.aes(GordianLength.LEN_192)),
                new AlgorithmIdentifier(NISTObjectIdentifiers.id_aes192_OFB, DERNull.INSTANCE));
        addToMaps(GordianSymCipherSpec.cfb(GordianSymKeySpec.aes(GordianLength.LEN_192)),
                new AlgorithmIdentifier(NISTObjectIdentifiers.id_aes192_CFB, DERNull.INSTANCE));
        addToMaps(GordianSymCipherSpec.ccm(GordianSymKeySpec.aes(GordianLength.LEN_192)),
                new AlgorithmIdentifier(NISTObjectIdentifiers.id_aes192_CCM, DERNull.INSTANCE));
        addToMaps(GordianSymCipherSpec.gcm(GordianSymKeySpec.aes(GordianLength.LEN_192)),
                new AlgorithmIdentifier(NISTObjectIdentifiers.id_aes192_GCM, DERNull.INSTANCE));
        addToMaps(GordianSymCipherSpec.ecb(GordianSymKeySpec.aria(GordianLength.LEN_192), GordianPadding.NONE),
                new AlgorithmIdentifier(NSRIObjectIdentifiers.id_aria192_ecb, DERNull.INSTANCE));
        addToMaps(GordianSymCipherSpec.cbc(GordianSymKeySpec.aria(GordianLength.LEN_192), GordianPadding.NONE),
                new AlgorithmIdentifier(NSRIObjectIdentifiers.id_aria192_cbc, DERNull.INSTANCE));
        addToMaps(GordianSymCipherSpec.sic(GordianSymKeySpec.aria(GordianLength.LEN_192)),
                new AlgorithmIdentifier(NSRIObjectIdentifiers.id_aria192_ctr, DERNull.INSTANCE));
        addToMaps(GordianSymCipherSpec.ofb(GordianSymKeySpec.aria(GordianLength.LEN_192)),
                new AlgorithmIdentifier(NSRIObjectIdentifiers.id_aria192_ofb, DERNull.INSTANCE));
        addToMaps(GordianSymCipherSpec.cfb(GordianSymKeySpec.aria(GordianLength.LEN_192)),
                new AlgorithmIdentifier(NSRIObjectIdentifiers.id_aria192_cfb, DERNull.INSTANCE));
        addToMaps(GordianSymCipherSpec.ccm(GordianSymKeySpec.aria(GordianLength.LEN_192)),
                new AlgorithmIdentifier(NSRIObjectIdentifiers.id_aria192_ccm, DERNull.INSTANCE));
        addToMaps(GordianSymCipherSpec.gcm(GordianSymKeySpec.aria(GordianLength.LEN_192)),
                new AlgorithmIdentifier(NSRIObjectIdentifiers.id_aria192_gcm, DERNull.INSTANCE));
        addToMaps(GordianSymCipherSpec.cbc(GordianSymKeySpec.camellia(GordianLength.LEN_192), GordianPadding.NONE),
                new AlgorithmIdentifier(NTTObjectIdentifiers.id_camellia192_cbc, DERNull.INSTANCE));
    }


    /**
     * Add WellKnown Ciphers for 256bit keys.
     */
    private void addWellKnownCiphers256() {
        addToMaps(GordianSymCipherSpec.ecb(GordianSymKeySpec.aes(GordianLength.LEN_256), GordianPadding.NONE),
                new AlgorithmIdentifier(NISTObjectIdentifiers.id_aes256_ECB, DERNull.INSTANCE));
        addToMaps(GordianSymCipherSpec.cbc(GordianSymKeySpec.aes(GordianLength.LEN_256), GordianPadding.NONE),
                new AlgorithmIdentifier(NISTObjectIdentifiers.id_aes256_CBC, DERNull.INSTANCE));
        addToMaps(GordianSymCipherSpec.ofb(GordianSymKeySpec.aes(GordianLength.LEN_256)),
                new AlgorithmIdentifier(NISTObjectIdentifiers.id_aes256_OFB, DERNull.INSTANCE));
        addToMaps(GordianSymCipherSpec.cfb(GordianSymKeySpec.aes(GordianLength.LEN_256)),
                new AlgorithmIdentifier(NISTObjectIdentifiers.id_aes256_CFB, DERNull.INSTANCE));
        addToMaps(GordianSymCipherSpec.ccm(GordianSymKeySpec.aes(GordianLength.LEN_256)),
                new AlgorithmIdentifier(NISTObjectIdentifiers.id_aes256_CCM, DERNull.INSTANCE));
        addToMaps(GordianSymCipherSpec.gcm(GordianSymKeySpec.aes(GordianLength.LEN_256)),
                new AlgorithmIdentifier(NISTObjectIdentifiers.id_aes256_GCM, DERNull.INSTANCE));
        addToMaps(GordianSymCipherSpec.ecb(GordianSymKeySpec.aria(GordianLength.LEN_256), GordianPadding.NONE),
                new AlgorithmIdentifier(NSRIObjectIdentifiers.id_aria256_ecb, DERNull.INSTANCE));
        addToMaps(GordianSymCipherSpec.cbc(GordianSymKeySpec.aria(GordianLength.LEN_256), GordianPadding.NONE),
                new AlgorithmIdentifier(NSRIObjectIdentifiers.id_aria256_cbc, DERNull.INSTANCE));
        addToMaps(GordianSymCipherSpec.sic(GordianSymKeySpec.aria(GordianLength.LEN_256)),
                new AlgorithmIdentifier(NSRIObjectIdentifiers.id_aria256_ctr, DERNull.INSTANCE));
        addToMaps(GordianSymCipherSpec.ofb(GordianSymKeySpec.aria(GordianLength.LEN_256)),
                new AlgorithmIdentifier(NSRIObjectIdentifiers.id_aria256_ofb, DERNull.INSTANCE));
        addToMaps(GordianSymCipherSpec.cfb(GordianSymKeySpec.aria(GordianLength.LEN_256)),
                new AlgorithmIdentifier(NSRIObjectIdentifiers.id_aria256_cfb, DERNull.INSTANCE));
        addToMaps(GordianSymCipherSpec.ccm(GordianSymKeySpec.aria(GordianLength.LEN_256)),
                new AlgorithmIdentifier(NSRIObjectIdentifiers.id_aria256_ccm, DERNull.INSTANCE));
        addToMaps(GordianSymCipherSpec.gcm(GordianSymKeySpec.aria(GordianLength.LEN_256)),
                new AlgorithmIdentifier(NSRIObjectIdentifiers.id_aria256_gcm, DERNull.INSTANCE));
        addToMaps(GordianSymCipherSpec.cbc(GordianSymKeySpec.camellia(GordianLength.LEN_256), GordianPadding.NONE),
                new AlgorithmIdentifier(NTTObjectIdentifiers.id_camellia256_cbc, DERNull.INSTANCE));
        addToMaps(GordianSymCipherSpec.ecb(GordianSymKeySpec.kalyna(GordianLength.LEN_256, GordianLength.LEN_256), GordianPadding.NONE),
                new AlgorithmIdentifier(UAObjectIdentifiers.dstu7624ecb_256, DERNull.INSTANCE));
        addToMaps(GordianSymCipherSpec.cbc(GordianSymKeySpec.kalyna(GordianLength.LEN_256, GordianLength.LEN_256), GordianPadding.NONE),
                new AlgorithmIdentifier(UAObjectIdentifiers.dstu7624cbc_256, DERNull.INSTANCE));
        addToMaps(GordianSymCipherSpec.sic(GordianSymKeySpec.kalyna(GordianLength.LEN_256, GordianLength.LEN_256)),
                new AlgorithmIdentifier(UAObjectIdentifiers.dstu7624ctr_256, DERNull.INSTANCE));
        addToMaps(GordianSymCipherSpec.ofb(GordianSymKeySpec.kalyna(GordianLength.LEN_256, GordianLength.LEN_256)),
                new AlgorithmIdentifier(UAObjectIdentifiers.dstu7624ofb_256, DERNull.INSTANCE));
        addToMaps(GordianSymCipherSpec.cfb(GordianSymKeySpec.kalyna(GordianLength.LEN_256, GordianLength.LEN_256)),
                new AlgorithmIdentifier(UAObjectIdentifiers.dstu7624cfb_256, DERNull.INSTANCE));
        addToMaps(GordianSymCipherSpec.ccm(GordianSymKeySpec.kalyna(GordianLength.LEN_256, GordianLength.LEN_256)),
                new AlgorithmIdentifier(UAObjectIdentifiers.dstu7624ccm_256, DERNull.INSTANCE));
        addToMaps(GordianSymCipherSpec.gcm(GordianSymKeySpec.kalyna(GordianLength.LEN_256, GordianLength.LEN_256)),
                new AlgorithmIdentifier(UAObjectIdentifiers.dstu7624gmac_256, DERNull.INSTANCE));
    }

    /**
     * Add symCipherSpec to map if supported and not already present.
     *
     * @param pSpec the symCipherSpec
     */
    private void ensureSymCipher(final GordianSymCipherSpec pSpec) {
        /* If the cipher is not already known */
        if (!theSpecMap.containsKey(pSpec)) {
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
        ASN1ObjectIdentifier myId = GordianKeyAlgId.appendSymKeyOID(CIPHEROID.branch("1"), true, pSpec.getKeyType());

        /* Add mode */
        final GordianCipherMode myMode = pSpec.getCipherMode();
        myId = myId.branch(Integer.toString(myMode.ordinal() + 1));

        /* Add padding if necessary */
        if (myMode.hasPadding()) {
            myId = myId.branch(Integer.toString(pSpec.getPadding().ordinal() + 1));
        }

        /* Add the spec to the maps */
        addToMaps(pSpec, new AlgorithmIdentifier(myId, DERNull.INSTANCE));
    }

    /**
     * Add streamCipherSpec to map if supported and not already present.
     *
     * @param pSpec the streamCipherSpec
     */
    private void ensureStreamCipher(final GordianStreamCipherSpec pSpec) {
        /* If the cipher is not already known */
        if (!theSpecMap.containsKey(pSpec)) {
            addStreamCipher(pSpec);
        }
    }

    /**
     * Create Identifier for a streamCipherSpec.
     *
     * @param pSpec the cipherSpec
     */
    private void addStreamCipher(final GordianStreamCipherSpec pSpec) {
        /* Create a branch for cipher based on the KeyType */
        final GordianStreamKeySpec mySpec = pSpec.getKeyType();
        ASN1ObjectIdentifier myId = GordianKeyAlgId.appendStreamKeyOID(CIPHEROID.branch("2"), mySpec);
        if (mySpec.supportsAAD()) {
            myId = myId.branch(Integer.toString(pSpec.isAAD() ? 2 : 1));
        }

        /* Add the spec to the maps */
        addToMaps(pSpec, new AlgorithmIdentifier(myId, DERNull.INSTANCE));
    }

    /**
     * Add symCipherSpec to maps.
     * @param pSpec the cipherSpec
     * @param pIdentifier the identifier
     */
    private void addToMaps(final GordianCipherSpec<?> pSpec,
                           final AlgorithmIdentifier pIdentifier) {
        theSpecMap.put(pSpec, pIdentifier);
        theIdentifierMap.put(pIdentifier, pSpec);
    }
}
