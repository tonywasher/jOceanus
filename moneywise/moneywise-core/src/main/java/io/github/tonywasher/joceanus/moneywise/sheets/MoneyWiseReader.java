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
package io.github.tonywasher.joceanus.moneywise.sheets;

import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseBasicDataType;
import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWiseStaticDataType;
import io.github.tonywasher.joceanus.prometheus.security.PrometheusSecurityPasswordManager;
import io.github.tonywasher.joceanus.prometheus.sheets.PrometheusSheetDataItem;
import io.github.tonywasher.joceanus.prometheus.sheets.PrometheusSheetReader;
import io.github.tonywasher.joceanus.tethys.api.factory.TethysUIFactory;
import io.github.tonywasher.joceanus.tethys.api.thread.TethysUIThreadStatusReport;

/**
 * SheetReader extension for MoneyWiseData.
 *
 * @author Tony Washer
 */
public class MoneyWiseReader
        extends PrometheusSheetReader {
    /**
     * Constructor.
     *
     * @param pFactory     the gui factory
     * @param pReport      the report
     * @param pPasswordMgr the password manager
     */
    public MoneyWiseReader(final TethysUIFactory<?> pFactory,
                           final TethysUIThreadStatusReport pReport,
                           final PrometheusSecurityPasswordManager pPasswordMgr) {
        /* Call super-constructor */
        super(pFactory, pReport, pPasswordMgr);
    }

    /**
     * Register sheets.
     */
    @Override
    protected void registerSheets() {
        /* Loop through the static types */
        for (MoneyWiseStaticDataType myType : MoneyWiseStaticDataType.values()) {
            /* Create the sheet */
            addSheet(newSheet(myType));
        }

        /* Loop through the basic types */
        for (MoneyWiseBasicDataType myType : MoneyWiseBasicDataType.values()) {
            /* Create the sheet */
            addSheet(newSheet(myType));
        }
    }

    /**
     * Create new sheet of required type.
     *
     * @param pDataType the data type
     * @return the new sheet
     */
    private PrometheusSheetDataItem<?> newSheet(final MoneyWiseStaticDataType pDataType) {
        /* Switch on data Type */
        return switch (pDataType) {
            case DEPOSITTYPE -> new MoneyWiseSheetDepositCategoryType(this);
            case CASHTYPE -> new MoneyWiseSheetCashCategoryType(this);
            case LOANTYPE -> new MoneyWiseSheetLoanCategoryType(this);
            case PORTFOLIOTYPE -> new MoneyWiseSheetPortfolioType(this);
            case PAYEETYPE -> new MoneyWiseSheetPayeeType(this);
            case SECURITYTYPE -> new MoneyWiseSheetSecurityType(this);
            case TRANSTYPE -> new MoneyWiseSheetTransCategoryType(this);
            case ACCOUNTINFOTYPE -> new MoneyWiseSheetAccountInfoType(this);
            case TRANSINFOTYPE -> new MoneyWiseSheetTransInfoType(this);
            case CURRENCY -> new MoneyWiseSheetCurrency(this);
            case TAXBASIS -> new MoneyWiseSheetTaxBasis(this);
            default -> throw new IllegalArgumentException(pDataType.toString());
        };
    }

    /**
     * Create new sheet of required type.
     *
     * @param pDataType the data type
     * @return the new sheet
     */
    private PrometheusSheetDataItem<?> newSheet(final MoneyWiseBasicDataType pDataType) {
        /* Switch on data Type */
        return switch (pDataType) {
            case TRANSTAG -> new MoneyWiseSheetTransTag(this);
            case REGION -> new MoneyWiseSheetRegion(this);
            case DEPOSITCATEGORY -> new MoneyWiseSheetDepositCategory(this);
            case CASHCATEGORY -> new MoneyWiseSheetCashCategory(this);
            case LOANCATEGORY -> new MoneyWiseSheetLoanCategory(this);
            case TRANSCATEGORY -> new MoneyWiseSheetTransCategory(this);
            case EXCHANGERATE -> new MoneyWiseSheetExchangeRate(this);
            case PAYEE -> new MoneyWiseSheetPayee(this);
            case PAYEEINFO -> new MoneyWiseSheetPayeeInfo(this);
            case SECURITY -> new MoneyWiseSheetSecurity(this);
            case SECURITYPRICE -> new MoneyWiseSheetSecurityPrice(this);
            case SECURITYINFO -> new MoneyWiseSheetSecurityInfo(this);
            case DEPOSIT -> new MoneyWiseSheetDeposit(this);
            case DEPOSITRATE -> new MoneyWiseSheetDepositRate(this);
            case DEPOSITINFO -> new MoneyWiseSheetDepositInfo(this);
            case CASH -> new MoneyWiseSheetCash(this);
            case CASHINFO -> new MoneyWiseSheetCashInfo(this);
            case LOAN -> new MoneyWiseSheetLoan(this);
            case LOANINFO -> new MoneyWiseSheetLoanInfo(this);
            case PORTFOLIO -> new MoneyWiseSheetPortfolio(this);
            case PORTFOLIOINFO -> new MoneyWiseSheetPortfolioInfo(this);
            case TRANSACTION -> new MoneyWiseSheetTransaction(this);
            case TRANSACTIONINFO -> new MoneyWiseSheetTransInfo(this);
            default -> throw new IllegalArgumentException(pDataType.toString());
        };
    }
}
