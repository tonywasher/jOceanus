/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2013 Tony Washer
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
package net.sourceforge.jOceanus.jMoneyWise.analysis;

import java.util.Map;

import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDateDay.JDateDayRange;
import net.sourceforge.jOceanus.jMoneyWise.data.FinanceData;
import net.sourceforge.jOceanus.jSortedList.NestedHashMap;

/**
 * Analysis manager.
 */
public class AnalysisManager {
    /**
     * The analysis map.
     */
    private final Map<JDateDayRange, Analysis> theMap;

    /**
     * The data.
     */
    private FinanceData theData = null;

    /**
     * Constructor.
     */
    protected AnalysisManager() {
        /* Create the map */
        theMap = new NestedHashMap<JDateDayRange, Analysis>();
    }

    /**
     * Obtain an analysis for a range.
     * @param pRange the date range for the analysis.
     * @return the analysis
     * @throws JDataException on error
     */
    protected Analysis getAnalysis(final JDateDayRange pRange) throws JDataException {
        /* Look for the existing analysis */
        Analysis myAnalysis = theMap.get(pRange);
        if (myAnalysis == null) {
            /* Create the new event analysis */
            // DataAnalyser myAnalyser = new DataAnalysis(theData, pRange);
            // myAnalysis = myAnalyser.getAnalysis();

            /* Save the analysis */
            // theMap.put(pRange, myAnalysis);
        }

        /* return the analysis */
        return myAnalysis;
    }

    /**
     * Record new data.
     * @param pData the new dataSet
     */
    protected void setNewData(final FinanceData pData) {
        /* Reset the map */
        theMap.clear();

        /* Store the data */
        theData = pData;
    }
}
