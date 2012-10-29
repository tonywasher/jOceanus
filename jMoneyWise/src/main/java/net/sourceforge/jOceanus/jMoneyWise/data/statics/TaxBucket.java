/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012 Tony Washer
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
package net.sourceforge.jOceanus.jMoneyWise.data.statics;

/**
 * Enumeration of Tax Type Buckets.
 */
public enum TaxBucket {
    /**
     * Transaction Summary.
     */
    TRANSSUMM(0),

    /**
     * Transaction Total.
     */
    TRANSTOTAL(100),

    /**
     * Tax Detail.
     */
    TAXDETAIL(200),

    /**
     * Tax Summary.
     */
    TAXSUMM(300),

    /**
     * Tax Total.
     */
    TAXTOTAL(400);

    /**
     * Order base.
     */
    private final int theBase;

    /**
     * Get the order base.
     * @return the order base
     */
    protected int getBase() {
        return theBase;
    }

    /**
     * Constructor.
     * @param pBase the base
     */
    private TaxBucket(final int pBase) {
        theBase = pBase;
    }
}
