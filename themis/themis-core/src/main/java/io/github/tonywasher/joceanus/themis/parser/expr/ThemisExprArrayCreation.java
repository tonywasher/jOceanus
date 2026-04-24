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

import com.github.javaparser.ast.expr.ArrayCreationExpr;
import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisParserDef;

import java.util.List;

/**
 * Array Creation Expression Declaration.
 */
public class ThemisExprArrayCreation
        extends ThemisBaseExpression<ArrayCreationExpr> {
    /**
     * The created type.
     */
    private final ThemisTypeInstance theCreated;

    /**
     * The element type.
     */
    private final ThemisTypeInstance theType;

    /**
     * The levels.
     */
    private final List<ThemisNodeInstance> theLevels;

    /**
     * The initializer.
     */
    private final ThemisExpressionInstance theInit;

    /**
     * Constructor.
     *
     * @param pParser     the parser
     * @param pExpression the expression
     * @throws OceanusException on error
     */
    ThemisExprArrayCreation(final ThemisParserDef pParser,
                            final ArrayCreationExpr pExpression) throws OceanusException {
        super(pParser, pExpression);
        theCreated = pParser.parseType(pExpression.createdType());
        theType = pParser.parseType(pExpression.getElementType());
        theInit = pParser.parseExpression(pExpression.getInitializer().orElse(null));
        theLevels = pParser.parseNodeList(pExpression.getLevels());
    }

    /**
     * Obtain the created type.
     *
     * @return the type
     */
    public ThemisTypeInstance getCreatedType() {
        return theCreated;
    }

    /**
     * Obtain the element type.
     *
     * @return the type
     */
    public ThemisTypeInstance getElementType() {
        return theType;
    }

    /**
     * Obtain the levels.
     *
     * @return the levels
     */
    public List<ThemisNodeInstance> getLevels() {
        return theLevels;
    }

    /**
     * Obtain the initializer.
     *
     * @return the initializer
     */
    public ThemisExpressionInstance getInitializer() {
        return theInit;
    }
}
