/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2020 Tony Washer
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
package net.sourceforge.joceanus.jtethys.ui;

import java.util.Arrays;
import java.util.Currency;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimalFormatter;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimalParser;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilutedPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilution;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;

/**
 * Data edit converter.
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
    class TethysStringEditConverter
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
    class TethysCharArrayEditConverter
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
            final char[] myArray = new char[pValue.length];
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
     * @param <T> the number type
     */
    abstract class TethysNumberEditConverter<T extends Comparable<? super T>>
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
    }

    /**
     * ShortEditConverter class.
     */
    class TethysShortEditConverter
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
            return getParser().parseShortValue(pValue);
        }
    }

    /**
     * IntegerEditConverter class.
     */
    class TethysIntegerEditConverter
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
            return getParser().parseIntegerValue(pValue);
        }
    }

    /**
     * LongEditConverter class.
     */
    class TethysLongEditConverter
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
            return getParser().parseLongValue(pValue);
        }
    }

    /**
     * DecimalEditConverter class.
     */
    class TethysRawDecimalEditConverter
            extends TethysNumberEditConverter<TethysDecimal> {
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
        public TethysRawDecimalEditConverter(final TethysDataFormatter pFormatter) {
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
    class TethysRateEditConverter
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
            return getParser().parseRateValue(pValue);
        }
    }

    /**
     * UnitsEditConverter class.
     */
    class TethysUnitsEditConverter
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
            return getParser().parseUnitsValue(pValue);
        }
    }

    /**
     * DilutionEditConverter class.
     */
    class TethysDilutionEditConverter
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
            return getParser().parseDilutionValue(pValue);
        }
    }

    /**
     * RatioEditConverter class.
     */
    class TethysRatioEditConverter
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
            return getParser().parseRatioValue(pValue);
        }
    }

    /**
     * MoneyEditConverter class.
     * @param <T> the data type
     */
    abstract class TethysMoneyEditConverterBase<T extends TethysMoney>
            extends TethysNumberEditConverter<T> {
        /**
         * Default currency.
         */
        private Supplier<Currency> theCurrency = () -> null;

        /**
         * Constructor.
         * @param pFormatter the formatter
         */
        protected TethysMoneyEditConverterBase(final TethysDataFormatter pFormatter) {
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
    class TethysMoneyEditConverter
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
            return getParser().parseMoneyValue(pValue, getCurrency());
        }
    }

    /**
     * PriceEditConverter class.
     */
    class TethysPriceEditConverter
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
            return getParser().parsePriceValue(pValue, getCurrency());
        }
    }

    /**
     * DilutedPriceEditConverter class.
     */
    class TethysDilutedPriceEditConverter
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
            return getParser().parseDilutedPriceValue(pValue, getCurrency());
        }
    }
}
