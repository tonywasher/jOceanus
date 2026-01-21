/*
 * Prometheus: Application Framework
 * Copyright 2012-2026. Tony Washer
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
 */
package io.github.tonywasher.joceanus.prometheus.preference;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.oceanus.resource.OceanusBundleId;
import io.github.tonywasher.joceanus.metis.preference.MetisPreferenceKey;
import io.github.tonywasher.joceanus.metis.preference.MetisPreferenceSet;

/**
 * Prometheus PreferenceSet.
 */
public abstract class PrometheusPreferenceSet
        extends MetisPreferenceSet {
    /**
     * The Security Manager.
     */
    private PrometheusPreferenceSecurity theSecurity;

    /**
     * Constructor.
     *
     * @param pManager the preference manager
     * @param pId      the Id
     * @throws OceanusException on error
     */
    protected PrometheusPreferenceSet(final PrometheusPreferenceManager pManager,
                                      final OceanusBundleId pId) throws OceanusException {
        this(pManager, pId.getValue());
    }

    /**
     * Constructor.
     *
     * @param pManager the preference manager
     * @param pName    the name
     * @throws OceanusException on error
     */
    protected PrometheusPreferenceSet(final PrometheusPreferenceManager pManager,
                                      final String pName) throws OceanusException {
        /* Initialize super-class */
        super(pManager, pName);
    }

    @Override
    public PrometheusPreferenceManager getPreferenceManager() {
        return (PrometheusPreferenceManager) super.getPreferenceManager();
    }

    /**
     * Obtain the security.
     *
     * @return the security
     * @throws OceanusException on error
     */
    public PrometheusPreferenceSecurity getSecurity() throws OceanusException {
        if (theSecurity == null) {
            theSecurity = getPreferenceManager().getSecurity();
        }
        return theSecurity;
    }

    /**
     * Define new ByteArray preference.
     *
     * @param pKey the key for the preference
     * @return the preference item
     */
    protected PrometheusByteArrayPreference defineByteArrayPreference(final MetisPreferenceKey pKey) {
        /* Define the preference */
        final PrometheusByteArrayPreference myPref = new PrometheusByteArrayPreference(this, pKey);

        /* Add it to the list of preferences */
        definePreference(myPref);

        /* Return the preference */
        return myPref;
    }

    /**
     * Define new CharArray preference.
     *
     * @param pKey the key for the preference
     * @return the preference item
     * @throws OceanusException on error
     */
    protected PrometheusCharArrayPreference defineCharArrayPreference(final MetisPreferenceKey pKey) throws OceanusException {
        /* Define the preference */
        final PrometheusCharArrayPreference myPref = new PrometheusCharArrayPreference(this, pKey);

        /* Add it to the list of preferences */
        definePreference(myPref);

        /* Return the preference */
        return myPref;
    }

    /**
     * Obtain ByteArray preference.
     *
     * @param pKey the key of the preference
     * @return the ByteArray preference
     */
    public PrometheusByteArrayPreference getByteArrayPreference(final MetisPreferenceKey pKey) {
        /* Access preference */
        final MetisPreferenceItem myPref = getPreference(pKey);

        /* Reject if not found */
        if (myPref == null) {
            throw new IllegalArgumentException(ERROR_UNKNOWN
                    + pKey);
        }

        /* Reject if wrong type */
        if (!(myPref instanceof PrometheusByteArrayPreference)) {
            throw new IllegalArgumentException(ERROR_INVALID
                    + pKey);
        }

        /* Return the preference */
        return (PrometheusByteArrayPreference) myPref;
    }

    /**
     * Obtain ByteArray value.
     *
     * @param pKey the key of the preference
     * @return the ByteArray value
     */
    public byte[] getByteArrayValue(final MetisPreferenceKey pKey) {
        /* Access preference */
        final PrometheusByteArrayPreference myPref = getByteArrayPreference(pKey);

        /* Return the value */
        return myPref.getValue();
    }

    /**
     * Obtain CharArray preference.
     *
     * @param pKey the key of the preference
     * @return the CharArray preference
     */
    public PrometheusCharArrayPreference getCharArrayPreference(final MetisPreferenceKey pKey) {
        /* Access preference */
        final MetisPreferenceItem myPref = getPreference(pKey);

        /* Reject if not found */
        if (myPref == null) {
            throw new IllegalArgumentException(ERROR_UNKNOWN
                    + pKey);
        }

        /* Reject if wrong type */
        if (!(myPref instanceof PrometheusCharArrayPreference)) {
            throw new IllegalArgumentException(ERROR_INVALID
                    + pKey);
        }

        /* Return the preference */
        return (PrometheusCharArrayPreference) myPref;
    }

    /**
     * Obtain CharArray value.
     *
     * @param pKey the key of the preference
     * @return the CharArray value
     */
    public char[] getCharArrayValue(final MetisPreferenceKey pKey) {
        /* Access preference */
        final PrometheusCharArrayPreference myPref = getCharArrayPreference(pKey);

        /* Return the value */
        return myPref.getValue();
    }

    /**
     * ByteArray preference.
     */
    public static class PrometheusByteArrayPreference
            extends MetisPreferenceItem {
        /**
         * Constructor.
         *
         * @param pSet the preference Set
         * @param pKey the key of the preference
         */
        protected PrometheusByteArrayPreference(final PrometheusPreferenceSet pSet,
                                                final MetisPreferenceKey pKey) {
            /* Store name */
            super(pSet, pKey, PrometheusPreferenceType.BYTEARRAY);

            /* Check whether we have an existing value */
            if (pSet.checkExists(pKey)) {
                /* Access the value */
                final byte[] myValue = getHandle().getByteArray(getPreferenceName(), null);

                /* Set as initial value */
                setTheValue(myValue);
            }
        }

        @Override
        public byte[] getValue() {
            return (byte[]) super.getValue();
        }

        /**
         * Set value.
         *
         * @param pNewValue the new value
         */
        public void setValue(final byte[] pNewValue) {
            setNewValue(pNewValue);
        }

        @Override
        protected void storeThePreference(final Object pNewValue) {
            getHandle().putByteArray(getPreferenceName(), (byte[]) pNewValue);
        }
    }

    /**
     * CharArray preference.
     */
    public static class PrometheusCharArrayPreference
            extends MetisPreferenceItem {
        /**
         * Constructor.
         *
         * @param pSet the preference Set
         * @param pKey the key of the preference
         * @throws OceanusException on error
         */
        protected PrometheusCharArrayPreference(final PrometheusPreferenceSet pSet,
                                                final MetisPreferenceKey pKey) throws OceanusException {
            /* Store name */
            super(pSet, pKey, PrometheusPreferenceType.CHARARRAY);

            /* Check whether we have an existing value */
            if (pSet.checkExists(pKey)) {
                /* Access the value */
                final byte[] myBytes = getHandle().getByteArray(getPreferenceName(), null);
                final PrometheusPreferenceSecurity mySecurity = getSet().getSecurity();

                /* Decrypt the value */
                final char[] myValue = myBytes == null
                        ? null
                        : mySecurity.decryptValue(myBytes);

                /* Set as initial value */
                setTheValue(myValue);
            }
        }

        @Override
        protected PrometheusPreferenceSet getSet() {
            return (PrometheusPreferenceSet) super.getSet();
        }

        @Override
        public char[] getValue() {
            return (char[]) super.getValue();
        }

        /**
         * Set value.
         *
         * @param pNewValue the new value
         */
        public void setValue(final char[] pNewValue) {
            setNewValue(pNewValue);
        }

        @Override
        protected void storeThePreference(final Object pNewValue) throws OceanusException {
            /* Store the value */
            final PrometheusPreferenceSecurity mySecurity = getSet().getSecurity();
            getHandle().putByteArray(getPreferenceName(), mySecurity.encryptValue((char[]) pNewValue));
        }
    }
}
