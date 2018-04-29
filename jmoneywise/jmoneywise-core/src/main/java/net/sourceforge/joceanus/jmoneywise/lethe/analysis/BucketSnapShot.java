/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2018 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.lethe.analysis;

import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseDataResource;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Transaction;
import net.sourceforge.joceanus.jprometheus.lethe.data.PrometheusDataResource;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;

/**
 * History snapShot for a bucket.
 * @param <T> the values
 * @param <E> the enum class
 */
public class BucketSnapShot<T extends BucketValues<T, E>, E extends Enum<E> & BucketAttribute>
        implements MetisFieldItem {
    /**
     * Local Report fields.
     */
    @SuppressWarnings("rawtypes")
    private static final MetisFieldSet<BucketSnapShot> FIELD_DEFS = MetisFieldSet.newFieldSet(BucketSnapShot.class);

    /**
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(PrometheusDataResource.DATAITEM_ID, BucketSnapShot::getId);
        FIELD_DEFS.declareLocalField(MoneyWiseDataType.TRANSACTION, BucketSnapShot::getTransaction);
        FIELD_DEFS.declareLocalField(MoneyWiseDataResource.MONEYWISEDATA_FIELD_DATE, BucketSnapShot::getDate);
        FIELD_DEFS.declareLocalField(AnalysisResource.BUCKET_VALUES, BucketSnapShot::getSnapShot);
        FIELD_DEFS.declareLocalField(AnalysisResource.BUCKET_PREVIOUS, BucketSnapShot::getPrevious);
    }

    /**
     * The id of the transaction.
     */
    private final Integer theId;

    /**
     * The transaction.
     */
    private final Transaction theTransaction;

    /**
     * The date.
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
     * @param pTrans the transaction
     * @param pValues the values
     * @param pPrevious the previous snapShot
     */
    protected BucketSnapShot(final Transaction pTrans,
                             final T pValues,
                             final T pPrevious) {
        /* Store transaction details */
        theId = pTrans.getId();
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
    protected BucketSnapShot(final BucketSnapShot<T, E> pSnapShot,
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
    public String formatObject(final MetisDataFormatter pFormatter) {
        return theDate.toString();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public MetisFieldSet<BucketSnapShot> getDataFieldSet() {
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
    protected Transaction getTransaction() {
        return theTransaction;
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
