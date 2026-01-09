/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012-2026 Tony Washer
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

import net.sourceforge.joceanus.metis.field.MetisFieldItem;
import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicResource;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransaction;
import net.sourceforge.joceanus.oceanus.date.OceanusDate;
import net.sourceforge.joceanus.oceanus.decimal.OceanusDecimal;
import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;
import net.sourceforge.joceanus.oceanus.decimal.OceanusUnits;
import net.sourceforge.joceanus.oceanus.format.OceanusDataFormatter;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataResource;

/**
 * History snapShot for a bucket.
 * @param <T> the values
 * @param <E> the enum class
 */
public class MoneyWiseAnalysisSnapShot<T extends MoneyWiseAnalysisValues<T, E>, E extends Enum<E> & MoneyWiseAnalysisAttribute>
        implements MetisFieldItem {
    /**
     * Local Report fields.
     */
    @SuppressWarnings("rawtypes")
    private static final MetisFieldSet<MoneyWiseAnalysisSnapShot> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseAnalysisSnapShot.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(PrometheusDataResource.DATAITEM_ID, MoneyWiseAnalysisSnapShot::getId);
        FIELD_DEFS.declareLocalField(MoneyWiseBasicDataType.TRANSACTION, MoneyWiseAnalysisSnapShot::getTransaction);
        FIELD_DEFS.declareLocalField(MoneyWiseBasicResource.MONEYWISEDATA_FIELD_DATE, MoneyWiseAnalysisSnapShot::getDate);
        FIELD_DEFS.declareLocalField(MoneyWiseAnalysisBaseResource.SNAPSHOT_VALUES, MoneyWiseAnalysisSnapShot::getSnapShot);
        FIELD_DEFS.declareLocalField(MoneyWiseAnalysisBaseResource.SNAPSHOT_PREV, MoneyWiseAnalysisSnapShot::getPrevious);
    }

    /**
     * The id of the transaction.
     */
    private final Integer theId;

    /**
     * The transaction.
     */
    private final MoneyWiseTransaction theTransaction;

    /**
     * The date.
     */
    private final OceanusDate theDate;

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
     * @param pTrans the transaction
     * @param pValues the values
     * @param pPrevious the previous snapShot
     */
    protected MoneyWiseAnalysisSnapShot(final MoneyWiseTransaction pTrans,
                                        final T pValues,
                                        final T pPrevious) {
        /* Store transaction details */
        theId = pTrans.getIndexedId();
        theTransaction = pTrans;
        theDate = pTrans.getDate();

        /* Store the snapshot map */
        theSnapShot = pValues.getCounterSnapShot();
        thePrevious = pPrevious;
    }

    /**
     * Constructor.
     * @param pSnapShot the snapShot
     * @param pBaseValues the base values
     * @param pPrevious the previous snapShot
     */
    protected MoneyWiseAnalysisSnapShot(final MoneyWiseAnalysisSnapShot<T, E> pSnapShot,
                                        final T pBaseValues,
                                        final T pPrevious) {
        /* Store event details */
        theId = pSnapShot.getId();
        theTransaction = pSnapShot.getTransaction();
        theDate = pSnapShot.getDate();

        /* Store the snapshot map */
        theSnapShot = pSnapShot.getFullSnapShot();
        theSnapShot.adjustToBaseValues(pBaseValues);
        thePrevious = pPrevious;
    }

    @Override
    public String formatObject(final OceanusDataFormatter pFormatter) {
        return theDate.toString();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public MetisFieldSet<MoneyWiseAnalysisSnapShot> getDataFieldSet() {
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
     * Obtain transaction.
     * @return the transaction
     */
    protected MoneyWiseTransaction getTransaction() {
        return theTransaction;
    }

    /**
     * Obtain date.
     * @return the date
     */
    protected OceanusDate getDate() {
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
     * Obtain counter snapShot.
     * @return the snapShot
     */
    protected T getCounterSnapShot() {
        return theSnapShot.getCounterSnapShot();
    }

    /**
     * Obtain full snapShot.
     * @return the snapShot
     */
    protected T getFullSnapShot() {
        return theSnapShot.getFullSnapShot();
    }

    /**
     * Obtain delta snapShot.
     * @param pAttr the attribute
     * @return the delta snapShot
     */
    protected OceanusDecimal getDeltaValue(final E pAttr) {
        /* return the delta value */
        return theSnapShot.getDeltaValue(thePrevious, pAttr);
    }

    /**
     * Obtain delta snapShot.
     * @param pAttr the attribute
     * @return the delta snapShot
     */
    protected OceanusMoney getDeltaMoneyValue(final E pAttr) {
        /* return the delta value */
        return theSnapShot.getDeltaMoneyValue(thePrevious, pAttr);
    }

    /**
     * Obtain delta snapShot.
     * @param pAttr the attribute
     * @return the delta snapShot
     */
    protected OceanusUnits getDeltaUnitsValue(final E pAttr) {
        /* return the delta value */
        return theSnapShot.getDeltaUnitsValue(thePrevious, pAttr);
    }
}
