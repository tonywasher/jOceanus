/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2019 Tony Washer
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
package net.sourceforge.joceanus.jtethys.test.ui;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;

/**
 * PieChart Data.
 */
public class TethysPieChartData {
    /**
     * The Chart Title.
     */
    private final String theTitle;

    /**
     * The SectionMap.
     */
    private final Map<String, TethysPieChartSection> theSectionMap;

    /**
     * Constructor.
     * @param pTitle the title
     */
    public TethysPieChartData(final String pTitle) {
        theTitle = pTitle;
        theSectionMap = new LinkedHashMap<>();
    }

    /**
     * Obtain the title.
     * @return the title.
     */
    public String getTitle() {
        return theTitle;
    }

    /**
     * Obtain the pieChart sections.
     * @return the iterator
     */
    public Iterator<TethysPieChartSection> sectionIterator() {
        return theSectionMap.values().iterator();
    }

    /**
     * Add a section.
     * @param pName the name
     * @param pValue the value
     */
    public void addSection(final String pName,
                           final TethysMoney pValue) {
        addSection(pName, pValue, pName);
    }

    /**
     * Add a section.
     * @param pName the name
     * @param pValue the value
     * @param pSource the source
     */
    public void addSection(final String pName,
                           final TethysMoney pValue,
                           final Object pSource) {
        final TethysPieChartSection mySection = new TethysPieChartSection(pName, pValue, pSource);
        theSectionMap.put(pName, mySection);
    }

    /**
     * Create Test Data.
     * @return the test data
     */
    public static TethysPieChartData createTestData() {
        final TethysPieChartData myData = new TethysPieChartData("Test PieChart");
        myData.addSection("Banking", TethysMoney.getWholeUnits(213));
        myData.addSection("Cash", TethysMoney.getWholeUnits(67));
        myData.addSection("Portfolios", TethysMoney.getWholeUnits(42));
        myData.addSection("Loans", TethysMoney.getWholeUnits(36));
        return myData;
    }

    /**
     * The Section definition.
     */
    public static final class TethysPieChartSection {
        /**
         * The name of the section.
         */
        private final String theName;

        /**
         * The value of the section.
         */
        private final TethysMoney theValue;

        /**
         * The source of the section.
         */
        private final Object theSource;

        /**
         * Constructor.
         * @param pName the name
         * @param pValue the value
         * @param pSource the source
         */
        private TethysPieChartSection(final String pName,
                                      final TethysMoney pValue,
                                      final Object pSource) {
            theName = pName;
            theValue = pValue;
            theSource = pSource;
        }

        /**
         * Obtain the name.
         * @return the name.
         */
        public String getName() {
            return theName;
        }

        /**
         * Obtain the value.
         * @return the value.
         */
        public TethysMoney getValue() {
            return theValue;
        }

        /**
         * Obtain the source.
         * @return the source.
         */
        public Object getSource() {
            return theSource;
        }
    }
}
