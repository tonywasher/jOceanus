/*******************************************************************************
 * jGordianKnot: Security Suite
 * Copyright 2012,2014 Tony Washer
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
 * $URL: http://localhost/svn/Finance/jOceanus/trunk/jgordianknot/jgordianknot-core/src/main/java/net/sourceforge/joceanus/jgordianknot/crypto/CryptoResource.java $
 * $Revision: 647 $
 * $Author: Tony $
 * $Date: 2015-11-04 08:58:02 +0000 (Wed, 04 Nov 2015) $
 ******************************************************************************/
package net.sourceforge.joceanus.jgordianknot.crypto;

import net.sourceforge.joceanus.jgordianknot.GordianCryptoException;
import net.sourceforge.joceanus.jtethys.resource.ResourceBuilder;
import net.sourceforge.joceanus.jtethys.resource.ResourceId;

/**
 * Resource IDs for Cryptographic package.
 */
public enum CryptoResource implements ResourceId {
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
     * Digest GOST.
     */
    DIGEST_GOST("digest.GOST"),

    /**
     * Digest KECCAK.
     */
    DIGEST_KECCAK("digest.KECCAK"),

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
     * MAC HMAC.
     */
    MAC_HMAC("mac.HMAC"),

    /**
     * MAC GMAC.
     */
    MAC_GMAC("mac.GMAC"),

    /**
     * MAC POLY1305.
     */
    MAC_POLY("mac.POLY1305"),

    /**
     * MAC SKEIN.
     */
    MAC_SKEIN("mac.SKEIN"),

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
    STREAMKEY_GRAIN("streamKey.GRAIN");

    /**
     * The Resource Builder.
     */
    private static final ResourceBuilder BUILDER = ResourceBuilder.getPackageResourceBuilder(GordianCryptoException.class.getCanonicalName());

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
    CryptoResource(final String pKeyName) {
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
     * Obtain key for Factory.
     * @param pFactoryType the factoryType
     * @return the resource key
     */
    protected static ResourceId getKeyForFactoryType(final GordianFactoryType pFactoryType) {
        switch (pFactoryType) {
            case BC:
                return FACTORY_BC;
            case JCA:
                return FACTORY_JCA;
            default:
                throw new IllegalArgumentException(ResourceBuilder.getErrorNoResource(pFactoryType));
        }
    }

    /**
     * Obtain key for Digest.
     * @param pDigest the DigestType
     * @return the resource key
     */
    protected static ResourceId getKeyForDigest(final GordianDigestType pDigest) {
        switch (pDigest) {
            case SHA2:
                return DIGEST_SHA2;
            case TIGER:
                return DIGEST_TIGER;
            case WHIRLPOOL:
                return DIGEST_WHIRLPOOL;
            case RIPEMD:
                return DIGEST_RIPEMD;
            case GOST:
                return DIGEST_GOST;
            case KECCAK:
                return DIGEST_KECCAK;
            case SKEIN:
                return DIGEST_SKEIN;
            case SM3:
                return DIGEST_SM3;
            case BLAKE:
                return DIGEST_BLAKE;
            default:
                throw new IllegalArgumentException(ResourceBuilder.getErrorNoResource(pDigest));
        }
    }

    /**
     * Obtain key for HMac.
     * @param pMac the MacType
     * @return the resource key
     */
    protected static ResourceId getKeyForMac(final GordianMacType pMac) {
        switch (pMac) {
            case HMAC:
                return MAC_HMAC;
            case GMAC:
                return MAC_GMAC;
            case POLY1305:
                return MAC_POLY;
            case SKEIN:
                return MAC_SKEIN;
            case VMPC:
                return MAC_VMPC;
            default:
                throw new IllegalArgumentException(ResourceBuilder.getErrorNoResource(pMac));
        }
    }

    /**
     * Obtain key for SymKey.
     * @param pKeyType the keyType
     * @return the resource key
     */
    protected static ResourceId getKeyForSym(final GordianSymKeyType pKeyType) {
        switch (pKeyType) {
            case AES:
                return SYMKEY_AES;
            case TWOFISH:
                return SYMKEY_TWOFISH;
            case SERPENT:
                return SYMKEY_SERPENT;
            case CAMELLIA:
                return SYMKEY_CAMELLIA;
            case CAST6:
                return SYMKEY_CAST6;
            case RC6:
                return SYMKEY_RC6;
            case THREEFISH:
                return SYMKEY_THREEFISH;
            case NOEKEON:
                return SYMKEY_NOEKEON;
            case SEED:
                return SYMKEY_SEED;
            case SM4:
                return SYMKEY_SM4;
            default:
                throw new IllegalArgumentException(ResourceBuilder.getErrorNoResource(pKeyType));
        }
    }

    /**
     * Obtain key for StreamKey.
     * @param pKeyType the keyType
     * @return the resource key
     */
    protected static ResourceId getKeyForStream(final GordianStreamKeyType pKeyType) {
        switch (pKeyType) {
            case XSALSA20:
                return STREAMKEY_XSALSA20;
            case SALSA20:
                return STREAMKEY_SALSA20;
            case HC:
                return STREAMKEY_HC;
            case CHACHA:
                return STREAMKEY_CHACHA;
            case VMPC:
                return STREAMKEY_VMPC;
            case ISAAC:
                return STREAMKEY_ISAAC;
            case GRAIN:
                return STREAMKEY_GRAIN;
            default:
                throw new IllegalArgumentException(ResourceBuilder.getErrorNoResource(pKeyType));
        }
    }
}
