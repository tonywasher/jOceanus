/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2013 Tony Washer
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
package net.sourceforge.jOceanus.jMoneyWise.data;

import net.sourceforge.jOceanus.jDataManager.DataState;
import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataManager.JDataFields;
import net.sourceforge.jOceanus.jDataManager.JDataFields.JDataField;
import net.sourceforge.jOceanus.jDataManager.JDataObject.JDataContents;
import net.sourceforge.jOceanus.jDataManager.JDataObject.JDataFieldValue;
import net.sourceforge.jOceanus.jDataModels.data.DataItem;
import net.sourceforge.jOceanus.jDateDay.JDateDay;

/**
 * Status of an account.
 */
public class AccountStatus
        implements JDataContents {
    /**
     * Report fields.
     */
    protected static final JDataFields FIELD_DEFS = new JDataFields(AccountStatus.class.getSimpleName());

    @Override
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public String formatObject() {
        return AccountStatus.class.getSimpleName();
    }

    /**
     * CloseDate Field Id.
     */
    public static final JDataField FIELD_CLOSEDATE = FIELD_DEFS.declareLocalField("CloseDate");

    /**
     * firstEvent Field Id.
     */
    public static final JDataField FIELD_EVTFIRST = FIELD_DEFS.declareLocalField("FirstEvent");

    /**
     * lastEvent Field Id.
     */
    public static final JDataField FIELD_EVTLAST = FIELD_DEFS.declareLocalField("LastEvent");

    /**
     * initialPrice Field Id.
     */
    public static final JDataField FIELD_INITPRC = FIELD_DEFS.declareLocalField("InitialPrice");

    /**
     * hasLoans Field Id.
     */
    public static final JDataField FIELD_HASLOANS = FIELD_DEFS.declareLocalField("hasLoans");

    /**
     * hasRates Field Id.
     */
    public static final JDataField FIELD_HASRATES = FIELD_DEFS.declareLocalField("hasRates");

    /**
     * hasPrice Field Id.
     */
    public static final JDataField FIELD_HASPRICE = FIELD_DEFS.declareLocalField("hasPrices");

    /**
     * hasPatterns Field Id.
     */
    public static final JDataField FIELD_HASPATT = FIELD_DEFS.declareLocalField("hasPatterns");

    /**
     * isParent Field Id.
     */
    public static final JDataField FIELD_ISPARENT = FIELD_DEFS.declareLocalField("isParent");

    /**
     * isAliased Field Id.
     */
    public static final JDataField FIELD_ISALIASD = FIELD_DEFS.declareLocalField("isAliasedTo");

    /**
     * isHolding Field Id.
     */
    public static final JDataField FIELD_ISHOLDING = FIELD_DEFS.declareLocalField("isHolding");

    /**
     * isCloseable Field Id.
     */
    public static final JDataField FIELD_ISCLSABL = FIELD_DEFS.declareLocalField("isCloseable");

    @Override
    public Object getFieldValue(final JDataField pField) {
        /* Handle flags */
        if (FIELD_CLOSEDATE.equals(pField)) {
            return (theCloseDate != null)
                    ? theCloseDate
                    : JDataFieldValue.SkipField;
        }
        if (FIELD_EVTFIRST.equals(pField)) {
            return (theEarliest != null)
                    ? theEarliest
                    : JDataFieldValue.SkipField;
        }
        if (FIELD_EVTLAST.equals(pField)) {
            return (theLatest != null)
                    ? theLatest
                    : JDataFieldValue.SkipField;
        }
        if (FIELD_INITPRC.equals(pField)) {
            return (theInitPrice != null)
                    ? theInitPrice
                    : JDataFieldValue.SkipField;
        }
        if (FIELD_HASLOANS.equals(pField)) {
            return hasLoans
                    ? hasLoans
                    : JDataFieldValue.SkipField;
        }
        if (FIELD_HASRATES.equals(pField)) {
            return hasRates
                    ? hasRates
                    : JDataFieldValue.SkipField;
        }
        if (FIELD_HASPRICE.equals(pField)) {
            return hasPrices
                    ? hasPrices
                    : JDataFieldValue.SkipField;
        }
        if (FIELD_HASPATT.equals(pField)) {
            return hasPatterns
                    ? hasPatterns
                    : JDataFieldValue.SkipField;
        }
        if (FIELD_ISPARENT.equals(pField)) {
            return isParent
                    ? isParent
                    : JDataFieldValue.SkipField;
        }
        if (FIELD_ISALIASD.equals(pField)) {
            return isAliasedTo
                    ? isAliasedTo
                    : JDataFieldValue.SkipField;
        }
        if (FIELD_ISHOLDING.equals(pField)) {
            return isHolding
                    ? isHolding
                    : JDataFieldValue.SkipField;
        }
        if (FIELD_ISCLSABL.equals(pField)) {
            return isCloseable;
        }

        /* Reject field */
        return JDataFieldValue.UnknownField;
    }

    /**
     * Close Date.
     */
    private JDateDay theCloseDate = null;

    /**
     * Earliest Event.
     */
    private Event theEarliest = null;

    /**
     * Latest Event.
     */
    private Event theLatest = null;

    /**
     * Initial Price.
     */
    private AccountPrice theInitPrice = null;

    /**
     * Is this closeable?
     */
    private boolean isCloseable = true;

    /**
     * Does this have loans?
     */
    private boolean hasLoans = false;

    /**
     * Does this have rates?
     */
    private boolean hasRates = false;

    /**
     * Does this have prices?
     */
    private boolean hasPrices = false;

    /**
     * Does this have patterns?
     */
    private boolean hasPatterns = false;

    /**
     * is this a Parent?.
     */
    private boolean isParent = false;

    /**
     * is this Aliased to?
     */
    private boolean isAliasedTo = false;

    /**
     * is this a holding account for a portfolio?
     */
    private boolean isHolding = false;

    /**
     * Get the close Date of the account.
     * @return the closeDate
     */
    protected JDateDay getCloseDate() {
        return theCloseDate;
    }

    /**
     * Are there events relating to this account?
     * @return true/false
     */
    protected boolean hasEvents() {
        return (theEarliest != null);
    }

    /**
     * Obtain Earliest event.
     * @return the event
     */
    protected Event getEarliest() {
        return theEarliest;
    }

    /**
     * Obtain Latest Event.
     * @return the event
     */
    protected Event getLatest() {
        return theLatest;
    }

    /**
     * Obtain Initial Price.
     * @return the price
     */
    protected AccountPrice getInitPrice() {
        return theInitPrice;
    }

    /**
     * Is the account closeable?
     * @return true/false
     */
    protected boolean isCloseable() {
        return isCloseable;
    }

    /**
     * Does the account have loans?
     * @return true/false
     */
    protected boolean hasLoans() {
        return hasLoans;
    }

    /**
     * Does the account have rates?
     * @return true/false
     */
    protected boolean hasRates() {
        return hasRates;
    }

    /**
     * Does the account have prices?
     * @return true/false
     */
    protected boolean hasPrices() {
        return hasPrices;
    }

    /**
     * Does the account have patterns?
     * @return true/false
     */
    protected boolean hasPatterns() {
        return hasPatterns;
    }

    /**
     * Is the account a parent?
     * @return true/false
     */
    protected boolean isParent() {
        return isParent;
    }

    /**
     * Is the account aliased to?
     * @return true/false
     */
    protected boolean isAliasedTo() {
        return isAliasedTo;
    }

    /**
     * Is the account a holding account?
     * @return true/false
     */
    protected boolean isHolding() {
        return isHolding;
    }

    /**
     * Standard constructor.
     */
    protected AccountStatus() {
    }

    /**
     * Copy constructor.
     * @param pStatus the original status
     */
    protected AccountStatus(final AccountStatus pStatus) {
        theEarliest = pStatus.theEarliest;
        theLatest = pStatus.theLatest;
        theCloseDate = pStatus.theCloseDate;
        theInitPrice = pStatus.theInitPrice;
        isCloseable = pStatus.isCloseable;
        isHolding = pStatus.isHolding;
        isAliasedTo = pStatus.isAliasedTo;
        isParent = pStatus.isParent;
        hasPatterns = pStatus.hasPatterns;
        hasRates = pStatus.hasRates;
        hasPrices = pStatus.hasPrices;
        hasLoans = pStatus.hasLoans;
    }

    /**
     * Reset the account status.
     */
    protected void resetStatus() {
        /* Reset flags */
        isCloseable = true;
        theEarliest = null;
        theLatest = null;
        theInitPrice = null;
        hasLoans = false;
        hasRates = false;
        hasPrices = false;
        hasPatterns = false;
        isParent = false;
        isAliasedTo = false;
        isHolding = false;
    }

    /**
     * Is the account deletable?
     * @param pState the account state
     * @return true/false
     */
    protected boolean isDeletable(final DataState pState) {
        return ((theLatest == null)
                && (!isParent)
                && (!hasRates)
                && ((!hasPrices) || (pState == DataState.NEW))
                && (!hasPatterns) && (!isAliasedTo));
    }

    /**
     * Set non-closeable.
     */
    protected void setNonCloseable() {
        /* Record the status */
        isCloseable = false;
    }

    /**
     * Adjust closed date.
     * @throws JDataException on error
     */
    protected void adjustClosed() throws JDataException {
        /* Access latest activity date */
        JDateDay myCloseDate = (theLatest == null)
                ? null
                : theLatest.getDate();

        /* Store the close date */
        theCloseDate = myCloseDate;
    }

    /**
     * Touch an account.
     * @param pAccount the owning account
     * @param pObject the object touch the account
     */
    protected void touchItem(final Account pAccount,
                             final DataItem pObject) {
        /* If we are being touched by a rate */
        if (pObject instanceof AccountRate) {
            /* Note flags */
            hasRates = true;

            /* If we are being touched by a price */
        } else if (pObject instanceof AccountPrice) {
            /* Note flags */
            hasPrices = true;
            if (theInitPrice == null) {
                theInitPrice = (AccountPrice) pObject;
            }

            /* If we are being touched by a pattern */
        } else if (pObject instanceof Pattern) {
            /* Note flags */
            hasPatterns = true;
            isCloseable = false;

            /* If we are being touched by an event */
        } else if (pObject instanceof Event) {
            /* Access as event */
            Event myEvent = (Event) pObject;

            /* Record the event */
            if (theEarliest == null) {
                theEarliest = myEvent;
            }
            theLatest = myEvent;

            /* If we have a parent, touch it */
            Account myParent = pAccount.getParent();
            if (myParent != null) {
                myParent.touchItem(pObject);
            }

            /* If we are being touched by another account */
        } else if (pObject instanceof AccountInfo) {
            /* Access as account */
            AccountInfo myInfo = (AccountInfo) pObject;
            Account myAccount = myInfo.getOwner();
            Boolean isClosed = myAccount.isClosed();

            /* If we are being aliased to */
            switch (myInfo.getInfoClass()) {
                case Alias:
                    /* Set flags */
                    isAliasedTo = true;
                    if (!isClosed) {
                        isCloseable = false;
                    }
                    break;
                case Parent:
                    /* Set flags */
                    isParent = true;
                    if (!isClosed) {
                        isCloseable = false;
                    }
                    if (myAccount.isLoan()) {
                        hasLoans = true;
                    }
                    break;
                case Holding:
                    /* Set flags */
                    isHolding = true;
                    if (!isClosed) {
                        isCloseable = false;
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
