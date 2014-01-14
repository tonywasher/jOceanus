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
package net.sourceforge.joceanus.jgordianknot;


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
     * Default Long Hash.
     */
    public static final Boolean DEFAULT_LONGHASH = Boolean.TRUE;

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
    public static final String DEFAULT_SECURITY_PHRASE = "JG0rdianKn0t";

    /**
     * The Security provider.
     */
    private final SecurityProvider theProvider;

    /**
     * Do we use restricted keys?
     */
    private final Boolean useRestricted;

    /**
     * Do we use long hashes?
     */
    private Boolean useLongHash;

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
     * Do we use long hashes.
     * @return true/false
     */
    public Boolean useLongHash() {
        return useLongHash;
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
        useLongHash = DEFAULT_LONGHASH;
        theCipherSteps = DEFAULT_CIPHER_STEPS;
        theIterations = DEFAULT_HASH_ITERATIONS;
        theSecurityPhrase = null;
    }

    /**
     * Set useLongHash flag.
     * @param pLongHash use long hash (true/false)
     */
    public void setUseLongHash(final boolean pLongHash) {
        /* Store parameters */
        useLongHash = pLongHash;
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
}
