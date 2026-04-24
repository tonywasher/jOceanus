/*
 * Themis: Java Project Framework
 * Copyright 2026. Tony Washer
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

package io.github.tonywasher.joceanus.themis.gui.stats;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.themis.gui.base.ThemisUIBaseDocument;
import io.github.tonywasher.joceanus.themis.gui.base.ThemisUIHTMLTag;
import io.github.tonywasher.joceanus.themis.stats.ThemisStat;
import io.github.tonywasher.joceanus.themis.stats.ThemisStats;
import io.github.tonywasher.joceanus.themis.stats.ThemisStatsElement;
import org.w3c.dom.Element;

import java.util.Map;

/**
 * Document Builder for stats.
 */
public class ThemisUIStatsDocument
        extends ThemisUIBaseDocument {
    /**
     * Constructor.
     *
     * @throws OceanusException on error
     */
    ThemisUIStatsDocument() throws OceanusException {
        super();
    }

    /**
     * Create document for element.
     *
     * @param pElement the element
     * @return the formatted document
     */
    public String formatElement(final ThemisStatsElement pElement) {
        /* Create new document and obtain the body */
        final Element myBody = newDocument();

        /* Access the stats and create the tables */
        final ThemisStats myStats = pElement.getStats();
        myBody.appendChild(formatStats(myStats.getStats()));
        myBody.appendChild(formatStats(myStats.getTotals()));

        /* Return the formatted HTML */
        return formatXML();
    }

    /**
     * Create table for stats.
     *
     * @param pStats the stats
     * @return the stats element
     */
    private Element formatStats(final Map<ThemisStat, Number> pStats) {
        final Element myTable = createElement(ThemisUIHTMLTag.TABLE);
        addHeaderRow(myTable);
        for (ThemisStat myStat : ThemisStat.values()) {
            if (myStat.isInteger()) {
                final Integer myValue = (Integer) pStats.get(myStat);
                if (myValue != 0) {
                    addStatsRow(myTable, myStat, myValue);
                }
            } else {
                final Double myValue = (Double) pStats.get(myStat);
                if (myValue != 0) {
                    addStatsRow(myTable, myStat, myValue);
                }
            }
        }
        return myTable;
    }

    /**
     * Add header row for stats.
     *
     * @param pTable the table
     */
    private void addHeaderRow(final Element pTable) {
        final Element myHeaderRow = createElement(ThemisUIHTMLTag.TR);
        addHeaderCell(myHeaderRow, "Statistic");
        addHeaderCell(myHeaderRow, "Key");
        addHeaderCell(myHeaderRow, "Value");
        pTable.appendChild(myHeaderRow);
    }

    /**
     * Add header cell for stats.
     *
     * @param pHeaderRow the headerRow
     * @param pTitle     the header title
     */
    private void addHeaderCell(final Element pHeaderRow,
                               final String pTitle) {
        final Element myHeader = createElement(ThemisUIHTMLTag.TH);
        pHeaderRow.appendChild(myHeader);
        myHeader.setTextContent(pTitle);
    }

    /**
     * Add data row for integer stat.
     *
     * @param pTable the table
     * @param pStat  the stat
     * @param pValue the value
     */
    private void addStatsRow(final Element pTable,
                             final ThemisStat pStat,
                             final Integer pValue) {
        final Element myDataRow = createElement(ThemisUIHTMLTag.TR);
        addDataCell(myDataRow, pStat.getDesc());
        addDataCell(myDataRow, pStat.name());
        addDataCell(myDataRow, Integer.toString(pValue));
        pTable.appendChild(myDataRow);
    }

    /**
     * Add data row for integer stat.
     *
     * @param pTable the table
     * @param pStat  the stat
     * @param pValue the value
     */
    private void addStatsRow(final Element pTable,
                             final ThemisStat pStat,
                             final Double pValue) {
        final Element myDataRow = createElement(ThemisUIHTMLTag.TR);
        addDataCell(myDataRow, pStat.getDesc());
        addDataCell(myDataRow, pStat.name());
        addDataCell(myDataRow, Double.toString(pValue));
        pTable.appendChild(myDataRow);
    }

    /**
     * Add data cell for stat.
     *
     * @param pDataRow the dataRow
     * @param pData    the data
     */
    private void addDataCell(final Element pDataRow,
                             final String pData) {
        final Element myData = createElement(ThemisUIHTMLTag.TD);
        pDataRow.appendChild(myData);
        myData.setTextContent(pData);
    }
}
