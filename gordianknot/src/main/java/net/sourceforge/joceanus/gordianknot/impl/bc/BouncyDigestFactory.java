/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.gordianknot.impl.bc;

import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.gordianknot.api.digest.GordianDigestSpec;
import net.sourceforge.joceanus.gordianknot.api.digest.GordianDigestSubSpec.GordianDigestState;
import net.sourceforge.joceanus.gordianknot.api.digest.GordianDigestType;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.digest.GordianCoreDigestFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianDataException;
import net.sourceforge.joceanus.gordianknot.impl.ext.digests.GordianBlake2Base;
import net.sourceforge.joceanus.gordianknot.impl.ext.digests.GordianBlake2Xof;
import net.sourceforge.joceanus.gordianknot.impl.ext.digests.GordianBlake2bDigest;
import net.sourceforge.joceanus.gordianknot.impl.ext.digests.GordianBlake2sDigest;
import net.sourceforge.joceanus.gordianknot.impl.ext.digests.GordianBlake3Digest;
import net.sourceforge.joceanus.gordianknot.impl.ext.digests.GordianCubeHashDigest;
import net.sourceforge.joceanus.gordianknot.impl.ext.digests.GordianGroestlDigest;
import net.sourceforge.joceanus.gordianknot.impl.ext.digests.GordianJHDigest;
import net.sourceforge.joceanus.gordianknot.impl.ext.digests.GordianKangarooDigest.GordianKangarooBase;
import net.sourceforge.joceanus.gordianknot.impl.ext.digests.GordianKangarooDigest.GordianKangarooTwelve;
import net.sourceforge.joceanus.gordianknot.impl.ext.digests.GordianKangarooDigest.GordianMarsupilamiFourteen;
import net.sourceforge.joceanus.gordianknot.impl.ext.digests.GordianSkeinDigest;
import net.sourceforge.joceanus.gordianknot.impl.ext.digests.GordianSkeinXof;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Xof;
import org.bouncycastle.crypto.digests.AsconHash256;
import org.bouncycastle.crypto.digests.AsconXof128;
import org.bouncycastle.crypto.digests.DSTU7564Digest;
import org.bouncycastle.crypto.digests.GOST3411Digest;
import org.bouncycastle.crypto.digests.GOST3411_2012_256Digest;
import org.bouncycastle.crypto.digests.GOST3411_2012_512Digest;
import org.bouncycastle.crypto.digests.Haraka256Digest;
import org.bouncycastle.crypto.digests.Haraka512Digest;
import org.bouncycastle.crypto.digests.ISAPDigest;
import org.bouncycastle.crypto.digests.MD2Digest;
import org.bouncycastle.crypto.digests.MD4Digest;
import org.bouncycastle.crypto.digests.MD5Digest;
import org.bouncycastle.crypto.digests.PhotonBeetleDigest;
import org.bouncycastle.crypto.digests.RIPEMD128Digest;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.bouncycastle.crypto.digests.RIPEMD256Digest;
import org.bouncycastle.crypto.digests.RIPEMD320Digest;
import org.bouncycastle.crypto.digests.RomulusDigest;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.digests.SHA224Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA384Digest;
import org.bouncycastle.crypto.digests.SHA3Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.digests.SHA512tDigest;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.crypto.digests.SparkleDigest;
import org.bouncycastle.crypto.digests.SparkleDigest.SparkleParameters;
import org.bouncycastle.crypto.digests.TigerDigest;
import org.bouncycastle.crypto.digests.WhirlpoolDigest;
import org.bouncycastle.crypto.digests.XoodyakDigest;

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
    public BouncyDigest createDigest(final GordianDigestSpec pDigestSpec) throws GordianException {
        /* Check validity of DigestSpec */
        checkDigestSpec(pDigestSpec);

        /* Create digest */
        final Digest myBCDigest = getBCDigest(pDigestSpec);
        return myBCDigest instanceof Xof myXof
                ? new BouncyDigestXof(pDigestSpec, myXof)
                : new BouncyDigest(pDigestSpec, myBCDigest);
    }

    /**
     * Create the BouncyCastle digest.
     *
     * @param pDigestSpec the digestSpec
     * @return the digest
     * @throws GordianException on error
     */
    private static Digest getBCDigest(final GordianDigestSpec pDigestSpec) throws GordianException {
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
                return pDigestSpec.isXofMode()
                    ? getSkeinXof(pDigestSpec.getDigestState())
                    : getSkeinDigest(pDigestSpec.getDigestState(), myLen);
            case SHA3:
                return getSHA3Digest(myLen);
            case SHAKE:
                return new SHAKEDigest(pDigestSpec.getDigestState().getLength().getLength());
            case KANGAROO:
                return getKangarooDigest(pDigestSpec);
            case HARAKA:
                return getHarakaDigest(pDigestSpec);
            case BLAKE2:
                return pDigestSpec.isXofMode()
                        ? getBlake2Xof(pDigestSpec)
                        : getBlake2Digest(pDigestSpec);
            case BLAKE3:
                return new GordianBlake3Digest(myLen.getByteLength());
            case STREEBOG:
                return getStreebogDigest(myLen);
            case KUPYNA:
                return getKupynaDigest(myLen);
            case GROESTL:
                return new GordianGroestlDigest(myLen.getLength());
            case JH:
                return new GordianJHDigest(myLen.getLength());
            case CUBEHASH:
                return new GordianCubeHashDigest(myLen.getLength());
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
            case ASCON:
                return getAsconDigest(pDigestSpec);
            case ISAP:
                return new ISAPDigest();
            case PHOTONBEETLE:
                return new PhotonBeetleDigest();
            case ROMULUS:
                return new RomulusDigest();
            case SPARKLE:
                return getSparkleDigest(pDigestSpec);
            case XOODYAK:
                return new XoodyakDigest();
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
    static GordianBlake2Base getBlake2Digest(final GordianDigestSpec pSpec) {
        final int myLength = pSpec.getDigestLength().getLength();
        return pSpec.getDigestState().isBlake2bState()
               ? new GordianBlake2bDigest(myLength)
               : new GordianBlake2sDigest(myLength);
    }

    /**
     * Create the BouncyCastle Blake2Xof digest.
     *
     * @param pSpec the digest spec
     * @return the digest
     */
    static GordianBlake2Xof getBlake2Xof(final GordianDigestSpec pSpec) {
        final GordianDigestState myState = pSpec.getDigestState();
        final int myLength = pSpec.getDigestLength().getLength();
        return myState.isBlake2bState()
                ? new GordianBlake2Xof(new GordianBlake2bDigest(myLength))
                : new GordianBlake2Xof(new GordianBlake2sDigest(myLength));
    }

    /**
     * Create the BouncyCastle Kangaroo digest.
     *
     * @param pSpec the digest spec
     * @return the digest
     */
    private static GordianKangarooBase getKangarooDigest(final GordianDigestSpec pSpec) {
        final int myLength = pSpec.getDigestLength().getByteLength();
        return GordianDigestState.STATE128.equals(pSpec.getDigestState())
               ? new GordianKangarooTwelve(myLength)
               : new GordianMarsupilamiFourteen(myLength);
    }

    /**
     * Create the BouncyCastle Haraka digest.
     *
     * @param pSpec the digest spec
     * @return the digest
     */
    private static Digest getHarakaDigest(final GordianDigestSpec pSpec) {
        return GordianDigestState.STATE256.equals(pSpec.getDigestState())
               ? new Haraka256Digest()
               : new Haraka512Digest();
    }

    /**
     * Create the BouncyCastle Ascon digest.
     *
     * @param pSpec the digest spec
     * @return the digest
     */
    private static Digest getAsconDigest(final GordianDigestSpec pSpec) {
        return pSpec.isXofMode() ? new AsconXof128()
                                 : new AsconHash256();
    }

    /**
     * Create the BouncyCastle Sparkle digest.
     *
     * @param pSpec the digest spec
     * @return the digest
     */
    private static Digest getSparkleDigest(final GordianDigestSpec pSpec) {
        return GordianLength.LEN_256 == pSpec.getDigestLength()
                ? new SparkleDigest(SparkleParameters.ESCH256)
                : new SparkleDigest(SparkleParameters.ESCH384);
    }

    /**
     * Create the BouncyCastle SHA2 digest.
     *
     * @param pSpec the digestSpec
     * @return the digest
     */
    private static Digest getSHA2Digest(final GordianDigestSpec pSpec) {
        final GordianLength myLen = pSpec.getDigestLength();
        final GordianDigestState myState = pSpec.getDigestState();
        switch (myLen) {
            case LEN_224:
                return GordianDigestState.STATE256.equals(myState)
                       ? new SHA224Digest()
                       : new SHA512tDigest(myLen.getLength());
            case LEN_256:
                return GordianDigestState.STATE256.equals(myState)
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
     * @param pState the state
     * @param pLength the digest length
     * @return the digest
     */
    private static Digest getSkeinDigest(final GordianDigestState pState,
                                         final GordianLength pLength) {
        return new GordianSkeinDigest(pState.getLength().getLength(), pLength.getLength());
    }

    /**
     * Create the BouncyCastle skeinXof.
     *
     * @param pState the state
     * @return the digest
     */
    private static Digest getSkeinXof(final GordianDigestState pState) {
        final int myLength = pState.getLength().getLength();
        return new GordianSkeinXof(new GordianSkeinDigest(myLength, myLength));
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
