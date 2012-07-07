/*******************************************************************************
 * JFinanceApp: Finance Application
 * Copyright 2012 Tony Washer
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
package uk.co.tolcroft.finance.sheets;

import java.util.Date;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataException.ExceptionClass;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;

import uk.co.tolcroft.finance.data.FinanceData;
import uk.co.tolcroft.finance.views.DilutionEvent.DilutionEventList;
import uk.co.tolcroft.models.data.TaskControl;
import uk.co.tolcroft.models.sheets.SheetReader.SheetHelper;

/**
 * Class to handle loading dilution details from archive.
 * @author Tony Washer
 */
public final class SheetDilution {
    /**
     * NamedArea for Dilution Details.
     */
    private static final String AREA_DILUTIONS = "DilutionDetails";

    /**
     * Private constructor to avoid instantiation.
     */
    private SheetDilution() {
    }

    /**
     * Load the Dilution Details from an archive.
     * @param pTask the task control
     * @param pHelper the sheet helper
     * @param pData the data set to link to
     * @param pList the dilution list to load into
     * @return continue to load <code>true/false</code>
     * @throws JDataException on error
     */
    protected static boolean loadArchive(final TaskControl<FinanceData> pTask,
                                         final SheetHelper pHelper,
                                         final FinanceData pData,
                                         final DilutionEventList pList) throws JDataException {
        /* Protect against exceptions */
        try {
            /* Find the range of cells */
            AreaReference myRange = pHelper.resolveAreaReference(AREA_DILUTIONS);

            /* Access the number of reporting steps */
            int mySteps = pTask.getReportingSteps();
            int myCount = 0;

            /* Declare the new stage */
            if (!pTask.setNewStage(AREA_DILUTIONS)) {
                return false;
            }

            /* If we found the range OK */
            if (myRange != null) {
                /* Access the relevant sheet and Cell references */
                CellReference myTop = myRange.getFirstCell();
                CellReference myBottom = myRange.getLastCell();
                Sheet mySheet = pHelper.getSheetByName(myTop.getSheetName());
                int myCol = myTop.getCol();

                /* Count the number of dilutions */
                int myTotal = myBottom.getRow() - myTop.getRow() + 1;

                /* Declare the number of steps */
                if (!pTask.setNumSteps(myTotal)) {
                    return false;
                }

                /* Loop through the rows of the table */
                for (int i = myTop.getRow(); i <= myBottom.getRow(); i++) {
                    /* Access the row */
                    Row myRow = mySheet.getRow(i);

                    /* Access account */
                    Cell myCell = myRow.getCell(myCol);
                    String myAccount = myCell.getStringCellValue();

                    /* Access date */
                    myCell = myRow.getCell(myCol + 1);
                    Date myDate = myCell.getDateCellValue();

                    /* Access Factor */
                    myCell = myRow.getCell(myCol + 2);
                    String myFactor = pHelper.formatNumericCell(myCell);

                    /* Add any non-zero prices into the finance tables */
                    pList.addDilution(myAccount, myDate, myFactor);

                    /* Report the progress */
                    myCount++;
                    if (((myCount % mySteps) == 0) && (!pTask.setStepsDone(myCount))) {
                        return false;
                    }
                }
            }

            /* Handle exceptions */
        } catch (JDataException e) {
            throw new JDataException(ExceptionClass.EXCEL, "Failed to Load Dilution Details", e);
        }

        /* Return to caller */
        return true;
    }
}
