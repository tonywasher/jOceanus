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
package net.sourceforge.joceanus.jmoneywise.data;

import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.viewer.JDataFields;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jprometheus.data.DataErrorList;
import net.sourceforge.joceanus.jprometheus.data.DataGroup;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;

/**
 * Event group type.
 * @param <T> the event type
 * @author Tony Washer
 */
public abstract class TransactionBaseGroup<T extends TransactionBase<T>>
        extends DataGroup<T, MoneyWiseDataType> {
    /**
     * Split Indication.
     */
    protected static final String NAME_SPLIT = MoneyWiseDataResource.TRANSACTION_ID_SPLIT.getValue();

    /**
     * Multiple Dates Error.
     */
    private static final String ERROR_DATE = MoneyWiseDataResource.TRANSACTION_ERROR_MULTDATES.getValue();

    /**
     * Multiple Payees Error.
     */
    private static final String ERROR_PAYEE = MoneyWiseDataResource.TRANSACTION_ERROR_MULTPAYEES.getValue();

    /**
     * Partial Reconcile Error.
     */
    private static final String ERROR_RECONCILE = MoneyWiseDataResource.TRANSACTION_ERROR_PARTIALRECONCILE.getValue();

    /**
     * Bad Owner Error.
     */
    private static final String ERROR_OWNER = MoneyWiseDataResource.TRANSACTION_ERROR_BADOWNER.getValue();

    /**
     * Local Report fields.
     */
    protected static final JDataFields FIELD_DEFS = new JDataFields(TransactionGroup.class.getSimpleName(), DataGroup.FIELD_DEFS);

    /**
     * Active Date.
     */
    private JDateDay theDate;

    /**
     * Active Owner.
     */
    private TransactionAsset theOwner;

    /**
     * Active Reconciled.
     */
    private Boolean isReconciled;

    /**
     * Active Payee.
     */
    private Payee thePayee;

    /**
     * Constructor.
     * @param pParent the parent.
     * @param pClass the class
     */
    protected TransactionBaseGroup(final T pParent,
                                   final Class<T> pClass) {
        /* Call super-constructor */
        super(pParent, pClass);
    }

    @Override
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    /**
     * Obtain Owner.
     * @return the owner account if it exists
     */
    public TransactionAsset getOwner() {
        return theOwner;
    }

    /**
     * Obtain payee.
     * @return the payee if it exists
     */
    public Payee getPayee() {
        return thePayee;
    }

    /**
     * Validate the group.
     * @return the error list (or null if no errors)
     */
    public DataErrorList<T> validate() {
        /* Allocate error list */
        DataErrorList<T> myErrors = null;

        /* Analyse the parent */
        analyseParent();

        /* Loop through the children */
        Iterator<T> myIterator = iterator();
        while (myIterator.hasNext()) {
            T myTrans = myIterator.next();

            /* Ignore deleted children */
            if (myTrans.isDeleted()) {
                continue;
            }

            /* Validate the child */
            validateChild(myTrans);

            /* If the item is in error */
            if (myTrans.hasErrors()) {
                /* If this is the first error */
                if (myErrors == null) {
                    /* Allocate error list */
                    myErrors = new DataErrorList<T>();
                }

                /* Add to the error list */
                myErrors.add(myTrans);
            }
        }

        /* Return the errors */
        return myErrors;
    }

    /**
     * Analyse the parent.
     */
    protected void analyseParent() {
        /* Access parent details */
        T myParent = getParent();
        theDate = myParent.getDate();
        TransactionAsset myAccount = myParent.getAccount();
        TransactionAsset myPartner = myParent.getPartner();

        /* Store owner */
        theOwner = myAccount;
        isReconciled = myParent.isReconciled();

        /* Handle if we are dealing with a payee */
        if (myPartner instanceof Payee) {
            /* Store payee */
            thePayee = (Payee) myPartner;
        }
    }

    /**
     * Validate the item.
     * @param pTrans the child transaction
     */
    protected void validateChild(final T pTrans) {
        /* Access details */
        JDateDay myDate = pTrans.getDate();
        TransactionAsset myAccount = pTrans.getAccount();
        TransactionAsset myPartner = pTrans.getPartner();
        boolean isCash = myAccount instanceof Cash;
        Boolean bIsReconciled = pTrans.isReconciled();

        /* If the date differs */
        if (!myDate.equals(theDate)) {
            pTrans.addError(ERROR_DATE, Transaction.FIELD_DATE);
        }

        /* If the Account differs it must be cash */
        if (!myAccount.equals(theOwner) && !isCash) {
            pTrans.addError(ERROR_OWNER, Transaction.FIELD_ACCOUNT);
        }

        /* Reconciled state must be identical */
        if (!bIsReconciled.equals(isReconciled)) {
            pTrans.addError(ERROR_RECONCILE, Transaction.FIELD_RECONCILED);
        }

        /* If we have a Payee Partner */
        if (myPartner instanceof Payee) {
            /* If this is the first payee */
            if (thePayee == null) {
                /* Store it */
                thePayee = (Payee) myPartner;

                /* else check that it is the same payee (Cash allows alternate payee) */
            } else if (!thePayee.equals(myPartner) && !isCash) {
                pTrans.addError(ERROR_PAYEE, Transaction.FIELD_PARTNER);
            }
        }
    }
}
