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
package net.sourceforge.joceanus.jgordianknot.api.cipher;

import java.util.EnumMap;
import java.util.Map;
import java.util.ResourceBundle;

import net.sourceforge.joceanus.jtethys.resource.TethysBundleId;
import net.sourceforge.joceanus.jtethys.resource.TethysBundleLoader;

/**
 * Resource IDs for Cipher package.
 */
public enum GordianCipherResource implements TethysBundleId {
    /**
     * SymKey AES.
     */
    SYMKEY_AES("symKey.AES"),

    /**
     * SymKey TwoFish.
     */
    SYMKEY_TWOFISH("symKey.TWOFISH"),

    /**
     * SymKey Serpent.
     */
    SYMKEY_SERPENT("symKey.SERPENT"),

    /**
     * SymKey Camellia.
     */
    SYMKEY_CAMELLIA("symKey.CAMELLIA"),

    /**
     * SymKey CAST6.
     */
    SYMKEY_CAST6("symKey.CAST6"),

    /**
     * SymKey RC6.
     */
    SYMKEY_RC6("symKey.RC6"),

    /**
     * SymKey THREEFISH.
     */
    SYMKEY_THREEFISH("symKey.THREEFISH"),

    /**
     * SymKey ARIA.
     */
    SYMKEY_ARIA("symKey.ARIA"),

    /**
     * SymKey NOEKEON.
     */
    SYMKEY_NOEKEON("symKey.NOEKEON"),

    /**
     * SymKey SEED.
     */
    SYMKEY_SEED("symKey.SEED"),

    /**
     * SymKey SM4.
     */
    SYMKEY_SM4("symKey.SM4"),

    /**
     * SymKey RC2.
     */
    SYMKEY_RC2("symKey.RC2"),

    /**
     * SymKey RC5.
     */
    SYMKEY_RC5("symKey.RC5"),

    /**
     * SymKey CAST5.
     */
    SYMKEY_CAST5("symKey.CAST5"),

    /**
     * SymKey IDEA.
     */
    SYMKEY_IDEA("symKey.IDEA"),

    /**
     * SymKey TEA.
     */
    SYMKEY_TEA("symKey.TEA"),

    /**
     * SymKey XTEA.
     */
    SYMKEY_XTEA("symKey.XTEA"),

    /**
     * SymKey Blowfish.
     */
    SYMKEY_BLOWFISH("symKey.BlowFish"),

    /**
     * SymKey GOST.
     */
    SYMKEY_GOST("symKey.GOST"),

    /**
     * SymKey Kuznyechik.
     */
    SYMKEY_KUZNYECHIK("symKey.KUZNYECHIK"),

    /**
     * SymKey Kalyna.
     */
    SYMKEY_KALYNA("symKey.Kalyna"),

    /**
     * SymKey SkipJack.
     */
    SYMKEY_SKIPJACK("symKey.Skipjack"),

    /**
     * SymKey DESede.
     */
    SYMKEY_DESEDE("symKey.DESede"),

    /**
     * SymKey SHACAL2.
     */
    SYMKEY_SHACAL2("symKey.SHACAL2"),

    /**
     * SymKey SHACAL2.
     */
    SYMKEY_SPECK("symKey.Speck"),

    /**
     * SymKey Anubis.
     */
    SYMKEY_ANUBIS("symKey.Anubis"),

    /**
     * SymKey Simon.
     */
    SYMKEY_SIMON("symKey.Simon"),

    /**
     * SymKey MARS.
     */
    SYMKEY_MARS("symKey.MARS"),

    /**
     * StreamKey XSALSA20.
     */
    STREAMKEY_XSALSA20("streamKey.XSALSA20"),

    /**
     * StreamKey SALSA20.
     */
    STREAMKEY_SALSA20("streamKey.SALSA20"),

    /**
     * StreamKey HC.
     */
    STREAMKEY_HC("streamKey.HC"),

    /**
     * StreamKey CHACHA.
     */
    STREAMKEY_CHACHA("streamKey.CHACHA"),

    /**
     * StreamKey XCHACHA20.
     */
    STREAMKEY_XCHACHA20("streamKey.XCHACHA20"),

    /**
     * StreamKey VMPC.
     */
    STREAMKEY_VMPC("streamKey.VMPC"),

    /**
     * StreamKey ISAAC.
     */
    STREAMKEY_ISAAC("streamKey.ISAAC"),

    /**
     * StreamKey GRAIN.
     */
    STREAMKEY_GRAIN("streamKey.GRAIN"),

    /**
     * StreamKey RC4.
     */
    STREAMKEY_RC4("streamKey.RC4"),

    /**
     * StreamKey SOSEMANUK.
     */
    STREAMKEY_SOSEMANUK("streamKey.SOSEMANUK");

    /**
     * The SymKey Map.
     */
    private static final Map<GordianSymKeyType, TethysBundleId> SYM_MAP = buildSymKeyMap();

    /**
     * The StreamKey Map.
     */
    private static final Map<GordianStreamKeyType, TethysBundleId> STREAM_MAP = buildStreamKeyMap();

    /**
     * The Resource Loader.
     */
    private static final TethysBundleLoader LOADER = TethysBundleLoader.getLoader(GordianCipher.class.getCanonicalName(),
            ResourceBundle::getBundle);

    /**
     * The Id.
     */
    private final String theKeyName;

    /**
     * The Value.
     */
    private String theValue;

    /**
     * Constructor.
     * @param pKeyName the key name
     */
    GordianCipherResource(final String pKeyName) {
        theKeyName = pKeyName;
    }

    @Override
    public String getKeyName() {
        return theKeyName;
    }

    @Override
    public String getNameSpace() {
        return "cipher";
    }

    @Override
    public String getValue() {
        /* If we have not initialised the value */
        if (theValue == null) {
            /* Derive the value */
            theValue = LOADER.getValue(this);
        }

        /* return the value */
        return theValue;
    }

    /**
     * Build SymKey map.
     * @return the map
     */
    private static Map<GordianSymKeyType, TethysBundleId> buildSymKeyMap() {
        /* Create the map and return it */
        final Map<GordianSymKeyType, TethysBundleId> myMap = new EnumMap<>(GordianSymKeyType.class);
        myMap.put(GordianSymKeyType.AES, SYMKEY_AES);
        myMap.put(GordianSymKeyType.SERPENT, SYMKEY_SERPENT);
        myMap.put(GordianSymKeyType.TWOFISH, SYMKEY_TWOFISH);
        myMap.put(GordianSymKeyType.CAMELLIA, SYMKEY_CAMELLIA);
        myMap.put(GordianSymKeyType.RC6, SYMKEY_RC6);
        myMap.put(GordianSymKeyType.CAST6, SYMKEY_CAST6);
        myMap.put(GordianSymKeyType.ARIA, SYMKEY_ARIA);
        myMap.put(GordianSymKeyType.THREEFISH, SYMKEY_THREEFISH);
        myMap.put(GordianSymKeyType.NOEKEON, SYMKEY_NOEKEON);
        myMap.put(GordianSymKeyType.SEED, SYMKEY_SEED);
        myMap.put(GordianSymKeyType.SM4, SYMKEY_SM4);
        myMap.put(GordianSymKeyType.RC2, SYMKEY_RC2);
        myMap.put(GordianSymKeyType.RC5, SYMKEY_RC5);
        myMap.put(GordianSymKeyType.CAST5, SYMKEY_CAST5);
        myMap.put(GordianSymKeyType.TEA, SYMKEY_TEA);
        myMap.put(GordianSymKeyType.XTEA, SYMKEY_XTEA);
        myMap.put(GordianSymKeyType.IDEA, SYMKEY_IDEA);
        myMap.put(GordianSymKeyType.SKIPJACK, SYMKEY_SKIPJACK);
        myMap.put(GordianSymKeyType.BLOWFISH, SYMKEY_BLOWFISH);
        myMap.put(GordianSymKeyType.DESEDE, SYMKEY_DESEDE);
        myMap.put(GordianSymKeyType.GOST, SYMKEY_GOST);
        myMap.put(GordianSymKeyType.KUZNYECHIK, SYMKEY_KUZNYECHIK);
        myMap.put(GordianSymKeyType.KALYNA, SYMKEY_KALYNA);
        myMap.put(GordianSymKeyType.SHACAL2, SYMKEY_SHACAL2);
        myMap.put(GordianSymKeyType.SPECK, SYMKEY_SPECK);
        myMap.put(GordianSymKeyType.ANUBIS, SYMKEY_ANUBIS);
        myMap.put(GordianSymKeyType.SIMON, SYMKEY_SIMON);
        myMap.put(GordianSymKeyType.MARS, SYMKEY_MARS);
        return myMap;
    }

    /**
     * Obtain key for SymKey.
     * @param pKeyType the keyType
     * @return the resource key
     */
    protected static TethysBundleId getKeyForSym(final GordianSymKeyType pKeyType) {
        return TethysBundleLoader.getKeyForEnum(SYM_MAP, pKeyType);
    }

    /**
     * Build StreamKey map.
     * @return the map
     */
    private static Map<GordianStreamKeyType, TethysBundleId> buildStreamKeyMap() {
        /* Create the map and return it */
        final Map<GordianStreamKeyType, TethysBundleId> myMap = new EnumMap<>(GordianStreamKeyType.class);
        myMap.put(GordianStreamKeyType.XSALSA20, STREAMKEY_XSALSA20);
        myMap.put(GordianStreamKeyType.SALSA20, STREAMKEY_SALSA20);
        myMap.put(GordianStreamKeyType.HC, STREAMKEY_HC);
        myMap.put(GordianStreamKeyType.CHACHA, STREAMKEY_CHACHA);
        myMap.put(GordianStreamKeyType.XCHACHA20, STREAMKEY_XCHACHA20);
        myMap.put(GordianStreamKeyType.VMPC, STREAMKEY_VMPC);
        myMap.put(GordianStreamKeyType.ISAAC, STREAMKEY_ISAAC);
        myMap.put(GordianStreamKeyType.GRAIN, STREAMKEY_GRAIN);
        myMap.put(GordianStreamKeyType.RC4, STREAMKEY_RC4);
        myMap.put(GordianStreamKeyType.SOSEMANUK, STREAMKEY_SOSEMANUK);
        return myMap;
    }

    /**
     * Obtain key for StreamKey.
     * @param pKeyType the keyType
     * @return the resource key
     */
    protected static TethysBundleId getKeyForStream(final GordianStreamKeyType pKeyType) {
        return TethysBundleLoader.getKeyForEnum(STREAM_MAP, pKeyType);
    }
}
