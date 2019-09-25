/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2019 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.jprometheus.service.sheet.odf;

import net.sourceforge.joceanus.jprometheus.service.sheet.odf.MetisOdfNameSpace.MetisOdfItem;

/**
 * Table Item.
 */
public enum MetisOdfTableItem
    implements MetisOdfItem {
    /**
     * Table.
     */
    TABLE("table"),

    /**
     * Name.
     */
    NAME("name"),

    /**
     * StyleName.
     */
    STYLENAME("style-name"),

    /**
     * Column.
     */
    COLUMN("table-column"),

    /**
     * Columns.
     */
    COLUMNS("table-columns"),

    /**
     * HdrCols.
     */
    HDRCOLUMNS("table-header-columns"),

    /**
     * ColumnGroup.
     */
    COLUMNGROUP("table-column-group"),

    /**
     * DefaultCellStyle.
     */
    DEFAULTCELLSTYLE("default-cell-style-name"),

    /**
     * Visibility.
     */
    VISIBILITY("visibility"),

    /**
     * ColumnRepeat.
     */
    COLUMNREPEAT("number-columns-repeated"),

    /**
     * Row.
     */
    ROW("table-row"),

    /**
     * Rows.
     */
    ROWS("table-rows"),

    /**
     * HdrRows.
     */
    HDRROWS("table-header-rows"),

    /**
     * RowGroup.
     */
    ROWGROUP("table-row-group"),

    /**
     * RowRepeat.
     */
    ROWREPEAT("number-rows-repeated"),

    /**
     * Cell.
     */
    CELL("table-cell"),

    /**
     * Text.
     */
    TEXT("p"),

    /**
     * ValidationName.
     */
    VALIDATIONNAME("content-validation-name"),

    /**
     * Expressions.
     */
    EXPRESSIONS("named-expressions"),

    /**
     * Range.
     */
    RANGE("named-range"),

    /**
     * BaseCellAddress.
     */
    BASECELLADDRESS("base-cell-address"),

    /**
     * CellRangeAddress.
     */
    CELLRANGEADDRESS("cell-range-address"),

    /**
     * Validations.
     */
    VALIDATIONS("content-validations"),

    /**
     * Validation.
     */
    VALIDATION("content-validation"),

    /**
     * Condition.
     */
    CONDITION("condition"),

    /**
     * ErrorMsg.
     */
    ERRORMSG("error-message"),

    /**
     * Display.
     */
    DISPLAY("display"),

    /**
     * AllowEmptyCell.
     */
    ALLOWEMPTYCELL("allow-empty-cell"),

    /**
     * Database Ranges.
     */
    DATARANGES("database-ranges"),

    /**
     * Data Range.
     */
    DATARANGE("database-range"),

    /**
     * Target Range.
     */
    TARGETRANGE("target-range-address"),

    /**
     * DisplayFilter.
     */
    DISPLAYFILTER("display-filter-buttons"),

    /**
     * Orientation.
     */
    ORIENTATION("orientation");

    /**
     * Name.
     */
    private final String theName;

    /**
     * Qualified Name.
     */
    private String theQualifiedName;

    /**
     * Constructor.
     * @param pName the name
     */
    MetisOdfTableItem(final String pName) {
        theName = pName;
    }

    @Override
    public String getName() {
        return theName;
    }

    @Override
    public String getQualifiedName() {
        if (theQualifiedName == null) {
            theQualifiedName = MetisOdfNameSpace.buildQualifiedName(this);
        }
        return theQualifiedName;
    }

    @Override
    public MetisOdfNameSpace getNameSpace() {
        switch (this) {
            case TEXT:
                return MetisOdfNameSpace.TEXT;
            default:
                return MetisOdfNameSpace.TABLE;
        }
    }
}
