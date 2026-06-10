/*
 * Prometheus: Application Framework
 * Copyright 2012-2026. Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package io.github.tonywasher.joceanus.prometheus.service.sheet.hssf;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.oceanus.date.OceanusDate;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusDecimal;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusMoney;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusPrice;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusRate;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusRatio;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusUnits;
import io.github.tonywasher.joceanus.prometheus.service.sheet.PrometheusSheetCellAddress;
import io.github.tonywasher.joceanus.prometheus.service.sheet.PrometheusSheetException;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;

import java.sql.Date;
import java.time.Instant;
import java.time.ZoneId;

/**
 * Class representing a cell within a sheet or a view.
 */
public class PrometheusExcelHSSFCell
        extends PrometheusExcelCell {
    /**
     * The underlying row.
     */
    private final PrometheusExcelRow theExcelRow;

    /**
     * The Excel Cell.
     */
    private final HSSFCell theExcelCell;

    /**
     * Constructor.
     *
     * @param pRow       the row for the cell
     * @param pExcelCell the Excel Cell
     * @param pColIndex  the column index
     * @param pReadOnly  is the cell readOnly?
     */
    PrometheusExcelHSSFCell(final PrometheusExcelRow pRow,
                            final HSSFCell pExcelCell,
                            final int pColIndex,
                            final boolean pReadOnly) {
        /* Store parameters */
        super(pRow, pColIndex, pReadOnly);
        theExcelRow = pRow;
        theExcelCell = pExcelCell;
    }

    /**
     * Parse a value.
     *
     * @param <T>     the value type to parse
     * @param pSource the string to parse.
     * @param pClass  the value type class.
     * @return the parsed value
     * @throws OceanusException on error
     */
    private <T> T parseValue(final String pSource,
                             final Class<T> pClass) throws OceanusException {
        try {
            return theExcelRow.parseValue(pSource, pClass);
        } catch (IllegalArgumentException e) {
            final PrometheusSheetCellAddress myAddress = new PrometheusSheetCellAddress(theExcelRow.getSheet().getName(), getPosition());
            throw new PrometheusSheetException(pSource, "Bad Value at Cell "
                    + myAddress, e);
        }
    }

    @Override
    public Boolean getBoolean() {
        return switch (theExcelCell.getCellType()) {
            case BOOLEAN -> theExcelCell.getBooleanCellValue();
            case NUMERIC -> theExcelCell.getNumericCellValue() != 0;
            case FORMULA -> {
                final CellValue myValue = theExcelRow.evaluateFormula(theExcelCell);
                yield CellType.BOOLEAN == myValue.getCellType()
                        ? myValue.getBooleanValue()
                        : null;
            }
            default -> null;
        };
    }

    @Override
    public OceanusDate getDate() {
        if (CellType.NUMERIC == theExcelCell.getCellType()) {
            final Instant myDate = theExcelCell.getDateCellValue().toInstant();
            return new OceanusDate(myDate.atZone(ZoneId.systemDefault()).toLocalDate());
        }
        return null;
    }

    @Override
    public Integer getInteger() {
        return switch (theExcelCell.getCellType()) {
            case NUMERIC -> {
                final Double myValue = theExcelCell.getNumericCellValue();
                yield myValue.intValue();
            }
            case FORMULA -> {
                final CellValue myCellValue = theExcelRow.evaluateFormula(theExcelCell);
                yield CellType.NUMERIC == myCellValue.getCellType()
                        ? ((Double) myCellValue.getNumberValue()).intValue()
                        : null;
            }
            default -> null;
        };
    }

    @Override
    public Long getLong() {
        return switch (theExcelCell.getCellType()) {
            case NUMERIC -> {
                final Double myValue = theExcelCell.getNumericCellValue();
                yield myValue.longValue();
            }
            case FORMULA -> {
                final CellValue myCellValue = theExcelRow.evaluateFormula(theExcelCell);
                yield CellType.NUMERIC == myCellValue.getCellType()
                        ? ((Double) myCellValue.getNumberValue()).longValue()
                        : null;
            }
            default -> null;
        };
    }

    @Override
    public String getString() {
        return switch (theExcelCell.getCellType()) {
            case NUMERIC, BOOLEAN -> theExcelRow.formatCellValue(theExcelCell);
            case FORMULA -> getStringFormulaValue();
            default -> theExcelCell.getStringCellValue();
        };
    }

    /**
     * Resolve the formula value as string.
     *
     * @return the resolved value
     */
    private String getStringFormulaValue() {
        final CellValue myValue = theExcelRow.evaluateFormula(theExcelCell);
        return switch (myValue.getCellType()) {
            case STRING, NUMERIC, BOOLEAN -> myValue.formatAsString();
            default -> null;
        };
    }

    @Override
    public OceanusMoney getMoney() throws OceanusException {
        return parseValue(getString(), OceanusMoney.class);
    }

    @Override
    public OceanusPrice getPrice() throws OceanusException {
        return parseValue(getString(), OceanusPrice.class);
    }

    @Override
    public OceanusRate getRate() throws OceanusException {
        return parseValue(getString(), OceanusRate.class);
    }

    @Override
    public OceanusUnits getUnits() throws OceanusException {
        return parseValue(getString(), OceanusUnits.class);
    }

    @Override
    public OceanusRatio getRatio() throws OceanusException {
        return parseValue(getString(), OceanusRatio.class);
    }

    @Override
    protected void setNullValue() {
        theExcelCell.setCellValue((String) null);
    }

    @Override
    protected void setBooleanValue(final Boolean pValue) {
        /* Set the value */
        theExcelCell.setCellValue(pValue);

        /* Set the style for the cell */
        theExcelRow.setCellStyle(this, pValue);
    }

    @Override
    protected void setDateValue(final OceanusDate pValue) {
        /* Set the value */
        theExcelCell.setCellValue(Date.valueOf(pValue.getDate()));

        /* Set the style for the cell */
        theExcelRow.setCellStyle(this, pValue);
    }

    @Override
    protected void setIntegerValue(final Integer pValue) {
        /* Set the value */
        theExcelCell.setCellValue(pValue.doubleValue());

        /* Set the style for the cell */
        theExcelRow.setCellStyle(this, pValue);
    }

    @Override
    protected void setLongValue(final Long pValue) {
        /* Set the value */
        theExcelCell.setCellValue(pValue.doubleValue());

        /* Set the style for the cell */
        theExcelRow.setCellStyle(this, pValue);
    }

    @Override
    protected void setStringValue(final String pValue) {
        /* Set the value */
        theExcelCell.setCellValue(pValue);

        /* Set the style for the cell */
        theExcelRow.setCellStyle(this, pValue);
    }

    @Override
    protected void setHeaderValue(final String pValue) {
        /* Set as string value */
        theExcelCell.setCellValue(pValue);

        /* Adjust the style for the cell */
        theExcelRow.setAlternateCellStyle(this, pValue);
    }

    @Override
    protected void setDecimalValue(final OceanusDecimal pValue) {
        /* Set the value */
        theExcelCell.setCellValue(pValue.doubleValue());

        /* Set the style for the cell */
        theExcelRow.setCellStyle(this, pValue);
    }

    @Override
    protected void setMonetaryValue(final OceanusMoney pValue) {
        /* Pass through as decimal */
        setDecimalValue(pValue);
    }

    @Override
    void setCellStyle(final HSSFCellStyle pStyle) {
        theExcelCell.setCellStyle(pStyle);
    }
}
