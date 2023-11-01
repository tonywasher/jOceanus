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
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionTag;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Tag Builder.
 */
public class MoneyWiseTagBuilder {
    /**
     * DataSet.
     */
    private final MoneyWiseData theDataSet;

    /**
     * The TagName.
     */
    private String theName;

    /**
     * Constructor.
     * @param pDataSet the dataSet
     */
    public MoneyWiseTagBuilder(final MoneyWiseData pDataSet) {
        theDataSet = pDataSet;
        theDataSet.getTransactionTags().ensureMap();
    }

    /**
     * Set Name.
     * @param pName the name of the tag.
     * @return the builder
     */
    public MoneyWiseTagBuilder name(final String pName) {
        theName = pName;
        return this;
    }

    /**
     * Build the tag.
     * @return the new tag
     * @throws OceanusException on error
     */
    public TransactionTag build() throws OceanusException {
        /* Create the tag */
        final TransactionTag myTag = theDataSet.getTransactionTags().addNewItem();
        myTag.setName(theName);

        /* Check for errors */
        myTag.adjustMapForItem();
        myTag.validate();
        if (myTag.hasErrors()) {
            theDataSet.getTransactionTags().remove(myTag);
            throw new MoneyWiseDataException("Failed validation");
        }

        /* Reset values */
        theName = null;

        /* Return the tag */
        return myTag;
    }
}
