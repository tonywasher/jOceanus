/*******************************************************************************
 * jMetis: Java Data Framework
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
package net.sourceforge.joceanus.jmetis.sheet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.util.CellReference;

import net.sourceforge.joceanus.jmetis.MetisIOException;
import net.sourceforge.joceanus.jmetis.MetisLogicException;
import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * The Excel WorkBook.
 */
public class MetisExcelWorkBook {
    /**
     * Excel WorkBook.
     */
    private final HSSFWorkbook theBook;

    /**
     * ExcelDataFormatter.
     */
    private final DataFormatter theExcelFormatter;

    /**
     * JDataFormatter.
     */
    private final MetisDataFormatter theDataFormatter;

    /**
     * FormulaEvaluator.
     */
    private final FormulaEvaluator theEvaluator;

    /**
     * Map of Allocated styles.
     */
    private final Map<String, HSSFCellStyle> theStyleMap;

    /**
     * Map of Constraints.
     */
    private final Map<Object, DVConstraint> theConstraintMap;

    /**
     * Style engine.
     */
    private final DataFormat theStyleEngine;

    /**
     * The Value font.
     */
    private final Font theValueFont;

    /**
     * The Number font.
     */
    private final Font theNumberFont;

    /**
     * The Header font.
     */
    private final Font theHeaderFont;

    /**
     * Constructor.
     * @param pInput the input stream
     * @throws OceanusException on error
     */
    public MetisExcelWorkBook(final InputStream pInput) throws OceanusException {
        try {
            /* Load the book and set null map */
            theBook = new HSSFWorkbook(pInput);
            theStyleMap = null;
            theConstraintMap = null;
            theStyleEngine = null;
            theNumberFont = null;
            theValueFont = null;
            theHeaderFont = null;

            /* Allocate the formatter */
            theDataFormatter = MetisDataWorkBook.createFormatter();

            /* Create evaluator and formatter */
            theEvaluator = new HSSFFormulaEvaluator(theBook);
            theExcelFormatter = new DataFormatter();

        } catch (IOException e) {
            throw new MetisIOException("Failed to load workbook", e);
        }
    }

    /**
     * Constructor.
     */
    public MetisExcelWorkBook() {
        /* Create new book and map */
        theBook = new HSSFWorkbook();
        theStyleMap = new HashMap<>();
        theConstraintMap = new HashMap<>();

        /* Allocate the formatter */
        theDataFormatter = MetisDataWorkBook.createFormatter();

        /* Create evaluator and formatter */
        theEvaluator = new HSSFFormulaEvaluator(theBook);
        theExcelFormatter = new DataFormatter();

        /* Ensure that we can create data formats */
        theStyleEngine = theBook.createDataFormat();

        /* Create the Standard fonts */
        theValueFont = theBook.createFont();
        theValueFont.setFontName(MetisDataWorkBook.FONT_VALUE);
        theValueFont.setFontHeightInPoints((short) MetisDataWorkBook.FONT_HEIGHT);
        theNumberFont = theBook.createFont();
        theNumberFont.setFontName(MetisDataWorkBook.FONT_NUMERIC);
        theNumberFont.setFontHeightInPoints((short) MetisDataWorkBook.FONT_HEIGHT);
        theHeaderFont = theBook.createFont();
        theHeaderFont.setFontName(MetisDataWorkBook.FONT_VALUE);
        theHeaderFont.setBold(true);
        theHeaderFont.setFontHeightInPoints((short) MetisDataWorkBook.FONT_HEIGHT);
    }

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
        return theExcelFormatter.formatCellValue(pCell);
    }

    /**
     * Obtain the data formatter.
     * @return the formatter
     */
    protected MetisDataFormatter getDataFormatter() {
        return theDataFormatter;
    }

    /**
     * Save the workBook to output stream.
     * @param pOutput the output stream
     * @throws OceanusException on error
     */
    public void saveToStream(final OutputStream pOutput) throws OceanusException {
        try {
            theBook.write(pOutput);
        } catch (IOException e) {
            throw new MetisIOException("Failed to save workbook", e);
        }
    }

    /**
     * Obtain a new named sheet.
     * @param pName the name of the sheet
     * @return the new sheet.
     */
    protected MetisDataSheet newSheet(final String pName) {
        /* Create the new Sheet */
        HSSFSheet mySheet = theBook.createSheet(pName);
        return new MetisExcelSheet(this, mySheet, theBook.getSheetIndex(pName), false);
    }

    /**
     * Obtain a named sheet.
     * @param pName the name of the sheet
     * @return the sheet.
     */
    protected MetisDataSheet getSheet(final String pName) {
        /* Create the new Sheet */
        HSSFSheet mySheet = theBook.getSheet(pName);
        return new MetisExcelSheet(this, mySheet, theBook.getSheetIndex(mySheet), true);
    }

    /**
     * Is the sheet hidden?
     * @param pSheetIndex the sheet index
     * @return true/false
     */
    protected boolean isSheetHidden(final int pSheetIndex) {
        return theBook.isSheetHidden(pSheetIndex);
    }

    /**
     * Set the sheet's hidden status.
     * @param pSheetIndex the sheet index
     * @param isHidden true/false
     */
    protected void setSheetHidden(final int pSheetIndex,
                                  final boolean isHidden) {
        theBook.setSheetHidden(pSheetIndex, isHidden);
    }

    /**
     * Obtain a view of the named range.
     * @param pName the name of the range
     * @return the view of the range or null if range does not exist
     * @throws OceanusException on error
     */
    protected MetisDataView getRangeView(final String pName) throws OceanusException {
        /* Find the range of cells */
        Name myName = theBook.getName(pName);
        if (myName == null) {
            return null;
        }

        /* Parse the name reference */
        AreaReference myArea = new AreaReference(myName.getRefersToFormula(), SpreadsheetVersion.EXCEL2007);

        /* Determine extent of Range */
        CellReference myRef = myArea.getLastCell();
        MetisCellPosition myLastCell = new MetisCellPosition(myRef.getCol(), myRef.getRow());
        myRef = myArea.getFirstCell();
        MetisCellPosition myFirstCell = new MetisCellPosition(myRef.getCol(), myRef.getRow());

        /* Obtain the sheet */
        MetisDataSheet mySheet = getSheet(myRef.getSheetName());

        /* Return the view */
        return new MetisDataView(mySheet, myFirstCell, myLastCell);
    }

    /**
     * Declare the named range.
     * @param pName the name of the range
     * @param pRange the range to declare
     * @throws OceanusException on error
     */
    protected void declareRange(final String pName,
                                final AreaReference pRange) throws OceanusException {
        /* Check for existing range */
        Name myName = theBook.getName(pName);
        if (myName != null) {
            throw new MetisLogicException("Name "
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
     * @throws OceanusException on error
     */
    protected void applyDataValidation(final Sheet pSheet,
                                       final CellRangeAddressList pCells,
                                       final String pValidRange) throws OceanusException {
        /* Access the constraint */
        DVConstraint myConstraint = theConstraintMap.get(pValidRange);
        if (myConstraint == null) {
            /* Create and add to map */
            myConstraint = DVConstraint.createFormulaListConstraint(pValidRange);
            theConstraintMap.put(pValidRange, myConstraint);
        }

        /* Link the two and use drop down arrow */
        DataValidation myValidation = new HSSFDataValidation(pCells, myConstraint);
        myValidation.setSuppressDropDownArrow(false);

        /* Apply to the sheet */
        pSheet.addValidationData(myValidation);
    }

    /**
     * Apply Data Validation.
     * @param pSheet the workSheet containing the cells
     * @param pCells the Cells to apply validation to
     * @param pValueList the list of valid values
     * @throws OceanusException on error
     */
    protected void applyDataValidation(final Sheet pSheet,
                                       final CellRangeAddressList pCells,
                                       final String[] pValueList) throws OceanusException {
        /* Access the constraint */
        DVConstraint myConstraint = theConstraintMap.get(pValueList);
        if (myConstraint == null) {
            /* Create and add to map */
            myConstraint = DVConstraint.createExplicitListConstraint(pValueList);
            theConstraintMap.put(pValueList, myConstraint);
        }

        /* Link the two and use drop down arrow */
        DataValidation myValidation = new HSSFDataValidation(pCells, myConstraint);
        myValidation.setSuppressDropDownArrow(false);

        /* Apply to the sheet */
        pSheet.addValidationData(myValidation);
    }

    /**
     * Apply Data Filter.
     * @param pSheet the sheet to filter
     * @param pRange the range to apply the filter to
     * @throws OceanusException on error
     */
    protected void applyDataFilter(final Sheet pSheet,
                                   final CellRangeAddressList pRange) throws OceanusException {
        /* Create the new filter */
        pSheet.setAutoFilter(pRange.getCellRangeAddress(0));
    }

    /**
     * Obtain alignment for a cell.
     * @param pType the cell type
     * @return the alignment
     */
    private HorizontalAlignment getStyleAlignment(final MetisCellStyleType pType) {
        switch (pType) {
            case HEADER:
            case BOOLEAN:
                return HorizontalAlignment.CENTER;
            case DATE:
            case STRING:
                return HorizontalAlignment.LEFT;
            default:
                return HorizontalAlignment.RIGHT;
        }
    }

    /**
     * Obtain font for a cell.
     * @param pType the cell type
     * @return the font
     */
    private Font getStyleFont(final MetisCellStyleType pType) {
        switch (pType) {
            case HEADER:
                return theHeaderFont;
            case BOOLEAN:
            case STRING:
                return theValueFont;
            default:
                return theNumberFont;
        }
    }

    /**
     * Obtain the required CellStyle.
     * @param pType the CellStyleType
     * @return the required CellStyle
     */
    protected HSSFCellStyle getCellStyle(final MetisCellStyleType pType) {
        /* Determine the correct format */
        String myStyleName = MetisDataFormats.getFormatName(pType);

        /* Look for existing format */
        HSSFCellStyle myStyle = theStyleMap.get(myStyleName);
        if (myStyle != null) {
            return myStyle;
        }

        /* Create the New Cell Style */
        myStyle = theBook.createCellStyle();
        myStyle.setFont(getStyleFont(pType));
        myStyle.setAlignment(getStyleAlignment(pType));

        /* If we have a data format */
        if (MetisDataFormats.hasDataFormat(pType)) {
            /* Determine the format */
            String myFormat = MetisDataFormats.getDataFormatString(pType);
            myStyle.setDataFormat(theStyleEngine.getFormat(myFormat));
        }

        /* Add to the map and return new style */
        theStyleMap.put(myStyleName, myStyle);
        return myStyle;
    }

    /**
     * Obtain the required CellStyle.
     * @param pValue the Cell Value
     * @return the required CellStyle
     */
    protected HSSFCellStyle getCellStyle(final Object pValue) {
        /* Determine the correct format */
        String myStyleName = MetisDataFormats.getFormatName(pValue);

        /* Look for existing format */
        HSSFCellStyle myStyle = theStyleMap.get(myStyleName);
        if (myStyle != null) {
            return myStyle;
        }

        /* Determine the CellStyleType */
        MetisCellStyleType myType = MetisDataFormats.getCellStyleType(pValue);

        /* Create the New Cell Style */
        myStyle = theBook.createCellStyle();
        myStyle.setFont(getStyleFont(myType));
        myStyle.setAlignment(getStyleAlignment(myType));

        /* If we have a data format */
        if ((myType != MetisCellStyleType.BOOLEAN)
            && (MetisDataFormats.hasDataFormat(myType))) {
            /* Determine the format */
            String myFormat = MetisDataFormats.getDataFormatString(pValue);
            myStyle.setDataFormat(theStyleEngine.getFormat(myFormat));
        }

        /* Add to the map and return new style */
        theStyleMap.put(myStyleName, myStyle);
        return myStyle;
    }

    /**
     * Obtain the required alternate CellStyle.
     * @param pValue the Cell Value
     * @return the required CellStyle
     */
    protected HSSFCellStyle getAlternateCellStyle(final Object pValue) {
        /* Determine the correct format */
        String myStyleName = MetisDataFormats.getAlternateFormatName(pValue);

        /* Look for existing format */
        HSSFCellStyle myStyle = theStyleMap.get(myStyleName);
        if (myStyle != null) {
            return myStyle;
        }

        /* Create the New Cell Style */
        myStyle = theBook.createCellStyle();

        /* Determine the CellStyleType */
        MetisCellStyleType myType = MetisDataFormats.getCellStyleType(pValue);

        /* Handle the header style */
        if (myType == MetisCellStyleType.STRING) {
            myType = MetisCellStyleType.HEADER;
            myStyle.setLocked(true);
        }

        /* Set font and style */
        myStyle.setFont(getStyleFont(myType));
        myStyle.setAlignment(getStyleAlignment(myType));

        /* If we have a data format */
        if ((myType != MetisCellStyleType.BOOLEAN)
            && (MetisDataFormats.hasDataFormat(myType))) {
            /* Determine the format */
            String myFormat = MetisDataFormats.getAlternateFormatString(pValue);
            myStyle.setDataFormat(theStyleEngine.getFormat(myFormat));
        }

        /* Add to the map and return new style */
        theStyleMap.put(myStyleName, myStyle);
        return myStyle;
    }
}
