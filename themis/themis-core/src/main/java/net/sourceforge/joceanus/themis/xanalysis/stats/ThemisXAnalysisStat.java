/* *****************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012-2026 Tony Washer
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
package net.sourceforge.joceanus.themis.xanalysis.stats;

/**
 * Statistics.
 */
public enum ThemisXAnalysisStat {
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
    NMTHD("Number of Methods"),

    /**
     * NATTR.
     */
    NATTR("Number of Attributes/Fields"),

    /**
     * NSTMT.
     */
    NSTMT("Number of Statements"),

    /**
     * NPARM.
     */
    NPARM("Number of Parameters"),

    /**
     * IOVARS.
     */
    IOVARS("Number of Input/Output Parameters"),

    /**
     * TLOC.
     */
    TPLOC("Total Physical Lines Of Code"),

    /**
     * TLLOC.
     */
    TLLOC("Total Logical Lines Of Code"),

    /**
     * TBLOC.
     */
    TBLOC("Total Blank Lines of Code"),

    /**
     * TCLOC.
     */
    TCLOC("Total Comment Lines of Code"),

    /**
     * TNFLS.
     */
    TNFLS("Total Number of Files"),

    /**
     * TNPKG.
     */
    TNPKG("Total Number of Packages"),

    /**
     * TNCLS.
     */
    TNCLS("Total Number of Classes/Enums/Records"),

    /**
     * TNINT.
     */
    TNINT("Total Number of Interfaces/Abstract Classes"),

    /**
     * TNMTHD.
     */
    TNMTHD("Total Number of Methods"),

    /**
     * TNATTR.
     */
    TNATTR("Total Number of Attributes"),

    /**
     * TNSTMT.
     */
    TNSTMT("Total Number of Statements");

    /**
     * The description.
     */
    private final String theDesc;

    /**
     * Constructor.
     * @param pDesc the description
     */
    ThemisXAnalysisStat(final String pDesc) {
        theDesc = pDesc;
    }

    /**
     * Obtain the description.
     * @return the description
     */
    public String getDesc() {
        return theDesc;
    }

    /**
     * Check that stat is supported.
     * @param pName the name of the stat
     * @return the supported stat (or null)
     */
    public static ThemisXAnalysisStat determineStat(final String pName) {
        for (ThemisXAnalysisStat myStat : values()) {
            if (pName.equals(myStat.name())) {
                return myStat;
            }
        }
        return null;
    }
}
