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
package io.github.tonywasher.joceanus.moneywise.data.validate;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.metis.field.MetisFieldRequired;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseAssetBase;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseBasicDataType;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseBasicResource;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseDataValidator.MoneyWiseDataValidatorAutoCorrect;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWisePayee;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWisePayee.MoneyWisePayeeList;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseSecurity;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseSecurity.MoneyWiseSecurityDataMap;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseSecurity.MoneyWiseSecurityList;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseSecurityHolding;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseSecurityInfoSet;
import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWiseAccountInfoClass;
import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWiseCurrency;
import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWisePayeeClass;
import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWiseSecurityClass;
import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWiseSecurityType;
import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWiseSecurityType.MoneyWiseSecurityTypeList;
import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWiseStaticDataType;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataItem;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataResource;
import io.github.tonywasher.joceanus.prometheus.views.PrometheusEditSet;

import java.util.Iterator;

/**
 * Validator for Security.
 */
public class MoneyWiseValidateSecurity
        extends MoneyWiseValidateAccount<MoneyWiseSecurity>
        implements MoneyWiseDataValidatorAutoCorrect<MoneyWiseSecurity> {
    /**
     * New Account name.
     */
    private static final String NAME_NEWACCOUNT = MoneyWiseBasicResource.SECURITY_NEWACCOUNT.getValue();

    /**
     * The infoSet validator.
     */
    private final MoneyWiseValidateSecurityInfoSet theInfoSet;

    /**
     * Constructor.
     */
    MoneyWiseValidateSecurity() {
        theInfoSet = new MoneyWiseValidateSecurityInfoSet();
    }

    @Override
    public void setEditSet(final PrometheusEditSet pEditSet) {
        super.setEditSet(pEditSet);
        theInfoSet.storeEditSet(pEditSet);
    }

    @Override
    public void validate(final PrometheusDataItem pSecurity) {
        final MoneyWiseSecurity mySecurity = (MoneyWiseSecurity) pSecurity;
        final MoneyWiseSecurityList myList = mySecurity.getList();
        final MoneyWisePayee myParent = mySecurity.getParent();
        final MoneyWiseSecurityType mySecType = mySecurity.getCategory();
        final MoneyWiseCurrency myCurrency = mySecurity.getAssetCurrency();
        final String mySymbol = mySecurity.getSymbol();

        /* Validate base components */
        super.validate(pSecurity);

        /* SecurityType must be non-null */
        if (mySecType == null) {
            pSecurity.addError(PrometheusDataItem.ERROR_MISSING, MoneyWiseBasicResource.CATEGORY_NAME);
        } else {
            /* Access the class */
            final MoneyWiseSecurityClass myClass = mySecType.getSecurityClass();

            /* SecurityType must be enabled */
            if (!mySecType.getEnabled()) {
                pSecurity.addError(PrometheusDataItem.ERROR_DISABLED, MoneyWiseBasicResource.CATEGORY_NAME);
            }

            /* If the SecurityType is singular */
            if (myClass.isSingular()) {
                /* Count the elements of this class */
                final MoneyWiseSecurityDataMap myMap = myList.getDataMap();
                if (!myMap.validSingularCount(myClass)) {
                    pSecurity.addError(PrometheusDataItem.ERROR_MULT, MoneyWiseBasicResource.CATEGORY_NAME);
                }
            }
        }

        /* Currency must be non-null and enabled */
        if (myCurrency == null) {
            pSecurity.addError(PrometheusDataItem.ERROR_MISSING, MoneyWiseStaticDataType.CURRENCY);
        } else if (!myCurrency.getEnabled()) {
            pSecurity.addError(PrometheusDataItem.ERROR_DISABLED, MoneyWiseStaticDataType.CURRENCY);
        }

        /* Parent must be non-null */
        if (myParent == null) {
            pSecurity.addError(PrometheusDataItem.ERROR_MISSING, MoneyWiseBasicResource.ASSET_PARENT);
        } else {
            /* If we are open then parent must be open */
            if (!mySecurity.isClosed() && Boolean.TRUE.equals(myParent.isClosed())) {
                pSecurity.addError(ERROR_PARCLOSED, MoneyWiseBasicResource.ASSET_CLOSED);
            }

            /* Check class */
            if (mySecType != null) {
                /* Access the classes */
                final MoneyWiseSecurityClass myClass = mySecType.getSecurityClass();
                final MoneyWisePayeeClass myParClass = myParent.getCategoryClass();

                /* Parent must be suitable */
                if (!myParClass.canParentSecurity(myClass)) {
                    pSecurity.addError(ERROR_BADPARENT, MoneyWiseBasicResource.ASSET_PARENT);
                }
            }
        }

        /* If we have a securityType */
        if (mySecType != null) {
            /* Check symbol rules */
            if (mySecType.getSecurityClass().needsSymbol()) {
                if (mySymbol == null) {
                    pSecurity.addError(PrometheusDataItem.ERROR_MISSING, MoneyWiseSecurityInfoSet.getFieldForClass(MoneyWiseAccountInfoClass.SYMBOL));
                } else if (!myList.validSymbolCount(mySymbol)) {
                    pSecurity.addError(PrometheusDataItem.ERROR_DUPLICATE, MoneyWiseSecurityInfoSet.getFieldForClass(MoneyWiseAccountInfoClass.SYMBOL));
                }
            } else if (mySymbol != null) {
                pSecurity.addError(PrometheusDataItem.ERROR_EXIST, MoneyWiseSecurityInfoSet.getFieldForClass(MoneyWiseAccountInfoClass.SYMBOL));
            }
        }

        /* If we have an infoSet */
        if (mySecurity.getInfoSet() != null) {
            /* Validate the InfoSet */
            theInfoSet.validate(mySecurity.getInfoSet());
        }

        /* Set validation flag */
        if (!pSecurity.hasErrors()) {
            pSecurity.setValidEdit();
        }
    }

    @Override
    public void validateName(final MoneyWiseAssetBase pSecurity,
                             final String pName) {
        /* Perform basic checks */
        super.validateName(pSecurity, pName);

        /* Check that the name is not a reserved name */
        if (pName.equals(MoneyWiseSecurityHolding.SECURITYHOLDING_NEW)
                || pName.equals(MoneyWiseValidatePortfolio.NAME_CASHACCOUNT)) {
            pSecurity.addError(ERROR_RESERVED, PrometheusDataResource.DATAITEM_FIELD_NAME);
        }
    }

    /**
     * Determine if an infoSet class is required.
     *
     * @param pClass the infoSet class
     * @return the status
     */
    public MetisFieldRequired isClassRequired(final MoneyWiseAccountInfoClass pClass) {
        return theInfoSet.isClassRequired(pClass);
    }

    @Override
    public void setDefaults(final MoneyWiseSecurity pSecurity) throws OceanusException {
        /* Set values */
        final MoneyWiseSecurityList myList = pSecurity.getList();
        pSecurity.setName(getUniqueName(myList, NAME_NEWACCOUNT));
        pSecurity.setCategory(getDefaultSecurityType());
        pSecurity.setAssetCurrency(getReportingCurrency());
        pSecurity.setSymbol(pSecurity.getName());
        pSecurity.setClosed(Boolean.FALSE);
        autoCorrect(pSecurity);
    }

    @Override
    public void autoCorrect(final MoneyWiseSecurity pSecurity) throws OceanusException {
        /* Access category class and parent */
        final MoneyWiseSecurityClass myClass = pSecurity.getCategoryClass();
        final MoneyWisePayee myParent = pSecurity.getParent();

        /* Ensure that we have a valid parent */
        if (myParent == null
                || myParent.getCategoryClass().canParentSecurity(myClass)) {
            pSecurity.setParent(getDefaultParent(pSecurity));
        }

        /* autoCorrect the infoSet */
        theInfoSet.autoCorrect(pSecurity.getInfoSet());
    }

    /**
     * Obtain security type for new security account.
     *
     * @return the security type
     */
    private MoneyWiseSecurityType getDefaultSecurityType() {
        /* loop through the security types */
        final MoneyWiseSecurityTypeList myTypes
                = getEditSet().getDataList(MoneyWiseStaticDataType.SECURITYTYPE, MoneyWiseSecurityTypeList.class);
        final Iterator<MoneyWiseSecurityType> myIterator = myTypes.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseSecurityType myType = myIterator.next();

            /* Ignore deleted types */
            if (!myType.isDeleted()) {
                return myType;
            }
        }

        /* Return no category */
        return null;
    }

    /**
     * Obtain default parent for new security.
     *
     * @param pSecurity the security
     * @return the default parent
     */
    private MoneyWisePayee getDefaultParent(final MoneyWiseSecurity pSecurity) {
        /* Access details */
        final MoneyWisePayeeList myPayees = getEditSet().getDataList(MoneyWiseBasicDataType.PAYEE, MoneyWisePayeeList.class);
        final MoneyWiseSecurityClass myClass = pSecurity.getCategoryClass();

        /* loop through the payees */
        final Iterator<MoneyWisePayee> myIterator = myPayees.iterator();
        while (myIterator.hasNext()) {
            final MoneyWisePayee myPayee = myIterator.next();

            /* Ignore deleted and closed payees */
            if (myPayee.isDeleted() || Boolean.TRUE.equals(myPayee.isClosed())) {
                continue;
            }

            /* If the payee can parent */
            if (myPayee.getCategoryClass().canParentSecurity(myClass)) {
                return myPayee;
            }
        }

        /* Return no payee */
        return null;
    }
}
