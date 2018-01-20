/*******************************************************************************
 * jGordianKnot: Security Suite
 * Copyright 2012,2017 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jgordianknot.crypto;

import java.util.EnumMap;
import java.util.Map;

import net.sourceforge.joceanus.jgordianknot.GordianCryptoException;
import net.sourceforge.joceanus.jtethys.resource.TethysResourceBuilder;
import net.sourceforge.joceanus.jtethys.resource.TethysResourceId;

/**
 * Resource IDs for Cryptographic package.
 */
public enum GordianCryptoResource implements TethysResourceId {
    /**
     * Factory BC.
     */
    FACTORY_BC("factory.BC"),

    /**
     * Factory JCA.
     */
    FACTORY_JCA("factory.JCA"),

    /**
     * Digest SHA2.
     */
    DIGEST_SHA2("digest.SHA2"),

    /**
     * Digest Tiger.
     */
    DIGEST_TIGER("digest.TIGER"),

    /**
     * Digest WhirlPool.
     */
    DIGEST_WHIRLPOOL("digest.WHIRLPOOL"),

    /**
     * Digest SHA2.
     */
    DIGEST_RIPEMD("digest.RIPEMD"),

    /**
     * Digest Streebog.
     */
    DIGEST_STREEBOG("digest.STREEBOG"),

    /**
     * Digest GOST.
     */
    DIGEST_GOST("digest.GOST"),

    /**
     * Digest SHA3.
     */
    DIGEST_SHA3("digest.SHA3"),

    /**
     * Digest SHAKE.
     */
    DIGEST_SHAKE("digest.SHAKE"),

    /**
     * Digest Skein.
     */
    DIGEST_SKEIN("digest.SKEIN"),

    /**
     * Digest SM3.
     */
    DIGEST_SM3("digest.SM3"),

    /**
     * Digest BLAKE.
     */
    DIGEST_BLAKE("digest.BLAKE"),

    /**
     * Digest Kupyna.
     */
    DIGEST_KUPYNA("digest.KUPYNA"),

    /**
     * Digest SHA.
     */
    DIGEST_SHA1("digest.SHA1"),

    /**
     * Digest MD5.
     */
    DIGEST_MD5("digest.MD5"),

    /**
     * Digest MD4.
     */
    DIGEST_MD4("digest.MD4"),

    /**
     * Digest MD2.
     */
    DIGEST_MD2("digest.MD2"),

    /**
     * Digest JH.
     */
    DIGEST_JH("digest.JH"),

    /**
     * Digest Groestl.
     */
    DIGEST_GROESTL("digest.GROESTL"),

    /**
     * MAC HMAC.
     */
    MAC_HMAC("mac.HMAC"),

    /**
     * MAC GMAC.
     */
    MAC_GMAC("mac.GMAC"),

    /**
     * MAC CMAC.
     */
    MAC_CMAC("mac.CMAC"),

    /**
     * MAC POLY1305.
     */
    MAC_POLY("mac.POLY1305"),

    /**
     * MAC SKEIN.
     */
    MAC_SKEIN("mac.SKEIN"),

    /**
     * MAC KALYNA.
     */
    MAC_KALYNA("mac.KALYNA"),

    /**
     * MAC KUPYNA.
     */
    MAC_KUPYNA("mac.KUPYNA"),

    /**
     * MAC BLAKE.
     */
    MAC_BLAKE("mac.BLAKE"),

    /**
     * MAC VMPC.
     */
    MAC_VMPC("mac.VMPC"),

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
     * StreamKey CHACHA7539.
     */
    STREAMKEY_CHACHA7539("streamKey.CHACHA7539"),

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
     * The Factory Map.
     */
    private static final Map<GordianFactoryType, TethysResourceId> FACTORY_MAP = buildFactoryMap();

    /**
     * The Digest Map.
     */
    private static final Map<GordianDigestType, TethysResourceId> DIGEST_MAP = buildDigestMap();

    /**
     * The MAC Map.
     */
    private static final Map<GordianMacType, TethysResourceId> MAC_MAP = buildMacMap();

    /**
     * The SymKey Map.
     */
    private static final Map<GordianSymKeyType, TethysResourceId> SYM_MAP = buildSymKeyMap();

    /**
     * The StreamKey Map.
     */
    private static final Map<GordianStreamKeyType, TethysResourceId> STREAM_MAP = buildStreamKeyMap();

    /**
     * The Resource Builder.
     */
    private static final TethysResourceBuilder BUILDER = TethysResourceBuilder.getPackageResourceBuilder(GordianCryptoException.class.getCanonicalName());

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
    GordianCryptoResource(final String pKeyName) {
        theKeyName = pKeyName;
    }

    @Override
    public String getKeyName() {
        return theKeyName;
    }

    @Override
    public String getNameSpace() {
        return "crypto";
    }

    @Override
    public String getValue() {
        /* If we have not initialised the value */
        if (theValue == null) {
            /* Derive the value */
            theValue = BUILDER.getValue(this);
        }

        /* return the value */
        return theValue;
    }

    /**
     * Build factory map.
     * @return the map
     */
    private static Map<GordianFactoryType, TethysResourceId> buildFactoryMap() {
        /* Create the map and return it */
        final Map<GordianFactoryType, TethysResourceId> myMap = new EnumMap<>(GordianFactoryType.class);
        myMap.put(GordianFactoryType.BC, FACTORY_BC);
        myMap.put(GordianFactoryType.JCA, FACTORY_JCA);
        return myMap;
    }

    /**
     * Obtain key for Factory.
     * @param pFactoryType the factoryType
     * @return the resource key
     */
    protected static TethysResourceId getKeyForFactoryType(final GordianFactoryType pFactoryType) {
        return TethysResourceBuilder.getKeyForEnum(FACTORY_MAP, pFactoryType);
    }

    /**
     * Build digest map.
     * @return the map
     */
    private static Map<GordianDigestType, TethysResourceId> buildDigestMap() {
        /* Create the map and return it */
        final Map<GordianDigestType, TethysResourceId> myMap = new EnumMap<>(GordianDigestType.class);
        myMap.put(GordianDigestType.SHA2, DIGEST_SHA2);
        myMap.put(GordianDigestType.TIGER, DIGEST_TIGER);
        myMap.put(GordianDigestType.WHIRLPOOL, DIGEST_WHIRLPOOL);
        myMap.put(GordianDigestType.RIPEMD, DIGEST_RIPEMD);
        myMap.put(GordianDigestType.STREEBOG, DIGEST_STREEBOG);
        myMap.put(GordianDigestType.GOST, DIGEST_GOST);
        myMap.put(GordianDigestType.SHA3, DIGEST_SHA3);
        myMap.put(GordianDigestType.SHAKE, DIGEST_SHAKE);
        myMap.put(GordianDigestType.SKEIN, DIGEST_SKEIN);
        myMap.put(GordianDigestType.SM3, DIGEST_SM3);
        myMap.put(GordianDigestType.BLAKE, DIGEST_BLAKE);
        myMap.put(GordianDigestType.KUPYNA, DIGEST_KUPYNA);
        myMap.put(GordianDigestType.SHA1, DIGEST_SHA1);
        myMap.put(GordianDigestType.MD5, DIGEST_MD5);
        myMap.put(GordianDigestType.MD4, DIGEST_MD4);
        myMap.put(GordianDigestType.MD2, DIGEST_MD2);
        myMap.put(GordianDigestType.JH, DIGEST_JH);
        myMap.put(GordianDigestType.GROESTL, DIGEST_GROESTL);
        return myMap;
    }

    /**
     * Obtain key for Digest.
     * @param pDigest the DigestType
     * @return the resource key
     */
    protected static TethysResourceId getKeyForDigest(final GordianDigestType pDigest) {
        return TethysResourceBuilder.getKeyForEnum(DIGEST_MAP, pDigest);
    }

    /**
     * Build MAC map.
     * @return the map
     */
    private static Map<GordianMacType, TethysResourceId> buildMacMap() {
        /* Create the map and return it */
        final Map<GordianMacType, TethysResourceId> myMap = new EnumMap<>(GordianMacType.class);
        myMap.put(GordianMacType.HMAC, MAC_HMAC);
        myMap.put(GordianMacType.GMAC, MAC_GMAC);
        myMap.put(GordianMacType.CMAC, MAC_CMAC);
        myMap.put(GordianMacType.POLY1305, MAC_POLY);
        myMap.put(GordianMacType.SKEIN, MAC_SKEIN);
        myMap.put(GordianMacType.KALYNA, MAC_KALYNA);
        myMap.put(GordianMacType.KUPYNA, MAC_KUPYNA);
        myMap.put(GordianMacType.BLAKE, MAC_BLAKE);
        myMap.put(GordianMacType.VMPC, MAC_VMPC);
        return myMap;
    }

    /**
     * Obtain key for MAC.
     * @param pMac the MacType
     * @return the resource key
     */
    protected static TethysResourceId getKeyForMac(final GordianMacType pMac) {
        return TethysResourceBuilder.getKeyForEnum(MAC_MAP, pMac);
    }

    /**
     * Build SymKey map.
     * @return the map
     */
    private static Map<GordianSymKeyType, TethysResourceId> buildSymKeyMap() {
        /* Create the map and return it */
        final Map<GordianSymKeyType, TethysResourceId> myMap = new EnumMap<>(GordianSymKeyType.class);
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
        return myMap;
    }

    /**
     * Obtain key for SymKey.
     * @param pKeyType the keyType
     * @return the resource key
     */
    protected static TethysResourceId getKeyForSym(final GordianSymKeyType pKeyType) {
        return TethysResourceBuilder.getKeyForEnum(SYM_MAP, pKeyType);
    }

    /**
     * Build StreamKey map.
     * @return the map
     */
    private static Map<GordianStreamKeyType, TethysResourceId> buildStreamKeyMap() {
        /* Create the map and return it */
        final Map<GordianStreamKeyType, TethysResourceId> myMap = new EnumMap<>(GordianStreamKeyType.class);
        myMap.put(GordianStreamKeyType.XSALSA20, STREAMKEY_XSALSA20);
        myMap.put(GordianStreamKeyType.SALSA20, STREAMKEY_SALSA20);
        myMap.put(GordianStreamKeyType.HC, STREAMKEY_HC);
        myMap.put(GordianStreamKeyType.CHACHA, STREAMKEY_CHACHA);
        myMap.put(GordianStreamKeyType.CHACHA7539, STREAMKEY_CHACHA7539);
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
    protected static TethysResourceId getKeyForStream(final GordianStreamKeyType pKeyType) {
        return TethysResourceBuilder.getKeyForEnum(STREAM_MAP, pKeyType);
    }
}
