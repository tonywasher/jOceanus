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
package net.sourceforge.joceanus.themis.xanalysis.body;

import com.github.javaparser.ast.body.RecordDeclaration;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.xanalysis.util.ThemisXAnalysisModifiers;
import net.sourceforge.joceanus.themis.xanalysis.util.ThemisXAnalysisParser;
import net.sourceforge.joceanus.themis.xanalysis.util.ThemisXAnalysisParser.ThemisXAnalysisParsedBody;
import net.sourceforge.joceanus.themis.xanalysis.util.ThemisXAnalysisParser.ThemisXAnalysisParsedParam;
import net.sourceforge.joceanus.themis.xanalysis.util.ThemisXAnalysisParser.ThemisXAnalysisParsedType;

import java.util.List;

/**
 * Record Declaration.
 */
public class ThemisXAnalysisBodyRecord
        implements ThemisXAnalysisParsedBody {
    /**
     * The declaration.
     */
    private final RecordDeclaration theDeclaration;

    /**
     * The shortName.
     */
    private final String theShortName;

    /**
     * The fullName.
     */
    private final String theFullName;

    /**
     * The modifiers.
     */
    private final ThemisXAnalysisModifiers theModifiers;

    /**
     * The parameters.
     */
    private final List<ThemisXAnalysisParsedParam> theParameters;

    /**
     * The members.
     */
    private final List<ThemisXAnalysisParsedBody> theMembers;

    /**
     * The implements.
     */
    private final List<ThemisXAnalysisParsedType> theImplements;

    /**
     * The typeParameters.
     */
    private final List<ThemisXAnalysisParsedType> theTypeParameters;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pDeclaration the declaration
     * @throws OceanusException on error
     */
    public ThemisXAnalysisBodyRecord(final ThemisXAnalysisParser pParser,
                                     final RecordDeclaration pDeclaration) throws OceanusException {
        /* Store values */
        theDeclaration = pDeclaration;
        theShortName = theDeclaration.getNameAsString();
        theFullName = theDeclaration.getFullyQualifiedName().orElse(null);
        theModifiers = new ThemisXAnalysisModifiers(theDeclaration.getModifiers());
        theImplements = pParser.parseTypeList(theDeclaration.getImplementedTypes());
        theTypeParameters = pParser.parseTypeList(theDeclaration.getTypeParameters());
        theParameters = pParser.parseParamList(theDeclaration.getParameters());
        theMembers = pParser.parseMemberList(theDeclaration.getMembers());
    }

    /**
     * Obtain the declaration.
     * @return the declaration
     */
    public RecordDeclaration getDeclaration() {
        return theDeclaration;
    }

    /**
     * Obtain the short name.
     * @return the short name
     */
    public String getShortName() {
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
     * Obtain the parameters.
     * @return the parameters
     */
    public List<ThemisXAnalysisParsedParam> getParameters() {
        return theParameters;
    }

    /**
     * Obtain the members.
     * @return the members
     */
    public List<ThemisXAnalysisParsedBody> getMembers() {
        return theMembers;
    }

    /**
     * Obtain the implements types.
     * @return the implements
     */
    public List<ThemisXAnalysisParsedType> getImplements() {
        return theImplements;
    }

    /**
     * Obtain the type parameters.
     * @return the parameters
     */
    public List<ThemisXAnalysisParsedType> getTypeParameters() {
        return theTypeParameters;
    }

    @Override
    public String toString() {
        return theDeclaration.toString();
    }
}
