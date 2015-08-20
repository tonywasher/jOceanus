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

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.joceanus.jmetis.data.JDataFields;
import net.sourceforge.joceanus.jmetis.data.JDataFields.JDataField;

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
    protected static final JDataFields FIELD_DEFS = new JDataFields(PrimaryTransaction.class.getSimpleName(), TransactionBase.FIELD_DEFS);

    /**
     * Do we have secondary transactions Field Id.
     */
    private static final JDataField FIELD_HASSUBTRANS = FIELD_DEFS.declareDerivedValueField("hasSubTransactions");

    /**
     * Secondary Transaction Field Id.
     */
    private static final JDataField FIELD_SUBTRANS = FIELD_DEFS.declareLocalField("SubTransactions");

    /**
     * List of secondary transactions.
     */
    private final List<S> theSubTransactions = new ArrayList<S>();

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
     * Create secondary transaction
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
     * Create secondary transaction
     * @param pPrimary transaction
     * @return the secondary transaction
     */
    protected abstract S newSubTransaction();
}
