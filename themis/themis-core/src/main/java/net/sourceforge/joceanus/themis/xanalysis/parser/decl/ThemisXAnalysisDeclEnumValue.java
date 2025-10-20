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

import com.github.javaparser.ast.body.EnumConstantDeclaration;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisParserDef;

import java.util.List;

/**
 * Class Declaration.
 */
public class ThemisXAnalysisDeclEnumValue
        extends ThemisXAnalysisBaseDeclaration<EnumConstantDeclaration> {
    /**
     * The name.
     */
    private final ThemisXAnalysisNodeInstance theName;

    /**
     * The arguments.
     */
    private final List<ThemisXAnalysisExpressionInstance> theArguments;

    /**
     * The class body.
     */
    private final List<ThemisXAnalysisDeclarationInstance> theBody;

    /**
     * The annotations.
     */
    private final List<ThemisXAnalysisExpressionInstance> theAnnotations;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pDeclaration the declaration
     */
    ThemisXAnalysisDeclEnumValue(final ThemisXAnalysisParserDef pParser,
                                 final EnumConstantDeclaration pDeclaration) throws OceanusException {
        super(pParser, pDeclaration);
        theName = pParser.parseNode(pDeclaration.getName());
        theAnnotations = pParser.parseExprList(pDeclaration.getAnnotations());
        theArguments = pParser.parseExprList(pDeclaration.getArguments());
        theBody = pParser.parseDeclarationList(pDeclaration.getClassBody());
    }

    /**
     * Obtain the name.
     * @return the name
     */
    public ThemisXAnalysisNodeInstance getName() {
        return theName;
    }

    /**
     * Obtain the arguments.
     * @return the arguments
     */
    public List<ThemisXAnalysisExpressionInstance> getArguments() {
        return theArguments;
    }

    /**
     * Obtain the body.
     * @return the body
     */
    public List<ThemisXAnalysisDeclarationInstance> getBody() {
        return theBody;
    }

    /**
     * Obtain the annotations.
     * @return the annotations
     */
    public List<ThemisXAnalysisExpressionInstance> getAnnotations() {
        return theAnnotations;
    }
}
