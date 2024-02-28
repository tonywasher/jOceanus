/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2024 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.sourceforge.joceanus.jmoneywise.quicken.file;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseTransAsset;

/**
 * Class representing an account and its events.
 */
public class MoneyWiseQIFAccountEvents
        implements Comparable<MoneyWiseQIFAccountEvents> {
    /**
     * Account.
     */
    private final MoneyWiseQIFAccount theAccount;

    /**
     * Events.
     */
    private final List<MoneyWiseQIFEventRecord<?>> theEvents;

    /**
     * Constructor.
     * @param pFile the QIF file.
     * @param pAccount the account.
     */
    protected MoneyWiseQIFAccountEvents(final MoneyWiseQIFFile pFile,
                                        final MoneyWiseTransAsset pAccount) {
        /* Store parameters */
        theAccount = new MoneyWiseQIFAccount(pFile, pAccount);

        /* Create the list */
        theEvents = new ArrayList<>();
    }

    /**
     * Constructor.
     * @param pFile the QIF file.
     * @param pAccount the account name.
     */
    protected MoneyWiseQIFAccountEvents(final MoneyWiseQIFFile pFile,
                                        final String pAccount) {
        /* Store parameters */
        theAccount = new MoneyWiseQIFAccount(pFile, pAccount);

        /* Create the list */
        theEvents = new ArrayList<>();
    }

    /**
     * Constructor.
     * @param pAccount the account.
     */
    protected MoneyWiseQIFAccountEvents(final MoneyWiseQIFAccount pAccount) {
        /* Store parameters */
        theAccount = pAccount;

        /* Create the list */
        theEvents = new ArrayList<>();
    }

    /**
     * Obtain the account.
     * @return the account
     */
    public MoneyWiseQIFAccount getAccount() {
        return theAccount;
    }

    /**
     * Obtain the events.
     * @return the events
     */
    public List<MoneyWiseQIFEventRecord<?>> getEvents() {
        return theEvents;
    }

    /**
     * Event iterator.
     * @return the iterator
     */
    public Iterator<MoneyWiseQIFEventRecord<?>> eventIterator() {
        return theEvents.iterator();
    }

    /**
     * Add event.
     * @param pEvent the event record set
     */
    protected void addEvent(final MoneyWiseQIFEventRecord<?> pEvent) {
        /* Add the event */
        theEvents.add(pEvent);
    }

    /**
     * Sort the events.
     */
    protected void sortEvents() {
        Collections.sort(theEvents);
    }

    @Override
    public boolean equals(final Object pThat) {
        /* Handle trivial cases */
        if (this == pThat) {
            return true;
        }
        if (pThat == null) {
            return false;
        }

        /* Check class */
        if (!getClass().equals(pThat.getClass())) {
            return false;
        }

        /* Cast correctly */
        final MoneyWiseQIFAccountEvents myEvents = (MoneyWiseQIFAccountEvents) pThat;

        /* Check account */
        if (!theAccount.equals(myEvents.getAccount())) {
            return false;
        }

        /* Check events */
        return theEvents.equals(myEvents.getEvents());
    }

    @Override
    public int hashCode() {
        final int myResult = MoneyWiseQIFFile.HASH_BASE * theAccount.hashCode();
        return myResult + theEvents.hashCode();
    }

    @Override
    public int compareTo(final MoneyWiseQIFAccountEvents pThat) {
        return theAccount.compareTo(pThat.getAccount());
    }
}
