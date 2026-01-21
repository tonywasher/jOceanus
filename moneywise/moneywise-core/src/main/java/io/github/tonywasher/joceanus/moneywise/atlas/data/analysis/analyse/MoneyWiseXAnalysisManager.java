/*
 * MoneyWise: Finance Application
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
package io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.analyse;

import io.github.tonywasher.joceanus.oceanus.date.OceanusDate;
import io.github.tonywasher.joceanus.oceanus.date.OceanusDateRange;
import io.github.tonywasher.joceanus.oceanus.event.OceanusEventManager;
import io.github.tonywasher.joceanus.oceanus.event.OceanusEventRegistrar;
import io.github.tonywasher.joceanus.oceanus.event.OceanusEventRegistrar.OceanusEventProvider;
import io.github.tonywasher.joceanus.oceanus.format.OceanusDataFormatter;
import io.github.tonywasher.joceanus.metis.data.MetisDataItem.MetisDataMap;
import io.github.tonywasher.joceanus.metis.field.MetisFieldItem;
import io.github.tonywasher.joceanus.metis.field.MetisFieldSet;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysis;
import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisBucketResource;
import io.github.tonywasher.joceanus.prometheus.views.PrometheusDataEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * Analysis manager.
 */
public class MoneyWiseXAnalysisManager
        implements OceanusEventProvider<PrometheusDataEvent>, MetisFieldItem, MetisDataMap<OceanusDateRange, MoneyWiseXAnalysis> {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MoneyWiseXAnalysisManager> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseXAnalysisManager.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MoneyWiseXAnalysisBucketResource.ANALYSIS_NAME, MoneyWiseXAnalysisManager::getAnalysis);
    }

    /**
     * The Event Manager.
     */
    private final OceanusEventManager<PrometheusDataEvent> theEventManager;

    /**
     * The analysis map.
     */
    private final Map<OceanusDateRange, MoneyWiseXAnalysis> theAnalysisMap;

    /**
     * The base analysis.
     */
    private MoneyWiseXAnalysis theAnalysis;

    /**
     * Constructor.
     *
     * @param pAnalysis the new analysis
     */
    public MoneyWiseXAnalysisManager(final MoneyWiseXAnalysis pAnalysis) {
        /* Store the parameters */
        theAnalysis = pAnalysis;

        /* Create the event manager */
        theEventManager = new OceanusEventManager<>();

        /* Create the analysis map */
        theAnalysisMap = new HashMap<>();
    }

    @Override
    public MetisFieldSet<MoneyWiseXAnalysisManager> getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public OceanusEventRegistrar<PrometheusDataEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    @Override
    public String formatObject(final OceanusDataFormatter pFormatter) {
        return getClass().getSimpleName();
    }

    @Override
    public Map<OceanusDateRange, MoneyWiseXAnalysis> getUnderlyingMap() {
        return theAnalysisMap;
    }

    /**
     * Is the analysis manager idle?
     *
     * @return true/false
     */
    public boolean isIdle() {
        return theAnalysis.isIdle();
    }

    /**
     * Set analysis.
     *
     * @param pAnalysis the analysis
     */
    public void setAnalysis(final MoneyWiseXAnalysis pAnalysis) {
        /* Record analysis and clear the map */
        theAnalysis = pAnalysis;
        theAnalysisMap.clear();

        /* Report to listeners */
        theEventManager.fireEvent(PrometheusDataEvent.DATACHANGED);
    }

    /**
     * Obtain the base analysis.
     *
     * @return the base analysis
     */
    public MoneyWiseXAnalysis getAnalysis() {
        return theAnalysis;
    }

    /**
     * Do we have a foreign currency?
     *
     * @return true/false
     */
    public Boolean haveForeignCurrency() {
        return theAnalysis.haveForeignCurrency();
    }

    /**
     * Do we have active securities?
     *
     * @return true/false
     */
    public Boolean haveActiveSecurities() {
        return theAnalysis.haveActiveSecurities();
    }

    /**
     * Obtain an analysis for a date.
     *
     * @param pDate the date for the analysis.
     * @return the analysis
     */
    public MoneyWiseXAnalysis getDatedAnalysis(final OceanusDate pDate) {
        /* Create the new Range */
        final OceanusDateRange myRange = new OceanusDateRange(null, pDate);

        /* Look for the existing analysis */
        return theAnalysisMap.computeIfAbsent(myRange, r -> {
            /* Create the new event analysis */
            final MoneyWiseXAnalysis myAnalysis = new MoneyWiseXAnalysis(theAnalysis, pDate);
            myAnalysis.produceTotals();

            /* Check the totals */
            myAnalysis.checkTotals();

            /* Put it into the map */
            return myAnalysis;
        });
    }

    /**
     * Obtain an analysis for a range.
     *
     * @param pRange the date range for the analysis.
     * @return the analysis
     */
    public MoneyWiseXAnalysis getRangedAnalysis(final OceanusDateRange pRange) {
        /* Look for the existing analysis */
        return theAnalysisMap.computeIfAbsent(pRange, r -> {
            /* Create the new event analysis */
            final MoneyWiseXAnalysis myAnalysis = new MoneyWiseXAnalysis(theAnalysis, r);
            myAnalysis.produceTotals();

            /* Check the totals */
            myAnalysis.checkTotals();

            /* Put it into the map */
            return myAnalysis;
        });
    }

    /**
     * Analyse the base analysis.
     */
    public void analyseBase() {
        /* Produce totals for the base analysis */
        theAnalysis.produceTotals();
    }
}
