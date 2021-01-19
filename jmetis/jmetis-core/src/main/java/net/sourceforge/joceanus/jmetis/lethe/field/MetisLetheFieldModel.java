/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012,2021 Tony Washer
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
package net.sourceforge.joceanus.jmetis.lethe.field;

import java.util.Currency;
import java.util.List;

import net.sourceforge.joceanus.jmetis.data.MetisDataDifference;
import net.sourceforge.joceanus.jmetis.data.MetisDataFieldValue;
import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.data.MetisDataType;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisEncryptedData.MetisEncryptedField;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFieldSetItem;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisLetheField;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimalFormatter;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimalParser;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilution;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;
import net.sourceforge.joceanus.jtethys.event.TethysEvent;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;

/**
 * JFieldSet data model abstraction.
 * @param <T> the Data Item type
 */
public abstract class MetisLetheFieldModel<T extends MetisFieldSetItem> {
    /**
     * Bad Data type error text.
     */
    private static final String ERROR_TYPE = "Invalid DataType: ";

    /**
     * The FieldSet.
     */
    private final MetisLetheFieldSetBase theFieldSet;

    /**
     * The DataType value.
     */
    private final MetisDataType theClass;

    /**
     * The Field value.
     */
    private final MetisLetheField theField;

    /**
     * The Item field value.
     */
    private Object theValue;

    /**
     * Constructor.
     * @param pFieldSet the fieldSet
     * @param pField the field for the model
     * @param pClass the class of the model
     */
    protected MetisLetheFieldModel(final MetisLetheFieldSetBase pFieldSet,
                                   final MetisLetheField pField,
                                   final MetisDataType pClass) {
        /* Store values */
        theFieldSet = pFieldSet;
        theClass = pClass;
        theField = pField;
    }

    /**
     * Obtain Field.
     * @return the field
     */
    protected MetisLetheField getField() {
        return theField;
    }

    /**
     * Obtain Value.
     * @return the value
     */
    public Object getValue() {
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
    protected MetisDataType getDataClass() {
        return theClass;
    }

    /**
     * is the value null?
     * @return true/false
     */
    protected boolean isNull() {
        return theValue == null;
    }

    /**
     * is the value fixed width?
     * @return true/false
     */
    public boolean isFixedWidth() {
        return false;
    }

    /**
     * Load value.
     * @param pItem the item to load the value from
     */
    public void loadValue(final T pItem) {
        /* Access the value */
        Object myValue = pItem.getFieldValue(theField);

        /* If we are skipping the field */
        if (MetisDataFieldValue.SKIP.equals(myValue)) {
            /* Set to null */
            myValue = null;
        }

        /* Handle encrypted values */
        if (myValue instanceof MetisEncryptedField) {
            /* Access unEncrypted value */
            final MetisEncryptedField<?> myField = (MetisEncryptedField<?>) myValue;
            myValue = myField.getValue();
        }

        /* Record new value */
        theValue = myValue;
    }

    /**
     * Load value.
     */
    public void loadNullValue() {
        /* Record new value */
        theValue = null;
    }

    /**
     * String model.
     * @param <T> itemType
     */
    public static class TethysFieldModelString<T extends MetisFieldSetItem>
            extends MetisLetheFieldModel<T> {
        /**
         * The Decimal Parser.
         */
        private final TethysDecimalParser theParser;

        /**
         * The Decimal Formatter.
         */
        private final TethysDecimalFormatter theFormatter;

        /**
         * Are we in error mode?
         */
        private boolean isError;

        /**
         * The Display text.
         */
        private String theDisplay;

        /**
         * The Edit Text.
         */
        private String theEdit;

        /**
         * The Error Text.
         */
        private String theError;

        /**
         * The Assumed Currency.
         */
        private Currency theCurrency;

        /**
         * Constructor.
         * @param pFieldSet the fieldSet
         * @param pField the field for the model
         * @param pClass the class of the model
         */
        public TethysFieldModelString(final MetisLetheFieldSetBase pFieldSet,
                                      final MetisLetheField pField,
                                      final MetisDataType pClass) {
            /* Pass call onwards */
            super(pFieldSet, pField, pClass);

            /* Store formatter value */
            final MetisDataFormatter myDataFormatter = pFieldSet.getDataFormatter();
            theParser = myDataFormatter.getDecimalParser();
            theFormatter = myDataFormatter.getDecimalFormatter();
            theCurrency = theParser.getDefaultCurrency();

            /* Switch on class */
            switch (pClass) {
                /* Supported classes */
                case STRING:
                case MONEY:
                case RATE:
                case PRICE:
                case UNITS:
                case DILUTION:
                case RATIO:
                case SHORT:
                case INTEGER:
                case LONG:
                case CHARARRAY:
                    break;

                /* Unsupported classes */
                default:
                    throw new IllegalArgumentException(ERROR_TYPE
                                                       + pClass);
            }
        }

        /**
         * Set the assumed currency.
         * @param pCurrency the assumed currency
         */
        public void setAssumedCurrency(final Currency pCurrency) {
            theCurrency = pCurrency;
        }

        /**
         * is the value in error?
         * @return true/false
         */
        public boolean isError() {
            return isError;
        }

        /**
         * Get Edit string.
         * @return the edit string
         */
        public String getEditString() {
            return isError
                           ? theError
                           : theEdit;
        }

        /**
         * Get Display string.
         * @return the display string
         */
        public String getDisplayString() {
            return theDisplay;
        }

        @Override
        public boolean isFixedWidth() {
            /* Switch on class */
            switch (getDataClass()) {
                /* Number classes */
                case MONEY:
                case RATE:
                case PRICE:
                case UNITS:
                case DILUTION:
                case RATIO:
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
        public void loadValue(final T pItem) {
            /* Process the value */
            super.loadValue(pItem);

            /* Obtain the value */
            final Object myValue = getValue();

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
                theDisplay = theEdit;

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
            } else if (myValue instanceof TethysMoney) {
                /* Set edit/display values */
                final TethysMoney myMoney = (TethysMoney) myValue;
                theEdit = myMoney.toString();
                theDisplay = theFormatter.formatMoney(myMoney);
                /* If this is a JRate */
            } else if (myValue instanceof TethysRate) {
                /* Set edit/display values */
                final TethysRate myRate = (TethysRate) myValue;
                theEdit = myRate.toString();
                theDisplay = theFormatter.formatRate(myRate);
                /* If this is a JPrice */
            } else if (myValue instanceof TethysPrice) {
                /* Set edit/display values */
                final TethysPrice myPrice = (TethysPrice) myValue;
                theEdit = myPrice.toString();
                theDisplay = theFormatter.formatPrice(myPrice);
                /* If this is a JUnits */
            } else if (myValue instanceof TethysUnits) {
                /* Set edit/display values */
                final TethysUnits myUnits = (TethysUnits) myValue;
                theEdit = myUnits.toString();
                theDisplay = theFormatter.formatUnits(myUnits);
                /* If this is a JDilution */
            } else if (myValue instanceof TethysDilution) {
                /* Set edit/display values */
                final TethysDilution myDilution = (TethysDilution) myValue;
                theEdit = myDilution.toString();
                theDisplay = theFormatter.formatDilution(myDilution);
                /* If this is a JDilution */
            } else if (myValue instanceof TethysRatio) {
                /* Set edit/display values */
                final TethysRatio myRatio = (TethysRatio) myValue;
                theEdit = myRatio.toString();
                theDisplay = theFormatter.formatRatio(myRatio);
            }
        }

        /**
         * Process value.
         * @param pValue the value
         * @return has the value changed?
         */
        public boolean processValue(final String pValue) {
            /* Obtain the text value and trim it */
            final String myText = pValue.trim();
            Object myValue = null;

            /* If there is no change in the valid value */
            if ((!isError)
                && (MetisDataDifference.isEqual(theEdit, myText))) {
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
                            myValue = theParser.parseMoneyValue(pValue, theCurrency);
                            break;
                        case RATE:
                            myValue = theParser.parseRateValue(pValue);
                            break;
                        case PRICE:
                            myValue = theParser.parsePriceValue(pValue, theCurrency);
                            break;
                        case UNITS:
                            myValue = theParser.parseUnitsValue(pValue);
                            break;
                        case DILUTION:
                            myValue = theParser.parseDilutionValue(pValue);
                            break;
                        case RATIO:
                            myValue = theParser.parseRatioValue(pValue);
                            break;

                        /* Other types are invalid */
                        default:
                            isError = true;
                            break;
                    }
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
            } else if (!MetisDataDifference.isEqual(myValue, getValue())) {
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
     * @param <I> element type
     * @param <T> itemType
     */
    public static class TethysFieldModelObject<I, T extends MetisFieldSetItem>
            extends MetisLetheFieldModel<T> {
        /**
         * Class of object.
         */
        private final Class<I> theClazz;

        /**
         * Constructor.
         * @param pFieldSet the fieldSet
         * @param pField the field for the model
         * @param pClazz the class of the model
         */
        public TethysFieldModelObject(final MetisLetheFieldSetBase pFieldSet,
                                      final MetisLetheField pField,
                                      final Class<I> pClazz) {
            /* Pass call onwards */
            super(pFieldSet, pField, null);

            /* Store the class */
            theClazz = pClazz;
        }

        /**
         * Process Object value.
         * @param pValue the value
         */
        public void processValue(final Object pValue) {
            /* If this is a new value */
            if (!MetisDataDifference.isEqual(pValue, getValue())) {
                /* Record new value */
                setValue(pValue);
            }
        }

        @Override
        public I getValue() {
            return theClazz.cast(super.getValue());
        }
    }

    /**
     * Object List model.
     * @param <I> element type
     * @param <T> itemType
     */
    public static class TethysFieldModelObjectList<I, T extends MetisFieldSetItem>
            extends MetisLetheFieldModel<T> {
        /**
         * Constructor.
         * @param pFieldSet the fieldSet
         * @param pField the field for the model
         */
        public TethysFieldModelObjectList(final MetisLetheFieldSetBase pFieldSet,
                                          final MetisLetheField pField) {
            /* Pass call onwards */
            super(pFieldSet, pField, null);
        }

        /**
         * Process Object value.
         * @param pValue the value
         */
        public void processValue(final TethysEvent<TethysUIEvent> pValue) {
            /* Record new value */
            setValue(pValue.getDetails(List.class));
        }

        @SuppressWarnings("unchecked")
        @Override
        public List<I> getValue() {
            return (List<I>) super.getValue();
        }
    }

    /**
     * Date model.
     * @param <T> itemType
     */
    public static class TethysFieldModelDate<T extends MetisFieldSetItem>
            extends MetisLetheFieldModel<T> {
        /**
         * Constructor.
         * @param pFieldSet the fieldSet
         * @param pField the field for the model
         */
        public TethysFieldModelDate(final MetisLetheFieldSetBase pFieldSet,
                                    final MetisLetheField pField) {
            /* Pass call onwards */
            super(pFieldSet, pField, MetisDataType.DATE);
        }

        /**
         * Process DateDay value.
         * @param pValue the value
         */
        public void processValue(final TethysDate pValue) {
            /* If this is a new value */
            if (!MetisDataDifference.isEqual(pValue, getValue())) {
                /* Record new value */
                setValue(pValue);
            }
        }

        @Override
        public TethysDate getValue() {
            return (TethysDate) super.getValue();
        }
    }
}
