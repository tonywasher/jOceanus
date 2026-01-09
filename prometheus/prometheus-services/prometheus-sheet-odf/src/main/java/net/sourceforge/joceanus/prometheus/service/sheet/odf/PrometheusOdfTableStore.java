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

import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.prometheus.service.sheet.PrometheusSheetCellAddress;
import net.sourceforge.joceanus.prometheus.service.sheet.PrometheusSheetCellPosition;
import net.sourceforge.joceanus.prometheus.service.sheet.PrometheusSheetCellRange;
import net.sourceforge.joceanus.prometheus.service.sheet.PrometheusSheetException;
import net.sourceforge.joceanus.prometheus.service.sheet.PrometheusSheetSheet;
import net.sourceforge.joceanus.prometheus.service.sheet.PrometheusSheetView;
import org.w3c.dom.Element;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Table storage.
 */
class PrometheusOdfTableStore {
    /**
     * The map of sheets.
     */
    private final Map<String, Object> theSheets;

    /**
     * The map of ranges.
     */
    private final Map<String, Element> theRanges;

    /**
     * The map of constraints.
     */
    private final Map<Object, Element> theConstraints;

    /**
     * The workBook.
     */
    private final PrometheusOdfWorkBook theBook;

    /**
     * The parser.
     */
    private final PrometheusOdfParser theParser;

    /**
     * The spreadSheet.
     */
    private final Element theSpreadSheet;

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
     * Constructor.
     * @param pBook the workBook.
     * @param pSpreadSheet the spreadSheet element
     */
    PrometheusOdfTableStore(final PrometheusOdfWorkBook pBook,
                            final Element pSpreadSheet) {
        /* Store parameters */
        theBook = pBook;
        theSpreadSheet = pSpreadSheet;
        theParser = theBook.getParser();

        /* Create the maps */
        theSheets = new HashMap<>();
        theRanges = new HashMap<>();
        theConstraints = new HashMap<>();
    }

    /**
     * Obtain the workBook.
     * @return the WorkBook
     */
    PrometheusOdfWorkBook getWorkBook() {
        return theBook;
    }

    /**
     * Build sheetXML.
     */
    void buildSheetXML() {
        /* Loop through the sheets */
        for (Object mySheet : theSheets.values()) {
            ((PrometheusOdfSheetCore) mySheet).populateSheet();
        }
    }

    /**
     * Build maps.
      */
    void loadMaps() {
        buildSheetMap();
        buildRangeMap();
    }

    /**
     * Build sheet map.
     */
    private void buildSheetMap() {
        /* Access the list of tables */
        final List<Element> mySheets = theParser.getAllNamedChildren(theSpreadSheet, PrometheusOdfTableItem.TABLE);

        /* Loop through the list */
        for (Element mySheet : mySheets) {
            /* Store the sheet in the map */
            theSheets.put(theParser.getAttribute(mySheet, PrometheusOdfTableItem.NAME), mySheet);
        }
    }

    /**
     * Obtain existing sheet.
     * @param pName the name of the sheet
     * @return the sheet
     * @throws OceanusException on error
     */
    PrometheusOdfSheet getSheet(final String pName) throws OceanusException {
        /* Obtain the existing sheet */
        Object myCore = theSheets.get(pName);
        if (myCore instanceof Element myElement) {
            myCore = new PrometheusOdfSheetCore(this, theNumTables++, myElement);
        }
        return myCore == null
               ? null
               : ((PrometheusOdfSheetCore) myCore).getReadOnlySheet();
    }

    /**
     * Create a new sheet.
     * @param pName the name of the sheet.
     * @param pNumRows the initial number of rows
     * @param pNumCols the initial number of columns
     * @return the new sheet
     * @throws OceanusException on error
     */
    PrometheusOdfSheet newSheet(final String pName,
                                final int pNumRows,
                                final int pNumCols) throws OceanusException {
        /* Check for existing sheet */
        if (theSheets.get(pName) != null) {
            throw new PrometheusSheetException("Sheet "
                    + pName
                    + "already exists in workbook");
        }

        /* Create the new Sheet */
        final Element myTable = theParser.newElement(PrometheusOdfTableItem.TABLE);
        theParser.setAttribute(myTable, PrometheusOdfTableItem.NAME, pName);

        /* Access the expressions */
        final Element myExpressions = theParser.getFirstNamedChild(theSpreadSheet, PrometheusOdfTableItem.EXPRESSIONS);
        PrometheusOdfParser.addAsPriorSibling(myTable, myExpressions);

        /* Create the Core sheet and add to the map */
        final PrometheusOdfSheetCore myCore = new PrometheusOdfSheetCore(this, theNumTables++, pNumRows, pNumCols, myTable);
        theSheets.put(pName, myCore);

        /* Return the sheet */
        return myCore.getMutableSheet();
    }

    /**
     * Build range map.
     */
    private void buildRangeMap() {
        /* Access the list of ranges */
        final Element myExpressions = theParser.getFirstNamedChild(theSpreadSheet, PrometheusOdfTableItem.EXPRESSIONS);
        final List<Element> myRanges = theParser.getAllNamedChildren(myExpressions, PrometheusOdfTableItem.RANGE);

        /* Loop through the named ranges */
        for (Element myRange : myRanges) {
            /* Add range to map */
            theRanges.put(theParser.getAttribute(myRange, PrometheusOdfTableItem.NAME), myRange);
        }
    }

    /**
     * Obtain the range view.
     * @param pName the name of the view
     * @return the range
     * @throws OceanusException on error
     */
    PrometheusSheetView getRangeView(final String pName) throws OceanusException {
        /* Locate the named range in the map */
        final Element myNamedRange = theRanges.get(pName);
        if (myNamedRange == null) {
            return null;
        }

        /* Obtain the address */
        final String myRange = theParser.getAttribute(myNamedRange, PrometheusOdfTableItem.CELLRANGEADDRESS);
        final PrometheusSheetCellRange myCellRange = new PrometheusSheetCellRange(myRange);
        final PrometheusSheetCellPosition myFirstCell = myCellRange.getFirstCell().getPosition();
        final PrometheusSheetCellPosition myLastCell = myCellRange.getLastCell().getPosition();

        /* Obtain the sheet and reject if missing */
        final PrometheusSheetSheet mySheet = getSheet(myCellRange.getFirstCell().getSheetName());
        if (mySheet == null) {
            throw new PrometheusSheetException("Sheet for "
                    + pName
                    + " not found in workbook");
        }

        /* Return the view */
        return new PrometheusSheetView(mySheet, myFirstCell, myLastCell);
    }

    /**
     * Declare the named range.
     * @param pName the name of the range
     * @param pRange the range to declare
     * @throws OceanusException on error
     */
    void declareRange(final String pName,
                      final PrometheusSheetCellRange pRange) throws OceanusException {
        /* Check for existing range */
        if (theRanges.get(pName) != null) {
            throw new PrometheusSheetException("Name "
                    + pName
                    + "already exists in workbook");
        }

        /* Access the expressions */
        final Element myExpressions = theParser.getFirstNamedChild(theSpreadSheet, PrometheusOdfTableItem.EXPRESSIONS);

        /* Protect against exceptions */
        try {
            /* Add the new range */
            final Element myRange = theParser.newElement(PrometheusOdfTableItem.RANGE);
            theParser.setAttribute(myRange, PrometheusOdfTableItem.NAME, pName);
            theParser.setAttribute(myRange, PrometheusOdfTableItem.CELLRANGEADDRESS, pRange.toString());
            theParser.setAttribute(myRange, PrometheusOdfTableItem.BASECELLADDRESS, pRange.getFirstCell().toString());
            myExpressions.appendChild(myRange);
            theRanges.put(pName, myRange);
        } catch (Exception e) {
            throw new PrometheusSheetException("Failed to declare range", e);
        }
    }

    /**
     * Apply Data Validation.
     * @param pSheet the workSheet containing the cells
     * @param pFirstCell the the first cell in the range
     * @param pLastCell the last cell in the range
     * @param pValidRange the name of the validation range
     */
    void applyDataValidation(final PrometheusOdfSheetCore pSheet,
                             final PrometheusSheetCellPosition pFirstCell,
                             final PrometheusSheetCellPosition pLastCell,
                             final String pValidRange) {
        /* Access constraint */
        Element myConstraint = theConstraints.get(pValidRange);
        if (myConstraint == null) {
            /* Create the constraint */
            myConstraint = createDataConstraint(pValidRange);

            /* Store the constraint */
            theConstraints.put(pValidRange, myConstraint);
        }

        /* Apply the constraint */
        pSheet.applyValidation(theParser.getAttribute(myConstraint, PrometheusOdfTableItem.NAME), pFirstCell, pLastCell);
    }

    /**
     * Apply Data Validation.
     * @param pSheet the workSheet containing the cells
     * @param pFirstCell the the first cell in the range
     * @param pLastCell the last cell in the range
     * @param pValueList the value list
     */
    void applyDataValidation(final PrometheusOdfSheetCore pSheet,
                             final PrometheusSheetCellPosition pFirstCell,
                             final PrometheusSheetCellPosition pLastCell,
                             final String[] pValueList) {
        /* Access constraint */
        Element myConstraint = theConstraints.get(pValueList);
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
                myBuilder.append(PrometheusSheetCellAddress.escapeApostrophes(myValue));
            }

            /* Create the constraint */
            myConstraint = createDataConstraint(myBuilder.toString());

            /* Store the constraint */
            theConstraints.put(pValueList, myConstraint);
        }

        /* Apply the constraint */
        pSheet.applyValidation(theParser.getAttribute(myConstraint, PrometheusOdfTableItem.NAME), pFirstCell, pLastCell);
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
        Element myConstraints = theParser.getFirstNamedChild(theSpreadSheet, PrometheusOdfTableItem.VALIDATIONS);
        if (myConstraints == null) {
            myConstraints = theParser.newElement(PrometheusOdfTableItem.VALIDATIONS);
            theSpreadSheet.insertBefore(myConstraints, theSpreadSheet.getFirstChild());
        }

        /* Create the new constraint */
        final Element myConstraint = theParser.newElement(PrometheusOdfTableItem.VALIDATION);
        myConstraints.appendChild(myConstraint);
        theParser.setAttribute(myConstraint, PrometheusOdfTableItem.NAME, myName);

        /* Create the rule */
        final String myRule = "of:cell-content-is-in-list("
                + pConstraint
                + ")";
        theParser.setAttribute(myConstraint, PrometheusOdfTableItem.CONDITION, myRule);
        theParser.setAttribute(myConstraint, PrometheusOdfTableItem.ALLOWEMPTYCELL, Boolean.TRUE);
        final Element myError = theParser.newElement(PrometheusOdfTableItem.ERRORMSG);
        myConstraint.appendChild(myError);
        theParser.setAttribute(myError, PrometheusOdfTableItem.DISPLAY, Boolean.TRUE);

        /* Store the constraint */
        return myConstraint;
    }

    /**
     * Apply Data Filter.
     * @param pRange the range
     */
    void applyDataFilter(final PrometheusSheetCellRange pRange) {
        /* Access the dbRanges */
        Element myDBRanges = theParser.getFirstNamedChild(theSpreadSheet, PrometheusOdfTableItem.DATARANGES);
        if (myDBRanges == null) {
            myDBRanges = theParser.newElement(PrometheusOdfTableItem.DATARANGES);
            theSpreadSheet.appendChild(myDBRanges);
        }

        /* Build the name */
        final String myName = "dbRange"
                + ++theNumDBRanges;

        /* Create the new filter */
        final Element myFilter = theParser.newElement(PrometheusOdfTableItem.DATARANGE);
        theParser.setAttribute(myFilter, PrometheusOdfTableItem.NAME, myName);
        theParser.setAttribute(myFilter, PrometheusOdfTableItem.TARGETRANGE, pRange.toString());
        theParser.setAttribute(myFilter, PrometheusOdfTableItem.DISPLAYFILTER, Boolean.TRUE);
        theParser.setAttribute(myFilter, PrometheusOdfTableItem.ORIENTATION, PrometheusOdfValue.COLUMN);
    }
}
