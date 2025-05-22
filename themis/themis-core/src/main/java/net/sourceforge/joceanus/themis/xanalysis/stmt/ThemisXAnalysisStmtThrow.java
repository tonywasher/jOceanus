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

import com.github.javaparser.ast.stmt.ThrowStmt;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.xanalysis.util.ThemisXAnalysisParser;
import net.sourceforge.joceanus.themis.xanalysis.util.ThemisXAnalysisParser.ThemisXAnalysisParsedExpr;
import net.sourceforge.joceanus.themis.xanalysis.util.ThemisXAnalysisParser.ThemisXAnalysisParsedStatement;

/**
 * Throw Statement.
 */
public class ThemisXAnalysisStmtThrow
        implements ThemisXAnalysisParsedStatement {
    /**
     * The contents.
     */
    private final ThrowStmt theStatement;

    /**
     * The thrown.
     */
    private final ThemisXAnalysisParsedExpr theThrown;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pStatement the statement
     * @throws OceanusException on error
     */
    public ThemisXAnalysisStmtThrow(final ThemisXAnalysisParser pParser,
                                    final ThrowStmt pStatement) throws OceanusException {
        theStatement = pStatement;
        theThrown = pParser.parseExpression(theStatement.getExpression());
    }

    /**
     * Obtain the statement.
     * @return the statement
     */
    public ThrowStmt getStatement() {
        return theStatement;
    }

    /**
     * Obtain the thrown.
     * @return the thrown
     */
    public ThemisXAnalysisParsedExpr getThrown() {
        return theThrown;
    }

    @Override
    public String toString() {
        return theStatement.toString();
    }
}
