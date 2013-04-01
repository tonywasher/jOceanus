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
import net.sourceforge.jOceanus.jDataModels.sheets.SheetDataItem;
import net.sourceforge.jOceanus.jMoneyWise.data.AccountCategory;
import net.sourceforge.jOceanus.jMoneyWise.data.AccountCategory.AccountCategoryList;
import net.sourceforge.jOceanus.jMoneyWise.data.FinanceData;
import net.sourceforge.jOceanus.jSpreadSheetManager.DataCell;
import net.sourceforge.jOceanus.jSpreadSheetManager.DataRow;
import net.sourceforge.jOceanus.jSpreadSheetManager.DataView;
import net.sourceforge.jOceanus.jSpreadSheetManager.DataWorkBook;

/**
 * SheetDataItem extension for AccountCategory.
 * @author Tony Washer
 */
public class SheetAccountCategory
        extends SheetDataItem<AccountCategory> {
    /**
     * NamedArea for Categories.
     */
    protected static final String AREA_ACTCATEGORIES = AccountCategory.LIST_NAME;

    /**
     * NameList for AccountCategories.
     */
    protected static final String AREA_ACTCATNAMES = AccountCategory.OBJECT_NAME
                                                     + "Names";

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
    private final AccountCategoryList theList;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the spreadsheet reader
     */
    protected SheetAccountCategory(final FinanceReader pReader) {
        /* Call super constructor */
        super(pReader, AREA_ACTCATEGORIES);

        /* Access the Rates list */
        theList = pReader.getData().getAccountCategories();
        setDataList(theList);
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected SheetAccountCategory(final FinanceWriter pWriter) {
        /* Call super constructor */
        super(pWriter, AREA_ACTCATEGORIES);

        /* Access the Rates list */
        theList = pWriter.getData().getAccountCategories();
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
        /* Access the links */
        String myType = loadString(COL_TYPE);
        String myParent = loadString(COL_PARENT);

        /* Access the name and description bytes */
        String myName = loadString(COL_NAME);
        String myDesc = loadString(COL_DESC);

        /* Load the item */
        theList.addOpenItem(pId, myName, myDesc, myType, myParent);
    }

    @Override
    protected void insertSecureItem(final AccountCategory pItem) throws JDataException {
        /* Set the fields */
        writeInteger(COL_CONTROLID, pItem.getControlKeyId());
        writeInteger(COL_TYPE, pItem.getCategoryTypeId());
        writeInteger(COL_PARENT, pItem.getParentCategoryId());
        writeBytes(COL_NAME, pItem.getNameBytes());
        writeBytes(COL_DESC, pItem.getDescBytes());
    }

    @Override
    protected void insertOpenItem(final AccountCategory pItem) throws JDataException {
        /* Set the fields */
        writeString(COL_TYPE, pItem.getCategoryTypeName());
        writeString(COL_PARENT, pItem.getParentCategoryName());
        writeString(COL_NAME, pItem.getName());
        writeString(COL_DESC, pItem.getDesc());
    }

    @Override
    protected void prepareSheet() throws JDataException {
        /* Write titles */
        writeHeader(COL_TYPE, AccountCategory.FIELD_CATTYPE.getName());
        writeHeader(COL_PARENT, AccountCategory.FIELD_PARENT.getName());
        writeHeader(COL_NAME, AccountCategory.FIELD_NAME.getName());
        writeHeader(COL_DESC, AccountCategory.FIELD_DESC.getName());
    }

    @Override
    protected void formatSheet() throws JDataException {
        /* Set the column types */
        setStringColumn(COL_NAME);
        setStringColumn(COL_DESC);
        setStringColumn(COL_TYPE);
        setStringColumn(COL_PARENT);

        /* Set the name column range */
        nameColumnRange(COL_NAME, AREA_ACTCATNAMES);

        /* Set validation */
        applyDataValidation(COL_TYPE, SheetAccountCategoryType.AREA_ACCOUNTCATTYPENAMES);
        applyDataValidation(COL_PARENT, AREA_ACTCATNAMES);
    }

    @Override
    protected int getLastColumn() {
        /* Return the last column */
        return COL_DESC;
    }

    /**
     * Load the AccountCategories from an archive.
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
            DataView myView = pWorkBook.getRangeView(AREA_ACTCATEGORIES);

            /* Access the number of reporting steps */
            int mySteps = pTask.getReportingSteps();
            int myCount = 0;

            /* Declare the new stage */
            if (!pTask.setNewStage(AREA_ACTCATEGORIES)) {
                return false;
            }

            /* Count the number of Categories */
            int myTotal = myView.getRowCount();

            /* Access the list of categories */
            AccountCategoryList myList = pData.getAccountCategories();

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
                myCell = myView.getRowCellByIndex(myRow, iAdjust++);
                String myParent = myCell.getStringValue();

                /* Add the value into the finance tables */
                myList.addOpenItem(0, myName, null, myType, myParent);

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
            throw new JDataException(ExceptionClass.EXCEL, "Failed to Load AccountCategories", e);
        }

        /* Return to caller */
        return true;
    }
}
