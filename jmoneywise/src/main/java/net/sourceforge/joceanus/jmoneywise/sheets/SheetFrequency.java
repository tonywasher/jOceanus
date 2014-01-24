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

import net.sourceforge.joceanus.jprometheus.data.TaskControl;
import net.sourceforge.joceanus.jprometheus.sheets.SheetStaticData;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseIOException;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.statics.Frequency;
import net.sourceforge.joceanus.jmoneywise.data.statics.Frequency.FrequencyList;
import net.sourceforge.joceanus.jmetis.sheet.DataCell;
import net.sourceforge.joceanus.jmetis.sheet.DataRow;
import net.sourceforge.joceanus.jmetis.sheet.DataView;
import net.sourceforge.joceanus.jmetis.sheet.DataWorkBook;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * SheetStaticData extension for Frequency.
 * @author Tony Washer
 */
public class SheetFrequency
        extends SheetStaticData<Frequency> {

    /**
     * NamedArea for Frequencies.
     */
    private static final String AREA_FREQUENCIES = Frequency.LIST_NAME;

    /**
     * NameList for Frequencies.
     */
    protected static final String AREA_FREQUENCYNAMES = Frequency.OBJECT_NAME
                                                        + "Names";

    /**
     * Frequencies data list.
     */
    private final FrequencyList theList;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the spreadsheet reader
     */
    protected SheetFrequency(final MoneyWiseReader pReader) {
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
    protected SheetFrequency(final MoneyWiseWriter pWriter) {
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
     * @throws JOceanusException on error
     */
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

    /**
     * Load clear text.
     * @param pId the id
     * @param isEnabled isEnabled
     * @param iOrder the sort order
     * @param pName the name
     * @param pDesc the description
     * @throws JOceanusException on error
     */
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
     * Load the Frequencies from an archive.
     * @param pTask the task control
     * @param pWorkBook the workbook
     * @param pData the data set to load into
     * @return continue to load <code>true/false</code>
     * @throws JOceanusException on error
     */
    protected static boolean loadArchive(final TaskControl<MoneyWiseData> pTask,
                                         final DataWorkBook pWorkBook,
                                         final MoneyWiseData pData) throws JOceanusException {
        /* Protect against exceptions */
        try {
            /* Find the range of cells */
            DataView myView = pWorkBook.getRangeView(AREA_FREQUENCIES);

            /* Declare the new stage */
            if (!pTask.setNewStage(AREA_FREQUENCIES)) {
                return false;
            }

            /* Access the number of reporting steps */
            int mySteps = pTask.getReportingSteps();
            int myCount = 0;

            /* Count the number of Frequencies */
            int myTotal = myView.getRowCount();

            /* Access the list of frequencies */
            FrequencyList myList = pData.getFrequencys();

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
        } catch (JOceanusException e) {
            throw new JMoneyWiseIOException("Failed to load Frequencies", e);
        }

        /* Return to caller */
        return true;
    }
}