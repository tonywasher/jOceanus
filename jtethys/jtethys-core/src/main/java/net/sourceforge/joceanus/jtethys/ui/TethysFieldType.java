/*******************************************************************************
 * jTethys: Java Utilities
 * Copyright 2012,2014 Tony Washer
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
 * $URL: http://localhost/svn/Finance/JDateButton/trunk/jdatebutton-javafx/src/main/java/net/sourceforge/jdatebutton/javafx/ArrowIcon.java $
 * $Revision: 573 $
 * $Author: Tony $
 * $Date: 2015-03-03 17:54:12 +0000 (Tue, 03 Mar 2015) $
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
     * Obtain default width.
     * @return the default width
     */
    public int getDefaultWidth() {
        switch (this) {
            case STRING:
            case SCROLL:
            case LIST:
                return 200;
            case DATE:
            case MONEY:
                return 100;
            case PRICE:
            case RATE:
            case UNITS:
            case DILUTION:
            case DILUTEDPRICE:
            case RATIO:
                return 90;
            case INTEGER:
            case SHORT:
            case LONG:
                return 50;
            case ICON:
            case STATEICON:
            default:
                return 20;
        }
    }

    /**
     * Date column standard width.
     */
    protected static final int WIDTH_DATE = 100;

    /**
     * Money column standard width.
     */
    protected static final int WIDTH_MONEY = 100;

    /**
     * Rate column standard width.
     */
    protected static final int WIDTH_RATE = 90;

    /**
     * Price column standard width.
     */
    protected static final int WIDTH_PRICE = 90;

    /**
     * Units column standard width.
     */
    protected static final int WIDTH_UNITS = 90;

    /**
     * Dilution column standard width.
     */
    protected static final int WIDTH_DILUTION = 90;

    /**
     * Name column standard width.
     */
    protected static final int WIDTH_NAME = 130;

    /**
     * Description column standard width.
     */
    protected static final int WIDTH_DESC = 200;

    /**
     * Icon column width.
     */
    protected static final int WIDTH_ICON = 20;

    /**
     * Integer column width.
     */
    protected static final int WIDTH_INT = 30;

    /**
     * Currency column width.
     */
    protected static final int WIDTH_CURR = 50;

}
