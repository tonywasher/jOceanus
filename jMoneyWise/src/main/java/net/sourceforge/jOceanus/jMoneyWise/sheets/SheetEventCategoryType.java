/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2013 Tony Washer
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
package net.sourceforge.jOceanus.jMoneyWise.sheets;

import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataManager.JDataException.ExceptionClass;
import net.sourceforge.jOceanus.jDataModels.data.TaskControl;
import net.sourceforge.jOceanus.jDataModels.sheets.SheetStaticData;
import net.sourceforge.jOceanus.jMoneyWise.data.FinanceData;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.EventCategoryType;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.EventCategoryType.EventCategoryTypeList;
import net.sourceforge.jOceanus.jSpreadSheetManager.DataCell;
import net.sourceforge.jOceanus.jSpreadSheetManager.DataRow;
import net.sourceforge.jOceanus.jSpreadSheetManager.DataView;
import net.sourceforge.jOceanus.jSpreadSheetManager.DataWorkBook;

/**
 * SheetStaticData extension for EventCategoryType.
 * @author Tony Washer
 */
public class SheetEventCategoryType
        extends SheetStaticData<EventCategoryType> {

    /**
     * NamedArea for Category Types.
     */
    private static final String AREA_CATTYPES = EventCategoryType.LIST_NAME;

    /**
     * NameList for Category Types.
     */
    protected static final String AREA_CATTYPENAMES = EventCategoryType.OBJECT_NAME
                                                      + "Names";

    /**
     * EventCategoryTypes data list.
     */
    private final EventCategoryTypeList theList;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the spreadsheet reader
     */
    protected SheetEventCategoryType(final FinanceReader pReader) {
        /* Call super-constructor */
        super(pReader, AREA_CATTYPES);

        /* Access the Category Type list */
        theList = pReader.getData().getEventCategoryTypes();
        setDataList(theList);
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected SheetEventCategoryType(final FinanceWriter pWriter) {
        /* Call super-constructor */
        super(pWriter, AREA_CATTYPES, AREA_CATTYPENAMES);

        /* Access the Category Type list */
        theList = pWriter.getData().getEventCategoryTypes();
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
     * @param pId the id
     * @param isEnabled isEnabled
     * @param iOrder the sort order
     * @param pName the name
     * @param pDesc the description
     * @throws JDataException on error
     */
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
     * Load the Transaction Types from an archive.
     * @param pTask the task control
     * @param pWorkBook the workbook
     * @param pData the data set to load into
     * @return continue to load <code>true/false</code>
     * @throws JDataException on error
     */
    protected static boolean loadArchive(final TaskControl<FinanceData> pTask,
                                         final DataWorkBook pWorkBook,
                                         final FinanceData pData) throws JDataException {
        /* Protect against exceptions */
        try {
            /* Find the range of cells */
            DataView myView = pWorkBook.getRangeView(AREA_CATTYPES);

            /* Declare the new stage */
            if (!pTask.setNewStage(AREA_CATTYPES)) {
                return false;
            }

            /* Access the number of reporting steps */
            int mySteps = pTask.getReportingSteps();
            int myCount = 0;

            /* Count the number of EventCategoryTypes */
            int myTotal = myView.getRowCount();

            /* Access the list of category types */
            EventCategoryTypeList myList = pData.getEventCategoryTypes();

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
            throw new JDataException(ExceptionClass.EXCEL, "Failed to Load Category Types", e);
        }

        /* Return to caller */
        return true;
    }
}