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
import net.sourceforge.jOceanus.jSpreadSheetManager.DataWorkBook.CellStyleType;
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
import org.odftoolkit.odfdom.dom.element.table.TableTableCellElementBase;
import org.odftoolkit.odfdom.dom.element.table.TableTableColumnElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableRowElement;
import org.odftoolkit.odfdom.dom.style.OdfStyleFamily;
import org.odftoolkit.odfdom.dom.style.props.OdfParagraphProperties;
import org.odftoolkit.odfdom.dom.style.props.OdfTextProperties;
import org.odftoolkit.odfdom.incubator.doc.number.OdfNumberDateStyle;
import org.odftoolkit.odfdom.incubator.doc.number.OdfNumberPercentageStyle;
import org.odftoolkit.odfdom.incubator.doc.number.OdfNumberStyle;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeAutomaticStyles;
import org.odftoolkit.odfdom.incubator.doc.style.OdfStyle;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.Row;
import org.odftoolkit.simple.table.Table;
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
    private final Map<CellStyleType, OdfStyle> theStyleMap;

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

            /* Clean out the document */
            cleanOutDocument();

            /* Create key elements */
            theValidations = theContents.newTableContentValidationsElement();
            theExpressions = theContents.newTableNamedExpressionsElement();
            theFilters = theContents.newTableDatabaseRangesElement();

            /* Allocate the maps */
            theSheetMap = new HashMap<String, OasisSheet>();
            theStyleMap = new EnumMap<CellStyleType, OdfStyle>(CellStyleType.class);
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

        /* Create the columns */
        TableTableColumnElement myCol = myElement.newTableTableColumnElement();
        myCol.setTableDefaultCellStyleNameAttribute("Default");
        if (pNumCols > 1) {
            myCol.setTableNumberColumnsRepeatedAttribute(pNumCols);
        }

        /* Create the rows */
        TableTableRowElement myRow = myElement.newTableTableRowElement();
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
        /* Loop through the named ranges */
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
    public void applyDataValidation(final Table pSheet,
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
        int iFirstRow = pFirstCell.getRowIndex();
        int iLastRow = pLastCell.getRowIndex();
        int iFirstCol = pFirstCell.getColumnIndex();
        int iLastCol = pLastCell.getColumnIndex();
        int iRow;
        Row myRow;

        /* Loop through the rows */
        for (iRow = iFirstRow, myRow = pSheet.getRowByIndex(iRow); iRow <= iLastRow; iRow++, myRow = myRow.getNextRow()) {
            /* Loop through the columns */
            for (int iCol = iFirstCol; iCol <= iLastCol; iCol++) {
                /* Access the cell and set the constraint */
                Cell myCell = myRow.getCellByIndex(iCol);
                TableTableCellElementBase myElement = myCell.getOdfElement();
                myElement.setTableContentValidationNameAttribute(myName);
            }
        }
    }

    /**
     * Apply Data Filter.
     * @param pRange the range to apply the filter to
     * @throws JDataException on error
     */
    public void applyDataFilter(final OasisCellRange pRange) throws JDataException {
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
     * @param pType the style type
     * @return the name of the style
     */
    protected String getDataStyleName(final CellStyleType pType) {
        return "dsn"
               + pType;
    }

    /**
     * Obtain style name.
     * @param pType the style type
     * @return the name of the style
     */
    protected String getStyleName(final CellStyleType pType) {
        return "sn"
               + pType;
    }

    /**
     * Create the standard CellStyles.
     */
    private void createCellStyles() {
        /* Access create styles holders */
        OdfOfficeAutomaticStyles myStyles = theContentDom.getOrCreateAutomaticStyles();
        theBook.getOrCreateDocumentStyles();

        /* Create the Date Cell Style */
        OdfNumberDateStyle myDateStyle = new OdfNumberDateStyle(theContentDom, "dd-MMM-yy", getDataStyleName(CellStyleType.Date));
        myStyles.appendChild(myDateStyle);
        OdfStyle myStyle = myStyles.newStyle(OdfStyleFamily.TableCell);
        myStyle.setStyleDataStyleNameAttribute(getDataStyleName(CellStyleType.Date));
        myStyle.setStyleNameAttribute(getStyleName(CellStyleType.Date));
        myStyle.setProperty(OdfTextProperties.FontName, DataWorkBook.FONT_NUMERIC);
        myStyle.setProperty(OdfParagraphProperties.TextAlign, ALIGN_LEFT);
        theStyleMap.put(CellStyleType.Date, myStyle);

        /* Create the Money Cell Style */
        OdfNumberStyle myNumberStyle = new OdfNumberStyle(theContentDom, "£#,##0.00", getDataStyleName(CellStyleType.Money));
        myStyles.appendChild(myNumberStyle);
        myStyle = myStyles.newStyle(OdfStyleFamily.TableCell);
        myStyle.setStyleDataStyleNameAttribute(getDataStyleName(CellStyleType.Money));
        myStyle.setStyleNameAttribute(getStyleName(CellStyleType.Money));
        myStyle.setProperty(OdfTextProperties.FontName, DataWorkBook.FONT_NUMERIC);
        myStyle.setProperty(OdfParagraphProperties.TextAlign, ALIGN_RIGHT);
        theStyleMap.put(CellStyleType.Money, myStyle);

        /* Create the Price Cell Style */
        myNumberStyle = new OdfNumberStyle(theContentDom, "£#,##0.0000", getDataStyleName(CellStyleType.Price));
        myStyles.appendChild(myNumberStyle);
        myStyle = myStyles.newStyle(OdfStyleFamily.TableCell);
        myStyle.setStyleDataStyleNameAttribute(getDataStyleName(CellStyleType.Price));
        myStyle.setStyleNameAttribute(getStyleName(CellStyleType.Price));
        myStyle.setProperty(OdfTextProperties.FontName, DataWorkBook.FONT_NUMERIC);
        myStyle.setProperty(OdfParagraphProperties.TextAlign, ALIGN_RIGHT);
        theStyleMap.put(CellStyleType.Price, myStyle);

        /* Create the Units Cell Style */
        myNumberStyle = new OdfNumberStyle(theContentDom, "#,##0.0000", getDataStyleName(CellStyleType.Units));
        myStyles.appendChild(myNumberStyle);
        myStyle = myStyles.newStyle(OdfStyleFamily.TableCell);
        myStyle.setStyleDataStyleNameAttribute(getDataStyleName(CellStyleType.Units));
        myStyle.setStyleNameAttribute(getStyleName(CellStyleType.Units));
        myStyle.setProperty(OdfTextProperties.FontName, DataWorkBook.FONT_NUMERIC);
        myStyle.setProperty(OdfParagraphProperties.TextAlign, ALIGN_RIGHT);
        theStyleMap.put(CellStyleType.Units, myStyle);

        /* Create the Rate Cell Style */
        OdfNumberPercentageStyle myPercentStyle = new OdfNumberPercentageStyle(theContentDom, "0.00%", getDataStyleName(CellStyleType.Rate));
        myStyles.appendChild(myPercentStyle);
        myStyle = myStyles.newStyle(OdfStyleFamily.TableCell);
        myStyle.setStyleDataStyleNameAttribute(getDataStyleName(CellStyleType.Rate));
        myStyle.setStyleNameAttribute(getStyleName(CellStyleType.Rate));
        myStyle.setProperty(OdfTextProperties.FontName, DataWorkBook.FONT_NUMERIC);
        myStyle.setProperty(OdfParagraphProperties.TextAlign, ALIGN_RIGHT);
        theStyleMap.put(CellStyleType.Rate, myStyle);

        /* Create the Dilution Cell Style */
        myNumberStyle = new OdfNumberStyle(theContentDom, "0.000000", getDataStyleName(CellStyleType.Dilution));
        myStyles.appendChild(myNumberStyle);
        myStyle = myStyles.newStyle(OdfStyleFamily.TableCell);
        myStyle.setStyleDataStyleNameAttribute(getDataStyleName(CellStyleType.Dilution));
        myStyle.setStyleNameAttribute(getStyleName(CellStyleType.Dilution));
        myStyle.setProperty(OdfTextProperties.FontName, DataWorkBook.FONT_NUMERIC);
        myStyle.setProperty(OdfParagraphProperties.TextAlign, ALIGN_RIGHT);
        theStyleMap.put(CellStyleType.Dilution, myStyle);

        /* Create the Integer Cell Style */
        myNumberStyle = new OdfNumberStyle(theContentDom, "0", getDataStyleName(CellStyleType.Integer));
        myStyles.appendChild(myNumberStyle);
        myStyle = myStyles.newStyle(OdfStyleFamily.TableCell);
        myStyle.setStyleDataStyleNameAttribute(getDataStyleName(CellStyleType.Integer));
        myStyle.setStyleNameAttribute(getStyleName(CellStyleType.Integer));
        myStyle.setProperty(OdfTextProperties.FontName, DataWorkBook.FONT_NUMERIC);
        myStyle.setProperty(OdfParagraphProperties.TextAlign, ALIGN_RIGHT);
        theStyleMap.put(CellStyleType.Integer, myStyle);

        /* Create the Boolean Cell Style */
        myNumberStyle = new OdfNumberStyle(theContentDom, "BOOLEAN", getDataStyleName(CellStyleType.Boolean));
        myStyles.appendChild(myNumberStyle);
        myStyle = myStyles.newStyle(OdfStyleFamily.TableCell);
        myStyle.setStyleDataStyleNameAttribute(getDataStyleName(CellStyleType.Boolean));
        myStyle.setStyleNameAttribute(getStyleName(CellStyleType.Boolean));
        myStyle.setProperty(OdfTextProperties.FontName, DataWorkBook.FONT_VALUE);
        myStyle.setProperty(OdfParagraphProperties.TextAlign, ALIGN_CENT);
        theStyleMap.put(CellStyleType.Boolean, myStyle);

        /* Create the String Cell Style */
        myStyle = myStyles.newStyle(OdfStyleFamily.TableCell);
        myStyle.setStyleNameAttribute(getStyleName(CellStyleType.String));
        myStyle.setProperty(OdfTextProperties.FontName, DataWorkBook.FONT_VALUE);
        myStyle.setProperty(OdfParagraphProperties.TextAlign, ALIGN_LEFT);
        theStyleMap.put(CellStyleType.String, myStyle);

        /* Create the Header Cell Style */
        myStyle = myStyles.newStyle(OdfStyleFamily.TableCell);
        myStyle.setStyleNameAttribute(getStyleName(CellStyleType.Header));
        myStyle.setProperty(OdfTextProperties.FontName, DataWorkBook.FONT_VALUE);
        myStyle.setProperty(OdfTextProperties.FontWeight, FONT_BOLD);
        myStyle.setProperty(OdfParagraphProperties.TextAlign, ALIGN_CENT);
        theStyleMap.put(CellStyleType.Header, myStyle);
    }

    /**
     * Obtain the required CellStyle.
     * @param pType the CellStyleType
     * @return the required CellStyle
     */
    protected OdfStyle getCellStyle(final CellStyleType pType) {
        return theStyleMap.get(pType);
    }
}
