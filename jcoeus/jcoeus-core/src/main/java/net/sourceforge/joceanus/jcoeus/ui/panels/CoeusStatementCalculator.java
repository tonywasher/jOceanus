/*******************************************************************************
 * jCoeus: Peer2Peer Analysis
 * Copyright 2012,2017 Tony Washer
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

import java.util.Iterator;
import java.util.function.Predicate;

import net.sourceforge.joceanus.jcoeus.data.CoeusTotalSet;
import net.sourceforge.joceanus.jcoeus.data.CoeusTotals;
import net.sourceforge.joceanus.jcoeus.data.CoeusTotalsField;
import net.sourceforge.joceanus.jmetis.atlas.ui.MetisTableCalculator;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem.MetisFieldDef;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmetis.list.MetisListIndexed;

/**
 * Statement calculator.
 */
public class CoeusStatementCalculator
        implements MetisTableCalculator<CoeusTotals> {
    /**
     * The FieldSet.
     */
    private static final MetisFieldSet<CoeusTotals> FIELD_SET = CoeusTotals.getTheFieldSet();

    /**
     * The totals.
     */
    private final MetisListIndexed<CoeusTotals> theTotals;

    /**
     * The current filter.
     */
    private Predicate<CoeusTotals> theFilter;

    /**
     * Constructor.
     * @param pTotals the totals
     */
    public CoeusStatementCalculator(final MetisListIndexed<CoeusTotals> pTotals) {
        theTotals = pTotals;
    }

    /**
     * Set the TotalSet.
     * @param pTotalSet the totalSet
     */
    protected void setTotalSet(final CoeusTotalSet pTotalSet) {
        /* Determine the field of interest */
        final MetisFieldDef myField = FIELD_SET.getField(pTotalSet.getBalanceField());

        /* Create new filter */
        theFilter = p -> p.getDelta() != null;

        /* Loop through all the fields */
        final Iterator<CoeusTotals> myIterator = theTotals.iterator();
        while (myIterator.hasNext()) {
            final CoeusTotals myTotals = myIterator.next();

            /* Calculate fields */
            myTotals.calculateFields(myField);
        }
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
                                 final MetisFieldDef pField) {
        switch ((CoeusTotalsField) pField.getFieldId()) {
            case DELTA:
                return pTotals.getDelta();
            case BALANCE:
                return pTotals.getBalance();
            default:
                return null;
        }
    }
}
