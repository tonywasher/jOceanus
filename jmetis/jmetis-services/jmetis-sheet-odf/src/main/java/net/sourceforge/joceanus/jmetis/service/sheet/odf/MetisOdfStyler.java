/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012, 2018 Tony Washer
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
package net.sourceforge.joceanus.jmetis.service.sheet.odf;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Element;

import net.sourceforge.joceanus.jmetis.service.sheet.MetisSheetCellStyleType;
import net.sourceforge.joceanus.jmetis.service.sheet.MetisSheetFormats;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimalParser;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;

/**
 * Odf Styles.
 */
public class MetisOdfStyler {
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
    private static final String FONT_SIZE = MetisSheetFormats.FONT_HEIGHT
            + "pt";

    /**
     * Parser.
     */
    private final MetisOdfParser theParser;

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
    MetisOdfStyler(final MetisOdfParser pParser) {
        /* Store parameters */
        theParser = pParser;
        theStyleMap = new HashMap<>();

        /* Access the styles */
        final Element myMain = theParser.getDocument().getDocumentElement();
        theStyles = theParser.getFirstNamedChild(myMain, MetisOdfOfficeItem.STYLES);

        /* Create standard styles */
        createStandardStyles();
    }

    /**
     * Obtain the required CellStyle.
     * @param pType the CellStyleType
     * @return the required CellStyle name
     */
    String getCellStyle(final MetisSheetCellStyleType pType) {
        /* Determine the correct format */
        final String myStyleName = MetisSheetFormats.getFormatName(pType);

        /* Look for existing format */
        Element myStyle = theStyleMap.get(myStyleName);

        /* If it has not yet been created */
        if (myStyle == null) {
            /* Create the Mew Cell Style */
            myStyle = theParser.newElement(MetisOdfStyleItem.STYLE);
            theParser.setAttribute(myStyle, MetisOdfStyleItem.FAMILY, MetisOdfTableItem.CELL.getName());
            theParser.setAttribute(myStyle, MetisOdfStyleItem.NAME, myStyleName);
            theParser.setAttribute(myStyle, MetisOdfStyleItem.PARENTSTYLE, STYLE_DEFPARENT);
            theStyles.appendChild(myStyle);

            /* Add text properties */
            Element myProperty = theParser.newElement(MetisOdfStyleItem.TEXTPROPS);
            myStyle.appendChild(myProperty);
            theParser.setAttribute(myProperty, MetisOdfStyleItem.FONTNAME, getStyleFont(pType));
            theParser.setAttribute(myProperty, MetisOdfStyleItem.FONTSIZE, FONT_SIZE);

            /* Add paragraph properties */
            myProperty = theParser.newElement(MetisOdfStyleItem.PARAGRAPHPROPS);
            myStyle.appendChild(myProperty);
            theParser.setAttribute(myProperty, MetisOdfStyleItem.TEXTALIGN, getStyleAlignment(pType));

            /* If we have a data format */
            if (MetisSheetFormats.hasDataFormat(pType)) {
                /* Determine the format */
                final Object myValue = MetisSheetFormats.getDefaultValue(pType);
                final String myFormatName = getDataStyleName(myStyleName);
                createNumericStyle(myFormatName, myValue, pType);
                theParser.setAttribute(myStyle, MetisOdfStyleItem.DATASTYLE, myFormatName);
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
        final String myStyleName = MetisSheetFormats.getFormatName(pValue);

        /* Look for existing format */
        Element myStyle = theStyleMap.get(myStyleName);

        /* If it has not yet been created */
        if (myStyle == null) {
            /* Determine the CellStyleType */
            final MetisSheetCellStyleType myType = MetisSheetFormats.getCellStyleType(pValue);

            /* Create the New Cell Style */
            myStyle = theParser.newElement(MetisOdfStyleItem.STYLE);
            theParser.setAttribute(myStyle, MetisOdfStyleItem.FAMILY, MetisOdfTableItem.CELL.getName());
            theParser.setAttribute(myStyle, MetisOdfStyleItem.NAME, myStyleName);
            theParser.setAttribute(myStyle, MetisOdfStyleItem.PARENTSTYLE, STYLE_DEFPARENT);
            theStyles.appendChild(myStyle);

            /* Add text properties */
            Element myProperty = theParser.newElement(MetisOdfStyleItem.TEXTPROPS);
            myStyle.appendChild(myProperty);
            theParser.setAttribute(myProperty, MetisOdfStyleItem.FONTNAME, getStyleFont(myType));
            theParser.setAttribute(myProperty, MetisOdfStyleItem.FONTSIZE, FONT_SIZE);

            /* Add paragraph properties */
            myProperty = theParser.newElement(MetisOdfStyleItem.PARAGRAPHPROPS);
            myStyle.appendChild(myProperty);
            theParser.setAttribute(myProperty, MetisOdfStyleItem.TEXTALIGN, getStyleAlignment(myType));

            /* If we have a data format */
            if (MetisSheetFormats.hasDataFormat(myType)) {
                /* Determine the format */
                final String myFormatName = getDataStyleName(myStyleName);
                createNumericStyle(myFormatName, pValue, myType);
                theParser.setAttribute(myStyle, MetisOdfStyleItem.DATASTYLE, myFormatName);
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
        final String myStyleName = MetisSheetFormats.getAlternateFormatName(pValue);

        /* Look for existing format */
        Element myAltStyle = theStyleMap.get(myStyleName);

        /* If it has not yet been created */
        if (myAltStyle == null) {
            /* Determine the CellStyleType */
            MetisSheetCellStyleType myType = MetisSheetFormats.getCellStyleType(pValue);
            if (myType == MetisSheetCellStyleType.STRING) {
                myType = MetisSheetCellStyleType.HEADER;
            }

            /* Create the New Cell Style */
            myAltStyle = theParser.newElement(MetisOdfStyleItem.STYLE);
            theParser.setAttribute(myAltStyle, MetisOdfStyleItem.FAMILY, MetisOdfTableItem.CELL.getName());
            theParser.setAttribute(myAltStyle, MetisOdfStyleItem.NAME, myStyleName);
            theParser.setAttribute(myAltStyle, MetisOdfStyleItem.PARENTSTYLE, STYLE_DEFPARENT);
            theStyles.appendChild(myAltStyle);

            /* Add text properties */
            Element myProperty = theParser.newElement(MetisOdfStyleItem.TEXTPROPS);
            myAltStyle.appendChild(myProperty);
            theParser.setAttribute(myProperty, MetisOdfStyleItem.FONTNAME, getStyleFont(myType));
            theParser.setAttribute(myProperty, MetisOdfStyleItem.FONTSIZE, FONT_SIZE);
            if (myType == MetisSheetCellStyleType.HEADER) {
                theParser.setAttribute(myProperty, MetisOdfStyleItem.FONTWEIGHT, FONT_BOLD);
            }

            /* Add paragraph properties */
            myProperty = theParser.newElement(MetisOdfStyleItem.PARAGRAPHPROPS);
            myAltStyle.appendChild(myProperty);
            theParser.setAttribute(myProperty, MetisOdfStyleItem.TEXTALIGN, getStyleAlignment(myType));

            /* Add protection for headers */
            if (myType == MetisSheetCellStyleType.HEADER) {
                myProperty = theParser.newElement(MetisOdfStyleItem.CELLPROPS);
                myAltStyle.appendChild(myProperty);
                theParser.setAttribute(myProperty, MetisOdfStyleItem.PROTECT, Boolean.TRUE);
            }

            /* If we have a data format */
            if (MetisSheetFormats.hasDataFormat(myType)) {
                /* Determine the format */
                final String myFormatName = getDataStyleName(myStyleName);
                createNumericStyle(myFormatName, pValue, myType);
                theParser.setAttribute(myAltStyle, MetisOdfStyleItem.DATASTYLE, myFormatName);
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
    static String getColumnStyleName(final MetisSheetCellStyleType pType) {
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
                                    final MetisSheetCellStyleType pType) {
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
                createMoneyStyle(pStyleName, (TethysMoney) pValue);
                break;
            case INTEGER:
                createIntegerStyle(pStyleName);
                break;
            case RATE:
                createRateStyle(pStyleName, (TethysRate) pValue);
                break;
            case UNITS:
            case RATIO:
            case DILUTION:
            default:
                createDecimalStyle(pStyleName, (TethysDecimal) pValue);
                break;
        }
    }

    /**
     * Define a Date Style.
     * @param pStyleName the style name
     */
    private void createDateStyle(final String pStyleName) {
        /* Create the Date Cell Style */
        final Element myStyle = theParser.newElement(MetisOdfNumberItem.DATESTYLE);
        theParser.setAttribute(myStyle, MetisOdfStyleItem.NAME, pStyleName);
        theStyles.appendChild(myStyle);

        /* Define the day properties */
        final Element myDay = theParser.newElement(MetisOdfNumberItem.DAY);
        theParser.setAttribute(myDay, MetisOdfNumberItem.STYLE, "long");
        myStyle.appendChild(myDay);

        /* Define the separator properties */
        Element mySep = theParser.newElement(MetisOdfNumberItem.TEXT);
        mySep.setTextContent("-");
        myStyle.appendChild(mySep);

        /* Define the month properties */
        final Element myMonth = theParser.newElement(MetisOdfNumberItem.MONTH);
        theParser.setAttribute(myMonth, MetisOdfNumberItem.STYLE, "short");
        theParser.setAttribute(myMonth, MetisOdfNumberItem.TEXTUAL, Boolean.TRUE);
        myStyle.appendChild(myMonth);

        /* Define the separator properties */
        mySep = theParser.newElement(MetisOdfNumberItem.TEXT);
        mySep.setTextContent("-");
        myStyle.appendChild(mySep);

        /* Define the year properties */
        final Element myYear = theParser.newElement(MetisOdfNumberItem.YEAR);
        theParser.setAttribute(myYear, MetisOdfNumberItem.STYLE, "short");
        myStyle.appendChild(myYear);
    }

    /**
     * Define a Boolean Style.
     * @param pStyleName the style name
     */
    private void createBooleanStyle(final String pStyleName) {
        /* Create the Boolean Cell Style */
        final Element myStyle = theParser.newElement(MetisOdfNumberItem.BOOLEANSTYLE);
        theParser.setAttribute(myStyle, MetisOdfStyleItem.NAME, pStyleName);
        theStyles.appendChild(myStyle);

        /* Define the number properties */
        final Element myNumber = theParser.newElement(MetisOdfNumberItem.BOOLEANVALUE);
        myStyle.appendChild(myNumber);

        /* Define the boolean properties */
        final Element myBoolean = theParser.newElement(MetisOdfNumberItem.BOOLEAN);
        myNumber.appendChild(myBoolean);
    }

    /**
     * Define an Integer Style.
     * @param pStyleName the style name
     */
    private void createIntegerStyle(final String pStyleName) {
        /* Create the Integer Cell Style */
        final Element myStyle = theParser.newElement(MetisOdfNumberItem.NUMBERSTYLE);
        theParser.setAttribute(myStyle, MetisOdfStyleItem.NAME, pStyleName);
        theStyles.appendChild(myStyle);

        /* Define the number properties */
        final Element myNumber = theParser.newElement(MetisOdfNumberItem.NUMBER);
        myStyle.appendChild(myNumber);
        theParser.setAttribute(myNumber, MetisOdfNumberItem.DECPLACES, 0);
        theParser.setAttribute(myNumber, MetisOdfNumberItem.MININTDIGITS, 1);
    }

    /**
     * Define a Decimal Style.
     * @param pStyleName the style name
     * @param pValue the value
     */
    private void createDecimalStyle(final String pStyleName,
                                    final TethysDecimal pValue) {
        /* Create the Ratio Cell Style */
        final Element myStyle = theParser.newElement(MetisOdfNumberItem.NUMBERSTYLE);
        theParser.setAttribute(myStyle, MetisOdfStyleItem.NAME, pStyleName);
        theStyles.appendChild(myStyle);

        /* Define the number properties */
        final Element myNumber = theParser.newElement(MetisOdfNumberItem.NUMBER);
        myStyle.appendChild(myNumber);
        final int myDecimals = pValue.scale();
        theParser.setAttribute(myNumber, MetisOdfNumberItem.DECPLACES, myDecimals);
        theParser.setAttribute(myNumber, MetisOdfNumberItem.MININTDIGITS, 1);
    }

    /**
     * Define a rateStyle.
     * @param pStyleName the style name
     * @param pValue the value
     */
    private void createRateStyle(final String pStyleName,
                                 final TethysRate pValue) {
        /* Create the Rate Cell Style */
        final Element myStyle = theParser.newElement(MetisOdfNumberItem.PERCENTAGESTYLE);
        theParser.setAttribute(myStyle, MetisOdfStyleItem.NAME, pStyleName);
        theStyles.appendChild(myStyle);

        /* Define the number properties */
        final Element myNumber = theParser.newElement(MetisOdfNumberItem.NUMBER);
        myStyle.appendChild(myNumber);
        final int myDecimals = pValue.scale() - TethysDecimalParser.ADJUST_PERCENT;
        theParser.setAttribute(myNumber, MetisOdfNumberItem.DECPLACES, myDecimals);
        theParser.setAttribute(myNumber, MetisOdfNumberItem.MININTDIGITS, 1);

        /* Define the text properties */
        final Element myText = theParser.newElement(MetisOdfNumberItem.TEXT);
        myStyle.appendChild(myText);
        myText.setTextContent("%");
    }

    /**
     * Define a priceStyle.
     * @param pStyleName the style name
     * @param pValue the value
     */
    private void createMoneyStyle(final String pStyleName,
                                  final TethysMoney pValue) {
        /* Create the Price Cell Style */
        final Element myStyle = theParser.newElement(MetisOdfNumberItem.NUMBERSTYLE);
        theParser.setAttribute(myStyle, MetisOdfStyleItem.NAME, pStyleName);
        theStyles.appendChild(myStyle);

        /* Define the text properties */
        final Element myText = theParser.newElement(MetisOdfNumberItem.TEXT);
        myText.setTextContent(pValue.getCurrency().getSymbol() + " -");
        myStyle.appendChild(myText);

        /* Define the negative map */
        Element myMap = theParser.newElement(MetisOdfStyleItem.MAP);
        myStyle.appendChild(myMap);
        String myName = createCurrencyStyle(pStyleName, pValue, true);
        theParser.setAttribute(myMap, MetisOdfStyleItem.APPLYSTYLE, myName);
        theParser.setAttribute(myMap, MetisOdfStyleItem.CONDITION, "value()<0");

        /* Define the negative map */
        myMap = theParser.newElement(MetisOdfStyleItem.MAP);
        myStyle.appendChild(myMap);
        myName = createCurrencyStyle(pStyleName, pValue, false);
        theParser.setAttribute(myMap, MetisOdfStyleItem.APPLYSTYLE, myName);
        theParser.setAttribute(myMap, MetisOdfStyleItem.CONDITION, "value()>0");
    }

    /**
     * Define a currency subStyle.
     * @param pStyleName the style name
     * @param pValue the value
     * @param pNegative is this the negative form
     * @return the
     */
    private String createCurrencyStyle(final String pStyleName,
                                       final TethysMoney pValue,
                                        final boolean pNegative) {
        /* Determine the prefix */
        final String myPrefix = pNegative ? "n" : "p";
        final String myName = myPrefix + pStyleName;

        /* Create the Price Cell Style */
        final Element myStyle = theParser.newElement(MetisOdfNumberItem.NUMBERSTYLE);
        theParser.setAttribute(myStyle, MetisOdfStyleItem.NAME, myName);
        theStyles.appendChild(myStyle);

        /* If this is a negative value */
        if (pNegative) {
            /* Make the format red */
            final Element myProperties = theParser.newElement(MetisOdfStyleItem.TEXTPROPS);
            theParser.setAttribute(myProperties, MetisOdfStyleItem.COLOR, COLOR_NEG);
            myStyle.appendChild(myProperties);
        }

        /* Define the text properties */
        final Element myText = theParser.newElement(MetisOdfNumberItem.TEXT);
        myText.setTextContent(pValue.getCurrency().getSymbol());
        myStyle.appendChild(myText);

        /* Define the number properties */
        final Element myNumber = theParser.newElement(MetisOdfNumberItem.NUMBER);
        myStyle.appendChild(myNumber);
        final int myDecimals = pValue.scale();
        theParser.setAttribute(myNumber, MetisOdfNumberItem.DECPLACES, myDecimals);
        theParser.setAttribute(myNumber, MetisOdfNumberItem.MININTDIGITS, 1);
        theParser.setAttribute(myNumber, MetisOdfNumberItem.GROUPING, Boolean.TRUE);

        /* Return the name */
        return myName;
    }

    /**
     * Create the standard Styles.
     */
    private void createStandardStyles() {
        /* Create the Date Column Style */
        Element myStyle = theParser.newElement(MetisOdfStyleItem.STYLE);
        theParser.setAttribute(myStyle, MetisOdfStyleItem.FAMILY, MetisOdfTableItem.COLUMN.getName());
        String myName = getColumnStyleName(MetisSheetCellStyleType.DATE);
        theParser.setAttribute(myStyle, MetisOdfStyleItem.NAME, myName);
        Element myProperty = theParser.newElement(MetisOdfStyleItem.COLUMNPROPS);
        myStyle.appendChild(myProperty);
        theParser.setAttribute(myProperty, MetisOdfStyleItem.COLUMNWIDTH, getStyleWidth(MetisSheetFormats.WIDTH_DATE));
        theStyles.appendChild(myStyle);

        /* Create the Money Column Style */
        myStyle = theParser.newElement(MetisOdfStyleItem.STYLE);
        theParser.setAttribute(myStyle, MetisOdfStyleItem.FAMILY, MetisOdfTableItem.COLUMN.getName());
        myName = getColumnStyleName(MetisSheetCellStyleType.MONEY);
        theParser.setAttribute(myStyle, MetisOdfStyleItem.NAME, myName);
        myProperty = theParser.newElement(MetisOdfStyleItem.COLUMNPROPS);
        myStyle.appendChild(myProperty);
        theParser.setAttribute(myProperty, MetisOdfStyleItem.COLUMNWIDTH, getStyleWidth(MetisSheetFormats.WIDTH_MONEY));
        theStyles.appendChild(myStyle);

        /* Create the Price Column Style */
        myStyle = theParser.newElement(MetisOdfStyleItem.STYLE);
        theParser.setAttribute(myStyle, MetisOdfStyleItem.FAMILY, MetisOdfTableItem.COLUMN.getName());
        myName = getColumnStyleName(MetisSheetCellStyleType.PRICE);
        theParser.setAttribute(myStyle, MetisOdfStyleItem.NAME, myName);
        myProperty = theParser.newElement(MetisOdfStyleItem.COLUMNPROPS);
        myStyle.appendChild(myProperty);
        theParser.setAttribute(myProperty, MetisOdfStyleItem.COLUMNWIDTH, getStyleWidth(MetisSheetFormats.WIDTH_PRICE));

        /* Create the Units Column Style */
        myStyle = theParser.newElement(MetisOdfStyleItem.STYLE);
        theParser.setAttribute(myStyle, MetisOdfStyleItem.FAMILY, MetisOdfTableItem.COLUMN.getName());
        myName = getColumnStyleName(MetisSheetCellStyleType.UNITS);
        theParser.setAttribute(myStyle, MetisOdfStyleItem.NAME, myName);
        myProperty = theParser.newElement(MetisOdfStyleItem.COLUMNPROPS);
        myStyle.appendChild(myProperty);
        theParser.setAttribute(myProperty, MetisOdfStyleItem.COLUMNWIDTH, getStyleWidth(MetisSheetFormats.WIDTH_UNITS));
        theStyles.appendChild(myStyle);

        /* Create the Rate Column Style */
        myStyle = theParser.newElement(MetisOdfStyleItem.STYLE);
        theParser.setAttribute(myStyle, MetisOdfStyleItem.FAMILY, MetisOdfTableItem.COLUMN.getName());
        myName = getColumnStyleName(MetisSheetCellStyleType.RATE);
        theParser.setAttribute(myStyle, MetisOdfStyleItem.NAME, myName);
        myProperty = theParser.newElement(MetisOdfStyleItem.COLUMNPROPS);
        myStyle.appendChild(myProperty);
        theParser.setAttribute(myProperty, MetisOdfStyleItem.COLUMNWIDTH, getStyleWidth(MetisSheetFormats.WIDTH_RATE));
        theStyles.appendChild(myStyle);

        /* Create the Dilution Column Style */
        myStyle = theParser.newElement(MetisOdfStyleItem.STYLE);
        theParser.setAttribute(myStyle, MetisOdfStyleItem.FAMILY, MetisOdfTableItem.COLUMN.getName());
        myName = getColumnStyleName(MetisSheetCellStyleType.DILUTION);
        theParser.setAttribute(myStyle, MetisOdfStyleItem.NAME, myName);
        myProperty = theParser.newElement(MetisOdfStyleItem.COLUMNPROPS);
        myStyle.appendChild(myProperty);
        theParser.setAttribute(myProperty, MetisOdfStyleItem.COLUMNWIDTH, getStyleWidth(MetisSheetFormats.WIDTH_DILUTION));
        theStyles.appendChild(myStyle);

        /* Create the Ratio Column Style */
        myStyle = theParser.newElement(MetisOdfStyleItem.STYLE);
        theParser.setAttribute(myStyle, MetisOdfStyleItem.FAMILY, MetisOdfTableItem.COLUMN.getName());
        myName = getColumnStyleName(MetisSheetCellStyleType.RATIO);
        theParser.setAttribute(myStyle, MetisOdfStyleItem.NAME, myName);
        myProperty = theParser.newElement(MetisOdfStyleItem.COLUMNPROPS);
        myStyle.appendChild(myProperty);
        theParser.setAttribute(myProperty, MetisOdfStyleItem.COLUMNWIDTH, getStyleWidth(MetisSheetFormats.WIDTH_RATIO));
        theStyles.appendChild(myStyle);

        /* Create the Integer Column Style */
        myStyle = theParser.newElement(MetisOdfStyleItem.STYLE);
        theParser.setAttribute(myStyle, MetisOdfStyleItem.FAMILY, MetisOdfTableItem.COLUMN.getName());
        myName = getColumnStyleName(MetisSheetCellStyleType.INTEGER);
        theParser.setAttribute(myStyle, MetisOdfStyleItem.NAME, myName);
        myProperty = theParser.newElement(MetisOdfStyleItem.COLUMNPROPS);
        myStyle.appendChild(myProperty);
        theParser.setAttribute(myProperty, MetisOdfStyleItem.COLUMNWIDTH, getStyleWidth(MetisSheetFormats.WIDTH_INT));
        theStyles.appendChild(myStyle);

        /* Create the Boolean Column Style */
        myStyle = theParser.newElement(MetisOdfStyleItem.STYLE);
        theParser.setAttribute(myStyle, MetisOdfStyleItem.FAMILY, MetisOdfTableItem.COLUMN.getName());
        myName = getColumnStyleName(MetisSheetCellStyleType.BOOLEAN);
        theParser.setAttribute(myStyle, MetisOdfStyleItem.NAME, myName);
        myProperty = theParser.newElement(MetisOdfStyleItem.COLUMNPROPS);
        myStyle.appendChild(myProperty);
        theParser.setAttribute(myProperty, MetisOdfStyleItem.COLUMNWIDTH, getStyleWidth(MetisSheetFormats.WIDTH_BOOL));
        theStyles.appendChild(myStyle);

        /* Create the String Column Style */
        myStyle = theParser.newElement(MetisOdfStyleItem.STYLE);
        theParser.setAttribute(myStyle, MetisOdfStyleItem.FAMILY, MetisOdfTableItem.COLUMN.getName());
        myName = getColumnStyleName(MetisSheetCellStyleType.STRING);
        theParser.setAttribute(myStyle, MetisOdfStyleItem.NAME, myName);
        myProperty = theParser.newElement(MetisOdfStyleItem.COLUMNPROPS);
        myStyle.appendChild(myProperty);
        theParser.setAttribute(myProperty, MetisOdfStyleItem.COLUMNWIDTH, getStyleWidth(MetisSheetFormats.WIDTH_STRING));
        theStyles.appendChild(myStyle);

        /* Create the Table Style */
        myStyle = theParser.newElement(MetisOdfStyleItem.STYLE);
        theParser.setAttribute(myStyle, MetisOdfStyleItem.FAMILY, MetisOdfTableItem.TABLE.getName());
        theParser.setAttribute(myStyle, MetisOdfStyleItem.NAME, STYLE_TABLE);
        myProperty = theParser.newElement(MetisOdfStyleItem.TABLEPROPS);
        myStyle.appendChild(myProperty);
        theParser.setAttribute(myProperty, MetisOdfStyleItem.DISPLAY, Boolean.TRUE);
        theStyles.appendChild(myStyle);

        /* Create the Hidden Table Style */
        myStyle = theParser.newElement(MetisOdfStyleItem.STYLE);
        theParser.setAttribute(myStyle, MetisOdfStyleItem.FAMILY, MetisOdfTableItem.TABLE.getName());
        theParser.setAttribute(myStyle, MetisOdfStyleItem.NAME, STYLE_HIDDENTABLE);
        myProperty = theParser.newElement(MetisOdfStyleItem.TABLEPROPS);
        myStyle.appendChild(myProperty);
        theParser.setAttribute(myProperty, MetisOdfStyleItem.DISPLAY, Boolean.FALSE);
        theStyles.appendChild(myStyle);

        /* Create the Row Style */
        myStyle = theParser.newElement(MetisOdfStyleItem.STYLE);
        theParser.setAttribute(myStyle, MetisOdfStyleItem.FAMILY, MetisOdfTableItem.ROW.getName());
        theParser.setAttribute(myStyle, MetisOdfStyleItem.NAME, STYLE_ROW);
        myProperty = theParser.newElement(MetisOdfStyleItem.ROWPROPS);
        myStyle.appendChild(myProperty);
        theParser.setAttribute(myProperty, MetisOdfStyleItem.ROWHEIGHT, "5mm");
        theStyles.appendChild(myStyle);
    }

    /**
     * Obtain alignment for a cell.
     * @param pType the cell type
     * @return the alignment
     */
    private static String getStyleAlignment(final MetisSheetCellStyleType pType) {
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
    private static String getStyleFont(final MetisSheetCellStyleType pType) {
        switch (pType) {
            case HEADER:
            case BOOLEAN:
            case STRING:
                return MetisSheetFormats.FONT_VALUE;
            default:
                return MetisSheetFormats.FONT_NUMERIC;
        }
    }
}
