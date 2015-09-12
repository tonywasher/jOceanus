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

import net.sourceforge.joceanus.jtethys.decimal.JDecimalFormatter;
import net.sourceforge.joceanus.jtethys.decimal.JDecimalParser;
import net.sourceforge.joceanus.jtethys.decimal.JDilutedPrice;
import net.sourceforge.joceanus.jtethys.decimal.JDilution;
import net.sourceforge.joceanus.jtethys.decimal.JMoney;
import net.sourceforge.joceanus.jtethys.decimal.JPrice;
import net.sourceforge.joceanus.jtethys.decimal.JRate;
import net.sourceforge.joceanus.jtethys.decimal.JRatio;
import net.sourceforge.joceanus.jtethys.decimal.JUnits;
import net.sourceforge.joceanus.jtethys.event.JOceanusEventManager;
import net.sourceforge.joceanus.jtethys.event.JOceanusEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.JOceanusEventRegistrar.JOceanusEventProvider;
import net.sourceforge.joceanus.jtethys.ui.ScrollMenuContent.ScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.ScrollMenuContent.ScrollMenuItem;

/**
 * Generic class for displaying and editing a data field.
 * @param <T> the data type
 * @param <N> the node type
 * @param <I> the Icon type
 */
public abstract class DataEditField<T, N, I>
        implements JOceanusEventProvider {
    /**
     * DataEditConverter interface.
     * @param <T> the data type
     */
    public interface DataEditConverter<T> {

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
     * Menu build.
     */
    public static final int ACTION_CMDMENU_BUILD = 102;

    /**
     * Invalid value text.
     */
    protected static final String TOOLTIP_BAD_VALUE = "Invalid Value";

    /**
     * The Event Manager.
     */
    private final JOceanusEventManager theEventManager;

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
    private ScrollMenu<String, I> theMenu;

    /**
     * Constructor.
     */
    protected DataEditField() {
        /* Create event manager */
        theEventManager = new JOceanusEventManager();
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
    public ScrollMenu<String, I> getMenu() {
        return theMenu;
    }

    @Override
    public JOceanusEventRegistrar getEventRegistrar() {
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
        ScrollMenuItem<String> mySelected = theMenu.getSelectedItem();
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
    protected void declareMenu(final ScrollMenu<String, I> pMenu) {
        /* Store the menu */
        theMenu = pMenu;
    }

    /**
     * DataEditTextField base class.
     * @param <T> the data type
     * @param <N> the node type
     * @param <I> the Icon type
     */
    public abstract static class DataEditTextFieldBase<T, N, I>
            extends DataEditField<T, N, I> {
        /**
         * The DataConverter.
         */
        private final DataEditConverter<T> theConverter;

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
        protected DataEditTextFieldBase(final DataEditConverter<T> pConverter) {
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
        protected DataEditConverter<T> getConverter() {
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
    public static class StringEditConverter
            implements DataEditConverter<String> {
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
    public abstract static class NumberEditConverter<T>
            implements DataEditConverter<T> {
        /**
         * Decimal formatter.
         */
        private final JDecimalFormatter theFormatter;

        /**
         * Decimal parser.
         */
        private final JDecimalParser theParser;

        /**
         * Constructor.
         * @param pFormatter the formatter
         * @param pParser the parser
         */
        protected NumberEditConverter(final JDecimalFormatter pFormatter,
                                      final JDecimalParser pParser) {
            theFormatter = pFormatter;
            theParser = pParser;
        }

        /**
         * Obtain the formatter.
         * @return the formatter
         */
        protected JDecimalFormatter getFormatter() {
            return theFormatter;
        }

        /**
         * Obtain the parser.
         * @return the parser
         */
        protected JDecimalParser getParser() {
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
    public static class ShortEditConverter
            extends NumberEditConverter<Short> {
        /**
         * Constructor.
         * @param pFormatter the formatter
         * @param pParser the parser
         */
        public ShortEditConverter(final JDecimalFormatter pFormatter,
                                  final JDecimalParser pParser) {
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
    public static class IntegerEditConverter
            extends NumberEditConverter<Integer> {
        /**
         * Constructor.
         * @param pFormatter the formatter
         * @param pParser the parser
         */
        public IntegerEditConverter(final JDecimalFormatter pFormatter,
                                    final JDecimalParser pParser) {
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
    public static class LongEditConverter
            extends NumberEditConverter<Long> {
        /**
         * Constructor.
         * @param pFormatter the formatter
         * @param pParser the parser
         */
        public LongEditConverter(final JDecimalFormatter pFormatter,
                                 final JDecimalParser pParser) {
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
    public static class RateEditConverter
            extends NumberEditConverter<JRate> {
        /**
         * Constructor.
         * @param pFormatter the formatter
         * @param pParser the parser
         */
        public RateEditConverter(final JDecimalFormatter pFormatter,
                                 final JDecimalParser pParser) {
            super(pFormatter, pParser);
        }

        @Override
        public String formatDisplayValue(final JRate pValue) {
            return getFormatter().formatRate(pValue);
        }

        @Override
        public JRate parseEditedValue(final String pValue) {
            return getParser().parseRateValue(pValue);
        }
    }

    /**
     * UnitsEditConverter class.
     */
    public static class UnitsEditConverter
            extends NumberEditConverter<JUnits> {
        /**
         * Constructor.
         * @param pFormatter the formatter
         * @param pParser the parser
         */
        public UnitsEditConverter(final JDecimalFormatter pFormatter,
                                  final JDecimalParser pParser) {
            super(pFormatter, pParser);
        }

        @Override
        public String formatDisplayValue(final JUnits pValue) {
            return getFormatter().formatUnits(pValue);
        }

        @Override
        public JUnits parseEditedValue(final String pValue) {
            return getParser().parseUnitsValue(pValue);
        }
    }

    /**
     * DilutionEditConverter class.
     */
    public static class DilutionEditConverter
            extends NumberEditConverter<JDilution> {
        /**
         * Constructor.
         * @param pFormatter the formatter
         * @param pParser the parser
         */
        public DilutionEditConverter(final JDecimalFormatter pFormatter,
                                     final JDecimalParser pParser) {
            super(pFormatter, pParser);
        }

        @Override
        public String formatDisplayValue(final JDilution pValue) {
            return getFormatter().formatDilution(pValue);
        }

        @Override
        public JDilution parseEditedValue(final String pValue) {
            return getParser().parseDilutionValue(pValue);
        }
    }

    /**
     * RatioEditConverter class.
     */
    public static class RatioEditConverter
            extends NumberEditConverter<JRatio> {
        /**
         * Constructor.
         * @param pFormatter the formatter
         * @param pParser the parser
         */
        public RatioEditConverter(final JDecimalFormatter pFormatter,
                                  final JDecimalParser pParser) {
            super(pFormatter, pParser);
        }

        @Override
        public String formatDisplayValue(final JRatio pValue) {
            return getFormatter().formatRatio(pValue);
        }

        @Override
        public JRatio parseEditedValue(final String pValue) {
            return getParser().parseRatioValue(pValue);
        }
    }

    /**
     * MoneyEditConverter class.
     * @param <T> the data type
     */
    public abstract static class MoneyEditConverterBase<T extends JMoney>
            extends NumberEditConverter<T> {
        /**
         * Default currency.
         */
        private Currency theCurrency;

        /**
         * Constructor.
         * @param pFormatter the formatter
         * @param pParser the parser
         */
        protected MoneyEditConverterBase(final JDecimalFormatter pFormatter,
                                         final JDecimalParser pParser) {
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
    public static class MoneyEditConverter
            extends MoneyEditConverterBase<JMoney> {
        /**
         * Constructor.
         * @param pFormatter the formatter
         * @param pParser the parser
         */
        public MoneyEditConverter(final JDecimalFormatter pFormatter,
                                  final JDecimalParser pParser) {
            super(pFormatter, pParser);
        }

        @Override
        public JMoney parseEditedValue(final String pValue) {
            return getParser().parseMoneyValue(pValue, getCurrency());
        }
    }

    /**
     * PriceEditConverter class.
     */
    public static class PriceEditConverter
            extends MoneyEditConverterBase<JPrice> {
        /**
         * Constructor.
         * @param pFormatter the formatter
         * @param pParser the parser
         */
        public PriceEditConverter(final JDecimalFormatter pFormatter,
                                  final JDecimalParser pParser) {
            super(pFormatter, pParser);
        }

        @Override
        public JPrice parseEditedValue(final String pValue) {
            return getParser().parsePriceValue(pValue, getCurrency());
        }
    }

    /**
     * DilutedPriceEditConverter class.
     */
    public static class DilutedPriceEditConverter
            extends MoneyEditConverterBase<JDilutedPrice> {
        /**
         * Constructor.
         * @param pFormatter the formatter
         * @param pParser the parser
         */
        public DilutedPriceEditConverter(final JDecimalFormatter pFormatter,
                                         final JDecimalParser pParser) {
            super(pFormatter, pParser);
        }

        @Override
        public JDilutedPrice parseEditedValue(final String pValue) {
            return getParser().parseDilutedPriceValue(pValue, getCurrency());
        }
    }
}
