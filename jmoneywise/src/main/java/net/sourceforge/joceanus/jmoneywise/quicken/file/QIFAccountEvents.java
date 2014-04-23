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
import java.util.List;

import net.sourceforge.joceanus.jmoneywise.data.AssetBase;

/**
 * Class representing an account and its events.
 */
public class QIFAccountEvents {
    /**
     * Account.
     */
    private final QIFAccount theAccount;

    /**
     * Events.
     */
    private final List<QIFRecord<?>> theEvents;

    /**
     * Obtain the account.
     * @return the account
     */
    public QIFAccount getAccount() {
        return theAccount;
    }

    /**
     * Constructor.
     * @param pFile the QIF file.
     * @param pAccount the account.
     */
    protected QIFAccountEvents(final QIFFile pFile,
                               final AssetBase<?> pAccount) {
        /* Store parameters */
        theAccount = new QIFAccount(pFile, pAccount);

        /* Create the list */
        theEvents = new ArrayList<QIFRecord<?>>();
    }

    /**
     * Add event.
     * @param pEvent the event record set
     */
    protected void addEvent(final QIFRecord<?> pEvent) {
        /* Add the event */
        theEvents.add(pEvent);
    }
}
