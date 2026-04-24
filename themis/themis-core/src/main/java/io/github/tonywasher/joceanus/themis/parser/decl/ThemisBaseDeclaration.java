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
package io.github.tonywasher.joceanus.themis.parser.decl;

import com.github.javaparser.ast.body.BodyDeclaration;
import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisBaseInstance;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisInstance.ThemisDeclarationInstance;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisParserDef;

/**
 * Declaration Base Class.
 *
 * @param <T> the Declaration
 */
public abstract class ThemisBaseDeclaration<T extends BodyDeclaration<T>>
        extends ThemisBaseInstance<T>
        implements ThemisDeclarationInstance {
    /**
     * The declarationId.
     */
    private final ThemisDeclaration theId;

    /**
     * Constructor.
     *
     * @param pParser the parser
     * @param pDecl   the declaration
     * @throws OceanusException on error
     */
    ThemisBaseDeclaration(final ThemisParserDef pParser,
                          final T pDecl) throws OceanusException {
        super(pParser, pDecl);
        theId = ThemisDeclaration.determineDeclaration(pParser, pDecl);
    }

    @Override
    public ThemisDeclaration getId() {
        return theId;
    }
}
