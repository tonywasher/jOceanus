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

import com.github.javaparser.ast.expr.InstanceOfExpr;
import com.github.javaparser.ast.expr.SimpleName;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisParserDef;

/**
 * InstanceOf Expression Declaration.
 */
public class ThemisXAnalysisExprInstanceOf
        extends ThemisXAnalysisBaseExpression<InstanceOfExpr> {
    /**
     * The name.
     */
    private final String theName;

    /**
     * The value.
     */
    private final ThemisXAnalysisExpressionInstance theValue;

    /**
     * The type.
     */
    private final ThemisXAnalysisTypeInstance theType;

    /**
     * The pattern.
     */
    private final ThemisXAnalysisExpressionInstance thePattern;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pExpression the expression
     * @throws OceanusException on error
     */
    ThemisXAnalysisExprInstanceOf(final ThemisXAnalysisParserDef pParser,
                                  final InstanceOfExpr pExpression) throws OceanusException {
        super(pParser, pExpression);
        theName = pExpression.getName().map(SimpleName::asString).orElse(null);
        theValue = pParser.parseExpression(pExpression.getExpression());
        theType = pParser.parseType(pExpression.getType());
        thePattern = pParser.parseExpression(pExpression.getPattern().orElse(null));
    }

    /**
     * Obtain the name.
     * @return the name
     */
    public String getName() {
        return theName;
    }

    /**
     * Obtain the value.
     * @return the value
     */
    public ThemisXAnalysisExpressionInstance getValue() {
        return theValue;
    }

    /**
     * Obtain the type.
     * @return the type
     */
    public ThemisXAnalysisTypeInstance getType() {
        return theType;
    }

    /**
     * Obtain the pattern.
     * @return the pattern
     */
    public ThemisXAnalysisExpressionInstance getPattern() {
        return thePattern;
    }
}
