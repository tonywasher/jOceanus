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
 * $URL: http://localhost/svn/Finance/jOceanus/trunk/jmoneywise/jmoneywise-core/src/main/java/net/sourceforge/joceanus/jmoneywise/data/Cash.java $
 * $Revision: 607 $
 * $Author: Tony $
 * $Date: 2015-05-07 06:50:36 +0100 (Thu, 07 May 2015) $
 ******************************************************************************/
package net.sourceforge.joceanus.jmoneywise.data;

import net.sourceforge.joceanus.jmetis.data.JDataFields;
import net.sourceforge.joceanus.jmetis.data.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.data.ValueSet;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jprometheus.data.DataItem;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Secondary Transaction data type.
 * @author Tony Washer
 * @param <S> the secondary transaction data type
 * @param <O> the primary transaction data type
 */
public abstract class SecondaryTransaction<S extends SecondaryTransaction<S, O>, O extends PrimaryTransaction<O, S>>
        extends TransactionBase<S> {
    /**
     * Local Report fields.
     */
    protected static final JDataFields FIELD_DEFS = new JDataFields(SecondaryTransaction.class.getSimpleName(), TransactionBase.FIELD_DEFS);

    /**
     * Owner Field Id.
     */
    public static final JDataField FIELD_OWNER = FIELD_DEFS.declareEqualityValueField(MoneyWiseDataResource.TRANSACTION_ASSETPAIR.getValue());

    /**
     * Copy Constructor.
     * @param pList the event list
     * @param pTrans The Transaction to copy
     */
    protected SecondaryTransaction(final TransactionBaseList<S> pList,
                                   final S pTrans) {
        /* Set standard values */
        super(pList, pTrans);
    }

    /**
     * Values constructor.
     * @param pList the List to add to
     * @param pValues the values constructor
     * @throws OceanusException on error
     */
    protected SecondaryTransaction(final TransactionBaseList<S> pList,
                                   final DataValues<MoneyWiseDataType> pValues) throws OceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Store the Owner */
        Object myValue = pValues.getValue(FIELD_OWNER);
        if (myValue instanceof Integer) {
            setValueOwner((Integer) myValue);
        } else if (myValue instanceof PrimaryTransaction) {
            setValueOwner((PrimaryTransaction<?, ?>) myValue);
        }
    }

    /**
     * Edit Constructor.
     * @param pList the event list
     * @param pOwner The PrimaryTransaction
     */
    protected SecondaryTransaction(final TransactionBaseList<S> pList,
                                   final O pOwner) {
        /* Set standard values */
        super(pList);

        /* Set the owner */
        setValueOwner(pOwner);
    }

    /**
     * Obtain Owner.
     * @return the Owner
     */
    public abstract O getOwner();

    /**
     * Obtain OwnerId.
     * @return the OwnerId
     */
    public Integer getOwnerId() {
        return getOwner(getValueSet(), DataItem.class).getId();
    }

    /**
     * Obtain Owner.
     * @param <X> the owner type
     * @param pValueSet the valueSet
     * @param pClass the class of the owner
     * @return the Owner
     */
    protected static <X> X getOwner(final ValueSet pValueSet,
                                    final Class<X> pClass) {
        return pValueSet.getValue(FIELD_OWNER, pClass);
    }

    /**
     * Set the owner.
     * @param pValue the owner
     */
    private void setValueOwner(final PrimaryTransaction<?, ?> pValue) {
        getValueSet().setValue(FIELD_OWNER, pValue);
    }

    /**
     * Set the owner id.
     * @param pId the owner id
     */
    private void setValueOwner(final Integer pId) {
        getValueSet().setValue(FIELD_OWNER, pId);
    }

    @Override
    public void validate() {
        O myOwner = getOwner();

        /* Pass call on */
        super.validate();

        /* Owner must be non-null */
        if (myOwner == null) {
            addError(ERROR_MISSING, FIELD_OWNER);
        }
    }
}
