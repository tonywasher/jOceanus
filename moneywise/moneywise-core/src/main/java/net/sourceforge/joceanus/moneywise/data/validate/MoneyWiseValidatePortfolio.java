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
package net.sourceforge.joceanus.moneywise.data.validate;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseAssetBase;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicResource;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDataValidator.MoneyWiseDataValidatorAutoCorrect;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePayee;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePayee.MoneyWisePayeeList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePortfolio;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePortfolio.MoneyWisePortfolioDataMap;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePortfolio.MoneyWisePortfolioList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseSecurityHolding;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWisePayeeClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWisePortfolioClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWisePortfolioType;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWisePortfolioType.MoneyWisePortfolioTypeList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseStaticDataType;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataItem;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataResource;
import io.github.tonywasher.joceanus.prometheus.views.PrometheusEditSet;

import java.util.Iterator;

/**
 * Validator for Payee.
 */
public class MoneyWiseValidatePortfolio
        extends MoneyWiseValidateAccount<MoneyWisePortfolio>
        implements MoneyWiseDataValidatorAutoCorrect<MoneyWisePortfolio> {
    /**
     * New Account name.
     */
    private static final String NAME_NEWACCOUNT = MoneyWiseBasicResource.PORTFOLIO_NEWACCOUNT.getValue();

    /**
     * Portfolio Cash account.
     */
    static final String NAME_CASHACCOUNT = MoneyWiseBasicResource.PORTFOLIO_CASHACCOUNT.getValue();

    /**
     * The infoSet validator.
     */
    private final MoneyWiseValidatePortfolioInfoSet theInfoSet;

    /**
     * Constructor.
     */
    MoneyWiseValidatePortfolio() {
        theInfoSet = new MoneyWiseValidatePortfolioInfoSet();
    }

    @Override
    public void setEditSet(final PrometheusEditSet pEditSet) {
        super.setEditSet(pEditSet);
        theInfoSet.storeEditSet(pEditSet);
    }

    @Override
    public void validate(final PrometheusDataItem pPortfolio) {
        final MoneyWisePortfolio myPortfolio = (MoneyWisePortfolio) pPortfolio;
        final MoneyWisePortfolioList myList = myPortfolio.getList();
        final MoneyWisePayee myParent = myPortfolio.getParent();
        final MoneyWisePortfolioType myPortType = myPortfolio.getCategory();
        final MoneyWiseCurrency myCurrency = myPortfolio.getAssetCurrency();

        /* Validate base components */
        super.validate(pPortfolio);

        /* PortfolioType must be non-null */
        if (myPortType == null) {
            pPortfolio.addError(PrometheusDataItem.ERROR_MISSING, MoneyWiseBasicResource.CATEGORY_NAME);
        } else {
            /* Access the class */
            final MoneyWisePortfolioClass myClass = myPortType.getPortfolioClass();

            /* PortfolioType must be enabled */
            if (!myPortType.getEnabled()) {
                pPortfolio.addError(PrometheusDataItem.ERROR_DISABLED, MoneyWiseBasicResource.CATEGORY_NAME);
            }

            /* If the PortfolioType is singular */
            if (myClass.isSingular()) {
                /* Count the elements of this class */
                final MoneyWisePortfolioDataMap myMap = myList.getDataMap();
                if (!myMap.validSingularCount(myClass)) {
                    pPortfolio.addError(PrometheusDataItem.ERROR_MULT, MoneyWiseBasicResource.CATEGORY_NAME);
                }
            }
        }

        /* Parent account must exist */
        if (myParent == null) {
            pPortfolio.addError(PrometheusDataItem.ERROR_MISSING, MoneyWiseBasicResource.ASSET_PARENT);
        } else {
            /* Parent must be suitable */
            final MoneyWisePayeeClass myParClass = myParent.getCategoryClass();
            if (!myParClass.canParentPortfolio()) {
                pPortfolio.addError(ERROR_BADPARENT, MoneyWiseBasicResource.ASSET_PARENT);
            }

            /* If we are open then parent must be open */
            if (!myPortfolio.isClosed() && Boolean.TRUE.equals(myParent.isClosed())) {
                pPortfolio.addError(ERROR_PARCLOSED, MoneyWiseBasicResource.ASSET_CLOSED);
            }
        }

        /* Currency must be non-null and enabled */
        if (myCurrency == null) {
            pPortfolio.addError(PrometheusDataItem.ERROR_MISSING, MoneyWiseStaticDataType.CURRENCY);
        } else if (!myCurrency.getEnabled()) {
            pPortfolio.addError(PrometheusDataItem.ERROR_DISABLED, MoneyWiseStaticDataType.CURRENCY);
        }

        /* If we have an infoSet */
        if (myPortfolio.getInfoSet() != null) {
            /* Validate the InfoSet */
            theInfoSet.validate(myPortfolio.getInfoSet());
        }

        /* Set validation flag */
        if (!pPortfolio.hasErrors()) {
            pPortfolio.setValidEdit();
        }
    }

    @Override
    public void validateName(final MoneyWiseAssetBase pPortfolio,
                             final String pName) {
        /* Perform basic checks */
        super.validateName(pPortfolio, pName);

        /* Check that the name does not contain invalid characters */
        if (pName.contains(MoneyWiseSecurityHolding.SECURITYHOLDING_SEP)) {
            pPortfolio.addError(PrometheusDataItem.ERROR_INVALIDCHAR, PrometheusDataResource.DATAITEM_FIELD_NAME);
        }
    }

    @Override
    public void setDefaults(final MoneyWisePortfolio pPortfolio) throws OceanusException {
        /* Set values */
        final MoneyWisePortfolioList myList = pPortfolio.getList();
        pPortfolio.setName(getUniqueName(myList, NAME_NEWACCOUNT));
        pPortfolio.setCategory(getDefaultPortfolioType());
        pPortfolio.setParent(getDefaultParent());
        pPortfolio.setAssetCurrency(getReportingCurrency());
        pPortfolio.setClosed(Boolean.FALSE);
    }

    @Override
    public void autoCorrect(final MoneyWisePortfolio pPortfolio) throws OceanusException {
        /* Ensure that we have a valid parent */
        final MoneyWisePayee myParent = pPortfolio.getParent();
        if (myParent == null
                || !myParent.getCategoryClass().canParentPortfolio()) {
            pPortfolio.setParent(getDefaultParent());
        }

        /* autoCorrect the infoSet */
        theInfoSet.autoCorrect(pPortfolio.getInfoSet());
    }

    /**
     * Obtain default parent for portfolio.
     *
     * @return the default parent
     */
    private MoneyWisePayee getDefaultParent() {
        /* loop through the payees */
        final MoneyWisePayeeList myPayees = getEditSet().getDataList(MoneyWiseBasicDataType.PAYEE, MoneyWisePayeeList.class);
        final Iterator<MoneyWisePayee> myIterator = myPayees.iterator();
        while (myIterator.hasNext()) {
            final MoneyWisePayee myPayee = myIterator.next();

            /* Ignore deleted and closed payees and those that cannot parent this portfolio */
            boolean bIgnore = myPayee.isDeleted() || myPayee.isClosed();
            bIgnore |= !myPayee.getCategoryClass().canParentPortfolio();
            if (!bIgnore) {
                return myPayee;
            }
        }

        /* Return no payee */
        return null;
    }

    /**
     * Obtain portfolio type for new portfolio account.
     *
     * @return the security type
     */
    private MoneyWisePortfolioType getDefaultPortfolioType() {
        /* loop through the portfolio types */
        final MoneyWisePortfolioTypeList myTypes
                = getEditSet().getDataList(MoneyWiseStaticDataType.PORTFOLIOTYPE, MoneyWisePortfolioTypeList.class);
        final Iterator<MoneyWisePortfolioType> myIterator = myTypes.iterator();
        while (myIterator.hasNext()) {
            final MoneyWisePortfolioType myType = myIterator.next();

            /* Ignore deleted types */
            if (!myType.isDeleted()) {
                return myType;
            }
        }

        /* Return no category */
        return null;
    }
}
