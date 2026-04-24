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

import com.github.javaparser.ast.expr.ArrayAccessExpr;
import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisParserDef;

/**
 * Array Access Expression Declaration.
 */
public class ThemisExprArrayAccess
        extends ThemisBaseExpression<ArrayAccessExpr> {
    /**
     * The name.
     */
    private final ThemisExpressionInstance theName;

    /**
     * The index.
     */
    private final ThemisExpressionInstance theIndex;

    /**
     * Constructor.
     *
     * @param pParser     the parser
     * @param pExpression the expression
     * @throws OceanusException on error
     */
    ThemisExprArrayAccess(final ThemisParserDef pParser,
                          final ArrayAccessExpr pExpression) throws OceanusException {
        super(pParser, pExpression);
        theName = pParser.parseExpression(pExpression.getName());
        theIndex = pParser.parseExpression(pExpression.getIndex());
    }

    /**
     * Obtain the name.
     *
     * @return the name
     */
    public ThemisExpressionInstance getName() {
        return theName;
    }

    /**
     * Obtain the index.
     *
     * @return the index
     */
    public ThemisExpressionInstance getIndex() {
        return theIndex;
    }
}
