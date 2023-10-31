/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2023 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.atlas.data.builder;

import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Payee;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.PayeeType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.PayeeTypeClass;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Payee Builder.
 */
public class MoneyWisePayeeBuilder {
    /**
     * DataSet.
     */
    private final MoneyWiseData theDataSet;

    /**
     * The PayeeName.
     */
    private String theName;

    /**
     * The PayeeType.
     */
    private PayeeType theType;

    /**
     * Constructor.
     * @param pDataSet the dataSet
     */
    public MoneyWisePayeeBuilder(final MoneyWiseData pDataSet) {
        theDataSet = pDataSet;
    }

    /**
     * Set Name.
     * @param pName the name of the payee.
     * @return the builder
     */
    public MoneyWisePayeeBuilder name(final String pName) {
        theName = pName;
        return this;
    }

    /**
     * Set the payeeType.
     * @param pType the type of the payee.
     * @return the builder
     */
    public MoneyWisePayeeBuilder type(final PayeeTypeClass pType) {
        return type(theDataSet.getPayeeTypes().findItemByClass(pType));
    }

    /**
     * Set the payeeType.
     * @param pType the type of the payee.
     * @return the builder
     */
    public MoneyWisePayeeBuilder type(final PayeeType pType) {
        theType = pType;
        return this;
    }

    /**
     * Build the payee.
     * @return the new Payee
     * @throws OceanusException on error
     */
    public Payee build() throws OceanusException {
        /* Create the payee */
        final Payee myPayee = theDataSet.getPayees().addNewItem();
        myPayee.setName(theName);
        myPayee.setCategory(theType);

        /* Check for errors */
        myPayee.validate();
        if (myPayee.hasErrors()) {
            theDataSet.getPayees().remove(myPayee);
            throw new MoneyWiseDataException("Failed validation");
        }

        /* Update maps to reflect the new object */
        myPayee.adjustMapForItem();

        /* Reset values */
        theName = null;
        theType = null;

        /* Return the payee */
        return myPayee;
    }
}
