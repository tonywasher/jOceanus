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
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountInfoType;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountInfoType.AccountInfoTypeList;
import net.sourceforge.joceanus.jspreadsheetmanager.DataCell;
import net.sourceforge.joceanus.jspreadsheetmanager.DataRow;
import net.sourceforge.joceanus.jspreadsheetmanager.DataView;
import net.sourceforge.joceanus.jspreadsheetmanager.DataWorkBook;

/**
 * SheetStaticData extension for AccountInfoType.
 * @author Tony Washer
 */
public class SheetAccountInfoType
        extends SheetStaticData<AccountInfoType> {
    /**
     * NamedArea for AccountInfoType.
     */
    private static final String AREA_ACCOUNTINFOTYPES = AccountInfoType.LIST_NAME;

    /**
     * NameList for AccountInfoType.
     */
    protected static final String AREA_ACCOUNTINFOTYPENAMES = AccountInfoType.OBJECT_NAME
                                                              + "Names";

    /**
     * AccountInfoTypes data list.
     */
    private final AccountInfoTypeList theList;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the spreadsheet reader
     */
    protected SheetAccountInfoType(final MoneyWiseReader pReader) {
        /* Call super-constructor */
        super(pReader, AREA_ACCOUNTINFOTYPES);

        /* Access the InfoType list */
        theList = pReader.getData().getActInfoTypes();
        setDataList(theList);
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected SheetAccountInfoType(final MoneyWiseWriter pWriter) {
        /* Call super-constructor */
        super(pWriter, AREA_ACCOUNTINFOTYPES, AREA_ACCOUNTINFOTYPENAMES);

        /* Access the InfoType list */
        theList = pWriter.getData().getActInfoTypes();
        setDataList(theList);
    }

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

    @Override
    protected void loadClearTextItem(final Integer pId,
                                     final Boolean isEnabled,
                                     final Integer iOrder,
                                     final String pName,
                                     final String pDesc) throws JDataException {
        /* Create the item */
        theList.addOpenItem(pId, isEnabled, iOrder, pName, pDesc);
    }

    /**
     * Load the InfoTypes from an archive.
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
            DataView myView = pWorkBook.getRangeView(AREA_ACCOUNTINFOTYPES);

            /* Declare the new stage */
            if (!pTask.setNewStage(AREA_ACCOUNTINFOTYPES)) {
                return false;
            }

            /* Access the number of reporting steps */
            int myCount = 0;
            int mySteps = pTask.getReportingSteps();

            /* Count the number of InfoTypes */
            int myTotal = myView.getRowCount();

            /* Access the list of InfoTypes */
            AccountInfoTypeList myList = pData.getActInfoTypes();

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

            /* Handle Exceptions */
        } catch (JDataException e) {
            throw new JDataException(ExceptionClass.EXCEL, "Failed to load AccountInfoTypes", e);
        }

        /* Return to caller */
        return true;
    }
}
