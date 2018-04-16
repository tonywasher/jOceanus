/*******************************************************************************
 * jMetis: Java Data Framework
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jmetis.preference;

import net.sourceforge.joceanus.jgordianknot.crypto.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianFactoryType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeySet;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeySetHash;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianParameters;
import net.sourceforge.joceanus.jgordianknot.manager.GordianHashManager;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.TethysDataConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Security for Preferences.
 */
public class MetisPreferenceSecurity {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(MetisPreferenceSecurity.class);

    /**
     * The KeySet.
     */
    private final GordianKeySet theKeySet;

    /**
     * Constructor.
     * @param pManager the preference manager
     * @throws OceanusException on error
     */
    protected MetisPreferenceSecurity(final MetisPreferenceManager pManager) throws OceanusException {
        /* Create the Bouncy Parameters */
        final GordianParameters myParms = new GordianParameters();
        myParms.setFactoryType(GordianFactoryType.BC);
        myParms.useRestricted();
        myParms.setSecurityPhrase(getHostName());

        /* Create a Security Factory */
        final GordianFactory myFactory = GordianHashManager.newFactory(myParms);

        /* Obtain the hash as a preference */
        final MetisBaseSecurityPreferences myPrefs = pManager.getPreferenceSet(MetisBaseSecurityPreferences.class);
        final byte[] myHash = myPrefs.getByteArrayValue(MetisSecurityPreferenceKey.HASH);

        /* Derive or create the hash */
        final char[] myPassword = System.getProperty("user.name").toCharArray();
        final GordianKeySetHash myKeySetHash = myHash == null
                                                              ? myFactory.generateKeySetHash(myPassword)
                                                              : myFactory.deriveKeySetHash(myHash, myPassword);

        /* record the KeySet */
        theKeySet = myKeySetHash.getKeySet();

        /* If we have created a new hash */
        if (myHash == null) {
            /* Record the hash */
            myPrefs.setHash(myKeySetHash.getHash());
            myPrefs.storeChanges();
        }
    }

    /**
     * Encrypt the value.
     * @param pValue the value to encrypt
     * @return the encrypted value
     * @throws OceanusException on error
     */
    protected byte[] encryptValue(final char[] pValue) throws OceanusException {
        final byte[] myBytes = TethysDataConverter.charsToByteArray(pValue);
        return theKeySet.encryptBytes(myBytes);
    }

    /**
     * Decrypt the value.
     * @param pValue the value to decrypt
     * @return the decrypted value
     * @throws OceanusException on error
     */
    protected char[] decryptValue(final byte[] pValue) throws OceanusException {
        final byte[] myBytes = theKeySet.decryptBytes(pValue);
        return TethysDataConverter.bytesToCharArray(myBytes);
    }

    /**
     * determine hostName.
     * @return the hostName
     */
    private static char[] getHostName() {
        /* Protect against exceptions */
        try {
            final InetAddress myAddr = InetAddress.getLocalHost();
            return myAddr.getHostName().toCharArray();

        } catch (UnknownHostException e) {
            LOGGER.error("Hostname can not be resolved", e);
            return "localhost".toCharArray();
        }
    }

    /**
     * SecurityPreferenceKey.
     */
    public enum MetisSecurityPreferenceKey implements MetisPreferenceKey {
        /**
         * Hash.
         */
        HASH("Hash", null),

        /**
         * Factory.
         */
        FACTORY("FactoryType", MetisPreferenceResource.SECPREF_FACTORY),

        /**
         * Restricted Keys.
         */
        RESTRICTED("RestrictedKeys", MetisPreferenceResource.SECPREF_RESTRICTED),

        /**
         * Cipher Steps.
         */
        CIPHERSTEPS("CipherSteps", MetisPreferenceResource.SECPREF_CIPHERSTEPS),

        /**
         * Hash Iterations.
         */
        HASHITERATIONS("HashIterations", MetisPreferenceResource.SECPREF_ITERATIONS),

        /**
         * SecurityPhrase.
         */
        SECURITYPHRASE("SecurityPhrase", MetisPreferenceResource.SECPREF_PHRASE),

        /**
         * ActiveKeySets.
         */
        ACTIVEKEYSETS("NumActiveKeySets", MetisPreferenceResource.SECPREF_KEYSETS);

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
         * @param pDisplay the display resource
         */
        MetisSecurityPreferenceKey(final String pName,
                                   final MetisPreferenceResource pDisplay) {
            theName = pName;
            theDisplay = pDisplay != null
                                          ? pDisplay.getValue()
                                          : null;
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
     * PrefSecurityPreferences.
     */
    public static class MetisBaseSecurityPreferences
            extends MetisPreferenceSet<MetisSecurityPreferenceKey> {
        /**
         * Constructor.
         * @param pManager the preference manager
         * @throws OceanusException on error
         */
        public MetisBaseSecurityPreferences(final MetisPreferenceManager pManager) throws OceanusException {
            super(pManager, MetisSecurityPreferenceKey.class, MetisPreferenceResource.SECPREF_BASEPREFNAME);
            setHidden();
        }

        /**
         * Set hash.
         * @param pHash the hash
         */
        protected void setHash(final byte[] pHash) {
            getByteArrayPreference(MetisSecurityPreferenceKey.HASH).setValue(pHash);
        }

        @Override
        protected void definePreferences() throws OceanusException {
            defineByteArrayPreference(MetisSecurityPreferenceKey.HASH);
        }

        @Override
        public void autoCorrectPreferences() {
            /* No-OP */
        }
    }

    /**
     * PrefSecurityPreferences.
     */
    public static class MetisSecurityPreferences
            extends MetisPreferenceSet<MetisSecurityPreferenceKey> {
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
        public MetisSecurityPreferences(final MetisPreferenceManager pManager) throws OceanusException {
            super(pManager, MetisSecurityPreferenceKey.class, MetisPreferenceResource.SECPREF_PREFNAME);
        }

        /**
         * Get Security Parameters.
         * @return the parameters
         */
        public GordianParameters getParameters() {
            /* Create default preferences */
            final GordianParameters myParms = new GordianParameters(getBooleanValue(MetisSecurityPreferenceKey.RESTRICTED));

            /* Set other parameters */
            myParms.setFactoryType(getEnumValue(MetisSecurityPreferenceKey.FACTORY, GordianFactoryType.class));
            myParms.setNumCipherSteps(getIntegerValue(MetisSecurityPreferenceKey.CIPHERSTEPS));
            myParms.setNumIterations(getIntegerValue(MetisSecurityPreferenceKey.HASHITERATIONS));
            myParms.setSecurityPhrase(getCharArrayValue(MetisSecurityPreferenceKey.SECURITYPHRASE));

            /* return the parameters */
            return myParms;
        }

        @Override
        protected void definePreferences() throws OceanusException {
            defineEnumPreference(MetisSecurityPreferenceKey.FACTORY, GordianFactoryType.class);
            defineBooleanPreference(MetisSecurityPreferenceKey.RESTRICTED);
            defineIntegerPreference(MetisSecurityPreferenceKey.CIPHERSTEPS);
            defineIntegerPreference(MetisSecurityPreferenceKey.HASHITERATIONS);
            defineCharArrayPreference(MetisSecurityPreferenceKey.SECURITYPHRASE);
            defineIntegerPreference(MetisSecurityPreferenceKey.ACTIVEKEYSETS);
        }

        @Override
        public void autoCorrectPreferences() {
            /* Make sure that the factory is specified */
            final MetisEnumPreference<MetisSecurityPreferenceKey, GordianFactoryType> myFactPref = getEnumPreference(MetisSecurityPreferenceKey.FACTORY, GordianFactoryType.class);
            if (!myFactPref.isAvailable()) {
                myFactPref.setValue(GordianParameters.DEFAULT_FACTORY);
            }

            /* Make sure that the restricted state is specified */
            final MetisBooleanPreference<MetisSecurityPreferenceKey> myRestrictPref = getBooleanPreference(MetisSecurityPreferenceKey.RESTRICTED);
            if (!myRestrictPref.isAvailable()) {
                myRestrictPref.setValue(GordianParameters.DEFAULT_RESTRICTED);
            }

            /* Make sure that the security phrase is specified */
            final MetisCharArrayPreference<MetisSecurityPreferenceKey> myPhrasePref = getCharArrayPreference(MetisSecurityPreferenceKey.SECURITYPHRASE);
            if (!myPhrasePref.isAvailable()) {
                myPhrasePref.setValue(GordianParameters.getDefaultSecurityPhrase());
            }

            /* Make sure that the cipherSteps is specified */
            MetisIntegerPreference<MetisSecurityPreferenceKey> myPref = getIntegerPreference(MetisSecurityPreferenceKey.CIPHERSTEPS);
            if (!myPref.isAvailable()) {
                myPref.setValue(GordianParameters.DEFAULT_CIPHER_STEPS);
            }

            /* Define the range */
            final Integer maxSteps = GordianHashManager.getMaximumCipherSteps(myFactPref.getValue(), myRestrictPref.getValue());
            myPref.setRange(GordianParameters.MINIMUM_CIPHER_STEPS, maxSteps);
            if (!myPref.validate()) {
                myPref.setValue(GordianParameters.DEFAULT_CIPHER_STEPS);
            }

            /* Make sure that the hashIterations is specified */
            myPref = getIntegerPreference(MetisSecurityPreferenceKey.HASHITERATIONS);
            if (!myPref.isAvailable()) {
                myPref.setValue(GordianParameters.DEFAULT_HASH_ITERATIONS);
            }

            /* Define the range */
            myPref.setRange(GordianParameters.MINIMUM_HASH_ITERATIONS, GordianParameters.MAXIMUM_HASH_ITERATIONS);
            if (!myPref.validate()) {
                myPref.setValue(GordianParameters.DEFAULT_HASH_ITERATIONS);
            }

            /* Make sure that the activeKeySets is specified */
            myPref = getIntegerPreference(MetisSecurityPreferenceKey.ACTIVEKEYSETS);
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
