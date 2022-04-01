/* *****************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012,2022 Tony Washer
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
 * SourceMeter Class/Interface/Enum.
 */
public class ThemisSMClass
        implements ThemisSMStatHolder {
    /**
     * Class Type.
     */
    private final ThemisSMClassType theType;

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
     * @param pType the class type
     * @param pParent the parent
     * @param pId the Id.
     * @param pName the name
     */
    ThemisSMClass(final ThemisSMStatHolder pParent,
                  final ThemisSMClassType pType,
                  final String pId,
                  final String pName) {
        /* Store parameters */
        theParent = pParent;
        theType = pType;
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

    /**
     * Obtain the classType.
     * @return the classType
     */
    public ThemisSMClassType getClassType() {
        return theType;
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

    /**
     * ClassType.
     */
    public enum ThemisSMClassType {
        /**
         * Class.
         */
        CLASS("Class"),

        /**
         * Interface.
         */
        INTERFACE("Interface"),

        /**
         * Enum.
         */
        ENUM("Enum"),

        /**
         * Annotation.
         */
        ANNOTATION("Annotation");

        /**
         * The name.
         */
        private final String theName;

        /**
         * Constructor.
         * @param pName the name
         */
        ThemisSMClassType(final String pName) {
            theName = pName;
        }

        @Override
        public String toString() {
            return theName;
        }
    }
}
