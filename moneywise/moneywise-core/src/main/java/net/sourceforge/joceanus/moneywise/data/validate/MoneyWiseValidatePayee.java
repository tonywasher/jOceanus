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
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicResource;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePayee;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePayee.MoneyWisePayeeDataMap;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePayee.MoneyWisePayeeList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWisePayeeClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWisePayeeType;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWisePayeeType.MoneyWisePayeeTypeList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseStaticDataType;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataItem;

import java.util.Iterator;

/**
 * Validator for Payee.
 */
public class MoneyWiseValidatePayee
        extends MoneyWiseValidateAccount<MoneyWisePayee> {
    /**
     * New Account name.
     */
    private static final String NAME_NEWACCOUNT = MoneyWiseBasicResource.PAYEE_NEWACCOUNT.getValue();

    /**
     * The infoSet validator.
     */
    private final MoneyWiseValidatePayeeInfoSet theInfoSet;

    /**
     * Constructor.
     */
    MoneyWiseValidatePayee() {
        theInfoSet = new MoneyWiseValidatePayeeInfoSet();
    }

    @Override
    public void validate(final PrometheusDataItem pPayee) {
        final MoneyWisePayee myPayee = (MoneyWisePayee) pPayee;
        final MoneyWisePayeeList myList = myPayee.getList();
        final MoneyWisePayeeType myPayeeType = myPayee.getCategory();
        final MoneyWisePayee myParent = myPayee.getParent();
        final MoneyWiseCurrency myCurrency = myPayee.getAssetCurrency();

        /* Validate base components */
        super.validate(pPayee);

        /* PayeeType must be non-null */
        if (myPayeeType == null) {
            pPayee.addError(PrometheusDataItem.ERROR_MISSING, MoneyWiseBasicResource.CATEGORY_NAME);
        } else {
            /* Access the class */
            final MoneyWisePayeeClass myClass = myPayeeType.getPayeeClass();

            /* PayeeType must be enabled */
            if (!myPayeeType.getEnabled()) {
                pPayee.addError(PrometheusDataItem.ERROR_DISABLED, MoneyWiseBasicResource.CATEGORY_NAME);
            }

            /* If the PayeeType is singular */
            if (myClass.isSingular()) {
                /* Count the elements of this class */
                final MoneyWisePayeeDataMap myMap = myList.getDataMap();
                if (!myMap.validSingularCount(myClass)) {
                    pPayee.addError(PrometheusDataItem.ERROR_MULT, MoneyWiseBasicResource.CATEGORY_NAME);
                }
            }
        }

        /* Parent must be null */
        if (myParent != null) {
            pPayee.addError(PrometheusDataItem.ERROR_EXIST, MoneyWiseBasicResource.ASSET_PARENT);
        }

        /* Currency must be null */
        if (myCurrency != null) {
            pPayee.addError(PrometheusDataItem.ERROR_EXIST, MoneyWiseStaticDataType.CURRENCY);
        }

        /* If we have an infoSet */
        if (myPayee.getInfoSet() != null) {
            /* Validate the InfoSet */
            theInfoSet.validate(myPayee.getInfoSet());
        }

        /* Set validation flag */
        if (!pPayee.hasErrors()) {
            pPayee.setValidEdit();
        }
    }

    @Override
    public void setDefaults(final MoneyWisePayee pPayee) throws OceanusException {
        /* Set values */
        final MoneyWisePayeeList myList = pPayee.getList();
        pPayee.setCategory(getDefaultPayeeType());
        pPayee.setName(getUniqueName(myList, NAME_NEWACCOUNT));
        pPayee.setClosed(Boolean.FALSE);
    }

    @Override
    public void autoCorrect(final MoneyWisePayee pItem) throws OceanusException {
        theInfoSet.autoCorrect(pItem.getInfoSet());
    }

    /**
     * Obtain payee type for new payee account.
     *
     * @return the payee type
     */
    private MoneyWisePayeeType getDefaultPayeeType() {
        /* Access payee types */
        final MoneyWisePayeeTypeList myTypes
                = getEditSet().getDataList(MoneyWiseStaticDataType.PAYEETYPE, MoneyWisePayeeTypeList.class);

        /* loop through the payee types */
        final Iterator<MoneyWisePayeeType> myIterator = myTypes.iterator();
        while (myIterator.hasNext()) {
            final MoneyWisePayeeType myType = myIterator.next();

            /* Ignore deleted and singular types */
            if (!myType.isDeleted() && !myType.getPayeeClass().isSingular()) {
                return myType;
            }
        }

        /* Return no category */
        return null;
    }
}
