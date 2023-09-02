/*******************************************************************************
 * Metis: Java Data Framework
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
package net.sourceforge.joceanus.jmetis.preference;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import net.sourceforge.joceanus.jmetis.MetisDataException;
import net.sourceforge.joceanus.jmetis.data.MetisDataDifference;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerEntry;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerManager;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerStandardEntry;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.resource.TethysBundleId;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;

/**
 * Wrapper class for java preferences.
 * @author Tony Washer
 */
public abstract class MetisPreferenceSet
        implements MetisFieldItem, TethysEventProvider<MetisPreferenceEvent> {
    /**
     * Id interface.
     */
    public interface MetisPreferenceId {
    }

    /**
     * Unknown preference string.
     */
    protected static final String ERROR_UNKNOWN = "Unknown Preference: ";

    /**
     * Invalid preference string.
     */
    protected static final String ERROR_INVALID = "Invalid Preference: ";

    /**
     * The Preference Manager.
     */
    private final MetisPreferenceManager thePreferenceManager;

    /**
     * The Event Manager.
     */
    private final TethysEventManager<MetisPreferenceEvent> theEventManager;

    /**
     * Report fields.
     */
    private final MetisFieldSet<MetisPreferenceSet> theFields;

    /**
     * The Preference node for this set.
     */
    private final Preferences theHandle;

    /**
     * The map of preferences.
     */
    private final Map<String, MetisPreferenceItem> theNameMap;

    /**
     * The map of preferences.
     */
    private final Map<MetisPreferenceKey, MetisPreferenceItem> theKeyMap;

    /**
     * The list of preferences that have a value on initialisation.
     */
    private final String[] theActive;

    /**
     * The name of the preferenceSet.
     */
    private final String theName;

    /**
     * The viewer entry.
     */
    private final MetisViewerEntry theViewerEntry;

    /**
     * Is this a hidden preferenceSet.
     */
    private boolean isHidden;

    /**
     * Constructor.
     * @param pManager the preference manager
     * @param pId the resource id for the set name
     * @throws OceanusException on error
     */
    protected MetisPreferenceSet(final MetisPreferenceManager pManager,
                                 final TethysBundleId pId) throws OceanusException {
        this(pManager, pId.getValue());
    }

    /**
     * Constructor.
     * @param pManager the preference manager
     * @param pName the set name
     * @throws OceanusException on error
     */
    protected MetisPreferenceSet(final MetisPreferenceManager pManager,
                                 final String pName) throws OceanusException {
        /* Store name */
        thePreferenceManager = pManager;
        theName = pName;

        /* Allocate the fields */
        theFields = MetisFieldSet.newFieldSet(this);

        /* Access the handle */
        theHandle = deriveHandle();

        /* Create Event Manager */
        theEventManager = new TethysEventManager<>();

        /* Allocate the preference maps */
        theNameMap = new HashMap<>();
        theKeyMap = new HashMap<>();

        /* Access the active key names */
        try {
            theActive = theHandle.keys();
        } catch (BackingStoreException e) {
            throw new MetisDataException("Failed to access preferences", e);
        }

        /* Define the preferences */
        definePreferences();
        autoCorrectPreferences();

        /* Store any changes */
        storeChanges();

        /* Create the viewer record */
        final MetisViewerManager myViewer = pManager.getViewer();
        final MetisViewerEntry myParent = myViewer.getStandardEntry(MetisViewerStandardEntry.PREFERENCES);
        theViewerEntry = myViewer.newEntry(myParent, theName);
        theViewerEntry.setObject(this);
    }

    /**
     * Obtain the preference manager
     * @return the manager
     */
    public MetisPreferenceManager getPreferenceManager() {
        return thePreferenceManager;
    }

    @Override
    public MetisFieldSetDef getDataFieldSet() {
        return theFields;
    }

    @Override
    public String formatObject(final TethysUIDataFormatter pFormatter) {
        return theFields.getName();
    }

    /**
     * Declare preference.
     * @param pPref the preference to declare
     */
    void declarePreference(final MetisPreferenceItem pPref) {
        /* Create the DataField */
        theFields.declareLocalField(pPref.getPreferenceName(), s -> pPref.getViewerValue());
    }

    @Override
    public TethysEventRegistrar<MetisPreferenceEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * Hook to enable preferenceSets to define their preferences.
     * @throws OceanusException on error
     */
    protected abstract void definePreferences() throws OceanusException;

    /**
     * Hook to enable preferenceSets to autoCorrect their preferences.
     * <p>
     * This is used both to initialise preferencesSet defaults and to adjust the set when a value
     * changes.
     */
    public abstract void autoCorrectPreferences();

    /**
     * Obtain the name of the set.
     * @return the name
     */
    public String getName() {
        return theName;
    }

    /**
     * Is this a hidden preferenceSet?
     * @return true/false
     */
    public boolean isHidden() {
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
    public Collection<MetisPreferenceItem> getPreferences() {
        return theKeyMap.values();
    }

    /**
     * Set the focus.
     */
    public void setFocus() {
        theViewerEntry.setFocus();
    }

    /**
     * Update the viewer entry.
     */
    public void updateViewerEntry() {
        theViewerEntry.setObject(this);
    }

    /**
     * Derive handle for node.
     * @return the class name
     */
    private Preferences deriveHandle() {
        /* Obtain the class name */
        final Class<?> myClass = this.getClass();
        String myName = myClass.getCanonicalName();

        /* Obtain the package name */
        final String myPackage = myClass.getPackage().getName();

        /* Strip off the package name */
        myName = myName.substring(myPackage.length() + 1);

        /* Derive the handle */
        final Preferences myHandle = Preferences.userNodeForPackage(myClass);
        return myHandle.node(myName);
    }

    /**
     * Define new String preference.
     * @param pKey the key for the preference
     * @return the preference item
     */
    protected MetisStringPreference defineStringPreference(final MetisPreferenceKey pKey) {
        /* Define the preference */
        final MetisStringPreference myPref = new MetisStringPreference(this, pKey);

        /* Add it to the list of preferences */
        definePreference(myPref);

        /* Return the preference */
        return myPref;
    }

    /**
     * Define new File preference.
     * @param pKey the key for the preference
     * @return the preference item
     */
    protected MetisStringPreference defineFilePreference(final MetisPreferenceKey pKey) {
        /* Define the preference */
        final MetisStringPreference myPref = new MetisStringPreference(this, pKey, MetisPreferenceType.FILE);

        /* Add it to the list of preferences */
        definePreference(myPref);

        /* Return the preference */
        return myPref;
    }

    /**
     * Define new Directory preference.
     * @param pKey the key for the preference
     * @return the preference item
     */
    protected MetisStringPreference defineDirectoryPreference(final MetisPreferenceKey pKey) {
        /* Define the preference */
        final MetisStringPreference myPref = new MetisStringPreference(this, pKey, MetisPreferenceType.DIRECTORY);

        /* Add it to the list of preferences */
        definePreference(myPref);

        /* Return the preference */
        return myPref;
    }

    /**
     * Define new Colour preference.
     * @param pKey the key for the preference
     * @return the preference item
     */
    protected MetisStringPreference defineColorPreference(final MetisPreferenceKey pKey) {
        /* Define the preference */
        final MetisStringPreference myPref = new MetisStringPreference(this, pKey, MetisPreferenceType.COLOR);

        /* Add it to the list of preferences */
        definePreference(myPref);

        /* Return the preference */
        return myPref;
    }

    /**
     * Define new Integer preference.
     * @param pKey the key for the preference
     * @return the preference item
     */
    protected MetisIntegerPreference defineIntegerPreference(final MetisPreferenceKey pKey) {
        /* Define the preference */
        final MetisIntegerPreference myPref = new MetisIntegerPreference(this, pKey);

        /* Add it to the list of preferences */
        definePreference(myPref);

        /* Return the preference */
        return myPref;
    }

    /**
     * Define new Boolean preference.
     * @param pKey the key for the preference
     * @return the preference item
     */
    protected MetisBooleanPreference defineBooleanPreference(final MetisPreferenceKey pKey) {
        /* Define the preference */
        final MetisBooleanPreference myPref = new MetisBooleanPreference(this, pKey);

        /* Add it to the list of preferences */
        definePreference(myPref);

        /* Return the preference */
        return myPref;
    }

    /**
     * Define new Date preference.
     * @param pKey the key for the preference
     * @return the preference item
     */
    protected MetisDatePreference defineDatePreference(final MetisPreferenceKey pKey) {
        /* Define the preference */
        final MetisDatePreference myPref = new MetisDatePreference(this, pKey);

        /* Add it to the list of preferences */
        definePreference(myPref);

        /* Return the preference */
        return myPref;
    }

    /**
     * Define new Enum preference.
     * @param <E> the Enum type
     * @param pKey the key for the preference
     * @param pClazz the Enum class
     * @return the newly created preference
     */
    protected <E extends Enum<E>> MetisEnumPreference<E> defineEnumPreference(final MetisPreferenceKey pKey,
                                                                              final Class<E> pClazz) {
        /* Create the preference */
        final MetisEnumPreference<E> myPref = new MetisEnumPreference<>(this, pKey, pClazz);

        /* Add it to the list of preferences */
        definePreference(myPref);

        /* Return the preference */
        return myPref;
    }

    /**
     * Define new ByteArray preference.
     * @param pKey the key for the preference
     * @return the preference item
     */
    protected MetisByteArrayPreference defineByteArrayPreference(final MetisPreferenceKey pKey) {
        /* Define the preference */
        final MetisByteArrayPreference myPref = new MetisByteArrayPreference(this, pKey);

        /* Add it to the list of preferences */
        definePreference(myPref);

        /* Return the preference */
        return myPref;
    }

    /**
     * Define a preference for the node.
     * @param pPreference the preference to define
     */
    protected void definePreference(final MetisPreferenceItem pPreference) {
        /* Access the key of the preference */
        final String myName = pPreference.getPreferenceName();

        /* Reject if the name is already present */
        if (theNameMap.get(myName) != null) {
            throw new IllegalArgumentException("preference "
                                               + myName
                                               + " is already defined");
        }

        /* Add the preference to the map */
        theNameMap.put(myName, pPreference);
        theKeyMap.put(pPreference.getKey(), pPreference);
    }

    /**
     * Obtain preference by key.
     * @param pKey the key of the preference
     * @return the preference
     */
    public MetisPreferenceItem getPreference(final MetisPreferenceKey pKey) {
        return theKeyMap.get(pKey);
    }

    /**
     * Obtain preference.
     * @param pName the name of the preference
     * @return the preference
     */
    protected MetisPreferenceItem getPreference(final String pName) {
        return theNameMap.get(pName);
    }

    /**
     * Obtain String preference.
     * @param pKey the key of the preference
     * @return the String preference
     */
    public MetisStringPreference getStringPreference(final MetisPreferenceKey pKey) {
        /* Access preference */
        final MetisPreferenceItem myPref = getPreference(pKey);

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

        /* Return the preference */
        return (MetisStringPreference) myPref;
    }

    /**
     * Obtain String value.
     * @param pKey the key of the preference
     * @return the String value
     */
    public String getStringValue(final MetisPreferenceKey pKey) {
        /* Access preference */
        final MetisStringPreference myPref = getStringPreference(pKey);

        /* Return the value */
        return myPref.getValue();
    }

    /**
     * Obtain Integer preference.
     * @param pKey the key of the preference
     * @return the Integer preference
     */
    public MetisIntegerPreference getIntegerPreference(final MetisPreferenceKey pKey) {
        /* Access preference */
        final MetisPreferenceItem myPref = getPreference(pKey);

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

        /* Return the preference */
        return (MetisIntegerPreference) myPref;
    }

    /**
     * Obtain Integer value.
     * @param pKey the key of the preference
     * @return the Integer value
     */
    public Integer getIntegerValue(final MetisPreferenceKey pKey) {
        /* Access preference */
        final MetisIntegerPreference myPref = getIntegerPreference(pKey);

        /* Return the value */
        return myPref.getValue();
    }

    /**
     * Obtain Boolean preference.
     * @param pKey the key of the preference
     * @return the Boolean preference
     */
    public MetisBooleanPreference getBooleanPreference(final MetisPreferenceKey pKey) {
        /* Access preference */
        final MetisPreferenceItem myPref = getPreference(pKey);

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

        /* Return the preference */
        return (MetisBooleanPreference) myPref;
    }

    /**
     * Obtain Boolean value.
     * @param pKey the key of the preference
     * @return the Boolean value
     */
    public Boolean getBooleanValue(final MetisPreferenceKey pKey) {
        /* Access preference */
        final MetisBooleanPreference myPref = getBooleanPreference(pKey);

        /* Return the value */
        return myPref.getValue();
    }

    /**
     * Obtain Date preference.
     * @param pKey the key of the preference
     * @return the Date preference
     */
    public MetisDatePreference getDatePreference(final MetisPreferenceKey pKey) {
        /* Access preference */
        final MetisPreferenceItem myPref = getPreference(pKey);

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

        /* Return the preference */
        return (MetisDatePreference) myPref;
    }

    /**
     * Obtain Date value.
     * @param pKey the key of the preference
     * @return the Date value
     */
    public TethysDate getDateValue(final MetisPreferenceKey pKey) {
        /* Access preference */
        final MetisDatePreference myPref = getDatePreference(pKey);

        /* Return the value */
        return myPref.getValue();
    }

    /**
     * Obtain Enum preference.
     * @param <E> the EnumType
     * @param pKey the key of the preference
     * @param pClazz the Enum class
     * @return the Enum preference
     */
    public <E extends Enum<E>> MetisEnumPreference<E> getEnumPreference(final MetisPreferenceKey pKey,
                                                                        final Class<E> pClazz) {
        /* Access preference */
        final MetisPreferenceItem myPref = getPreference(pKey);

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
        final MetisEnumPreference<E> myEnumPref = (MetisEnumPreference<E>) myPref;
        if (!myEnumPref.theClazz.equals(pClazz)) {
            throw new IllegalArgumentException(ERROR_INVALID
                                               + pKey);
        }

        /* Return the preference */
        return myEnumPref;
    }

    /**
     * Obtain Enum value.
     * @param <E> the EnumType
     * @param pKey the key of the preference
     * @param pClazz the Enum class
     * @return the Enum value
     */
    public <E extends Enum<E>> E getEnumValue(final MetisPreferenceKey pKey,
                                              final Class<E> pClazz) {
        /* Access preference */
        final MetisEnumPreference<E> myPref = getEnumPreference(pKey, pClazz);

        /* Return the value */
        return myPref.getValue();
    }

    /**
     * Obtain ByteArray preference.
     * @param pKey the key of the preference
     * @return the ByteArray preference
     */
    public MetisByteArrayPreference getByteArrayPreference(final MetisPreferenceKey pKey) {
        /* Access preference */
        final MetisPreferenceItem myPref = getPreference(pKey);

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

        /* Return the preference */
        return (MetisByteArrayPreference) myPref;
    }

    /**
     * Obtain ByteArray value.
     * @param pKey the key of the preference
     * @return the ByteArray value
     */
    public byte[] getByteArrayValue(final MetisPreferenceKey pKey) {
        /* Access preference */
        final MetisByteArrayPreference myPref = getByteArrayPreference(pKey);

        /* Return the value */
        return myPref.getValue();
    }

    /**
     * Reset all changes in this preference set.
     */
    public void resetChanges() {
        /* Loop through all the preferences */
        for (MetisPreferenceItem myPref : theKeyMap.values()) {
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
        for (MetisPreferenceItem myPref : theKeyMap.values()) {
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
        for (MetisPreferenceItem myPref : theKeyMap.values()) {
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
    protected boolean checkExists(final MetisPreferenceKey pKey) {
        /* Obtain the name */
        final String myKeyName = pKey.getName();

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
     */
    public abstract static class MetisPreferenceItem {
        /**
         * preferenceSet.
         */
        private final MetisPreferenceSet theSet;

        /**
         * preference Key.
         */
        private final MetisPreferenceKey theKey;

        /**
         * preference Name.
         */
        private final String theName;

        /**
         * Display Name.
         */
        private final String theDisplay;

        /**
         * preference Type.
         */
        private final MetisPreferenceId theType;

        /**
         * preference Value.
         */
        private Object theValue;

        /**
         * New preference Value.
         */
        private Object theNewValue;

        /**
         * Is there a change to the preference?
         */
        private boolean isChanged;

        /**
         * Is the preference hidden?
         */
        private boolean isHidden;

        /**
         * Constructor.
         * @param pSet the preference Set
         * @param pKey the key of the preference
         * @param pType the type of the preference
         */
        protected MetisPreferenceItem(final MetisPreferenceSet pSet,
                                      final MetisPreferenceKey pKey,
                                      final MetisPreferenceId pType) {
            /* Store parameters */
            theSet = pSet;
            theKey = pKey;
            theType = pType;

            /* Obtain key details */
            theName = pKey.getName();
            theDisplay = pKey.getDisplay();

            /* Create the DataField */
            theSet.declarePreference(this);
        }

        /**
         * Obtain viewer value.
         * @return the value
         */
        Object getViewerValue() {
            return isHidden
                            ? null
                            : getValue();
        }

        /**
         * Obtain the preferenceSet.
         * @return the set
         */
        protected MetisPreferenceSet getSet() {
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
        protected MetisPreferenceKey getKey() {
            return theKey;
        }

        /**
         * Obtain the name of the preference.
         * @return the name of the preference
         */
        protected String getPreferenceName() {
            return theName;
        }

        /**
         * Obtain the display name of the preference.
         * @return the display name of the preference
         */
        public String getDisplay() {
            return theDisplay;
        }

        /**
         * Obtain the type of the preference.
         * @return the type of the preference
         */
        public MetisPreferenceId getType() {
            return theType;
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
         * Is the preference available?
         * @return true/false
         */
        public boolean isAvailable() {
            return getValue() != null;
        }

        /**
         * Is the preference changed?
         * @return true/false
         */
        public boolean isChanged() {
            return isChanged;
        }

        /**
         * Is the preference hidden?
         * @return true/false
         */
        public boolean isHidden() {
            return isHidden;
        }

        /**
         * Set hidden.
         * @param pHidden true/false
         */
        public void setHidden(final boolean pHidden) {
            isHidden = pHidden;
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
            isChanged = !MetisDataDifference.isEqual(theNewValue, theValue);
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
        protected abstract void storeThePreference(Object pNewValue) throws OceanusException;
    }

    /**
     * String preference.
     */
    public static class MetisStringPreference
            extends MetisPreferenceItem {
        /**
         * Constructor.
         * @param pSet the preference Set
         * @param pKey the key of the preference
         */
        protected MetisStringPreference(final MetisPreferenceSet pSet,
                                        final MetisPreferenceKey pKey) {
            this(pSet, pKey, MetisPreferenceType.STRING);
        }

        /**
         * Constructor.
         * @param pSet the preference Set
         * @param pKey the key of the preference
         * @param pType the type of the preference
         */
        private MetisStringPreference(final MetisPreferenceSet pSet,
                                      final MetisPreferenceKey pKey,
                                      final MetisPreferenceType pType) {
            /* Store name */
            super(pSet, pKey, pType);

            /* Check whether we have an existing value */
            if (pSet.checkExists(pKey)) {
                /* Access the value */
                final String myValue = getHandle().get(getPreferenceName(), null);

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
            getHandle().put(getPreferenceName(), (String) pNewValue);
        }
    }

    /**
     * Integer preference.
     */
    public static class MetisIntegerPreference
            extends MetisPreferenceItem {
        /**
         * The minimum value.
         */
        private Integer theMinimum;

        /**
         * The maximum value.
         */
        private Integer theMaximum;

        /**
         * Constructor.
         * @param pSet the preference Set
         * @param pKey the key of the preference
         */
        protected MetisIntegerPreference(final MetisPreferenceSet pSet,
                                         final MetisPreferenceKey pKey) {
            /* Store name */
            super(pSet, pKey, MetisPreferenceType.INTEGER);

            /* Check whether we have an existing value */
            if (pSet.checkExists(pKey)) {
                /* Access the value */
                final int myValue = getHandle().getInt(getPreferenceName(), -1);

                /* Set as initial value */
                setTheValue(myValue);
            }
        }

        @Override
        public Integer getValue() {
            return (Integer) super.getValue();
        }

        /**
         * Obtain the minimum value.
         * @return the minimum
         */
        public Integer getMinimum() {
            return theMinimum;
        }

        /**
         * Obtain the maximum value.
         * @return the maximum
         */
        public Integer getMaximum() {
            return theMaximum;
        }

        /**
         * Set value.
         * @param pNewValue the new value
         */
        public void setValue(final Integer pNewValue) {
            setNewValue(pNewValue);
        }

        /**
         * Set range.
         * @param pMinimum the minimum value
         * @param pMaximum the maximum value
         */
        public void setRange(final Integer pMinimum,
                             final Integer pMaximum) {
            theMinimum = pMinimum;
            theMaximum = pMaximum;
        }

        /**
         * Validate the range.
         * @return true/false
         */
        public boolean validate() {
            if (isAvailable()) {
                final Integer myValue = getValue();
                if ((theMinimum != null)
                    && theMinimum > myValue) {
                    return false;
                }
                if ((theMaximum != null)
                    && theMaximum < myValue) {
                    return false;
                }
            }
            return true;
        }

        @Override
        protected void storeThePreference(final Object pNewValue) {
            getHandle().putInt(getPreferenceName(), (Integer) pNewValue);
        }
    }

    /**
     * Boolean preference.
     */
    public static class MetisBooleanPreference
            extends MetisPreferenceItem {
        /**
         * Constructor.
         * @param pSet the preference Set
         * @param pKey the key of the preference
         */
        protected MetisBooleanPreference(final MetisPreferenceSet pSet,
                                         final MetisPreferenceKey pKey) {
            /* Store name */
            super(pSet, pKey, MetisPreferenceType.BOOLEAN);

            /* Check whether we have an existing value */
            if (pSet.checkExists(pKey)) {
                /* Access the value */
                final boolean myValue = getHandle().getBoolean(getPreferenceName(), false);

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
            getHandle().putBoolean(getPreferenceName(), (Boolean) pNewValue);
        }
    }

    /**
     * Date preference.
     */
    public static class MetisDatePreference
            extends MetisPreferenceItem {
        /**
         * Constructor.
         * @param pSet the preference Set
         * @param pKey the key of the preference
         */
        protected MetisDatePreference(final MetisPreferenceSet pSet,
                                      final MetisPreferenceKey pKey) {
            /* Store name */
            super(pSet, pKey, MetisPreferenceType.DATE);

            /* Check whether we have an existing value */
            if (pSet.checkExists(pKey)) {
                /* Access the value */
                final String myValue = getHandle().get(getPreferenceName(), null);

                /* Parse the Date */
                final TethysDate myDate = new TethysDate(myValue);

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
            getHandle().put(getPreferenceName(), ((TethysDate) pNewValue).toString());
        }
    }

    /**
     * Enum preference.
     * @param <E> the Enum type
     */
    public static class MetisEnumPreference<E extends Enum<E>>
            extends MetisPreferenceItem {
        /**
         * The enum class.
         */
        private final Class<E> theClazz;

        /**
         * The enum values.
         */
        private final E[] theValues;

        /**
         * The filter.
         */
        private Predicate<E> theFilter;

        /**
         * Constructor.
         * @param pSet the preference Set
         * @param pKey the key of the preference
         * @param pClazz the class of the preference
         */
        public MetisEnumPreference(final MetisPreferenceSet pSet,
                                   final MetisPreferenceKey pKey,
                                   final Class<E> pClazz) {
            /* Store name */
            super(pSet, pKey, MetisPreferenceType.ENUM);

            /* Store the class */
            theClazz = pClazz;
            theValues = theClazz.getEnumConstants();

            /* Set null filter */
            setFilter(null);

            /* Check whether we have an existing value */
            if (pSet.checkExists(pKey)) {
                /* Access the value */
                final String myValue = getHandle().get(getPreferenceName(), null);

                /* Set the value */
                final E myEnum = findValue(myValue);
                setTheValue(myEnum);
            }
        }

        @Override
        public E getValue() {
            return theClazz.cast(super.getValue());
        }

        /**
         * Obtain the values of the preference.
         * @return the values of the preference
         */
        public E[] getValues() {
            return Arrays.copyOf(theValues, theValues.length);
        }

        /**
         * Obtain the filter.
         * @return the filter
         */
        public Predicate<E> getFilter() {
            return theFilter;
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
            final E myEnum = findValue(pNewValue);
            setNewValue(myEnum);
        }

        /**
         * Set value.
         * @param pNewValue the new value
         */
        public void setValue(final E pNewValue) {
            setNewValue(pNewValue);
        }

        /**
         * Set filter.
         * @param pFilter the new filter
         */
        public void setFilter(final Predicate<E> pFilter) {
            theFilter = theFilter == null
                                          ? p -> true
                                          : pFilter;
        }

        @Override
        protected void storeThePreference(final Object pNewValue) {
            getHandle().put(getPreferenceName(), theClazz.cast(pNewValue).name());
        }
    }

    /**
     * ByteArray preference.
     */
    public static class MetisByteArrayPreference
            extends MetisPreferenceItem {
        /**
         * Constructor.
         * @param pSet the preference Set
         * @param pKey the key of the preference
         */
        protected MetisByteArrayPreference(final MetisPreferenceSet pSet,
                                           final MetisPreferenceKey pKey) {
            /* Store name */
            super(pSet, pKey, MetisPreferenceType.BYTEARRAY);

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
}
