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
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountCurrency;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountCurrency.AccountCurrencyList;
import net.sourceforge.joceanus.jprometheus.data.TaskControl;
import net.sourceforge.joceanus.jprometheus.sheets.SheetStaticData;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * SheetStaticData extension for AccountCurrency.
 * @author Tony Washer
 */
public class SheetAccountCurrency
        extends SheetStaticData<AccountCurrency, MoneyWiseDataType> {
    /**
     * NamedArea for AccountCurrencies.
     */
    private static final String AREA_ACCOUNTCURRENCIES = AccountCurrency.LIST_NAME;

    /**
     * NameList for AccountCurrencies.
     */
    protected static final String AREA_ACCOUNTCURRNAMES = AccountCurrency.OBJECT_NAME + "Names";

    /**
     * Default column.
     */
    private static final int COL_DEFAULT = COL_DESC + 1;

    /**
     * AccountCurrencies data list.
     */
    private final AccountCurrencyList theList;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the spreadsheet reader
     */
    protected SheetAccountCurrency(final MoneyWiseReader pReader) {
        /* Call super-constructor */
        super(pReader, AREA_ACCOUNTCURRENCIES);

        /* Access the Account Type list */
        theList = pReader.getData().getAccountCurrencies();
        setDataList(theList);
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected SheetAccountCurrency(final MoneyWiseWriter pWriter) {
        /* Call super-constructor */
        super(pWriter, AREA_ACCOUNTCURRENCIES, AREA_ACCOUNTCURRNAMES);

        /* Access the Account Type list */
        theList = pWriter.getData().getAccountCurrencies();
        setDataList(theList);
    }

    @Override
    protected void loadEncryptedItem(final Integer pId,
                                     final Integer pControlId,
                                     final Boolean isEnabled,
                                     final Integer pOrder,
                                     final byte[] pName,
                                     final byte[] pDesc) throws JOceanusException {
        /* Access the Default indication */
        Boolean isDefault = loadBoolean(COL_DEFAULT);

        /* Create the item */
        theList.addSecureItem(pId, pControlId, isEnabled, pOrder, pName, pDesc, isDefault);
    }

    @Override
    protected void loadClearTextItem(final Integer uId,
                                     final Boolean isEnabled,
                                     final Integer pOrder,
                                     final String pName,
                                     final String pDesc) throws JOceanusException {
        /* Access the Default indication */
        Boolean isDefault = loadBoolean(COL_DEFAULT);

        /* Create the item */
        theList.addOpenItem(uId, isEnabled, pOrder, pName, pDesc, isDefault);
    }

    @Override
    protected void insertSecureItem(final AccountCurrency pItem) throws JOceanusException {
        /* Insert standard fields */
        super.insertSecureItem(pItem);

        /* Set default indication */
        writeBoolean(COL_DEFAULT, pItem.isDefault());
    }

    @Override
    protected void insertOpenItem(final AccountCurrency pItem) throws JOceanusException {
        /* Insert standard fields */
        super.insertOpenItem(pItem);

        /* Set default indication */
        writeBoolean(COL_DEFAULT, pItem.isDefault());
    }

    @Override
    protected void prepareSheet() throws JOceanusException {
        /* Prepare standard fields */
        super.prepareSheet();

        /* Write titles */
        writeHeader(COL_DEFAULT, AccountCurrency.FIELD_DEFAULT.getName());
    }

    @Override
    protected void formatSheet() throws JOceanusException {
        /* Format standard fields */
        super.formatSheet();

        /* Set default column types */
        setBooleanColumn(COL_DEFAULT);
    }

    @Override
    protected int getLastColumn() {
        /* Return the last column */
        return COL_DEFAULT;
    }

    /**
     * Load the Account Currencies from an archive.
     * @param pTask the task control
     * @param pWorkBook the workbook
     * @param pData the data set to load into
     * @return continue to load <code>true/false</code>
     * @throws JOceanusException on error
     */
    protected static boolean loadArchive(final TaskControl<MoneyWiseData> pTask,
                                         final DataWorkBook pWorkBook,
                                         final MoneyWiseData pData) throws JOceanusException {
        /* Access the list of account currencies */
        AccountCurrencyList myList = pData.getAccountCurrencies();

        /* Protect against exceptions */
        try {
            /* Find the range of cells */
            DataView myView = pWorkBook.getRangeView(AREA_ACCOUNTCURRENCIES);

            /* Declare the new stage */
            if (!pTask.setNewStage(AREA_ACCOUNTCURRENCIES)) {
                return false;
            }

            /* Access the number of reporting steps */
            int mySteps = pTask.getReportingSteps();
            int myCount = 0;

            /* Count the number of AccountCurrencies */
            int myTotal = myView.getRowCount();

            /* Declare the number of steps */
            if (!pTask.setNumSteps(myTotal)) {
                return false;
            }

            /* Loop through the rows of the single column range */
            for (int i = 0; i < myTotal; i++) {
                /* Access the cell by reference */
                DataRow myRow = myView.getRowByIndex(i);
                DataCell myCell = myView.getRowCellByIndex(myRow, 0);

                /* Add the value into the finance tables */
                myList.addBasicItem(myCell.getStringValue());

                /* Report the progress */
                myCount++;
                if (((myCount % mySteps) == 0) && (!pTask.setStepsDone(myCount))) {
                    return false;
                }
            }

            /* Initialise the default currency */
            myList.initialiseDefault();

            /* Sort the list */
            myList.reSort();

            /* Handle exceptions */
        } catch (JOceanusException e) {
            throw new JMoneyWiseIOException("Failed to Load " + myList.getItemType().getListName(), e);
        }

        /* Return to caller */
        return true;
    }
}
