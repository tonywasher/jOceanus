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
package net.sourceforge.joceanus.themis.xanalysis.expr;

import com.github.javaparser.ast.expr.MethodCallExpr;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisParser;

import java.util.List;

/**
 * MethodCall Expression Declaration.
 */
public class ThemisXAnalysisExprMethodCall
        extends ThemisXAnalysisBaseExpression<MethodCallExpr> {
    /**
     * The name.
     */
    private final ThemisXAnalysisNodeInstance theName;

    /**
     * The arguments.
     */
    private final List<ThemisXAnalysisExpressionInstance> theArguments;

    /**
     * The type.
     */
    private final List<ThemisXAnalysisTypeInstance> theTypeParams;

    /**
     * The pattern.
     */
    private final ThemisXAnalysisExpressionInstance theScope;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pExpression the expression
     * @throws OceanusException on error
     */
    ThemisXAnalysisExprMethodCall(final ThemisXAnalysisParser pParser,
                                  final MethodCallExpr pExpression) throws OceanusException {
        super(pParser, pExpression);
        theName = pParser.parseNode(pExpression.getName());
        theArguments = pParser.parseExprList(pExpression.getArguments());
        theTypeParams = pParser.parseTypeList(pExpression.getTypeArguments().orElse(null));
        theScope = pParser.parseExpression(pExpression.getScope().orElse(null));
    }

    /**
     * Obtain the name.
     * @return the name
     */
    public ThemisXAnalysisNodeInstance getName() {
        return theName;
    }

    /**
     * Obtain the arguments.
     * @return the arguments
     */
    public List<ThemisXAnalysisExpressionInstance> getArguments() {
        return theArguments;
    }

    /**
     * Obtain the typeParams.
     * @return the typeParams
     */
    public List<ThemisXAnalysisTypeInstance> getTypeParams() {
        return theTypeParams;
    }

    /**
     * Obtain the scope.
     * @return the scope
     */
    public ThemisXAnalysisExpressionInstance getScope() {
        return theScope;
    }
}
