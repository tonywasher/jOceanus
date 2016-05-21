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
import net.sourceforge.joceanus.jmoneywise.MoneyWiseIOException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.statics.LoanCategoryType;
import net.sourceforge.joceanus.jmoneywise.data.statics.LoanCategoryType.LoanCategoryTypeList;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.data.TaskControl;
import net.sourceforge.joceanus.jprometheus.sheets.PrometheusSheetStaticData;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * SheetStaticData extension for LoanCategoryType.
 * @author Tony Washer
 */
public class SheetLoanCategoryType
        extends PrometheusSheetStaticData<LoanCategoryType, MoneyWiseDataType> {
    /**
     * NamedArea for LoanCategoryTypes.
     */
    private static final String AREA_LOANCATTYPES = LoanCategoryType.LIST_NAME;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the spreadsheet reader
     */
    protected SheetLoanCategoryType(final MoneyWiseReader pReader) {
        /* Call super-constructor */
        super(pReader, AREA_LOANCATTYPES);

        /* Access the Loan Type list */
        MoneyWiseData myData = pReader.getData();
        setDataList(myData.getLoanCategoryTypes());
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected SheetLoanCategoryType(final MoneyWiseWriter pWriter) {
        /* Call super-constructor */
        super(pWriter, AREA_LOANCATTYPES);

        /* Access the Loan Type list */
        MoneyWiseData myData = pWriter.getData();
        setDataList(myData.getLoanCategoryTypes());
    }

    @Override
    protected DataValues<MoneyWiseDataType> loadSecureValues() throws OceanusException {
        /* Build data values */
        return getRowValues(LoanCategoryType.OBJECT_NAME);
    }

    /**
     * Load the Account Types from an archive.
     * @param pTask the task control
     * @param pWorkBook the workbook
     * @param pData the data set to load into
     * @return continue to load <code>true/false</code>
     * @throws OceanusException on error
     */
    protected static boolean loadArchive(final TaskControl<MoneyWiseData> pTask,
                                         final MetisDataWorkBook pWorkBook,
                                         final MoneyWiseData pData) throws OceanusException {
        /* Access the list of loan types */
        LoanCategoryTypeList myList = pData.getLoanCategoryTypes();

        /* Protect against exceptions */
        try {
            /* Find the range of cells */
            MetisDataView myView = pWorkBook.getRangeView(AREA_LOANCATTYPES);

            /* Declare the new stage */
            if (!pTask.setNewStage(AREA_LOANCATTYPES)) {
                return false;
            }

            /* Access the number of reporting steps */
            int mySteps = pTask.getReportingSteps();
            int myCount = 0;

            /* Count the number of LoanCategoryTypes */
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

            /* Handle exceptions */
        } catch (OceanusException e) {
            throw new MoneyWiseIOException("Failed to Load " + myList.getItemType().getListName(), e);
        }

        /* Return to caller */
        return true;
    }
}
