/*
 * MoneyWise: Finance Application
 * Copyright 2012-2026. Tony Washer
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
 */
package io.github.tonywasher.joceanus.moneywise.data.basic;

import io.github.tonywasher.joceanus.metis.field.MetisFieldItem;
import io.github.tonywasher.joceanus.metis.field.MetisFieldSet;
import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWiseCurrency;
import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWiseSecurityClass;
import io.github.tonywasher.joceanus.oceanus.format.OceanusDataFormatter;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataItem;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataResource;

import java.util.Currency;

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
    private final MoneyWisePortfolio thePortfolio;

    /**
     * The security of the holding.
     */
    private final MoneyWiseSecurity theSecurity;

    /**
     * Constructor.
     *
     * @param pPortfolio the portfolio
     * @param pSecurity  the security
     */
    MoneyWiseSecurityHolding(final MoneyWisePortfolio pPortfolio,
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
    public String formatObject(final OceanusDataFormatter pFormatter) {
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
     *
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
    public boolean isTaxFree() {
        return thePortfolio.isTaxFree();
    }

    @Override
    public boolean isGross() {
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
     *
     * @return the portfolio
     */
    public MoneyWisePortfolio getPortfolio() {
        return thePortfolio;
    }

    /**
     * Obtain Security.
     *
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
    public boolean isForeign() {
        final MoneyWiseCurrency myDefault = thePortfolio.getDefaultCurrency();
        return !myDefault.equals(getAssetCurrency());
    }

    /**
     * Generate the name.
     *
     * @return the name.
     */
    private String generateName() {
        /* Build and return the name */
        return thePortfolio.getName()
                + SECURITYHOLDING_SEP
                + theSecurity.getName();
    }

    /**
     * Obtain the security class.
     *
     * @return the security class.
     */
    private MoneyWiseSecurityClass getSecurityTypeClass() {
        /* Check for match */
        return theSecurity.getCategoryClass();
    }

    /**
     * Is this holding the required security class.
     *
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
        if (!(pThat instanceof MoneyWiseSecurityHolding myThat)) {
            return false;
        }

        /* Compare the Portfolios */
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
        if (!(pThat instanceof MoneyWiseSecurityHolding myThat)) {
            return 1;
        }

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
     *
     * @return true/false
     */
    boolean validCurrencies() {
        return thePortfolio.getCurrency().equals(theSecurity.getCurrency());
    }
}
