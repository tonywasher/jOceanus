/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2014 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jmoneywise.views;

import net.sourceforge.joceanus.jmoneywise.analysis.Analysis;
import net.sourceforge.joceanus.jtethys.resource.ResourceMgr.ResourceId;

/**
 * Resource IDs for jMoneyWise View Fields.
 */
public enum MoneyWiseViewResource implements ResourceId {
    /**
     * View Name.
     */
    VIEW_NAME("View.Name"),

    /**
     * SpotPrice Name.
     */
    SPOTPRICE_NAME("SpotPrice.Name"),

    /**
     * SpotPrice NextDate.
     */
    SPOTPRICE_NEXTDATE("SpotPrice.NextDate"),

    /**
     * SpotPrice PrevDate.
     */
    SPOTPRICE_PREVDATE("SpotPrice.PrevDate"),

    /**
     * SpotPrice PrevDate.
     */
    SPOTPRICE_PREVPRICE("SpotPrice.PrevPrice"),

    /**
     * ViewPrice Name.
     */
    VIEWPRICE_NAME("ViewPrice.Name"),

    /**
     * ViewPrice List.
     */
    VIEWPRICE_LIST("ViewPrice.List"),

    /**
     * ViewPrice DilutedPrice.
     */
    VIEWPRICE_DILUTEDPRICE("ViewPrice.DilutedPrice"),

    /**
     * Filter Name.
     */
    FILTER_NAME("Filter.Name"),

    /**
     * Filter Bucket.
     */
    FILTER_BUCKET("Filter.Bucket"),

    /**
     * Filter Attribute.
     */
    FILTER_ATTR("Filter.Attr");

    /**
     * The Bundle name.
     */
    private static final String BUNDLE_NAME = Analysis.class.getCanonicalName();

    /**
     * The Id.
     */
    private final String theKeyName;

    /**
     * Constructor.
     * @param pKeyName the key name
     */
    private MoneyWiseViewResource(final String pKeyName) {
        theKeyName = pKeyName;
    }

    @Override
    public String getKeyName() {
        return theKeyName;
    }

    @Override
    public String getNameSpace() {
        return "jMoneyWise.view";
    }

    @Override
    public String getBundleName() {
        return BUNDLE_NAME;
    }
}
