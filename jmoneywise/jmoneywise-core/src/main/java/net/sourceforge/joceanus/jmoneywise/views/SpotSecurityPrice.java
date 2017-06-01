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
package net.sourceforge.joceanus.jmoneywise.views;

import java.util.Iterator;
import java.util.ListIterator;

import net.sourceforge.joceanus.jmetis.lethe.data.MetisDataState;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisEditState;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisEncryptedValueSet;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.analysis.Analysis;
import net.sourceforge.joceanus.jmoneywise.analysis.AnalysisManager;
import net.sourceforge.joceanus.jmoneywise.analysis.PortfolioBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.PortfolioBucket.PortfolioBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.SecurityBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.SecurityBucket.SecurityBucketList;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseDataResource;
import net.sourceforge.joceanus.jmoneywise.data.Portfolio;
import net.sourceforge.joceanus.jmoneywise.data.Security;
import net.sourceforge.joceanus.jmoneywise.data.SecurityPrice;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataItem;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataList;
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
     * Constructor for a new SpotPrice where no price data exists.
     * @param pList the Spot Price List
     * @param pSecurity the price for the date
     */
    private SpotSecurityPrice(final SpotSecurityList<?, ?> pList,
                              final Security pSecurity) {
        super(pList);

        /* Store base values */
        setDate(pList.theDate);
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
     * Obtain previous date.
     * @return the date.
     */
    public TethysDate getPrevDate() {
        return thePrevDate;
    }

    @Override
    public boolean isDisabled() {
        return getSecurity().isClosed();
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
        MetisEncryptedValueSet myCurr = getValueSet();
        MetisEncryptedValueSet myBase = getOriginalValues();

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
         * Local Report fields.
         */
        protected static final MetisFields FIELD_DEFS = new MetisFields(MoneyWiseViewResource.SPOTPRICE_NAME.getValue(), DataList.FIELD_DEFS);

        /**
         * The portfolio field Id.
         */
        public static final MetisField FIELD_PORTFOLIO = FIELD_DEFS.declareLocalField(MoneyWiseDataType.PORTFOLIO.getItemName());

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
         * The portfolio.
         */
        private final Portfolio thePortfolio;

        /**
         * The next date.
         */
        private TethysDate theNext = null;

        /**
         * The previous date.
         */
        private TethysDate thePrev = null;

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
            AnalysisManager myManager = theView.getAnalysisManager();
            Analysis myAnalysis = myManager.getAnalysis();
            PortfolioBucketList myPortfolios = myAnalysis.getPortfolios();
            PortfolioBucket myBucket = myPortfolios.findItemById(thePortfolio.getId());
            SecurityBucketList mySecurities = myBucket.getSecurities();

            /* Loop through the Securities */
            TethysDate myDate = new TethysDate(theDate);
            Iterator<SecurityBucket> mySecIterator = mySecurities.iterator();
            while (mySecIterator.hasNext()) {
                SecurityBucket mySecBucket = mySecIterator.next();
                Security mySecurity = mySecBucket.getSecurity();

                /* Create a SpotPrice entry */
                SpotSecurityPrice mySpot = new SpotSecurityPrice(this, mySecurity);
                mySpot.setId(mySecurity.getId());
                mySpot.setDate(myDate);
                add(mySpot);
            }

            /* Set the base for this list */
            MoneyWiseData myData = theView.getData();
            SecurityPriceList myPrices = myData.getSecurityPrices();
            setBase(myPrices);

            /* Loop through the prices */
            ListIterator<SecurityPrice> myIterator = myPrices.listIterator();
            while (myIterator.hasPrevious()) {
                SecurityPrice myPrice = myIterator.previous();

                /* Ignore deleted prices */
                if (myPrice.isDeleted()) {
                    continue;
                }

                /* Test the Date */
                int iDiff = theDate.compareTo(myPrice.getDate());

                /* If we are past the date */
                if (iDiff < 0) {
                    /* Record the next date and break the loop */
                    theNext = myPrice.getDate();
                    break;
                }

                /* Access the Spot Price and ignore if not relevant */
                Security mySecurity = myPrice.getSecurity();
                SpotSecurityPrice mySpot = findItemById(mySecurity.getId());
                if (mySpot == null) {
                    continue;
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
                    mySpot.thePrevDate = myPrice.getDate();
                    mySpot.thePrevPrice = myPrice.getPrice();

                    /* Record the latest previous date */
                    thePrev = myPrice.getDate();
                }
            }
        }

        @Override
        public MetisFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(final MetisField pField) {
            if (FIELD_PORTFOLIO.equals(pField)) {
                return thePortfolio;
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
