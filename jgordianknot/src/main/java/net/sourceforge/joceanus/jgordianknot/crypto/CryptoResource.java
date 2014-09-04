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
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jgordianknot.crypto;

import net.sourceforge.joceanus.jgordianknot.JGordianCryptoException;
import net.sourceforge.joceanus.jtethys.resource.ResourceMgr;
import net.sourceforge.joceanus.jtethys.resource.ResourceMgr.ResourceId;

/**
 * Resource IDs for Cryptographic package.
 */
public enum CryptoResource implements ResourceId {
    /**
     * Provider BC.
     */
    PROVIDER_BC("provider.BC"),

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
     * Digest SHA3.
     */
    DIGEST_SHA3("digest.SHA3"),

    /**
     * Digest Skein.
     */
    DIGEST_SKEIN("digest.SKEIN"),

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
     * StreamKey CHACHA.
     */
    STREAMKEY_XSALSA20("streamKey.XSALSA20"),

    /**
     * StreamKey HC256.
     */
    STREAMKEY_HC256("streamKey.HC256"),

    /**
     * StreamKey CHACHA.
     */
    STREAMKEY_CHACHA("streamKey.CHACHA"),

    /**
     * StreamKey VMPC.
     */
    STREAMKEY_VMPC("streamKey.VMPC"),

    /**
     * Label Password.
     */
    LABEL_PASSWORD("label.password"),

    /**
     * Label Confirm.
     */
    LABEL_CONFIRM("label.confirm"),

    /**
     * Button OK.
     */
    BUTTON_OK("button.ok"),

    /**
     * Button Cancel.
     */
    BUTTON_CANCEL("button.cancel"),

    /**
     * Title for password.
     */
    TITLE_PASSWORD("title.password"),

    /**
     * Title for new password.
     */
    TITLE_NEWPASS("title.newPassword"),

    /**
     * Title for error.
     */
    TITLE_ERROR("title.error"),

    /**
     * Error Bad Password.
     */
    ERROR_BADPASS("error.badPassword"),

    /**
     * Error Confirm.
     */
    ERROR_CONFIRM("error.confirm"),

    /**
     * Error length 1.
     */
    ERROR_LENGTH1("error.length1"),

    /**
     * Error length 2.
     */
    ERROR_LENGTH2("error.length2");

    /**
     * The Bundle name.
     */
    private final static String BUNDLE_NAME = ResourceMgr.getPackageBundle(JGordianCryptoException.class.getCanonicalName());

    /**
     * The Id.
     */
    private final String theKeyName;

    /**
     * Constructor.
     * @param pKeyName the key name
     */
    private CryptoResource(final String pKeyName) {
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
    public String getBundleName() {
        return BUNDLE_NAME;
    }

    /**
     * Obtain key for Provider.
     * @param pProvider the Provider
     * @return the resource key
     */
    protected static CryptoResource getKeyForProvider(final SecurityProvider pProvider) {
        switch (pProvider) {
            case BC:
                return PROVIDER_BC;
            default:
                return null;
        }
    }

    /**
     * Obtain key for Digest.
     * @param pDigest the DigestType
     * @return the resource key
     */
    protected static CryptoResource getKeyForDigest(final DigestType pDigest) {
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
            case SHA3:
                return DIGEST_SHA3;
            case SKEIN:
                return DIGEST_SKEIN;
            default:
                return null;
        }
    }

    /**
     * Obtain key for HMac.
     * @param pMac the MacType
     * @return the resource key
     */
    protected static CryptoResource getKeyForHMac(final MacType pMac) {
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
                return null;
        }
    }

    /**
     * Obtain key for SymKey.
     * @param pKeyType the keyType
     * @return the resource key
     */
    protected static CryptoResource getKeyForSym(final SymKeyType pKeyType) {
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
            default:
                return null;
        }
    }

    /**
     * Obtain key for StreamKey.
     * @param pKeyType the keyType
     * @return the resource key
     */
    protected static CryptoResource getKeyForStream(final StreamKeyType pKeyType) {
        switch (pKeyType) {
            case XSALSA20:
                return STREAMKEY_XSALSA20;
            case HC256:
                return STREAMKEY_HC256;
            case CHACHA:
                return STREAMKEY_CHACHA;
            case VMPC:
                return STREAMKEY_VMPC;
            default:
                return null;
        }
    }
}
