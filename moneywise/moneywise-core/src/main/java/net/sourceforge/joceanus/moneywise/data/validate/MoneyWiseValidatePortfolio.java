/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.moneywise.data.validate;

import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseAssetBase;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicResource;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePayee;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePortfolio;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePortfolio.MoneyWisePortfolioDataMap;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePortfolio.MoneyWisePortfolioList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseSecurityHolding;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWisePayeeClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWisePortfolioClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWisePortfolioType;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseStaticDataType;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataItem;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataResource;

/**
 * Validator for Payee.
 */
public class MoneyWiseValidatePortfolio
        extends MoneyWiseValidateAccount<MoneyWisePortfolio> {
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
    public void validate(final MoneyWisePortfolio pPortfolio) {
        final MoneyWisePortfolioList myList = pPortfolio.getList();
        final MoneyWisePayee myParent = pPortfolio.getParent();
        final MoneyWisePortfolioType myPortType = pPortfolio.getCategory();
        final MoneyWiseCurrency myCurrency = pPortfolio.getAssetCurrency();

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
                pPortfolio.addError(MoneyWiseAssetBase.ERROR_BADPARENT, MoneyWiseBasicResource.ASSET_PARENT);
            }

            /* If we are open then parent must be open */
            if (!pPortfolio.isClosed() && Boolean.TRUE.equals(myParent.isClosed())) {
                pPortfolio.addError(MoneyWiseAssetBase.ERROR_PARCLOSED, MoneyWiseBasicResource.ASSET_CLOSED);
            }
        }

        /* Currency must be non-null and enabled */
        if (myCurrency == null) {
            pPortfolio.addError(PrometheusDataItem.ERROR_MISSING, MoneyWiseStaticDataType.CURRENCY);
        } else if (!myCurrency.getEnabled()) {
            pPortfolio.addError(PrometheusDataItem.ERROR_DISABLED, MoneyWiseStaticDataType.CURRENCY);
        }

        /* If we have an infoSet */
        if (pPortfolio.getInfoSet() != null) {
            /* Validate the InfoSet */
            theInfoSet.validate(pPortfolio.getInfoSet());
        }

        /* Set validation flag */
        if (!pPortfolio.hasErrors()) {
            pPortfolio.setValidEdit();
        }
    }

    @Override
    public void validateName(final MoneyWisePortfolio pPortfolio,
                             final String pName) {
        /* Perform basic checks */
        super.validateName(pPortfolio, pName);

        /* Check that the name does not contain invalid characters */
        if (pName.contains(MoneyWiseSecurityHolding.SECURITYHOLDING_SEP)) {
            pPortfolio.addError(PrometheusDataItem.ERROR_INVALIDCHAR, PrometheusDataResource.DATAITEM_FIELD_NAME);
        }
    }
}
