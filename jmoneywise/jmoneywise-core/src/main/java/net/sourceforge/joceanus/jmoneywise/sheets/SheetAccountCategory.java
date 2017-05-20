/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2016 Tony Washer
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

import net.sourceforge.joceanus.jmetis.lethe.threads.MetisThreadCancelException;
import net.sourceforge.joceanus.jmetis.lethe.threads.MetisThreadStatusReport;
import net.sourceforge.joceanus.jmetis.sheet.MetisDataCell;
import net.sourceforge.joceanus.jmetis.sheet.MetisDataRow;
import net.sourceforge.joceanus.jmetis.sheet.MetisDataView;
import net.sourceforge.joceanus.jmetis.sheet.MetisDataWorkBook;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseIOException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseLogicException;
import net.sourceforge.joceanus.jmoneywise.data.CashCategory;
import net.sourceforge.joceanus.jmoneywise.data.CashCategory.CashCategoryList;
import net.sourceforge.joceanus.jmoneywise.data.DepositCategory;
import net.sourceforge.joceanus.jmoneywise.data.DepositCategory.DepositCategoryList;
import net.sourceforge.joceanus.jmoneywise.data.LoanCategory;
import net.sourceforge.joceanus.jmoneywise.data.LoanCategory.LoanCategoryList;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jtethys.OceanusException;

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
     * @param pReport the report
     * @param pWorkBook the workbook
     * @param pData the data set to load into
     * @throws OceanusException on error
     */
    protected static void loadArchive(final MetisThreadStatusReport pReport,
                                      final MetisDataWorkBook pWorkBook,
                                      final MoneyWiseData pData) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Find the range of cells */
            MetisDataView myView = pWorkBook.getRangeView(AREA_ACTCATEGORIES);

            /* Declare the new stage */
            pReport.setNewStage(AREA_ACTCATEGORIES);

            /* Count the number of Categories */
            int myTotal = myView.getRowCount();

            /* Declare the number of steps */
            pReport.setNumSteps(myTotal);

            /* Loop through the rows of the table */
            for (int i = 0; i < myTotal; i++) {
                /* Access the cell by reference */
                MetisDataRow myRow = myView.getRowByIndex(i);

                /* Process category */
                processCategory(pData, myView, myRow);

                /* Report the progress */
                pReport.setNextStep();
            }

            /* Resolve Category lists */
            resolveCategoryLists(pData);

            /* Handle exceptions */
        } catch (MetisThreadCancelException e) {
            throw e;
        } catch (OceanusException e) {
            throw new MoneyWiseIOException("Failed to Load " + AREA_ACTCATEGORIES, e);
        }
    }

    /**
     * Process row into alternate form.
     * @param pData the DataSet
     * @param pView the spreadsheet view
     * @param pRow the spreadsheet row
     * @throws OceanusException on error
     */
    private static void processCategory(final MoneyWiseData pData,
                                        final MetisDataView pView,
                                        final MetisDataRow pRow) throws OceanusException {
        /* Access name */
        int iAdjust = -1;
        String myName = pView.getRowCellByIndex(pRow, ++iAdjust).getStringValue();
        ++iAdjust;

        /* Access parent */
        MetisDataCell myCell = pView.getRowCellByIndex(pRow, ++iAdjust);
        String myParent = (myCell == null)
                                           ? null
                                           : myCell.getStringValue();

        /* Access category class and ignore if doesn't exist */
        myCell = pView.getRowCellByIndex(pRow, ++iAdjust);
        if (myCell == null) {
            return;
        }

        /* Access class and category */
        String myClass = myCell.getStringValue();
        String myCat = pView.getRowCellByIndex(pRow, ++iAdjust).getStringValue();

        /* If the category is parent then null the parent reference */
        if (myName.indexOf(':') == -1) {
            myParent = null;
        }

        /* If this is a Deposit Category */
        if (myClass.equals(MoneyWiseDataType.DEPOSIT.toString())) {
            /* Build data values */
            DataValues<MoneyWiseDataType> myValues = new DataValues<>(DepositCategory.OBJECT_NAME);
            myValues.addValue(DepositCategory.FIELD_CATTYPE, myCat);
            myValues.addValue(DepositCategory.FIELD_PARENT, myParent);
            myValues.addValue(DepositCategory.FIELD_NAME, myName);

            /* Add the value into the list */
            DepositCategoryList myList = pData.getDepositCategories();
            myList.addValuesItem(myValues);

            /* If this is a cash category */
        } else if (myClass.equals(MoneyWiseDataType.CASH.toString())) {
            /* Build data values */
            DataValues<MoneyWiseDataType> myValues = new DataValues<>(CashCategory.OBJECT_NAME);
            myValues.addValue(CashCategory.FIELD_CATTYPE, myCat);
            myValues.addValue(CashCategory.FIELD_PARENT, myParent);
            myValues.addValue(CashCategory.FIELD_NAME, myName);

            /* Add the value into the list */
            CashCategoryList myList = pData.getCashCategories();
            myList.addValuesItem(myValues);

            /* If this is a loan category */
        } else if (myClass.equals(MoneyWiseDataType.LOAN.toString())) {
            /* Build data values */
            DataValues<MoneyWiseDataType> myValues = new DataValues<>(LoanCategory.OBJECT_NAME);
            myValues.addValue(LoanCategory.FIELD_CATTYPE, myCat);
            myValues.addValue(LoanCategory.FIELD_PARENT, myParent);
            myValues.addValue(LoanCategory.FIELD_NAME, myName);

            /* Add the value into the list */
            LoanCategoryList myList = pData.getLoanCategories();
            myList.addValuesItem(myValues);

        } else {
            throw new MoneyWiseLogicException("Unexpected Account Class" + myClass);
        }
    }

    /**
     * Resolve category lists.
     * @param pData the DataSet
     * @throws OceanusException on error
     */
    private static void resolveCategoryLists(final MoneyWiseData pData) throws OceanusException {
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
