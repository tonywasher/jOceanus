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
import net.sourceforge.joceanus.jmoneywise.lethe.data.DepositCategory;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.DepositCategoryClass;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.DepositCategoryType;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * DepositCategory Builder.
 */
public class MoneyWiseDepositCategoryBuilder {
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
    private DepositCategory theParent;

    /**
     * The CategoryType.
     */
    private DepositCategoryType theType;

    /**
     * Constructor.
     * @param pDataSet the dataSet
     */
    public MoneyWiseDepositCategoryBuilder(final MoneyWiseData pDataSet) {
        theDataSet = pDataSet;
        theDataSet.getDepositCategories().ensureMap();
    }

    /**
     * Set Name.
     * @param pName the name of the category.
     * @return the builder
     */
    public MoneyWiseDepositCategoryBuilder name(final String pName) {
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
    public MoneyWiseDepositCategoryBuilder type(final DepositCategoryType pType) {
        theType = pType;
        return this;
    }

    /**
     * Set the categoryType.
     * @param pType the type of the category.
     * @return the builder
     */
    public MoneyWiseDepositCategoryBuilder type(final DepositCategoryClass pType) {
        return type(theDataSet.getDepositCategoryTypes().findItemByClass(pType));
    }

    /**
     * Obtain the depositCategory.
     * @param pCategory the name of the category.
     * @return the depositCategory
     */
    private DepositCategory lookupCategory(final String pCategory) {
        return theDataSet.getDepositCategories().findItemByName(pCategory);
    }

    /**
     * Build the depositCategory.
     * @return the new Category
     * @throws OceanusException on error
     */
    public DepositCategory build() throws OceanusException {
        /* Create the category */
        final DepositCategory myCategory = theDataSet.getDepositCategories().addNewItem();
        myCategory.setCategoryType(theType);
        myCategory.setParentCategory(theParent);
        myCategory.setSubCategoryName(theName);

        /* Check for errors */
        myCategory.adjustMapForItem();
        myCategory.validate();
        if (myCategory.hasErrors()) {
            theDataSet.getDepositCategories().remove(myCategory);
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
