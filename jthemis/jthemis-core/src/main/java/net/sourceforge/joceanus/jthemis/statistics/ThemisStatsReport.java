/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012,2020 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.jthemis.statistics;

import java.util.Map;
import javax.swing.text.html.HTML.Tag;

import net.sourceforge.joceanus.jthemis.sourcemeter.ThemisSMStat;

/**
 * DSM report.
 */
public final class ThemisStatsReport {
    /**
     * Private constructor.
     */
    private ThemisStatsReport() {
    }

    /**
     * report on a stats element.
     * @param pStats the stats to report on
     * @return the report
     */
    public static String reportOnStats(final ThemisStatsBase pStats) {
        /* Create a stringBuilder */
        final StringBuilder myBuilder = new StringBuilder();

        /* Start document */
        addStartElement(myBuilder, Tag.HTML);
        addStartElement(myBuilder, Tag.BODY);

        /* Build id table */
        addStartElement(myBuilder, Tag.TABLE);
        buildIdTableHeader(myBuilder);

        /* Loop through the identity chain */
        ThemisStatsBase myId = pStats;
        int myRowNo = 0;
        while (myId != null) {
            buildIdTableRow(myBuilder, myId, myRowNo++);
            myId = myId.getParent();
        }

        /* Finish the table */
        addEndElement(myBuilder, Tag.TABLE);

        addStartElement(myBuilder, Tag.HR);
        addEndElement(myBuilder, Tag.HR);

        /* Build stats table */
        addStartElement(myBuilder, Tag.TABLE);
        buildStatsTableHeader(myBuilder);

        /* Access the sourceMeter statistics */
        final Map<ThemisSMStat, Integer> myMap = pStats.getSourceMeterStats();

        /* Loop through the statistics */
        myRowNo = 0;
        for (ThemisSMStat myStat : ThemisSMStat.values()) {
            final Integer myValue = pStats.getStat(myStat);
            final Integer mySMValue = myMap != null ? myMap.computeIfAbsent(myStat, s -> 0) : null;
            if (myValue != 0 || mySMValue != null && mySMValue != 0) {
                buildStatsTableRow(myBuilder, myStat, myValue, mySMValue, myRowNo++);
            }
        }

        /* Finish the table */
        addEndElement(myBuilder, Tag.TABLE);

        /* Finish document */
        addEndElement(myBuilder, Tag.BODY);
        addEndElement(myBuilder, Tag.HTML);
        return myBuilder.toString();
    }

    /**
     * build the id table header.
     * @param pBuilder the builder
     */
    private static void buildIdTableHeader(final StringBuilder pBuilder) {
        /* Start the row */
        addStartElementWithClass(pBuilder, Tag.TR, "dsm-row-header");

        /* Build the headers */
        addTextElementWithClass(pBuilder, Tag.TH, "stats-name", "Element");
        addTextElementWithClass(pBuilder, Tag.TH, "stats-desc", "Name");

        /* Complete the row */
        addEndElement(pBuilder, Tag.TR);
    }

    /**
     * build a row for the id table.
     * @param pBuilder the builder
     * @param pIdentity the identity to report on
     * @param pRowNo the row number
     */
    private static void buildIdTableRow(final StringBuilder pBuilder,
                                        final ThemisStatsBase pIdentity,
                                        final int pRowNo) {
        /* Start the row */
        final String myClass = (pRowNo % 2 == 0) ? "dsm-row-even" : "dsm-row-odd";
        addStartElementWithClass(pBuilder, Tag.TR, myClass);

        /* Build the cells */
        addTextElementWithClass(pBuilder, Tag.TD, "dsm-cell-name-left", getIdElement(pIdentity));
        addTextElementWithClass(pBuilder, Tag.TD, "dsm-cell-name-left", getIdName(pIdentity));

        /* Complete the row */
        addEndElement(pBuilder, Tag.TR);
    }

    /**
     * get the id element.
     * @param pIdentity the identity to report on
     * @return the element
     */
    private static String getIdElement(final ThemisStatsBase pIdentity) {
        if (pIdentity instanceof ThemisStatsProject) {
            return "Project";
        }
        if (pIdentity instanceof ThemisStatsModule) {
            return "Module";
        }
        if (pIdentity instanceof ThemisStatsPackage) {
            return "Package";
        }
        if (pIdentity instanceof ThemisStatsFile) {
            return "File";
        }
        if (pIdentity instanceof ThemisStatsClass) {
            return ((ThemisStatsClass) pIdentity).getClassType().toString();
        }
        if (pIdentity instanceof ThemisStatsMethod) {
            return "Method";
        }
        return "Unknown";
    }

    /**
     * get the id name.
     * @param pIdentity the identity to report on
     * @return the name
     */
    private static String getIdName(final ThemisStatsBase pIdentity) {
        if (pIdentity instanceof ThemisStatsProject) {
            return ((ThemisStatsProject) pIdentity).getProject().getName();
        }
        if (pIdentity instanceof ThemisStatsModule) {
            return ((ThemisStatsModule) pIdentity).getModule().getName();
        }
        if (pIdentity instanceof ThemisStatsPackage) {
            return ((ThemisStatsPackage) pIdentity).getPackage().getPackage();
        }
        if (pIdentity instanceof ThemisStatsFile) {
            return ((ThemisStatsFile) pIdentity).getFile().getName();
        }
        if (pIdentity instanceof ThemisStatsClass) {
            return ((ThemisStatsClass) pIdentity).getObject().getFullName();
        }
        if (pIdentity instanceof ThemisStatsMethod) {
            return ((ThemisStatsMethod) pIdentity).getMethod().toString();
        }
        return "Unknown";
    }

    /**
     * build the stats table header.
     * @param pBuilder the builder
     */
    private static void buildStatsTableHeader(final StringBuilder pBuilder) {
        /* Start the row */
        addStartElementWithClass(pBuilder, Tag.TR, "dsm-row-header");

        /* Build the headers */
        addTextElementWithClass(pBuilder, Tag.TH, "stats-desc", "Statistic");
        addTextElementWithClass(pBuilder, Tag.TH, "stats-name", "Key");
        addTextElementWithClass(pBuilder, Tag.TH, "stats-count", "Value");
        addTextElementWithClass(pBuilder, Tag.TH, "stats-count", "SourceMeter");

        /* Complete the row */
        addEndElement(pBuilder, Tag.TR);
    }

    /**
     * build a row for the stats table.
     * @param pBuilder the builder
     * @param pStat the statistic to report on
     * @param pValue the value
     * @param pSMValue the sourceMeter value
     * @param pRowNo the row number
     */
    private static void buildStatsTableRow(final StringBuilder pBuilder,
                                           final ThemisSMStat pStat,
                                           final Integer pValue,
                                           final Integer pSMValue,
                                           final int pRowNo) {
        /* Start the row */
        final String myClass = (pRowNo % 2 == 0) ? "dsm-row-even" : "dsm-row-odd";
        addStartElementWithClass(pBuilder, Tag.TR, myClass);

        /* Build the cells */
        addTextElementWithClass(pBuilder, Tag.TD, "dsm-cell-name-left", pStat.getDesc());
        addTextElement(pBuilder, Tag.TD, pStat.toString());
        addTextElement(pBuilder, Tag.TD, Integer.toString(pValue));
        addTextElement(pBuilder, Tag.TD, pSMValue == null ? "" : Integer.toString(pSMValue));

        /* Complete the row */
        addEndElement(pBuilder, Tag.TR);
    }

    /**
     * Add text element.
     * @param pBuilder the builder
     * @param pElement the element
     * @param pText the text
     */
    private static void addTextElement(final StringBuilder pBuilder,
                                       final Tag pElement,
                                       final String pText) {
        /* Add the text element */
        addStartElement(pBuilder, pElement);
        pBuilder.append(pText);
        addEndElement(pBuilder, pElement);
    }

    /**
     * Add text element with Class.
     * @param pBuilder the builder
     * @param pElement the element
     * @param pClass the class of the element
     * @param pText the text
     */
    private static void addTextElementWithClass(final StringBuilder pBuilder,
                                                final Tag pElement,
                                                final String pClass,
                                                final String pText) {
        /* Add the text element */
        addStartElementWithClass(pBuilder, pElement, pClass);
        pBuilder.append(pText);
        addEndElement(pBuilder, pElement);
    }

    /**
     * Add start element with class.
     * @param pBuilder the builder
     * @param pElement the element
     * @param pClass the class of the element
     */
    private static void addStartElementWithClass(final StringBuilder pBuilder,
                                                 final Tag pElement,
                                                 final String pClass) {
        /* Start the element */
        pBuilder.append("<");
        pBuilder.append(pElement);
        pBuilder.append(" class=\"");
        pBuilder.append(pClass);
        pBuilder.append("\">");
    }

    /**
     * Add start element.
     * @param pBuilder the builder
     * @param pElement the element
     */
    private static void addStartElement(final StringBuilder pBuilder,
                                        final Tag pElement) {
        /* Start the element */
        pBuilder.append("<");
        pBuilder.append(pElement);
        pBuilder.append(">");
    }

    /**
     * Add end element.
     * @param pBuilder the builder
     * @param pElement the element
     */
    private static void addEndElement(final StringBuilder pBuilder,
                                      final Tag pElement) {
        /* Start the element */
        pBuilder.append("</");
        pBuilder.append(pElement);
        pBuilder.append(">");
    }
}

