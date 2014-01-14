/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2014 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.sheets;

import java.util.Iterator;

import net.sourceforge.joceanus.jdatamanager.JDataException;
import net.sourceforge.joceanus.jdatamanager.JDataException.ExceptionClass;
import net.sourceforge.joceanus.jdatamodels.data.TaskControl;
import net.sourceforge.joceanus.jdatamodels.sheets.SheetDataInfoSet;
import net.sourceforge.joceanus.jdatamodels.sheets.SheetDataItem;
import net.sourceforge.joceanus.jdateday.JDateDay;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.TaxYear;
import net.sourceforge.joceanus.jmoneywise.data.TaxYear.TaxYearList;
import net.sourceforge.joceanus.jmoneywise.data.TaxYearBase;
import net.sourceforge.joceanus.jmoneywise.data.TaxYearInfo;
import net.sourceforge.joceanus.jmoneywise.data.TaxYearInfo.TaxInfoList;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxYearInfoClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxYearInfoType;
import net.sourceforge.joceanus.jmoneywise.sheets.MoneyWiseSheet.ArchiveYear;
import net.sourceforge.joceanus.jmoneywise.sheets.MoneyWiseSheet.YearRange;
import net.sourceforge.joceanus.jspreadsheetmanager.DataCell;
import net.sourceforge.joceanus.jspreadsheetmanager.DataView;
import net.sourceforge.joceanus.jspreadsheetmanager.DataWorkBook;

/**
 * SheetDataItem extension for TaxYear.
 * @author Tony Washer
 */
public class SheetTaxYear
        extends SheetDataItem<TaxYear> {
    /**
     * NamedArea for TaxYears.
     */
    private static final String AREA_TAXYEARS = "TaxParams";

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
    private MoneyWiseData theData = null;

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
    protected SheetTaxYear(final MoneyWiseReader pReader) {
        /* Call super constructor */
        super(pReader, AREA_TAXYEARS);

        /* Access the Lists */
        theData = pReader.getData();
        theList = theData.getTaxYears();
        theInfoList = theData.getTaxInfo();
        setDataList(theList);

        /* Set up info Sheet */
        theInfoSheet = isBackup()
                ? null
                : new SheetTaxInfoSet(TaxYearInfoClass.class, this, COL_REGIME);
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected SheetTaxYear(final MoneyWiseWriter pWriter) {
        /* Call super constructor */
        super(pWriter, AREA_TAXYEARS);

        /* Access the TaxYears list */
        MoneyWiseData myData = pWriter.getData();
        theList = myData.getTaxYears();
        theInfoList = myData.getTaxInfo();
        setDataList(theList);

        /* Set up info Sheet */
        theInfoSheet = isBackup()
                ? null
                : new SheetTaxInfoSet(TaxYearInfoClass.class, this, COL_REGIME);
    }

    @Override
    protected void loadSecureItem(final Integer pId) throws JDataException {
        /* Access the IDs */
        Integer myRegimeId = loadInteger(COL_REGIME);

        /* Access the dates */
        JDateDay myYear = loadDate(COL_TAXYEAR);

        /* Add the Tax Year */
        theList.addSecureItem(pId, myRegimeId, myYear);
    }

    @Override
    protected void loadOpenItem(final Integer pId) throws JDataException {
        /* Access the Strings */
        String myTaxRegime = loadString(COL_REGIME);

        /* Access the year */
        JDateDay myYear = loadDate(COL_TAXYEAR);

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
        /* Set the column types */
        setStringColumn(COL_REGIME);
        setDateColumn(COL_TAXYEAR);

        /* Apply validation */
        applyDataValidation(COL_REGIME, SheetTaxRegime.AREA_TAXREGIMENAMES);

        /* Format the info sheet */
        theInfoSheet.formatSheet();
    }

    @Override
    protected void postProcessOnLoad() throws JDataException {
        /* Resolve links and reSort */
        theList.resolveDataSetLinks();
        theList.reSort();

        /* Touch underlying items */
        theList.touchUnderlyingItems();

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
    protected static boolean loadArchive(final TaskControl<MoneyWiseData> pTask,
                                         final DataWorkBook pWorkBook,
                                         final MoneyWiseData pData,
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

            /* Obtain the range iterator */
            Iterator<ArchiveYear> myIterator = pRange.getIterator();
            int iRow = 0;

            /* Loop through the required years */
            while (myIterator.hasNext()) {
                /* Row Adjust value */
                int iAdjust = 1;

                /* Access Year */
                ArchiveYear myYear = myIterator.next();
                JDateDay myDate = myYear.getDate();

                /* Access the values */
                String myTaxRegime = myView.getCellByPosition(iAdjust++, iRow).getStringValue();
                String myAllowance = myView.getCellByPosition(iAdjust++, iRow).getStringValue();
                String myLoTaxBand = myView.getCellByPosition(iAdjust++, iRow).getStringValue();
                String myBasicTaxBand = myView.getCellByPosition(iAdjust++, iRow).getStringValue();
                String myRentalAllow = myView.getCellByPosition(iAdjust++, iRow).getStringValue();
                String myLoTaxRate = myView.getCellByPosition(iAdjust++, iRow).getStringValue();
                String myBasicTaxRate = myView.getCellByPosition(iAdjust++, iRow).getStringValue();
                String myIntTaxRate = myView.getCellByPosition(iAdjust++, iRow).getStringValue();
                String myDivTaxRate = myView.getCellByPosition(iAdjust++, iRow).getStringValue();
                String myHiTaxRate = myView.getCellByPosition(iAdjust++, iRow).getStringValue();
                String myHiDivTaxRate = myView.getCellByPosition(iAdjust++, iRow).getStringValue();

                /* Handle AddTaxRate which may be missing */
                DataCell myCell = myView.getCellByPosition(iAdjust++, iRow);
                String myAddTaxRate = null;
                if (myCell != null) {
                    myAddTaxRate = myCell.getStringValue();
                }

                /* Handle AddDivTaxRate which may be missing */
                myCell = myView.getCellByPosition(iAdjust++, iRow);
                String myAddDivTaxRate = null;
                if (myCell != null) {
                    myAddDivTaxRate = myCell.getStringValue();
                }

                /* Access the values */
                String myLoAgeAllow = myView.getCellByPosition(iAdjust++, iRow).getStringValue();
                String myHiAgeAllow = myView.getCellByPosition(iAdjust++, iRow).getStringValue();
                String myAgeAllowLimit = myView.getCellByPosition(iAdjust++, iRow).getStringValue();

                /* Handle AddAllowLimit which may be missing */
                myCell = myView.getCellByPosition(iAdjust++, iRow);
                String myAddAllowLimit = null;
                if (myCell != null) {
                    myAddAllowLimit = myCell.getStringValue();
                }

                /* Handle AddIncomeBoundary which may be missing */
                myCell = myView.getCellByPosition(iAdjust++, iRow);
                String myAddIncBound = null;
                if (myCell != null) {
                    myAddIncBound = myCell.getStringValue();
                }

                /* Access the values */
                String myCapitalAllow = myView.getCellByPosition(iAdjust++, iRow).getStringValue();

                /* Handle CapTaxRate which may be missing */
                myCell = myView.getCellByPosition(iAdjust++, iRow);
                String myCapTaxRate = null;
                if (myCell != null) {
                    myCapTaxRate = myCell.getStringValue();
                }

                /* Handle HiCapTaxRate which may be missing */
                myCell = myView.getCellByPosition(iAdjust++, iRow);
                String myHiCapTaxRate = null;
                if (myCell != null) {
                    myHiCapTaxRate = myCell.getStringValue();
                }

                /* Add the Tax Year */
                TaxYear myTaxYear = myList.addOpenItem(0, myTaxRegime, myDate);

                /* Add information relating to the tax year */
                myInfoList.addOpenItem(0, myTaxYear, TaxYearInfoClass.ALLOWANCE, myAllowance);
                myInfoList.addOpenItem(0, myTaxYear, TaxYearInfoClass.RENTALALLOWANCE, myRentalAllow);
                myInfoList.addOpenItem(0, myTaxYear, TaxYearInfoClass.LOTAXBAND, myLoTaxBand);
                myInfoList.addOpenItem(0, myTaxYear, TaxYearInfoClass.BASICTAXBAND, myBasicTaxBand);
                myInfoList.addOpenItem(0, myTaxYear, TaxYearInfoClass.CAPITALALLOWANCE, myCapitalAllow);
                myInfoList.addOpenItem(0, myTaxYear, TaxYearInfoClass.LOAGEALLOWANCE, myLoAgeAllow);
                myInfoList.addOpenItem(0, myTaxYear, TaxYearInfoClass.HIAGEALLOWANCE, myHiAgeAllow);
                myInfoList.addOpenItem(0, myTaxYear, TaxYearInfoClass.AGEALLOWANCELIMIT, myAgeAllowLimit);
                myInfoList.addOpenItem(0, myTaxYear, TaxYearInfoClass.ADDITIONALALLOWANCELIMIT, myAddAllowLimit);
                myInfoList.addOpenItem(0, myTaxYear, TaxYearInfoClass.ADDITIONALINCOMETHRESHOLD, myAddIncBound);
                myInfoList.addOpenItem(0, myTaxYear, TaxYearInfoClass.LOTAXRATE, myLoTaxRate);
                myInfoList.addOpenItem(0, myTaxYear, TaxYearInfoClass.BASICTAXRATE, myBasicTaxRate);
                myInfoList.addOpenItem(0, myTaxYear, TaxYearInfoClass.HITAXRATE, myHiTaxRate);
                myInfoList.addOpenItem(0, myTaxYear, TaxYearInfoClass.ADDITIONALTAXRATE, myAddTaxRate);
                myInfoList.addOpenItem(0, myTaxYear, TaxYearInfoClass.INTERESTTAXRATE, myIntTaxRate);
                myInfoList.addOpenItem(0, myTaxYear, TaxYearInfoClass.DIVIDENDTAXRATE, myDivTaxRate);
                myInfoList.addOpenItem(0, myTaxYear, TaxYearInfoClass.HIDIVIDENDTAXRATE, myHiDivTaxRate);
                myInfoList.addOpenItem(0, myTaxYear, TaxYearInfoClass.ADDITIONALDIVIDENDTAXRATE, myAddDivTaxRate);
                myInfoList.addOpenItem(0, myTaxYear, TaxYearInfoClass.CAPITALTAXRATE, myCapTaxRate);
                myInfoList.addOpenItem(0, myTaxYear, TaxYearInfoClass.HICAPITALTAXRATE, myHiCapTaxRate);

                /* Report the progress */
                myCount++;
                iRow++;

                if (((myCount % mySteps) == 0)
                    && (!pTask.setStepsDone(myCount))) {
                    return false;
                }
            }

            /* Sort the list */
            myList.resolveDataSetLinks();
            myList.reSort();

            /* Touch underlying items */
            myList.touchUnderlyingItems();

            /* Validate the tax years */
            myList.validateOnLoad();

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
