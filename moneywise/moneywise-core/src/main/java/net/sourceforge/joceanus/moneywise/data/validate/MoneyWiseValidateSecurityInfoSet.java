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

import net.sourceforge.joceanus.metis.field.MetisFieldRequired;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseSecurity;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseSecurityInfo;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseSecurityInfoSet;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseAccountInfoClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseSecurityClass;
import net.sourceforge.joceanus.oceanus.decimal.OceanusPrice;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataInfoClass;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataItem;

/**
 * Validate SecurityInfoSet.
 */
public class MoneyWiseValidateSecurityInfoSet
        extends MoneyWiseValidateInfoSet<MoneyWiseSecurityInfo> {
    @Override
    public MoneyWiseSecurity getOwner() {
        return (MoneyWiseSecurity) super.getOwner();
    }

    @Override
    public MetisFieldRequired isClassRequired(final PrometheusDataInfoClass pClass) {
        /* Access details about the Security */
        final MoneyWiseSecurity mySec = getOwner();
        final MoneyWiseSecurityClass myType = mySec.getCategoryClass();

        /* If we have no Type, no class is allowed */
        if (myType == null) {
            return MetisFieldRequired.NOTALLOWED;
        }
        /* Switch on class */
        switch ((MoneyWiseAccountInfoClass) pClass) {
            /* Allowed set */
            case NOTES:
                return MetisFieldRequired.CANEXIST;

            /* Symbol */
            case SYMBOL:
                return myType.needsSymbol()
                        ? MetisFieldRequired.MUSTEXIST
                        : MetisFieldRequired.NOTALLOWED;

            /* Region */
            case REGION:
                return myType.needsRegion()
                        ? MetisFieldRequired.MUSTEXIST
                        : MetisFieldRequired.NOTALLOWED;

            /* Options */
            case UNDERLYINGSTOCK:
            case OPTIONPRICE:
                return myType.isOption()
                        ? MetisFieldRequired.MUSTEXIST
                        : MetisFieldRequired.NOTALLOWED;

            /* Not Allowed */
            case SORTCODE:
            case ACCOUNT:
            case REFERENCE:
            case WEBSITE:
            case CUSTOMERNO:
            case USERID:
            case PASSWORD:
            case MATURITY:
            case OPENINGBALANCE:
            case AUTOEXPENSE:
            case AUTOPAYEE:
            default:
                return MetisFieldRequired.NOTALLOWED;
        }
    }

    @Override
    void validateClass(final MoneyWiseSecurityInfo pInfo,
                       final PrometheusDataInfoClass pClass) {
        /* Switch on class */
        switch ((MoneyWiseAccountInfoClass) pClass) {
            case NOTES:
                validateNotes(pInfo);
                break;
            case SYMBOL:
                validateSymbol(pInfo);
                break;
            case UNDERLYINGSTOCK:
                validateUnderlyingStock(pInfo);
                break;
            case OPTIONPRICE:
                validateOptionPrice(pInfo);
                break;
            default:
                break;
        }
    }

    /**
     * Validate the Notes info.
     * @param pInfo the info
     */
    private void validateNotes(final MoneyWiseSecurityInfo pInfo) {
        final char[] myArray = pInfo.getValue(char[].class);
        if (myArray.length > MoneyWiseAccountInfoClass.NOTES.getMaximumLength()) {
            getOwner().addError(PrometheusDataItem.ERROR_LENGTH, MoneyWiseSecurityInfoSet.getFieldForClass(MoneyWiseAccountInfoClass.NOTES));
        }
    }

    /**
     * Validate the Symbol info.
     * @param pInfo the info
     */
    private void validateSymbol(final MoneyWiseSecurityInfo pInfo) {
        final String mySymbol = pInfo.getValue(String.class);
        if (mySymbol.length() > MoneyWiseAccountInfoClass.SYMBOL.getMaximumLength()) {
            getOwner().addError(PrometheusDataItem.ERROR_LENGTH, MoneyWiseSecurityInfoSet.getFieldForClass(MoneyWiseAccountInfoClass.SYMBOL));
        }
    }

    /**
     * Validate the UnderlyingStock info.
     * @param pInfo the info
     */
    private void validateUnderlyingStock(final MoneyWiseSecurityInfo pInfo) {
        final MoneyWiseSecurity myStock = pInfo.getValue(MoneyWiseSecurity.class);
        if (!myStock.getCategoryClass().isShares()) {
            getOwner().addError("Invalid underlying stock", MoneyWiseSecurityInfoSet.getFieldForClass(MoneyWiseAccountInfoClass.UNDERLYINGSTOCK));
        }
    }

    /**
     * Validate the OptionPrice info.
     * @param pInfo the info
     */
    private void validateOptionPrice(final MoneyWiseSecurityInfo pInfo) {
        final OceanusPrice myPrice = pInfo.getValue(OceanusPrice.class);
        if (myPrice.isZero()) {
            getOwner().addError(PrometheusDataItem.ERROR_ZERO, MoneyWiseSecurityInfoSet.getFieldForClass(MoneyWiseAccountInfoClass.OPTIONPRICE));
        } else if (!myPrice.isPositive()) {
            getOwner().addError(PrometheusDataItem.ERROR_NEGATIVE, MoneyWiseSecurityInfoSet.getFieldForClass(MoneyWiseAccountInfoClass.OPTIONPRICE));
        }
    }
}
