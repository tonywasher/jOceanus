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
package net.sourceforge.joceanus.jmoneywise.views;

import java.util.Iterator;
import java.util.ListIterator;

import net.sourceforge.joceanus.jmetis.data.MetisDataEditState;
import net.sourceforge.joceanus.jmetis.data.MetisDataState;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmoneywise.data.analysis.data.MoneyWiseAnalysis;
import net.sourceforge.joceanus.jmoneywise.data.analysis.data.MoneyWiseAnalysisManager;
import net.sourceforge.joceanus.jmoneywise.data.analysis.data.MoneyWiseAnalysisPortfolioBucket;
import net.sourceforge.joceanus.jmoneywise.data.analysis.data.MoneyWiseAnalysisPortfolioBucket.MoneyWiseAnalysisPortfolioBucketList;
import net.sourceforge.joceanus.jmoneywise.data.analysis.data.MoneyWiseAnalysisSecurityBucket;
import net.sourceforge.joceanus.jmoneywise.data.analysis.data.MoneyWiseAnalysisSecurityBucket.MoneyWiseAnalysisSecurityBucketList;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseBasicResource;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWisePortfolio;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseSecurity;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseSecurityPrice;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataItem;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataValues;
import net.sourceforge.joceanus.jprometheus.data.PrometheusEncryptedValues;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;

/**
 * Extension of SecurityPrice to cater for spot prices.
 * @author Tony Washer
 */
public final class MoneyWiseSpotSecurityPrice
        extends MoneyWiseSecurityPrice {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseSpotSecurityPrice.class.getSimpleName();

    /**
     * List name.
     */
    public static final String LIST_NAME = OBJECT_NAME + "s";

    /**
     * Report fields.
     */
    @SuppressWarnings("rawtypes")
    private static final MetisFieldSet<MoneyWiseSpotSecurityPrice> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseSpotSecurityPrice.class);

    /*
     * The fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MoneyWiseViewResource.SPOTEVENT_PREVDATE, MoneyWiseSpotSecurityPrice::getPrevDate);
        FIELD_DEFS.declareLocalField(MoneyWiseViewResource.SPOTPRICE_PREVPRICE, MoneyWiseSpotSecurityPrice::getPrevPrice);
    }

    /**
     * the previous date.
     */
    private TethysDate thePrevDate;

    /**
     * the previous price.
     */
    private TethysPrice thePrevPrice;

    /**
     * isDisabled.
     */
    private boolean isDisabled;

    /**
     * Constructor for a new SpotPrice where no price data exists.
     * @param pList the Spot Price List
     * @param pSecurity the price for the date
     */
    private MoneyWiseSpotSecurityPrice(final MoneyWiseSpotSecurityList pList,
                                       final MoneyWiseSecurity pSecurity) {
        super(pList);

        /* Store base values */
        setSecurity(pSecurity);
    }

    @Override
    public MetisFieldSetDef getDataFieldSet() {
        return FIELD_DEFS;
    }

    /**
     * Obtain previous price.
     * @return the price.
     */
    public TethysPrice getPrevPrice() {
        return thePrevPrice;
    }

    /**
     * Set previous price.
     * @param pPrice the price
     */
    protected void setPrevPrice(final TethysPrice pPrice) {
        thePrevPrice = pPrice;
    }

    /**
     * Obtain previous date.
     * @return the date.
     */
    public TethysDate getPrevDate() {
        return thePrevDate;
    }

    /**
     * Set previous date.
     * @param pDate the date
     */
    protected void setPrevDate(final TethysDate pDate) {
        thePrevDate = pDate;
    }

    @Override
    public boolean isDisabled() {
        return isDisabled;
    }

    /**
     * Set disabled.
     * @param pDisabled the flag
     */
    protected void setDisabled(final boolean pDisabled) {
        isDisabled = pDisabled;
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
    public TethysPrice getPrice() {
        /* Switch on state */
        switch (getState()) {
            case NEW:
            case CHANGED:
            case RECOVERED:
            case CLEAN:
                return super.getPrice();
            default:
                return null;
        }
    }

    @Override
    public MetisDataState getState() {
        final PrometheusEncryptedValues myCurr = getValues();
        final PrometheusEncryptedValues myBase = getOriginalValues();

        /* If we have no changes we are CLEAN */
        if (myCurr.getVersion() == 0) {
            return MetisDataState.CLEAN;
        }

        /* If the original price is Null */
        if (myBase.getValue(MoneyWiseBasicResource.MONEYWISEDATA_FIELD_PRICE) == null) {
            /* Return status */
            return myCurr.getValue(MoneyWiseBasicResource.MONEYWISEDATA_FIELD_PRICE) == null
                    ? MetisDataState.DELNEW
                    : MetisDataState.NEW;
        }

        /* If we are deleted return so */
        return myCurr.getValue(MoneyWiseBasicResource.MONEYWISEDATA_FIELD_PRICE) == null
                ? MetisDataState.DELETED
                : MetisDataState.CHANGED;
    }

    /**
     * The Spot Prices List class.
     */
    public static class MoneyWiseSpotSecurityList
            extends MoneyWiseSecurityPriceBaseList<MoneyWiseSpotSecurityPrice> {
        /**
         * Report fields.
         */
        @SuppressWarnings("rawtypes")
        private static final MetisFieldSet<MoneyWiseSpotSecurityList> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseSpotSecurityList.class);

        /*
         * The fields.
         */
        static {
            FIELD_DEFS.declareLocalField(MoneyWiseBasicDataType.PORTFOLIO, MoneyWiseSpotSecurityList::getPortfolio);
            FIELD_DEFS.declareLocalField(MoneyWiseBasicResource.MONEYWISEDATA_FIELD_DATE, MoneyWiseSpotSecurityList::getDate);
            FIELD_DEFS.declareLocalField(MoneyWiseViewResource.SPOTEVENT_NEXTDATE, MoneyWiseSpotSecurityList::getNext);
            FIELD_DEFS.declareLocalField(MoneyWiseViewResource.SPOTEVENT_PREVDATE, MoneyWiseSpotSecurityList::getPrev);
        }

        /**
         * The date.
         */
        private final TethysDate theDate;

        /**
         * The view.
         */
        private final MoneyWiseView theView;

        /**
         * The portfolio.
         */
        private final MoneyWisePortfolio thePortfolio;

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
         * @param pPortfolio the portfolio
         * @param pDate the date
         */
        public MoneyWiseSpotSecurityList(final MoneyWiseView pView,
                                         final MoneyWisePortfolio pPortfolio,
                                         final TethysDate pDate) {
            /* Build initial list */
            super((MoneyWiseDataSet) pView.getData(), MoneyWiseSpotSecurityPrice.class, MoneyWiseBasicDataType.SECURITYPRICE);
            setStyle(PrometheusListStyle.EDIT);
            ensureMap();

            /* Store parameters */
            theDate = pDate;
            theView = pView;
            thePortfolio = pPortfolio;

            /* Obtain the portfolio bucket */
            final MoneyWiseAnalysisManager myManager = theView.getAnalysisManager();
            final MoneyWiseAnalysis myAnalysis = myManager.getAnalysis();
            final MoneyWiseAnalysisPortfolioBucketList myPortfolios = myAnalysis.getPortfolios();
            final MoneyWiseAnalysisPortfolioBucket myBucket = myPortfolios.findItemById(thePortfolio.getIndexedId());
            if (myBucket == null) {
                return;
            }
            final MoneyWiseAnalysisSecurityBucketList mySecurities = myBucket.getSecurities();

            /* Loop through the Securities */
            final TethysDate myDate = new TethysDate(theDate);
            final Iterator<MoneyWiseAnalysisSecurityBucket> mySecIterator = mySecurities.iterator();
            while (mySecIterator.hasNext()) {
                final MoneyWiseAnalysisSecurityBucket mySecBucket = mySecIterator.next();
                final MoneyWiseSecurity mySecurity = mySecBucket.getSecurity();

                /* Ignore Options */
                if (mySecurity.getCategoryClass().isOption()) {
                    continue;
                }

                /* Create a SpotPrice entry */
                final MoneyWiseSpotSecurityPrice mySpot = new MoneyWiseSpotSecurityPrice(this, mySecurity);
                mySpot.setIndexedId(mySecurity.getIndexedId());
                mySpot.setDate(myDate);
                mySpot.setDisabled(!mySecBucket.isActive());
                add(mySpot);
            }

            /* Set the base for this list */
            final MoneyWiseDataSet myData = (MoneyWiseDataSet) theView.getData();
            final MoneyWiseSecurityPriceList myPrices = myData.getSecurityPrices();
            setBase(myPrices);

            /* Loop through the prices */
            final ListIterator<MoneyWiseSecurityPrice> myIterator = myPrices.listIterator(myPrices.size());
            while (myIterator.hasPrevious()) {
                final MoneyWiseSecurityPrice myPrice = myIterator.previous();

                /* Access the Spot Price and ignore if not relevant/deleted */
                final MoneyWiseSecurity mySecurity = myPrice.getSecurity();
                final MoneyWiseSpotSecurityPrice mySpot = findItemById(mySecurity.getIndexedId());
                if (mySpot == null || myPrice.isDeleted()) {
                    continue;
                }

                /* Test the Date */
                final int iDiff = theDate.compareTo(myPrice.getDate());

                /* If we are past the date */
                if (iDiff < 0) {
                    /* Record the next date and break the loop */
                    theNext = myPrice.getDate();
                    break;
                }

                /* If we are exactly the date */
                if (iDiff == 0) {
                    /* Set price */
                    mySpot.setValuePrice(myPrice.getPriceField());

                    /* Link to base and re-establish state */
                    mySpot.setBase(myPrice);

                    /* else we are a previous date */
                } else {
                    /* Set previous date and value */
                    mySpot.setPrevDate(myPrice.getDate());
                    mySpot.setPrevPrice(myPrice.getPrice());

                    /* Record the latest previous date */
                    thePrev = myPrice.getDate();
                }
            }
        }

        @SuppressWarnings("rawtypes")
        @Override
        public MetisFieldSet<MoneyWiseSpotSecurityList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return MoneyWiseSpotSecurityList.class.getSimpleName();
        }

        @Override
        public MetisFieldSetDef getItemFields() {
            return MoneyWiseSpotSecurityPrice.FIELD_DEFS;
        }

        @Override
        protected MoneyWiseSpotSecurityList getEmptyList(final PrometheusListStyle pStyle) {
            throw new UnsupportedOperationException();
        }

        @Override
        public MoneyWiseDataSet getDataSet() {
            return (MoneyWiseDataSet) super.getDataSet();
        }

        /**
         * Obtain the portfolio.
         * @return the portfolio
         */
        private MoneyWisePortfolio getPortfolio() {
            return thePortfolio;
        }

        /**
         * Obtain the date.
         * @return the date
         */
        private TethysDate getDate() {
            return theDate;
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
        public MoneyWiseSpotSecurityPrice addCopyItem(final PrometheusDataItem pElement) {
            throw new UnsupportedOperationException();
        }

        @Override
        public MoneyWiseSpotSecurityPrice addNewItem() {
            throw new UnsupportedOperationException();
        }

        @Override
        public MoneyWiseSpotSecurityPrice addValuesItem(final PrometheusDataValues pValues) {
            throw new UnsupportedOperationException();
        }
    }
}
