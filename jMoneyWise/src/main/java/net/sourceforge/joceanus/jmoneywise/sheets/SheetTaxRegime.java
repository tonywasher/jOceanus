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
import net.sourceforge.joceanus.jdatamodels.sheets.SheetStaticData;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxRegime;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxRegime.TaxRegimeList;
import net.sourceforge.joceanus.jspreadsheetmanager.DataCell;
import net.sourceforge.joceanus.jspreadsheetmanager.DataRow;
import net.sourceforge.joceanus.jspreadsheetmanager.DataView;
import net.sourceforge.joceanus.jspreadsheetmanager.DataWorkBook;

/**
 * SheetStaticData extension for TaxRegime.
 * @author Tony Washer
 */
public class SheetTaxRegime
        extends SheetStaticData<TaxRegime> {

    /**
     * NamedArea for Tax Regimes.
     */
    private static final String AREA_TAXREGIMES = TaxRegime.LIST_NAME;

    /**
     * NameList for Tax Regime Names.
     */
    protected static final String AREA_TAXREGIMENAMES = TaxRegime.OBJECT_NAME
                                                        + "Names";

    /**
     * TaxRegime data list.
     */
    private final TaxRegimeList theList;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the spreadsheet reader
     */
    protected SheetTaxRegime(final MoneyWiseReader pReader) {
        /* Call super-constructor */
        super(pReader, AREA_TAXREGIMES);

        /* Access the TaxRegime list */
        theList = pReader.getData().getTaxRegimes();
        setDataList(theList);
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected SheetTaxRegime(final MoneyWiseWriter pWriter) {
        /* Call super-constructor */
        super(pWriter, AREA_TAXREGIMES, AREA_TAXREGIMENAMES);

        /* Access the TaxRegime list */
        theList = pWriter.getData().getTaxRegimes();
        setDataList(theList);
    }

    /**
     * Load encrypted.
     * @param pId the id
     * @param pControlId the Control Id
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
     * Load the Tax Regimes from an archive.
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
            DataView myView = pWorkBook.getRangeView(AREA_TAXREGIMES);

            /* Access the number of reporting steps */
            int mySteps = pTask.getReportingSteps();
            int myCount = 0;

            /* Declare the new stage */
            if (!pTask.setNewStage(AREA_TAXREGIMES)) {
                return false;
            }

            /* Count the number of TaxRegimes */
            int myTotal = myView.getRowCount();

            /* Access the list of tax regimes */
            TaxRegimeList myList = pData.getTaxRegimes();

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
        } catch (JDataException e) {
            throw new JDataException(ExceptionClass.EXCEL, "Failed to load Tax Regimes", e);
        }

        /* Return to caller */
        return true;
    }
}
