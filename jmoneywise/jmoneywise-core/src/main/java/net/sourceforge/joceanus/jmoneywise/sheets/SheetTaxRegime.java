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

import net.sourceforge.joceanus.jmetis.sheet.MetisDataCell;
import net.sourceforge.joceanus.jmetis.sheet.MetisDataRow;
import net.sourceforge.joceanus.jmetis.sheet.MetisDataView;
import net.sourceforge.joceanus.jmetis.sheet.MetisDataWorkBook;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseIOException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxRegime;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxRegime.TaxRegimeList;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.data.TaskControl;
import net.sourceforge.joceanus.jprometheus.sheets.SheetStaticData;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * SheetStaticData extension for TaxRegime.
 * @author Tony Washer
 */
public class SheetTaxRegime
        extends SheetStaticData<TaxRegime, MoneyWiseDataType> {
    /**
     * NamedArea for Tax Regimes.
     */
    private static final String AREA_TAXREGIMES = TaxRegime.LIST_NAME;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the spreadsheet reader
     */
    protected SheetTaxRegime(final MoneyWiseReader pReader) {
        /* Call super-constructor */
        super(pReader, AREA_TAXREGIMES);

        /* Access the TaxRegime list */
        MoneyWiseData myData = pReader.getData();
        setDataList(myData.getTaxRegimes());
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected SheetTaxRegime(final MoneyWiseWriter pWriter) {
        /* Call super-constructor */
        super(pWriter, AREA_TAXREGIMES);

        /* Access the TaxRegime list */
        MoneyWiseData myData = pWriter.getData();
        setDataList(myData.getTaxRegimes());
    }

    @Override
    protected DataValues<MoneyWiseDataType> loadSecureValues() throws OceanusException {
        /* Build data values */
        return getRowValues(TaxRegime.OBJECT_NAME);
    }

    /**
     * Load the Tax Regimes from an archive.
     * @param pTask the task control
     * @param pWorkBook the workbook
     * @param pData the data set to load into
     * @return continue to load <code>true/false</code>
     * @throws OceanusException on error
     */
    protected static boolean loadArchive(final TaskControl<MoneyWiseData> pTask,
                                         final MetisDataWorkBook pWorkBook,
                                         final MoneyWiseData pData) throws OceanusException {
        /* Access the list of tax regimes */
        TaxRegimeList myList = pData.getTaxRegimes();

        /* Protect against exceptions */
        try {
            /* Find the range of cells */
            MetisDataView myView = pWorkBook.getRangeView(AREA_TAXREGIMES);

            /* Access the number of reporting steps */
            int mySteps = pTask.getReportingSteps();
            int myCount = 0;

            /* Declare the new stage */
            if (!pTask.setNewStage(AREA_TAXREGIMES)) {
                return false;
            }

            /* Count the number of TaxRegimes */
            int myTotal = myView.getRowCount();

            /* Declare the number of steps */
            if (!pTask.setNumSteps(myTotal)) {
                return false;
            }

            /* Loop through the rows of the single column range */
            for (int i = 0; i < myTotal; i++) {
                /* Access the cell by reference */
                MetisDataRow myRow = myView.getRowByIndex(i);
                MetisDataCell myCell = myView.getRowCellByIndex(myRow, 0);

                /* Add the value into the tables */
                myList.addBasicItem(myCell.getStringValue());

                /* Report the progress */
                myCount++;
                if (((myCount % mySteps) == 0) && (!pTask.setStepsDone(myCount))) {
                    return false;
                }
            }

            /* PostProcess the list */
            myList.postProcessOnLoad();

            /* Handle Exceptions */
        } catch (OceanusException e) {
            throw new JMoneyWiseIOException("Failed to load " + myList.getItemType().getListName(), e);
        }

        /* Return to caller */
        return true;
    }
}
