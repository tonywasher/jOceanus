/*
 * MoneyWise: Finance Application
 * Copyright 2012-2026. Tony Washer
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
 */
package net.sourceforge.joceanus.moneywise.archive;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.oceanus.profile.OceanusProfile;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWisePortfolioType;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWisePortfolioType.MoneyWisePortfolioTypeList;
import net.sourceforge.joceanus.moneywise.exc.MoneyWiseIOException;
import io.github.tonywasher.joceanus.prometheus.service.sheet.PrometheusSheetCell;
import io.github.tonywasher.joceanus.prometheus.service.sheet.PrometheusSheetRow;
import io.github.tonywasher.joceanus.prometheus.service.sheet.PrometheusSheetView;
import io.github.tonywasher.joceanus.prometheus.service.sheet.PrometheusSheetWorkBook;
import net.sourceforge.joceanus.tethys.api.thread.TethysUIThreadCancelException;
import net.sourceforge.joceanus.tethys.api.thread.TethysUIThreadStatusReport;

/**
 * ArchiveLoader for PortfolioType.
 *
 * @author Tony Washer
 */
public final class MoneyWiseArchivePortfolioType {
    /**
     * NamedArea for PortfolioTypes.
     */
    private static final String AREA_PORTFOLIOTYPES = MoneyWisePortfolioType.LIST_NAME;

    /**
     * Report processor.
     */
    private final TethysUIThreadStatusReport theReport;

    /**
     * Workbook.
     */
    private final PrometheusSheetWorkBook theWorkBook;

    /**
     * DataSet.
     */
    private final MoneyWiseDataSet theData;

    /**
     * Constructor.
     *
     * @param pReport   the report
     * @param pWorkBook the workbook
     * @param pData     the data set to load into
     */
    MoneyWiseArchivePortfolioType(final TethysUIThreadStatusReport pReport,
                                  final PrometheusSheetWorkBook pWorkBook,
                                  final MoneyWiseDataSet pData) {
        theReport = pReport;
        theWorkBook = pWorkBook;
        theData = pData;
    }

    /**
     * Load the Portfolio Types from an archive.
     *
     * @param pStage the stage
     * @throws OceanusException on error
     */
    void loadArchive(final OceanusProfile pStage) throws OceanusException {
        /* Access the list of portfolio types */
        pStage.startTask(AREA_PORTFOLIOTYPES);
        final MoneyWisePortfolioTypeList myList = theData.getPortfolioTypes();

        /* Protect against exceptions */
        try {
            /* Find the range of cells */
            final PrometheusSheetView myView = theWorkBook.getRangeView(AREA_PORTFOLIOTYPES);

            /* Declare the new stage */
            theReport.setNewStage(AREA_PORTFOLIOTYPES);

            /* Count the number of PortfolioTypes */
            final int myTotal = myView.getRowCount();

            /* Declare the number of steps */
            theReport.setNumSteps(myTotal);

            /* Loop through the rows of the single column range */
            for (int i = 0; i < myTotal; i++) {
                /* Access the cell by reference */
                final PrometheusSheetRow myRow = myView.getRowByIndex(i);
                final PrometheusSheetCell myCell = myView.getRowCellByIndex(myRow, 0);

                /* Add the value into the tables */
                myList.addBasicItem(myCell.getString());

                /* Report the progress */
                theReport.setNextStep();
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
