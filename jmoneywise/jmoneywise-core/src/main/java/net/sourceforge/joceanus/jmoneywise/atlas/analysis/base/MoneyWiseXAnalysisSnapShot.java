/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2023 Tony Washer
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

import net.sourceforge.joceanus.jmetis.field.MetisFieldItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jtethys.date.TethysDate;

/**
 * SnapShot.
 * @param <E> the enum class
 */
public class MoneyWiseXAnalysisSnapShot<E extends Enum<E> & MoneyWiseXAnalysisAttribute>
        implements MetisFieldItem {
    /**
     * Local Report fields.
     */
    @SuppressWarnings("rawtypes")
    private static final MetisFieldSet<MoneyWiseXAnalysisSnapShot> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseXAnalysisSnapShot.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MoneyWiseXAnalysisBaseResource.SNAPSHOT_EVENT, MoneyWiseXAnalysisSnapShot::getEvent);
        FIELD_DEFS.declareLocalField(MoneyWiseXAnalysisBaseResource.SNAPSHOT_VALUES, MoneyWiseXAnalysisSnapShot::getValues);
        FIELD_DEFS.declareLocalField(MoneyWiseXAnalysisBaseResource.SNAPSHOT_PREV, MoneyWiseXAnalysisSnapShot::getPrevious);
    }

    /**
     * The event related to this snapShot.
     */
    private final MoneyWiseXAnalysisEvent theEvent;

    /**
     * The current bucket values.
     */
    private final MoneyWiseXAnalysisValues<E> theValues;

    /**
     * The previous values.
     */
    private final MoneyWiseXAnalysisValues<E> thePrevious;

    /**
     * Constructor.
     * @param pEvent the event
     * @param pPrevious the previous values
     */
    MoneyWiseXAnalysisSnapShot(final MoneyWiseXAnalysisEvent pEvent,
                               final MoneyWiseXAnalysisValues<E> pPrevious) {
        theEvent = pEvent;
        thePrevious = pPrevious;
        theValues = new MoneyWiseXAnalysisValues<>(thePrevious);
    }

    /**
     * Obtain the event.
     * @return the event
     */
    public MoneyWiseXAnalysisEvent getEvent() {
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
    public MoneyWiseXAnalysisValues<E> getValues() {
        return theValues;
    }

    /**
     * Obtain the previous values.
     * @return the values
     */
    public MoneyWiseXAnalysisValues<E> getPrevious() {
        return thePrevious;
    }

    @Override
    public MetisFieldSetDef getDataFieldSet() {
        return FIELD_DEFS;
    }

    /**
     * Flatten values.
     */
    protected void flattenValues() {
        theValues.flattenValues(thePrevious);
    }
}
