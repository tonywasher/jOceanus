/*******************************************************************************
 * JDataModel: Data models
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
package uk.co.tolcroft.models.ui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.Arrays;

import javax.swing.JTextField;

import net.sourceforge.JDataManager.Difference;
import net.sourceforge.JDecimal.Dilution;
import net.sourceforge.JDecimal.Money;
import net.sourceforge.JDecimal.Price;
import net.sourceforge.JDecimal.Rate;
import net.sourceforge.JDecimal.Units;
import uk.co.tolcroft.models.ui.Renderer.RendererFieldValue;

/**
 * ValueField provides a JTextField which is geared to a particular type of data.
 * @author Tony Washer
 */
public class ValueField extends JTextField {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = -1865387282196348594L;

    /**
     * Self reference.
     */
    private final ValueField theSelf = this;

    /**
     * The Data Model.
     */
    private transient DataModel theModel = null;

    /**
     * The Cached foreground colour.
     */
    private Color theCache = null;

    /**
     * Value property.
     */
    public static final String PROPERTY_VALUE = "value";

    /**
     * Get the Value.
     * @return the value
     */
    public Object getValue() {
        return theModel.getValue();
    }

    /**
     * Constructor.
     */
    public ValueField() {
        this(ValueClass.String);
    }

    /**
     * Constructor.
     * @param pClass the value class
     */
    public ValueField(final ValueClass pClass) {
        /* Switch on requested class */
        switch (pClass) {
        /* Create appropriate model class */
            case String:
                theModel = new StringModel();
                break;
            case Integer:
                theModel = new IntegerModel();
                break;
            case Money:
                theModel = new MoneyModel();
                break;
            case Rate:
                theModel = new RateModel();
                break;
            case Units:
                theModel = new UnitsModel();
                break;
            case Price:
                theModel = new PriceModel();
                break;
            case Dilution:
                theModel = new DilutionModel();
                break;
            case CharArray:
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
     * Set Standard display string.
     * @param pValue the value
     */
    public void setDisplay(final RendererFieldValue pValue) {
        setDisplay(pValue.toString());
    }

    /**
     * Set Display Value.
     * @param pValue the value to set
     */
    protected void setDisplay(final String pValue) {
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

        /* Clear old character array values */
        if (myOld instanceof char[]) {
            Arrays.fill((char[]) myOld, (char) 0);
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
        theSelf.setToolTipText(null);
        setValue(myValue);

        /* Reset the cache */
        theCache = null;
    }

    /**
     * startEdit.
     */
    private void startEdit() {
        /* Save the current colour */
        theCache = getForeground();

        /* Show the edit text */
        setText(theModel.getEdit());
    }

    /**
     * Handle loss of focus.
     */
    private final class TextFocus extends FocusAdapter {
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
    private final class TextAction implements ActionListener {
        @Override
        public void actionPerformed(final ActionEvent e) {
            /* If this relates to the value */
            if (theSelf.equals(e.getSource())) {
                /* Check for finish of edit */
                finishEdit();
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
            if (pValue.length() > 0) {
                theDisplay = pValue;
            }
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
    private static class StringModel extends DataModel {
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
            return (!Difference.isEqual(getValue(), pValue));
        }
    }

    /**
     * The Integer Data Model class.
     */
    private static class IntegerModel extends DataModel {
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
            theString = (pValue == null) ? "" : Integer.toString(getValue());
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
            return (!Difference.isEqual(getValue(), pValue));
        }
    }

    /**
     * The Money Data Model class.
     */
    private static class MoneyModel extends DataModel {
        /**
         * Cached display string value.
         */
        private String theDisplayString = "";

        /**
         * Cached edit string value.
         */
        private String theEditString = "";

        @Override
        protected Money getValue() {
            return (Money) super.getValue();
        }

        @Override
        protected void setValue(final Object pValue) {
            Money myNew = (Money) pValue;

            /* Store the new value */
            super.setValue((pValue == null) ? null : new Money(myNew));

            /* Set new edit and display values */
            theDisplayString = ((myNew == null) ? "" : myNew.format(true));
            theEditString = ((myNew == null) ? "" : myNew.format(false));
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
            return Money.parseString(pValue);
        }

        @Override
        protected void validateObject(final Object pValue) {
            /* Reject non-money */
            if (!(pValue instanceof Money)) {
                throw new IllegalArgumentException();
            }
        }

        @Override
        protected boolean isNewValue(final Object pValue) {
            /* Determine whether the value has changed */
            return (!Difference.isEqual(getValue(), pValue));
        }
    }

    /**
     * The Rate Data Model class.
     */
    private static class RateModel extends DataModel {
        /**
         * Cached display string value.
         */
        private String theDisplayString = "";

        /**
         * Cached edit string value.
         */
        private String theEditString = "";

        @Override
        protected Rate getValue() {
            return (Rate) super.getValue();
        }

        @Override
        protected void setValue(final Object pValue) {
            Rate myNew = (Rate) pValue;

            /* Store the new value */
            super.setValue((pValue == null) ? null : new Rate(myNew));

            /* Set new edit and display values */
            theDisplayString = ((myNew == null) ? "" : myNew.format(true));
            theEditString = ((myNew == null) ? "" : myNew.format(false));
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
            return Rate.parseString(pValue);
        }

        @Override
        protected void validateObject(final Object pValue) {
            /* Reject non-rate */
            if (!(pValue instanceof Rate)) {
                throw new IllegalArgumentException();
            }
        }

        @Override
        protected boolean isNewValue(final Object pValue) {
            /* Determine whether the value has changed */
            return (!Difference.isEqual(getValue(), pValue));
        }
    }

    /**
     * The Units Data Model class.
     */
    private static class UnitsModel extends DataModel {
        /**
         * Cached display string value.
         */
        private String theDisplayString = "";

        /**
         * Cached edit string value.
         */
        private String theEditString = "";

        @Override
        protected Units getValue() {
            return (Units) super.getValue();
        }

        @Override
        protected void setValue(final Object pValue) {
            Units myNew = (Units) pValue;

            /* Store the new value */
            super.setValue((pValue == null) ? null : new Units(myNew));

            /* Set new edit and display values */
            theDisplayString = ((myNew == null) ? "" : myNew.format(true));
            theEditString = ((myNew == null) ? "" : myNew.format(false));
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
            return Units.parseString(pValue);
        }

        @Override
        protected void validateObject(final Object pValue) {
            /* Reject non-rate */
            if (!(pValue instanceof Units)) {
                throw new IllegalArgumentException();
            }
        }

        @Override
        protected boolean isNewValue(final Object pValue) {
            /* Determine whether the value has changed */
            return (!Difference.isEqual(getValue(), pValue));
        }
    }

    /**
     * The Price Data Model class.
     */
    private static class PriceModel extends DataModel {
        /**
         * Cached display string value.
         */
        private String theDisplayString = "";

        /**
         * Cached edit string value.
         */
        private String theEditString = "";

        @Override
        protected Price getValue() {
            return (Price) super.getValue();
        }

        @Override
        protected void setValue(final Object pValue) {
            Price myNew = (Price) pValue;

            /* Store the new value */
            super.setValue((pValue == null) ? null : new Price(myNew));

            /* Set new edit and display values */
            theDisplayString = ((myNew == null) ? "" : myNew.format(true));
            theEditString = ((myNew == null) ? "" : myNew.format(false));
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
            return Price.parseString(pValue);
        }

        @Override
        protected void validateObject(final Object pValue) {
            /* Reject non-rate */
            if (!(pValue instanceof Price)) {
                throw new IllegalArgumentException();
            }
        }

        @Override
        protected boolean isNewValue(final Object pValue) {
            /* Determine whether the value has changed */
            return (!Difference.isEqual(getValue(), pValue));
        }
    }

    /**
     * The Dilution Data Model class.
     */
    private static class DilutionModel extends DataModel {
        /**
         * Cached display string value.
         */
        private String theDisplayString = "";

        /**
         * Cached edit string value.
         */
        private String theEditString = "";

        @Override
        protected Dilution getValue() {
            return (Dilution) super.getValue();
        }

        @Override
        protected void setValue(final Object pValue) {
            Dilution myNew = (Dilution) pValue;

            /* Store the new value */
            super.setValue((pValue == null) ? null : new Dilution(myNew));

            /* Set new edit and display values */
            theDisplayString = ((myNew == null) ? "" : myNew.format(true));
            theEditString = ((myNew == null) ? "" : myNew.format(false));
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
            return Dilution.parseString(pValue);
        }

        @Override
        protected void validateObject(final Object pValue) {
            /* Reject non-rate */
            if (!(pValue instanceof Dilution)) {
                throw new IllegalArgumentException();
            }
        }

        @Override
        protected boolean isNewValue(final Object pValue) {
            /* Determine whether the value has changed */
            return (!Difference.isEqual(getValue(), pValue));
        }
    }

    /**
     * The Char Array Model class.
     */
    private static class CharArrayModel extends DataModel {
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
            super.setValue((pValue == null) ? null : Arrays.copyOf(myNew, myNew.length));

            /* Set default edit/display values */
            theEditString = ((myNew == null) ? "" : new String(myNew));
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
            return (!Difference.isEqual(getValue(), pValue));
        }
    }

    /**
     * Classes of ValueField.
     */
    public enum ValueClass {
        /**
         * String.
         */
        String,

        /**
         * Integer.
         */
        Integer,

        /**
         * Money.
         */
        Money,

        /**
         * Rate.
         */
        Rate,

        /**
         * Units.
         */
        Units,

        /**
         * Price.
         */
        Price,

        /**
         * Dilution.
         */
        Dilution,

        /**
         * Char Array.
         */
        CharArray;
    }
}
