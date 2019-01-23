/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2018 Tony Washer
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
     * Default Restricted Security.
     */
    public static final Boolean DEFAULT_RESTRICTED = Boolean.FALSE;

    /**
     * Default Factory.
     */
    public static final GordianFactoryType DEFAULT_FACTORY = GordianFactoryType.BC;

    /**
     * Minimum Cipher Steps.
     */
    public static final Integer MINIMUM_CIPHER_STEPS = 3;

    /**
     * Default Cipher Steps.
     */
    public static final Integer DEFAULT_CIPHER_STEPS = 4;

    /**
     * Minimum Hash iterations.
     */
    public static final Integer MINIMUM_HASH_ITERATIONS = 1024;

    /**
     * Maximum Hash iterations.
     */
    public static final Integer MAXIMUM_HASH_ITERATIONS = 4096;

    /**
     * Default Hash iterations.
     */
    public static final Integer DEFAULT_HASH_ITERATIONS = 2048;

    /**
     * Default Security Phrase.
     */
    private static final String DEFAULT_SECURITY_PHRASE = "PleaseChangeMeToSomethingMoreUnique";

    /**
     * Do we use restricted keys?
     */
    private final Boolean useRestricted;

    /**
     * The Factory Type.
     */
    private GordianFactoryType theFactoryType;

    /**
     * The Number of cipher steps.
     */
    private int theCipherSteps;

    /**
     * The Number of hash iterations.
     */
    private int theIterations;

    /**
     * The Security phrase.
     */
    private byte[] theSecurityPhrase;

    /**
     * Constructor.
     */
    public GordianParameters() {
        /* Default restricted value */
        this(DEFAULT_RESTRICTED, DEFAULT_FACTORY);
    }

    /**
     * Constructor for explicit restriction.
     * @param pRestricted do we use restricted security
     */
    public GordianParameters(final Boolean pRestricted) {
        /* Default factory */
        this(pRestricted, DEFAULT_FACTORY);
    }

    /**
     * Constructor for explicit factory.
     * @param pFactoryType the factoryType
     */
    public GordianParameters(final GordianFactoryType pFactoryType) {
        /* Default restruction */
        this(DEFAULT_RESTRICTED, pFactoryType);
    }

    /**
     * Constructor for explicit restriction.
     * @param pRestricted do we use restricted security
     * @param pFactoryType the factory type
     */
    public GordianParameters(final Boolean pRestricted,
                             final GordianFactoryType pFactoryType) {
        /* Store parameters */
        useRestricted = pRestricted;
        theFactoryType = pFactoryType;
        theCipherSteps = DEFAULT_CIPHER_STEPS;
        theIterations = DEFAULT_HASH_ITERATIONS;
        theSecurityPhrase = null;
    }

    /**
     * Do we use restricted security?
     * @return true/false
     */
    public Boolean useRestricted() {
        return useRestricted;
    }

    /**
     * Access the factory type.
     * @return the factory type
     */
    public GordianFactoryType getFactoryType() {
        return theFactoryType;
    }

    /**
     * Access the number of Cipher Steps.
     * @return the number of cipher steps
     */
    public int getNumCipherSteps() {
        return theCipherSteps;
    }

    /**
     * Access the number of Hash Iterations.
     * @return the number of hash iterations
     */
    public int getNumHashIterations() {
        return theIterations;
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
     * Set number of cipher steps.
     * @param pNumCipherSteps number of cipher steps
     */
    public void setNumCipherSteps(final int pNumCipherSteps) {
        theCipherSteps = pNumCipherSteps;
    }

    /**
     * Set number of iterations.
     * @param pNumIterations the number of iterations
     */
    public void setNumIterations(final int pNumIterations) {
        theIterations = pNumIterations;
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
        if (theCipherSteps != myThat.getNumCipherSteps()
                || useRestricted != myThat.useRestricted()
                || theIterations != myThat.getNumHashIterations()) {
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
        int myCode = theCipherSteps;
        myCode *= myPrime;
        myCode += theIterations;
        myCode *= myPrime;
        myCode += useRestricted
                  ? 1
                  : 0;
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
