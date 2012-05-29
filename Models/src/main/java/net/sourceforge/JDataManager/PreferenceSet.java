/*******************************************************************************
 * Copyright 2012 Tony Washer
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
package net.sourceforge.JDataManager;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;

import net.sourceforge.JDataManager.ModelException.ExceptionClass;
import net.sourceforge.JDateDay.DateDay;

public abstract class PreferenceSet {
    /**
     * The Preference node for this set
     */
    private Preferences theHandle = null;

    /**
     * The map of preferences
     */
    private Map<String, PreferenceItem> theMap = null;

    /**
     * The list of preferences that have a value on initialisation
     */
    private String[] theActive = null;

    /**
     * Obtain the collection of preferences
     * @return the preferences
     */
    public Collection<PreferenceItem> getPreferences() {
        return theMap.values();
    }

    /**
     * Constructor
     * @throws ModelException
     */
    public PreferenceSet() throws ModelException {
        /* Access the handle */
        theHandle = Preferences.userNodeForPackage(this.getClass());

        /* Allocate the preference map */
        theMap = new HashMap<String, PreferenceItem>();

        /* Access the active key names */
        try {
            theActive = theHandle.keys();
        } catch (Exception e) {
        }

        /* Define the preferences */
        definePreferences();

        /* Store preference changes */
        storeChanges();
    }

    /**
     * Callback from Constructor to define the preferences within the preferenceSet
     */
    protected abstract void definePreferences();

    /**
     * Callback from Constructor to obtain default value
     * @param pName the name of the preference
     * @return the default value
     */
    protected abstract Object getDefaultValue(String pName);

    /**
     * Callback from Constructor to obtain display name
     * @param pName the name of the preference
     * @return the display name
     */
    protected abstract String getDisplayName(String pName);

    /**
     * Define preference, callback from {@link #definePreferences}
     * @param pName the name of the preference
     * @param pType the type of the preference
     * @return the newly created preference
     */
    protected PreferenceItem definePreference(String pName,
                                              PreferenceType pType) {
        PreferenceItem myPref = null;

        /* Switch on preference type */
        switch (pType) {
        /* Create preference */
            case String:
                myPref = new StringPreference(pName);
                break;
            case File:
                myPref = new StringPreference(pName, PreferenceType.File);
                break;
            case Directory:
                myPref = new StringPreference(pName, PreferenceType.Directory);
                break;
            case Integer:
                myPref = new IntegerPreference(pName);
                break;
            case Boolean:
                myPref = new BooleanPreference(pName);
                break;
            case Date:
                myPref = new DatePreference(pName);
                break;
        }

        /* Add it to the list of preferences */
        definePreference(myPref);

        /* Return the preference */
        return myPref;
    }

    /**
     * Define Enum preference, callback from {@link #definePreferences}
     * @param <E> the Enum type
     * @param pName the name of the preference
     * @param pClass the Enum class
     * @return the newly created preference
     */
    protected <E extends Enum<E>> PreferenceItem definePreference(String pName,
                                                                  Class<E> pClass) {
        /* Create the preference */
        PreferenceItem myPref = new EnumPreference<E>(pName, pClass);

        /* Add it to the list of preferences */
        definePreference(myPref);

        /* Return the preference */
        return myPref;
    }

    /**
     * Obtain preference
     * @param pName the name of the preference
     * @return the preference
     */
    public PreferenceItem getPreference(String pName) {
        return theMap.get(pName);
    }

    /**
     * Obtain Integer preference
     * @param pName the name of the preference
     * @return the Integer preference or null if no such integer preference exists
     */
    private IntegerPreference getIntegerPreference(String pName) {
        /* Access preference */
        PreferenceItem myPref = getPreference(pName);

        /* If not found or wrong type return null */
        if (myPref == null)
            return null;
        if (!(myPref instanceof IntegerPreference))
            return null;

        /* Return the preference */
        return (IntegerPreference) myPref;
    }

    /**
     * Obtain Integer value
     * @param pName the name of the preference
     * @return the Integer value or null if no such integer preference exists
     */
    public Integer getIntegerValue(String pName) {
        /* Access preference */
        IntegerPreference myPref = getIntegerPreference(pName);

        /* If not found or wrong type return null */
        if (myPref == null)
            return null;

        /* Return the preference */
        return myPref.getValue();
    }

    /**
     * Obtain Boolean preference
     * @param pName the name of the preference
     * @return the Boolean preference or null if no such boolean preference exists
     */
    private BooleanPreference getBooleanPreference(String pName) {
        /* Access preference */
        PreferenceItem myPref = getPreference(pName);

        /* If not found or wrong type return null */
        if (myPref == null)
            return null;
        if (!(myPref instanceof BooleanPreference))
            return null;

        /* Return the preference */
        return (BooleanPreference) myPref;
    }

    /**
     * Obtain Boolean value
     * @param pName the name of the preference
     * @return the Boolean value or null if no such boolean preference exists
     */
    public Boolean getBooleanValue(String pName) {
        /* Access preference */
        BooleanPreference myPref = getBooleanPreference(pName);

        /* If not found or wrong type return null */
        if (myPref == null)
            return null;

        /* Return the value */
        return myPref.getValue();
    }

    /**
     * Obtain String preference
     * @param pName the name of the preference
     * @return the String preference or null if no such string preference exists
     */
    private StringPreference getStringPreference(String pName) {
        /* Access preference */
        PreferenceItem myPref = getPreference(pName);

        /* If not found or wrong type return null */
        if (myPref == null)
            return null;
        if (!(myPref instanceof StringPreference))
            return null;

        /* Return the preference */
        return (StringPreference) myPref;
    }

    /**
     * Obtain String value
     * @param pName the name of the preference
     * @return the String value or null if no such string preference exists
     */
    public String getStringValue(String pName) {
        /* Access preference */
        StringPreference myPref = getStringPreference(pName);

        /* If not found or wrong type return null */
        if (myPref == null)
            return null;

        /* Return the value */
        return myPref.getValue();
    }

    /**
     * Obtain Date preference
     * @param pName the name of the preference
     * @return the Date preference or null if no such date preference exists
     */
    private DatePreference getDatePreference(String pName) {
        /* Access preference */
        PreferenceItem myPref = getPreference(pName);

        /* If not found or wrong type return null */
        if (myPref == null)
            return null;
        if (!(myPref instanceof DatePreference))
            return null;

        /* Return the preference */
        return (DatePreference) myPref;
    }

    /**
     * Obtain Date value
     * @param pName the name of the preference
     * @return the Date or null if no such date preference exists
     */
    public DateDay getDateValue(String pName) {
        /* Access preference */
        DatePreference myPref = getDatePreference(pName);

        /* If not found or wrong type return null */
        if (myPref == null)
            return null;

        /* Return the value */
        return myPref.getValue();
    }

    /**
     * Obtain Enum preference
     * @param <E> the enum type
     * @param pName the name of the preference
     * @param pClass the Enum class
     * @return the Enum preference or null if no such Enum preference exists
     */
    private <E extends Enum<E>> EnumPreference<E> getEnumPreference(String pName,
                                                                    Class<E> pClass) {
        /* Access preference */
        PreferenceItem myPref = getPreference(pName);

        /* If not found or wrong type return null */
        if (myPref == null)
            return null;
        if (!(myPref instanceof EnumPreference))
            return null;

        /* Access as Enum preference */
        EnumPreference<?> myEnumPref = (EnumPreference<?>) myPref;
        if (myEnumPref.theClass != pClass)
            return null;

        /* Return the preference */
        @SuppressWarnings("unchecked")
        EnumPreference<E> myResult = (EnumPreference<E>) myPref;
        return myResult;
    }

    /**
     * Obtain Enum value
     * @param <E> the EnumType
     * @param pName the name of the preference
     * @param pClass the Enum class
     * @return the Enum or null if no such Enum preference exists
     */
    public <E extends Enum<E>> E getEnumValue(String pName,
                                              Class<E> pClass) {
        /* Access Enum preference */
        EnumPreference<E> myPref = getEnumPreference(pName, pClass);

        /* If not found or wrong type return null */
        if (myPref == null)
            return null;

        /* Return the value */
        return myPref.getValue();
    }

    /**
     * Define a preference for the node
     * @param pPreference the preference to define
     */
    private void definePreference(PreferenceItem pPreference) {
        /* Access the name of the preference */
        String myName = pPreference.getName();

        /* Reject if the name is already present */
        if (theMap.get(myName) != null)
            throw new IllegalArgumentException("preference " + myName + " is already defined");

        /* Add the preference to the map */
        theMap.put(pPreference.getName(), pPreference);
    }

    /**
     * Reset all changes in this preference set
     */
    public void resetChanges() {
        /* Loop through all the preferences */
        for (PreferenceItem myPref : theMap.values()) {
            /* Reset the changes */
            myPref.resetChanges();
        }
    }

    /**
     * Store preference changes
     * @throws ModelException
     */
    public void storeChanges() throws ModelException {
        /* Loop through all the preferences */
        for (PreferenceItem myPref : theMap.values()) {
            /* Store any changes */
            myPref.storePreference();
        }

        /* Protect against exceptions */
        try {
            /* Flush the output */
            theHandle.flush();
        }

        catch (Exception e) {
            throw new ModelException(ExceptionClass.PREFERENCE, "Failed to flush preferences to store", e);
        }
    }

    /**
     * Does the preference set have changes
     * @return true/false
     */
    public boolean hasChanges() {
        /* Loop through all the preferences */
        for (PreferenceItem myPref : theMap.values()) {
            /* Check for changes */
            if (myPref.isChanged())
                return true;
        }

        /* Return no changes */
        return false;
    }

    /**
     * Check whether a preference exists
     * @param pName the name of the preference
     * @return whether the preference already exists
     */
    private boolean checkExists(String pName) {
        /* If we failed to get the active keys, assume it exists */
        if (theActive == null)
            return false;

        /* Loop through all the keys */
        for (String myName : theActive) {
            /* If the name matches return true */
            if (myName.equals(pName))
                return true;
        }

        /* return no match */
        return false;
    }

    /**
     * Underlying preference item class
     */
    public abstract class PreferenceItem {
        /**
         * preference Name
         */
        private final String theName;

        /**
         * Display Name
         */
        private final String theDisplay;

        /**
         * preference Type
         */
        private final PreferenceType theType;

        /**
         * preference Value
         */
        private Object theValue = null;

        /**
         * New preference Value
         */
        private Object theNewValue = null;

        /**
         * Is there a change to the preference
         */
        private boolean isChanged = false;

        /**
         * Obtain the name of the preference
         * @return the name of the preference
         */
        public String getName() {
            return theName;
        }

        /**
         * Obtain the display name of the preference
         * @return the display name of the preference
         */
        public String getDisplay() {
            return theDisplay;
        }

        /**
         * Obtain the type of the preference
         * @return the type of the preference
         */
        public PreferenceType getType() {
            return theType;
        }

        /**
         * Obtain the value of the preference
         * @return the value of the preference
         */
        private Object getValue() {
            /* Return the active value */
            return (isChanged) ? theNewValue : theValue;
        }

        /**
         * Is the preference changed
         * @return is the preference changed
         */
        public boolean isChanged() {
            return isChanged;
        }

        /**
         * Constructor
         * @param pName the name of the preference
         * @param pType the type of the preference
         */
        private PreferenceItem(String pName,
                               PreferenceType pType) {
            /* Store name and type */
            theName = pName;
            theType = pType;
            theDisplay = getDisplayName(theName);
        }

        /**
         * Set value
         * @param pValue the value
         */
        private void setValue(Object pValue) {
            theValue = pValue;
        }

        /**
         * Set new value
         * @param pNewValue the new value
         */
        private void setNewValue(Object pNewValue) {
            theNewValue = (pNewValue == null) ? getDefaultValue(theName) : pNewValue;
            isChanged = Difference.getDifference(theNewValue, theValue).isDifferent();
        }

        /**
         * Reset changes
         */
        private void resetChanges() {
            /* Reset change indicators */
            theNewValue = null;
            isChanged = false;
        }

        /**
         * Store preference
         */
        private void storePreference() {
            /* If the preference has changed */
            if (isChanged) {
                /* else Store the value */
                storeThePreference(theNewValue);

                /* Note the new value and reset changes */
                setValue(theNewValue);
                resetChanges();
            }
        }

        /**
         * Store the value of the preference
         * @param pNewValue the new value to store
         */
        protected abstract void storeThePreference(Object pNewValue);
    }

    /**
     * Integer preference
     */
    public class IntegerPreference extends PreferenceItem {
        /**
         * Obtain the value of the preference
         * @return the value of the preference
         */
        public Integer getValue() {
            return (Integer) super.getValue();
        }

        /**
         * Constructor
         * @param pName the name of the preference
         */
        public IntegerPreference(String pName) {
            /* Store name */
            super(pName, PreferenceType.Integer);

            /* Check whether we have an existing value */
            boolean bExists = checkExists(pName);

            /* If it exists */
            if (bExists) {
                /* Access the value */
                int myValue = theHandle.getInt(pName, -1);

                /* Set as initial value */
                super.setValue(new Integer(myValue));
            }

            /* else value does not exist */
            else {
                /* Use default as a changed value */
                super.setNewValue(getDefaultValue(pName));
            }
        }

        /**
         * Set value
         * @param pNewValue the new value
         */
        public void setValue(Integer pNewValue) {
            Integer myNewValue = pNewValue;

            /* Take a copy if not null */
            if (myNewValue != null)
                myNewValue = new Integer(myNewValue);

            /* Set the new value */
            super.setNewValue(myNewValue);
        }

        @Override
        protected void storeThePreference(Object pNewValue) {
            /* Store the value */
            theHandle.putInt(getName(), (Integer) pNewValue);
        }
    }

    /**
     * Boolean preference
     */
    public class BooleanPreference extends PreferenceItem {
        /**
         * Obtain the value of the preference
         * @return the value of the preference
         */
        public Boolean getValue() {
            return (Boolean) super.getValue();
        }

        /**
         * Constructor
         * @param pName the name of the preference
         */
        public BooleanPreference(String pName) {
            /* Store name */
            super(pName, PreferenceType.Boolean);

            /* Check whether we have an existing value */
            boolean bExists = checkExists(pName);

            /* If it exists */
            if (bExists) {
                /* Access the value */
                boolean myValue = theHandle.getBoolean(pName, false);

                /* Set as initial value */
                super.setValue(myValue ? Boolean.TRUE : Boolean.FALSE);
            }

            /* else value does not exist */
            else {
                /* Use default as a changed value */
                super.setNewValue(getDefaultValue(pName));
            }
        }

        /**
         * Set value
         * @param pNewValue the new value
         */
        public void setValue(Boolean pNewValue) {
            Boolean myNewValue = pNewValue;

            /* Take a copy if not null */
            if (myNewValue != null)
                myNewValue = new Boolean(myNewValue ? Boolean.TRUE : Boolean.FALSE);

            /* Set the new value */
            super.setNewValue(myNewValue);
        }

        @Override
        protected void storeThePreference(Object pNewValue) {
            /* Store the value */
            theHandle.putBoolean(getName(), (Boolean) pNewValue);
        }
    }

    /**
     * String preference
     */
    public class StringPreference extends PreferenceItem {
        /**
         * Obtain the value of the preference
         * @return the value of the preference
         */
        public String getValue() {
            return (String) super.getValue();
        }

        /**
         * Constructor
         * @param pName the name of the preference
         */
        public StringPreference(String pName) {
            this(pName, PreferenceType.String);
        }

        /**
         * Constructor
         * @param pName the name of the preference
         * @param pType the type of the preference
         */
        public StringPreference(String pName,
                                PreferenceType pType) {
            /* Store name */
            super(pName, pType);

            /* Check whether we have an existing value */
            boolean bExists = checkExists(pName);

            /* If it exists */
            if (bExists) {
                /* Access the value */
                String myValue = theHandle.get(pName, null);

                /* Set as initial value */
                super.setValue(new String(myValue));
            }

            /* else value does not exist */
            else {
                /* Use default as a changed value */
                super.setNewValue(getDefaultValue(pName));
            }
        }

        /**
         * Set value
         * @param pNewValue the new value
         */
        public void setValue(String pNewValue) {
            String myNewValue = pNewValue;

            /* Take a copy if not null */
            if (myNewValue != null)
                myNewValue = new String(myNewValue);

            /* Set the new value */
            super.setNewValue(myNewValue);
        }

        @Override
        protected void storeThePreference(Object pNewValue) {
            /* Store the value */
            theHandle.put(getName(), (String) pNewValue);
        }
    }

    /**
     * Date preference
     */
    public class DatePreference extends PreferenceItem {
        /**
         * Obtain the value of the preference
         * @return the value of the preference
         */
        public DateDay getValue() {
            return (DateDay) super.getValue();
        }

        /**
         * Constructor
         * @param pName the name of the preference
         */
        public DatePreference(String pName) {
            /* Store name */
            super(pName, PreferenceType.Date);

            /* Check whether we have an existing value */
            boolean bExists = checkExists(pName);

            /* If it exists */
            if (bExists) {
                /* Access the value */
                String myValue = theHandle.get(pName, null);
                if (myValue == null)
                    bExists = false;
                else {
                    /* Parse the Date */
                    DateDay myDate = new DateDay(myValue);

                    /* Set as initial value */
                    super.setValue(myDate);
                }
            }

            /* if value does not exist or is invalid */
            if (!bExists) {
                /* Use default as a changed value */
                super.setNewValue(getDefaultValue(pName));
            }
        }

        /**
         * Set value
         * @param pNewValue the new value
         */
        public void setValue(DateDay pNewValue) {
            DateDay myNewValue = pNewValue;

            /* Take a copy if not null */
            if (myNewValue != null)
                myNewValue = new DateDay(myNewValue);

            /* Set the new value */
            super.setNewValue(myNewValue);
        }

        @Override
        protected void storeThePreference(Object pNewValue) {
            /* Store the value */
            theHandle.put(getName(), ((DateDay) pNewValue).toString());
        }
    }

    /**
     * Enum preference
     * @param <E> the Enum type
     */
    public class EnumPreference<E extends Enum<E>> extends PreferenceItem {
        /**
         * The enum class
         */
        private Class<E> theClass = null;

        /**
         * The enum class
         */
        private E[] theValues = null;

        /**
         * Obtain the value of the preference
         * @return the value of the preference
         */
        public E getValue() {
            return theClass.cast(super.getValue());
        }

        /**
         * Obtain the values of the preference
         * @return the values of the preference
         */
        public E[] getValues() {
            return theValues;
        }

        /**
         * Constructor
         * @param pName the name of the preference
         * @param pClass the class of the preference
         */
        public EnumPreference(String pName,
                              Class<E> pClass) {
            /* Store name */
            super(pName, PreferenceType.Enum);

            /* Store the class */
            theClass = pClass;
            theValues = theClass.getEnumConstants();

            /* Check whether we have an existing value */
            boolean bExists = checkExists(pName);

            /* If it exists */
            if (bExists) {
                /* Access the value */
                String myValue = theHandle.get(pName, null);
                if (myValue == null)
                    bExists = false;
                else {
                    /* Set the value */
                    bExists = setValue(myValue);
                }
            }

            /* if value does not exist or is invalid */
            if (!bExists) {
                /* Use default as a changed value */
                super.setNewValue(getDefaultValue(pName));
            }
        }

        /**
         * Set value
         * @param pNewValue the new value
         * @return whether the value was valid or not
         */
        public boolean setValue(String pNewValue) {
            /* Loop through the Enum constants */
            for (E myEnum : theValues) {
                /* If we match */
                if (pNewValue.equals(myEnum.name())) {
                    /* Set as initial value */
                    super.setValue(myEnum);
                    return true;
                }
            }

            /* Return invalid value */
            return false;
        }

        /**
         * Set value
         * @param pNewValue the new value
         */
        public void setValue(Enum<E> pNewValue) {
            /* Set the new value */
            super.setNewValue(pNewValue);
        }

        @Override
        protected void storeThePreference(Object pNewValue) {
            /* Store the value */
            theHandle.put(getName(), theClass.cast(pNewValue).name());
        }
    }

    /**
     * Enum class for preference types
     */
    public enum PreferenceType {
        String, Integer, Boolean, Date, File, Directory, Enum;
    }

    /**
     * Interface to determine relevant preference set
     */
    public static interface PreferenceSetChooser {
        /**
         * Determine class of relevant preference set
         * @return the preferenceSet class
         */
        public Class<? extends PreferenceSet> getPreferenceSetClass();
    }

    /**
     * preferenceSetManager class
     */
    public static class PreferenceManager {
        /**
         * Map of preferenceSets
         */
        private static Map<Class<?>, PreferenceSet> theMap = new HashMap<Class<?>, PreferenceSet>();

        /**
         * Obtain the collection of preference sets
         * @return the preference sets
         */
        public static Collection<PreferenceSet> getPreferenceSets() {
            return theMap.values();
        }

        /**
         * Listener manager
         */
        private static EventManager theListeners = new EventManager(PreferenceManager.class);

        /**
         * Add action Listener to list
         * @param pListener the listener to add
         */
        public static void addActionListener(ActionListener pListener) {
            /* Add the Action Listener to the list */
            theListeners.addActionListener(pListener);
        }

        /**
         * Remove action Listener to list
         * @param pListener the listener to remove
         */
        public static void removeActionListener(ActionListener pListener) {
            /* Remove the Action Listener from the list */
            theListeners.removeActionListener(pListener);
        }

        /**
         * Obtain the preference set for the calling class
         * @param pOwner the owning class
         * @return the relevant preferenceSet
         */
        public static PreferenceSet getPreferenceSet(PreferenceSetChooser pOwner) {
            /* Determine the required preferenceSet class */
            Class<? extends PreferenceSet> myClass = pOwner.getPreferenceSetClass();

            /* Return the PreferenceSet */
            return getPreferenceSet(myClass);
        }

        /**
         * Obtain the preference set for the calling class
         * @param <X> the preference set type
         * @param pClass the class of the preference set
         * @return the relevant preferenceSet
         */
        @SuppressWarnings("unchecked")
        public static synchronized <X extends PreferenceSet> X getPreferenceSet(Class<X> pClass) {
            /* Locate a cached PreferenceSet */
            PreferenceSet mySet = theMap.get(pClass);

            /* If we have not seen this set before */
            if (mySet == null) {
                /* Protect against exceptions */
                try {
                    /* Access the new set */
                    mySet = pClass.newInstance();

                    /* Cache the set */
                    theMap.put(pClass, mySet);

                    /* Fire the action performed */
                    theListeners.fireActionPerformed(mySet, ActionEvent.ACTION_PERFORMED, null);
                } catch (Exception e) {
                }
            }

            /* Return the PreferenceSet */
            return (X) mySet;
        }
    }
}
