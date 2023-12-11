/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2023 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.atlas.sheets;

import net.sourceforge.joceanus.jprometheus.atlas.sheets.PrometheusSheetWriter;
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
        /* Register the sheets */
        addSheet(new MoneyWiseSheetDepositCategoryType(this));
        addSheet(new MoneyWiseSheetCashCategoryType(this));
        addSheet(new MoneyWiseSheetLoanCategoryType(this));
        addSheet(new MoneyWiseSheetPayeeType(this));
        addSheet(new MoneyWiseSheetPortfolioType(this));
        addSheet(new MoneyWiseSheetSecurityType(this));
        addSheet(new MoneyWiseSheetTransCategoryType(this));
        addSheet(new MoneyWiseSheetTaxBasis(this));
        addSheet(new MoneyWiseSheetCurrency(this));
        addSheet(new MoneyWiseSheetAccountInfoType(this));
        addSheet(new MoneyWiseSheetTransInfoType(this));
        addSheet(new MoneyWiseSheetTransTag(this));
        addSheet(new MoneyWiseSheetRegion(this));
        addSheet(new MoneyWiseSheetDepositCategory(this));
        addSheet(new MoneyWiseSheetCashCategory(this));
        addSheet(new MoneyWiseSheetLoanCategory(this));
        addSheet(new MoneyWiseSheetTransCategory(this));
        addSheet(new MoneyWiseSheetExchangeRate(this));
        addSheet(new MoneyWiseSheetPayee(this));
        addSheet(new MoneyWiseSheetPayeeInfo(this));
        addSheet(new MoneyWiseSheetSecurity(this));
        addSheet(new MoneyWiseSheetSecurityInfo(this));
        addSheet(new MoneyWiseSheetSecurityPrice(this));
        addSheet(new MoneyWiseSheetDeposit(this));
        addSheet(new MoneyWiseSheetDepositInfo(this));
        addSheet(new MoneyWiseSheetDepositRate(this));
        addSheet(new MoneyWiseSheetCash(this));
        addSheet(new MoneyWiseSheetCashInfo(this));
        addSheet(new MoneyWiseSheetLoan(this));
        addSheet(new MoneyWiseSheetLoanInfo(this));
        addSheet(new MoneyWiseSheetPortfolio(this));
        addSheet(new MoneyWiseSheetPortfolioInfo(this));
        addSheet(new MoneyWiseSheetTransaction(this));
        addSheet(new MoneyWiseSheetTransInfo(this));
    }
}
