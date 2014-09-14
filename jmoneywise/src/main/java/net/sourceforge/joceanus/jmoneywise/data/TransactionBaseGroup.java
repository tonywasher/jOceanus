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

import net.sourceforge.joceanus.jmetis.viewer.Difference;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jprometheus.data.DataErrorList;
import net.sourceforge.joceanus.jprometheus.data.DataGroup;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;
import net.sourceforge.joceanus.jtethys.decimal.JMoney;

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
     * Bad Owner Error.
     */
    private static final String ERROR_OWNER = MoneyWiseDataResource.TRANSACTION_ERROR_BADOWNER.getValue();

    /**
     * Local Report fields.
     */
    protected static final JDataFields FIELD_DEFS = new JDataFields(TransactionGroup.class.getSimpleName(), DataGroup.FIELD_DEFS);

    @Override
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    /**
     * Active Date.
     */
    private JDateDay theDate;

    /**
     * Active Owner.
     */
    private AssetBase<?> theOwner;

    /**
     * Active Payee.
     */
    private Payee thePayee;

    /**
     * Obtain Owner.
     * @return the owner account if it exists
     */
    public AssetBase<?> getOwner() {
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
     * Constructor.
     * @param pParent the parent.
     * @param pClass the class
     */
    protected TransactionBaseGroup(final T pParent,
                                   final Class<T> pClass) {
        /* Call super-constructor */
        super(pParent, pClass);
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
        AssetBase<?> myDebit = myParent.getDebit();
        AssetBase<?> myCredit = myParent.getCredit();

        /* Handle if we are dealing with a payee */
        if (myDebit instanceof Payee) {
            /* Store payee and owner */
            theOwner = myCredit;
            thePayee = (Payee) myDebit;
        } else if (myCredit instanceof Payee) {
            /* Store payee and owner */
            theOwner = myDebit;
            thePayee = (Payee) myCredit;
        } else if (myDebit.equals(myCredit)) {
            theOwner = myDebit;
        }
    }

    /**
     * Validate the item.
     * @param pTrans the child transaction
     */
    protected void validateChild(final T pTrans) {
        /* Access details */
        JDateDay myDate = pTrans.getDate();
        AssetBase<?> myDebit = pTrans.getDebit();
        AssetBase<?> myCredit = pTrans.getCredit();

        /* If the date differs */
        if (!myDate.equals(theDate)) {
            pTrans.addError(ERROR_DATE, Transaction.FIELD_DATE);
        }

        /* If we have a debit Payee */
        if (myDebit instanceof Payee) {
            /* If this is the first payee */
            if (thePayee == null) {
                /* Store it */
                thePayee = (Payee) myDebit;

                /* else check that it is the same payee */
            } else if (!thePayee.equals(myDebit)) {
                /* We do allow a cashRecovery from alternate payee */
                if (!(myCredit instanceof Cash)) {
                    pTrans.addError(ERROR_PAYEE, Transaction.FIELD_DEBIT);
                }
            }

            /* If we have no owner */
            if (theOwner == null) {
                /* Must match one of parents elements */
                T myParent = getParent();
                if (myCredit.equals(myParent.getDebit())
                    || myCredit.equals(myParent.getCredit())) {
                    /* Store the owner */
                    theOwner = myCredit;
                } else {
                    pTrans.addError(ERROR_OWNER, Transaction.FIELD_CREDIT);
                }

                /* else check that it is the same owner */
            } else if (!theOwner.equals(myCredit)) {
                /* We do allow a cashPayment to alternate payee */
                if (!(myCredit instanceof Cash)) {
                    pTrans.addError(ERROR_OWNER, Transaction.FIELD_CREDIT);
                }
            }

            /* If we have a credit Payee */
        } else if (myCredit instanceof Payee) {
            /* If this is the first payee */
            if (thePayee == null) {
                /* Store it */
                thePayee = (Payee) myCredit;

                /* else check that it is the same payee */
            } else if (!thePayee.equals(myCredit)) {
                /* We do allow a cashPayment to alternate payee */
                if (!(myDebit instanceof Cash)) {
                    pTrans.addError(ERROR_PAYEE, Transaction.FIELD_CREDIT);
                }
            }

            /* If we have no owner */
            if (theOwner == null) {
                /* Must match one of parents elements */
                T myParent = getParent();
                if (myDebit.equals(myParent.getDebit())
                    || myDebit.equals(myParent.getCredit())) {
                    /* Store the owner */
                    theOwner = myDebit;
                } else {
                    pTrans.addError(ERROR_OWNER, Transaction.FIELD_DEBIT);
                }

                /* else check that it is the same owner */
            } else if (!theOwner.equals(myDebit)) {
                /* We do allow a cashPayment to alternate payee */
                if (!(myDebit instanceof Cash)) {
                    pTrans.addError(ERROR_OWNER, Transaction.FIELD_DEBIT);
                }
            }

            /* else this is a transfer. If we have no current owner */
        } else if (theOwner == null) {
            /* Must match one of parents elements */
            T myParent = getParent();
            if (myDebit.equals(myParent.getDebit())
                || myDebit.equals(myParent.getCredit())) {
                /* Store the owner */
                theOwner = myDebit;
            } else if (myCredit.equals(myParent.getDebit())
                       || myCredit.equals(myParent.getCredit())) {
                /* Store the owner */
                theOwner = myCredit;

                /* must match one of the initial elements */
            } else {
                pTrans.addError(ERROR_OWNER, Transaction.FIELD_DEBIT);
                pTrans.addError(ERROR_OWNER, Transaction.FIELD_CREDIT);
            }

            /* Credit or debit must match the owner */
        } else if (!myDebit.equals(theOwner)
                   && !myCredit.equals(theOwner)) {
            pTrans.addError(ERROR_OWNER, Transaction.FIELD_DEBIT);
            pTrans.addError(ERROR_OWNER, Transaction.FIELD_CREDIT);
        }
    }

    /**
     * Get display partner for asset.
     * @param pAsset the asset
     * @return the display text for partner
     */
    public String getPartner(final AssetBase<?> pAsset) {
        /* If the asset is not the owner */
        if (!theOwner.equals(pAsset)) {
            /* Partner is always the owner */
            return theOwner.getName();
        }

        /* If we have a Payee */
        if (thePayee != null) {
            /* Partner is the payee */
            return thePayee.getName();
        }

        /* Access iterator and loop through transactions */
        String myPartner = null;
        Iterator<T> myIterator = iterator();
        while (myIterator.hasNext()) {
            T myTrans = myIterator.next();

            /* Ignore deleted children */
            if (myTrans.isDeleted()) {
                continue;
            }

            /* If the transaction relates to the asset */
            if (myTrans.relatesTo(pAsset)) {
                /* Access partner name */
                String myName = (pAsset.equals(myTrans.getDebit()))
                                                                   ? myTrans.getCreditName()
                                                                   : myTrans.getDebitName();

                /* Determine differences in partners */
                if (myPartner == null) {
                    myPartner = myName;
                } else if (!Difference.isEqual(myName, myPartner)) {
                    return NAME_SPLIT;
                }
            }
        }

        /* Return the standard partner name */
        return myPartner;
    }

    /**
     * Get display category for asset.
     * @param pAsset the asset
     * @return the display text for category
     */
    public String getCategory(final AssetBase<?> pAsset) {
        /* Initialise category name */
        String myCategory = null;

        /* Access iterator and loop through transactions */
        Iterator<T> myIterator = iterator();
        while (myIterator.hasNext()) {
            T myTrans = myIterator.next();

            /* Ignore deleted children */
            if (myTrans.isDeleted()) {
                continue;
            }

            /* If the transaction relates to the asset */
            if (myTrans.relatesTo(pAsset)) {
                /* Access category name */
                String myName = myTrans.getCategoryName();

                /* Determine differences in partners */
                if (myCategory == null) {
                    myCategory = myName;
                } else if (!Difference.isEqual(myName, myCategory)) {
                    return NAME_SPLIT;
                }
            }
        }

        /* Return the standard category name */
        return myCategory;
    }

    /**
     * Get display amount for asset.
     * @param pAsset the asset
     * @return the display amount
     */
    public JMoney getAmount(final AssetBase<?> pAsset) {
        /* Initialise category name */
        JMoney myAmount = new JMoney();

        /* Access iterator and loop through transactions */
        Iterator<T> myIterator = iterator();
        while (myIterator.hasNext()) {
            T myTrans = myIterator.next();

            /* Ignore deleted children */
            if (myTrans.isDeleted()) {
                continue;
            }

            /* If the transaction relates to the asset */
            if (myTrans.relatesTo(pAsset)) {
                /* Access amount */
                JMoney myValue = myTrans.getAmount();

                /* Handle deltas to the asset */
                if (pAsset.equals(myTrans.getCredit())) {
                    myAmount.addAmount(myValue);
                } else if ((!myTrans.isDividend()) && (!myTrans.isInterest())) {
                    myAmount.subtractAmount(myValue);
                }
            }
        }

        /* Return the amount */
        return myAmount;
    }
}
