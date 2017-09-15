/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.lethe.sheets;

import net.sourceforge.joceanus.jmetis.atlas.threads.MetisThreadCancelException;
import net.sourceforge.joceanus.jmetis.atlas.threads.MetisThreadStatusReport;
import net.sourceforge.joceanus.jmetis.sheet.MetisDataCell;
import net.sourceforge.joceanus.jmetis.sheet.MetisDataRow;
import net.sourceforge.joceanus.jmetis.sheet.MetisDataView;
import net.sourceforge.joceanus.jmetis.sheet.MetisDataWorkBook;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseIOException;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.Frequency;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.Frequency.FrequencyList;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues;
import net.sourceforge.joceanus.jprometheus.lethe.sheets.PrometheusSheetStaticData;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * SheetStaticData extension for Frequency.
 * @author Tony Washer
 */
public class SheetFrequency
        extends PrometheusSheetStaticData<Frequency, MoneyWiseDataType> {
    /**
     * NamedArea for Frequencies.
     */
    private static final String AREA_FREQUENCIES = Frequency.LIST_NAME;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the spreadsheet reader
     */
    protected SheetFrequency(final MoneyWiseReader pReader) {
        /* Call super-constructor */
        super(pReader, AREA_FREQUENCIES);

        /* Access the Frequency list */
        final MoneyWiseData myData = pReader.getData();
        setDataList(myData.getFrequencys());
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected SheetFrequency(final MoneyWiseWriter pWriter) {
        /* Call super-constructor */
        super(pWriter, AREA_FREQUENCIES);

        /* Access the Frequency list */
        final MoneyWiseData myData = pWriter.getData();
        setDataList(myData.getFrequencys());
    }

    @Override
    protected DataValues<MoneyWiseDataType> loadSecureValues() throws OceanusException {
        /* Build data values */
        return getRowValues(Frequency.OBJECT_NAME);
    }

    /**
     * Load the Frequencies from an archive.
     * @param pReport the report
     * @param pWorkBook the workbook
     * @param pData the data set to load into
     * @throws OceanusException on error
     */
    protected static void loadArchive(final MetisThreadStatusReport pReport,
                                      final MetisDataWorkBook pWorkBook,
                                      final MoneyWiseData pData) throws OceanusException {
        /* Access the list of frequencies */
        final FrequencyList myList = pData.getFrequencys();

        /* Protect against exceptions */
        try {
            /* Find the range of cells */
            final MetisDataView myView = pWorkBook.getRangeView(AREA_FREQUENCIES);

            /* Declare the new stage */
            pReport.setNewStage(AREA_FREQUENCIES);

            /* Count the number of Frequencies */
            final int myTotal = myView.getRowCount();

            /* Declare the number of steps */
            pReport.setNumSteps(myTotal);

            /* Loop through the rows of the single column range */
            for (int i = 0; i < myTotal; i++) {
                /* Access the cell by reference */
                final MetisDataRow myRow = myView.getRowByIndex(i);
                final MetisDataCell myCell = myView.getRowCellByIndex(myRow, 0);

                /* Add the value into the tables */
                myList.addBasicItem(myCell.getStringValue());

                /* Report the progress */
                pReport.setNextStep();
            }

            /* PostProcess the list */
            myList.postProcessOnLoad();

            /* Handle Exceptions */
        } catch (MetisThreadCancelException e) {
            throw e;
        } catch (OceanusException e) {
            throw new MoneyWiseIOException("Failed to load " + myList.getItemType().getListName(), e);
        }
    }
}
