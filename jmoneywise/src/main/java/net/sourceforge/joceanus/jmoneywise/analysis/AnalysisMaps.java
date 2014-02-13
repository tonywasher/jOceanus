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
package net.sourceforge.joceanus.jmoneywise.analysis;

import java.util.ArrayList;
import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.list.NestedHashMap;
import net.sourceforge.joceanus.jmetis.viewer.JDataFieldValue;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.viewer.JDataObject.JDataContents;
import net.sourceforge.joceanus.jmetis.viewer.JDataObject.JDataFormat;
import net.sourceforge.joceanus.jmoneywise.data.Account;
import net.sourceforge.joceanus.jmoneywise.data.AccountRate;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.SecurityPrice;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;
import net.sourceforge.joceanus.jtethys.dateday.JDateDayRange;
import net.sourceforge.joceanus.jtethys.decimal.JPrice;

/**
 * Analysis Map classes.
 */
public class AnalysisMaps {
    /**
     * Constructor.
     */
    protected AnalysisMaps() {
    }

    /**
     * Map of Security prices indexed by Security Id.
     */
    public static class SecurityPriceMap
            extends NestedHashMap<Integer, PriceList>
            implements JDataFormat {

        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -4423424113521596997L;

        @Override
        public String formatObject() {
            return getClass().getSimpleName();
        }

        /**
         * Constructor.
         * @param pData the dataSet
         */
        protected SecurityPriceMap(final MoneyWiseData pData) {
            /* Loop through the prices */
            Iterator<SecurityPrice> myIterator = pData.getPrices().iterator();
            while (myIterator.hasNext()) {
                SecurityPrice myPrice = myIterator.next();

                /* Add to the map */
                addPriceToMap(myPrice);

                /* Touch underlying items */
                myPrice.clearActive();
                myPrice.touchUnderlyingItems();
            }
        }

        /**
         * Add price to map.
         * @param pPrice the price to add.
         */
        private void addPriceToMap(final SecurityPrice pPrice) {
            /* Access security prices */
            Account mySecurity = pPrice.getSecurity();
            PriceList myList = get(mySecurity.getId());

            /* If the list is new */
            if (myList == null) {
                /* Allocate list and add to map */
                myList = new PriceList(mySecurity);
                put(mySecurity.getId(), myList);
            }

            /* Add the price to the list */
            myList.add(pPrice);
        }

        /**
         * Obtain price for date.
         * @param pSecurity the security
         * @param pDate the date
         * @return the latest price for the date.
         */
        public JPrice getPriceForDate(final Account pSecurity,
                                      final JDateDay pDate) {
            /* Initialise price */
            JPrice myPrice = new JPrice();

            /* Access list for security */
            PriceList myList = get(pSecurity.getId());
            if (myList != null) {
                /* Loop through the prices */
                Iterator<SecurityPrice> myIterator = myList.iterator();
                while (myIterator.hasNext()) {
                    SecurityPrice myCurr = myIterator.next();

                    /* Break if this is later than the date */
                    if (pDate.compareTo(myCurr.getDate()) > 0) {
                        break;
                    }

                    /* Record as best price */
                    myPrice = myCurr.getPrice();
                }

                /* else if we have an alias */
            } else if (pSecurity.getAlias() != null) {
                /* Return price for alias */
                return getPriceForDate(pSecurity.getAlias(), pDate);
            }

            /* return price */
            return myPrice;
        }

        /**
         * Obtain prices for range.
         * @param pSecurity the security
         * @param pRange the date range
         * @return the two deep array of prices for the range.
         */
        public JPrice[] getPricesForRange(final Account pSecurity,
                                          final JDateDayRange pRange) {
            /* Set price */
            JPrice myFirst = new JPrice();
            JPrice myLatest = new JPrice();

            /* Access list for security */
            PriceList myList = get(pSecurity.getId());
            if (myList != null) {
                /* Loop through the prices */
                Iterator<SecurityPrice> myIterator = myList.iterator();
                while (myIterator.hasNext()) {
                    SecurityPrice myCurr = myIterator.next();

                    /* Check for the range of the date */
                    int iComp = pRange.compareTo(myCurr.getDate());

                    /* Break if this is later than the date */
                    if (iComp < 0) {
                        break;
                    }

                    /* Record as best price */
                    myLatest = myCurr.getPrice();

                    /* Record early date if required */
                    if (iComp > 0) {
                        myFirst = myLatest;
                    }
                }

                /* else if we have an alias */
            } else if (pSecurity.getAlias() != null) {
                /* Return prices for alias */
                return getPricesForRange(pSecurity.getAlias(), pRange);
            }

            /* Return the prices */
            return new JPrice[]
            { myFirst, myLatest };
        }
    }

    /**
     * Price List class.
     */
    private static final class PriceList
            extends ArrayList<SecurityPrice>
            implements JDataContents {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -2152201611765871295L;

        /**
         * Report fields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields("SecurityPriceList");

        @Override
        public JDataFields getDataFields() {
            return FIELD_DEFS;
        }

        /**
         * Size Field Id.
         */
        private static final JDataField FIELD_SIZE = FIELD_DEFS.declareLocalField("Size");

        @Override
        public Object getFieldValue(final JDataField pField) {
            if (FIELD_SIZE.equals(pField)) {
                return size();
            }
            return JDataFieldValue.UNKNOWN;
        }

        /**
         * The security.
         */
        private final transient Account theSecurity;

        @Override
        public String formatObject() {
            return theSecurity.formatObject()
                   + "("
                   + size()
                   + ")";
        }

        @Override
        public String toString() {
            return formatObject();
        }

        /**
         * Constructor.
         * @param pSecurity the security
         */
        private PriceList(final Account pSecurity) {
            /* Store the security */
            theSecurity = pSecurity;
        }
    }

    /**
     * Map of Account Rates indexed by Security Id.
     */
    public static class AccountRateMap
            extends NestedHashMap<Integer, RateList>
            implements JDataFormat {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 7840526844888764877L;

        @Override
        public String formatObject() {
            return getClass().getSimpleName();
        }

        /**
         * Constructor.
         * @param pData the dataSet
         */
        protected AccountRateMap(final MoneyWiseData pData) {
            /* Loop through the rates */
            Iterator<AccountRate> myIterator = pData.getRates().iterator();
            while (myIterator.hasNext()) {
                AccountRate myRate = myIterator.next();

                /* Add to the map */
                addRateToMap(myRate);

                /* Touch underlying items */
                myRate.clearActive();
                myRate.touchUnderlyingItems();
            }
        }

        /**
         * Add rate to map.
         * @param pRate the rate to add.
         */
        private void addRateToMap(final AccountRate pRate) {
            /* Access security prices */
            Account myAccount = pRate.getAccount();
            RateList myList = get(myAccount.getId());

            /* If the list is new */
            if (myList == null) {
                /* Allocate list and add to map */
                myList = new RateList(myAccount);
                put(myAccount.getId(), myList);
            }

            /* Add the rate to the list */
            myList.add(pRate);
        }

        /**
         * Obtain rate for date.
         * @param pAccount the account
         * @param pDate the date
         * @return the latest rate for the date.
         */
        public AccountRate getRateForDate(final Account pAccount,
                                          final JDateDay pDate) {
            /* Access list for security */
            RateList myList = get(pAccount.getId());
            if (myList != null) {
                /* Loop through the rates */
                Iterator<AccountRate> myIterator = myList.iterator();
                while (myIterator.hasNext()) {
                    AccountRate myCurr = myIterator.next();

                    /* Access the date */
                    JDateDay myDate = myCurr.getDate();

                    /* break loop if we have the correct record */
                    if ((myDate == null)
                        || (myDate.compareTo(pDate) >= 0)) {
                        return myCurr;
                    }
                }
            }

            /* return null */
            return null;
        }
    }

    /**
     * Rate List class.
     */
    private static final class RateList
            extends ArrayList<AccountRate>
            implements JDataContents {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -5497123207341099896L;

        /**
         * Report fields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields("AccountRateList");

        @Override
        public JDataFields getDataFields() {
            return FIELD_DEFS;
        }

        /**
         * Size Field Id.
         */
        private static final JDataField FIELD_SIZE = FIELD_DEFS.declareLocalField("Size");

        @Override
        public Object getFieldValue(final JDataField pField) {
            if (FIELD_SIZE.equals(pField)) {
                return size();
            }
            return JDataFieldValue.UNKNOWN;
        }

        /**
         * The account.
         */
        private final transient Account theAccount;

        @Override
        public String formatObject() {
            return theAccount.formatObject()
                   + "("
                   + size()
                   + ")";
        }

        @Override
        public String toString() {
            return formatObject();
        }

        /**
         * Constructor.
         * @param pAccount the account
         */
        private RateList(final Account pAccount) {
            /* Store the account */
            theAccount = pAccount;
        }
    }
}
