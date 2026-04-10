/*
 * Themis: Java Project Framework
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
package io.github.tonywasher.joceanus.themis.lethe.statistics;

import io.github.tonywasher.joceanus.themis.xanalysis.gui.base.ThemisXAnalysisUIHTMLTag;

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
     *
     * @param pStats the stats to report on
     * @return the report
     */
    public static String reportOnStats(final ThemisStatsBase pStats) {
        /* Create a stringBuilder */
        final StringBuilder myBuilder = new StringBuilder();

        /* Start document */
        addStartElement(myBuilder, ThemisXAnalysisUIHTMLTag.HTML);
        addStartElement(myBuilder, ThemisXAnalysisUIHTMLTag.BODY);

        /* Build id table */
        addStartElement(myBuilder, ThemisXAnalysisUIHTMLTag.TABLE);
        buildIdTableHeader(myBuilder);

        /* Loop through the identity chain */
        ThemisStatsBase myId = pStats;
        int myRowNo = 0;
        while (myId != null) {
            buildIdTableRow(myBuilder, myId, myRowNo++);
            myId = myId.getParent();
        }

        /* Finish the table */
        addEndElement(myBuilder, ThemisXAnalysisUIHTMLTag.TABLE);

        addStartElement(myBuilder, ThemisXAnalysisUIHTMLTag.HR);
        addEndElement(myBuilder, ThemisXAnalysisUIHTMLTag.HR);

        /* Build stats table */
        addStartElement(myBuilder, ThemisXAnalysisUIHTMLTag.TABLE);
        buildStatsTableHeader(myBuilder);

        /* If we have statistics */
        if (pStats != null) {
            /* Loop through the statistics */
            myRowNo = 0;
            for (ThemisStat myStat : ThemisStat.values()) {
                final Integer myValue = pStats.getStat(myStat);
                if (myValue != 0) {
                    buildStatsTableRow(myBuilder, myStat, myValue, myRowNo++);
                }
            }
        }

        /* Finish the table */
        addEndElement(myBuilder, ThemisXAnalysisUIHTMLTag.TABLE);

        /* Finish document */
        addEndElement(myBuilder, ThemisXAnalysisUIHTMLTag.BODY);
        addEndElement(myBuilder, ThemisXAnalysisUIHTMLTag.HTML);
        return myBuilder.toString();
    }

    /**
     * build the id table header.
     *
     * @param pBuilder the builder
     */
    private static void buildIdTableHeader(final StringBuilder pBuilder) {
        /* Start the row */
        addStartElementWithClass(pBuilder, ThemisXAnalysisUIHTMLTag.TR, "dsm-row-header");

        /* Build the headers */
        addTextElementWithClass(pBuilder, ThemisXAnalysisUIHTMLTag.TH, "stats-name", "Element");
        addTextElementWithClass(pBuilder, ThemisXAnalysisUIHTMLTag.TH, "stats-desc", "Name");

        /* Complete the row */
        addEndElement(pBuilder, ThemisXAnalysisUIHTMLTag.TR);
    }

    /**
     * build a row for the id table.
     *
     * @param pBuilder  the builder
     * @param pIdentity the identity to report on
     * @param pRowNo    the row number
     */
    private static void buildIdTableRow(final StringBuilder pBuilder,
                                        final ThemisStatsBase pIdentity,
                                        final int pRowNo) {
        /* Start the row */
        final String myClass = (pRowNo % 2 == 0) ? "dsm-row-even" : "dsm-row-odd";
        addStartElementWithClass(pBuilder, ThemisXAnalysisUIHTMLTag.TR, myClass);

        /* Build the cells */
        addTextElementWithClass(pBuilder, ThemisXAnalysisUIHTMLTag.TD, "dsm-cell-name-left", getIdElement(pIdentity));
        addTextElementWithClass(pBuilder, ThemisXAnalysisUIHTMLTag.TD, "dsm-cell-name-left", getIdName(pIdentity));

        /* Complete the row */
        addEndElement(pBuilder, ThemisXAnalysisUIHTMLTag.TR);
    }

    /**
     * get the id element.
     *
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
        if (pIdentity instanceof ThemisStatsClass myClass) {
            return myClass.getClassType().toString();
        }
        if (pIdentity instanceof ThemisStatsMethod) {
            return "Method";
        }
        return "Unknown";
    }

    /**
     * get the id name.
     *
     * @param pIdentity the identity to report on
     * @return the name
     */
    private static String getIdName(final ThemisStatsBase pIdentity) {
        if (pIdentity instanceof ThemisStatsProject myProject) {
            return myProject.getProject().getName();
        }
        if (pIdentity instanceof ThemisStatsModule myModule) {
            return myModule.getModule().getName();
        }
        if (pIdentity instanceof ThemisStatsPackage myPackage) {
            return myPackage.getPackage().getPackage();
        }
        if (pIdentity instanceof ThemisStatsFile myFile) {
            return myFile.getFile().getName();
        }
        if (pIdentity instanceof ThemisStatsClass myClass) {
            return myClass.getObject().getFullName();
        }
        if (pIdentity instanceof ThemisStatsMethod myMethod) {
            return myMethod.getMethod().toString();
        }
        return "Unknown";
    }

    /**
     * build the stats table header.
     *
     * @param pBuilder the builder
     */
    private static void buildStatsTableHeader(final StringBuilder pBuilder) {
        /* Start the row */
        addStartElementWithClass(pBuilder, ThemisXAnalysisUIHTMLTag.TR, "dsm-row-header");

        /* Build the headers */
        addTextElementWithClass(pBuilder, ThemisXAnalysisUIHTMLTag.TH, "stats-desc", "Statistic");
        addTextElementWithClass(pBuilder, ThemisXAnalysisUIHTMLTag.TH, "stats-name", "Key");
        addTextElementWithClass(pBuilder, ThemisXAnalysisUIHTMLTag.TH, "stats-count", "Value");

        /* Complete the row */
        addEndElement(pBuilder, ThemisXAnalysisUIHTMLTag.TR);
    }

    /**
     * build a row for the stats table.
     *
     * @param pBuilder the builder
     * @param pStat    the statistic to report on
     * @param pValue   the value
     * @param pRowNo   the row number
     */
    private static void buildStatsTableRow(final StringBuilder pBuilder,
                                           final ThemisStat pStat,
                                           final Integer pValue,
                                           final int pRowNo) {
        /* Start the row */
        final String myClass = (pRowNo % 2 == 0) ? "dsm-row-even" : "dsm-row-odd";
        addStartElementWithClass(pBuilder, ThemisXAnalysisUIHTMLTag.TR, myClass);

        /* Build the cells */
        addTextElementWithClass(pBuilder, ThemisXAnalysisUIHTMLTag.TD, "dsm-cell-name-left", pStat.getDesc());
        addTextElement(pBuilder, ThemisXAnalysisUIHTMLTag.TD, pStat.toString());
        addTextElement(pBuilder, ThemisXAnalysisUIHTMLTag.TD, Integer.toString(pValue));

        /* Complete the row */
        addEndElement(pBuilder, ThemisXAnalysisUIHTMLTag.TR);
    }

    /**
     * Add text element.
     *
     * @param pBuilder the builder
     * @param pElement the element
     * @param pText    the text
     */
    private static void addTextElement(final StringBuilder pBuilder,
                                       final ThemisXAnalysisUIHTMLTag pElement,
                                       final String pText) {
        /* Add the text element */
        addStartElement(pBuilder, pElement);
        pBuilder.append(pText);
        addEndElement(pBuilder, pElement);
    }

    /**
     * Add text element with Class.
     *
     * @param pBuilder the builder
     * @param pElement the element
     * @param pClass   the class of the element
     * @param pText    the text
     */
    private static void addTextElementWithClass(final StringBuilder pBuilder,
                                                final ThemisXAnalysisUIHTMLTag pElement,
                                                final String pClass,
                                                final String pText) {
        /* Add the text element */
        addStartElementWithClass(pBuilder, pElement, pClass);
        pBuilder.append(pText);
        addEndElement(pBuilder, pElement);
    }

    /**
     * Add start element with class.
     *
     * @param pBuilder the builder
     * @param pElement the element
     * @param pClass   the class of the element
     */
    private static void addStartElementWithClass(final StringBuilder pBuilder,
                                                 final ThemisXAnalysisUIHTMLTag pElement,
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
     *
     * @param pBuilder the builder
     * @param pElement the element
     */
    private static void addStartElement(final StringBuilder pBuilder,
                                        final ThemisXAnalysisUIHTMLTag pElement) {
        /* Start the element */
        pBuilder.append("<");
        pBuilder.append(pElement);
        pBuilder.append(">");
    }

    /**
     * Add end element.
     *
     * @param pBuilder the builder
     * @param pElement the element
     */
    private static void addEndElement(final StringBuilder pBuilder,
                                      final ThemisXAnalysisUIHTMLTag pElement) {
        /* Start the element */
        pBuilder.append("</");
        pBuilder.append(pElement);
        pBuilder.append(">");
    }
}

