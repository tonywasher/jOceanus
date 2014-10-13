/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2014 Tony Washer
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

import net.sourceforge.joceanus.jmetis.viewer.JDataFieldValue;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.viewer.JDataObject.JDataContents;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseDataResource;
import net.sourceforge.joceanus.jmoneywise.data.Transaction;
import net.sourceforge.joceanus.jprometheus.data.DataItem;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;
import net.sourceforge.joceanus.jtethys.decimal.JDecimal;
import net.sourceforge.joceanus.jtethys.decimal.JMoney;
import net.sourceforge.joceanus.jtethys.decimal.JUnits;

/**
 * History snapShot for a bucket.
 * @param <T> the values
 * @param <E> the enum class
 */
public class BucketSnapShot<T extends BucketValues<T, E>, E extends Enum<E> & BucketAttribute>
        implements JDataContents {
    /**
     * Local Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(AnalysisResource.BUCKET_SNAPSHOT.getValue());

    /**
     * Id Id.
     */
    private static final JDataField FIELD_ID = FIELD_DEFS.declareEqualityField(DataItem.FIELD_ID.getName());

    /**
     * Transaction Id.
     */
    private static final JDataField FIELD_TRANS = FIELD_DEFS.declareEqualityField(MoneyWiseDataType.TRANSACTION.getItemName());

    /**
     * Date Id.
     */
    private static final JDataField FIELD_DATE = FIELD_DEFS.declareEqualityField(MoneyWiseDataResource.MONEYWISEDATA_FIELD_DATE.getValue());

    /**
     * Values Id.
     */
    private static final JDataField FIELD_VALUES = FIELD_DEFS.declareEqualityField(AnalysisResource.BUCKET_VALUES.getValue());

    /**
     * Previous Values Id.
     */
    private static final JDataField FIELD_PREVIOUS = FIELD_DEFS.declareEqualityField(AnalysisResource.BUCKET_PREVIOUS.getValue());

    @Override
    public String formatObject() {
        return theDate.toString();
    }

    @Override
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final JDataField pField) {
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
        return JDataFieldValue.UNKNOWN;
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
    private final JDateDay theDate;

    /**
     * SnapShot Values.
     */
    private final T theSnapShot;

    /**
     * Previous SnapShot Values.
     */
    private final T thePrevious;

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
    protected JDateDay getDate() {
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
    protected T getNewSnapShot() {
        return theSnapShot.getSnapShot();
    }

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
        theSnapShot = pValues.getSnapShot();
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
        theSnapShot = pSnapShot.getNewSnapShot();
        theSnapShot.adjustToBaseValues(pBaseValues);
        thePrevious = pPrevious;
    }

    /**
     * Obtain delta snapShot.
     * @param pAttr the attribute
     * @return the delta snapShot
     */
    protected JDecimal getDeltaValue(final E pAttr) {
        /* return the delta value */
        return theSnapShot.getDeltaValue(thePrevious, pAttr);
    }

    /**
     * Obtain delta snapShot.
     * @param pAttr the attribute
     * @return the delta snapShot
     */
    protected JMoney getDeltaMoneyValue(final E pAttr) {
        /* return the delta value */
        return theSnapShot.getDeltaMoneyValue(thePrevious, pAttr);
    }

    /**
     * Obtain delta snapShot.
     * @param pAttr the attribute
     * @return the delta snapShot
     */
    protected JUnits getDeltaUnitsValue(final E pAttr) {
        /* return the delta value */
        return theSnapShot.getDeltaUnitsValue(thePrevious, pAttr);
    }
}
