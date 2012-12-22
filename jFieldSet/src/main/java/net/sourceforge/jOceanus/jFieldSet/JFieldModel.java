/*******************************************************************************
 * jFieldSet: Java Swing Field Set
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
package net.sourceforge.jOceanus.jFieldSet;

import net.sourceforge.jOceanus.jDataManager.DataType;
import net.sourceforge.jOceanus.jDataManager.Difference;
import net.sourceforge.jOceanus.jDataManager.JDataFields.JDataField;
import net.sourceforge.jOceanus.jDataManager.JDataFormatter;
import net.sourceforge.jOceanus.jDateDay.JDateDay;
import net.sourceforge.jOceanus.jDecimal.JDecimalFormatter;
import net.sourceforge.jOceanus.jDecimal.JDecimalParser;
import net.sourceforge.jOceanus.jDecimal.JDilution;
import net.sourceforge.jOceanus.jDecimal.JMoney;
import net.sourceforge.jOceanus.jDecimal.JPrice;
import net.sourceforge.jOceanus.jDecimal.JRate;
import net.sourceforge.jOceanus.jDecimal.JUnits;
import net.sourceforge.jOceanus.jGordianKnot.EncryptedData.EncryptedField;

/**
 * JFieldSet data model abstraction.
 * @param <T> the Data Item type
 */
public abstract class JFieldModel<T extends JFieldSetItem> {
    /**
     * The standard mask.
     */
    private static final String STRING_MASK = "*****";

    /**
     * The FieldSet.
     */
    private final JFieldSet<T> theFieldSet;

    /**
     * The DataType value.
     */
    private final DataType theClass;

    /**
     * The Field value.
     */
    private final JDataField theField;

    /**
     * The Item field value.
     */
    private Object theValue = null;

    /**
     * Obtain Field.
     * @return the field
     */
    protected JDataField getField() {
        return theField;
    }

    /**
     * Obtain Value.
     * @return the value
     */
    protected Object getValue() {
        return theValue;
    }

    /**
     * Set Value.
     * @param pValue the value
     */
    protected void setValue(final Object pValue) {
        /* Record new value */
        theValue = pValue;

        /* Send notification */
        theFieldSet.notifyUpdate(theField, theValue);
    }

    /**
     * Obtain Class.
     * @return the class
     */
    protected DataType getDataClass() {
        return theClass;
    }

    /**
     * is the value null?
     * @return true/false
     */
    protected boolean isNull() {
        return (theValue == null);
    }

    /**
     * is the value fixed width?
     * @return true/false
     */
    protected boolean isFixedWidth() {
        return false;
    }

    /**
     * Constructor.
     * @param pFieldSet the fieldSet
     * @param pField the field for the model
     * @param pClass the class of the model
     */
    private JFieldModel(final JFieldSet<T> pFieldSet,
                        final JDataField pField,
                        final DataType pClass) {
        /* Store values */
        theFieldSet = pFieldSet;
        theClass = pClass;
        theField = pField;
    }

    /**
     * Load value.
     * @param pItem the item to load the value from
     */
    protected void loadValue(final T pItem) {
        /* Access the value */
        Object myValue = pItem.getFieldValue(theField);

        /* Handle encrypted values */
        if (myValue instanceof EncryptedField) {
            /* Access unEncrypted value */
            EncryptedField<?> myField = (EncryptedField<?>) myValue;
            myValue = myField.getValue();
        }

        /* Record new value */
        theValue = myValue;
    }

    /**
     * Load value.
     */
    protected void loadNullValue() {
        /* Record new value */
        theValue = null;
    }

    /**
     * String model.
     */
    protected static class JModelString<T extends JFieldSetItem>
            extends JFieldModel<T> {
        /**
         * The Decimal Parser.
         */
        private final transient JDecimalParser theParser;

        /**
         * The Decimal Formatter.
         */
        private final transient JDecimalFormatter theFormatter;

        /**
         * Are we in error mode?
         */
        private boolean isError = false;

        /**
         * The Display text.
         */
        private String theDisplay = null;

        /**
         * The Edit Text.
         */
        private String theEdit = null;

        /**
         * The Error Text.
         */
        private String theError = null;

        /**
         * Constructor.
         * @param pFieldSet the fieldSet
         * @param pField the field for the model
         * @param pClass the class of the model
         */
        protected JModelString(final JFieldSet<T> pFieldSet,
                               final JDataField pField,
                               final DataType pClass) {
            /* Pass call onwards */
            super(pFieldSet, pField, pClass);

            /* Store formatter value */
            JDataFormatter myDataFormatter = pFieldSet.getDataFormatter();
            theParser = myDataFormatter.getDecimalParser();
            theFormatter = myDataFormatter.getDecimalFormatter();

            /* Switch on class */
            switch (pClass) {
            /* Supported classes */
                case STRING:
                case MONEY:
                case RATE:
                case PRICE:
                case UNITS:
                case DILUTION:
                case SHORT:
                case INTEGER:
                case LONG:
                case CHARARRAY:
                    break;

                /* Unsupported classes */
                default:
                    throw new IllegalArgumentException("Invalid DataType "
                                                       + pClass);
            }
        }

        /**
         * is the value in error?
         * @return true/false
         */
        protected boolean isError() {
            return isError;
        }

        /**
         * Get Edit string.
         * @return the edit string
         */
        protected String getEditString() {
            return (isError) ? theError : theEdit;
        }

        /**
         * Get Display string.
         * @return the display string
         */
        protected String getDisplayString() {
            return theDisplay;
        }

        @Override
        protected boolean isFixedWidth() {
            /* Switch on class */
            switch (getDataClass()) {
            /* Number classes */
                case MONEY:
                case RATE:
                case PRICE:
                case UNITS:
                case DILUTION:
                case SHORT:
                case INTEGER:
                case LONG:
                    return true;

                    /* Other classes */
                default:
                    return false;
            }
        }

        @Override
        protected void loadValue(final T pItem) {
            /* Process the value */
            super.loadValue(pItem);

            /* Obtain the value */
            Object myValue = getValue();

            /* Initialise strings */
            theEdit = "";
            theDisplay = "";

            /* If this is a string */
            if (myValue instanceof String) {
                /* Set edit/display values */
                theEdit = (String) myValue;
                theDisplay = theEdit;

                /* If this is a character array */
            } else if (myValue instanceof char[]) {
                /* Set edit/display values */
                theEdit = new String((char[]) myValue);
                theDisplay = STRING_MASK;

                /* If this is a Short */
            } else if (myValue instanceof Short) {
                /* Set edit/display values */
                theEdit = Short.toString((Short) myValue);
                theDisplay = theEdit;
                /* If this is an Integer */
            } else if (myValue instanceof Integer) {
                /* Set edit/display values */
                theEdit = Integer.toString((Integer) myValue);
                theDisplay = theEdit;
                /* If this is an Integer */
            } else if (myValue instanceof Long) {
                /* Set edit/display values */
                theEdit = Long.toString((Long) myValue);
                theDisplay = theEdit;

                /* If this is a JMoney */
            } else if (myValue instanceof JMoney) {
                /* Set edit/display values */
                JMoney myMoney = (JMoney) myValue;
                theEdit = myMoney.toString();
                theDisplay = theFormatter.formatMoney(myMoney);
                /* If this is a JRate */
            } else if (myValue instanceof JRate) {
                /* Set edit/display values */
                JRate myRate = (JRate) myValue;
                theEdit = myRate.toString();
                theDisplay = theFormatter.formatRate(myRate);
                /* If this is a JPrice */
            } else if (myValue instanceof JPrice) {
                /* Set edit/display values */
                JPrice myPrice = (JPrice) myValue;
                theEdit = myPrice.toString();
                theDisplay = theFormatter.formatPrice(myPrice);
                /* If this is a JUnits */
            } else if (myValue instanceof JUnits) {
                /* Set edit/display values */
                JUnits myUnits = (JUnits) myValue;
                theEdit = myUnits.toString();
                theDisplay = theFormatter.formatUnits(myUnits);
                /* If this is a JDilution */
            } else if (myValue instanceof JDilution) {
                /* Set edit/display values */
                JDilution myDilution = (JDilution) myValue;
                theEdit = myDilution.toString();
                theDisplay = theFormatter.formatDilution(myDilution);
            }
        }

        /**
         * Process value.
         * @param pValue the value
         * @return has the value changed?
         */
        protected boolean processValue(final String pValue) {
            /* Obtain the text value and trim it */
            String myText = pValue.trim();
            Object myValue = null;

            /* If there is no change in the valid value */
            if ((!isError)
                && (Difference.isEqual(theEdit, myText))) {
                return false;
            }

            /* Clear error indication */
            isError = false;
            theError = null;

            /* If we have a non-empty string */
            if (myText.length() != 0) {
                /* Catch format exceptions */
                try {
                    /* Switch on data type */
                    switch (getDataClass()) {
                    /* Handle strings */
                        case STRING:
                            myValue = pValue;
                            break;

                        /* Handle CharArray */
                        case CHARARRAY:
                            myValue = pValue.toCharArray();
                            break;

                        /* Handle Integer variants */
                        case SHORT:
                            myValue = Short.parseShort(pValue);
                            break;
                        case INTEGER:
                            myValue = Integer.parseInt(pValue);
                            break;
                        case LONG:
                            myValue = Long.parseLong(pValue);
                            break;

                        /* Handle Decimal variants */
                        case MONEY:
                            myValue = theParser.parseMoneyValue(pValue);
                            break;
                        case RATE:
                            myValue = theParser.parseRateValue(pValue);
                            break;
                        case PRICE:
                            myValue = theParser.parsePriceValue(pValue);
                            break;
                        case UNITS:
                            myValue = theParser.parseUnitsValue(pValue);
                            break;
                        case DILUTION:
                            myValue = theParser.parseDilutionValue(pValue);
                            break;

                        /* Other types are invalid */
                        default:
                            isError = true;
                            break;
                    }
                } catch (NumberFormatException e) {
                    /* Set error indication */
                    isError = true;

                } catch (IllegalArgumentException e) {
                    /* Set error indication */
                    isError = true;
                }
            }

            /* If the value is invalid */
            if (isError) {
                /* Store input as error text */
                theError = myText;

                /* If this is a new value */
            } else if (!Difference.isEqual(myValue, getValue())) {
                /* Record new value */
                setValue(myValue);
                return true;
            }

            /* return no change */
            return false;
        }
    }

    /**
     * Object model.
     * @param <I> ComboBox element type
     */
    protected static class JModelObject<I, T extends JFieldSetItem>
            extends JFieldModel<T> {
        /**
         * Class of object.
         */
        private final Class<I> theClass;

        /**
         * Constructor.
         * @param pFieldSet the fieldSet
         * @param pField the field for the model
         * @param pClass the class of the model
         */
        protected JModelObject(final JFieldSet<T> pFieldSet,
                               final JDataField pField,
                               final Class<I> pClass) {
            /* Pass call onwards */
            super(pFieldSet, pField, null);

            /* Store the class */
            theClass = pClass;
        }

        /**
         * Process Object value.
         * @param pValue the value
         */
        protected void processValue(final Object pValue) {
            /* If this is a new value */
            if (!Difference.isEqual(pValue, getValue())) {
                /* Record new value */
                setValue(pValue);
            }
        }

        /**
         * Obtain Value.
         * @return the value
         */
        protected I getValue() {
            return theClass.cast(super.getValue());
        }
    }

    /**
     * Boolean model.
     */
    protected static class JModelBoolean<T extends JFieldSetItem>
            extends JFieldModel<T> {

        /**
         * Constructor.
         * @param pFieldSet the fieldSet
         * @param pField the field for the model
         * @param pClass the class of the model
         */
        protected JModelBoolean(final JFieldSet<T> pFieldSet,
                                final JDataField pField,
                                final DataType pClass) {
            /* Pass call onwards */
            super(pFieldSet, pField, pClass);

            /* Switch on class */
            switch (pClass) {
            /* Supported classes */
                case BOOLEAN:
                    break;

                /* Unsupported classes */
                default:
                    throw new IllegalArgumentException("Invalid DataType "
                                                       + pClass);
            }
        }

        /**
         * Process value.
         * @param pValue the value
         */
        protected void processValue(final Boolean pValue) {
            /* If this is a new value */
            if (!Difference.isEqual(pValue, getValue())) {
                /* Record new value */
                setValue(pValue);
            }
        }

        /**
         * Obtain Value.
         * @return the value
         */
        protected Boolean getValue() {
            return (Boolean) super.getValue();
        }
    }

    /**
     * DateDay model.
     */
    protected static class JModelDateDay<T extends JFieldSetItem>
            extends JFieldModel<T> {

        /**
         * Constructor.
         * @param pFieldSet the fieldSet
         * @param pField the field for the model
         * @param pClass the class of the model
         */
        protected JModelDateDay(final JFieldSet<T> pFieldSet,
                                final JDataField pField,
                                final DataType pClass) {
            /* Pass call onwards */
            super(pFieldSet, pField, pClass);

            /* Switch on class */
            switch (pClass) {
            /* Supported classes */
                case DATEDAY:
                    break;

                /* Unsupported classes */
                default:
                    throw new IllegalArgumentException("Invalid DataType "
                                                       + pClass);
            }
        }

        /**
         * Process DateDay value.
         * @param pValue the value
         */
        protected void processValue(final JDateDay pValue) {
            /* If this is a new value */
            if (!Difference.isEqual(pValue, getValue())) {
                /* Record new value */
                setValue(pValue);
            }
        }

        /**
         * Obtain Value.
         * @return the value
         */
        protected JDateDay getValue() {
            return (JDateDay) super.getValue();
        }
    }
}
