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
package net.sourceforge.joceanus.themis.xanalysis.parser.stmt;

import com.github.javaparser.ast.stmt.ExplicitConstructorInvocationStmt;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisParserDef;

import java.util.List;

/**
 * Constructor Statement.
 */
public class ThemisXAnalysisStmtConstructor
        extends ThemisXAnalysisBaseStatement<ExplicitConstructorInvocationStmt> {
    /**
     * The expression.
     */
    private final ThemisXAnalysisExpressionInstance theExpression;

    /**
     * The arguments.
     */
    private final List<ThemisXAnalysisExpressionInstance> theArguments;

    /**
     * The type.
     */
    private final List<ThemisXAnalysisTypeInstance> theTypeParams;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pStatement the statement
     */
    ThemisXAnalysisStmtConstructor(final ThemisXAnalysisParserDef pParser,
                                   final ExplicitConstructorInvocationStmt pStatement) throws OceanusException {
        super(pParser, pStatement);
        theExpression = pParser.parseExpression(pStatement.getExpression().orElse(null));
        theArguments = pParser.parseExprList(pStatement.getArguments());
        theTypeParams = pParser.parseTypeList(pStatement.getTypeArguments().orElse(null));
    }

    /**
     * Obtain the expression.
     * @return the expression
     */
    public ThemisXAnalysisExpressionInstance getExpression() {
        return theExpression;
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
}
