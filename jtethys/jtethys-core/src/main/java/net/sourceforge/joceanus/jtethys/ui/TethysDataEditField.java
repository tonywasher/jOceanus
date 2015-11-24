/*******************************************************************************
 * jTethys: Java Utilities
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
 * $URL: http://localhost/svn/Finance/JDateButton/trunk/jdatebutton-javafx/src/main/java/net/sourceforge/jdatebutton/javafx/ArrowIcon.java $
 * $Revision: 573 $
 * $Author: Tony $
 * $Date: 2015-03-03 17:54:12 +0000 (Tue, 03 Mar 2015) $
 ******************************************************************************/
package net.sourceforge.joceanus.jtethys.ui;

import java.util.Currency;
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
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenuItem;

/**
 * Generic class for displaying and editing a data field.
 * @param <T> the data type
 * @param <N> the node type
 * @param <I> the Icon type
 */
public abstract class TethysDataEditField<T, N, I>
        implements TethysEventProvider {
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
     * Value updated.
     */
    public static final int ACTION_NEW_VALUE = 100;

    /**
     * Command issued.
     */
    public static final int ACTION_NEW_COMMAND = 101;

    /**
     * Command Menu build.
     */
    public static final int ACTION_CMDMENU_BUILD = 102;

    /**
     * Invalid value text.
     */
    protected static final String TOOLTIP_BAD_VALUE = "Invalid Value";

    /**
     * The Event Manager.
     */
    private final TethysEventManager theEventManager;

    /**
     * Is the field editable?
     */
    private boolean isEditable;

    /**
     * The value.
     */
    private T theValue;

    /**
     * The ScrollMenu.
     */
    private TethysScrollMenu<String, I> theMenu;

    /**
     * Constructor.
     */
    protected TethysDataEditField() {
        /* Create event manager */
        theEventManager = new TethysEventManager();
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
        theValue = pValue;
    }

    /**
     * Obtain the value.
     * @return the value.
     */
    public T getValue() {
        return theValue;
    }

    /**
     * Obtain the menu.
     * @return the menu.
     */
    public TethysScrollMenu<String, I> getMenu() {
        return theMenu;
    }

    @Override
    public TethysEventRegistrar getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * Fire event.
     * @param pActionId the actionId
     * @param pValue the relevant value
     */
    public void fireEvent(final int pActionId, final Object pValue) {
        theEventManager.fireActionEvent(pActionId, pValue);
    }

    /**
     * handleMenuRequest.
     */
    public void handleMenuRequest() {
        /* fire menuBuild actionEvent */
        fireEvent(ACTION_CMDMENU_BUILD, theMenu);

        /* If a menu is provided */
        if (!theMenu.isEmpty()) {
            /* Show the menu */
            showMenu();
        }
    }

    /**
     * handleMenuClosed.
     */
    protected void handleMenuClosed() {
        /* If we selected a value */
        TethysScrollMenuItem<String> mySelected = theMenu.getSelectedItem();
        if (mySelected != null) {
            /* fire new command actionEvent */
            theEventManager.fireActionEvent(ACTION_NEW_COMMAND, mySelected.getValue());
        }
    }

    /**
     * Obtain the node.
     * @return the node.
     */
    public abstract N getNode();

    /**
     * Show the menu.
     */
    protected abstract void showMenu();

    /**
     * Declare menu.
     * @param pMenu the menu
     */
    protected void declareMenu(final TethysScrollMenu<String, I> pMenu) {
        /* Store the menu */
        theMenu = pMenu;
    }

    /**
     * DataEditTextField base class.
     * @param <T> the data type
     * @param <N> the node type
     * @param <I> the Icon type
     */
    public abstract static class TethysDataEditTextFieldBase<T, N, I>
            extends TethysDataEditField<T, N, I> {
        /**
         * The DataConverter.
         */
        private final TethysDataEditConverter<T> theConverter;

        /**
         * The display text.
         */
        private String theDisplay;

        /**
         * The edit text.
         */
        private String theEdit;

        /**
         * Constructor.
         * @param pConverter the data converter
         */
        protected TethysDataEditTextFieldBase(final TethysDataEditConverter<T> pConverter) {
            /* Store parameter */
            theConverter = pConverter;
        }

        /**
         * Obtain the display text.
         * @return the display text.
         */
        protected String getDisplayText() {
            return theDisplay;
        }

        /**
         * Obtain the edit text.
         * @return the edit text.
         */
        protected String getEditText() {
            return theEdit;
        }

        /**
         * Obtain the converter.
         * @return the converter.
         */
        protected TethysDataEditConverter<T> getConverter() {
            return theConverter;
        }

        /**
         * Process value.
         * @param pNewValue the new value
         * @return is value valid?
         */
        protected boolean processValue(final String pNewValue) {
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
                    fireEvent(ACTION_NEW_VALUE, myValue);

                    /* Catch parsing error */
                } catch (IllegalArgumentException e) {
                    return false;
                }
            }

            /* Return success */
            return true;
        }

        @Override
        public void setValue(final T pValue) {
            /* Store the value */
            super.setValue(pValue);

            /* Obtain display text */
            theDisplay = pValue == null
                                        ? null
                                        : theConverter.formatDisplayValue(pValue);

            /* Obtain edit text */
            theEdit = pValue == null
                                     ? null
                                     : theConverter.formatEditValue(pValue);
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
     * NumberEditConverter class.
     * @param <T> the
     */
    public abstract static class TethysNumberEditConverter<T>
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
         * Constructor.
         * @param pFormatter the formatter
         * @param pParser the parser
         */
        protected TethysNumberEditConverter(final TethysDecimalFormatter pFormatter,
                                            final TethysDecimalParser pParser) {
            theFormatter = pFormatter;
            theParser = pParser;
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
    }

    /**
     * ShortEditConverter class.
     */
    public static class TethysShortEditConverter
            extends TethysNumberEditConverter<Short> {
        /**
         * Constructor.
         * @param pFormatter the formatter
         * @param pParser the parser
         */
        public TethysShortEditConverter(final TethysDecimalFormatter pFormatter,
                                        final TethysDecimalParser pParser) {
            super(pFormatter, pParser);
        }

        @Override
        public String formatDisplayValue(final Short pValue) {
            return getFormatter().formatShort(pValue);
        }

        @Override
        public Short parseEditedValue(final String pValue) {
            return getParser().parseShortValue(pValue);
        }
    }

    /**
     * IntegerEditConverter class.
     */
    public static class TethysIntegerEditConverter
            extends TethysNumberEditConverter<Integer> {
        /**
         * Constructor.
         * @param pFormatter the formatter
         * @param pParser the parser
         */
        public TethysIntegerEditConverter(final TethysDecimalFormatter pFormatter,
                                          final TethysDecimalParser pParser) {
            super(pFormatter, pParser);
        }

        @Override
        public String formatDisplayValue(final Integer pValue) {
            return getFormatter().formatInteger(pValue);
        }

        @Override
        public Integer parseEditedValue(final String pValue) {
            return getParser().parseIntegerValue(pValue);
        }
    }

    /**
     * LongEditConverter class.
     */
    public static class TethysLongEditConverter
            extends TethysNumberEditConverter<Long> {
        /**
         * Constructor.
         * @param pFormatter the formatter
         * @param pParser the parser
         */
        public TethysLongEditConverter(final TethysDecimalFormatter pFormatter,
                                       final TethysDecimalParser pParser) {
            super(pFormatter, pParser);
        }

        @Override
        public String formatDisplayValue(final Long pValue) {
            return getFormatter().formatLong(pValue);
        }

        @Override
        public Long parseEditedValue(final String pValue) {
            return getParser().parseLongValue(pValue);
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
         * @param pParser the parser
         */
        public TethysRateEditConverter(final TethysDecimalFormatter pFormatter,
                                       final TethysDecimalParser pParser) {
            super(pFormatter, pParser);
        }

        @Override
        public String formatDisplayValue(final TethysRate pValue) {
            return getFormatter().formatRate(pValue);
        }

        @Override
        public TethysRate parseEditedValue(final String pValue) {
            return getParser().parseRateValue(pValue);
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
         * @param pParser the parser
         */
        public TethysUnitsEditConverter(final TethysDecimalFormatter pFormatter,
                                        final TethysDecimalParser pParser) {
            super(pFormatter, pParser);
        }

        @Override
        public String formatDisplayValue(final TethysUnits pValue) {
            return getFormatter().formatUnits(pValue);
        }

        @Override
        public TethysUnits parseEditedValue(final String pValue) {
            return getParser().parseUnitsValue(pValue);
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
         * @param pParser the parser
         */
        public TethysDilutionEditConverter(final TethysDecimalFormatter pFormatter,
                                           final TethysDecimalParser pParser) {
            super(pFormatter, pParser);
        }

        @Override
        public String formatDisplayValue(final TethysDilution pValue) {
            return getFormatter().formatDilution(pValue);
        }

        @Override
        public TethysDilution parseEditedValue(final String pValue) {
            return getParser().parseDilutionValue(pValue);
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
         * @param pParser the parser
         */
        public TethysRatioEditConverter(final TethysDecimalFormatter pFormatter,
                                        final TethysDecimalParser pParser) {
            super(pFormatter, pParser);
        }

        @Override
        public String formatDisplayValue(final TethysRatio pValue) {
            return getFormatter().formatRatio(pValue);
        }

        @Override
        public TethysRatio parseEditedValue(final String pValue) {
            return getParser().parseRatioValue(pValue);
        }
    }

    /**
     * MoneyEditConverter class.
     * @param <T> the data type
     */
    public abstract static class TethysMoneyEditConverterBase<T extends TethysMoney>
            extends TethysNumberEditConverter<T> {
        /**
         * Default currency.
         */
        private Currency theCurrency;

        /**
         * Constructor.
         * @param pFormatter the formatter
         * @param pParser the parser
         */
        protected TethysMoneyEditConverterBase(final TethysDecimalFormatter pFormatter,
                                               final TethysDecimalParser pParser) {
            super(pFormatter, pParser);
        }

        /**
         * Set deemed currency.
         * @param pCurrency the deemed currency
         */
        public void setDeemedCurrency(final Currency pCurrency) {
            theCurrency = pCurrency;
        }

        /**
         * Set deemed currency.
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
         * @param pParser the parser
         */
        public TethysMoneyEditConverter(final TethysDecimalFormatter pFormatter,
                                        final TethysDecimalParser pParser) {
            super(pFormatter, pParser);
        }

        @Override
        public TethysMoney parseEditedValue(final String pValue) {
            return getParser().parseMoneyValue(pValue, getCurrency());
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
         * @param pParser the parser
         */
        public TethysPriceEditConverter(final TethysDecimalFormatter pFormatter,
                                        final TethysDecimalParser pParser) {
            super(pFormatter, pParser);
        }

        @Override
        public TethysPrice parseEditedValue(final String pValue) {
            return getParser().parsePriceValue(pValue, getCurrency());
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
         * @param pParser the parser
         */
        public TethysDilutedPriceEditConverter(final TethysDecimalFormatter pFormatter,
                                               final TethysDecimalParser pParser) {
            super(pFormatter, pParser);
        }

        @Override
        public TethysDilutedPrice parseEditedValue(final String pValue) {
            return getParser().parseDilutedPriceValue(pValue, getCurrency());
        }
    }
}
