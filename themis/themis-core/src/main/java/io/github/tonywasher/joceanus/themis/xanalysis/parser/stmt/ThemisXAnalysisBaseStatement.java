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
package io.github.tonywasher.joceanus.themis.xanalysis.parser.stmt;

import com.github.javaparser.ast.stmt.Statement;
import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisBaseInstance;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisInstance.ThemisXAnalysisStatementInstance;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisParserDef;

/**
 * Statement Base Class.
 *
 * @param <T> the Statement
 */
public abstract class ThemisXAnalysisBaseStatement<T extends Statement>
        extends ThemisXAnalysisBaseInstance<T>
        implements ThemisXAnalysisStatementInstance {
    /**
     * The statementId.
     */
    private final ThemisXAnalysisStatement theId;

    /**
     * Constructor.
     *
     * @param pParser    the parser
     * @param pStatement the statement
     * @throws OceanusException on error
     */
    ThemisXAnalysisBaseStatement(final ThemisXAnalysisParserDef pParser,
                                 final T pStatement) throws OceanusException {
        super(pParser, pStatement);
        theId = ThemisXAnalysisStatement.determineStatement(pParser, pStatement);
    }

    @Override
    public ThemisXAnalysisStatement getId() {
        return theId;
    }
}
