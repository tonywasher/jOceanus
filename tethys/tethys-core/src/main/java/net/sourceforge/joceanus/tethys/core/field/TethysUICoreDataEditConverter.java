/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.tethys.core.field;

import net.sourceforge.joceanus.oceanus.decimal.OceanusDecimal;
import net.sourceforge.joceanus.oceanus.decimal.OceanusDecimalFormatter;
import net.sourceforge.joceanus.oceanus.decimal.OceanusDecimalParser;
import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;
import net.sourceforge.joceanus.oceanus.decimal.OceanusPrice;
import net.sourceforge.joceanus.oceanus.decimal.OceanusRate;
import net.sourceforge.joceanus.oceanus.decimal.OceanusRatio;
import net.sourceforge.joceanus.oceanus.decimal.OceanusUnits;
import net.sourceforge.joceanus.oceanus.format.OceanusDataFormatter;
import net.sourceforge.joceanus.tethys.core.control.TethysUICorePasswordField;

import java.util.Arrays;
import java.util.Currency;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

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
        private final OceanusDecimalFormatter theFormatter;

        /**
         * Decimal parser.
         */
        private final OceanusDecimalParser theParser;

        /**
         * Constructor.
         * @param pFormatter the data formatter
         */
        protected TethysUICoreNumberEditConverter(final OceanusDataFormatter pFormatter) {
            theFormatter = pFormatter.getDecimalFormatter();
            theParser = pFormatter.getDecimalParser();
        }

        /**
         * Obtain the formatter.
         * @return the formatter
         */
        protected OceanusDecimalFormatter getFormatter() {
            return theFormatter;
        }

        /**
         * Obtain the parser.
         * @return the parser
         */
        protected OceanusDecimalParser getParser() {
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
        public TethysUICoreShortEditConverter(final OceanusDataFormatter pFormatter) {
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
        public TethysUICoreIntegerEditConverter(final OceanusDataFormatter pFormatter) {
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
        public TethysUICoreLongEditConverter(final OceanusDataFormatter pFormatter) {
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
            extends TethysUICoreNumberEditConverter<OceanusDecimal> {
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
        public TethysUICoreRawDecimalEditConverter(final OceanusDataFormatter pFormatter) {
            super(pFormatter);
        }

        @Override
        public String formatDisplayValue(final OceanusDecimal pValue) {
            return getFormatter().formatDecimal(pValue);
        }

        @Override
        public OceanusDecimal parseEditedValue(final String pValue) {
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
            extends TethysUICoreNumberEditConverter<OceanusRate> {
        /**
         * Constructor.
         * @param pFormatter the formatter
         */
        public TethysUICoreRateEditConverter(final OceanusDataFormatter pFormatter) {
            super(pFormatter);
        }

        @Override
        public String formatDisplayValue(final OceanusRate pValue) {
            return getFormatter().formatRate(pValue);
        }

        @Override
        public OceanusRate parseEditedValue(final String pValue) {
            return getParser().parseRateValue(pValue);
        }
    }

    /**
     * UnitsEditConverter class.
     */
    class TethysUICoreUnitsEditConverter
            extends TethysUICoreNumberEditConverter<OceanusUnits> {
        /**
         * Constructor.
         * @param pFormatter the formatter
         */
        public TethysUICoreUnitsEditConverter(final OceanusDataFormatter pFormatter) {
            super(pFormatter);
        }

        @Override
        public String formatDisplayValue(final OceanusUnits pValue) {
            return getFormatter().formatUnits(pValue);
        }

        @Override
        public OceanusUnits parseEditedValue(final String pValue) {
            return getParser().parseUnitsValue(pValue);
        }
    }

    /**
     * RatioEditConverter class.
     */
    class TethysUICoreRatioEditConverter
            extends TethysUICoreNumberEditConverter<OceanusRatio> {
        /**
         * Constructor.
         * @param pFormatter the formatter
         */
        public TethysUICoreRatioEditConverter(final OceanusDataFormatter pFormatter) {
            super(pFormatter);
        }

        @Override
        public String formatDisplayValue(final OceanusRatio pValue) {
            return getFormatter().formatRatio(pValue);
        }

        @Override
        public OceanusRatio parseEditedValue(final String pValue) {
            return getParser().parseRatioValue(pValue);
        }
    }

    /**
     * MoneyEditConverter class.
     * @param <T> the data type
     */
    abstract class TethysUICoreMoneyEditConverterBase<T extends OceanusMoney>
            extends TethysUICoreNumberEditConverter<T> {
        /**
         * Default currency.
         */
        private Supplier<Currency> theCurrency = () -> null;

        /**
         * Constructor.
         * @param pFormatter the formatter
         */
        protected TethysUICoreMoneyEditConverterBase(final OceanusDataFormatter pFormatter) {
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
            extends TethysUICoreMoneyEditConverterBase<OceanusMoney> {
        /**
         * Constructor.
         * @param pFormatter the formatter
         */
        public TethysUICoreMoneyEditConverter(final OceanusDataFormatter pFormatter) {
            super(pFormatter);
        }

        @Override
        public OceanusMoney parseEditedValue(final String pValue) {
            return getParser().parseMoneyValue(pValue, getCurrency());
        }
    }

    /**
     * PriceEditConverter class.
     */
    class TethysUICorePriceEditConverter
            extends TethysUICoreMoneyEditConverterBase<OceanusPrice> {
        /**
         * Constructor.
         * @param pFormatter the formatter
         */
        public TethysUICorePriceEditConverter(final OceanusDataFormatter pFormatter) {
            super(pFormatter);
        }

        @Override
        public OceanusPrice parseEditedValue(final String pValue) {
            return getParser().parsePriceValue(pValue, getCurrency());
        }
    }
}
