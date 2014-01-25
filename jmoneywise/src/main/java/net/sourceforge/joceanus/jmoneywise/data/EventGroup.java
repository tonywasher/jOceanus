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
import java.util.ResourceBundle;

import net.sourceforge.joceanus.jmetis.viewer.Difference;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields;
import net.sourceforge.joceanus.jprometheus.data.DataGroup;
import net.sourceforge.joceanus.jtethys.decimal.JMoney;

/**
 * Event group type.
 * @param <T> the event type
 * @author Tony Washer
 */
public class EventGroup<T extends EventBase>
        extends DataGroup<T, MoneyWiseDataType> {
    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(EventGroup.class.getName());

    /**
     * Split Indication.
     */
    protected static final String NAME_SPLIT = NLS_BUNDLE.getString("SplitIndication");

    /**
     * Local Report fields.
     */
    protected static final JDataFields FIELD_DEFS = new JDataFields(EventGroup.class.getSimpleName(), DataGroup.FIELD_DEFS);

    @Override
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    /**
     * Constructor.
     * @param pParent the parent.
     * @param pClass the class
     */
    public EventGroup(final T pParent,
                      final Class<T> pClass) {
        /* Call super-constructor */
        super(pParent, pClass);
    }

    /**
     * Does this group relate to the account?
     * @param pAccount the account
     * @return true/false
     */
    public boolean relatesTo(final Account pAccount) {
        /* Loop through the events */
        Iterator<T> myIterator = iterator();
        while (myIterator.hasNext()) {
            T myEvent = myIterator.next();

            /* Ignore deleted children */
            if (myEvent.isDeleted()) {
                continue;
            }

            /* If the child relates to the account, say so */
            if (myEvent.relatesTo(pAccount)) {
                return true;
            }
        }

        /* Does not relate */
        return false;
    }

    /**
     * Get display debit.
     * @return the display text for debit
     */
    public String getDebit() {
        /* Access parent debit */
        Account myDebit = getParent().getDebit();

        /* Access iterator and skip first event */
        Iterator<T> myIterator = iterator();
        myIterator.next();

        /* Loop through events */
        while (myIterator.hasNext()) {
            T myEvent = myIterator.next();

            /* Ignore deleted children */
            if (myEvent.isDeleted()) {
                continue;
            }

            /* Handle different debits */
            if (!myDebit.equals(myEvent.getDebit())) {
                return NAME_SPLIT;
            }
        }

        /* Return the standard debit name */
        return myDebit.getName();
    }

    /**
     * Get display credit.
     * @return the display text for debit
     */
    public String getCredit() {
        /* Access parent credit */
        Account myCredit = getParent().getCredit();

        /* Access iterator and skip first event */
        Iterator<T> myIterator = iterator();
        myIterator.next();

        /* Loop through events */
        while (myIterator.hasNext()) {
            T myEvent = myIterator.next();

            /* Ignore deleted children */
            if (myEvent.isDeleted()) {
                continue;
            }

            /* Handle different credits */
            if (!myCredit.equals(myEvent.getCredit())) {
                return NAME_SPLIT;
            }
        }

        /* Return the standard credit name */
        return myCredit.getName();
    }

    /**
     * Get display category.
     * @return the display text for category
     */
    public String getCategory() {
        /* Access parent category */
        EventCategory myCategory = getParent().getCategory();

        /* Access iterator and skip first event */
        Iterator<T> myIterator = iterator();
        myIterator.next();

        /* Loop through events */
        while (myIterator.hasNext()) {
            T myEvent = myIterator.next();

            /* Ignore deleted children */
            if (myEvent.isDeleted()) {
                continue;
            }

            /* Handle different categories */
            if (!myCategory.equals(myEvent.getCategory())) {
                return NAME_SPLIT;
            }
        }

        /* Return the standard category name */
        return myCategory.getName();
    }

    /**
     * Get display partner for account.
     * @param pAccount the account
     * @return the display text for partner
     */
    public String getPartner(final Account pAccount) {
        /* Initialise partner name */
        String myPartner = null;

        /* Access iterator and loop through events */
        Iterator<T> myIterator = iterator();
        while (myIterator.hasNext()) {
            T myEvent = myIterator.next();

            /* Ignore deleted children */
            if (myEvent.isDeleted()) {
                continue;
            }

            /* If the event relates to the account */
            if (myEvent.relatesTo(pAccount)) {
                /* Access partner name */
                String myName = (pAccount.equals(myEvent.getDebit()))
                                                                     ? myEvent.getCreditName()
                                                                     : myEvent.getDebitName();

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
     * Get display category for account.
     * @param pAccount the account
     * @return the display text for category
     */
    public String getCategory(final Account pAccount) {
        /* Initialise category name */
        String myCategory = null;

        /* Access iterator and loop through events */
        Iterator<T> myIterator = iterator();
        while (myIterator.hasNext()) {
            T myEvent = myIterator.next();

            /* Ignore deleted children */
            if (myEvent.isDeleted()) {
                continue;
            }

            /* If the event relates to the account */
            if (myEvent.relatesTo(pAccount)) {
                /* Access category name */
                String myName = myEvent.getCategoryName();

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
     * Get display amount for account.
     * @param pAccount the account
     * @return the display amount
     */
    public JMoney getAmount(final Account pAccount) {
        /* Initialise category name */
        JMoney myAmount = new JMoney();

        /* Access iterator and loop through events */
        Iterator<T> myIterator = iterator();
        while (myIterator.hasNext()) {
            T myEvent = myIterator.next();

            /* Ignore deleted children */
            if (myEvent.isDeleted()) {
                continue;
            }

            /* If the event relates to the account */
            if (myEvent.relatesTo(pAccount)) {
                /* Access amount */
                JMoney myValue = myEvent.getAmount();

                /* Handle deltas to the account */
                if (pAccount.equals(myEvent.getCredit())) {
                    myAmount.addAmount(myValue);
                } else if ((!myEvent.isDividend()) && (!myEvent.isInterest())) {
                    myAmount.subtractAmount(myValue);
                }
            }
        }

        /* Return the amount */
        return myAmount;
    }
}
