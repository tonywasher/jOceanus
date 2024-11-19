/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2024 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.tethys.ui.core.field;

import java.util.Arrays;
import java.util.Currency;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

import net.sourceforge.joceanus.tethys.decimal.TethysDecimal;
import net.sourceforge.joceanus.tethys.decimal.TethysDecimalFormatter;
import net.sourceforge.joceanus.tethys.decimal.TethysDecimalParser;
import net.sourceforge.joceanus.tethys.decimal.TethysMoney;
import net.sourceforge.joceanus.tethys.decimal.TethysPrice;
import net.sourceforge.joceanus.tethys.decimal.TethysRate;
import net.sourceforge.joceanus.tethys.decimal.TethysRatio;
import net.sourceforge.joceanus.tethys.decimal.TethysUnits;
import net.sourceforge.joceanus.tethys.ui.api.base.TethysUIDataFormatter;
import net.sourceforge.joceanus.tethys.ui.core.control.TethysUICorePasswordField;

/**
 * Data edit converter.
 * @param <T> the data type
 */
public interface TethysUICoreDataEditConverter<T> {
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
    String formatDisplayValue(T pValue);

    /**
     * Format the edit value.
     * @param pValue the value
     * @return the edit string.
     */
    String formatEditValue(T pValue);

    /**
     * Parse the edited value.
     * @param pValue the value
     * @return the parsed value.
     * @throws IllegalArgumentException on parsing error
     */
    T parseEditedValue(String pValue);

    /**
     * StringEditConverter class.
     */
    class TethysUICoreStringEditConverter
            implements TethysUICoreDataEditConverter<String> {
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
    class TethysUICoreCharArrayEditConverter
            implements TethysUICoreDataEditConverter<char[]> {
        @Override
        public boolean rightAlignFields() {
            return false;
        }

        @Override
        public String formatDisplayValue(final char[] pValue) {
            if (pValue == null) {
                return null;
            }
            final char[] myArray = new char[pValue.length];
            Arrays.fill(myArray, TethysUICorePasswordField.BULLET);
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
     * @param <T> the number type
     */
    abstract class TethysUICoreNumberEditConverter<T extends Comparable<? super T>>
            implements TethysUICoreDataEditConverter<T> {
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
         * @param pFormatter the data formatter
         */
        protected TethysUICoreNumberEditConverter(final TethysUIDataFormatter pFormatter) {
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
    }

    /**
     * ShortEditConverter class.
     */
    class TethysUICoreShortEditConverter
            extends TethysUICoreNumberEditConverter<Short> {
        /**
         * Constructor.
         * @param pFormatter the data formatter
         */
        public TethysUICoreShortEditConverter(final TethysUIDataFormatter pFormatter) {
            super(pFormatter);
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
    class TethysUICoreIntegerEditConverter
            extends TethysUICoreNumberEditConverter<Integer> {
        /**
         * Constructor.
         * @param pFormatter the data formatter
         */
        public TethysUICoreIntegerEditConverter(final TethysUIDataFormatter pFormatter) {
            super(pFormatter);
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
    class TethysUICoreLongEditConverter
            extends TethysUICoreNumberEditConverter<Long> {
        /**
         * Constructor.
         * @param pFormatter the data formatter
         */
        public TethysUICoreLongEditConverter(final TethysUIDataFormatter pFormatter) {
            super(pFormatter);
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
     * DecimalEditConverter class.
     */
    class TethysUICoreRawDecimalEditConverter
            extends TethysUICoreNumberEditConverter<TethysDecimal> {
        /**
         * The default number of decimals.
         */
        public static final int DEFAULT_DECIMALS = 6;

        /**
         * The number of decimals supplier.
         */
        private IntSupplier theNumDecimals = () -> DEFAULT_DECIMALS;

        /**
         * Constructor.
         * @param pFormatter the formatter
         */
        public TethysUICoreRawDecimalEditConverter(final TethysUIDataFormatter pFormatter) {
            super(pFormatter);
        }

        @Override
        public String formatDisplayValue(final TethysDecimal pValue) {
            return getFormatter().formatDecimal(pValue);
        }

        @Override
        public TethysDecimal parseEditedValue(final String pValue) {
            final int myNumDecimals = theNumDecimals.getAsInt();
            return getParser().parseDecimalValue(pValue, myNumDecimals);
        }

        /**
         * Set the Number of decimals supplier.
         * @param pSupplier the supplier
         */
        public void setNumDecimals(final IntSupplier pSupplier) {
            theNumDecimals = pSupplier;
        }
    }

    /**
     * RateEditConverter class.
     */
    class TethysUICoreRateEditConverter
            extends TethysUICoreNumberEditConverter<TethysRate> {
        /**
         * Constructor.
         * @param pFormatter the formatter
         */
        public TethysUICoreRateEditConverter(final TethysUIDataFormatter pFormatter) {
            super(pFormatter);
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
    class TethysUICoreUnitsEditConverter
            extends TethysUICoreNumberEditConverter<TethysUnits> {
        /**
         * Constructor.
         * @param pFormatter the formatter
         */
        public TethysUICoreUnitsEditConverter(final TethysUIDataFormatter pFormatter) {
            super(pFormatter);
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
     * RatioEditConverter class.
     */
    class TethysUICoreRatioEditConverter
            extends TethysUICoreNumberEditConverter<TethysRatio> {
        /**
         * Constructor.
         * @param pFormatter the formatter
         */
        public TethysUICoreRatioEditConverter(final TethysUIDataFormatter pFormatter) {
            super(pFormatter);
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
    abstract class TethysUICoreMoneyEditConverterBase<T extends TethysMoney>
            extends TethysUICoreNumberEditConverter<T> {
        /**
         * Default currency.
         */
        private Supplier<Currency> theCurrency = () -> null;

        /**
         * Constructor.
         * @param pFormatter the formatter
         */
        protected TethysUICoreMoneyEditConverterBase(final TethysUIDataFormatter pFormatter) {
            super(pFormatter);
        }

        /**
         * Set the currency supplier.
         * @param pSupplier the supplier
         */
        public void setDeemedCurrency(final Supplier<Currency> pSupplier) {
            theCurrency = pSupplier;
        }

        /**
         * Obtain deemed currency.
         * @return the deemed currency
         */
        protected Currency getCurrency() {
            return theCurrency.get();
        }

        @Override
        public String formatDisplayValue(final T pValue) {
            return getFormatter().formatMoney(pValue);
        }
    }

    /**
     * MoneyEditConverter class.
     */
    class TethysUICoreMoneyEditConverter
            extends TethysUICoreMoneyEditConverterBase<TethysMoney> {
        /**
         * Constructor.
         * @param pFormatter the formatter
         */
        public TethysUICoreMoneyEditConverter(final TethysUIDataFormatter pFormatter) {
            super(pFormatter);
        }

        @Override
        public TethysMoney parseEditedValue(final String pValue) {
            return getParser().parseMoneyValue(pValue, getCurrency());
        }
    }

    /**
     * PriceEditConverter class.
     */
    class TethysUICorePriceEditConverter
            extends TethysUICoreMoneyEditConverterBase<TethysPrice> {
        /**
         * Constructor.
         * @param pFormatter the formatter
         */
        public TethysUICorePriceEditConverter(final TethysUIDataFormatter pFormatter) {
            super(pFormatter);
        }

        @Override
        public TethysPrice parseEditedValue(final String pValue) {
            return getParser().parsePriceValue(pValue, getCurrency());
        }
    }
}
