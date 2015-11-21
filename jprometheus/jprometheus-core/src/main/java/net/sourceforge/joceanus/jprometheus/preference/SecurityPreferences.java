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
package net.sourceforge.joceanus.jprometheus.preference;

import net.sourceforge.joceanus.jgordianknot.crypto.GordianFactoryType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianParameters;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSP800Type;
import net.sourceforge.joceanus.jmetis.preference.PreferenceSet;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Security preferences.
 * @author Tony Washer
 */
public class SecurityPreferences
        extends PreferenceSet {
    /**
     * Registry name for Restricted Security.
     */
    public static final String NAME_RESTRICTED = "RestrictedKeys";

    /**
     * Registry name for Factory Type.
     */
    public static final String NAME_FACTORY = "FactoryType";

    /**
     * Registry name for SP800 Type.
     */
    public static final String NAME_SP800 = "SP800Type";

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
     * Display name for Restricted Security.
     */
    private static final String DISPLAY_RESTRICTED = "Restricted Keys";

    /**
     * Display name for Factory Type.
     */
    private static final String DISPLAY_FACTORY = "Security Factory Type";

    /**
     * Display name for SP800 Type.
     */
    private static final String DISPLAY_SP800 = "SP800 Random Type";

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
     * Default Restricted Security.
     */
    private static final Boolean DEFAULT_RESTRICTED = GordianParameters.DEFAULT_RESTRICTED;

    /**
     * Default Factory.
     */
    private static final GordianFactoryType DEFAULT_FACTORY = GordianParameters.DEFAULT_FACTORY;

    /**
     * Default SP800 Type.
     */
    private static final GordianSP800Type DEFAULT_SP800 = GordianParameters.DEFAULT_SP800;

    /**
     * Default Cipher Steps.
     */
    private static final Integer DEFAULT_CIPHER_STEPS = GordianParameters.DEFAULT_CIPHER_STEPS;

    /**
     * Default Hash iterations.
     */
    private static final Integer DEFAULT_HASH_ITERATIONS = GordianParameters.DEFAULT_HASH_ITERATIONS;

    /**
     * Default Security Phrase.
     */
    private static final String DEFAULT_SECURITY_PHRASE = "Mon3yW1se";

    /**
     * Default Number of Active KeySets.
     */
    private static final int DEFAULT_ACTIVE_KEYSETS = 4;

    /**
     * Constructor.
     * @throws JOceanusException on error
     */
    public SecurityPreferences() throws JOceanusException {
        super();
    }

    @Override
    protected void definePreferences() {
        /* Define the properties */
        defineBooleanPreference(NAME_RESTRICTED, DEFAULT_RESTRICTED);
        definePreference(NAME_FACTORY, DEFAULT_FACTORY, GordianFactoryType.class);
        definePreference(NAME_SP800, DEFAULT_SP800, GordianSP800Type.class);
        defineIntegerPreference(NAME_CIPHER_STEPS, DEFAULT_CIPHER_STEPS);
        defineIntegerPreference(NAME_HASH_ITERATIONS, DEFAULT_HASH_ITERATIONS);
        defineStringPreference(NAME_SECURITY_PHRASE, DEFAULT_SECURITY_PHRASE);
        defineIntegerPreference(NAME_ACTIVE_KEYSETS, DEFAULT_ACTIVE_KEYSETS);
    }

    @Override
    protected String getDisplayName(final String pName) {
        /* Handle default values */
        if (pName.equals(NAME_RESTRICTED)) {
            return DISPLAY_RESTRICTED;
        }
        if (pName.equals(NAME_FACTORY)) {
            return DISPLAY_FACTORY;
        }
        if (pName.equals(NAME_SP800)) {
            return DISPLAY_SP800;
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
    public GordianParameters getParameters() {
        /* Create default preferences */
        GordianParameters myParms = new GordianParameters(getBooleanValue(NAME_RESTRICTED));

        /* Set other parameters */
        myParms.setFactoryType(getEnumValue(NAME_FACTORY, GordianFactoryType.class));
        myParms.setSP800Type(getEnumValue(NAME_SP800, GordianSP800Type.class));
        myParms.setNumCipherSteps(getIntegerValue(NAME_CIPHER_STEPS));
        myParms.setNumIterations(getIntegerValue(NAME_HASH_ITERATIONS));
        myParms.setSecurityPhrase(getStringValue(NAME_SECURITY_PHRASE));

        /* return the parameters */
        return myParms;
    }

    @Override
    public boolean isDisabled() {
        return false;
    }
}
