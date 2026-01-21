/*
 * MoneyWise: Finance Application
 * Copyright 2012-2026. Tony Washer
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
 */
package io.github.tonywasher.joceanus.moneywise.data.builder;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseDataSet;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWisePayee;
import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWisePayeeClass;
import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWisePayeeType;
import io.github.tonywasher.joceanus.moneywise.exc.MoneyWiseDataException;

/**
 * Payee Builder.
 */
public class MoneyWisePayeeBuilder {
    /**
     * DataSet.
     */
    private final MoneyWiseDataSet theDataSet;

    /**
     * The PayeeName.
     */
    private String theName;

    /**
     * The PayeeType.
     */
    private MoneyWisePayeeType theType;

    /**
     * Constructor.
     *
     * @param pDataSet the dataSet
     */
    public MoneyWisePayeeBuilder(final MoneyWiseDataSet pDataSet) {
        theDataSet = pDataSet;
        theDataSet.getPayees().ensureMap();
    }

    /**
     * Set Name.
     *
     * @param pName the name of the payee.
     * @return the builder
     */
    public MoneyWisePayeeBuilder name(final String pName) {
        theName = pName;
        return this;
    }

    /**
     * Set the payeeType.
     *
     * @param pType the type of the payee.
     * @return the builder
     */
    public MoneyWisePayeeBuilder type(final MoneyWisePayeeClass pType) {
        return type(theDataSet.getPayeeTypes().findItemByClass(pType));
    }

    /**
     * Set the payeeType.
     *
     * @param pType the type of the payee.
     * @return the builder
     */
    public MoneyWisePayeeBuilder type(final MoneyWisePayeeType pType) {
        theType = pType;
        return this;
    }

    /**
     * Build the payee.
     *
     * @return the new Payee
     * @throws OceanusException on error
     */
    public MoneyWisePayee build() throws OceanusException {
        /* Create the payee */
        final MoneyWisePayee myPayee = theDataSet.getPayees().addNewItem();
        myPayee.setName(theName);
        myPayee.setCategory(theType);
        myPayee.setClosed(Boolean.FALSE);

        /* Reset the values */
        reset();

        /* Check for errors */
        myPayee.adjustMapForItem();
        myPayee.validate();
        if (myPayee.hasErrors()) {
            myPayee.removeItem();
            throw new MoneyWiseDataException(myPayee, "Failed validation");
        }

        /* Return the payee */
        return myPayee;
    }

    /**
     * Reset the builder.
     */
    public void reset() {
        /* Reset values */
        theName = null;
        theType = null;
    }
}
