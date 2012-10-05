/*******************************************************************************
 * Copyright 2012 Tony Washer
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
package net.sourceforge.JFinanceApp.sheets;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataException.ExceptionClass;
import net.sourceforge.JDataModels.data.TaskControl;
import net.sourceforge.JDataModels.sheets.SheetReader.SheetHelper;
import net.sourceforge.JDataModels.sheets.SheetStaticData;
import net.sourceforge.JFinanceApp.data.FinanceData;
import net.sourceforge.JFinanceApp.data.statics.AccountInfoType;
import net.sourceforge.JFinanceApp.data.statics.AccountInfoType.AccountInfoTypeList;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;

/**
 * SheetStaticData extension for AccountInfoType.
 * @author Tony Washer
 */
public class SheetAccountInfoType extends SheetStaticData<AccountInfoType> {
    /**
     * NamedArea for AccountInfoType.
     */
    private static final String AREA_ACCOUNTINFOTYPES = AccountInfoType.LIST_NAME;

    /**
     * NameList for AccountInfoType.
     */
    protected static final String AREA_ACCOUNTINFOTYPENAMES = AccountInfoType.OBJECT_NAME + "Names";

    /**
     * AccountInfoTypes data list.
     */
    private final AccountInfoTypeList theList;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the spreadsheet reader
     */
    protected SheetAccountInfoType(final FinanceReader pReader) {
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
    protected SheetAccountInfoType(final FinanceWriter pWriter) {
        /* Call super-constructor */
        super(pWriter, AREA_ACCOUNTINFOTYPES, AREA_ACCOUNTINFOTYPENAMES);

        /* Access the InfoType list */
        theList = pWriter.getData().getActInfoTypes();
        setDataList(theList);
    }

    @Override
    protected void loadEncryptedItem(final int pId,
                                     final int pControlId,
                                     final boolean isEnabled,
                                     final int iOrder,
                                     final byte[] pName,
                                     final byte[] pDesc) throws JDataException {
        /* Create the item */
        theList.addSecureItem(pId, pControlId, isEnabled, iOrder, pName, pDesc);
    }

    @Override
    protected void loadClearTextItem(final int pId,
                                     final boolean isEnabled,
                                     final int iOrder,
                                     final String pName,
                                     final String pDesc) throws JDataException {
        /* Create the item */
        theList.addOpenItem(pId, isEnabled, iOrder, pName, pDesc);
    }

    /**
     * Load the InfoTypes from an archive.
     * @param pTask the task control
     * @param pHelper the sheet helper
     * @param pData the data set to load into
     * @return continue to load <code>true/false</code>
     * @throws JDataException on error
     */
    protected static boolean loadArchive(final TaskControl<FinanceData> pTask,
                                         final SheetHelper pHelper,
                                         final FinanceData pData) throws JDataException {
        /* Protect against exceptions */
        try {
            /* Find the range of cells */
            AreaReference myRange = pHelper.resolveAreaReference(AREA_ACCOUNTINFOTYPES);

            /* Declare the new stage */
            if (!pTask.setNewStage(AREA_ACCOUNTINFOTYPES)) {
                return false;
            }

            /* Access the number of reporting steps */
            int myCount = 0;
            int mySteps = pTask.getReportingSteps();

            /* If we found the range OK */
            if (myRange != null) {
                /* Access the relevant sheet and Cell references */
                CellReference myTop = myRange.getFirstCell();
                CellReference myBottom = myRange.getLastCell();
                Sheet mySheet = pHelper.getSheetByName(myTop.getSheetName());
                int myCol = myTop.getCol();

                /* Count the number of InfoTypes */
                int myTotal = myBottom.getRow() - myTop.getRow() + 1;

                /* Access the list of InfoTypes */
                AccountInfoTypeList myList = pData.getActInfoTypes();

                /* Declare the number of steps */
                if (!pTask.setNumSteps(myTotal)) {
                    return false;
                }

                /* Loop through the rows of the single column range */
                for (int i = myTop.getRow(); i <= myBottom.getRow(); i++) {
                    /* Access the cell by reference */
                    Row myRow = mySheet.getRow(i);
                    Cell myCell = myRow.getCell(myCol);

                    /* Add the value into the finance tables */
                    myList.addItem(myCell.getStringCellValue());

                    /* Report the progress */
                    myCount++;
                    if (((myCount % mySteps) == 0) && (!pTask.setStepsDone(myCount))) {
                        return false;
                    }
                }

                /* Sort the list */
                myList.reSort();
            }
        } catch (JDataException e) {
            throw new JDataException(ExceptionClass.EXCEL, "Failed to load AccountInfoTypes", e);
        }

        /* Return to caller */
        return true;
    }
}
