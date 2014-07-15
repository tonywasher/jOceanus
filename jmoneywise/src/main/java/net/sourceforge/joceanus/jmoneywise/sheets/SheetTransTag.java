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
import net.sourceforge.joceanus.jmoneywise.data.TransactionTag;
import net.sourceforge.joceanus.jmoneywise.data.TransactionTag.TransactionTagList;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.data.TaskControl;
import net.sourceforge.joceanus.jprometheus.sheets.SheetEncrypted;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * SheetDataItem extension for TransactionTag.
 * @author Tony Washer
 */
public class SheetTransTag
        extends SheetEncrypted<TransactionTag, MoneyWiseDataType> {
    /**
     * NamedArea for TransactionTags.
     */
    private static final String AREA_TRANSTAGS = TransactionTag.LIST_NAME;

    /**
     * Name column.
     */
    private static final int COL_NAME = COL_KEYSETID + 1;

    /**
     * Description column.
     */
    private static final int COL_DESC = COL_NAME + 1;

    /**
     * Class data list.
     */
    private final TransactionTagList theList;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the spreadsheet reader
     */
    protected SheetTransTag(final MoneyWiseReader pReader) {
        /* Call super constructor */
        super(pReader, AREA_TRANSTAGS);

        /* Access the Class list */
        theList = pReader.getData().getTransactionTags();
        setDataList(theList);
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected SheetTransTag(final MoneyWiseWriter pWriter) {
        /* Call super constructor */
        super(pWriter, AREA_TRANSTAGS);

        /* Access the Class list */
        theList = pWriter.getData().getTransactionTags();
        setDataList(theList);
    }

    @Override
    protected DataValues<MoneyWiseDataType> loadSecureValues() throws JOceanusException {
        /* Build data values */
        DataValues<MoneyWiseDataType> myValues = getRowValues(TransactionTag.OBJECT_NAME);
        myValues.addValue(TransactionTag.FIELD_NAME, loadBytes(COL_NAME));
        myValues.addValue(TransactionTag.FIELD_DESC, loadBytes(COL_DESC));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void insertSecureItem(final TransactionTag pItem) throws JOceanusException {
        /* Set the fields */
        super.insertSecureItem(pItem);
        writeBytes(COL_NAME, pItem.getNameBytes());
        writeBytes(COL_DESC, pItem.getDescBytes());
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

        /* Validate the tags */
        theList.validateOnLoad();
    }

    /**
     * Load the TransactionTags from an archive.
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
        TransactionTagList myList = pData.getTransactionTags();

        /* Protect against exceptions */
        try {
            /* Find the range of cells */
            DataView myView = pWorkBook.getRangeView(AREA_TRANSTAGS);

            /* Access the number of reporting steps */
            int mySteps = pTask.getReportingSteps();
            int myCount = 0;

            /* Declare the new stage */
            if (!pTask.setNewStage(TransactionTag.LIST_NAME)) {
                return false;
            }

            /* Count the number of tags */
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

                /* Build data values */
                DataValues<MoneyWiseDataType> myValues = new DataValues<MoneyWiseDataType>(TransactionTag.OBJECT_NAME);
                myValues.addValue(TransactionTag.FIELD_NAME, myName);

                /* Add the value into the list */
                myList.addValuesItem(myValues);

                /* Report the progress */
                myCount++;
                if (((myCount % mySteps) == 0) && (!pTask.setStepsDone(myCount))) {
                    return false;
                }
            }

            /* reSort */
            myList.reSort();

            /* Validate the tags */
            myList.validateOnLoad();

            /* Handle exceptions */
        } catch (JOceanusException e) {
            throw new JMoneyWiseIOException("Failed to Load " + myList.getItemType().getListName(), e);
        }

        /* Return to caller */
        return true;
    }
}