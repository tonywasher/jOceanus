/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2019 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.lethe.sheets;

import net.sourceforge.joceanus.jmetis.threads.MetisThreadCancelException;
import net.sourceforge.joceanus.jmetis.threads.MetisThreadStatusReport;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseIOException;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionCategory;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionCategory.TransactionCategoryList;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues;
import net.sourceforge.joceanus.jprometheus.lethe.sheets.PrometheusSheetEncrypted;
import net.sourceforge.joceanus.jprometheus.service.sheet.PrometheusSheetCell;
import net.sourceforge.joceanus.jprometheus.service.sheet.PrometheusSheetRow;
import net.sourceforge.joceanus.jprometheus.service.sheet.PrometheusSheetView;
import net.sourceforge.joceanus.jprometheus.service.sheet.PrometheusSheetWorkBook;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * SheetDataItem extension for TransactionCategory.
 * @author Tony Washer
 */
public class SheetTransCategory
        extends PrometheusSheetEncrypted<TransactionCategory, MoneyWiseDataType> {
    /**
     * NamedArea for TransactionCategories.
     */
    private static final String AREA_TRANSCATEGORIES = "TransCategoryInfo";

    /**
     * Name column.
     */
    private static final int COL_NAME = COL_KEYSETID + 1;

    /**
     * Type column.
     */
    private static final int COL_TYPE = COL_NAME + 1;

    /**
     * Parent column.
     */
    private static final int COL_PARENT = COL_TYPE + 1;

    /**
     * Description column.
     */
    private static final int COL_DESC = COL_PARENT + 1;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the spreadsheet reader
     */
    protected SheetTransCategory(final MoneyWiseReader pReader) {
        /* Call super constructor */
        super(pReader, AREA_TRANSCATEGORIES);

        /* Access the Categories list */
        final MoneyWiseData myData = pReader.getData();
        setDataList(myData.getTransCategories());
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected SheetTransCategory(final MoneyWiseWriter pWriter) {
        /* Call super constructor */
        super(pWriter, AREA_TRANSCATEGORIES);

        /* Access the Categories list */
        final MoneyWiseData myData = pWriter.getData();
        setDataList(myData.getTransCategories());
    }

    @Override
    protected DataValues<MoneyWiseDataType> loadSecureValues() throws OceanusException {
        /* Build data values */
        final DataValues<MoneyWiseDataType> myValues = getRowValues(TransactionCategory.OBJECT_NAME);
        myValues.addValue(TransactionCategory.FIELD_CATTYPE, loadInteger(COL_TYPE));
        myValues.addValue(TransactionCategory.FIELD_PARENT, loadInteger(COL_PARENT));
        myValues.addValue(TransactionCategory.FIELD_NAME, loadBytes(COL_NAME));
        myValues.addValue(TransactionCategory.FIELD_DESC, loadBytes(COL_DESC));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void insertSecureItem(final TransactionCategory pItem) throws OceanusException {
        /* Set the fields */
        super.insertSecureItem(pItem);
        writeInteger(COL_TYPE, pItem.getCategoryTypeId());
        writeInteger(COL_PARENT, pItem.getParentCategoryId());
        writeBytes(COL_NAME, pItem.getNameBytes());
        writeBytes(COL_DESC, pItem.getDescBytes());
    }

    @Override
    protected int getLastColumn() {
        /* Return the last column */
        return COL_DESC;
    }

    /**
     * Load the EventCategories from an archive.
     * @param pReport the report
     * @param pWorkBook the workbook
     * @param pData the data set to load into
     * @param pLoader the archive loader
     * @throws OceanusException on error
     */
    protected static void loadArchive(final MetisThreadStatusReport pReport,
                                      final PrometheusSheetWorkBook pWorkBook,
                                      final MoneyWiseData pData,
                                      final ArchiveLoader pLoader) throws OceanusException {
        /* Access the list of categories */
        final TransactionCategoryList myList = pData.getTransCategories();

        /* Protect against exceptions */
        try {
            /* Find the range of cells */
            final PrometheusSheetView myView = pWorkBook.getRangeView(AREA_TRANSCATEGORIES);

            /* Declare the new stage */
            pReport.setNewStage(TransactionCategory.LIST_NAME);

            /* Count the number of Categories */
            final int myTotal = myView.getRowCount();

            /* Declare the number of steps */
            pReport.setNumSteps(myTotal);

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
                final DataValues<MoneyWiseDataType> myValues = new DataValues<>(TransactionCategory.OBJECT_NAME);
                myValues.addValue(TransactionCategory.FIELD_CATTYPE, myType);
                myValues.addValue(TransactionCategory.FIELD_PARENT, myParent);
                myValues.addValue(TransactionCategory.FIELD_NAME, myName);

                /* Add the value into the list */
                final TransactionCategory myCategory = myList.addValuesItem(myValues);

                /* Declare the category */
                pLoader.declareCategory(myCategory);

                /* Report the progress */
                pReport.setNextStep();
            }

            /* PostProcess on load */
            myList.postProcessOnLoad();

            /* Handle exceptions */
        } catch (MetisThreadCancelException e) {
            throw e;
        } catch (OceanusException e) {
            throw new MoneyWiseIOException("Failed to Load " + myList.getItemType().getListName(), e);
        }
    }
}
