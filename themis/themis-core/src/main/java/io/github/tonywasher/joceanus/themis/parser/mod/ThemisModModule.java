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

import com.github.javaparser.ast.modules.ModuleDeclaration;
import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisParserDef;

import java.util.List;

/**
 * Module.
 */
public class ThemisModModule
        extends ThemisBaseModule<ModuleDeclaration> {
    /**
     * The Name.
     */
    private final ThemisNodeInstance theName;

    /**
     * The Directives.
     */
    private final List<ThemisModuleInstance> theDirectives;

    /**
     * The annotations.
     */
    private final List<ThemisExpressionInstance> theAnnotations;

    /**
     * Constructor.
     *
     * @param pParser      the parser
     * @param pDeclaration the declaration
     * @throws OceanusException on error
     */
    ThemisModModule(final ThemisParserDef pParser,
                    final ModuleDeclaration pDeclaration) throws OceanusException {
        super(pParser, pDeclaration);
        theName = pParser.parseNode(pDeclaration.getName());
        theDirectives = pParser.parseModuleList(pDeclaration.getDirectives());
        theAnnotations = pParser.parseExprList(pDeclaration.getAnnotations());
    }

    /**
     * Obtain the name.
     *
     * @return the name
     */
    public ThemisNodeInstance getName() {
        return theName;
    }

    /**
     * Obtain the directives.
     *
     * @return the directives
     */
    public List<ThemisModuleInstance> getDirectives() {
        return theDirectives;
    }

    /**
     * Obtain the annotations.
     *
     * @return the annotations
     */
    public List<ThemisExpressionInstance> getAnnotations() {
        return theAnnotations;
    }
}
