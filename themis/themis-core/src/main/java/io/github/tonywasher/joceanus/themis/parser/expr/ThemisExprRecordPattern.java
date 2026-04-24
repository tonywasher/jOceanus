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

import com.github.javaparser.ast.expr.RecordPatternExpr;
import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisModifierList;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisParserDef;

import java.util.List;

/**
 * recordPattern Expression Declaration.
 */
public class ThemisExprRecordPattern
        extends ThemisExprPattern<RecordPatternExpr> {
    /**
     * The modifiers.
     */
    private final ThemisModifierList theModifiers;

    /**
     * The patterns.
     */
    private final List<ThemisExpressionInstance> thePatterns;

    /**
     * Constructor.
     *
     * @param pParser     the parser
     * @param pExpression the expression
     * @throws OceanusException on error
     */
    ThemisExprRecordPattern(final ThemisParserDef pParser,
                            final RecordPatternExpr pExpression) throws OceanusException {
        super(pParser, pExpression);
        theModifiers = pParser.parseModifierList(pExpression.getModifiers());
        thePatterns = pParser.parseExprList(pExpression.getPatternList());
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
     * Obtain the Patterns.
     *
     * @return the patterns
     */
    public List<ThemisExpressionInstance> getPatterns() {
        return thePatterns;
    }
}
