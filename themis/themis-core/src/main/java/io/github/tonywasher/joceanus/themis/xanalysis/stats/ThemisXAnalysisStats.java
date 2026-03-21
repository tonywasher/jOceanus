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
package io.github.tonywasher.joceanus.themis.xanalysis.stats;

import java.util.EnumMap;
import java.util.Map;

/**
 * Statistics Set.
 */
public class ThemisXAnalysisStats {
    /**
     * The stats.
     */
    private final Map<ThemisXAnalysisStat, Number> theStats;

    /**
     * The stats.
     */
    private final Map<ThemisXAnalysisStat, Number> theTotals;

    /**
     * Constructor.
     */
    ThemisXAnalysisStats() {
        theStats = new EnumMap<>(ThemisXAnalysisStat.class);
        theTotals = new EnumMap<>(ThemisXAnalysisStat.class);
    }

    /**
     * Obtain the stats.
     *
     * @return the stats
     */
    public Map<ThemisXAnalysisStat, Number> getStats() {
        return theStats;
    }

    /**
     * Obtain the totals.
     *
     * @return the totals
     */
    public Map<ThemisXAnalysisStat, Number> getTotals() {
        return theTotals;
    }

    /**
     * Does the stats value exist?.
     *
     * @param pStat the statistic
     * @return true/false
     */
    public boolean hasStat(final ThemisXAnalysisStat pStat) {
        return theStats.containsKey(pStat);
    }

    /**
     * Obtain statistic value.
     *
     * @param pStat the statistic
     * @return the value
     */
    public Number getStat(final ThemisXAnalysisStat pStat) {
        return theStats.computeIfAbsent(pStat, s -> 0);
    }

    /**
     * Does the total value exist?.
     *
     * @param pStat the statistic
     * @return true/false
     */
    public boolean hasTotal(final ThemisXAnalysisStat pStat) {
        return theStats.containsKey(pStat);
    }

    /**
     * Obtain total value.
     *
     * @param pStat the statistic
     * @return the value
     */
    public Number getTotal(final ThemisXAnalysisStat pStat) {
        return theStats.computeIfAbsent(pStat, s -> 0);
    }

    /**
     * Set Statistic value.
     *
     * @param pStat  the statistic
     * @param pValue the value
     */
    public void setStat(final ThemisXAnalysisStat pStat,
                        final Number pValue) {
        theStats.put(pStat, pValue);
    }

    /**
     * Set Total value.
     *
     * @param pStat  the statistic
     * @param pValue the value
     */
    public void setTotal(final ThemisXAnalysisStat pStat,
                         final Number pValue) {
        theTotals.put(pStat, pValue);
    }
}
