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
package net.sourceforge.joceanus.moneywise.data.builder;

import net.sourceforge.joceanus.moneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransCategory;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseTransCategoryClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseTransCategoryType;
import net.sourceforge.joceanus.tethys.OceanusException;

/**
 * TransactionCategory Builder.
 */
public class MoneyWiseTransCategoryBuilder {
    /**
     * DataSet.
     */
    private final MoneyWiseDataSet theDataSet;

    /**
     * The CategoryName.
     */
    private String theName;

    /**
     * The Parent.
     */
    private MoneyWiseTransCategory theParent;

    /**
     * The CategoryType.
     */
    private MoneyWiseTransCategoryType theType;

    /**
     * Constructor.
     * @param pDataSet the dataSet
     */
    public MoneyWiseTransCategoryBuilder(final MoneyWiseDataSet pDataSet) {
        theDataSet = pDataSet;
        theDataSet.getTransCategories().ensureMap();
    }

    /**
     * Set Name.
     * @param pName the name of the category.
     * @return the builder
     */
    public MoneyWiseTransCategoryBuilder name(final String pName) {
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
    public MoneyWiseTransCategoryBuilder type(final MoneyWiseTransCategoryType pType) {
        theType = pType;
        return this;
    }

    /**
     * Set the categoryType.
     * @param pType the type of the category.
     * @return the builder
     */
    public MoneyWiseTransCategoryBuilder type(final MoneyWiseTransCategoryClass pType) {
        return type(theDataSet.getTransCategoryTypes().findItemByClass(pType));
    }

    /**
     * Set the parent.
     * @param pParent the parent.
     * @return the builder
     */
    public MoneyWiseTransCategoryBuilder parent(final MoneyWiseTransCategory pParent) {
        theParent = pParent;
        return this;
    }

    /**
     * Obtain the transCategory.
     * @param pCategory the name of the category.
     * @return the loanCategory
     */
    private MoneyWiseTransCategory lookupCategory(final String pCategory) {
        return theDataSet.getTransCategories().findItemByName(pCategory);
    }

    /**
     * Build the transactionCategory.
     * @return the new Category
     * @throws OceanusException on error
     */
    public MoneyWiseTransCategory build() throws OceanusException {
        /* Create the category */
        final MoneyWiseTransCategory myCategory = theDataSet.getTransCategories().addNewItem();
        myCategory.setCategoryType(theType);
        myCategory.setParentCategory(theParent);
        myCategory.setSubCategoryName(theName);

        /* Check for errors */
        myCategory.adjustMapForItem();
        myCategory.validate();
        if (myCategory.hasErrors()) {
            myCategory.removeItem();
            throw new MoneyWiseDataException(myCategory, "Failed validation");
        }

        /* Reset values */
        theName = null;
        theParent = null;
        theType = null;

        /* Return the category */
        return myCategory;
    }
}
