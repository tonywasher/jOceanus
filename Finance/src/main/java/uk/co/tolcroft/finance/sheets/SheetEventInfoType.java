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
package uk.co.tolcroft.finance.sheets;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataException.ExceptionClass;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;

import uk.co.tolcroft.finance.data.EventInfoType;
import uk.co.tolcroft.finance.data.EventInfoType.EventInfoTypeList;
import uk.co.tolcroft.finance.data.FinanceData;
import uk.co.tolcroft.models.sheets.SheetReader.SheetHelper;
import uk.co.tolcroft.models.sheets.SheetStaticData;
import uk.co.tolcroft.models.threads.ThreadStatus;

public class SheetEventInfoType extends SheetStaticData<EventInfoType> {
    /**
     * NamedArea for EventInfoType.
     */
    private static final String EventInfoTypes = EventInfoType.LIST_NAME;

    /**
     * NameList for EventInfoType.
     */
    protected static final String EventInfoTypeNames = EventInfoType.OBJECT_NAME + "Names";

    /**
     * EventInfoTypes data list.
     */
    private final EventInfoTypeList theList;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the spreadsheet reader
     */
    protected SheetEventInfoType(final FinanceReader pReader) {
        /* Call super-constructor */
        super(pReader, EventInfoTypes);

        /* Access the InfoType list */
        theList = pReader.getData().getInfoTypes();
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected SheetEventInfoType(final FinanceWriter pWriter) {
        /* Call super-constructor */
        super(pWriter, EventInfoTypes, EventInfoTypeNames);

        /* Access the InfoType list */
        theList = pWriter.getData().getInfoTypes();
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
        theList.addItem(pId, pControlId, isEnabled, iOrder, pName, pDesc);
    }

    @Override
    protected void loadClearTextItem(final int pId,
                                     final boolean isEnabled,
                                     final int iOrder,
                                     final String pName,
                                     final String pDesc) throws JDataException {
        /* Create the item */
        theList.addItem(pId, isEnabled, iOrder, pName, pDesc);
    }

    /**
     * Load the InfoTypes from an archive.
     * @param pThread the thread status control
     * @param pHelper the sheet helper
     * @param pData the data set to load into
     * @return continue to load <code>true/false</code>
     * @throws JDataException on error
     */
    protected static boolean loadArchive(final ThreadStatus<FinanceData> pThread,
                                         final SheetHelper pHelper,
                                         final FinanceData pData) throws JDataException {
        /* Protect against exceptions */
        try {
            /* Find the range of cells */
            AreaReference myRange = pHelper.resolveAreaReference(EventInfoTypes);

            /* Declare the new stage */
            if (!pThread.setNewStage(EventInfoTypes)) {
                return false;
            }

            /* Access the number of reporting steps */
            int myCount = 0;
            int mySteps = pThread.getReportingSteps();

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
                EventInfoTypeList myList = pData.getInfoTypes();

                /* Declare the number of steps */
                if (!pThread.setNumSteps(myTotal)) {
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
                    if (((myCount % mySteps) == 0) && (!pThread.setStepsDone(myCount))) {
                        return false;
                    }
                }
            }
        } catch (Exception e) {
            throw new JDataException(ExceptionClass.EXCEL, "Failed to load EventInfoTypes", e);
        }

        /* Return to caller */
        return true;
    }
}
