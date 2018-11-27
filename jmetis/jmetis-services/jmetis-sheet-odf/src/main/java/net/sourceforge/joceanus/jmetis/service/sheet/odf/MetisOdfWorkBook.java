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

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.sourceforge.joceanus.jmetis.service.sheet.MetisSheetCellAddress;
import net.sourceforge.joceanus.jmetis.service.sheet.MetisSheetCellPosition;
import net.sourceforge.joceanus.jmetis.service.sheet.MetisSheetCellRange;
import net.sourceforge.joceanus.jmetis.service.sheet.MetisSheetException;
import net.sourceforge.joceanus.jmetis.service.sheet.MetisSheetSheet;
import net.sourceforge.joceanus.jmetis.service.sheet.MetisSheetView;
import net.sourceforge.joceanus.jmetis.service.sheet.MetisSheetWorkBook;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.TethysDataFormatter;

/**
 * Odf WorkBook.
 */
public class MetisOdfWorkBook
        implements MetisSheetWorkBook {
    /**
     * The Contents Document.
     */
    private final Document theContents;

    /**
     * The Parser.
     */
    private final MetisOdfParser theParser;

    /**
     * The Styler.
     */
    private final MetisOdfStyler theStyler;

    /**
     * Map of Sheets.
     */
    private final Map<String, SheetReference> theSheetMap;

    /**
     * Map of Ranges.
     */
    private final Map<String, Element> theRangeMap;

    /**
     * Map of Constraints.
     */
    private final Map<Object, Element> theConstraintMap;

    /**
     * Data formatter.
     */
    private final TethysDataFormatter theDataFormatter;

    /**
     * Count of table elements.
     */
    private int theNumTables;

    /**
     * Count of table constraints.
     */
    private int theNumConstraints;

    /**
     * Count of dataBaseRanges.
     */
    private int theNumDBRanges;

    /**
     * Is the workBook readOnly?
     */
    private final boolean isReadOnly;

    /**
     * Constructor.
     * @param pInput the input stream
     * @throws OceanusException on error
     */
    MetisOdfWorkBook(final InputStream pInput) throws OceanusException {
        /* Load the contents of the spreadSheet */
        theContents = MetisOdfLoader.loadNewSpreadSheet(pInput);
        theParser = new MetisOdfParser(theContents);
        theStyler = null;

        /* Allocate the formatter */
        theDataFormatter = createFormatter();

        /* Note readOnly */
        isReadOnly = true;

        /* Allocate the maps */
        theSheetMap = new HashMap<>();
        theRangeMap = new HashMap<>();
        theConstraintMap = null;

        /* Build the maps */
        buildSheetMap();
        buildRangeMap();
    }

    /**
     * Constructor.
     * @throws OceanusException on error
     */
    public MetisOdfWorkBook() throws OceanusException {
        /* Create empty workBook */
        theContents = MetisOdfLoader.loadInitialSpreadSheet();
        theParser = new MetisOdfParser(theContents);
        theStyler = new MetisOdfStyler(theParser);

        /* Allocate the formatter */
        theDataFormatter = createFormatter();

        /* Allocate the maps */
        theSheetMap = new HashMap<>();
        theRangeMap = new HashMap<>();
        theConstraintMap = new HashMap<>();

        /* Note writable */
        isReadOnly = false;
    }

    @Override
    public boolean isReadOnly() {
        return isReadOnly;
    }

    /**
     * Obtain the parser.
     * @return the parser
     */
    MetisOdfParser getParser() {
        return theParser;
    }

    /**
     * Obtain the styler.
     * @return the styler
     */
    MetisOdfStyler getStyler() {
        return theStyler;
    }

    /**
     * Obtain the data formatter.
     * @return the formatter
     */
    TethysDataFormatter getFormatter() {
        return theDataFormatter;
    }

    @Override
    public void saveToStream(final OutputStream pOutput) throws OceanusException {
        /* Write to a completely new spreadSheet */
        MetisOdfLoader.writeNewSpreadSheet(theContents, pOutput);
    }

    /**
     * Build sheet map.
     */
    private void buildSheetMap() {
        /* Access the list of tables */
        final Element myMain = theContents.getDocumentElement();
        final Element myBody = theParser.getFirstNamedChild(myMain, MetisOdfOfficeItem.BODY);
        final Element mySpreadSheet = theParser.getFirstNamedChild(myBody, MetisOdfOfficeItem.SPREADSHEET);
        final List<Element> mySheets = theParser.getAllNamedChildren(mySpreadSheet, MetisOdfTableItem.TABLE);

        /* Loop through the list */
        for (Element mySheet : mySheets) {
            /* Add sheet to map */
            final SheetReference myRef = new SheetReference(mySheet);
            myRef.addToMap();
        }
    }

    @Override
    public MetisSheetSheet newSheet(final String pName,
                                    final int pNumRows,
                                    final int pNumCols) {
        /* Create the new Sheet */
        final Element myTable = theParser.newElement(MetisOdfTableItem.TABLE);
        theParser.setAttribute(myTable, MetisOdfTableItem.NAME, pName);
        theParser.setAttribute(myTable, MetisOdfTableItem.STYLENAME, MetisOdfStyler.STYLE_TABLE);

        /* Access the expressions */
        final Element myMain = theContents.getDocumentElement();
        final Element myBody = theParser.getFirstNamedChild(myMain, MetisOdfOfficeItem.BODY);
        final Element mySpreadSheet = theParser.getFirstNamedChild(myBody, MetisOdfOfficeItem.SPREADSHEET);
        final Element myExpressions = theParser.getFirstNamedChild(mySpreadSheet, MetisOdfTableItem.EXPRESSIONS);
        MetisOdfParser.addAsPriorSibling(myTable, myExpressions);

        /* Create the columns */
        final Element myCol = theParser.newElement(MetisOdfTableItem.COLUMN);
        myTable.appendChild(myCol);
        if (pNumCols > 1) {
            theParser.setAttribute(myCol, MetisOdfTableItem.COLUMNREPEAT, pNumCols);
        }

        /* Create the rows */
        final Element myRow = theParser.newElement(MetisOdfTableItem.ROW);
        myTable.appendChild(myRow);
        theParser.setAttribute(myRow, MetisOdfTableItem.STYLENAME, MetisOdfStyler.STYLE_ROW);
        if (pNumCols > 1) {
            theParser.setAttribute(myRow, MetisOdfTableItem.ROWREPEAT, pNumCols);
        }

        /* Create the cells */
        final Element myCell = theParser.newElement(MetisOdfTableItem.CELL);
        myRow.appendChild(myCell);
        if (pNumCols > 1) {
            theParser.setAttribute(myCell, MetisOdfTableItem.COLUMNREPEAT, pNumCols);
        }

        /* Create the sheet representation */
        final SheetReference myRef = new SheetReference(myTable);
        myRef.addToMap();
        return myRef.getSheet();
    }

    @Override
    public MetisSheetSheet newSheet(final String pName) {
        return newSheet(pName, 1, 1);
    }

    @Override
    public MetisSheetSheet getSheet(final String pName) {
        /* Obtain the existing sheet */
        final SheetReference myRef = theSheetMap.get(pName);
        return myRef == null
               ? null
               : myRef.getReadOnlySheet();
    }

    /**
     * Build range map.
     */
    private void buildRangeMap() {
        /* Access the list of ranges */
        final Element myMain = theContents.getDocumentElement();
        final Element myBody = theParser.getFirstNamedChild(myMain, MetisOdfOfficeItem.BODY);
        final Element mySpreadSheet = theParser.getFirstNamedChild(myBody, MetisOdfOfficeItem.SPREADSHEET);
        final Element myExpressions = theParser.getFirstNamedChild(mySpreadSheet, MetisOdfTableItem.EXPRESSIONS);
        final List<Element> myRanges = theParser.getAllNamedChildren(myExpressions, MetisOdfTableItem.RANGE);

        /* Loop through the named ranges */
        for (Element myRange : myRanges) {
            /* Add range to map */
            theRangeMap.put(theParser.getAttribute(myRange, MetisOdfTableItem.NAME), myRange);
        }
    }

    @Override
    public MetisSheetView getRangeView(final String pName) throws OceanusException {
        /* Locate the named range in the map */
        final Element myNamedRange = theRangeMap.get(pName);
        if (myNamedRange == null) {
            return null;
        }

        /* Obtain the address */
        final String myRange = theParser.getAttribute(myNamedRange, MetisOdfTableItem.CELLRANGEADDRESS);
        final MetisSheetCellRange myCellRange = new MetisSheetCellRange(myRange);
        final MetisSheetCellPosition myFirstCell = myCellRange.getFirstCell().getPosition();
        final MetisSheetCellPosition myLastCell = myCellRange.getLastCell().getPosition();

        /* Obtain the sheet and reject if missing */
        final MetisSheetSheet mySheet = getSheet(myCellRange.getFirstCell().getSheetName());
        if (mySheet == null) {
          throw new MetisSheetException("Sheet for "
                    + pName
                    + " not found in workbook");
        }

        /* Return the view */
        return new MetisSheetView(mySheet, myFirstCell, myLastCell);
    }

    /**
     * Declare the named range.
     * @param pName the name of the range
     * @param pRange the range to declare
     * @throws OceanusException on error
     */
    void declareRange(final String pName,
                      final MetisSheetCellRange pRange) throws OceanusException {
        /* Check for existing range */
        if (theRangeMap.get(pName) != null) {
            throw new MetisSheetException("Name "
                    + pName
                    + "already exists in workbook");
        }

        /* Access the expressions */
        final Element myMain = theContents.getDocumentElement();
        final Element myBody = theParser.getFirstNamedChild(myMain, MetisOdfOfficeItem.BODY);
        final Element mySpreadSheet = theParser.getFirstNamedChild(myBody, MetisOdfOfficeItem.SPREADSHEET);
        final Element myExpressions = theParser.getFirstNamedChild(mySpreadSheet, MetisOdfTableItem.EXPRESSIONS);

        /* Protect against exceptions */
        try {
            /* Add the new range */
            final Element myRange = theParser.newElement(MetisOdfTableItem.RANGE);
            theParser.setAttribute(myRange, MetisOdfTableItem.NAME, pName);
            theParser.setAttribute(myRange, MetisOdfTableItem.CELLRANGEADDRESS, pRange.toString());
            theParser.setAttribute(myRange, MetisOdfTableItem.BASECELLADDRESS, pRange.getFirstCell().toString());
            myExpressions.appendChild(myRange);
            theRangeMap.put(pName, myRange);
        } catch (Exception e) {
            throw new MetisSheetException("Failed to declare range", e);
        }
    }

    /**
     * Apply Data Validation.
     * @param pSheet the workSheet containing the cells
     * @param pFirstCell the the first cell in the range
     * @param pLastCell the last cell in the range
     * @param pValidRange the name of the validation range
     */
    void applyDataValidation(final MetisOdfSheet pSheet,
                             final MetisSheetCellPosition pFirstCell,
                             final MetisSheetCellPosition pLastCell,
                             final String pValidRange) {
        /* Access constraint */
        Element myConstraint = theConstraintMap.get(pValidRange);
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
    protected void applyDataValidation(final MetisOdfSheet pSheet,
                                       final MetisSheetCellPosition pFirstCell,
                                       final MetisSheetCellPosition pLastCell,
                                       final String[] pValueList) {
        /* Access constraint */
        Element myConstraint = theConstraintMap.get(pValueList);
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
                myBuilder.append(MetisSheetCellAddress.escapeApostrophes(myValue));
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
    private void applyDataConstraint(final MetisOdfSheet pSheet,
                                     final MetisSheetCellPosition pFirstCell,
                                     final MetisSheetCellPosition pLastCell,
                                     final Element pConstraint) {
        /* Determine size of range */
        final String myName = theParser.getAttribute(pConstraint, MetisOdfTableItem.NAME);
        int iRow = pFirstCell.getRowIndex();
        final int iLastRow = pLastCell.getRowIndex();
        final int iFirstCol = pFirstCell.getColumnIndex();
        final int iLastCol = pLastCell.getColumnIndex();

        /* Loop through the rows */
        for (MetisOdfRow myRow = pSheet.getMutableRowByIndex(iRow); iRow <= iLastRow; iRow++, myRow = pSheet.getMutableRowByIndex(iRow)) {
            /* Loop through the columns */
            for (int iCol = iFirstCol; iCol <= iLastCol; iCol++) {
                /* Access the cell and set the constraint */
                final MetisOdfCell myCell = myRow.getMutableCellByIndex(iCol);
                myCell.setValidationName(myName);
            }
        }
    }

    /**
     * Create Data Constraint.
     * @param pConstraint the constraint list
     * @return the constraint
     */
    private Element createDataConstraint(final String pConstraint) {
        /* Build the name */
        final String myName = "val"
                + ++theNumConstraints;

        /* Access the constraints */
        final Element myMain = theContents.getDocumentElement();
        final Element myBody = theParser.getFirstNamedChild(myMain, MetisOdfOfficeItem.BODY);
        final Element mySpreadSheet = theParser.getFirstNamedChild(myBody, MetisOdfOfficeItem.SPREADSHEET);
        Element myConstraints = theParser.getFirstNamedChild(mySpreadSheet, MetisOdfTableItem.VALIDATIONS);
        if (myConstraints == null) {
            myConstraints = theParser.newElement(MetisOdfTableItem.VALIDATIONS);
            mySpreadSheet.insertBefore(myConstraints, mySpreadSheet.getFirstChild());
        }

        /* Create the new constraint */
        final Element myConstraint = theParser.newElement(MetisOdfTableItem.VALIDATION);
        myConstraints.appendChild(myConstraint);
        theParser.setAttribute(myConstraint, MetisOdfTableItem.NAME, myName);

        /* Create the rule */
        final String myRule = "of:cell-content-is-in-list("
                + pConstraint
                + ")";
        theParser.setAttribute(myConstraint, MetisOdfTableItem.CONDITION, myRule);
        theParser.setAttribute(myConstraint, MetisOdfTableItem.ALLOWEMPTYCELL, Boolean.TRUE);
        final Element myError = theParser.newElement(MetisOdfTableItem.ERRORMSG);
        myConstraint.appendChild(myError);
        theParser.setAttribute(myError, MetisOdfTableItem.DISPLAY, Boolean.TRUE);

        /* Store the constraint */
        return myConstraint;
    }

    /**
     * Apply Data Filter.
     * @param pRange the range
     */
    void applyDataFilter(final MetisSheetCellRange pRange) {
        /* Access the dbRanges */
        final Element myMain = theContents.getDocumentElement();
        final Element myBody = theParser.getFirstNamedChild(myMain, MetisOdfOfficeItem.BODY);
        final Element mySpreadSheet = theParser.getFirstNamedChild(myBody, MetisOdfOfficeItem.SPREADSHEET);
        Element myDBRanges = theParser.getFirstNamedChild(mySpreadSheet, MetisOdfTableItem.DATARANGES);
        if (myDBRanges == null) {
            myDBRanges = theParser.newElement(MetisOdfTableItem.DATARANGES);
            mySpreadSheet.appendChild(myDBRanges);
        }

        /* Build the name */
        final String myName = "dbRange"
                + ++theNumDBRanges;

        /* Create the new filter */
        final Element myFilter = theParser.newElement(MetisOdfTableItem.DATARANGE);
        theParser.setAttribute(myFilter, MetisOdfTableItem.NAME, myName);
        theParser.setAttribute(myFilter, MetisOdfTableItem.TARGETRANGE, pRange.toString());
        theParser.setAttribute(myFilter, MetisOdfTableItem.DISPLAYFILTER, Boolean.TRUE);
        theParser.setAttribute(myFilter, MetisOdfTableItem.ORIENTATION, MetisOdfValue.COLUMN);
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
        private final Element theElement;

        /**
         * Constructor.
         * @param pElement the sheet element
         */
        private SheetReference(final Element pElement) {
            /* Store parameters */
            theName = theParser.getAttribute(pElement, MetisOdfTableItem.NAME);
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
        private MetisOdfSheet getReadOnlySheet() {
            return new MetisOdfSheet(MetisOdfWorkBook.this, theElement, theIndex, true);
        }

        /**
         * Obtain Sheet representation.
         * @return the sheet representation
         */
        private MetisOdfSheet getSheet() {
            return new MetisOdfSheet(MetisOdfWorkBook.this, theElement, theIndex, false);
        }
    }
}
