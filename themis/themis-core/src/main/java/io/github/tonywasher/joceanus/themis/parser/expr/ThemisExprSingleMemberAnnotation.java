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

import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisParserDef;

/**
 * SingleMemberAnnotation Expression Declaration.
 */
public class ThemisExprSingleMemberAnnotation
        extends ThemisBaseExpression<SingleMemberAnnotationExpr> {
    /**
     * The name of the annotation.
     */
    private final ThemisNodeInstance theName;

    /**
     * The member value.
     */
    private final ThemisExpressionInstance theValue;

    /**
     * Constructor.
     *
     * @param pParser     the parser
     * @param pExpression the expression
     * @throws OceanusException on error
     */
    ThemisExprSingleMemberAnnotation(final ThemisParserDef pParser,
                                     final SingleMemberAnnotationExpr pExpression) throws OceanusException {
        super(pParser, pExpression);
        theName = pParser.parseNode(pExpression.getName());
        theValue = pParser.parseExpression(pExpression.getMemberValue());
    }

    /**
     * Obtain the name.
     *
     * @return the name
     */
    public ThemisNodeInstance getName() {
        return theName;
    }

    /**
     * Obtain the value.
     *
     * @return the value
     */
    public ThemisExpressionInstance getValue() {
        return theValue;
    }
}
