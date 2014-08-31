/*******************************************************************************
 * jPrometheus: Application Framework
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
package net.sourceforge.joceanus.jprometheus.preferences;

import net.sourceforge.joceanus.jgordianknot.crypto.SecureManager;
import net.sourceforge.joceanus.jgordianknot.crypto.SecurityGenerator;
import net.sourceforge.joceanus.jgordianknot.crypto.SecurityParameters;
import net.sourceforge.joceanus.jgordianknot.crypto.SecurityProvider;
import net.sourceforge.joceanus.jmetis.preference.PreferenceSet;
import net.sourceforge.joceanus.jtethys.JOceanusException;

import org.slf4j.Logger;

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
     * Registry name for Long Hash.
     */
    public static final String NAME_LONGHASH = "LongHash";

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
     * Registry name for NumActiveKeySets.
     */
    public static final String NAME_ACTIVE_KEYSETS = "NumActiveKeySets";

    /**
     * Display name for Security Provider.
     */
    private static final String DISPLAY_PROVIDER = "Security Provider";

    /**
     * Display name for Restricted Security.
     */
    private static final String DISPLAY_RESTRICTED = "Restricted Keys";

    /**
     * Display name for LongHash.
     */
    private static final String DISPLAY_LONGHASH = "Use Long Hash";

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
     * Display name for NumActiveKeySets.
     */
    private static final String DISPLAY_ACTIVE_KEYSETS = "Number of Active KeySets";

    /**
     * Default Security Provider.
     */
    private static final SecurityProvider DEFAULT_PROVIDER = SecurityParameters.DEFAULT_PROVIDER;

    /**
     * Default Restricted Security.
     */
    private static final Boolean DEFAULT_RESTRICTED = SecurityParameters.DEFAULT_RESTRICTED;

    /**
     * Default Long Hash.
     */
    private static final Boolean DEFAULT_LONGHASH = SecurityParameters.DEFAULT_LONGHASH;

    /**
     * Default Cipher Steps.
     */
    private static final Integer DEFAULT_CIPHER_STEPS = SecurityParameters.DEFAULT_CIPHER_STEPS;

    /**
     * Default Hash iterations.
     */
    private static final Integer DEFAULT_HASH_ITERATIONS = SecurityParameters.DEFAULT_HASH_ITERATIONS;

    /**
     * Default Security Phrase.
     */
    private static final String DEFAULT_SECURITY_PHRASE = SecurityParameters.DEFAULT_SECURITY_PHRASE;

    /**
     * Default Number of Active KeySets.
     */
    private static final int DEFAULT_ACTIVE_KEYSETS = SecurityParameters.DEFAULT_ACTIVE_KEYSETS;

    /**
     * Constructor.
     * @throws JOceanusException on error
     */
    public SecurityPreferences() throws JOceanusException {
        super();
    }

    /**
     * Get SecureManager.
     * @param pLogger the logger
     * @return Security Manager for these preferences
     * @throws JOceanusException on error
     */
    public SecureManager getSecurity(final Logger pLogger) throws JOceanusException {
        return new SecureManager(pLogger, getParameters());
    }

    /**
     * Get SecurityGenerator.
     * @param pLogger the logger
     * @return Security Generator for these preferences
     * @throws JOceanusException on error
     */
    public SecurityGenerator getGenerator(final Logger pLogger) throws JOceanusException {
        return new SecurityGenerator(pLogger, getParameters());
    }

    @Override
    protected void definePreferences() {
        /* Define the properties */
        definePreference(NAME_PROVIDER, DEFAULT_PROVIDER, SecurityProvider.class);
        defineBooleanPreference(NAME_RESTRICTED, DEFAULT_RESTRICTED);
        defineBooleanPreference(NAME_LONGHASH, DEFAULT_LONGHASH);
        defineIntegerPreference(NAME_CIPHER_STEPS, DEFAULT_CIPHER_STEPS);
        defineIntegerPreference(NAME_HASH_ITERATIONS, DEFAULT_HASH_ITERATIONS);
        defineStringPreference(NAME_SECURITY_PHRASE, DEFAULT_SECURITY_PHRASE);
        defineIntegerPreference(NAME_ACTIVE_KEYSETS, DEFAULT_ACTIVE_KEYSETS);
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
        if (pName.equals(NAME_LONGHASH)) {
            return DISPLAY_LONGHASH;
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
        if (pName.equals(NAME_ACTIVE_KEYSETS)) {
            return DISPLAY_ACTIVE_KEYSETS;
        }
        return null;
    }

    /**
     * Get Security Parameters.
     * @return the parameters
     */
    private SecurityParameters getParameters() {
        /* Create default preferences */
        SecurityParameters myParms = new SecurityParameters(getEnumValue(NAME_PROVIDER, SecurityProvider.class), getBooleanValue(NAME_RESTRICTED));

        /* Set other parameters */
        myParms.setUseLongHash(getBooleanValue(NAME_LONGHASH));
        myParms.setNumCipherSteps(getIntegerValue(NAME_CIPHER_STEPS));
        myParms.setNumIterations(getIntegerValue(NAME_HASH_ITERATIONS));
        myParms.setSecurityPhrase(getStringValue(NAME_SECURITY_PHRASE));
        myParms.setNumActiveKeySets(getIntegerValue(NAME_ACTIVE_KEYSETS));

        /* return the parameters */
        return myParms;
    }

    @Override
    public boolean isDisabled() {
        return false;
    }
}
