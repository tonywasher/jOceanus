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

import java.util.function.Predicate;

import net.sourceforge.joceanus.jcoeus.data.CoeusTotalSet;
import net.sourceforge.joceanus.jcoeus.data.CoeusTotals;
import net.sourceforge.joceanus.jcoeus.data.CoeusTotalsField;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldValue;
import net.sourceforge.joceanus.jmetis.atlas.ui.MetisTableCalculator;
import net.sourceforge.joceanus.jmetis.eos.data.MetisDataEosFieldItem.MetisDataEosFieldDef;
import net.sourceforge.joceanus.jmetis.eos.data.MetisDataEosFieldSet;

/**
 * Statement calculator.
 */
public class CoeusStatementCalculator
        implements MetisTableCalculator<CoeusTotals> {
    /**
     * The FieldSet.
     */
    private static final MetisDataEosFieldSet<CoeusTotals> FIELD_SET = CoeusTotals.getTheFieldSet();

    /**
     * The delta field.
     */
    private static final MetisDataEosFieldDef FIELD_DELTA = FIELD_SET.getField(CoeusTotalsField.DELTA);

    /**
     * The current underlying field.
     */
    private MetisDataEosFieldDef theField;

    /**
     * The current filter.
     */
    private Predicate<CoeusTotals> theFilter;

    /**
     * Set the TotalSet.
     * @param pTotalSet the totalSet
     */
    protected void setTotalSet(final CoeusTotalSet pTotalSet) {
        theField = FIELD_SET.getField(pTotalSet.getBalanceField());
        theFilter = p -> calculateValue(p, FIELD_DELTA) != null;
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
                                 final MetisDataEosFieldDef pField) {
        if (CoeusTotalsField.DELTA.equals(pField.getFieldId())) {
            return pTotals.getDeltaForField(theField);
        } else if (CoeusTotalsField.BALANCE.equals(pField.getFieldId())) {
            final Object myValue = theField.getFieldValue(pTotals);
            return MetisDataFieldValue.SKIP.equals(myValue)
                                                            ? null
                                                            : myValue;
        }
        return null;
    }
}
