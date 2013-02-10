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
import net.sourceforge.jOceanus.jDataModels.sheets.SheetDataInfoSet;
import net.sourceforge.jOceanus.jDataModels.sheets.SheetDataItem;
import net.sourceforge.jOceanus.jMoneyWise.data.FinanceData;
import net.sourceforge.jOceanus.jMoneyWise.data.TaxYear;
import net.sourceforge.jOceanus.jMoneyWise.data.TaxYear.TaxYearList;
import net.sourceforge.jOceanus.jMoneyWise.data.TaxYearBase;
import net.sourceforge.jOceanus.jMoneyWise.data.TaxYearInfo;
import net.sourceforge.jOceanus.jMoneyWise.data.TaxYearInfo.TaxInfoList;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.TaxYearInfoClass;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.TaxYearInfoType;
import net.sourceforge.jOceanus.jMoneyWise.sheets.FinanceSheet.YearRange;
import net.sourceforge.jOceanus.jSpreadSheetManager.DataCell;
import net.sourceforge.jOceanus.jSpreadSheetManager.DataView;
import net.sourceforge.jOceanus.jSpreadSheetManager.DataWorkBook;

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
     * The DataSet.
     */
    private FinanceData theData = null;

    /**
     * TaxYear data list.
     */
    private final TaxYearList theList;

    /**
     * TaxYear info list.
     */
    private final TaxInfoList theInfoList;

    /**
     * DataInfoSet Helper.
     */
    private final SheetTaxInfoSet theInfoSheet;

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
        theInfoList = theData.getTaxInfo();
        setDataList(theList);

        /* Set up info Sheet */
        theInfoSheet = isBackup() ? null : new SheetTaxInfoSet(TaxYearInfoClass.class, this, COL_REGIME);
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected SheetTaxYear(final FinanceWriter pWriter) {
        /* Call super constructor */
        super(pWriter, AREA_TAXYEARS);

        /* Access the TaxYears list */
        FinanceData myData = pWriter.getData();
        theList = myData.getTaxYears();
        theInfoList = myData.getTaxInfo();
        setDataList(theList);

        /* Set up info Sheet */
        theInfoSheet = isBackup() ? null : new SheetTaxInfoSet(TaxYearInfoClass.class, this, COL_REGIME);
    }

    @Override
    protected void loadSecureItem(final Integer pId) throws JDataException {
        /* Access the IDs */
        Integer myRegimeId = loadInteger(COL_REGIME);

        /* Access the dates */
        Date myYear = loadDate(COL_TAXYEAR);

        /* Add the Tax Year */
        theList.addSecureItem(pId, myRegimeId, myYear);
    }

    @Override
    protected void loadOpenItem(final Integer pId) throws JDataException {
        /* Access the Strings */
        String myTaxRegime = loadString(COL_REGIME);

        /* Access the year */
        Date myYear = loadDate(COL_TAXYEAR);

        /* Add the Tax Year */
        TaxYear myTaxYear = theList.addOpenItem(pId, myTaxRegime, myYear);

        /* Load infoSet items */
        theInfoSheet.loadDataInfoSet(theInfoList, myTaxYear);
    }

    @Override
    protected void insertSecureItem(final TaxYear pItem) throws JDataException {
        /* Set the fields */
        writeDate(COL_TAXYEAR, pItem.getTaxYear());
        writeInteger(COL_REGIME, pItem.getTaxRegimeId());
    }

    @Override
    protected void insertOpenItem(final TaxYear pItem) throws JDataException {
        /* Set the fields */
        writeDate(COL_TAXYEAR, pItem.getTaxYear());
        writeString(COL_REGIME, pItem.getTaxRegimeName());

        /* Write infoSet fields */
        theInfoSheet.writeDataInfoSet(pItem.getInfoSet());
    }

    @Override
    protected void prepareSheet() throws JDataException {
        /* Write titles */
        writeHeader(COL_TAXYEAR, TaxYearBase.FIELD_TAXYEAR.getName());
        writeHeader(COL_REGIME, TaxYearBase.FIELD_REGIME.getName());

        /* prepare infoSet sheet */
        theInfoSheet.prepareSheet();
    }

    @Override
    protected void formatSheet() throws JDataException {
        /* Set the String column width */
        setColumnWidth(COL_REGIME, StaticData.NAMELEN);

        /* Set Date columns */
        setDateColumn(COL_TAXYEAR);

        /* Apply validation */
        applyDataValidation(COL_REGIME, SheetTaxRegime.AREA_TAXREGIMENAMES);

        /* Format the info sheet */
        theInfoSheet.formatSheet();
    }

    @Override
    protected void postProcessOnLoad() throws JDataException {
        /* Calculate the date range */
        theData.calculateDateRange();
    }

    @Override
    protected int getLastColumn() {
        /* Set default */
        int myLastCol = COL_REGIME;

        /* If we are not creating a backup */
        if (!isBackup()) {
            /* Name range plus infoSet */
            myLastCol += theInfoSheet.getXtraColumnCount();
        }

        /* Return the last column */
        return myLastCol;
    }

    /**
     * Load the TaxYears from an archive.
     * @param pTask the task control
     * @param pWorkBook the workbook
     * @param pData the data set to load into
     * @param pRange the range of tax years
     * @return continue to load <code>true/false</code>
     * @throws JDataException on error
     */
    protected static boolean loadArchive(final TaskControl<FinanceData> pTask,
                                         final DataWorkBook pWorkBook,
                                         final FinanceData pData,
                                         final YearRange pRange) throws JDataException {
        /* Protect against exceptions */
        try {
            /* Find the range of cells */
            DataView myView = pWorkBook.getRangeView(AREA_TAXYEARS);

            /* Access the number of reporting steps */
            int mySteps = pTask.getReportingSteps();
            int myCount = 0;

            /* Declare the new stage */
            if (!pTask.setNewStage(AREA_TAXYEARS)) {
                return false;
            }

            /* Count the number of TaxYears */
            int myTotal = myView.getColumnCount();

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
            for (int i = 0; i < myTotal; i++, myYear.add(Calendar.YEAR, -1)) {
                /* Row Adjust value */
                int iAdjust = 0;

                /* Access the values */
                String myAllowance = myView.getCellByPosition(i, iAdjust++).getStringValue();
                String myLoTaxBand = myView.getCellByPosition(i, iAdjust++).getStringValue();
                String myBasicTaxBand = myView.getCellByPosition(i, iAdjust++).getStringValue();
                String myRentalAllow = myView.getCellByPosition(i, iAdjust++).getStringValue();
                String myLoTaxRate = myView.getCellByPosition(i, iAdjust++).getStringValue();
                String myBasicTaxRate = myView.getCellByPosition(i, iAdjust++).getStringValue();
                String myIntTaxRate = myView.getCellByPosition(i, iAdjust++).getStringValue();
                String myDivTaxRate = myView.getCellByPosition(i, iAdjust++).getStringValue();
                String myHiTaxRate = myView.getCellByPosition(i, iAdjust++).getStringValue();
                String myHiDivTaxRate = myView.getCellByPosition(i, iAdjust++).getStringValue();
                String myTaxRegime = myView.getCellByPosition(i, iAdjust++).getStringValue();

                /* Handle AddTaxRate which may be missing */
                DataCell myCell = myView.getCellByPosition(i, iAdjust++);
                String myAddTaxRate = null;
                if (myCell != null) {
                    myAddTaxRate = myCell.getStringValue();
                }

                /* Handle AddDivTaxRate which may be missing */
                myCell = myView.getCellByPosition(i, iAdjust++);
                String myAddDivTaxRate = null;
                if (myCell != null) {
                    myAddDivTaxRate = myCell.getStringValue();
                }

                /* Access the values */
                String myLoAgeAllow = myView.getCellByPosition(i, iAdjust++).getStringValue();
                String myHiAgeAllow = myView.getCellByPosition(i, iAdjust++).getStringValue();
                String myAgeAllowLimit = myView.getCellByPosition(i, iAdjust++).getStringValue();

                /* Handle AddAllowLimit which may be missing */
                myCell = myView.getCellByPosition(i, iAdjust++);
                String myAddAllowLimit = null;
                if (myCell != null) {
                    myAddAllowLimit = myCell.getStringValue();
                }

                /* Handle AddIncomeBoundary which may be missing */
                myCell = myView.getCellByPosition(i, iAdjust++);
                String myAddIncBound = null;
                if (myCell != null) {
                    myAddIncBound = myCell.getStringValue();
                }

                /* Access the values */
                String myCapitalAllow = myView.getCellByPosition(i, iAdjust++).getStringValue();

                /* Handle CapTaxRate which may be missing */
                myCell = myView.getCellByPosition(i, iAdjust++);
                String myCapTaxRate = null;
                if (myCell != null) {
                    myCapTaxRate = myCell.getStringValue();
                }

                /* Handle HiCapTaxRate which may be missing */
                myCell = myView.getCellByPosition(i, iAdjust++);
                String myHiCapTaxRate = null;
                if (myCell != null) {
                    myHiCapTaxRate = myCell.getStringValue();
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

            /* Handle exceptions */
        } catch (JDataException e) {
            throw new JDataException(ExceptionClass.EXCEL, "Failed to Load TaxYears", e);
        }

        /* Return to caller */
        return true;
    }

    /**
     * TaxYearInfoSet sheet.
     */
    private static class SheetTaxInfoSet
            extends SheetDataInfoSet<TaxYearInfo, TaxYear, TaxYearInfoType, TaxYearInfoClass> {

        /**
         * Constructor.
         * @param pClass the info type class
         * @param pOwner the Owner
         * @param pBaseCol the base column
         */
        public SheetTaxInfoSet(final Class<TaxYearInfoClass> pClass,
                               final SheetDataItem<TaxYear> pOwner,
                               final int pBaseCol) {
            super(pClass, pOwner, pBaseCol);
        }
    }
}
