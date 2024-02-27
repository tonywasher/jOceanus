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
package net.sourceforge.joceanus.jmoneywise.data.builder;

import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseCashCategory;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWiseCashCategoryClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWiseCashCategoryType;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * CashCategory Builder.
 */
public class MoneyWiseCashCategoryBuilder {
    /**
     * DataSet.
     */
    private final MoneyWiseDataSet theDataSet;

    /**
     * The categoryName.
     */
    private String theName;

    /**
     * The Parent.
     */
    private MoneyWiseCashCategory theParent;

    /**
     * The CategoryType.
     */
    private MoneyWiseCashCategoryType theType;

    /**
     * Constructor.
     * @param pDataSet the dataSet
     */
    public MoneyWiseCashCategoryBuilder(final MoneyWiseDataSet pDataSet) {
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
    public MoneyWiseCashCategoryBuilder type(final MoneyWiseCashCategoryType pType) {
        theType = pType;
        return this;
    }

    /**
     * Set the categoryType.
     * @param pType the type of the category.
     * @return the builder
     */
    public MoneyWiseCashCategoryBuilder type(final MoneyWiseCashCategoryClass pType) {
        return type(theDataSet.getCashCategoryTypes().findItemByClass(pType));
    }

    /**
     * Obtain the cashCategory.
     * @param pCategory the name of the category.
     * @return the cashCategory
     */
    private MoneyWiseCashCategory lookupCategory(final String pCategory) {
        return theDataSet.getCashCategories().findItemByName(pCategory);
    }

    /**
     * Build the cashCategory.
     * @return the new Category
     * @throws OceanusException on error
     */
    public MoneyWiseCashCategory build() throws OceanusException {
        /* Create the category */
        final MoneyWiseCashCategory myCategory = theDataSet.getCashCategories().addNewItem();
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
