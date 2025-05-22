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

import com.github.javaparser.ast.body.EnumConstantDeclaration;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.xanalysis.util.ThemisXAnalysisParser;
import net.sourceforge.joceanus.themis.xanalysis.util.ThemisXAnalysisParser.ThemisXAnalysisParsedBody;
import net.sourceforge.joceanus.themis.xanalysis.util.ThemisXAnalysisParser.ThemisXAnalysisParsedExpr;

import java.util.List;

/**
 * Class Declaration.
 */
public class ThemisXAnalysisBodyEnumValue
        implements ThemisXAnalysisParsedBody {
    /**
     * The declaration.
     */
    private final EnumConstantDeclaration theDeclaration;

    /**
     * The name.
     */
    private final String theName;

    /**
     * The arguments.
     */
    private final List<ThemisXAnalysisParsedExpr> theArguments;

    /**
     * The class body.
     */
    private final List<ThemisXAnalysisParsedBody> theBody;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pDeclaration the declaration
     */
    public ThemisXAnalysisBodyEnumValue(final ThemisXAnalysisParser pParser,
                                        final EnumConstantDeclaration pDeclaration) throws OceanusException {
        theDeclaration = pDeclaration;
        theName = theDeclaration.getNameAsString();
        theArguments = pParser.parseExprList(theDeclaration.getArguments());
        theBody = pParser.parseMemberList(theDeclaration.getClassBody());
    }

    /**
     * Obtain the declaration.
     * @return the declaration
     */
    public EnumConstantDeclaration getDeclaration() {
        return theDeclaration;
    }

    /**
     * Obtain the name.
     * @return the name
     */
    public String getName() {
        return theName;
    }

    /**
     * Obtain the arguments.
     * @return the arguments
     */
    public List<ThemisXAnalysisParsedExpr> getArguments() {
        return theArguments;
    }

    /**
     * Obtain the body.
     * @return the body
     */
    public List<ThemisXAnalysisParsedBody> getBody() {
        return theBody;
    }

    @Override
    public String toString() {
        return theDeclaration.toString();
    }
}
