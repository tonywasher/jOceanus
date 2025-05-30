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

import com.github.javaparser.ast.body.RecordDeclaration;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisParser;

import java.util.List;

/**
 * Record Declaration.
 */
public class ThemisXAnalysisDeclRecord
        extends ThemisXAnalysisBaseDeclaration<RecordDeclaration> {
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
    private final List<ThemisXAnalysisNodeInstance> theModifiers;

    /**
     * The parameters.
     */
    private final List<ThemisXAnalysisNodeInstance> theParameters;

    /**
     * The members.
     */
    private final List<ThemisXAnalysisDeclarationInstance> theMembers;

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
    ThemisXAnalysisDeclRecord(final ThemisXAnalysisParser pParser,
                              final RecordDeclaration pDeclaration) throws OceanusException {
        /* Store values */
        super(pDeclaration);
        theShortName = pParser.parseNode(pDeclaration.getName());
        theFullName = pDeclaration.getFullyQualifiedName().orElse(null);
        theModifiers = pParser.parseNodeList(pDeclaration.getModifiers());
        theImplements = pParser.parseTypeList(pDeclaration.getImplementedTypes());
        theTypeParameters = pParser.parseTypeList(pDeclaration.getTypeParameters());
        theParameters = pParser.parseNodeList(pDeclaration.getParameters());
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
    public List<ThemisXAnalysisNodeInstance> getModifiers() {
        return theModifiers;
    }

    /**
     * Obtain the parameters.
     * @return the parameters
     */
    public List<ThemisXAnalysisNodeInstance> getParameters() {
        return theParameters;
    }

    /**
     * Obtain the members.
     * @return the members
     */
    public List<ThemisXAnalysisDeclarationInstance> getMembers() {
        return theMembers;
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
