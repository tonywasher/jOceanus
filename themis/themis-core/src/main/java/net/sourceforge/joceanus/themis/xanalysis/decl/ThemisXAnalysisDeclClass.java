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
package net.sourceforge.joceanus.themis.xanalysis.decl;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisBaseDeclaration;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisInstance.ThemisXAnalysisNodeInstance;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisModifiers;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisParser;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisInstance.ThemisXAnalysisDeclarationInstance;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisInstance.ThemisXAnalysisTypeInstance;

import java.util.List;

/**
 * Class Declaration.
 */
public class ThemisXAnalysisDeclClass
        extends ThemisXAnalysisBaseDeclaration<ClassOrInterfaceDeclaration> {
    /**
     * The shortName.
     */
    private final ThemisXAnalysisNodeInstance theShortName;

    /**
     * The fullName.
     */
    private final String theFullName;

    /**
     * The modifiers.
     */
    private final ThemisXAnalysisModifiers theModifiers;

    /**
     * The members.
     */
    private final List<ThemisXAnalysisDeclarationInstance> theMembers;

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
     * Constructor.
     * @param pParser the parser
     * @param pDeclaration the declaration
     * @throws OceanusException on error
     */
    public ThemisXAnalysisDeclClass(final ThemisXAnalysisParser pParser,
                                    final ClassOrInterfaceDeclaration pDeclaration) throws OceanusException {
        /* Store values */
        super(pDeclaration);
        theShortName = pParser.parseNode(pDeclaration.getName());
        theFullName = pDeclaration.getFullyQualifiedName().orElse(null);
        theModifiers = new ThemisXAnalysisModifiers(pDeclaration.getModifiers());
        theImplements = pParser.parseTypeList(pDeclaration.getImplementedTypes());
        theExtends = pParser.parseTypeList(pDeclaration.getExtendedTypes());
        theTypeParameters = pParser.parseTypeList(pDeclaration.getTypeParameters());
        theMembers = pParser.parseDeclarationList(pDeclaration.getMembers());
    }

    /**
     * Obtain the short name.
     * @return the short name
     */
    public ThemisXAnalysisNodeInstance getShortName() {
        return theShortName;
    }

    /**
     * Obtain the fullName.
     * @return the fullName
     */
    public String getFullName() {
        return theFullName;
    }

    /**
     * Obtain the modifiers.
     * @return the modifiers
     */
    public ThemisXAnalysisModifiers getModifiers() {
        return theModifiers;
    }

    /**
     * Obtain the members.
     * @return the members
     */
    public List<ThemisXAnalysisDeclarationInstance> getMembers() {
        return theMembers;
    }

    /**
     * Obtain the extends types.
     * @return the extends
     */
    public List<ThemisXAnalysisTypeInstance> getExtends() {
        return theExtends;
    }

    /**
     * Obtain the implements types.
     * @return the implements
     */
    public List<ThemisXAnalysisTypeInstance> getImplements() {
        return theImplements;
    }

    /**
     * Obtain the type parameters.
     * @return the parameters
     */
    public List<ThemisXAnalysisTypeInstance> getTypeParameters() {
        return theTypeParameters;
    }
}
