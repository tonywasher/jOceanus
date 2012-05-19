/*******************************************************************************
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
package uk.co.tolcroft.models.sheets;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sourceforge.JDataManager.ModelException;
import net.sourceforge.JDataManager.ModelException.ExceptionClass;
import net.sourceforge.JDecimal.Decimal;
import net.sourceforge.JDecimal.Decimal.Dilution;
import net.sourceforge.JDecimal.Decimal.Money;
import net.sourceforge.JDecimal.Decimal.Price;
import net.sourceforge.JDecimal.Decimal.Rate;
import net.sourceforge.JDecimal.Decimal.Units;
import net.sourceforge.JGordianKnot.PasswordHash;
import net.sourceforge.JGordianKnot.SecureManager;
import net.sourceforge.JGordianKnot.ZipFile.ZipWriteFile;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Workbook;

import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.sheets.SpreadSheet.SheetType;
import uk.co.tolcroft.models.threads.ThreadStatus;

public abstract class SheetWriter<T extends DataSet<T>> {
    /**
     * Thread control
     */
    private ThreadStatus<T> theThread = null;

    /**
     * Writable spreadsheet
     */
    private Workbook theWorkBook = null;

    /**
     * The DataSet
     */
    private T theData = null;

    /**
     * The WorkSheets
     */
    private List<SheetDataItem<?>> theSheets = null;

    /**
     * Class of output sheet
     */
    private SheetType theType = null;

    /**
     * Map of Allocated styles
     */
    private Map<CellStyleType, CellStyle> theMap = null;

    /* Access methods */
    protected ThreadStatus<T> getThread() {
        return theThread;
    }

    protected Workbook getWorkBook() {
        return theWorkBook;
    }

    public T getData() {
        return theData;
    }

    public SheetType getType() {
        return theType;
    }

    /**
     * Constructor
     * @param pThread the Thread control
     */
    protected SheetWriter(ThreadStatus<T> pThread) {
        theThread = pThread;
    }

    /**
     * Add Sheet to list
     * @param pSheet the sheet
     */
    protected void addSheet(SheetDataItem<?> pSheet) {
        theSheets.add(pSheet);
    }

    /**
     * Create a Backup Workbook
     * @param pData Data to write out
     * @param pFile the backup file to write to
     * @throws ModelException
     */
    public void createBackup(T pData,
                             File pFile) throws ModelException {
        OutputStream myStream = null;
        File myTgtFile = null;
        ZipWriteFile myZipFile = null;
        PasswordHash myHash;

        /* Protect the workbook access */
        try {
            /* Note the type of file */
            theType = SheetType.BACKUP;

            /* Record the DataSet */
            theData = pData;

            /* The Target file has ".zip" appended */
            myTgtFile = new File(pFile.getPath() + ".zip");

            /* Create a clone of the security control */
            SecureManager mySecure = pData.getSecurity();
            PasswordHash myBase = pData.getPasswordHash();
            myHash = mySecure.clonePasswordHash(myBase);

            /* Create the new output Zip file */
            myZipFile = new ZipWriteFile(myHash, myTgtFile);
            myStream = myZipFile.getOutputStream(new File(SpreadSheet.fileData));

            /* Initialise the WorkBook */
            initialiseWorkBook();

            /* Write the data to the work book */
            writeWorkBook(myStream);

            /* Close the Stream to force out errors */
            myStream.close();
            myStream = null;

            /* Close the Zip file */
            myZipFile.close();
            myZipFile = null;
        }

        catch (Exception e) {
            /* Protect while cleaning up */
            try {
                /* Close the output stream */
                if (myStream != null)
                    myStream.close();

                /* If we are encrypted close the Zip file */
                if (myZipFile != null)
                    myZipFile.close();
            }

            /* Ignore errors */
            catch (Exception ex) {
            }

            /* Delete the file on error */
            if (myTgtFile != null)
                myTgtFile.delete();

            /* Report the error */
            throw new ModelException(ExceptionClass.EXCEL, "Failed to create Backup Workbook: "
                    + pFile.getName(), e);
        }
    }

    /**
     * Create an Extract Workbook
     * @param pData Data to write out
     * @param pFile the backup file to write to
     * @throws ModelException
     */
    public void createExtract(T pData,
                              File pFile) throws ModelException {
        OutputStream myStream = null;
        FileOutputStream myOutFile = null;
        File myTgtFile = null;

        /* Protect the workbook access */
        try {
            /* Note the type of file */
            theType = SheetType.EXTRACT;

            /* Record the DataSet */
            theData = pData;

            /* The Target file has ".xls" appended */
            myTgtFile = new File(pFile.getPath() + ".xls");

            /* Create an output stream to the file */
            myOutFile = new FileOutputStream(myTgtFile);
            myStream = new BufferedOutputStream(myOutFile);

            /* Initialise the WorkBook */
            initialiseWorkBook();

            /* Write the data to the work book */
            writeWorkBook(myStream);

            /* Close the Stream to force out errors */
            myStream.close();
            myStream = null;
        }

        catch (Exception e) {
            /* Protect while cleaning up */
            try {
                /* Close the output stream */
                if (myStream != null)
                    myStream.close();
            }

            /* Ignore errors */
            catch (Exception ex) {
            }

            /* Delete the file on error */
            if (myTgtFile != null)
                myTgtFile.delete();

            /* Report the error */
            throw new ModelException(ExceptionClass.EXCEL, "Failed to create Editable Workbook: "
                    + myTgtFile.getName(), e);
        }
    }

    /**
     * Register sheets
     */
    protected abstract void registerSheets();

    /**
     * Create the list of sheets to write
     * @throws Exception
     */
    private void initialiseWorkBook() throws Exception {
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
     * Obtain the required CellStyle
     * @param pType the CellStyleType
     * @return the required CellStyle
     */
    protected CellStyle getCellStyle(CellStyleType pType) {
        return theMap.get(pType);
    }

    /**
     * Obtain the required CellStyle
     * @param pValue the value
     * @return the required CellStyle
     */
    protected CellStyle getCellStyle(Decimal pValue) {
        if (pValue instanceof Money)
            return getCellStyle(CellStyleType.Money);
        if (pValue instanceof Units)
            return getCellStyle(CellStyleType.Units);
        if (pValue instanceof Rate)
            return getCellStyle(CellStyleType.Rate);
        if (pValue instanceof Price)
            return getCellStyle(CellStyleType.Price);
        if (pValue instanceof Dilution)
            return getCellStyle(CellStyleType.Dilution);
        return null;
    }

    /**
     * Create the standard CellStyles
     */
    private void createCellStyles() {
        /* Create the map */
        theMap = new EnumMap<CellStyleType, CellStyle>(CellStyleType.class);

        /* Ensure that we can create data formats */
        DataFormat myFormat = theWorkBook.createDataFormat();

        /* Create the Standard fonts */
        Font myValueFont = theWorkBook.createFont();
        myValueFont.setFontName("Arial");
        myValueFont.setFontHeightInPoints((short) 10);
        Font myNumberFont = theWorkBook.createFont();
        myNumberFont.setFontName("Courier");
        myNumberFont.setFontHeightInPoints((short) 10);
        Font myHeaderFont = theWorkBook.createFont();
        myHeaderFont.setFontName("Arial");
        myHeaderFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        myHeaderFont.setFontHeightInPoints((short) 10);

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
     * Write the WorkBook
     * @param pStream the output stream
     * @throws Exception
     */
    private void writeWorkBook(OutputStream pStream) throws Exception {
        SheetDataItem<?> mySheet;

        /* Access the iterator for the list */
        Iterator<SheetDataItem<?>> myIterator = theSheets.iterator();

        /* Declare the number of stages */
        boolean bContinue = theThread.setNumStages(theSheets.size() + 1);

        /* Loop through the sheets */
        while ((bContinue) && (myIterator.hasNext())) {
            /* Access the next sheet */
            mySheet = myIterator.next();

            /* Write data for the sheet */
            bContinue = mySheet.writeSpreadSheet();
        }

        /* If we have built all the sheets */
        if (bContinue)
            bContinue = theThread.setNewStage("Writing");

        /* If we have created the workbook OK */
        if (bContinue) {
            /* Write it out to disk and close the stream */
            theWorkBook.write(pStream);
        }

        /* Check for cancellation */
        if (!bContinue)
            throw new ModelException(ExceptionClass.EXCEL, "Operation Cancelled");
    }

    /**
     * Cell Styles
     */
    protected enum CellStyleType {
        Integer, Boolean, Rate, Dilution, Price, Money, Units, Date, String, Header, Trailer;
    }
}
