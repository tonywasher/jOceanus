/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2025 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.sourceforge.joceanus.moneywise.sheets;

import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseCashCategory;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseCashCategory.MoneyWiseCashCategoryList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDepositCategory;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDepositCategory.MoneyWiseDepositCategoryList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseLoanCategory;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseLoanCategory.MoneyWiseLoanCategoryList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseStaticDataType;
import net.sourceforge.joceanus.moneywise.exc.MoneyWiseIOException;
import net.sourceforge.joceanus.moneywise.exc.MoneyWiseLogicException;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.oceanus.profile.OceanusProfile;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataResource;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataValues;
import net.sourceforge.joceanus.prometheus.service.sheet.PrometheusSheetCell;
import net.sourceforge.joceanus.prometheus.service.sheet.PrometheusSheetRow;
import net.sourceforge.joceanus.prometheus.service.sheet.PrometheusSheetView;
import net.sourceforge.joceanus.prometheus.service.sheet.PrometheusSheetWorkBook;
import net.sourceforge.joceanus.tethys.api.thread.TethysUIThreadCancelException;
import net.sourceforge.joceanus.tethys.api.thread.TethysUIThreadStatusReport;

/**
 * Archive Loader for AccountCategory.
 * @author Tony Washer
 */
public final class MoneyWiseArchiveAccountCategory {
    /**
     * NamedArea for Categories.
     */
    static final String AREA_ACTCATEGORIES = "AccountCategoryInfo";

    /**
     * Report processor.
     */
    private final TethysUIThreadStatusReport theReport;

    /**
     * Workbook.
     */
    private final PrometheusSheetWorkBook theWorkBook;

    /**
     * DataSet.
     */
    private final MoneyWiseDataSet theData;

    /**
     * Constructor.
     * @param pReport the report
     * @param pWorkBook the workbook
     * @param pData the data set to load into
     */
    MoneyWiseArchiveAccountCategory(final TethysUIThreadStatusReport pReport,
                                    final PrometheusSheetWorkBook pWorkBook,
                                    final MoneyWiseDataSet pData) {
        theReport = pReport;
        theWorkBook = pWorkBook;
        theData = pData;
    }

    /**
     * Load the AccountCategories from an archive.
     * @param pStage the stage
     * @throws OceanusException on error
     */
    void loadArchive(final OceanusProfile pStage) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Find the range of cells */
            pStage.startTask("AccountCategories");
            final PrometheusSheetView myView = theWorkBook.getRangeView(AREA_ACTCATEGORIES);

            /* Declare the new stage */
            theReport.setNewStage(AREA_ACTCATEGORIES);

            /* Count the number of Categories */
            final int myTotal = myView.getRowCount();

            /* Declare the number of steps */
            theReport.setNumSteps(myTotal);

            /* Loop through the rows of the table */
            for (int i = 0; i < myTotal; i++) {
                /* Access the cell by reference */
                final PrometheusSheetRow myRow = myView.getRowByIndex(i);

                /* Process category */
                processCategory(myView, myRow);

                /* Report the progress */
                theReport.setNextStep();
            }

            /* Resolve Category lists */
            resolveCategoryLists();

            /* Handle exceptions */
        } catch (TethysUIThreadCancelException e) {
            throw e;
        } catch (OceanusException e) {
            throw new MoneyWiseIOException("Failed to Load " + AREA_ACTCATEGORIES, e);
        }
    }

    /**
     * Process row into alternate form.
     * @param pView the spreadsheet view
     * @param pRow the spreadsheet row
     * @throws OceanusException on error
     */
    private void processCategory(final PrometheusSheetView pView,
                                 final PrometheusSheetRow pRow) throws OceanusException {
        /* Access name */
        int iAdjust = -1;
        final String myName = pView.getRowCellByIndex(pRow, ++iAdjust).getString();
        ++iAdjust;

        /* Access parent */
        PrometheusSheetCell myCell = pView.getRowCellByIndex(pRow, ++iAdjust);
        String myParent = myCell == null
                ? null
                : myCell.getString();

        /* Access category class and ignore if doesn't exist */
        myCell = pView.getRowCellByIndex(pRow, ++iAdjust);
        if (myCell == null) {
            return;
        }

        /* Access class and category */
        final String myClass = myCell.getString();
        final String myCat = pView.getRowCellByIndex(pRow, ++iAdjust).getString();

        /* If the category is parent then null the parent reference */
        if (myName.indexOf(':') == -1) {
            myParent = null;
        }

        /* If this is a Deposit Category */
        if (myClass.equals(MoneyWiseBasicDataType.DEPOSIT.toString())) {
            /* Build data values */
            final PrometheusDataValues myValues = new PrometheusDataValues(MoneyWiseDepositCategory.OBJECT_NAME);
            myValues.addValue(MoneyWiseStaticDataType.DEPOSITTYPE, myCat);
            myValues.addValue(PrometheusDataResource.DATAGROUP_PARENT, myParent);
            myValues.addValue(PrometheusDataResource.DATAITEM_FIELD_NAME, myName);

            /* Add the value into the list */
            final MoneyWiseDepositCategoryList myList = theData.getDepositCategories();
            myList.addValuesItem(myValues);

            /* If this is a cash category */
        } else if (myClass.equals(MoneyWiseBasicDataType.CASH.toString())) {
            /* Build data values */
            final  PrometheusDataValues myValues = new  PrometheusDataValues(MoneyWiseCashCategory.OBJECT_NAME);
            myValues.addValue(MoneyWiseStaticDataType.CASHTYPE, myCat);
            myValues.addValue(PrometheusDataResource.DATAGROUP_PARENT, myParent);
            myValues.addValue(PrometheusDataResource.DATAITEM_FIELD_NAME, myName);

            /* Add the value into the list */
            final MoneyWiseCashCategoryList myList = theData.getCashCategories();
            myList.addValuesItem(myValues);

            /* If this is a loan category */
        } else if (myClass.equals(MoneyWiseBasicDataType.LOAN.toString())) {
            /* Build data values */
            final  PrometheusDataValues myValues = new  PrometheusDataValues(MoneyWiseLoanCategory.OBJECT_NAME);
            myValues.addValue(MoneyWiseStaticDataType.LOANTYPE, myCat);
            myValues.addValue(PrometheusDataResource.DATAGROUP_PARENT, myParent);
            myValues.addValue(PrometheusDataResource.DATAITEM_FIELD_NAME, myName);

            /* Add the value into the list */
            final MoneyWiseLoanCategoryList myList = theData.getLoanCategories();
            myList.addValuesItem(myValues);

        } else {
            throw new MoneyWiseLogicException("Unexpected Account Class" + myClass);
        }
    }

    /**
     * Resolve category lists.
     * @throws OceanusException on error
     */
    private void resolveCategoryLists() throws OceanusException {
        /* Post process the deposit category list */
        final MoneyWiseDepositCategoryList myDepositList = theData.getDepositCategories();
        myDepositList.postProcessOnLoad();

        /* Post process the cash category list */
        final MoneyWiseCashCategoryList myCashList = theData.getCashCategories();
        myCashList.postProcessOnLoad();

        /* Post process the loan category list */
        final MoneyWiseLoanCategoryList myLoanList = theData.getLoanCategories();
        myLoanList.postProcessOnLoad();
    }
}
