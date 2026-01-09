/*******************************************************************************
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
package net.sourceforge.joceanus.themis.xanalysis.parser.expr;

import com.github.javaparser.ast.expr.FieldAccessExpr;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisParserDef;

import java.util.List;

/**
 * FieldAccess Expression Declaration.
 */
public class ThemisXAnalysisExprFieldAccess
        extends ThemisXAnalysisBaseExpression<FieldAccessExpr> {
    /**
     * The name.
     */
    private final ThemisXAnalysisNodeInstance theName;

    /**
     * The scope.
     */
    private final ThemisXAnalysisExpressionInstance theScope;

    /**
     * The expression.
     */
    private final List<ThemisXAnalysisTypeInstance> theTypes;


    /**
     * Constructor.
     * @param pParser the parser
     * @param pExpression the expression
     */
    ThemisXAnalysisExprFieldAccess(final ThemisXAnalysisParserDef pParser,
                                   final FieldAccessExpr pExpression) throws OceanusException {
        super(pParser, pExpression);
        theName = pParser.parseNode(pExpression.getName());
        theScope = pParser.parseExpression(pExpression.getScope());
        theTypes = pParser.parseTypeList(pExpression.getTypeArguments().orElse(null));
    }


    /**
     * Obtain the Name.
     * @return the name
     */
    public ThemisXAnalysisNodeInstance getName() {
        return theName;
    }

    /**
     * Obtain the scope.
     * @return the scope
     */
    public ThemisXAnalysisExpressionInstance getScope() {
        return theScope;
    }

    /**
     * Obtain the types.
     * @return the types
     */
    public List<ThemisXAnalysisTypeInstance> getTypes() {
        return theTypes;
    }
}
