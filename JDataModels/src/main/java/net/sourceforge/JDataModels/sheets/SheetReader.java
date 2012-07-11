/*******************************************************************************
 * JDataModels: Data models
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
package net.sourceforge.JDataModels.sheets;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataException.ExceptionClass;
import net.sourceforge.JDataModels.data.DataSet;
import net.sourceforge.JDataModels.data.TaskControl;
import net.sourceforge.JDataModels.sheets.SpreadSheet.SheetType;
import net.sourceforge.JGordianKnot.PasswordHash;
import net.sourceforge.JGordianKnot.SecureManager;
import net.sourceforge.JGordianKnot.ZipFile.ZipReadFile;

import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.AreaReference;

/**
 * Load control for spreadsheets.
 * @author Tony Washer
 * @param <T> the DataSet type
 */
public abstract class SheetReader<T extends DataSet<T>> {
    /**
     * Rate conversion factor.
     */
    private static final int RATE_CONVERSION = 100;

    /**
     * Task control.
     */
    private final TaskControl<T> theTask;

    /**
     * Spreadsheet.
     */
    private Workbook theWorkBook = null;

    /**
     * The DataSet.
     */
    private T theData = null;

    /**
     * The WorkSheets.
     */
    private List<SheetDataItem<?>> theSheets = null;

    /**
     * Class of output sheet.
     */
    private SheetType theType = null;

    /**
     * get task control.
     * @return the task control
     */
    protected TaskControl<T> getTask() {
        return theTask;
    }

    /**
     * get workbook.
     * @return the workbook
     */
    protected Workbook getWorkBook() {
        return theWorkBook;
    }

    /**
     * get dataSet.
     * @return the dataSet
     */
    public T getData() {
        return theData;
    }

    /**
     * get sheet type.
     * @return the sheet type
     */
    public SheetType getType() {
        return theType;
    }

    /**
     * Constructor.
     * @param pTask the Task control
     */
    public SheetReader(final TaskControl<T> pTask) {
        theTask = pTask;
    }

    /**
     * Add Sheet to list.
     * @param pSheet the sheet
     */
    protected void addSheet(final SheetDataItem<?> pSheet) {
        theSheets.add(pSheet);
    }

    /**
     * Load a Backup Workbook.
     * @param pFile the backup file to write to
     * @return the loaded DataSet
     * @throws JDataException on error
     */
    public T loadBackup(final File pFile) throws JDataException {
        InputStream myStream = null;

        /* Protect the workbook retrieval */
        try {
            /* Note the type of file */
            theType = SheetType.BACKUP;

            /* Access the zip file */
            ZipReadFile myFile = new ZipReadFile(pFile);

            /* Obtain the hash bytes from the file */
            byte[] myHashBytes = myFile.getHashBytes();

            /* Access the Security manager */
            SecureManager mySecurity = theTask.getSecurity();

            /* Obtain the initialised password hash */
            PasswordHash myHash = mySecurity.resolvePasswordHash(myHashBytes, pFile.getName());

            /* Associate this password hash with the Zip file */
            myFile.setPasswordHash(myHash);

            /* Access the input stream for the first file */
            myStream = myFile.getInputStream(myFile.getContents().findFileEntry(SpreadSheet.FILE_NAME));

            /* Initialise the workbook */
            boolean bContinue = initialiseWorkBook(myStream);

            /* Load the workbook */
            if (bContinue) {
                bContinue = loadWorkBook();
            }

            /* Close the Stream to force out errors */
            myStream.close();
            myStream = null;

            /* Check for cancellation */
            if (!bContinue) {
                throw new JDataException(ExceptionClass.EXCEL, "Operation Cancelled");
            }
        } catch (IOException e) {
            /* Report the error */
            throw new JDataException(ExceptionClass.EXCEL, "Failed to load Backup Workbook: "
                    + pFile.getName(), e);
        } finally {
            /* Protect while cleaning up */
            try {
                /* Close the output stream */
                if (myStream != null) {
                    myStream.close();
                }

                /* Ignore errors */
            } catch (IOException ex) {
                myStream = null;
            }
        }

        /* Return the new DataSet */
        return theData;
    }

    /**
     * Load an Extract Workbook.
     * @param pFile the Extract file to load from
     * @return the loaded DataSet
     * @throws JDataException on error
     */
    public T loadExtract(final File pFile) throws JDataException {
        InputStream myStream = null;

        /* Protect the workbook retrieval */
        try {
            /* Note the type of file */
            theType = SheetType.EXTRACT;

            /* Create an input stream to the file */
            FileInputStream myInFile = new FileInputStream(pFile);
            myStream = new BufferedInputStream(myInFile);

            /* Initialise the workbook */
            boolean bContinue = initialiseWorkBook(myStream);

            /* Load the workbook */
            if (bContinue) {
                bContinue = loadWorkBook();
            }

            /* Close the Stream to force out errors */
            myStream.close();
            myStream = null;

            /* Check for cancellation */
            if (!bContinue) {
                throw new JDataException(ExceptionClass.EXCEL, "Operation Cancelled");
            }
        } catch (IOException e) {
            /* Report the error */
            throw new JDataException(ExceptionClass.EXCEL, "Failed to load Edit-able Workbook: "
                    + pFile.getName(), e);
        } finally {
            /* Protect while cleaning up */
            try {
                /* Close the output stream */
                if (myStream != null) {
                    myStream.close();
                }

                /* Ignore errors */
            } catch (IOException ex) {
                myStream = null;
            }
        }

        /* Return the new DataSet */
        return theData;
    }

    /**
     * Register sheets.
     */
    protected abstract void registerSheets();

    /**
     * Obtain empty DataSet.
     * @return the dataSet
     */
    protected abstract T newDataSet();

    /**
     * Create the list of sheets to load.
     * @param pStream the input stream
     * @return continue true/false
     * @throws JDataException on error
     * @throws IOException on read error
     */
    private boolean initialiseWorkBook(final InputStream pStream) throws JDataException, IOException {
        /* Create the new DataSet */
        theData = newDataSet();

        /* Initialise the list */
        theSheets = new ArrayList<SheetDataItem<?>>();

        /* If this is a backup */
        if (theType == SheetType.BACKUP) {
            /* Add security details */
            theSheets.add(new SheetControlKey(this));
            theSheets.add(new SheetDataKey(this));
        }

        /* Add the items */
        theSheets.add(new SheetControl(this));

        /* register additional sheets */
        registerSheets();

        /* Declare the number of stages */
        boolean bContinue = theTask.setNumStages(theSheets.size() + 2);

        /* Note the stage */
        if (bContinue) {
            bContinue = theTask.setNewStage("Loading");
        }

        /* Access the workbook from the stream */
        if (bContinue) {
            theWorkBook = new HSSFWorkbook(pStream);
        }

        /* Set the missing Cell Policy */
        theWorkBook.setMissingCellPolicy(Row.RETURN_BLANK_AS_NULL);

        /* Return continue status */
        return bContinue;
    }

    /**
     * Load the WorkBook.
     * @return continue true/false
     * @throws JDataException on error
     */
    private boolean loadWorkBook() throws JDataException {
        SheetDataItem<?> mySheet;

        /* Access the iterator for the list */
        Iterator<SheetDataItem<?>> myIterator = theSheets.iterator();

        /* Declare the number of stages */
        boolean bContinue = theTask.setNumStages(theSheets.size() + 1);

        /* Loop through the sheets */
        while ((bContinue) && (myIterator.hasNext())) {
            /* Access the next sheet */
            mySheet = myIterator.next();

            /* Load data for the sheet */
            bContinue = mySheet.loadSpreadSheet();
        }

        /* Analyse the data */
        if (!theTask.setNewStage("Refreshing data")) {
            bContinue = false;
        }

        /* Return continue status */
        return bContinue;
    }

    /**
     * SheetHelper class.
     */
    public static class SheetHelper {
        /**
         * The Workbook.
         */
        private Workbook theWorkBook = null;

        /**
         * The FormulaEvaluator.
         */
        private FormulaEvaluator theEvaluator = null;

        /**
         * Constructor.
         * @param pWorkbook the workbook
         */
        public SheetHelper(final HSSFWorkbook pWorkbook) {
            /* Store the workbook */
            theWorkBook = pWorkbook;

            /* Create the evaluator */
            theEvaluator = new HSSFFormulaEvaluator(pWorkbook);
        }

        /**
         * Resolve reference.
         * @param pName the name of the range
         * @return the AreaReference (or null)
         */
        public AreaReference resolveAreaReference(final String pName) {
            AreaReference myRef = null;
            Name myName = theWorkBook.getName(pName);
            if (myName != null) {
                myRef = new AreaReference(myName.getRefersToFormula());
            }
            return myRef;
        }

        /**
         * getSheetByName.
         * @param pName the name of the sheet
         * @return the Sheet
         */
        public Sheet getSheetByName(final String pName) {
            return theWorkBook.getSheet(pName);
        }

        /**
         * Format numeric cell into decimal format.
         * @param pCell the cell to format
         * @return the formatted string
         */
        public String formatNumericCell(final Cell pCell) {
            double myDouble;
            /* If this is a formula cell ensure that it is evaluated */
            if (pCell.getCellType() == Cell.CELL_TYPE_FORMULA) {
                CellValue myValue = theEvaluator.evaluate(pCell);
                myDouble = myValue.getNumberValue();

                /* else just extract the value directly */
            } else {
                myDouble = pCell.getNumericCellValue();
            }

            /* return the formatted string */
            return Double.toString(myDouble);
        }

        /**
         * Parse the cell to return an integer.
         * @param pCell the cell to parse
         * @return the parsed cell
         */
        public Integer parseIntegerCell(final Cell pCell) {
            double myDouble;
            /* If this is a formula cell ensure that it is evaluated */
            if (pCell.getCellType() == Cell.CELL_TYPE_FORMULA) {
                CellValue myValue = theEvaluator.evaluate(pCell);
                myDouble = myValue.getNumberValue();

                /* else just extract the value directly */
            } else {
                myDouble = pCell.getNumericCellValue();
            }

            /* Return the value as an integer */
            return (int) myDouble;
        }

        /**
         * Parse the cell to return an integer.
         * @param pCell the cell to parse
         * @return the parsed cell
         */
        public String formatRateCell(final Cell pCell) {
            double myDouble;
            /* If this is a formula cell ensure that it is evaluated */
            if (pCell.getCellType() == Cell.CELL_TYPE_FORMULA) {
                CellValue myValue = theEvaluator.evaluate(pCell);
                myDouble = myValue.getNumberValue();

                /* else just extract the value directly */
            } else {
                myDouble = pCell.getNumericCellValue();
            }

            /* return the formatted string */
            return Double.toString(RATE_CONVERSION * myDouble);
        }
    }
}
