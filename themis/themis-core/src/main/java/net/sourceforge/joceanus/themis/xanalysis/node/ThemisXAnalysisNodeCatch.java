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
package net.sourceforge.joceanus.themis.xanalysis.node;

import com.github.javaparser.ast.stmt.CatchClause;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisParserDef;

/**
 * Switch Case.
 */
public class ThemisXAnalysisNodeCatch
        extends ThemisXAnalysisBaseNode<CatchClause> {

    /**
     * The parameter.
     */
    private final ThemisXAnalysisNodeInstance theParameter;

    /**
     * The body.
     */
    private final ThemisXAnalysisStatementInstance theBody;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pCatch the catch
     * @throws OceanusException on error
     */
    ThemisXAnalysisNodeCatch(final ThemisXAnalysisParserDef pParser,
                             final CatchClause pCatch) throws OceanusException {
        super(pParser, pCatch);
        theBody = pParser.parseStatement(pCatch.getBody());
        theParameter = pParser.parseNode(pCatch.getParameter());
    }

    /**
     * Obtain the parameter.
     * @return the parameter
     */
    public ThemisXAnalysisNodeInstance getParameter() {
        return theParameter;
    }

    /**
     * Obtain the body.
     * @return the body
     */
    public ThemisXAnalysisStatementInstance getBody() {
        return theBody;
    }
}
