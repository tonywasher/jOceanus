/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2014 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.views;

import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.viewer.DataState;
import net.sourceforge.joceanus.jmetis.viewer.EditState;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.viewer.ValueSet;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.ExchangeRate;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseDataResource;
import net.sourceforge.joceanus.jmoneywise.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jmoneywise.data.statics.AssetCurrency.AssetCurrencyList;
import net.sourceforge.joceanus.jprometheus.data.DataItem;
import net.sourceforge.joceanus.jprometheus.data.DataList;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;
import net.sourceforge.joceanus.jtethys.decimal.JRatio;

/**
 * Extension of ExchangeRate to cater for spot rates.
 * @author Tony Washer
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
    private static final JDataFields FIELD_DEFS = new JDataFields(OBJECT_NAME, ExchangeRate.FIELD_DEFS);

    /**
     * Previous Date field Id.
     */
    public static final JDataField FIELD_PREVDATE = FIELD_DEFS.declareEqualityField(MoneyWiseViewResource.SPOTEVENT_PREVDATE.getValue());

    /**
     * Previous Rate field Id.
     */
    public static final JDataField FIELD_PREVRATE = FIELD_DEFS.declareEqualityField(MoneyWiseViewResource.SPOTRATE_PREVRATE.getValue());

    /**
     * the previous date.
     */
    private JDateDay thePrevDate;

    /**
     * the previous rate.
     */
    private JRatio thePrevRate;

    /**
     * Constructor for a new SpotRate where no rate data exists.
     * @param pList the Spot Rate List
     * @param pCurrency the currency
     */
    private SpotExchangeRate(final SpotExchangeList pList,
                             final AssetCurrency pCurrency) {
        super(pList);

        /* Store base values */
        setDate(pList.theDate);
        setToCurrency(pCurrency);
    }

    @Override
    public JDataFields declareFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final JDataField pField) {
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
    public JRatio getPrevRate() {
        return thePrevRate;
    }

    /**
     * Obtain previous date.
     * @return the date.
     */
    public JDateDay getPrevDate() {
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
        setEditState((hasHistory())
                                   ? EditState.VALID
                                   : EditState.CLEAN);
    }

    @Override
    public JRatio getExchangeRate() {
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
    public DataState getState() {
        ValueSet myCurr = getValueSet();
        ValueSet myBase = getOriginalValues();

        /* If we have no changes we are CLEAN */
        if (myCurr.getVersion() == 0) {
            return DataState.CLEAN;
        }

        /* If the original rate is Null */
        if (getExchangeRate(myBase) == null) {
            /* Return status */
            return getExchangeRate(myCurr) == null
                                                  ? DataState.DELNEW
                                                  : DataState.NEW;
        }

        /* If we are deleted return so */
        return getExchangeRate(myCurr) == null
                                              ? DataState.DELETED
                                              : DataState.CHANGED;
    }

    /**
     * The Spot Rates List class.
     */
    public static class SpotExchangeList
            extends ExchangeRateBaseList<SpotExchangeRate> {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(MoneyWiseViewResource.SPOTRATE_NAME.getValue(), DataList.FIELD_DEFS);

        /**
         * The currency field Id.
         */
        public static final JDataField FIELD_CURRENCY = FIELD_DEFS.declareLocalField(MoneyWiseDataType.PORTFOLIO.getItemName());

        /**
         * The date field Id.
         */
        public static final JDataField FIELD_DATE = FIELD_DEFS.declareLocalField(MoneyWiseDataResource.MONEYWISEDATA_FIELD_DATE.getValue());

        /**
         * The next date field Id.
         */
        public static final JDataField FIELD_NEXT = FIELD_DEFS.declareLocalField(MoneyWiseViewResource.SPOTEVENT_NEXTDATE.getValue());

        /**
         * The previous date field Id.
         */
        public static final JDataField FIELD_PREV = FIELD_DEFS.declareLocalField(MoneyWiseViewResource.SPOTEVENT_PREVDATE.getValue());

        /**
         * The date.
         */
        private final JDateDay theDate;

        /**
         * The view.
         */
        private final View theView;

        /**
         * The currency.
         */
        private final AssetCurrency theCurrency;

        /**
         * The next date.
         */
        private JDateDay theNext = null;

        /**
         * The previous date.
         */
        private JDateDay thePrev = null;

        /**
         * Constructor.
         * @param pView the view
         * @param pDate the date
         */
        public SpotExchangeList(final View pView,
                                final JDateDay pDate) {
            /* Build initial list */
            super(pView.getData(), SpotExchangeRate.class, MoneyWiseDataType.SECURITYPRICE);
            setStyle(ListStyle.EDIT);
            ensureMap();

            /* Store parameters */
            theDate = pDate;
            theView = pView;

            /* Obtain the portfolio bucket */
            MoneyWiseData myData = theView.getData();
            theCurrency = myData.getDefaultCurrency();
            AssetCurrencyList myCurrencies = myData.getAccountCurrencies();

            /* Loop through the Currencies */
            JDateDay myDate = new JDateDay(theDate);
            Iterator<AssetCurrency> myCurIterator = myCurrencies.iterator();
            while (myCurIterator.hasNext()) {
                AssetCurrency myCurrency = myCurIterator.next();

                /* Ignore deleted/disabled and default currency */
                boolean bIgnore = myCurrency.isDeleted() || myCurrency.isDisabled();
                bIgnore |= myCurrency.equals(theCurrency);
                if (bIgnore) {
                    continue;
                }

                /* Create a SpotRate entry */
                SpotExchangeRate mySpot = new SpotExchangeRate(this, myCurrency);
                mySpot.setId(myCurrency.getId());
                mySpot.setDate(myDate);
                add(mySpot);
            }

            /* Set the base for this list */
            ExchangeRateList myRates = myData.getExchangeRates();
            setBase(myRates);

            /* Loop through the rates */
            Iterator<ExchangeRate> myIterator = myRates.iterator();
            while (myIterator.hasNext()) {
                ExchangeRate myRate = myIterator.next();

                /* Ignore deleted rates */
                if (myRate.isDeleted()) {
                    continue;
                }

                /* Test the Date */
                int iDiff = theDate.compareTo(myRate.getDate());

                /* If we are past the date */
                if (iDiff < 0) {
                    /* Record the next date and break the loop */
                    theNext = myRate.getDate();
                    break;
                }

                /* Access the Spot Rate and ignore if not relevant */
                AssetCurrency myCurrency = myRate.getToCurrency();
                SpotExchangeRate mySpot = findItemById(myCurrency.getId());
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
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(final JDataField pField) {
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
        public JDataFields getItemFields() {
            return SpotExchangeRate.FIELD_DEFS;
        }

        @Override
        protected SpotExchangeList getEmptyList(final ListStyle pStyle) {
            throw new UnsupportedOperationException();
        }

        /**
         * Obtain the next date.
         * @return the date
         */
        public JDateDay getNext() {
            return theNext;
        }

        /**
         * Obtain the previous date.
         * @return the date
         */
        public JDateDay getPrev() {
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
