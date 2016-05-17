/*******************************************************************************
 * jMetis: Java Data Framework
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
package net.sourceforge.joceanus.jmetis.preference;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeySet;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeySetHash;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianParameters;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyFactory;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.TethysDataConverter;

/**
 * Security for Preferences.
 */
public class MetisPreferenceSecurity {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MetisPreferenceSecurity.class);

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
        GordianParameters myParms = new GordianParameters();
        myParms.useRestricted();
        myParms.setSecurityPhrase(getHostName());

        /* Create a Bouncy Factory */
        BouncyFactory myFactory = new BouncyFactory(myParms);

        /* Obtain the hash as a preference */
        PrefSecurityPreferences myPrefs = pManager.getPreferenceSet(PrefSecurityPreferences.class);
        byte[] myHash = myPrefs.getByteArrayValue(PrefSecurityKey.HASH);

        /* Derive or create the hash */
        char[] myPassword = System.getProperty("user.name").toCharArray();
        GordianKeySetHash myKeySetHash = myHash == null
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
        byte[] myBytes = TethysDataConverter.charsToByteArray(pValue);
        return theKeySet.encryptBytes(myBytes);
    }

    /**
     * Decrypt the value.
     * @param pValue the value to decrypt
     * @return the decrypted value
     * @throws OceanusException on error
     */
    protected char[] decryptValue(final byte[] pValue) throws OceanusException {
        byte[] myBytes = theKeySet.decryptBytes(pValue);
        return TethysDataConverter.bytesToCharArray(myBytes);
    }

    /**
     * determine hostName.
     * @return the hostName
     */
    private static char[] getHostName() {
        /* Protect against exceptions */
        try {
            InetAddress myAddr = InetAddress.getLocalHost();
            return myAddr.getHostName().toCharArray();

        } catch (UnknownHostException e) {
            LOGGER.error("Hostname can not be resolved", e);
            return "localhost".toCharArray();
        }
    }

    /**
     * PreferencePreferences.
     */
    protected enum PrefSecurityKey implements MetisPreferenceKey {
        /**
         * Hash.
         */
        HASH;

        @Override
        public String getName() {
            return "Hash";
        }

        @Override
        public String getDisplay() {
            return null;
        }
    }

    /**
     * PrefSecurityPreferences.
     */
    protected static class PrefSecurityPreferences
            extends MetisPreferenceSet<PrefSecurityKey> {
        /**
         * Constructor.
         * @param pManager the preference manager
         * @throws OceanusException on error
         */
        public PrefSecurityPreferences(final MetisPreferenceManager pManager) throws OceanusException {
            super(pManager, PrefSecurityKey.class);
            setHidden();
        }

        /**
         * Set hash.
         * @param pHash the hash
         */
        protected void setHash(final byte[] pHash) {
            getByteArrayPreference(PrefSecurityKey.HASH).setValue(pHash);
        }

        @Override
        protected void definePreferences() {
            defineByteArrayPreference(PrefSecurityKey.HASH);
        }

        @Override
        protected void autoCorrectPreferences() {
            /* No-OP */
        }
    }
}
