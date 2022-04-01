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
package net.sourceforge.joceanus.jgordianknot.impl.bc;

import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.DSTU7564Digest;
import org.bouncycastle.crypto.digests.GOST3411Digest;
import org.bouncycastle.crypto.digests.GOST3411_2012_256Digest;
import org.bouncycastle.crypto.digests.GOST3411_2012_512Digest;
import org.bouncycastle.crypto.digests.Haraka256Digest;
import org.bouncycastle.crypto.digests.Haraka512Digest;
import org.bouncycastle.crypto.digests.MD2Digest;
import org.bouncycastle.crypto.digests.MD4Digest;
import org.bouncycastle.crypto.digests.MD5Digest;
import org.bouncycastle.crypto.digests.RIPEMD128Digest;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.bouncycastle.crypto.digests.RIPEMD256Digest;
import org.bouncycastle.crypto.digests.RIPEMD320Digest;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.digests.SHA224Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA384Digest;
import org.bouncycastle.crypto.digests.SHA3Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.digests.SHA512tDigest;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.crypto.digests.TigerDigest;
import org.bouncycastle.crypto.digests.WhirlpoolDigest;
import org.bouncycastle.crypto.ext.digests.Blake2;
import org.bouncycastle.crypto.ext.digests.Blake2b;
import org.bouncycastle.crypto.ext.digests.Blake2s;
import org.bouncycastle.crypto.ext.digests.Blake3Digest;
import org.bouncycastle.crypto.ext.digests.CubeHashDigest;
import org.bouncycastle.crypto.ext.digests.GroestlDigest;
import org.bouncycastle.crypto.ext.digests.JHDigest;
import org.bouncycastle.crypto.ext.digests.Kangaroo.KangarooBase;
import org.bouncycastle.crypto.ext.digests.Kangaroo.KangarooTwelve;
import org.bouncycastle.crypto.ext.digests.Kangaroo.MarsupilamiFourteen;
import org.bouncycastle.crypto.ext.digests.SkeinDigest;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestSpec;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestType;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.digest.GordianCoreDigestFactory;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * BouncyCastle Digest Factory.
 */
public class BouncyDigestFactory
        extends GordianCoreDigestFactory {
    /**
     * Constructor.
     *
     * @param pFactory the factory
     */
    BouncyDigestFactory(final GordianCoreFactory pFactory) {
        /* Initialise underlying class */
        super(pFactory);
    }

    @Override
    public BouncyFactory getFactory() {
        return (BouncyFactory) super.getFactory();
    }

    @Override
    public BouncyDigest createDigest(final GordianDigestSpec pDigestSpec) throws OceanusException {
        /* Check validity of DigestSpec */
        checkDigestSpec(pDigestSpec);

        /* Create digest */
        final Digest myBCDigest = getBCDigest(pDigestSpec);
        return new BouncyDigest(pDigestSpec, myBCDigest);
    }

    /**
     * Create the BouncyCastle digest.
     *
     * @param pDigestSpec the digestSpec
     * @return the digest
     * @throws OceanusException on error
     */
    private static Digest getBCDigest(final GordianDigestSpec pDigestSpec) throws OceanusException {
        /* Access digest details */
        final GordianDigestType myType = pDigestSpec.getDigestType();
        final GordianLength myLen = pDigestSpec.getDigestLength();

        /* Switch on digest type */
        switch (myType) {
            case SHA2:
                return getSHA2Digest(pDigestSpec);
            case RIPEMD:
                return getRIPEMDDigest(myLen);
            case SKEIN:
                return getSkeinDigest(pDigestSpec.getStateLength(), myLen);
            case SHA3:
                return getSHA3Digest(myLen);
            case SHAKE:
                return new SHAKEDigest(pDigestSpec.getStateLength().getLength());
            case KANGAROO:
                return getKangarooDigest(pDigestSpec);
            case HARAKA:
                return getHarakaDigest(pDigestSpec);
            case BLAKE2:
                return getBlake2Digest(pDigestSpec);
            case BLAKE3:
                return new Blake3Digest(myLen.getByteLength());
            case STREEBOG:
                return getStreebogDigest(myLen);
            case KUPYNA:
                return getKupynaDigest(myLen);
            case GROESTL:
                return new GroestlDigest(myLen.getLength());
            case JH:
                return new JHDigest(myLen.getLength());
            case CUBEHASH:
                return new CubeHashDigest(myLen.getLength());
            case GOST:
                return new GOST3411Digest();
            case TIGER:
                return new TigerDigest();
            case WHIRLPOOL:
                return new WhirlpoolDigest();
            case SM3:
                return new SM3Digest();
            case SHA1:
                return new SHA1Digest();
            case MD5:
                return new MD5Digest();
            case MD4:
                return new MD4Digest();
            case MD2:
                return new MD2Digest();
            default:
                throw new GordianDataException(GordianCoreFactory.getInvalidText(pDigestSpec.toString()));
        }
    }

    /**
     * Create the BouncyCastle RIPEMD digest.
     *
     * @param pLength the digest length
     * @return the digest
     */
    private static Digest getRIPEMDDigest(final GordianLength pLength) {
        switch (pLength) {
            case LEN_128:
                return new RIPEMD128Digest();
            case LEN_160:
                return new RIPEMD160Digest();
            case LEN_256:
                return new RIPEMD256Digest();
            case LEN_320:
            default:
                return new RIPEMD320Digest();
        }
    }

    /**
     * Create the BouncyCastle Blake2 digest.
     *
     * @param pSpec the digest spec
     * @return the digest
     */
    static Blake2 getBlake2Digest(final GordianDigestSpec pSpec) {
        final int myLength = pSpec.getDigestLength().getLength();
        return GordianDigestType.isBlake2bState(pSpec.getStateLength())
               ? new Blake2b(myLength)
               : new Blake2s(myLength);
    }

    /**
     * Create the BouncyCastle Kangaroo digest.
     *
     * @param pSpec the digest spec
     * @return the digest
     */
    private static KangarooBase getKangarooDigest(final GordianDigestSpec pSpec) {
        final int myLength = pSpec.getDigestLength().getByteLength();
        return GordianLength.LEN_128 == pSpec.getStateLength()
               ? new KangarooTwelve(myLength)
               : new MarsupilamiFourteen(myLength);
    }

    /**
     * Create the BouncyCastle Haraka digest.
     *
     * @param pSpec the digest spec
     * @return the digest
     */
    private static Digest getHarakaDigest(final GordianDigestSpec pSpec) {
        return GordianLength.LEN_256 == pSpec.getStateLength()
               ? new Haraka256Digest()
               : new Haraka512Digest();
    }

    /**
     * Create the BouncyCastle SHA2 digest.
     *
     * @param pSpec the digestSpec
     * @return the digest
     */
    private static Digest getSHA2Digest(final GordianDigestSpec pSpec) {
        final GordianLength myLen = pSpec.getDigestLength();
        final GordianLength myState = pSpec.getStateLength();
        switch (myLen) {
            case LEN_224:
                return myState == null
                       ? new SHA224Digest()
                       : new SHA512tDigest(myLen.getLength());
            case LEN_256:
                return myState == null
                       ? new SHA256Digest()
                       : new SHA512tDigest(myLen.getLength());
            case LEN_384:
                return new SHA384Digest();
            case LEN_512:
            default:
                return new SHA512Digest();
        }
    }

    /**
     * Create the BouncyCastle SHA3 digest.
     *
     * @param pLength the digest length
     * @return the digest
     */
    private static Digest getSHA3Digest(final GordianLength pLength) {
        return new SHA3Digest(pLength.getLength());
    }

    /**
     * Create the BouncyCastle Kupyna digest.
     *
     * @param pLength the digest length
     * @return the digest
     */
    private static Digest getKupynaDigest(final GordianLength pLength) {
        return new DSTU7564Digest(pLength.getLength());
    }

    /**
     * Create the BouncyCastle skeinDigest.
     *
     * @param pStateLength the state length
     * @param pLength      the digest length
     * @return the digest
     */
    private static Digest getSkeinDigest(final GordianLength pStateLength,
                                         final GordianLength pLength) {
        return new SkeinDigest(pStateLength.getLength(), pLength.getLength());
    }

    /**
     * Create the BouncyCastle Streebog digest.
     *
     * @param pLength the digest length
     * @return the digest
     */
    private static Digest getStreebogDigest(final GordianLength pLength) {
        return GordianLength.LEN_256.equals(pLength)
               ? new GOST3411_2012_256Digest()
               : new GOST3411_2012_512Digest();
    }
}
