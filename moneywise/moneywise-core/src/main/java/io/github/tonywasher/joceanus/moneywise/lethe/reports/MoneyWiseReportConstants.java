/*
 * MoneyWise: Finance Application
 * Copyright 2026. Tony Washer
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

package io.github.tonywasher.joceanus.moneywise.lethe.reports;

import io.github.tonywasher.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisDataResource;
import io.github.tonywasher.joceanus.moneywise.lethe.data.analysis.values.MoneyWiseAnalysisValuesResource;

/**
 * Report Constants.
 */
public final class MoneyWiseReportConstants {
    /**
     * Private constructor.
     */
    private MoneyWiseReportConstants() {
    }

    /**
     * The Total text.
     */
    static final String TEXT_TOTAL = MoneyWiseAnalysisDataResource.ANALYSIS_TOTALS.getValue();

    /**
     * The Profit text.
     */
    static final String TEXT_PROFIT = MoneyWiseAnalysisValuesResource.SECURITYATTR_PROFIT.getValue();

    /**
     * The Income text.
     */
    static final String TEXT_INCOME = MoneyWiseAnalysisValuesResource.PAYEEATTR_INCOME.getValue();

    /**
     * The Expense text.
     */
    static final String TEXT_EXPENSE = MoneyWiseAnalysisValuesResource.PAYEEATTR_EXPENSE.getValue();
}
