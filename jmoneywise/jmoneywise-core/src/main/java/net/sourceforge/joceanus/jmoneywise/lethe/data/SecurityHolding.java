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
package net.sourceforge.joceanus.jmoneywise.lethe.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataMap;
import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataObjectFormat;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Portfolio.PortfolioList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Security.SecurityList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.SecurityTypeClass;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataItem;
import net.sourceforge.joceanus.jprometheus.lethe.data.PrometheusDataResource;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateSet;

/**
 * Portfolio/Security combination.
 */
public final class SecurityHolding
        implements MetisFieldItem, TransactionAsset {
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
     * Report fields.
     */
    private static final MetisFieldSet<SecurityHolding> FIELD_DEFS = MetisFieldSet.newFieldSet(SecurityHolding.class);

    /**
     * UnderlyingMap Field Id.
     */
    static {
        FIELD_DEFS.declareLocalField(PrometheusDataResource.DATAITEM_ID, SecurityHolding::getId);
        FIELD_DEFS.declareLocalField(PrometheusDataResource.DATAITEM_FIELD_NAME, SecurityHolding::getName);
        FIELD_DEFS.declareLocalField(MoneyWiseDataType.PORTFOLIO, SecurityHolding::getPortfolio);
        FIELD_DEFS.declareLocalField(MoneyWiseDataType.SECURITY, SecurityHolding::getSecurity);
    }

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
    public MetisFieldSet<SecurityHolding> getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public String formatObject(final MetisDataFormatter pFormatter) {
        return toString();
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public Boolean isClosed() {
        /* Access constituent parts */
        final Portfolio myPortfolio = getPortfolio();
        final Security mySecurity = getSecurity();

        /* Test underlying items */
        return myPortfolio.isClosed()
               || mySecurity.isClosed();
    }

    /**
     * Is the holding deleted?
     * @return true/false
     */
    public boolean isDeleted() {
        /* Access constituent parts */
        final Portfolio myPortfolio = getPortfolio();
        final Security mySecurity = getSecurity();

        /* Test underlying items */
        return myPortfolio.isDeleted()
               || mySecurity.isDeleted();
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
    protected void resetName() {
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
        return theSecurity.getAssetCurrency();
    }

    @Override
    public Currency getCurrency() {
        return getAssetCurrency().getCurrency();
    }

    @Override
    public Boolean isForeign() {
        final AssetCurrency myDefault = thePortfolio.getDataSet().getDefaultCurrency();
        return !myDefault.equals(getAssetCurrency());
    }

    /**
     * Generate the name.
     * @return the name.
     */
    private String generateName() {
        /* Build and return the name */
        final StringBuilder myBuilder = new StringBuilder();
        myBuilder.append(thePortfolio.getName());
        myBuilder.append(SECURITYHOLDING_SEP);
        myBuilder.append(theSecurity.getName());
        return myBuilder.toString();
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
        final SecurityHolding myThat = (SecurityHolding) pThat;
        if (!getPortfolio().equals(myThat.getPortfolio())) {
            return false;
        }

        /* Compare securities */
        return getSecurity().equals(myThat.getSecurity());
    }

    @Override
    public int hashCode() {
        final int myHash = MetisFields.HASH_PRIME * getPortfolio().hashCode();
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
        final SecurityHolding myThat = (SecurityHolding) pThat;

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
            implements MetisDataObjectFormat, MetisDataMap<Integer, PortfolioHoldingsMap> {
        /**
         * Underlying portfolio list.
         */
        private final PortfolioList thePortfolios;

        /**
         * Underlying security list.
         */
        private final SecurityList theSecurities;

        /**
         * The underlying map.
         */
        private final Map<Integer, PortfolioHoldingsMap> theMap;

        /**
         * Constructor.
         * @param pData the dataSet
         */
        public SecurityHoldingMap(final MoneyWiseData pData) {
            /* Access lists */
            thePortfolios = pData.getPortfolios();
            theSecurities = pData.getSecurities();
            theMap = new HashMap<>();
        }

        /**
         * Constructor.
         * @param pUpdateSet the updateSet
         */
        public SecurityHoldingMap(final UpdateSet<MoneyWiseDataType> pUpdateSet) {
            /* Access lists */
            thePortfolios = pUpdateSet.getDataList(MoneyWiseDataType.PORTFOLIO, PortfolioList.class);
            theSecurities = pUpdateSet.getDataList(MoneyWiseDataType.SECURITY, SecurityList.class);
            theMap = new HashMap<>();
        }

        @Override
        public Map<Integer, PortfolioHoldingsMap> getUnderlyingMap() {
            return theMap;
        }

        @Override
        public String formatObject(final MetisDataFormatter pFormatter) {
            return SecurityHoldingMap.class.getSimpleName();
        }

        /**
         * Look up a security holding.
         * @param pId the id of the security holding
         * @return the security holding
         */
        public SecurityHolding findHoldingById(final Integer pId) {
            /* Access the component IDs */
            final Integer myPortId = getPortfolioId(pId);
            final Integer mySecId = getSecurityId(pId);

            /* Look up security map for portfolio */
            final PortfolioHoldingsMap myMap = getMapForPortfolio(myPortId);
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
            final String myPortName = getPortfolioName(pName);
            final String mySecName = getSecurityName(pName);

            /* Look up security map for portfolio */
            final PortfolioHoldingsMap myMap = getMapForPortfolio(myPortName);
            return myMap == null
                                 ? null
                                 : myMap.getHoldingForSecurity(mySecName);
        }

        /**
         * Obtain portfolio name from composite name.
         * @param pName the composite name
         * @return the portfolio name
         */
        private static String getPortfolioName(final String pName) {
            final int iIndex = pName.indexOf(SECURITYHOLDING_SEP);
            return iIndex == -1
                                ? pName
                                : pName.substring(0, iIndex);
        }

        /**
         * Obtain security name from composite name.
         * @param pName the composite name
         * @return the security name
         */
        private static String getSecurityName(final String pName) {
            final int iIndex = pName.indexOf(SECURITYHOLDING_SEP);
            return iIndex == -1
                                ? ""
                                : pName.substring(iIndex + 1);
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
         * Declare holding.
         * @param pPortfolio the portfolio
         * @param pSecurity the security
         * @return the holding
         */
        public SecurityHolding declareHolding(final Portfolio pPortfolio,
                                              final Security pSecurity) {
            /* Access the portfolio map */
            final PortfolioHoldingsMap myPortMap = getMapForPortfolio(pPortfolio.getId());
            if (myPortMap == null) {
                throw new IllegalStateException("Invalid Portfolio");
            }
            final Map<Integer, SecurityHolding> myMap = myPortMap.getUnderlyingMap();

            /* Look up existing holding */
            final Integer myId = pSecurity.getId();
            return myMap.computeIfAbsent(myId, i -> new SecurityHolding(pPortfolio, pSecurity));
        }

        /**
         * Obtain or allocate the map for the portfolio.
         * @param pId the id of the portfolio
         * @return the map
         */
        private PortfolioHoldingsMap getMapForPortfolio(final Integer pId) {
            /* Look up in the map */
            PortfolioHoldingsMap myMap = theMap.get(pId);

            /* If the Id is not found */
            if (myMap == null) {
                /* Look up the portfolio as a double check */
                final Portfolio myPortfolio = thePortfolios.findItemById(pId);

                /* Reject if no such portfolio */
                if (myPortfolio == null
                    || myPortfolio.isDeleted()) {
                    return null;
                }

                /* Allocate and store the map */
                myMap = new PortfolioHoldingsMap(myPortfolio, theSecurities);
                theMap.put(pId, myMap);
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
            final Portfolio myPortfolio = thePortfolios.findItemByName(pName);

            /* Reject if no such portfolio */
            if (myPortfolio == null) {
                return null;
            }

            /* Look up in the map */
            final Integer myId = myPortfolio.getId();
            return theMap.computeIfAbsent(myId, i -> new PortfolioHoldingsMap(myPortfolio, theSecurities));
        }

        /**
         * Reset names.
         */
        public void resetNames() {
            /* Iterate through the portfolio maps */
            final Iterator<PortfolioHoldingsMap> myIterator = theMap.values().iterator();
            while (myIterator.hasNext()) {
                final PortfolioHoldingsMap myMap = myIterator.next();

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
            final Integer myId = pPortfolio.getId();
            theMap.remove(myId);
        }

        /**
         * DeRegister Security.
         * @param pSecurity the security
         */
        public void deRegister(final Security pSecurity) {
            /* Access the id */
            final Integer myId = pSecurity.getId();

            /* Iterate through the portfolio maps */
            final Iterator<PortfolioHoldingsMap> myIterator = theMap.values().iterator();
            while (myIterator.hasNext()) {
                final PortfolioHoldingsMap myMap = myIterator.next();

                /* Ensure that we do not reference this security */
                myMap.getUnderlyingMap().remove(myId);
            }
        }

        /**
         * Obtain existing holding list for Portfolio.
         * @param pPortfolio the portfolio
         * @return the iterator
         */
        public Iterator<SecurityHolding> existingIterator(final Portfolio pPortfolio) {
            /* Look up in the map */
            final PortfolioHoldingsMap myMap = theMap.get(pPortfolio.getId());

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
            return newIterator(pPortfolio, null);
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
            final PortfolioHoldingsMap myMap = theMap.get(pPortfolio.getId());

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
            List<SecurityHolding> myList = new ArrayList<>();

            /* Loop through the securities */
            final Iterator<Security> myIterator = theSecurities.iterator();
            while (myIterator.hasNext()) {
                final Security mySecurity = myIterator.next();

                /* Ignore closed/deleted and wrong class */
                boolean bIgnore = mySecurity.isClosed() || mySecurity.isDeleted();
                bIgnore |= pClass != null && !pClass.equals(mySecurity.getSecurityTypeClass());
                if (bIgnore) {
                    continue;
                }

                /* Create a new holding and add to the list */
                final SecurityHolding myHolding = new SecurityHolding(pPortfolio, mySecurity);
                myList.add(myHolding);
            }

            /* Sort list or delete if empty */
            if (myList.isEmpty()) {
                myList = null;
            } else {
                Collections.sort(myList);
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
            implements MetisDataObjectFormat, MetisDataMap<Integer, SecurityHolding> {
        /**
         * Portfolio.
         */
        private final Portfolio thePortfolio;

        /**
         * Underlying security list.
         */
        private final SecurityList theSecurities;

        /**
         * The underlying map.
         */
        private final Map<Integer, SecurityHolding> theMap;

        /**
         * Constructor.
         * @param pPortfolio the portfolio
         * @param pSecurities the security list
         */
        private PortfolioHoldingsMap(final Portfolio pPortfolio,
                                     final SecurityList pSecurities) {
            thePortfolio = pPortfolio;
            theSecurities = pSecurities;
            theMap = new HashMap<>();
        }

        @Override
        public Map<Integer, SecurityHolding> getUnderlyingMap() {
            return theMap;
        }

        @Override
        public String formatObject(final MetisDataFormatter pFormatter) {
            return thePortfolio.formatObject(pFormatter);
        }

        @Override
        public String toString() {
            return thePortfolio.toString();
        }

        /**
         * Obtain or allocate the holding for the security.
         * @param pId the id of the security
         * @return the holding
         */
        private SecurityHolding getHoldingForSecurity(final Integer pId) {
            /* Look up in the map */
            SecurityHolding myHolding = theMap.get(pId);

            /* If the Id is not found */
            if (myHolding == null) {
                /* Look up the security as a double check */
                final Security mySecurity = theSecurities.findItemById(pId);

                /* Reject if no such security */
                if ((mySecurity == null) || (mySecurity.isDeleted())) {
                    return null;
                }

                /* Allocate and store the holding */
                myHolding = new SecurityHolding(thePortfolio, mySecurity);
                theMap.put(pId, myHolding);
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
            final Security mySecurity = theSecurities.findItemByName(pName);

            /* Reject if no such security */
            if (mySecurity == null) {
                return null;
            }

            /* Look up in the map */
            final Integer myId = mySecurity.getId();
            return theMap.computeIfAbsent(myId, i -> new SecurityHolding(thePortfolio, mySecurity));
        }

        /**
         * Reset names.
         */
        private void resetNames() {
            /* Iterate through the security holdings */
            final Iterator<SecurityHolding> myIterator = theMap.values().iterator();
            while (myIterator.hasNext()) {
                final SecurityHolding myHolding = myIterator.next();

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
            List<SecurityHolding> myList = new ArrayList<>();

            /* Loop through the holdings */
            final Iterator<SecurityHolding> myIterator = theMap.values().iterator();
            while (myIterator.hasNext()) {
                final SecurityHolding myHolding = myIterator.next();
                final Security mySecurity = myHolding.getSecurity();

                /* Ignore closed/deleted */
                if (!mySecurity.isClosed() && !mySecurity.isDeleted()) {
                    /* Add to the list */
                    myList.add(myHolding);
                }
            }

            /* Sort list or delete if empty */
            if (myList.isEmpty()) {
                myList = null;
            } else {
                Collections.sort(myList);
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
            List<SecurityHolding> myList = new ArrayList<>();

            /* Loop through the securities */
            final Iterator<Security> myIterator = theSecurities.iterator();
            while (myIterator.hasNext()) {
                final Security mySecurity = myIterator.next();

                /* Ignore closed/deleted and wrong class/currency */
                boolean bIgnore = mySecurity.isClosed() || mySecurity.isDeleted();
                bIgnore |= pClass != null && pClass.equals(mySecurity.getSecurityTypeClass());
                if (bIgnore) {
                    continue;
                }

                /* Only add if not currently present */
                if (theMap.get(mySecurity.getId()) == null) {
                    /* Create a new holding and add to the list */
                    final SecurityHolding myHolding = new SecurityHolding(thePortfolio, mySecurity);
                    myList.add(myHolding);
                }
            }

            /* Sort list or delete if empty */
            if (myList.isEmpty()) {
                myList = null;
            } else {
                Collections.sort(myList);
            }

            /* return iterator */
            return myList == null
                                  ? null
                                  : myList.iterator();
        }
    }
}
