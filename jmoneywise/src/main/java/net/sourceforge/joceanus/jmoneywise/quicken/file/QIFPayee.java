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

import net.sourceforge.joceanus.jmoneywise.data.Payee;

/**
 * Class representing a Payee.
 * @author Tony Washer
 */
public class QIFPayee {
    /**
     * Payee name.
     */
    private final String theName;

    /**
     * Obtain the Name.
     * @return the Name
     */
    public String getName() {
        return theName;
    }

    /**
     * Constructor.
     * @param pFile the QIF File
     * @param pPayee the Payee
     */
    public QIFPayee(final QIFFile pFile,
                    final Payee pPayee) {
        /* Store data */
        theName = pPayee.getName();
    }
}
