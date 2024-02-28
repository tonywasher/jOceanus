/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.sheets;

import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWiseStaticDataType;
import net.sourceforge.joceanus.jprometheus.sheets.PrometheusSheetDataItem;
import net.sourceforge.joceanus.jprometheus.sheets.PrometheusSheetWriter;
import net.sourceforge.joceanus.jtethys.ui.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.jtethys.ui.api.thread.TethysUIThreadStatusReport;

/**
 * SheetWriter extension for MoneyWiseData.
 * @author Tony Washer
 */
public class MoneyWiseWriter
        extends PrometheusSheetWriter {
    /**
     * Constructor.
     * @param pFactory the gui factory
     * @param pReport the report
     */
    public MoneyWiseWriter(final TethysUIFactory<?> pFactory,
                           final TethysUIThreadStatusReport pReport) {
        /* Call super-constructor */
        super(pFactory, pReport);
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
     * @param pDataType the data type
     * @return the new sheet
     */
    private PrometheusSheetDataItem<?> newSheet(final MoneyWiseStaticDataType pDataType) {
        /* Switch on data Type */
        switch (pDataType) {
            case DEPOSITTYPE:
                return new MoneyWiseSheetDepositCategoryType(this);
            case CASHTYPE:
                return new MoneyWiseSheetCashCategoryType(this);
            case LOANTYPE:
                return new MoneyWiseSheetLoanCategoryType(this);
            case PORTFOLIOTYPE:
                return new MoneyWiseSheetPortfolioType(this);
            case PAYEETYPE:
                return new MoneyWiseSheetPayeeType(this);
            case SECURITYTYPE:
                return new MoneyWiseSheetSecurityType(this);
            case TRANSTYPE:
                return new MoneyWiseSheetTransCategoryType(this);
            case ACCOUNTINFOTYPE:
                return new MoneyWiseSheetAccountInfoType(this);
            case TRANSINFOTYPE:
                return new MoneyWiseSheetTransInfoType(this);
            case CURRENCY:
                return new MoneyWiseSheetCurrency(this);
            case TAXBASIS:
                return new MoneyWiseSheetTaxBasis(this);
            default:
                throw new IllegalArgumentException(pDataType.toString());
        }
    }

    /**
     * Create new sheet of required type.
     * @param pDataType the data type
     * @return the new sheet
     */
    private PrometheusSheetDataItem<?> newSheet(final MoneyWiseBasicDataType pDataType) {
        /* Switch on data Type */
        switch (pDataType) {
            case TRANSTAG:
                return new MoneyWiseSheetTransTag(this);
            case REGION:
                return new MoneyWiseSheetRegion(this);
            case DEPOSITCATEGORY:
                return new MoneyWiseSheetDepositCategory(this);
            case CASHCATEGORY:
                return new MoneyWiseSheetCashCategory(this);
            case LOANCATEGORY:
                return new MoneyWiseSheetLoanCategory(this);
            case TRANSCATEGORY:
                return new MoneyWiseSheetTransCategory(this);
            case EXCHANGERATE:
                return new MoneyWiseSheetExchangeRate(this);
            case PAYEE:
                return new MoneyWiseSheetPayee(this);
            case PAYEEINFO:
                return new MoneyWiseSheetPayeeInfo(this);
            case SECURITY:
                return new MoneyWiseSheetSecurity(this);
            case SECURITYPRICE:
                return new MoneyWiseSheetSecurityPrice(this);
            case SECURITYINFO:
                return new MoneyWiseSheetSecurityInfo(this);
            case DEPOSIT:
                return new MoneyWiseSheetDeposit(this);
            case DEPOSITRATE:
                return new MoneyWiseSheetDepositRate(this);
            case DEPOSITINFO:
                return new MoneyWiseSheetDepositInfo(this);
            case CASH:
                return new MoneyWiseSheetCash(this);
            case CASHINFO:
                return new MoneyWiseSheetCashInfo(this);
            case LOAN:
                return new MoneyWiseSheetLoan(this);
            case LOANINFO:
                return new MoneyWiseSheetLoanInfo(this);
            case PORTFOLIO:
                return new MoneyWiseSheetPortfolio(this);
            case PORTFOLIOINFO:
                return new MoneyWiseSheetPortfolioInfo(this);
            case TRANSACTION:
                return new MoneyWiseSheetTransaction(this);
            case TRANSACTIONINFO:
                return new MoneyWiseSheetTransInfo(this);
            default:
                throw new IllegalArgumentException(pDataType.toString());
        }
    }
}
