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
package io.github.tonywasher.joceanus.themis.parser.mod;

import com.github.javaparser.ast.modules.ModuleRequiresDirective;
import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisModifierList;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisParserDef;

/**
 * Module Requires.
 */
public class ThemisModRequires
        extends ThemisBaseModule<ModuleRequiresDirective> {
    /**
     * The Required.
     */
    private final ThemisNodeInstance theRequired;

    /**
     * The modifiers.
     */
    private final ThemisModifierList theModifiers;

    /**
     * Constructor.
     *
     * @param pParser    the parser
     * @param pDirective the directive
     * @throws OceanusException on error
     */
    ThemisModRequires(final ThemisParserDef pParser,
                      final ModuleRequiresDirective pDirective) throws OceanusException {
        super(pParser, pDirective);
        theRequired = pParser.parseNode(pDirective.getName());
        theModifiers = pParser.parseModifierList(pDirective.getModifiers());
    }

    /**
     * Obtain the required.
     *
     * @return the required
     */
    public ThemisNodeInstance getName() {
        return theRequired;
    }

    /**
     * Obtain the modifiers.
     *
     * @return the modifiers
     */
    public ThemisModifierList getModifiers() {
        return theModifiers;
    }
}
