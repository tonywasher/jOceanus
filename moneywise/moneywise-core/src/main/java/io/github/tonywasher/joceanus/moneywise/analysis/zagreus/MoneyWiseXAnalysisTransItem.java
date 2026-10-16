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

package io.github.tonywasher.joceanus.moneywise.analysis.zagreus;

/**
 * Transaction Analysis elements.
 */
public enum MoneyWiseXAnalysisTransItem {
    /**
     * Account Delta Value.
     */
    ACCOUNTDELTA,

    /**
     * Account Reporting Delta Value.
     */
    ACCOUNTREPORTING,

    /**
     * Partner Delta Value.
     */
    PARTNERDELTA,

    /**
     * Account Reporting Delta Value.
     */
    PARTNERREPORTING,

    /**
     * Returned Delta Value.
     */
    RETURNEDDELTA,

    /**
     * Returned Reporting Delta Value.
     */
    RETURNEDREPORTING,

    /**
     * TaxCredit Delta Value.
     */
    TAXCREDITDELTA,

    /**
     * TaxCredit Reporting Delta Value.
     */
    TAXCREDITREPORTING,

    /**
     * Employer NatIns Delta Value.
     */
    ERNATINSDELTA,

    /**
     * Employer NatIns Reporting Delta Value.
     */
    ERNATINSREPORTING,

    /**
     * Employee NatIns Delta Value.
     */
    EENATINSDELTA,

    /**
     * Employee NatIns Reporting Delta Value.
     */
    EENATINSREPORTING,

    /**
     * Benefit Delta Value.
     */
    BENEFITDELTA,

    /**
     * ThirdParty Reporting Delta Value.
     */
    BENEFITREPORTING,

    /**
     * Withheld Delta Value.
     */
    WITHHELDDELTA,

    /**
     * Withheld Reporting Delta Value.
     */
    WITHHELDREPORTING,
}
