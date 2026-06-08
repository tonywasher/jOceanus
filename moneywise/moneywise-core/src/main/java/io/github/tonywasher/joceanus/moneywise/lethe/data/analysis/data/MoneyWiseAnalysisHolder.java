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

package io.github.tonywasher.joceanus.moneywise.lethe.data.analysis.data;

import io.github.tonywasher.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisPayeeBucket.MoneyWiseAnalysisPayeeBucketList;
import io.github.tonywasher.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisTaxBasisBucket.MoneyWiseAnalysisTaxBasisBucketList;
import io.github.tonywasher.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisTransCategoryBucket.MoneyWiseAnalysisTransCategoryBucketList;

/**
 * Analysis Data Holder.
 */
public interface MoneyWiseAnalysisHolder
        extends MoneyWiseAnalysisControl {
    /**
     * Obtain the payee buckets list.
     *
     * @return the list
     */
    MoneyWiseAnalysisPayeeBucketList getPayees();

    /**
     * Obtain the transaction categories list.
     *
     * @return the list
     */
    MoneyWiseAnalysisTransCategoryBucketList getTransCategories();

    /**
     * Obtain the tax basis list.
     *
     * @return the list
     */
    MoneyWiseAnalysisTaxBasisBucketList getTaxBasis();
}
