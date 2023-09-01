/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2023 Tony Washer
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
package net.sourceforge.joceanus.jprometheus.atlas.preference;

import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceKey;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceSet;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceType;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.resource.TethysBundleId;

/**
 * Prometheus PreferenceSet.
 */
public abstract class PrometheusPreferenceSet
    extends MetisPreferenceSet {
    /**
     * The Security Manager.
     */
    private final PrometheusPreferenceSecurity theSecurity;

    /**
     * Constructor.
     * @param pManager the preference manager
     * @param pId the Id
     * @throws OceanusException on error
     */
    protected PrometheusPreferenceSet(final PrometheusPreferenceManager pManager,
                                      final TethysBundleId pId) throws OceanusException {
        this(pManager, pId.getValue());
    }

    /**
     * Constructor.
     * @param pManager the preference manager
     * @param pName the name
     * @throws OceanusException on error
     */
    protected PrometheusPreferenceSet(final PrometheusPreferenceManager pManager,
                                      final String pName) throws OceanusException {
        /* Initialize super-class */
        super(pManager, pName);

        /* Store security manager */
        theSecurity = pManager.getSecurity1();
    }

    /**
     * Define new CharArray preference.
     * @param pKey the key for the preference
     * @return the preference item
     * @throws OceanusException on error
     */
    protected PrometheusCharArrayPreference defineCharArray1Preference(final MetisPreferenceKey pKey) throws OceanusException {
        /* Define the preference */
        final PrometheusCharArrayPreference myPref = new PrometheusCharArrayPreference(this, pKey);

        /* Add it to the list of preferences */
        definePreference(myPref);

        /* Return the preference */
        return myPref;
    }

    /**
     * Obtain CharArray preference.
     * @param pKey the key of the preference
     * @return the CharArray preference
     */
    public PrometheusCharArrayPreference getCharArray1Preference(final MetisPreferenceKey pKey) {
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
     * @param pKey the key of the preference
     * @return the CharArray value
     */
    public char[] getCharArray1Value(final MetisPreferenceKey pKey) {
        /* Access preference */
        final PrometheusCharArrayPreference myPref = getCharArray1Preference(pKey);

        /* Return the value */
        return myPref.getValue();
    }

    /**
     * CharArray preference.
     */
    public static class PrometheusCharArrayPreference
            extends MetisPreferenceItem {
        /**
         * Constructor.
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

                /* Decrypt the value */
                final char[] myValue = myBytes == null
                        ? null
                        : pSet.theSecurity.decryptValue(myBytes);

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
         * @param pNewValue the new value
         */
        public void setValue(final char[] pNewValue) {
            setNewValue(pNewValue);
        }

        @Override
        protected void storeThePreference(final Object pNewValue) throws OceanusException {
            /* Store the value */
            getHandle().putByteArray(getPreferenceName(), getSet().theSecurity.encryptValue((char[]) pNewValue));
        }
    }
}
