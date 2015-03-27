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
import net.sourceforge.joceanus.jmoneywise.data.CashCategory;
import net.sourceforge.joceanus.jmoneywise.data.CashCategory.CashCategoryList;
import net.sourceforge.joceanus.jmoneywise.data.DepositCategory;
import net.sourceforge.joceanus.jmoneywise.data.DepositCategory.DepositCategoryList;
import net.sourceforge.joceanus.jmoneywise.data.LoanCategory;
import net.sourceforge.joceanus.jmoneywise.data.LoanCategory.LoanCategoryList;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.data.TaskControl;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * SheetDataItem extension for AccountCategory.
 * @author Tony Washer
 */
public final class SheetAccountCategory {
    /**
     * NamedArea for Categories.
     */
    protected static final String AREA_ACTCATEGORIES = "AccountCategoryInfo";

    /**
     * Private constructor.
     */
    private SheetAccountCategory() {
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
        /* Protect against exceptions */
        try {
            /* Find the range of cells */
            DataView myView = pWorkBook.getRangeView(AREA_ACTCATEGORIES);

            /* Access the number of reporting steps */
            int mySteps = pTask.getReportingSteps();
            int myCount = 0;

            /* Declare the new stage */
            if (!pTask.setNewStage(AREA_ACTCATEGORIES)) {
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

                /* Process category */
                processCategory(pData, myView, myRow);

                /* Report the progress */
                myCount++;
                if (((myCount % mySteps) == 0) && (!pTask.setStepsDone(myCount))) {
                    return false;
                }
            }

            /* Resolve Category lists */
            resolveCategoryLists(pData);

            /* Handle exceptions */
        } catch (JOceanusException e) {
            throw new JMoneyWiseIOException("Failed to Load " + AREA_ACTCATEGORIES, e);
        }

        /* Return to caller */
        return true;
    }

    /**
     * Process row into alternate form.
     * @param pData the DataSet
     * @param pView the spreadsheet view
     * @param pRow the spreadsheet row
     * @throws JOceanusException on error
     */
    private static void processCategory(final MoneyWiseData pData,
                                        final DataView pView,
                                        final DataRow pRow) throws JOceanusException {
        /* Access name */
        int iAdjust = 0;
        String myName = pView.getRowCellByIndex(pRow, iAdjust++).getStringValue();
        iAdjust++;

        /* Access parent */
        DataCell myCell = pView.getRowCellByIndex(pRow, iAdjust++);
        String myParent = (myCell == null)
                                          ? null
                                          : myCell.getStringValue();

        /* Access category class and ignore if doesn't exist */
        myCell = pView.getRowCellByIndex(pRow, iAdjust++);
        if (myCell == null) {
            return;
        }

        /* Access class and category */
        String myClass = myCell.getStringValue();
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
     * Resolve category lists.
     * @param pData the DataSet
     * @throws JOceanusException on error
     */
    private static void resolveCategoryLists(final MoneyWiseData pData) throws JOceanusException {
        /* Post process the deposit category list */
        DepositCategoryList myDepositList = pData.getDepositCategories();
        myDepositList.postProcessOnLoad();

        /* Post process the cash category list */
        CashCategoryList myCashList = pData.getCashCategories();
        myCashList.postProcessOnLoad();

        /* Post process the loan category list */
        LoanCategoryList myLoanList = pData.getLoanCategories();
        myLoanList.postProcessOnLoad();
    }
}
