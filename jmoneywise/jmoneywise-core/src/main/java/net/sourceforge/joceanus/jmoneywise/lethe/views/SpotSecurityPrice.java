/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2018 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.lethe.views;

import java.util.Iterator;
import java.util.ListIterator;

import net.sourceforge.joceanus.jmetis.data.MetisDataEditState;
import net.sourceforge.joceanus.jmetis.data.MetisDataState;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisEncryptedValueSet;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.Analysis;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.AnalysisManager;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.PortfolioBucket;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.PortfolioBucket.PortfolioBucketList;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.SecurityBucket;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.SecurityBucket.SecurityBucketList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseDataResource;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Portfolio;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Security;
import net.sourceforge.joceanus.jmoneywise.lethe.data.SecurityPrice;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataItem;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;

/**
 * Extension of SecurityPrice to cater for spot prices.
 * @author Tony Washer
 */
public final class SpotSecurityPrice
        extends SecurityPrice {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = SpotSecurityPrice.class.getSimpleName();

    /**
     * List name.
     */
    public static final String LIST_NAME = OBJECT_NAME + "s";

    /**
     * Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(OBJECT_NAME, SecurityPrice.FIELD_DEFS);

    /**
     * Previous Date field Id.
     */
    public static final MetisField FIELD_PREVDATE = FIELD_DEFS.declareEqualityField(MoneyWiseViewResource.SPOTEVENT_PREVDATE.getValue());

    /**
     * Previous Price field Id.
     */
    public static final MetisField FIELD_PREVPRICE = FIELD_DEFS.declareEqualityField(MoneyWiseViewResource.SPOTPRICE_PREVPRICE.getValue());

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
    private SpotSecurityPrice(final SpotSecurityList<?, ?> pList,
                              final Security pSecurity) {
        super(pList);

        /* Store base values */
        setSecurity(pSecurity);
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
        if (FIELD_PREVPRICE.equals(pField)) {
            return thePrevPrice;
        }
        return super.getFieldValue(pField);
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
        final MetisEncryptedValueSet myCurr = getValueSet();
        final MetisEncryptedValueSet myBase = getOriginalValues();

        /* If we have no changes we are CLEAN */
        if (myCurr.getVersion() == 0) {
            return MetisDataState.CLEAN;
        }

        /* If the original price is Null */
        if (getPrice(myBase) == null) {
            /* Return status */
            return getPrice(myCurr) == null
                                            ? MetisDataState.DELNEW
                                            : MetisDataState.NEW;
        }

        /* If we are deleted return so */
        return getPrice(myCurr) == null
                                        ? MetisDataState.DELETED
                                        : MetisDataState.CHANGED;
    }

    /**
     * The Spot Prices List class.
     * @param <N> the node type
     * @param <I> the icon type
     */
    public static class SpotSecurityList<N, I>
            extends SecurityPriceBaseList<SpotSecurityPrice> {
        /**
         * Report fields.
         */
        @SuppressWarnings("rawtypes")
        private static final MetisFieldSet<SpotSecurityList> FIELD_DEFS = MetisFieldSet.newFieldSet(SpotSecurityList.class);

        /**
         * The fields.
         */
        static {
            FIELD_DEFS.declareLocalField(MoneyWiseDataType.PORTFOLIO, SpotSecurityList::getPortfolio);
            FIELD_DEFS.declareLocalField(MoneyWiseDataResource.MONEYWISEDATA_FIELD_DATE, SpotSecurityList::getDate);
            FIELD_DEFS.declareLocalField(MoneyWiseViewResource.SPOTEVENT_NEXTDATE, SpotSecurityList::getNext);
            FIELD_DEFS.declareLocalField(MoneyWiseViewResource.SPOTEVENT_PREVDATE, SpotSecurityList::getPrev);
        }

        /**
         * The date.
         */
        private final TethysDate theDate;

        /**
         * The view.
         */
        private final View<N, I> theView;

        /**
         * The portfolio.
         */
        private final Portfolio thePortfolio;

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
        public SpotSecurityList(final View<N, I> pView,
                                final Portfolio pPortfolio,
                                final TethysDate pDate) {
            /* Build initial list */
            super(pView.getData(), SpotSecurityPrice.class, MoneyWiseDataType.SECURITYPRICE);
            setStyle(ListStyle.EDIT);
            ensureMap();

            /* Store parameters */
            theDate = pDate;
            theView = pView;
            thePortfolio = pPortfolio;

            /* Obtain the portfolio bucket */
            final AnalysisManager myManager = theView.getAnalysisManager();
            final Analysis myAnalysis = myManager.getAnalysis();
            final PortfolioBucketList myPortfolios = myAnalysis.getPortfolios();
            final PortfolioBucket myBucket = myPortfolios.findItemById(thePortfolio.getId());
            if (myBucket == null) {
                return;
            }
            final SecurityBucketList mySecurities = myBucket.getSecurities();

            /* Loop through the Securities */
            final TethysDate myDate = new TethysDate(theDate);
            final Iterator<SecurityBucket> mySecIterator = mySecurities.iterator();
            while (mySecIterator.hasNext()) {
                final SecurityBucket mySecBucket = mySecIterator.next();
                final Security mySecurity = mySecBucket.getSecurity();

                /* Ignore Options */
                if (mySecurity.getSecurityTypeClass().isOption()) {
                    continue;
                }

                /* Create a SpotPrice entry */
                final SpotSecurityPrice mySpot = new SpotSecurityPrice(this, mySecurity);
                mySpot.setId(mySecurity.getId());
                mySpot.setDate(myDate);
                mySpot.setDisabled(!mySecBucket.isActive());
                add(mySpot);
            }

            /* Set the base for this list */
            final MoneyWiseData myData = theView.getData();
            final SecurityPriceList myPrices = myData.getSecurityPrices();
            setBase(myPrices);

            /* Loop through the prices */
            final ListIterator<SecurityPrice> myIterator = myPrices.listIterator(myPrices.size());
            while (myIterator.hasPrevious()) {
                final SecurityPrice myPrice = myIterator.previous();

                /* Access the Spot Price and ignore if not relevant/deleted */
                final Security mySecurity = myPrice.getSecurity();
                final SpotSecurityPrice mySpot = findItemById(mySecurity.getId());
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
        public MetisFieldSet<SpotSecurityList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return SpotSecurityList.class.getSimpleName();
        }

        @Override
        public MetisFields getItemFields() {
            return SpotSecurityPrice.FIELD_DEFS;
        }

        @Override
        protected SpotSecurityList<N, I> getEmptyList(final ListStyle pStyle) {
            throw new UnsupportedOperationException();
        }

        @Override
        public MoneyWiseData getDataSet() {
            return (MoneyWiseData) super.getDataSet();
        }

        /**
         * Obtain the portfolio.
         * @return the portfolio
         */
        private Portfolio getPortfolio() {
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
        public SpotSecurityPrice addCopyItem(final DataItem<?> pElement) {
            throw new UnsupportedOperationException();
        }

        @Override
        public SpotSecurityPrice addNewItem() {
            throw new UnsupportedOperationException();
        }

        @Override
        public SpotSecurityPrice addValuesItem(final DataValues<MoneyWiseDataType> pValues) {
            throw new UnsupportedOperationException();
        }
    }
}
