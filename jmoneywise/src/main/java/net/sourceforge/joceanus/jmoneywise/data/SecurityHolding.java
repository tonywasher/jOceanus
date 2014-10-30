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

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.viewer.JDataFieldValue;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.viewer.JDataObject.JDataContents;
import net.sourceforge.joceanus.jmetis.viewer.JDataObject.JDataFormat;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.Portfolio.PortfolioList;
import net.sourceforge.joceanus.jmoneywise.data.Security.SecurityList;
import net.sourceforge.joceanus.jprometheus.data.DataItem;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataResource;

/**
 * Portfolio/Security combination.
 */
public final class SecurityHolding
        implements JDataContents, TransactionAsset {
    /**
     * Name separator.
     */
    private static final String NAME_SEP = ":";

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
            return theName;
        }
        if (FIELD_PORTFOLIO.equals(pField)) {
            return thePortfolio;
        }
        if (FIELD_SECURITY.equals(pField)) {
            return theSecurity;
        }
        return JDataFieldValue.UNKNOWN;
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

    @Override
    public Integer getId() {
        return theId;
    }

    @Override
    public String getName() {
        return theName;
    }

    @Override
    public Payee getParent() {
        return null;
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

    /**
     * Generate the name.
     * @return the name.
     */
    private String generateName() {
        /* Build and return the name */
        StringBuilder myBuilder = new StringBuilder();
        myBuilder.append(thePortfolio.getName());
        myBuilder.append(NAME_SEP);
        myBuilder.append(theSecurity.getName());
        return myBuilder.toString();
    }

    /**
     * Obtain portfolio name from composite name.
     * @param pName the composite name
     * @return the portfolio name
     */
    private static String getPortfolioName(final String pName) {
        int iIndex = pName.indexOf(NAME_SEP);
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
        int iIndex = pName.indexOf(NAME_SEP);
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
               | (thePortfolio.getId() << Short.SIZE);
    }

    /**
     * Obtain portfolio id from composite id.
     * @param pId the composite id
     * @return the portfolio id
     */
    private static Integer getPortfolioId(final Integer pId) {
        return (pId >> Short.SIZE) & Short.MAX_VALUE;
    }

    /**
     * Obtain security id from composite id.
     * @param pId the composite id
     * @return the security id
     */
    private static Integer getSecurityId(final Integer pId) {
        return pId & Short.MAX_VALUE;
    }

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

        /* Generate the id and name */
        theId = generateId();
        theName = generateName();
    }

    @Override
    public void touchItem(final DataItem<MoneyWiseDataType> pSource) {
        /* Touch references */
        thePortfolio.touchItem(pSource);
        theSecurity.touchItem(pSource);
    }

    /**
     * SecurityHolding Map.
     */
    public static class SecurityHoldingMap
            extends HashMap<Integer, Map<Integer, SecurityHolding>> {
        /**
         * SerialId.
         */
        private static final long serialVersionUID = -4221855114560425582L;

        /**
         * Underlying data.
         */
        private final transient MoneyWiseData theData;

        /**
         * Constructor.
         * @param pData the dataSet
         */
        public SecurityHoldingMap(final MoneyWiseData pData) {
            theData = pData;
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
         * Obtain or allocate the map for the portfolio.
         * @param pId the id of the portfolio
         * @return the map
         */
        private PortfolioHoldingsMap getMapForPortfolio(final Integer pId) {
            /* Look up in the map */
            Map<Integer, SecurityHolding> myMap = get(pId);

            /* If the Id is not found */
            if (myMap == null) {
                /* Look up the portfolio as a double check */
                PortfolioList myList = theData.getPortfolios();
                Portfolio myPortfolio = myList.findItemById(pId);

                /* Reject if no such portfolio */
                if (myPortfolio == null) {
                    return null;
                }

                /* Allocate and store the map */
                myMap = new PortfolioHoldingsMap(myPortfolio);
                put(pId, myMap);
            }

            /* Return the map */
            return (PortfolioHoldingsMap) myMap;
        }

        /**
         * Obtain or allocate the map for the portfolio.
         * @param pName the name of the portfolio
         * @return the map
         */
        private PortfolioHoldingsMap getMapForPortfolio(final String pName) {
            /* Look up the portfolio */
            PortfolioList myList = theData.getPortfolios();
            Portfolio myPortfolio = myList.findItemByName(pName);

            /* Reject if no such portfolio */
            if (myPortfolio == null) {
                return null;
            }

            /* Look up in the map */
            Integer myId = myPortfolio.getId();
            Map<Integer, SecurityHolding> myMap = get(myId);

            /* If the Id is not found */
            if (myMap == null) {
                /* Allocate and store the map */
                myMap = new PortfolioHoldingsMap(myPortfolio);
                put(myId, myMap);
            }

            /* Return the map */
            return (PortfolioHoldingsMap) myMap;
        }

        /**
         * PortfolioHoldings Map.
         */
        private final class PortfolioHoldingsMap
                extends HashMap<Integer, SecurityHolding>
                implements JDataFormat {
            /**
             * Serial Id.
             */
            private static final long serialVersionUID = -5532168973693014505L;

            @Override
            public String formatObject() {
                return thePortfolio.formatObject();
            }

            @Override
            public String toString() {
                return formatObject();
            }

            /**
             * Portfolio.
             */
            private final Portfolio thePortfolio;

            /**
             * Constructor.
             * @param pPortfolio the portfolio
             */
            private PortfolioHoldingsMap(final Portfolio pPortfolio) {
                thePortfolio = pPortfolio;
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
                    SecurityList myList = theData.getSecurities();
                    Security mySecurity = myList.findItemById(pId);

                    /* Reject if no such security */
                    if (mySecurity == null) {
                        return null;
                    }

                    /* Allocate and store the map */
                    myHolding = new SecurityHolding(thePortfolio, mySecurity);
                    put(pId, myHolding);
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
                SecurityList myList = theData.getSecurities();
                Security mySecurity = myList.findItemByName(pName);

                /* Reject if no such security */
                if (mySecurity == null) {
                    return null;
                }

                /* Look up in the map */
                Integer myId = mySecurity.getId();
                SecurityHolding myHolding = get(myId);

                /* If the Id is not found */
                if (myHolding == null) {
                    /* Allocate and store the map */
                    myHolding = new SecurityHolding(thePortfolio, mySecurity);
                    put(myId, myHolding);
                }

                /* Return the holding */
                return myHolding;
            }
        }
    }
}
