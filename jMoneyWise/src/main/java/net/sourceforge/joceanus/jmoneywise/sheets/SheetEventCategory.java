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
import net.sourceforge.joceanus.jdatamodels.sheets.SheetDataItem;
import net.sourceforge.joceanus.jmoneywise.data.EventCategory;
import net.sourceforge.joceanus.jmoneywise.data.EventCategory.EventCategoryList;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jspreadsheetmanager.DataCell;
import net.sourceforge.joceanus.jspreadsheetmanager.DataRow;
import net.sourceforge.joceanus.jspreadsheetmanager.DataView;
import net.sourceforge.joceanus.jspreadsheetmanager.DataWorkBook;

/**
 * SheetDataItem extension for EventCategory.
 * @author Tony Washer
 */
public class SheetEventCategory
        extends SheetDataItem<EventCategory> {
    /**
     * NamedArea for Rates.
     */
    private static final String AREA_EVTCATEGORIES = "EventCategoryInfo";

    /**
     * NameList for EventCategories.
     */
    protected static final String AREA_EVTCATNAMES = "EventCategoryNames";

    /**
     * Name column.
     */
    private static final int COL_NAME = COL_CONTROLID + 1;

    /**
     * Type column.
     */
    private static final int COL_TYPE = COL_NAME + 1;

    /**
     * Parent column.
     */
    private static final int COL_PARENT = COL_TYPE + 1;

    /**
     * Description column.
     */
    private static final int COL_DESC = COL_PARENT + 1;

    /**
     * Category data list.
     */
    private final EventCategoryList theList;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the spreadsheet reader
     */
    protected SheetEventCategory(final MoneyWiseReader pReader) {
        /* Call super constructor */
        super(pReader, AREA_EVTCATEGORIES);

        /* Access the Rates list */
        theList = pReader.getData().getEventCategories();
        setDataList(theList);
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected SheetEventCategory(final MoneyWiseWriter pWriter) {
        /* Call super constructor */
        super(pWriter, AREA_EVTCATEGORIES);

        /* Access the Rates list */
        theList = pWriter.getData().getEventCategories();
        setDataList(theList);
    }

    @Override
    protected void loadSecureItem(final Integer pId) throws JDataException {
        /* Access the IDs */
        Integer myControlId = loadInteger(COL_CONTROLID);
        Integer myCatId = loadInteger(COL_TYPE);
        Integer myParentId = loadInteger(COL_PARENT);

        /* Access the Name and description */
        byte[] myNameBytes = loadBytes(COL_NAME);
        byte[] myDescBytes = loadBytes(COL_DESC);

        /* Load the item */
        theList.addSecureItem(pId, myControlId, myNameBytes, myDescBytes, myCatId, myParentId);
    }

    @Override
    protected void loadOpenItem(final Integer pId) throws JDataException {
        /* Access the name */
        String myType = loadString(COL_TYPE);
        String myParent = loadString(COL_PARENT);

        /* Access the name and description bytes */
        String myName = loadString(COL_NAME);
        String myDesc = loadString(COL_DESC);

        /* Load the item */
        theList.addOpenItem(pId, myName, myDesc, myType, myParent);
    }

    @Override
    protected void insertSecureItem(final EventCategory pItem) throws JDataException {
        /* Set the fields */
        writeInteger(COL_CONTROLID, pItem.getControlKeyId());
        writeInteger(COL_TYPE, pItem.getCategoryTypeId());
        writeInteger(COL_PARENT, pItem.getParentCategoryId());
        writeBytes(COL_NAME, pItem.getNameBytes());
        writeBytes(COL_DESC, pItem.getDescBytes());
    }

    @Override
    protected void insertOpenItem(final EventCategory pItem) throws JDataException {
        /* Set the fields */
        writeString(COL_TYPE, pItem.getCategoryTypeName());
        writeString(COL_PARENT, pItem.getParentCategoryName());
        writeString(COL_NAME, pItem.getName());
        writeString(COL_DESC, pItem.getDesc());
    }

    @Override
    protected void prepareSheet() throws JDataException {
        /* Write titles */
        writeHeader(COL_TYPE, EventCategory.FIELD_CATTYPE.getName());
        writeHeader(COL_PARENT, EventCategory.FIELD_PARENT.getName());
        writeHeader(COL_NAME, EventCategory.FIELD_NAME.getName());
        writeHeader(COL_DESC, EventCategory.FIELD_DESC.getName());
    }

    @Override
    protected void formatSheet() throws JDataException {
        /* Set the column types */
        setStringColumn(COL_NAME);
        setStringColumn(COL_DESC);
        setStringColumn(COL_TYPE);
        setStringColumn(COL_PARENT);

        /* Set the name column range */
        nameColumnRange(COL_NAME, AREA_EVTCATNAMES);

        /* Set validation */
        applyDataValidation(COL_TYPE, SheetEventCategoryType.AREA_CATTYPENAMES);
        applyDataValidation(COL_PARENT, AREA_EVTCATNAMES);
    }

    @Override
    protected int getLastColumn() {
        /* Return the last column */
        return COL_DESC;
    }

    @Override
    protected void postProcessOnLoad() throws JDataException {
        /* Resolve links and reSort */
        theList.resolveDataSetLinks();
        theList.reSort();

        /* Touch underlying items */
        theList.touchUnderlyingItems();

        /* Validate the event categories */
        theList.validateOnLoad();
    }

    /**
     * Load the EventCategories from an archive.
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
            DataView myView = pWorkBook.getRangeView(AREA_EVTCATEGORIES);

            /* Access the number of reporting steps */
            int mySteps = pTask.getReportingSteps();
            int myCount = 0;

            /* Declare the new stage */
            if (!pTask.setNewStage(EventCategory.LIST_NAME)) {
                return false;
            }

            /* Count the number of Categories */
            int myTotal = myView.getRowCount();

            /* Access the list of categories */
            EventCategoryList myList = pData.getEventCategories();

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

                /* Access Type */
                myCell = myView.getRowCellByIndex(myRow, iAdjust++);
                String myType = myCell.getStringValue();

                /* Access Parent */
                String myParent = null;
                myCell = myView.getRowCellByIndex(myRow, iAdjust++);
                if (myCell != null) {
                    myParent = myCell.getStringValue();
                }

                /* Add the value into the finance tables */
                myList.addOpenItem(0, myName, null, myType, myParent);

                /* Report the progress */
                myCount++;
                if (((myCount % mySteps) == 0)
                    && (!pTask.setStepsDone(myCount))) {
                    return false;
                }
            }

            /* Resolve links and reSort */
            myList.resolveDataSetLinks();
            myList.reSort();

            /* Touch underlying items */
            myList.touchUnderlyingItems();

            /* Validate the event categories */
            myList.validateOnLoad();

            /* Handle exceptions */
        } catch (JDataException e) {
            throw new JDataException(ExceptionClass.EXCEL, "Failed to Load EventCategories", e);
        }

        /* Return to caller */
        return true;
    }
}
