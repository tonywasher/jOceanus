/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2016 Tony Washer
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
import net.sourceforge.joceanus.jmetis.threads.MetisThreadCancelException;
import net.sourceforge.joceanus.jmetis.threads.MetisThreadStatusReport;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseIOException;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxCategory;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxCategory.TaxCategoryList;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.sheets.PrometheusSheetStaticData;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * SheetStaticData extension for TaxCategory.
 * @author Tony Washer
 */
public class SheetTaxCategory
        extends PrometheusSheetStaticData<TaxCategory, MoneyWiseDataType> {
    /**
     * NamedArea for Tax Types.
     */
    private static final String AREA_TAXCATEGORIES = TaxCategory.LIST_NAME;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the spreadsheet reader
     */
    protected SheetTaxCategory(final MoneyWiseReader pReader) {
        /* Call super-constructor */
        super(pReader, AREA_TAXCATEGORIES);

        /* Access the Tax Type list */
        MoneyWiseData myData = pReader.getData();
        setDataList(myData.getTaxCategories());
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected SheetTaxCategory(final MoneyWiseWriter pWriter) {
        /* Call super-constructor */
        super(pWriter, AREA_TAXCATEGORIES);

        /* Access the Tax Type list */
        MoneyWiseData myData = pWriter.getData();
        setDataList(myData.getTaxCategories());
    }

    @Override
    protected DataValues<MoneyWiseDataType> loadSecureValues() throws OceanusException {
        /* Build data values */
        return getRowValues(TaxCategory.OBJECT_NAME);
    }

    /**
     * Load the Tax Types from an archive.
     * @param pReport the report
     * @param pWorkBook the workbook
     * @param pData the data set to load into
     * @throws OceanusException on error
     */
    protected static void loadArchive(final MetisThreadStatusReport pReport,
                                      final MetisDataWorkBook pWorkBook,
                                      final MoneyWiseData pData) throws OceanusException {
        /* Access the list of tax categories */
        TaxCategoryList myList = pData.getTaxCategories();

        /* Protect against exceptions */
        try {
            /* Find the range of cells */
            MetisDataView myView = pWorkBook.getRangeView(AREA_TAXCATEGORIES);

            /* Declare the new stage */
            pReport.setNewStage(AREA_TAXCATEGORIES);

            /* Count the number of TaxCategories */
            int myTotal = myView.getRowCount();

            /* Declare the number of steps */
            pReport.setNumSteps(myTotal);

            /* Loop through the rows of the single column range */
            for (int i = 0; i < myTotal; i++) {
                /* Access the cell by reference */
                MetisDataRow myRow = myView.getRowByIndex(i);
                MetisDataCell myCell = myView.getRowCellByIndex(myRow, 0);

                /* Add the value into the tables */
                myList.addBasicItem(myCell.getStringValue());

                /* Report the progress */
                pReport.setNextStep();
            }

            /* PostProcess the list */
            myList.postProcessOnLoad();

            /* Handle exceptions */
        } catch (MetisThreadCancelException e) {
            throw e;
        } catch (OceanusException e) {
            throw new MoneyWiseIOException("Failed to Load " + myList.getItemType().getListName(), e);
        }
    }
}
