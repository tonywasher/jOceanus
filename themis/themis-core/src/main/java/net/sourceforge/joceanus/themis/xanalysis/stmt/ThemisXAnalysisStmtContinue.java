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
package net.sourceforge.joceanus.themis.xanalysis.stmt;

import com.github.javaparser.ast.stmt.ContinueStmt;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisParserDef;
import net.sourceforge.joceanus.themis.xanalysis.node.ThemisXAnalysisNodeSimpleName;

/**
 * Continue Statement.
 */
public class ThemisXAnalysisStmtContinue
        extends ThemisXAnalysisBaseStatement<ContinueStmt> {
    /**
     * The label.
     */
    private final String theLabel;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pStatement the statement
     * @throws OceanusException on error
     */
    ThemisXAnalysisStmtContinue(final ThemisXAnalysisParserDef pParser,
                                final ContinueStmt pStatement) throws OceanusException {
        super(pParser, pStatement);
        final ThemisXAnalysisNodeSimpleName myName = (ThemisXAnalysisNodeSimpleName) pParser.parseNode(pStatement.getLabel().orElse(null));
        theLabel = myName == null ? null : myName.getName();
    }

    /**
     * Obtain the label.
     * @return the label
     */
    public String getLabel() {
        return theLabel;
    }
}
