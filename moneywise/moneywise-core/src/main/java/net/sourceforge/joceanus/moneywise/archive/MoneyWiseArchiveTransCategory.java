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
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransCategory;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransCategory.MoneyWiseTransCategoryList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseStaticDataType;
import net.sourceforge.joceanus.moneywise.exc.MoneyWiseIOException;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataResource;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataValues;
import net.sourceforge.joceanus.prometheus.service.sheet.PrometheusSheetCell;
import net.sourceforge.joceanus.prometheus.service.sheet.PrometheusSheetRow;
import net.sourceforge.joceanus.prometheus.service.sheet.PrometheusSheetView;
import net.sourceforge.joceanus.prometheus.service.sheet.PrometheusSheetWorkBook;
import net.sourceforge.joceanus.tethys.api.thread.TethysUIThreadCancelException;
import net.sourceforge.joceanus.tethys.api.thread.TethysUIThreadStatusReport;

/**
 * ArchiveLoader for TransactionCategory.
 *
 * @author Tony Washer
 */
public class MoneyWiseArchiveTransCategory {
    /**
     * NamedArea for TransactionCategories.
     */
    private static final String AREA_TRANSCATEGORIES = "TransCategoryInfo";

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
     * Cache.
     */
    private final MoneyWiseArchiveCache theCache;

    /**
     * Constructor.
     *
     * @param pReport   the report
     * @param pWorkBook the workbook
     * @param pData     the data set to load into
     * @param pCache    the cache
     */
    MoneyWiseArchiveTransCategory(final TethysUIThreadStatusReport pReport,
                                  final PrometheusSheetWorkBook pWorkBook,
                                  final MoneyWiseDataSet pData,
                                  final MoneyWiseArchiveCache pCache) {
        theReport = pReport;
        theWorkBook = pWorkBook;
        theData = pData;
        theCache = pCache;
    }

    /**
     * Load the TransCategories from an archive.
     *
     * @param pStage the stage
     * @throws OceanusException on error
     */
    void loadArchive(final OceanusProfile pStage) throws OceanusException {
        /* Access the list of categories */
        pStage.startTask(AREA_TRANSCATEGORIES);
        final MoneyWiseTransCategoryList myList = theData.getTransCategories();

        /* Protect against exceptions */
        try {
            /* Find the range of cells */
            final PrometheusSheetView myView = theWorkBook.getRangeView(AREA_TRANSCATEGORIES);

            /* Declare the new stage */
            theReport.setNewStage(MoneyWiseTransCategory.LIST_NAME);

            /* Count the number of Categories */
            final int myTotal = myView.getRowCount();

            /* Declare the number of steps */
            theReport.setNumSteps(myTotal);

            /* Loop through the rows of the table */
            for (int i = 0; i < myTotal; i++) {
                /* Access the cell by reference */
                final PrometheusSheetRow myRow = myView.getRowByIndex(i);
                int iAdjust = -1;

                /* Access name */
                PrometheusSheetCell myCell = myView.getRowCellByIndex(myRow, ++iAdjust);
                final String myName = myCell.getString();

                /* Access Type */
                myCell = myView.getRowCellByIndex(myRow, ++iAdjust);
                final String myType = myCell.getString();

                /* Access Parent */
                String myParent = null;
                myCell = myView.getRowCellByIndex(myRow, ++iAdjust);
                if (myCell != null) {
                    myParent = myCell.getString();
                }

                /* Build data values */
                final PrometheusDataValues myValues = new PrometheusDataValues(MoneyWiseTransCategory.OBJECT_NAME);
                myValues.addValue(MoneyWiseStaticDataType.TRANSTYPE, myType);
                myValues.addValue(PrometheusDataResource.DATAGROUP_PARENT, myParent);
                myValues.addValue(PrometheusDataResource.DATAITEM_FIELD_NAME, myName);

                /* Add the value into the list */
                final MoneyWiseTransCategory myCategory = myList.addValuesItem(myValues);

                /* Declare the category */
                theCache.declareCategory(myCategory);

                /* Report the progress */
                theReport.setNextStep();
            }

            /* PostProcess on load */
            myList.postProcessOnLoad();

            /* Handle exceptions */
        } catch (TethysUIThreadCancelException e) {
            throw e;
        } catch (OceanusException e) {
            throw new MoneyWiseIOException("Failed to Load " + myList.getItemType().getListName(), e);
        }
    }
}
