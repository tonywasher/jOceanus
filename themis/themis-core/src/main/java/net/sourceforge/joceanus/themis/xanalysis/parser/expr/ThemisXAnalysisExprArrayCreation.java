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
package net.sourceforge.joceanus.themis.xanalysis.parser.expr;

import com.github.javaparser.ast.expr.ArrayCreationExpr;
import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisParserDef;

import java.util.List;

/**
 * Array Creation Expression Declaration.
 */
public class ThemisXAnalysisExprArrayCreation
        extends ThemisXAnalysisBaseExpression<ArrayCreationExpr> {
    /**
     * The created type.
     */
    private final ThemisXAnalysisTypeInstance theCreated;

    /**
     * The element type.
     */
    private final ThemisXAnalysisTypeInstance theType;

    /**
     * The levels.
     */
    private final List<ThemisXAnalysisNodeInstance> theLevels;

    /**
     * The initializer.
     */
    private final ThemisXAnalysisExpressionInstance theInit;

    /**
     * Constructor.
     *
     * @param pParser     the parser
     * @param pExpression the expression
     * @throws OceanusException on error
     */
    ThemisXAnalysisExprArrayCreation(final ThemisXAnalysisParserDef pParser,
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
    public ThemisXAnalysisTypeInstance getCreatedType() {
        return theCreated;
    }

    /**
     * Obtain the element type.
     *
     * @return the type
     */
    public ThemisXAnalysisTypeInstance getElementType() {
        return theType;
    }

    /**
     * Obtain the levels.
     *
     * @return the levels
     */
    public List<ThemisXAnalysisNodeInstance> getLevels() {
        return theLevels;
    }

    /**
     * Obtain the initializer.
     *
     * @return the initializer
     */
    public ThemisXAnalysisExpressionInstance getInitializer() {
        return theInit;
    }
}
