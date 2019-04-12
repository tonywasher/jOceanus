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
package net.sourceforge.joceanus.jgordianknot.api.factory;

import java.util.Arrays;

import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.TethysDataConverter;

/**
 * Security Parameters.
 */
public class GordianParameters {
    /**
     * The Hash Prime.
     */
    public static final int HASH_PRIME = 37;

    /**
     * Default Factory.
     */
    public static final GordianFactoryType DEFAULT_FACTORY = GordianFactoryType.BC;

    /**
     * Minimum iterations.
     */
    public static final Integer MINIMUM_ITERATIONS = 1;

    /**
     * Maximum iterations.
     */
    public static final Integer MAXIMUM_ITERATIONS = 64;

    /**
     * Default iterations.
     */
    public static final Integer DEFAULT_ITERATIONS = 2;

    /**
     * 1K Multiplier.
     */
    public static final int K_MULTIPLIER = 1024;

    /**
     * Default Security Phrase.
     */
    private static final String DEFAULT_SECURITY_PHRASE = "PleaseChangeMeToSomethingMoreUnique";

    /**
     * The Factory Type.
     */
    private GordianFactoryType theFactoryType;

    /**
     * The Number of iterations (x 1K).
     */
    private int theKIterations;

    /**
     * The Security phrase.
     */
    private byte[] theSecurityPhrase;

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
        theKIterations = DEFAULT_ITERATIONS;
        theSecurityPhrase = null;
    }

    /**
     * Access the factory type.
     * @return the factory type
     */
    public GordianFactoryType getFactoryType() {
        return theFactoryType;
    }

    /**
     * Access the number of Iterations.
     * @return the number of iterations
     */
    public int getNumIterations() {
        return theKIterations * K_MULTIPLIER;
    }

    /**
     * Access the number of Hash Iterations (x 1K).
     * @return the number of hash iterations
     */
    public int getKIterations() {
        return theKIterations;
    }

    /**
     * Access the security phrase in bytes format.
     * @return the security phrase
     */
    public byte[] getSecurityPhrase() {
        return theSecurityPhrase;
    }

    /**
     * Set factory type.
     * @param pType the factory type
     */
    public void setFactoryType(final GordianFactoryType pType) {
        theFactoryType = pType;
    }

    /**
     * Set number of iterations.
     * @param pKIterations the number of iterations (x 1K)
     */
    public void setKIterations(final int pKIterations) {
        theKIterations = pKIterations;
    }

    /**
     * Set security phrase.
     * @param pSecurityPhrase the security phrase (or null)
     * @throws OceanusException on error
     */
    public void setSecurityPhrase(final char[] pSecurityPhrase) throws OceanusException {
        theSecurityPhrase = TethysDataConverter.charsToByteArray(pSecurityPhrase);
    }

    /**
     * Set security phrase.
     * @param pSecurityPhrase the security phrase (or null)
     */
    public void setSecurityPhrase(final byte[] pSecurityPhrase) {
        theSecurityPhrase = pSecurityPhrase;
    }

    /**
     * Get default security phrase.
     * @return the default security phrase
     */
    public static char[] getDefaultSecurityPhrase() {
        return DEFAULT_SECURITY_PHRASE.toCharArray();
    }

    /**
     * Validate the Parameters.
     * @return valid true/false
     */
    public boolean validate() {
        /* Check factory type */
        if (theFactoryType == null) {
            return false;
        }

        /* Check iterations is in range */
        return (theKIterations >= MINIMUM_ITERATIONS
                && theKIterations <= MAXIMUM_ITERATIONS);
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
        if (theKIterations != myThat.getKIterations()) {
            return false;
        }
        if (theFactoryType != myThat.getFactoryType()) {
            return false;
        }

        /* Check phrase */
        return theSecurityPhrase == null
               ? myThat.getSecurityPhrase() == null
               : Arrays.equals(theSecurityPhrase, myThat.getSecurityPhrase());
    }

    @Override
    public int hashCode() {
        /* Access multiplier */
        final int myPrime = HASH_PRIME;

        /* Calculate hash from simple values */
        int myCode = theKIterations;
        myCode *= myPrime;

        /* Calculate hash from types */
        myCode += theFactoryType.hashCode();
        myCode *= myPrime;

        /* Calculate hash from phrase */
        return myCode + (theSecurityPhrase == null
                         ? 0
                         : Arrays.hashCode(theSecurityPhrase));
    }
}
