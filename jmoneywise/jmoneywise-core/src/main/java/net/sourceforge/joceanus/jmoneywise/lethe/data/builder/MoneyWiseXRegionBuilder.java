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
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Region;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Region Builder.
 */
public class MoneyWiseXRegionBuilder {
    /**
     * DataSet.
     */
    private final MoneyWiseData theDataSet;

    /**
     * The RegionName.
     */
    private String theName;

    /**
     * Constructor.
     * @param pDataSet the dataSet
     */
    public MoneyWiseXRegionBuilder(final MoneyWiseData pDataSet) {
        theDataSet = pDataSet;
        theDataSet.getRegions().ensureMap();
    }

    /**
     * Set Name.
     * @param pName the name of the tag.
     * @return the builder
     */
    public MoneyWiseXRegionBuilder name(final String pName) {
        theName = pName;
        return this;
    }

    /**
     * Obtain the region.
     * @param pRegion the region.
     * @return the region
     */
    public Region lookupRegion(final String pRegion) {
        return theDataSet.getRegions().findItemByName(pRegion);
    }

    /**
     * Build the tag.
     * @return the new tag
     * @throws OceanusException on error
     */
    public Region build() throws OceanusException {
        /* Create the region */
        final Region myRegion = theDataSet.getRegions().addNewItem();
        myRegion.setName(theName);

        /* Check for errors */
        myRegion.adjustMapForItem();
        myRegion.validate();
        if (myRegion.hasErrors()) {
            theDataSet.getRegions().remove(myRegion);
            throw new MoneyWiseDataException(myRegion, "Failed validation");
        }

        /* Reset values */
        theName = null;

        /* Return the region */
        return myRegion;
    }
}
