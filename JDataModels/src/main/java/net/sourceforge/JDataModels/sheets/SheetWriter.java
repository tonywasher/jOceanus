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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataException.ExceptionClass;
import net.sourceforge.JDataModels.data.DataSet;
import net.sourceforge.JDataModels.data.TaskControl;
import net.sourceforge.JDataModels.sheets.SpreadSheet.SheetType;
import net.sourceforge.JDecimal.Decimal;
import net.sourceforge.JDecimal.Dilution;
import net.sourceforge.JDecimal.Money;
import net.sourceforge.JDecimal.Price;
import net.sourceforge.JDecimal.Rate;
import net.sourceforge.JDecimal.Units;
import net.sourceforge.JGordianKnot.PasswordHash;
import net.sourceforge.JGordianKnot.SecureManager;
import net.sourceforge.JGordianKnot.ZipFile.ZipWriteFile;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * Write control for spreadsheets.
 * @author Tony Washer
 * @param <T> the DataSet type
 */
public abstract class SheetWriter<T extends DataSet<T>> {
    /**
     * Font Height.
     */
    private static final int FONT_HEIGHT = 10;

    /**
     * Task control.
     */
    private final TaskControl<T> theTask;

    /**
     * Writable spreadsheet.
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
     * Map of Allocated styles.
     */
    private Map<CellStyleType, CellStyle> theMap = null;

    /**
     * get thread status.
     * @return the status
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
    protected SheetWriter(final TaskControl<T> pTask) {
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
     * Create a Backup Workbook.
     * @param pData Data to write out
     * @param pFile the backup file to write to
     * @throws JDataException on error
     */
    public void createBackup(final T pData,
                             final File pFile) throws JDataException {
        OutputStream myStream = null;
        ZipWriteFile myZipFile = null;
        boolean bSuccess = false;

        /* The Target file has ".zip" appended */
        File myTgtFile = new File(pFile.getPath() + ".zip");

        /* Protect the workbook access */
        try {
            /* Note the type of file */
            theType = SheetType.BACKUP;

            /* Record the DataSet */
            theData = pData;

            /* Create a clone of the security control */
            SecureManager mySecure = pData.getSecurity();
            PasswordHash myBase = pData.getPasswordHash();
            PasswordHash myHash = mySecure.clonePasswordHash(myBase);

            /* Create the new output Zip file */
            myZipFile = new ZipWriteFile(myHash, myTgtFile);
            myStream = myZipFile.getOutputStream(new File(SpreadSheet.FILE_NAME));

            /* Initialise the WorkBook */
            initialiseWorkBook();

            /* Write the data to the work book */
            writeWorkBook(myStream);

            /* Close the Stream to force out errors */
            myStream.close();
            myStream = null;

            /* Close the Zip file */
            myZipFile.close();

            /* Set success to avoid deleting file */
            bSuccess = true;
        } catch (IOException e) {
            /* Report the error */
            throw new JDataException(ExceptionClass.EXCEL, "Failed to create Backup Workbook: "
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

            /* Delete the file on error */
            if ((!bSuccess) && (!myTgtFile.delete())) {
                /* Nothing that we can do. At least we tried */
                myStream = null;
            }
        }
    }

    /**
     * Create an Extract Workbook.
     * @param pData Data to write out
     * @param pFile the backup file to write to
     * @throws JDataException on error
     */
    public void createExtract(final T pData,
                              final File pFile) throws JDataException {
        OutputStream myStream = null;
        FileOutputStream myOutFile = null;
        boolean bSuccess = false;

        /* The Target file has ".xls" appended */
        File myTgtFile = new File(pFile.getPath() + ".xls");

        /* Protect the workbook access */
        try {
            /* Note the type of file */
            theType = SheetType.EXTRACT;

            /* Record the DataSet */
            theData = pData;

            /* Create an output stream to the file */
            myOutFile = new FileOutputStream(myTgtFile);
            myStream = new BufferedOutputStream(myOutFile);

            /* Initialise the WorkBook */
            initialiseWorkBook();

            /* Write the data to the work book */
            writeWorkBook(myStream);

            /* Close the Stream to force out errors */
            myStream.close();

            /* Set success to avoid deleting file */
            bSuccess = true;
        } catch (IOException e) {
            /* Report the error */
            throw new JDataException(ExceptionClass.EXCEL, "Failed to create Editable Workbook: "
                    + myTgtFile.getName(), e);
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

            /* Delete the file on error */
            if ((!bSuccess) && (!myTgtFile.delete())) {
                /* Nothing that we can do. At least we tried */
                myStream = null;
            }
        }
    }

    /**
     * Register sheets.
     */
    protected abstract void registerSheets();

    /**
     * Create the list of sheets to write.
     * @throws JDataException on error
     */
    private void initialiseWorkBook() throws JDataException {
        /* Create the workbook attached to the output stream */
        theWorkBook = new HSSFWorkbook();
        createCellStyles();

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
    }

    /**
     * Obtain the required CellStyle.
     * @param pType the CellStyleType
     * @return the required CellStyle
     */
    protected CellStyle getCellStyle(final CellStyleType pType) {
        return theMap.get(pType);
    }

    /**
     * Obtain the required CellStyle.
     * @param pValue the value
     * @return the required CellStyle
     */
    protected CellStyle getCellStyle(final Decimal pValue) {
        if (pValue instanceof Money) {
            return getCellStyle(CellStyleType.Money);
        }
        if (pValue instanceof Units) {
            return getCellStyle(CellStyleType.Units);
        }
        if (pValue instanceof Rate) {
            return getCellStyle(CellStyleType.Rate);
        }
        if (pValue instanceof Price) {
            return getCellStyle(CellStyleType.Price);
        }
        if (pValue instanceof Dilution) {
            return getCellStyle(CellStyleType.Dilution);
        }
        return null;
    }

    /**
     * Create the standard CellStyles.
     */
    private void createCellStyles() {
        /* Create the map */
        theMap = new EnumMap<CellStyleType, CellStyle>(CellStyleType.class);

        /* Ensure that we can create data formats */
        DataFormat myFormat = theWorkBook.createDataFormat();

        /* Create the Standard fonts */
        Font myValueFont = theWorkBook.createFont();
        myValueFont.setFontName("Arial");
        myValueFont.setFontHeightInPoints((short) FONT_HEIGHT);
        Font myNumberFont = theWorkBook.createFont();
        myNumberFont.setFontName("Courier");
        myNumberFont.setFontHeightInPoints((short) FONT_HEIGHT);
        Font myHeaderFont = theWorkBook.createFont();
        myHeaderFont.setFontName("Arial");
        myHeaderFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        myHeaderFont.setFontHeightInPoints((short) FONT_HEIGHT);

        /* Create the Date Cell Style */
        CellStyle myStyle = theWorkBook.createCellStyle();
        myStyle.setDataFormat(myFormat.getFormat("dd-MMM-yy"));
        myStyle.setFont(myNumberFont);
        myStyle.setAlignment(CellStyle.ALIGN_LEFT);
        theMap.put(CellStyleType.Date, myStyle);

        /* Create the Money Cell Style */
        myStyle = theWorkBook.createCellStyle();
        myStyle.setDataFormat(myFormat.getFormat("£#,##0.00"));
        myStyle.setFont(myNumberFont);
        myStyle.setAlignment(CellStyle.ALIGN_RIGHT);
        theMap.put(CellStyleType.Money, myStyle);

        /* Create the Price Cell Style */
        myStyle = theWorkBook.createCellStyle();
        myStyle.setDataFormat(myFormat.getFormat("£#,##0.0000"));
        myStyle.setFont(myNumberFont);
        myStyle.setAlignment(CellStyle.ALIGN_RIGHT);
        theMap.put(CellStyleType.Price, myStyle);

        /* Create the Units Cell Style */
        myStyle = theWorkBook.createCellStyle();
        myStyle.setDataFormat(myFormat.getFormat("#,##0.0000"));
        myStyle.setFont(myNumberFont);
        myStyle.setAlignment(CellStyle.ALIGN_RIGHT);
        theMap.put(CellStyleType.Units, myStyle);

        /* Create the Rate Cell Style */
        myStyle = theWorkBook.createCellStyle();
        myStyle.setDataFormat(myFormat.getFormat("0.00%"));
        myStyle.setFont(myNumberFont);
        myStyle.setAlignment(CellStyle.ALIGN_RIGHT);
        theMap.put(CellStyleType.Rate, myStyle);

        /* Create the Dilution Cell Style */
        myStyle = theWorkBook.createCellStyle();
        myStyle.setDataFormat(myFormat.getFormat("0.000000"));
        myStyle.setAlignment(CellStyle.ALIGN_RIGHT);
        myStyle.setFont(myNumberFont);
        theMap.put(CellStyleType.Dilution, myStyle);

        /* Create the Integer Cell Style */
        myStyle = theWorkBook.createCellStyle();
        myStyle.setDataFormat(myFormat.getFormat("0"));
        myStyle.setFont(myNumberFont);
        myStyle.setAlignment(CellStyle.ALIGN_RIGHT);
        theMap.put(CellStyleType.Integer, myStyle);

        /* Create the Boolean Cell Style */
        myStyle = theWorkBook.createCellStyle();
        myStyle.setFont(myValueFont);
        myStyle.setAlignment(CellStyle.ALIGN_CENTER);
        theMap.put(CellStyleType.Boolean, myStyle);

        /* Create the String Cell Style */
        myStyle = theWorkBook.createCellStyle();
        myStyle.setFont(myValueFont);
        myStyle.setAlignment(CellStyle.ALIGN_LEFT);
        theMap.put(CellStyleType.String, myStyle);

        /* Create the Header Cell Style */
        myStyle = theWorkBook.createCellStyle();
        myStyle.setFont(myHeaderFont);
        myStyle.setAlignment(CellStyle.ALIGN_CENTER);
        myStyle.setLocked(true);
        theMap.put(CellStyleType.Header, myStyle);

        /* Create the Trailer Cell Style */
        myStyle = theWorkBook.createCellStyle();
        myStyle.setFillForegroundColor(IndexedColors.DARK_GREEN.getIndex());
        myStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        myStyle.setAlignment(CellStyle.ALIGN_LEFT);
        myStyle.setLocked(true);
        theMap.put(CellStyleType.Trailer, myStyle);
    }

    /**
     * Write the WorkBook.
     * @param pStream the output stream
     * @throws JDataException on error
     * @throws IOException on write error
     */
    private void writeWorkBook(final OutputStream pStream) throws JDataException, IOException {
        SheetDataItem<?> mySheet;

        /* Access the iterator for the list */
        Iterator<SheetDataItem<?>> myIterator = theSheets.iterator();

        /* Declare the number of stages */
        boolean bContinue = theTask.setNumStages(theSheets.size() + 1);

        /* Loop through the sheets */
        while ((bContinue) && (myIterator.hasNext())) {
            /* Access the next sheet */
            mySheet = myIterator.next();

            /* Write data for the sheet */
            bContinue = mySheet.writeSpreadSheet();
        }

        /* If we have built all the sheets */
        if (bContinue) {
            bContinue = theTask.setNewStage("Writing");
        }

        /* If we have created the workbook OK */
        if (bContinue) {
            /* Write it out to disk and close the stream */
            theWorkBook.write(pStream);
        }

        /* Check for cancellation */
        if (!bContinue) {
            throw new JDataException(ExceptionClass.EXCEL, "Operation Cancelled");
        }
    }

    /**
     * Cell Styles.
     */
    protected enum CellStyleType {
        /**
         * Integer.
         */
        Integer,

        /**
         * Boolean.
         */
        Boolean,

        /**
         * Rate.
         */
        Rate,

        /**
         * Dilution.
         */
        Dilution,

        /**
         * Price.
         */
        Price,

        /**
         * Money.
         */
        Money,

        /**
         * Units.
         */
        Units,

        /**
         * Date.
         */
        Date,

        /**
         * String.
         */
        String,

        /**
         * Header.
         */
        Header,

        /**
         * Trailer.
         */
        Trailer;
    }
}
