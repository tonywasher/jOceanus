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
package net.sourceforge.joceanus.themis.xanalysis.decl;

import com.github.javaparser.ast.body.InitializerDeclaration;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisParser;

/**
 * Initializer Declaration.
 */
public class ThemisXAnalysisDeclInitializer
        extends ThemisXAnalysisBaseDeclaration<InitializerDeclaration> {
    /**
     * Is this initializer static?
     */
    private final boolean isStatic;

    /**
     * The body.
     */
    private final ThemisXAnalysisStatementInstance theBody;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pDeclaration the declaration
     * @throws OceanusException on error
     */
    ThemisXAnalysisDeclInitializer(final ThemisXAnalysisParser pParser,
                                   final InitializerDeclaration pDeclaration) throws OceanusException {
        super(pDeclaration);
        isStatic = pDeclaration.isStatic();
        theBody = pParser.parseStatement(pDeclaration.getBody());
    }

    /**
     * Is the initializer static?
     * @return true/false
     */
    public boolean isStatic() {
        return isStatic;
    }

    /**
     * Obtain the body.
     * @return the body
     */
    public ThemisXAnalysisStatementInstance getBody() {
        return theBody;
    }
}
