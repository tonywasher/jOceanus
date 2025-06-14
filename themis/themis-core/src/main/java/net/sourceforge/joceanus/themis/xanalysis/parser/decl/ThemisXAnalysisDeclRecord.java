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

import com.github.javaparser.ast.body.RecordDeclaration;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisInstance.ThemisXAnalysisClassInstance;
import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisInstance.ThemisXAnalysisDeclarationInstance;
import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisParserDef;
import net.sourceforge.joceanus.themis.xanalysis.parser.node.ThemisXAnalysisNodeSimpleName;

import java.util.List;

/**
 * Record Declaration.
 */
public class ThemisXAnalysisDeclRecord
        extends ThemisXAnalysisBaseDeclaration<RecordDeclaration>
        implements ThemisXAnalysisDeclarationInstance, ThemisXAnalysisClassInstance {
    /**
     * The Name.
     */
    private final String theName;

    /**
     * The modifiers.
     */
    private final List<ThemisXAnalysisNodeInstance> theModifiers;

    /**
     * The parameters.
     */
    private final List<ThemisXAnalysisNodeInstance> theParameters;

    /**
     * The body.
     */
    private final List<ThemisXAnalysisDeclarationInstance> theBody;

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
     * The fullName.
     */
    private String theFullName;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pDeclaration the declaration
     * @throws OceanusException on error
     */
    ThemisXAnalysisDeclRecord(final ThemisXAnalysisParserDef pParser,
                              final RecordDeclaration pDeclaration) throws OceanusException {
        /* Store values */
        super(pParser, pDeclaration);
        theName = ((ThemisXAnalysisNodeSimpleName) pParser.parseNode(pDeclaration.getName())).getName();
        theModifiers = pParser.parseNodeList(pDeclaration.getModifiers());
        theImplements = pParser.parseTypeList(pDeclaration.getImplementedTypes());
        theTypeParameters = pParser.parseTypeList(pDeclaration.getTypeParameters());
        theParameters = pParser.parseNodeList(pDeclaration.getParameters());
        theAnnotations = pParser.parseExprList(pDeclaration.getAnnotations());

        /* Access the intended full name and overwrite it with the correct name */
        theFullName = pDeclaration.getFullyQualifiedName().orElse(null);
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
    public boolean isLocalDeclaration() {
        return getNode().isLocalRecordDeclaration();
    }

    @Override
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

    @Override
    public List<ThemisXAnalysisDeclarationInstance> getBody() {
        return theBody;
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
