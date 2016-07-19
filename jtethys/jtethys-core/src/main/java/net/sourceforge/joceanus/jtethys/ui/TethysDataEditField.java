/*******************************************************************************
 * jTethys: Java Utilities
 * Copyright 2012,2016 Tony Washer
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
package net.sourceforge.joceanus.jtethys.ui;

import java.util.Arrays;
import java.util.Currency;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

import net.sourceforge.joceanus.jtethys.decimal.TethysDecimalFormatter;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimalParser;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilutedPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilution;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager.TethysSimpleIconButtonManager;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager.TethysStateIconButtonManager;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenuItem;

/**
 * Generic class for displaying and editing a data field.
 * @param <T> the data type
 * @param <N> the Node type
 * @param <I> the Icon type
 */
public abstract class TethysDataEditField<T, N, I>
        implements TethysEventProvider<TethysUIEvent>, TethysNode<N> {
    /**
     * DataEditConverter interface.
     * @param <T> the data type
     */
    public interface TethysDataEditConverter<T> {
        /**
         * Should we right-align the fields.
         * @return true/false
         */
        boolean rightAlignFields();

        /**
         * Format the display value.
         * @param pValue the value
         * @return the display string.
         */
        String formatDisplayValue(final T pValue);

        /**
         * Format the edit value.
         * @param pValue the value
         * @return the edit string.
         */
        String formatEditValue(final T pValue);

        /**
         * Parse the edited value.
         * @param pValue the value
         * @return the parsed value.
         * @throws IllegalArgumentException on parsing error
         */
        T parseEditedValue(final String pValue);
    }

    /**
     * Invalid value text.
     */
    protected static final String TOOLTIP_BAD_VALUE = "Invalid Value";

    /**
     * The Event Manager.
     */
    private final TethysEventManager<TethysUIEvent> theEventManager;

    /**
     * The Attributes.
     */
    private final Map<TethysFieldAttribute, TethysFieldAttribute> theAttributes;

    /**
     * The id.
     */
    private Integer theId;

    /**
     * Is the field editable?
     */
    private boolean isEditable;

    /**
     * The value.
     */
    private T theValue;

    /**
     * The CommandMenu.
     */
    private TethysScrollMenu<String, I> theCmdMenu;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     */
    protected TethysDataEditField(final TethysGuiFactory<N, I> pFactory) {
        /* Create event manager */
        theId = pFactory.getNextId();
        theEventManager = new TethysEventManager<>();
        theAttributes = new EnumMap<>(TethysFieldAttribute.class);
    }

    @Override
    public Integer getId() {
        return theId;
    }

    /**
     * Set Editable state.
     * @param pEditable true/false.
     */
    public void setEditable(final boolean pEditable) {
        isEditable = pEditable;
    }

    /**
     * Is the field editable?
     * @return true/false.
     */
    public boolean isEditable() {
        return isEditable;
    }

    /**
     * Set the value.
     * @param pValue the value
     */
    public void setValue(final T pValue) {
        setTheValue(pValue);
    }

    /**
     * Set the value.
     * @param pValue the value
     */
    protected void setTheValue(final T pValue) {
        theValue = pValue;
    }

    /**
     * Set the attribute state.
     * @param pAttr the attribute
     * @param pState the state
     */
    public void setTheAttributeState(final TethysFieldAttribute pAttr,
                                     final boolean pState) {
        if (pState) {
            setTheAttribute(pAttr);
        } else {
            clearTheAttribute(pAttr);
        }
    }

    /**
     * Set the attribute.
     * @param pAttr the attribute
     */
    public void setTheAttribute(final TethysFieldAttribute pAttr) {
        theAttributes.put(pAttr, pAttr);
    }

    /**
     * Clear the attribute.
     * @param pAttr the attribute
     */
    public void clearTheAttribute(final TethysFieldAttribute pAttr) {
        theAttributes.remove(pAttr);
    }

    /**
     * Is the attribute set?
     * @param pAttr the attribute
     * @return true/false
     */
    public boolean isAttributeSet(final TethysFieldAttribute pAttr) {
        return theAttributes.containsKey(pAttr);
    }

    /**
     * Adjust data field.
     */
    public abstract void adjustField();

    /**
     * Obtain the value.
     * @return the value.
     */
    public T getValue() {
        return theValue;
    }

    /**
     * Obtain the command menu.
     * @return the command menu.
     */
    public TethysScrollMenu<String, I> getCmdMenu() {
        return theCmdMenu;
    }

    @Override
    public TethysEventRegistrar<TethysUIEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * Fire event.
     * @param pEventId the eventId
     */
    protected void fireEvent(final TethysUIEvent pEventId) {
        theEventManager.fireEvent(pEventId);
    }

    /**
     * Fire event.
     * @param pEventId the eventId
     * @param pValue the relevant value
     */
    protected void fireEvent(final TethysUIEvent pEventId, final Object pValue) {
        theEventManager.fireEvent(pEventId, pValue);
    }

    /**
     * handleCmdMenuRequest.
     */
    public void handleCmdMenuRequest() {
        /* fire menuBuild actionEvent */
        fireEvent(TethysUIEvent.PREPARECMDDIALOG, theCmdMenu);

        /* If a menu is provided */
        if (!theCmdMenu.isEmpty()) {
            /* Show the menu */
            showCmdMenu();
        }
    }

    /**
     * handleCmdMenuClosed.
     */
    protected void handleCmdMenuClosed() {
        /* If we selected a value */
        TethysScrollMenuItem<String> mySelected = theCmdMenu.getSelectedItem();
        if (mySelected != null) {
            /* fire new command Event */
            theEventManager.fireEvent(TethysUIEvent.NEWCOMMAND, mySelected.getValue());
        }
    }

    /**
     * Show the command button.
     * @param pShow true/false
     */
    public abstract void showCmdButton(final boolean pShow);

    /**
     * Show the command menu.
     */
    protected abstract void showCmdMenu();

    /**
     * Declare command menu.
     * @param pMenu the menu
     */
    protected void declareCmdMenu(final TethysScrollMenu<String, I> pMenu) {
        /* Store the menu */
        theCmdMenu = pMenu;
    }

    /**
     * Set the Preferred Width.
     * @param pWidth the width
     */
    public abstract void setPreferredWidth(final Integer pWidth);

    /**
     * Set the Preferred Height.
     * @param pHeight the height
     */
    public abstract void setPreferredHeight(final Integer pHeight);

    /**
     * DataEditTextField base class.
     * @param <T> the data type
     */
    protected static class TethysDataEditTextFieldControl<T> {
        /**
         * The Field.
         */
        private final TethysDataEditField<T, ?, ?> theField;

        /**
         * The DataConverter.
         */
        private final TethysDataEditConverter<T> theConverter;

        /**
         * The vase value.
         */
        private T theValue;

        /**
         * The display text.
         */
        private String theDisplay;

        /**
         * The edit text.
         */
        private String theEdit;

        /**
         * Did we create a new value?
         */
        private boolean parsedNewValue;

        /**
         * Constructor.
         * @param pField the owing field
         * @param pConverter the data converter
         */
        public TethysDataEditTextFieldControl(final TethysDataEditField<T, ?, ?> pField,
                                              final TethysDataEditConverter<T> pConverter) {
            theField = pField;
            theConverter = pConverter;
        }

        /**
         * Obtain the display text.
         * @return the display text.
         */
        public String getDisplayText() {
            return theDisplay;
        }

        /**
         * Obtain the edit text.
         * @return the edit text.
         */
        public String getEditText() {
            if (theEdit == null) {
                theEdit = theConverter.formatEditValue(theValue);
            }
            return theEdit;
        }

        /**
         * Obtain the converter.
         * @return the converter.
         */
        public TethysDataEditConverter<T> getConverter() {
            return theConverter;
        }

        /**
         * Did we create a new value?
         * @return true/false
         */
        public boolean parsedNewValue() {
            return parsedNewValue;
        }

        /**
         * Clear the new value indication.
         */
        public void clearNewValue() {
            parsedNewValue = false;
        }

        /**
         * Process value.
         * @param pNewValue the new value
         * @return is value valid?
         */
        public boolean processValue(final String pNewValue) {
            /* Clear flag */
            parsedNewValue = false;

            /* NullOp if there are no changes */
            if (!Objects.equals(pNewValue, theEdit)) {
                /* Protect to catch parsing errors */
                try {
                    /* Parse the value */
                    T myValue = pNewValue == null
                                                  ? null
                                                  : theConverter.parseEditedValue(pNewValue);

                    /* set the value and fire Event */
                    setValue(myValue);
                    theField.fireEvent(TethysUIEvent.NEWVALUE, myValue);
                    parsedNewValue = true;

                    /* Catch parsing error */
                } catch (IllegalArgumentException e) {
                    return false;
                }
            }

            /* Return success */
            return true;
        }

        /**
         * Set the value.
         * @param pValue the value
         */
        public void setValue(final T pValue) {
            /* Obtain display text */
            theDisplay = pValue == null
                                        ? null
                                        : theConverter.formatDisplayValue(pValue);

            /* Initialise edit text */
            theValue = pValue;
            theEdit = null;

            /* Store the value */
            theField.setTheValue(pValue);
        }
    }

    /**
     * StringEditConverter class.
     */
    public static class TethysStringEditConverter
            implements TethysDataEditConverter<String> {
        @Override
        public boolean rightAlignFields() {
            return false;
        }

        @Override
        public String formatDisplayValue(final String pValue) {
            return pValue;
        }

        @Override
        public String formatEditValue(final String pValue) {
            return pValue;
        }

        @Override
        public String parseEditedValue(final String pValue) {
            return pValue;
        }
    }

    /**
     * CharArrayEditConverter class.
     */
    public static class TethysCharArrayEditConverter
            implements TethysDataEditConverter<char[]> {
        @Override
        public boolean rightAlignFields() {
            return false;
        }

        @Override
        public String formatDisplayValue(final char[] pValue) {
            if (pValue == null) {
                return null;
            }
            char[] myArray = new char[pValue.length];
            Arrays.fill(myArray, TethysPasswordField.BULLET);
            return new String(myArray);
        }

        @Override
        public String formatEditValue(final char[] pValue) {
            return pValue == null
                                  ? null
                                  : new String(pValue);
        }

        @Override
        public char[] parseEditedValue(final String pValue) {
            return pValue == null
                                  ? null
                                  : pValue.toCharArray();
        }
    }

    /**
     * NumberEditConverter class.
     * @param <T> the
     */
    public abstract static class TethysNumberEditConverter<T extends Comparable<? super T>>
            implements TethysDataEditConverter<T> {
        /**
         * Decimal formatter.
         */
        private final TethysDecimalFormatter theFormatter;

        /**
         * Decimal parser.
         */
        private final TethysDecimalParser theParser;

        /**
         * Minimum value.
         */
        private T theMinimum;

        /**
         * Decimal parser.
         */
        private T theMaximum;

        /**
         * Constructor.
         * @param pFormatter the data formatter
         */
        protected TethysNumberEditConverter(final TethysDataFormatter pFormatter) {
            theFormatter = pFormatter.getDecimalFormatter();
            theParser = pFormatter.getDecimalParser();
        }

        /**
         * Obtain the formatter.
         * @return the formatter
         */
        protected TethysDecimalFormatter getFormatter() {
            return theFormatter;
        }

        /**
         * Obtain the parser.
         * @return the parser
         */
        protected TethysDecimalParser getParser() {
            return theParser;
        }

        @Override
        public boolean rightAlignFields() {
            return true;
        }

        @Override
        public String formatEditValue(final T pValue) {
            return pValue == null
                                  ? null
                                  : pValue.toString();
        }

        /**
         * Set value range.
         * @param pMinimum the minimum value
         * @param pMaximum the maximum value
         */
        public void setValueRange(final T pMinimum,
                                  final T pMaximum) {
            /* Store minimum and maximum */
            theMinimum = pMinimum;
            theMaximum = pMaximum;
        }

        /**
         * Check the value range.
         * @param pValue the value
         * @throws IllegalArgumentException on range error
         */
        protected void checkValue(final T pValue) {
            /* Check against minimum */
            boolean bOK = theMinimum == null
                          || theMinimum.compareTo(pValue) <= 0;

            /* Check against maximum */
            if (bOK) {
                bOK = theMaximum == null
                      || theMaximum.compareTo(pValue) >= 0;
            }

            /* Reject failed */
            if (!bOK) {
                throw new IllegalArgumentException("Out of Range");
            }
        }
    }

    /**
     * ShortEditConverter class.
     */
    public static class TethysShortEditConverter
            extends TethysNumberEditConverter<Short> {
        /**
         * Constructor.
         * @param pFormatter the data formatter
         */
        public TethysShortEditConverter(final TethysDataFormatter pFormatter) {
            super(pFormatter);
        }

        @Override
        public String formatDisplayValue(final Short pValue) {
            return getFormatter().formatShort(pValue);
        }

        @Override
        public Short parseEditedValue(final String pValue) {
            Short myValue = getParser().parseShortValue(pValue);
            checkValue(myValue);
            return myValue;
        }
    }

    /**
     * IntegerEditConverter class.
     */
    public static class TethysIntegerEditConverter
            extends TethysNumberEditConverter<Integer> {
        /**
         * Constructor.
         * @param pFormatter the data formatter
         */
        public TethysIntegerEditConverter(final TethysDataFormatter pFormatter) {
            super(pFormatter);
        }

        @Override
        public String formatDisplayValue(final Integer pValue) {
            return getFormatter().formatInteger(pValue);
        }

        @Override
        public Integer parseEditedValue(final String pValue) {
            Integer myValue = getParser().parseIntegerValue(pValue);
            checkValue(myValue);
            return myValue;
        }
    }

    /**
     * LongEditConverter class.
     */
    public static class TethysLongEditConverter
            extends TethysNumberEditConverter<Long> {
        /**
         * Constructor.
         * @param pFormatter the data formatter
         */
        public TethysLongEditConverter(final TethysDataFormatter pFormatter) {
            super(pFormatter);
        }

        @Override
        public String formatDisplayValue(final Long pValue) {
            return getFormatter().formatLong(pValue);
        }

        @Override
        public Long parseEditedValue(final String pValue) {
            Long myValue = getParser().parseLongValue(pValue);
            checkValue(myValue);
            return myValue;
        }
    }

    /**
     * RateEditConverter class.
     */
    public static class TethysRateEditConverter
            extends TethysNumberEditConverter<TethysRate> {
        /**
         * Constructor.
         * @param pFormatter the formatter
         */
        public TethysRateEditConverter(final TethysDataFormatter pFormatter) {
            super(pFormatter);
        }

        @Override
        public String formatDisplayValue(final TethysRate pValue) {
            return getFormatter().formatRate(pValue);
        }

        @Override
        public TethysRate parseEditedValue(final String pValue) {
            TethysRate myValue = getParser().parseRateValue(pValue);
            checkValue(myValue);
            return myValue;
        }
    }

    /**
     * UnitsEditConverter class.
     */
    public static class TethysUnitsEditConverter
            extends TethysNumberEditConverter<TethysUnits> {
        /**
         * Constructor.
         * @param pFormatter the formatter
         */
        public TethysUnitsEditConverter(final TethysDataFormatter pFormatter) {
            super(pFormatter);
        }

        @Override
        public String formatDisplayValue(final TethysUnits pValue) {
            return getFormatter().formatUnits(pValue);
        }

        @Override
        public TethysUnits parseEditedValue(final String pValue) {
            TethysUnits myValue = getParser().parseUnitsValue(pValue);
            checkValue(myValue);
            return myValue;
        }
    }

    /**
     * DilutionEditConverter class.
     */
    public static class TethysDilutionEditConverter
            extends TethysNumberEditConverter<TethysDilution> {
        /**
         * Constructor.
         * @param pFormatter the formatter
         */
        public TethysDilutionEditConverter(final TethysDataFormatter pFormatter) {
            super(pFormatter);
        }

        @Override
        public String formatDisplayValue(final TethysDilution pValue) {
            return getFormatter().formatDilution(pValue);
        }

        @Override
        public TethysDilution parseEditedValue(final String pValue) {
            TethysDilution myValue = getParser().parseDilutionValue(pValue);
            checkValue(myValue);
            return myValue;
        }
    }

    /**
     * RatioEditConverter class.
     */
    public static class TethysRatioEditConverter
            extends TethysNumberEditConverter<TethysRatio> {
        /**
         * Constructor.
         * @param pFormatter the formatter
         */
        public TethysRatioEditConverter(final TethysDataFormatter pFormatter) {
            super(pFormatter);
        }

        @Override
        public String formatDisplayValue(final TethysRatio pValue) {
            return getFormatter().formatRatio(pValue);
        }

        @Override
        public TethysRatio parseEditedValue(final String pValue) {
            TethysRatio myValue = getParser().parseRatioValue(pValue);
            checkValue(myValue);
            return myValue;
        }
    }

    /**
     * CurrencyItem.
     */
    @FunctionalInterface
    public interface TethysCurrencyField {
        /**
         * Set the assumed currency.
         * @param pCurrency the currency
         */
        void setDeemedCurrency(final Currency pCurrency);
    }

    /**
     * Ranged Field interface.
     * @param <T> the data type
     */
    @FunctionalInterface
    public interface TethysRangedField<T> {
        /**
         * Set value range.
         * @param pMinimum the minimum value
         * @param pMaximum the maximum value
         */
        void setValueRange(final T pMinimum,
                           final T pMaximum);
    }

    /**
     * Date Field interface.
     * @param <N> the Node type
     * @param <I> the Icon type
     */
    @FunctionalInterface
    public interface TethysDateField<N, I> {
        /**
         * Obtain the manager.
         * @return the manager
         */
        TethysDateButtonManager<N, I> getDateManager();
    }

    /**
     * Scroll Field interface.
     * @param <T> the value type
     * @param <N> the Node type
     * @param <I> the Icon type
     */
    @FunctionalInterface
    public interface TethysScrollField<T, N, I> {
        /**
         * Obtain the manager.
         * @return the manager
         */
        TethysScrollButtonManager<T, N, I> getScrollManager();
    }

    /**
     * List Field interface.
     * @param <T> the value type
     * @param <N> the Node type
     * @param <I> the Icon type
     */
    @FunctionalInterface
    public interface TethysListField<T, N, I> {
        /**
         * Obtain the manager.
         * @return the manager
         */
        TethysListButtonManager<T, N, I> getListManager();
    }

    /**
     * Icon Field interface.
     * @param <T> the value type
     * @param <N> the Node type
     * @param <I> the Icon type
     */
    @FunctionalInterface
    public interface TethysIconField<T, N, I> {
        /**
         * Obtain the manager.
         * @return the manager
         */
        TethysSimpleIconButtonManager<T, N, I> getIconManager();
    }

    /**
     * StateIcon Field interface.
     * @param <T> the value type
     * @param <S> the state type
     * @param <N> the Node type
     * @param <I> the Icon type
     */
    @FunctionalInterface
    public interface TethysStateIconField<T, S, N, I> {
        /**
         * Obtain the manager.
         * @return the manager
         */
        TethysStateIconButtonManager<T, S, N, I> getIconManager();
    }

    /**
     * MoneyEditConverter class.
     * @param <T> the data type
     */
    public abstract static class TethysMoneyEditConverterBase<T extends TethysMoney>
            extends TethysNumberEditConverter<T>
            implements TethysCurrencyField {
        /**
         * Default currency.
         */
        private Currency theCurrency;

        /**
         * Constructor.
         * @param pFormatter the formatter
         */
        protected TethysMoneyEditConverterBase(final TethysDataFormatter pFormatter) {
            super(pFormatter);
        }

        @Override
        public void setDeemedCurrency(final Currency pCurrency) {
            theCurrency = pCurrency;
        }

        /**
         * Obtain deemed currency.
         * @return the deemed currency
         */
        protected Currency getCurrency() {
            return theCurrency;
        }

        @Override
        public String formatDisplayValue(final T pValue) {
            return getFormatter().formatMoney(pValue);
        }
    }

    /**
     * MoneyEditConverter class.
     */
    public static class TethysMoneyEditConverter
            extends TethysMoneyEditConverterBase<TethysMoney> {
        /**
         * Constructor.
         * @param pFormatter the formatter
         */
        public TethysMoneyEditConverter(final TethysDataFormatter pFormatter) {
            super(pFormatter);
        }

        @Override
        public TethysMoney parseEditedValue(final String pValue) {
            TethysMoney myValue = getParser().parseMoneyValue(pValue, getCurrency());
            checkValue(myValue);
            return myValue;
        }
    }

    /**
     * PriceEditConverter class.
     */
    public static class TethysPriceEditConverter
            extends TethysMoneyEditConverterBase<TethysPrice> {
        /**
         * Constructor.
         * @param pFormatter the formatter
         */
        public TethysPriceEditConverter(final TethysDataFormatter pFormatter) {
            super(pFormatter);
        }

        @Override
        public TethysPrice parseEditedValue(final String pValue) {
            TethysPrice myValue = getParser().parsePriceValue(pValue, getCurrency());
            checkValue(myValue);
            return myValue;
        }
    }

    /**
     * DilutedPriceEditConverter class.
     */
    public static class TethysDilutedPriceEditConverter
            extends TethysMoneyEditConverterBase<TethysDilutedPrice> {
        /**
         * Constructor.
         * @param pFormatter the formatter
         */
        public TethysDilutedPriceEditConverter(final TethysDataFormatter pFormatter) {
            super(pFormatter);
        }

        @Override
        public TethysDilutedPrice parseEditedValue(final String pValue) {
            TethysDilutedPrice myValue = getParser().parseDilutedPriceValue(pValue, getCurrency());
            checkValue(myValue);
            return myValue;
        }
    }
}
