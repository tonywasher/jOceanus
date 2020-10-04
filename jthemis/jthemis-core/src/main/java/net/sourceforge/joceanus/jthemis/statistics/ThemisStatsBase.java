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
     * The parent.
     */
    private ThemisStatsBase theParent;

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
     * Obtain the parent.
     * @return the parent
     */
    public ThemisStatsBase getParent() {
        return theParent;
    }

    /**
     * Set the parent.
     * @param pParent the parent
     */
    public void setParent(final ThemisStatsBase pParent) {
        theParent = pParent;
    }

    /**
     * Obtain statistic value.
     * @param pStat the statistic
     * @return the value
     */
    public Integer getStat(final ThemisSMStat pStat) {
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
     * Adjust statistic.
     * @param pStat the statistic
     * @param pAdjust the adjustment value
     */
    private void adjustSMStat(final ThemisSMStat pStat,
                              final Integer pAdjust) {
        /* Adjust the value */
        final Map<ThemisSMStat, Integer> myMap = getSourceMeterStats();
        final Integer myAdjust = pAdjust == null ? 0 : pAdjust;
        final Integer myCurr = myMap.computeIfAbsent(pStat, s -> 0);
        myMap.put(pStat, myCurr + myAdjust);
    }

    /**
     * Obtain the sourceMeter stats.
     * @return the stats
     */
    public ThemisSMStatHolder getSourceMeter() {
        return null;
    }

    /**
     * Obtain the sourceMeter stats.
     * @return the stats
     */
    public abstract Map<ThemisSMStat, Integer> getSourceMeterStats();

    /**
     * Obtain class iterator.
     * @return the iterator
     */
    public abstract Iterator<ThemisStatsBase> childIterator();

    /**
     * Add class to list.
     * @param pClass the class
     */
    void addClass(final ThemisStatsClass pClass) {
        /* NoOp by default */
    }

    /**
     * Add method to list.
     * @param pMethod the method
     */
    void addMethod(final ThemisStatsMethod pMethod) {
        /* NoOp by default */
    }

    /**
     * Add child totals.
     * @param pChild the child
     */
    void addChildTotals(final ThemisStatsBase pChild) {
        /* Adjust counts */
        adjustChildStat(pChild, ThemisSMStat.TNCL);
        adjustChildStat(pChild, ThemisSMStat.TNIN);
        adjustChildStat(pChild, ThemisSMStat.TNEN);
        adjustChildStat(pChild, ThemisSMStat.TNM);
        adjustChildStat(pChild, ThemisSMStat.TNOS);
        adjustChildStat(pChild, ThemisSMStat.TLOC);
        adjustChildStat(pChild, ThemisSMStat.TLLOC);
        adjustChildStat(pChild, ThemisSMStat.TCLOC);
        adjustChildStat(pChild, ThemisSMStat.TDLOC);
    }

    /**
     * Adjust child stat.
     * @param pChild the child
     * @param pStat the stat
     */
    void adjustChildStat(final ThemisStatsBase pChild,
                         final ThemisSMStat pStat) {
        adjustChildStat(pChild, pStat, pStat);
    }

    /**
     * Adjust child stat.
     * @param pChild the child
     * @param pStat the stat
     * @param pChildStat the child stat
     */
    void adjustChildStat(final ThemisStatsBase pChild,
                         final ThemisSMStat pStat,
                         final ThemisSMStat pChildStat) {
        /* Adjust counts */
        adjustStat(pStat, pChild.getStat(pChildStat));
        if (getSourceMeter() == null) {
            final Map<ThemisSMStat, Integer> myMap = pChild.getSourceMeterStats();
            if (myMap != null) {
                adjustSMStat(pStat, myMap.get(pChildStat));
            }
        }
    }
}
