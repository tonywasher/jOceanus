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
package net.sourceforge.joceanus.jmoneywise.data;

import net.sourceforge.joceanus.jmetis.data.MetisDataType;
import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.data.MetisValueSet;
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
    protected static final MetisFields FIELD_DEFS = new MetisFields(SecondaryTransaction.class.getSimpleName(), TransactionBase.FIELD_DEFS);

    /**
     * Owner Field Id.
     */
    public static final MetisField FIELD_OWNER = FIELD_DEFS.declareEqualityValueField(MoneyWiseDataResource.TRANSACTION_ASSETPAIR.getValue(), MetisDataType.INTEGER);

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
    protected static <X> X getOwner(final MetisValueSet pValueSet,
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
