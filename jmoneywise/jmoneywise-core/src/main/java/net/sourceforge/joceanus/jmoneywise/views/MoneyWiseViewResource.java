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
import net.sourceforge.joceanus.jtethys.resource.ResourceBuilder;
import net.sourceforge.joceanus.jtethys.resource.ResourceId;

/**
 * Resource IDs for jMoneyWise View Fields.
 */
public enum MoneyWiseViewResource implements ResourceId {
    /**
     * View Name.
     */
    VIEW_NAME("View.Name"),

    /**
     * AnalysisView Name.
     */
    ANALYSISVIEW_NAME("AnalysisView.Name"),

    /**
     * AnalysisView Base.
     */
    ANALYSISVIEW_BASE("AnalysisView.Base"),

    /**
     * AnalysisView UpdateSet.
     */
    ANALYSISVIEW_UPDATESET("AnalysisView.UpdateSet"),

    /**
     * SpotPrice Name.
     */
    SPOTPRICE_NAME("SpotPrice.Name"),

    /**
     * SpotRate Name.
     */
    SPOTRATE_NAME("SpotRate.Name"),

    /**
     * SpotEvent NextDate.
     */
    SPOTEVENT_NEXTDATE("SpotEvent.NextDate"),

    /**
     * SpotEvent PrevDate.
     */
    SPOTEVENT_PREVDATE("SpotEvent.PrevDate"),

    /**
     * SpotPrice PrevPrice.
     */
    SPOTPRICE_PREVPRICE("SpotPrice.PrevPrice"),

    /**
     * SpotRate PrevRate.
     */
    SPOTRATE_PREVRATE("SpotRate.PrevRate"),

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
    FILTER_ATTR("Filter.Attr"),

    /**
     * Filter Combine Groups.
     */
    FILTER_COMBINE("Filter.CombineGroups");

    /**
     * The Resource Builder.
     */
    private static final ResourceBuilder BUILDER = ResourceBuilder.getResourceBuilder(Analysis.class.getCanonicalName());

    /**
     * The Id.
     */
    private final String theKeyName;

    /**
     * The Value.
     */
    private String theValue;

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
    public String getValue() {
        /* If we have not initialised the value */
        if (theValue == null) {
            /* Derive the value */
            theValue = BUILDER.getValue(this);
        }

        /* return the value */
        return theValue;
    }
}
