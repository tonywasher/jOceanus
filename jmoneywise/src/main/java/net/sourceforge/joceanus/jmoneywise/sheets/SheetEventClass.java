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
import net.sourceforge.joceanus.jmoneywise.data.EventCategory;
import net.sourceforge.joceanus.jmoneywise.data.EventClass;
import net.sourceforge.joceanus.jmoneywise.data.EventClass.EventClassList;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jprometheus.data.TaskControl;
import net.sourceforge.joceanus.jprometheus.sheets.SheetDataItem;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * SheetDataItem extension for EventClass.
 * @author Tony Washer
 */
public class SheetEventClass
        extends SheetDataItem<EventClass, MoneyWiseDataType> {
    /**
     * NamedArea for Event Classes.
     */
    private static final String AREA_EVENTCLASSES = EventClass.LIST_NAME;

    /**
     * Name column.
     */
    private static final int COL_NAME = COL_CONTROLID + 1;

    /**
     * Description column.
     */
    private static final int COL_DESC = COL_NAME + 1;

    /**
     * Class data list.
     */
    private final EventClassList theList;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the spreadsheet reader
     */
    protected SheetEventClass(final MoneyWiseReader pReader) {
        /* Call super constructor */
        super(pReader, AREA_EVENTCLASSES);

        /* Access the Class list */
        theList = pReader.getData().getEventClasses();
        setDataList(theList);
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected SheetEventClass(final MoneyWiseWriter pWriter) {
        /* Call super constructor */
        super(pWriter, AREA_EVENTCLASSES);

        /* Access the Class list */
        theList = pWriter.getData().getEventClasses();
        setDataList(theList);
    }

    @Override
    protected void loadSecureItem(final Integer pId) throws JOceanusException {
        /* Access the IDs */
        Integer myControlId = loadInteger(COL_CONTROLID);

        /* Access the Name and description */
        byte[] myNameBytes = loadBytes(COL_NAME);
        byte[] myDescBytes = loadBytes(COL_DESC);

        /* Load the item */
        theList.addSecureItem(pId, myControlId, myNameBytes, myDescBytes);
    }

    @Override
    protected void loadOpenItem(final Integer pId) throws JOceanusException {
        /* Access the name and description bytes */
        String myName = loadString(COL_NAME);
        String myDesc = loadString(COL_DESC);

        /* Load the item */
        theList.addOpenItem(pId, myName, myDesc);
    }

    @Override
    protected void insertSecureItem(final EventClass pItem) throws JOceanusException {
        /* Set the fields */
        writeInteger(COL_CONTROLID, pItem.getControlKeyId());
        writeBytes(COL_NAME, pItem.getNameBytes());
        writeBytes(COL_DESC, pItem.getDescBytes());
    }

    @Override
    protected void insertOpenItem(final EventClass pItem) throws JOceanusException {
        /* Set the fields */
        writeString(COL_NAME, pItem.getName());
        writeString(COL_DESC, pItem.getDesc());
    }

    @Override
    protected void prepareSheet() throws JOceanusException {
        /* Write titles */
        writeHeader(COL_NAME, EventCategory.FIELD_NAME.getName());
        writeHeader(COL_DESC, EventCategory.FIELD_DESC.getName());
    }

    @Override
    protected void formatSheet() throws JOceanusException {
        /* Set the column types */
        setStringColumn(COL_NAME);
        setStringColumn(COL_DESC);
    }

    @Override
    protected int getLastColumn() {
        /* Return the last column */
        return COL_DESC;
    }

    @Override
    protected void postProcessOnLoad() throws JOceanusException {
        /* reSort */
        theList.reSort();

        /* Validate the event tags */
        theList.validateOnLoad();
    }

    /**
     * Load the EventClasses from an archive.
     * @param pTask the task control
     * @param pWorkBook the workbook
     * @param pData the data set to load into
     * @return continue to load <code>true/false</code>
     * @throws JOceanusException on error
     */
    protected static boolean loadArchive(final TaskControl<MoneyWiseData> pTask,
                                         final DataWorkBook pWorkBook,
                                         final MoneyWiseData pData) throws JOceanusException {
        /* Access the list of tags */
        EventClassList myList = pData.getEventClasses();

        /* Protect against exceptions */
        try {
            /* Find the range of cells */
            DataView myView = pWorkBook.getRangeView(AREA_EVENTCLASSES);

            /* Access the number of reporting steps */
            int mySteps = pTask.getReportingSteps();
            int myCount = 0;

            /* Declare the new stage */
            if (!pTask.setNewStage(EventClass.LIST_NAME)) {
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

                /* Add the value into the finance tables */
                myList.addOpenItem(0, myName, null);

                /* Report the progress */
                myCount++;
                if (((myCount % mySteps) == 0) && (!pTask.setStepsDone(myCount))) {
                    return false;
                }
            }

            /* reSort */
            myList.reSort();

            /* Validate the event tags */
            myList.validateOnLoad();

            /* Handle exceptions */
        } catch (JOceanusException e) {
            throw new JMoneyWiseIOException("Failed to Load " + myList.getItemType().getListName(), e);
        }

        /* Return to caller */
        return true;
    }
}
