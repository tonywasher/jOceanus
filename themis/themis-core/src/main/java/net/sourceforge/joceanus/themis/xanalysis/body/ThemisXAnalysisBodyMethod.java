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

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.Type;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.xanalysis.util.ThemisXAnalysisModifiers;
import net.sourceforge.joceanus.themis.xanalysis.util.ThemisXAnalysisParser;
import net.sourceforge.joceanus.themis.xanalysis.util.ThemisXAnalysisParser.ThemisXAnalysisParsedBody;
import net.sourceforge.joceanus.themis.xanalysis.util.ThemisXAnalysisParser.ThemisXAnalysisParsedParam;
import net.sourceforge.joceanus.themis.xanalysis.util.ThemisXAnalysisParser.ThemisXAnalysisParsedStatement;
import net.sourceforge.joceanus.themis.xanalysis.util.ThemisXAnalysisParser.ThemisXAnalysisParsedType;

import java.util.List;

/**
 * Method Declaration.
 */
public class ThemisXAnalysisBodyMethod
        implements ThemisXAnalysisParsedBody {
    /**
     * The declaration.
     */
    private final MethodDeclaration theDeclaration;

    /**
     * The name.
     */
    private final String theName;

    /**
     * The type.
     */
    private final Type theType;

    /**
     * The modifiers.
     */
    private final ThemisXAnalysisModifiers theModifiers;

    /**
     * The parameters.
     */
    private final List<ThemisXAnalysisParsedParam> theParameters;

    /**
     * The body.
     */
    private final ThemisXAnalysisParsedStatement theBody;

    /**
     * The typeParameters.
     */
    private final List<ThemisXAnalysisParsedType> theTypeParameters;

    /**
     * The thrown exceptions.
     */
    private final List<ThemisXAnalysisParsedType> theThrown;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pDeclaration the declaration
     * @throws OceanusException on error
     */
    public ThemisXAnalysisBodyMethod(final ThemisXAnalysisParser pParser,
                                     final MethodDeclaration pDeclaration) throws OceanusException {
        theDeclaration = pDeclaration;
        theName = theDeclaration.getNameAsString();
        theType = theDeclaration.getType();
        theModifiers = new ThemisXAnalysisModifiers(theDeclaration.getModifiers());
        final Statement myBody = theDeclaration.getBody().orElse(null);
        theBody = myBody == null ? null : pParser.parseStatement(myBody);
        theTypeParameters = pParser.parseTypeList(theDeclaration.getTypeParameters());
        theThrown = pParser.parseTypeList(theDeclaration.getThrownExceptions());
        theParameters = pParser.parseParamList(theDeclaration.getParameters());
    }

    /**
     * Obtain the declaration.
     * @return the declaration
     */
    public MethodDeclaration getDeclaration() {
        return theDeclaration;
    }

    /**
     * Obtain the modifiers.
     * @return the modifiers
     */
    public ThemisXAnalysisModifiers getModifiers() {
        return theModifiers;
    }

    /**
     * Obtain the name.
     * @return the name
     */
    public String getName() {
        return theName;
    }

    /**
     * Obtain the type.
     * @return the type
     */
    public Type getType() {
        return theType;
    }

    /**
     * Obtain the parameters.
     * @return the parameters
     */
    public List<ThemisXAnalysisParsedParam> getParameters() {
        return theParameters;
    }

    /**
     * Obtain the body.
     * @return the body
     */
    public ThemisXAnalysisParsedStatement getBody() {
        return theBody;
    }

    /**
     * Obtain the type parameters.
     * @return the parameters
     */
    public List<ThemisXAnalysisParsedType> getTypeParameters() {
        return theTypeParameters;
    }

    /**
     * Obtain the thrown exceptions.
     * @return the exceptions
     */
    public List<ThemisXAnalysisParsedType> getThrown() {
        return theThrown;
    }

    @Override
    public String toString() {
        return theDeclaration.toString();
    }
}
