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

/**
 * Named Elliptic Curves.
 */
public enum GordianElliptic {
    /**
     * sect571k1.
     */
    SECT571K1("sect571k1", 571),

    /**
     * sect571r1.
     */
    SECT571R1("sect571r1", 571),

    /**
     * secp521r1.
     */
    SECP521R1("secp521r1", 521),

    /**
     * sect409k1.
     */
    SECT409K1("sect409k1", 409),

    /**
     * sect409r1.
     */
    SECT409R1("sect409r1", 409),

    /**
     * secp384r1.
     */
    SECP384R1("secp384r1", 384),

    /**
     * sect283k1.
     */
    SECT283K1("sect283k1", 283),

    /**
     * sect283r1.
     */
    SECT283R1("sect283r1", 283),

    /**
     * secp256k1.
     */
    SECP256K1("secp256k1", 256),

    /**
     * secp256r1.
     */
    SECP256R1("secp256r1", 256),

    /**
     * sect239k1.
     */
    SECT239K1("sect239k1", 239),

    /**
     * sect233k1.
     */
    SECT233K1("sect233k1", 233),

    /**
     * sect233r1.
     */
    SECT233R1("sect233r1", 233),

    /**
     * secp224k1.
     */
    SECP224K1("secp224k1", 224),

    /**
     * secp224r1.
     */
    SECP224R1("secp224r1", 224),

    /**
     * sect193r1.
     */
    SECT193R1("sect193r1", 193),

    /**
     * sect193r2.
     */
    SECT193R2("sect193r2", 193),

    /**
     * secp192k1.
     */
    SECP192K1("secp192k1", 192),

    /**
     * secp192r1.
     */
    SECP192R1("secp192r1", 192),

    /**
     * sect163k1.
     */
    SECT163K1("sect163k1", 163),

    /**
     * secp160r1.
     */
    SECT163R1("sect163r1", 163),

    /**
     * sect163r2.
     */
    SECT163R2("sect163r2", 163),

    /**
     * secp160k1.
     */
    SECP160K1("secp160k1", 160),

    /**
     * secp160r1.
     */
    SECP160R1("secp160r1", 160),

    /**
     * secp160r2.
     */
    SECP160R2("secp160r2", 160),

    /**
     * secp131r1.
     */
    SECT131R1("sect131r1", 131),

    /**
     * sect131r2.
     */
    SECT131R2("sect131r2", 131),

    /**
     * secp128r1.
     */
    SECP128R1("secp128r1", 128),

    /**
     * secp128r2.
     */
    SECP128R2("secp128r2", 127),

    /**
     * sect113r1.
     */
    SECT113R1("sect113r1", 113),

    /**
     * sect113r2.
     */
    SECT113R2("sect113r2", 113),

    /**
     * secp112r1.
     */
    SECP112R1("secp112r1", 112),

    /**
     * secp112r2.
     */
    SECP112R2("secp112r2", 112),

    /**
     * sm2p256v1.
     */
    SM2P256V1("sm2p256v1", 256),

    /**
     * wapip192v1.
     */
    WAPIP192V1("wapip192v1", 192),

    /**
     * prime256v1.
     */
    PRIME256V1("prime256v1", 256),

    /**
     * prime239v1.
     */
    PRIME239V1("prime239v1", 239),

    /**
     * prime239v2.
     */
    PRIME239V2("prime239v2", 239),

    /**
     * prime239v3.
     */
    PRIME239V3("prime239v3", 239),

    /**
     * prime192v1.
     */
    PRIME192V1("prime192v1", 192),

    /**
     * prime192v2.
     */
    PRIME192V2("prime192v2", 192),

    /**
     * prime192v3.
     */
    PRIME192V3("prime192v3", 192),

    /**
     * c2tnb431r1.
     */
    C2TNB431R1("c2tnb431r1", 431),

    /**
     * c2pnb368w1.
     */
    C2PNB368W1("c2pnb368w1", 368),

    /**
     * c2tnb359v1.
     */
    C2TNB359V1("c2tnb359v1", 359),

    /**
     * c2pnb304w1.
     */
    C2PNB304W1("c2pnb304w1", 304),

    /**
     * c2pnb272w1.
     */
    C2PNB272W1("c2pnb272w1", 272),

    /**
     * c2tnb239v1.
     */
    C2TNB239V1("c2tnb239v1", 239),

    /**
     * c2tnb239v2.
     */
    C2TNB239V2("c2tnb239v2", 239),

    /**
     * c2tnb239v3.
     */
    C2TNB239V3("c2tnb239v3", 239),

    /**
     * c2pnb208w1.
     */
    C2PNB208W1("c2pnb208w1", 208),

    /**
     * c2tnb191v1.
     */
    C2TNB191V1("c2tnb191v1", 191),

    /**
     * c2tnb191r2.
     */
    C2TNB191V2("c2tnb191v2", 191),

    /**
     * c2tnb191v3.
     */
    C2TNB191V3("c2tnb191v3", 191),

    /**
     * c2pnb176w1.
     */
    C2PNB176W1("c2pnb176w1", 176),

    /**
     * c2pnb163v1.
     */
    C2PNB163V1("c2pnb163v1", 163),

    /**
     * c2pnb163v2.
     */
    C2PNB163V2("c2pnb163v2", 163),

    /**
     * c2pnb163v3.
     */
    C2PNB163V3("c2pnb163v3", 163),

    /**
     * brainpoolp512r1.
     */
    BRAINPOOLP512R1("brainpoolp512r1", 512),

    /**
     * brainpoolp512t1.
     */
    BRAINPOOLP512T1("brainpoolp512t1", 512),

    /**
     * brainpoolp384r1.
     */
    BRAINPOOLP384R1("brainpoolp384r1", 384),

    /**
     * brainpoolt384t1.
     */
    BRAINPOOLP384T1("brainpoolp384t1", 384),

    /**
     * brainpoolp320r1.
     */
    BRAINPOOLP320R1("brainpoolp320r1", 320),

    /**
     * brainpoolp320t1.
     */
    BRAINPOOLP320T1("brainpoolp320t1", 320),

    /**
     * brainpoolp256r1.
     */
    BRAINPOOLP256R1("brainpoolp256r1", 256),

    /**
     * brainpoolp256t1.
     */
    BRAINPOOLP256T1("brainpoolp256t1", 256),

    /**
     * brainpoolp224r1.
     */
    BRAINPOOLP224R1("brainpoolp224r1", 224),

    /**
     * brainpoolp224t1.
     */
    BRAINPOOLP224T1("brainpoolp224t1", 224),

    /**
     * brainpoolp192r1.
     */
    BRAINPOOLP192R1("brainpoolp192r1", 192),

    /**
     * brainpoolp192t1.
     */
    BRAINPOOLP192T1("brainpoolp192t1", 192),

    /**
     * brainpoolp160r1.
     */
    BRAINPOOLP160R1("brainpoolp160r1", 160),

    /**
     * brainpoolp160t1.
     */
    BRAINPOOLP160T1("brainpoolp160t1", 160);

    /**
     * The curve name.
     */
    private final String theName;

    /**
     * The key size.
     */
    private final int theSize;

    /**
     * Constructor.
     * @param pName the name of the curve
     * @param pSize the bitSize of the curve
     */
    GordianElliptic(final String pName,
                    final int pSize) {
        theName = pName;
        theSize = pSize;
    }

    /**
     * Obtain the name of the curve.
     * @return the name
     */
    public String getCurveName() {
        return theName;
    }

    /**
     * Obtain the bitSize of the curve.
     * @return the size
     */
    public int getKeySize() {
        return theSize;
    }

    @Override
    public String toString() {
        return theName;
    }
}
