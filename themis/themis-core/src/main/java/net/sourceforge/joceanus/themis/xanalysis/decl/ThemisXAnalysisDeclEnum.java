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

import com.github.javaparser.ast.body.EnumDeclaration;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisInstance.ThemisXAnalysisClassInstance;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisInstance.ThemisXAnalysisDeclarationInstance;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisParser;
import net.sourceforge.joceanus.themis.xanalysis.node.ThemisXAnalysisNodeSimpleName;

import java.util.List;

/**
 * Enum Declaration.
 */
public class ThemisXAnalysisDeclEnum
        extends ThemisXAnalysisBaseDeclaration<EnumDeclaration>
        implements ThemisXAnalysisDeclarationInstance, ThemisXAnalysisClassInstance {
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
    private final List<ThemisXAnalysisNodeInstance> theModifiers;

    /**
     * The enumConstants.
     */
    private final List<ThemisXAnalysisDeclarationInstance> theValues;

    /**
     * The body.
     */
    private final List<ThemisXAnalysisDeclarationInstance> theBody;

    /**
     * The implements.
     */
    private final List<ThemisXAnalysisTypeInstance> theImplements;

    /**
     * The annotations.
     */
    private final List<ThemisXAnalysisExpressionInstance> theAnnotations;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pDeclaration the declaration
     * @throws OceanusException on error
     */
    ThemisXAnalysisDeclEnum(final ThemisXAnalysisParser pParser,
                            final EnumDeclaration pDeclaration) throws OceanusException {
        /* Store values */
        super(pParser, pDeclaration);
        theName = ((ThemisXAnalysisNodeSimpleName) pParser.parseNode(pDeclaration.getName())).getName();
        theFullName = pDeclaration.getFullyQualifiedName().orElse(null);
        theModifiers = pParser.parseNodeList(pDeclaration.getModifiers());
        theImplements = pParser.parseTypeList(pDeclaration.getImplementedTypes());
        theValues = pParser.parseDeclarationList(pDeclaration.getEntries());
        theBody = pParser.parseDeclarationList(pDeclaration.getMembers());
        theAnnotations = pParser.parseExprList(pDeclaration.getAnnotations());
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
    public List<ThemisXAnalysisNodeInstance> getModifiers() {
        return theModifiers;
    }

    /**
     * Obtain the values.
     * @return the values
     */
    public List<ThemisXAnalysisDeclarationInstance> getValues() {
        return theValues;
    }

    @Override
    public List<ThemisXAnalysisDeclarationInstance> getBody() {
        return theBody;
    }

    @Override
    public List<ThemisXAnalysisTypeInstance> getImplements() {
        return theImplements;
    }

    @Override
    public List<ThemisXAnalysisExpressionInstance> getAnnotations() {
        return theAnnotations;
    }
}
