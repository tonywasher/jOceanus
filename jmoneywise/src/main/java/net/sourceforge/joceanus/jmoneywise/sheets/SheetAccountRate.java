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

import net.sourceforge.joceanus.jmetis.sheet.DataCell;
import net.sourceforge.joceanus.jmetis.sheet.DataRow;
import net.sourceforge.joceanus.jmetis.sheet.DataView;
import net.sourceforge.joceanus.jmetis.sheet.DataWorkBook;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseIOException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.AccountRate;
import net.sourceforge.joceanus.jmoneywise.data.AccountRate.AccountRateList;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.data.TaskControl;
import net.sourceforge.joceanus.jprometheus.sheets.SheetEncrypted;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;

/**
 * SheetDataItem extension for AccountRate.
 * @author Tony Washer
 */
public class SheetAccountRate
        extends SheetEncrypted<AccountRate, MoneyWiseDataType> {
    /**
     * NamedArea for Rates.
     */
    private static final String AREA_RATES = AccountRate.LIST_NAME;

    /**
     * Account column.
     */
    private static final int COL_ACCOUNT = COL_CONTROLID + 1;

    /**
     * Rate column.
     */
    private static final int COL_RATE = COL_ACCOUNT + 1;

    /**
     * Bonus column.
     */
    private static final int COL_BONUS = COL_RATE + 1;

    /**
     * EndDate column.
     */
    private static final int COL_ENDDATE = COL_BONUS + 1;

    /**
     * Rates data list.
     */
    private final AccountRateList theList;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the spreadsheet reader
     */
    protected SheetAccountRate(final MoneyWiseReader pReader) {
        /* Call super constructor */
        super(pReader, AREA_RATES);

        /* Access the Rates list */
        theList = pReader.getData().getAccountRates();
        setDataList(theList);
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected SheetAccountRate(final MoneyWiseWriter pWriter) {
        /* Call super constructor */
        super(pWriter, AREA_RATES);

        /* Access the Rates list */
        theList = pWriter.getData().getAccountRates();
        setDataList(theList);
    }

    @Override
    protected DataValues<MoneyWiseDataType> loadSecureValues() throws JOceanusException {
        /* Build data values */
        DataValues<MoneyWiseDataType> myValues = getRowValues(AccountRate.OBJECT_NAME);
        myValues.addValue(AccountRate.FIELD_ACCOUNT, loadInteger(COL_ACCOUNT));
        myValues.addValue(AccountRate.FIELD_RATE, loadBytes(COL_RATE));
        myValues.addValue(AccountRate.FIELD_BONUS, loadBytes(COL_BONUS));
        myValues.addValue(AccountRate.FIELD_ENDDATE, loadDate(COL_ENDDATE));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void insertSecureItem(final AccountRate pItem) throws JOceanusException {
        /* Set the fields */
        super.insertSecureItem(pItem);
        writeInteger(COL_ACCOUNT, pItem.getAccountId());
        writeBytes(COL_RATE, pItem.getRateBytes());
        writeBytes(COL_BONUS, pItem.getBonusBytes());
        writeDate(COL_ENDDATE, pItem.getEndDate());
    }

    @Override
    protected int getLastColumn() {
        /* Return the last column */
        return COL_ENDDATE;
    }

    @Override
    protected void postProcessOnLoad() throws JOceanusException {
        /* Resolve links and reSort */
        theList.resolveDataSetLinks();
        theList.reSort();

        /* Validate the rates */
        theList.validateOnLoad();
    }

    /**
     * Load the Rates from an archive.
     * @param pTask the task control
     * @param pWorkBook the workbook
     * @param pData the data set to load into
     * @return continue to load <code>true/false</code>
     * @throws JOceanusException on error
     */
    protected static boolean loadArchive(final TaskControl<MoneyWiseData> pTask,
                                         final DataWorkBook pWorkBook,
                                         final MoneyWiseData pData) throws JOceanusException {
        /* Access the list of rates */
        AccountRateList myList = pData.getAccountRates();

        /* Protect against exceptions */
        try {
            /* Find the range of cells */
            DataView myView = pWorkBook.getRangeView(AREA_RATES);

            /* Access the number of reporting steps */
            int mySteps = pTask.getReportingSteps();
            int myCount = 0;

            /* Declare the new stage */
            if (!pTask.setNewStage(AREA_RATES)) {
                return false;
            }

            /* Count the number of Rates */
            int myTotal = myView.getRowCount();

            /* Declare the number of steps */
            if (!pTask.setNumSteps(myTotal)) {
                return false;
            }

            /* Loop through the rows of the table */
            for (int i = 0; i < myTotal; i++) {
                /* Access the cell by reference */
                DataRow myRow = myView.getRowByIndex(i);
                int iAdjust = 0;

                /* Access account */
                DataCell myCell = myView.getRowCellByIndex(myRow, iAdjust++);
                String myAccount = myCell.getStringValue();

                /* Handle Rate */
                myCell = myView.getRowCellByIndex(myRow, iAdjust++);
                String myRate = myCell.getStringValue();

                /* Handle bonus which may be missing */
                myCell = myView.getRowCellByIndex(myRow, iAdjust++);
                String myBonus = null;
                if (myCell != null) {
                    myBonus = myCell.getStringValue();
                }

                /* Handle expiration which may be missing */
                myCell = myView.getRowCellByIndex(myRow, iAdjust++);
                JDateDay myExpiry = null;
                if (myCell != null) {
                    myExpiry = myCell.getDateValue();
                }

                /* Build data values */
                DataValues<MoneyWiseDataType> myValues = new DataValues<MoneyWiseDataType>(AccountRate.OBJECT_NAME);
                myValues.addValue(AccountRate.FIELD_ACCOUNT, myAccount);
                myValues.addValue(AccountRate.FIELD_RATE, myRate);
                myValues.addValue(AccountRate.FIELD_BONUS, myBonus);
                myValues.addValue(AccountRate.FIELD_ENDDATE, myExpiry);

                /* Add the value into the list */
                myList.addValuesItem(myValues);

                /* Report the progress */
                myCount++;
                if (((myCount % mySteps) == 0) && (!pTask.setStepsDone(myCount))) {
                    return false;
                }
            }

            /* Resolve links and reSort */
            myList.resolveDataSetLinks();
            myList.reSort();

            /* Validate the rates */
            myList.validateOnLoad();

            /* Handle exceptions */
        } catch (JOceanusException e) {
            throw new JMoneyWiseIOException("Failed to Load " + myList.getItemType().getListName(), e);
        }

        /* Return to caller */
        return true;
    }
}
