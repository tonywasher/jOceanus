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
import net.sourceforge.joceanus.jmoneywise.lethe.data.LoanCategory;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.LoanCategoryClass;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.LoanCategoryType;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * LoanCategory Builder.
 */
public class MoneyWiseLoanCategoryBuilder {
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
    private LoanCategory theParent;

    /**
     * The CategoryType.
     */
    private LoanCategoryType theType;

    /**
     * Constructor.
     * @param pDataSet the dataSet
     */
    public MoneyWiseLoanCategoryBuilder(final MoneyWiseData pDataSet) {
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
    public MoneyWiseLoanCategoryBuilder type(final LoanCategoryType pType) {
        theType = pType;
        return this;
    }

    /**
     * Set the categoryType.
     * @param pType the type of the category.
     * @return the builder
     */
    public MoneyWiseLoanCategoryBuilder type(final LoanCategoryClass pType) {
        return type(theDataSet.getLoanCategoryTypes().findItemByClass(pType));
    }

    /**
     * Obtain the loanCategory.
     * @param pCategory the name of the category.
     * @return the loanCategory
     */
    private LoanCategory lookupCategory(final String pCategory) {
        return theDataSet.getLoanCategories().findItemByName(pCategory);
    }

    /**
     * Build the loanCategory.
     * @return the new Category
     * @throws OceanusException on error
     */
    public LoanCategory build() throws OceanusException {
        /* Create the category */
        final LoanCategory myCategory = theDataSet.getLoanCategories().addNewItem();
        myCategory.setCategoryType(theType);
        myCategory.setParentCategory(theParent);
        myCategory.setSubCategoryName(theName);

        /* Check for errors */
        myCategory.adjustMapForItem();
        myCategory.validate();
        if (myCategory.hasErrors()) {
            theDataSet.getLoanCategories().remove(myCategory);
            throw new MoneyWiseDataException("Failed validation");
        }

        /* Reset values */
        theName = null;
        theParent = null;
        theType = null;

        /* Return the category */
        return myCategory;
    }
}
