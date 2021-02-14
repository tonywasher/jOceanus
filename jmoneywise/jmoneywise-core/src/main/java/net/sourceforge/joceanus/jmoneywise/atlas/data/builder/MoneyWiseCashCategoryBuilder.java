/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2021 Tony Washer
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

import net.sourceforge.joceanus.jmoneywise.lethe.data.CashCategory;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.CashCategoryType;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * CashCategory Builder.
 */
public class MoneyWiseCashCategoryBuilder {
    /**
     * DataSet.
     */
    private final MoneyWiseData theDataSet;

    /**
     * The categoryName.
     */
    private String theName;

    /**
     * The Parent.
     */
    private CashCategory theParent;

    /**
     * The CategoryType.
     */
    private CashCategoryType theType;

    /**
     * Constructor.
     * @param pDataSet the dataSet
     */
    MoneyWiseCashCategoryBuilder(final MoneyWiseData pDataSet) {
        theDataSet = pDataSet;
    }

    /**
     * Set Name.
     * @param pName the name of the category.
     * @return the builder
     */
    public MoneyWiseCashCategoryBuilder name(final String pName) {
        final int myIndex = pName.lastIndexOf(':');
        if (myIndex == -1) {
            theName = pName;
            theParent = null;
        } else {
            theName = pName.substring(myIndex + 1);
            theParent = lookupCategory(pName.substring(0, myIndex));
        }
        return this;
    }

    /**
     * Set the categoryType.
     * @param pType the type of the category.
     * @return the builder
     */
    public MoneyWiseCashCategoryBuilder type(final CashCategoryType pType) {
        theType = pType;
        return this;
    }

    /**
     * Obtain the cashCategory.
     * @param pCategory the name of the category.
     * @return the cashCategory
     */
    public CashCategory lookupCategory(final String pCategory) {
        return theDataSet.getCashCategories().findItemByName(pCategory);
    }

    /**
     * Build the cashCategory.
     * @return the new Category
     * @throws OceanusException on error
     */
    public CashCategory build() throws OceanusException {
        /* Create the category */
        final CashCategory myCategory = theDataSet.getCashCategories().addNewItem();
        myCategory.setSubCategoryName(theName);
        myCategory.setParentCategory(theParent);
        myCategory.setCategoryType(theType);

        /* Reset values */
        theName = null;
        theParent = null;
        theType = null;

        /* Return the category */
        return myCategory;
    }
}
