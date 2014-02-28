/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2014 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jmoneywise.sheets;

import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jprometheus.data.TaskControl;
import net.sourceforge.joceanus.jprometheus.sheets.SheetWriter;

/**
 * SheetWriter extension for MoneyWiseData.
 * @author Tony Washer
 */
public class MoneyWiseWriter
        extends SheetWriter<MoneyWiseData> {
    /**
     * Constructor.
     * @param pTask the Task control.
     */
    public MoneyWiseWriter(final TaskControl<MoneyWiseData> pTask) {
        /* Call super-constructor */
        super(pTask);
    }

    /**
     * Register sheets.
     */
    @Override
    protected void registerSheets() {
        /* Register the sheets */
        addSheet(new SheetAccountCategoryType(this));
        addSheet(new SheetDepositCategoryType(this));
        addSheet(new SheetCashCategoryType(this));
        addSheet(new SheetLoanCategoryType(this));
        addSheet(new SheetPayeeType(this));
        addSheet(new SheetSecurityType(this));
        addSheet(new SheetEventCategoryType(this));
        addSheet(new SheetTaxBasis(this));
        addSheet(new SheetTaxCategory(this));
        addSheet(new SheetAccountCurrency(this));
        addSheet(new SheetTaxRegime(this));
        addSheet(new SheetFrequency(this));
        addSheet(new SheetTaxYearInfoType(this));
        addSheet(new SheetAccountInfoType(this));
        addSheet(new SheetEventInfoType(this));
        addSheet(new SheetEventTag(this));
        addSheet(new SheetAccountCategory(this));
        addSheet(new SheetDepositCategory(this));
        addSheet(new SheetCashCategory(this));
        addSheet(new SheetLoanCategory(this));
        addSheet(new SheetEventCategory(this));
        addSheet(new SheetTaxYear(this));
        addSheet(new SheetTaxYearInfo(this));
        addSheet(new SheetExchangeRate(this));
        addSheet(new SheetPayee(this));
        addSheet(new SheetPayeeInfo(this));
        addSheet(new SheetSecurity(this));
        addSheet(new SheetSecurityInfo(this));
        addSheet(new SheetDeposit(this));
        addSheet(new SheetDepositInfo(this));
        addSheet(new SheetCash(this));
        addSheet(new SheetCashInfo(this));
        addSheet(new SheetLoan(this));
        addSheet(new SheetLoanInfo(this));
        addSheet(new SheetAccount(this));
        addSheet(new SheetPortfolio(this));
        addSheet(new SheetPortfolioInfo(this));
        addSheet(new SheetAccountRate(this));
        addSheet(new SheetDepositRate(this));
        addSheet(new SheetAccountPrice(this));
        addSheet(new SheetSecurityPrice(this));
        addSheet(new SheetAccountInfo(this));
        addSheet(new SheetEvent(this));
        addSheet(new SheetEventInfo(this));
        addSheet(new SheetPattern(this));
    }
}
