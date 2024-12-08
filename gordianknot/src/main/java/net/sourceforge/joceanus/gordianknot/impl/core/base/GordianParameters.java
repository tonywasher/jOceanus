/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.gordianknot.impl.core.base;

import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianFactoryType;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Objects;

/**
 * Security Parameters.
 */
public class GordianParameters {
    /**
     * Secret length.
     */
    public static final GordianLength SECRET_LEN = GordianLength.LEN_512;

    /**
     * Seed length.
     */
    public static final GordianLength SEED_LEN = GordianLength.LEN_1024;

    /**
     * Default Factory.
     */
    public static final GordianFactoryType DEFAULT_FACTORY = GordianFactoryType.BC;

    /**
     * FactoryTypeMask.
     */
    public static final byte FACTORY_MASK = (byte) 0x80;

    /**
     * The Factory Type.
     */
    private final GordianFactoryType theFactoryType;

    /**
     * The Security seed.
     */
    private final byte[] theSecuritySeed;

    /**
     * The Security phrase.
     */
    private final byte[] theKeySetSeed;

    /**
     * Is this an internal set?
     */
    private final boolean isInternal;

    /**
     * Constructor.
     * @param pFactoryType the factory type
     * @param pRandom the secureRandom
     */
    public GordianParameters(final GordianFactoryType pFactoryType,
                             final SecureRandom pRandom) {
        /* Store factory Type */
        theFactoryType = pFactoryType;

        /* Generate the security seeds */
        final int mySecretLen = SECRET_LEN.getByteLength();
        theSecuritySeed = new byte[mySecretLen];
        pRandom.nextBytes(theSecuritySeed);
        theKeySetSeed = new byte[mySecretLen];
        pRandom.nextBytes(theKeySetSeed);

        /* Adjust security seed according to factory type */
        adjustSecuritySeed();

        /* Note that this is internal */
        isInternal = true;
    }

    /**
     * Constructor.
     * @param pSecuritySeeds the security seeds
     */
    public GordianParameters(final byte[] pSecuritySeeds) {
        /* Split out the security seeds */
        final int mySecretLen = SECRET_LEN.getByteLength();
        theSecuritySeed = new byte[mySecretLen];
        System.arraycopy(pSecuritySeeds, 0, theSecuritySeed, 0, mySecretLen);
        theKeySetSeed = new byte[mySecretLen];
        System.arraycopy(pSecuritySeeds, mySecretLen, theKeySetSeed, 0, mySecretLen);
        Arrays.fill(pSecuritySeeds, (byte) 0);

        /* Determine the factory type */
        theFactoryType = (theSecuritySeed[0] & FACTORY_MASK) == FACTORY_MASK
                ? GordianFactoryType.BC : GordianFactoryType.JCA;

        /* Note that this is internal */
        isInternal = true;
    }

    /**
     * Constructor.
     * @param pFactoryType the factory type
     * @param pSecuritySeeds the security seeds
     */
    public GordianParameters(final GordianFactoryType pFactoryType,
                             final byte[] pSecuritySeeds) {
        /* Store the factory type */
        theFactoryType = pFactoryType;

        /* Split out the security seeds */
        final int mySecretLen = SECRET_LEN.getByteLength();
        theSecuritySeed = new byte[mySecretLen];
        System.arraycopy(pSecuritySeeds, 0, theSecuritySeed, 0, mySecretLen);
        theKeySetSeed = new byte[mySecretLen];
        System.arraycopy(pSecuritySeeds, mySecretLen, theKeySetSeed, 0, mySecretLen);
        Arrays.fill(pSecuritySeeds, (byte) 0);

        /* Adjust security seed according to factory type */
        adjustSecuritySeed();

        /* Note that this is internal */
        isInternal = true;
    }

    /**
     * Constructor.
     * @param pFactoryType the factory type
     * @param pSecurityPhrase the security phrase (or null)
     * @throws GordianException on error
     */
    public GordianParameters(final GordianFactoryType pFactoryType,
                             final char[] pSecurityPhrase) throws GordianException {
        /* Store factory Type */
        theFactoryType = pFactoryType;

        /* Store seeds */
        theSecuritySeed = pSecurityPhrase == null
                ? null
                : GordianDataConverter.charsToByteArray(pSecurityPhrase);
        theKeySetSeed = null;

        /* Note that this is not internal */
        isInternal = false;
    }

    /**
     * Access the factory type.
     * @return the factory type
     */
    public GordianFactoryType getFactoryType() {
        return theFactoryType;
    }

    /**
     * Access the security seed.
     * @return the security seed
     */
    public byte[] getSecuritySeed() {
        return theSecuritySeed;
    }

    /**
     * Access the keySet seed.
     * @return the keySet seed
     */
    public byte[] getKeySetSeed() {
        return theKeySetSeed;
    }

    /**
     * Obtain Security Seeds as single array.
     * @return the seeds
     */
    public byte[] getSecuritySeeds() {
        final int mySecretLen = SECRET_LEN.getByteLength();
        final byte[] myBuffer = new byte[mySecretLen << 1];
        System.arraycopy(theSecuritySeed, 0, myBuffer, 0, mySecretLen);
        System.arraycopy(theKeySetSeed, 0, myBuffer, mySecretLen, mySecretLen);
        return myBuffer;
    }

    /**
     * Is this an internal set of parameters.
     * @return true/false
     */
    public boolean isInternal() {
        return isInternal;
    }

    /**
     * Renew keySet.
     * @param pRandom the secureRandom
     */
    public void renewKeySet(final SecureRandom pRandom) {
        pRandom.nextBytes(theKeySetSeed);
    }

    /**
     * Adjust the securitySeed.
     */
    private void adjustSecuritySeed() {
        /* Adjust first byte of security seed according to factory type */
        if (GordianFactoryType.BC.equals(theFactoryType)) {
            theSecuritySeed[0] |= FACTORY_MASK;
        } else {
            theSecuritySeed[0] &= ~FACTORY_MASK;
        }
    }

    /**
     * Validate the Parameters.
     * @return valid true/false
     */
    public boolean validate() {
        /* If there is a keySetSeed */
        if (theKeySetSeed != null) {
            /* it must be of length SEED_LEN */
            if (theKeySetSeed.length != SECRET_LEN.getByteLength()) {
                return false;
            }

            /* It must be of equal length to SecuritySeed */
            if (theSecuritySeed == null || theSecuritySeed.length != SECRET_LEN.getByteLength()) {
                return false;
            }
        }

        /* Check factory type */
        return theFactoryType != null;
    }

    @Override
    public boolean equals(final Object pThat) {
        /* Handle the trivial cases */
        if (pThat == this) {
            return true;
        }
        if (pThat == null) {
            return false;
        }

        /* Make sure that the object is the same class */
        if (!(pThat instanceof GordianParameters)) {
            return false;
        }

        /* Access the target field */
        final GordianParameters myThat = (GordianParameters) pThat;

        /* Check Differences */
        if (theFactoryType != myThat.getFactoryType()
            || isInternal != myThat.isInternal()) {
            return false;
        }

        /* Check Differences */
        if (!Arrays.equals(theKeySetSeed, myThat.getKeySetSeed())) {
            return false;
        }

        /* Check seed */
        return Arrays.equals(theSecuritySeed, myThat.getSecuritySeed());
    }

    @Override
    public int hashCode() {
        return Objects.hash(theFactoryType, isInternal, Arrays.hashCode(theSecuritySeed), Arrays.hashCode(theKeySetSeed));
    }

    /**
     * Create random parameters.
     * @param pType the factory type
     * @return the parameters
     * @throws GordianException on error
     */
    public static GordianParameters randomParams(final GordianFactoryType pType) throws GordianException {
        return new GordianParameters(pType, GordianRandomSource.getStrongRandom());
    }
}
