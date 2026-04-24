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

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisInstance.ThemisClassInstance;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisInstance.ThemisDeclarationInstance;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisModifierList;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisParserDef;
import io.github.tonywasher.joceanus.themis.parser.node.ThemisNodeSimpleName;

import java.util.List;

/**
 * Class Declaration.
 */
public class ThemisDeclClassInterface
        extends ThemisBaseDeclaration<ClassOrInterfaceDeclaration>
        implements ThemisDeclarationInstance, ThemisClassInstance {
    /**
     * The shortName.
     */
    private final String theName;

    /**
     * The modifiers.
     */
    private final ThemisModifierList theModifiers;

    /**
     * The body.
     */
    private final List<ThemisDeclarationInstance> theBody;

    /**
     * The extends.
     */
    private final List<ThemisTypeInstance> theExtends;

    /**
     * The implements.
     */
    private final List<ThemisTypeInstance> theImplements;

    /**
     * The typeParameters.
     */
    private final List<ThemisTypeInstance> theTypeParameters;

    /**
     * The annotations.
     */
    private final List<ThemisExpressionInstance> theAnnotations;

    /**
     * Is this an anonymous class?
     */
    private final boolean isAnonClass;

    /**
     * The fullName.
     */
    private String theFullName;

    /**
     * Constructor.
     *
     * @param pParser      the parser
     * @param pDeclaration the declaration
     * @throws OceanusException on error
     */
    ThemisDeclClassInterface(final ThemisParserDef pParser,
                             final ClassOrInterfaceDeclaration pDeclaration) throws OceanusException {
        /* Store values */
        super(pParser, pDeclaration);
        theName = ((ThemisNodeSimpleName) pParser.parseNode(pDeclaration.getName())).getName();
        theModifiers = pParser.parseModifierList(pDeclaration.getModifiers());
        theAnnotations = pParser.parseExprList(pDeclaration.getAnnotations());
        theTypeParameters = pParser.parseTypeList(pDeclaration.getTypeParameters());
        theImplements = pParser.parseTypeList(pDeclaration.getImplementedTypes());
        theExtends = pParser.parseTypeList(pDeclaration.getExtendedTypes());

        /* Access the full name and determine whether this is an anonymous class */
        theFullName = pDeclaration.getFullyQualifiedName().orElse(null);
        isAnonClass = theFullName == null && !isLocalDeclaration();
        theFullName = pParser.registerClass(this);

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
    public boolean isInner() {
        return getNode().isInnerClass();
    }

    @Override
    public boolean isLocalDeclaration() {
        return getNode().isLocalClassDeclaration();
    }

    @Override
    public boolean isAnonClass() {
        return isAnonClass;
    }

    @Override
    public boolean isInterface() {
        return getNode().isInterface();
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
    public List<ThemisTypeInstance> getExtends() {
        return theExtends;
    }

    @Override
    public List<ThemisTypeInstance> getImplements() {
        return theImplements;
    }

    @Override
    public List<ThemisTypeInstance> getTypeParameters() {
        return theTypeParameters;
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
