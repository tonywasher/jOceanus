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

/**
 * Security Parameters.
 */
public class SecurityParameters {
    /**
     * Default Security Provider.
     */
    public static final SecurityProvider DEFAULT_PROVIDER = SecurityProvider.BC;

    /**
     * Default Restricted Security.
     */
    public static final Boolean DEFAULT_RESTRICTED = Boolean.FALSE;

    /**
     * Default Hash Algorithm.
     */
    public static final DigestType DEFAULT_HASHALGO = DigestType.KECCAK;

    /**
     * Default Cipher Steps.
     */
    public static final Integer DEFAULT_CIPHER_STEPS = 3;

    /**
     * Default Hash iterations.
     */
    public static final Integer DEFAULT_HASH_ITERATIONS = 2048;

    /**
     * Default Security Phrase.
     */
    protected static final String DEFAULT_SECURITY_PHRASE = "JG0rdianKn0t";

    /**
     * Default Active KeySets.
     */
    public static final Integer DEFAULT_ACTIVE_KEYSETS = 4;

    /**
     * The Security provider.
     */
    private final SecurityProvider theProvider;

    /**
     * Do we use restricted keys?
     */
    private final Boolean useRestricted;

    /**
     * The Base Hash algorithm.
     */
    private DigestType theHashAlgorithm;

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
    private String theSecurityPhrase;

    /**
     * The Number of active KeySets.
     */
    private int theActiveKeySets;

    /**
     * Default Constructor.
     */
    public SecurityParameters() {
        /* Default provider */
        this(DEFAULT_PROVIDER);
    }

    /**
     * Constructor for explicit provider.
     * @param pProvider the Security provider
     */
    public SecurityParameters(final SecurityProvider pProvider) {
        /* Default restricted value */
        this(pProvider, DEFAULT_RESTRICTED);
    }

    /**
     * Constructor for explicit provider.
     * @param pProvider the Security provider
     * @param pRestricted do we use restricted security
     */
    public SecurityParameters(final SecurityProvider pProvider,
                              final Boolean pRestricted) {
        /* Store parameters */
        theProvider = pProvider;
        useRestricted = pRestricted;
        theHashAlgorithm = DEFAULT_HASHALGO;
        theCipherSteps = DEFAULT_CIPHER_STEPS;
        theIterations = DEFAULT_HASH_ITERATIONS;
        theActiveKeySets = DEFAULT_ACTIVE_KEYSETS;
        theSecurityPhrase = null;
    }

    /**
     * Access the Security provider.
     * @return the security provider
     */
    public SecurityProvider getProvider() {
        return theProvider;
    }

    /**
     * Do we use restricted security?
     * @return true/false
     */
    public Boolean useRestricted() {
        return useRestricted;
    }

    /**
     * Access the base hash algorithm.
     * @return the hash algorithm
     */
    public DigestType getBaseHashAlgorithm() {
        return theHashAlgorithm;
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
    protected int getNumHashIterations() {
        return theIterations;
    }

    /**
     * Access the security phrase in bytes format.
     * @return the security phrase
     */
    protected String getSecurityPhrase() {
        return theSecurityPhrase;
    }

    /**
     * Access the number of Active KeySets.
     * @return the number of active KeySets
     */
    protected int getNumActiveKeySets() {
        return theActiveKeySets;
    }

    /**
     * Set number of cipher steps.
     * @param pNumCipherSteps number of cipher steps
     */
    public void setNumCipherSteps(final int pNumCipherSteps) {
        /* Store parameters */
        theCipherSteps = pNumCipherSteps;
    }

    /**
     * Set base hash algorithm.
     * @param pHashAlgo the algorithm
     */
    public void setBaseHashAlgorithm(final DigestType pHashAlgo) {
        /* Store parameters */
        theHashAlgorithm = pHashAlgo;
    }

    /**
     * Set number of iterations.
     * @param pNumIterations the number of iterations
     */
    public void setNumIterations(final int pNumIterations) {
        /* Store parameters */
        theIterations = pNumIterations;
    }

    /**
     * Set security phrase.
     * @param pSecurityPhrase the security phrase (or null)
     */
    public void setSecurityPhrase(final String pSecurityPhrase) {
        /* Store parameters */
        theSecurityPhrase = pSecurityPhrase;
    }

    /**
     * Set number of active KeySets.
     * @param pNumActiveKeySets the number of active KeySets
     */
    public void setNumActiveKeySets(final int pNumActiveKeySets) {
        /* Store parameters */
        theActiveKeySets = pNumActiveKeySets;
    }
}
