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

import com.github.javaparser.ast.body.CompactConstructorDeclaration;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.xanalysis.util.ThemisXAnalysisModifiers;
import net.sourceforge.joceanus.themis.xanalysis.util.ThemisXAnalysisParser;
import net.sourceforge.joceanus.themis.xanalysis.util.ThemisXAnalysisParser.ThemisXAnalysisParsedBody;
import net.sourceforge.joceanus.themis.xanalysis.util.ThemisXAnalysisParser.ThemisXAnalysisParsedStatement;
import net.sourceforge.joceanus.themis.xanalysis.util.ThemisXAnalysisParser.ThemisXAnalysisParsedType;

import java.util.List;

/**
 * Compact Constructor Declaration.
 */
public class ThemisXAnalysisBodyCompact
        implements ThemisXAnalysisParsedBody {
    /**
     * The declaration.
     */
    private final CompactConstructorDeclaration theDeclaration;

    /**
     * The name.
     */
    private final String theName;

    /**
     * The modifiers.
     */
    private final ThemisXAnalysisModifiers theModifiers;

    /**
     * The body.
     */
    private final ThemisXAnalysisParsedStatement theBody;

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
    public ThemisXAnalysisBodyCompact(final ThemisXAnalysisParser pParser,
                                      final CompactConstructorDeclaration pDeclaration) throws OceanusException {
        theDeclaration = pDeclaration;
        theBody = pParser.parseStatement(theDeclaration.getBody());
        theModifiers = new ThemisXAnalysisModifiers(theDeclaration.getModifiers());
        theName = theDeclaration.getNameAsString();
        theThrown = pParser.parseTypeList(theDeclaration.getThrownExceptions());
    }

    /**
     * Obtain the declaration.
     * @return the declaration
     */
    public CompactConstructorDeclaration getDeclaration() {
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
     * Obtain the modifiers.
     * @return the modifiers
     */
    public ThemisXAnalysisModifiers getModifiers() {
        return theModifiers;
    }

    /**
     * Obtain the body.
     * @return the body
     */
    public ThemisXAnalysisParsedStatement getBody() {
        return theBody;
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
