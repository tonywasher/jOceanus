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
package net.sourceforge.joceanus.themis.xanalysis.body;

import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.xanalysis.util.ThemisXAnalysisParser;
import net.sourceforge.joceanus.themis.xanalysis.util.ThemisXAnalysisParser.ThemisXAnalysisParsedExpr;
import net.sourceforge.joceanus.themis.xanalysis.util.ThemisXAnalysisParser.ThemisXAnalysisParsedType;
import net.sourceforge.joceanus.themis.xanalysis.util.ThemisXAnalysisParser.ThemisXAnalysisParsedVar;
/**
 * Class Declaration.
 */
public class ThemisXAnalysisBodyVariable
        implements ThemisXAnalysisParsedVar {
    /**
     * The declaration.
     */
    private final VariableDeclarator theDeclaration;

    /**
     * The name.
     */
    private final String theName;

    /**
     * The type.
     */
    private final ThemisXAnalysisParsedType theType;

    /**
     * The initializer.
     */
    private final ThemisXAnalysisParsedExpr theInitializer;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pDeclaration the declaration
     * @throws OceanusException on error
     */
    public ThemisXAnalysisBodyVariable(final ThemisXAnalysisParser pParser,
                                       final VariableDeclarator pDeclaration) throws OceanusException {
        theDeclaration = pDeclaration;
        theName = theDeclaration.getNameAsString();
        theType = pParser.parseType(theDeclaration.getType());
        final Expression myInit = theDeclaration.getInitializer().orElse(null);
        theInitializer = myInit == null ? null : pParser.parseExpression(myInit);
    }

    /**
     * Obtain the declaration.
     * @return the declaration
     */
    public VariableDeclarator getDeclaration() {
        return theDeclaration;
    }

    /**
     * Obtain the name.
     * @return the name
     */
    public String getName() {
        return theName;
    }

    /**
     * Obtain the type.
     * @return the type
     */
    public ThemisXAnalysisParsedType getType() {
        return theType;
    }

    /**
     * Obtain the initializer.
     * @return the initializer
     */
    public ThemisXAnalysisParsedExpr getInitializer() {
        return theInitializer;
    }

    @Override
    public String toString() {
        return theDeclaration.toString();
    }
}
