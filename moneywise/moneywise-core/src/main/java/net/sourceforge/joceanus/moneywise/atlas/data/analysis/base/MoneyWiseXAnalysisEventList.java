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
package net.sourceforge.joceanus.moneywise.atlas.data.analysis.base;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.oceanus.date.OceanusDate;
import io.github.tonywasher.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransaction;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataItem;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataList;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataMapItem;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataValues;
import io.github.tonywasher.joceanus.prometheus.views.PrometheusEditSet;

/**
 * AnalysisEvent list.
 */
public class MoneyWiseXAnalysisEventList
        extends PrometheusDataList<MoneyWiseXAnalysisEvent> {
    /**
     * Report fields.
     */
    private static final MetisFieldSet<MoneyWiseXAnalysisEventList> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseXAnalysisEventList.class);

    /**
     * Constructor.
     *
     * @param pEditSet the editSet
     */
    public MoneyWiseXAnalysisEventList(final PrometheusEditSet pEditSet) {
        super(MoneyWiseXAnalysisEvent.class, pEditSet.getDataSet(), MoneyWiseXAnalysisDataType.EVENT, PrometheusListStyle.EDIT);
    }

    @Override
    public MetisFieldSetDef getItemFields() {
        return FIELD_DEFS;
    }

    @Override
    public MetisFieldSetDef getDataFieldSet() {
        return MoneyWiseXAnalysisEvent.FIELD_DEFS;
    }

    @Override
    public String listName() {
        return MoneyWiseXAnalysisDataType.EVENT.getListName();
    }

    @Override
    protected PrometheusDataList<MoneyWiseXAnalysisEvent> getEmptyList(final PrometheusListStyle pStyle) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected PrometheusDataMapItem allocateDataMap() {
        throw new UnsupportedOperationException();
    }

    @Override
    public MoneyWiseXAnalysisEvent addCopyItem(final PrometheusDataItem pElement) {
        throw new UnsupportedOperationException();
    }

    @Override
    public MoneyWiseXAnalysisEvent addNewItem() {
        throw new UnsupportedOperationException();
    }

    @Override
    public MoneyWiseXAnalysisEvent addValuesItem(final PrometheusDataValues pValues) throws OceanusException {
        throw new UnsupportedOperationException();
    }

    /**
     * Create and add an event for a transaction.
     *
     * @param pTrans the transaction
     * @return the event
     */
    public MoneyWiseXAnalysisEvent newTransaction(final MoneyWiseTransaction pTrans) {
        final MoneyWiseXAnalysisEvent myEvent = new MoneyWiseXAnalysisEvent(this, pTrans);
        add(myEvent);
        return myEvent;
    }

    /**
     * Create and add an event for a securityPrice change.
     *
     * @param pDate the date
     * @return the event
     */
    public MoneyWiseXAnalysisEvent newSecurityPrice(final OceanusDate pDate) {
        final MoneyWiseXAnalysisEvent myEvent = new MoneyWiseXAnalysisEvent(this, MoneyWiseXAnalysisEventType.SECURITYPRICE, pDate);
        add(myEvent);
        return myEvent;
    }

    /**
     * Create and add an event for an exchangeRate change.
     *
     * @param pDate the date
     * @return the event
     */
    public MoneyWiseXAnalysisEvent newXchangeRate(final OceanusDate pDate) {
        final MoneyWiseXAnalysisEvent myEvent = new MoneyWiseXAnalysisEvent(this, MoneyWiseXAnalysisEventType.XCHANGERATE, pDate);
        add(myEvent);
        return myEvent;
    }

    /**
     * Create and add an event for a depositRate change.
     *
     * @param pDate the date
     * @return the event
     */
    public MoneyWiseXAnalysisEvent newDepositRate(final OceanusDate pDate) {
        final MoneyWiseXAnalysisEvent myEvent = new MoneyWiseXAnalysisEvent(this, MoneyWiseXAnalysisEventType.DEPOSITRATE, pDate);
        add(myEvent);
        return myEvent;
    }

    /**
     * Create and add an event for opening balances.
     *
     * @param pDate the date
     * @return the event
     */
    public MoneyWiseXAnalysisEvent newOpeningBalance(final OceanusDate pDate) {
        final MoneyWiseXAnalysisEvent myEvent = new MoneyWiseXAnalysisEvent(this, MoneyWiseXAnalysisEventType.OPENINGBALANCE, pDate);
        add(myEvent);
        return myEvent;
    }
}
