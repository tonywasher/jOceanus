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

import java.awt.Color;
import java.awt.Font;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import net.sourceforge.joceanus.jmetis.JMetisDataException;
import net.sourceforge.joceanus.jmetis.data.Difference;
import net.sourceforge.joceanus.jmetis.data.JDataFieldValue;
import net.sourceforge.joceanus.jmetis.data.JDataFields;
import net.sourceforge.joceanus.jmetis.data.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.field.JFieldSetItem;
import net.sourceforge.joceanus.jmetis.field.JFieldState;
import net.sourceforge.joceanus.jtethys.DataConverter;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;
import net.sourceforge.joceanus.jtethys.event.swing.JEventObject;

/**
 * Wrapper class for java preferences.
 * @author Tony Washer
 */
public abstract class PreferenceSet
        extends JEventObject
        implements JFieldSetItem {
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
     * Report fields.
     */
    private final JDataFields theFields = new JDataFields(PreferenceSet.class.getSimpleName());

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
     * Constructor.
     * @throws JOceanusException on error
     */
    public PreferenceSet() throws JOceanusException {
        /* Access the handle */
        theHandle = Preferences.userNodeForPackage(this.getClass());
        theHandle = theHandle.node(this.getClass().getSimpleName());

        /* Allocate the preference map */
        theMap = new LinkedHashMap<String, PreferenceItem>();

        /* Access the active key names */
        try {
            theActive = theHandle.keys();
        } catch (BackingStoreException e) {
            throw new JMetisDataException("Failed to access preferences", e);
        }

        /* Define the preferences */
        definePreferences();

        /* Store preference changes */
        storeChanges();
    }

    @Override
    public JDataFields getDataFields() {
        return theFields;
    }

    /**
     * Obtain the collection of preferences.
     * @return the preferences
     */
    public Collection<PreferenceItem> getPreferences() {
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
        StringPreference myPref = new StringPreference(pName, pDefault, PreferenceType.FILE);

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
        StringPreference myPref = new StringPreference(pName, pDefault, PreferenceType.DIRECTORY);

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
            throw new IllegalArgumentException(ERROR_UNKNOWN
                                               + pName);
        }

        /* Reject if wrong type */
        if (!(myPref instanceof IntegerPreference)) {
            throw new IllegalArgumentException(ERROR_INVALID
                                               + pName);
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
            throw new IllegalArgumentException(ERROR_UNKNOWN
                                               + pName);
        }

        /* Reject if wrong type */
        if (!(myPref instanceof BooleanPreference)) {
            throw new IllegalArgumentException(ERROR_INVALID
                                               + pName);
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
            throw new IllegalArgumentException(ERROR_UNKNOWN
                                               + pName);
        }

        /* Reject if wrong type */
        if (!(myPref instanceof StringPreference)) {
            throw new IllegalArgumentException(ERROR_INVALID
                                               + pName);
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
            throw new IllegalArgumentException(ERROR_UNKNOWN
                                               + pName);
        }

        /* Reject if wrong type */
        if (!(myPref instanceof DatePreference)) {
            throw new IllegalArgumentException(ERROR_INVALID
                                               + pName);
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
            throw new IllegalArgumentException(ERROR_UNKNOWN
                                               + pName);
        }

        /* Reject if wrong type */
        if (!(myPref instanceof ColorPreference)) {
            throw new IllegalArgumentException(ERROR_INVALID
                                               + pName);
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
            throw new IllegalArgumentException(ERROR_UNKNOWN
                                               + pName);
        }

        /* Reject if wrong type */
        if (!(myPref instanceof FontPreference)) {
            throw new IllegalArgumentException(ERROR_INVALID
                                               + pName);
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
            throw new IllegalArgumentException(ERROR_UNKNOWN
                                               + pName);
        }

        /* Reject if wrong type */
        if (!(myPref instanceof EnumPreference)) {
            throw new IllegalArgumentException(ERROR_INVALID
                                               + pName);
        }

        /* Access as Enum preference */
        @SuppressWarnings("unchecked")
        EnumPreference<E> myEnumPref = (EnumPreference<E>) myPref;
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
    private void definePreference(final PreferenceItem pPreference) {
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
        for (PreferenceItem myPref : theMap.values()) {
            /* Reset the changes */
            myPref.resetChanges();
        }
    }

    /**
     * Store preference changes.
     * @throws JOceanusException on error
     */
    public final void storeChanges() throws JOceanusException {
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
            throw new JMetisDataException("Failed to flush preferences to store", e);
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
         * New preference Value.
         */
        private final JDataField theField;

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
        public PreferenceType getType() {
            return theType;
        }

        /**
         * Obtain the field for the preference.
         * @return the field for the preference
         */
        public JDataField getDataField() {
            return theField;
        }

        /**
         * Obtain the value of the preference.
         * @return the value of the preference
         */
        private Object getValue() {
            /* Return the active value */
            return (isChanged)
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
        private void setValue(final Object pValue) {
            theValue = pValue;
        }

        /**
         * Set new value.
         * @param pNewValue the new value
         */
        private void setNewValue(final Object pNewValue) {
            theNewValue = (pNewValue == null)
                                             ? theDefault
                                             : pNewValue;
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
    public class IntegerPreference
            extends PreferenceItem {
        /**
         * Constructor.
         * @param pName the name of the preference
         * @param pDefault the default value
         */
        public IntegerPreference(final String pName,
                                 final Integer pDefault) {
            /* Store name */
            super(pName, pDefault, PreferenceType.INTEGER);

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
         * Obtain the value of the preference.
         * @return the value of the preference
         */
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
    public class BooleanPreference
            extends PreferenceItem {
        /**
         * Constructor.
         * @param pName the name of the preference
         * @param pDefault the default value
         */
        public BooleanPreference(final String pName,
                                 final Boolean pDefault) {
            /* Store name */
            super(pName, pDefault, PreferenceType.BOOLEAN);

            /* Check whether we have an existing value */
            boolean bExists = checkExists(pName);

            /* If it exists */
            if (bExists) {
                /* Access the value */
                boolean myValue = theHandle.getBoolean(pName, false);

                /* Set as initial value */
                super.setValue(myValue
                                      ? Boolean.TRUE
                                      : Boolean.FALSE);

                /* else value does not exist */
            } else {
                /* Use default as a changed value */
                super.setNewValue(pDefault);
            }
        }

        /**
         * Obtain the value of the preference.
         * @return the value of the preference
         */
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
    public class StringPreference
            extends PreferenceItem {
        /**
         * Constructor.
         * @param pName the name of the preference
         * @param pDefault the default value
         */
        public StringPreference(final String pName,
                                final String pDefault) {
            this(pName, pDefault, PreferenceType.STRING);
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
         * Obtain the value of the preference.
         * @return the value of the preference
         */
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
    public class DatePreference
            extends PreferenceItem {
        /**
         * Constructor.
         * @param pName the name of the preference
         * @param pDefault the default value
         */
        public DatePreference(final String pName,
                              final JDateDay pDefault) {
            /* Store name */
            super(pName, pDefault, PreferenceType.DATE);

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
         * Obtain the value of the preference.
         * @return the value of the preference
         */
        public JDateDay getValue() {
            return (JDateDay) super.getValue();
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
    public class ColorPreference
            extends PreferenceItem {
        /**
         * Constructor.
         * @param pName the name of the preference
         * @param pDefault the default value
         */
        public ColorPreference(final String pName,
                               final Color pDefault) {
            /* Store name */
            super(pName, pDefault, PreferenceType.COLOR);

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
         * Obtain the value of the preference.
         * @return the value of the preference
         */
        public Color getValue() {
            return (Color) super.getValue();
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
    public class FontPreference
            extends PreferenceItem {
        /**
         * Constructor.
         * @param pName the name of the preference
         * @param pDefault the default value
         */
        public FontPreference(final String pName,
                              final Font pDefault) {
            /* Store name */
            super(pName, pDefault, PreferenceType.FONT);

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
         * Obtain the value of the preference.
         * @return the value of the preference
         */
        public Font getValue() {
            return (Font) super.getValue();
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
            theHandle.put(getName(), myFont.getFontName()
                                     + FONT_SEPARATOR
                                     + myFont.getSize());
        }
    }

    /**
     * Enum preference.
     * @param <E> the Enum type
     */
    public class EnumPreference<E extends Enum<E>>
            extends PreferenceItem {
        /**
         * The enum class.
         */
        private Class<E> theClass = null;

        /**
         * The enum values.
         */
        private E[] theValues = null;

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
            super(pName, pDefault, PreferenceType.ENUM);

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
    public String getFieldErrors(final JDataField pField) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getFieldErrors(final JDataField[] pFields) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JFieldState getItemState() {
        throw new UnsupportedOperationException();
    }

    @Override
    public JFieldState getFieldState(final JDataField pField) {
        /* Access preference */
        PreferenceItem myPref = getPreference(pField.getName());

        /* If it is found */
        if (myPref != null) {
            /* Return the relevant state */
            return myPref.isChanged()
                                     ? JFieldState.CHANGED
                                     : JFieldState.NORMAL;
        }

        /* Not recognised */
        return JFieldState.NORMAL;
    }

    @Override
    public String formatObject() {
        return theFields.getName();
    }

    @Override
    public Object getFieldValue(final JDataField pField) {
        /* Access preference */
        PreferenceItem myPref = getPreference(pField.getName());

        /* Return the value */
        return (myPref == null)
                               ? JDataFieldValue.UNKNOWN
                               : myPref.getValue();
    }
}
