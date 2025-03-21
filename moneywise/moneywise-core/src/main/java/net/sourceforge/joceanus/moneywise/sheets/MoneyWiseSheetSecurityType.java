/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2025 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.sourceforge.joceanus.moneywise.sheets;

import net.sourceforge.joceanus.moneywise.exc.MoneyWiseIOException;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseSecurityType;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseSecurityType.MoneyWiseSecurityTypeList;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataValues;
import net.sourceforge.joceanus.prometheus.sheets.PrometheusSheetStaticData;
import net.sourceforge.joceanus.prometheus.service.sheet.PrometheusSheetCell;
import net.sourceforge.joceanus.prometheus.service.sheet.PrometheusSheetRow;
import net.sourceforge.joceanus.prometheus.service.sheet.PrometheusSheetView;
import net.sourceforge.joceanus.prometheus.service.sheet.PrometheusSheetWorkBook;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.tethys.api.thread.TethysUIThreadCancelException;
import net.sourceforge.joceanus.tethys.api.thread.TethysUIThreadStatusReport;

/**
 * SheetStaticData extension for SecurityType.
 * @author Tony Washer
 */
public class MoneyWiseSheetSecurityType
        extends PrometheusSheetStaticData<MoneyWiseSecurityType> {
    /**
     * NamedArea for SecurityTypes.
     */
    private static final String AREA_SECURITYTYPES = MoneyWiseSecurityType.LIST_NAME;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the spreadsheet reader
     */
    protected MoneyWiseSheetSecurityType(final MoneyWiseReader pReader) {
        /* Call super-constructor */
        super(pReader, AREA_SECURITYTYPES);

        /* Access the Security Type list */
        final MoneyWiseDataSet myData = (MoneyWiseDataSet) pReader.getData();
        setDataList(myData.getSecurityTypes());
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected MoneyWiseSheetSecurityType(final MoneyWiseWriter pWriter) {
        /* Call super-constructor */
        super(pWriter, AREA_SECURITYTYPES);

        /* Access the Security Type list */
        final MoneyWiseDataSet myData = (MoneyWiseDataSet) pWriter.getData();
        setDataList(myData.getSecurityTypes());
    }

    @Override
    protected PrometheusDataValues loadSecureValues() throws OceanusException {
        /* Build data values */
        return getRowValues(MoneyWiseSecurityType.OBJECT_NAME);
    }

    /**
     * Load the Security Types from an archive.
     * @param pReport the report
     * @param pWorkBook the workbook
     * @param pData the data set to load into
     * @throws OceanusException on error
     */
    protected static void loadArchive(final TethysUIThreadStatusReport pReport,
                                      final PrometheusSheetWorkBook pWorkBook,
                                      final MoneyWiseDataSet pData) throws OceanusException {
        /* Access the list of security types */
        final MoneyWiseSecurityTypeList myList = pData.getSecurityTypes();

        /* Protect against exceptions */
        try {
            /* Find the range of cells */
            final PrometheusSheetView myView = pWorkBook.getRangeView(AREA_SECURITYTYPES);

            /* Declare the new stage */
            pReport.setNewStage(AREA_SECURITYTYPES);

            /* Count the number of SecurityTypes */
            final int myTotal = myView.getRowCount();

            /* Declare the number of steps */
            pReport.setNumSteps(myTotal);

            /* Loop through the rows of the single column range */
            for (int i = 0; i < myTotal; i++) {
                /* Access the cell by reference */
                final PrometheusSheetRow myRow = myView.getRowByIndex(i);
                final PrometheusSheetCell myCell = myView.getRowCellByIndex(myRow, 0);

                /* Add the value into the tables */
                myList.addBasicItem(myCell.getString());

                /* Report the progress */
                pReport.setNextStep();
            }

            /* PostProcess the list */
            myList.postProcessOnLoad();

            /* Handle exceptions */
        } catch (TethysUIThreadCancelException e) {
            throw e;
        } catch (OceanusException e) {
            throw new MoneyWiseIOException("Failed to Load " + myList.getItemType().getListName(), e);
        }
    }
}
