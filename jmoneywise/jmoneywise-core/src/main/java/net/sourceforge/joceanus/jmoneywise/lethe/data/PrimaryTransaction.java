/*******************************************************************************
 * jMoneyWise: Finance Application
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
package net.sourceforge.joceanus.jmoneywise.lethe.data;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisField;

/**
 * Transaction data type.
 * @author Tony Washer
 * @param <T> the primary transaction data type
 * @param <S> the secondary transaction data type
 */
public abstract class PrimaryTransaction<T extends PrimaryTransaction<T, S>, S extends SecondaryTransaction<S, T>>
        extends TransactionBase<T> {
    /**
     * Local Report fields.
     */
    protected static final MetisFields FIELD_DEFS = new MetisFields(PrimaryTransaction.class.getSimpleName(), TransactionBase.FIELD_DEFS);

    /**
     * Do we have secondary transactions Field Id.
     */
    private static final MetisField FIELD_HASSUBTRANS = FIELD_DEFS.declareDerivedValueField("hasSubTransactions");

    /**
     * Secondary Transaction Field Id.
     */
    private static final MetisField FIELD_SUBTRANS = FIELD_DEFS.declareLocalField("SubTransactions");

    /**
     * List of secondary transactions.
     */
    private final List<S> theSubTransactions = new ArrayList<>();

    /**
     * Copy Constructor.
     * @param pList the event list
     * @param pTrans The Transaction to copy
     */
    protected PrimaryTransaction(final TransactionBaseList<T> pList,
                                 final T pTrans) {
        /* Set standard values */
        super(pList, pTrans);
    }

    /**
     * Create secondary transaction.
     * @return the secondary transaction
     */
    protected S addSubTransaction() {
        /* Create the subTransaction */
        S mySubTrans = newSubTransaction();

        /* Add it to the list */
        theSubTransactions.add(mySubTrans);

        /* Note that we have subTransactions */

        /* return the new subTransaction */
        return mySubTrans;
    }

    /**
     * Create secondary transaction.
     * @return the secondary transaction
     */
    protected abstract S newSubTransaction();
}
