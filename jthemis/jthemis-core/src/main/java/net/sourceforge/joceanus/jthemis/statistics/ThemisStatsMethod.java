/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012,2022 Tony Washer
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisKeyWord;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisMethod;
import net.sourceforge.joceanus.jthemis.sourcemeter.ThemisSMMethod;
import net.sourceforge.joceanus.jthemis.sourcemeter.ThemisSMStat;

/**
 * Method statistics.
 */
public class ThemisStatsMethod
        extends ThemisStatsBase {
    /**
     * The class.
     */
    private final ThemisAnalysisMethod theMethod;

    /**
     * The sourceMeter class Stats.
     */
    private final ThemisSMMethod theSMMethod;

    /**
     * The child list.
     */
    private final List<ThemisStatsBase> theChildren;

    /**
     * Constructor.
     * @param pMethod the method
     * @param pSourceMeter the sourceMeter stats
     */
    ThemisStatsMethod(final ThemisAnalysisMethod pMethod,
                      final ThemisSMMethod pSourceMeter) {
        /* Store parameters */
        theMethod = pMethod;
        theSMMethod = pSourceMeter;

        /* Create lists */
        theChildren = new ArrayList<>();
    }

    /**
     * Obtain the method.
     * @return the method
     */
    public ThemisAnalysisMethod getMethod() {
        return theMethod;
    }

    @Override
    public ThemisSMMethod getSourceMeter() {
        return theSMMethod;
    }

    @Override
    public Map<ThemisSMStat, Integer> getSourceMeterStats() {
        return theSMMethod == null ? null : theSMMethod.getStatistics();
    }

    @Override
    public Iterator<ThemisStatsBase> childIterator() {
        return theChildren.iterator();
    }

    @Override
    public String toString() {
        final String myName = theMethod.toString();
        final int myIndex = myName.indexOf(ThemisAnalysisKeyWord.THROWS.toString());
        return myIndex != -1 ?  myName.substring(0, myIndex - 1) : myName;
    }

    @Override
    public void addClass(final ThemisStatsClass pClass) {
        /* Add class to list */
        theChildren.add(pClass);
        pClass.setParent(this);

        /* Adjust counts */
        pClass.addStatsToTotals();
        addChildTotals(pClass);
    }

    @Override
    public void addMethod(final ThemisStatsMethod pMethod) {
        /* Methods cannot be a direct child of a method */
    }
}
