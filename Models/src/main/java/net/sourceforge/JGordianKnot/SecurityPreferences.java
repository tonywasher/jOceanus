/*******************************************************************************
 * JGordianKnot: Security Suite
 * Copyright 2012 Tony Washer
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
package net.sourceforge.JGordianKnot;

import net.sourceforge.JDataManager.ModelException;
import net.sourceforge.JDataManager.PreferenceSet;

/**
 * Security preferences.
 * @author Tony Washer
 */
public class SecurityPreferences extends PreferenceSet {
    /**
     * Registry name for Security Provider.
     */
    protected static final String NAME_PROVIDER = "SecurityProvider";

    /**
     * Registry name for Restricted Security.
     */
    protected static final String NAME_RESTRICTED = "RestrictedKeys";

    /**
     * Registry name for Cipher Steps.
     */
    protected static final String NAME_CIPHER_STEPS = "CipherSteps";

    /**
     * Registry name for Hash iterations.
     */
    protected static final String NAME_HASH_ITERATIONS = "HashIterations";

    /**
     * Registry name for SecurityPhrase.
     */
    protected static final String NAME_SECURITY_PHRASE = "SecurityPhrase";

    /**
     * Display name for Security Provider.
     */
    protected static final String DISPLAY_PROVIDER = "Security Provider";

    /**
     * Display name for Restricted Security.
     */
    protected static final String DISPLAY_RESTRICTED = "Restricted Keys";

    /**
     * Display name for Cipher Steps.
     */
    protected static final String DISPLAY_CIPHER_STEPS = "Number of CipherSteps";

    /**
     * Display name for Cipher Steps.
     */
    protected static final String DISPLAY_HASH_ITERATIONS = "Hash Iterations";

    /**
     * Display name for Security Phrase.
     */
    protected static final String DISPLAY_SECURITY_PHRASE = "SecurityPhrase";

    /**
     * Default Security Provider.
     */
    private static final SecurityProvider DEFAULT_PROVIDER = SecurityProvider.BouncyCastle;

    /**
     * Default Restricted Security.
     */
    private static final Boolean DEFAULT_RESTRICTED = Boolean.FALSE;

    /**
     * Default Cipher Steps.
     */
    private static final Integer DEFAULT_CIPHER_STEPS = 3;

    /**
     * Default Hash iterations.
     */
    private static final Integer DEFAULT_HASH_ITERATIONS = 2051;

    /**
     * Default Security Phrase.
     */
    private static final String DEFAULT_SECURITY_PHRASE = "JG0rdianKn0t";

    /**
     * Constructor.
     * @throws ModelException on error
     */
    public SecurityPreferences() throws ModelException {
        super();
    }

    @Override
    protected void definePreferences() {
        /* Define the properties */
        definePreference(NAME_PROVIDER, SecurityProvider.class);
        definePreference(NAME_RESTRICTED, PreferenceType.Boolean);
        definePreference(NAME_CIPHER_STEPS, PreferenceType.Integer);
        definePreference(NAME_HASH_ITERATIONS, PreferenceType.Integer);
        definePreference(NAME_SECURITY_PHRASE, PreferenceType.String);
    }

    @Override
    protected Object getDefaultValue(final String pName) {
        /* Handle default values */
        if (pName.equals(NAME_PROVIDER)) {
            return DEFAULT_PROVIDER;
        }
        if (pName.equals(NAME_RESTRICTED)) {
            return DEFAULT_RESTRICTED;
        }
        if (pName.equals(NAME_CIPHER_STEPS)) {
            return DEFAULT_CIPHER_STEPS;
        }
        if (pName.equals(NAME_HASH_ITERATIONS)) {
            return DEFAULT_HASH_ITERATIONS;
        }
        if (pName.equals(NAME_SECURITY_PHRASE)) {
            return DEFAULT_SECURITY_PHRASE;
        }
        return null;
    }

    @Override
    protected String getDisplayName(final String pName) {
        /* Handle default values */
        if (pName.equals(NAME_PROVIDER)) {
            return DISPLAY_PROVIDER;
        }
        if (pName.equals(NAME_RESTRICTED)) {
            return DISPLAY_RESTRICTED;
        }
        if (pName.equals(NAME_CIPHER_STEPS)) {
            return DISPLAY_CIPHER_STEPS;
        }
        if (pName.equals(NAME_HASH_ITERATIONS)) {
            return DISPLAY_HASH_ITERATIONS;
        }
        if (pName.equals(NAME_SECURITY_PHRASE)) {
            return DISPLAY_SECURITY_PHRASE;
        }
        return null;
    }
}
