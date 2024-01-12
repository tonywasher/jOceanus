/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2023 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.impl.core.base;

import java.security.SecureRandom;
import java.util.Arrays;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactoryType;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.TethysDataConverter;

/**
 * Security Parameters.
 */
public class GordianParameters {
    /**
     * Seed length.
     */
    public static final int SEED_LEN = GordianLength.LEN_256.getByteLength();

    /**
     * Default Factory.
     */
    public static final GordianFactoryType DEFAULT_FACTORY = GordianFactoryType.BC;

    /**
     * The Factory Type.
     */
    private GordianFactoryType theFactoryType;

    /**
     * The Security seed.
     */
    private byte[] theSecuritySeed;

    /**
     * The Security phrase.
     */
    private byte[] theKeySetSeed;

    /**
     * Is this an internal set?
     */
    private boolean isInternal;

    /**
     * Default Constructor.
     */
    public GordianParameters() {
        this(DEFAULT_FACTORY);
    }

    /**
     * Constructor.
     * @param pFactoryType the factory type
     */
    public GordianParameters(final GordianFactoryType pFactoryType) {
        /* Store parameters */
        theFactoryType = pFactoryType;
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
     * Is this an internal set of parameters.
     * @return true/false
     */
    public boolean isInternal() {
        return isInternal;
    }

    /**
     * Set factory type.
     * @param pType the factory type
     */
    public void setFactoryType(final GordianFactoryType pType) {
        theFactoryType = pType;
    }

    /**
     * Set security phrase.
     * @param pSecurityPhrase the security phrase (or null)
     * @throws OceanusException on error
     */
    public void setSecurityPhrase(final char[] pSecurityPhrase) throws OceanusException {
        theSecuritySeed = pSecurityPhrase == null
                                ? null
                                : TethysDataConverter.charsToByteArray(pSecurityPhrase);
    }

    /**
     * Set security seed.
     * @param pSecuritySeed the security seed (or null)
     */
    public void setSecuritySeed(final byte[] pSecuritySeed) {
        theSecuritySeed = pSecuritySeed == null
                            ? null
                            : Arrays.copyOf(pSecuritySeed, pSecuritySeed.length);
    }

    /**
     * Set security seed.
     * @param pSecuritySeed the security seed
     * @param pKeySetSeed the keySet seed
     */
    public void setSecuritySeeds(final byte[] pSecuritySeed,
                                 final byte[] pKeySetSeed) {
        theSecuritySeed = Arrays.copyOf(pSecuritySeed, pSecuritySeed.length);
        Arrays.fill(pSecuritySeed, (byte) 0);
        theKeySetSeed = Arrays.copyOf(pKeySetSeed, pKeySetSeed.length);
        Arrays.fill(pKeySetSeed, (byte) 0);
    }

    /**
     * Set random security seeds.
     * @param pRandom the secureRandom
     */
    public void setSecuritySeeds(final SecureRandom pRandom) {
        theSecuritySeed = new byte[SEED_LEN];
        pRandom.nextBytes(theSecuritySeed);
        theKeySetSeed = new byte[SEED_LEN];
        pRandom.nextBytes(theKeySetSeed);
    }

    /**
     * Renew keySet.
     * @param pRandom the secureRandom
     */
    public void renewKeySet(final SecureRandom pRandom) {
        pRandom.nextBytes(theKeySetSeed);
    }

    /**
     * Set internal.
     */
    public void setInternal() {
        isInternal = true;
    }

    /**
     * Validate the Parameters.
     * @return valid true/false
     */
    public boolean validate() {
        /* If there is a keySetSeed */
        if (theKeySetSeed != null) {
            /* it must be of length SEED_LEN */
            if (theKeySetSeed.length != SEED_LEN) {
                return false;
            }

            /* It must be of equal length to SecuritySeed */
            if (theSecuritySeed == null || theSecuritySeed.length != SEED_LEN) {
                return false;
            }

            /* Factory type must be BC and it must be internal */
            return theFactoryType == GordianFactoryType.BC
                   && isInternal;
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
        /* Access multiplier */
        final int myPrime = GordianCoreFactory.HASH_PRIME;

        /* Calculate hash from types */
        int myCode = theFactoryType.hashCode();
        if (isInternal) {
            myCode++;
        }
        myCode *= myPrime;

        /* Calculate hash from seeds */
        return myCode + Arrays.hashCode(theSecuritySeed) + Arrays.hashCode(theKeySetSeed);
    }
}
