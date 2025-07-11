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
package net.sourceforge.joceanus.themis.xanalysis.parser.expr;

import com.github.javaparser.ast.expr.MarkerAnnotationExpr;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisParserDef;

/**
 * Marker Annotation Expression Declaration.
 */
public class ThemisXAnalysisExprMarkerAnnotation
        extends ThemisXAnalysisBaseExpression<MarkerAnnotationExpr> {
    /**
     * The name of the annotation.
     */
    private final ThemisXAnalysisNodeInstance theName;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pExpression the expression
     * @throws OceanusException on error
     */
    ThemisXAnalysisExprMarkerAnnotation(final ThemisXAnalysisParserDef pParser,
                                        final MarkerAnnotationExpr pExpression) throws OceanusException {
        super(pParser, pExpression);
        theName = pParser.parseNode(pExpression.getName());
    }

    /**
     * Obtain the name.
     * @return the name
     */
    public ThemisXAnalysisNodeInstance getName() {
        return theName;
    }
}
