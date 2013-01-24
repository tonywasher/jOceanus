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
import java.util.Map;

import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataManager.JDataException.ExceptionClass;
import net.sourceforge.jOceanus.jSpreadSheetManager.OasisCellAddress.OasisCellRange;
import net.sourceforge.jOceanus.jSpreadSheetManager.SheetWorkBook.CellStyleType;

import org.odftoolkit.odfdom.doc.OdfSpreadsheetDocument;
import org.odftoolkit.odfdom.doc.table.OdfTable;
import org.odftoolkit.odfdom.doc.table.OdfTableCell;
import org.odftoolkit.odfdom.doc.table.OdfTableCellRange;
import org.odftoolkit.odfdom.doc.table.OdfTableRow;
import org.odftoolkit.odfdom.dom.OdfContentDom;
import org.odftoolkit.odfdom.dom.element.office.OfficeSpreadsheetElement;
import org.odftoolkit.odfdom.dom.element.table.TableContentValidationElement;
import org.odftoolkit.odfdom.dom.element.table.TableContentValidationsElement;
import org.odftoolkit.odfdom.dom.element.table.TableNamedExpressionsElement;
import org.odftoolkit.odfdom.dom.element.table.TableNamedRangeElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableCellElementBase;
import org.odftoolkit.odfdom.dom.style.OdfStyleFamily;
import org.odftoolkit.odfdom.dom.style.props.OdfParagraphProperties;
import org.odftoolkit.odfdom.dom.style.props.OdfTextProperties;
import org.odftoolkit.odfdom.incubator.doc.number.OdfNumberDateStyle;
import org.odftoolkit.odfdom.incubator.doc.number.OdfNumberPercentageStyle;
import org.odftoolkit.odfdom.incubator.doc.number.OdfNumberStyle;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeAutomaticStyles;
import org.odftoolkit.odfdom.incubator.doc.style.OdfStyle;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.w3c.dom.Node;

/**
 * The Oasis WorkBook.
 */
public class OasisWorkBook {
    /**
     * Oasis WorkBook.
     */
    private final OdfSpreadsheetDocument theBook;

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
     * Map of Allocated styles.
     */
    private final Map<CellStyleType, OdfStyle> theMap;

    /**
     * Constructor.
     * @param pInput the input stream
     * @throws JDataException on error
     */
    public OasisWorkBook(final InputStream pInput) throws JDataException {
        try {
            /* Access book and contents */
            theBook = OdfSpreadsheetDocument.loadDocument(pInput);
            theContents = theBook.getContentRoot();
            theContentDom = theBook.getContentDom();
            theMap = null;

            /* Access Named expressions list */
            theExpressions = OdfElement.findFirstChildNode(TableNamedExpressionsElement.class, theContents);

            /* Access Contents validations list */
            theValidations = OdfElement.findFirstChildNode(TableContentValidationsElement.class, theContents);
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
            theBook = OdfSpreadsheetDocument.newSpreadsheetDocument();
            theContents = theBook.getContentRoot();
            theContentDom = theBook.getContentDom();

            /* Clean out the document */
            cleanOutDocument();

            /* Create new expressions/validations containers */
            theExpressions = theContents.newTableNamedExpressionsElement();
            theValidations = theContents.newTableContentValidationsElement();

            /* Allocate the style map */
            theMap = new EnumMap<CellStyleType, OdfStyle>(CellStyleType.class);

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
     * @return the new sheet.
     */
    protected SheetSheet newSheet(final String pName) {
        /* Create the new Sheet */
        OdfTable mySheet = OdfTable.newTable(theBook);
        mySheet.setTableName(pName);
        return getSheet(pName);
    }

    /**
     * Obtain a named sheet.
     * @param pName the name of the sheet
     * @return the sheet.
     */
    protected SheetSheet getSheet(final String pName) {
        /* Create the new Sheet */
        OdfTable mySheet = theBook.getTableByName(pName);
        return new SheetSheet(this, mySheet);
    }

    /**
     * Obtain a view of the named range.
     * @param pName the name of the range
     * @return the view of the range
     * @throws JDataException on error
     */
    protected SheetView getRangeView(final String pName) throws JDataException {
        /* Locate the named range in the list */
        TableNamedRangeElement myRange = lookUpRange(pName);
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
        SheetSheet mySheet = getSheet(myFirstCell.getSheetName());
        if (mySheet == null) {
            throw new JDataException(ExceptionClass.EXCEL, "Sheet for "
                                                           + pName
                                                           + "not found in workbook");
        }

        /* Return the view */
        return new SheetView(mySheet, myFirstCell.getPosition(), myLastCell.getPosition());
    }

    /**
     * LookUp the named range.
     * @param pName the name of the range
     * @return the resolved range (or null)
     */
    private TableNamedRangeElement lookUpRange(final String pName) {
        /* Locate the named range in the list */
        for (Node myNode = theExpressions.getFirstChild(); myNode != null; myNode = myNode.getNextSibling()) {
            if (!(myNode instanceof TableNamedRangeElement)) {
                continue;
            }
            TableNamedRangeElement myRange = (TableNamedRangeElement) myNode;
            if (myRange.getTableNameAttribute().equals(pName)) {
                return myRange;
            }
        }

        /* Handle not found */
        return null;
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
        if (lookUpRange(pName) != null) {
            throw new JDataException(ExceptionClass.EXCEL, "Name "
                                                           + pName
                                                           + "already exists in workbook");
        }

        /* Protect against exceptions */
        try {
            /* Add the new range */
            theExpressions.newTableNamedRangeElement(pRange.toString(), pName);
        } catch (Exception e) {
            throw new JDataException(ExceptionClass.EXCEL, "Failed to declare range", e);
        }
    }

    /**
     * LookUp the named constraint.
     * @param pName the name of the constraint
     * @return the resolved range (or null)
     */
    private TableContentValidationElement lookUpConstraint(final String pName) {
        /* Locate the named range in the list */
        for (Node myNode = theValidations.getFirstChild(); myNode != null; myNode = myNode.getNextSibling()) {
            if (!(myNode instanceof TableContentValidationElement)) {
                continue;
            }
            TableContentValidationElement myRange = (TableContentValidationElement) myNode;
            if (myRange.getTableNameAttribute().equals(pName)) {
                return myRange;
            }
        }

        /* Handle not found */
        return null;
    }

    /**
     * Apply Data Validation.
     * @param pSheet the workSheet containing the cells
     * @param pCells the Cells to apply validation to
     * @param pValidRange the name of the validation range
     * @throws JDataException on error
     */
    public void applyDataValidation(final OdfTable pSheet,
                                    final OdfTableCellRange pCells,
                                    final String pValidRange) throws JDataException {
        /* Create the rule name */
        String myName = "Validate"
                        + pValidRange;

        /* If the validation does not exist */
        TableContentValidationElement myConstraint = lookUpConstraint(myName);
        if (myConstraint == null) {
            /* Create the new constraint */
            myConstraint = theValidations.newTableContentValidationElement(myName);

            /* Create the rule */
            String myRule = "of:cell-content-is-in-list("
                            + pValidRange
                            + ")";
            myConstraint.setTableConditionAttribute(myRule);
        }

        /* Determine size of range */
        int iNumRows = pCells.getRowNumber();
        int iNumCols = pCells.getColumnNumber();

        /* Loop through the rows */
        for (int iRow = 0; iRow < iNumRows; iRow++) {
            /* Access the row */
            OdfTableRow myRow = pSheet.getRowByIndex(iRow);

            /* Loop through the columns */
            for (int iCol = 0; iCol < iNumCols; iRow++) {
                /* Access the cell and set the constraint */
                OdfTableCell myCell = myRow.getCellByIndex(iCol);
                TableTableCellElementBase myElement = myCell.getOdfElement();
                myElement.setTableContentValidationNameAttribute(myName);
            }
        }
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
     * Obtain style name.
     * @param pType the style type
     * @return the name of the style
     */
    protected String getStyleName(final CellStyleType pType) {
        return "style"
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
        OdfNumberDateStyle myDateStyle = new OdfNumberDateStyle(theContentDom, "yyyy-MM-dd", getStyleName(CellStyleType.Date));
        myStyles.appendChild(myDateStyle);
        OdfStyle myStyle = myStyles.newStyle(OdfStyleFamily.TableCell);
        myStyle.setStyleDataStyleNameAttribute(getStyleName(CellStyleType.Date));
        myStyle.setProperty(OdfTextProperties.FontName, SheetWorkBook.FONT_NUMERIC);
        myStyle.setProperty(OdfParagraphProperties.TextAlign, "left");
        theMap.put(CellStyleType.Date, myStyle);

        /* Create the Money Cell Style */
        OdfNumberStyle myNumberStyle = new OdfNumberStyle(theContentDom, "£#,##0.00", getStyleName(CellStyleType.Money));
        myStyles.appendChild(myNumberStyle);
        myStyle = myStyles.newStyle(OdfStyleFamily.TableCell);
        myStyle.setStyleDataStyleNameAttribute(getStyleName(CellStyleType.Money));
        myStyle.setProperty(OdfTextProperties.FontName, SheetWorkBook.FONT_NUMERIC);
        myStyle.setProperty(OdfParagraphProperties.TextAlign, "right");
        theMap.put(CellStyleType.Money, myStyle);

        /* Create the Price Cell Style */
        myNumberStyle = new OdfNumberStyle(theContentDom, "£#,##0.0000", getStyleName(CellStyleType.Price));
        myStyles.appendChild(myNumberStyle);
        myStyle = myStyles.newStyle(OdfStyleFamily.TableCell);
        myStyle.setStyleDataStyleNameAttribute(getStyleName(CellStyleType.Price));
        myStyle.setProperty(OdfTextProperties.FontName, SheetWorkBook.FONT_NUMERIC);
        myStyle.setProperty(OdfParagraphProperties.TextAlign, "right");
        theMap.put(CellStyleType.Price, myStyle);

        /* Create the Units Cell Style */
        myNumberStyle = new OdfNumberStyle(theContentDom, "#,##0.0000", getStyleName(CellStyleType.Units));
        myStyles.appendChild(myNumberStyle);
        myStyle = myStyles.newStyle(OdfStyleFamily.TableCell);
        myStyle.setStyleDataStyleNameAttribute(getStyleName(CellStyleType.Units));
        myStyle.setProperty(OdfTextProperties.FontName, SheetWorkBook.FONT_NUMERIC);
        myStyle.setProperty(OdfParagraphProperties.TextAlign, "right");
        theMap.put(CellStyleType.Units, myStyle);

        /* Create the Rate Cell Style */
        OdfNumberPercentageStyle myPercentStyle = new OdfNumberPercentageStyle(theContentDom, "0.00%", getStyleName(CellStyleType.Rate));
        myStyles.appendChild(myPercentStyle);
        myStyle = myStyles.newStyle(OdfStyleFamily.TableCell);
        myStyle.setStyleDataStyleNameAttribute(getStyleName(CellStyleType.Rate));
        myStyle.setProperty(OdfTextProperties.FontName, SheetWorkBook.FONT_NUMERIC);
        myStyle.setProperty(OdfParagraphProperties.TextAlign, "right");
        theMap.put(CellStyleType.Rate, myStyle);

        /* Create the Dilution Cell Style */
        myNumberStyle = new OdfNumberStyle(theContentDom, "0.000000", getStyleName(CellStyleType.Dilution));
        myStyles.appendChild(myNumberStyle);
        myStyle = myStyles.newStyle(OdfStyleFamily.TableCell);
        myStyle.setStyleDataStyleNameAttribute(getStyleName(CellStyleType.Dilution));
        myStyle.setProperty(OdfTextProperties.FontName, SheetWorkBook.FONT_NUMERIC);
        myStyle.setProperty(OdfParagraphProperties.TextAlign, "right");
        theMap.put(CellStyleType.Dilution, myStyle);

        /* Create the Integer Cell Style */
        myNumberStyle = new OdfNumberStyle(theContentDom, "0", getStyleName(CellStyleType.Integer));
        myStyles.appendChild(myNumberStyle);
        myStyle = myStyles.newStyle(OdfStyleFamily.TableCell);
        myStyle.setStyleDataStyleNameAttribute(getStyleName(CellStyleType.Integer));
        myStyle.setProperty(OdfTextProperties.FontName, SheetWorkBook.FONT_NUMERIC);
        myStyle.setProperty(OdfParagraphProperties.TextAlign, "right");
        theMap.put(CellStyleType.Integer, myStyle);

        /* Create the Boolean Cell Style */
        myStyle = myStyles.newStyle(OdfStyleFamily.TableCell);
        myStyle.setProperty(OdfTextProperties.FontName, SheetWorkBook.FONT_VALUE);
        myStyle.setProperty(OdfParagraphProperties.TextAlign, "center");
        theMap.put(CellStyleType.Boolean, myStyle);

        /* Create the String Cell Style */
        myStyle = myStyles.newStyle(OdfStyleFamily.TableCell);
        myStyle.setProperty(OdfTextProperties.FontName, SheetWorkBook.FONT_VALUE);
        myStyle.setProperty(OdfParagraphProperties.TextAlign, "left");
        theMap.put(CellStyleType.String, myStyle);

        /* Create the Header Cell Style */
        myStyle = myStyles.newStyle(OdfStyleFamily.TableCell);
        myStyle.setProperty(OdfTextProperties.FontName, SheetWorkBook.FONT_VALUE);
        myStyle.setProperty(OdfTextProperties.FontWeight, "bold");
        myStyle.setProperty(OdfParagraphProperties.TextAlign, "center");
        theMap.put(CellStyleType.Header, myStyle);
    }

    /**
     * Obtain the required CellStyle.
     * @param pType the CellStyleType
     * @return the required CellStyle
     */
    protected OdfStyle getCellStyle(final CellStyleType pType) {
        return theMap.get(pType);
    }
}
