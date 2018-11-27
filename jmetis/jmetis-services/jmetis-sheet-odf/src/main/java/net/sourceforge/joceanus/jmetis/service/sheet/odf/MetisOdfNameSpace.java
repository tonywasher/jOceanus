/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012, 2018 Tony Washer
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
package net.sourceforge.joceanus.jmetis.service.sheet.odf;

/**
 * NameSpace definitions.
 */
public enum MetisOdfNameSpace {
    /**
     * Office.
     */
    OFFICE("office"),

    /**
     * Table.
     */
    TABLE("table"),

    /**
     * CalcExt.
     */
    CALCEXT("calcext"),

    /**
     * LoExt.
     */
    LOEXT("loext"),

    /**
     * Text.
     */
    TEXT("text"),

    /**
     * Number.
     */
    NUMBER("number"),

    /**
     * Format.
     */
    FORMAT("fo"),

    /**
     * Style.
     */
    STYLE("style");

    /**
     * The prefix.
     */
    private final String thePrefix;

    /**
     * Constructor.
     * @param pPrefix the prefix
     */
    MetisOdfNameSpace(final String pPrefix) {
        thePrefix = pPrefix;
    }

    /**
     * Obtain the prefix.
     * @return the prefix.
     */
    public String getPrefix() {
        return thePrefix;
    }

    /**
     * Element definition.
     */
    public interface MetisOdfItem {
        /**
         * Obtain the name.
         * @return the name
         */
        String getName();

        /**
         * Obtain the nameSpace.
         * @return the nameSpace
         */
        MetisOdfNameSpace getNameSpace();
    }
}
