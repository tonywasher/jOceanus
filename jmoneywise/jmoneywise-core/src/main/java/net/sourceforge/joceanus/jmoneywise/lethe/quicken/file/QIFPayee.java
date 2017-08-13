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
package net.sourceforge.joceanus.jmoneywise.lethe.quicken.file;

import net.sourceforge.joceanus.jmoneywise.lethe.data.Payee;

/**
 * Class representing a Payee.
 * @author Tony Washer
 */
public class QIFPayee
        implements Comparable<QIFPayee> {
    /**
     * Payee name.
     */
    private final String theName;

    /**
     * Constructor.
     * @param pPayee the Payee
     */
    public QIFPayee(final Payee pPayee) {
        /* Store data */
        theName = pPayee.getName();
    }

    /**
     * Constructor.
     * @param pPayee the Payee
     */
    public QIFPayee(final String pPayee) {
        /* Store data */
        theName = pPayee;
    }

    /**
     * Obtain the Name.
     * @return the Name
     */
    public String getName() {
        return theName;
    }

    @Override
    public String toString() {
        return getName();
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
        final QIFPayee myPayee = (QIFPayee) pThat;

        /* Check date */
        return theName.equals(myPayee.getName());
    }

    @Override
    public int hashCode() {
        return theName.hashCode();
    }

    @Override
    public int compareTo(final QIFPayee pThat) {
        return theName.compareTo(pThat.getName());
    }
}
