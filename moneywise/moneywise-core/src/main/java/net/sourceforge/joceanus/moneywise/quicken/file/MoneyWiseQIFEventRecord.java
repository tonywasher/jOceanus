/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.moneywise.quicken.file;

import net.sourceforge.joceanus.moneywise.quicken.definitions.MoneyWiseQLineType;
import net.sourceforge.joceanus.oceanus.date.OceanusDate;

/**
 * Class representing a QIF Event record.
 * @param <T> the line type
 */
public abstract class MoneyWiseQIFEventRecord<T extends Enum<T> & MoneyWiseQLineType>
        extends MoneyWiseQIFRecord<T>
        implements Comparable<MoneyWiseQIFEventRecord<?>> {
    /**
     * Constructor.
     * @param pFile the QIF File
     * @param pClass the line type class
     */
    protected MoneyWiseQIFEventRecord(final MoneyWiseQIFFile pFile,
                                      final Class<T> pClass) {
        /* Call super-constructor */
        super(pFile, pClass);
    }

    /**
     * Obtain the date.
     * @return the date.
     */
    public abstract OceanusDate getDate();

    /**
     * Is the record cleared.
     * @return true/false.
     */
    public abstract Boolean isCleared();

    @Override
    public int compareTo(final MoneyWiseQIFEventRecord<?> pThat) {
        return getDate().compareTo(pThat.getDate());
    }
}
