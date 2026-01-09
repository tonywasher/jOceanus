/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012-2026 Tony Washer
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

import com.github.javaparser.ast.body.FieldDeclaration;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisModifierList;
import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisParserDef;

import java.util.List;

/**
 * Field Declaration.
 */
public class ThemisXAnalysisDeclField
        extends ThemisXAnalysisBaseDeclaration<FieldDeclaration> {
    /**
     * The modifiers.
     */
    private final ThemisXAnalysisModifierList theModifiers;

    /**
     * The variables.
     */
    private final List<ThemisXAnalysisNodeInstance> theVariables;

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
    ThemisXAnalysisDeclField(final ThemisXAnalysisParserDef pParser,
                             final FieldDeclaration pDeclaration) throws OceanusException {
        super(pParser, pDeclaration);
        theModifiers = pParser.parseModifierList(pDeclaration.getModifiers());
        theAnnotations = pParser.parseExprList(pDeclaration.getAnnotations());
        theVariables = pParser.parseNodeList(pDeclaration.getVariables());
    }

    /**
     * Obtain the modifiers.
     * @return the modifiers
     */
    public ThemisXAnalysisModifierList getModifiers() {
        return theModifiers;
    }

    /**
     * Obtain the variables.
     * @return the variables
     */
    public List<ThemisXAnalysisNodeInstance> getVariables() {
        return theVariables;
    }

    /**
     * Obtain the annotations.
     * @return the annotations
     */
    public List<ThemisXAnalysisExpressionInstance> getAnnotations() {
        return theAnnotations;
    }
}
