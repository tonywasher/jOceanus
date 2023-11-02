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
package net.sourceforge.joceanus.jmoneywise.lethe.data.builder;

import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.lethe.data.CashCategory;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.CashCategoryClass;
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
    public MoneyWiseCashCategoryBuilder(final MoneyWiseData pDataSet) {
        theDataSet = pDataSet;
        theDataSet.getCashCategories().ensureMap();
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
     * Set the categoryType.
     * @param pType the type of the category.
     * @return the builder
     */
    public MoneyWiseCashCategoryBuilder type(final CashCategoryClass pType) {
        return type(theDataSet.getCashCategoryTypes().findItemByClass(pType));
    }

    /**
     * Obtain the cashCategory.
     * @param pCategory the name of the category.
     * @return the cashCategory
     */
    private CashCategory lookupCategory(final String pCategory) {
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
        myCategory.setCategoryType(theType);
        myCategory.setParentCategory(theParent);
        myCategory.setSubCategoryName(theName);

        /* Check for errors */
        myCategory.adjustMapForItem();
        myCategory.validate();
        if (myCategory.hasErrors()) {
            theDataSet.getCashCategories().remove(myCategory);
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
