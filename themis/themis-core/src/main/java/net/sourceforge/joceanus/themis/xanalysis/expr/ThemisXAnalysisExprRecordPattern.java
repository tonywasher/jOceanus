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
package net.sourceforge.joceanus.themis.xanalysis.expr;

import com.github.javaparser.ast.expr.RecordPatternExpr;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisInstance.ThemisXAnalysisExpressionInstance;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisModifiers;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisParser;

import java.util.List;

/**
 * recordPattern Expression Declaration.
 */
public class ThemisXAnalysisExprRecordPattern
        extends ThemisXAnalysisExprPattern<RecordPatternExpr> {
    /**
     * The modifiers
     */
    private final ThemisXAnalysisModifiers theModifiers;

    /**
     * The patterns
     */
    private final List<ThemisXAnalysisExpressionInstance> thePatterns;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pExpression the expression
     * @throws OceanusException on error
     */
    public ThemisXAnalysisExprRecordPattern(final ThemisXAnalysisParser pParser,
                                            final RecordPatternExpr pExpression) throws OceanusException {
        super(pParser, pExpression);
        theModifiers = new ThemisXAnalysisModifiers(pExpression.getModifiers());
        thePatterns = pParser.parseExprList(pExpression.getPatternList());
    }

    /**
     * Obtain the Modifiers.
     * @return the modifiers
     */
    public ThemisXAnalysisModifiers getModifiers() {
        return theModifiers;
    }

    /**
     * Obtain the Patterns.
     * @return the patterns
     */
    public List<ThemisXAnalysisExpressionInstance> getPatterns() {
        return thePatterns;
    }
}
