/*******************************************************************************
 MetisOdfColumn * Copyright 2012,2019 Tony Washer
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
 * Number element.
 */
public enum MetisOdfNumberItem
        implements MetisOdfItem {
    /**
     * Text.
     */
    TEXT("text"),

    /**
     * Style.
     */
    STYLE("style"),

    /**
     * NumberStyle.
     */
    NUMBERSTYLE("number-style"),

    /**
     * DateStyle.
     */
    DATESTYLE("date-style"),

    /**
     * Textual.
     */
    TEXTUAL("textual"),

    /**
     * Day.
     */
    DAY("day"),

    /**
     * Month.
     */
    MONTH("month"),

    /**
     * Year.
     */
    YEAR("year"),

    /**
     * BooleanStyle.
     */
    BOOLEANSTYLE("boolean-style"),

    /**
     * BooleanValue.
     */
    BOOLEANVALUE("boolean-value"),

    /**
     * Boolean.
     */
    BOOLEAN("boolean"),

    /**
     * Number.
     */
    NUMBER("number"),

    /**
     * Grouping.
     */
    GROUPING("grouping"),

    /**
     * MinIntDigits.
     */
    MININTDIGITS("min-integer-digits"),

    /**
     * DecPlaces.
     */
    DECPLACES("decimal-places"),

    /**
     * Styles.
     */
    PERCENTAGESTYLE("percentage-style");

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
    MetisOdfNumberItem(final String pName) {
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
       return MetisOdfNameSpace.NUMBER;
    }
}

