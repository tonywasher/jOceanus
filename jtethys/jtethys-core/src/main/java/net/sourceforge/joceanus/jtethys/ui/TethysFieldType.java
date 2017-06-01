/*******************************************************************************
 * jTethys: Java Utilities
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
package net.sourceforge.joceanus.jtethys.ui;

/**
 * FieldType.
 */
public enum TethysFieldType {
    /**
     * String.
     */
    STRING,

    /**
     * Character Array.
     */
    CHARARRAY,

    /**
     * Short.
     */
    SHORT,

    /**
     * Integer.
     */
    INTEGER,

    /**
     * Long.
     */
    LONG,

    /**
     * Decimal.
     */
    DECIMAL,

    /**
     * Money.
     */
    MONEY,

    /**
     * Rate.
     */
    RATE,

    /**
     * Units.
     */
    UNITS,

    /**
     * Price.
     */
    PRICE,

    /**
     * Dilution.
     */
    DILUTION,

    /**
     * Ratio.
     */
    RATIO,

    /**
     * DilutedPrice.
     */
    DILUTEDPRICE,

    /**
     * Date.
     */
    DATE,

    /**
     * Icon.
     */
    ICON,

    /**
     * StateIcon.
     */
    STATEICON,

    /**
     * Scroll.
     */
    SCROLL,

    /**
     * List.
     */
    LIST;

    /**
     * Date column standard width.
     */
    private static final int WIDTH_DATE = 130;

    /**
     * Money column standard width.
     */
    private static final int WIDTH_MONEY = 110;

    /**
     * Decimal column standard width.
     */
    private static final int WIDTH_DECIMAL = 90;

    /**
     * Description column standard width.
     */
    private static final int WIDTH_DESC = 200;

    /**
     * Icon column width.
     */
    private static final int WIDTH_ICON = 30;

    /**
     * Integer column width.
     */
    private static final int WIDTH_INT = 50;

    /**
     * Obtain default width.
     * @return the default width
     */
    public int getDefaultWidth() {
        switch (this) {
            case STRING:
            case SCROLL:
            case LIST:
                return WIDTH_DESC;
            case DATE:
                return WIDTH_DATE;
            case MONEY:
            case PRICE:
            case DILUTEDPRICE:
                return WIDTH_MONEY;
            case RATE:
            case UNITS:
            case DILUTION:
            case RATIO:
            case DECIMAL:
                return WIDTH_DECIMAL;
            case INTEGER:
            case SHORT:
            case LONG:
                return WIDTH_INT;
            case ICON:
            case STATEICON:
            default:
                return WIDTH_ICON;
        }
    }
}
