/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2020 Tony Washer
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

import net.sourceforge.joceanus.jprometheus.service.sheet.odf.PrometheusOdfNameSpace.PrometheusOdfItem;

/**
 * Style Item.
 */
public enum PrometheusOdfStyleItem
    implements PrometheusOdfItem {
    /**
     * Style.
     */
    STYLE("style"),

    /**
     * Name.
     */
    NAME("name"),

    /**
     * Family.
     */
    FAMILY("family"),

    /**
     * ParentStyle.
     */
    PARENTSTYLE("parent-style-name"),

    /**
     * DataStyle.
     */
    DATASTYLE("data-style-name"),

    /**
     * ColumnProps.
     */
    TABLEPROPS("table-properties"),

    /**
     * Display.
     */
    DISPLAY("display"),

    /**
     * BreakBefore.
     */
    BREAKBEFORE("break-before"),

    /**
     * TextAlign.
     */
    TEXTALIGN("text-align"),

    /**
     * ColumnProps.
     */
    COLUMNPROPS("table-column-properties"),

    /**
     * ColumnWidth.
     */
    COLUMNWIDTH("column-width"),

    /**
     * RowProps.
     */
    ROWPROPS("table-row-properties"),

    /**
     * RowHeight.
     */
    ROWHEIGHT("row-height"),

    /**
     * CellProps.
     */
    CELLPROPS("table-cell-properties"),

    /**
     * Protect.
     */
    PROTECT("cell-protect"),

    /**
     * ParaProps.
     */
    PARAGRAPHPROPS("paragraph-properties"),

    /**
     * TextProps.
     */
    TEXTPROPS("text-properties"),

    /**
     * Map.
     */
    MAP("map"),

    /**
     * ApplyStyle.
     */
    APPLYSTYLE("apply-style-name"),

    /**
     * Condition.
     */
    CONDITION("condition"),

    /**
     * Colour.
     */
    COLOR("color"),

    /**
     * FontName.
     */
    FONTNAME("font-name"),

    /**
     * FontSize.
     */
    FONTSIZE("font-size"),

    /**
     * FontSize.
     */
    FONTWEIGHT("font-weight");

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
    PrometheusOdfStyleItem(final String pName) {
        theName = pName;
    }

    @Override
    public String getName() {
        return theName;
    }

    @Override
    public String getQualifiedName() {
        if (theQualifiedName == null) {
            theQualifiedName = PrometheusOdfNameSpace.buildQualifiedName(this);
        }
        return theQualifiedName;
    }

    @Override
    public PrometheusOdfNameSpace getNameSpace() {
        switch (this) {
            case BREAKBEFORE:
            case TEXTALIGN:
            case COLOR:
                return PrometheusOdfNameSpace.FORMAT;
            default:
                return PrometheusOdfNameSpace.STYLE;
        }
    }
}
