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

import java.util.Collections;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;

import net.sourceforge.joceanus.jthemis.sourcemeter.ThemisSMStat;
import net.sourceforge.joceanus.jthemis.sourcemeter.ThemisSMStatHolder;

/**
 * Statistics Base.
 */
public abstract class ThemisStatsBase {
    /**
     * The stats.
     */
    private final Map<ThemisSMStat, Integer> theStats;

    /**
     * Constructor.
     */
    protected ThemisStatsBase() {
        theStats = new EnumMap<>(ThemisSMStat.class);
    }

    /**
     * Obtain statistic value.
     * @param pStat the statistic
     * @return the value
     */
    Integer getStat(final ThemisSMStat pStat) {
        return theStats.computeIfAbsent(pStat, s -> 0);
    }

    /**
     * Set statistic value.
     * @param pStat the statistic
     * @param pValue the value
     */
    private void setStat(final ThemisSMStat pStat,
                         final Integer pValue) {
        theStats.put(pStat, pValue);
    }

    /**
     * Increment statistic.
     * @param pStat the statistic
     */
    void incrementStat(final ThemisSMStat pStat) {
        adjustStat(pStat, 1);
    }

    /**
     * Adjust statistic.
     * @param pStat the statistic
     * @param pAdjust the adjustment value
     */
    void adjustStat(final ThemisSMStat pStat,
                    final Integer pAdjust) {
        /* Adjust the value */
        final Integer myCurr = getStat(pStat);
        setStat(pStat, myCurr + pAdjust);
    }

    /**
     * Obtain the sourceMeter stats.
     * @return the stats
     */
    ThemisSMStatHolder getSourceMeter() {
        return null;
    }

    /**
     * Obtain class iterator.
     * @return the iterator
     */
    Iterator<ThemisStatsClass> classIterator() {
        return Collections.emptyIterator();
    }

    /**
     * Add class to list.
     * @param pClass the class
     */
    void addClass(final ThemisStatsClass pClass) {
        /* NoOp by default */
    }

    /**
     * Obtain class iterator.
     * @return the iterator
     */
    Iterator<ThemisStatsMethod> methodIterator() {
        return Collections.emptyIterator();
    }

    /**
     * Add method to list.
     * @param pMethod the method
     */
    void addMethod(final ThemisStatsMethod pMethod) {
        /* NoOp by default */
    }
}
