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

import net.sourceforge.joceanus.metis.list.MetisListKey;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseStaticDataType;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataSet.PrometheusCryptographyDataType;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataValidator;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataValidator.PrometheusDataValidatorFactory;
import net.sourceforge.joceanus.prometheus.validate.PrometheusValidateBasic;
import net.sourceforge.joceanus.prometheus.validate.PrometheusValidateInfo;
import net.sourceforge.joceanus.prometheus.validate.PrometheusValidateStatic;

/**
 * Validator factory.
 */
public class MoneyWiseValidatorFactory
        implements PrometheusDataValidatorFactory {
    /**
     * Are we using new Validation?
     */
    private final boolean newValidation;

    /**
     * Constructor.
     */
    public MoneyWiseValidatorFactory() {
        this(false);
    }

    /**
     * Constructor.
     * @param pNewValidation true/false
     */
    public MoneyWiseValidatorFactory(final boolean pNewValidation) {
        newValidation = pNewValidation;
    }

    @Override
    public PrometheusDataValidator newValidator(final MetisListKey pItemType) {
        if (pItemType instanceof PrometheusCryptographyDataType) {
            return new PrometheusValidateBasic();
        } else if (MoneyWiseStaticDataType.CURRENCY.equals(pItemType)) {
            return new MoneyWiseValidateCurrency();
        } else if (pItemType instanceof MoneyWiseStaticDataType) {
            return new PrometheusValidateStatic();
        } else if (pItemType instanceof MoneyWiseBasicDataType) {
            switch ((MoneyWiseBasicDataType) pItemType) {
                case TRANSTAG:
                    return new MoneyWiseValidateTransTag();
                case REGION:
                    return new MoneyWiseValidateRegion();
                case DEPOSITCATEGORY:
                    return new MoneyWiseValidateDepositCategory();
                case CASHCATEGORY:
                    return new MoneyWiseValidateCashCategory();
                case LOANCATEGORY:
                    return new MoneyWiseValidateLoanCategory();
                case TRANSCATEGORY:
                    return new MoneyWiseValidateTransCategory();
                case EXCHANGERATE:
                    return new MoneyWiseValidateXchangeRate();
                case PAYEE:
                    return new MoneyWiseValidatePayee();
                case SECURITY:
                    return new MoneyWiseValidateSecurity();
                case SECURITYPRICE:
                    return new MoneyWiseValidateSecurityPrice();
                case DEPOSIT:
                    return new MoneyWiseValidateDeposit();
                case DEPOSITRATE:
                    return new MoneyWiseValidateDepositRate();
                case CASH:
                    return new MoneyWiseValidateCash();
                case LOAN:
                    return new MoneyWiseValidateLoan();
                case PORTFOLIO:
                    return new MoneyWiseValidatePortfolio();
                case TRANSACTION:
                    return new MoneyWiseValidateTransaction(newValidation);
                case PAYEEINFO:
                case DEPOSITINFO:
                case SECURITYINFO:
                case CASHINFO:
                case LOANINFO:
                case PORTFOLIOINFO:
                case TRANSACTIONINFO:
                    return new PrometheusValidateInfo();
                default:
                    break;
            }
        }

        /* Throw error */
        throw new IllegalArgumentException("Unexpected itemType " + pItemType);
    }
}
