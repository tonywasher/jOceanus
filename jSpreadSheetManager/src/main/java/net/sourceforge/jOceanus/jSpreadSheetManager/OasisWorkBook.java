/*******************************************************************************
 * jSpreadSheetManager: SpreadSheet management
 * Copyright 2012,2013 Tony Washer
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
package net.sourceforge.jOceanus.jSpreadSheetManager;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataManager.JDataException.ExceptionClass;
import net.sourceforge.jOceanus.jDataManager.JDataFormatter;
import net.sourceforge.jOceanus.jSpreadSheetManager.OasisCellAddress.OasisCellRange;

import org.odftoolkit.odfdom.dom.OdfContentDom;
import org.odftoolkit.odfdom.dom.attribute.table.TableMessageTypeAttribute;
import org.odftoolkit.odfdom.dom.attribute.table.TableOrientationAttribute;
import org.odftoolkit.odfdom.dom.element.office.OfficeSpreadsheetElement;
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

/**
 * The Oasis WorkBook.
 */
public class OasisWorkBook {
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
    private static final String FONT_SIZE = DataWorkBook.FONT_HEIGHT
                                            + "pt";

    /**
     * Self Reference.
     */
    private final OasisWorkBook theWorkBook = this;

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
    private final JDataFormatter theFormatter;

    /**
     * OdfStyles.
     */
    private final OdfOfficeAutomaticStyles theStyles;

    /**
     * Count of table elements.
     */
    private int theNumTables = 0;

    /**
     * Count of validation elements.
     */
    private int theNumConstraints = 0;

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
    private final Map<String, TableContentValidationElement> theConstraintMap;

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
    protected JDataFormatter getFormatter() {
        return theFormatter;
    }

    /**
     * Constructor.
     * @param pInput the input stream
     * @throws JDataException on error
     */
    public OasisWorkBook(final InputStream pInput) throws JDataException {
        try {
            /* Access book and contents */
            theBook = SpreadsheetDocument.loadDocument(pInput);
            theContents = theBook.getContentRoot();
            theContentDom = theBook.getContentDom();
            theStyles = null;
            theStyleMap = null;
            theConstraintMap = null;

            /* Allocate the formatter */
            theFormatter = DataWorkBook.createFormatter();

            /* Allocate the maps */
            theSheetMap = new HashMap<String, SheetReference>();
            theRangeMap = new HashMap<String, TableNamedRangeElement>();

            /* Access Key elements */
            theExpressions = OdfElement.findFirstChildNode(TableNamedExpressionsElement.class, theContents);
            theValidations = OdfElement.findFirstChildNode(TableContentValidationsElement.class, theContents);
            theFilters = OdfElement.findFirstChildNode(TableDatabaseRangesElement.class, theContents);

            /* Build the maps */
            buildSheetMap();
            buildRangeMap();
        } catch (Exception e) {
            throw new JDataException(ExceptionClass.EXCEL, "Failed to load workbook", e);
        }
    }

    /**
     * Constructor.
     * @throws JDataException on error
     */
    public OasisWorkBook() throws JDataException {
        try {
            /* Create an empty document */
            theBook = SpreadsheetDocument.newSpreadsheetDocument();
            theContents = theBook.getContentRoot();
            theContentDom = theBook.getContentDom();

            /* Access the styles */
            theStyles = theContentDom.getOrCreateAutomaticStyles();

            /* Allocate the formatter */
            theFormatter = DataWorkBook.createFormatter();

            /* Clean out the document */
            cleanOutDocument();

            /* Create key elements */
            theValidations = theContents.newTableContentValidationsElement();
            theExpressions = theContents.newTableNamedExpressionsElement();
            theFilters = theContents.newTableDatabaseRangesElement();

            /* Allocate the maps */
            theSheetMap = new HashMap<String, SheetReference>();
            theStyleMap = new HashMap<String, OdfStyle>();
            theRangeMap = new HashMap<String, TableNamedRangeElement>();
            theConstraintMap = new HashMap<String, TableContentValidationElement>();

            /* Create the cellStyles */
            createCellStyles();
        } catch (Exception e) {
            throw new JDataException(ExceptionClass.EXCEL, "Failed to load workbook", e);
        }
    }

    /**
     * Save the workBook to output stream.
     * @param pOutput the output stream
     * @throws JDataException on error
     */
    public void saveToStream(final OutputStream pOutput) throws JDataException {
        try {
            theBook.save(pOutput);
        } catch (Exception e) {
            throw new JDataException(ExceptionClass.EXCEL, "Failed to save workbook", e);
        }
    }

    /**
     * Obtain a new named sheet.
     * @param pName the name of the sheet
     * @param pNumRows the number of rows to allocate
     * @param pNumCols the number of columns to allocate
     * @return the new sheet.
     */
    protected DataSheet newSheet(final String pName,
                                 final int pNumRows,
                                 final int pNumCols) {
        /* Create the new Sheet */
        TableTableElement myElement = theContents.newTableTableElement();
        myElement.setTableNameAttribute(pName);
        myElement.setTableStyleNameAttribute(STYLE_TABLE);

        /* Create the columns */
        TableTableColumnElement myCol = myElement.newTableTableColumnElement();
        if (pNumCols > 1) {
            myCol.setTableNumberColumnsRepeatedAttribute(pNumCols);
        }

        /* Create the rows */
        TableTableRowElement myRow = myElement.newTableTableRowElement();
        myRow.setTableStyleNameAttribute(STYLE_ROW);
        if (pNumRows > 1) {
            myRow.setTableNumberRowsRepeatedAttribute(pNumRows);
        }

        /* Create the cells */
        TableTableCellElement myCell = new TableTableCellElement(theContentDom);
        myRow.appendChild(myCell);
        if (pNumCols > 1) {
            myCell.setTableNumberColumnsRepeatedAttribute(pNumCols);
        }

        /* Create the sheet representation */
        SheetReference myRef = new SheetReference(myElement);
        myRef.addToMap();
        return myRef.getSheet();
    }

    /**
     * Obtain a new named sheet.
     * @param pName the name of the sheet
     * @return the new sheet.
     */
    protected DataSheet newSheet(final String pName) {
        return newSheet(pName, 1, 1);
    }

    /**
     * Obtain a named sheet.
     * @param pName the name of the sheet
     * @return the sheet.
     */
    protected DataSheet getSheet(final String pName) {
        /* Obtain the existing sheet */
        SheetReference myRef = theSheetMap.get(pName);
        return (myRef == null)
                ? null
                : myRef.getReadOnlySheet();
    }

    /**
     * Obtain a view of the named range.
     * @param pName the name of the range
     * @return the view of the range
     * @throws JDataException on error
     */
    protected DataView getRangeView(final String pName) throws JDataException {
        /* Locate the named range in the map */
        TableNamedRangeElement myRange = theRangeMap.get(pName);
        if (myRange == null) {
            throw new JDataException(ExceptionClass.EXCEL, "Name "
                                                           + pName
                                                           + " not found in workbook");
        }

        /* Obtain the address */
        String myAddress = myRange.getTableCellRangeAddressAttribute();
        OasisCellRange myCellRange = new OasisCellRange(myAddress);
        OasisCellAddress myFirstCell = myCellRange.getFirstCell();
        OasisCellAddress myLastCell = myCellRange.getLastCell();

        /* Obtain the sheet and reject if missing */
        DataSheet mySheet = getSheet(myFirstCell.getSheetName());
        if (mySheet == null) {
            throw new JDataException(ExceptionClass.EXCEL, "Sheet for "
                                                           + pName
                                                           + " not found in workbook");
        }

        /* Return the view */
        return new DataView(mySheet, myFirstCell.getPosition(), myLastCell.getPosition());
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
            TableTableElement myTable = (TableTableElement) myNode;
            SheetReference myRef = new SheetReference(myTable);
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
            TableNamedRangeElement myRange = (TableNamedRangeElement) myNode;
            theRangeMap.put(myRange.getTableNameAttribute(), myRange);
        }
    }

    /**
     * Declare the named range.
     * @param pName the name of the range
     * @param pRange the range to declare
     * @throws JDataException on error
     */
    protected void declareRange(final String pName,
                                final OasisCellRange pRange) throws JDataException {
        /* Check for existing range */
        if (theRangeMap.get(pName) != null) {
            throw new JDataException(ExceptionClass.EXCEL, "Name "
                                                           + pName
                                                           + "already exists in workbook");
        }

        /* Protect against exceptions */
        try {
            /* Add the new range */
            TableNamedRangeElement myRange = theExpressions.newTableNamedRangeElement(pRange.toString(), pName);
            myRange.setTableBaseCellAddressAttribute(pRange.getFirstCell().toString());
            theRangeMap.put(pName, myRange);
        } catch (Exception e) {
            throw new JDataException(ExceptionClass.EXCEL, "Failed to declare range", e);
        }
    }

    /**
     * Apply Data Validation.
     * @param pSheet the workSheet containing the cells
     * @param pFirstCell the the first cell in the range
     * @param pLastCell the last cell in the range
     * @param pValidRange the name of the validation range
     * @throws JDataException on error
     */
    protected void applyDataValidation(final OasisSheet pSheet,
                                       final CellPosition pFirstCell,
                                       final CellPosition pLastCell,
                                       final String pValidRange) throws JDataException {
        /* Access constraint */
        TableContentValidationElement myConstraint = theConstraintMap.get(pValidRange);
        String myName;
        if (myConstraint == null) {
            /* Create a name for the constraint */
            myName = "val"
                     + ++theNumConstraints;

            /* Create the new constraint */
            myConstraint = theValidations.newTableContentValidationElement(myName);

            /* Create the rule */
            String myRule = "of:cell-content-is-in-list("
                            + pValidRange
                            + ")";
            myConstraint.setTableConditionAttribute(myRule);
            myConstraint.setTableAllowEmptyCellAttribute(Boolean.TRUE);
            TableErrorMessageElement myError = myConstraint.newTableErrorMessageElement();
            myError.setTableDisplayAttribute(Boolean.TRUE);
            myError.setTableMessageTypeAttribute(TableMessageTypeAttribute.Value.STOP.toString());

            /* Store the constraint */
            theConstraintMap.put(pValidRange, myConstraint);
        }

        /* Determine size of range */
        myName = myConstraint.getTableNameAttribute();
        int iRow = pFirstCell.getRowIndex();
        int iLastRow = pLastCell.getRowIndex();
        int iFirstCol = pFirstCell.getColumnIndex();
        int iLastCol = pLastCell.getColumnIndex();

        /* Loop through the rows */
        for (OasisRow myRow = pSheet.getMutableRowByIndex(iRow); iRow <= iLastRow; iRow++, myRow = pSheet.getMutableRowByIndex(iRow)) {
            /* Loop through the columns */
            for (int iCol = iFirstCol; iCol <= iLastCol; iCol++) {
                /* Access the cell and set the constraint */
                OasisCell myCell = myRow.getMutableCellByIndex(iCol);
                myCell.setValidationName(myName);
            }
        }
    }

    /**
     * Apply Data Filter.
     * @param pRange the range to apply the filter to
     * @throws JDataException on error
     */
    protected void applyDataFilter(final OasisCellRange pRange) throws JDataException {
        /* Create the new filter */
        TableDatabaseRangeElement myFilter = theFilters.newTableDatabaseRangeElement("Events.E1:Events.E15");
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
        Node myParent = pRef.getParentNode();

        /* Obtain the next element */
        Node myNextElement = pRef.getNextSibling();
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
        Node myParent = pRef.getParentNode();

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
    protected static String getColumnStyleName(final CellStyleType pType) {
        return "csn"
               + pType.name();
    }

    /**
     * Obtain column width.
     * @param pWidth the character width
     * @return the name of the style
     */
    protected String getStyleWidth(final int pWidth) {
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
                                    final CellStyleType pType) {
        /* Switch on type */
        switch (pType) {
            case Date:
                theStyles.appendChild(new OdfNumberDateStyle(theContentDom, pFormat, pStyleName));
                break;
            case Rate:
                theStyles.appendChild(new OdfNumberPercentageStyle(theContentDom, pFormat, pStyleName));
                break;
            default:
                theStyles.appendChild(new OdfNumberStyle(theContentDom, pFormat, pStyleName));
                break;
        }
    }

    /**
     * Create the standard CellStyles.
     */
    private void createCellStyles() {
        /* Access create styles holders */
        theBook.getOrCreateDocumentStyles();

        /* Create the Date Column Style */
        OdfStyle myStyle = theStyles.newStyle(OdfStyleFamily.TableColumn);
        String myName = getColumnStyleName(CellStyleType.Date);
        myStyle.setStyleNameAttribute(myName);
        myStyle.setProperty(OdfTableColumnProperties.ColumnWidth, getStyleWidth(DataWorkBook.WIDTH_DATE));
        theStyleMap.put(myName, myStyle);

        /* Create the Money Column Style */
        myStyle = theStyles.newStyle(OdfStyleFamily.TableColumn);
        myName = getColumnStyleName(CellStyleType.Money);
        myStyle.setStyleNameAttribute(myName);
        myStyle.setProperty(OdfTableColumnProperties.ColumnWidth, getStyleWidth(DataWorkBook.WIDTH_MONEY));
        theStyleMap.put(myName, myStyle);

        /* Create the Price Column Style */
        myStyle = theStyles.newStyle(OdfStyleFamily.TableColumn);
        myName = getColumnStyleName(CellStyleType.Price);
        myStyle.setStyleNameAttribute(myName);
        myStyle.setProperty(OdfTableColumnProperties.ColumnWidth, getStyleWidth(DataWorkBook.WIDTH_PRICE));
        theStyleMap.put(myName, myStyle);

        /* Create the Units Column Style */
        myStyle = theStyles.newStyle(OdfStyleFamily.TableColumn);
        myName = getColumnStyleName(CellStyleType.Units);
        myStyle.setStyleNameAttribute(myName);
        myStyle.setProperty(OdfTableColumnProperties.ColumnWidth, getStyleWidth(DataWorkBook.WIDTH_UNITS));
        theStyleMap.put(myName, myStyle);

        /* Create the Rate Column Style */
        myStyle = theStyles.newStyle(OdfStyleFamily.TableColumn);
        myName = getColumnStyleName(CellStyleType.Rate);
        myStyle.setStyleNameAttribute(myName);
        myStyle.setProperty(OdfTableColumnProperties.ColumnWidth, getStyleWidth(DataWorkBook.WIDTH_RATE));
        theStyleMap.put(myName, myStyle);

        /* Create the Dilution Column Style */
        myStyle = theStyles.newStyle(OdfStyleFamily.TableColumn);
        myName = getColumnStyleName(CellStyleType.Dilution);
        myStyle.setStyleNameAttribute(myName);
        myStyle.setProperty(OdfTableColumnProperties.ColumnWidth, getStyleWidth(DataWorkBook.WIDTH_DILUTION));
        theStyleMap.put(myName, myStyle);

        /* Create the Ratio Column Style */
        myStyle = theStyles.newStyle(OdfStyleFamily.TableColumn);
        myName = getColumnStyleName(CellStyleType.Ratio);
        myStyle.setStyleNameAttribute(myName);
        myStyle.setProperty(OdfTableColumnProperties.ColumnWidth, getStyleWidth(DataWorkBook.WIDTH_RATIO));
        theStyleMap.put(myName, myStyle);

        /* Create the Integer Column Style */
        myStyle = theStyles.newStyle(OdfStyleFamily.TableColumn);
        myName = getColumnStyleName(CellStyleType.Integer);
        myStyle.setStyleNameAttribute(myName);
        myStyle.setProperty(OdfTableColumnProperties.ColumnWidth, getStyleWidth(DataWorkBook.WIDTH_INT));
        theStyleMap.put(myName, myStyle);

        /* Create the Boolean Column Style */
        myStyle = theStyles.newStyle(OdfStyleFamily.TableColumn);
        myName = getColumnStyleName(CellStyleType.Boolean);
        myStyle.setStyleNameAttribute(myName);
        myStyle.setProperty(OdfTableColumnProperties.ColumnWidth, getStyleWidth(DataWorkBook.WIDTH_BOOL));
        theStyleMap.put(myName, myStyle);

        /* Create the String Column Style */
        myStyle = theStyles.newStyle(OdfStyleFamily.TableColumn);
        myName = getColumnStyleName(CellStyleType.String);
        myStyle.setStyleNameAttribute(myName);
        myStyle.setProperty(OdfTableColumnProperties.ColumnWidth, getStyleWidth(DataWorkBook.WIDTH_STRING));
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
    private String getStyleAlignment(final CellStyleType pType) {
        switch (pType) {
            case Header:
            case Boolean:
                return ALIGN_CENTER;
            case Date:
            case String:
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
    private String getStyleFont(final CellStyleType pType) {
        switch (pType) {
            case Header:
            case Boolean:
            case String:
                return DataWorkBook.FONT_VALUE;
            default:
                return DataWorkBook.FONT_NUMERIC;
        }
    }

    /**
     * Obtain the required CellStyle.
     * @param pType the CellStyleType
     * @return the required CellStyle
     */
    protected OdfStyle getCellStyle(final CellStyleType pType) {
        /* Determine the correct format */
        String myStyleName = DataFormats.getFormatName(pType);

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
        if (DataFormats.hasDataFormat(pType)) {
            /* Determine the format */
            String myFormat = DataFormats.getDataFormatString(pType);
            String myFormatName = getDataStyleName(myStyleName);
            createNumericStyle(myFormat, myFormatName, pType);
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
        String myStyleName = DataFormats.getFormatName(pValue);

        /* Look for existing format */
        OdfStyle myStyle = theStyleMap.get(myStyleName);
        if (myStyle != null) {
            return myStyle;
        }

        /* Determine the CellStyleType */
        CellStyleType myType = DataFormats.getCellStyleType(pValue);

        /* Create the New Cell Style */
        myStyle = theStyles.newStyle(OdfStyleFamily.TableCell);
        myStyle.setStyleNameAttribute(myStyleName);
        myStyle.setStyleParentStyleNameAttribute(STYLE_DEFPARENT);
        myStyle.setProperty(OdfTextProperties.FontName, getStyleFont(myType));
        myStyle.setProperty(OdfTextProperties.FontSize, FONT_SIZE);
        myStyle.setProperty(OdfParagraphProperties.TextAlign, getStyleAlignment(myType));

        /* If we have a data format */
        if (DataFormats.hasDataFormat(myType)) {
            /* Determine the format */
            String myFormat = DataFormats.getDataFormatString(pValue);
            String myFormatName = getDataStyleName(myStyleName);
            createNumericStyle(myFormat, myFormatName, myType);
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
        String myStyleName = DataFormats.getAlternateFormatName(pValue);

        /* Look for existing format */
        OdfStyle myStyle = theStyleMap.get(myStyleName);
        if (myStyle != null) {
            return myStyle;
        }

        /* Create the New Cell Style */
        myStyle = theStyles.newStyle(OdfStyleFamily.TableCell);

        /* Determine the CellStyleType */
        CellStyleType myType = DataFormats.getCellStyleType(pValue);

        /* Handle the header style */
        if (myType == CellStyleType.String) {
            myType = CellStyleType.Header;
            myStyle.setProperty(OdfTextProperties.FontWeight, FONT_BOLD);
            myStyle.setProperty(OdfTableCellProperties.CellProtect, Boolean.TRUE.toString());
        }

        /* Create the New Cell Style */
        myStyle.setStyleNameAttribute(myStyleName);
        myStyle.setStyleParentStyleNameAttribute(STYLE_DEFPARENT);
        myStyle.setProperty(OdfTextProperties.FontName, getStyleFont(myType));
        myStyle.setProperty(OdfTextProperties.FontSize, FONT_SIZE);
        myStyle.setProperty(OdfParagraphProperties.TextAlign, getStyleAlignment(myType));

        /* If we have a data format */
        if (DataFormats.hasDataFormat(myType)) {
            /* Determine the format */
            String myFormat = DataFormats.getDataFormatString(pValue);
            String myFormatName = getDataStyleName(myStyleName);
            createNumericStyle(myFormat, myFormatName, myType);
            myStyle.setStyleDataStyleNameAttribute(myFormatName);
        }

        /* Add to the map and return new style */
        theStyleMap.put(myStyleName, myStyle);
        return myStyle;
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
        private OasisSheet getReadOnlySheet() {
            return new OasisSheet(theWorkBook, theElement, theIndex, true);
        }

        /**
         * Obtain Sheet representation.
         * @return the sheet representation
         */
        private OasisSheet getSheet() {
            return new OasisSheet(theWorkBook, theElement, theIndex, false);
        }
    }
}
