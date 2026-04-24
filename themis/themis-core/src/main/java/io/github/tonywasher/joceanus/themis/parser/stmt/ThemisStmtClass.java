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

import com.github.javaparser.ast.stmt.LocalClassDeclarationStmt;
import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisParserDef;

/**
 * Class Statement.
 */
public class ThemisStmtClass
        extends ThemisBaseStatement<LocalClassDeclarationStmt> {
    /**
     * The class declaration.
     */
    private final ThemisDeclarationInstance theClass;

    /**
     * Constructor.
     *
     * @param pParser    the parser
     * @param pStatement the statement
     * @throws OceanusException on error
     */
    ThemisStmtClass(final ThemisParserDef pParser,
                    final LocalClassDeclarationStmt pStatement) throws OceanusException {
        super(pParser, pStatement);
        theClass = pParser.parseDeclaration(pStatement.getClassDeclaration());
    }

    /**
     * Obtain the class.
     *
     * @return the class
     */
    public ThemisDeclarationInstance getBody() {
        return theClass;
    }
}
