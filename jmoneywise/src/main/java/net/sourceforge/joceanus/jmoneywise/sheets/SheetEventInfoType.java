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
import net.sourceforge.joceanus.jmoneywise.data.statics.EventInfoType;
import net.sourceforge.joceanus.jmoneywise.data.statics.EventInfoType.EventInfoTypeList;
import net.sourceforge.joceanus.jprometheus.data.TaskControl;
import net.sourceforge.joceanus.jprometheus.sheets.SheetStaticData;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * SheetStaticData extension for EventInfoType.
 * @author Tony Washer
 */
public class SheetEventInfoType
        extends SheetStaticData<EventInfoType, MoneyWiseDataType> {
    /**
     * NamedArea for EventInfoType.
     */
    private static final String AREA_EVENTINFOTYPES = EventInfoType.LIST_NAME;

    /**
     * NameList for EventInfoType.
     */
    protected static final String AREA_EVENTINFOTYPENAMES = EventInfoType.OBJECT_NAME + "Names";

    /**
     * EventInfoTypes data list.
     */
    private final EventInfoTypeList theList;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the spreadsheet reader
     */
    protected SheetEventInfoType(final MoneyWiseReader pReader) {
        /* Call super-constructor */
        super(pReader, AREA_EVENTINFOTYPES);

        /* Access the InfoType list */
        theList = pReader.getData().getEventInfoTypes();
        setDataList(theList);
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected SheetEventInfoType(final MoneyWiseWriter pWriter) {
        /* Call super-constructor */
        super(pWriter, AREA_EVENTINFOTYPES, AREA_EVENTINFOTYPENAMES);

        /* Access the InfoType list */
        theList = pWriter.getData().getEventInfoTypes();
        setDataList(theList);
    }

    @Override
    protected void loadEncryptedItem(final Integer pId,
                                     final Integer pControlId,
                                     final Boolean isEnabled,
                                     final Integer iOrder,
                                     final byte[] pName,
                                     final byte[] pDesc) throws JOceanusException {
        /* Create the item */
        theList.addSecureItem(pId, pControlId, isEnabled, iOrder, pName, pDesc);
    }

    @Override
    protected void loadClearTextItem(final Integer pId,
                                     final Boolean isEnabled,
                                     final Integer iOrder,
                                     final String pName,
                                     final String pDesc) throws JOceanusException {
        /* Create the item */
        theList.addOpenItem(pId, isEnabled, iOrder, pName, pDesc);
    }

    /**
     * Load the InfoTypes from an archive.
     * @param pTask the task control
     * @param pWorkBook the workbook
     * @param pData the data set to load into
     * @return continue to load <code>true/false</code>
     * @throws JOceanusException on error
     */
    protected static boolean loadArchive(final TaskControl<MoneyWiseData> pTask,
                                         final DataWorkBook pWorkBook,
                                         final MoneyWiseData pData) throws JOceanusException {
        /* Access the list of InfoTypes */
        EventInfoTypeList myList = pData.getEventInfoTypes();

        /* Protect against exceptions */
        try {
            /* Find the range of cells */
            DataView myView = pWorkBook.getRangeView(AREA_EVENTINFOTYPES);

            /* Declare the new stage */
            if (!pTask.setNewStage(AREA_EVENTINFOTYPES)) {
                return false;
            }

            /* Access the number of reporting steps */
            int myCount = 0;
            int mySteps = pTask.getReportingSteps();

            /* Count the number of InfoTypes */
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

            /* Sort the list */
            myList.reSort();

            /* Handle Exceptions */
        } catch (JOceanusException e) {
            throw new JMoneyWiseIOException("Failed to load " + myList.getItemType().getListName(), e);
        }

        /* Return to caller */
        return true;
    }
}
