/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2017 Tony Washer
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
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jmoneywise.lethe.views;

import java.util.Iterator;
import java.util.ListIterator;

import net.sourceforge.joceanus.jmetis.lethe.data.MetisDataState;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisEditState;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisValueSet;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.ExchangeRate;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseDataResource;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency.AssetCurrencyList;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataItem;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataList;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;

/**
 * Extension of ExchangeRate to cater for spot rates.
 */
public final class SpotExchangeRate
        extends ExchangeRate {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = SpotExchangeRate.class.getSimpleName();

    /**
     * List name.
     */
    public static final String LIST_NAME = OBJECT_NAME + "s";

    /**
     * Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(OBJECT_NAME, ExchangeRate.FIELD_DEFS);

    /**
     * Previous Date field Id.
     */
    public static final MetisField FIELD_PREVDATE = FIELD_DEFS.declareEqualityField(MoneyWiseViewResource.SPOTEVENT_PREVDATE.getValue());

    /**
     * Previous Rate field Id.
     */
    public static final MetisField FIELD_PREVRATE = FIELD_DEFS.declareEqualityField(MoneyWiseViewResource.SPOTRATE_PREVRATE.getValue());

    /**
     * the previous date.
     */
    private TethysDate thePrevDate;

    /**
     * the previous rate.
     */
    private TethysRatio thePrevRate;

    /**
     * Constructor for a new SpotRate where no rate data exists.
     * @param pList the Spot Rate List
     * @param pCurrency the currency
     */
    private SpotExchangeRate(final SpotExchangeList<?, ?> pList,
                             final AssetCurrency pCurrency) {
        super(pList);

        /* Store base values */
        setDate(pList.theDate);
        setToCurrency(pCurrency);
    }

    @Override
    public MetisFields declareFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final MetisField pField) {
        if (FIELD_PREVDATE.equals(pField)) {
            return thePrevDate;
        }
        if (FIELD_PREVRATE.equals(pField)) {
            return thePrevRate;
        }
        return super.getFieldValue(pField);
    }

    /**
     * Obtain previous rate.
     * @return the rate.
     */
    public TethysRatio getPrevRate() {
        return thePrevRate;
    }

    /**
     * Obtain previous date.
     * @return the date.
     */
    public TethysDate getPrevDate() {
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
                                  ? MetisEditState.VALID
                                  : MetisEditState.CLEAN);
    }

    @Override
    public TethysRatio getExchangeRate() {
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
        final MetisValueSet myCurr = getValueSet();
        final MetisValueSet myBase = getOriginalValues();

        /* If we have no changes we are CLEAN */
        if (myCurr.getVersion() == 0) {
            return MetisDataState.CLEAN;
        }

        /* If the original rate is Null */
        if (getExchangeRate(myBase) == null) {
            /* Return status */
            return getExchangeRate(myCurr) == null
                                                   ? MetisDataState.DELNEW
                                                   : MetisDataState.NEW;
        }

        /* If we are deleted return so */
        return getExchangeRate(myCurr) == null
                                               ? MetisDataState.DELETED
                                               : MetisDataState.CHANGED;
    }

    /**
     * The Spot Rates List class.
     * @param <N> the node type
     * @param <I> the icon type
     */
    public static class SpotExchangeList<N, I>
            extends ExchangeRateBaseList<SpotExchangeRate> {
        /**
         * Local Report fields.
         */
        protected static final MetisFields FIELD_DEFS = new MetisFields(MoneyWiseViewResource.SPOTRATE_NAME.getValue(), DataList.FIELD_DEFS);

        /**
         * The currency field Id.
         */
        public static final MetisField FIELD_CURRENCY = FIELD_DEFS.declareLocalField(MoneyWiseDataType.PORTFOLIO.getItemName());

        /**
         * The date field Id.
         */
        public static final MetisField FIELD_DATE = FIELD_DEFS.declareLocalField(MoneyWiseDataResource.MONEYWISEDATA_FIELD_DATE.getValue());

        /**
         * The next date field Id.
         */
        public static final MetisField FIELD_NEXT = FIELD_DEFS.declareLocalField(MoneyWiseViewResource.SPOTEVENT_NEXTDATE.getValue());

        /**
         * The previous date field Id.
         */
        public static final MetisField FIELD_PREV = FIELD_DEFS.declareLocalField(MoneyWiseViewResource.SPOTEVENT_PREVDATE.getValue());

        /**
         * The date.
         */
        private final TethysDate theDate;

        /**
         * The view.
         */
        private final View<N, I> theView;

        /**
         * The currency.
         */
        private final AssetCurrency theCurrency;

        /**
         * The next date.
         */
        private TethysDate theNext;

        /**
         * The previous date.
         */
        private TethysDate thePrev;

        /**
         * Constructor.
         * @param pView the view
         * @param pDate the date
         */
        public SpotExchangeList(final View<N, I> pView,
                                final TethysDate pDate) {
            /* Build initial list */
            super(pView.getData(), SpotExchangeRate.class, MoneyWiseDataType.SECURITYPRICE);
            setStyle(ListStyle.EDIT);
            ensureMap();

            /* Store parameters */
            theDate = pDate;
            theView = pView;

            /* Obtain the portfolio bucket */
            final MoneyWiseData myData = theView.getData();
            theCurrency = myData.getDefaultCurrency();
            final AssetCurrencyList myCurrencies = myData.getAccountCurrencies();

            /* Loop through the Currencies */
            final TethysDate myDate = new TethysDate(theDate);
            final Iterator<AssetCurrency> myCurIterator = myCurrencies.iterator();
            while (myCurIterator.hasNext()) {
                final AssetCurrency myCurrency = myCurIterator.next();

                /* Ignore deleted/disabled and default currency */
                boolean bIgnore = myCurrency.isDeleted() || myCurrency.isDisabled();
                bIgnore |= myCurrency.equals(theCurrency);
                if (bIgnore) {
                    continue;
                }

                /* Create a SpotRate entry */
                final SpotExchangeRate mySpot = new SpotExchangeRate(this, myCurrency);
                mySpot.setId(myCurrency.getId());
                mySpot.setDate(myDate);
                add(mySpot);
            }

            /* Set the base for this list */
            final ExchangeRateList myRates = myData.getExchangeRates();
            setBase(myRates);

            /* Loop through the rates */
            final ListIterator<ExchangeRate> myIterator = myRates.listIterator(myRates.size());
            while (myIterator.hasPrevious()) {
                final ExchangeRate myRate = myIterator.previous();

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
                final AssetCurrency myCurrency = myRate.getToCurrency();
                final SpotExchangeRate mySpot = findItemById(myCurrency.getId());
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

        @Override
        public MetisFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(final MetisField pField) {
            if (FIELD_CURRENCY.equals(pField)) {
                return theCurrency;
            }
            if (FIELD_DATE.equals(pField)) {
                return theDate;
            }
            if (FIELD_NEXT.equals(pField)) {
                return getNext();
            }
            if (FIELD_PREV.equals(pField)) {
                return getPrev();
            }
            return super.getFieldValue(pField);
        }

        @Override
        public String listName() {
            return SpotExchangeList.class.getSimpleName();
        }

        @Override
        public MetisFields getItemFields() {
            return SpotExchangeRate.FIELD_DEFS;
        }

        @Override
        protected SpotExchangeList<N, I> getEmptyList(final ListStyle pStyle) {
            throw new UnsupportedOperationException();
        }

        @Override
        public MoneyWiseData getDataSet() {
            return (MoneyWiseData) super.getDataSet();
        }

        /**
         * Obtain the next date.
         * @return the date
         */
        public TethysDate getNext() {
            return theNext;
        }

        /**
         * Obtain the previous date.
         * @return the date
         */
        public TethysDate getPrev() {
            return thePrev;
        }

        /* Is this list locked */
        @Override
        public boolean isLocked() {
            return false;
        }

        /* Disable Add a new item */
        @Override
        public SpotExchangeRate addCopyItem(final DataItem<?> pElement) {
            throw new UnsupportedOperationException();
        }

        @Override
        public SpotExchangeRate addNewItem() {
            throw new UnsupportedOperationException();
        }

        @Override
        public SpotExchangeRate addValuesItem(final DataValues<MoneyWiseDataType> pValues) {
            throw new UnsupportedOperationException();
        }
    }
}
