/*******************************************************************************
 * JDataModels: Data models
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
package net.sourceforge.JDataModels.data;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import net.sourceforge.JDataManager.DataConverter;
import net.sourceforge.JDataManager.Difference;
import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataException.ExceptionClass;
import net.sourceforge.JDateDay.JDateDay;
import net.sourceforge.JEventManager.JEventManager;
import net.sourceforge.JEventManager.JEventObject;

/**
 * Wrapper class for java preferences.
 * @author Tony Washer
 */
public abstract class PreferenceSet extends JEventObject {
    /**
     * Unknown preference string.
     */
    private static final String ERROR_UNKNOWN = "Unknown Preference: ";

    /**
     * Invalid preference string.
     */
    private static final String ERROR_INVALID = "Invalid Preference: ";

    /**
     * Font separator.
     */
    private static final String FONT_SEPARATOR = ":";

    /**
     * The Preference node for this set.
     */
    private Preferences theHandle = null;

    /**
     * The map of preferences.
     */
    private Map<String, PreferenceItem> theMap = null;

    /**
     * The list of preferences that have a value on initialisation.
     */
    private String[] theActive = null;

    /**
     * Obtain the collection of preferences.
     * @return the preferences
     */
    public Collection<PreferenceItem> getPreferences() {
        return theMap.values();
    }

    /**
     * Constructor.
     * @throws JDataException on error
     */
    public PreferenceSet() throws JDataException {
        /* Access the handle */
        theHandle = Preferences.userNodeForPackage(this.getClass());

        /* Allocate the preference map */
        theMap = new HashMap<String, PreferenceItem>();

        /* Access the active key names */
        try {
            theActive = theHandle.keys();
        } catch (BackingStoreException e) {
            throw new JDataException(ExceptionClass.PREFERENCE, "Failed to access preferences", e);
        }

        /* Define the preferences */
        definePreferences();

        /* Store preference changes */
        storeChanges();
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

    /**
     * Define new String preference.
     * @param pName the name of the preference
     * @param pDefault the default value of the preference
     * @return the preference item
     */
    protected StringPreference defineStringPreference(final String pName,
                                                      final String pDefault) {
        /* Define the preference */
        StringPreference myPref = new StringPreference(pName, pDefault);

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
    protected StringPreference defineFilePreference(final String pName,
                                                    final String pDefault) {
        /* Define the preference */
        StringPreference myPref = new StringPreference(pName, pDefault, PreferenceType.File);

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
    protected StringPreference defineDirectoryPreference(final String pName,
                                                         final String pDefault) {
        /* Define the preference */
        StringPreference myPref = new StringPreference(pName, pDefault, PreferenceType.Directory);

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
    protected IntegerPreference defineIntegerPreference(final String pName,
                                                        final Integer pDefault) {
        /* Define the preference */
        IntegerPreference myPref = new IntegerPreference(pName, pDefault);

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
    protected BooleanPreference defineBooleanPreference(final String pName,
                                                        final Boolean pDefault) {
        /* Define the preference */
        BooleanPreference myPref = new BooleanPreference(pName, pDefault);

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
    protected DatePreference defineDatePreference(final String pName,
                                                  final JDateDay pDefault) {
        /* Define the preference */
        DatePreference myPref = new DatePreference(pName, pDefault);

        /* Add it to the list of preferences */
        definePreference(myPref);

        /* Return the preference */
        return myPref;
    }

    /**
     * Define new Colour preference.
     * @param pName the name of the preference
     * @param pDefault the default value of the preference
     * @return the preference item
     */
    protected ColorPreference defineColorPreference(final String pName,
                                                    final Color pDefault) {
        /* Define the preference */
        ColorPreference myPref = new ColorPreference(pName, pDefault);

        /* Add it to the list of preferences */
        definePreference(myPref);

        /* Return the preference */
        return myPref;
    }

    /**
     * Define new Font preference.
     * @param pName the name of the preference
     * @param pDefault the default value of the preference
     * @return the preference item
     */
    protected FontPreference defineFontPreference(final String pName,
                                                  final Font pDefault) {
        /* Define the preference */
        FontPreference myPref = new FontPreference(pName, pDefault);

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
    protected <E extends Enum<E>> EnumPreference<E> definePreference(final String pName,
                                                                     final E pDefault,
                                                                     final Class<E> pClass) {
        /* Create the preference */
        EnumPreference<E> myPref = new EnumPreference<E>(pName, pDefault, pClass);

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
    public PreferenceItem getPreference(final String pName) {
        return theMap.get(pName);
    }

    /**
     * Obtain Integer value.
     * @param pName the name of the preference
     * @return the Integer value
     */
    public Integer getIntegerValue(final String pName) {
        /* Access preference */
        PreferenceItem myPref = getPreference(pName);

        /* Reject if not found */
        if (myPref == null) {
            throw new IllegalArgumentException(ERROR_UNKNOWN + pName);
        }

        /* Reject if wrong type */
        if (!(myPref instanceof IntegerPreference)) {
            throw new IllegalArgumentException(ERROR_INVALID + pName);
        }

        /* Access preference */
        IntegerPreference myIntPref = (IntegerPreference) myPref;

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
        PreferenceItem myPref = getPreference(pName);

        /* Reject if not found */
        if (myPref == null) {
            throw new IllegalArgumentException(ERROR_UNKNOWN + pName);
        }

        /* Reject if wrong type */
        if (!(myPref instanceof BooleanPreference)) {
            throw new IllegalArgumentException(ERROR_INVALID + pName);
        }

        /* Access preference */
        BooleanPreference myBoolPref = (BooleanPreference) myPref;

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
        PreferenceItem myPref = getPreference(pName);

        /* Reject if not found */
        if (myPref == null) {
            throw new IllegalArgumentException(ERROR_UNKNOWN + pName);
        }

        /* Reject if wrong type */
        if (!(myPref instanceof StringPreference)) {
            throw new IllegalArgumentException(ERROR_INVALID + pName);
        }

        /* Access preference */
        StringPreference myStringPref = (StringPreference) myPref;

        /* Return the value */
        return myStringPref.getValue();
    }

    /**
     * Obtain Date value.
     * @param pName the name of the preference
     * @return the Date
     */
    public JDateDay getDateValue(final String pName) {
        /* Access preference */
        PreferenceItem myPref = getPreference(pName);

        /* Reject if not found */
        if (myPref == null) {
            throw new IllegalArgumentException(ERROR_UNKNOWN + pName);
        }

        /* Reject if wrong type */
        if (!(myPref instanceof DatePreference)) {
            throw new IllegalArgumentException(ERROR_INVALID + pName);
        }

        /* Access preference */
        DatePreference myDatePref = (DatePreference) myPref;

        /* Return the value */
        return myDatePref.getValue();
    }

    /**
     * Obtain Colour value.
     * @param pName the name of the preference
     * @return the Colour
     */
    public Color getColorValue(final String pName) {
        /* Access preference */
        PreferenceItem myPref = getPreference(pName);

        /* Reject if not found */
        if (myPref == null) {
            throw new IllegalArgumentException(ERROR_UNKNOWN + pName);
        }

        /* Reject if wrong type */
        if (!(myPref instanceof ColorPreference)) {
            throw new IllegalArgumentException(ERROR_INVALID + pName);
        }

        /* Access preference */
        ColorPreference myColorPref = (ColorPreference) myPref;

        /* Return the value */
        return myColorPref.getValue();
    }

    /**
     * Obtain Font value.
     * @param pName the name of the preference
     * @return the Font
     */
    public Font getFontValue(final String pName) {
        /* Access preference */
        PreferenceItem myPref = getPreference(pName);

        /* Reject if not found */
        if (myPref == null) {
            throw new IllegalArgumentException(ERROR_UNKNOWN + pName);
        }

        /* Reject if wrong type */
        if (!(myPref instanceof FontPreference)) {
            throw new IllegalArgumentException(ERROR_INVALID + pName);
        }

        /* Access preference */
        FontPreference myFontPref = (FontPreference) myPref;

        /* Return the value */
        return myFontPref.getValue();
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
        PreferenceItem myPref = getPreference(pName);

        /* Reject if not found */
        if (myPref == null) {
            throw new IllegalArgumentException(ERROR_UNKNOWN + pName);
        }

        /* Reject if wrong type */
        if (!(myPref instanceof EnumPreference)) {
            throw new IllegalArgumentException(ERROR_INVALID + pName);
        }

        /* Access as Enum preference */
        @SuppressWarnings("unchecked")
        EnumPreference<E> myEnumPref = (EnumPreference<E>) myPref;
        if (myEnumPref.theClass != pClass) {
            throw new IllegalArgumentException(ERROR_INVALID + pName);
        }

        /* Return the value */
        return myEnumPref.getValue();
    }

    /**
     * Define a preference for the node.
     * @param pPreference the preference to define
     */
    private void definePreference(final PreferenceItem pPreference) {
        /* Access the name of the preference */
        String myName = pPreference.getName();

        /* Reject if the name is already present */
        if (theMap.get(myName) != null) {
            throw new IllegalArgumentException("preference " + myName + " is already defined");
        }

        /* Add the preference to the map */
        theMap.put(pPreference.getName(), pPreference);
    }

    /**
     * Reset all changes in this preference set.
     */
    public void resetChanges() {
        /* Loop through all the preferences */
        for (PreferenceItem myPref : theMap.values()) {
            /* Reset the changes */
            myPref.resetChanges();
        }
    }

    /**
     * Store preference changes.
     * @throws JDataException on error
     */
    public final void storeChanges() throws JDataException {
        /* Loop through all the preferences */
        for (PreferenceItem myPref : theMap.values()) {
            /* Store any changes */
            myPref.storePreference();
        }

        /* Protect against exceptions */
        try {
            /* Flush the output */
            theHandle.flush();

            /* Notify listeners */
            fireStateChanged();
        } catch (BackingStoreException e) {
            throw new JDataException(ExceptionClass.PREFERENCE, "Failed to flush preferences to store", e);
        }
    }

    /**
     * Does the preference set have changes.
     * @return true/false
     */
    public boolean hasChanges() {
        /* Loop through all the preferences */
        for (PreferenceItem myPref : theMap.values()) {
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
    private boolean checkExists(final String pName) {
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
    public abstract class PreferenceItem {
        /**
         * preference Name.
         */
        private final String theName;

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
        private final PreferenceType theType;

        /**
         * preference Value.
         */
        private Object theValue = null;

        /**
         * New preference Value.
         */
        private Object theNewValue = null;

        /**
         * Is there a change to the preference.
         */
        private boolean isChanged = false;

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
        public PreferenceType getType() {
            return theType;
        }

        /**
         * Obtain the value of the preference.
         * @return the value of the preference
         */
        private Object getValue() {
            /* Return the active value */
            return (isChanged) ? theNewValue : theValue;
        }

        /**
         * Is the preference changed.
         * @return is the preference changed
         */
        public boolean isChanged() {
            return isChanged;
        }

        /**
         * Constructor.
         * @param pName the name of the preference
         * @param pDefault the default value
         * @param pType the type of the preference
         */
        private PreferenceItem(final String pName,
                               final Object pDefault,
                               final PreferenceType pType) {
            /* Store name and type */
            theName = pName;
            theDefault = pDefault;
            theType = pType;
            theDisplay = getDisplayName(theName);
        }

        /**
         * Set value.
         * @param pValue the value
         */
        private void setValue(final Object pValue) {
            theValue = pValue;
        }

        /**
         * Set new value.
         * @param pNewValue the new value
         */
        private void setNewValue(final Object pNewValue) {
            theNewValue = (pNewValue == null) ? theDefault : pNewValue;
            isChanged = !Difference.isEqual(theNewValue, theValue);
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
                setValue(theNewValue);
                resetChanges();
            }
        }

        /**
         * Store the value of the preference.
         * @param pNewValue the new value to store
         */
        protected abstract void storeThePreference(final Object pNewValue);
    }

    /**
     * Integer preference.
     */
    public class IntegerPreference extends PreferenceItem {
        /**
         * Obtain the value of the preference.
         * @return the value of the preference
         */
        public Integer getValue() {
            return (Integer) super.getValue();
        }

        /**
         * Constructor.
         * @param pName the name of the preference
         * @param pDefault the default value
         */
        public IntegerPreference(final String pName,
                                 final Integer pDefault) {
            /* Store name */
            super(pName, pDefault, PreferenceType.Integer);

            /* Check whether we have an existing value */
            boolean bExists = checkExists(pName);

            /* If it exists */
            if (bExists) {
                /* Access the value */
                int myValue = theHandle.getInt(pName, -1);

                /* Set as initial value */
                super.setValue(Integer.valueOf(myValue));

                /* else value does not exist */
            } else {
                /* Use default as a changed value */
                super.setNewValue(pDefault);
            }
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
    public class BooleanPreference extends PreferenceItem {
        /**
         * Obtain the value of the preference.
         * @return the value of the preference
         */
        public Boolean getValue() {
            return (Boolean) super.getValue();
        }

        /**
         * Constructor.
         * @param pName the name of the preference
         * @param pDefault the default value
         */
        public BooleanPreference(final String pName,
                                 final Boolean pDefault) {
            /* Store name */
            super(pName, pDefault, PreferenceType.Boolean);

            /* Check whether we have an existing value */
            boolean bExists = checkExists(pName);

            /* If it exists */
            if (bExists) {
                /* Access the value */
                boolean myValue = theHandle.getBoolean(pName, false);

                /* Set as initial value */
                super.setValue(myValue ? Boolean.TRUE : Boolean.FALSE);

                /* else value does not exist */
            } else {
                /* Use default as a changed value */
                super.setNewValue(pDefault);
            }
        }

        /**
         * Set value.
         * @param pNewValue the new value
         */
        public void setValue(final Boolean pNewValue) {
            Boolean myNewValue = pNewValue;

            /* Take a copy if not null */
            if (myNewValue != null) {
                myNewValue = myNewValue ? Boolean.TRUE : Boolean.FALSE;
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
    public class StringPreference extends PreferenceItem {
        /**
         * Obtain the value of the preference.
         * @return the value of the preference
         */
        public String getValue() {
            return (String) super.getValue();
        }

        /**
         * Constructor.
         * @param pName the name of the preference
         * @param pDefault the default value
         */
        public StringPreference(final String pName,
                                final String pDefault) {
            this(pName, pDefault, PreferenceType.String);
        }

        /**
         * Constructor.
         * @param pName the name of the preference
         * @param pDefault the default value
         * @param pType the type of the preference
         */
        public StringPreference(final String pName,
                                final String pDefault,
                                final PreferenceType pType) {
            /* Store name */
            super(pName, pDefault, pType);

            /* Check whether we have an existing value */
            boolean bExists = checkExists(pName);

            /* If it exists */
            if (bExists) {
                /* Access the value */
                String myValue = theHandle.get(pName, null);

                /* Set as initial value */
                super.setValue(myValue);

                /* else value does not exist */
            } else {
                /* Use default as a changed value */
                super.setNewValue(pDefault);
            }
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
    public class DatePreference extends PreferenceItem {
        /**
         * Obtain the value of the preference.
         * @return the value of the preference
         */
        public JDateDay getValue() {
            return (JDateDay) super.getValue();
        }

        /**
         * Constructor.
         * @param pName the name of the preference
         * @param pDefault the default value
         */
        public DatePreference(final String pName,
                              final JDateDay pDefault) {
            /* Store name */
            super(pName, pDefault, PreferenceType.Date);

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
                    JDateDay myDate = new JDateDay(myValue);

                    /* Set as initial value */
                    super.setValue(myDate);
                }
            }

            /* if value does not exist or is invalid */
            if (!bExists) {
                /* Use default as a changed value */
                super.setNewValue(pDefault);
            }
        }

        /**
         * Set value.
         * @param pNewValue the new value
         */
        public void setValue(final JDateDay pNewValue) {
            JDateDay myNewValue = pNewValue;

            /* Take a copy if not null */
            if (myNewValue != null) {
                myNewValue = new JDateDay(myNewValue);
            }

            /* Set the new value */
            super.setNewValue(myNewValue);
        }

        @Override
        protected void storeThePreference(final Object pNewValue) {
            /* Store the value */
            theHandle.put(getName(), ((JDateDay) pNewValue).toString());
        }
    }

    /**
     * Colour preference.
     */
    public class ColorPreference extends PreferenceItem {
        /**
         * Obtain the value of the preference.
         * @return the value of the preference
         */
        public Color getValue() {
            return (Color) super.getValue();
        }

        /**
         * Constructor.
         * @param pName the name of the preference
         * @param pDefault the default value
         */
        public ColorPreference(final String pName,
                               final Color pDefault) {
            /* Store name */
            super(pName, pDefault, PreferenceType.Color);

            /* Check whether we have an existing value */
            boolean bExists = checkExists(pName);

            /* If it exists */
            if (bExists) {
                /* Access the value */
                String myValue = theHandle.get(pName, null);
                if (myValue == null) {
                    bExists = false;
                } else {
                    /* Parse the Colour */
                    Color myColor = Color.decode(myValue);

                    /* Set as initial value */
                    super.setValue(myColor);
                }
            }

            /* if value does not exist or is invalid */
            if (!bExists) {
                /* Use default as a changed value */
                super.setNewValue(pDefault);
            }
        }

        /**
         * Set value.
         * @param pNewValue the new value
         */
        public void setValue(final Color pNewValue) {
            /* Set the new value */
            super.setNewValue(pNewValue);
        }

        @Override
        protected void storeThePreference(final Object pNewValue) {
            /* Store the value */
            theHandle.put(getName(), DataConverter.colorToHexString((Color) pNewValue));
        }
    }

    /**
     * Font preference.
     */
    public class FontPreference extends PreferenceItem {
        /**
         * Obtain the value of the preference.
         * @return the value of the preference
         */
        public Font getValue() {
            return (Font) super.getValue();
        }

        /**
         * Constructor.
         * @param pName the name of the preference
         * @param pDefault the default value
         */
        public FontPreference(final String pName,
                              final Font pDefault) {
            /* Store name */
            super(pName, pDefault, PreferenceType.Font);

            /* Check whether we have an existing value */
            boolean bExists = checkExists(pName);

            /* If it exists */
            if (bExists) {
                /* Access the value */
                String myValue = theHandle.get(pName, null);
                if (myValue == null) {
                    bExists = false;
                } else {
                    /* Parse the value */
                    int myPos = myValue.indexOf(FONT_SEPARATOR);
                    bExists = (myPos > 0);

                    /* If we look good */
                    if (bExists) {
                        /* Access parts */
                        int mySize = Integer.parseInt(myValue.substring(myPos + 1));
                        String myName = myValue.substring(0, myPos);

                        /* Create the font */
                        Font myFont = new Font(myName, Font.PLAIN, mySize);

                        /* Set as initial value */
                        super.setValue(myFont);
                    }
                }
            }

            /* if value does not exist or is invalid */
            if (!bExists) {
                /* Use default as a changed value */
                super.setNewValue(pDefault);
            }
        }

        /**
         * Set value.
         * @param pNewValue the new value
         */
        public void setValue(final Font pNewValue) {
            /* Set the new value */
            super.setNewValue(pNewValue);
        }

        @Override
        protected void storeThePreference(final Object pNewValue) {
            /* Store the value */
            Font myFont = (Font) pNewValue;
            theHandle.put(getName(), myFont.getFontName() + FONT_SEPARATOR + myFont.getSize());
        }
    }

    /**
     * Enum preference.
     * @param <E> the Enum type
     */
    public class EnumPreference<E extends Enum<E>> extends PreferenceItem {
        /**
         * The enum class.
         */
        private Class<E> theClass = null;

        /**
         * The enum values.
         */
        private E[] theValues = null;

        /**
         * Obtain the value of the preference.
         * @return the value of the preference
         */
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
         * Constructor.
         * @param pName the name of the preference
         * @param pDefault the default value
         * @param pClass the class of the preference
         */
        public EnumPreference(final String pName,
                              final E pDefault,
                              final Class<E> pClass) {
            /* Store name */
            super(pName, pDefault, PreferenceType.Enum);

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
                    bExists = (myEnum != null);
                    if (bExists) {
                        super.setValue(myEnum);
                    }
                }
            }

            /* if value does not exist or is invalid */
            if (!bExists) {
                /* Use default as a changed value */
                super.setNewValue(pDefault);
            }
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
        public void setValue(final E pNewValue) {
            /* Set the new value */
            super.setNewValue(pNewValue);
        }

        @Override
        protected void storeThePreference(final Object pNewValue) {
            /* Store the value */
            theHandle.put(getName(), theClass.cast(pNewValue).name());
        }
    }

    /**
     * Enum class for preference types.
     */
    public enum PreferenceType {
        /**
         * String.
         */
        String,

        /**
         * Integer.
         */
        Integer,

        /**
         * Boolean.
         */
        Boolean,

        /**
         * Date.
         */
        Date,

        /**
         * File.
         */
        File,

        /**
         * Directory.
         */
        Directory,

        /**
         * Enum.
         */
        Enum,

        /**
         * Colour.
         */
        Color,

        /**
         * Font.
         */
        Font;
    }

    /**
     * Interface to determine relevant preference set.
     */
    public interface PreferenceSetChooser {
        /**
         * Determine class of relevant preference set.
         * @return the preferenceSet class
         */
        Class<? extends PreferenceSet> getPreferenceSetClass();
    }

    /**
     * preferenceSetManager class.
     */
    public static class PreferenceManager {
        /**
         * Map of preferenceSets.
         */
        private static Map<Class<?>, PreferenceSet> theMap = new HashMap<Class<?>, PreferenceSet>();

        /**
         * Obtain the collection of preference sets.
         * @return the preference sets
         */
        public static Collection<PreferenceSet> getPreferenceSets() {
            return theMap.values();
        }

        /**
         * Listener manager.
         */
        private static JEventManager theListeners = new JEventManager(PreferenceManager.class);

        /**
         * Add action Listener to list.
         * @param pListener the listener to add
         */
        public static void addActionListener(final ActionListener pListener) {
            /* Add the Action Listener to the list */
            theListeners.addActionListener(pListener);
        }

        /**
         * Remove action Listener from list.
         * @param pListener the listener to remove
         */
        public static void removeActionListener(final ActionListener pListener) {
            /* Remove the Action Listener from the list */
            theListeners.removeActionListener(pListener);
        }

        /**
         * Obtain the preference set for the calling class.
         * @param pOwner the owning class
         * @return the relevant preferenceSet
         */
        public static PreferenceSet getPreferenceSet(final PreferenceSetChooser pOwner) {
            /* Determine the required preferenceSet class */
            Class<? extends PreferenceSet> myClass = pOwner.getPreferenceSetClass();

            /* Return the PreferenceSet */
            return getPreferenceSet(myClass);
        }

        /**
         * Obtain the preference set for the calling class.
         * @param <X> the preference set type
         * @param pClass the class of the preference set
         * @return the relevant preferenceSet
         */
        @SuppressWarnings("unchecked")
        public static synchronized <X extends PreferenceSet> X getPreferenceSet(final Class<X> pClass) {
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
                    theListeners.fireActionEvent(mySet, ActionEvent.ACTION_PERFORMED, null);
                } catch (IllegalAccessException e) {
                    mySet = null;
                } catch (InstantiationException e) {
                    mySet = null;
                }
            }

            /* Return the PreferenceSet */
            return (X) mySet;
        }
    }
}
