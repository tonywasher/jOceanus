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
package net.sourceforge.joceanus.themis.xanalysis.node;

import com.github.javaparser.ast.PackageDeclaration;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisParser;

/**
 * Package.
 */
public class ThemisXAnalysisNodePackage
        extends ThemisXAnalysisBaseNode<PackageDeclaration> {
    /**
     * The Package.
     */
    private final ThemisXAnalysisNodeInstance thePackage;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pPackage the package
     * @throws OceanusException on error
     */
    ThemisXAnalysisNodePackage(final ThemisXAnalysisParser pParser,
                               final PackageDeclaration pPackage) throws OceanusException {
        super(pParser, pPackage);
        thePackage = pParser.parseNode(pPackage.getName());
        pParser.checkPackage(pPackage);
    }

    /**
     * Obtain the package.
     * @return the package
     */
    public ThemisXAnalysisNodeInstance getPackage() {
        return thePackage;
    }
}
