/*******************************************************************************
 * jMoneyWise: Finance Application
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
package net.sourceforge.joceanus.jmoneywise.analysis;

import net.sourceforge.joceanus.jmetis.data.MetisDataObject.MetisDataContents;
import net.sourceforge.joceanus.jmetis.data.MetisFieldValue;
import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseDataResource;
import net.sourceforge.joceanus.jmoneywise.data.Transaction;
import net.sourceforge.joceanus.jprometheus.data.DataItem;
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
        implements MetisDataContents {
    /**
     * Local Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(AnalysisResource.BUCKET_SNAPSHOT.getValue());

    /**
     * Id Id.
     */
    private static final MetisField FIELD_ID = FIELD_DEFS.declareEqualityField(DataItem.FIELD_ID.getName());

    /**
     * Transaction Id.
     */
    private static final MetisField FIELD_TRANS = FIELD_DEFS.declareEqualityField(MoneyWiseDataType.TRANSACTION.getItemName());

    /**
     * Date Id.
     */
    private static final MetisField FIELD_DATE = FIELD_DEFS.declareEqualityField(MoneyWiseDataResource.MONEYWISEDATA_FIELD_DATE.getValue());

    /**
     * Values Id.
     */
    private static final MetisField FIELD_VALUES = FIELD_DEFS.declareEqualityField(AnalysisResource.BUCKET_VALUES.getValue());

    /**
     * Previous Values Id.
     */
    private static final MetisField FIELD_PREVIOUS = FIELD_DEFS.declareEqualityField(AnalysisResource.BUCKET_PREVIOUS.getValue());

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
    public String formatObject() {
        return theDate.toString();
    }

    @Override
    public MetisFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final MetisField pField) {
        if (FIELD_ID.equals(pField)) {
            return theId;
        }
        if (FIELD_TRANS.equals(pField)) {
            return theTransaction;
        }
        if (FIELD_DATE.equals(pField)) {
            return theDate;
        }
        if (FIELD_VALUES.equals(pField)) {
            return theSnapShot;
        }
        if (FIELD_PREVIOUS.equals(pField)) {
            return thePrevious;
        }
        return MetisFieldValue.UNKNOWN;
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
