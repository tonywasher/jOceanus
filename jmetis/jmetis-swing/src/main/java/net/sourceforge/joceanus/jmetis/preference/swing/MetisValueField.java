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
package net.sourceforge.joceanus.jmetis.preference.swing;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.Arrays;

import javax.swing.JTextField;

import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.data.MetisDifference;
import net.sourceforge.joceanus.jmetis.field.MetisFieldValue;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceValueClass;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilution;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;

/**
 * ValueField provides a JTextField which is geared to a particular type of data.
 * @author Tony Washer
 */
public class MetisValueField
        extends JTextField {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = -1865387282196348594L;

    /**
     * The Data Model.
     */
    private transient DataModel theModel = null;

    /**
     * The Data Formatter.
     */
    private final transient MetisDataFormatter theFormatter;

    /**
     * The Cached foreground colour.
     */
    private Color theCache = null;

    /**
     * Value property.
     */
    public static final String PROPERTY_VALUE = "value";

    /**
     * Constructor.
     */
    public MetisValueField() {
        this(MetisPreferenceValueClass.STRING);
    }

    /**
     * Constructor.
     * @param pClass the value class
     */
    public MetisValueField(final MetisPreferenceValueClass pClass) {
        this(pClass, new MetisDataFormatter());
    }

    /**
     * Constructor.
     * @param pClass the value class
     * @param pFormatter the date formatter
     */
    public MetisValueField(final MetisPreferenceValueClass pClass,
                           final MetisDataFormatter pFormatter) {
        /* Store the formatter */
        theFormatter = pFormatter;

        /* Switch on requested class */
        switch (pClass) {
            /* Create appropriate model class */
            case STRING:
                theModel = new StringModel();
                break;
            case INTEGER:
                theModel = new IntegerModel();
                break;
            case MONEY:
                theModel = new MoneyModel();
                break;
            case RATE:
                theModel = new RateModel();
                break;
            case UNITS:
                theModel = new UnitsModel();
                break;
            case PRICE:
                theModel = new PriceModel();
                break;
            case DILUTION:
                theModel = new DilutionModel();
                break;
            case CHARARRAY:
                theModel = new CharArrayModel();
                break;
            default:
                throw new IllegalArgumentException();
        }

        /* Add Action Listener */
        addActionListener(new TextAction());

        /* Add Focus Listener */
        addFocusListener(new TextFocus());
    }

    /**
     * Get the Value.
     * @return the value
     */
    public Object getValue() {
        return theModel.getValue();
    }

    /**
     * Set Special display string.
     * @param pValue the value
     */
    public void setDisplayValue(final MetisFieldValue pValue) {
        setDisplayString(pValue.toString());
    }

    /**
     * Set Display Value.
     * @param pValue the value to set
     */
    protected void setDisplayString(final String pValue) {
        /* Set the display value */
        theModel.setDisplay(pValue);
        setText(pValue);
    }

    /**
     * Set Value.
     * @param pValue the value to set
     */
    public void setValue(final Object pValue) {
        /* Reject invalid objects */
        if (pValue != null) {
            theModel.validateObject(pValue);
        }

        /* Determine whether this is a new value */
        boolean bNew = theModel.isNewValue(pValue);

        /* Access old value */
        Object myOld = getValue();

        /* Store the new value */
        if (bNew) {
            theModel.setValue(pValue);
        } else {
            theModel.establishStrings();
        }

        /* Determine value to display */
        if (theCache != null) {
            setForeground(theCache);
        }
        setText(theModel.getDisplay());

        /* Fire a Property change if required */
        if (bNew) {
            firePropertyChange(PROPERTY_VALUE, myOld, pValue);
        }
    }

    /**
     * finishEdit.
     */
    private void finishEdit() {
        Object myValue = null;

        /* Obtain the text value and trim it */
        String myEditText = getText().trim();

        /* Convert empty string to null */
        if (myEditText.length() == 0) {
            myEditText = null;
        }

        /* If we have a parse-able object */
        if (myEditText != null) {
            /* Parse the value into the correct object */
            myValue = theModel.parseValue(myEditText);

            /* If we have an invalid value */
            if (myValue == null) {
                /* If the object is invalid */
                setToolTipText("Invalid Value");
                setForeground(Color.red);

                /* Store as the edit value for the model */
                theModel.setEdit(myEditText);

                /* Re-acquire the focus */
                requestFocusInWindow();

                /* Return to caller */
                return;
            }
        }

        /* Store value */
        setToolTipText(null);
        setValue(myValue);

        /* Reset the cache */
        theCache = null;
    }

    /**
     * startEdit.
     */
    private void startEdit() {
        /* Save the current colour (if not already recorded) */
        if (theCache == null) {
            theCache = getForeground();
        }

        /* Show the edit text */
        setText(theModel.getEdit());
    }

    /**
     * Handle loss of focus.
     */
    private final class TextFocus
            extends FocusAdapter {
        @Override
        public void focusGained(final FocusEvent e) {
            startEdit();
        }

        @Override
        public void focusLost(final FocusEvent e) {
            finishEdit();
        }
    }

    /**
     * Handle actions.
     */
    private final class TextAction
            implements ActionListener {
        @Override
        public void actionPerformed(final ActionEvent e) {
            /* If this relates to the value */
            if (MetisValueField.this.equals(e.getSource())) {
                /* Check for finish of edit */
                finishEdit();

                /* Restart the edit, since we have not exited the field */
                startEdit();
            }
        }
    }

    /**
     * The Data Model class.
     */
    private abstract static class DataModel {
        /**
         * The value of the Data.
         */
        private Object theValue = null;

        /**
         * The display value for the text.
         */
        private String theDisplay = null;

        /**
         * The edit value for the text.
         */
        private String theEdit = null;

        /**
         * Get Value.
         * @return the value associated with the field
         */
        protected Object getValue() {
            return theValue;
        }

        /**
         * Get Display Value.
         * @return the string to be shown when the field is not being edited
         */
        protected String getDisplay() {
            return theDisplay;
        }

        /**
         * Get Edit Display String.
         * @return the string to be shown at the start of an edit session
         */
        protected String getEdit() {
            return theEdit;
        }

        /**
         * Set Edit display string.
         * @param pValue the value
         */
        protected void setEdit(final String pValue) {
            theEdit = pValue;
        }

        /**
         * Set Standard display string.
         * @param pValue the value
         */
        protected void setDisplay(final String pValue) {
            theDisplay = pValue;
        }

        /**
         * Set Value.
         * @param pValue the value
         */
        protected void setValue(final Object pValue) {
            theValue = pValue;
        }

        /**
         * Parse value.
         * @param pValue the non-Null text to parse
         * @return the parsed object (or Null if the string is invalid)
         */
        protected abstract Object parseValue(final String pValue);

        /**
         * Validate object type.
         * @param pObject the non-Null object to check
         */
        protected abstract void validateObject(final Object pObject);

        /**
         * Determine whether the new value is different from the existing value.
         * @param pObject the new value
         * @return true if the new value is different
         */
        protected abstract boolean isNewValue(final Object pObject);

        /**
         * Establish display and edit strings for the value.
         */
        protected abstract void establishStrings();
    }

    /**
     * The String Data Model class.
     */
    private static class StringModel
            extends DataModel {
        @Override
        protected String getValue() {
            return (String) super.getValue();
        }

        @Override
        protected void setValue(final Object pValue) {
            /* Store the new value */
            super.setValue(pValue);

            /* Establish strings */
            establishStrings();
        }

        @Override
        protected void establishStrings() {
            /* Set edit and display values */
            String s = getValue();
            if (s == null) {
                s = "";
            }
            setDisplay(s);
            setEdit(s);
        }

        @Override
        protected Object parseValue(final String pValue) {
            /* Return success */
            return pValue;
        }

        @Override
        protected void validateObject(final Object pValue) {
            /* Reject non-string */
            if (!(pValue instanceof String)) {
                throw new IllegalArgumentException();
            }
        }

        @Override
        protected boolean isNewValue(final Object pValue) {
            /* Determine whether the value has changed */
            return !MetisDifference.isEqual(getValue(), pValue);
        }
    }

    /**
     * The Integer Data Model class.
     */
    private static class IntegerModel
            extends DataModel {
        /**
         * Cached string value.
         */
        private String theString = "";

        @Override
        protected Integer getValue() {
            return (Integer) super.getValue();
        }

        @Override
        protected void setValue(final Object pValue) {
            /* Store the new value */
            super.setValue(pValue);

            /* Set edit and display values */
            theString = pValue == null
                                       ? ""
                                       : Integer.toString(getValue());
            establishStrings();
        }

        @Override
        protected void establishStrings() {
            /* Set edit and display values */
            setDisplay(theString);
            setEdit(theString);
        }

        @Override
        protected Object parseValue(final String pValue) {
            /* Protect against exceptions */
            try {
                return Integer.parseInt(pValue);
            } catch (NumberFormatException e) {
                return null;
            }
        }

        @Override
        protected void validateObject(final Object pValue) {
            /* Reject non-string */
            if (!(pValue instanceof Integer)) {
                throw new IllegalArgumentException();
            }
        }

        @Override
        protected boolean isNewValue(final Object pValue) {
            /* Determine whether the value has changed */
            return !MetisDifference.isEqual(getValue(), pValue);
        }
    }

    /**
     * The Money Data Model class.
     */
    private class MoneyModel
            extends DataModel {
        /**
         * Cached display string value.
         */
        private String theDisplayString = "";

        /**
         * Cached edit string value.
         */
        private String theEditString = "";

        @Override
        protected TethysMoney getValue() {
            return (TethysMoney) super.getValue();
        }

        @Override
        protected void setValue(final Object pValue) {
            TethysMoney myNew = (TethysMoney) pValue;

            /* Store the new value */
            super.setValue(pValue == null
                                          ? null
                                          : new TethysMoney(myNew));

            /* Set new edit and display values */
            theDisplayString = myNew == null
                                             ? ""
                                             : theFormatter.formatObject(myNew);
            theEditString = myNew == null
                                          ? ""
                                          : myNew.toString();
            establishStrings();
        }

        @Override
        protected void establishStrings() {
            /* Set edit and display values */
            setDisplay(theDisplayString);
            setEdit(theEditString);
        }

        @Override
        protected Object parseValue(final String pValue) {
            /* Parse the value */
            try {
                return new TethysMoney(pValue);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }

        @Override
        protected void validateObject(final Object pValue) {
            /* Reject non-money */
            if (!(pValue instanceof TethysMoney)) {
                throw new IllegalArgumentException();
            }
        }

        @Override
        protected boolean isNewValue(final Object pValue) {
            /* Determine whether the value has changed */
            return !MetisDifference.isEqual(getValue(), pValue);
        }
    }

    /**
     * The Rate Data Model class.
     */
    private class RateModel
            extends DataModel {
        /**
         * Cached display string value.
         */
        private String theDisplayString = "";

        /**
         * Cached edit string value.
         */
        private String theEditString = "";

        @Override
        protected TethysRate getValue() {
            return (TethysRate) super.getValue();
        }

        @Override
        protected void setValue(final Object pValue) {
            TethysRate myNew = (TethysRate) pValue;

            /* Store the new value */
            super.setValue(pValue == null
                                          ? null
                                          : new TethysRate(myNew));

            /* Set new edit and display values */
            theDisplayString = myNew == null
                                             ? ""
                                             : theFormatter.formatObject(myNew);
            theEditString = myNew == null
                                          ? ""
                                          : myNew.toString();
            establishStrings();
        }

        @Override
        protected void establishStrings() {
            /* Set edit and display values */
            setDisplay(theDisplayString);
            setEdit(theEditString);
        }

        @Override
        protected Object parseValue(final String pValue) {
            /* Parse the value */
            try {
                return new TethysRate(pValue);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }

        @Override
        protected void validateObject(final Object pValue) {
            /* Reject non-rate */
            if (!(pValue instanceof TethysRate)) {
                throw new IllegalArgumentException();
            }
        }

        @Override
        protected boolean isNewValue(final Object pValue) {
            /* Determine whether the value has changed */
            return !MetisDifference.isEqual(getValue(), pValue);
        }
    }

    /**
     * The Units Data Model class.
     */
    private class UnitsModel
            extends DataModel {
        /**
         * Cached display string value.
         */
        private String theDisplayString = "";

        /**
         * Cached edit string value.
         */
        private String theEditString = "";

        @Override
        protected TethysUnits getValue() {
            return (TethysUnits) super.getValue();
        }

        @Override
        protected void setValue(final Object pValue) {
            TethysUnits myNew = (TethysUnits) pValue;

            /* Store the new value */
            super.setValue(pValue == null
                                          ? null
                                          : new TethysUnits(myNew));

            /* Set new edit and display values */
            theDisplayString = myNew == null
                                             ? ""
                                             : theFormatter.formatObject(myNew);
            theEditString = myNew == null
                                          ? ""
                                          : myNew.toString();
            establishStrings();
        }

        @Override
        protected void establishStrings() {
            /* Set edit and display values */
            setDisplay(theDisplayString);
            setEdit(theEditString);
        }

        @Override
        protected Object parseValue(final String pValue) {
            /* Parse the value */
            try {
                return new TethysUnits(pValue);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }

        @Override
        protected void validateObject(final Object pValue) {
            /* Reject non-rate */
            if (!(pValue instanceof TethysUnits)) {
                throw new IllegalArgumentException();
            }
        }

        @Override
        protected boolean isNewValue(final Object pValue) {
            /* Determine whether the value has changed */
            return !MetisDifference.isEqual(getValue(), pValue);
        }
    }

    /**
     * The Price Data Model class.
     */
    private class PriceModel
            extends DataModel {
        /**
         * Cached display string value.
         */
        private String theDisplayString = "";

        /**
         * Cached edit string value.
         */
        private String theEditString = "";

        @Override
        protected TethysPrice getValue() {
            return (TethysPrice) super.getValue();
        }

        @Override
        protected void setValue(final Object pValue) {
            TethysPrice myNew = (TethysPrice) pValue;

            /* Store the new value */
            super.setValue(pValue == null
                                          ? null
                                          : new TethysPrice(myNew));

            /* Set new edit and display values */
            theDisplayString = myNew == null
                                             ? ""
                                             : theFormatter.formatObject(myNew);
            theEditString = myNew == null
                                          ? ""
                                          : myNew.toString();
            establishStrings();
        }

        @Override
        protected void establishStrings() {
            /* Set edit and display values */
            setDisplay(theDisplayString);
            setEdit(theEditString);
        }

        @Override
        protected Object parseValue(final String pValue) {
            /* Parse the value */
            try {
                return new TethysPrice(pValue);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }

        @Override
        protected void validateObject(final Object pValue) {
            /* Reject non-rate */
            if (!(pValue instanceof TethysPrice)) {
                throw new IllegalArgumentException();
            }
        }

        @Override
        protected boolean isNewValue(final Object pValue) {
            /* Determine whether the value has changed */
            return !MetisDifference.isEqual(getValue(), pValue);
        }
    }

    /**
     * The Dilution Data Model class.
     */
    private class DilutionModel
            extends DataModel {
        /**
         * Cached display string value.
         */
        private String theDisplayString = "";

        /**
         * Cached edit string value.
         */
        private String theEditString = "";

        @Override
        protected TethysDilution getValue() {
            return (TethysDilution) super.getValue();
        }

        @Override
        protected void setValue(final Object pValue) {
            TethysDilution myNew = (TethysDilution) pValue;

            /* Store the new value */
            super.setValue(pValue == null
                                          ? null
                                          : new TethysDilution(myNew));

            /* Set new edit and display values */
            theDisplayString = myNew == null
                                             ? ""
                                             : theFormatter.formatObject(myNew);
            theEditString = myNew == null
                                          ? ""
                                          : myNew.toString();
            establishStrings();
        }

        @Override
        protected void establishStrings() {
            /* Set edit and display values */
            setDisplay(theDisplayString);
            setEdit(theEditString);
        }

        @Override
        protected Object parseValue(final String pValue) {
            /* Parse the value */
            try {
                return new TethysDilution(pValue);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }

        @Override
        protected void validateObject(final Object pValue) {
            /* Reject non-rate */
            if (!(pValue instanceof TethysDilution)) {
                throw new IllegalArgumentException();
            }
        }

        @Override
        protected boolean isNewValue(final Object pValue) {
            /* Determine whether the value has changed */
            return !MetisDifference.isEqual(getValue(), pValue);
        }
    }

    /**
     * The Char Array Model class.
     */
    private static class CharArrayModel
            extends DataModel {
        /**
         * Cached display string value.
         */
        private String theDisplayString = "";

        /**
         * Cached edit string value.
         */
        private String theEditString = "";

        @Override
        protected char[] getValue() {
            return (char[]) super.getValue();
        }

        @Override
        protected void setValue(final Object pValue) {
            char[] myNew = (char[]) pValue;

            /* Store the new value */
            super.setValue((pValue == null)
                                            ? null
                                            : Arrays.copyOf(myNew, myNew.length));

            /* Set default edit/display values */
            theEditString = myNew == null
                                          ? ""
                                          : new String(myNew);
            theDisplayString = "";

            /* Set new display value */
            if (myNew != null) {
                char[] myMask = new char[myNew.length];
                Arrays.fill(myMask, '*');
                theDisplayString = new String(myMask);
            }

            /* Establish Strings */
            establishStrings();
        }

        @Override
        protected void establishStrings() {
            /* Set edit and display values */
            setDisplay(theDisplayString);
            setEdit(theEditString);
        }

        @Override
        protected Object parseValue(final String pValue) {
            /* Return the parsed value */
            return pValue.toCharArray();
        }

        @Override
        protected void validateObject(final Object pValue) {
            /* Reject non-rate */
            if (!(pValue instanceof char[])) {
                throw new IllegalArgumentException();
            }
        }

        @Override
        protected boolean isNewValue(final Object pValue) {
            /* Determine whether the value has changed */
            return !MetisDifference.isEqual(getValue(), pValue);
        }
    }
}