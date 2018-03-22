/*******************************************************************************
 * jMetis: Java Data Framework
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jmetis.service.sheet.xssf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
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
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import net.sourceforge.joceanus.jmetis.MetisIOException;
import net.sourceforge.joceanus.jmetis.MetisLogicException;
import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.service.sheet.MetisSheetCellPosition;
import net.sourceforge.joceanus.jmetis.service.sheet.MetisSheetCellStyleType;
import net.sourceforge.joceanus.jmetis.service.sheet.MetisSheetFormats;
import net.sourceforge.joceanus.jmetis.service.sheet.MetisSheetSheet;
import net.sourceforge.joceanus.jmetis.service.sheet.MetisSheetView;
import net.sourceforge.joceanus.jmetis.service.sheet.MetisSheetWorkBook;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * The Excel WorkBook.
 */
public class MetisExcelXSSFWorkBook
        implements MetisSheetWorkBook {
    /**
     * Excel WorkBook.
     */
    private final XSSFWorkbook theBook;

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
    private final Map<String, XSSFCellStyle> theStyleMap;

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
     * Is the workBook readOnly?
     */
    private final boolean isReadOnly;

    /**
     * Constructor.
     * @param pInput the input stream
     * @throws OceanusException on error
     */
    protected MetisExcelXSSFWorkBook(final InputStream pInput) throws OceanusException {
        try {
            /* Load the book and set null map */
            theBook = new XSSFWorkbook(pInput);
            theStyleMap = null;
            theConstraintMap = null;
            theStyleEngine = null;
            theNumberFont = null;
            theValueFont = null;
            theHeaderFont = null;

            /* Allocate the formatter */
            theDataFormatter = createFormatter();

            /* Create evaluator and formatter */
            theEvaluator = new XSSFFormulaEvaluator(theBook);
            theExcelFormatter = new DataFormatter();

            /* Note readOnly */
            isReadOnly = true;

        } catch (IOException e) {
            throw new MetisIOException("Failed to load workbook", e);
        }
    }

    /**
     * Constructor.
     */
    protected MetisExcelXSSFWorkBook() {
        /* Create new book and map */
        theBook = new XSSFWorkbook();
        theStyleMap = new HashMap<>();
        theConstraintMap = new HashMap<>();

        /* Allocate the formatter */
        theDataFormatter = createFormatter();

        /* Create evaluator and formatter */
        theEvaluator = new XSSFFormulaEvaluator(theBook);
        theExcelFormatter = new DataFormatter();

        /* Ensure that we can create data formats */
        theStyleEngine = theBook.createDataFormat();

        /* Create the Standard fonts */
        theValueFont = theBook.createFont();
        theValueFont.setFontName(MetisSheetFormats.FONT_VALUE);
        theValueFont.setFontHeightInPoints((short) MetisSheetFormats.FONT_HEIGHT);
        theNumberFont = theBook.createFont();
        theNumberFont.setFontName(MetisSheetFormats.FONT_NUMERIC);
        theNumberFont.setFontHeightInPoints((short) MetisSheetFormats.FONT_HEIGHT);
        theHeaderFont = theBook.createFont();
        theHeaderFont.setFontName(MetisSheetFormats.FONT_VALUE);
        theHeaderFont.setBold(true);
        theHeaderFont.setFontHeightInPoints((short) MetisSheetFormats.FONT_HEIGHT);

        /* Note writable */
        isReadOnly = false;
    }

    @Override
    public boolean isReadOnly() {
        return isReadOnly;
    }

    /**
     * evaluate the formula for a cell.
     * @param pCell the cell to evaluate
     * @return the calculated value
     */
    protected CellValue evaluateFormula(final XSSFCell pCell) {
        return theEvaluator.evaluate(pCell);
    }

    /**
     * Format the cell value.
     * @param pCell the cell to evaluate
     * @return the formatted value
     */
    protected String formatCellValue(final XSSFCell pCell) {
        return theExcelFormatter.formatCellValue(pCell);
    }

    /**
     * Obtain the data formatter.
     * @return the formatter
     */
    protected MetisDataFormatter getDataFormatter() {
        return theDataFormatter;
    }

    @Override
    public void saveToStream(final OutputStream pOutput) throws OceanusException {
        try {
            theBook.write(pOutput);
        } catch (IOException e) {
            throw new MetisIOException("Failed to save workbook", e);
        }
    }

    @Override
    public MetisSheetSheet newSheet(final String pName) {
        /* Create the new Sheet */
        final XSSFSheet mySheet = theBook.createSheet(pName);
        return new MetisExcelXSSFSheet(this, mySheet, theBook.getSheetIndex(pName), false);
    }

    @Override
    public MetisSheetSheet newSheet(final String pName,
                                    final int pNumRows,
                                    final int pNumCols) throws OceanusException {
        return newSheet(pName);
    }

    @Override
    public MetisSheetSheet getSheet(final String pName) {
        /* Create the new Sheet */
        final XSSFSheet mySheet = theBook.getSheet(pName);
        return new MetisExcelXSSFSheet(this, mySheet, theBook.getSheetIndex(mySheet), true);
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

    @Override
    public MetisSheetView getRangeView(final String pName) {
        /* Find the range of cells */
        final Name myName = theBook.getName(pName);
        if (myName == null) {
            return null;
        }

        /* Parse the name reference */
        final AreaReference myArea = new AreaReference(myName.getRefersToFormula(), SpreadsheetVersion.EXCEL2007);

        /* Determine extent of Range */
        CellReference myRef = myArea.getLastCell();
        final MetisSheetCellPosition myLastCell = new MetisSheetCellPosition(myRef.getCol(), myRef.getRow());
        myRef = myArea.getFirstCell();
        final MetisSheetCellPosition myFirstCell = new MetisSheetCellPosition(myRef.getCol(), myRef.getRow());

        /* Obtain the sheet */
        final MetisSheetSheet mySheet = getSheet(myRef.getSheetName());

        /* Return the view */
        return new MetisSheetView(mySheet, myFirstCell, myLastCell);
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
        final String myRef = pRange.formatAsString();
        myName.setRefersToFormula(myRef);
    }

    /**
     * Apply Data Validation.
     * @param pSheet the workSheet containing the cells
     * @param pCells the Cells to apply validation to
     * @param pValidRange the name of the validation range
     */
    protected void applyDataValidation(final Sheet pSheet,
                                       final CellRangeAddressList pCells,
                                       final String pValidRange) {
        /* Access the constraint */
        DVConstraint myConstraint = theConstraintMap.get(pValidRange);
        if (myConstraint == null) {
            /* Create and add to map */
            myConstraint = DVConstraint.createFormulaListConstraint(pValidRange);
            theConstraintMap.put(pValidRange, myConstraint);
        }

        /* Link the two and use drop down arrow */
        final DataValidation myValidation = new HSSFDataValidation(pCells, myConstraint);
        myValidation.setSuppressDropDownArrow(false);

        /* Apply to the sheet */
        pSheet.addValidationData(myValidation);
    }

    /**
     * Apply Data Validation.
     * @param pSheet the workSheet containing the cells
     * @param pCells the Cells to apply validation to
     * @param pValueList the list of valid values
     */
    protected void applyDataValidation(final Sheet pSheet,
                                       final CellRangeAddressList pCells,
                                       final String[] pValueList) {
        /* Access the constraint */
        DVConstraint myConstraint = theConstraintMap.get(pValueList);
        if (myConstraint == null) {
            /* Create and add to map */
            myConstraint = DVConstraint.createExplicitListConstraint(pValueList);
            theConstraintMap.put(pValueList, myConstraint);
        }

        /* Link the two and use drop down arrow */
        final DataValidation myValidation = new HSSFDataValidation(pCells, myConstraint);
        myValidation.setSuppressDropDownArrow(false);

        /* Apply to the sheet */
        pSheet.addValidationData(myValidation);
    }

    /**
     * Apply Data Filter.
     * @param pSheet the sheet to filter
     * @param pRange the range to apply the filter to
     */
    protected void applyDataFilter(final Sheet pSheet,
                                   final CellRangeAddressList pRange) {
        /* Create the new filter */
        pSheet.setAutoFilter(pRange.getCellRangeAddress(0));
    }

    /**
     * Obtain alignment for a cell.
     * @param pType the cell type
     * @return the alignment
     */
    private HorizontalAlignment getStyleAlignment(final MetisSheetCellStyleType pType) {
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
    private Font getStyleFont(final MetisSheetCellStyleType pType) {
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
    protected XSSFCellStyle getCellStyle(final MetisSheetCellStyleType pType) {
        /* Determine the correct format */
        final String myStyleName = MetisSheetFormats.getFormatName(pType);

        /* Look for existing format */
        XSSFCellStyle myStyle = theStyleMap.get(myStyleName);
        if (myStyle != null) {
            return myStyle;
        }

        /* Create the New Cell Style */
        myStyle = theBook.createCellStyle();
        myStyle.setFont(getStyleFont(pType));
        myStyle.setAlignment(getStyleAlignment(pType));

        /* If we have a data format */
        if (MetisSheetFormats.hasDataFormat(pType)) {
            /* Determine the format */
            final String myFormat = MetisSheetFormats.getDataFormatString(pType);
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
    protected XSSFCellStyle getCellStyle(final Object pValue) {
        /* Determine the correct format */
        final String myStyleName = MetisSheetFormats.getFormatName(pValue);

        /* Look for existing format */
        XSSFCellStyle myStyle = theStyleMap.get(myStyleName);
        if (myStyle != null) {
            return myStyle;
        }

        /* Determine the CellStyleType */
        final MetisSheetCellStyleType myType = MetisSheetFormats.getCellStyleType(pValue);

        /* Create the New Cell Style */
        myStyle = theBook.createCellStyle();
        myStyle.setFont(getStyleFont(myType));
        myStyle.setAlignment(getStyleAlignment(myType));

        /* If we have a data format */
        if (myType != MetisSheetCellStyleType.BOOLEAN
            && MetisSheetFormats.hasDataFormat(myType)) {
            /* Determine the format */
            final String myFormat = MetisSheetFormats.getDataFormatString(pValue);
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
    protected XSSFCellStyle getAlternateCellStyle(final Object pValue) {
        /* Determine the correct format */
        final String myStyleName = MetisSheetFormats.getAlternateFormatName(pValue);

        /* Look for existing format */
        XSSFCellStyle myStyle = theStyleMap.get(myStyleName);
        if (myStyle != null) {
            return myStyle;
        }

        /* Create the New Cell Style */
        myStyle = theBook.createCellStyle();

        /* Determine the CellStyleType */
        MetisSheetCellStyleType myType = MetisSheetFormats.getCellStyleType(pValue);

        /* Handle the header style */
        if (myType == MetisSheetCellStyleType.STRING) {
            myType = MetisSheetCellStyleType.HEADER;
            myStyle.setLocked(true);
        }

        /* Set font and style */
        myStyle.setFont(getStyleFont(myType));
        myStyle.setAlignment(getStyleAlignment(myType));

        /* If we have a data format */
        if (myType != MetisSheetCellStyleType.BOOLEAN
            && MetisSheetFormats.hasDataFormat(myType)) {
            /* Determine the format */
            final String myFormat = MetisSheetFormats.getAlternateFormatString(pValue);
            myStyle.setDataFormat(theStyleEngine.getFormat(myFormat));
        }

        /* Add to the map and return new style */
        theStyleMap.put(myStyleName, myStyle);
        return myStyle;
    }
}
