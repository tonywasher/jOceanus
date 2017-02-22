/*******************************************************************************
 * jCoeus: Peer2Peer Analysis
 * Copyright 2012,2016 Tony Washer
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
package net.sourceforge.joceanus.jcoeus.ui.panels;

import java.util.function.Predicate;

import net.sourceforge.joceanus.jcoeus.data.CoeusTotalSet;
import net.sourceforge.joceanus.jcoeus.data.CoeusTotals;
import net.sourceforge.joceanus.jmetis.data.MetisFieldValue;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.ui.MetisTableCalculator;

/**
 * Statement calculator.
 */
public class CoeusStatementCalculator
        implements MetisTableCalculator<CoeusTotals> {
    /**
     * The current totalSet.
     */
    private CoeusTotalSet theTotalSet;

    /**
     * The current underlying field.
     */
    private MetisField theField;

    /**
     * The current filter.
     */
    private Predicate<CoeusTotals> theFilter;

    /**
     * Set the TotalSet.
     * @param pTotalSet the totalSet
     */
    protected void setTotalSet(final CoeusTotalSet pTotalSet) {
        theTotalSet = pTotalSet;
        theField = theTotalSet.getBalanceField();
        theFilter = p -> calculateValue(p, CoeusTotals.FIELD_DELTA) != null;
    }

    /**
     * Obtain the filter.
     * @return the filter
     */
    protected Predicate<CoeusTotals> getFilter() {
        return theFilter;
    }

    @Override
    public Object calculateValue(final CoeusTotals pTotals,
                                 final MetisField pField) {
        if (CoeusTotals.FIELD_DELTA.equals(pField)) {
            return pTotals.getDeltaForField(theField);
        } else if (CoeusTotals.FIELD_BALANCE.equals(pField)) {
            Object myValue = pTotals.getFieldValue(theField);
            return MetisFieldValue.SKIP.equals(myValue)
                                                        ? null
                                                        : myValue;
        }
        return null;
    }
}