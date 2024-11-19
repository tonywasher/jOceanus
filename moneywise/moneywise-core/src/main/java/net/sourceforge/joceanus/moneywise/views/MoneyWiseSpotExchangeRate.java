/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.moneywise.views;

import java.util.Iterator;
import java.util.ListIterator;

import net.sourceforge.joceanus.metis.data.MetisDataEditState;
import net.sourceforge.joceanus.metis.data.MetisDataState;
import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.metis.field.MetisFieldVersionValues;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicResource;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseExchangeRate;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCurrency.MoneyWiseCurrencyList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseStaticDataType;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataItem;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataValues;
import net.sourceforge.joceanus.oceanus.date.OceanusDate;
import net.sourceforge.joceanus.oceanus.decimal.OceanusRatio;

/**
 * Extension of ExchangeRate to cater for spot rates.
 */
public final class MoneyWiseSpotExchangeRate
        extends MoneyWiseExchangeRate {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseSpotExchangeRate.class.getSimpleName();

    /**
     * List name.
     */
    public static final String LIST_NAME = OBJECT_NAME + "s";

    /**
     * Report fields.
     */
    @SuppressWarnings("rawtypes")
    private static final MetisFieldSet<MoneyWiseSpotExchangeRate> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseSpotExchangeRate.class);

    /*
     * The fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MoneyWiseViewResource.SPOTEVENT_PREVDATE, MoneyWiseSpotExchangeRate::getPrevDate);
        FIELD_DEFS.declareLocalField(MoneyWiseViewResource.SPOTRATE_PREVRATE, MoneyWiseSpotExchangeRate::getPrevRate);
    }

    /**
     * the previous date.
     */
    private OceanusDate thePrevDate;

    /**
     * the previous rate.
     */
    private OceanusRatio thePrevRate;

    /**
     * Constructor for a new SpotRate where no rate data exists.
     * @param pList the Spot Rate List
     * @param pCurrency the currency
     */
    private MoneyWiseSpotExchangeRate(final MoneyWiseSpotExchangeList pList,
                                      final MoneyWiseCurrency pCurrency) {
        super(pList);

        /* Store base values */
        setDate(pList.theDate);
        setFromCurrency(pList.getCurrency());
        setToCurrency(pCurrency);
    }

    @Override
    public MetisFieldSetDef getDataFieldSet() {
        return FIELD_DEFS;
    }

    /**
     * Obtain previous rate.
     * @return the rate.
     */
    public OceanusRatio getPrevRate() {
        return thePrevRate;
    }

    /**
     * Obtain previous date.
     * @return the date.
     */
    public OceanusDate getPrevDate() {
        return thePrevDate;
    }

    /**
     * Validate the line.
     */
    @Override
    public void validate() {
        setValidEdit();
    }

    /* Is this row locked */
    @Override
    public boolean isLocked() {
        return isDeleted();
    }

    /**
     * Note that this item has been validated.
     */
    @Override
    public void setValidEdit() {
        setEditState(hasHistory()
                ? MetisDataEditState.VALID
                : MetisDataEditState.CLEAN);
    }

    @Override
    public OceanusRatio getExchangeRate() {
        /* Switch on state */
        switch (getState()) {
            case NEW:
            case CHANGED:
            case RECOVERED:
            case CLEAN:
                return super.getExchangeRate();
            default:
                return null;
        }
    }

    @Override
    public MetisDataState getState() {
        final MetisFieldVersionValues myCurr = getValues();
        final MetisFieldVersionValues myBase = getOriginalValues();

        /* If we have no changes we are CLEAN */
        if (myCurr.getVersion() == 0) {
            return MetisDataState.CLEAN;
        }

        /* If the original rate is Null */
        if (myBase.getValue(MoneyWiseBasicResource.XCHGRATE_RATE) == null) {
            /* Return status */
            return myCurr.getValue(MoneyWiseBasicResource.XCHGRATE_RATE) == null
                    ? MetisDataState.DELNEW
                    : MetisDataState.NEW;
        }

        /* If we are deleted return so */
        return myCurr.getValue(MoneyWiseBasicResource.XCHGRATE_RATE) == null
                ? MetisDataState.DELETED
                : MetisDataState.CHANGED;
    }

    /**
     * The Spot Rates List class.
     */
    public static class MoneyWiseSpotExchangeList
            extends MoneyWiseExchangeRateBaseList<MoneyWiseSpotExchangeRate> {
        /**
         * Report fields.
         */
        @SuppressWarnings("rawtypes")
        private static final MetisFieldSet<MoneyWiseSpotExchangeList> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseSpotExchangeList.class);

        /*
         * The currency field Id.
         */
        static {
            FIELD_DEFS.declareLocalField(MoneyWiseStaticDataType.CURRENCY, MoneyWiseSpotExchangeList::getCurrency);
            FIELD_DEFS.declareLocalField(MoneyWiseBasicResource.MONEYWISEDATA_FIELD_DATE, MoneyWiseSpotExchangeList::getDate);
            FIELD_DEFS.declareLocalField(MoneyWiseViewResource.SPOTEVENT_NEXTDATE, MoneyWiseSpotExchangeList::getNext);
            FIELD_DEFS.declareLocalField(MoneyWiseViewResource.SPOTEVENT_PREVDATE, MoneyWiseSpotExchangeList::getPrev);
        }

        /**
         * The date.
         */
        private final OceanusDate theDate;

        /**
         * The view.
         */
        private final MoneyWiseView theView;

        /**
         * The currency.
         */
        private final MoneyWiseCurrency theCurrency;

        /**
         * The next date.
         */
        private OceanusDate theNext;

        /**
         * The previous date.
         */
        private OceanusDate thePrev;

        /**
         * Constructor.
         * @param pView the view
         * @param pDate the date
         */
        public MoneyWiseSpotExchangeList(final MoneyWiseView pView,
                                         final OceanusDate pDate) {
            /* Build initial list */
            super((MoneyWiseDataSet) pView.getData(), MoneyWiseSpotExchangeRate.class, MoneyWiseBasicDataType.SECURITYPRICE);
            setStyle(PrometheusListStyle.EDIT);
            ensureMap();

            /* Store parameters */
            theDate = pDate;
            theView = pView;

            /* Obtain the portfolio bucket */
            final MoneyWiseDataSet myData = (MoneyWiseDataSet) theView.getData();
            theCurrency = myData.getReportingCurrency();
            final MoneyWiseCurrencyList myCurrencies = myData.getAccountCurrencies();

            /* Loop through the Currencies */
            final OceanusDate myDate = new OceanusDate(theDate);
            final Iterator<MoneyWiseCurrency> myCurIterator = myCurrencies.iterator();
            while (myCurIterator.hasNext()) {
                final MoneyWiseCurrency myCurrency = myCurIterator.next();

                /* Ignore deleted/disabled and default currency */
                boolean bIgnore = myCurrency.isDeleted() || myCurrency.isDisabled();
                bIgnore |= myCurrency.equals(theCurrency);
                if (bIgnore) {
                    continue;
                }

                /* Create a SpotRate entry */
                final MoneyWiseSpotExchangeRate mySpot = new MoneyWiseSpotExchangeRate(this, myCurrency);
                mySpot.setIndexedId(myCurrency.getIndexedId());
                mySpot.setDate(myDate);
                add(mySpot);
            }

            /* Set the base for this list */
            final MoneyWiseExchangeRateList myRates = myData.getExchangeRates();
            setBase(myRates);

            /* Loop through the rates */
            final ListIterator<MoneyWiseExchangeRate> myIterator = myRates.listIterator(myRates.size());
            while (myIterator.hasPrevious()) {
                final MoneyWiseExchangeRate myRate = myIterator.previous();

                /* Ignore deleted rates */
                if (myRate.isDeleted()) {
                    continue;
                }

                /* Test the Date */
                final int iDiff = theDate.compareTo(myRate.getDate());

                /* If we are past the date */
                if (iDiff < 0) {
                    /* Record the next date and break the loop */
                    theNext = myRate.getDate();
                    break;
                }

                /* Access the Spot Rate and ignore if not relevant */
                final MoneyWiseCurrency myCurrency = myRate.getToCurrency();
                final MoneyWiseSpotExchangeRate mySpot = findItemById(myCurrency.getIndexedId());
                if (mySpot == null) {
                    continue;
                }

                /* If we are exactly the date */
                if (iDiff == 0) {
                    /* Set rate */
                    mySpot.setValueExchangeRate(myRate.getExchangeRate());

                    /* Link to base and re-establish state */
                    mySpot.setBase(myRate);

                    /* else we are a previous date */
                } else {
                    /* Set previous date and value */
                    mySpot.thePrevDate = myRate.getDate();
                    mySpot.thePrevRate = myRate.getExchangeRate();

                    /* Record the latest previous date */
                    thePrev = myRate.getDate();
                }
            }
        }

        @SuppressWarnings("rawtypes")
        @Override
        public MetisFieldSet<MoneyWiseSpotExchangeList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return MoneyWiseSpotExchangeList.class.getSimpleName();
        }

        @Override
        public MetisFieldSetDef getItemFields() {
            return MoneyWiseSpotExchangeRate.FIELD_DEFS;
        }

        @Override
        protected MoneyWiseSpotExchangeList getEmptyList(final PrometheusListStyle pStyle) {
            throw new UnsupportedOperationException();
        }

        @Override
        public MoneyWiseDataSet getDataSet() {
            return (MoneyWiseDataSet) super.getDataSet();
        }

        /**
         * Obtain the currency.
         * @return the currency
         */
        private MoneyWiseCurrency getCurrency() {
            return theCurrency;
        }

        /**
         * Obtain the date.
         * @return the date
         */
        private OceanusDate getDate() {
            return theDate;
        }

        /**
         * Obtain the next date.
         * @return the date
         */
        public OceanusDate getNext() {
            return theNext;
        }

        /**
         * Obtain the previous date.
         * @return the date
         */
        public OceanusDate getPrev() {
            return thePrev;
        }

        /* Is this list locked */
        @Override
        public boolean isLocked() {
            return false;
        }

        /* Disable Add a new item */
        @Override
        public MoneyWiseSpotExchangeRate addCopyItem(final PrometheusDataItem pElement) {
            throw new UnsupportedOperationException();
        }

        @Override
        public MoneyWiseSpotExchangeRate addNewItem() {
            throw new UnsupportedOperationException();
        }

        @Override
        public MoneyWiseSpotExchangeRate addValuesItem(final PrometheusDataValues pValues) {
            throw new UnsupportedOperationException();
        }
    }
}
