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

import com.github.javaparser.ast.body.Parameter;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.xanalysis.util.ThemisXAnalysisModifiers;
import net.sourceforge.joceanus.themis.xanalysis.util.ThemisXAnalysisParser;
import net.sourceforge.joceanus.themis.xanalysis.util.ThemisXAnalysisParser.ThemisXAnalysisParsedParam;
import net.sourceforge.joceanus.themis.xanalysis.util.ThemisXAnalysisParser.ThemisXAnalysisParsedType;

/**
 * Class Declaration.
 */
public class ThemisXAnalysisBodyParameter
        implements ThemisXAnalysisParsedParam {
    /**
     * The declaration.
     */
    private final Parameter theParameter;

    /**
     * The name.
     */
    private final String theName;

    /**
     * The type.
     */
    private final ThemisXAnalysisParsedType theType;

    /**
     * The modifiers.
     */
    private final ThemisXAnalysisModifiers theModifiers;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pParameter the parameter
     * @throws OceanusException on error
     */
    public ThemisXAnalysisBodyParameter(final ThemisXAnalysisParser pParser,
                                        final Parameter pParameter) throws OceanusException {
        theParameter = pParameter;
        theModifiers = new ThemisXAnalysisModifiers(theParameter.getModifiers());
        theName = theParameter.getNameAsString();
        theType = pParser.parseType(theParameter.getType());
    }

    /**
     * Obtain the parameter.
     * @return the parameter
     */
    public Parameter getParameter() {
        return theParameter;
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
    public ThemisXAnalysisParsedType getType() {
        return theType;
    }

    /**
     * Obtain the modifiers.
     * @return the modifiers
     */
    public ThemisXAnalysisModifiers getModifiers() {
        return theModifiers;
    }

    @Override
    public String toString() {
        return theParameter.toString();
    }
}
