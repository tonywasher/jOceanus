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
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseLoanCategory;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWiseLoanCategoryClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWiseLoanCategoryType;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * LoanCategory Builder.
 */
public class MoneyWiseLoanCategoryBuilder {
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
    private MoneyWiseLoanCategory theParent;

    /**
     * The CategoryType.
     */
    private MoneyWiseLoanCategoryType theType;

    /**
     * Constructor.
     * @param pDataSet the dataSet
     */
    public MoneyWiseLoanCategoryBuilder(final MoneyWiseDataSet pDataSet) {
        theDataSet = pDataSet;
        theDataSet.getLoanCategories().ensureMap();
    }

    /**
     * Set Name.
     * @param pName the name of the category.
     * @return the builder
     */
    public MoneyWiseLoanCategoryBuilder name(final String pName) {
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
    public MoneyWiseLoanCategoryBuilder type(final MoneyWiseLoanCategoryType pType) {
        theType = pType;
        return this;
    }

    /**
     * Set the categoryType.
     * @param pType the type of the category.
     * @return the builder
     */
    public MoneyWiseLoanCategoryBuilder type(final MoneyWiseLoanCategoryClass pType) {
        return type(theDataSet.getLoanCategoryTypes().findItemByClass(pType));
    }

    /**
     * Obtain the loanCategory.
     * @param pCategory the name of the category.
     * @return the loanCategory
     */
    private MoneyWiseLoanCategory lookupCategory(final String pCategory) {
        return theDataSet.getLoanCategories().findItemByName(pCategory);
    }

    /**
     * Build the loanCategory.
     * @return the new Category
     * @throws OceanusException on error
     */
    public MoneyWiseLoanCategory build() throws OceanusException {
        /* Create the category */
        final MoneyWiseLoanCategory myCategory = theDataSet.getLoanCategories().addNewItem();
        myCategory.setCategoryType(theType);
        myCategory.setParentCategory(theParent);
        myCategory.setSubCategoryName(theName);

        /* Check for errors */
        myCategory.adjustMapForItem();
        myCategory.validate();
        if (myCategory.hasErrors()) {
            theDataSet.getLoanCategories().remove(myCategory);
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
