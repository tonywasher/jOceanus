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
import net.sourceforge.joceanus.jgordianknot.manager.GordianHashManager;
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
         * Minimum Number of Active KeySets.
         */
        private static final int MINIMUM_ACTIVE_KEYSETS = 2;

        /**
         * Maximum Number of Active KeySets.
         */
        private static final int MAXIMUM_ACTIVE_KEYSETS = 8;

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
            super(pManager, PrometheusSecurityPreferenceKey.class);
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

        @Override
        protected void definePreferences() throws OceanusException {
            defineEnumPreference(PrometheusSecurityPreferenceKey.FACTORY, GordianFactoryType.class);
            defineBooleanPreference(PrometheusSecurityPreferenceKey.RESTRICTED);
            defineEnumPreference(PrometheusSecurityPreferenceKey.HASH, GordianDigestType.class);
            defineEnumPreference(PrometheusSecurityPreferenceKey.SP800, GordianSP800Type.class);
            defineIntegerPreference(PrometheusSecurityPreferenceKey.CIPHERSTEPS);
            defineIntegerPreference(PrometheusSecurityPreferenceKey.HASHITERATIONS);
            defineCharArrayPreference(PrometheusSecurityPreferenceKey.SECURITYPHRASE);
            defineIntegerPreference(PrometheusSecurityPreferenceKey.ACTIVEKEYSETS);
        }

        @Override
        protected void autoCorrectPreferences() {
            /* Make sure that the factory is specified */
            MetisEnumPreference<PrometheusSecurityPreferenceKey, GordianFactoryType> myFactPref = getEnumPreference(PrometheusSecurityPreferenceKey.FACTORY, GordianFactoryType.class);
            if (!myFactPref.isAvailable()) {
                myFactPref.setValue(GordianParameters.DEFAULT_FACTORY);
            }

            /* Make sure that the restricted state is specified */
            MetisBooleanPreference<PrometheusSecurityPreferenceKey> myRestrictPref = getBooleanPreference(PrometheusSecurityPreferenceKey.RESTRICTED);
            if (!myRestrictPref.isAvailable()) {
                myRestrictPref.setValue(GordianParameters.DEFAULT_RESTRICTED);
            }

            /* Make sure that the sp800 is specified */
            MetisEnumPreference<PrometheusSecurityPreferenceKey, GordianSP800Type> mySP800Pref = getEnumPreference(PrometheusSecurityPreferenceKey.SP800, GordianSP800Type.class);
            if (!mySP800Pref.isAvailable()) {
                mySP800Pref.setValue(GordianParameters.DEFAULT_SP800);
            }

            /* Make sure that the hash is specified */
            MetisEnumPreference<PrometheusSecurityPreferenceKey, GordianDigestType> myHashPref = getEnumPreference(PrometheusSecurityPreferenceKey.HASH, GordianDigestType.class);
            if (!myHashPref.isAvailable()) {
                myHashPref.setValue(GordianParameters.DEFAULT_HASHALGO);
            }

            /* Set appropriate filter */
            myHashPref.setFilter(GordianHashManager.getDigestPredicate(myFactPref.getValue()));

            /* Make sure that the security phrase is specified */
            MetisCharArrayPreference<PrometheusSecurityPreferenceKey> myPhrasePref = getCharArrayPreference(PrometheusSecurityPreferenceKey.SECURITYPHRASE);
            if (!myPhrasePref.isAvailable()) {
                myPhrasePref.setValue(GordianParameters.getDefaultSecurityPhrase());
            }

            /* Make sure that the cipherSteps is specified */
            MetisIntegerPreference<PrometheusSecurityPreferenceKey> myPref = getIntegerPreference(PrometheusSecurityPreferenceKey.CIPHERSTEPS);
            if (!myPref.isAvailable()) {
                myPref.setValue(GordianParameters.DEFAULT_CIPHER_STEPS);
            }

            /* Define the range */
            Integer maxSteps = GordianHashManager.getMaximumCipherSteps(myFactPref.getValue(), myRestrictPref.getValue());
            myPref.setRange(GordianParameters.MINIMUM_CIPHER_STEPS, maxSteps);
            if (!myPref.validate()) {
                myPref.setValue(GordianParameters.DEFAULT_CIPHER_STEPS);
            }

            /* Make sure that the hashIterations is specified */
            myPref = getIntegerPreference(PrometheusSecurityPreferenceKey.HASHITERATIONS);
            if (!myPref.isAvailable()) {
                myPref.setValue(GordianParameters.DEFAULT_HASH_ITERATIONS);
            }

            /* Define the range */
            myPref.setRange(GordianParameters.MINIMUM_HASH_ITERATIONS, GordianParameters.MAXIMUM_HASH_ITERATIONS);
            if (!myPref.validate()) {
                myPref.setValue(GordianParameters.DEFAULT_HASH_ITERATIONS);
            }

            /* Make sure that the activeKeySets is specified */
            myPref = getIntegerPreference(PrometheusSecurityPreferenceKey.ACTIVEKEYSETS);
            if (!myPref.isAvailable()) {
                myPref.setValue(DEFAULT_ACTIVE_KEYSETS);
            }

            /* Define the range */
            myPref.setRange(MINIMUM_ACTIVE_KEYSETS, MAXIMUM_ACTIVE_KEYSETS);
            if (!myPref.validate()) {
                myPref.setValue(DEFAULT_ACTIVE_KEYSETS);
            }
        }
    }
}
