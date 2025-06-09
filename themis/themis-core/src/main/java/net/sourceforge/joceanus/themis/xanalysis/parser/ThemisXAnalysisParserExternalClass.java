/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.themis.xanalysis.parser;

import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisChar;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisInstance.ThemisXAnalysisClassInstance;
import net.sourceforge.joceanus.themis.xanalysis.node.ThemisXAnalysisNodeImport;
import net.sourceforge.joceanus.themis.xanalysis.node.ThemisXAnalysisNodeName;

/**
 * External Class representation.
 */
public class ThemisXAnalysisParserExternalClass
        implements ThemisXAnalysisClassInstance {
    /**
     * The name of the class.
     */
    private final String theName;

    /**
     * The full name of the class.
     */
    private final String theFullName;

    /**
     * Constructor.
     * @param pImport the import definition
     */
    public ThemisXAnalysisParserExternalClass(final ThemisXAnalysisNodeImport pImport) {
        final ThemisXAnalysisNodeName myName = ((ThemisXAnalysisNodeName) pImport.getImport());
        theName = myName.getName();
        theFullName = myName.getQualifier().toString() + ThemisXAnalysisChar.PERIOD + theName;
    }

    /**
     * Constructor.
     * @param pPrefix the prefix
     * @param pName the name
     */
    ThemisXAnalysisParserExternalClass(final String pPrefix,
                                       final String pName) {
        theName = pName;
        theFullName = pPrefix + theName;
    }

    @Override
    public String getName() {
        return theName;
    }

    @Override
    public String getFullName() {
        return theFullName;
    }
}
