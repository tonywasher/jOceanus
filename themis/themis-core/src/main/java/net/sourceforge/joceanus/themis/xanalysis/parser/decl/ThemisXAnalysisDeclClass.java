/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012,2025 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package net.sourceforge.joceanus.themis.xanalysis.parser.decl;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisInstance.ThemisXAnalysisClassInstance;
import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisInstance.ThemisXAnalysisDeclarationInstance;
import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisParserDef;
import net.sourceforge.joceanus.themis.xanalysis.parser.node.ThemisXAnalysisNodeSimpleName;

import java.util.List;

/**
 * Class Declaration.
 */
public class ThemisXAnalysisDeclClass
        extends ThemisXAnalysisBaseDeclaration<ClassOrInterfaceDeclaration>
        implements ThemisXAnalysisDeclarationInstance, ThemisXAnalysisClassInstance {
    /**
     * The shortName.
     */
    private final String theName;

    /**
     * The modifiers.
     */
    private final List<ThemisXAnalysisNodeInstance> theModifiers;

    /**
     * The body.
     */
    private final List<ThemisXAnalysisDeclarationInstance> theBody;

    /**
     * The extends.
     */
    private final List<ThemisXAnalysisTypeInstance> theExtends;

    /**
     * The implements.
     */
    private final List<ThemisXAnalysisTypeInstance> theImplements;

    /**
     * The typeParameters.
     */
    private final List<ThemisXAnalysisTypeInstance> theTypeParameters;

    /**
     * The annotations.
     */
    private final List<ThemisXAnalysisExpressionInstance> theAnnotations;

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
     * @param pParser the parser
     * @param pDeclaration the declaration
     * @throws OceanusException on error
     */
    ThemisXAnalysisDeclClass(final ThemisXAnalysisParserDef pParser,
                             final ClassOrInterfaceDeclaration pDeclaration) throws OceanusException {
        /* Store values */
        super(pParser, pDeclaration);
        theName = ((ThemisXAnalysisNodeSimpleName) pParser.parseNode(pDeclaration.getName())).getName();
        theModifiers = pParser.parseNodeList(pDeclaration.getModifiers());
        theImplements = pParser.parseTypeList(pDeclaration.getImplementedTypes());
        theExtends = pParser.parseTypeList(pDeclaration.getExtendedTypes());
        theTypeParameters = pParser.parseTypeList(pDeclaration.getTypeParameters());
        theAnnotations = pParser.parseExprList(pDeclaration.getAnnotations());

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
    public List<ThemisXAnalysisNodeInstance> getModifiers() {
        return theModifiers;
    }

    @Override
    public List<ThemisXAnalysisDeclarationInstance> getBody() {
        return theBody;
    }

    @Override
    public List<ThemisXAnalysisTypeInstance> getExtends() {
        return theExtends;
    }

    @Override
    public List<ThemisXAnalysisTypeInstance> getImplements() {
        return theImplements;
    }

    @Override
    public List<ThemisXAnalysisTypeInstance> getTypeParameters() {
        return theTypeParameters;
    }

    @Override
    public List<ThemisXAnalysisExpressionInstance> getAnnotations() {
        return theAnnotations;
    }

    @Override
    public String toString() {
        return theFullName;
    }
}
