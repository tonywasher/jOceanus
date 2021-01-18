/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2021 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.atlas.analysis.base;

import net.sourceforge.joceanus.jtethys.date.TethysDate;

/**
 * SnapShot.
 * @param <E> the enum class
 */
public class MoneyWiseAnalysisSnapShot<E extends Enum<E> & MoneyWiseAnalysisAttribute> {
    /**
     * The event related to this snapShot.
     */
    private final MoneyWiseAnalysisEvent theEvent;

    /**
     * The current bucket values.
     */
    private final MoneyWiseAnalysisValues<E> theValues;

    /**
     * The previous values.
     */
    private final MoneyWiseAnalysisValues<E> thePrevious;

    /**
     * Constructor.
     * @param pEvent the event
     * @param pPrevious the previous values
     */
    MoneyWiseAnalysisSnapShot(final MoneyWiseAnalysisEvent pEvent,
                              final MoneyWiseAnalysisValues<E> pPrevious) {
        theEvent = pEvent;
        thePrevious = pPrevious;
        theValues = new MoneyWiseAnalysisValues<>(thePrevious);
    }

    /**
     * Obtain the event.
     * @return the event
     */
    public MoneyWiseAnalysisEvent getEvent() {
        return theEvent;
    }

    /**
     * Obtain the id.
     * @return the id
     */
    public Integer getId() {
        return theEvent.getId();
    }

    /**
     * Obtain the date.
     * @return the date
     */
    public TethysDate getDate() {
        return theEvent.getDate();
    }

    /**
     * Obtain the values.
     * @return the values
     */
    public MoneyWiseAnalysisValues<E> getValues() {
        return theValues;
    }

    /**
     * Obtain the previous values.
     * @return the values
     */
    public MoneyWiseAnalysisValues<E> getPrevious() {
        return thePrevious;
    }

    /**
     * Flatten values.
     */
    protected void flattenValues() {
        theValues.flattenValues(thePrevious);
    }
}
