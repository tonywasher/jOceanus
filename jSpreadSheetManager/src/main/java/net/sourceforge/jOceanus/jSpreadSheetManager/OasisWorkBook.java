/*******************************************************************************
 * jSpreadSheetManager: SpreadSheet management
 * Copyright 2013 Tony Washer
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
import java.util.EnumMap;
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
    private static final String ALIGN_CENT = "center";

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
     * Count of table elements.
     */
    private int theNumTables = 0;

    /**
     * Count of validation elements.
     */
    private int theNumConstraints = 0;

    /**
     * Map of Data styles.
     */
    private final Map<String, OasisSheet> theSheetMap;

    /**
     * Map of Data styles.
     */
    private final Map<OasisStyle, OdfStyle> theStyleMap;

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
            theStyleMap = null;
            theConstraintMap = null;

            /* Allocate the formatter and set date format */
            theFormatter = new JDataFormatter();
            theFormatter.setFormat(DataWorkBook.FORMAT_DATE);

            /* Allocate the maps */
            theSheetMap = new HashMap<String, OasisSheet>();
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

            /* Allocate the formatter and set date format */
            theFormatter = new JDataFormatter();
            theFormatter.setFormat(DataWorkBook.FORMAT_DATE);

            /* Clean out the document */
            cleanOutDocument();

            /* Create key elements */
            theValidations = theContents.newTableContentValidationsElement();
            theExpressions = theContents.newTableNamedExpressionsElement();
            theFilters = theContents.newTableDatabaseRangesElement();

            /* Allocate the maps */
            theSheetMap = new HashMap<String, OasisSheet>();
            theStyleMap = new EnumMap<OasisStyle, OdfStyle>(OasisStyle.class);
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
        myElement.setTableStyleNameAttribute(getStyleName(OasisStyle.Table));

        /* Create the columns */
        TableTableColumnElement myCol = myElement.newTableTableColumnElement();
        myCol.setTableStyleNameAttribute(getStyleName(OasisStyle.StringColumn));
        if (pNumCols > 1) {
            myCol.setTableNumberColumnsRepeatedAttribute(pNumCols);
        }

        /* Create the rows */
        TableTableRowElement myRow = myElement.newTableTableRowElement();
        myRow.setTableStyleNameAttribute(getStyleName(OasisStyle.Row));
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
        return new OasisSheet(this, myElement, theNumTables++);
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
        return theSheetMap.get(pName);
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
                                                           + "not found in workbook");
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
                                                           + "not found in workbook");
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
            OasisSheet mySheet = new OasisSheet(this, myTable, theNumTables++);
            theSheetMap.put(myTable.getTableNameAttribute(), mySheet);
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
        for (OasisRow myRow = pSheet.getRowByIndex(iRow); iRow <= iLastRow; iRow++, myRow = myRow.getNextRow()) {
            /* Loop through the columns */
            for (int iCol = iFirstCol; iCol <= iLastCol; iCol++) {
                /* Access the cell and set the constraint */
                OasisCell myCell = myRow.getCellByIndex(iCol);
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
     * Obtain data style name.
     * @param pStyle the style type
     * @return the name of the style
     */
    private static String getDataStyleName(final OasisStyle pStyle) {
        return "dsn"
               + pStyle;
    }

    /**
     * Obtain style name.
     * @param pStyle the style type
     * @return the name of the style
     */
    protected static String getStyleName(final OasisStyle pStyle) {
        return "sn"
               + pStyle;
    }

    /**
     * Obtain cell style.
     * @param pStyle the style type
     * @return the name of the style
     */
    protected static OasisStyle getOasisCellStyle(final CellStyleType pStyle) {
        switch (pStyle) {
            case Integer:
                return OasisStyle.IntegerCell;
            case Boolean:
                return OasisStyle.BooleanCell;
            case Date:
                return OasisStyle.DateCell;
            case Money:
                return OasisStyle.MoneyCell;
            case Price:
                return OasisStyle.PriceCell;
            case Units:
                return OasisStyle.UnitsCell;
            case Rate:
                return OasisStyle.RateCell;
            case Dilution:
                return OasisStyle.DilutionCell;
            case Header:
                return OasisStyle.HeaderCell;
            case String:
            default:
                return OasisStyle.StringCell;
        }
    }

    /**
     * Obtain column cell style.
     * @param pStyle the style type
     * @return the name of the style
     */
    protected static OasisStyle getOasisColumnStyle(final OasisStyle pStyle) {
        switch (pStyle) {
            case IntegerCell:
                return OasisStyle.IntegerColumn;
            case BooleanCell:
                return OasisStyle.BooleanColumn;
            case DateCell:
                return OasisStyle.DateColumn;
            case MoneyCell:
                return OasisStyle.MoneyColumn;
            case PriceCell:
                return OasisStyle.PriceColumn;
            case UnitsCell:
                return OasisStyle.UnitsColumn;
            case RateCell:
                return OasisStyle.RateColumn;
            case DilutionCell:
                return OasisStyle.DilutionColumn;
            case StringCell:
            default:
                return OasisStyle.StringColumn;
        }
    }

    /**
     * Obtain column cell width.
     * @param pStyle the style type
     * @return the name of the style
     */
    protected static int getOasisColumnWidth(final OasisStyle pStyle) {
        switch (pStyle) {
            case IntegerCell:
                return DataWorkBook.WIDTH_INT << 1;
            case BooleanCell:
                return DataWorkBook.WIDTH_BOOL << 1;
            case DateCell:
                return DataWorkBook.WIDTH_DATE << 1;
            case MoneyCell:
                return DataWorkBook.WIDTH_MONEY << 1;
            case PriceCell:
                return DataWorkBook.WIDTH_PRICE << 1;
            case UnitsCell:
                return DataWorkBook.WIDTH_UNITS << 1;
            case RateCell:
                return DataWorkBook.WIDTH_RATE << 1;
            case DilutionCell:
                return DataWorkBook.WIDTH_DILUTION << 1;
            case StringCell:
            default:
                return DataWorkBook.WIDTH_STRING << 1;
        }
    }

    /**
     * Obtain column width.
     * @param pChars the character in width
     * @return the name of the style
     */
    protected String getStyleWidth(final int pWidth) {
        return (pWidth << 1)
               + "mm";
    }

    /**
     * Create the standard CellStyles.
     */
    private void createCellStyles() {
        /* Access create styles holders */
        OdfOfficeAutomaticStyles myStyles = theContentDom.getOrCreateAutomaticStyles();
        theBook.getOrCreateDocumentStyles();

        /* Create the Date Cell Style */
        OdfNumberDateStyle myDateStyle = new OdfNumberDateStyle(theContentDom, "dd-MMM-yy", getDataStyleName(OasisStyle.DateCell));
        myStyles.appendChild(myDateStyle);
        OdfStyle myStyle = myStyles.newStyle(OdfStyleFamily.TableCell);
        myStyle.setStyleDataStyleNameAttribute(getDataStyleName(OasisStyle.DateCell));
        myStyle.setStyleNameAttribute(getStyleName(OasisStyle.DateCell));
        myStyle.setProperty(OdfTextProperties.FontName, DataWorkBook.FONT_NUMERIC);
        myStyle.setProperty(OdfTextProperties.FontSize, FONT_SIZE);
        myStyle.setProperty(OdfParagraphProperties.TextAlign, ALIGN_LEFT);
        theStyleMap.put(OasisStyle.DateCell, myStyle);

        /* Create the Date Column Style */
        myStyle = myStyles.newStyle(OdfStyleFamily.TableColumn);
        myStyle.setStyleNameAttribute(getStyleName(OasisStyle.DateColumn));
        myStyle.setProperty(OdfTableColumnProperties.ColumnWidth, getStyleWidth(DataWorkBook.WIDTH_DATE));
        theStyleMap.put(OasisStyle.DateColumn, myStyle);

        /* Create the Money Cell Style */
        OdfNumberStyle myNumberStyle = new OdfNumberStyle(theContentDom, "£#,##0.00", getDataStyleName(OasisStyle.MoneyCell));
        myStyles.appendChild(myNumberStyle);
        myStyle = myStyles.newStyle(OdfStyleFamily.TableCell);
        myStyle.setStyleDataStyleNameAttribute(getDataStyleName(OasisStyle.MoneyCell));
        myStyle.setStyleNameAttribute(getStyleName(OasisStyle.MoneyCell));
        myStyle.setProperty(OdfTextProperties.FontName, DataWorkBook.FONT_NUMERIC);
        myStyle.setProperty(OdfTextProperties.FontSize, FONT_SIZE);
        myStyle.setProperty(OdfParagraphProperties.TextAlign, ALIGN_RIGHT);
        theStyleMap.put(OasisStyle.MoneyCell, myStyle);

        /* Create the Money Column Style */
        myStyle = myStyles.newStyle(OdfStyleFamily.TableColumn);
        myStyle.setStyleNameAttribute(getStyleName(OasisStyle.MoneyColumn));
        myStyle.setProperty(OdfTableColumnProperties.ColumnWidth, getStyleWidth(DataWorkBook.WIDTH_MONEY));
        theStyleMap.put(OasisStyle.MoneyColumn, myStyle);

        /* Create the Price Cell Style */
        myNumberStyle = new OdfNumberStyle(theContentDom, "£#,##0.0000", getDataStyleName(OasisStyle.PriceCell));
        myStyles.appendChild(myNumberStyle);
        myStyle = myStyles.newStyle(OdfStyleFamily.TableCell);
        myStyle.setStyleDataStyleNameAttribute(getDataStyleName(OasisStyle.PriceCell));
        myStyle.setStyleNameAttribute(getStyleName(OasisStyle.PriceCell));
        myStyle.setProperty(OdfTextProperties.FontName, DataWorkBook.FONT_NUMERIC);
        myStyle.setProperty(OdfTextProperties.FontSize, FONT_SIZE);
        myStyle.setProperty(OdfParagraphProperties.TextAlign, ALIGN_RIGHT);
        theStyleMap.put(OasisStyle.PriceCell, myStyle);

        /* Create the Price Column Style */
        myStyle = myStyles.newStyle(OdfStyleFamily.TableColumn);
        myStyle.setStyleNameAttribute(getStyleName(OasisStyle.PriceColumn));
        myStyle.setProperty(OdfTableColumnProperties.ColumnWidth, getStyleWidth(DataWorkBook.WIDTH_PRICE));
        theStyleMap.put(OasisStyle.PriceColumn, myStyle);

        /* Create the Units Cell Style */
        myNumberStyle = new OdfNumberStyle(theContentDom, "#,##0.0000", getDataStyleName(OasisStyle.UnitsCell));
        myStyles.appendChild(myNumberStyle);
        myStyle = myStyles.newStyle(OdfStyleFamily.TableCell);
        myStyle.setStyleDataStyleNameAttribute(getDataStyleName(OasisStyle.UnitsCell));
        myStyle.setStyleNameAttribute(getStyleName(OasisStyle.UnitsCell));
        myStyle.setProperty(OdfTextProperties.FontName, DataWorkBook.FONT_NUMERIC);
        myStyle.setProperty(OdfTextProperties.FontSize, FONT_SIZE);
        myStyle.setProperty(OdfParagraphProperties.TextAlign, ALIGN_RIGHT);
        theStyleMap.put(OasisStyle.UnitsCell, myStyle);

        /* Create the Units Column Style */
        myStyle = myStyles.newStyle(OdfStyleFamily.TableColumn);
        myStyle.setStyleNameAttribute(getStyleName(OasisStyle.UnitsColumn));
        myStyle.setProperty(OdfTableColumnProperties.ColumnWidth, getStyleWidth(DataWorkBook.WIDTH_UNITS));
        theStyleMap.put(OasisStyle.UnitsColumn, myStyle);

        /* Create the Rate Cell Style */
        OdfNumberPercentageStyle myPercentStyle = new OdfNumberPercentageStyle(theContentDom, "0.00%", getDataStyleName(OasisStyle.RateCell));
        myStyles.appendChild(myPercentStyle);
        myStyle = myStyles.newStyle(OdfStyleFamily.TableCell);
        myStyle.setStyleDataStyleNameAttribute(getDataStyleName(OasisStyle.RateCell));
        myStyle.setStyleParentStyleNameAttribute("Default");
        myStyle.setStyleNameAttribute(getStyleName(OasisStyle.RateCell));
        myStyle.setProperty(OdfTextProperties.FontName, DataWorkBook.FONT_NUMERIC);
        myStyle.setProperty(OdfTextProperties.FontSize, FONT_SIZE);
        myStyle.setProperty(OdfParagraphProperties.TextAlign, ALIGN_RIGHT);
        theStyleMap.put(OasisStyle.RateCell, myStyle);

        /* Create the Rate Column Style */
        myStyle = myStyles.newStyle(OdfStyleFamily.TableColumn);
        myStyle.setStyleNameAttribute(getStyleName(OasisStyle.RateColumn));
        myStyle.setProperty(OdfTableColumnProperties.ColumnWidth, getStyleWidth(DataWorkBook.WIDTH_RATE));
        theStyleMap.put(OasisStyle.RateColumn, myStyle);

        /* Create the Dilution Cell Style */
        myNumberStyle = new OdfNumberStyle(theContentDom, "0.000000", getDataStyleName(OasisStyle.DilutionCell));
        myStyles.appendChild(myNumberStyle);
        myStyle = myStyles.newStyle(OdfStyleFamily.TableCell);
        myStyle.setStyleDataStyleNameAttribute(getDataStyleName(OasisStyle.DilutionCell));
        myStyle.setStyleParentStyleNameAttribute("Default");
        myStyle.setStyleNameAttribute(getStyleName(OasisStyle.DilutionCell));
        myStyle.setProperty(OdfTextProperties.FontName, DataWorkBook.FONT_NUMERIC);
        myStyle.setProperty(OdfTextProperties.FontSize, FONT_SIZE);
        myStyle.setProperty(OdfParagraphProperties.TextAlign, ALIGN_RIGHT);
        theStyleMap.put(OasisStyle.DilutionCell, myStyle);

        /* Create the Dilution Column Style */
        myStyle = myStyles.newStyle(OdfStyleFamily.TableColumn);
        myStyle.setStyleNameAttribute(getStyleName(OasisStyle.DilutionColumn));
        myStyle.setProperty(OdfTableColumnProperties.ColumnWidth, getStyleWidth(DataWorkBook.WIDTH_DILUTION));
        theStyleMap.put(OasisStyle.DilutionColumn, myStyle);

        /* Create the Integer Cell Style */
        myNumberStyle = new OdfNumberStyle(theContentDom, "0", getDataStyleName(OasisStyle.IntegerCell));
        myStyles.appendChild(myNumberStyle);
        myStyle = myStyles.newStyle(OdfStyleFamily.TableCell);
        myStyle.setStyleDataStyleNameAttribute(getDataStyleName(OasisStyle.IntegerCell));
        myStyle.setStyleNameAttribute(getStyleName(OasisStyle.IntegerCell));
        myStyle.setProperty(OdfTextProperties.FontName, DataWorkBook.FONT_NUMERIC);
        myStyle.setProperty(OdfTextProperties.FontSize, FONT_SIZE);
        myStyle.setProperty(OdfParagraphProperties.TextAlign, ALIGN_RIGHT);
        theStyleMap.put(OasisStyle.IntegerCell, myStyle);

        /* Create the Integer Column Style */
        myStyle = myStyles.newStyle(OdfStyleFamily.TableColumn);
        myStyle.setStyleNameAttribute(getStyleName(OasisStyle.IntegerColumn));
        myStyle.setProperty(OdfTableColumnProperties.ColumnWidth, getStyleWidth(DataWorkBook.WIDTH_INT));
        theStyleMap.put(OasisStyle.IntegerColumn, myStyle);

        /* Create the Boolean Cell Style */
        myNumberStyle = new OdfNumberStyle(theContentDom, "BOOLEAN", getDataStyleName(OasisStyle.BooleanCell));
        myStyles.appendChild(myNumberStyle);
        myStyle = myStyles.newStyle(OdfStyleFamily.TableCell);
        myStyle.setStyleDataStyleNameAttribute(getDataStyleName(OasisStyle.BooleanCell));
        myStyle.setStyleNameAttribute(getStyleName(OasisStyle.BooleanCell));
        myStyle.setProperty(OdfTextProperties.FontName, DataWorkBook.FONT_VALUE);
        myStyle.setProperty(OdfTextProperties.FontSize, FONT_SIZE);
        myStyle.setProperty(OdfParagraphProperties.TextAlign, ALIGN_CENT);
        theStyleMap.put(OasisStyle.BooleanCell, myStyle);

        /* Create the Boolean Column Style */
        myStyle = myStyles.newStyle(OdfStyleFamily.TableColumn);
        myStyle.setStyleNameAttribute(getStyleName(OasisStyle.BooleanColumn));
        myStyle.setProperty(OdfTableColumnProperties.ColumnWidth, getStyleWidth(DataWorkBook.WIDTH_BOOL));
        theStyleMap.put(OasisStyle.BooleanColumn, myStyle);

        /* Create the String Cell Style */
        myStyle = myStyles.newStyle(OdfStyleFamily.TableCell);
        myStyle.setStyleNameAttribute(getStyleName(OasisStyle.StringCell));
        myStyle.setProperty(OdfTextProperties.FontName, DataWorkBook.FONT_VALUE);
        myStyle.setProperty(OdfTextProperties.FontSize, FONT_SIZE);
        myStyle.setProperty(OdfParagraphProperties.TextAlign, ALIGN_LEFT);
        theStyleMap.put(OasisStyle.StringCell, myStyle);

        /* Create the Header Cell Style */
        myStyle = myStyles.newStyle(OdfStyleFamily.TableCell);
        myStyle.setStyleNameAttribute(getStyleName(OasisStyle.HeaderCell));
        myStyle.setProperty(OdfTextProperties.FontName, DataWorkBook.FONT_VALUE);
        myStyle.setProperty(OdfTextProperties.FontSize, FONT_SIZE);
        myStyle.setProperty(OdfTextProperties.FontWeight, FONT_BOLD);
        myStyle.setProperty(OdfParagraphProperties.TextAlign, ALIGN_CENT);
        theStyleMap.put(OasisStyle.HeaderCell, myStyle);

        /* Create the String Column Style */
        myStyle = myStyles.newStyle(OdfStyleFamily.TableColumn);
        myStyle.setStyleNameAttribute(getStyleName(OasisStyle.StringColumn));
        myStyle.setProperty(OdfTableColumnProperties.ColumnWidth, getStyleWidth(20));
        theStyleMap.put(OasisStyle.StringColumn, myStyle);

        /* Create the Table Style */
        myStyle = myStyles.newStyle(OdfStyleFamily.Table);
        myStyle.setStyleNameAttribute(getStyleName(OasisStyle.Table));
        myStyle.setProperty(OdfTableProperties.Display, Boolean.TRUE.toString());
        theStyleMap.put(OasisStyle.Table, myStyle);

        /* Create the Hidden Table Style */
        myStyle = myStyles.newStyle(OdfStyleFamily.Table);
        myStyle.setStyleNameAttribute(getStyleName(OasisStyle.HiddenTable));
        myStyle.setProperty(OdfTableProperties.Display, Boolean.FALSE.toString());
        theStyleMap.put(OasisStyle.HiddenTable, myStyle);

        /* Create the Row Style */
        myStyle = myStyles.newStyle(OdfStyleFamily.TableRow);
        myStyle.setStyleNameAttribute(getStyleName(OasisStyle.Row));
        myStyle.setProperty(OdfTableRowProperties.RowHeight, "5mm");
        theStyleMap.put(OasisStyle.Row, myStyle);
    }

    /**
     * Obtain the required CellStyle.
     * @param pType the OasisStyle
     * @return the required CellStyle
     */
    protected OdfStyle getCellStyle(final OasisStyle pStyle) {
        return theStyleMap.get(pStyle);
    }

    /**
     * OasisCellStyle.
     */
    protected enum OasisStyle {
        /**
         * Visible table.
         */
        Table,

        /**
         * Hidden Table.
         */
        HiddenTable,

        /**
         * Row.
         */
        Row,

        /**
         * Integer Cell.
         */
        IntegerCell,

        /**
         * Integer Column.
         */
        IntegerColumn,

        /**
         * Boolean Cell.
         */
        BooleanCell,

        /**
         * Boolean Column.
         */
        BooleanColumn,

        /**
         * Date Cell.
         */
        DateCell,

        /**
         * Date Column.
         */
        DateColumn,

        /**
         * Money Cell.
         */
        MoneyCell,

        /**
         * Money Column.
         */
        MoneyColumn,

        /**
         * Price Cell.
         */
        PriceCell,

        /**
         * Price Column.
         */
        PriceColumn,

        /**
         * Rate Cell.
         */
        RateCell,

        /**
         * Rate Column.
         */
        RateColumn,

        /**
         * Units Cell.
         */
        UnitsCell,

        /**
         * Units Column.
         */
        UnitsColumn,

        /**
         * Dilution Cell.
         */
        DilutionCell,

        /**
         * Dilution Column.
         */
        DilutionColumn,

        /**
         * String Cell.
         */
        StringCell,

        /**
         * String Column.
         */
        StringColumn,

        /**
         * Header Cell.
         */
        HeaderCell;
    }
}
