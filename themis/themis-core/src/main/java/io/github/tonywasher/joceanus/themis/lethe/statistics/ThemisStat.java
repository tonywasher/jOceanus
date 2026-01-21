/*
 * Themis: Java Project Framework
 * Copyright 2012-2026. Tony Washer
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
 */
package io.github.tonywasher.joceanus.themis.lethe.statistics;

/**
 * Statistics.
 */
public enum ThemisStat {
    /**
     * LOC.
     */
    LOC("Lines Of Code"),

    /**
     * LLOC.
     */
    LLOC("Logical Lines Of Code"),

    /**
     * NCL.
     */
    NCL("Number of Classes"),

    /**
     * NEN.
     */
    NEN("Number of Enums"),

    /**
     * NIN.
     */
    NIN("Number of Interfaces"),

    /**
     * NM.
     */
    NM("Number of Methods"),

    /**
     * NA.
     */
    NA("Number of Attributes"),

    /**
     * NOS.
     */
    NOS("Number of Statements"),

    /**
     * DLOC.
     */
    DLOC("Documentation Lines of Code"),

    /**
     * CLOC.
     */
    CLOC("Comment Lines of Code"),

    /**
     * TLOC.
     */
    TLOC("Total Lines Of Code"),

    /**
     * TLLOC.
     */
    TLLOC("Total Logical Lines Of Code"),

    /**
     * TNFI.
     */
    TNFI("Total Number of Files"),

    /**
     * TNPKG.
     */
    TNPKG("Total Number of Packages"),

    /**
     * NCL.
     */
    TNCL("Total Number of Classes"),

    /**
     * NEN.
     */
    TNEN("Total Number of Enums"),

    /**
     * TNIN.
     */
    TNIN("Total Number of Interfaces"),

    /**
     * TNM.
     */
    TNM("Total Number of Methods"),

    /**
     * TNA.
     */
    TNA("Total Number of Attributes"),

    /**
     * TNOS.
     */
    TNOS("Total Number of Statements"),

    /**
     * TDLOC.
     */
    TDLOC("Total Documentation Lines of Code"),

    /**
     * TCLOC.
     */
    TCLOC("Total Comment Lines of Code");

    /**
     * The description.
     */
    private final String theDesc;

    /**
     * Constructor.
     *
     * @param pDesc the description
     */
    ThemisStat(final String pDesc) {
        theDesc = pDesc;
    }

    /**
     * Obtain the description.
     *
     * @return the description
     */
    public String getDesc() {
        return theDesc;
    }

    /**
     * Check that stat is supported.
     *
     * @param pName the name of the stat
     * @return the supported stat (or null)
     */
    public static ThemisStat determineStat(final String pName) {
        for (ThemisStat myStat : values()) {
            if (pName.equals(myStat.name())) {
                return myStat;
            }
        }
        return null;
    }
}
