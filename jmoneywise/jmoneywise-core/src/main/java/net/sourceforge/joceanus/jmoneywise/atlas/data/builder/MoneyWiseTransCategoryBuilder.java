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
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionCategory;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.TransactionCategoryClass;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.TransactionCategoryType;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * TransactionCategory Builder.
 */
public class MoneyWiseTransCategoryBuilder {
    /**
     * DataSet.
     */
    private final MoneyWiseData theDataSet;

    /**
     * The CategoryName.
     */
    private String theName;

    /**
     * The Parent.
     */
    private TransactionCategory theParent;

    /**
     * The CategoryType.
     */
    private TransactionCategoryType theType;

    /**
     * Constructor.
     * @param pDataSet the dataSet
     */
    public MoneyWiseTransCategoryBuilder(final MoneyWiseData pDataSet) {
        theDataSet = pDataSet;
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
    public MoneyWiseTransCategoryBuilder type(final TransactionCategoryType pType) {
        theType = pType;
        return this;
    }

    /**
     * Set the categoryType.
     * @param pType the type of the category.
     * @return the builder
     */
    public MoneyWiseTransCategoryBuilder type(final TransactionCategoryClass pType) {
        return type(theDataSet.getTransCategoryTypes().findItemByClass(pType));
    }

    /**
     * Set the parent.
     * @param pParent the parent.
     * @return the builder
     */
    public MoneyWiseTransCategoryBuilder parent(final TransactionCategory pParent) {
        theParent = pParent;
        return this;
    }

    /**
     * Obtain the transCategory.
     * @param pCategory the name of the category.
     * @return the loanCategory
     */
    private TransactionCategory lookupCategory(final String pCategory) {
        return theDataSet.getTransCategories().findItemByName(pCategory);
    }

    /**
     * Build the transactionCategory.
     * @return the new Category
     * @throws OceanusException on error
     */
    public TransactionCategory build() throws OceanusException {
        /* Create the category */
        final TransactionCategory myCategory = theDataSet.getTransCategories().addNewItem();
        myCategory.setSubCategoryName(theName);
        myCategory.setParentCategory(theParent);
        myCategory.setCategoryType(theType);

        /* Check for errors */
        myCategory.validate();
        if (myCategory.hasErrors()) {
            theDataSet.getTransCategories().remove(myCategory);
            throw new MoneyWiseDataException("Failed validation");
        }

        /* Update maps to reflect the new object */
        myCategory.adjustMapForItem();

        /* Reset values */
        theName = null;
        theParent = null;
        theType = null;

        /* Return the category */
        return myCategory;
    }
}
