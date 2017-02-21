/*******************************************************************************
 * jMetis: Java Data Framework
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
package net.sourceforge.joceanus.jmetis.sheet;

import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimalFormatter;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimalParser;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilution;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;

/**
 * Excel/Oasis format string builder class.
 */
public final class MetisDataFormats {
    /**
     * The digit placeholder.
     */
    private static final char CHAR_DIGIT = '#';

    /**
     * The separator placeholder.
     */
    protected static final char CHAR_SEP = ';';

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
    protected static final String OASIS_DATE = "yyyy-MM-dd";

    /**
     * Internal date format.
     */
    private static final String FORMAT_DATE = "dd-MMM-yy";

    /**
     * Internal boolean format.
     */
    private static final String FORMAT_BOOLEAN = "BOOLEAN";

    /**
     * Prevent instantiation.
     */
    private MetisDataFormats() {
    }

    /**
     * Obtain format for integer.
     * @return the format
     */
    private static String getIntegerFormat() {
        /* Create String builder */
        StringBuilder myBuilder = new StringBuilder();

        /* Start with zero */
        myBuilder.append(TethysDecimalFormatter.CHAR_ZERO);

        /* Return the format */
        return myBuilder.toString();
    }

    /**
     * Obtain format for standard decimal.
     * @param pValue the example decimal
     * @return the format
     */
    private static String getStandardFormat(final TethysDecimal pValue) {
        /* Create String builder */
        StringBuilder myBuilder = new StringBuilder();

        /* Start with zero */
        myBuilder.append(TethysDecimalFormatter.CHAR_ZERO);

        /* Determine scale */
        int myScale = pValue.scale();
        if (myScale > 0) {
            /* Append the decimal point */
            myBuilder.append(TethysDecimalFormatter.STR_DEC);

            for (int i = 0; i < myScale; i++) {
                /* Append the decimal places */
                myBuilder.append(TethysDecimalFormatter.CHAR_ZERO);
            }
        }

        /* Return the format */
        return myBuilder.toString();
    }

    /**
     * Obtain format for rate.
     * @param pValue the example rate
     * @return the format
     */
    private static String getRateFormat(final TethysRate pValue) {
        /* Create String builder */
        StringBuilder myBuilder = new StringBuilder();

        /* Start with zero */
        myBuilder.append(TethysDecimalFormatter.CHAR_ZERO);

        /* Determine scale */
        int myScale = pValue.scale()
                      - TethysDecimalParser.ADJUST_PERCENT;
        if (myScale > 0) {
            /* Append the decimal point */
            myBuilder.append(TethysDecimalFormatter.STR_DEC);

            for (int i = 0; i < myScale; i++) {
                /* Append the decimal places */
                myBuilder.append(TethysDecimalFormatter.CHAR_ZERO);
            }
        }

        /* Append the percent */
        myBuilder.append('%');

        /* Return the format */
        return myBuilder.toString();
    }

    /**
     * Obtain format for extended decimal.
     * @param pValue the example decimal
     * @return the format
     */
    private static String getExtendedFormat(final TethysDecimal pValue) {
        /* Create String builder */
        StringBuilder myBuilder = new StringBuilder();

        /* Start with standard decimal */
        myBuilder.append(getStandardFormat(pValue));

        /* Insert initial values */
        myBuilder.insert(0, CHAR_DIGIT);
        myBuilder.insert(0, CHAR_DIGIT);
        myBuilder.insert(0, TethysDecimalFormatter.CHAR_GROUP);
        myBuilder.insert(0, CHAR_DIGIT);

        /* Return the format */
        return myBuilder.toString();
    }

    /**
     * Obtain format for currency.
     * @param pValue the example currency
     * @return the format
     */
    private static String getCurrencyFormat(final TethysMoney pValue) {
        /* Create String builder */
        StringBuilder myBuilder = new StringBuilder();

        /* Start with extended decimal */
        String myFormat = getExtendedFormat(pValue);

        /* Obtain currency code */
        String myCurrency = pValue.getCurrency().getSymbol();

        /* Insert initial values */
        myBuilder.append(myCurrency);
        myBuilder.append(myFormat);
        myBuilder.append(CHAR_SEP);
        myBuilder.append(STR_RED);
        myBuilder.append(TethysDecimalFormatter.CHAR_MINUS);
        myBuilder.append(myCurrency);
        myBuilder.append(myFormat);

        /* Return the format */
        return myBuilder.toString();
    }

    /**
     * Obtain format for currency, with zero usage.
     * @param pValue the example currency
     * @return the format
     */
    private static String getAccountingFormat(final TethysMoney pValue) {
        /* Create String builder */
        StringBuilder myBuilder = new StringBuilder();

        /* Start with currency format */
        String myFormat = getCurrencyFormat(pValue);

        /* Obtain currency code */
        String myCurrency = pValue.getCurrency().getSymbol();

        /* Insert initial values */
        myBuilder.append(myFormat);
        myBuilder.append(CHAR_SEP);
        myBuilder.append(myCurrency);
        myBuilder.append(TethysDecimalFormatter.CHAR_BLANK);
        myBuilder.append(TethysDecimalFormatter.CHAR_MINUS);

        /* Return the format */
        return myBuilder.toString();
    }

    /**
     * Obtain data format string for a cell type.
     * @param pType the cell style type
     * @return the format string
     */
    protected static String getDataFormatString(final MetisCellStyleType pType) {
        switch (pType) {
            case DATE:
                return getDataFormatString(new TethysDate());
            case BOOLEAN:
                return getDataFormatString(Boolean.TRUE);
            case INTEGER:
                return getDataFormatString(Integer.valueOf(0));
            case MONEY:
                return getDataFormatString(new TethysMoney(STR_ZERO));
            case PRICE:
                return getDataFormatString(new TethysPrice(STR_ZERO));
            case RATE:
                return getDataFormatString(new TethysRate(STR_ZERO));
            case UNITS:
                return getDataFormatString(new TethysUnits(STR_ZERO));
            case DILUTION:
                return getDataFormatString(new TethysDilution(STR_ZERO));
            case RATIO:
                return getDataFormatString(new TethysRatio(STR_ZERO));
            default:
                return null;
        }
    }

    /**
     * Obtain data format string for a cell value.
     * @param pValue the cell value
     * @return the format string
     */
    protected static String getDataFormatString(final Object pValue) {
        if (pValue instanceof TethysDate) {
            return FORMAT_DATE;
        }
        if (pValue instanceof Boolean) {
            return FORMAT_BOOLEAN;
        }
        if ((pValue instanceof Integer) ||
            (pValue instanceof Long)) {
            return getIntegerFormat();
        }
        if (pValue instanceof TethysMoney) {
            return getAccountingFormat((TethysMoney) pValue);
        }
        if (pValue instanceof TethysRate) {
            return getRateFormat((TethysRate) pValue);
        }
        if (pValue instanceof TethysUnits) {
            return getExtendedFormat((TethysUnits) pValue);
        }
        if (pValue instanceof TethysDecimal) {
            return getStandardFormat((TethysDecimal) pValue);
        }
        return null;
    }

    /**
     * Obtain alternate data format string for a cell value.
     * @param pValue the cell value
     * @return the format string
     */
    protected static String getAlternateFormatString(final Object pValue) {
        if (pValue instanceof TethysMoney) {
            return getCurrencyFormat((TethysMoney) pValue);
        }
        return null;
    }

    /**
     * Obtain style name.
     * @param pStyle the style type
     * @return the name of the style
     */
    private static String getStyleName(final String pStyle) {
        return "sn"
               + pStyle;
    }

    /**
     * Obtain data format string for a cell type.
     * @param pType the cell style type
     * @return the format string
     */
    protected static String getFormatName(final MetisCellStyleType pType) {
        switch (pType) {
            case HEADER:
                return getAlternateFormatName(STR_NULL);
            case STRING:
                return getFormatName(STR_NULL);
            case DATE:
                return getFormatName(new TethysDate());
            case BOOLEAN:
                return getFormatName(Boolean.TRUE);
            case INTEGER:
                return getFormatName(Integer.valueOf(0));
            case MONEY:
                return getFormatName(new TethysMoney(STR_ZERO));
            case PRICE:
                return getFormatName(new TethysPrice(STR_ZERO));
            case RATE:
                return getFormatName(new TethysRate(STR_ZERO));
            case UNITS:
                return getFormatName(new TethysUnits(STR_ZERO));
            case DILUTION:
                return getFormatName(new TethysDilution(STR_ZERO));
            case RATIO:
                return getFormatName(new TethysRatio(STR_ZERO));
            default:
                return null;
        }
    }

    /**
     * Obtain format name for a cell.
     * @param pValue the cell value
     * @return the format string
     */
    protected static String getFormatName(final Object pValue) {
        if (pValue instanceof String) {
            return getStyleName(String.class.getSimpleName());
        }
        if (pValue instanceof Boolean) {
            return getStyleName(Boolean.class.getSimpleName());
        }
        if (pValue instanceof TethysDate) {
            return getStyleName(TethysDate.class.getSimpleName());
        }
        if ((pValue instanceof Integer) ||
            (pValue instanceof Long)) {
            return getStyleName(Integer.class.getSimpleName());
        }
        if (pValue instanceof TethysPrice) {
            String myCurr = ((TethysPrice) pValue).getCurrency().getCurrencyCode();
            return getStyleName(TethysPrice.class.getSimpleName()
                                + myCurr);
        }
        if (pValue instanceof TethysMoney) {
            String myCurr = ((TethysMoney) pValue).getCurrency().getCurrencyCode();
            return getStyleName(TethysMoney.class.getSimpleName()
                                + myCurr);
        }
        if (pValue instanceof TethysRate) {
            return getStyleName(TethysRate.class.getSimpleName());
        }
        if (pValue instanceof TethysUnits) {
            return getStyleName(TethysUnits.class.getSimpleName());
        }
        if (pValue instanceof TethysDilution) {
            return getStyleName(TethysDilution.class.getSimpleName());
        }
        if (pValue instanceof TethysRatio) {
            return getStyleName(TethysRatio.class.getSimpleName());
        }
        return null;
    }

    /**
     * Obtain currency format name for a cell.
     * @param pValue the cell value
     * @return the format string
     */
    protected static String getAlternateFormatName(final Object pValue) {
        if (pValue instanceof String) {
            return getStyleName(MetisCellStyleType.HEADER.toString());
        }
        String myXtra = "Curr";
        if (pValue instanceof TethysPrice) {
            String myCurr = ((TethysPrice) pValue).getCurrency().getCurrencyCode();
            return getStyleName(TethysPrice.class.getSimpleName()
                                + myXtra
                                + myCurr);
        }
        if (pValue instanceof TethysMoney) {
            String myCurr = ((TethysMoney) pValue).getCurrency().getCurrencyCode();
            return getStyleName(TethysMoney.class.getSimpleName()
                                + myXtra
                                + myCurr);
        }
        return null;
    }

    /**
     * Determine whether cell type has data format.
     * @param pType the cell type
     * @return true/false
     */
    protected static boolean hasDataFormat(final MetisCellStyleType pType) {
        switch (pType) {
            case DATE:
            case BOOLEAN:
            case INTEGER:
            case MONEY:
            case PRICE:
            case RATE:
            case UNITS:
            case DILUTION:
            case RATIO:
                return true;
            default:
                return false;
        }
    }

    /**
     * Obtain data format for a value.
     * @param pValue the cell value
     * @return the cell style type
     */
    protected static MetisCellStyleType getCellStyleType(final Object pValue) {
        if (pValue instanceof String) {
            return MetisCellStyleType.STRING;
        }
        if (pValue instanceof TethysDate) {
            return MetisCellStyleType.DATE;
        }
        if (pValue instanceof Boolean) {
            return MetisCellStyleType.BOOLEAN;
        }
        if ((pValue instanceof Integer)
            || (pValue instanceof Long)) {
            return MetisCellStyleType.INTEGER;
        }
        if (pValue instanceof TethysPrice) {
            return MetisCellStyleType.PRICE;
        }
        if (pValue instanceof TethysMoney) {
            return MetisCellStyleType.MONEY;
        }
        if (pValue instanceof TethysRate) {
            return MetisCellStyleType.RATE;
        }
        if (pValue instanceof TethysUnits) {
            return MetisCellStyleType.UNITS;
        }
        if (pValue instanceof TethysDilution) {
            return MetisCellStyleType.DILUTION;
        }
        if (pValue instanceof TethysRatio) {
            return MetisCellStyleType.RATIO;
        }
        return null;
    }
}
