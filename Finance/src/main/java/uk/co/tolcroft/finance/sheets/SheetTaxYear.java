/*******************************************************************************
 * Copyright 2012 Tony Washer
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
package uk.co.tolcroft.finance.sheets;

import java.util.Calendar;
import java.util.Date;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataException.ExceptionClass;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;

import uk.co.tolcroft.finance.data.FinanceData;
import uk.co.tolcroft.finance.data.TaxYear;
import uk.co.tolcroft.finance.data.TaxYear.TaxYearList;
import uk.co.tolcroft.finance.sheets.FinanceSheet.YearRange;
import uk.co.tolcroft.models.data.DataItem;
import uk.co.tolcroft.models.data.StaticData;
import uk.co.tolcroft.models.sheets.SheetDataItem;
import uk.co.tolcroft.models.sheets.SheetReader.SheetHelper;
import uk.co.tolcroft.models.sheets.SpreadSheet.SheetType;
import uk.co.tolcroft.models.threads.ThreadStatus;

public class SheetTaxYear extends SheetDataItem<TaxYear> {
    /**
     * NamedArea for TaxYears
     */
    private static final String TaxYears = "TaxParameters";

    /**
     * Is the spreadsheet a backup spreadsheet or an edit-able one
     */
    private boolean isBackup = false;

    /**
     * TaxYear data list
     */
    private TaxYearList theList = null;

    /**
     * DataSet
     */
    private FinanceData theData = null;

    /**
     * Constructor for loading a spreadsheet
     * @param pReader the spreadsheet reader
     */
    protected SheetTaxYear(FinanceReader pReader) {
        /* Call super constructor */
        super(pReader, TaxYears);

        /* Note whether this is a backup */
        isBackup = (pReader.getType() == SheetType.BACKUP);

        /* Access the Lists */
        theData = pReader.getData();
        theList = theData.getTaxYears();
    }

    /**
     * Constructor for creating a spreadsheet
     * @param pWriter the spreadsheet writer
     */
    protected SheetTaxYear(FinanceWriter pWriter) {
        /* Call super constructor */
        super(pWriter, TaxYears);

        /* Note whether this is a backup */
        isBackup = (pWriter.getType() == SheetType.BACKUP);

        /* Access the TaxYears list */
        theList = pWriter.getData().getTaxYears();
        setDataList(theList);
    }

    /**
     * Load an item from the spreadsheet
     * @throws JDataException
     */
    @Override
    protected void loadItem() throws JDataException {

        /* If this is a backup load */
        if (isBackup) {
            /* Access the IDs */
            int myID = loadInteger(0);
            int myRegimeId = loadInteger(2);

            /* Access the dates */
            Date myYear = loadDate(1);

            /* Access the String values */
            String myAllowance = loadString(3);
            String myLoAgeAllw = loadString(4);
            String myHiAgeAllw = loadString(5);
            String myCapAllow = loadString(6);
            String myRental = loadString(7);
            String myAgeLimit = loadString(8);
            String myLoBand = loadString(9);
            String myBasicBand = loadString(10);
            String myLoTax = loadString(11);
            String myBasicTax = loadString(12);
            String myHiTax = loadString(13);
            String myAddTax = loadString(14);
            String myIntTax = loadString(15);
            String myDivTax = loadString(16);
            String myHiDivTax = loadString(17);
            String myAddDivTax = loadString(18);
            String myCapTax = loadString(19);
            String myHiCapTax = loadString(20);
            String myAddLimit = loadString(21);
            String myAddBound = loadString(22);

            /* Add the Tax Year */
            theList.addItem(myID, myRegimeId, myYear, myAllowance, myRental, myLoAgeAllw, myHiAgeAllw,
                            myCapAllow, myAgeLimit, myAddLimit, myLoBand, myBasicBand, myAddBound, myLoTax,
                            myBasicTax, myHiTax, myIntTax, myDivTax, myHiDivTax, myAddTax, myAddDivTax,
                            myCapTax, myHiCapTax);
        }

        /* else this is a load from an edit-able spreadsheet */
        else {
            /* Access the ID */
            int myID = loadInteger(0);

            /* Access the Strings */
            String myTaxRegime = loadString(2);

            /* Access the year */
            Date myYear = loadDate(1);

            /* Access the binary values */
            String myAllowance = loadString(3);
            String myLoAgeAllw = loadString(4);
            String myHiAgeAllw = loadString(5);
            String myCapAllow = loadString(6);
            String myRental = loadString(7);
            String myAgeLimit = loadString(8);
            String myLoBand = loadString(9);
            String myBasicBand = loadString(10);
            String myLoTax = loadString(11);
            String myBasicTax = loadString(12);
            String myHiTax = loadString(13);
            String myAddTax = loadString(14);
            String myIntTax = loadString(15);
            String myDivTax = loadString(16);
            String myHiDivTax = loadString(17);
            String myAddDivTax = loadString(18);
            String myCapTax = loadString(19);
            String myHiCapTax = loadString(20);
            String myAddLimit = loadString(21);
            String myAddBound = loadString(22);

            /* Add the Tax Year */
            theList.addItem(myID, myTaxRegime, myYear, myAllowance, myRental, myLoAgeAllw, myHiAgeAllw,
                            myCapAllow, myAgeLimit, myAddLimit, myLoBand, myBasicBand, myAddBound, myLoTax,
                            myBasicTax, myHiTax, myIntTax, myDivTax, myHiDivTax, myAddTax, myAddDivTax,
                            myCapTax, myHiCapTax);

        }
    }

    /**
     * Insert a item into the spreadsheet
     * @param pItem the Item to insert
     * @throws JDataException
     */
    @Override
    protected void insertItem(TaxYear pItem) throws JDataException {
        /* If we are creating a backup */
        if (isBackup) {
            /* Set the fields */
            writeInteger(0, pItem.getId());
            writeDate(1, pItem.getTaxYear());
            writeInteger(2, pItem.getTaxRegime().getId());
            writeNumber(3, pItem.getAllowance());
            writeNumber(4, pItem.getLoAgeAllow());
            writeNumber(5, pItem.getHiAgeAllow());
            writeNumber(6, pItem.getCapitalAllow());
            writeNumber(7, pItem.getRentalAllowance());
            writeNumber(8, pItem.getAgeAllowLimit());
            writeNumber(9, pItem.getLoBand());
            writeNumber(10, pItem.getBasicBand());
            writeNumber(11, pItem.getLoTaxRate());
            writeNumber(12, pItem.getBasicTaxRate());
            writeNumber(13, pItem.getHiTaxRate());
            writeNumber(14, pItem.getAddTaxRate());
            writeNumber(15, pItem.getIntTaxRate());
            writeNumber(16, pItem.getDivTaxRate());
            writeNumber(17, pItem.getHiDivTaxRate());
            writeNumber(18, pItem.getAddDivTaxRate());
            writeNumber(19, pItem.getCapTaxRate());
            writeNumber(20, pItem.getHiCapTaxRate());
            writeNumber(21, pItem.getAddAllowLimit());
            writeNumber(22, pItem.getAddIncBound());
        }

        /* else we are creating an edit-able spreadsheet */
        else {
            /* Set the fields */
            writeInteger(0, pItem.getId());
            writeDate(1, pItem.getTaxYear());
            writeString(2, pItem.getTaxRegime().getName());
            writeNumber(3, pItem.getAllowance());
            writeNumber(4, pItem.getLoAgeAllow());
            writeNumber(5, pItem.getHiAgeAllow());
            writeNumber(6, pItem.getCapitalAllow());
            writeNumber(7, pItem.getRentalAllowance());
            writeNumber(8, pItem.getAgeAllowLimit());
            writeNumber(9, pItem.getLoBand());
            writeNumber(10, pItem.getBasicBand());
            writeNumber(11, pItem.getLoTaxRate());
            writeNumber(12, pItem.getBasicTaxRate());
            writeNumber(13, pItem.getHiTaxRate());
            writeNumber(14, pItem.getAddTaxRate());
            writeNumber(15, pItem.getIntTaxRate());
            writeNumber(16, pItem.getDivTaxRate());
            writeNumber(17, pItem.getHiDivTaxRate());
            writeNumber(18, pItem.getAddDivTaxRate());
            writeNumber(19, pItem.getCapTaxRate());
            writeNumber(20, pItem.getHiCapTaxRate());
            writeNumber(21, pItem.getAddAllowLimit());
            writeNumber(22, pItem.getAddIncBound());
        }
    }

    @Override
    protected void preProcessOnWrite() throws JDataException {
        /* Ignore if this is a backup */
        if (isBackup)
            return;

        /* Create a new row */
        newRow();

        /* Write titles */
        writeHeader(0, DataItem.FIELD_ID.getName());
        writeHeader(1, TaxYear.FIELD_TAXYEAR.getName());
        writeHeader(2, TaxYear.FIELD_REGIME.getName());
        writeHeader(3, TaxYear.FIELD_ALLOW.getName());
        writeHeader(4, TaxYear.FIELD_LOAGAL.getName());
        writeHeader(5, TaxYear.FIELD_HIAGAL.getName());
        writeHeader(6, TaxYear.FIELD_CAPALW.getName());
        writeHeader(7, TaxYear.FIELD_RENTAL.getName());
        writeHeader(8, TaxYear.FIELD_AGELMT.getName());
        writeHeader(9, TaxYear.FIELD_LOBAND.getName());
        writeHeader(10, TaxYear.FIELD_BSBAND.getName());
        writeHeader(11, TaxYear.FIELD_LOTAX.getName());
        writeHeader(12, TaxYear.FIELD_BASTAX.getName());
        writeHeader(13, TaxYear.FIELD_HITAX.getName());
        writeHeader(14, TaxYear.FIELD_ADDTAX.getName());
        writeHeader(15, TaxYear.FIELD_INTTAX.getName());
        writeHeader(16, TaxYear.FIELD_DIVTAX.getName());
        writeHeader(17, TaxYear.FIELD_HDVTAX.getName());
        writeHeader(18, TaxYear.FIELD_ADVTAX.getName());
        writeHeader(19, TaxYear.FIELD_CAPTAX.getName());
        writeHeader(20, TaxYear.FIELD_HCPTAX.getName());
        writeHeader(21, TaxYear.FIELD_ADDLMT.getName());
        writeHeader(22, TaxYear.FIELD_ADDBDY.getName());

        /* Adjust for Header */
        adjustForHeader();
    }

    @Override
    protected void postProcessOnWrite() throws JDataException {
        /* If we are creating a backup */
        if (isBackup) {
            /* Set the twenty-three columns as the range */
            nameRange(23);
        }

        /* else this is an edit-able spreadsheet */
        else {
            /* Set the twenty-three columns as the range */
            nameRange(23);

            /* Set the Id column as hidden */
            setHiddenColumn(0);
            setIntegerColumn(0);

            /* Set the String column width */
            setColumnWidth(2, StaticData.NAMELEN);
            applyDataValidation(2, SheetTaxRegime.TaxRegNames);

            /* Set Number columns */
            setDateColumn(1);
            setMoneyColumn(3);
            setMoneyColumn(4);
            setMoneyColumn(5);
            setMoneyColumn(6);
            setMoneyColumn(7);
            setMoneyColumn(8);
            setMoneyColumn(9);
            setMoneyColumn(10);
            setMoneyColumn(21);
            setMoneyColumn(22);
            setRateColumn(11);
            setRateColumn(12);
            setRateColumn(13);
            setRateColumn(14);
            setRateColumn(15);
            setRateColumn(16);
            setRateColumn(17);
            setRateColumn(18);
            setRateColumn(19);
            setRateColumn(20);
        }
    }

    @Override
    protected void postProcessOnLoad() throws JDataException {
        theData.calculateDateRange();
    }

    /**
     * Load the TaxYears from an archive
     * @param pThread the thread status control
     * @param pHelper the sheet helper
     * @param pData the data set to load into
     * @param pRange the range of tax years
     * @return continue to load <code>true/false</code>
     * @throws JDataException
     */
    protected static boolean loadArchive(ThreadStatus<FinanceData> pThread,
                                         SheetHelper pHelper,
                                         FinanceData pData,
                                         YearRange pRange) throws JDataException {
        /* Local variables */
        TaxYearList myList;
        AreaReference myRange;
        Sheet mySheet;
        CellReference myTop;
        CellReference myBottom;
        String myAllowance;
        String myRentalAllow;
        String myCapitalAllow;
        String myLoTaxBand;
        String myBasicTaxBand;
        String myLoAgeAllow;
        String myHiAgeAllow;
        String myAgeAllowLimit;
        String myAddAllowLimit;
        String myAddIncBound;
        String myLoTaxRate;
        String myBasicTaxRate;
        String myHiTaxRate;
        String myIntTaxRate;
        String myDivTaxRate;
        String myHiDivTaxRate;
        String myAddTaxRate;
        String myAddDivTaxRate;
        String myCapTaxRate;
        String myHiCapTaxRate;
        String myTaxRegime;
        Calendar myYear;
        Cell myCell;
        int myAllRow;
        int myTotal;
        int mySteps;
        int myCount = 0;

        /* Protect against exceptions */
        try {
            /* Find the range of cells */
            myRange = pHelper.resolveAreaReference(TaxYears);

            /* Access the number of reporting steps */
            mySteps = pThread.getReportingSteps();

            /* Declare the new stage */
            if (!pThread.setNewStage(TaxYears))
                return false;

            /* If we found the range OK */
            if (myRange != null) {
                /* Access the relevant sheet and Cell references */
                myTop = myRange.getFirstCell();
                myBottom = myRange.getLastCell();
                mySheet = pHelper.getSheetByName(myTop.getSheetName());
                myAllRow = myTop.getRow();

                /* Count the number of TaxYears */
                myTotal = myBottom.getRow() - myTop.getRow() + 1;

                /* Access the list of tax years */
                myList = pData.getTaxYears();

                /* Declare the number of steps */
                if (!pThread.setNumSteps(myTotal))
                    return false;

                /* Create the calendar instance */
                myYear = Calendar.getInstance();
                myYear.set(pRange.getMaxYear(), Calendar.APRIL, 5, 0, 0, 0);

                /* Loop through the columns of the table */
                for (int i = myTop.getCol(); i <= myBottom.getCol(); i++, myYear.add(Calendar.YEAR, -1)) {

                    /* Access the values */
                    myAllowance = pHelper.formatNumericCell(mySheet.getRow(myAllRow).getCell(i));
                    myLoTaxBand = pHelper.formatNumericCell(mySheet.getRow(myAllRow + 1).getCell(i));
                    myBasicTaxBand = pHelper.formatNumericCell(mySheet.getRow(myAllRow + 2).getCell(i));
                    myRentalAllow = pHelper.formatNumericCell(mySheet.getRow(myAllRow + 3).getCell(i));
                    myLoTaxRate = pHelper.formatRateCell(mySheet.getRow(myAllRow + 4).getCell(i));
                    myBasicTaxRate = pHelper.formatRateCell(mySheet.getRow(myAllRow + 5).getCell(i));
                    myIntTaxRate = pHelper.formatRateCell(mySheet.getRow(myAllRow + 6).getCell(i));
                    myDivTaxRate = pHelper.formatRateCell(mySheet.getRow(myAllRow + 7).getCell(i));
                    myHiTaxRate = pHelper.formatRateCell(mySheet.getRow(myAllRow + 8).getCell(i));
                    myHiDivTaxRate = pHelper.formatRateCell(mySheet.getRow(myAllRow + 9).getCell(i));
                    myTaxRegime = mySheet.getRow(myAllRow + 10).getCell(i).getStringCellValue();
                    myLoAgeAllow = pHelper.formatNumericCell(mySheet.getRow(myAllRow + 13).getCell(i));
                    myHiAgeAllow = pHelper.formatNumericCell(mySheet.getRow(myAllRow + 14).getCell(i));
                    myAgeAllowLimit = pHelper.formatNumericCell(mySheet.getRow(myAllRow + 15).getCell(i));
                    myCapitalAllow = pHelper.formatNumericCell(mySheet.getRow(myAllRow + 18).getCell(i));

                    /* Handle AddTaxRate which may be missing */
                    myCell = mySheet.getRow(myAllRow + 11).getCell(i);
                    myAddTaxRate = null;
                    if (myCell != null) {
                        myAddTaxRate = pHelper.formatRateCell(myCell);
                    }

                    /* Handle AddDivTaxRate which may be missing */
                    myCell = mySheet.getRow(myAllRow + 12).getCell(i);
                    myAddDivTaxRate = null;
                    if (myCell != null) {
                        myAddDivTaxRate = pHelper.formatRateCell(myCell);
                    }

                    /* Handle AddAllowLimit which may be missing */
                    myCell = mySheet.getRow(myAllRow + 16).getCell(i);
                    myAddAllowLimit = null;
                    if (myCell != null) {
                        myAddAllowLimit = pHelper.formatNumericCell(myCell);
                    }

                    /* Handle AddIncomeBoundary which may be missing */
                    myCell = mySheet.getRow(myAllRow + 17).getCell(i);
                    myAddIncBound = null;
                    if (myCell != null) {
                        myAddIncBound = pHelper.formatNumericCell(myCell);
                    }

                    /* Handle CapTaxRate which may be missing */
                    myCell = mySheet.getRow(myAllRow + 19).getCell(i);
                    myCapTaxRate = null;
                    if (myCell != null) {
                        myCapTaxRate = pHelper.formatRateCell(myCell);
                    }

                    /* Handle HiCapTaxRate which may be missing */
                    myCell = mySheet.getRow(myAllRow + 20).getCell(i);
                    myHiCapTaxRate = null;
                    if (myCell != null) {
                        myHiCapTaxRate = pHelper.formatRateCell(myCell);
                    }

                    /* Add the Tax Year */
                    myList.addItem(0, myTaxRegime, myYear.getTime(), myAllowance, myRentalAllow,
                                   myLoAgeAllow, myHiAgeAllow, myCapitalAllow, myAgeAllowLimit,
                                   myAddAllowLimit, myLoTaxBand, myBasicTaxBand, myAddIncBound, myLoTaxRate,
                                   myBasicTaxRate, myHiTaxRate, myIntTaxRate, myDivTaxRate, myHiDivTaxRate,
                                   myAddTaxRate, myAddDivTaxRate, myCapTaxRate, myHiCapTaxRate);

                    /* Report the progress */
                    myCount++;
                    if ((myCount % mySteps) == 0)
                        if (!pThread.setStepsDone(myCount))
                            return false;
                }
            }

            /* Handle exceptions */
        } catch (Exception e) {
            throw new JDataException(ExceptionClass.EXCEL, "Failed to Load TaxYears", e);
        }

        /* Return to caller */
        return true;
    }
}
