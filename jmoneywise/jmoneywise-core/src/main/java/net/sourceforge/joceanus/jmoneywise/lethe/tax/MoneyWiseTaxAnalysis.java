/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2018 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.lethe.tax;

import java.util.Iterator;

import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;

/**
 * UK Tax Analysis.
 */
public interface MoneyWiseTaxAnalysis {
    /**
     * Obtain the taxYear.
     * @return the taxYear
     */
    MoneyWiseTaxYear getTaxYear();

    /**
     * Obtain the taxableIncome.
     * @return the taxableIncome
     */
    TethysMoney getTaxableIncome();

    /**
     * Obtain the taxDue.
     * @return the taxDue
     */
    TethysMoney getTaxDue();

    /**
     * Obtain the taxPaid.
     * @return the taxPaid
     */
    TethysMoney getTaxPaid();

    /**
     * Obtain the taxProfit.
     * @return the taxProfit
     */
    TethysMoney getTaxProfit();

    /**
     * Obtain the taxBands iterator.
     * @return the iterator
     */
    Iterator<MoneyWiseTaxDueBucket> taxDueIterator();
}
