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

import net.sourceforge.joceanus.jprometheus.data.TaskControl;
import net.sourceforge.joceanus.jprometheus.sheets.SheetDataItem;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseIOException;
import net.sourceforge.joceanus.jmoneywise.data.AccountRate;
import net.sourceforge.joceanus.jmoneywise.data.AccountRate.AccountRateList;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmetis.sheet.DataCell;
import net.sourceforge.joceanus.jmetis.sheet.DataRow;
import net.sourceforge.joceanus.jmetis.sheet.DataView;
import net.sourceforge.joceanus.jmetis.sheet.DataWorkBook;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * SheetDataItem extension for AccountRate.
 * @author Tony Washer
 */
public class SheetAccountRate
        extends SheetDataItem<AccountRate> {
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
        theList = pReader.getData().getRates();
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
        theList = pWriter.getData().getRates();
        setDataList(theList);
    }

    @Override
    protected void loadSecureItem(final Integer pId) throws JOceanusException {
        /* Access the IDs */
        Integer myControlId = loadInteger(COL_CONTROLID);
        Integer myActId = loadInteger(COL_ACCOUNT);

        /* Access the rates and end-date */
        byte[] myRateBytes = loadBytes(COL_RATE);
        byte[] myBonusBytes = loadBytes(COL_BONUS);
        JDateDay myEndDate = loadDate(COL_ENDDATE);

        /* Load the item */
        theList.addSecureItem(pId, myControlId, myActId, myRateBytes, myEndDate, myBonusBytes);
    }

    @Override
    protected void loadOpenItem(final Integer pId) throws JOceanusException {
        /* Access the account */
        String myAccount = loadString(COL_ACCOUNT);

        /* Access the name and description bytes */
        String myRate = loadString(COL_RATE);
        String myBonus = loadString(COL_BONUS);
        JDateDay myEndDate = loadDate(COL_ENDDATE);

        /* Load the item */
        theList.addOpenItem(pId, myAccount, myRate, myEndDate, myBonus);
    }

    @Override
    protected void insertSecureItem(final AccountRate pItem) throws JOceanusException {
        /* Set the fields */
        writeInteger(COL_CONTROLID, pItem.getControlKeyId());
        writeInteger(COL_ACCOUNT, pItem.getAccountId());
        writeBytes(COL_RATE, pItem.getRateBytes());
        writeBytes(COL_BONUS, pItem.getBonusBytes());
        writeDate(COL_ENDDATE, pItem.getEndDate());
    }

    @Override
    protected void insertOpenItem(final AccountRate pItem) throws JOceanusException {
        /* Set the fields */
        writeString(COL_ACCOUNT, pItem.getAccountName());
        writeDecimal(COL_RATE, pItem.getRate());
        writeDecimal(COL_BONUS, pItem.getBonus());
        writeDate(COL_ENDDATE, pItem.getEndDate());
    }

    @Override
    protected void prepareSheet() throws JOceanusException {
        /* Write titles */
        writeHeader(COL_ACCOUNT, AccountRate.FIELD_ACCOUNT.getName());
        writeHeader(COL_RATE, AccountRate.FIELD_RATE.getName());
        writeHeader(COL_BONUS, AccountRate.FIELD_BONUS.getName());
        writeHeader(COL_ENDDATE, AccountRate.FIELD_ENDDATE.getName());
    }

    @Override
    protected void formatSheet() throws JOceanusException {
        /* Set the column types */
        setStringColumn(COL_ACCOUNT);
        setRateColumn(COL_RATE);
        setRateColumn(COL_BONUS);
        setDateColumn(COL_ENDDATE);

        /* Set validation */
        applyDataValidation(COL_ACCOUNT, SheetAccount.AREA_ACCOUNTNAMES);
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

        /* Touch underlying items */
        theList.touchUnderlyingItems();

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

            /* Access the list of rates */
            AccountRateList myList = pData.getRates();

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

                /* Add the value into the finance tables */
                myList.addOpenItem(0, myAccount, myRate, myExpiry, myBonus);

                /* Report the progress */
                myCount++;
                if (((myCount % mySteps) == 0)
                    && (!pTask.setStepsDone(myCount))) {
                    return false;
                }
            }

            /* Resolve links and reSort */
            myList.resolveDataSetLinks();
            myList.reSort();

            /* Touch underlying items */
            myList.touchUnderlyingItems();

            /* Validate the rates */
            myList.validateOnLoad();

            /* Handle exceptions */
        } catch (JOceanusException e) {
            throw new JMoneyWiseIOException("Failed to Load Rates", e);
        }

        /* Return to caller */
        return true;
    }
}
