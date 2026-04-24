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
package io.github.tonywasher.joceanus.themis.parser.stmt;

import com.github.javaparser.ast.stmt.TryStmt;
import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisParserDef;

import java.util.List;

/**
 * Try Statement.
 */
public class ThemisStmtTry
        extends ThemisBaseStatement<TryStmt> {
    /**
     * The contents.
     */
    private final List<ThemisExpressionInstance> theResources;

    /**
     * The tryBlock.
     */
    private final ThemisStatementInstance theTry;

    /**
     * The catchClauses.
     */
    private final List<ThemisNodeInstance> theCatches;

    /**
     * The contents.
     */
    private final ThemisStatementInstance theFinally;

    /**
     * Constructor.
     *
     * @param pParser    the parser
     * @param pStatement the statement
     * @throws OceanusException on error
     */
    ThemisStmtTry(final ThemisParserDef pParser,
                  final TryStmt pStatement) throws OceanusException {
        super(pParser, pStatement);
        theResources = pParser.parseExprList(pStatement.getResources());
        theTry = pParser.parseStatement(pStatement.getTryBlock());
        theFinally = pParser.parseStatement(pStatement.getFinallyBlock().orElse(null));
        theCatches = pParser.parseNodeList(pStatement.getCatchClauses());
    }

    /**
     * Obtain the body.
     *
     * @return the body
     */
    public List<ThemisExpressionInstance> getResources() {
        return theResources;
    }

    /**
     * Obtain the try.
     *
     * @return the try
     */
    public ThemisStatementInstance getTry() {
        return theTry;
    }

    /**
     * Obtain the catches.
     *
     * @return the catches
     */
    public List<ThemisNodeInstance> getCatches() {
        return theCatches;
    }

    /**
     * Obtain the finally.
     *
     * @return the finally
     */
    public ThemisStatementInstance getFinally() {
        return theFinally;
    }
}
