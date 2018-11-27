/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012, 2018 Tony Washer
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
package net.sourceforge.joceanus.jmetis.service.sheet.odf;

import org.w3c.dom.Element;

import net.sourceforge.joceanus.jmetis.service.sheet.MetisSheetCellStyleType;
import net.sourceforge.joceanus.jmetis.service.sheet.MetisSheetColumn;

/**
 * Class representing a column in Oasis.
 * @author Tony Washer
 */
public class MetisOdfColumn
        extends MetisSheetColumn {
    /**
     * The Parser.
     */
    private final MetisOdfParser theParser;

    /**
     * The map of columns.
     */
    private final MetisOdfColumnMap theColumnMap;

    /**
     * The underlying ODFDOM column.
     */
    private final Element theOasisColumn;

    /**
     * Is the column readOnly.
     */
    private final boolean isReadOnly;

    /**
     * Constructor.
     * @param pMap the column map
     * @param pColumn the Oasis column
     * @param pIndex the index
     * @param pReadOnly is the column readOnly?
     */
    MetisOdfColumn(final MetisOdfColumnMap pMap,
                   final Element pColumn,
                   final int pIndex,
                   final boolean pReadOnly) {
        /* Store parameters */
        super(pMap.getSheet(), pIndex);
        theParser = getSheet().getParser();
        theColumnMap = pMap;
        theOasisColumn = pColumn;
        isReadOnly = pReadOnly;
    }

    @Override
    public MetisOdfSheet getSheet() {
        return (MetisOdfSheet) super.getSheet();
    }

    @Override
    public MetisOdfColumn getNextColumn() {
        return theColumnMap.getReadOnlyColumnByIndex(getColumnIndex() + 1);
    }

    @Override
    public MetisOdfColumn getPreviousColumn() {
        return theColumnMap.getReadOnlyColumnByIndex(getColumnIndex() - 1);
    }

    /**
     * Obtain the column style name.
     * @return the column style name
     */
    protected String getColumnStyle() {
        return theParser.getAttribute(theOasisColumn, MetisOdfTableItem.STYLENAME);
    }

    @Override
    public boolean isHidden() {
        final String myString = theParser.getAttribute(theOasisColumn, MetisOdfTableItem.VISIBILITY);
        return myString != null
                && !myString.equals(MetisOdfValue.VISIBLE.getValue());
    }

    @Override
    public void setDefaultCellStyle(final MetisSheetCellStyleType pStyle) {
        /* Ignore if readOnly */
        if (!isReadOnly) {
            /* Set the default cell style and the column style */
            final MetisOdfSheet mySheet = theColumnMap.getSheet();
            mySheet.setColumnStyle(theOasisColumn, pStyle);
            mySheet.setDefaultCellStyle(theOasisColumn, pStyle);
        }
    }

    @Override
    public void setHidden(final boolean isHidden) {
        /* Ignore if readOnly */
        if (!isReadOnly) {
            /* Set the visibility attribute */
            theParser.setAttribute(theOasisColumn, MetisOdfTableItem.VISIBILITY,
                    isHidden
                    ? MetisOdfValue.COLLAPSE
                    : MetisOdfValue.VISIBLE);
        }
    }
}
