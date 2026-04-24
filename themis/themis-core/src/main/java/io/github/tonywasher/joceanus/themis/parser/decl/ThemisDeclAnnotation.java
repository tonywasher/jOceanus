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

import com.github.javaparser.ast.body.AnnotationDeclaration;
import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisInstance.ThemisClassInstance;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisInstance.ThemisDeclarationInstance;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisModifierList;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisParserDef;
import io.github.tonywasher.joceanus.themis.parser.node.ThemisNodeSimpleName;

import java.util.List;

/**
 * Annotation Declaration.
 */
public class ThemisDeclAnnotation
        extends ThemisBaseDeclaration<AnnotationDeclaration>
        implements ThemisDeclarationInstance, ThemisClassInstance {
    /**
     * The Name.
     */
    private final String theName;

    /**
     * The fullName.
     */
    private final String theFullName;

    /**
     * The modifiers.
     */
    private final ThemisModifierList theModifiers;

    /**
     * The body.
     */
    private final List<ThemisDeclarationInstance> theBody;

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
    ThemisDeclAnnotation(final ThemisParserDef pParser,
                         final AnnotationDeclaration pDeclaration) throws OceanusException {
        super(pParser, pDeclaration);
        theName = ((ThemisNodeSimpleName) pParser.parseNode(pDeclaration.getName())).getName();
        theFullName = pDeclaration.getFullyQualifiedName().orElse(null);
        theModifiers = pParser.parseModifierList(pDeclaration.getModifiers());
        theAnnotations = pParser.parseExprList(pDeclaration.getAnnotations());

        /* Register the class */
        pParser.registerClass(this);

        /* Finally parse the underlying declarations */
        theBody = pParser.parseDeclarationList(pDeclaration.getMembers());
    }

    @Override
    public String getName() {
        return theName;
    }

    @Override
    public String getFullName() {
        return theFullName;
    }

    @Override
    public boolean isTopLevel() {
        return getNode().isTopLevelType();
    }

    @Override
    public ThemisModifierList getModifiers() {
        return theModifiers;
    }

    @Override
    public List<ThemisDeclarationInstance> getBody() {
        return theBody;
    }

    @Override
    public List<ThemisExpressionInstance> getAnnotations() {
        return theAnnotations;
    }

    @Override
    public String toString() {
        return theFullName;
    }
}
