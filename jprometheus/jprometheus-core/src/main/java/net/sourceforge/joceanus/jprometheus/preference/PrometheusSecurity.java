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

import net.sourceforge.joceanus.jgordianknot.crypto.GordianDigestType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianFactoryType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianParameters;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSP800Type;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceKey;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceSet;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Security preferences.
 * @author Tony Washer
 */
public final class PrometheusSecurity {
    /**
     * Constructor.
     */
    private PrometheusSecurity() {
    }

    /**
     * SecurityPreferenceKeys.
     */
    public enum PrometheusSecurityPreferenceKey implements MetisPreferenceKey {
        /**
         * Factory.
         */
        FACTORY("FactoryType", PrometheusPreferenceResource.SECPREF_FACTORY),

        /**
         * Restricted Keys.
         */
        RESTRICTED("RestrictedKeys", PrometheusPreferenceResource.SECPREF_RESTRICTED),

        /**
         * Hash Algorithm.
         */
        HASH("HashType", PrometheusPreferenceResource.SECPREF_HASH),

        /**
         * SP800 Type.
         */
        SP800("SP800Type", PrometheusPreferenceResource.SECPREF_SP800),

        /**
         * Cipher Steps.
         */
        CIPHERSTEPS("CipherSteps", PrometheusPreferenceResource.SECPREF_CIPHERSTEPS),

        /**
         * Hash Iterations.
         */
        HASHITERATIONS("HashIterations", PrometheusPreferenceResource.SECPREF_ITERATIONS),

        /**
         * SecurityPhrase.
         */
        SECURITYPHRASE("SecurityPhrase", PrometheusPreferenceResource.SECPREF_PHRASE),

        /**
         * Hash Iterations.
         */
        ACTIVEKEYSETS("NumActiveKeySets", PrometheusPreferenceResource.SECPREF_KEYSETS);

        /**
         * The name of the Preference.
         */
        private final String theName;

        /**
         * The display string.
         */
        private final String theDisplay;

        /**
         * Constructor.
         * @param pName the name
         * @param pDisplay the display string;
         */
        PrometheusSecurityPreferenceKey(final String pName,
                                        final PrometheusPreferenceResource pDisplay) {
            theName = pName;
            theDisplay = pDisplay.getValue();
        }

        @Override
        public String getName() {
            return theName;
        }

        @Override
        public String getDisplay() {
            return theDisplay;
        }
    }

    /**
     * PrometheusSecurityPreferences.
     */
    public static class PrometheusSecurityPreferences
            extends MetisPreferenceSet<PrometheusSecurityPreferenceKey> {
        /**
         * Default Number of Active KeySets.
         */
        private static final int DEFAULT_ACTIVE_KEYSETS = 4;

        /**
         * Constructor.
         * @param pManager the preference manager
         * @throws OceanusException on error
         */
        public PrometheusSecurityPreferences(final MetisPreferenceManager pManager) throws OceanusException {
            super(pManager);
            defineEnumPreference(PrometheusSecurityPreferenceKey.FACTORY, GordianParameters.DEFAULT_FACTORY, GordianFactoryType.class);
            defineBooleanPreference(PrometheusSecurityPreferenceKey.RESTRICTED, GordianParameters.DEFAULT_RESTRICTED);
            defineEnumPreference(PrometheusSecurityPreferenceKey.HASH, GordianParameters.DEFAULT_HASHALGO, GordianDigestType.class);
            defineEnumPreference(PrometheusSecurityPreferenceKey.SP800, GordianParameters.DEFAULT_SP800, GordianSP800Type.class);
            defineIntegerPreference(PrometheusSecurityPreferenceKey.CIPHERSTEPS, GordianParameters.DEFAULT_CIPHER_STEPS);
            defineIntegerPreference(PrometheusSecurityPreferenceKey.HASHITERATIONS, GordianParameters.DEFAULT_HASH_ITERATIONS);
            defineCharArrayPreference(PrometheusSecurityPreferenceKey.SECURITYPHRASE, GordianParameters.getDefaultSecurityPhrase());
            defineIntegerPreference(PrometheusSecurityPreferenceKey.ACTIVEKEYSETS, DEFAULT_ACTIVE_KEYSETS);
            setName(PrometheusPreferenceResource.SECPREF_PREFNAME.getValue());
            storeChanges();
        }

        /**
         * Get Security Parameters.
         * @return the parameters
         */
        public GordianParameters getParameters() {
            /* Create default preferences */
            GordianParameters myParms = new GordianParameters(getBooleanValue(PrometheusSecurityPreferenceKey.RESTRICTED));

            /* Set other parameters */
            myParms.setFactoryType(getEnumValue(PrometheusSecurityPreferenceKey.FACTORY, GordianFactoryType.class));
            myParms.setBaseHashAlgorithm(getEnumValue(PrometheusSecurityPreferenceKey.HASH, GordianDigestType.class));
            myParms.setSP800Type(getEnumValue(PrometheusSecurityPreferenceKey.SP800, GordianSP800Type.class));
            myParms.setNumCipherSteps(getIntegerValue(PrometheusSecurityPreferenceKey.CIPHERSTEPS));
            myParms.setNumIterations(getIntegerValue(PrometheusSecurityPreferenceKey.HASHITERATIONS));
            myParms.setSecurityPhrase(getCharArrayValue(PrometheusSecurityPreferenceKey.SECURITYPHRASE));

            /* return the parameters */
            return myParms;
        }
    }
}
