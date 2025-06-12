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
package net.sourceforge.joceanus.themis.xanalysis.parser.node;

import com.github.javaparser.ast.stmt.SwitchEntry;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisParserDef;

import java.util.List;

/**
 * Switch Case.
 */
public class ThemisXAnalysisNodeCase
        extends ThemisXAnalysisBaseNode<SwitchEntry> {
    /**
     * The guard.
     */
    private final ThemisXAnalysisExpressionInstance theGuard;

    /**
     * The labels.
     */
    private final List<ThemisXAnalysisExpressionInstance> theLabels;

    /**
     * The body.
     */
    private final List<ThemisXAnalysisStatementInstance> theBody;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pCase the case
     * @throws OceanusException on error
     */
    ThemisXAnalysisNodeCase(final ThemisXAnalysisParserDef pParser,
                            final SwitchEntry pCase) throws OceanusException {
        super(pParser, pCase);
        theGuard = pParser.parseExpression(pCase.getGuard().orElse(null));
        theLabels = pParser.parseExprList(pCase.getLabels());
        theBody = pParser.parseStatementList(pCase.getStatements());
    }

    /**
     * Obtain the guard.
     * @return the guard
     */
    public ThemisXAnalysisExpressionInstance getGuard() {
        return theGuard;
    }

    /**
     * Obtain the labels.
     * @return the labels
     */
    public List<ThemisXAnalysisExpressionInstance> getLabels() {
        return theLabels;
    }

    /**
     * Obtain the body.
     * @return the body
     */
    public List<ThemisXAnalysisStatementInstance> getBody() {
        return theBody;
    }
}
