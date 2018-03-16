/*******************************************************************************
 * jMetis: Java Data Framework
 * Copyright 2012,2017 Tony Washer
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

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.odftoolkit.odfdom.dom.OdfContentDom;
import org.odftoolkit.odfdom.dom.attribute.table.TableMessageTypeAttribute;
import org.odftoolkit.odfdom.dom.attribute.table.TableOrientationAttribute;
import org.odftoolkit.odfdom.dom.element.number.NumberTextElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeSpreadsheetElement;
import org.odftoolkit.odfdom.dom.element.style.StyleTextPropertiesElement;
import org.odftoolkit.odfdom.dom.element.table.TableContentValidationElement;
import org.odftoolkit.odfdom.dom.element.table.TableContentValidationsElement;
import org.odftoolkit.odfdom.dom.element.table.TableDatabaseRangeElement;
import org.odftoolkit.odfdom.dom.element.table.TableDatabaseRangesElement;
import org.odftoolkit.odfdom.dom.element.table.TableErrorMessageElement;
import org.odftoolkit.odfdom.dom.element.table.TableNamedExpressionsElement;
import org.odftoolkit.odfdom.dom.element.table.TableNamedRangeElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableCellElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableColumnElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableRowElement;
import org.odftoolkit.odfdom.dom.style.OdfStyleFamily;
import org.odftoolkit.odfdom.dom.style.props.OdfParagraphProperties;
import org.odftoolkit.odfdom.dom.style.props.OdfTableCellProperties;
import org.odftoolkit.odfdom.dom.style.props.OdfTableColumnProperties;
import org.odftoolkit.odfdom.dom.style.props.OdfTableProperties;
import org.odftoolkit.odfdom.dom.style.props.OdfTableRowProperties;
import org.odftoolkit.odfdom.dom.style.props.OdfTextProperties;
import org.odftoolkit.odfdom.incubator.doc.number.OdfNumberDateStyle;
import org.odftoolkit.odfdom.incubator.doc.number.OdfNumberPercentageStyle;
import org.odftoolkit.odfdom.incubator.doc.number.OdfNumberStyle;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeAutomaticStyles;
import org.odftoolkit.odfdom.incubator.doc.style.OdfStyle;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.w3c.dom.Node;

import net.sourceforge.joceanus.jmetis.MetisDataException;
import net.sourceforge.joceanus.jmetis.MetisIOException;
import net.sourceforge.joceanus.jmetis.MetisLogicException;
import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.sheet.MetisOasisCellAddress.OasisCellRange;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * The Oasis WorkBook.
 */
public class MetisOasisWorkBook {
    /**
     * Default Parent style name.
     */
    private static final String STYLE_DEFPARENT = "Default";

    /**
     * Row style name.
     */
    protected static final String STYLE_ROW = "snRow";

    /**
     * Table style name.
     */
    protected static final String STYLE_TABLE = "snTable";

    /**
     * Hidden Table style name.
     */
    protected static final String STYLE_HIDDENTABLE = "snHiddenTable";

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
    private static final String FONT_SIZE = MetisDataWorkBook.FONT_HEIGHT
                                            + "pt";

    /**
     * The load failure error text.
     */
    private static final String ERROR_LOAD = "Failed to load workbook";

    /**
     * The save failure error text.
     */
    private static final String ERROR_SAVE = "Failed to save workbook";

    /**
     * Oasis WorkBook.
     */
    private final SpreadsheetDocument theBook;

    /**
     * Contents.
     */
    private final OfficeSpreadsheetElement theContents;

    /**
     * Contents Dom.
     */
    private final OdfContentDom theContentDom;

    /**
     * Named Expressions List.
     */
    private final TableNamedExpressionsElement theExpressions;

    /**
     * Contents Validation Rules List.
     */
    private final TableContentValidationsElement theValidations;

    /**
     * Table Database Ranges List.
     */
    private final TableDatabaseRangesElement theFilters;

    /**
     * Data formatter.
     */
    private final MetisDataFormatter theFormatter;

    /**
     * OdfStyles.
     */
    private final OdfOfficeAutomaticStyles theStyles;

    /**
     * Count of table elements.
     */
    private int theNumTables;

    /**
     * Count of validation elements.
     */
    private int theNumConstraints;

    /**
     * Map of Sheets.
     */
    private final Map<String, SheetReference> theSheetMap;

    /**
     * Map of Data styles.
     */
    private final Map<String, OdfStyle> theStyleMap;

    /**
     * Map of Ranges.
     */
    private final Map<String, TableNamedRangeElement> theRangeMap;

    /**
     * Map of Constraints.
     */
    private final Map<Object, TableContentValidationElement> theConstraintMap;

    /**
     * Constructor.
     * @param pInput the input stream
     * @throws OceanusException on error
     */
    public MetisOasisWorkBook(final InputStream pInput) throws OceanusException {
        try {
            /* Access book and contents */
            theBook = SpreadsheetDocument.loadDocument(pInput);
            theContents = theBook.getContentRoot();
            theContentDom = theBook.getContentDom();
            theStyles = null;
            theStyleMap = null;
            theConstraintMap = null;

            /* Allocate the formatter */
            theFormatter = MetisDataWorkBook.createFormatter();

            /* Allocate the maps */
            theSheetMap = new HashMap<>();
            theRangeMap = new HashMap<>();

            /* Access Key elements */
            theExpressions = OdfElement.findFirstChildNode(TableNamedExpressionsElement.class, theContents);
            theValidations = OdfElement.findFirstChildNode(TableContentValidationsElement.class, theContents);
            theFilters = OdfElement.findFirstChildNode(TableDatabaseRangesElement.class, theContents);

            /* Build the maps */
            buildSheetMap();
            buildRangeMap();
        } catch (Exception e) {
            throw new MetisIOException(ERROR_LOAD, e);
        }
    }

    /**
     * Constructor.
     * @throws OceanusException on error
     */
    public MetisOasisWorkBook() throws OceanusException {
        try {
            /* Create an empty document */
            theBook = SpreadsheetDocument.newSpreadsheetDocument();
            theContents = theBook.getContentRoot();
            theContentDom = theBook.getContentDom();

            /* Access the styles */
            theStyles = theContentDom.getOrCreateAutomaticStyles();

            /* Allocate the formatter */
            theFormatter = MetisDataWorkBook.createFormatter();

            /* Clean out the document */
            cleanOutDocument();

            /* Create key elements */
            theValidations = theContents.newTableContentValidationsElement();
            theExpressions = theContents.newTableNamedExpressionsElement();
            theFilters = theContents.newTableDatabaseRangesElement();

            /* Allocate the maps */
            theSheetMap = new HashMap<>();
            theStyleMap = new HashMap<>();
            theRangeMap = new HashMap<>();
            theConstraintMap = new HashMap<>();

            /* Create the cellStyles */
            createCellStyles();
        } catch (Exception e) {
            throw new MetisIOException(ERROR_LOAD, e);
        }
    }

    /**
     * Obtain the contentsDom.
     * @return the content Dom
     */
    protected OdfContentDom getContentDom() {
        return theContentDom;
    }

    /**
     * Obtain the data formatter.
     * @return the formatter
     */
    protected MetisDataFormatter getFormatter() {
        return theFormatter;
    }

    /**
     * Save the workBook to output stream.
     * @param pOutput the output stream
     * @throws OceanusException on error
     */
    public void saveToStream(final OutputStream pOutput) throws OceanusException {
        try {
            theBook.save(pOutput);
        } catch (Exception e) {
            throw new MetisIOException(ERROR_SAVE, e);
        }
    }

    /**
     * Obtain a new named sheet.
     * @param pName the name of the sheet
     * @param pNumRows the number of rows to allocate
     * @param pNumCols the number of columns to allocate
     * @return the new sheet.
     */
    protected MetisDataSheet newSheet(final String pName,
                                      final int pNumRows,
                                      final int pNumCols) {
        /* Create the new Sheet */
        final TableTableElement myElement = theContents.newTableTableElement();
        myElement.setTableNameAttribute(pName);
        myElement.setTableStyleNameAttribute(STYLE_TABLE);

        /* Create the columns */
        final TableTableColumnElement myCol = myElement.newTableTableColumnElement();
        if (pNumCols > 1) {
            myCol.setTableNumberColumnsRepeatedAttribute(pNumCols);
        }

        /* Create the rows */
        final TableTableRowElement myRow = myElement.newTableTableRowElement();
        myRow.setTableStyleNameAttribute(STYLE_ROW);
        if (pNumRows > 1) {
            myRow.setTableNumberRowsRepeatedAttribute(pNumRows);
        }

        /* Create the cells */
        final TableTableCellElement myCell = new TableTableCellElement(theContentDom);
        myRow.appendChild(myCell);
        if (pNumCols > 1) {
            myCell.setTableNumberColumnsRepeatedAttribute(pNumCols);
        }

        /* Create the sheet representation */
        final SheetReference myRef = new SheetReference(myElement);
        myRef.addToMap();
        return myRef.getSheet();
    }

    /**
     * Obtain a new named sheet.
     * @param pName the name of the sheet
     * @return the new sheet.
     */
    protected MetisDataSheet newSheet(final String pName) {
        return newSheet(pName, 1, 1);
    }

    /**
     * Obtain a named sheet.
     * @param pName the name of the sheet
     * @return the sheet.
     */
    protected MetisDataSheet getSheet(final String pName) {
        /* Obtain the existing sheet */
        final SheetReference myRef = theSheetMap.get(pName);
        return myRef == null
                             ? null
                             : myRef.getReadOnlySheet();
    }

    /**
     * Obtain a view of the named range.
     * @param pName the name of the range
     * @return the view of the range or null if range does not exist
     * @throws OceanusException on error
     */
    protected MetisDataView getRangeView(final String pName) throws OceanusException {
        /* Locate the named range in the map */
        final TableNamedRangeElement myRange = theRangeMap.get(pName);
        if (myRange == null) {
            return null;
        }

        /* Obtain the address */
        final String myAddress = myRange.getTableCellRangeAddressAttribute();
        final OasisCellRange myCellRange = new OasisCellRange(myAddress);
        final MetisOasisCellAddress myFirstCell = myCellRange.getFirstCell();
        final MetisOasisCellAddress myLastCell = myCellRange.getLastCell();

        /* Obtain the sheet and reject if missing */
        final MetisDataSheet mySheet = getSheet(myFirstCell.getSheetName());
        if (mySheet == null) {
            throw new MetisLogicException("Sheet for "
                                          + pName
                                          + " not found in workbook");
        }

        /* Return the view */
        return new MetisDataView(mySheet, myFirstCell.getPosition(), myLastCell.getPosition());
    }

    /**
     * Build sheet map.
     */
    private void buildSheetMap() {
        /* Loop through the tables */
        for (Node myNode = theContents.getFirstChild(); myNode != null; myNode = myNode.getNextSibling()) {
            /* Skip uninteresting elements */
            if (!(myNode instanceof TableTableElement)) {
                continue;
            }

            /* Add sheet to map */
            final TableTableElement myTable = (TableTableElement) myNode;
            final SheetReference myRef = new SheetReference(myTable);
            myRef.addToMap();
        }
    }

    /**
     * Build range map.
     */
    private void buildRangeMap() {
        /* Loop through the named ranges */
        for (Node myNode = theExpressions.getFirstChild(); myNode != null; myNode = myNode.getNextSibling()) {
            /* Skip uninteresting elements */
            if (!(myNode instanceof TableNamedRangeElement)) {
                continue;
            }

            /* Add range to map */
            final TableNamedRangeElement myRange = (TableNamedRangeElement) myNode;
            theRangeMap.put(myRange.getTableNameAttribute(), myRange);
        }
    }

    /**
     * Declare the named range.
     * @param pName the name of the range
     * @param pRange the range to declare
     * @throws OceanusException on error
     */
    protected void declareRange(final String pName,
                                final OasisCellRange pRange) throws OceanusException {
        /* Check for existing range */
        if (theRangeMap.get(pName) != null) {
            throw new MetisLogicException("Name "
                                          + pName
                                          + "already exists in workbook");
        }

        /* Protect against exceptions */
        try {
            /* Add the new range */
            final TableNamedRangeElement myRange = theExpressions.newTableNamedRangeElement(pRange.toString(), pName);
            myRange.setTableBaseCellAddressAttribute(pRange.getFirstCell().toString());
            theRangeMap.put(pName, myRange);
        } catch (Exception e) {
            throw new MetisDataException("Failed to declare range", e);
        }
    }

    /**
     * Apply Data Validation.
     * @param pSheet the workSheet containing the cells
     * @param pFirstCell the the first cell in the range
     * @param pLastCell the last cell in the range
     * @param pValidRange the name of the validation range
     */
    protected void applyDataValidation(final MetisOasisSheet pSheet,
                                       final MetisCellPosition pFirstCell,
                                       final MetisCellPosition pLastCell,
                                       final String pValidRange) {
        /* Access constraint */
        TableContentValidationElement myConstraint = theConstraintMap.get(pValidRange);
        if (myConstraint == null) {
            /* Create the constraint */
            myConstraint = createDataConstraint(pValidRange);

            /* Store the constraint */
            theConstraintMap.put(pValidRange, myConstraint);
        }

        /* Apply the constraint */
        applyDataConstraint(pSheet, pFirstCell, pLastCell, myConstraint);
    }

    /**
     * Apply Data Validation.
     * @param pSheet the workSheet containing the cells
     * @param pFirstCell the the first cell in the range
     * @param pLastCell the last cell in the range
     * @param pValueList the value list
     */
    protected void applyDataValidation(final MetisOasisSheet pSheet,
                                       final MetisCellPosition pFirstCell,
                                       final MetisCellPosition pLastCell,
                                       final String[] pValueList) {
        /* Access constraint */
        TableContentValidationElement myConstraint = theConstraintMap.get(pValueList);
        if (myConstraint == null) {
            /* Build the constraint list */
            final StringBuilder myBuilder = new StringBuilder();

            /* Loop through the values */
            for (String myValue : pValueList) {
                /* If this is not the first element */
                if (myBuilder.length() > 0) {
                    /* Add a comma */
                    myBuilder.append(',');
                }
                /* Add the escaped value */
                myBuilder.append(MetisOasisCellAddress.escapeApostrophes(myValue));
            }

            /* Create the constraint */
            myConstraint = createDataConstraint(myBuilder.toString());

            /* Store the constraint */
            theConstraintMap.put(pValueList, myConstraint);
        }

        /* Apply the constraint */
        applyDataConstraint(pSheet, pFirstCell, pLastCell, myConstraint);
    }

    /**
     * Apply Data Validation.
     * @param pSheet the workSheet containing the cells
     * @param pFirstCell the the first cell in the range
     * @param pLastCell the last cell in the range
     * @param pConstraint the constraint
     */
    private static void applyDataConstraint(final MetisOasisSheet pSheet,
                                            final MetisCellPosition pFirstCell,
                                            final MetisCellPosition pLastCell,
                                            final TableContentValidationElement pConstraint) {
        /* Determine size of range */
        final String myName = pConstraint.getTableNameAttribute();
        int iRow = pFirstCell.getRowIndex();
        final int iLastRow = pLastCell.getRowIndex();
        final int iFirstCol = pFirstCell.getColumnIndex();
        final int iLastCol = pLastCell.getColumnIndex();

        /* Loop through the rows */
        for (MetisOasisRow myRow = pSheet.getMutableRowByIndex(iRow); iRow <= iLastRow; iRow++, myRow = pSheet.getMutableRowByIndex(iRow)) {
            /* Loop through the columns */
            for (int iCol = iFirstCol; iCol <= iLastCol; iCol++) {
                /* Access the cell and set the constraint */
                final MetisOasisCell myCell = myRow.getMutableCellByIndex(iCol);
                myCell.setValidationName(myName);
            }
        }
    }

    /**
     * Create Data Constraint.
     * @param pConstraint the constraint list
     * @return the constraint
     */
    private TableContentValidationElement createDataConstraint(final String pConstraint) {
        /* Build the name */
        final String myName = "val"
                              + ++theNumConstraints;

        /* Create the new constraint */
        final TableContentValidationElement myConstraint = theValidations.newTableContentValidationElement(myName);

        /* Create the rule */
        final String myRule = "of:cell-content-is-in-list("
                              + pConstraint
                              + ")";
        myConstraint.setTableConditionAttribute(myRule);
        myConstraint.setTableAllowEmptyCellAttribute(Boolean.TRUE);
        final TableErrorMessageElement myError = myConstraint.newTableErrorMessageElement();
        myError.setTableDisplayAttribute(Boolean.TRUE);
        myError.setTableMessageTypeAttribute(TableMessageTypeAttribute.Value.STOP.toString());

        /* Store the constraint */
        return myConstraint;
    }

    /**
     * Apply Data Filter.
     * @param pRange the range
     */
    protected void applyDataFilter(final OasisCellRange pRange) {
        /* Create the new filter */
        final TableDatabaseRangeElement myFilter = theFilters.newTableDatabaseRangeElement("Events.E1:Events.E15");
        myFilter.setTableNameAttribute("__Anonymous_Sheet_DB__14");
        myFilter.setTableDisplayFilterButtonsAttribute(Boolean.TRUE);
        myFilter.setTableOrientationAttribute(TableOrientationAttribute.Value.COLUMN.toString());
    }

    /**
     * Clean out the document.
     */
    private void cleanOutDocument() {
        Node myChild = theContents.getFirstChild();
        while (myChild != null) {
            theContents.removeChild(myChild);
            myChild = theContents.getFirstChild();
        }
    }

    /**
     * Add element as next sibling of reference node.
     * @param pNew the node to add
     * @param pRef the node to add after
     */
    protected static void addAsNextSibling(final Node pNew,
                                           final Node pRef) {
        /* Obtain parent of reference node */
        final Node myParent = pRef.getParentNode();

        /* Obtain the next element */
        final Node myNextElement = pRef.getNextSibling();
        if (myNextElement != null) {
            myParent.insertBefore(pNew, myNextElement);
        } else {
            myParent.appendChild(pNew);
        }
    }

    /**
     * Add element as previous sibling of reference node.
     * @param pNew the node to add
     * @param pRef the node to add after
     */
    protected static void addAsPriorSibling(final Node pNew,
                                            final Node pRef) {
        /* Obtain parent of reference node */
        final Node myParent = pRef.getParentNode();

        /* Insert before reference node */
        myParent.insertBefore(pNew, pRef);
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
    protected static String getColumnStyleName(final MetisCellStyleType pType) {
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
     * @param pFormat the style format
     * @param pType the style type
     */
    private void createNumericStyle(final String pStyleName,
                                    final String pFormat,
                                    final MetisCellStyleType pType) {
        /* Switch on type */
        switch (pType) {
            case DATE:
                theStyles.appendChild(new OdfNumberDateStyle(theContentDom, pFormat, pStyleName));
                break;
            case RATE:
                theStyles.appendChild(new OdfNumberPercentageStyle(theContentDom, pFormat, pStyleName));
                break;
            default:
                createStandardNumericStyle(pStyleName, pFormat);
                break;
        }
    }

    /**
     * Define a standard numeric CellStyle.
     * @param pStyleName the style name
     * @param pFormat the style format
     */
    private void createStandardNumericStyle(final String pStyleName,
                                            final String pFormat) {
        /* Look for format splits */
        final String[] myParts = pFormat.split(Character.toString(MetisDataFormats.CHAR_SEP));
        switch (myParts.length) {
            case 1:
                theStyles.appendChild(new OdfNumberStyle(theContentDom, pFormat, pStyleName));
                break;
            case 2:
                createDoubleNumericStyle(pStyleName, myParts);
                break;
            default:
                createTripleNumericStyle(pStyleName, myParts);
                break;
        }
    }

    /**
     * Define a double numeric CellStyle.
     * @param pStyleName the style name
     * @param pParts the format parts
     */
    private void createDoubleNumericStyle(final String pStyleName,
                                          final String[] pParts) {
        /* Build style */
        final String myNegName = "m"
                                 + pStyleName;
        final OdfNumberStyle myPos = new OdfNumberStyle(theContentDom, pParts[0], pStyleName);
        final OdfNumberStyle myNeg = new OdfNumberStyle(theContentDom, pParts[0], myNegName);
        myPos.setMapNegative(myNegName);
        final StyleTextPropertiesElement myNegStyle = new StyleTextPropertiesElement(theContentDom);
        myNegStyle.setFoColorAttribute(COLOR_NEG);
        myNeg.insertBefore(myNegStyle, myNeg.getFirstChild());
        theStyles.appendChild(myNeg);
        theStyles.appendChild(myPos);
    }

    /**
     * Define a triple numeric CellStyle.
     * @param pStyleName the style name
     * @param pParts the format parts
     */
    private void createTripleNumericStyle(final String pStyleName,
                                          final String[] pParts) {
        /* Build style */
        final String myNegName = "n"
                                 + pStyleName;
        final String myPosName = "p"
                                 + pStyleName;
        final OdfNumberStyle myPos = new OdfNumberStyle(theContentDom, pParts[0], myPosName);
        final OdfNumberStyle myNeg = new OdfNumberStyle(theContentDom, pParts[0], myNegName);
        final OdfNumberStyle myZero = new OdfNumberStyle(theContentDom);
        myZero.setStyleNameAttribute(pStyleName);
        final NumberTextElement myZeroText = myZero.newNumberTextElement();
        myZeroText.setTextContent(pParts[2]);
        myZero.setMapNegative(myNegName);
        myZero.setMapPositive(myPosName);
        final StyleTextPropertiesElement myNegStyle = new StyleTextPropertiesElement(theContentDom);
        myNegStyle.setFoColorAttribute(COLOR_NEG);
        myNeg.insertBefore(myNegStyle, myNeg.getFirstChild());
        theStyles.appendChild(myNeg);
        theStyles.appendChild(myPos);
        theStyles.appendChild(myZero);
    }

    /**
     * Create the standard CellStyles.
     */
    private void createCellStyles() {
        /* Access create styles holders */
        theBook.getOrCreateDocumentStyles();

        /* Create the Date Column Style */
        OdfStyle myStyle = theStyles.newStyle(OdfStyleFamily.TableColumn);
        String myName = getColumnStyleName(MetisCellStyleType.DATE);
        myStyle.setStyleNameAttribute(myName);
        myStyle.setProperty(OdfTableColumnProperties.ColumnWidth, getStyleWidth(MetisDataWorkBook.WIDTH_DATE));
        theStyleMap.put(myName, myStyle);

        /* Create the Money Column Style */
        myStyle = theStyles.newStyle(OdfStyleFamily.TableColumn);
        myName = getColumnStyleName(MetisCellStyleType.MONEY);
        myStyle.setStyleNameAttribute(myName);
        myStyle.setProperty(OdfTableColumnProperties.ColumnWidth, getStyleWidth(MetisDataWorkBook.WIDTH_MONEY));
        theStyleMap.put(myName, myStyle);

        /* Create the Price Column Style */
        myStyle = theStyles.newStyle(OdfStyleFamily.TableColumn);
        myName = getColumnStyleName(MetisCellStyleType.PRICE);
        myStyle.setStyleNameAttribute(myName);
        myStyle.setProperty(OdfTableColumnProperties.ColumnWidth, getStyleWidth(MetisDataWorkBook.WIDTH_PRICE));
        theStyleMap.put(myName, myStyle);

        /* Create the Units Column Style */
        myStyle = theStyles.newStyle(OdfStyleFamily.TableColumn);
        myName = getColumnStyleName(MetisCellStyleType.UNITS);
        myStyle.setStyleNameAttribute(myName);
        myStyle.setProperty(OdfTableColumnProperties.ColumnWidth, getStyleWidth(MetisDataWorkBook.WIDTH_UNITS));
        theStyleMap.put(myName, myStyle);

        /* Create the Rate Column Style */
        myStyle = theStyles.newStyle(OdfStyleFamily.TableColumn);
        myName = getColumnStyleName(MetisCellStyleType.RATE);
        myStyle.setStyleNameAttribute(myName);
        myStyle.setProperty(OdfTableColumnProperties.ColumnWidth, getStyleWidth(MetisDataWorkBook.WIDTH_RATE));
        theStyleMap.put(myName, myStyle);

        /* Create the Dilution Column Style */
        myStyle = theStyles.newStyle(OdfStyleFamily.TableColumn);
        myName = getColumnStyleName(MetisCellStyleType.DILUTION);
        myStyle.setStyleNameAttribute(myName);
        myStyle.setProperty(OdfTableColumnProperties.ColumnWidth, getStyleWidth(MetisDataWorkBook.WIDTH_DILUTION));
        theStyleMap.put(myName, myStyle);

        /* Create the Ratio Column Style */
        myStyle = theStyles.newStyle(OdfStyleFamily.TableColumn);
        myName = getColumnStyleName(MetisCellStyleType.RATIO);
        myStyle.setStyleNameAttribute(myName);
        myStyle.setProperty(OdfTableColumnProperties.ColumnWidth, getStyleWidth(MetisDataWorkBook.WIDTH_RATIO));
        theStyleMap.put(myName, myStyle);

        /* Create the Integer Column Style */
        myStyle = theStyles.newStyle(OdfStyleFamily.TableColumn);
        myName = getColumnStyleName(MetisCellStyleType.INTEGER);
        myStyle.setStyleNameAttribute(myName);
        myStyle.setProperty(OdfTableColumnProperties.ColumnWidth, getStyleWidth(MetisDataWorkBook.WIDTH_INT));
        theStyleMap.put(myName, myStyle);

        /* Create the Boolean Column Style */
        myStyle = theStyles.newStyle(OdfStyleFamily.TableColumn);
        myName = getColumnStyleName(MetisCellStyleType.BOOLEAN);
        myStyle.setStyleNameAttribute(myName);
        myStyle.setProperty(OdfTableColumnProperties.ColumnWidth, getStyleWidth(MetisDataWorkBook.WIDTH_BOOL));
        theStyleMap.put(myName, myStyle);

        /* Create the String Column Style */
        myStyle = theStyles.newStyle(OdfStyleFamily.TableColumn);
        myName = getColumnStyleName(MetisCellStyleType.STRING);
        myStyle.setStyleNameAttribute(myName);
        myStyle.setProperty(OdfTableColumnProperties.ColumnWidth, getStyleWidth(MetisDataWorkBook.WIDTH_STRING));
        theStyleMap.put(myName, myStyle);

        /* Create the Table Style */
        myStyle = theStyles.newStyle(OdfStyleFamily.Table);
        myStyle.setStyleNameAttribute(STYLE_TABLE);
        myStyle.setProperty(OdfTableProperties.Display, Boolean.TRUE.toString());
        theStyleMap.put(STYLE_TABLE, myStyle);

        /* Create the Hidden Table Style */
        myStyle = theStyles.newStyle(OdfStyleFamily.Table);
        myStyle.setStyleNameAttribute(STYLE_HIDDENTABLE);
        myStyle.setProperty(OdfTableProperties.Display, Boolean.FALSE.toString());
        theStyleMap.put(STYLE_HIDDENTABLE, myStyle);

        /* Create the Row Style */
        myStyle = theStyles.newStyle(OdfStyleFamily.TableRow);
        myStyle.setStyleNameAttribute(STYLE_ROW);
        myStyle.setProperty(OdfTableRowProperties.RowHeight, "5mm");
        theStyleMap.put(STYLE_ROW, myStyle);
    }

    /**
     * Obtain alignment for a cell.
     * @param pType the cell type
     * @return the alignment
     */
    private String getStyleAlignment(final MetisCellStyleType pType) {
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
    private String getStyleFont(final MetisCellStyleType pType) {
        switch (pType) {
            case HEADER:
            case BOOLEAN:
            case STRING:
                return MetisDataWorkBook.FONT_VALUE;
            default:
                return MetisDataWorkBook.FONT_NUMERIC;
        }
    }

    /**
     * Obtain the required CellStyle.
     * @param pType the CellStyleType
     * @return the required CellStyle
     */
    protected OdfStyle getCellStyle(final MetisCellStyleType pType) {
        /* Determine the correct format */
        final String myStyleName = MetisDataFormats.getFormatName(pType);

        /* Look for existing format */
        OdfStyle myStyle = theStyleMap.get(myStyleName);
        if (myStyle != null) {
            return myStyle;
        }

        /* Create the New Cell Style */
        myStyle = theStyles.newStyle(OdfStyleFamily.TableCell);
        myStyle.setStyleNameAttribute(myStyleName);
        myStyle.setStyleParentStyleNameAttribute(STYLE_DEFPARENT);
        myStyle.setProperty(OdfTextProperties.FontName, getStyleFont(pType));
        myStyle.setProperty(OdfTextProperties.FontSize, FONT_SIZE);
        myStyle.setProperty(OdfParagraphProperties.TextAlign, getStyleAlignment(pType));

        /* If we have a data format */
        if (MetisDataFormats.hasDataFormat(pType)) {
            /* Determine the format */
            final String myFormat = MetisDataFormats.getDataFormatString(pType);
            final String myFormatName = getDataStyleName(myStyleName);
            createNumericStyle(myFormatName, myFormat, pType);
            myStyle.setStyleDataStyleNameAttribute(myFormatName);
        }

        /* Add to the map and return new style */
        theStyleMap.put(myStyleName, myStyle);
        return myStyle;
    }

    /**
     * Obtain the required CellStyle.
     * @param pValue the Cell Value
     * @return the required CellStyle
     */
    protected OdfStyle getCellStyle(final Object pValue) {
        /* Determine the correct format */
        final String myStyleName = MetisDataFormats.getFormatName(pValue);

        /* Look for existing format */
        OdfStyle myStyle = theStyleMap.get(myStyleName);
        if (myStyle != null) {
            return myStyle;
        }

        /* Determine the CellStyleType */
        final MetisCellStyleType myType = MetisDataFormats.getCellStyleType(pValue);

        /* Create the New Cell Style */
        myStyle = theStyles.newStyle(OdfStyleFamily.TableCell);
        myStyle.setStyleNameAttribute(myStyleName);
        myStyle.setStyleParentStyleNameAttribute(STYLE_DEFPARENT);
        myStyle.setProperty(OdfTextProperties.FontName, getStyleFont(myType));
        myStyle.setProperty(OdfTextProperties.FontSize, FONT_SIZE);
        myStyle.setProperty(OdfParagraphProperties.TextAlign, getStyleAlignment(myType));

        /* If we have a data format */
        if (MetisDataFormats.hasDataFormat(myType)) {
            /* Determine the format */
            final String myFormat = MetisDataFormats.getDataFormatString(pValue);
            final String myFormatName = getDataStyleName(myStyleName);
            createNumericStyle(myFormatName, myFormat, myType);
            myStyle.setStyleDataStyleNameAttribute(myFormatName);
        }

        /* Add to the map and return new style */
        theStyleMap.put(myStyleName, myStyle);
        return myStyle;
    }

    /**
     * Obtain the required alternate CellStyle.
     * @param pValue the Cell Value
     * @return the required CellStyle
     */
    protected OdfStyle getAlternateCellStyle(final Object pValue) {
        /* Determine the correct format */
        final String myStyleName = MetisDataFormats.getAlternateFormatName(pValue);

        /* Look for existing format */
        OdfStyle myAltStyle = theStyleMap.get(myStyleName);
        if (myAltStyle != null) {
            return myAltStyle;
        }

        /* Create the New Cell Style */
        myAltStyle = theStyles.newStyle(OdfStyleFamily.TableCell);

        /* Determine the CellStyleType */
        MetisCellStyleType myType = MetisDataFormats.getCellStyleType(pValue);

        /* Handle the header style */
        if (myType == MetisCellStyleType.STRING) {
            myType = MetisCellStyleType.HEADER;
            myAltStyle.setProperty(OdfTextProperties.FontWeight, FONT_BOLD);
            myAltStyle.setProperty(OdfTableCellProperties.CellProtect, Boolean.TRUE.toString());
        }

        /* Create the New Cell Style */
        myAltStyle.setStyleNameAttribute(myStyleName);
        myAltStyle.setStyleParentStyleNameAttribute(STYLE_DEFPARENT);
        myAltStyle.setProperty(OdfTextProperties.FontName, getStyleFont(myType));
        myAltStyle.setProperty(OdfTextProperties.FontSize, FONT_SIZE);
        myAltStyle.setProperty(OdfParagraphProperties.TextAlign, getStyleAlignment(myType));

        /* If we have a data format */
        if (MetisDataFormats.hasDataFormat(myType)) {
            /* Determine the format */
            final String myFormat = MetisDataFormats.getDataFormatString(pValue);
            final String myFormatName = getDataStyleName(myStyleName);
            createNumericStyle(myFormatName, myFormat, myType);
            myAltStyle.setStyleDataStyleNameAttribute(myFormatName);
        }

        /* Add to the map and return new style */
        theStyleMap.put(myStyleName, myAltStyle);
        return myAltStyle;
    }

    /**
     * Sheet Reference class.
     */
    private final class SheetReference {
        /**
         * Table name.
         */
        private final String theName;

        /**
         * Table index.
         */
        private final int theIndex;

        /**
         * Table element.
         */
        private final TableTableElement theElement;

        /**
         * Constructor.
         * @param pElement the sheet element
         */
        private SheetReference(final TableTableElement pElement) {
            /* Store parameters */
            theName = pElement.getTableNameAttribute();
            theIndex = theNumTables++;
            theElement = pElement;
        }

        /**
         * Add to map.
         */
        private void addToMap() {
            /* Add to the map */
            theSheetMap.put(theName, this);
        }

        /**
         * Obtain ReadOnly Sheet representation.
         * @return the sheet representation
         */
        private MetisOasisSheet getReadOnlySheet() {
            return new MetisOasisSheet(MetisOasisWorkBook.this, theElement, theIndex, true);
        }

        /**
         * Obtain Sheet representation.
         * @return the sheet representation
         */
        private MetisOasisSheet getSheet() {
            return new MetisOasisSheet(MetisOasisWorkBook.this, theElement, theIndex, false);
        }
    }
}
