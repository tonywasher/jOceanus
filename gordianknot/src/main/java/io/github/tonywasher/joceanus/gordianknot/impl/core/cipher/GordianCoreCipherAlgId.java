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
package io.github.tonywasher.joceanus.gordianknot.impl.core.cipher;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.GordianCipherFactory;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewCipherSpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewPadding;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewStreamCipherSpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewSymCipherSpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewSymCipherSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewSymKeySpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewSymKeySpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.key.GordianKeyLengths;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianASN1Util;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.key.GordianCoreKeyAlgId;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.cipher.GordianCoreCipherMode;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.cipher.GordianCoreStreamKeySpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.cipher.GordianCoreSymCipherSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.cipher.GordianCoreSymCipherSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.cipher.GordianCoreSymKeySpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.cipher.GordianCoreSymKeySpecBuilder;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.gm.GMObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.nsri.NSRIObjectIdentifiers;
import org.bouncycastle.asn1.ntt.NTTObjectIdentifiers;
import org.bouncycastle.asn1.ua.UAObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Mappings from EncodedId to CipherSpec.
 */
public class GordianCoreCipherAlgId {
    /**
     * CipherOID branch.
     */
    private static final ASN1ObjectIdentifier CIPHEROID = GordianASN1Util.SYMOID.branch("4");

    /**
     * Map of cipherSpec to Identifier.
     */
    private final Map<GordianNewCipherSpec<?>, AlgorithmIdentifier> theSpecMap;

    /**
     * Map of Identifier to cipherSpec.
     */
    private final Map<AlgorithmIdentifier, GordianNewCipherSpec<?>> theIdentifierMap;

    /**
     * Constructor.
     *
     * @param pFactory the factory
     */
    GordianCoreCipherAlgId(final GordianBaseFactory pFactory) {
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
            for (GordianNewSymKeySpec mySymKey : myFactory.listAllSupportedSymKeySpecs(myKeyLen)) {
                /* Loop through the possible CipherSpecs */
                for (GordianNewSymCipherSpec mySpec : myFactory.listAllSupportedSymCipherSpecs(mySymKey)) {
                    /* Add any non-standard symCiphers */
                    ensureSymCipher(mySpec);
                }
            }

            /* Loop through the possible StreamCipherSpecs */
            for (GordianNewStreamCipherSpec mySpec : myFactory.listAllSupportedStreamCipherSpecs(myKeyLen)) {
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
    public AlgorithmIdentifier getIdentifierForSpec(final GordianNewCipherSpec<?> pSpec) {
        return theSpecMap.get(pSpec);
    }

    /**
     * Obtain symCipherSpec for Identifier.
     *
     * @param pIdentifier the identifier.
     * @return the cipherSpec (or null if not found)
     */
    GordianNewCipherSpec<?> getCipherSpecForIdentifier(final AlgorithmIdentifier pIdentifier) {
        return theIdentifierMap.get(pIdentifier);
    }

    /**
     * Add WellKnown Ciphers for 128bit keys.
     */
    private void addWellKnownCiphers128() {
        final GordianNewSymKeySpecBuilder myKeyBuilder = GordianCoreSymKeySpecBuilder.newInstance();
        final GordianNewSymCipherSpecBuilder myCipherBuilder = GordianCoreSymCipherSpecBuilder.newInstance();
        addToMaps(myCipherBuilder.ecb(myKeyBuilder.sm4(), GordianNewPadding.NONE), new AlgorithmIdentifier(GMObjectIdentifiers.sms4_ecb, DERNull.INSTANCE));
        addToMaps(myCipherBuilder.cbc(myKeyBuilder.sm4(), GordianNewPadding.NONE), new AlgorithmIdentifier(GMObjectIdentifiers.sms4_cbc, DERNull.INSTANCE));
        addToMaps(myCipherBuilder.sic(myKeyBuilder.sm4()), new AlgorithmIdentifier(GMObjectIdentifiers.sms4_ctr, DERNull.INSTANCE));
        addToMaps(myCipherBuilder.ofb(myKeyBuilder.sm4()), new AlgorithmIdentifier(GMObjectIdentifiers.sms4_ofb128, DERNull.INSTANCE));
        addToMaps(myCipherBuilder.cfb(myKeyBuilder.sm4()), new AlgorithmIdentifier(GMObjectIdentifiers.sms4_cfb8, DERNull.INSTANCE));
        addToMaps(myCipherBuilder.ccm(myKeyBuilder.sm4()), new AlgorithmIdentifier(GMObjectIdentifiers.sms4_ccm, DERNull.INSTANCE));
        addToMaps(myCipherBuilder.gcm(myKeyBuilder.sm4()), new AlgorithmIdentifier(GMObjectIdentifiers.sms4_gcm, DERNull.INSTANCE));
        addToMaps(myCipherBuilder.ocb(myKeyBuilder.sm4()), new AlgorithmIdentifier(GMObjectIdentifiers.sms4_ocb, DERNull.INSTANCE));
        addToMaps(myCipherBuilder.ecb(myKeyBuilder.aes(GordianLength.LEN_128), GordianNewPadding.NONE),
                new AlgorithmIdentifier(NISTObjectIdentifiers.id_aes128_ECB, DERNull.INSTANCE));
        addToMaps(myCipherBuilder.cbc(myKeyBuilder.aes(GordianLength.LEN_128), GordianNewPadding.NONE),
                new AlgorithmIdentifier(NISTObjectIdentifiers.id_aes128_CBC, DERNull.INSTANCE));
        addToMaps(myCipherBuilder.ofb(myKeyBuilder.aes(GordianLength.LEN_128)),
                new AlgorithmIdentifier(NISTObjectIdentifiers.id_aes128_OFB, DERNull.INSTANCE));
        addToMaps(myCipherBuilder.cfb(myKeyBuilder.aes(GordianLength.LEN_128)),
                new AlgorithmIdentifier(NISTObjectIdentifiers.id_aes128_CFB, DERNull.INSTANCE));
        addToMaps(myCipherBuilder.ccm(myKeyBuilder.aes(GordianLength.LEN_128)),
                new AlgorithmIdentifier(NISTObjectIdentifiers.id_aes128_CCM, DERNull.INSTANCE));
        addToMaps(myCipherBuilder.gcm(myKeyBuilder.aes(GordianLength.LEN_128)),
                new AlgorithmIdentifier(NISTObjectIdentifiers.id_aes128_GCM, DERNull.INSTANCE));
        addToMaps(myCipherBuilder.ecb(myKeyBuilder.aria(GordianLength.LEN_128), GordianNewPadding.NONE),
                new AlgorithmIdentifier(NSRIObjectIdentifiers.id_aria128_ecb, DERNull.INSTANCE));
        addToMaps(myCipherBuilder.cbc(myKeyBuilder.aria(GordianLength.LEN_128), GordianNewPadding.NONE),
                new AlgorithmIdentifier(NSRIObjectIdentifiers.id_aria128_cbc, DERNull.INSTANCE));
        addToMaps(myCipherBuilder.sic(myKeyBuilder.aria(GordianLength.LEN_128)),
                new AlgorithmIdentifier(NSRIObjectIdentifiers.id_aria128_ctr, DERNull.INSTANCE));
        addToMaps(myCipherBuilder.ofb(myKeyBuilder.aria(GordianLength.LEN_128)),
                new AlgorithmIdentifier(NSRIObjectIdentifiers.id_aria128_ofb, DERNull.INSTANCE));
        addToMaps(myCipherBuilder.cfb(myKeyBuilder.aria(GordianLength.LEN_128)),
                new AlgorithmIdentifier(NSRIObjectIdentifiers.id_aria128_cfb, DERNull.INSTANCE));
        addToMaps(myCipherBuilder.ccm(myKeyBuilder.aria(GordianLength.LEN_128)),
                new AlgorithmIdentifier(NSRIObjectIdentifiers.id_aria128_ccm, DERNull.INSTANCE));
        addToMaps(myCipherBuilder.gcm(myKeyBuilder.aria(GordianLength.LEN_128)),
                new AlgorithmIdentifier(NSRIObjectIdentifiers.id_aria128_gcm, DERNull.INSTANCE));
        addToMaps(myCipherBuilder.cbc(myKeyBuilder.camellia(GordianLength.LEN_128), GordianNewPadding.NONE),
                new AlgorithmIdentifier(NTTObjectIdentifiers.id_camellia128_cbc, DERNull.INSTANCE));
        addToMaps(myCipherBuilder.ecb(myKeyBuilder.kalyna(GordianLength.LEN_128), GordianNewPadding.NONE),
                new AlgorithmIdentifier(UAObjectIdentifiers.dstu7624ecb_128, DERNull.INSTANCE));
        addToMaps(myCipherBuilder.cbc(myKeyBuilder.kalyna(GordianLength.LEN_128), GordianNewPadding.NONE),
                new AlgorithmIdentifier(UAObjectIdentifiers.dstu7624cbc_128, DERNull.INSTANCE));
        addToMaps(myCipherBuilder.sic(myKeyBuilder.kalyna(GordianLength.LEN_128, GordianLength.LEN_128)),
                new AlgorithmIdentifier(UAObjectIdentifiers.dstu7624ctr_128, DERNull.INSTANCE));
        addToMaps(myCipherBuilder.ofb(myKeyBuilder.kalyna(GordianLength.LEN_128, GordianLength.LEN_128)),
                new AlgorithmIdentifier(UAObjectIdentifiers.dstu7624ofb_128, DERNull.INSTANCE));
        addToMaps(myCipherBuilder.cfb(myKeyBuilder.kalyna(GordianLength.LEN_128, GordianLength.LEN_128)),
                new AlgorithmIdentifier(UAObjectIdentifiers.dstu7624cfb_128, DERNull.INSTANCE));
        addToMaps(myCipherBuilder.ccm(myKeyBuilder.kalyna(GordianLength.LEN_128, GordianLength.LEN_128)),
                new AlgorithmIdentifier(UAObjectIdentifiers.dstu7624ccm_128, DERNull.INSTANCE));
        addToMaps(myCipherBuilder.gcm(myKeyBuilder.kalyna(GordianLength.LEN_128, GordianLength.LEN_128)),
                new AlgorithmIdentifier(UAObjectIdentifiers.dstu7624gmac_128, DERNull.INSTANCE));
    }

    /**
     * Add WellKnown ciphers for 192bit keys.
     */
    private void addWellKnownCiphers192() {
        final GordianNewSymKeySpecBuilder myKeyBuilder = GordianCoreSymKeySpecBuilder.newInstance();
        final GordianNewSymCipherSpecBuilder myCipherBuilder = GordianCoreSymCipherSpecBuilder.newInstance();
        addToMaps(myCipherBuilder.ecb(myKeyBuilder.aes(GordianLength.LEN_192), GordianNewPadding.NONE),
                new AlgorithmIdentifier(NISTObjectIdentifiers.id_aes192_ECB, DERNull.INSTANCE));
        addToMaps(myCipherBuilder.cbc(myKeyBuilder.aes(GordianLength.LEN_192), GordianNewPadding.NONE),
                new AlgorithmIdentifier(NISTObjectIdentifiers.id_aes192_CBC, DERNull.INSTANCE));
        addToMaps(myCipherBuilder.ofb(myKeyBuilder.aes(GordianLength.LEN_192)),
                new AlgorithmIdentifier(NISTObjectIdentifiers.id_aes192_OFB, DERNull.INSTANCE));
        addToMaps(myCipherBuilder.cfb(myKeyBuilder.aes(GordianLength.LEN_192)),
                new AlgorithmIdentifier(NISTObjectIdentifiers.id_aes192_CFB, DERNull.INSTANCE));
        addToMaps(myCipherBuilder.ccm(myKeyBuilder.aes(GordianLength.LEN_192)),
                new AlgorithmIdentifier(NISTObjectIdentifiers.id_aes192_CCM, DERNull.INSTANCE));
        addToMaps(myCipherBuilder.gcm(myKeyBuilder.aes(GordianLength.LEN_192)),
                new AlgorithmIdentifier(NISTObjectIdentifiers.id_aes192_GCM, DERNull.INSTANCE));
        addToMaps(myCipherBuilder.ecb(myKeyBuilder.aria(GordianLength.LEN_192), GordianNewPadding.NONE),
                new AlgorithmIdentifier(NSRIObjectIdentifiers.id_aria192_ecb, DERNull.INSTANCE));
        addToMaps(myCipherBuilder.cbc(myKeyBuilder.aria(GordianLength.LEN_192), GordianNewPadding.NONE),
                new AlgorithmIdentifier(NSRIObjectIdentifiers.id_aria192_cbc, DERNull.INSTANCE));
        addToMaps(myCipherBuilder.sic(myKeyBuilder.aria(GordianLength.LEN_192)),
                new AlgorithmIdentifier(NSRIObjectIdentifiers.id_aria192_ctr, DERNull.INSTANCE));
        addToMaps(myCipherBuilder.ofb(myKeyBuilder.aria(GordianLength.LEN_192)),
                new AlgorithmIdentifier(NSRIObjectIdentifiers.id_aria192_ofb, DERNull.INSTANCE));
        addToMaps(myCipherBuilder.cfb(myKeyBuilder.aria(GordianLength.LEN_192)),
                new AlgorithmIdentifier(NSRIObjectIdentifiers.id_aria192_cfb, DERNull.INSTANCE));
        addToMaps(myCipherBuilder.ccm(myKeyBuilder.aria(GordianLength.LEN_192)),
                new AlgorithmIdentifier(NSRIObjectIdentifiers.id_aria192_ccm, DERNull.INSTANCE));
        addToMaps(myCipherBuilder.gcm(myKeyBuilder.aria(GordianLength.LEN_192)),
                new AlgorithmIdentifier(NSRIObjectIdentifiers.id_aria192_gcm, DERNull.INSTANCE));
        addToMaps(myCipherBuilder.cbc(myKeyBuilder.camellia(GordianLength.LEN_192), GordianNewPadding.NONE),
                new AlgorithmIdentifier(NTTObjectIdentifiers.id_camellia192_cbc, DERNull.INSTANCE));
    }


    /**
     * Add WellKnown Ciphers for 256bit keys.
     */
    private void addWellKnownCiphers256() {
        final GordianNewSymKeySpecBuilder myKeyBuilder = GordianCoreSymKeySpecBuilder.newInstance();
        final GordianNewSymCipherSpecBuilder myCipherBuilder = GordianCoreSymCipherSpecBuilder.newInstance();
        addToMaps(myCipherBuilder.ecb(myKeyBuilder.aes(GordianLength.LEN_256), GordianNewPadding.NONE),
                new AlgorithmIdentifier(NISTObjectIdentifiers.id_aes256_ECB, DERNull.INSTANCE));
        addToMaps(myCipherBuilder.cbc(myKeyBuilder.aes(GordianLength.LEN_256), GordianNewPadding.NONE),
                new AlgorithmIdentifier(NISTObjectIdentifiers.id_aes256_CBC, DERNull.INSTANCE));
        addToMaps(myCipherBuilder.ofb(myKeyBuilder.aes(GordianLength.LEN_256)),
                new AlgorithmIdentifier(NISTObjectIdentifiers.id_aes256_OFB, DERNull.INSTANCE));
        addToMaps(myCipherBuilder.cfb(myKeyBuilder.aes(GordianLength.LEN_256)),
                new AlgorithmIdentifier(NISTObjectIdentifiers.id_aes256_CFB, DERNull.INSTANCE));
        addToMaps(myCipherBuilder.ccm(myKeyBuilder.aes(GordianLength.LEN_256)),
                new AlgorithmIdentifier(NISTObjectIdentifiers.id_aes256_CCM, DERNull.INSTANCE));
        addToMaps(myCipherBuilder.gcm(myKeyBuilder.aes(GordianLength.LEN_256)),
                new AlgorithmIdentifier(NISTObjectIdentifiers.id_aes256_GCM, DERNull.INSTANCE));
        addToMaps(myCipherBuilder.ecb(myKeyBuilder.aria(GordianLength.LEN_256), GordianNewPadding.NONE),
                new AlgorithmIdentifier(NSRIObjectIdentifiers.id_aria256_ecb, DERNull.INSTANCE));
        addToMaps(myCipherBuilder.cbc(myKeyBuilder.aria(GordianLength.LEN_256), GordianNewPadding.NONE),
                new AlgorithmIdentifier(NSRIObjectIdentifiers.id_aria256_cbc, DERNull.INSTANCE));
        addToMaps(myCipherBuilder.sic(myKeyBuilder.aria(GordianLength.LEN_256)),
                new AlgorithmIdentifier(NSRIObjectIdentifiers.id_aria256_ctr, DERNull.INSTANCE));
        addToMaps(myCipherBuilder.ofb(myKeyBuilder.aria(GordianLength.LEN_256)),
                new AlgorithmIdentifier(NSRIObjectIdentifiers.id_aria256_ofb, DERNull.INSTANCE));
        addToMaps(myCipherBuilder.cfb(myKeyBuilder.aria(GordianLength.LEN_256)),
                new AlgorithmIdentifier(NSRIObjectIdentifiers.id_aria256_cfb, DERNull.INSTANCE));
        addToMaps(myCipherBuilder.ccm(myKeyBuilder.aria(GordianLength.LEN_256)),
                new AlgorithmIdentifier(NSRIObjectIdentifiers.id_aria256_ccm, DERNull.INSTANCE));
        addToMaps(myCipherBuilder.gcm(myKeyBuilder.aria(GordianLength.LEN_256)),
                new AlgorithmIdentifier(NSRIObjectIdentifiers.id_aria256_gcm, DERNull.INSTANCE));
        addToMaps(myCipherBuilder.cbc(myKeyBuilder.camellia(GordianLength.LEN_256), GordianNewPadding.NONE),
                new AlgorithmIdentifier(NTTObjectIdentifiers.id_camellia256_cbc, DERNull.INSTANCE));
        addToMaps(myCipherBuilder.ecb(myKeyBuilder.kalyna(GordianLength.LEN_256, GordianLength.LEN_256), GordianNewPadding.NONE),
                new AlgorithmIdentifier(UAObjectIdentifiers.dstu7624ecb_256, DERNull.INSTANCE));
        addToMaps(myCipherBuilder.cbc(myKeyBuilder.kalyna(GordianLength.LEN_256, GordianLength.LEN_256), GordianNewPadding.NONE),
                new AlgorithmIdentifier(UAObjectIdentifiers.dstu7624cbc_256, DERNull.INSTANCE));
        addToMaps(myCipherBuilder.sic(myKeyBuilder.kalyna(GordianLength.LEN_256, GordianLength.LEN_256)),
                new AlgorithmIdentifier(UAObjectIdentifiers.dstu7624ctr_256, DERNull.INSTANCE));
        addToMaps(myCipherBuilder.ofb(myKeyBuilder.kalyna(GordianLength.LEN_256, GordianLength.LEN_256)),
                new AlgorithmIdentifier(UAObjectIdentifiers.dstu7624ofb_256, DERNull.INSTANCE));
        addToMaps(myCipherBuilder.cfb(myKeyBuilder.kalyna(GordianLength.LEN_256, GordianLength.LEN_256)),
                new AlgorithmIdentifier(UAObjectIdentifiers.dstu7624cfb_256, DERNull.INSTANCE));
        addToMaps(myCipherBuilder.ccm(myKeyBuilder.kalyna(GordianLength.LEN_256, GordianLength.LEN_256)),
                new AlgorithmIdentifier(UAObjectIdentifiers.dstu7624ccm_256, DERNull.INSTANCE));
        addToMaps(myCipherBuilder.gcm(myKeyBuilder.kalyna(GordianLength.LEN_256, GordianLength.LEN_256)),
                new AlgorithmIdentifier(UAObjectIdentifiers.dstu7624gmac_256, DERNull.INSTANCE));
    }

    /**
     * Add symCipherSpec to map if supported and not already present.
     *
     * @param pSpec the symCipherSpec
     */
    private void ensureSymCipher(final GordianNewSymCipherSpec pSpec) {
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
    private void addSymCipher(final GordianNewSymCipherSpec pSpec) {
        /* Build SymKey id */
        final GordianCoreSymCipherSpec mySpec = (GordianCoreSymCipherSpec) pSpec;
        final GordianCoreSymKeySpec myKeySpec = (GordianCoreSymKeySpec) mySpec.getKeySpec();
        ASN1ObjectIdentifier myId = GordianCoreKeyAlgId.appendSymKeyOID(CIPHEROID.branch("1"), true, myKeySpec);

        /* Add mode */
        final GordianCoreCipherMode myMode = mySpec.getCoreCipherMode();
        myId = myId.branch(Integer.toString(myMode.getMode().ordinal() + 1));

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
    private void ensureStreamCipher(final GordianNewStreamCipherSpec pSpec) {
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
    private void addStreamCipher(final GordianNewStreamCipherSpec pSpec) {
        /* Create a branch for cipher based on the KeyType */
        final GordianCoreStreamKeySpec mySpec = (GordianCoreStreamKeySpec) pSpec.getKeySpec();
        ASN1ObjectIdentifier myId = GordianCoreKeyAlgId.appendStreamKeyOID(CIPHEROID.branch("2"), mySpec);
        if (mySpec.supportsAEAD()) {
            myId = myId.branch(Integer.toString(pSpec.isAEAD() ? 2 : 1));
        }

        /* Add the spec to the maps */
        addToMaps(pSpec, new AlgorithmIdentifier(myId, DERNull.INSTANCE));
    }

    /**
     * Add symCipherSpec to maps.
     *
     * @param pSpec       the cipherSpec
     * @param pIdentifier the identifier
     */
    private void addToMaps(final GordianNewCipherSpec<?> pSpec,
                           final AlgorithmIdentifier pIdentifier) {
        theSpecMap.put(pSpec, pIdentifier);
        theIdentifierMap.put(pIdentifier, pSpec);
    }
}
