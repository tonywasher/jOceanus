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
package net.sourceforge.joceanus.themis.xanalysis.type;

import com.github.javaparser.ast.type.TypeParameter;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisParserDef;
import net.sourceforge.joceanus.themis.xanalysis.node.ThemisXAnalysisNodeSimpleName;

import java.util.List;

/**
 * TypeParameter Declaration.
 */
public class ThemisXAnalysisTypeParameter
        extends ThemisXAnalysisBaseType<TypeParameter> {
    /**
     * The name of the parameter.
     */
    private final String theName;

    /**
     * The bounds.
     */
    private final List<ThemisXAnalysisTypeInstance> theBounds;

    /**
     * The annotations.
     */
    private final List<ThemisXAnalysisExpressionInstance> theAnnotations;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pType the type
     * @throws OceanusException on error
     */
    ThemisXAnalysisTypeParameter(final ThemisXAnalysisParserDef pParser,
                                 final TypeParameter pType) throws OceanusException {
        super(pParser, pType);
        theName = ((ThemisXAnalysisNodeSimpleName) pParser.parseNode(pType.getName())).getName();
        theBounds = pParser.parseTypeList(pType.getTypeBound());
        theAnnotations = pParser.parseExprList(pType.getAnnotations());
    }

    /**
     * Obtain the name.
     * @return the name
     */
    public String getName() {
        return theName;
    }

    /**
     * Obtain the bounds.
     * @return the bounds
     */
    public List<ThemisXAnalysisTypeInstance> getBounds() {
        return theBounds;
    }

    /**
     * Obtain the annotations.
     * @return the annotations
     */
    public List<ThemisXAnalysisExpressionInstance> getAnnotations() {
        return theAnnotations;
    }
}
