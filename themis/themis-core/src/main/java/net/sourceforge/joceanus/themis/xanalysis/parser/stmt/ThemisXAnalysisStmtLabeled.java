/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012-2026 Tony Washer
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
package net.sourceforge.joceanus.themis.xanalysis.parser.stmt;

import com.github.javaparser.ast.stmt.LabeledStmt;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisParserDef;

/**
 * Labeled Statement.
 */
public class ThemisXAnalysisStmtLabeled
        extends ThemisXAnalysisBaseStatement<LabeledStmt> {
    /**
     * The label.
     */
    private final String theLabel;

    /**
     * The contents.
     */
    private final ThemisXAnalysisStatementInstance theLabeled;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pStatement the statement
     * @throws OceanusException on error
     */
    ThemisXAnalysisStmtLabeled(final ThemisXAnalysisParserDef pParser,
                               final LabeledStmt pStatement) throws OceanusException {
        super(pParser, pStatement);
        theLabel = pStatement.getLabel().asString();
        theLabeled = pParser.parseStatement(pStatement.getStatement());
    }

    /**
     * Obtain the label.
     * @return the label
     */
    public String getLabel() {
        return theLabel;
    }

    /**
     * Obtain the labeledStatement.
     * @return the statement
     */
    public ThemisXAnalysisStatementInstance getLabeled() {
        return theLabeled;
    }
}
