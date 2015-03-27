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
package net.sourceforge.joceanus.jmoneywise.quicken.file;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jmoneywise.data.TransactionAsset;

/**
 * Class representing an account and its events.
 */
public class QIFAccountEvents
        implements Comparable<QIFAccountEvents> {
    /**
     * Account.
     */
    private final QIFAccount theAccount;

    /**
     * Events.
     */
    private final List<QIFEventRecord<?>> theEvents;

    /**
     * Constructor.
     * @param pFile the QIF file.
     * @param pAccount the account.
     */
    protected QIFAccountEvents(final QIFFile pFile,
                               final TransactionAsset pAccount) {
        /* Store parameters */
        theAccount = new QIFAccount(pFile, pAccount);

        /* Create the list */
        theEvents = new ArrayList<QIFEventRecord<?>>();
    }

    /**
     * Constructor.
     * @param pFile the QIF file.
     * @param pAccount the account name.
     */
    protected QIFAccountEvents(final QIFFile pFile,
                               final String pAccount) {
        /* Store parameters */
        theAccount = new QIFAccount(pFile, pAccount);

        /* Create the list */
        theEvents = new ArrayList<QIFEventRecord<?>>();
    }

    /**
     * Constructor.
     * @param pAccount the account.
     */
    protected QIFAccountEvents(final QIFAccount pAccount) {
        /* Store parameters */
        theAccount = pAccount;

        /* Create the list */
        theEvents = new ArrayList<QIFEventRecord<?>>();
    }

    /**
     * Obtain the account.
     * @return the account
     */
    public QIFAccount getAccount() {
        return theAccount;
    }

    /**
     * Obtain the events.
     * @return the events
     */
    public List<QIFEventRecord<?>> getEvents() {
        return theEvents;
    }

    /**
     * Event iterator.
     * @return the iterator
     */
    public Iterator<QIFEventRecord<?>> eventIterator() {
        return theEvents.iterator();
    }

    /**
     * Add event.
     * @param pEvent the event record set
     */
    protected void addEvent(final QIFEventRecord<?> pEvent) {
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
        QIFAccountEvents myEvents = (QIFAccountEvents) pThat;

        /* Check account */
        if (!theAccount.equals(myEvents.getAccount())) {
            return false;
        }

        /* Check events */
        return theEvents.equals(myEvents.getEvents());
    }

    @Override
    public int hashCode() {
        int myResult = QIFFile.HASH_BASE * theAccount.hashCode();
        return myResult + theEvents.hashCode();
    }

    @Override
    public int compareTo(final QIFAccountEvents pThat) {
        return theAccount.compareTo(pThat.getAccount());
    }
}
