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
import net.sourceforge.joceanus.jmoneywise.data.Deposit;
import net.sourceforge.joceanus.jmoneywise.data.Deposit.DepositList;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountCurrency;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.data.TaskControl;
import net.sourceforge.joceanus.jprometheus.sheets.SheetEncrypted;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * SheetDataItem extension for Deposit.
 * @author Tony Washer
 */
public class SheetDeposit
        extends SheetEncrypted<Deposit, MoneyWiseDataType> {
    /**
     * NamedArea for Deposits.
     */
    private static final String AREA_DEPOSITS = Deposit.LIST_NAME;

    /**
     * NameList for Deposit.
     */
    protected static final String AREA_DEPOSITNAMES = Deposit.OBJECT_NAME + "Names";

    /**
     * Name column.
     */
    private static final int COL_NAME = COL_CONTROLID + 1;

    /**
     * Category column.
     */
    private static final int COL_CATEGORY = COL_NAME + 1;

    /**
     * Parent column.
     */
    private static final int COL_PARENT = COL_CATEGORY + 1;

    /**
     * Description column.
     */
    private static final int COL_DESC = COL_PARENT + 1;

    /**
     * Currency column.
     */
    private static final int COL_CURRENCY = COL_DESC + 1;

    /**
     * Gross column.
     */
    private static final int COL_GROSS = COL_CURRENCY + 1;

    /**
     * TaxFree column.
     */
    private static final int COL_TAXFREE = COL_GROSS + 1;

    /**
     * Closed column.
     */
    private static final int COL_CLOSED = COL_TAXFREE + 1;

    /**
     * Deposit data list.
     */
    private final DepositList theList;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the spreadsheet reader
     */
    protected SheetDeposit(final MoneyWiseReader pReader) {
        /* Call super constructor */
        super(pReader, AREA_DEPOSITS);

        /* Access the Deposits list */
        theList = pReader.getData().getDeposits();
        setDataList(theList);
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected SheetDeposit(final MoneyWiseWriter pWriter) {
        /* Call super constructor */
        super(pWriter, AREA_DEPOSITS);

        /* Access the Deposits list */
        theList = pWriter.getData().getDeposits();
        setDataList(theList);
    }

    @Override
    protected DataValues<MoneyWiseDataType> loadSecureValues() throws JOceanusException {
        /* Build data values */
        DataValues<MoneyWiseDataType> myValues = getRowValues(Deposit.OBJECT_NAME);
        myValues.addValue(Deposit.FIELD_CATEGORY, loadInteger(COL_CATEGORY));
        myValues.addValue(Deposit.FIELD_PARENT, loadInteger(COL_PARENT));
        myValues.addValue(Deposit.FIELD_CURRENCY, loadInteger(COL_CURRENCY));
        myValues.addValue(Deposit.FIELD_NAME, loadBytes(COL_NAME));
        myValues.addValue(Deposit.FIELD_DESC, loadBytes(COL_DESC));
        myValues.addValue(Deposit.FIELD_GROSS, loadBoolean(COL_GROSS));
        myValues.addValue(Deposit.FIELD_TAXFREE, loadBoolean(COL_TAXFREE));
        myValues.addValue(Deposit.FIELD_CLOSED, loadBoolean(COL_CLOSED));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void insertSecureItem(final Deposit pItem) throws JOceanusException {
        /* Set the fields */
        super.insertSecureItem(pItem);
        writeInteger(COL_CATEGORY, pItem.getCategoryId());
        writeInteger(COL_PARENT, pItem.getParentId());
        writeInteger(COL_CURRENCY, pItem.getDepositCurrencyId());
        writeBytes(COL_NAME, pItem.getNameBytes());
        writeBytes(COL_DESC, pItem.getDescBytes());
        writeBoolean(COL_GROSS, pItem.isGross());
        writeBoolean(COL_TAXFREE, pItem.isTaxFree());
        writeBoolean(COL_CLOSED, pItem.isClosed());
    }

    @Override
    protected int getLastColumn() {
        /* Return the last column */
        return COL_CLOSED;
    }

    @Override
    protected void postProcessOnLoad() throws JOceanusException {
        /* Resolve links and reSort */
        theList.resolveDataSetLinks();
        theList.reSort();

        /* Validate the deposits */
        theList.validateOnLoad();
    }

    /**
     * Load the Deposits from an archive.
     * @param pTask the task control
     * @param pWorkBook the workbook
     * @param pData the data set to load into
     * @return continue to load <code>true/false</code>
     * @throws JOceanusException on error
     */
    protected static boolean loadArchive(final TaskControl<MoneyWiseData> pTask,
                                         final DataWorkBook pWorkBook,
                                         final MoneyWiseData pData) throws JOceanusException {
        /* Access the list of deposits */
        DepositList myList = pData.getDeposits();

        /* Protect against exceptions */
        try {
            /* Find the range of cells */
            DataView myView = pWorkBook.getRangeView(AREA_DEPOSITS);

            /* Access the number of reporting steps */
            int mySteps = pTask.getReportingSteps();
            int myCount = 0;

            /* Declare the new stage */
            if (!pTask.setNewStage(Deposit.LIST_NAME)) {
                return false;
            }

            /* Count the number of deposits */
            int myTotal = myView.getRowCount();

            /* Declare the number of steps */
            if (!pTask.setNumSteps(myTotal)) {
                return false;
            }

            /* Access default currency */
            AccountCurrency myCurrency = pData.getDefaultCurrency();

            /* Loop through the rows of the table */
            for (int i = 0; i < myTotal; i++) {
                /* Access the cell by reference */
                DataRow myRow = myView.getRowByIndex(i);
                int iAdjust = 0;

                /* Access name */
                DataCell myCell = myView.getRowCellByIndex(myRow, iAdjust++);
                String myName = myCell.getStringValue();

                /* Access Category */
                myCell = myView.getRowCellByIndex(myRow, iAdjust++);
                String myCategory = myCell.getStringValue();

                /* Access Parent */
                myCell = myView.getRowCellByIndex(myRow, iAdjust++);
                String myParent = myCell.getStringValue();

                /* Access Gross Flag */
                myCell = myView.getRowCellByIndex(myRow, iAdjust++);
                Boolean isGross = myCell.getBooleanValue();

                /* Access TaxFree Flag */
                myCell = myView.getRowCellByIndex(myRow, iAdjust++);
                Boolean isTaxFree = myCell.getBooleanValue();

                /* Access Closed Flag */
                myCell = myView.getRowCellByIndex(myRow, iAdjust++);
                Boolean isClosed = myCell.getBooleanValue();

                /* Build data values */
                DataValues<MoneyWiseDataType> myValues = new DataValues<MoneyWiseDataType>(Deposit.OBJECT_NAME);
                myValues.addValue(Deposit.FIELD_NAME, myName);
                myValues.addValue(Deposit.FIELD_CATEGORY, myCategory);
                myValues.addValue(Deposit.FIELD_CURRENCY, myCurrency);
                myValues.addValue(Deposit.FIELD_PARENT, myParent);
                myValues.addValue(Deposit.FIELD_GROSS, isGross);
                myValues.addValue(Deposit.FIELD_TAXFREE, isTaxFree);
                myValues.addValue(Deposit.FIELD_CLOSED, isClosed);

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

            /* Touch underlying items */
            myList.touchUnderlyingItems();

            /* Validate the event categories */
            myList.validateOnLoad();

            /* Handle exceptions */
        } catch (JOceanusException e) {
            throw new JMoneyWiseIOException("Failed to Load " + myList.getItemType().getListName(), e);
        }

        /* Return to caller */
        return true;
    }
}
