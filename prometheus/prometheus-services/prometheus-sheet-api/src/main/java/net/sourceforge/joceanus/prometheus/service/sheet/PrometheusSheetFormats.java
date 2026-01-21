/*
 * Prometheus: Application Framework
 * Copyright 2012-2026. Tony Washer
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
 */
package net.sourceforge.joceanus.prometheus.service.sheet;

import io.github.tonywasher.joceanus.oceanus.date.OceanusDate;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusDecimal;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusDecimalFormatter;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusDecimalParser;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusMoney;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusPrice;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusRate;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusRatio;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusUnits;

/**
 * Excel/Oasis format string builder class.
 */
public final class PrometheusSheetFormats {
    /**
     * Money accounting format width.
     */
    static final int ACCOUNTING_WIDTH = 10;

    /**
     * Date width.
     */
    public static final int WIDTH_DATE = 11;

    /**
     * Integer width.
     */
    public static final int WIDTH_INT = 8;

    /**
     * Boolean width.
     */
    public static final int WIDTH_BOOL = 8;

    /**
     * Money width.
     */
    public static final int WIDTH_MONEY = 13;

    /**
     * Units width.
     */
    public static final int WIDTH_UNITS = 13;

    /**
     * Rate width.
     */
    public static final int WIDTH_RATE = 13;

    /**
     * Dilution width.
     */
    public static final int WIDTH_DILUTION = 13;

    /**
     * Ratio width.
     */
    public static final int WIDTH_RATIO = 13;

    /**
     * Price width.
     */
    public static final int WIDTH_PRICE = 15;

    /**
     * String width.
     */
    public static final int WIDTH_STRING = 30;

    /**
     * Font Height.
     */
    public static final int FONT_HEIGHT = 10;

    /**
     * Value Font.
     */
    public static final String FONT_VALUE = "Arial";

    /**
     * Numeric Font.
     */
    public static final String FONT_NUMERIC = "Courier";

    /**
     * The digit placeholder.
     */
    private static final char CHAR_DIGIT = '#';

    /**
     * The separator placeholder.
     */
    private static final char CHAR_SEP = ';';

    /**
     * The Quote String.
     */
    private static final String STR_QUOTE = "\"";

    /**
     * The Red indicating.
     */
    private static final String STR_RED = "[RED]";

    /**
     * The Zero string.
     */
    private static final String STR_ZERO = "0";

    /**
     * The Null string.
     */
    private static final String STR_NULL = "";

    /**
     * Oasis date format.
     */
    static final String OASIS_DATE = "yyyy-MM-dd";

    /**
     * Internal date format.
     */
    private static final String FORMAT_DATE = "dd-MMM-yy";

    /**
     * Internal boolean format.
     */
    private static final String FORMAT_BOOLEAN = "\"TRUE\";;\"FALSE\"";

    /**
     * Format current prefix.
     */
    private static final String FORMAT_CURR = "Curr";

    /**
     * Prevent instantiation.
     */
    private PrometheusSheetFormats() {
    }

    /**
     * Obtain format for integer.
     *
     * @return the format
     */
    private static String getIntegerFormat() {
        /* Create String builder */
        final StringBuilder myBuilder = new StringBuilder();

        /* Start with zero */
        myBuilder.append(OceanusDecimalFormatter.CHAR_ZERO);

        /* Return the format */
        return myBuilder.toString();
    }

    /**
     * Obtain format for standard decimal.
     *
     * @param pValue the example decimal
     * @return the format
     */
    private static String getStandardFormat(final OceanusDecimal pValue) {
        /* Create String builder */
        final StringBuilder myBuilder = new StringBuilder();

        /* Start with zero */
        myBuilder.append(OceanusDecimalFormatter.CHAR_ZERO);

        /* Determine scale */
        final int myScale = pValue.scale();
        if (myScale > 0) {
            /* Append the decimal point */
            myBuilder.append(OceanusDecimalFormatter.STR_DEC);

            /* Append the decimal places */
            myBuilder.append(String.valueOf(OceanusDecimalFormatter.CHAR_ZERO).repeat(myScale));
        }

        /* Return the format */
        return myBuilder.toString();
    }

    /**
     * Obtain format for rate.
     *
     * @param pValue the example rate
     * @return the format
     */
    private static String getRateFormat(final OceanusRate pValue) {
        /* Create String builder */
        final StringBuilder myBuilder = new StringBuilder();

        /* Start with zero */
        myBuilder.append(OceanusDecimalFormatter.CHAR_ZERO);

        /* Determine scale */
        final int myScale = pValue.scale()
                - OceanusDecimalParser.ADJUST_PERCENT;
        if (myScale > 0) {
            /* Append the decimal point */
            myBuilder.append(OceanusDecimalFormatter.STR_DEC);

            /* Append the decimal places */
            myBuilder.append(String.valueOf(OceanusDecimalFormatter.CHAR_ZERO).repeat(myScale));
        }

        /* Append the percent */
        myBuilder.append('%');

        /* Return the format */
        return myBuilder.toString();
    }

    /**
     * Obtain format for extended decimal.
     *
     * @param pValue the example decimal
     * @return the format
     */
    private static String getExtendedFormat(final OceanusDecimal pValue) {
        /* Create String builder */
        final StringBuilder myBuilder = new StringBuilder();

        /* Start with standard decimal */
        myBuilder.append(getStandardFormat(pValue));

        /* Insert initial values */
        myBuilder.insert(0, CHAR_DIGIT);
        myBuilder.insert(0, CHAR_DIGIT);
        myBuilder.insert(0, OceanusDecimalFormatter.CHAR_GROUP);
        myBuilder.insert(0, CHAR_DIGIT);

        /* Return the format */
        return myBuilder.toString();
    }

    /**
     * Obtain format for currency.
     *
     * @param pValue the example currency
     * @return the format
     */
    private static String getCurrencyFormat(final OceanusMoney pValue) {
        /* Create String builder */
        final StringBuilder myBuilder = new StringBuilder();

        /* Start with extended decimal */
        final String myFormat = getExtendedFormat(pValue);

        /* Obtain currency code */
        String myCurrency = pValue.getCurrency().getSymbol();
        myCurrency = STR_QUOTE + myCurrency + STR_QUOTE;

        /* Insert initial values */
        myBuilder.append(myCurrency);
        myBuilder.append(myFormat);
        myBuilder.append(CHAR_SEP);
        myBuilder.append(STR_RED);
        myBuilder.append(OceanusDecimalFormatter.CHAR_MINUS);
        myBuilder.append(myCurrency);
        myBuilder.append(myFormat);

        /* Return the format */
        return myBuilder.toString();
    }

    /**
     * Obtain format for currency, with zero usage.
     *
     * @param pValue the example currency
     * @return the format
     */
    private static String getAccountingFormat(final OceanusMoney pValue) {
        /* Create String builder */
        final StringBuilder myBuilder = new StringBuilder();

        /* Start with currency format */
        final String myFormat = getCurrencyFormat(pValue);

        /* Obtain currency code */
        String myCurrency = pValue.getCurrency().getSymbol();
        myCurrency = STR_QUOTE + myCurrency + STR_QUOTE;

        /* Insert initial values */
        myBuilder.append(myFormat);
        myBuilder.append(CHAR_SEP);
        myBuilder.append(myCurrency);
        myBuilder.append(OceanusDecimalFormatter.CHAR_BLANK);
        myBuilder.append(OceanusDecimalFormatter.CHAR_MINUS);

        /* Return the format */
        return myBuilder.toString();
    }

    /**
     * Obtain data format string for a cell type.
     *
     * @param pType the cell style type
     * @return the format string
     */
    public static String getDataFormatString(final PrometheusSheetCellStyleType pType) {
        return getDataFormatString(getDefaultValue(pType));
    }

    /**
     * Obtain default value for a cell type.
     *
     * @param pType the cell style type
     * @return the format string
     */
    public static Object getDefaultValue(final PrometheusSheetCellStyleType pType) {
        switch (pType) {
            case STRING:
                return STR_NULL;
            case DATE:
                return new OceanusDate();
            case BOOLEAN:
                return Boolean.TRUE;
            case INTEGER:
                return 0;
            case MONEY:
                return new OceanusMoney(STR_ZERO);
            case PRICE:
                return new OceanusPrice(STR_ZERO);
            case RATE:
                return new OceanusRate(STR_ZERO);
            case UNITS:
                return new OceanusUnits(STR_ZERO);
            case RATIO:
                return new OceanusRatio(STR_ZERO);
            default:
                return null;
        }
    }

    /**
     * Obtain data format string for a cell value.
     *
     * @param pValue the cell value
     * @return the format string
     */
    public static String getDataFormatString(final Object pValue) {
        if (pValue instanceof OceanusDate) {
            return FORMAT_DATE;
        }
        if (pValue instanceof Boolean) {
            return FORMAT_BOOLEAN;
        }
        if (pValue instanceof Integer
                || pValue instanceof Long) {
            return getIntegerFormat();
        }
        if (pValue instanceof OceanusMoney m) {
            return getAccountingFormat(m);
        }
        if (pValue instanceof OceanusRate r) {
            return getRateFormat(r);
        }
        if (pValue instanceof OceanusUnits u) {
            return getExtendedFormat(u);
        }
        if (pValue instanceof OceanusDecimal d) {
            return getStandardFormat(d);
        }
        return null;
    }

    /**
     * Obtain alternate data format string for a cell value.
     *
     * @param pValue the cell value
     * @return the format string
     */
    public static String getAlternateFormatString(final Object pValue) {
        if (pValue instanceof OceanusMoney m) {
            return getCurrencyFormat(m);
        }
        return null;
    }

    /**
     * Obtain style name.
     *
     * @param pStyle the style type
     * @return the name of the style
     */
    private static String getStyleName(final String pStyle) {
        return "sn"
                + pStyle;
    }

    /**
     * Obtain data format string for a cell type.
     *
     * @param pType the cell style type
     * @return the format string
     */
    public static String getFormatName(final PrometheusSheetCellStyleType pType) {
        return pType == PrometheusSheetCellStyleType.HEADER
                ? getAlternateFormatName(STR_NULL)
                : getFormatName(getDefaultValue(pType));
    }

    /**
     * Obtain format name for a cell.
     *
     * @param pValue the cell value
     * @return the format string
     */
    public static String getFormatName(final Object pValue) {
        if (pValue instanceof String) {
            return getStyleName(String.class.getSimpleName());
        }
        if (pValue instanceof Boolean) {
            return getStyleName(Boolean.class.getSimpleName());
        }
        if (pValue instanceof OceanusDate) {
            return getStyleName(OceanusDate.class.getSimpleName());
        }
        if (pValue instanceof Integer
                || pValue instanceof Long) {
            return getStyleName(Integer.class.getSimpleName());
        }
        if (pValue instanceof OceanusPrice p) {
            final String myCurr = p.getCurrency().getCurrencyCode();
            return getStyleName(OceanusPrice.class.getSimpleName()
                    + myCurr);
        }
        if (pValue instanceof OceanusMoney m) {
            final String myCurr = m.getCurrency().getCurrencyCode();
            return getStyleName(OceanusMoney.class.getSimpleName()
                    + myCurr);
        }
        if (pValue instanceof OceanusRate) {
            return getStyleName(OceanusRate.class.getSimpleName());
        }
        if (pValue instanceof OceanusUnits) {
            return getStyleName(OceanusUnits.class.getSimpleName());
        }
        if (pValue instanceof OceanusRatio) {
            return getStyleName(OceanusRatio.class.getSimpleName());
        }
        return null;
    }

    /**
     * Obtain currency format name for a cell.
     *
     * @param pValue the cell value
     * @return the format string
     */
    public static String getAlternateFormatName(final Object pValue) {
        if (pValue instanceof String) {
            return getStyleName(PrometheusSheetCellStyleType.HEADER.toString());
        }
        if (pValue instanceof OceanusPrice p) {
            final String myCurr = p.getCurrency().getCurrencyCode();
            return getStyleName(OceanusPrice.class.getSimpleName()
                    + FORMAT_CURR
                    + myCurr);
        }
        if (pValue instanceof OceanusMoney m) {
            final String myCurr = m.getCurrency().getCurrencyCode();
            return getStyleName(OceanusMoney.class.getSimpleName()
                    + FORMAT_CURR
                    + myCurr);
        }
        return null;
    }

    /**
     * Determine whether cell type has data format.
     *
     * @param pType the cell type
     * @return true/false
     */
    public static boolean hasDataFormat(final PrometheusSheetCellStyleType pType) {
        switch (pType) {
            case DATE:
            case BOOLEAN:
            case INTEGER:
            case MONEY:
            case PRICE:
            case RATE:
            case UNITS:
            case RATIO:
                return true;
            default:
                return false;
        }
    }

    /**
     * Obtain data format for a value.
     *
     * @param pValue the cell value
     * @return the cell style type
     */
    public static PrometheusSheetCellStyleType getCellStyleType(final Object pValue) {
        if (pValue instanceof String) {
            return PrometheusSheetCellStyleType.STRING;
        }
        if (pValue instanceof OceanusDate) {
            return PrometheusSheetCellStyleType.DATE;
        }
        if (pValue instanceof Boolean) {
            return PrometheusSheetCellStyleType.BOOLEAN;
        }
        if (pValue instanceof Integer
                || pValue instanceof Long) {
            return PrometheusSheetCellStyleType.INTEGER;
        }
        if (pValue instanceof OceanusPrice) {
            return PrometheusSheetCellStyleType.PRICE;
        }
        if (pValue instanceof OceanusMoney) {
            return PrometheusSheetCellStyleType.MONEY;
        }
        if (pValue instanceof OceanusRate) {
            return PrometheusSheetCellStyleType.RATE;
        }
        if (pValue instanceof OceanusUnits) {
            return PrometheusSheetCellStyleType.UNITS;
        }
        if (pValue instanceof OceanusRatio) {
            return PrometheusSheetCellStyleType.RATIO;
        }
        return null;
    }
}
