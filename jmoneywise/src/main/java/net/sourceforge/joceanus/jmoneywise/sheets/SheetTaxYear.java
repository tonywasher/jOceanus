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

import net.sourceforge.joceanus.jmetis.sheet.DataCell;
import net.sourceforge.joceanus.jmetis.sheet.DataView;
import net.sourceforge.joceanus.jmetis.sheet.DataWorkBook;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseIOException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.TaxYear;
import net.sourceforge.joceanus.jmoneywise.data.TaxYear.TaxYearList;
import net.sourceforge.joceanus.jmoneywise.data.TaxYearInfo.TaxInfoList;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxYearInfoClass;
import net.sourceforge.joceanus.jmoneywise.sheets.ArchiveLoader.ArchiveYear;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.data.TaskControl;
import net.sourceforge.joceanus.jprometheus.sheets.SheetDataItem;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;

/**
 * SheetDataItem extension for TaxYear.
 * @author Tony Washer
 */
public class SheetTaxYear
        extends SheetDataItem<TaxYear, MoneyWiseDataType> {
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
     * Constructor for loading a spreadsheet.
     * @param pReader the spreadsheet reader
     */
    protected SheetTaxYear(final MoneyWiseReader pReader) {
        /* Call super constructor */
        super(pReader, AREA_TAXYEARS);

        /* Access the Lists */
        MoneyWiseData myData = pReader.getData();
        setDataList(myData.getTaxYears());
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
        setDataList(myData.getTaxYears());
    }

    @Override
    protected DataValues<MoneyWiseDataType> loadSecureValues() throws JOceanusException {
        /* Build data values */
        DataValues<MoneyWiseDataType> myValues = getRowValues(TaxYear.OBJECT_NAME);
        myValues.addValue(TaxYear.FIELD_TAXYEAR, loadDate(COL_TAXYEAR));
        myValues.addValue(TaxYear.FIELD_REGIME, loadInteger(COL_REGIME));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void insertSecureItem(final TaxYear pItem) throws JOceanusException {
        /* Set the fields */
        super.insertSecureItem(pItem);
        writeDate(COL_TAXYEAR, pItem.getTaxYear());
        writeInteger(COL_REGIME, pItem.getTaxRegimeId());
    }

    @Override
    protected int getLastColumn() {
        /* Return the last column */
        return COL_REGIME;
    }

    /**
     * Load the TaxYears from an archive.
     * @param pTask the task control
     * @param pWorkBook the workbook
     * @param pData the data set to load into
     * @param pLoader the archive loader
     * @return continue to load <code>true/false</code>
     * @throws JOceanusException on error
     */
    protected static boolean loadArchive(final TaskControl<MoneyWiseData> pTask,
                                         final DataWorkBook pWorkBook,
                                         final MoneyWiseData pData,
                                         final ArchiveLoader pLoader) throws JOceanusException {
        /* Access the lists */
        TaxYearList myList = pData.getTaxYears();
        TaxInfoList myInfoList = pData.getTaxInfo();

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

            /* Declare the number of steps */
            if (!pTask.setNumSteps(myTotal)) {
                return false;
            }

            /* Obtain the range iterator */
            Iterator<ArchiveYear> myIterator = pLoader.getIterator();
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

                /* Build data values */
                DataValues<MoneyWiseDataType> myValues = new DataValues<MoneyWiseDataType>(TaxYear.OBJECT_NAME);
                myValues.addValue(TaxYear.FIELD_TAXYEAR, myDate);
                myValues.addValue(TaxYear.FIELD_REGIME, myTaxRegime);

                /* Add the value into the list */
                TaxYear myTaxYear = myList.addValuesItem(myValues);

                /* Add information relating to the tax year */
                myInfoList.addInfoItem(null, myTaxYear, TaxYearInfoClass.ALLOWANCE, myAllowance);
                myInfoList.addInfoItem(null, myTaxYear, TaxYearInfoClass.RENTALALLOWANCE, myRentalAllow);
                myInfoList.addInfoItem(null, myTaxYear, TaxYearInfoClass.LOTAXBAND, myLoTaxBand);
                myInfoList.addInfoItem(null, myTaxYear, TaxYearInfoClass.BASICTAXBAND, myBasicTaxBand);
                myInfoList.addInfoItem(null, myTaxYear, TaxYearInfoClass.CAPITALALLOWANCE, myCapitalAllow);
                myInfoList.addInfoItem(null, myTaxYear, TaxYearInfoClass.LOAGEALLOWANCE, myLoAgeAllow);
                myInfoList.addInfoItem(null, myTaxYear, TaxYearInfoClass.HIAGEALLOWANCE, myHiAgeAllow);
                myInfoList.addInfoItem(null, myTaxYear, TaxYearInfoClass.AGEALLOWANCELIMIT, myAgeAllowLimit);
                myInfoList.addInfoItem(null, myTaxYear, TaxYearInfoClass.ADDITIONALALLOWANCELIMIT, myAddAllowLimit);
                myInfoList.addInfoItem(null, myTaxYear, TaxYearInfoClass.ADDITIONALINCOMETHRESHOLD, myAddIncBound);
                myInfoList.addInfoItem(null, myTaxYear, TaxYearInfoClass.LOTAXRATE, myLoTaxRate);
                myInfoList.addInfoItem(null, myTaxYear, TaxYearInfoClass.BASICTAXRATE, myBasicTaxRate);
                myInfoList.addInfoItem(null, myTaxYear, TaxYearInfoClass.HITAXRATE, myHiTaxRate);
                myInfoList.addInfoItem(null, myTaxYear, TaxYearInfoClass.ADDITIONALTAXRATE, myAddTaxRate);
                myInfoList.addInfoItem(null, myTaxYear, TaxYearInfoClass.INTERESTTAXRATE, myIntTaxRate);
                myInfoList.addInfoItem(null, myTaxYear, TaxYearInfoClass.DIVIDENDTAXRATE, myDivTaxRate);
                myInfoList.addInfoItem(null, myTaxYear, TaxYearInfoClass.HIDIVIDENDTAXRATE, myHiDivTaxRate);
                myInfoList.addInfoItem(null, myTaxYear, TaxYearInfoClass.ADDITIONALDIVIDENDTAXRATE, myAddDivTaxRate);
                myInfoList.addInfoItem(null, myTaxYear, TaxYearInfoClass.CAPITALTAXRATE, myCapTaxRate);
                myInfoList.addInfoItem(null, myTaxYear, TaxYearInfoClass.HICAPITALTAXRATE, myHiCapTaxRate);

                /* Report the progress */
                myCount++;
                iRow++;

                if (((myCount % mySteps) == 0) && (!pTask.setStepsDone(myCount))) {
                    return false;
                }
            }

            /* PostProcess the lists */
            myList.postProcessOnLoad();
            myInfoList.postProcessOnLoad();

            /* Handle exceptions */
        } catch (JOceanusException e) {
            throw new JMoneyWiseIOException("Failed to Load " + myList.getItemType().getListName(), e);
        }

        /* Return to caller */
        return true;
    }
}
