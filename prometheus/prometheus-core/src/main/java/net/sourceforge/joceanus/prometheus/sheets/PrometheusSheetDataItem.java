/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.prometheus.sheets;

import java.util.Iterator;

import net.sourceforge.joceanus.metis.data.MetisDataResource;
import net.sourceforge.joceanus.prometheus.exc.PrometheusIOException;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataItem;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataList;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataValues;
import net.sourceforge.joceanus.prometheus.service.sheet.PrometheusSheetCell;
import net.sourceforge.joceanus.prometheus.service.sheet.PrometheusSheetCellPosition;
import net.sourceforge.joceanus.prometheus.service.sheet.PrometheusSheetCellStyleType;
import net.sourceforge.joceanus.prometheus.service.sheet.PrometheusSheetRow;
import net.sourceforge.joceanus.prometheus.service.sheet.PrometheusSheetSheet;
import net.sourceforge.joceanus.prometheus.service.sheet.PrometheusSheetView;
import net.sourceforge.joceanus.prometheus.service.sheet.PrometheusSheetWorkBook;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.oceanus.date.OceanusDate;
import net.sourceforge.joceanus.oceanus.decimal.OceanusDecimal;
import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;
import net.sourceforge.joceanus.oceanus.decimal.OceanusPrice;
import net.sourceforge.joceanus.oceanus.decimal.OceanusRate;
import net.sourceforge.joceanus.oceanus.decimal.OceanusRatio;
import net.sourceforge.joceanus.oceanus.decimal.OceanusUnits;
import net.sourceforge.joceanus.tethys.api.thread.TethysUIThreadStatusReport;

/**
 * SheetDataItem class for accessing a sheet that is related to a data type.
 * @author Tony Washer
 * @param <T> the data type
 */
public abstract class PrometheusSheetDataItem<T extends PrometheusDataItem> {
    /**
     * ID column.
     */
    protected static final int COL_ID = 0;

    /**
     * The report.
     */
    private final TethysUIThreadStatusReport theReport;

    /**
     * The input sheet.
     */
    private PrometheusSheetReader theReader;

    /**
     * The workbook.
     */
    private PrometheusSheetWorkBook theWorkBook;

    /**
     * The DataList.
     */
    private PrometheusDataList<T> theList;

    /**
     * The name of the related range.
     */
    private final String theRangeName;

    /**
     * The WorkSheet of the range.
     */
    private PrometheusSheetSheet theWorkSheet;

    /**
     * The Active row.
     */
    private PrometheusSheetRow theActiveRow;

    /**
     * The Active view.
     */
    private PrometheusSheetView theActiveView;

    /**
     * The last loaded item.
     */
    private T theLastItem;

    /**
     * The Row number of the current row.
     */
    private int theCurrRow;

    /**
     * The Row number of the base row.
     */
    private int theBaseRow;

    /**
     * Constructor for a load operation.
     * @param pReader the spreadsheet reader
     * @param pRange the range to load
     */
    protected PrometheusSheetDataItem(final PrometheusSheetReader pReader,
                                      final String pRange) {
        /* Store parameters */
        theReport = pReader.getReport();
        theReader = pReader;
        theRangeName = pRange;
    }

    /**
     * Constructor for a write operation.
     * @param pWriter the spreadsheet writer
     * @param pRange the range to create
     */
    protected PrometheusSheetDataItem(final PrometheusSheetWriter pWriter,
                                      final String pRange) {
        /* Store parameters */
        theReport = pWriter.getReport();
        theWorkBook = pWriter.getWorkBook();
        theRangeName = pRange;
    }

    /**
     * Obtain the last loaded item.
     * @return the item
     */
    protected T getLastItem() {
        return theLastItem;
    }

    @Override
    public String toString() {
        return theRangeName;
    }

    /**
     * Set the DataList.
     * @param pList the Data list
     */
    protected void setDataList(final PrometheusDataList<T> pList) {
        /* Store parameters */
        theList = pList;
    }

    /**
     * Load the DataItems from a spreadsheet.
     * @throws OceanusException on error
     */
    public void loadSpreadSheet() throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Access the workbook */
            theWorkBook = theReader.getWorkBook();

            /* Access the view of the range */
            theActiveView = theWorkBook.getRangeView(theRangeName);
            if (theActiveView != null) {
                final Iterator<PrometheusSheetRow> myIterator = theActiveView.rowIterator();

                /* Declare the new stage */
                theReport.setNewStage(theRangeName);

                /* Determine count of rows */
                final int myTotal = theActiveView.getRowCount();

                /* Declare the number of steps */
                theReport.setNumSteps(myTotal);

                /* Loop through the rows of the range */
                theCurrRow = 0;
                while (myIterator.hasNext()) {
                    /* Access the row */
                    theActiveRow = myIterator.next();

                    /* load the item */
                    final PrometheusDataValues myValues = loadSecureValues();
                    theLastItem = theList.addValuesItem(myValues);

                    /* Report the progress */
                    theReport.setNextStep();
                    theCurrRow++;
                }
            }

            /* Post process the load */
            postProcessOnLoad();

            /* Handle exceptions */
        } catch (OceanusException e) {
            throw new PrometheusIOException("Failed to Load " + theRangeName, e);
        }
    }

    /**
     * Write the DataItems to a spreadsheet.
     * @throws OceanusException on error
     */
    protected void writeSpreadSheet() throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Declare the new stage */
            theReport.setNewStage(theRangeName);

            /* Count the number of items */
            final int myTotal = theList.size();

            /* Declare the number of steps */
            theReport.setNumSteps(myTotal);

            /* Determine size of sheet */
            final int myNumRows = myTotal;
            final int myNumCols = getLastColumn() + 1;

            /* Create the sheet */
            theWorkSheet = theWorkBook.newSheet(theRangeName, myNumRows, myNumCols);

            /* Initialise counts */
            theBaseRow = 0;
            theCurrRow = theBaseRow;

            /* Access the iterator */
            final Iterator<T> myItemIterator = theList.iterator();

            /* Loop through the data items */
            while (myItemIterator.hasNext()) {
                final T myCurr = myItemIterator.next();

                /* Create the new row */
                newRow();

                /* insert the item */
                insertSecureItem(myCurr);

                /* Report the progress */
                theCurrRow++;
                theReport.setNextStep();
            }

            /* If data was written then name the range */
            if (theCurrRow > theBaseRow) {
                nameRange();
            }
        } catch (OceanusException e) {
            throw new PrometheusIOException("Failed to create " + theRangeName, e);
        }
    }

    /**
     * Load secure item from spreadsheet.
     * @return the secure values
     * @throws OceanusException on error
     */
    protected abstract PrometheusDataValues loadSecureValues() throws OceanusException;

    /**
     * Insert secure item into spreadsheet.
     * @param pItem the item
     * @throws OceanusException on error
     */
    protected void insertSecureItem(final T pItem) throws OceanusException {
        /* Write the id */
        writeInteger(COL_ID, pItem.getIndexedId());
    }

    /**
     * PostProcess on load.
     * @throws OceanusException on error
     */
    protected void postProcessOnLoad() throws OceanusException {
        /* postProcess the list */
        theList.postProcessOnLoad();
    }

    /**
     * Determine last active column.
     * @return the last active column
     */
    protected abstract int getLastColumn();

    /**
     * Create a new row.
     */
    protected void newRow() {
        /* Create the new row */
        theActiveRow = theWorkSheet.getMutableRowByIndex(theCurrRow);
    }

    /**
     * Name the basic range.
     * @throws OceanusException on error
     */
    protected void nameRange() throws OceanusException {
        /* Adjust column if necessary */
        final int myCol = getLastColumn();

        /* Name the range */
        final PrometheusSheetCellPosition myFirst = new PrometheusSheetCellPosition(0, theBaseRow);
        final PrometheusSheetCellPosition myLast = new PrometheusSheetCellPosition(myCol, theCurrRow - 1);
        theWorkSheet.declareRange(theRangeName, myFirst, myLast);
    }

    /**
     * Name the column range.
     * @param pOffset offset of column
     * @param pName name of range
     * @throws OceanusException on error
     */
    protected void nameColumnRange(final int pOffset,
                                   final String pName) throws OceanusException {
        /* Name the range */
        final PrometheusSheetCellPosition myFirst = new PrometheusSheetCellPosition(pOffset, theBaseRow);
        final PrometheusSheetCellPosition myLast = new PrometheusSheetCellPosition(pOffset, theCurrRow - 1);
        theWorkSheet.declareRange(pName, myFirst, myLast);
    }

    /**
     * Apply Data Validation.
     * @param pOffset offset of column
     * @param pList name of validation range
     * @throws OceanusException on error
     */
    public void applyDataValidation(final int pOffset,
                                    final String pList) throws OceanusException {
        /* Name the range */
        final PrometheusSheetCellPosition myFirst = new PrometheusSheetCellPosition(pOffset, theBaseRow);
        final PrometheusSheetCellPosition myLast = new PrometheusSheetCellPosition(pOffset, theCurrRow - 1);
        theWorkSheet.applyDataValidation(myFirst, myLast, pList);
    }

    /**
     * Freeze titles.
     */
    protected void freezeTitles() {
        /* Freeze the top row */
        final PrometheusSheetCellPosition myPoint = new PrometheusSheetCellPosition(2, theBaseRow);
        theWorkSheet.createFreezePane(myPoint);
    }

    /**
     * Freeze titles.
     * @param pOffset column offset to freeze at
     * @throws OceanusException on error
     */
    protected void applyDataFilter(final int pOffset) throws OceanusException {
        /* Freeze the top row */
        final PrometheusSheetCellPosition myPoint = new PrometheusSheetCellPosition(pOffset, 0);
        theWorkSheet.applyDataFilter(myPoint, theCurrRow);
    }

    /**
     * Set Hidden column.
     * @param pOffset the offset of the column
     */
    protected void setHiddenColumn(final int pOffset) {
        /* Apply to the sheet */
        theWorkSheet.getMutableColumnByIndex(pOffset).setHidden(true);
    }

    /**
     * Set Date column.
     * @param pOffset the offset of the column
     */
    protected void setDateColumn(final int pOffset) {
        /* Apply the style to the sheet */
        theWorkSheet.getMutableColumnByIndex(pOffset).setDefaultCellStyle(PrometheusSheetCellStyleType.DATE);
    }

    /**
     * Set String column.
     * @param pOffset the offset of the column
     */
    protected void setStringColumn(final int pOffset) {
        /* Apply the style to the sheet */
        theWorkSheet.getMutableColumnByIndex(pOffset).setDefaultCellStyle(PrometheusSheetCellStyleType.STRING);
    }

    /**
     * Set Money column.
     * @param pOffset the offset of the column
     */
    protected void setMoneyColumn(final int pOffset) {
        /* Apply the style to the sheet */
        theWorkSheet.getMutableColumnByIndex(pOffset).setDefaultCellStyle(PrometheusSheetCellStyleType.MONEY);
    }

    /**
     * Set Price column.
     * @param pOffset the offset of the column
     */
    protected void setPriceColumn(final int pOffset) {
        /* Apply the style to the sheet */
        theWorkSheet.getMutableColumnByIndex(pOffset).setDefaultCellStyle(PrometheusSheetCellStyleType.PRICE);
    }

    /**
     * Set Units column.
     * @param pOffset the offset of the column
     */
    protected void setUnitsColumn(final int pOffset) {
        /* Apply the style to the sheet */
        theWorkSheet.getMutableColumnByIndex(pOffset).setDefaultCellStyle(PrometheusSheetCellStyleType.UNITS);
    }

    /**
     * Set Rate column.
     * @param pOffset the offset of the column
     */
    protected void setRateColumn(final int pOffset) {
        /* Apply the style to the sheet */
        theWorkSheet.getMutableColumnByIndex(pOffset).setDefaultCellStyle(PrometheusSheetCellStyleType.RATE);
    }

    /**
     * Set Ratio column.
     * @param pOffset the offset of the column
     */
    protected void setRatioColumn(final int pOffset) {
        /* Apply the style to the sheet */
        theWorkSheet.getMutableColumnByIndex(pOffset).setDefaultCellStyle(PrometheusSheetCellStyleType.RATIO);
    }

    /**
     * Set Boolean column.
     * @param pOffset the offset of the column
     */
    protected void setBooleanColumn(final int pOffset) {
        /* Apply the style to the sheet */
        theWorkSheet.getMutableColumnByIndex(pOffset).setDefaultCellStyle(PrometheusSheetCellStyleType.BOOLEAN);
    }

    /**
     * Set Integer column.
     * @param pOffset the offset of the column
     */
    protected void setIntegerColumn(final int pOffset) {
        /* Apply the style to the sheet */
        theWorkSheet.getMutableColumnByIndex(pOffset).setDefaultCellStyle(PrometheusSheetCellStyleType.INTEGER);
    }

    /**
     * Access an integer from the WorkSheet.
     * @param pOffset the column offset
     * @return the integer
     * @throws OceanusException on error
     */
    protected Integer loadInteger(final int pOffset) throws OceanusException {
        /* Access the cells by reference */
        final PrometheusSheetCell myCell = theActiveView.getRowCellByIndex(theActiveRow, pOffset);

        /* Return the value */
        return myCell != null
                ? myCell.getInteger()
                : null;
    }

    /**
     * Access a long from the WorkSheet.
     * @param pOffset the column offset
     * @return the long
     * @throws OceanusException on error
     */
    protected Long loadLong(final int pOffset) throws OceanusException {
        /* Access the cells by reference */
        final PrometheusSheetCell myCell = theActiveView.getRowCellByIndex(theActiveRow, pOffset);

        /* Return the value */
        return myCell != null
                ? myCell.getLongFromString()
                : null;
    }

    /**
     * Access a boolean from the WorkSheet.
     * @param pOffset the column offset
     * @return the date
     */
    protected Boolean loadBoolean(final int pOffset) {
        /* Access the cells by reference */
        final PrometheusSheetCell myCell = theActiveView.getRowCellByIndex(theActiveRow, pOffset);

        /* Return the value */
        return myCell != null
                ? myCell.getBoolean()
                : null;
    }

    /**
     * Access a date from the WorkSheet.
     * @param pOffset the column offset
     * @return the date
     * @throws OceanusException on error
     */
    protected OceanusDate loadDate(final int pOffset) throws OceanusException {
        /* Access the cells by reference */
        final PrometheusSheetCell myCell = theActiveView.getRowCellByIndex(theActiveRow, pOffset);

        /* Return the value */
        return myCell != null
                ? myCell.getDate()
                : null;
    }

    /**
     * Access a money value from the WorkSheet.
     * @param pOffset the column offset
     * @return the money
     * @throws OceanusException on error
     */
    protected OceanusMoney loadMoney(final int pOffset) throws OceanusException {
        /* Access the cells by reference */
        final PrometheusSheetCell myCell = theActiveView.getRowCellByIndex(theActiveRow, pOffset);

        /* Return the value */
        return myCell != null
                ? myCell.getMoney()
                : null;
    }

    /**
     * Access a price value from the WorkSheet.
     * @param pOffset the column offset
     * @return the price
     * @throws OceanusException on error
     */
    protected OceanusPrice loadPrice(final int pOffset) throws OceanusException {
        /* Access the cells by reference */
        final PrometheusSheetCell myCell = theActiveView.getRowCellByIndex(theActiveRow, pOffset);

        /* Return the value */
        return myCell != null
                ? myCell.getPrice()
                : null;
    }

    /**
     * Access a rate value from the WorkSheet.
     * @param pOffset the column offset
     * @return the rate
     * @throws OceanusException on error
     */
    protected OceanusRate loadRate(final int pOffset) throws OceanusException {
        /* Access the cells by reference */
        final PrometheusSheetCell myCell = theActiveView.getRowCellByIndex(theActiveRow, pOffset);

        /* Return the value */
        return myCell != null
                ? myCell.getRate()
                : null;
    }

    /**
     * Access a units value from the WorkSheet.
     * @param pOffset the column offset
     * @return the units
     * @throws OceanusException on error
     */
    protected OceanusUnits loadUnits(final int pOffset) throws OceanusException {
        /* Access the cells by reference */
        final PrometheusSheetCell myCell = theActiveView.getRowCellByIndex(theActiveRow, pOffset);

        /* Return the value */
        return myCell != null
                ? myCell.getUnits()
                : null;
    }

    /**
     * Access a ratio value from the WorkSheet.
     * @param pOffset the column offset
     * @return the ratio
     * @throws OceanusException on error
     */
    protected OceanusRatio loadRatio(final int pOffset) throws OceanusException {
        /* Access the cells by reference */
        final PrometheusSheetCell myCell = theActiveView.getRowCellByIndex(theActiveRow, pOffset);

        /* Return the value */
        return myCell != null
                ? myCell.getRatio()
                : null;
    }

    /**
     * Access a string from the WorkSheet.
     * @param pOffset the column offset
     * @return the string
     */
    protected String loadString(final int pOffset) {
        /* Access the cells by reference */
        final PrometheusSheetCell myCell = theActiveView.getRowCellByIndex(theActiveRow, pOffset);

        /* Return the value */
        return myCell != null
                ? myCell.getString()
                : null;
    }

    /**
     * Access a byte array from the WorkSheet.
     * @param pOffset the column offset
     * @return the byte array
     */
    protected byte[] loadBytes(final int pOffset) {
        /* Access the cells by reference */
        final PrometheusSheetCell myCell = theActiveView.getRowCellByIndex(theActiveRow, pOffset);

        /* Return the value */
        return myCell != null
                ? myCell.getBytes()
                : null;
    }

    /**
     * Access a char array from the WorkSheet.
     * @param pOffset the column offset
     * @return the char array
     * @throws OceanusException on error
     */
    protected char[] loadChars(final int pOffset) throws OceanusException {
        /* Access the cells by reference */
        final PrometheusSheetCell myCell = theActiveView.getRowCellByIndex(theActiveRow, pOffset);

        /* Return the value */
        return myCell != null
                ? myCell.getCharArray()
                : null;
    }

    /**
     * Write an integer to the WorkSheet.
     * @param pOffset the column offset
     * @param pValue the integer
     * @throws OceanusException on error
     */
    protected void writeInteger(final int pOffset,
                                final Integer pValue) throws OceanusException {
        /* If we have non-null value */
        if (pValue != null) {
            /* Create the cell and set its value */
            final PrometheusSheetCell myCell = theActiveRow.getMutableCellByIndex(pOffset);
            myCell.setInteger(pValue);
        }
    }

    /**
     * Write an integer to the WorkSheet.
     * @param pOffset the column offset
     * @param pValue the integer
     * @throws OceanusException on error
     */
    protected void writeLong(final int pOffset,
                             final Long pValue) throws OceanusException {
        /* If we have non-null value */
        if (pValue != null) {
            /* Create the cell and set its value */
            final PrometheusSheetCell myCell = theActiveRow.getMutableCellByIndex(pOffset);
            myCell.setLongAsString(pValue);
        }
    }

    /**
     * Write a boolean to the WorkSheet.
     * @param pOffset the column offset
     * @param pValue the boolean
     * @throws OceanusException on error
     */
    protected void writeBoolean(final int pOffset,
                                final Boolean pValue) throws OceanusException {
        /* If we have non-null value */
        if (pValue != null) {
            /* Create the cell and set its value */
            final PrometheusSheetCell myCell = theActiveRow.getMutableCellByIndex(pOffset);
            myCell.setBoolean(pValue);
        }
    }

    /**
     * Write a date to the WorkSheet.
     * @param pOffset the column offset
     * @param pValue the date
     * @throws OceanusException on error
     */
    protected void writeDate(final int pOffset,
                             final OceanusDate pValue) throws OceanusException {
        /* If we have non-null value */
        if (pValue != null) {
            /* Create the cell and set its value */
            final PrometheusSheetCell myCell = theActiveRow.getMutableCellByIndex(pOffset);
            myCell.setDate(pValue);
        }
    }

    /**
     * Write a decimal to the WorkSheet.
     * @param pOffset the column offset
     * @param pValue the number
     * @throws OceanusException on error
     */
    protected void writeDecimal(final int pOffset,
                                final OceanusDecimal pValue) throws OceanusException {
        /* If we have non-null value */
        if (pValue != null) {
            /* Create the cell and set its value */
            final PrometheusSheetCell myCell = theActiveRow.getMutableCellByIndex(pOffset);
            myCell.setDecimal(pValue);
        }
    }

    /**
     * Write a Header to the WorkSheet.
     * @param pOffset the column offset
     * @param pHeader the header text
     * @throws OceanusException on error
     */
    protected void writeHeader(final int pOffset,
                               final String pHeader) throws OceanusException {
        /* If we have non-null value */
        if (pHeader != null) {
            /* Create the cell and set its value */
            final PrometheusSheetCell myCell = theActiveRow.getMutableCellByIndex(pOffset);
            myCell.setHeader(pHeader);
        }
    }

    /**
     * Write a string to the WorkSheet.
     * @param pOffset the column offset
     * @param pValue the string
     * @throws OceanusException on error
     */
    protected void writeString(final int pOffset,
                               final String pValue) throws OceanusException {
        /* If we have non-null value */
        if (pValue != null) {
            /* Create the cell and set its value */
            final PrometheusSheetCell myCell = theActiveRow.getMutableCellByIndex(pOffset);
            myCell.setString(pValue);
        }
    }

    /**
     * Write a byte array to the WorkSheet.
     * @param pOffset the column offset
     * @param pBytes the byte array
     * @throws OceanusException on error
     */
    protected void writeBytes(final int pOffset,
                              final byte[] pBytes) throws OceanusException {
        /* If we have non-null bytes */
        if (pBytes != null) {
            /* Create the cell and set its value */
            final PrometheusSheetCell myCell = theActiveRow.getMutableCellByIndex(pOffset);
            myCell.setBytes(pBytes);
        }
    }

    /**
     * Write a char array to the WorkSheet.
     * @param pOffset the column offset
     * @param pChars the char array
     * @throws OceanusException on error
     */
    protected void writeChars(final int pOffset,
                              final char[] pChars) throws OceanusException {
        /* If we have non-null chars */
        if (pChars != null) {
            /* Create the cell and set its value */
            final PrometheusSheetCell myCell = theActiveRow.getMutableCellByIndex(pOffset);
            myCell.setCharArray(pChars);
        }
    }

    /**
     * Obtain row values.
     * @param pName the name of the item
     * @return the row values.
     * @throws OceanusException on error
     */
    protected PrometheusDataValues getRowValues(final String pName) throws OceanusException {
        /* Allocate the values */
        final PrometheusDataValues myValues = new PrometheusDataValues(pName);

        /* Add the id and return the new values */
        myValues.addValue(MetisDataResource.DATA_ID, loadInteger(COL_ID));
        return myValues;
    }
}
