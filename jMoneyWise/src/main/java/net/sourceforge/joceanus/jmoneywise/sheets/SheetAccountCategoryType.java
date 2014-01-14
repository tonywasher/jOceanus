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

import net.sourceforge.joceanus.jdatamanager.JDataException;
import net.sourceforge.joceanus.jdatamanager.JDataException.ExceptionClass;
import net.sourceforge.joceanus.jdatamodels.data.TaskControl;
import net.sourceforge.joceanus.jdatamodels.sheets.SheetStaticData;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountCategoryType;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountCategoryType.AccountCategoryTypeList;
import net.sourceforge.joceanus.jspreadsheetmanager.DataCell;
import net.sourceforge.joceanus.jspreadsheetmanager.DataRow;
import net.sourceforge.joceanus.jspreadsheetmanager.DataView;
import net.sourceforge.joceanus.jspreadsheetmanager.DataWorkBook;

/**
 * SheetStaticData extension for AccountCategoryType.
 * @author Tony Washer
 */
public class SheetAccountCategoryType
        extends SheetStaticData<AccountCategoryType> {
    /**
     * NamedArea for AccountCategoryTypes.
     */
    private static final String AREA_ACCOUNTCATTYPES = AccountCategoryType.LIST_NAME;

    /**
     * NameList for AccountCategoryTypes.
     */
    protected static final String AREA_ACCOUNTCATTYPENAMES = AccountCategoryType.OBJECT_NAME
                                                             + "Names";

    /**
     * AccountCategoryTypes data list.
     */
    private final AccountCategoryTypeList theList;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the spreadsheet reader
     */
    protected SheetAccountCategoryType(final MoneyWiseReader pReader) {
        /* Call super-constructor */
        super(pReader, AREA_ACCOUNTCATTYPES);

        /* Access the Account Type list */
        theList = pReader.getData().getAccountCategoryTypes();
        setDataList(theList);
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected SheetAccountCategoryType(final MoneyWiseWriter pWriter) {
        /* Call super-constructor */
        super(pWriter, AREA_ACCOUNTCATTYPES, AREA_ACCOUNTCATTYPENAMES);

        /* Access the Account Type list */
        theList = pWriter.getData().getAccountCategoryTypes();
        setDataList(theList);
    }

    /**
     * Load encrypted.
     * @param pId the id
     * @param pControlId the control id
     * @param isEnabled isEnabled
     * @param iOrder the sort order
     * @param pName the name
     * @param pDesc the description
     * @throws JDataException on error
     */
    @Override
    protected void loadEncryptedItem(final Integer pId,
                                     final Integer pControlId,
                                     final Boolean isEnabled,
                                     final Integer iOrder,
                                     final byte[] pName,
                                     final byte[] pDesc) throws JDataException {
        /* Create the item */
        theList.addSecureItem(pId, pControlId, isEnabled, iOrder, pName, pDesc);
    }

    /**
     * Load clear text.
     * @param uId the id
     * @param isEnabled isEnabled
     * @param iOrder the sort order
     * @param pName the name
     * @param pDesc the description
     * @throws JDataException on error
     */
    @Override
    protected void loadClearTextItem(final Integer uId,
                                     final Boolean isEnabled,
                                     final Integer iOrder,
                                     final String pName,
                                     final String pDesc) throws JDataException {
        /* Create the item */
        theList.addOpenItem(uId, isEnabled, iOrder, pName, pDesc);
    }

    /**
     * Load the Account Types from an archive.
     * @param pTask the task control
     * @param pWorkBook the workbook
     * @param pData the data set to load into
     * @return continue to load <code>true/false</code>
     * @throws JDataException on error
     */
    protected static boolean loadArchive(final TaskControl<MoneyWiseData> pTask,
                                         final DataWorkBook pWorkBook,
                                         final MoneyWiseData pData) throws JDataException {
        /* Protect against exceptions */
        try {
            /* Find the range of cells */
            DataView myView = pWorkBook.getRangeView(AREA_ACCOUNTCATTYPES);

            /* Declare the new stage */
            if (!pTask.setNewStage(AREA_ACCOUNTCATTYPES)) {
                return false;
            }

            /* Access the number of reporting steps */
            int mySteps = pTask.getReportingSteps();
            int myCount = 0;

            /* Count the number of AccountCategoryTypes */
            int myTotal = myView.getRowCount();

            /* Access the list of account types */
            AccountCategoryTypeList myList = pData.getAccountCategoryTypes();

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
                if (((myCount % mySteps) == 0)
                    && (!pTask.setStepsDone(myCount))) {
                    return false;
                }
            }

            /* Sort the list */
            myList.reSort();

            /* Handle exceptions */
        } catch (JDataException e) {
            throw new JDataException(ExceptionClass.EXCEL, "Failed to Load Account Types", e);
        }

        /* Return to caller */
        return true;
    }
}
