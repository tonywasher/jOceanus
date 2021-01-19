/* *****************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012,2021 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.jthemis.sourcemeter;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * SourceMeter Method.
 */
public class ThemisSMMethod
        implements ThemisSMStatHolder {
    /**
     * The id.
     */
    private final String theId;

    /**
     * The class name.
     */
    private final String theName;

    /**
     * The children.
     */
    private final List<ThemisSMStatHolder> theChildren;

    /**
     * The stats.
     */
    private final Map<ThemisSMStat, Integer> theStats;

    /**
     * The parent.
     */
    private ThemisSMStatHolder theParent;

    /**
     * Constructor.
     * @param pParent the parent
     * @param pId the Id.
     * @param pName the name
     */
    ThemisSMMethod(final ThemisSMStatHolder pParent,
                   final String pId,
                   final String pName) {
        /* Store parameters */
        theParent = pParent;
        theId = pId;
        theName = pName;

        /* Create the list/maps */
        theChildren = new ArrayList<>();
        theStats = new EnumMap<>(ThemisSMStat.class);
    }

    @Override
    public String getId() {
        return theId;
    }

    @Override
    public String getName() {
        return theName;
    }

    @Override
    public Map<ThemisSMStat, Integer> getStatistics() {
        return theStats;
    }

    @Override
    public Iterator<ThemisSMStatHolder> childIterator() {
        return theChildren.iterator();
    }

    @Override
    public void registerChild(final ThemisSMStatHolder pChild) {
        theChildren.add(pChild);
    }

    @Override
    public void setParent(final ThemisSMStatHolder pParent) {
        theParent = pParent;
        theParent.registerChild(this);
    }

    @Override
    public void setStatistic(final ThemisSMStat pStat,
                             final Integer pValue) {
        theStats.put(pStat, pValue);
    }
}