/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.jprometheus.preference;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.EnumSet;
import java.util.Set;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactoryType;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianLockFactory;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySet;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetSpec;
import net.sourceforge.joceanus.jgordianknot.api.lock.GordianKeySetLock;
import net.sourceforge.joceanus.jgordianknot.api.lock.GordianPasswordLockSpec;
import net.sourceforge.joceanus.jgordianknot.util.GordianGenerator;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceKey;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceResource;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.TethysDataConverter;
import net.sourceforge.joceanus.jtethys.logger.TethysLogManager;
import net.sourceforge.joceanus.jtethys.logger.TethysLogger;

/**
 * Security for Preferences.
 */
public class PrometheusPreferenceSecurity {
    /**
     * Logger.
     */
    private static final TethysLogger LOGGER = TethysLogManager.getLogger(PrometheusPreferenceSecurity.class);

    /**
     * Default KeyLength.
     */
    private static final GordianLength DEFAULT_KEYLEN = GordianLength.LEN_256;

    /**
     * The KeySet.
     */
    private final GordianKeySet theKeySet;

    /**
     * Constructor.
     *
     * @param pManager the preference manager
     * @throws OceanusException on error
     */
    PrometheusPreferenceSecurity(final PrometheusPreferenceManager pManager) throws OceanusException {
        /* Create a Security Factory */
        final GordianFactory myFactory = GordianGenerator.createFactory(GordianFactoryType.BC);
        final GordianLockFactory myLocks = myFactory.getLockFactory();

        /* Obtain the hash as a preference */
        final PrometheusBaseSecurityPreferences myPrefs = pManager.getPreferenceSet(PrometheusBaseSecurityPreferences.class);
        final byte[] myLock = myPrefs.getByteArrayValue(PrometheusSecurityPreferenceKey.LOCK);

        /* Derive the password */
        final char[] myHost = getHostName();
        final char[] myUser = System.getProperty("user.name").toCharArray();
        final char[] myPassword = new char[myHost.length + myUser.length];
        System.arraycopy(myHost, 0, myPassword, 0, myHost.length);
        System.arraycopy(myUser, 0, myPassword, myHost.length, myUser.length);

        /* Derive or create the lock */
        final GordianKeySetLock myKeySetLock = myLock == null
                ? myLocks.newKeySetLock(new GordianPasswordLockSpec(), myPassword)
                : myLocks.resolveKeySetLock(myLock, myPassword);

        /* record the KeySet */
        theKeySet = myKeySetLock.getKeySet();

        /* If we have created a new lock */
        if (myLock == null) {
            /* Record the lock */
            myPrefs.setHash(myKeySetLock.getLockBytes());
            myPrefs.storeChanges();
        }
    }

    /**
     * Encrypt the value.
     *
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
     *
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
     *
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
    public enum PrometheusSecurityPreferenceKey implements MetisPreferenceKey {
        /**
         * Lock.
         */
        LOCK("Lock", null),

        /**
         * Factory.
         */
        FACTORY("FactoryType", MetisPreferenceResource.SECPREF_FACTORY),

        /**
         * KeyLength.
         */
        KEYLENGTH("KeyLength", MetisPreferenceResource.SECPREF_KEYLEN),

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
         *
         * @param pName    the name
         * @param pDisplay the display resource
         */
        PrometheusSecurityPreferenceKey(final String pName,
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
    public static class PrometheusBaseSecurityPreferences
            extends PrometheusPreferenceSet {
        /**
         * Constructor.
         *
         * @param pManager the preference manager
         * @throws OceanusException on error
         */
        public PrometheusBaseSecurityPreferences(final MetisPreferenceManager pManager) throws OceanusException {
            super((PrometheusPreferenceManager) pManager, MetisPreferenceResource.SECPREF_BASEPREFNAME);
            setHidden();
        }

        /**
         * Set lock.
         *
         * @param pHash the lock
         */
        protected void setHash(final byte[] pHash) {
            getByteArrayPreference(PrometheusSecurityPreferenceKey.LOCK).setValue(pHash);
        }

        @Override
        protected void definePreferences() {
            defineByteArrayPreference(PrometheusSecurityPreferenceKey.LOCK);
        }

        @Override
        public void autoCorrectPreferences() {
            /* No-OP */
        }
    }

    /**
     * PrefSecurityPreferences.
     */
    public static class PrometheusSecurityPreferences
            extends PrometheusPreferenceSet {
        /**
         * Valid lengths.
         */
        private static final Set<GordianLength> VALID_LENGTHS = EnumSet.of(GordianLength.LEN_128, GordianLength.LEN_192, GordianLength.LEN_256);

        /**
         * Default Security Phrase.
         */
        private static final String DEFAULT_SECURITY_PHRASE = "PleaseChangeMeToSomethingMoreUnique";

        /**
         * Minimum Number of Active KeySets.
         */
        private static final int MINIMUM_ACTIVE_KEYSETS = 4;

        /**
         * Maximum Number of Active KeySets.
         */
        private static final int MAXIMUM_ACTIVE_KEYSETS = 64;

        /**
         * Default Number of Active KeySets.
         */
        private static final int DEFAULT_ACTIVE_KEYSETS = 8;

        /**
         * Constructor.
         *
         * @param pManager the preference manager
         * @throws OceanusException on error
         */
        public PrometheusSecurityPreferences(final MetisPreferenceManager pManager) throws OceanusException {
            super((PrometheusPreferenceManager) pManager, MetisPreferenceResource.SECPREF_PREFNAME);
        }

        /**
         * Get FactoryType.
         *
         * @return the factoryType
         */
        public GordianFactoryType getFactoryType() {
            return getEnumValue(PrometheusSecurityPreferenceKey.FACTORY, GordianFactoryType.class);
        }

        /**
         * Get Security Phrase.
         *
         * @return the phrase
         */
        public char[] getSecurityPhrase() {
            return getCharArrayValue(PrometheusSecurityPreferenceKey.SECURITYPHRASE);
        }

        /**
         * Get KeySetHashSpec.
         *
         * @return the parameters
         */
        public GordianPasswordLockSpec getPasswordLockSpec() {
            /* Build and return keySetSpec */
            final GordianLength myKeyLen = getEnumValue(PrometheusSecurityPreferenceKey.KEYLENGTH, GordianLength.class);
            final int myIterations = getIntegerValue(PrometheusSecurityPreferenceKey.HASHITERATIONS);
            final int mySteps = getIntegerValue(PrometheusSecurityPreferenceKey.CIPHERSTEPS);
            return new GordianPasswordLockSpec(myIterations, new GordianKeySetSpec(myKeyLen, mySteps));
        }

        @Override
        protected void definePreferences() throws OceanusException {
            defineEnumPreference(PrometheusSecurityPreferenceKey.FACTORY, GordianFactoryType.class);
            defineEnumPreference(PrometheusSecurityPreferenceKey.KEYLENGTH, GordianLength.class);
            defineIntegerPreference(PrometheusSecurityPreferenceKey.CIPHERSTEPS);
            defineIntegerPreference(PrometheusSecurityPreferenceKey.HASHITERATIONS);
            defineCharArrayPreference(PrometheusSecurityPreferenceKey.SECURITYPHRASE);
            defineIntegerPreference(PrometheusSecurityPreferenceKey.ACTIVEKEYSETS);
        }

        @Override
        public void autoCorrectPreferences() {
            /* Make sure that the factory is specified */
            final MetisEnumPreference<GordianFactoryType> myFactPref
                    = getEnumPreference(PrometheusSecurityPreferenceKey.FACTORY, GordianFactoryType.class);
            if (!myFactPref.isAvailable()) {
                myFactPref.setValue(GordianFactoryType.BC);
            }

            /* Make sure that the restricted state is specified */
            final MetisEnumPreference<GordianLength> myLengthPref
                    = getEnumPreference(PrometheusSecurityPreferenceKey.KEYLENGTH, GordianLength.class);
            if (!myLengthPref.isAvailable()) {
                myLengthPref.setValue(DEFAULT_KEYLEN);
            }

            /* Make sure that the length is restricted */
            myLengthPref.setFilter(VALID_LENGTHS::contains);

            /* Make sure that the security phrase is specified */
            final PrometheusCharArrayPreference myPhrasePref = getCharArrayPreference(PrometheusSecurityPreferenceKey.SECURITYPHRASE);
            if (!myPhrasePref.isAvailable()) {
                myPhrasePref.setValue(DEFAULT_SECURITY_PHRASE.toCharArray());
            }

            /* Make sure that the cipherSteps is specified */
            MetisIntegerPreference myPref = getIntegerPreference(PrometheusSecurityPreferenceKey.CIPHERSTEPS);
            if (!myPref.isAvailable()) {
                myPref.setValue(GordianKeySetSpec.DEFAULT_CIPHER_STEPS);
            }

            /* Define the range */
            myPref.setRange(GordianKeySetSpec.MINIMUM_CIPHER_STEPS, GordianKeySetSpec.MAXIMUM_CIPHER_STEPS);
            if (!myPref.validate()) {
                myPref.setValue(GordianKeySetSpec.DEFAULT_CIPHER_STEPS);
            }

            /* Make sure that the hashIterations is specified */
            myPref = getIntegerPreference(PrometheusSecurityPreferenceKey.HASHITERATIONS);
            if (!myPref.isAvailable()) {
                myPref.setValue(GordianPasswordLockSpec.DEFAULT_ITERATIONS);
            }

            /* Define the range */
            myPref.setRange(GordianPasswordLockSpec.MINIMUM_ITERATIONS, GordianPasswordLockSpec.MAXIMUM_ITERATIONS);
            if (!myPref.validate()) {
                myPref.setValue(GordianPasswordLockSpec.DEFAULT_ITERATIONS);
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
