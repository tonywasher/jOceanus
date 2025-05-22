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

import com.github.javaparser.ast.body.AnnotationMemberDeclaration;
import net.sourceforge.joceanus.themis.xanalysis.util.ThemisXAnalysisParser;
import net.sourceforge.joceanus.themis.xanalysis.util.ThemisXAnalysisParser.ThemisXAnalysisParsedBody;

/**
 * Annotation Declaration.
 */
public class ThemisXAnalysisBodyAnnotationMember
        implements ThemisXAnalysisParsedBody {
    /**
     * The declaration.
     */
    private final AnnotationMemberDeclaration theDeclaration;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pDeclaration the declaration
     */
    public ThemisXAnalysisBodyAnnotationMember(final ThemisXAnalysisParser pParser,
                                               final AnnotationMemberDeclaration pDeclaration) {
        theDeclaration = pDeclaration;
    }

    /**
     * Obtain the declaration.
     * @return the declaration
     */
    public AnnotationMemberDeclaration getDeclaration() {
        return theDeclaration;
    }

    @Override
    public String toString() {
        return theDeclaration.toString();
    }
}
