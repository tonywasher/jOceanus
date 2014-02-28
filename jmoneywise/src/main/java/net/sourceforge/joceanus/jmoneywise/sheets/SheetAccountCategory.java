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
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseLogicException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.AccountCategory;
import net.sourceforge.joceanus.jmoneywise.data.AccountCategory.AccountCategoryList;
import net.sourceforge.joceanus.jmoneywise.data.CashCategory;
import net.sourceforge.joceanus.jmoneywise.data.CashCategory.CashCategoryList;
import net.sourceforge.joceanus.jmoneywise.data.DepositCategory;
import net.sourceforge.joceanus.jmoneywise.data.DepositCategory.DepositCategoryList;
import net.sourceforge.joceanus.jmoneywise.data.LoanCategory;
import net.sourceforge.joceanus.jmoneywise.data.LoanCategory.LoanCategoryList;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.data.TaskControl;
import net.sourceforge.joceanus.jprometheus.sheets.SheetEncrypted;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * SheetDataItem extension for AccountCategory.
 * @author Tony Washer
 */
public class SheetAccountCategory
        extends SheetEncrypted<AccountCategory, MoneyWiseDataType> {
    /**
     * NamedArea for Categories.
     */
    protected static final String AREA_ACTCATEGORIES = "AccountCategoryInfo";

    /**
     * Name column.
     */
    private static final int COL_NAME = COL_CONTROLID + 1;

    /**
     * Type column.
     */
    private static final int COL_TYPE = COL_NAME + 1;

    /**
     * Parent column.
     */
    private static final int COL_PARENT = COL_TYPE + 1;

    /**
     * Description column.
     */
    private static final int COL_DESC = COL_PARENT + 1;

    /**
     * Category data list.
     */
    private final AccountCategoryList theList;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the spreadsheet reader
     */
    protected SheetAccountCategory(final MoneyWiseReader pReader) {
        /* Call super constructor */
        super(pReader, AREA_ACTCATEGORIES);

        /* Access the Categories list */
        theList = pReader.getData().getAccountCategories();
        setDataList(theList);
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected SheetAccountCategory(final MoneyWiseWriter pWriter) {
        /* Call super constructor */
        super(pWriter, AREA_ACTCATEGORIES);

        /* Access the Categories list */
        theList = pWriter.getData().getAccountCategories();
        setDataList(theList);
    }

    @Override
    protected DataValues<MoneyWiseDataType> loadSecureValues() throws JOceanusException {
        /* Build data values */
        DataValues<MoneyWiseDataType> myValues = getRowValues(AccountCategory.OBJECT_NAME);
        myValues.addValue(AccountCategory.FIELD_CATTYPE, loadInteger(COL_TYPE));
        myValues.addValue(AccountCategory.FIELD_PARENT, loadInteger(COL_PARENT));
        myValues.addValue(AccountCategory.FIELD_NAME, loadBytes(COL_NAME));
        myValues.addValue(AccountCategory.FIELD_DESC, loadBytes(COL_DESC));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void insertSecureItem(final AccountCategory pItem) throws JOceanusException {
        /* Set the fields */
        super.insertSecureItem(pItem);
        writeInteger(COL_TYPE, pItem.getCategoryTypeId());
        writeInteger(COL_PARENT, pItem.getParentCategoryId());
        writeBytes(COL_NAME, pItem.getNameBytes());
        writeBytes(COL_DESC, pItem.getDescBytes());
    }

    @Override
    protected int getLastColumn() {
        /* Return the last column */
        return COL_DESC;
    }

    @Override
    protected void postProcessOnLoad() throws JOceanusException {
        /* Resolve links and reSort */
        theList.resolveDataSetLinks();
        theList.reSort();

        /* Validate the account categories */
        theList.validateOnLoad();
    }

    /**
     * Load the AccountCategories from an archive.
     * @param pTask the task control
     * @param pWorkBook the workbook
     * @param pData the data set to load into
     * @return continue to load <code>true/false</code>
     * @throws JOceanusException on error
     */
    protected static boolean loadArchive(final TaskControl<MoneyWiseData> pTask,
                                         final DataWorkBook pWorkBook,
                                         final MoneyWiseData pData) throws JOceanusException {
        /* Access the list of categories */
        AccountCategoryList myList = pData.getAccountCategories();

        /* Protect against exceptions */
        try {
            /* Find the range of cells */
            DataView myView = pWorkBook.getRangeView(AREA_ACTCATEGORIES);

            /* Access the number of reporting steps */
            int mySteps = pTask.getReportingSteps();
            int myCount = 0;

            /* Declare the new stage */
            if (!pTask.setNewStage(AccountCategory.LIST_NAME)) {
                return false;
            }

            /* Count the number of Categories */
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

                /* Access name */
                DataCell myCell = myView.getRowCellByIndex(myRow, iAdjust++);
                String myName = myCell.getStringValue();

                /* Access Type */
                myCell = myView.getRowCellByIndex(myRow, iAdjust++);
                String myType = myCell.getStringValue();

                /* Access Parent */
                String myParent = null;
                myCell = myView.getRowCellByIndex(myRow, iAdjust++);
                if (myCell != null) {
                    myParent = myCell.getStringValue();
                }

                /* Build data values */
                DataValues<MoneyWiseDataType> myValues = new DataValues<MoneyWiseDataType>(AccountCategory.OBJECT_NAME);
                myValues.addValue(AccountCategory.FIELD_CATTYPE, myType);
                myValues.addValue(AccountCategory.FIELD_PARENT, myParent);
                myValues.addValue(AccountCategory.FIELD_NAME, myName);

                /* Add the value into the list */
                myList.addValuesItem(myValues);

                /* Access Alternate Type */
                myCell = myView.getRowCellByIndex(myRow, iAdjust++);
                if (myCell != null) {
                    /* Process alternate if it exists */
                    processAlternate(pData, myView, myRow, myCell);
                }

                /* Report the progress */
                myCount++;
                if (((myCount % mySteps) == 0) && (!pTask.setStepsDone(myCount))) {
                    return false;
                }
            }

            /* Resolve links and reSort */
            myList.resolveDataSetLinks();
            myList.reSort();

            /* Validate the account categories */
            myList.validateOnLoad();

            /* Resolve Alternate lists */
            resolveAlternate(pData);

            /* Handle exceptions */
        } catch (JOceanusException e) {
            throw new JMoneyWiseIOException("Failed to Load " + myList.getItemType().getListName(), e);
        }

        /* Return to caller */
        return true;
    }

    /**
     * Process row into alternate form.
     * @param pData the DataSet
     * @param pView the spreadsheet view
     * @param pRow the spreadsheet row
     * @param pCell the spreadsheet cell
     * @throws JOceanusException on error
     */
    private static void processAlternate(final MoneyWiseData pData,
                                         final DataView pView,
                                         final DataRow pRow,
                                         final DataCell pCell) throws JOceanusException {
        /* Access name */
        int iAdjust = 0;
        String myName = pView.getRowCellByIndex(pRow, iAdjust++).getStringValue();
        iAdjust++;

        /* Access parent */
        String myParent = pView.getRowCellByIndex(pRow, iAdjust++).getStringValue();
        iAdjust++;

        /* Access class and category */
        String myClass = pCell.getStringValue();
        String myCat = pView.getRowCellByIndex(pRow, iAdjust++).getStringValue();

        /* If the category is parent then null the parent reference */
        if (myName.indexOf(':') == -1) {
            myParent = null;
        }

        /* If this is a Deposit Category */
        if (myClass.equals(MoneyWiseDataType.DEPOSIT.toString())) {
            /* Build data values */
            DataValues<MoneyWiseDataType> myValues = new DataValues<MoneyWiseDataType>(DepositCategory.OBJECT_NAME);
            myValues.addValue(DepositCategory.FIELD_CATTYPE, myCat);
            myValues.addValue(DepositCategory.FIELD_PARENT, myParent);
            myValues.addValue(DepositCategory.FIELD_NAME, myName);

            /* Add the value into the list */
            DepositCategoryList myList = pData.getDepositCategories();
            myList.addValuesItem(myValues);

            /* If this is a cash category */
        } else if (myClass.equals(MoneyWiseDataType.CASH.toString())) {
            /* Build data values */
            DataValues<MoneyWiseDataType> myValues = new DataValues<MoneyWiseDataType>(CashCategory.OBJECT_NAME);
            myValues.addValue(CashCategory.FIELD_CATTYPE, myCat);
            myValues.addValue(CashCategory.FIELD_PARENT, myParent);
            myValues.addValue(CashCategory.FIELD_NAME, myName);

            /* Add the value into the list */
            CashCategoryList myList = pData.getCashCategories();
            myList.addValuesItem(myValues);

            /* If this is a loan category */
        } else if (myClass.equals(MoneyWiseDataType.LOAN.toString())) {
            /* Build data values */
            DataValues<MoneyWiseDataType> myValues = new DataValues<MoneyWiseDataType>(LoanCategory.OBJECT_NAME);
            myValues.addValue(LoanCategory.FIELD_CATTYPE, myCat);
            myValues.addValue(LoanCategory.FIELD_PARENT, myParent);
            myValues.addValue(LoanCategory.FIELD_NAME, myName);

            /* Add the value into the list */
            LoanCategoryList myList = pData.getLoanCategories();
            myList.addValuesItem(myValues);

        } else {
            throw new JMoneyWiseLogicException("Unexpected Account Class" + myClass);
        }
    }

    /**
     * Resolve alternate lists.
     * @param pData the DataSet
     * @throws JOceanusException on error
     */
    private static void resolveAlternate(final MoneyWiseData pData) throws JOceanusException {
        /* Access lists */
        DepositCategoryList myDepositList = pData.getDepositCategories();
        CashCategoryList myCashList = pData.getCashCategories();
        LoanCategoryList myLoanList = pData.getLoanCategories();

        /* Sort the deposit category list and validate */
        myDepositList.resolveDataSetLinks();
        myDepositList.reSort();
        myDepositList.validateOnLoad();

        /* Sort the cash category list and validate */
        myCashList.resolveDataSetLinks();
        myCashList.reSort();
        myCashList.validateOnLoad();

        /* Sort the loan category list and validate */
        myLoanList.resolveDataSetLinks();
        myLoanList.reSort();
        myLoanList.validateOnLoad();
    }
}
