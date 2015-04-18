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
package net.sourceforge.joceanus.jmoneywise.data;

import java.util.Currency;
import java.util.HashMap;
import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.data.JDataFieldValue;
import net.sourceforge.joceanus.jmetis.data.JDataFields;
import net.sourceforge.joceanus.jmetis.data.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.data.JDataObject.JDataContents;
import net.sourceforge.joceanus.jmetis.data.JDataObject.JDataFormat;
import net.sourceforge.joceanus.jmetis.list.OrderedList;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.Portfolio.PortfolioList;
import net.sourceforge.joceanus.jmoneywise.data.Security.SecurityList;
import net.sourceforge.joceanus.jmoneywise.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jmoneywise.data.statics.SecurityTypeClass;
import net.sourceforge.joceanus.jprometheus.data.DataItem;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataResource;
import net.sourceforge.joceanus.jprometheus.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;

/**
 * Portfolio/Security combination.
 */
public final class SecurityHolding
        implements JDataContents, TransactionAsset {
    /**
     * Name separator.
     */
    public static final String SECURITYHOLDING_SEP = ":";

    /**
     * New Security Holding text.
     */
    public static final String SECURITYHOLDING_NEW = MoneyWiseDataResource.SECURITYHOLDING_NEW.getValue();

    /**
     * Portfolio shift -> 20 bits giving 1M securities and 4K portfolios.
     */
    private static final int PORTFOLIO_SHIFT = 20;

    /**
     * Id mask -> 20 bits giving 1M securities and 4K portfolios.
     */
    private static final int ID_MASK = 0xFFFFF;

    /**
     * Local Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(SecurityHolding.class.getSimpleName());

    /**
     * Id Field Id.
     */
    public static final JDataField FIELD_ID = FIELD_DEFS.declareLocalField(PrometheusDataResource.DATAITEM_ID.getValue());

    /**
     * Name Field Id.
     */
    public static final JDataField FIELD_NAME = FIELD_DEFS.declareLocalField(PrometheusDataResource.DATAITEM_FIELD_NAME.getValue());

    /**
     * Portfolio Field Id.
     */
    public static final JDataField FIELD_PORTFOLIO = FIELD_DEFS.declareLocalField(MoneyWiseDataType.PORTFOLIO.getItemName());

    /**
     * Security Field Id.
     */
    public static final JDataField FIELD_SECURITY = FIELD_DEFS.declareLocalField(MoneyWiseDataType.SECURITY.getItemName());

    /**
     * Invalid currency combo error.
     */
    public static final String ERROR_CURRENCYCOMBO = MoneyWiseDataResource.SECURITYHOLDING_ERROR_CURRENCYCOMBO.getValue();

    /**
     * The id of the holding.
     */
    private final Integer theId;

    /**
     * The name of the holding.
     */
    private String theName;

    /**
     * The portfolio of the holding.
     */
    private Portfolio thePortfolio;

    /**
     * The security of the holding.
     */
    private Security theSecurity;

    /**
     * Constructor.
     * @param pPortfolio the portfolio
     * @param pSecurity the security
     */
    private SecurityHolding(final Portfolio pPortfolio,
                            final Security pSecurity) {
        /* Set portfolio and security */
        thePortfolio = pPortfolio;
        theSecurity = pSecurity;

        /* Generate the id */
        theId = generateId();
    }

    @Override
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public String formatObject() {
        return getName();
    }

    @Override
    public String toString() {
        return formatObject();
    }

    @Override
    public Object getFieldValue(final JDataField pField) {
        /* Handle fields */
        if (FIELD_ID.equals(pField)) {
            return theId;
        }
        if (FIELD_NAME.equals(pField)) {
            return getName();
        }
        if (FIELD_PORTFOLIO.equals(pField)) {
            return thePortfolio;
        }
        if (FIELD_SECURITY.equals(pField)) {
            return theSecurity;
        }
        return JDataFieldValue.UNKNOWN;
    }

    @Override
    public Boolean isClosed() {
        /* Access constituent parts */
        Portfolio myPortfolio = getPortfolio();
        Security mySecurity = getSecurity();

        /* Test underlying items */
        return myPortfolio.isClosed() || mySecurity.isClosed();
    }

    /**
     * Is the holding deleted?
     * @return true/false
     */
    public boolean isDeleted() {
        /* Access constituent parts */
        Portfolio myPortfolio = getPortfolio();
        Security mySecurity = getSecurity();

        /* Test underlying items */
        return myPortfolio.isDeleted() || mySecurity.isDeleted();
    }

    /**
     * Obtain earliest allowable date.
     * @return the date of the earliest price
     */
    public JDateDay getEarliestDate() {
        /* Access constituent parts */
        Security mySecurity = getSecurity();
        SecurityPrice myPrice = mySecurity.getInitialPrice();

        /* Test underlying items */
        return myPrice == null
                              ? null
                              : myPrice.getDate();
    }

    @Override
    public Integer getId() {
        return theId;
    }

    @Override
    public String getName() {
        if (theName == null) {
            theName = generateName();
        }
        return theName;
    }

    /**
     * Reset the name.
     */
    private void resetName() {
        theName = null;
    }

    @Override
    public Payee getParent() {
        return theSecurity.getParent();
    }

    @Override
    public Boolean isTaxFree() {
        return thePortfolio.isTaxFree();
    }

    @Override
    public Boolean isGross() {
        return thePortfolio.isGross();
    }

    @Override
    public boolean isShares() {
        return theSecurity.isShares();
    }

    @Override
    public boolean isCapital() {
        return theSecurity.isCapital();
    }

    @Override
    public boolean isAutoExpense() {
        return false;
    }

    @Override
    public boolean isHidden() {
        return false;
    }

    /**
     * Obtain Portfolio.
     * @return the portfolio
     */
    public Portfolio getPortfolio() {
        return thePortfolio;
    }

    /**
     * Obtain Security.
     * @return the security
     */
    public Security getSecurity() {
        return theSecurity;
    }

    @Override
    public AssetType getAssetType() {
        return AssetType.SECURITYHOLDING;
    }

    @Override
    public AssetCurrency getAssetCurrency() {
        return thePortfolio.getAssetCurrency();
    }

    @Override
    public Currency getCurrency() {
        return thePortfolio.getCurrency();
    }

    /**
     * Generate the name.
     * @return the name.
     */
    private String generateName() {
        /* Build and return the name */
        StringBuilder myBuilder = new StringBuilder();
        myBuilder.append(thePortfolio.getName());
        myBuilder.append(SECURITYHOLDING_SEP);
        myBuilder.append(theSecurity.getName());
        return myBuilder.toString();
    }

    /**
     * Obtain portfolio name from composite name.
     * @param pName the composite name
     * @return the portfolio name
     */
    private static String getPortfolioName(final String pName) {
        int iIndex = pName.indexOf(SECURITYHOLDING_SEP);
        return (iIndex == -1)
                             ? pName
                             : pName.substring(0, iIndex);
    }

    /**
     * Obtain security name from composite name.
     * @param pName the composite name
     * @return the security name
     */
    private static String getSecurityName(final String pName) {
        int iIndex = pName.indexOf(SECURITYHOLDING_SEP);
        return (iIndex == -1)
                             ? ""
                             : pName.substring(iIndex + 1);
    }

    /**
     * Generate the id.
     * @return the id.
     */
    private Integer generateId() {
        return theSecurity.getId()
               | (thePortfolio.getId() << PORTFOLIO_SHIFT);
    }

    /**
     * Obtain portfolio id from composite id.
     * @param pId the composite id
     * @return the portfolio id
     */
    private static Integer getPortfolioId(final Integer pId) {
        return (pId >>> PORTFOLIO_SHIFT) & ID_MASK;
    }

    /**
     * Obtain security id from composite id.
     * @param pId the composite id
     * @return the security id
     */
    private static Integer getSecurityId(final Integer pId) {
        return pId & ID_MASK;
    }

    /**
     * Obtain the security class.
     * @return the security class.
     */
    private SecurityTypeClass getSecurityTypeClass() {
        /* Check for match */
        return theSecurity.getSecurityTypeClass();
    }

    /**
     * Is this holding the required security class.
     * @param pClass the required security class.
     * @return true/false
     */
    public boolean isSecurityClass(final SecurityTypeClass pClass) {
        /* Check for match */
        return getSecurityTypeClass() == pClass;
    }

    @Override
    public void touchItem(final DataItem<MoneyWiseDataType> pSource) {
        /* Touch references */
        thePortfolio.touchItem(pSource);
        theSecurity.touchItem(pSource);
    }

    @Override
    public boolean equals(final Object pThat) {
        /* Handle the trivial cases */
        if (this == pThat) {
            return true;
        }
        if (pThat == null) {
            return false;
        }
        if (!(pThat instanceof SecurityHolding)) {
            return false;
        }

        /* Compare the Portfolios */
        SecurityHolding myThat = (SecurityHolding) pThat;
        if (!getPortfolio().equals(myThat.getPortfolio())) {
            return false;
        }

        /* Compare securities */
        return getSecurity().equals(myThat.getSecurity());
    }

    @Override
    public int hashCode() {
        int myHash = JDataFields.HASH_PRIME * getPortfolio().hashCode();
        return myHash + getSecurity().hashCode();
    }

    @Override
    public int compareTo(final TransactionAsset pThat) {
        /* Handle the trivial cases */
        if (this.equals(pThat)) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* If the Asset is not a SecurityHolding we are last */
        if (!(pThat instanceof SecurityHolding)) {
            return 1;
        }

        /* Access as AssetBase */
        SecurityHolding myThat = (SecurityHolding) pThat;

        /* Compare portfolios */
        int iDiff = getPortfolio().compareTo(myThat.getPortfolio());
        if (iDiff == 0) {
            /* Compare securities */
            iDiff = getSecurity().compareTo(myThat.getSecurity());
        }

        /* Return the result */
        return iDiff;
    }

    /**
     * Are the currencies the same?
     * @return true/false
     */
    protected boolean validCurrencies() {
        return thePortfolio.getCurrency().equals(theSecurity.getCurrency());
    }

    /**
     * SecurityHolding Map.
     */
    public static class SecurityHoldingMap
            extends HashMap<Integer, PortfolioHoldingsMap>
            implements JDataFormat {
        /**
         * SerialId.
         */
        private static final long serialVersionUID = -4221855114560425582L;

        /**
         * Underlying portfolio list.
         */
        private final transient PortfolioList thePortfolios;

        /**
         * Underlying security list.
         */
        private final transient SecurityList theSecurities;

        /**
         * Constructor.
         * @param pData the dataSet
         */
        public SecurityHoldingMap(final MoneyWiseData pData) {
            /* Access lists */
            thePortfolios = pData.getPortfolios();
            theSecurities = pData.getSecurities();
        }

        /**
         * Constructor.
         * @param pUpdateSet the updateSet
         */
        public SecurityHoldingMap(final UpdateSet<MoneyWiseDataType> pUpdateSet) {
            /* Access lists */
            thePortfolios = pUpdateSet.getDataList(MoneyWiseDataType.PORTFOLIO, PortfolioList.class);
            theSecurities = pUpdateSet.getDataList(MoneyWiseDataType.SECURITY, SecurityList.class);
        }

        @Override
        public String formatObject() {
            return SecurityHoldingMap.class.getSimpleName();
        }

        /**
         * Look up a security holding.
         * @param pId the id of the security holding
         * @return the security holding
         */
        public SecurityHolding findHoldingById(final Integer pId) {
            /* Access the component IDs */
            Integer myPortId = getPortfolioId(pId);
            Integer mySecId = getSecurityId(pId);

            /* Look up security map for portfolio */
            PortfolioHoldingsMap myMap = getMapForPortfolio(myPortId);
            return myMap == null
                                ? null
                                : myMap.getHoldingForSecurity(mySecId);
        }

        /**
         * Look up a security holding.
         * @param pName the name of the security holding
         * @return the security holding
         */
        public SecurityHolding findHoldingByName(final String pName) {
            /* Access the component Names */
            String myPortName = getPortfolioName(pName);
            String mySecName = getSecurityName(pName);

            /* Look up security map for portfolio */
            PortfolioHoldingsMap myMap = getMapForPortfolio(myPortName);
            return myMap == null
                                ? null
                                : myMap.getHoldingForSecurity(mySecName);
        }

        /**
         * Declare holding.
         * @param pPortfolio the portfolio
         * @param pSecurity the security
         * @return the holding
         */
        public SecurityHolding declareHolding(final Portfolio pPortfolio,
                                              final Security pSecurity) {
            /* Access the portfolio map */
            PortfolioHoldingsMap myMap = getMapForPortfolio(pPortfolio.getId());

            /* Look up existing holding */
            Integer myId = pSecurity.getId();
            SecurityHolding myHolding = myMap.get(myId);

            /* If the holding does not currently exist */
            if (myHolding == null) {
                /* Create the holding and store it */
                myHolding = new SecurityHolding(pPortfolio, pSecurity);
                myMap.put(myId, myHolding);
            }

            /* Return the holding */
            return myHolding;
        }

        /**
         * Obtain or allocate the map for the portfolio.
         * @param pId the id of the portfolio
         * @return the map
         */
        private PortfolioHoldingsMap getMapForPortfolio(final Integer pId) {
            /* Look up in the map */
            PortfolioHoldingsMap myMap = get(pId);

            /* If the Id is not found */
            if (myMap == null) {
                /* Look up the portfolio as a double check */
                Portfolio myPortfolio = thePortfolios.findItemById(pId);

                /* Reject if no such portfolio */
                if ((myPortfolio == null) || (myPortfolio.isDeleted())) {
                    return null;
                }

                /* Allocate and store the map */
                myMap = new PortfolioHoldingsMap(myPortfolio, theSecurities);
                put(pId, myMap);
            }

            /* Return the map */
            return myMap;
        }

        /**
         * Obtain or allocate the map for the portfolio.
         * @param pName the name of the portfolio
         * @return the map
         */
        private PortfolioHoldingsMap getMapForPortfolio(final String pName) {
            /* Look up the portfolio */
            Portfolio myPortfolio = thePortfolios.findItemByName(pName);

            /* Reject if no such portfolio */
            if (myPortfolio == null) {
                return null;
            }

            /* Look up in the map */
            Integer myId = myPortfolio.getId();
            PortfolioHoldingsMap myMap = get(myId);

            /* If the Id is not found */
            if (myMap == null) {
                /* Allocate and store the map */
                myMap = new PortfolioHoldingsMap(myPortfolio, theSecurities);
                put(myId, myMap);
            }

            /* Return the map */
            return myMap;
        }

        /**
         * Reset names.
         */
        public void resetNames() {
            /* Iterate through the portfolio maps */
            Iterator<PortfolioHoldingsMap> myIterator = values().iterator();
            while (myIterator.hasNext()) {
                PortfolioHoldingsMap myMap = myIterator.next();

                /* If the portfolio is not deleted */
                if (!myMap.thePortfolio.isDeleted()) {
                    /* Reset names for the portfolio */
                    myMap.resetNames();
                }
            }
        }

        /**
         * DeRegister Portfolio.
         * @param pPortfolio the portfolio
         */
        public void deRegister(final Portfolio pPortfolio) {
            /* Ensure that we do not reference this portfolio */
            Integer myId = pPortfolio.getId();
            remove(myId);
        }

        /**
         * DeRegister Security.
         * @param pSecurity the security
         */
        public void deRegister(final Security pSecurity) {
            /* Access the id */
            Integer myId = pSecurity.getId();

            /* Iterate through the portfolio maps */
            Iterator<PortfolioHoldingsMap> myIterator = values().iterator();
            while (myIterator.hasNext()) {
                PortfolioHoldingsMap myMap = myIterator.next();

                /* Ensure that we do not reference this security */
                myMap.remove(myId);
            }
        }

        /**
         * Obtain existing holding list for Portfolio.
         * @param pPortfolio the portfolio
         * @return the iterator
         */
        public Iterator<SecurityHolding> existingIterator(final Portfolio pPortfolio) {
            /* Look up in the map */
            PortfolioHoldingsMap myMap = get(pPortfolio.getId());

            /* return the iterator */
            return myMap == null
                                ? null
                                : myMap.existingIterator();
        }

        /**
         * Obtain new holding list for Portfolio.
         * @param pPortfolio the portfolio
         * @return the iterator
         */
        public Iterator<SecurityHolding> newIterator(final Portfolio pPortfolio) {
            return fullIterator(pPortfolio, null);
        }

        /**
         * Obtain new holding list for Portfolio.
         * @param pPortfolio the portfolio
         * @param pClass the class of holdings or null for all
         * @return the iterator
         */
        public Iterator<SecurityHolding> newIterator(final Portfolio pPortfolio,
                                                     final SecurityTypeClass pClass) {
            /* Look up in the map */
            PortfolioHoldingsMap myMap = get(pPortfolio.getId());

            /* return the iterator */
            return myMap == null
                                ? fullIterator(pPortfolio, pClass)
                                : myMap.newIterator(pClass);
        }

        /**
         * Obtain new holding list for Portfolio.
         * @param pPortfolio the portfolio
         * @param pClass the class of holdings or null for all
         * @return the iterator
         */
        private Iterator<SecurityHolding> fullIterator(final Portfolio pPortfolio,
                                                       final SecurityTypeClass pClass) {
            /* If the portfolio is closed/deleted */
            if (pPortfolio.isClosed() || pPortfolio.isDeleted()) {
                /* No iterator */
                return null;
            }

            /* Create an empty list */
            Currency myCurrency = pPortfolio.getCurrency();
            OrderedList<SecurityHolding> myList = new OrderedList<SecurityHolding>(SecurityHolding.class);

            /* Loop through the securities */
            Iterator<Security> myIterator = theSecurities.iterator();
            while (myIterator.hasNext()) {
                Security mySecurity = myIterator.next();

                /* Ignore closed/deleted and wrong class/currency */
                boolean bIgnore = mySecurity.isClosed() || mySecurity.isDeleted();
                bIgnore |= pClass != null && !pClass.equals(mySecurity.getSecurityTypeClass());
                bIgnore |= !myCurrency.equals(mySecurity.getCurrency());
                if (bIgnore) {
                    continue;
                }

                /* Create a new holding and add to the list */
                SecurityHolding myHolding = new SecurityHolding(pPortfolio, mySecurity);
                myList.append(myHolding);
            }

            /* Sort list or delete if empty */
            if (myList.isEmpty()) {
                myList = null;
            } else {
                myList.reSort();
            }

            /* return iterator */
            return myList == null
                                 ? null
                                 : myList.iterator();
        }
    }

    /**
     * PortfolioHoldings Map.
     */
    private static final class PortfolioHoldingsMap
            extends HashMap<Integer, SecurityHolding>
            implements JDataFormat {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -5532168973693014505L;

        /**
         * Portfolio.
         */
        private final transient Portfolio thePortfolio;

        /**
         * Underlying security list.
         */
        private final transient SecurityList theSecurities;

        /**
         * Constructor.
         * @param pPortfolio the portfolio
         * @param pSecurities the security list
         */
        private PortfolioHoldingsMap(final Portfolio pPortfolio,
                                     final SecurityList pSecurities) {
            thePortfolio = pPortfolio;
            theSecurities = pSecurities;
        }

        @Override
        public String formatObject() {
            return thePortfolio.formatObject();
        }

        @Override
        public String toString() {
            return formatObject();
        }

        /**
         * Obtain or allocate the holding for the security.
         * @param pId the id of the security
         * @return the holding
         */
        private SecurityHolding getHoldingForSecurity(final Integer pId) {
            /* Look up in the map */
            SecurityHolding myHolding = get(pId);

            /* If the Id is not found */
            if (myHolding == null) {
                /* Look up the security as a double check */
                Security mySecurity = theSecurities.findItemById(pId);

                /* Reject if no such security */
                if ((mySecurity == null) || (mySecurity.isDeleted())) {
                    return null;
                }

                /* Allocate the holding */
                myHolding = new SecurityHolding(thePortfolio, mySecurity);

                /* Only store the holding if it is correct currency */
                if (thePortfolio.getCurrency().equals(mySecurity.getCurrency())) {
                    put(pId, myHolding);
                }
            }

            /* Return the holding */
            return myHolding;
        }

        /**
         * Obtain or allocate the holding for the security.
         * @param pName the name of the security
         * @return the holding
         */
        private SecurityHolding getHoldingForSecurity(final String pName) {
            /* Look up the security */
            Security mySecurity = theSecurities.findItemByName(pName);

            /* Reject if no such security */
            if (mySecurity == null) {
                return null;
            }

            /* Look up in the map */
            Integer myId = mySecurity.getId();
            SecurityHolding myHolding = get(myId);

            /* If the Id is not found */
            if (myHolding == null) {
                /* Allocate the holding */
                myHolding = new SecurityHolding(thePortfolio, mySecurity);

                /* Only store the holding if it is correct currency */
                if (thePortfolio.getCurrency().equals(mySecurity.getCurrency())) {
                    put(myId, myHolding);
                }
            }

            /* Return the holding */
            return myHolding;
        }

        /**
         * Reset names.
         */
        private void resetNames() {
            /* Iterate through the security holdings */
            Iterator<SecurityHolding> myIterator = values().iterator();
            while (myIterator.hasNext()) {
                SecurityHolding myHolding = myIterator.next();

                /* Reset the name */
                myHolding.resetName();
            }
        }

        /**
         * Obtain existing holding list for Portfolio.
         * @return the iterator
         */
        private Iterator<SecurityHolding> existingIterator() {
            /* If the portfolio is closed/deleted */
            if (thePortfolio.isClosed() || thePortfolio.isDeleted()) {
                /* No iterator */
                return null;
            }

            /* Create an empty list */
            OrderedList<SecurityHolding> myList = new OrderedList<SecurityHolding>(SecurityHolding.class);

            /* Loop through the holdings */
            Iterator<SecurityHolding> myIterator = values().iterator();
            while (myIterator.hasNext()) {
                SecurityHolding myHolding = myIterator.next();
                Security mySecurity = myHolding.getSecurity();

                /* Ignore closed/deleted */
                if (!mySecurity.isClosed() && !mySecurity.isDeleted()) {
                    /* Add to the list */
                    myList.append(myHolding);
                }
            }

            /* Sort list or delete if empty */
            if (myList.isEmpty()) {
                myList = null;
            } else {
                myList.reSort();
            }

            /* return iterator */
            return myList == null
                                 ? null
                                 : myList.iterator();
        }

        /**
         * Obtain new holding list for Portfolio.
         * @param pClass the class of holdings or null for all
         * @return the iterator
         */
        private Iterator<SecurityHolding> newIterator(final SecurityTypeClass pClass) {
            /* If the portfolio is closed/deleted */
            if (thePortfolio.isClosed() || thePortfolio.isDeleted()) {
                /* No iterator */
                return null;
            }

            /* Create an empty list */
            Currency myCurrency = thePortfolio.getCurrency();
            OrderedList<SecurityHolding> myList = new OrderedList<SecurityHolding>(SecurityHolding.class);

            /* Loop through the securities */
            Iterator<Security> myIterator = theSecurities.iterator();
            while (myIterator.hasNext()) {
                Security mySecurity = myIterator.next();

                /* Ignore closed/deleted and wrong class/currency */
                boolean bIgnore = mySecurity.isClosed() || mySecurity.isDeleted();
                bIgnore |= pClass != null && pClass.equals(mySecurity.getSecurityTypeClass());
                bIgnore |= !myCurrency.equals(mySecurity.getCurrency());
                if (bIgnore) {
                    continue;
                }

                /* Only add if not currently present */
                if (get(mySecurity.getId()) == null) {
                    /* Create a new holding and add to the list */
                    SecurityHolding myHolding = new SecurityHolding(thePortfolio, mySecurity);
                    myList.append(myHolding);
                }
            }

            /* Sort list or delete if empty */
            if (myList.isEmpty()) {
                myList = null;
            } else {
                myList.reSort();
            }

            /* return iterator */
            return myList == null
                                 ? null
                                 : myList.iterator();
        }
    }
}
