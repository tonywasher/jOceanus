/*******************************************************************************
 * jMoneyWise: Finance Application
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
package net.sourceforge.jOceanus.jMoneyWise.sheets;

import java.util.Calendar;
import java.util.Date;

import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataManager.JDataException.ExceptionClass;
import net.sourceforge.jOceanus.jDataModels.data.StaticData;
import net.sourceforge.jOceanus.jDataModels.data.TaskControl;
import net.sourceforge.jOceanus.jDataModels.sheets.SheetDataItem;
import net.sourceforge.jOceanus.jDataModels.sheets.SheetReader.SheetHelper;
import net.sourceforge.jOceanus.jMoneyWise.data.FinanceData;
import net.sourceforge.jOceanus.jMoneyWise.data.TaxYear;
import net.sourceforge.jOceanus.jMoneyWise.data.TaxYear.TaxYearList;
import net.sourceforge.jOceanus.jMoneyWise.data.TaxYearInfo.TaxInfoList;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.TaxYearInfoClass;
import net.sourceforge.jOceanus.jMoneyWise.sheets.FinanceSheet.YearRange;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;

/**
 * SheetDataItem extension for TaxYear.
 * @author Tony Washer
 */
public class SheetTaxYear
        extends SheetDataItem<TaxYear> {
    /**
     * NamedArea for TaxYears.
     */
    private static final String AREA_TAXYEARS = "TaxParameters";

    /**
     * TaxYear column.
     */
    private static final int COL_TAXYEAR = COL_ID + 1;

    /**
     * Regime column.
     */
    private static final int COL_REGIME = COL_TAXYEAR + 1;

    /**
     * Allowance column.
     */
    private static final int COL_ALLOW = 3;

    /**
     * LoAgeAllow column.
     */
    private static final int COL_LOAGEALLOW = 4;

    /**
     * HiAgeAllow column.
     */
    private static final int COL_HIAGEALLOW = 5;

    /**
     * CapitalAllow column.
     */
    private static final int COL_CAPALLOW = 6;

    /**
     * RentalAllow column.
     */
    private static final int COL_RENTALLOW = 7;

    /**
     * AgeAllowLimit column.
     */
    private static final int COL_AGEALLOWLMT = 8;

    /**
     * LoTaxBand column.
     */
    private static final int COL_LOBAND = 9;

    /**
     * BasicTaxBand column.
     */
    private static final int COL_BASICBAND = 10;

    /**
     * LoTaxRate column.
     */
    private static final int COL_LOTAX = 11;

    /**
     * BasicTaxRate column.
     */
    private static final int COL_BASICTAX = 12;

    /**
     * HiTaxRate column.
     */
    private static final int COL_HITAX = 13;

    /**
     * AddTaxRate column.
     */
    private static final int COL_ADDTAX = 14;

    /**
     * IntTaxRate column.
     */
    private static final int COL_INTTAX = 15;

    /**
     * DivTaxRate column.
     */
    private static final int COL_DIVTAX = 16;

    /**
     * HiDivTaxRate column.
     */
    private static final int COL_HIDIVTAX = 17;

    /**
     * AddDivTaxRate column.
     */
    private static final int COL_ADDDIVTAX = 18;

    /**
     * CapTaxRate column.
     */
    private static final int COL_CAPTAX = 19;

    /**
     * HiCapTaxRate column.
     */
    private static final int COL_HICAPTAX = 20;

    /**
     * AddIncomeLimit column.
     */
    private static final int COL_ADDINCLMT = 21;

    /**
     * AddIncomeBoundary column.
     */
    private static final int COL_ADDINCBND = 22;

    /**
     * TaxYear data list.
     */
    private final TaxYearList theList;

    /**
     * DataSet.
     */
    private final FinanceData theData;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the spreadsheet reader
     */
    protected SheetTaxYear(final FinanceReader pReader) {
        /* Call super constructor */
        super(pReader, AREA_TAXYEARS);

        /* Access the Lists */
        theData = pReader.getData();
        theList = theData.getTaxYears();
        setDataList(theList);
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected SheetTaxYear(final FinanceWriter pWriter) {
        /* Call super constructor */
        super(pWriter, AREA_TAXYEARS);

        /* Access the TaxYears list */
        theData = pWriter.getData();
        theList = theData.getTaxYears();
        setDataList(theList);
    }

    @Override
    protected void loadSecureItem() throws JDataException {
        /* Access the IDs */
        Integer myID = loadInteger(COL_ID);
        Integer myRegimeId = loadInteger(COL_REGIME);

        /* Access the dates */
        Date myYear = loadDate(COL_TAXYEAR);

        /* Add the Tax Year */
        theList.addSecureItem(myID, myRegimeId, myYear);
    }

    @Override
    protected void loadOpenItem() throws JDataException {
        /* Access the ID */
        Integer myID = loadInteger(COL_ID);

        /* Access the Strings */
        String myTaxRegime = loadString(COL_REGIME);

        /* Access the year */
        Date myYear = loadDate(COL_TAXYEAR);

        /* Access the binary values */
        // String myAllowance = loadString(COL_ALLOW);
        // String myLoAgeAllw = loadString(COL_LOAGEALLOW);
        // String myHiAgeAllw = loadString(COL_HIAGEALLOW);
        // String myCapAllow = loadString(COL_CAPALLOW);
        // String myRental = loadString(COL_RENTALLOW);
        // String myAgeLimit = loadString(COL_AGEALLOWLMT);
        // String myLoBand = loadString(COL_LOBAND);
        // String myBasicBand = loadString(COL_BASICBAND);
        // String myLoTax = loadString(COL_LOTAX);
        // String myBasicTax = loadString(COL_BASICTAX);
        // String myHiTax = loadString(COL_HITAX);
        // String myAddTax = loadString(COL_ADDTAX);
        // String myIntTax = loadString(COL_INTTAX);
        // String myDivTax = loadString(COL_DIVTAX);
        // String myHiDivTax = loadString(COL_HIDIVTAX);
        // String myAddDivTax = loadString(COL_ADDDIVTAX);
        // String myCapTax = loadString(COL_CAPTAX);
        // String myHiCapTax = loadString(COL_HICAPTAX);
        // String myAddLimit = loadString(COL_ADDINCLMT);
        // String myAddBound = loadString(COL_ADDINCBND);

        /* Add the Tax Year */
        theList.addOpenItem(myID, myTaxRegime, myYear);
    }

    @Override
    protected void insertSecureItem(final TaxYear pItem) throws JDataException {
        /* Set the fields */
        writeInteger(COL_ID, pItem.getId());
        writeDate(COL_TAXYEAR, pItem.getTaxYear());
        writeInteger(COL_REGIME, pItem.getTaxRegime().getId());
    }

    @Override
    protected void insertOpenItem(final TaxYear pItem) throws JDataException {
        /* Set the fields */
        writeInteger(COL_ID, pItem.getId());
        writeDate(COL_TAXYEAR, pItem.getTaxYear());
        writeString(COL_REGIME, pItem.getTaxRegime().getName());
        writeNumber(COL_ALLOW, pItem.getAllowance());
        writeNumber(COL_LOAGEALLOW, pItem.getLoAgeAllow());
        writeNumber(COL_HIAGEALLOW, pItem.getHiAgeAllow());
        writeNumber(COL_CAPALLOW, pItem.getCapitalAllow());
        writeNumber(COL_RENTALLOW, pItem.getRentalAllowance());
        writeNumber(COL_AGEALLOWLMT, pItem.getAgeAllowLimit());
        writeNumber(COL_LOBAND, pItem.getLoBand());
        writeNumber(COL_BASICBAND, pItem.getBasicBand());
        writeNumber(COL_LOTAX, pItem.getLoTaxRate());
        writeNumber(COL_BASICTAX, pItem.getBasicTaxRate());
        writeNumber(COL_HITAX, pItem.getHiTaxRate());
        writeNumber(COL_ADDTAX, pItem.getAddTaxRate());
        writeNumber(COL_INTTAX, pItem.getIntTaxRate());
        writeNumber(COL_DIVTAX, pItem.getDivTaxRate());
        writeNumber(COL_HIDIVTAX, pItem.getHiDivTaxRate());
        writeNumber(COL_ADDDIVTAX, pItem.getAddDivTaxRate());
        writeNumber(COL_CAPTAX, pItem.getCapTaxRate());
        writeNumber(COL_HICAPTAX, pItem.getHiCapTaxRate());
        writeNumber(COL_ADDINCLMT, pItem.getAddAllowLimit());
        writeNumber(COL_ADDINCBND, pItem.getAddIncBound());
    }

    @Override
    protected void formatSheetHeader() throws JDataException {
        /* Write titles */
        writeHeader(COL_TAXYEAR, TaxYear.FIELD_TAXYEAR.getName());
        writeHeader(COL_REGIME, TaxYear.FIELD_REGIME.getName());
        writeHeader(COL_ALLOW, TaxYear.FIELD_ALLOW.getName());
        writeHeader(COL_LOAGEALLOW, TaxYear.FIELD_LOAGAL.getName());
        writeHeader(COL_HIAGEALLOW, TaxYear.FIELD_HIAGAL.getName());
        writeHeader(COL_CAPALLOW, TaxYear.FIELD_CAPALW.getName());
        writeHeader(COL_RENTALLOW, TaxYear.FIELD_RENTAL.getName());
        writeHeader(COL_AGEALLOWLMT, TaxYear.FIELD_AGELMT.getName());
        writeHeader(COL_LOBAND, TaxYear.FIELD_LOBAND.getName());
        writeHeader(COL_BASICBAND, TaxYear.FIELD_BSBAND.getName());
        writeHeader(COL_LOTAX, TaxYear.FIELD_LOTAX.getName());
        writeHeader(COL_BASICTAX, TaxYear.FIELD_BASTAX.getName());
        writeHeader(COL_HITAX, TaxYear.FIELD_HITAX.getName());
        writeHeader(COL_ADDTAX, TaxYear.FIELD_ADDTAX.getName());
        writeHeader(COL_INTTAX, TaxYear.FIELD_INTTAX.getName());
        writeHeader(COL_DIVTAX, TaxYear.FIELD_DIVTAX.getName());
        writeHeader(COL_HIDIVTAX, TaxYear.FIELD_HDVTAX.getName());
        writeHeader(COL_ADDDIVTAX, TaxYear.FIELD_ADVTAX.getName());
        writeHeader(COL_CAPTAX, TaxYear.FIELD_CAPTAX.getName());
        writeHeader(COL_HICAPTAX, TaxYear.FIELD_HCPTAX.getName());
        writeHeader(COL_ADDINCLMT, TaxYear.FIELD_ADDLMT.getName());
        writeHeader(COL_ADDINCBND, TaxYear.FIELD_ADDBDY.getName());

        /* Set the String column width */
        setColumnWidth(COL_REGIME, StaticData.NAMELEN);

        /* Set Number columns */
        setDateColumn(COL_TAXYEAR);
        setMoneyColumn(COL_ALLOW);
        setMoneyColumn(COL_LOAGEALLOW);
        setMoneyColumn(COL_HIAGEALLOW);
        setMoneyColumn(COL_CAPALLOW);
        setMoneyColumn(COL_RENTALLOW);
        setMoneyColumn(COL_AGEALLOWLMT);
        setMoneyColumn(COL_LOBAND);
        setMoneyColumn(COL_BASICBAND);
        setMoneyColumn(COL_ADDINCLMT);
        setMoneyColumn(COL_ADDINCBND);
        setRateColumn(COL_LOTAX);
        setRateColumn(COL_BASICTAX);
        setRateColumn(COL_HITAX);
        setRateColumn(COL_ADDTAX);
        setRateColumn(COL_INTTAX);
        setRateColumn(COL_DIVTAX);
        setRateColumn(COL_HIDIVTAX);
        setRateColumn(COL_ADDDIVTAX);
        setRateColumn(COL_CAPTAX);
        setRateColumn(COL_HICAPTAX);
    }

    @Override
    protected void postProcessOnWrite() throws JDataException {
        /* Set the range */
        nameRange(COL_ADDINCBND);

        /* If we are creating a backup */
        if (!isBackup()) {
            /* Apply validation */
            applyDataValidation(COL_REGIME, SheetTaxRegime.AREA_TAXREGIMENAMES);
        }
    }

    /**
     * Load the TaxYears from an archive.
     * @param pTask the task control
     * @param pHelper the sheet helper
     * @param pData the data set to load into
     * @param pRange the range of tax years
     * @return continue to load <code>true/false</code>
     * @throws JDataException on error
     */
    protected static boolean loadArchive(final TaskControl<FinanceData> pTask,
                                         final SheetHelper pHelper,
                                         final FinanceData pData,
                                         final YearRange pRange) throws JDataException {
        /* Protect against exceptions */
        try {
            /* Find the range of cells */
            AreaReference myRange = pHelper.resolveAreaReference(AREA_TAXYEARS);

            /* Access the number of reporting steps */
            int mySteps = pTask.getReportingSteps();
            int myCount = 0;

            /* Declare the new stage */
            if (!pTask.setNewStage(AREA_TAXYEARS)) {
                return false;
            }

            /* If we found the range OK */
            if (myRange != null) {
                /* Access the relevant sheet and Cell references */
                CellReference myTop = myRange.getFirstCell();
                CellReference myBottom = myRange.getLastCell();
                Sheet mySheet = pHelper.getSheetByName(myTop.getSheetName());
                int myAllRow = myTop.getRow();

                /* Count the number of TaxYears */
                int myTotal = myBottom.getRow()
                              - myTop.getRow()
                              + 1;

                /* Access the lists */
                TaxYearList myList = pData.getTaxYears();
                TaxInfoList myInfoList = pData.getTaxInfo();

                /* Declare the number of steps */
                if (!pTask.setNumSteps(myTotal)) {
                    return false;
                }

                /* Create the calendar instance */
                Calendar myYear = Calendar.getInstance();
                myYear.set(pRange.getMaxYear(), Calendar.APRIL, TaxYear.END_OF_MONTH_DAY, 0, 0, 0);

                /* Loop through the columns of the table */
                for (int i = myTop.getCol(); i <= myBottom.getCol(); i++, myYear.add(Calendar.YEAR, -1)) {
                    int iAdjust = 0;

                    /* Access the values */
                    String myAllowance = pHelper.formatNumericCell(mySheet.getRow(myAllRow
                                                                                  + iAdjust++).getCell(i));
                    String myLoTaxBand = pHelper.formatNumericCell(mySheet.getRow(myAllRow
                                                                                  + iAdjust++).getCell(i));
                    String myBasicTaxBand = pHelper.formatNumericCell(mySheet.getRow(myAllRow
                                                                                     + iAdjust++).getCell(i));
                    String myRentalAllow = pHelper.formatNumericCell(mySheet.getRow(myAllRow
                                                                                    + iAdjust++).getCell(i));
                    String myLoTaxRate = pHelper.formatNumericCell(mySheet.getRow(myAllRow
                                                                                  + iAdjust++).getCell(i));
                    String myBasicTaxRate = pHelper.formatNumericCell(mySheet.getRow(myAllRow
                                                                                     + iAdjust++).getCell(i));
                    String myIntTaxRate = pHelper.formatNumericCell(mySheet.getRow(myAllRow
                                                                                   + iAdjust++).getCell(i));
                    String myDivTaxRate = pHelper.formatNumericCell(mySheet.getRow(myAllRow
                                                                                   + iAdjust++).getCell(i));
                    String myHiTaxRate = pHelper.formatNumericCell(mySheet.getRow(myAllRow
                                                                                  + iAdjust++).getCell(i));
                    String myHiDivTaxRate = pHelper.formatNumericCell(mySheet.getRow(myAllRow
                                                                                     + iAdjust++).getCell(i));
                    String myTaxRegime = mySheet.getRow(myAllRow
                                                        + iAdjust++).getCell(i).getStringCellValue();

                    /* Handle AddTaxRate which may be missing */
                    Cell myCell = mySheet.getRow(myAllRow
                                                 + iAdjust++).getCell(i);
                    String myAddTaxRate = null;
                    if (myCell != null) {
                        myAddTaxRate = pHelper.formatNumericCell(myCell);
                    }

                    /* Handle AddDivTaxRate which may be missing */
                    myCell = mySheet.getRow(myAllRow
                                            + iAdjust++).getCell(i);
                    String myAddDivTaxRate = null;
                    if (myCell != null) {
                        myAddDivTaxRate = pHelper.formatNumericCell(myCell);
                    }

                    /* Access the values */
                    String myLoAgeAllow = pHelper.formatNumericCell(mySheet.getRow(myAllRow
                                                                                   + iAdjust++).getCell(i));
                    String myHiAgeAllow = pHelper.formatNumericCell(mySheet.getRow(myAllRow
                                                                                   + iAdjust++).getCell(i));
                    String myAgeAllowLimit = pHelper.formatNumericCell(mySheet.getRow(myAllRow
                                                                                      + iAdjust++).getCell(i));

                    /* Handle AddAllowLimit which may be missing */
                    myCell = mySheet.getRow(myAllRow
                                            + iAdjust++).getCell(i);
                    String myAddAllowLimit = null;
                    if (myCell != null) {
                        myAddAllowLimit = pHelper.formatNumericCell(myCell);
                    }

                    /* Handle AddIncomeBoundary which may be missing */
                    myCell = mySheet.getRow(myAllRow
                                            + iAdjust++).getCell(i);
                    String myAddIncBound = null;
                    if (myCell != null) {
                        myAddIncBound = pHelper.formatNumericCell(myCell);
                    }

                    /* Access the values */
                    String myCapitalAllow = pHelper.formatNumericCell(mySheet.getRow(myAllRow
                                                                                     + iAdjust++).getCell(i));

                    /* Handle CapTaxRate which may be missing */
                    myCell = mySheet.getRow(myAllRow
                                            + iAdjust++).getCell(i);
                    String myCapTaxRate = null;
                    if (myCell != null) {
                        myCapTaxRate = pHelper.formatNumericCell(myCell);
                    }

                    /* Handle HiCapTaxRate which may be missing */
                    myCell = mySheet.getRow(myAllRow
                                            + iAdjust++).getCell(i);
                    String myHiCapTaxRate = null;
                    if (myCell != null) {
                        myHiCapTaxRate = pHelper.formatNumericCell(myCell);
                    }

                    /* Add the Tax Year */
                    TaxYear myTaxYear = myList.addOpenItem(0, myTaxRegime, myYear.getTime());

                    /* Add information relating to the tax year */
                    myInfoList.addOpenItem(0, myTaxYear, TaxYearInfoClass.Allowance, myAllowance);
                    myInfoList.addOpenItem(0, myTaxYear, TaxYearInfoClass.RentalAllow, myRentalAllow);
                    myInfoList.addOpenItem(0, myTaxYear, TaxYearInfoClass.LoTaxBand, myLoTaxBand);
                    myInfoList.addOpenItem(0, myTaxYear, TaxYearInfoClass.BasicTaxBand, myBasicTaxBand);
                    myInfoList.addOpenItem(0, myTaxYear, TaxYearInfoClass.CapitalAllow, myCapitalAllow);
                    myInfoList.addOpenItem(0, myTaxYear, TaxYearInfoClass.LoAgeAllow, myLoAgeAllow);
                    myInfoList.addOpenItem(0, myTaxYear, TaxYearInfoClass.HiAgeAllow, myHiAgeAllow);
                    myInfoList.addOpenItem(0, myTaxYear, TaxYearInfoClass.AgeAllowLimit, myAgeAllowLimit);
                    myInfoList.addOpenItem(0, myTaxYear, TaxYearInfoClass.AddAllowLimit, myAddAllowLimit);
                    myInfoList.addOpenItem(0, myTaxYear, TaxYearInfoClass.AddIncomeThold, myAddIncBound);
                    myInfoList.addOpenItem(0, myTaxYear, TaxYearInfoClass.LoTaxRate, myLoTaxRate);
                    myInfoList.addOpenItem(0, myTaxYear, TaxYearInfoClass.BasicTaxRate, myBasicTaxRate);
                    myInfoList.addOpenItem(0, myTaxYear, TaxYearInfoClass.HiTaxRate, myHiTaxRate);
                    myInfoList.addOpenItem(0, myTaxYear, TaxYearInfoClass.AddTaxRate, myAddTaxRate);
                    myInfoList.addOpenItem(0, myTaxYear, TaxYearInfoClass.IntTaxRate, myIntTaxRate);
                    myInfoList.addOpenItem(0, myTaxYear, TaxYearInfoClass.DivTaxRate, myDivTaxRate);
                    myInfoList.addOpenItem(0, myTaxYear, TaxYearInfoClass.HiDivTaxRate, myHiDivTaxRate);
                    myInfoList.addOpenItem(0, myTaxYear, TaxYearInfoClass.AddDivTaxRate, myAddDivTaxRate);
                    myInfoList.addOpenItem(0, myTaxYear, TaxYearInfoClass.CapTaxRate, myCapTaxRate);
                    myInfoList.addOpenItem(0, myTaxYear, TaxYearInfoClass.HiCapTaxRate, myHiCapTaxRate);

                    /* Report the progress */
                    myCount++;

                    if (((myCount % mySteps) == 0)
                        && (!pTask.setStepsDone(myCount))) {
                        return false;
                    }
                }

                /* Sort the list */
                myList.reSort();
                myInfoList.reSort();

                /* Mark active items */
                myList.markActiveItems();

                /* Validate the tax years */
                myList.validate();
                if (myList.hasErrors()) {
                    throw new JDataException(ExceptionClass.VALIDATE, myList, "Validation error");
                }
            }

            /* Handle exceptions */
        } catch (JDataException e) {
            throw new JDataException(ExceptionClass.EXCEL, "Failed to Load TaxYears", e);
        }

        /* Return to caller */
        return true;
    }
}
