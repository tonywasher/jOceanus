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
package net.sourceforge.joceanus.moneywise.lethe.data.analysis.base;

import net.sourceforge.joceanus.jmetis.data.MetisDataType;

/**
 * Methods for a Bucket Attribute.
 */
public interface MoneyWiseAnalysisAttribute {
    /**
     * Obtain DataType.
     * @return the data type
     */
    MetisDataType getDataType();

    /**
     * Is this a counter value.
     * @return true/false
     */
    boolean isCounter();
}
