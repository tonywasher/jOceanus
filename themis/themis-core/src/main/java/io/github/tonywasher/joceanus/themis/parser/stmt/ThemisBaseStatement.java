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

import com.github.javaparser.ast.stmt.Statement;
import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisBaseInstance;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisInstance.ThemisStatementInstance;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisParserDef;

/**
 * Statement Base Class.
 *
 * @param <T> the Statement
 */
public abstract class ThemisBaseStatement<T extends Statement>
        extends ThemisBaseInstance<T>
        implements ThemisStatementInstance {
    /**
     * The statementId.
     */
    private final ThemisStatement theId;

    /**
     * Constructor.
     *
     * @param pParser    the parser
     * @param pStatement the statement
     * @throws OceanusException on error
     */
    ThemisBaseStatement(final ThemisParserDef pParser,
                        final T pStatement) throws OceanusException {
        super(pParser, pStatement);
        theId = ThemisStatement.determineStatement(pParser, pStatement);
    }

    @Override
    public ThemisStatement getId() {
        return theId;
    }
}
