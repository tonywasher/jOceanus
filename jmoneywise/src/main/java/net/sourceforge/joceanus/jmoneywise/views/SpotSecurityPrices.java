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
import java.util.ResourceBundle;

import net.sourceforge.joceanus.jmetis.viewer.DataState;
import net.sourceforge.joceanus.jmetis.viewer.EditState;
import net.sourceforge.joceanus.jmetis.viewer.EncryptedValueSet;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.viewer.JDataObject.JDataContents;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.analysis.Analysis;
import net.sourceforge.joceanus.jmoneywise.analysis.AnalysisManager;
import net.sourceforge.joceanus.jmoneywise.analysis.PortfolioBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.PortfolioBucket.PortfolioBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.SecurityBucket.SecurityBucketList;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.Portfolio;
import net.sourceforge.joceanus.jmoneywise.data.Security;
import net.sourceforge.joceanus.jmoneywise.data.SecurityPrice;
import net.sourceforge.joceanus.jmoneywise.data.SecurityPrice.SecurityPriceList;
import net.sourceforge.joceanus.jprometheus.data.DataItem;
import net.sourceforge.joceanus.jprometheus.data.DataList;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.data.EncryptedItem.EncryptedList;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;
import net.sourceforge.joceanus.jtethys.decimal.JPrice;

/**
 * Extension of AccountPrice to cater for spot prices.
 * @author Tony Washer
 */
public class SpotSecurityPrices
        implements JDataContents {
    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(SpotSecurityPrices.class.getName());

    /**
     * Local Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(NLS_BUNDLE.getString("DataName"));

    @Override
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    /**
     * View Field Id.
     */
    public static final JDataField FIELD_VIEW = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataView"));

    /**
     * Portfolio Field Id.
     */
    public static final JDataField FIELD_PORTFOLIO = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataPortfolio"));

    /**
     * Date Field Id.
     */
    public static final JDataField FIELD_DATE = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataDate"));

    /**
     * Next Field Id.
     */
    public static final JDataField FIELD_NEXT = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataNext"));

    /**
     * Previous Field Id.
     */
    public static final JDataField FIELD_PREV = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataPrevious"));

    /**
     * Prices Field Id.
     */
    public static final JDataField FIELD_PRICES = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataPrices"));

    @Override
    public Object getFieldValue(final JDataField pField) {
        if (FIELD_VIEW.equals(pField)) {
            return theView;
        }
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
        if (FIELD_PRICES.equals(pField)) {
            return thePrices;
        }
        return null;
    }

    @Override
    public String formatObject() {
        return FIELD_DEFS.getName();
    }

    /**
     * The view.
     */
    private final View theView;

    /**
     * The portfolio.
     */
    private final Portfolio thePortfolio;

    /**
     * The date.
     */
    private final JDateDay theDate;

    /**
     * The prices.
     */
    private final SpotSecurityList thePrices;

    /**
     * Obtain portfolio.
     * @return the portfolio
     */
    public Portfolio getPortfolio() {
        return thePortfolio;
    }

    /**
     * Obtain view.
     * @return the view
     */
    protected View getView() {
        return theView;
    }

    /**
     * Obtain dataSet.
     * @return the dataSet
     */
    protected MoneyWiseData getData() {
        return theView.getData();
    }

    /**
     * Obtain date.
     * @return the date
     */
    public JDateDay getDate() {
        return theDate;
    }

    /**
     * Obtain next date.
     * @return the date
     */
    public JDateDay getNext() {
        return thePrices.getNext();
    }

    /**
     * Obtain previous date.
     * @return the date
     */
    public JDateDay getPrev() {
        return thePrices.getPrev();
    }

    /**
     * Obtain prices.
     * @return the prices
     */
    public SpotSecurityList getPrices() {
        return thePrices;
    }

    /**
     * Obtain spotPrice at index.
     * @param uIndex the index
     * @return the spotPrice
     */
    public SpotSecurityPrice get(final long uIndex) {
        return (SpotSecurityPrice) thePrices.get((int) uIndex);
    }

    /**
     * Constructor.
     * @param pView the view
     * @param pPortfolio the portfolio
     * @param pDate the date
     */
    public SpotSecurityPrices(final View pView,
                              final Portfolio pPortfolio,
                              final JDateDay pDate) {
        /* Create a copy of the date and initiate the list */
        theView = pView;
        theDate = pDate;
        thePortfolio = pPortfolio;
        thePrices = new SpotSecurityList(this);
    }

    /**
     * The Spot Prices List class.
     */
    public static class SpotSecurityList
            extends EncryptedList<SpotSecurityPrice, MoneyWiseDataType> {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(NLS_BUNDLE.getString("DataListName"), DataList.FIELD_DEFS);

        /**
         * The portfolio field Id.
         */
        public static final JDataField FIELD_PORTFOLIO = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataPortfolio"));

        /**
         * The date field Id.
         */
        public static final JDataField FIELD_DATE = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataDate"));

        /**
         * The next date field Id.
         */
        public static final JDataField FIELD_NEXT = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataNext"));

        /**
         * The previous date field Id.
         */
        public static final JDataField FIELD_PREV = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataPrevious"));

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(final JDataField pField) {
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
        public JDataFields getItemFields() {
            return SpotSecurityPrice.FIELD_DEFS;
        }

        @Override
        protected SpotSecurityList getEmptyList(final ListStyle pStyle) {
            throw new UnsupportedOperationException();
        }

        /**
         * The date.
         */
        private final JDateDay theDate;

        /**
         * The view.
         */
        private final View theView;

        /**
         * The portfolio.
         */
        private final Portfolio thePortfolio;

        /**
         * The next date.
         */
        private JDateDay theNext = null;

        /**
         * The previous date.
         */
        private JDateDay thePrev = null;

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

        /**
         * Constructor.
         * @param pPrices the spot price control
         */
        public SpotSecurityList(final SpotSecurityPrices pPrices) {
            /* Build initial list */
            super(SpotSecurityPrice.class, pPrices.getData(), MoneyWiseDataType.SECURITYPRICE);
            setStyle(ListStyle.EDIT);
            theDate = pPrices.getDate();
            theView = pPrices.getView();
            thePortfolio = pPrices.getPortfolio();

            /* Obtain the portfolio bucket */
            AnalysisManager myManager = theView.getAnalysisManager();
            Analysis myAnalysis = myManager.getAnalysis();
            PortfolioBucketList myPortfolios = myAnalysis.getPortfolios();
            PortfolioBucket myBucket = myPortfolios.findItemById(thePortfolio.getId());
            SecurityBucketList mySecurities = myBucket.getSecurities();

            /* Loop through the Securities */
            MoneyWiseData myData = theView.getData();
            Iterator<Security> mySecIterator = myData.getSecurities().listIterator();
            while (mySecIterator.hasNext()) {
                Security mySecurity = mySecIterator.next();

                /* Ignore accounts that are wrong portfolio */
                if (mySecurities.findItemById(mySecurity.getId()) == null) {
                    continue;
                }

                /* Create a SpotPrice entry */
                SpotSecurityPrice mySpot = new SpotSecurityPrice(this, mySecurity);
                mySpot.setId(mySecurity.getId());
                mySpot.setDate(new JDateDay(theDate));
                add(mySpot);
            }

            /* Set the base for this list */
            SecurityPriceList myPrices = myData.getSecurityPrices();
            setBase(myPrices);

            /* Loop through the prices */
            Iterator<SecurityPrice> myIterator = myPrices.listIterator();
            while (myIterator.hasNext()) {
                SecurityPrice myPrice = myIterator.next();

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

        /**
         * Calculate the Edit State for the list.
         */
        @Override
        public void findEditState() {
            /* Access the iterator */
            Iterator<SpotSecurityPrice> myIterator = listIterator();
            EditState myEdit = EditState.CLEAN;

            /* Loop through the list */
            while (myIterator.hasNext()) {
                SpotSecurityPrice myCurr = myIterator.next();
                /* Switch on new state */
                switch (myCurr.getState()) {
                    case NEW:
                    case DELETED:
                    case DELCHG:
                    case CHANGED:
                    case RECOVERED:
                        myEdit = EditState.VALID;
                        break;
                    case CLEAN:
                    case DELNEW:
                    default:
                        break;
                }
            }

            /* Set the Edit State */
            setEditState(myEdit);
        }

        @Override
        public boolean hasUpdates() {
            /* Access the iterator */
            Iterator<SpotSecurityPrice> myIterator = listIterator();

            /* Loop through the list */
            while (myIterator.hasNext()) {
                SecurityPrice myCurr = myIterator.next();

                /* Switch on state */
                switch (myCurr.getState()) {
                    case DELETED:
                    case DELCHG:
                    case CHANGED:
                    case RECOVERED:
                    case DELNEW:
                    case NEW:
                        return true;
                    case CLEAN:
                    default:
                        break;
                }
            }

            /* Return no updates */
            return false;
        }
    }

    /**
     * Spot Price class.
     * @author Tony Washer
     */
    public static final class SpotSecurityPrice
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
        private static final JDataFields FIELD_DEFS = new JDataFields(OBJECT_NAME, SecurityPrice.FIELD_DEFS);

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        /**
         * Previous Date field Id.
         */
        public static final JDataField FIELD_PREVDATE = FIELD_DEFS.declareEqualityField(NLS_BUNDLE.getString("DataPrevDate"));

        /**
         * Previous Price field Id.
         */
        public static final JDataField FIELD_PREVPRICE = FIELD_DEFS.declareEqualityField(NLS_BUNDLE.getString("DataPrevPrice"));

        @Override
        public Object getFieldValue(final JDataField pField) {
            if (FIELD_PREVDATE.equals(pField)) {
                return thePrevDate;
            }
            if (FIELD_PREVPRICE.equals(pField)) {
                return thePrevPrice;
            }
            return super.getFieldValue(pField);
        }

        /**
         * the previous date.
         */
        private JDateDay thePrevDate;

        /**
         * the previous price.
         */
        private JPrice thePrevPrice;

        /**
         * Obtain previous price.
         * @return the price.
         */
        public JPrice getPrevPrice() {
            return thePrevPrice;
        }

        /**
         * Obtain previous date.
         * @return the date.
         */
        public JDateDay getPrevDate() {
            return thePrevDate;
        }

        @Override
        public boolean isDisabled() {
            return getSecurity().isClosed();
        }

        @Override
        public SecurityPrice getBase() {
            return (SecurityPrice) super.getBase();
        }

        /**
         * Constructor for a new SpotPrice where no price data exists.
         * @param pList the Spot Price List
         * @param pSecurity the price for the date
         */
        private SpotSecurityPrice(final SpotSecurityList pList,
                                  final Security pSecurity) {
            super(pList, pSecurity);

            /* Store base values */
            setDate(pList.theDate);
            setSecurity(pSecurity);
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
        public JPrice getPrice() {
            /* Switch on state */
            switch (getState()) {
                case NEW:
                case CHANGED:
                case RECOVERED:
                case CLEAN:
                    return super.getPrice();
                    /*
                     * case CLEAN: return (getBase().isDeleted()) ? null : getPrice();
                     */
                default:
                    return null;
            }
        }

        @Override
        public DataState getState() {
            EncryptedValueSet myCurr = getValueSet();
            EncryptedValueSet myBase = getOriginalValues();

            /* If we have no changes we are CLEAN */
            if (myCurr.getVersion() == 0) {
                return DataState.CLEAN;
            }

            /* If the original price is Null */
            if (getPrice(myBase) == null) {
                /* Return status */
                return myCurr.isDeletion()
                                          ? DataState.DELNEW
                                          : DataState.NEW;
            }

            /* If we are deleted return so */
            if (myCurr.isDeletion()) {
                return myBase.isDeletion()
                                          ? DataState.CLEAN
                                          : DataState.DELETED;
            }

            /* Return RECOVERED or CHANGED depending on whether we started as deleted */
            return (myBase.isDeletion())
                                        ? DataState.RECOVERED
                                        : DataState.CHANGED;
        }
    }
}