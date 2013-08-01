/*******************************************************************************
 * jDataModels: Data models
 * Copyright 2012,2013 Tony Washer
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
package net.sourceforge.jOceanus.jDataModels.preferences;

import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jGordianKnot.SecureManager;
import net.sourceforge.jOceanus.jGordianKnot.SecurityGenerator;
import net.sourceforge.jOceanus.jGordianKnot.SecurityProvider;
import net.sourceforge.jOceanus.jPreferenceSet.PreferenceSet;

/**
 * Security preferences.
 * @author Tony Washer
 */
public class SecurityPreferences
        extends PreferenceSet {
    /**
     * Registry name for Security Provider.
     */
    public static final String NAME_PROVIDER = "SecurityProvider";

    /**
     * Registry name for Restricted Security.
     */
    public static final String NAME_RESTRICTED = "RestrictedKeys";

    /**
     * Registry name for Cipher Steps.
     */
    public static final String NAME_CIPHER_STEPS = "CipherSteps";

    /**
     * Registry name for Hash iterations.
     */
    public static final String NAME_HASH_ITERATIONS = "HashIterations";

    /**
     * Registry name for SecurityPhrase.
     */
    public static final String NAME_SECURITY_PHRASE = "SecurityPhrase";

    /**
     * Display name for Security Provider.
     */
    private static final String DISPLAY_PROVIDER = "Security Provider";

    /**
     * Display name for Restricted Security.
     */
    private static final String DISPLAY_RESTRICTED = "Restricted Keys";

    /**
     * Display name for Cipher Steps.
     */
    private static final String DISPLAY_CIPHER_STEPS = "Number of CipherSteps";

    /**
     * Display name for Cipher Steps.
     */
    private static final String DISPLAY_HASH_ITERATIONS = "Hash Iterations";

    /**
     * Display name for Security Phrase.
     */
    private static final String DISPLAY_SECURITY_PHRASE = "SecurityPhrase";

    /**
     * Default Security Provider.
     */
    private static final SecurityProvider DEFAULT_PROVIDER = SecureManager.DEFAULT_PROVIDER;

    /**
     * Default Restricted Security.
     */
    private static final Boolean DEFAULT_RESTRICTED = SecureManager.DEFAULT_RESTRICTED;

    /**
     * Default Cipher Steps.
     */
    private static final Integer DEFAULT_CIPHER_STEPS = SecureManager.DEFAULT_CIPHER_STEPS;

    /**
     * Default Hash iterations.
     */
    private static final Integer DEFAULT_HASH_ITERATIONS = SecureManager.DEFAULT_HASH_ITERATIONS;

    /**
     * Default Security Phrase.
     */
    private static final String DEFAULT_SECURITY_PHRASE = SecureManager.DEFAULT_SECURITY_PHRASE;

    /**
     * Constructor.
     * @throws JDataException on error
     */
    public SecurityPreferences() throws JDataException {
        super();
    }

    /**
     * Get SecureManager.
     * @return Security Manager for these preferences
     */
    public SecureManager getSecurity() {
        return new SecureManager(getEnumValue(NAME_PROVIDER, SecurityProvider.class), getBooleanValue(NAME_RESTRICTED), getIntegerValue(NAME_CIPHER_STEPS),
                getIntegerValue(NAME_HASH_ITERATIONS), getStringValue(NAME_SECURITY_PHRASE));
    }

    /**
     * Get SecurityGenerator.
     * @return Security Generator for these preferences
     */
    public SecurityGenerator getGenerator() {
        return new SecurityGenerator(getEnumValue(NAME_PROVIDER, SecurityProvider.class), getBooleanValue(NAME_RESTRICTED), getIntegerValue(NAME_CIPHER_STEPS),
                getIntegerValue(NAME_HASH_ITERATIONS), getStringValue(NAME_SECURITY_PHRASE));
    }

    @Override
    protected void definePreferences() {
        /* Define the properties */
        definePreference(NAME_PROVIDER, DEFAULT_PROVIDER, SecurityProvider.class);
        defineBooleanPreference(NAME_RESTRICTED, DEFAULT_RESTRICTED);
        defineIntegerPreference(NAME_CIPHER_STEPS, DEFAULT_CIPHER_STEPS);
        defineIntegerPreference(NAME_HASH_ITERATIONS, DEFAULT_HASH_ITERATIONS);
        defineStringPreference(NAME_SECURITY_PHRASE, DEFAULT_SECURITY_PHRASE);
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
