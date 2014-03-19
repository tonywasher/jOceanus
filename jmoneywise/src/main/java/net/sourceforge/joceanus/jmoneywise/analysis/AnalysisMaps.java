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
import net.sourceforge.joceanus.jmoneywise.data.AssetBase;
import net.sourceforge.joceanus.jmoneywise.data.Deposit;
import net.sourceforge.joceanus.jmoneywise.data.DepositRate;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.Security;
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
            extends NestedHashMap<Integer, SecurityPrices>
            implements JDataFormat {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -218979425405385527L;

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
            Iterator<SecurityPrice> myIterator = pData.getSecurityPrices().iterator();
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
            Security mySecurity = pPrice.getSecurity();
            SecurityPrices myList = get(mySecurity.getId());

            /* If the list is new */
            if (myList == null) {
                /* Allocate list and add to map */
                myList = new SecurityPrices(mySecurity);
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
        public JPrice getPriceForDate(final AssetBase<?> pSecurity,
                                      final JDateDay pDate) {
            /* Access as security */
            Security mySecurity = Security.class.cast(pSecurity);

            /* Initialise price */
            JPrice myPrice = new JPrice();

            /* Access list for security */
            SecurityPrices myList = get(mySecurity.getId());
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
        public JPrice[] getPricesForRange(final Security pSecurity,
                                          final JDateDayRange pRange) {
            /* Set price */
            JPrice myFirst = new JPrice();
            JPrice myLatest = new JPrice();

            /* Access list for security */
            SecurityPrices myList = get(pSecurity.getId());
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
            }

            /* Return the prices */
            return new JPrice[]
            { myFirst, myLatest };
        }
    }

    /**
     * Price List class.
     */
    private static final class SecurityPrices
            extends ArrayList<SecurityPrice>
            implements JDataContents {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 5771676514199191344L;

        /**
         * Report fields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(SecurityPrices.class.getSimpleName());

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
        private final transient Security theSecurity;

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
        private SecurityPrices(final Security pSecurity) {
            /* Store the security */
            theSecurity = pSecurity;
        }
    }

    /**
     * Map of Deposit Rates indexed by Deposit Id.
     */
    public static class DepositRateMap
            extends NestedHashMap<Integer, DepositRates>
            implements JDataFormat {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -152121432889421521L;

        @Override
        public String formatObject() {
            return getClass().getSimpleName();
        }

        /**
         * Constructor.
         * @param pData the dataSet
         */
        protected DepositRateMap(final MoneyWiseData pData) {
            /* Loop through the rates */
            Iterator<DepositRate> myIterator = pData.getDepositRates().iterator();
            while (myIterator.hasNext()) {
                DepositRate myRate = myIterator.next();

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
        private void addRateToMap(final DepositRate pRate) {
            /* Access deposit rates */
            Deposit myDeposit = pRate.getDeposit();
            DepositRates myList = get(myDeposit.getId());

            /* If the list is new */
            if (myList == null) {
                /* Allocate list and add to map */
                myList = new DepositRates(myDeposit);
                put(myDeposit.getId(), myList);
            }

            /* Add the rate to the list */
            myList.add(pRate);
        }

        /**
         * Obtain rate for date.
         * @param pDeposit the deposit
         * @param pDate the date
         * @return the latest rate for the date.
         */
        public DepositRate getRateForDate(final Deposit pDeposit,
                                          final JDateDay pDate) {
            /* Access list for security */
            DepositRates myList = get(pDeposit.getId());
            if (myList != null) {
                /* Loop through the rates */
                Iterator<DepositRate> myIterator = myList.iterator();
                while (myIterator.hasNext()) {
                    DepositRate myCurr = myIterator.next();

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
    private static final class DepositRates
            extends ArrayList<DepositRate>
            implements JDataContents {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 3547312488948000352L;

        /**
         * Report fields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(DepositRates.class.getSimpleName());

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
         * The deposit.
         */
        private final transient Deposit theDeposit;

        @Override
        public String formatObject() {
            return theDeposit.formatObject()
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
         * @param pDeposit the deposit
         */
        private DepositRates(final Deposit pDeposit) {
            /* Store the deposit */
            theDeposit = pDeposit;
        }
    }
}
