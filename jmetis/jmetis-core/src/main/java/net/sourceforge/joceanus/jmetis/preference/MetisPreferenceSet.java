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
 */
public abstract class MetisPreferenceSet
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
     * Report fields.
     */
    private final MetisFields theFields = new MetisFields(MetisPreferenceSet.class.getSimpleName());

    /**
     * The Preference node for this set.
     */
    private Preferences theHandle;

    /**
     * The map of preferences.
     */
    private Map<String, MetisPreferenceItem> theMap;

    /**
     * The list of preferences that have a value on initialisation.
     */
    private String[] theActive;

    /**
     * Constructor.
     * @throws OceanusException on error
     */
    public MetisPreferenceSet() throws OceanusException {
        /* Access the handle */
        theHandle = Preferences.userNodeForPackage(this.getClass());
        theHandle = theHandle.node(this.getClass().getSimpleName());

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

        /* Define the preferences */
        definePreferences();

        /* Store preference changes */
        storeChanges();
    }

    @Override
    public MetisFields getDataFields() {
        return theFields;
    }

    @Override
    public TethysEventRegistrar<MetisPreferenceEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * Obtain the collection of preferences.
     * @return the preferences
     */
    public Collection<MetisPreferenceItem> getPreferences() {
        return theMap.values();
    }

    /**
     * Callback from Constructor to define the preferences within the preferenceSet.
     */
    protected abstract void definePreferences();

    /**
     * Callback from Constructor to obtain display name.
     * @param pName the name of the preference
     * @return the display name
     */
    protected abstract String getDisplayName(final String pName);

    @Override
    public boolean isEditable() {
        return true;
    }

    /**
     * Define new String preference.
     * @param pName the name of the preference
     * @param pDefault the default value of the preference
     * @return the preference item
     */
    protected MetisStringPreference defineStringPreference(final String pName,
                                                           final String pDefault) {
        /* Define the preference */
        MetisStringPreference myPref = new MetisStringPreference(pName, pDefault);

        /* Add it to the list of preferences */
        definePreference(myPref);

        /* Return the preference */
        return myPref;
    }

    /**
     * Define new File preference.
     * @param pName the name of the preference
     * @param pDefault the default value of the preference
     * @return the preference item
     */
    protected MetisStringPreference defineFilePreference(final String pName,
                                                         final String pDefault) {
        /* Define the preference */
        MetisStringPreference myPref = new MetisStringPreference(pName, pDefault, MetisPreferenceType.FILE);

        /* Add it to the list of preferences */
        definePreference(myPref);

        /* Return the preference */
        return myPref;
    }

    /**
     * Define new Directory preference.
     * @param pName the name of the preference
     * @param pDefault the default value of the preference
     * @return the preference item
     */
    protected MetisStringPreference defineDirectoryPreference(final String pName,
                                                              final String pDefault) {
        /* Define the preference */
        MetisStringPreference myPref = new MetisStringPreference(pName, pDefault, MetisPreferenceType.DIRECTORY);

        /* Add it to the list of preferences */
        definePreference(myPref);

        /* Return the preference */
        return myPref;
    }

    /**
     * Define new Integer preference.
     * @param pName the name of the preference
     * @param pDefault the default value of the preference
     * @return the preference item
     */
    protected MetisIntegerPreference defineIntegerPreference(final String pName,
                                                             final Integer pDefault) {
        /* Define the preference */
        MetisIntegerPreference myPref = new MetisIntegerPreference(pName, pDefault);

        /* Add it to the list of preferences */
        definePreference(myPref);

        /* Return the preference */
        return myPref;
    }

    /**
     * Define new Boolean preference.
     * @param pName the name of the preference
     * @param pDefault the default value of the preference
     * @return the preference item
     */
    protected MetisBooleanPreference defineBooleanPreference(final String pName,
                                                             final Boolean pDefault) {
        /* Define the preference */
        MetisBooleanPreference myPref = new MetisBooleanPreference(pName, pDefault);

        /* Add it to the list of preferences */
        definePreference(myPref);

        /* Return the preference */
        return myPref;
    }

    /**
     * Define new Date preference.
     * @param pName the name of the preference
     * @param pDefault the default value of the preference
     * @return the preference item
     */
    protected MetisDatePreference defineDatePreference(final String pName,
                                                       final TethysDate pDefault) {
        /* Define the preference */
        MetisDatePreference myPref = new MetisDatePreference(pName, pDefault);

        /* Add it to the list of preferences */
        definePreference(myPref);

        /* Return the preference */
        return myPref;
    }

    /**
     * Define new Enum preference.
     * @param <E> the Enum type
     * @param pName the name of the preference
     * @param pDefault the default value of the preference
     * @param pClass the Enum class
     * @return the newly created preference
     */
    protected <E extends Enum<E>> MetisEnumPreference<E> definePreference(final String pName,
                                                                          final E pDefault,
                                                                          final Class<E> pClass) {
        /* Create the preference */
        MetisEnumPreference<E> myPref = new MetisEnumPreference<>(pName, pDefault, pClass);

        /* Add it to the list of preferences */
        definePreference(myPref);

        /* Return the preference */
        return myPref;
    }

    /**
     * Obtain preference.
     * @param pName the name of the preference
     * @return the preference
     */
    public MetisPreferenceItem getPreference(final String pName) {
        return theMap.get(pName);
    }

    /**
     * Obtain Integer value.
     * @param pName the name of the preference
     * @return the Integer value
     */
    public Integer getIntegerValue(final String pName) {
        /* Access preference */
        MetisPreferenceItem myPref = getPreference(pName);

        /* Reject if not found */
        if (myPref == null) {
            throw new IllegalArgumentException(ERROR_UNKNOWN
                                               + pName);
        }

        /* Reject if wrong type */
        if (!(myPref instanceof MetisIntegerPreference)) {
            throw new IllegalArgumentException(ERROR_INVALID
                                               + pName);
        }

        /* Access preference */
        MetisIntegerPreference myIntPref = (MetisIntegerPreference) myPref;

        /* Return the preference */
        return myIntPref.getValue();
    }

    /**
     * Obtain Boolean value.
     * @param pName the name of the preference
     * @return the Boolean value
     */
    public Boolean getBooleanValue(final String pName) {
        /* Access preference */
        MetisPreferenceItem myPref = getPreference(pName);

        /* Reject if not found */
        if (myPref == null) {
            throw new IllegalArgumentException(ERROR_UNKNOWN
                                               + pName);
        }

        /* Reject if wrong type */
        if (!(myPref instanceof MetisBooleanPreference)) {
            throw new IllegalArgumentException(ERROR_INVALID
                                               + pName);
        }

        /* Access preference */
        MetisBooleanPreference myBoolPref = (MetisBooleanPreference) myPref;

        /* Return the value */
        return myBoolPref.getValue();
    }

    /**
     * Obtain String value.
     * @param pName the name of the preference
     * @return the String value
     */
    public String getStringValue(final String pName) {
        /* Access preference */
        MetisPreferenceItem myPref = getPreference(pName);

        /* Reject if not found */
        if (myPref == null) {
            throw new IllegalArgumentException(ERROR_UNKNOWN
                                               + pName);
        }

        /* Reject if wrong type */
        if (!(myPref instanceof MetisStringPreference)) {
            throw new IllegalArgumentException(ERROR_INVALID
                                               + pName);
        }

        /* Access preference */
        MetisStringPreference myStringPref = (MetisStringPreference) myPref;

        /* Return the value */
        return myStringPref.getValue();
    }

    /**
     * Obtain Date value.
     * @param pName the name of the preference
     * @return the Date
     */
    public TethysDate getDateValue(final String pName) {
        /* Access preference */
        MetisPreferenceItem myPref = getPreference(pName);

        /* Reject if not found */
        if (myPref == null) {
            throw new IllegalArgumentException(ERROR_UNKNOWN
                                               + pName);
        }

        /* Reject if wrong type */
        if (!(myPref instanceof MetisDatePreference)) {
            throw new IllegalArgumentException(ERROR_INVALID
                                               + pName);
        }

        /* Access preference */
        MetisDatePreference myDatePref = (MetisDatePreference) myPref;

        /* Return the value */
        return myDatePref.getValue();
    }

    /**
     * Obtain Enum value.
     * @param <E> the EnumType
     * @param pName the name of the preference
     * @param pClass the Enum class
     * @return the Enum or null if no such Enum preference exists
     */
    public <E extends Enum<E>> E getEnumValue(final String pName,
                                              final Class<E> pClass) {
        /* Access preference */
        MetisPreferenceItem myPref = getPreference(pName);

        /* Reject if not found */
        if (myPref == null) {
            throw new IllegalArgumentException(ERROR_UNKNOWN
                                               + pName);
        }

        /* Reject if wrong type */
        if (!(myPref instanceof MetisEnumPreference)) {
            throw new IllegalArgumentException(ERROR_INVALID
                                               + pName);
        }

        /* Access as Enum preference */
        @SuppressWarnings("unchecked")
        MetisEnumPreference<E> myEnumPref = (MetisEnumPreference<E>) myPref;
        if (!myEnumPref.theClass.equals(pClass)) {
            throw new IllegalArgumentException(ERROR_INVALID
                                               + pName);
        }

        /* Return the value */
        return myEnumPref.getValue();
    }

    /**
     * Define a preference for the node.
     * @param pPreference the preference to define
     */
    protected void definePreference(final MetisPreferenceItem pPreference) {
        /* Access the name of the preference */
        String myName = pPreference.getName();

        /* Reject if the name is already present */
        if (theMap.get(myName) != null) {
            throw new IllegalArgumentException("preference "
                                               + myName
                                               + " is already defined");
        }

        /* Add the preference to the map */
        theMap.put(pPreference.getName(), pPreference);
    }

    /**
     * Reset all changes in this preference set.
     */
    public void resetChanges() {
        /* Loop through all the preferences */
        for (MetisPreferenceItem myPref : theMap.values()) {
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
        for (MetisPreferenceItem myPref : theMap.values()) {
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
        for (MetisPreferenceItem myPref : theMap.values()) {
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
     * @param pName the name of the preference
     * @return whether the preference already exists
     */
    protected boolean checkExists(final String pName) {
        /* If we failed to get the active keys, assume it exists */
        if (theActive == null) {
            return false;
        }

        /* Loop through all the keys */
        for (String myName : theActive) {
            /* If the name matches return true */
            if (myName.equals(pName)) {
                return true;
            }
        }

        /* return no match */
        return false;
    }

    /**
     * Underlying preference item class.
     */
    public abstract class MetisPreferenceItem {
        /**
         * preference Name.
         */
        private final String theName;

        /**
         * New preference Value.
         */
        private final MetisField theField;

        /**
         * default Value.
         */
        private final Object theDefault;

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
         * @param pName the name of the preference
         * @param pDefault the default value
         * @param pType the type of the preference
         */
        protected MetisPreferenceItem(final String pName,
                                      final Object pDefault,
                                      final MetisPreferenceType pType) {
            /* Store name and type */
            theName = pName;
            theDefault = pDefault;
            theType = pType;
            theDisplay = getDisplayName(theName);

            /* Create the DataField */
            theField = theFields.declareLocalField(pName);
        }

        /**
         * Obtain the name of the preference.
         * @return the name of the preference
         */
        public String getName() {
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
        public MetisPreferenceType getType() {
            return theType;
        }

        /**
         * Obtain the field for the preference.
         * @return the field for the preference
         */
        public MetisField getDataField() {
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
         * Is the preference changed.
         * @return is the preference changed
         */
        public boolean isChanged() {
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
            theNewValue = (pNewValue == null)
                                              ? theDefault
                                              : pNewValue;
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
         */
        private void storePreference() {
            /* If the preference has changed */
            if (isChanged) {
                /* else Store the value */
                storeThePreference(theNewValue);

                /* Note the new value and reset changes */
                setTheValue(theNewValue);
                resetChanges();
            }
        }

        /**
         * Store the value of the preference.
         * @param pNewValue the new value to store
         */
        protected abstract void storeThePreference(final Object pNewValue);

        /**
         * Read the value.
         * @return the existing value
         */
        protected String readTheValue() {
            return theHandle.get(theName, null);
        }

        /**
         * Put the value.
         * @param pNewValue the new value to store
         */
        protected void storeTheValue(final String pNewValue) {
            theHandle.put(theName, pNewValue);
        }
    }

    /**
     * Integer preference.
     */
    public class MetisIntegerPreference
            extends MetisPreferenceItem {
        /**
         * Constructor.
         * @param pName the name of the preference
         * @param pDefault the default value
         */
        public MetisIntegerPreference(final String pName,
                                      final Integer pDefault) {
            /* Store name */
            super(pName, pDefault, MetisPreferenceType.INTEGER);

            /* Check whether we have an existing value */
            boolean bExists = checkExists(pName);

            /* If it exists */
            if (bExists) {
                /* Access the value */
                int myValue = theHandle.getInt(pName, -1);

                /* Set as initial value */
                setTheValue(Integer.valueOf(myValue));

                /* else value does not exist */
            } else {
                /* Use default as a changed value */
                super.setNewValue(pDefault);
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
            /* Set the new value */
            super.setNewValue(pNewValue);
        }

        @Override
        protected void storeThePreference(final Object pNewValue) {
            /* Store the value */
            theHandle.putInt(getName(), (Integer) pNewValue);
        }
    }

    /**
     * Boolean preference.
     */
    public class MetisBooleanPreference
            extends MetisPreferenceItem {
        /**
         * Constructor.
         * @param pName the name of the preference
         * @param pDefault the default value
         */
        public MetisBooleanPreference(final String pName,
                                      final Boolean pDefault) {
            /* Store name */
            super(pName, pDefault, MetisPreferenceType.BOOLEAN);

            /* Check whether we have an existing value */
            boolean bExists = checkExists(pName);

            /* If it exists */
            if (bExists) {
                /* Access the value */
                boolean myValue = theHandle.getBoolean(pName, false);

                /* Set as initial value */
                setTheValue(myValue
                                    ? Boolean.TRUE
                                    : Boolean.FALSE);

                /* else value does not exist */
            } else {
                /* Use default as a changed value */
                super.setNewValue(pDefault);
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
            super.setNewValue(myNewValue);
        }

        @Override
        protected void storeThePreference(final Object pNewValue) {
            /* Store the value */
            theHandle.putBoolean(getName(), (Boolean) pNewValue);
        }
    }

    /**
     * String preference.
     */
    public class MetisStringPreference
            extends MetisPreferenceItem {
        /**
         * Constructor.
         * @param pName the name of the preference
         * @param pDefault the default value
         */
        public MetisStringPreference(final String pName,
                                     final String pDefault) {
            this(pName, pDefault, MetisPreferenceType.STRING);
        }

        /**
         * Constructor.
         * @param pName the name of the preference
         * @param pDefault the default value
         * @param pType the type of the preference
         */
        public MetisStringPreference(final String pName,
                                     final String pDefault,
                                     final MetisPreferenceType pType) {
            /* Store name */
            super(pName, pDefault, pType);

            /* Check whether we have an existing value */
            boolean bExists = checkExists(pName);

            /* If it exists */
            if (bExists) {
                /* Access the value */
                String myValue = theHandle.get(pName, null);

                /* Set as initial value */
                setTheValue(myValue);

                /* else value does not exist */
            } else {
                /* Use default as a changed value */
                super.setNewValue(pDefault);
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
            String myNewValue = pNewValue;

            /* Set the new value */
            super.setNewValue(myNewValue);
        }

        @Override
        protected void storeThePreference(final Object pNewValue) {
            /* Store the value */
            theHandle.put(getName(), (String) pNewValue);
        }
    }

    /**
     * Date preference.
     */
    public class MetisDatePreference
            extends MetisPreferenceItem {
        /**
         * Constructor.
         * @param pName the name of the preference
         * @param pDefault the default value
         */
        public MetisDatePreference(final String pName,
                                   final TethysDate pDefault) {
            /* Store name */
            super(pName, pDefault, MetisPreferenceType.DATE);

            /* Check whether we have an existing value */
            boolean bExists = checkExists(pName);

            /* If it exists */
            if (bExists) {
                /* Access the value */
                String myValue = theHandle.get(pName, null);
                if (myValue == null) {
                    bExists = false;
                } else {
                    /* Parse the Date */
                    TethysDate myDate = new TethysDate(myValue);

                    /* Set as initial value */
                    setTheValue(myDate);
                }
            }

            /* if value does not exist or is invalid */
            if (!bExists) {
                /* Use default as a changed value */
                super.setNewValue(pDefault);
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
            super.setNewValue(myNewValue);
        }

        @Override
        protected void storeThePreference(final Object pNewValue) {
            /* Store the value */
            theHandle.put(getName(), ((TethysDate) pNewValue).toString());
        }
    }

    /**
     * Enum preference.
     * @param <E> the Enum type
     */
    public class MetisEnumPreference<E extends Enum<E>>
            extends MetisPreferenceItem {
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
         * @param pName the name of the preference
         * @param pDefault the default value
         * @param pClass the class of the preference
         */
        public MetisEnumPreference(final String pName,
                                   final E pDefault,
                                   final Class<E> pClass) {
            /* Store name */
            super(pName, pDefault, MetisPreferenceType.ENUM);

            /* Store the class */
            theClass = pClass;
            theValues = theClass.getEnumConstants();

            /* Check whether we have an existing value */
            boolean bExists = checkExists(pName);

            /* If it exists */
            if (bExists) {
                /* Access the value */
                String myValue = theHandle.get(pName, null);
                if (myValue == null) {
                    bExists = false;
                } else {
                    /* Set the value */
                    E myEnum = findValue(myValue);
                    bExists = myEnum != null;
                    if (bExists) {
                        setTheValue(myEnum);
                    }
                }
            }

            /* if value does not exist or is invalid */
            if (!bExists) {
                /* Use default as a changed value */
                super.setNewValue(pDefault);
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
            /* Loop through the Enum constants */
            E myEnum = findValue(pNewValue);
            super.setNewValue(myEnum);
        }

        /**
         * Set value.
         * @param pNewValue the new value
         */
        public void setValue(final Object pNewValue) {
            /* Set the new value */
            super.setNewValue(pNewValue);
        }

        @Override
        protected void storeThePreference(final Object pNewValue) {
            /* Store the value */
            theHandle.put(getName(), theClass.cast(pNewValue).name());
        }
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
        MetisPreferenceItem myPref = getPreference(pField.getName());

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
        MetisPreferenceItem myPref = getPreference(pField.getName());

        /* Return the value */
        return (myPref == null)
                                ? MetisFieldValue.UNKNOWN
                                : myPref.getValue();
    }
}
