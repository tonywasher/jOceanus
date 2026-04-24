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

import com.github.javaparser.ast.expr.FieldAccessExpr;
import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisParserDef;

import java.util.List;

/**
 * FieldAccess Expression Declaration.
 */
public class ThemisExprFieldAccess
        extends ThemisBaseExpression<FieldAccessExpr> {
    /**
     * The name.
     */
    private final ThemisNodeInstance theName;

    /**
     * The scope.
     */
    private final ThemisExpressionInstance theScope;

    /**
     * The expression.
     */
    private final List<ThemisTypeInstance> theTypes;


    /**
     * Constructor.
     *
     * @param pParser     the parser
     * @param pExpression the expression
     */
    ThemisExprFieldAccess(final ThemisParserDef pParser,
                          final FieldAccessExpr pExpression) throws OceanusException {
        super(pParser, pExpression);
        theName = pParser.parseNode(pExpression.getName());
        theScope = pParser.parseExpression(pExpression.getScope());
        theTypes = pParser.parseTypeList(pExpression.getTypeArguments().orElse(null));
    }


    /**
     * Obtain the Name.
     *
     * @return the name
     */
    public ThemisNodeInstance getName() {
        return theName;
    }

    /**
     * Obtain the scope.
     *
     * @return the scope
     */
    public ThemisExpressionInstance getScope() {
        return theScope;
    }

    /**
     * Obtain the types.
     *
     * @return the types
     */
    public List<ThemisTypeInstance> getTypes() {
        return theTypes;
    }
}
