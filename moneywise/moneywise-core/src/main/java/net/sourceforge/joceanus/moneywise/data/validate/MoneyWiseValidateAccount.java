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
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseAssetBase.MoneyWiseAssetBaseList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseSecurityHolding;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataItem;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataResource;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataValidator;
import net.sourceforge.joceanus.prometheus.views.PrometheusEditSet;

/**
 * Validator for assetBase.
 * @param <T> the asset type
 */
public abstract class MoneyWiseValidateAccount<T extends MoneyWiseAssetBase>
        implements PrometheusDataValidator<T> {
    /**
     * Set the editSet.
     */
    private PrometheusEditSet theEditSet;

    /**
     * Set the editSet.
     * @param pEditSet the editSet
     */
    public void setEditSet(final PrometheusEditSet pEditSet) {
        theEditSet = pEditSet;
    }

    /**
     * Obtain the editSet
     * @return the editSet
     */
    PrometheusEditSet getEditSet() {
        if (theEditSet == null) {
            throw new IllegalStateException("editSet not set up");
        }
        return theEditSet;
    }

    @Override
    public void validate(final PrometheusDataItem pAsset) {
        final MoneyWiseAssetBase myAsset = (MoneyWiseAssetBase) pAsset;
        final String myName = myAsset.getName();
        final String myDesc = myAsset.getDesc();

        /* Name must be non-null */
        if (myName == null) {
            pAsset.addError(PrometheusDataItem.ERROR_MISSING, PrometheusDataResource.DATAITEM_FIELD_NAME);

            /* Check that the name is unique */
        } else {
            /* Validate the name */
            validateName(myAsset, myName);
        }

        /* Check description length */
        if (myDesc != null
                && myDesc.length() > PrometheusDataItem.DESCLEN) {
            pAsset.addError(PrometheusDataItem.ERROR_LENGTH, PrometheusDataResource.DATAITEM_FIELD_DESC);
        }
    }

    /**
     * Validate the name.
     * @param pAsset the asset
     * @param pName the name
     */
    public void validateName(final MoneyWiseAssetBase pAsset,
                             final String pName) {
        /* Access the list */
        final MoneyWiseAssetBaseList<?> myList = pAsset.getList();

        /* The name must not be too long */
        if (pName.length() > PrometheusDataItem.NAMELEN) {
            pAsset.addError(PrometheusDataItem.ERROR_LENGTH, PrometheusDataResource.DATAITEM_FIELD_NAME);
        }

        /* Check name count */
        if (!myList.validNameCount(pName)) {
            pAsset.addError(PrometheusDataItem.ERROR_DUPLICATE, PrometheusDataResource.DATAITEM_FIELD_NAME);
        }

        /* Check that the name does not contain invalid characters */
        if (pName.contains(MoneyWiseSecurityHolding.SECURITYHOLDING_SEP)) {
            pAsset.addError(PrometheusDataItem.ERROR_INVALIDCHAR, PrometheusDataResource.DATAITEM_FIELD_NAME);
        }
    }

    /**
     * Obtain the reporting currency
     * @return the reporting currency
     */
    MoneyWiseCurrency getReportingCurrency() {
        final MoneyWiseDataSet myDataSet = (MoneyWiseDataSet) getEditSet().getDataSet();
        return myDataSet.getReportingCurrency();
    }
}
