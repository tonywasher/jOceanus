/*******************************************************************************
 * jMetis: Java Data Framework
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
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jmetis.sheet;

import net.sourceforge.joceanus.jtethys.dateday.JDateDay;
import net.sourceforge.joceanus.jtethys.decimal.JDecimal;
import net.sourceforge.joceanus.jtethys.decimal.JDecimalFormatter;
import net.sourceforge.joceanus.jtethys.decimal.JDecimalParser;
import net.sourceforge.joceanus.jtethys.decimal.JDilution;
import net.sourceforge.joceanus.jtethys.decimal.JMoney;
import net.sourceforge.joceanus.jtethys.decimal.JPrice;
import net.sourceforge.joceanus.jtethys.decimal.JRate;
import net.sourceforge.joceanus.jtethys.decimal.JRatio;
import net.sourceforge.joceanus.jtethys.decimal.JUnits;

/**
 * Excel/Oasis format string builder class.
 */
public final class DataFormats {
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
    private DataFormats() {
    }

    /**
     * Obtain format for integer.
     * @return the format
     */
    private static String getIntegerFormat() {
        /* Create String builder */
        StringBuilder myBuilder = new StringBuilder();

        /* Start with zero */
        myBuilder.append(JDecimalFormatter.CHAR_ZERO);

        /* Return the format */
        return myBuilder.toString();
    }

    /**
     * Obtain format for standard decimal.
     * @param pValue the example decimal
     * @return the format
     */
    private static String getStandardFormat(final JDecimal pValue) {
        /* Create String builder */
        StringBuilder myBuilder = new StringBuilder();

        /* Start with zero */
        myBuilder.append(JDecimalFormatter.CHAR_ZERO);

        /* Determine scale */
        int myScale = pValue.scale();
        if (myScale > 0) {
            /* Append the decimal point */
            myBuilder.append(JDecimalFormatter.STR_DEC);

            for (int i = 0; i < myScale; i++) {
                /* Append the decimal places */
                myBuilder.append(JDecimalFormatter.CHAR_ZERO);
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
    private static String getRateFormat(final JRate pValue) {
        /* Create String builder */
        StringBuilder myBuilder = new StringBuilder();

        /* Start with zero */
        myBuilder.append(JDecimalFormatter.CHAR_ZERO);

        /* Determine scale */
        int myScale = pValue.scale()
                      - JDecimalParser.ADJUST_PERCENT;
        if (myScale > 0) {
            /* Append the decimal point */
            myBuilder.append(JDecimalFormatter.STR_DEC);

            for (int i = 0; i < myScale; i++) {
                /* Append the decimal places */
                myBuilder.append(JDecimalFormatter.CHAR_ZERO);
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
    private static String getExtendedFormat(final JDecimal pValue) {
        /* Create String builder */
        StringBuilder myBuilder = new StringBuilder();

        /* Start with standard decimal */
        myBuilder.append(getStandardFormat(pValue));

        /* Insert initial values */
        myBuilder.insert(0, CHAR_DIGIT);
        myBuilder.insert(0, CHAR_DIGIT);
        myBuilder.insert(0, JDecimalFormatter.CHAR_GROUP);
        myBuilder.insert(0, CHAR_DIGIT);

        /* Return the format */
        return myBuilder.toString();
    }

    /**
     * Obtain format for currency.
     * @param pValue the example currency
     * @return the format
     */
    private static String getCurrencyFormat(final JMoney pValue) {
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
        myBuilder.append(JDecimalFormatter.CHAR_MINUS);
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
    private static String getAccountingFormat(final JMoney pValue) {
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
        myBuilder.append(JDecimalFormatter.CHAR_BLANK);
        myBuilder.append(JDecimalFormatter.CHAR_MINUS);

        /* Return the format */
        return myBuilder.toString();
    }

    /**
     * Obtain data format string for a cell type.
     * @param pType the cell style type
     * @return the format string
     */
    protected static String getDataFormatString(final CellStyleType pType) {
        switch (pType) {
            case DATE:
                return getDataFormatString(new JDateDay());
            case BOOLEAN:
                return getDataFormatString(Boolean.TRUE);
            case INTEGER:
                return getDataFormatString(Integer.valueOf(0));
            case MONEY:
                return getDataFormatString(new JMoney(STR_ZERO));
            case PRICE:
                return getDataFormatString(new JPrice(STR_ZERO));
            case RATE:
                return getDataFormatString(new JRate(STR_ZERO));
            case UNITS:
                return getDataFormatString(new JUnits(STR_ZERO));
            case DILUTION:
                return getDataFormatString(new JDilution(STR_ZERO));
            case RATIO:
                return getDataFormatString(new JRatio(STR_ZERO));
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
        if (pValue instanceof JDateDay) {
            return FORMAT_DATE;
        }
        if (pValue instanceof Boolean) {
            return FORMAT_BOOLEAN;
        }
        if (pValue instanceof Integer) {
            return getIntegerFormat();
        }
        if (pValue instanceof JMoney) {
            return getAccountingFormat((JMoney) pValue);
        }
        if (pValue instanceof JRate) {
            return getRateFormat((JRate) pValue);
        }
        if (pValue instanceof JUnits) {
            return getExtendedFormat((JUnits) pValue);
        }
        if (pValue instanceof JDecimal) {
            return getStandardFormat((JDecimal) pValue);
        }
        return null;
    }

    /**
     * Obtain alternate data format string for a cell value.
     * @param pValue the cell value
     * @return the format string
     */
    protected static String getAlternateFormatString(final Object pValue) {
        if (pValue instanceof JMoney) {
            return getCurrencyFormat((JMoney) pValue);
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
    protected static String getFormatName(final CellStyleType pType) {
        switch (pType) {
            case HEADER:
                return getAlternateFormatName(STR_NULL);
            case STRING:
                return getFormatName(STR_NULL);
            case DATE:
                return getFormatName(new JDateDay());
            case BOOLEAN:
                return getFormatName(Boolean.TRUE);
            case INTEGER:
                return getFormatName(Integer.valueOf(0));
            case MONEY:
                return getFormatName(new JMoney(STR_ZERO));
            case PRICE:
                return getFormatName(new JPrice(STR_ZERO));
            case RATE:
                return getFormatName(new JRate(STR_ZERO));
            case UNITS:
                return getFormatName(new JUnits(STR_ZERO));
            case DILUTION:
                return getFormatName(new JDilution(STR_ZERO));
            case RATIO:
                return getFormatName(new JRatio(STR_ZERO));
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
        if (pValue instanceof JDateDay) {
            return getStyleName(JDateDay.class.getSimpleName());
        }
        if (pValue instanceof Integer) {
            return getStyleName(Integer.class.getSimpleName());
        }
        if (pValue instanceof JPrice) {
            String myCurr = ((JPrice) pValue).getCurrency().getCurrencyCode();
            return getStyleName(JPrice.class.getSimpleName()
                                + myCurr);
        }
        if (pValue instanceof JMoney) {
            String myCurr = ((JMoney) pValue).getCurrency().getCurrencyCode();
            return getStyleName(JMoney.class.getSimpleName()
                                + myCurr);
        }
        if (pValue instanceof JRate) {
            return getStyleName(JRate.class.getSimpleName());
        }
        if (pValue instanceof JUnits) {
            return getStyleName(JUnits.class.getSimpleName());
        }
        if (pValue instanceof JDilution) {
            return getStyleName(JDilution.class.getSimpleName());
        }
        if (pValue instanceof JRatio) {
            return getStyleName(JRatio.class.getSimpleName());
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
            return getStyleName(CellStyleType.HEADER.toString());
        }
        String myXtra = "Curr";
        if (pValue instanceof JPrice) {
            String myCurr = ((JPrice) pValue).getCurrency().getCurrencyCode();
            return getStyleName(JPrice.class.getSimpleName()
                                + myXtra
                                + myCurr);
        }
        if (pValue instanceof JMoney) {
            String myCurr = ((JMoney) pValue).getCurrency().getCurrencyCode();
            return getStyleName(JMoney.class.getSimpleName()
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
    protected static boolean hasDataFormat(final CellStyleType pType) {
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
    protected static CellStyleType getCellStyleType(final Object pValue) {
        if (pValue instanceof String) {
            return CellStyleType.STRING;
        }
        if (pValue instanceof JDateDay) {
            return CellStyleType.DATE;
        }
        if (pValue instanceof Boolean) {
            return CellStyleType.BOOLEAN;
        }
        if (pValue instanceof Integer) {
            return CellStyleType.INTEGER;
        }
        if (pValue instanceof JPrice) {
            return CellStyleType.PRICE;
        }
        if (pValue instanceof JMoney) {
            return CellStyleType.MONEY;
        }
        if (pValue instanceof JRate) {
            return CellStyleType.RATE;
        }
        if (pValue instanceof JUnits) {
            return CellStyleType.UNITS;
        }
        if (pValue instanceof JDilution) {
            return CellStyleType.DILUTION;
        }
        if (pValue instanceof JRatio) {
            return CellStyleType.RATIO;
        }
        return null;
    }
}