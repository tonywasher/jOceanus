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
package net.sourceforge.joceanus.themis.xanalysis.parser.expr;

import com.github.javaparser.ast.expr.MethodReferenceExpr;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisParserDef;

import java.util.List;

/**
 * Method Reference Expression Declaration.
 */
public class ThemisXAnalysisExprMethodRef
        extends ThemisXAnalysisBaseExpression<MethodReferenceExpr> {
    /**
     * The name.
     */
    private final String theName;

    /**
     * The scope.
     */
    private final ThemisXAnalysisExpressionInstance theScope;

    /**
     * The typeArguments.
     */
    private final List<ThemisXAnalysisTypeInstance> theTypeArgs;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pExpression the expression
     * @throws OceanusException on error
     */
    ThemisXAnalysisExprMethodRef(final ThemisXAnalysisParserDef pParser,
                                 final MethodReferenceExpr pExpression) throws OceanusException {
        super(pParser, pExpression);
        theName = pExpression.getIdentifier();
        theScope = pParser.parseExpression(pExpression.getScope());
        theTypeArgs = pParser.parseTypeList(pExpression.getTypeArguments().orElse(null));
    }

    /**
     * Obtain the Name.
     * @return the name
     */
    public String getName() {
        return theName;
    }

    /**
     * Obtain the Scope.
     * @return the scope
     */
    public ThemisXAnalysisExpressionInstance getScope() {
        return theScope;
    }

    /**
     * Obtain the TypeArguments.
     * @return the typeArgs
     */
    public List<ThemisXAnalysisTypeInstance> getTypeArgs() {
        return theTypeArgs;
    }
}
