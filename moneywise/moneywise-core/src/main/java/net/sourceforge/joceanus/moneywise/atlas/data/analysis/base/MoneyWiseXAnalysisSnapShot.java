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
package net.sourceforge.joceanus.moneywise.atlas.data.analysis.base;

import net.sourceforge.joceanus.jmetis.field.MetisFieldItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicResource;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataResource;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;

/**
 * History snapShot for a bucket.
 * @param <T> the values
 * @param <E> the enum class
 */
public class MoneyWiseXAnalysisSnapShot<T extends MoneyWiseXAnalysisValues<T, E>, E extends Enum<E> & MoneyWiseXAnalysisAttribute>
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
        FIELD_DEFS.declareLocalField(PrometheusDataResource.DATAITEM_ID, MoneyWiseXAnalysisSnapShot::getId);
        FIELD_DEFS.declareLocalField(MoneyWiseXAnalysisBaseResource.EVENT, MoneyWiseXAnalysisSnapShot::getEvent);
        FIELD_DEFS.declareLocalField(MoneyWiseBasicResource.MONEYWISEDATA_FIELD_DATE, MoneyWiseXAnalysisSnapShot::getDate);
        FIELD_DEFS.declareLocalField(MoneyWiseXAnalysisBaseResource.SNAPSHOT_VALUES, MoneyWiseXAnalysisSnapShot::getSnapShot);
        FIELD_DEFS.declareLocalField(MoneyWiseXAnalysisBaseResource.SNAPSHOT_PREV, MoneyWiseXAnalysisSnapShot::getPrevious);
    }

    /**
     * The id of the event.
     */
    private final Integer theId;

    /**
     * The event.
     */
    private final MoneyWiseXAnalysisEvent theEvent;

    /**
     * The date of the event.
     */
    private final TethysDate theDate;

    /**
     * SnapShot Values.
     */
    private final T theSnapShot;

    /**
     * Previous SnapShot Values.
     */
    private final T thePrevious;

    /**
     * Constructor.
     * @param pEvent the event
     * @param pValues the values
     * @param pPrevious the previous snapShot
     */
    protected MoneyWiseXAnalysisSnapShot(final MoneyWiseXAnalysisEvent pEvent,
                                         final T pValues,
                                         final T pPrevious) {
        /* Store transaction details */
        theId = pEvent.getIndexedId();
        theEvent = pEvent;
        theDate = pEvent.getDate();

        /* Store the snapshot map */
        theSnapShot = pValues.newSnapShot();
        thePrevious = pPrevious;
    }

    /**
     * Constructor.
     * @param pSnapShot the snapShot
     * @param pBaseValues the base values
     * @param pPrevious the previous snapShot
     */
    protected MoneyWiseXAnalysisSnapShot(final MoneyWiseXAnalysisSnapShot<T, E> pSnapShot,
                                         final T pBaseValues,
                                         final T pPrevious) {
        /* Store event details */
        theId = pSnapShot.getId();
        theEvent = pSnapShot.getEvent();
        theDate = pSnapShot.getDate();

        /* Store the snapshot map */
        theSnapShot = pSnapShot.newSnapShot();
        theSnapShot.adjustToBaseValues(pBaseValues);
        thePrevious = pPrevious;
    }

    @Override
    public String formatObject(final TethysUIDataFormatter pFormatter) {
        return theDate.toString();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public MetisFieldSet<MoneyWiseXAnalysisSnapShot> getDataFieldSet() {
        return FIELD_DEFS;
    }

    /**
     * Obtain id.
     * @return the id
     */
    protected Integer getId() {
        return theId;
    }

    /**
     * Obtain event.
     * @return the event
     */
    protected MoneyWiseXAnalysisEvent getEvent() {
        return theEvent;
    }

    /**
     * Obtain date.
     * @return the date
     */
    protected TethysDate getDate() {
        return theDate;
    }

    /**
     * Obtain snapShot.
     * @return the snapShot
     */
    public T getSnapShot() {
        return theSnapShot;
    }

    /**
     * Obtain previous SnapShot.
     * @return the previous snapShot
     */
    public T getPrevious() {
        return thePrevious;
    }

    /**
     * Obtain new snapShot.
     * @return the snapShot
     */
    protected T newSnapShot() {
        return theSnapShot.newSnapShot();
    }

    /**
     * Obtain delta snapShot.
     * @param pAttr the attribute
     * @return the delta snapShot
     */
    protected TethysDecimal getDeltaValue(final E pAttr) {
        /* return the delta value */
        return theSnapShot.getDeltaValue(thePrevious, pAttr);
    }

    /**
     * Obtain delta snapShot.
     * @param pAttr the attribute
     * @return the delta snapShot
     */
    protected TethysMoney getDeltaMoneyValue(final E pAttr) {
        /* return the delta value */
        return theSnapShot.getDeltaMoneyValue(thePrevious, pAttr);
    }

    /**
     * Obtain delta snapShot.
     * @param pAttr the attribute
     * @return the delta snapShot
     */
    protected TethysUnits getDeltaUnitsValue(final E pAttr) {
        /* return the delta value */
        return theSnapShot.getDeltaUnitsValue(thePrevious, pAttr);
    }
}
