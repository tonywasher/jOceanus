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
package io.github.tonywasher.joceanus.themis.stats;

/**
 * Statistics.
 */
public enum ThemisStat {
    /**
     * PLOC.
     */
    PLOC("Physical Lines Of Code"),

    /**
     * LLOC.
     */
    LLOC("Logical Lines Of Code"),

    /**
     * BLOC.
     */
    BLOC("Blank Lines of Code"),

    /**
     * CLOC.
     */
    CLOC("Comment Lines of Code"),

    /**
     * NCLS.
     */
    NCLS("Number of Classes/Enums/Records"),

    /**
     * NINT.
     */
    NINT("Number of Interfaces/Abstract Classes"),

    /**
     * NMTHD.
     */
    NMTHD("Number of Methods/Constructors"),

    /**
     * NATTR.
     */
    NATTR("Number of Attributes/Fields"),

    /**
     * NSTMT.
     */
    NSTMT("Number of Statements");

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

    /**
     * Is the stat an integer.
     *
     * @return true/false
     */
    public boolean isInteger() {
        return true;
    }
}
