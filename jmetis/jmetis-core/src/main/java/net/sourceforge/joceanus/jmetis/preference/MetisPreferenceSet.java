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

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import net.sourceforge.joceanus.jmetis.MetisDataException;
import net.sourceforge.joceanus.jmetis.data.MetisDifference;
import net.sourceforge.joceanus.jmetis.data.MetisFieldValue;
import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSetItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldState;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;

/**
 * Wrapper class for java preferences.
 * @author Tony Washer
 * @param <K> the Key type
 */
public abstract class MetisPreferenceSet<K extends Enum<K> & MetisPreferenceKey>
        implements MetisFieldSetItem, TethysEventProvider<MetisPreferenceEvent> {
    /**
     * Unknown preference string.
     */
    protected static final String ERROR_UNKNOWN = "Unknown Preference: ";

    /**
     * Invalid preference string.
     */
    protected static final String ERROR_INVALID = "Invalid Preference: ";

    /**
     * The Event Manager.
     */
    private final TethysEventManager<MetisPreferenceEvent> theEventManager;

    /**
     * The Security Manager.
     */
    private final MetisPreferenceSecurity theSecurityManager;

    /**
     * Report fields.
     */
    private final MetisFields theFields = new MetisFields(MetisPreferenceSet.class.getSimpleName());

    /**
     * The Preference node for this set.
     */
    private final Preferences theHandle;

    /**
     * The map of preferences.
     */
    private Map<String, MetisPreferenceItem<K>> theMap;

    /**
     * The list of preferences that have a value on initialisation.
     */
    private String[] theActive;

    /**
     * The name of the preferenceSet.
     */
    private String theName;

    /**
     * Is this a hidden preferenceSet.
     */
    private boolean isHidden;

    /**
     * Constructor.
     * @param pManager the preference manager
     * @throws OceanusException on error
     */
    protected MetisPreferenceSet(final MetisPreferenceManager pManager) throws OceanusException {
        /* Store security manager */
        theSecurityManager = pManager.getSecurity();

        /* Access the handle */
        theHandle = deriveHandle();

        /* Create Event Manager */
        theEventManager = new TethysEventManager<>();

        /* Allocate the preference map */
        theMap = new LinkedHashMap<>();

        /* Access the active key names */
        try {
            theActive = theHandle.keys();
        } catch (BackingStoreException e) {
            throw new MetisDataException("Failed to access preferences", e);
        }
    }

    @Override
    public MetisFields getDataFields() {
        return theFields;
    }

    @Override
    public String getFieldErrors(final MetisField pField) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getFieldErrors(final MetisField[] pFields) {
        throw new UnsupportedOperationException();
    }

    @Override
    public MetisFieldState getItemState() {
        throw new UnsupportedOperationException();
    }

    @Override
    public MetisFieldState getFieldState(final MetisField pField) {
        /* Access preference */
        MetisPreferenceItem<K> myPref = getPreference(pField.getName());

        /* If it is found */
        if (myPref != null) {
            /* Return the relevant state */
            return myPref.isChanged()
                                      ? MetisFieldState.CHANGED
                                      : MetisFieldState.NORMAL;
        }

        /* Not recognised */
        return MetisFieldState.NORMAL;
    }

    @Override
    public String formatObject() {
        return theFields.getName();
    }

    @Override
    public Object getFieldValue(final MetisField pField) {
        /* Access preference */
        MetisPreferenceItem<K> myPref = getPreference(pField.getName());

        /* Return the value */
        return (myPref == null)
                                ? MetisFieldValue.UNKNOWN
                                : myPref.getValue();
    }

    @Override
    public boolean isDisabled() {
        return false;
    }

    @Override
    public TethysEventRegistrar<MetisPreferenceEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * Obtain the name of the set.
     * @return the name
     */
    public String getName() {
        return theName;
    }

    /**
     * Set the name of the set.
     * @param pName the name
     */
    protected void setName(final String pName) {
        theName = pName;
    }

    /**
     * Is this a hidden preferenceSet?
     * @return true/false
     */
    protected boolean isHidden() {
        return isHidden;
    }

    /**
     * Set this preferenceSet as hidden.
     */
    protected void setHidden() {
        isHidden = true;
    }

    /**
     * Obtain the collection of preferences.
     * @return the preferences
     */
    public Collection<MetisPreferenceItem<K>> getPreferences() {
        return theMap.values();
    }

    @Override
    public boolean isEditable() {
        return true;
    }

    /**
     * Derive handle for node.
     * @return the class name
     */
    private Preferences deriveHandle() {
        /* Obtain the class name */
        Class<?> myClass = this.getClass();
        String myName = myClass.getCanonicalName();

        /* Obtain the package name */
        String myPackage = myClass.getPackage().getName();

        /* Strip off the package name */
        myName = myName.substring(myPackage.length() + 1);

        /* Derive the handle */
        Preferences myHandle = Preferences.userNodeForPackage(myClass);
        return myHandle.node(myName);
    }

    /**
     * Define new String preference.
     * @param pKey the key for the preference
     * @param pDefault the default value
     * @return the preference item
     */
    protected MetisStringPreference<K> defineStringPreference(final K pKey,
                                                              final String pDefault) {
        /* Define the preference */
        MetisStringPreference<K> myPref = new MetisStringPreference<>(this, pKey);

        /* Add it to the list of preferences */
        definePreference(myPref);

        /* Initialise default if required */
        if (!myPref.isAvailable()) {
            myPref.setValue(pDefault);
        }

        /* Return the preference */
        return myPref;
    }

    /**
     * Define new File preference.
     * @param pKey the key for the preference
     * @param pDefault the default value
     * @return the preference item
     */
    protected MetisStringPreference<K> defineFilePreference(final K pKey,
                                                            final String pDefault) {
        /* Define the preference */
        MetisStringPreference<K> myPref = new MetisStringPreference<>(this, pKey, MetisPreferenceType.FILE);

        /* Add it to the list of preferences */
        definePreference(myPref);

        /* Initialise default if required */
        if (!myPref.isAvailable()) {
            myPref.setValue(pDefault);
        }

        /* Return the preference */
        return myPref;
    }

    /**
     * Define new Directory preference.
     * @param pKey the key for the preference
     * @param pDefault the default value
     * @return the preference item
     */
    protected MetisStringPreference<K> defineDirectoryPreference(final K pKey,
                                                                 final String pDefault) {
        /* Define the preference */
        MetisStringPreference<K> myPref = new MetisStringPreference<>(this, pKey, MetisPreferenceType.DIRECTORY);

        /* Add it to the list of preferences */
        definePreference(myPref);

        /* Initialise default if required */
        if (!myPref.isAvailable()) {
            myPref.setValue(pDefault);
        }

        /* Return the preference */
        return myPref;
    }

    /**
     * Define new Colour preference.
     * @param pKey the key for the preference
     * @param pDefault the default value
     * @return the preference item
     */
    protected MetisStringPreference<K> defineColorPreference(final K pKey,
                                                             final String pDefault) {
        /* Define the preference */
        MetisStringPreference<K> myPref = new MetisStringPreference<>(this, pKey, MetisPreferenceType.COLOR);

        /* Add it to the list of preferences */
        definePreference(myPref);

        /* Initialise default if required */
        if (!myPref.isAvailable()) {
            myPref.setValue(pDefault);
        }

        /* Return the preference */
        return myPref;
    }

    /**
     * Define new Integer preference.
     * @param pKey the key for the preference
     * @param pDefault the default value
     * @return the preference item
     */
    protected MetisIntegerPreference<K> defineIntegerPreference(final K pKey,
                                                                final Integer pDefault) {
        /* Define the preference */
        MetisIntegerPreference<K> myPref = new MetisIntegerPreference<>(this, pKey);

        /* Add it to the list of preferences */
        definePreference(myPref);

        /* Initialise default if required */
        if (!myPref.isAvailable()) {
            myPref.setValue(pDefault);
        }

        /* Return the preference */
        return myPref;
    }

    /**
     * Define new Boolean preference.
     * @param pKey the key for the preference
     * @param pDefault the default value
     * @return the preference item
     */
    protected MetisBooleanPreference<K> defineBooleanPreference(final K pKey,
                                                                final Boolean pDefault) {
        /* Define the preference */
        MetisBooleanPreference<K> myPref = new MetisBooleanPreference<>(this, pKey);

        /* Add it to the list of preferences */
        definePreference(myPref);

        /* Initialise default if required */
        if (!myPref.isAvailable()) {
            myPref.setValue(pDefault);
        }

        /* Return the preference */
        return myPref;
    }

    /**
     * Define new Date preference.
     * @param pKey the key for the preference
     * @param pDefault the default value
     * @return the preference item
     */
    protected MetisDatePreference<K> defineDatePreference(final K pKey,
                                                          final TethysDate pDefault) {
        /* Define the preference */
        MetisDatePreference<K> myPref = new MetisDatePreference<>(this, pKey);

        /* Add it to the list of preferences */
        definePreference(myPref);

        /* Initialise default if required */
        if (!myPref.isAvailable()) {
            myPref.setValue(pDefault);
        }
        /* Return the preference */
        return myPref;
    }

    /**
     * Define new Enum preference.
     * @param <E> the Enum type
     * @param pKey the key for the preference
     * @param pDefault the default value
     * @param pClass the Enum class
     * @return the newly created preference
     */
    protected <E extends Enum<E>> MetisEnumPreference<K, E> defineEnumPreference(final K pKey,
                                                                                 final E pDefault,
                                                                                 final Class<E> pClass) {
        /* Create the preference */
        MetisEnumPreference<K, E> myPref = new MetisEnumPreference<>(this, pKey, pClass);

        /* Add it to the list of preferences */
        definePreference(myPref);

        /* Initialise default if required */
        if (!myPref.isAvailable()) {
            myPref.setValue(pDefault);
        }

        /* Return the preference */
        return myPref;
    }

    /**
     * Define new ByteArray preference.
     * @param pKey the key for the preference
     * @return the preference item
     */
    protected MetisByteArrayPreference<K> defineByteArrayPreference(final K pKey) {
        /* Define the preference */
        MetisByteArrayPreference<K> myPref = new MetisByteArrayPreference<>(this, pKey);

        /* Add it to the list of preferences */
        definePreference(myPref);

        /* Return the preference */
        return myPref;
    }

    /**
     * Define new CharArray preference.
     * @param pKey the key for the preference
     * @param pDefault the default value
     * @return the preference item
     * @throws OceanusException on error
     */
    protected MetisCharArrayPreference<K> defineCharArrayPreference(final K pKey,
                                                                    final char[] pDefault) throws OceanusException {
        /* Define the preference */
        MetisCharArrayPreference<K> myPref = new MetisCharArrayPreference<>(this, pKey);

        /* Add it to the list of preferences */
        definePreference(myPref);

        /* Initialise default if required */
        if (!myPref.isAvailable()) {
            myPref.setValue(pDefault);
        }

        /* Return the preference */
        return myPref;
    }

    /**
     * Define a preference for the node.
     * @param pPreference the preference to define
     */
    private void definePreference(final MetisPreferenceItem<K> pPreference) {
        /* Access the key of the preference */
        String myName = pPreference.getName();

        /* Reject if the name is already present */
        if (theMap.get(myName) != null) {
            throw new IllegalArgumentException("preference "
                                               + myName
                                               + " is already defined");
        }

        /* Add the preference to the map */
        theMap.put(myName, pPreference);
    }

    /**
     * Obtain preference by key.
     * @param pKey the key of the preference
     * @return the preference
     */
    public MetisPreferenceItem<K> getPreference(final K pKey) {
        return theMap.get(pKey.getName());
    }

    /**
     * Obtain preference.
     * @param pName the name of the preference
     * @return the preference
     */
    protected MetisPreferenceItem<K> getPreference(final String pName) {
        return theMap.get(pName);
    }

    /**
     * Obtain String value.
     * @param pKey the key of the preference
     * @return the String value
     */
    public String getStringValue(final K pKey) {
        /* Access preference */
        MetisPreferenceItem<K> myPref = getPreference(pKey);

        /* Reject if not found */
        if (myPref == null) {
            throw new IllegalArgumentException(ERROR_UNKNOWN
                                               + pKey);
        }

        /* Reject if wrong type */
        if (!(myPref instanceof MetisStringPreference)) {
            throw new IllegalArgumentException(ERROR_INVALID
                                               + pKey);
        }

        /* Access preference */
        MetisStringPreference<K> myStringPref = (MetisStringPreference<K>) myPref;

        /* Return the value */
        return myStringPref.getValue();
    }

    /**
     * Obtain Integer value.
     * @param pKey the key of the preference
     * @return the Integer value
     */
    public Integer getIntegerValue(final K pKey) {
        /* Access preference */
        MetisPreferenceItem<K> myPref = getPreference(pKey);

        /* Reject if not found */
        if (myPref == null) {
            throw new IllegalArgumentException(ERROR_UNKNOWN
                                               + pKey);
        }

        /* Reject if wrong type */
        if (!(myPref instanceof MetisPreferenceSet.MetisIntegerPreference)) {
            throw new IllegalArgumentException(ERROR_INVALID
                                               + pKey);
        }

        /* Access preference */
        MetisIntegerPreference<K> myIntPref = (MetisIntegerPreference<K>) myPref;

        /* Return the preference */
        return myIntPref.getValue();
    }

    /**
     * Obtain Boolean value.
     * @param pKey the key of the preference
     * @return the Boolean value
     */
    public Boolean getBooleanValue(final K pKey) {
        /* Access preference */
        MetisPreferenceItem<K> myPref = getPreference(pKey);

        /* Reject if not found */
        if (myPref == null) {
            throw new IllegalArgumentException(ERROR_UNKNOWN
                                               + pKey);
        }

        /* Reject if wrong type */
        if (!(myPref instanceof MetisBooleanPreference)) {
            throw new IllegalArgumentException(ERROR_INVALID
                                               + pKey);
        }

        /* Access preference */
        MetisBooleanPreference<K> myBoolPref = (MetisBooleanPreference<K>) myPref;

        /* Return the value */
        return myBoolPref.getValue();
    }

    /**
     * Obtain Date value.
     * @param pKey the key of the preference
     * @return the Date
     */
    public TethysDate getDateValue(final K pKey) {
        /* Access preference */
        MetisPreferenceItem<K> myPref = getPreference(pKey);

        /* Reject if not found */
        if (myPref == null) {
            throw new IllegalArgumentException(ERROR_UNKNOWN
                                               + pKey);
        }

        /* Reject if wrong type */
        if (!(myPref instanceof MetisDatePreference)) {
            throw new IllegalArgumentException(ERROR_INVALID
                                               + pKey);
        }

        /* Access preference */
        MetisDatePreference<K> myDatePref = (MetisDatePreference<K>) myPref;

        /* Return the value */
        return myDatePref.getValue();
    }

    /**
     * Obtain Enum value.
     * @param <E> the EnumType
     * @param pKey the key of the preference
     * @param pClass the Enum class
     * @return the Enum or null if no such Enum preference exists
     */
    public <E extends Enum<E>> E getEnumValue(final K pKey,
                                              final Class<E> pClass) {
        /* Access preference */
        MetisPreferenceItem<K> myPref = getPreference(pKey);

        /* Reject if not found */
        if (myPref == null) {
            throw new IllegalArgumentException(ERROR_UNKNOWN
                                               + pKey);
        }

        /* Reject if wrong type */
        if (!(myPref instanceof MetisEnumPreference)) {
            throw new IllegalArgumentException(ERROR_INVALID
                                               + pKey);
        }

        /* Access as Enum preference */
        @SuppressWarnings("unchecked")
        MetisEnumPreference<K, E> myEnumPref = (MetisEnumPreference<K, E>) myPref;
        if (!myEnumPref.theClass.equals(pClass)) {
            throw new IllegalArgumentException(ERROR_INVALID
                                               + pKey);
        }

        /* Return the value */
        return myEnumPref.getValue();
    }

    /**
     * Obtain ByteArray value.
     * @param pKey the key of the preference
     * @return the ByteArray value
     */
    public byte[] getByteArrayValue(final K pKey) {
        /* Access preference */
        MetisPreferenceItem<K> myPref = getPreference(pKey);

        /* Reject if not found */
        if (myPref == null) {
            throw new IllegalArgumentException(ERROR_UNKNOWN
                                               + pKey);
        }

        /* Reject if wrong type */
        if (!(myPref instanceof MetisByteArrayPreference)) {
            throw new IllegalArgumentException(ERROR_INVALID
                                               + pKey);
        }

        /* Access preference */
        MetisByteArrayPreference<?> myByteArrayPref = (MetisByteArrayPreference<K>) myPref;

        /* Return the value */
        return myByteArrayPref.getValue();
    }

    /**
     * Obtain ByteArray value.
     * @param pKey the key of the preference
     * @return the ByteArray value
     */
    public char[] getCharArrayValue(final K pKey) {
        /* Access preference */
        MetisPreferenceItem<K> myPref = getPreference(pKey);

        /* Reject if not found */
        if (myPref == null) {
            throw new IllegalArgumentException(ERROR_UNKNOWN
                                               + pKey);
        }

        /* Reject if wrong type */
        if (!(myPref instanceof MetisCharArrayPreference)) {
            throw new IllegalArgumentException(ERROR_INVALID
                                               + pKey);
        }

        /* Access preference */
        MetisCharArrayPreference<K> myCharArrayPref = (MetisCharArrayPreference<K>) myPref;

        /* Return the value */
        return myCharArrayPref.getValue();
    }

    /**
     * Reset all changes in this preference set.
     */
    public void resetChanges() {
        /* Loop through all the preferences */
        for (MetisPreferenceItem<K> myPref : theMap.values()) {
            /* Reset the changes */
            myPref.resetChanges();
        }
    }

    /**
     * Store preference changes.
     * @throws OceanusException on error
     */
    public final void storeChanges() throws OceanusException {
        /* Loop through all the preferences */
        for (MetisPreferenceItem<K> myPref : theMap.values()) {
            /* Store any changes */
            myPref.storePreference();
        }

        /* Protect against exceptions */
        try {
            /* Flush the output */
            theHandle.flush();

            /* Notify listeners */
            theEventManager.fireEvent(MetisPreferenceEvent.PREFCHANGED);

        } catch (BackingStoreException e) {
            throw new MetisDataException("Failed to flush preferences to store", e);
        }
    }

    /**
     * Does the preference set have changes.
     * @return true/false
     */
    public boolean hasChanges() {
        /* Loop through all the preferences */
        for (MetisPreferenceItem<K> myPref : theMap.values()) {
            /* Check for changes */
            if (myPref.isChanged()) {
                return true;
            }
        }

        /* Return no changes */
        return false;
    }

    /**
     * Check whether a preference exists.
     * @param pKey the key of the preference
     * @return whether the preference already exists
     */
    protected boolean checkExists(final K pKey) {
        /* Obtain the name */
        String myKeyName = pKey.getName();

        /* Loop through all the keys */
        for (String myName : theActive) {
            /* If the name matches return true */
            if (myName.equals(myKeyName)) {
                return true;
            }
        }

        /* return no match */
        return false;
    }

    /**
     * Underlying preference item class.
     * @param <K> the keyType
     */
    public abstract static class MetisPreferenceItem<K extends Enum<K> & MetisPreferenceKey> {
        /**
         * preferenceSet.
         */
        private final MetisPreferenceSet<K> theSet;

        /**
         * preference Key.
         */
        private final K theKey;

        /**
         * preference Name.
         */
        private final String theName;

        /**
         * DataField.
         */
        private final MetisField theField;

        /**
         * Display Name.
         */
        private final String theDisplay;

        /**
         * preference Type.
         */
        private final MetisPreferenceType theType;

        /**
         * preference Value.
         */
        private Object theValue;

        /**
         * New preference Value.
         */
        private Object theNewValue;

        /**
         * Is there a change to the preference.
         */
        private boolean isChanged;

        /**
         * Constructor.
         * @param pSet the preference Set
         * @param pKey the key of the preference
         * @param pType the type of the preference
         */
        protected MetisPreferenceItem(final MetisPreferenceSet<K> pSet,
                                      final K pKey,
                                      final MetisPreferenceType pType) {
            /* Store parameters */
            theSet = pSet;
            theKey = pKey;
            theType = pType;

            /* Obtain key details */
            theName = pKey.getName();
            theDisplay = pKey.getDisplay();

            /* Create the DataField */
            theField = theSet.theFields.declareLocalField(theName);
        }

        /**
         * Obtain the preferenceSet.
         * @return the set
         */
        protected MetisPreferenceSet<K> getSet() {
            return theSet;
        }

        /**
         * Obtain the preference handle.
         * @return the preference handle
         */
        protected Preferences getHandle() {
            return theSet.theHandle;
        }

        /**
         * Obtain the key of the preference.
         * @return the key of the preference
         */
        protected K getKey() {
            return theKey;
        }

        /**
         * Obtain the name of the preference.
         * @return the name of the preference
         */
        protected String getName() {
            return theName;
        }

        /**
         * Obtain the display name of the preference.
         * @return the display name of the preference
         */
        protected String getDisplay() {
            return theDisplay;
        }

        /**
         * Obtain the type of the preference.
         * @return the type of the preference
         */
        protected MetisPreferenceType getType() {
            return theType;
        }

        /**
         * Obtain the field for the preference.
         * @return the field for the preference
         */
        protected MetisField getDataField() {
            return theField;
        }

        /**
         * Obtain the value of the preference.
         * @return the value of the preference
         */
        protected Object getValue() {
            /* Return the active value */
            return isChanged
                             ? theNewValue
                             : theValue;
        }

        /**
         * Is the preference available.
         * @return is the preference available
         */
        public boolean isAvailable() {
            return getValue() != null;
        }

        /**
         * Is the preference changed.
         * @return is the preference changed
         */
        protected boolean isChanged() {
            return isChanged;
        }

        /**
         * Set value.
         * @param pValue the value
         */
        protected void setTheValue(final Object pValue) {
            theValue = pValue;
        }

        /**
         * Set new value.
         * @param pNewValue the new value
         */
        protected void setNewValue(final Object pNewValue) {
            theNewValue = pNewValue;
            isChanged = !MetisDifference.isEqual(theNewValue, theValue);
        }

        /**
         * Reset changes.
         */
        private void resetChanges() {
            /* Reset change indicators */
            theNewValue = null;
            isChanged = false;
        }

        /**
         * Store preference.
         * @throws OceanusException on error
         */
        private void storePreference() throws OceanusException {
            /* If the preference has changed */
            if (isChanged) {
                /* If we have a value */
                if (theNewValue != null) {
                    /* Store the value */
                    storeThePreference(theNewValue);

                    /* Note the new value and reset changes */
                    setTheValue(theNewValue);

                    /* else no value */
                } else {
                    /* remove the preference */
                    getHandle().remove(theName);
                }

                /* reset changes */
                resetChanges();
            }
        }

        /**
         * Store the value of the preference.
         * @param pNewValue the new value to store
         * @throws OceanusException on error
         */
        protected abstract void storeThePreference(final Object pNewValue) throws OceanusException;
    }

    /**
     * String preference.
     * @param <K> the keyType
     */
    public static class MetisStringPreference<K extends Enum<K> & MetisPreferenceKey>
            extends MetisPreferenceItem<K> {
        /**
         * Constructor.
         * @param pSet the preference Set
         * @param pKey the key of the preference
         */
        protected MetisStringPreference(final MetisPreferenceSet<K> pSet,
                                        final K pKey) {
            this(pSet, pKey, MetisPreferenceType.STRING);
        }

        /**
         * Constructor.
         * @param pSet the preference Set
         * @param pKey the key of the preference
         * @param pType the type of the preference
         */
        private MetisStringPreference(final MetisPreferenceSet<K> pSet,
                                      final K pKey,
                                      final MetisPreferenceType pType) {
            /* Store name */
            super(pSet, pKey, pType);

            /* Check whether we have an existing value */
            if (pSet.checkExists(pKey)) {
                /* Access the value */
                String myValue = getHandle().get(getName(), null);

                /* Set as initial value */
                setTheValue(myValue);
            }
        }

        @Override
        public String getValue() {
            return (String) super.getValue();
        }

        /**
         * Set value.
         * @param pNewValue the new value
         */
        public void setValue(final String pNewValue) {
            setNewValue(pNewValue);
        }

        @Override
        protected void storeThePreference(final Object pNewValue) {
            getHandle().put(getName(), (String) pNewValue);
        }
    }

    /**
     * Integer preference.
     * @param <K> the keyType
     */
    public static class MetisIntegerPreference<K extends Enum<K> & MetisPreferenceKey>
            extends MetisPreferenceItem<K> {
        /**
         * Constructor.
         * @param pSet the preference Set
         * @param pKey the key of the preference
         */
        protected MetisIntegerPreference(final MetisPreferenceSet<K> pSet,
                                         final K pKey) {
            /* Store name */
            super(pSet, pKey, MetisPreferenceType.INTEGER);

            /* Check whether we have an existing value */
            if (pSet.checkExists(pKey)) {
                /* Access the value */
                int myValue = getHandle().getInt(getName(), -1);

                /* Set as initial value */
                setTheValue(Integer.valueOf(myValue));
            }
        }

        @Override
        public Integer getValue() {
            return (Integer) super.getValue();
        }

        /**
         * Set value.
         * @param pNewValue the new value
         */
        public void setValue(final Integer pNewValue) {
            setNewValue(pNewValue);
        }

        @Override
        protected void storeThePreference(final Object pNewValue) {
            getHandle().putInt(getName(), (Integer) pNewValue);
        }
    }

    /**
     * Boolean preference.
     * @param <K> the keyType
     */
    public static class MetisBooleanPreference<K extends Enum<K> & MetisPreferenceKey>
            extends MetisPreferenceItem<K> {
        /**
         * Constructor.
         * @param pSet the preference Set
         * @param pKey the key of the preference
         */
        protected MetisBooleanPreference(final MetisPreferenceSet<K> pSet,
                                         final K pKey) {
            /* Store name */
            super(pSet, pKey, MetisPreferenceType.BOOLEAN);

            /* Check whether we have an existing value */
            if (pSet.checkExists(pKey)) {
                /* Access the value */
                boolean myValue = getHandle().getBoolean(getName(), false);

                /* Set as initial value */
                setTheValue(myValue
                                    ? Boolean.TRUE
                                    : Boolean.FALSE);
            }
        }

        @Override
        public Boolean getValue() {
            return (Boolean) super.getValue();
        }

        /**
         * Set value.
         * @param pNewValue the new value
         */
        public void setValue(final Boolean pNewValue) {
            Boolean myNewValue = pNewValue;

            /* Take a copy if not null */
            if (myNewValue != null) {
                myNewValue = myNewValue
                                        ? Boolean.TRUE
                                        : Boolean.FALSE;
            }

            /* Set the new value */
            setNewValue(myNewValue);
        }

        @Override
        protected void storeThePreference(final Object pNewValue) {
            getHandle().putBoolean(getName(), (Boolean) pNewValue);
        }
    }

    /**
     * Date preference.
     * @param <K> the keyType
     */
    public static class MetisDatePreference<K extends Enum<K> & MetisPreferenceKey>
            extends MetisPreferenceItem<K> {
        /**
         * Constructor.
         * @param pSet the preference Set
         * @param pKey the key of the preference
         */
        protected MetisDatePreference(final MetisPreferenceSet<K> pSet,
                                      final K pKey) {
            /* Store name */
            super(pSet, pKey, MetisPreferenceType.DATE);

            /* Check whether we have an existing value */
            if (pSet.checkExists(pKey)) {
                /* Access the value */
                String myValue = getHandle().get(getName(), null);

                /* Parse the Date */
                TethysDate myDate = new TethysDate(myValue);

                /* Set as initial value */
                setTheValue(myDate);
            }
        }

        @Override
        public TethysDate getValue() {
            return (TethysDate) super.getValue();
        }

        /**
         * Set value.
         * @param pNewValue the new value
         */
        public void setValue(final TethysDate pNewValue) {
            TethysDate myNewValue = pNewValue;

            /* Take a copy if not null */
            if (myNewValue != null) {
                myNewValue = new TethysDate(myNewValue);
            }

            /* Set the new value */
            setNewValue(myNewValue);
        }

        @Override
        protected void storeThePreference(final Object pNewValue) {
            getHandle().put(getName(), ((TethysDate) pNewValue).toString());
        }
    }

    /**
     * Enum preference.
     * @param <K> the keyType
     * @param <E> the Enum type
     */
    public static class MetisEnumPreference<K extends Enum<K> & MetisPreferenceKey, E extends Enum<E>>
            extends MetisPreferenceItem<K> {
        /**
         * The enum class.
         */
        private final Class<E> theClass;

        /**
         * The enum values.
         */
        private final E[] theValues;

        /**
         * Constructor.
         * @param pSet the preference Set
         * @param pKey the key of the preference
         * @param pClass the class of the preference
         */
        public MetisEnumPreference(final MetisPreferenceSet<K> pSet,
                                   final K pKey,
                                   final Class<E> pClass) {
            /* Store name */
            super(pSet, pKey, MetisPreferenceType.ENUM);

            /* Store the class */
            theClass = pClass;
            theValues = theClass.getEnumConstants();

            /* Check whether we have an existing value */
            if (pSet.checkExists(pKey)) {
                /* Access the value */
                String myValue = getHandle().get(getName(), null);
                /* Set the value */
                E myEnum = findValue(myValue);
                setTheValue(myEnum);
            }
        }

        @Override
        public E getValue() {
            return theClass.cast(super.getValue());
        }

        /**
         * Obtain the values of the preference.
         * @return the values of the preference
         */
        public E[] getValues() {
            return Arrays.copyOf(theValues, theValues.length);
        }

        /**
         * Set value.
         * @param pNewValue the new value
         * @return the Enum value
         */
        private E findValue(final String pNewValue) {
            /* Loop through the Enum constants */
            for (E myEnum : theValues) {
                /* If we match */
                if (pNewValue.equals(myEnum.name())) {
                    /* Return the value */
                    return myEnum;
                }
            }

            /* Return invalid value */
            return null;
        }

        /**
         * Set value.
         * @param pNewValue the new value
         */
        public final void setValue(final String pNewValue) {
            /* Convert to enum and set */
            E myEnum = findValue(pNewValue);
            setNewValue(myEnum);
        }

        /**
         * Set value.
         * @param pNewValue the new value
         */
        public void setValue(final E pNewValue) {
            setNewValue(pNewValue);
        }

        @Override
        protected void storeThePreference(final Object pNewValue) {
            getHandle().put(getName(), theClass.cast(pNewValue).name());
        }
    }

    /**
     * ByteArray preference.
     * @param <K> the keyType
     */
    public static class MetisByteArrayPreference<K extends Enum<K> & MetisPreferenceKey>
            extends MetisPreferenceItem<K> {
        /**
         * Constructor.
         * @param pSet the preference Set
         * @param pKey the key of the preference
         */
        protected MetisByteArrayPreference(final MetisPreferenceSet<K> pSet,
                                           final K pKey) {
            /* Store name */
            super(pSet, pKey, MetisPreferenceType.BYTEARRAY);

            /* Check whether we have an existing value */
            if (pSet.checkExists(pKey)) {
                /* Access the value */
                byte[] myValue = getHandle().getByteArray(getName(), null);

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
         * @param pNewValue the new value
         */
        public void setValue(final byte[] pNewValue) {
            setNewValue(pNewValue);
        }

        @Override
        protected void storeThePreference(final Object pNewValue) {
            getHandle().putByteArray(getName(), (byte[]) pNewValue);
        }
    }

    /**
     * CharArray preference.
     * @param <K> the keyType
     */
    public static class MetisCharArrayPreference<K extends Enum<K> & MetisPreferenceKey>
            extends MetisPreferenceItem<K> {
        /**
         * Constructor.
         * @param pSet the preference Set
         * @param pKey the key of the preference
         * @throws OceanusException on error
         */
        protected MetisCharArrayPreference(final MetisPreferenceSet<K> pSet,
                                           final K pKey) throws OceanusException {
            /* Store name */
            super(pSet, pKey, MetisPreferenceType.CHARARRAY);

            /* Check whether we have an existing value */
            if (pSet.checkExists(pKey)) {
                /* Access the value */
                byte[] myBytes = getHandle().getByteArray(getName(), null);

                /* Decrypt the value */
                char[] myValue = myBytes == null
                                                 ? null
                                                 : pSet.theSecurityManager.decryptValue(myBytes);

                /* Set as initial value */
                setTheValue(myValue);
            }
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
            getHandle().putByteArray(getName(), getSet().theSecurityManager.encryptValue((char[]) pNewValue));
        }
    }
}
