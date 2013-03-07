/*******************************************************************************
 * jSpreadSheetManager: SpreadSheet management
 * Copyright 2013 Tony Washer
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
package net.sourceforge.jOceanus.jSpreadSheetManager;

import java.io.InputStream;
import java.io.OutputStream;

import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataManager.JDataException.ExceptionClass;

/**
 * WorkBook class.
 * @author Tony Washer
 */
public class DataWorkBook {
    /**
     * Date width.
     */
    protected static final int WIDTH_DATE = 11;

    /**
     * Integer width.
     */
    protected static final int WIDTH_INT = 8;

    /**
     * Boolean width.
     */
    protected static final int WIDTH_BOOL = 8;

    /**
     * Money width.
     */
    protected static final int WIDTH_MONEY = 13;

    /**
     * Units width.
     */
    protected static final int WIDTH_UNITS = 13;

    /**
     * Rate width.
     */
    protected static final int WIDTH_RATE = 13;

    /**
     * Dilution width.
     */
    protected static final int WIDTH_DILUTION = 13;

    /**
     * Price width.
     */
    protected static final int WIDTH_PRICE = 15;

    /**
     * String width.
     */
    protected static final int WIDTH_STRING = 30;

    /**
     * Font Height.
     */
    protected static final int FONT_HEIGHT = 10;

    /**
     * Value Font.
     */
    protected static final String FONT_VALUE = "Arial";

    /**
     * Numeric Font.
     */
    protected static final String FONT_NUMERIC = "Courier";

    /**
     * Internal date format.
     */
    protected static final String FORMAT_DATE = "yyyy-MM-dd";

    /**
     * ReadOnly.
     */
    private final boolean isReadOnly;

    /**
     * WorkBook type.
     */
    private final WorkBookType theBookType;

    /**
     * Excel WorkBook.
     */
    private final ExcelWorkBook theExcelBook;

    /**
     * Oasis WorkBook.
     */
    private final OasisWorkBook theOasisBook;

    /**
     * Is the workbook readOnly?
     * @return true/false
     */
    public boolean isReadOnly() {
        return isReadOnly;
    }

    /**
     * Obtain workBook type.
     * @return the type
     */
    public WorkBookType getType() {
        return theBookType;
    }

    /**
     * Obtain Excel workBook.
     * @return the book
     */
    public ExcelWorkBook getExcelBook() {
        return theExcelBook;
    }

    /**
     * Obtain Oasis workBook.
     * @return the book
     */
    public OasisWorkBook getOasisBook() {
        return theOasisBook;
    }

    /**
     * Check for readOnly.
     * @throws JDataException on error
     */
    private void checkReadOnly() throws JDataException {
        if (isReadOnly) {
            throw new JDataException(ExceptionClass.LOGIC, "Attempt to modify readOnly Book");
        }
    }

    /**
     * Load Excel workBook from file.
     * @param pInput the input stream
     * @param pType the workbook type
     * @throws JDataException on error
     */
    public DataWorkBook(final InputStream pInput,
                        final WorkBookType pType) throws JDataException {
        /* This is a readOnly sheet */
        isReadOnly = true;

        /* Switch on workbook type */
        switch (pType) {
            case ExcelXLS:
                theExcelBook = new ExcelWorkBook(pInput);
                theOasisBook = null;
                theBookType = pType;
                break;
            case OasisODS:
                theOasisBook = new OasisWorkBook(pInput);
                theExcelBook = null;
                theBookType = pType;
                break;
            default:
                throw new JDataException(ExceptionClass.LOGIC, "Unsupported WorkBookType: "
                                                               + pType);
        }
    }

    /**
     * Create empty workBook.
     * @param pType the workbook type
     * @throws JDataException on error
     */
    public DataWorkBook(final WorkBookType pType) throws JDataException {
        /* This is not a readOnly sheet */
        isReadOnly = false;

        /* Switch on workbook type */
        switch (pType) {
            case ExcelXLS:
                theExcelBook = new ExcelWorkBook();
                theOasisBook = null;
                theBookType = pType;
                break;
            case OasisODS:
                theOasisBook = new OasisWorkBook();
                theExcelBook = null;
                theBookType = pType;
                break;
            default:
                throw new JDataException(ExceptionClass.LOGIC, "Unsupported WorkBookType: "
                                                               + pType);
        }
    }

    /**
     * Save the workBook to output stream.
     * @param pOutput the output stream
     * @throws JDataException on error
     */
    public void saveToStream(final OutputStream pOutput) throws JDataException {
        /* Switch on workbook type */
        switch (theBookType) {
            case ExcelXLS:
                theExcelBook.saveToStream(pOutput);
                break;
            case OasisODS:
                theOasisBook.saveToStream(pOutput);
                break;
            default:
                break;
        }
    }

    /**
     * Create a new Sheet with the given name.
     * @param pName the name of the new sheet
     * @return the new sheet
     * @throws JDataException on error
     */
    public DataSheet newSheet(final String pName) throws JDataException {
        /* Check for modification rights */
        checkReadOnly();

        /* Switch on workbook type */
        switch (theBookType) {
            case ExcelXLS:
                return theExcelBook.newSheet(pName);
            case OasisODS:
                return theOasisBook.newSheet(pName);
            default:
                return null;
        }
    }

    /**
     * Create a new Sheet with the given name.
     * @param pName the name of the new sheet
     * @param pNumRows the number of rows to allocate
     * @param pNumCols the number of columns to allocate
     * @return the new sheet
     * @throws JDataException on error
     */
    public DataSheet newSheet(final String pName,
                              final int pNumRows,
                              final int pNumCols) throws JDataException {
        /* Check for modification rights */
        checkReadOnly();

        /* Switch on workbook type */
        switch (theBookType) {
            case ExcelXLS:
                return theExcelBook.newSheet(pName);
            case OasisODS:
                return theOasisBook.newSheet(pName, pNumRows, pNumCols);
            default:
                return null;
        }
    }

    /**
     * Access a named Sheet.
     * @param pName the name of the sheet
     * @return the sheet
     */
    public DataSheet getSheet(final String pName) {
        /* Switch on workbook type */
        switch (theBookType) {
            case ExcelXLS:
                return theExcelBook.getSheet(pName);
            case OasisODS:
                return theOasisBook.getSheet(pName);
            default:
                return null;
        }
    }

    /**
     * Obtain a view of the named range.
     * @param pName the name of the range
     * @return the view of the range
     * @throws JDataException on error
     */
    public DataView getRangeView(final String pName) throws JDataException {
        /* Switch on workbook type */
        switch (theBookType) {
            case ExcelXLS:
                return theExcelBook.getRangeView(pName);
            case OasisODS:
                return theOasisBook.getRangeView(pName);
            default:
                return null;
        }
    }

    /**
     * WorkBook types.
     */
    public enum WorkBookType {
        /**
         * Excel xls.
         */
        ExcelXLS("xls"),

        /**
         * Oasis ods.
         */
        OasisODS("ods");

        /**
         * File extension.
         */
        private final String theExtension;

        /**
         * Obtain the extension.
         * @return the extension
         */
        public String getExtension() {
            return "."
                   + theExtension;
        }

        /**
         * Constructor.
         * @param pExtension the extension code
         */
        private WorkBookType(final String pExtension) {
            theExtension = pExtension;
        }

        /**
         * Determine workBookType of file.
         * @param pFileName the filename
         * @return the workBookType
         * @throws JDataException on error
         */
        public static WorkBookType determineType(final String pFileName) throws JDataException {
            /* Loop through all values */
            for (WorkBookType myType : WorkBookType.values()) {
                /* If we have matched the type */
                if (pFileName.endsWith(myType.getExtension())) {
                    /* Return it */
                    return myType;
                }
            }

            /* Unrecognised type */
            throw new JDataException(ExceptionClass.EXCEL, "Unrecognised file type "
                                                           + pFileName);
        }
    }
}
