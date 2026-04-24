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
package io.github.tonywasher.joceanus.themis.parser.type;

import com.github.javaparser.ast.type.Type;
import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisBaseInstance;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisInstance.ThemisTypeInstance;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisParserDef;

/**
 * Type Base Class.
 *
 * @param <T> the Type
 */
public abstract class ThemisBaseType<T extends Type>
        extends ThemisBaseInstance<T>
        implements ThemisTypeInstance {
    /**
     * The typeId.
     */
    private final ThemisType theId;

    /**
     * Constructor.
     *
     * @param pParser the parser
     * @param pType   the type
     * @throws OceanusException on error
     */
    ThemisBaseType(final ThemisParserDef pParser,
                   final T pType) throws OceanusException {
        super(pParser, pType);
        theId = ThemisType.determineType(pParser, pType);
    }

    @Override
    public ThemisType getId() {
        return theId;
    }
}
