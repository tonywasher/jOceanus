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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.EnumMap;
import java.util.Map;

import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataManager.JDataException.ExceptionClass;
import net.sourceforge.jOceanus.jSpreadSheetManager.DataWorkBook.CellStyleType;

import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.util.CellReference;

/**
 * The Excel WorkBook.
 */
public class ExcelWorkBook {
    /**
     * Excel WorkBook.
     */
    private final HSSFWorkbook theBook;

    /**
     * DataFormatter.
     */
    private final DataFormatter theFormatter;

    /**
     * FormulaEvaluator.
     */
    private final FormulaEvaluator theEvaluator;

    /**
     * Map of Allocated styles.
     */
    private final Map<CellStyleType, HSSFCellStyle> theMap;

    /**
     * evaluate the formula for a cell.
     * @param pCell the cell to evaluate
     * @return the calculated value
     */
    protected CellValue evaluateFormula(final HSSFCell pCell) {
        return theEvaluator.evaluate(pCell);
    }

    /**
     * Format the cell value.
     * @param pCell the cell to evaluate
     * @return the formatted value
     */
    protected String formatCellValue(final HSSFCell pCell) {
        return theFormatter.formatCellValue(pCell);
    }

    /**
     * Constructor.
     * @param pInput the input stream
     * @throws JDataException on error
     */
    public ExcelWorkBook(final InputStream pInput) throws JDataException {
        try {
            /* Load the book and set null map */
            theBook = new HSSFWorkbook(pInput);
            theMap = null;

            /* Create evaluator and formatter */
            theEvaluator = new HSSFFormulaEvaluator(theBook);
            theFormatter = new DataFormatter();

            /* Set the missing Cell Policy */
            theBook.setMissingCellPolicy(Row.RETURN_BLANK_AS_NULL);

        } catch (IOException e) {
            throw new JDataException(ExceptionClass.EXCEL, "Failed to load workbook", e);
        }
    }

    /**
     * Constructor.
     */
    public ExcelWorkBook() {
        /* Create new book and map */
        theBook = new HSSFWorkbook();
        theMap = new EnumMap<CellStyleType, HSSFCellStyle>(CellStyleType.class);

        /* Create evaluator and formatter */
        theEvaluator = new HSSFFormulaEvaluator(theBook);
        theFormatter = new DataFormatter();

        /* Set the missing Cell Policy */
        theBook.setMissingCellPolicy(Row.CREATE_NULL_AS_BLANK);

        /* Create standard cell styles */
        createCellStyles();
    }

    /**
     * Save the workBook to output stream.
     * @param pOutput the output stream
     * @throws JDataException on error
     */
    public void saveToStream(final OutputStream pOutput) throws JDataException {
        try {
            theBook.write(pOutput);
        } catch (IOException e) {
            throw new JDataException(ExceptionClass.EXCEL, "Failed to save workbook", e);
        }
    }

    /**
     * Obtain a new named sheet.
     * @param pName the name of the sheet
     * @return the new sheet.
     */
    protected DataSheet newSheet(final String pName) {
        /* Create the new Sheet */
        HSSFSheet mySheet = theBook.createSheet(pName);
        return new DataSheet(this, mySheet);
    }

    /**
     * Obtain a named sheet.
     * @param pName the name of the sheet
     * @return the sheet.
     */
    protected DataSheet getSheet(final String pName) {
        /* Create the new Sheet */
        HSSFSheet mySheet = theBook.getSheet(pName);
        return new DataSheet(this, mySheet);
    }

    /**
     * Obtain a view of the named range.
     * @param pName the name of the range
     * @return the view of the range
     * @throws JDataException on error
     */
    protected DataView getRangeView(final String pName) throws JDataException {
        /* Find the range of cells */
        Name myName = theBook.getName(pName);
        if (myName == null) {
            throw new JDataException(ExceptionClass.EXCEL, "Name "
                                                           + pName
                                                           + "not found in workbook");
        }

        /* Parse the name reference */
        AreaReference myArea = new AreaReference(myName.getRefersToFormula());

        /* Determine extent of Range */
        CellReference myRef = myArea.getLastCell();
        CellPosition myLastCell = new CellPosition(myRef.getCol(), myRef.getRow());
        myRef = myArea.getFirstCell();
        CellPosition myFirstCell = new CellPosition(myRef.getCol(), myRef.getRow());

        /* Obtain the sheet and reject if missing */
        DataSheet mySheet = getSheet(myRef.getSheetName());
        if (mySheet == null) {
            throw new JDataException(ExceptionClass.EXCEL, "Sheet for "
                                                           + pName
                                                           + "not found in workbook");
        }

        /* Return the view */
        return new DataView(mySheet, myFirstCell, myLastCell);
    }

    /**
     * Declare the named range.
     * @param pName the name of the range
     * @param pRange the range to declare
     * @throws JDataException on error
     */
    protected void declareRange(final String pName,
                                final AreaReference pRange) throws JDataException {
        /* Check for existing range */
        Name myName = theBook.getName(pName);
        if (myName != null) {
            throw new JDataException(ExceptionClass.EXCEL, "Name "
                                                           + pName
                                                           + " already exists in workbook");
        }

        /* Build the basic name */
        myName = theBook.createName();
        myName.setNameName(pName);

        /* Set into Name */
        String myRef = pRange.formatAsString();
        myName.setRefersToFormula(myRef);
    }

    /**
     * Apply Data Validation.
     * @param pSheet the workSheet containing the cells
     * @param pCells the Cells to apply validation to
     * @param pValidRange the name of the validation range
     * @throws JDataException on error
     */
    public void applyDataValidation(final Sheet pSheet,
                                    final CellRangeAddressList pCells,
                                    final String pValidRange) throws JDataException {
        /* Create the constraint */
        DVConstraint myConstraint = DVConstraint.createFormulaListConstraint(pValidRange);

        /* Link the two and use drip down arrow */
        DataValidation myValidation = new HSSFDataValidation(pCells, myConstraint);
        myValidation.setSuppressDropDownArrow(false);

        /* Apply to the sheet */
        pSheet.addValidationData(myValidation);
    }

    /**
     * Create the standard CellStyles.
     */
    private void createCellStyles() {
        /* Ensure that we can create data formats */
        DataFormat myFormat = theBook.createDataFormat();

        /* Create the Standard fonts */
        Font myValueFont = theBook.createFont();
        myValueFont.setFontName(DataWorkBook.FONT_VALUE);
        myValueFont.setFontHeightInPoints((short) DataWorkBook.FONT_HEIGHT);
        Font myNumberFont = theBook.createFont();
        myNumberFont.setFontName(DataWorkBook.FONT_NUMERIC);
        myNumberFont.setFontHeightInPoints((short) DataWorkBook.FONT_HEIGHT);
        Font myHeaderFont = theBook.createFont();
        myHeaderFont.setFontName(DataWorkBook.FONT_VALUE);
        myHeaderFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        myHeaderFont.setFontHeightInPoints((short) DataWorkBook.FONT_HEIGHT);

        /* Create the Date Cell Style */
        HSSFCellStyle myStyle = theBook.createCellStyle();
        myStyle.setDataFormat(myFormat.getFormat("dd-MMM-yy"));
        myStyle.setFont(myNumberFont);
        myStyle.setAlignment(CellStyle.ALIGN_LEFT);
        theMap.put(CellStyleType.Date, myStyle);

        /* Create the Money Cell Style */
        myStyle = theBook.createCellStyle();
        myStyle.setDataFormat(myFormat.getFormat("£#,##0.00"));
        myStyle.setFont(myNumberFont);
        myStyle.setAlignment(CellStyle.ALIGN_RIGHT);
        theMap.put(CellStyleType.Money, myStyle);

        /* Create the Price Cell Style */
        myStyle = theBook.createCellStyle();
        myStyle.setDataFormat(myFormat.getFormat("£#,##0.0000"));
        myStyle.setFont(myNumberFont);
        myStyle.setAlignment(CellStyle.ALIGN_RIGHT);
        theMap.put(CellStyleType.Price, myStyle);

        /* Create the Units Cell Style */
        myStyle = theBook.createCellStyle();
        myStyle.setDataFormat(myFormat.getFormat("#,##0.0000"));
        myStyle.setFont(myNumberFont);
        myStyle.setAlignment(CellStyle.ALIGN_RIGHT);
        theMap.put(CellStyleType.Units, myStyle);

        /* Create the Rate Cell Style */
        myStyle = theBook.createCellStyle();
        myStyle.setDataFormat(myFormat.getFormat("0.00%"));
        myStyle.setFont(myNumberFont);
        myStyle.setAlignment(CellStyle.ALIGN_RIGHT);
        theMap.put(CellStyleType.Rate, myStyle);

        /* Create the Dilution Cell Style */
        myStyle = theBook.createCellStyle();
        myStyle.setDataFormat(myFormat.getFormat("0.000000"));
        myStyle.setAlignment(CellStyle.ALIGN_RIGHT);
        myStyle.setFont(myNumberFont);
        theMap.put(CellStyleType.Dilution, myStyle);

        /* Create the Integer Cell Style */
        myStyle = theBook.createCellStyle();
        myStyle.setDataFormat(myFormat.getFormat("0"));
        myStyle.setFont(myNumberFont);
        myStyle.setAlignment(CellStyle.ALIGN_RIGHT);
        theMap.put(CellStyleType.Integer, myStyle);

        /* Create the Boolean Cell Style */
        myStyle = theBook.createCellStyle();
        myStyle.setFont(myValueFont);
        myStyle.setAlignment(CellStyle.ALIGN_CENTER);
        theMap.put(CellStyleType.Boolean, myStyle);

        /* Create the String Cell Style */
        myStyle = theBook.createCellStyle();
        myStyle.setFont(myValueFont);
        myStyle.setAlignment(CellStyle.ALIGN_LEFT);
        theMap.put(CellStyleType.String, myStyle);

        /* Create the Header Cell Style */
        myStyle = theBook.createCellStyle();
        myStyle.setFont(myHeaderFont);
        myStyle.setAlignment(CellStyle.ALIGN_CENTER);
        myStyle.setLocked(true);
        theMap.put(CellStyleType.Header, myStyle);
    }

    /**
     * Obtain the required CellStyle.
     * @param pType the CellStyleType
     * @return the required CellStyle
     */
    protected HSSFCellStyle getCellStyle(final CellStyleType pType) {
        return theMap.get(pType);
    }
}
