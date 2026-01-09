/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012-2026 Tony Washer
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
package net.sourceforge.joceanus.prometheus.service.sheet.odf;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Element;

import net.sourceforge.joceanus.prometheus.service.sheet.PrometheusSheetCellStyleType;
import net.sourceforge.joceanus.prometheus.service.sheet.PrometheusSheetFormats;
import net.sourceforge.joceanus.oceanus.decimal.OceanusDecimal;
import net.sourceforge.joceanus.oceanus.decimal.OceanusDecimalParser;
import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;
import net.sourceforge.joceanus.oceanus.decimal.OceanusRate;

/**
 * Odf Styles.
 */
public class PrometheusOdfStyler {
    /**
     * Default Parent style name.
     */
    private static final String STYLE_DEFPARENT = "Default";

    /**
     * Row style name.
     */
    static final String STYLE_ROW = "snRow";

    /**
     * Table style name.
     */
    static final String STYLE_TABLE = "snTable";

    /**
     * Hidden Table style name.
     */
    static final String STYLE_HIDDENTABLE = "snHiddenTable";

    /**
     * negative color (red).
     */
    private static final String COLOR_NEG = "#ff0000";

    /**
     * left align value.
     */
    private static final String ALIGN_LEFT = "left";

    /**
     * right align value.
     */
    private static final String ALIGN_RIGHT = "right";

    /**
     * centre align value.
     */
    private static final String ALIGN_CENTER = "center";

    /**
     * bold font value.
     */
    private static final String FONT_BOLD = "bold";

    /**
     * font size.
     */
    private static final String FONT_SIZE = PrometheusSheetFormats.FONT_HEIGHT
            + "pt";

    /**
     * Parser.
     */
    private final PrometheusOdfParser theParser;

    /**
     * Styles.
     */
    private final Element theStyles;

    /**
     * Style map.
     */
    private final Map<String, Element> theStyleMap;

    /**
     * Constructor.
     * @param pParser the parser.
     */
    PrometheusOdfStyler(final PrometheusOdfParser pParser) {
        /* Store parameters */
        theParser = pParser;
        theStyleMap = new HashMap<>();

        /* Access the styles */
        final Element myMain = theParser.getDocument().getDocumentElement();
        theStyles = theParser.getFirstNamedChild(myMain, PrometheusOdfOfficeItem.STYLES);

        /* Create standard styles */
        createStandardStyles();
    }

    /**
     * Obtain the required CellStyle.
     * @param pType the CellStyleType
     * @return the required CellStyle name
     */
    String getCellStyle(final PrometheusSheetCellStyleType pType) {
        /* Determine the correct format */
        final String myStyleName = PrometheusSheetFormats.getFormatName(pType);

        /* Look for existing format */
        Element myStyle = theStyleMap.get(myStyleName);

        /* If it has not yet been created */
        if (myStyle == null) {
            /* Create the Mew Cell Style */
            myStyle = theParser.newElement(PrometheusOdfStyleItem.STYLE);
            theParser.setAttribute(myStyle, PrometheusOdfStyleItem.FAMILY, PrometheusOdfTableItem.CELL.getName());
            theParser.setAttribute(myStyle, PrometheusOdfStyleItem.NAME, myStyleName);
            theParser.setAttribute(myStyle, PrometheusOdfStyleItem.PARENTSTYLE, STYLE_DEFPARENT);
            theStyles.appendChild(myStyle);

            /* Add text properties */
            Element myProperty = theParser.newElement(PrometheusOdfStyleItem.TEXTPROPS);
            myStyle.appendChild(myProperty);
            theParser.setAttribute(myProperty, PrometheusOdfStyleItem.FONTNAME, getStyleFont(pType));
            theParser.setAttribute(myProperty, PrometheusOdfStyleItem.FONTSIZE, FONT_SIZE);

            /* Add paragraph properties */
            myProperty = theParser.newElement(PrometheusOdfStyleItem.PARAGRAPHPROPS);
            myStyle.appendChild(myProperty);
            theParser.setAttribute(myProperty, PrometheusOdfStyleItem.TEXTALIGN, getStyleAlignment(pType));

            /* If we have a data format */
            if (PrometheusSheetFormats.hasDataFormat(pType)) {
                /* Determine the format */
                final Object myValue = PrometheusSheetFormats.getDefaultValue(pType);
                final String myFormatName = getDataStyleName(myStyleName);
                createNumericStyle(myFormatName, myValue, pType);
                theParser.setAttribute(myStyle, PrometheusOdfStyleItem.DATASTYLE, myFormatName);
            }

            /* Add to the map and return new style */
            theStyleMap.put(myStyleName, myStyle);
        }

        /* Return the styleName */
        return myStyleName;
    }

    /**
     * Obtain the required CellStyle.
     * @param pValue the Cell Value
     * @return the required CellStyle
     */
    String getCellStyle(final Object pValue) {
        /* Determine the correct format */
        final String myStyleName = PrometheusSheetFormats.getFormatName(pValue);

        /* Look for existing format */
        Element myStyle = theStyleMap.get(myStyleName);

        /* If it has not yet been created */
        if (myStyle == null) {
            /* Determine the CellStyleType */
            final PrometheusSheetCellStyleType myType = PrometheusSheetFormats.getCellStyleType(pValue);

            /* Create the New Cell Style */
            myStyle = theParser.newElement(PrometheusOdfStyleItem.STYLE);
            theParser.setAttribute(myStyle, PrometheusOdfStyleItem.FAMILY, PrometheusOdfTableItem.CELL.getName());
            theParser.setAttribute(myStyle, PrometheusOdfStyleItem.NAME, myStyleName);
            theParser.setAttribute(myStyle, PrometheusOdfStyleItem.PARENTSTYLE, STYLE_DEFPARENT);
            theStyles.appendChild(myStyle);

            /* Add text properties */
            Element myProperty = theParser.newElement(PrometheusOdfStyleItem.TEXTPROPS);
            myStyle.appendChild(myProperty);
            theParser.setAttribute(myProperty, PrometheusOdfStyleItem.FONTNAME, getStyleFont(myType));
            theParser.setAttribute(myProperty, PrometheusOdfStyleItem.FONTSIZE, FONT_SIZE);

            /* Add paragraph properties */
            myProperty = theParser.newElement(PrometheusOdfStyleItem.PARAGRAPHPROPS);
            myStyle.appendChild(myProperty);
            theParser.setAttribute(myProperty, PrometheusOdfStyleItem.TEXTALIGN, getStyleAlignment(myType));

            /* If we have a data format */
            if (PrometheusSheetFormats.hasDataFormat(myType)) {
                /* Determine the format */
                final String myFormatName = getDataStyleName(myStyleName);
                createNumericStyle(myFormatName, pValue, myType);
                theParser.setAttribute(myStyle, PrometheusOdfStyleItem.DATASTYLE, myFormatName);
            }

            /* Add to the map and return new style */
            theStyleMap.put(myStyleName, myStyle);
        }

        /* Return the styleName */
        return myStyleName;
    }

    /**
     * Obtain the required alternate CellStyle.
     * @param pValue the Cell Value
     * @return the required CellStyle
     */
    String getAlternateCellStyle(final Object pValue) {
        /* Determine the correct format */
        final String myStyleName = PrometheusSheetFormats.getAlternateFormatName(pValue);

        /* Look for existing format */
        Element myAltStyle = theStyleMap.get(myStyleName);

        /* If it has not yet been created */
        if (myAltStyle == null) {
            /* Determine the CellStyleType */
            PrometheusSheetCellStyleType myType = PrometheusSheetFormats.getCellStyleType(pValue);
            if (myType == PrometheusSheetCellStyleType.STRING) {
                myType = PrometheusSheetCellStyleType.HEADER;
            }

            /* Create the New Cell Style */
            myAltStyle = theParser.newElement(PrometheusOdfStyleItem.STYLE);
            theParser.setAttribute(myAltStyle, PrometheusOdfStyleItem.FAMILY, PrometheusOdfTableItem.CELL.getName());
            theParser.setAttribute(myAltStyle, PrometheusOdfStyleItem.NAME, myStyleName);
            theParser.setAttribute(myAltStyle, PrometheusOdfStyleItem.PARENTSTYLE, STYLE_DEFPARENT);
            theStyles.appendChild(myAltStyle);

            /* Add text properties */
            Element myProperty = theParser.newElement(PrometheusOdfStyleItem.TEXTPROPS);
            myAltStyle.appendChild(myProperty);
            theParser.setAttribute(myProperty, PrometheusOdfStyleItem.FONTNAME, getStyleFont(myType));
            theParser.setAttribute(myProperty, PrometheusOdfStyleItem.FONTSIZE, FONT_SIZE);
            if (myType == PrometheusSheetCellStyleType.HEADER) {
                theParser.setAttribute(myProperty, PrometheusOdfStyleItem.FONTWEIGHT, FONT_BOLD);
            }

            /* Add paragraph properties */
            myProperty = theParser.newElement(PrometheusOdfStyleItem.PARAGRAPHPROPS);
            myAltStyle.appendChild(myProperty);
            theParser.setAttribute(myProperty, PrometheusOdfStyleItem.TEXTALIGN, getStyleAlignment(myType));

            /* Add protection for headers */
            if (myType == PrometheusSheetCellStyleType.HEADER) {
                myProperty = theParser.newElement(PrometheusOdfStyleItem.CELLPROPS);
                myAltStyle.appendChild(myProperty);
                theParser.setAttribute(myProperty, PrometheusOdfStyleItem.PROTECT, Boolean.TRUE);
            }

            /* If we have a data format */
            if (PrometheusSheetFormats.hasDataFormat(myType)) {
                /* Determine the format */
                final String myFormatName = getDataStyleName(myStyleName);
                createNumericStyle(myFormatName, pValue, myType);
                theParser.setAttribute(myAltStyle, PrometheusOdfStyleItem.DATASTYLE, myFormatName);
            }

            /* Add to the map and return new style */
            theStyleMap.put(myStyleName, myAltStyle);
        }

        /* Return the styleName */
        return myStyleName;
    }

    /**
     * Obtain data style name.
     * @param pStyle the style type
     * @return the name of the style
     */
    private static String getDataStyleName(final String pStyle) {
        return "d"
                + pStyle;
    }

    /**
     * Obtain column style name.
     * @param pType the style type
     * @return the name of the style
     */
    static String getColumnStyleName(final PrometheusSheetCellStyleType pType) {
        return "csn"
                + pType.name();
    }

    /**
     * Obtain column width.
     * @param pWidth the character width
     * @return the name of the style
     */
    private static String getStyleWidth(final int pWidth) {
        return (pWidth << 1)
                + "mm";
    }

    /**
     * Define a numeric CellStyle.
     * @param pStyleName the style name
     * @param pValue the value
     * @param pType the style type
     */
    private void createNumericStyle(final String pStyleName,
                                    final Object pValue,
                                    final PrometheusSheetCellStyleType pType) {
        /* Switch on type */
        switch (pType) {
            case BOOLEAN:
                createBooleanStyle(pStyleName);
                break;
            case DATE:
                createDateStyle(pStyleName);
                break;
            case MONEY:
            case PRICE:
                createMoneyStyle(pStyleName, (OceanusMoney) pValue);
                break;
            case INTEGER:
                createIntegerStyle(pStyleName);
                break;
            case RATE:
                createRateStyle(pStyleName, (OceanusRate) pValue);
                break;
            case UNITS:
            case RATIO:
            default:
                createDecimalStyle(pStyleName, (OceanusDecimal) pValue);
                break;
        }
    }

    /**
     * Define a Date Style.
     * @param pStyleName the style name
     */
    private void createDateStyle(final String pStyleName) {
        /* Create the Date Cell Style */
        final Element myStyle = theParser.newElement(PrometheusOdfNumberItem.DATESTYLE);
        theParser.setAttribute(myStyle, PrometheusOdfStyleItem.NAME, pStyleName);
        theStyles.appendChild(myStyle);

        /* Define the day properties */
        final Element myDay = theParser.newElement(PrometheusOdfNumberItem.DAY);
        theParser.setAttribute(myDay, PrometheusOdfNumberItem.STYLE, "long");
        myStyle.appendChild(myDay);

        /* Define the separator properties */
        Element mySep = theParser.newElement(PrometheusOdfNumberItem.TEXT);
        mySep.setTextContent("-");
        myStyle.appendChild(mySep);

        /* Define the month properties */
        final Element myMonth = theParser.newElement(PrometheusOdfNumberItem.MONTH);
        theParser.setAttribute(myMonth, PrometheusOdfNumberItem.STYLE, "short");
        theParser.setAttribute(myMonth, PrometheusOdfNumberItem.TEXTUAL, Boolean.TRUE);
        myStyle.appendChild(myMonth);

        /* Define the separator properties */
        mySep = theParser.newElement(PrometheusOdfNumberItem.TEXT);
        mySep.setTextContent("-");
        myStyle.appendChild(mySep);

        /* Define the year properties */
        final Element myYear = theParser.newElement(PrometheusOdfNumberItem.YEAR);
        theParser.setAttribute(myYear, PrometheusOdfNumberItem.STYLE, "short");
        myStyle.appendChild(myYear);
    }

    /**
     * Define a Boolean Style.
     * @param pStyleName the style name
     */
    private void createBooleanStyle(final String pStyleName) {
        /* Create the Boolean Cell Style */
        final Element myStyle = theParser.newElement(PrometheusOdfNumberItem.BOOLEANSTYLE);
        theParser.setAttribute(myStyle, PrometheusOdfStyleItem.NAME, pStyleName);
        theStyles.appendChild(myStyle);

        /* Define the number properties */
        final Element myNumber = theParser.newElement(PrometheusOdfNumberItem.BOOLEANVALUE);
        myStyle.appendChild(myNumber);

        /* Define the boolean properties */
        final Element myBoolean = theParser.newElement(PrometheusOdfNumberItem.BOOLEAN);
        myNumber.appendChild(myBoolean);
    }

    /**
     * Define an Integer Style.
     * @param pStyleName the style name
     */
    private void createIntegerStyle(final String pStyleName) {
        /* Create the Integer Cell Style */
        final Element myStyle = theParser.newElement(PrometheusOdfNumberItem.NUMBERSTYLE);
        theParser.setAttribute(myStyle, PrometheusOdfStyleItem.NAME, pStyleName);
        theStyles.appendChild(myStyle);

        /* Define the number properties */
        final Element myNumber = theParser.newElement(PrometheusOdfNumberItem.NUMBER);
        myStyle.appendChild(myNumber);
        theParser.setAttribute(myNumber, PrometheusOdfNumberItem.DECPLACES, 0);
        theParser.setAttribute(myNumber, PrometheusOdfNumberItem.MININTDIGITS, 1);
    }

    /**
     * Define a Decimal Style.
     * @param pStyleName the style name
     * @param pValue the value
     */
    private void createDecimalStyle(final String pStyleName,
                                    final OceanusDecimal pValue) {
        /* Create the Ratio Cell Style */
        final Element myStyle = theParser.newElement(PrometheusOdfNumberItem.NUMBERSTYLE);
        theParser.setAttribute(myStyle, PrometheusOdfStyleItem.NAME, pStyleName);
        theStyles.appendChild(myStyle);

        /* Define the number properties */
        final Element myNumber = theParser.newElement(PrometheusOdfNumberItem.NUMBER);
        myStyle.appendChild(myNumber);
        final int myDecimals = pValue.scale();
        theParser.setAttribute(myNumber, PrometheusOdfNumberItem.DECPLACES, myDecimals);
        theParser.setAttribute(myNumber, PrometheusOdfNumberItem.MININTDIGITS, 1);
    }

    /**
     * Define a rateStyle.
     * @param pStyleName the style name
     * @param pValue the value
     */
    private void createRateStyle(final String pStyleName,
                                 final OceanusRate pValue) {
        /* Create the Rate Cell Style */
        final Element myStyle = theParser.newElement(PrometheusOdfNumberItem.PERCENTAGESTYLE);
        theParser.setAttribute(myStyle, PrometheusOdfStyleItem.NAME, pStyleName);
        theStyles.appendChild(myStyle);

        /* Define the number properties */
        final Element myNumber = theParser.newElement(PrometheusOdfNumberItem.NUMBER);
        myStyle.appendChild(myNumber);
        final int myDecimals = pValue.scale() - OceanusDecimalParser.ADJUST_PERCENT;
        theParser.setAttribute(myNumber, PrometheusOdfNumberItem.DECPLACES, myDecimals);
        theParser.setAttribute(myNumber, PrometheusOdfNumberItem.MININTDIGITS, 1);

        /* Define the text properties */
        final Element myText = theParser.newElement(PrometheusOdfNumberItem.TEXT);
        myStyle.appendChild(myText);
        myText.setTextContent("%");
    }

    /**
     * Define a priceStyle.
     * @param pStyleName the style name
     * @param pValue the value
     */
    private void createMoneyStyle(final String pStyleName,
                                  final OceanusMoney pValue) {
        /* Create the Price Cell Style */
        final Element myStyle = theParser.newElement(PrometheusOdfNumberItem.NUMBERSTYLE);
        theParser.setAttribute(myStyle, PrometheusOdfStyleItem.NAME, pStyleName);
        theStyles.appendChild(myStyle);

        /* Define the text properties */
        final Element myText = theParser.newElement(PrometheusOdfNumberItem.TEXT);
        myText.setTextContent(pValue.getCurrency().getSymbol() + " -");
        myStyle.appendChild(myText);

        /* Define the negative map */
        Element myMap = theParser.newElement(PrometheusOdfStyleItem.MAP);
        myStyle.appendChild(myMap);
        String myName = createCurrencyStyle(pStyleName, pValue, true);
        theParser.setAttribute(myMap, PrometheusOdfStyleItem.APPLYSTYLE, myName);
        theParser.setAttribute(myMap, PrometheusOdfStyleItem.CONDITION, "value()<0");

        /* Define the negative map */
        myMap = theParser.newElement(PrometheusOdfStyleItem.MAP);
        myStyle.appendChild(myMap);
        myName = createCurrencyStyle(pStyleName, pValue, false);
        theParser.setAttribute(myMap, PrometheusOdfStyleItem.APPLYSTYLE, myName);
        theParser.setAttribute(myMap, PrometheusOdfStyleItem.CONDITION, "value()>0");
    }

    /**
     * Define a currency subStyle.
     * @param pStyleName the style name
     * @param pValue the value
     * @param pNegative is this the negative form
     * @return the
     */
    private String createCurrencyStyle(final String pStyleName,
                                       final OceanusMoney pValue,
                                        final boolean pNegative) {
        /* Determine the prefix */
        final String myPrefix = pNegative ? "n" : "p";
        final String myName = myPrefix + pStyleName;

        /* Create the Price Cell Style */
        final Element myStyle = theParser.newElement(PrometheusOdfNumberItem.NUMBERSTYLE);
        theParser.setAttribute(myStyle, PrometheusOdfStyleItem.NAME, myName);
        theStyles.appendChild(myStyle);

        /* If this is a negative value */
        if (pNegative) {
            /* Make the format red */
            final Element myProperties = theParser.newElement(PrometheusOdfStyleItem.TEXTPROPS);
            theParser.setAttribute(myProperties, PrometheusOdfStyleItem.COLOR, COLOR_NEG);
            myStyle.appendChild(myProperties);
        }

        /* Define the text properties */
        final Element myText = theParser.newElement(PrometheusOdfNumberItem.TEXT);
        myText.setTextContent(pValue.getCurrency().getSymbol());
        myStyle.appendChild(myText);

        /* Define the number properties */
        final Element myNumber = theParser.newElement(PrometheusOdfNumberItem.NUMBER);
        myStyle.appendChild(myNumber);
        final int myDecimals = pValue.scale();
        theParser.setAttribute(myNumber, PrometheusOdfNumberItem.DECPLACES, myDecimals);
        theParser.setAttribute(myNumber, PrometheusOdfNumberItem.MININTDIGITS, 1);
        theParser.setAttribute(myNumber, PrometheusOdfNumberItem.GROUPING, Boolean.TRUE);

        /* Return the name */
        return myName;
    }

    /**
     * Create the standard Styles.
     */
    private void createStandardStyles() {
        /* Create the Date Column Style */
        Element myStyle = theParser.newElement(PrometheusOdfStyleItem.STYLE);
        theParser.setAttribute(myStyle, PrometheusOdfStyleItem.FAMILY, PrometheusOdfTableItem.COLUMN.getName());
        String myName = getColumnStyleName(PrometheusSheetCellStyleType.DATE);
        theParser.setAttribute(myStyle, PrometheusOdfStyleItem.NAME, myName);
        Element myProperty = theParser.newElement(PrometheusOdfStyleItem.COLUMNPROPS);
        myStyle.appendChild(myProperty);
        theParser.setAttribute(myProperty, PrometheusOdfStyleItem.COLUMNWIDTH, getStyleWidth(PrometheusSheetFormats.WIDTH_DATE));
        theStyles.appendChild(myStyle);

        /* Create the Money Column Style */
        myStyle = theParser.newElement(PrometheusOdfStyleItem.STYLE);
        theParser.setAttribute(myStyle, PrometheusOdfStyleItem.FAMILY, PrometheusOdfTableItem.COLUMN.getName());
        myName = getColumnStyleName(PrometheusSheetCellStyleType.MONEY);
        theParser.setAttribute(myStyle, PrometheusOdfStyleItem.NAME, myName);
        myProperty = theParser.newElement(PrometheusOdfStyleItem.COLUMNPROPS);
        myStyle.appendChild(myProperty);
        theParser.setAttribute(myProperty, PrometheusOdfStyleItem.COLUMNWIDTH, getStyleWidth(PrometheusSheetFormats.WIDTH_MONEY));
        theStyles.appendChild(myStyle);

        /* Create the Price Column Style */
        myStyle = theParser.newElement(PrometheusOdfStyleItem.STYLE);
        theParser.setAttribute(myStyle, PrometheusOdfStyleItem.FAMILY, PrometheusOdfTableItem.COLUMN.getName());
        myName = getColumnStyleName(PrometheusSheetCellStyleType.PRICE);
        theParser.setAttribute(myStyle, PrometheusOdfStyleItem.NAME, myName);
        myProperty = theParser.newElement(PrometheusOdfStyleItem.COLUMNPROPS);
        myStyle.appendChild(myProperty);
        theParser.setAttribute(myProperty, PrometheusOdfStyleItem.COLUMNWIDTH, getStyleWidth(PrometheusSheetFormats.WIDTH_PRICE));

        /* Create the Units Column Style */
        myStyle = theParser.newElement(PrometheusOdfStyleItem.STYLE);
        theParser.setAttribute(myStyle, PrometheusOdfStyleItem.FAMILY, PrometheusOdfTableItem.COLUMN.getName());
        myName = getColumnStyleName(PrometheusSheetCellStyleType.UNITS);
        theParser.setAttribute(myStyle, PrometheusOdfStyleItem.NAME, myName);
        myProperty = theParser.newElement(PrometheusOdfStyleItem.COLUMNPROPS);
        myStyle.appendChild(myProperty);
        theParser.setAttribute(myProperty, PrometheusOdfStyleItem.COLUMNWIDTH, getStyleWidth(PrometheusSheetFormats.WIDTH_UNITS));
        theStyles.appendChild(myStyle);

        /* Create the Rate Column Style */
        myStyle = theParser.newElement(PrometheusOdfStyleItem.STYLE);
        theParser.setAttribute(myStyle, PrometheusOdfStyleItem.FAMILY, PrometheusOdfTableItem.COLUMN.getName());
        myName = getColumnStyleName(PrometheusSheetCellStyleType.RATE);
        theParser.setAttribute(myStyle, PrometheusOdfStyleItem.NAME, myName);
        myProperty = theParser.newElement(PrometheusOdfStyleItem.COLUMNPROPS);
        myStyle.appendChild(myProperty);
        theParser.setAttribute(myProperty, PrometheusOdfStyleItem.COLUMNWIDTH, getStyleWidth(PrometheusSheetFormats.WIDTH_RATE));
        theStyles.appendChild(myStyle);

        /* Create the Ratio Column Style */
        myStyle = theParser.newElement(PrometheusOdfStyleItem.STYLE);
        theParser.setAttribute(myStyle, PrometheusOdfStyleItem.FAMILY, PrometheusOdfTableItem.COLUMN.getName());
        myName = getColumnStyleName(PrometheusSheetCellStyleType.RATIO);
        theParser.setAttribute(myStyle, PrometheusOdfStyleItem.NAME, myName);
        myProperty = theParser.newElement(PrometheusOdfStyleItem.COLUMNPROPS);
        myStyle.appendChild(myProperty);
        theParser.setAttribute(myProperty, PrometheusOdfStyleItem.COLUMNWIDTH, getStyleWidth(PrometheusSheetFormats.WIDTH_RATIO));
        theStyles.appendChild(myStyle);

        /* Create the Integer Column Style */
        myStyle = theParser.newElement(PrometheusOdfStyleItem.STYLE);
        theParser.setAttribute(myStyle, PrometheusOdfStyleItem.FAMILY, PrometheusOdfTableItem.COLUMN.getName());
        myName = getColumnStyleName(PrometheusSheetCellStyleType.INTEGER);
        theParser.setAttribute(myStyle, PrometheusOdfStyleItem.NAME, myName);
        myProperty = theParser.newElement(PrometheusOdfStyleItem.COLUMNPROPS);
        myStyle.appendChild(myProperty);
        theParser.setAttribute(myProperty, PrometheusOdfStyleItem.COLUMNWIDTH, getStyleWidth(PrometheusSheetFormats.WIDTH_INT));
        theStyles.appendChild(myStyle);

        /* Create the Boolean Column Style */
        myStyle = theParser.newElement(PrometheusOdfStyleItem.STYLE);
        theParser.setAttribute(myStyle, PrometheusOdfStyleItem.FAMILY, PrometheusOdfTableItem.COLUMN.getName());
        myName = getColumnStyleName(PrometheusSheetCellStyleType.BOOLEAN);
        theParser.setAttribute(myStyle, PrometheusOdfStyleItem.NAME, myName);
        myProperty = theParser.newElement(PrometheusOdfStyleItem.COLUMNPROPS);
        myStyle.appendChild(myProperty);
        theParser.setAttribute(myProperty, PrometheusOdfStyleItem.COLUMNWIDTH, getStyleWidth(PrometheusSheetFormats.WIDTH_BOOL));
        theStyles.appendChild(myStyle);

        /* Create the String Column Style */
        myStyle = theParser.newElement(PrometheusOdfStyleItem.STYLE);
        theParser.setAttribute(myStyle, PrometheusOdfStyleItem.FAMILY, PrometheusOdfTableItem.COLUMN.getName());
        myName = getColumnStyleName(PrometheusSheetCellStyleType.STRING);
        theParser.setAttribute(myStyle, PrometheusOdfStyleItem.NAME, myName);
        myProperty = theParser.newElement(PrometheusOdfStyleItem.COLUMNPROPS);
        myStyle.appendChild(myProperty);
        theParser.setAttribute(myProperty, PrometheusOdfStyleItem.COLUMNWIDTH, getStyleWidth(PrometheusSheetFormats.WIDTH_STRING));
        theStyles.appendChild(myStyle);

        /* Create the Table Style */
        myStyle = theParser.newElement(PrometheusOdfStyleItem.STYLE);
        theParser.setAttribute(myStyle, PrometheusOdfStyleItem.FAMILY, PrometheusOdfTableItem.TABLE.getName());
        theParser.setAttribute(myStyle, PrometheusOdfStyleItem.NAME, STYLE_TABLE);
        myProperty = theParser.newElement(PrometheusOdfStyleItem.TABLEPROPS);
        myStyle.appendChild(myProperty);
        theParser.setAttribute(myProperty, PrometheusOdfStyleItem.DISPLAY, Boolean.TRUE);
        theStyles.appendChild(myStyle);

        /* Create the Hidden Table Style */
        myStyle = theParser.newElement(PrometheusOdfStyleItem.STYLE);
        theParser.setAttribute(myStyle, PrometheusOdfStyleItem.FAMILY, PrometheusOdfTableItem.TABLE.getName());
        theParser.setAttribute(myStyle, PrometheusOdfStyleItem.NAME, STYLE_HIDDENTABLE);
        myProperty = theParser.newElement(PrometheusOdfStyleItem.TABLEPROPS);
        myStyle.appendChild(myProperty);
        theParser.setAttribute(myProperty, PrometheusOdfStyleItem.DISPLAY, Boolean.FALSE);
        theStyles.appendChild(myStyle);

        /* Create the Row Style */
        myStyle = theParser.newElement(PrometheusOdfStyleItem.STYLE);
        theParser.setAttribute(myStyle, PrometheusOdfStyleItem.FAMILY, PrometheusOdfTableItem.ROW.getName());
        theParser.setAttribute(myStyle, PrometheusOdfStyleItem.NAME, STYLE_ROW);
        myProperty = theParser.newElement(PrometheusOdfStyleItem.ROWPROPS);
        myStyle.appendChild(myProperty);
        theParser.setAttribute(myProperty, PrometheusOdfStyleItem.ROWHEIGHT, "5mm");
        theStyles.appendChild(myStyle);
    }

    /**
     * Obtain alignment for a cell.
     * @param pType the cell type
     * @return the alignment
     */
    private static String getStyleAlignment(final PrometheusSheetCellStyleType pType) {
        switch (pType) {
            case HEADER:
            case BOOLEAN:
                return ALIGN_CENTER;
            case DATE:
            case STRING:
                return ALIGN_LEFT;
            default:
                return ALIGN_RIGHT;
        }
    }

    /**
     * Obtain font for a cell.
     * @param pType the cell type
     * @return the font
     */
    private static String getStyleFont(final PrometheusSheetCellStyleType pType) {
        switch (pType) {
            case HEADER:
            case BOOLEAN:
            case STRING:
                return PrometheusSheetFormats.FONT_VALUE;
            default:
                return PrometheusSheetFormats.FONT_NUMERIC;
        }
    }
}
