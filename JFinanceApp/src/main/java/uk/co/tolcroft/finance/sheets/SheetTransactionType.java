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

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataException.ExceptionClass;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;

import uk.co.tolcroft.finance.data.FinanceData;
import uk.co.tolcroft.finance.data.TransactionType;
import uk.co.tolcroft.finance.data.TransactionType.TransTypeList;
import uk.co.tolcroft.models.data.TaskControl;
import uk.co.tolcroft.models.sheets.SheetReader.SheetHelper;
import uk.co.tolcroft.models.sheets.SheetStaticData;

/**
 * SheetStaticData extension for TransactionType.
 * @author Tony Washer
 */
public class SheetTransactionType extends SheetStaticData<TransactionType> {

    /**
     * NamedArea for Transaction Types.
     */
    private static final String AREA_TRANSTYPES = TransactionType.LIST_NAME;

    /**
     * NameList for TranTypes.
     */
    protected static final String AREA_TRANSTYPENAMES = TransactionType.OBJECT_NAME + "Names";

    /**
     * Alternative NamedArea for Transaction Types.
     */
    private static final String AREA_TRANSTYPES1 = "TransType";

    /**
     * TransactionTypes data list.
     */
    private final TransTypeList theList;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the spreadsheet reader
     */
    protected SheetTransactionType(final FinanceReader pReader) {
        /* Call super-constructor */
        super(pReader, AREA_TRANSTYPES);

        /* Access the Transaction Type list */
        theList = pReader.getData().getTransTypes();
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected SheetTransactionType(final FinanceWriter pWriter) {
        /* Call super-constructor */
        super(pWriter, AREA_TRANSTYPES, AREA_TRANSTYPENAMES);

        /* Access the Transaction Type list */
        theList = pWriter.getData().getTransTypes();
        setDataList(theList);
    }

    /**
     * Load encrypted.
     * @param pId the id
     * @param pControlId the control id
     * @param isEnabled isEnabled
     * @param iOrder the sort order
     * @param pName the name
     * @param pDesc the description
     * @throws JDataException on error
     */
    @Override
    protected void loadEncryptedItem(final int pId,
                                     final int pControlId,
                                     final boolean isEnabled,
                                     final int iOrder,
                                     final byte[] pName,
                                     final byte[] pDesc) throws JDataException {
        /* Create the item */
        theList.addItem(pId, pControlId, isEnabled, iOrder, pName, pDesc);
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
    protected void loadClearTextItem(final int pId,
                                     final boolean isEnabled,
                                     final int iOrder,
                                     final String pName,
                                     final String pDesc) throws JDataException {
        /* Create the item */
        theList.addItem(pId, isEnabled, iOrder, pName, pDesc);
    }

    /**
     * Load the Transaction Types from an archive.
     * @param pTask the task control
     * @param pHelper the sheet helper
     * @param pData the data set to load into
     * @return continue to load <code>true/false</code>
     * @throws JDataException on error
     */
    protected static boolean loadArchive(final TaskControl<FinanceData> pTask,
                                         final SheetHelper pHelper,
                                         final FinanceData pData) throws JDataException {
        /* Protect against exceptions */
        try {
            /* Find the range of cells */
            AreaReference myRange = pHelper.resolveAreaReference(AREA_TRANSTYPES1);

            /* Declare the new stage */
            if (!pTask.setNewStage(AREA_TRANSTYPES)) {
                return false;
            }

            /* Access the number of reporting steps */
            int mySteps = pTask.getReportingSteps();
            int myCount = 0;

            /* If we found the range OK */
            if (myRange != null) {
                /* Access the relevant sheet and Cell references */
                CellReference myTop = myRange.getFirstCell();
                CellReference myBottom = myRange.getLastCell();
                Sheet mySheet = pHelper.getSheetByName(myTop.getSheetName());
                int myCol = myTop.getCol();

                /* Count the number of transaction types */
                int myTotal = myBottom.getRow() - myTop.getRow() + 1;

                /* Access the list of transaction types */
                TransTypeList myList = pData.getTransTypes();

                /* Declare the number of steps */
                if (!pTask.setNumSteps(myTotal)) {
                    return false;
                }

                /* Loop through the rows of the single column range */
                for (int i = myTop.getRow(); i <= myBottom.getRow(); i++) {
                    /* Access the cell by reference */
                    Row myRow = mySheet.getRow(i);
                    Cell myCell = myRow.getCell(myCol);

                    /* Add the value into the finance tables */
                    myList.addItem(myCell.getStringCellValue());

                    /* Report the progress */
                    myCount++;
                    if (((myCount % mySteps) == 0) && (!pTask.setStepsDone(myCount))) {
                        return false;
                    }
                }
            }

            /* Handle exceptions */
        } catch (JDataException e) {
            throw new JDataException(ExceptionClass.EXCEL, "Failed to Load Transaction Types", e);
        }

        /* Return to caller */
        return true;
    }
}
