/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012-2026 Tony Washer
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
package net.sourceforge.joceanus.themis.lethe.statistics;

import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;

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
    private final Map<ThemisStat, Integer> theStats;

    /**
     * Constructor.
     */
    protected ThemisStatsBase() {
        theStats = new EnumMap<>(ThemisStat.class);
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
    public Integer getStat(final ThemisStat pStat) {
        return theStats.computeIfAbsent(pStat, s -> 0);
    }

    /**
     * Set statistic value.
     * @param pStat the statistic
     * @param pValue the value
     */
    private void setStat(final ThemisStat pStat,
                         final Integer pValue) {
        theStats.put(pStat, pValue);
    }

    /**
     * Increment statistic.
     * @param pStat the statistic
     */
    void incrementStat(final ThemisStat pStat) {
        adjustStat(pStat, 1);
    }

    /**
     * Adjust statistic.
     * @param pStat the statistic
     * @param pAdjust the adjustment value
     */
    void adjustStat(final ThemisStat pStat,
                    final Integer pAdjust) {
        /* Adjust the value */
        final Integer myCurr = getStat(pStat);
        setStat(pStat, myCurr + pAdjust);
    }

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
     * Add all stats into totals.
     */
    void addStatsToTotals() {
        /* Adjust counts */
        adjustTotals(ThemisStat.LOC, ThemisStat.TLOC);
        adjustTotals(ThemisStat.LLOC, ThemisStat.TLLOC);
        adjustTotals(ThemisStat.NOS, ThemisStat.TNOS);
        adjustTotals(ThemisStat.CLOC, ThemisStat.TCLOC);
        adjustTotals(ThemisStat.DLOC, ThemisStat.TDLOC);
        adjustTotals(ThemisStat.NCL, ThemisStat.TNCL);
        adjustTotals(ThemisStat.NIN, ThemisStat.TNIN);
        adjustTotals(ThemisStat.NEN, ThemisStat.TNEN);
        adjustTotals(ThemisStat.NM, ThemisStat.TNM);
        adjustTotals(ThemisStat.NA, ThemisStat.TNA);
    }

    /**
     * Add method stats into totals.
     */
    void addMethodStatsToTotals() {
        /* Adjust counts */
        adjustTotals(ThemisStat.NCL, ThemisStat.TNCL);
        adjustTotals(ThemisStat.NIN, ThemisStat.TNIN);
        adjustTotals(ThemisStat.NEN, ThemisStat.TNEN);
        adjustTotals(ThemisStat.NM, ThemisStat.TNM);
        adjustTotals(ThemisStat.NA, ThemisStat.TNA);
    }

    /**
     * Add method stats into totals.
     * @param pMethod the method
     */
    void addMethodStatsToClass(final ThemisStatsMethod pMethod) {
        /* Adjust counts */
        adjustChildStat(pMethod, ThemisStat.LOC);
        adjustChildStat(pMethod, ThemisStat.LLOC);
        adjustChildStat(pMethod, ThemisStat.NOS);
        adjustChildStat(pMethod, ThemisStat.CLOC);
        adjustChildStat(pMethod, ThemisStat.DLOC);
    }

    /**
     * Adjust totals.
     * @param pItem the item stat
     * @param pTotal the total stat
     */
    void adjustTotals(final ThemisStat pItem,
                      final ThemisStat pTotal) {
        /* Adjust counts */
        adjustStat(pTotal, getStat(pItem));
    }

    /**
     * Add child totals.
     * @param pChild the child
     */
    void addChildTotals(final ThemisStatsBase pChild) {
        /* Adjust counts */
        adjustChildStat(pChild, ThemisStat.TLOC);
        adjustChildStat(pChild, ThemisStat.TLLOC);
        adjustChildStat(pChild, ThemisStat.TNCL);
        adjustChildStat(pChild, ThemisStat.TNIN);
        adjustChildStat(pChild, ThemisStat.TNEN);
        adjustChildStat(pChild, ThemisStat.TNM);
        adjustChildStat(pChild, ThemisStat.TNA);
        adjustChildStat(pChild, ThemisStat.TNOS);
        adjustChildStat(pChild, ThemisStat.TCLOC);
        adjustChildStat(pChild, ThemisStat.TDLOC);
    }

    /**
     * Adjust child stat.
     * @param pChild the child
     * @param pStat the stat
     */
    void adjustChildStat(final ThemisStatsBase pChild,
                         final ThemisStat pStat) {
        adjustChildStat(pChild, pStat, pStat);
    }

    /**
     * Adjust child stat.
     * @param pChild the child
     * @param pStat the stat
     * @param pChildStat the child stat
     */
    void adjustChildStat(final ThemisStatsBase pChild,
                         final ThemisStat pStat,
                         final ThemisStat pChildStat) {
        /* Adjust counts */
        adjustStat(pStat, pChild.getStat(pChildStat));
    }
}
