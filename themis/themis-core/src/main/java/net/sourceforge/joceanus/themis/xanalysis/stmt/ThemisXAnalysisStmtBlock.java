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

import com.github.javaparser.ast.stmt.BlockStmt;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.xanalysis.util.ThemisXAnalysisParser;
import net.sourceforge.joceanus.themis.xanalysis.util.ThemisXAnalysisParser.ThemisXAnalysisParsedStatement;

import java.util.List;

/**
 * Block Statement.
 */
public class ThemisXAnalysisStmtBlock
        implements ThemisXAnalysisParsedStatement {
    /**
     * The contents.
     */
    private final BlockStmt theStatement;

    /**
     * The body.
     */
    private final List<ThemisXAnalysisParsedStatement> theBody;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pStatement the statement
     * @throws OceanusException on error
     */
    public ThemisXAnalysisStmtBlock(final ThemisXAnalysisParser pParser,
                                    final BlockStmt pStatement) throws OceanusException {
        theStatement = pStatement;
        theBody = pParser.parseStatementList(theStatement.getStatements());
    }

    /**
     * Obtain the statement.
     * @return the statement
     */
    public BlockStmt getStatement() {
        return theStatement;
    }

    /**
     * Obtain the body.
     * @return the body
     */
    public List<ThemisXAnalysisParsedStatement> getBody() {
        return theBody;
    }

    @Override
    public String toString() {
        return theStatement.toString();
    }
}
