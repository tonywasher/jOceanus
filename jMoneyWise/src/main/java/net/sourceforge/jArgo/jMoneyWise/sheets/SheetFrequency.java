/*******************************************************************************
 * JFinanceApp: Finance Application
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
import net.sourceforge.JFinanceApp.data.statics.Frequency;
import net.sourceforge.JFinanceApp.data.statics.Frequency.FrequencyList;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;

/**
 * SheetStaticData extension for Frequency.
 * @author Tony Washer
 */
public class SheetFrequency extends SheetStaticData<Frequency> {

    /**
     * NamedArea for Frequencies.
     */
    private static final String AREA_FREQUENCIES = Frequency.LIST_NAME;

    /**
     * NameList for Frequencies.
     */
    protected static final String AREA_FREQUENCYNAMES = Frequency.OBJECT_NAME + "Names";

    /**
     * Frequencies data list.
     */
    private final FrequencyList theList;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the spreadsheet reader
     */
    protected SheetFrequency(final FinanceReader pReader) {
        /* Call super-constructor */
        super(pReader, AREA_FREQUENCIES);

        /* Access the Frequency list */
        theList = pReader.getData().getFrequencys();
        setDataList(theList);
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected SheetFrequency(final FinanceWriter pWriter) {
        /* Call super-constructor */
        super(pWriter, AREA_FREQUENCIES, AREA_FREQUENCYNAMES);

        /* Access the Frequency list */
        theList = pWriter.getData().getFrequencys();
        setDataList(theList);
    }

    /**
     * Load encrypted.
     * @param pId the id
     * @param pControlId the controlId
     * @param isEnabled isEnabled
     * @param iOrder the sort order
     * @param pName name
     * @param pDesc description
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
     * Load the Frequencies from an archive.
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
            AreaReference myRange = pHelper.resolveAreaReference(AREA_FREQUENCIES);

            /* Declare the new stage */
            if (!pTask.setNewStage(AREA_FREQUENCIES)) {
                return false;
            }

            /* Access the number of reporting steps */
            int mySteps = pTask.getReportingSteps();
            int myCount = 0;

            /* If we found the range OK */
            if (myRange != null) {
                /* Access the relevant sheet and Cell references */
                CellReference myTop = myRange.getFirstCell();
                CellReference myBottom = myRange.getLastCell();
                Sheet mySheet = pHelper.getSheetByName(myTop.getSheetName());
                int myCol = myTop.getCol();

                /* Count the number of frequencies */
                int myTotal = myBottom.getRow() - myTop.getRow() + 1;

                /* Access the list of frequencies */
                FrequencyList myList = pData.getFrequencys();

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
                    myList.addBasicItem(myCell.getStringCellValue());

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
            throw new JDataException(ExceptionClass.EXCEL, "Failed to load Frequencies", e);
        }

        /* Return to caller */
        return true;
    }
}
