/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2023 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.jmoneywise.atlas.analysis.base;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.field.MetisFieldItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmoneywise.lethe.data.ExchangeRate;
import net.sourceforge.joceanus.jmoneywise.lethe.data.SecurityPrice;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Transaction;
import net.sourceforge.joceanus.jtethys.date.TethysDate;

/**
 * Analysis Event.
 */
public class MoneyWiseXAnalysisEvent
        implements Comparable<MoneyWiseXAnalysisEvent>, MetisFieldItem {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MoneyWiseXAnalysisEvent> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseXAnalysisEvent.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MoneyWiseXAnalysisBaseResource.EVENT_ID, MoneyWiseXAnalysisEvent::getId);
        FIELD_DEFS.declareLocalField(MoneyWiseXAnalysisBaseResource.EVENT_DATE, MoneyWiseXAnalysisEvent::getDate);
        FIELD_DEFS.declareLocalField(MoneyWiseXAnalysisBaseResource.EVENT_TYPE, MoneyWiseXAnalysisEvent::getEventType);
        FIELD_DEFS.declareLocalField(MoneyWiseXAnalysisBaseResource.EVENT_TRANS, MoneyWiseXAnalysisEvent::getTransaction);
        FIELD_DEFS.declareLocalField(MoneyWiseXAnalysisBaseResource.EVENT_PRICES, MoneyWiseXAnalysisEvent::getPriceMap);
        FIELD_DEFS.declareLocalField(MoneyWiseXAnalysisBaseResource.EVENT_RATES, MoneyWiseXAnalysisEvent::getExchgRateMap);
    }

    /**
     * Id.
     */
    private final Integer theId;

    /**
     * Date.
     */
    private final TethysDate theDate;

    /**
     * EventType.
     */
    private final MoneyWiseAnalysisEventType theEventType;

    /**
     * Transaction.
     */
    private final Transaction theTransaction;

    /**
     * Prices.
     */
    private final Map<Integer, SecurityPrice> thePrices;

    /**
     * ExchangeRates.
     */
    private final Map<Integer, ExchangeRate> theXchangeRates;

    /**
     * Constructor.
     * @param pTransaction the transaction
     */
    public MoneyWiseXAnalysisEvent(final Transaction pTransaction) {
        /* Record details */
        theEventType = MoneyWiseAnalysisEventType.TRANSACTION;
        theTransaction = pTransaction;
        theId = pTransaction.getId();
        theDate = pTransaction.getDate();
        thePrices = Collections.emptyMap();
        theXchangeRates = Collections.emptyMap();
    }

    /**
     * Constructor.
     * @param pDate the date
     * @param pType the event type
     */
    public MoneyWiseXAnalysisEvent(final TethysDate pDate,
                                   final MoneyWiseAnalysisEventType pType) {
        theEventType = pType;
        final boolean isPrice = MoneyWiseAnalysisEventType.PRICE.equals(pType);
        theTransaction = null;
        theId = -(pDate.getId() << 1) + (isPrice ? -1 : 0);
        theDate = pDate;
        thePrices = isPrice ? new HashMap<>() : Collections.emptyMap();
        theXchangeRates = isPrice ? Collections.emptyMap() : new HashMap<>();
    }

    /**
     * Obtain the id.
     * @return the id
     */
    public Integer getId() {
        return theId;
    }

    /**
     * Obtain the date.
     * @return the date
     */
    public TethysDate getDate() {
        return theDate;
    }

    /**
     * Obtain the event Type.
     * @return the type
     */
    public MoneyWiseAnalysisEventType getEventType() {
        return theEventType;
    }

    /**
     * Obtain the transaction.
     * @return the transaction
     */
    public Transaction getTransaction() {
        return theTransaction;
    }

    /**
     * Obtain the price map.
     * @return the map
     */
    public Map<Integer, SecurityPrice> getPriceMap() {
        return thePrices;
    }

    /**
     * Obtain the xchgRate map.
     * @return the transaction
     */
    public Map<Integer, ExchangeRate> getExchgRateMap() {
        return theXchangeRates;
    }

    @Override
    public MetisFieldSetDef getDataFieldSet() {
        return FIELD_DEFS;
    }

    /**
     * Add SecurityPrice.
     * @param pPrice the securityPrice
     */
    public void addSecurityPrice(final SecurityPrice pPrice) {
        thePrices.put(pPrice.getSecurityId(), pPrice);
    }

    /**
     * Add ExchangeRate.
     * @param pRate the exchangeRate
     */
    public void addXchangeRate(final ExchangeRate pRate) {
        theXchangeRates.put(pRate.getToCurrencyId(), pRate);
    }

    @Override
    public int compareTo(final MoneyWiseXAnalysisEvent pThat) {
        /* Handle the trivial cases */
        if (this.equals(pThat)) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* Compare on date first */
        final int myDateDiff = theDate.compareTo(pThat.getDate());
        if (myDateDiff != 0) {
            return myDateDiff;
        }

        /* Compare on type next */
        if (!theEventType.equals(pThat.getEventType())) {
            return theEventType.ordinal() - pThat.getEventType().ordinal();
        }

        /* Compare transactions if required */
        return theEventType.equals(MoneyWiseAnalysisEventType.TRANSACTION)
                ? theTransaction.compareTo(pThat.getTransaction())
                : 0;
    }

    /**
     * EventTypes.
     */
    public enum MoneyWiseAnalysisEventType {
        /**
         * ExchangeRate.
         */
        XCHGRATE,

        /**
         * PricePoint.
         */
        PRICE,

        /**
         * Transaction.
         */
        TRANSACTION;
    }
}
