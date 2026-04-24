/*
 * Themis: Java Project Framework
 * Copyright 2012-2026. Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package io.github.tonywasher.joceanus.themis.parser.expr;

import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisModifierList;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisParserDef;

import java.util.List;

/**
 * Variable Declaration Expression.
 */
public class ThemisExprVarDecl
        extends ThemisBaseExpression<VariableDeclarationExpr> {
    /**
     * The modifiers.
     */
    private final ThemisModifierList theModifiers;

    /**
     * The variables.
     */
    private final List<ThemisNodeInstance> theVariables;

    /**
     * The annotations.
     */
    private final List<ThemisExpressionInstance> theAnnotations;

    /**
     * Constructor.
     *
     * @param pParser     the parser
     * @param pExpression the expression
     * @throws OceanusException on error
     */
    ThemisExprVarDecl(final ThemisParserDef pParser,
                      final VariableDeclarationExpr pExpression) throws OceanusException {
        super(pParser, pExpression);
        theModifiers = pParser.parseModifierList(pExpression.getModifiers());
        theVariables = pParser.parseNodeList(pExpression.getVariables());
        theAnnotations = pParser.parseExprList(pExpression.getAnnotations());
    }

    /**
     * Obtain the Modifiers.
     *
     * @return the modifiers
     */
    public ThemisModifierList getModifiers() {
        return theModifiers;
    }

    /**
     * Obtain the Variables.
     *
     * @return the variables
     */
    public List<ThemisNodeInstance> getVariables() {
        return theVariables;
    }

    /**
     * Obtain the annotations.
     *
     * @return the annotations
     */
    public List<ThemisExpressionInstance> getAnnotations() {
        return theAnnotations;
    }
}
