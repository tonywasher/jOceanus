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
package io.github.tonywasher.joceanus.themis.stats;

import java.util.EnumMap;
import java.util.Map;

/**
 * Statistics Set.
 */
public class ThemisStats {
    /**
     * The integer zero.
     */
    private static final Integer INT_ZERO = 0;

    /**
     * The double zero.
     */
    private static final Double DBL_ZERO = 0.0;

    /**
     * The stats.
     */
    private final Map<ThemisStat, Number> theStats;

    /**
     * The stats.
     */
    private final Map<ThemisStat, Number> theTotals;

    /**
     * Constructor.
     */
    ThemisStats() {
        theStats = initStats();
        theTotals = initStats();
    }

    /**
     * Obtain the stats.
     *
     * @return the stats
     */
    public Map<ThemisStat, Number> getStats() {
        return theStats;
    }

    /**
     * Obtain the totals.
     *
     * @return the totals
     */
    public Map<ThemisStat, Number> getTotals() {
        return theTotals;
    }

    /**
     * Obtain integer statistic value.
     *
     * @param pStat the statistic
     * @return the value
     */
    public Integer getIntegerStat(final ThemisStat pStat) {
        return (Integer) theStats.get(pStat);
    }

    /**
     * Obtain integer statistic value.
     *
     * @param pStat the statistic
     * @return the value
     */
    public Double getDoubleStat(final ThemisStat pStat) {
        return (Double) theStats.get(pStat);
    }

    /**
     * Obtain integer statistic value.
     *
     * @param pStat the statistic
     * @return the value
     */
    public Integer getIntegerTotal(final ThemisStat pStat) {
        return (Integer) theTotals.get(pStat);
    }

    /**
     * Obtain integer statistic value.
     *
     * @param pStat the statistic
     * @return the value
     */
    public Double getDoubleTotal(final ThemisStat pStat) {
        return (Double) theTotals.get(pStat);
    }

    /**
     * Set Statistic value.
     *
     * @param pStat  the statistic
     * @param pValue the value
     */
    public void setStat(final ThemisStat pStat,
                        final Number pValue) {
        theStats.put(pStat, pValue);
    }

    /**
     * Set Total value.
     *
     * @param pStat  the statistic
     * @param pValue the value
     */
    public void setTotal(final ThemisStat pStat,
                         final Number pValue) {
        theTotals.put(pStat, pValue);
    }

    /**
     * Initialize stats.
     *
     * @return the initialized stats
     */
    private Map<ThemisStat, Number> initStats() {
        final Map<ThemisStat, Number> myStats = new EnumMap<>(ThemisStat.class);
        for (ThemisStat myStat : ThemisStat.values()) {
            myStats.put(myStat, myStat.isInteger() ? INT_ZERO : DBL_ZERO);
        }
        return myStats;
    }
}
