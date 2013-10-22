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

import java.util.ResourceBundle;

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
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(AccountStatus.class.getName());

    /**
     * Local Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(NLS_BUNDLE.getString("DataName"));

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
    private static final JDataField FIELD_CLOSEDATE = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataCloseDate"));

    /**
     * firstEvent Field Id.
     */
    private static final JDataField FIELD_EVTFIRST = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataFirstEvent"));

    /**
     * lastEvent Field Id.
     */
    private static final JDataField FIELD_EVTLAST = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataLastEvent"));

    /**
     * initialPrice Field Id.
     */
    private static final JDataField FIELD_INITPRC = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataInitialPrice"));

    /**
     * hasLoans Field Id.
     */
    private static final JDataField FIELD_HASLOANS = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataHasLoans"));

    /**
     * hasRates Field Id.
     */
    private static final JDataField FIELD_HASRATES = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataHasRates"));

    /**
     * hasPrice Field Id.
     */
    private static final JDataField FIELD_HASPRICE = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataHasPrices"));

    /**
     * hasPatterns Field Id.
     */
    private static final JDataField FIELD_HASPATT = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataHasPatterns"));

    /**
     * isParent Field Id.
     */
    private static final JDataField FIELD_ISPARENT = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataIsParent"));

    /**
     * isAliased Field Id.
     */
    private static final JDataField FIELD_ISALIASD = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataIsAliasedTo"));

    /**
     * isPortfolio Field Id.
     */
    private static final JDataField FIELD_ISPORTFOLIO = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataIsPortfolio"));

    /**
     * isHolding Field Id.
     */
    private static final JDataField FIELD_ISHOLDING = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataIsHolding"));

    /**
     * isCloseable Field Id.
     */
    private static final JDataField FIELD_ISCLSABL = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataIsCloseable"));

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
        if (FIELD_ISPORTFOLIO.equals(pField)) {
            return isPortfolio
                    ? isPortfolio
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
     * is this a portfolio?
     */
    private boolean isPortfolio = false;

    /**
     * is this a holding account?
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
     * Is the account a portfolio account?
     * @return true/false
     */
    protected boolean isPortfolio() {
        return isPortfolio;
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
        isPortfolio = pStatus.isPortfolio;
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
        isPortfolio = false;
        isHolding = false;
    }

    /**
     * Is the account deletable?
     * @param pState the account state
     * @return true/false
     */
    protected boolean isDeletable(final DataState pState) {
        /* First of all we cannot delete if we are referenced by events */
        boolean canDelete = (theLatest == null);

        /* Next we cannot delete if we are referenced by another account */
        canDelete &= ((!isParent) && (!isAliasedTo));
        canDelete &= ((!isPortfolio) && (!isHolding));

        /* Next we cannot delete if we are referenced by rates/patterns */
        canDelete &= ((!hasRates) && (!hasPatterns));

        /* Next we cannot delete if we are referenced by prices (except for auto-price) */
        canDelete &= ((!hasPrices) || (pState == DataState.NEW));

        /* Finally we can only delete if we are new */
        return canDelete;
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
                case Portfolio:
                    /* Set flags */
                    isPortfolio = true;
                    if (!isClosed) {
                        isCloseable = false;
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
