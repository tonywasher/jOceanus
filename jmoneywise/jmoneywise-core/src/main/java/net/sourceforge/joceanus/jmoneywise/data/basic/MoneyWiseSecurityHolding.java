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
package net.sourceforge.joceanus.jmoneywise.data.basic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataMap;
import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataObjectFormat;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWisePortfolio.MoneyWisePortfolioList;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseSecurity.MoneyWiseSecurityList;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWiseSecurityClass;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataItem;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataResource;
import net.sourceforge.joceanus.jprometheus.views.PrometheusEditSet;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;

/**
 * Portfolio/Security combination.
 */
public final class MoneyWiseSecurityHolding
        implements MetisFieldItem, MoneyWiseTransAsset, Comparable<Object> {
    /**
     * Name separator.
     */
    public static final String SECURITYHOLDING_SEP = ":";

    /**
     * New Security Holding text.
     */
    public static final String SECURITYHOLDING_NEW = MoneyWiseBasicResource.SECURITYHOLDING_NEW.getValue();

    /**
     * Report fields.
     */
    private static final MetisFieldSet<MoneyWiseSecurityHolding> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseSecurityHolding.class);

    /*
     * UnderlyingMap Field Id.
     */
    static {
        FIELD_DEFS.declareLocalField(PrometheusDataResource.DATAITEM_ID, MoneyWiseSecurityHolding::getExternalId);
        FIELD_DEFS.declareLocalField(PrometheusDataResource.DATAITEM_FIELD_NAME, MoneyWiseSecurityHolding::getName);
        FIELD_DEFS.declareLocalField(MoneyWiseBasicDataType.PORTFOLIO, MoneyWiseSecurityHolding::getPortfolio);
        FIELD_DEFS.declareLocalField(MoneyWiseBasicDataType.SECURITY, MoneyWiseSecurityHolding::getSecurity);
    }

    /**
     * The id of the holding.
     */
    private final Long theId;

    /**
     * The portfolio of the holding.
     */
    private MoneyWisePortfolio thePortfolio;

    /**
     * The security of the holding.
     */
    private MoneyWiseSecurity theSecurity;

    /**
     * Constructor.
     * @param pPortfolio the portfolio
     * @param pSecurity the security
     */
    private MoneyWiseSecurityHolding(final MoneyWisePortfolio pPortfolio,
                                     final MoneyWiseSecurity pSecurity) {
        /* Set portfolio and security */
        thePortfolio = pPortfolio;
        theSecurity = pSecurity;

        /* Generate the id */
        theId = MoneyWiseAssetType.createExternalId(MoneyWiseAssetType.SECURITYHOLDING, thePortfolio.getIndexedId(), theSecurity.getIndexedId());
    }

    @Override
    public MetisFieldSet<MoneyWiseSecurityHolding> getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public String formatObject(final TethysUIDataFormatter pFormatter) {
        return toString();
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public Boolean isClosed() {
        /* Access constituent parts */
        final MoneyWisePortfolio myPortfolio = getPortfolio();
        final MoneyWiseSecurity mySecurity = getSecurity();

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
        final MoneyWisePortfolio myPortfolio = getPortfolio();
        final MoneyWiseSecurity mySecurity = getSecurity();

        /* Test underlying items */
        return myPortfolio.isDeleted()
                || mySecurity.isDeleted();
    }

    @Override
    public Long getExternalId() {
        return theId;
    }

    @Override
    public String getName() {
        return generateName();
    }

    @Override
    public MoneyWisePayee getParent() {
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
    public MoneyWisePortfolio getPortfolio() {
        return thePortfolio;
    }

    /**
     * Obtain Security.
     * @return the security
     */
    public MoneyWiseSecurity getSecurity() {
        return theSecurity;
    }

    @Override
    public MoneyWiseAssetType getAssetType() {
        return MoneyWiseAssetType.SECURITYHOLDING;
    }

    @Override
    public MoneyWiseCurrency getAssetCurrency() {
        return theSecurity.getAssetCurrency();
    }

    @Override
    public Currency getCurrency() {
        return getAssetCurrency().getCurrency();
    }

    @Override
    public Boolean isForeign() {
        final MoneyWiseCurrency myDefault = thePortfolio.getDataSet().getReportingCurrency();
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
     * Obtain the security class.
     * @return the security class.
     */
    private MoneyWiseSecurityClass getSecurityTypeClass() {
        /* Check for match */
        return theSecurity.getCategoryClass();
    }

    /**
     * Is this holding the required security class.
     * @param pClass the required security class.
     * @return true/false
     */
    public boolean isSecurityClass(final MoneyWiseSecurityClass pClass) {
        /* Check for match */
        return getSecurityTypeClass() == pClass;
    }

    @Override
    public void touchItem(final PrometheusDataItem pSource) {
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
        if (!(pThat instanceof MoneyWiseSecurityHolding)) {
            return false;
        }

        /* Compare the Portfolios */
        final MoneyWiseSecurityHolding myThat = (MoneyWiseSecurityHolding) pThat;
        if (!getPortfolio().equals(myThat.getPortfolio())) {
            return false;
        }

        /* Compare securities */
        return getSecurity().equals(myThat.getSecurity());
    }

    @Override
    public int hashCode() {
        final int myHash = MetisFieldSet.HASH_PRIME * getPortfolio().hashCode();
        return myHash + getSecurity().hashCode();
    }

    @Override
    public int compareTo(final Object pThat) {
        /* Handle the trivial cases */
        if (this.equals(pThat)) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* If the Asset is not a SecurityHolding we are last */
        if (!(pThat instanceof MoneyWiseSecurityHolding)) {
            return 1;
        }

        /* Access as AssetBase */
        final MoneyWiseSecurityHolding myThat = (MoneyWiseSecurityHolding) pThat;

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
    public static class MoneyWiseSecurityHoldingMap
            implements MetisDataObjectFormat, MetisDataMap<Integer, MoneyWisePortfolioHoldingsMap> {
        /**
         * Underlying portfolio list.
         */
        private final MoneyWisePortfolioList thePortfolios;

        /**
         * Underlying security list.
         */
        private final MoneyWiseSecurityList theSecurities;

        /**
         * The underlying map.
         */
        private final Map<Integer, MoneyWisePortfolioHoldingsMap> theMap;

        /**
         * Constructor.
         * @param pData the dataSet
         */
        public MoneyWiseSecurityHoldingMap(final MoneyWiseDataSet pData) {
            /* Access lists */
            thePortfolios = pData.getPortfolios();
            theSecurities = pData.getSecurities();
            theMap = new HashMap<>();
        }

        /**
         * Constructor.
         * @param pEditSet the editSet
         */
        public MoneyWiseSecurityHoldingMap(final PrometheusEditSet pEditSet) {
            /* Access lists */
            thePortfolios = pEditSet.getDataList(MoneyWiseBasicDataType.PORTFOLIO, MoneyWisePortfolioList.class);
            theSecurities = pEditSet.getDataList(MoneyWiseBasicDataType.SECURITY, MoneyWiseSecurityList.class);
            theMap = new HashMap<>();
        }

        @Override
        public Map<Integer, MoneyWisePortfolioHoldingsMap> getUnderlyingMap() {
            return theMap;
        }

        @Override
        public String formatObject(final TethysUIDataFormatter pFormatter) {
            return MoneyWiseSecurityHoldingMap.class.getSimpleName();
        }

        /**
         * Look up a security holding.
         * @param pId the id of the security holding
         * @return the security holding
         */
        public MoneyWiseSecurityHolding findHoldingById(final Long pId) {
            /* Access the component IDs */
            final Integer myPortId = getPortfolioId(pId);
            final Integer mySecId = getSecurityId(pId);

            /* Look up security map for portfolio */
            final MoneyWisePortfolioHoldingsMap myMap = getMapForPortfolio(myPortId);
            return myMap == null
                    ? null
                    : myMap.getHoldingForSecurity(mySecId);
        }

        /**
         * Look up a security holding.
         * @param pName the name of the security holding
         * @return the security holding
         */
        public MoneyWiseSecurityHolding findHoldingByName(final String pName) {
            /* Access the component Names */
            final String myPortName = getPortfolioName(pName);
            final String mySecName = getSecurityName(pName);

            /* Look up security map for portfolio */
            final MoneyWisePortfolioHoldingsMap myMap = getMapForPortfolio(myPortName);
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
        private static Integer getPortfolioId(final Long pId) {
            return MoneyWiseAssetType.getMajorId(pId);
        }

        /**
         * Obtain security id from composite id.
         * @param pId the composite id
         * @return the security id
         */
        private static Integer getSecurityId(final Long pId) {
            return MoneyWiseAssetType.getBaseId(pId);
        }

        /**
         * Declare holding.
         * @param pPortfolio the portfolio
         * @param pSecurity the security
         * @return the holding
         */
        public MoneyWiseSecurityHolding declareHolding(final MoneyWisePortfolio pPortfolio,
                                                       final MoneyWiseSecurity pSecurity) {
            /* Access the portfolio map */
            final MoneyWisePortfolioHoldingsMap myPortMap = getMapForPortfolio(pPortfolio.getIndexedId());
            if (myPortMap == null) {
                throw new IllegalStateException("Invalid Portfolio");
            }
            final Map<Integer, MoneyWiseSecurityHolding> myMap = myPortMap.getUnderlyingMap();

            /* Look up existing holding */
            final Integer myId = pSecurity.getIndexedId();
            return myMap.computeIfAbsent(myId, i -> new MoneyWiseSecurityHolding(pPortfolio, pSecurity));
        }

        /**
         * Obtain or allocate the map for the portfolio.
         * @param pId the id of the portfolio
         * @return the map
         */
        private MoneyWisePortfolioHoldingsMap getMapForPortfolio(final Integer pId) {
            /* Look up in the map */
            MoneyWisePortfolioHoldingsMap myMap = theMap.get(pId);

            /* If the Id is not found */
            if (myMap == null) {
                /* Look up the portfolio as a double check */
                final MoneyWisePortfolio myPortfolio = thePortfolios.findItemById(pId);

                /* Reject if no such portfolio */
                if (myPortfolio == null
                        || myPortfolio.isDeleted()) {
                    return null;
                }

                /* Allocate and store the map */
                myMap = new MoneyWisePortfolioHoldingsMap(myPortfolio, theSecurities);
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
        private MoneyWisePortfolioHoldingsMap getMapForPortfolio(final String pName) {
            /* Look up the portfolio */
            final MoneyWisePortfolio myPortfolio = thePortfolios.findItemByName(pName);

            /* Reject if no such portfolio */
            if (myPortfolio == null) {
                return null;
            }

            /* Look up in the map */
            final Integer myId = myPortfolio.getIndexedId();
            return theMap.computeIfAbsent(myId, i -> new MoneyWisePortfolioHoldingsMap(myPortfolio, theSecurities));
        }

        /**
         * DeRegister Portfolio.
         * @param pPortfolio the portfolio
         */
        public void deRegister(final MoneyWisePortfolio pPortfolio) {
            /* Ensure that we do not reference this portfolio */
            final Integer myId = pPortfolio.getIndexedId();
            theMap.remove(myId);
        }

        /**
         * DeRegister Security.
         * @param pSecurity the security
         */
        public void deRegister(final MoneyWiseSecurity pSecurity) {
            /* Access the id */
            final Integer myId = pSecurity.getIndexedId();

            /* Iterate through the portfolio maps */
            final Iterator<MoneyWisePortfolioHoldingsMap> myIterator = theMap.values().iterator();
            while (myIterator.hasNext()) {
                final MoneyWisePortfolioHoldingsMap myMap = myIterator.next();

                /* Ensure that we do not reference this security */
                myMap.getUnderlyingMap().remove(myId);
            }
        }

        /**
         * Obtain existing holding list for Portfolio.
         * @param pPortfolio the portfolio
         * @return the iterator
         */
        public Iterator<MoneyWiseSecurityHolding> existingIterator(final MoneyWisePortfolio pPortfolio) {
            /* Look up in the map */
            final MoneyWisePortfolioHoldingsMap myMap = theMap.get(pPortfolio.getIndexedId());

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
        public Iterator<MoneyWiseSecurityHolding> newIterator(final MoneyWisePortfolio pPortfolio) {
            return newIterator(pPortfolio, null);
        }

        /**
         * Obtain new holding list for Portfolio.
         * @param pPortfolio the portfolio
         * @param pClass the class of holdings or null for all
         * @return the iterator
         */
        public Iterator<MoneyWiseSecurityHolding> newIterator(final MoneyWisePortfolio pPortfolio,
                                                              final MoneyWiseSecurityClass pClass) {
            /* Look up in the map */
            final MoneyWisePortfolioHoldingsMap myMap = theMap.get(pPortfolio.getIndexedId());

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
        private Iterator<MoneyWiseSecurityHolding> fullIterator(final MoneyWisePortfolio pPortfolio,
                                                                final MoneyWiseSecurityClass pClass) {
            /* If the portfolio is closed/deleted */
            if (pPortfolio.isClosed() || pPortfolio.isDeleted()) {
                /* No iterator */
                return null;
            }

            /* Create an empty list */
            List<MoneyWiseSecurityHolding> myList = new ArrayList<>();

            /* Loop through the securities */
            final Iterator<MoneyWiseSecurity> myIterator = theSecurities.iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseSecurity mySecurity = myIterator.next();

                /* Ignore closed/deleted and wrong class */
                boolean bIgnore = mySecurity.isClosed() || mySecurity.isDeleted();
                bIgnore |= pClass != null && !pClass.equals(mySecurity.getCategoryClass());
                if (bIgnore) {
                    continue;
                }

                /* Create a new holding and add to the list */
                final MoneyWiseSecurityHolding myHolding = new MoneyWiseSecurityHolding(pPortfolio, mySecurity);
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
    public static final class MoneyWisePortfolioHoldingsMap
            implements MetisDataObjectFormat, MetisDataMap<Integer, MoneyWiseSecurityHolding> {
        /**
         * Portfolio.
         */
        private final MoneyWisePortfolio thePortfolio;

        /**
         * Underlying security list.
         */
        private final MoneyWiseSecurityList theSecurities;

        /**
         * The underlying map.
         */
        private final Map<Integer, MoneyWiseSecurityHolding> theMap;

        /**
         * Constructor.
         * @param pPortfolio the portfolio
         * @param pSecurities the security list
         */
        private MoneyWisePortfolioHoldingsMap(final MoneyWisePortfolio pPortfolio,
                                              final MoneyWiseSecurityList pSecurities) {
            thePortfolio = pPortfolio;
            theSecurities = pSecurities;
            theMap = new HashMap<>();
        }

        @Override
        public Map<Integer, MoneyWiseSecurityHolding> getUnderlyingMap() {
            return theMap;
        }

        @Override
        public String formatObject(final TethysUIDataFormatter pFormatter) {
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
        private MoneyWiseSecurityHolding getHoldingForSecurity(final Integer pId) {
            /* Look up in the map */
            MoneyWiseSecurityHolding myHolding = theMap.get(pId);

            /* If the Id is not found */
            if (myHolding == null) {
                /* Look up the security as a double check */
                final MoneyWiseSecurity mySecurity = theSecurities.findItemById(pId);

                /* Reject if no such security */
                if (mySecurity == null || mySecurity.isDeleted()) {
                    return null;
                }

                /* Allocate and store the holding */
                myHolding = new MoneyWiseSecurityHolding(thePortfolio, mySecurity);
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
        private MoneyWiseSecurityHolding getHoldingForSecurity(final String pName) {
            /* Look up the security */
            final MoneyWiseSecurity mySecurity = theSecurities.findItemByName(pName);

            /* Reject if no such security */
            if (mySecurity == null) {
                return null;
            }

            /* Look up in the map */
            final Integer myId = mySecurity.getIndexedId();
            return theMap.computeIfAbsent(myId, i -> new MoneyWiseSecurityHolding(thePortfolio, mySecurity));
        }

        /**
         * Obtain existing holding list for Portfolio.
         * @return the iterator
         */
        private Iterator<MoneyWiseSecurityHolding> existingIterator() {
            /* If the portfolio is closed/deleted */
            if (thePortfolio.isClosed() || thePortfolio.isDeleted()) {
                /* No iterator */
                return null;
            }

            /* Create an empty list */
            List<MoneyWiseSecurityHolding> myList = new ArrayList<>();

            /* Loop through the holdings */
            final Iterator<MoneyWiseSecurityHolding> myIterator = theMap.values().iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseSecurityHolding myHolding = myIterator.next();
                final MoneyWiseSecurity mySecurity = myHolding.getSecurity();

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
        private Iterator<MoneyWiseSecurityHolding> newIterator(final MoneyWiseSecurityClass pClass) {
            /* If the portfolio is closed/deleted */
            if (thePortfolio.isClosed() || thePortfolio.isDeleted()) {
                /* No iterator */
                return null;
            }

            /* Create an empty list */
            List<MoneyWiseSecurityHolding> myList = new ArrayList<>();

            /* Loop through the securities */
            final Iterator<MoneyWiseSecurity> myIterator = theSecurities.iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseSecurity mySecurity = myIterator.next();

                /* Ignore closed/deleted and wrong class/currency */
                boolean bIgnore = mySecurity.isClosed() || mySecurity.isDeleted();
                bIgnore |= pClass != null && pClass.equals(mySecurity.getCategoryClass());
                if (bIgnore) {
                    continue;
                }

                /* Only add if not currently present */
                if (theMap.get(mySecurity.getIndexedId()) == null) {
                    /* Create a new holding and add to the list */
                    final MoneyWiseSecurityHolding myHolding = new MoneyWiseSecurityHolding(thePortfolio, mySecurity);
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
