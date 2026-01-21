/*
 * Themis: Java Project Framework
 * Copyright 2012-2026. Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package net.sourceforge.joceanus.themis.xanalysis.parser.node;

import com.github.javaparser.ast.ArrayCreationLevel;
import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisParserDef;

import java.util.List;

/**
 * Array Creation Level.
 */
public class ThemisXAnalysisNodeArrayLevel
        extends ThemisXAnalysisBaseNode<ArrayCreationLevel> {
    /**
     * The Level.
     */
    private final ThemisXAnalysisExpressionInstance theLevel;

    /**
     * The annotations.
     */
    private final List<ThemisXAnalysisExpressionInstance> theAnnotations;

    /**
     * Constructor.
     *
     * @param pParser the parser
     * @param pLevel  the level
     * @throws OceanusException on error
     */
    ThemisXAnalysisNodeArrayLevel(final ThemisXAnalysisParserDef pParser,
                                  final ArrayCreationLevel pLevel) throws OceanusException {
        super(pParser, pLevel);
        theLevel = pParser.parseExpression(pLevel.getDimension().orElse(null));
        theAnnotations = pParser.parseExprList(pLevel.getAnnotations());
    }

    /**
     * Obtain the value.
     *
     * @return the value
     */
    public ThemisXAnalysisExpressionInstance getValue() {
        return theLevel;
    }

    /**
     * Obtain the annotations.
     *
     * @return the annotations
     */
    public List<ThemisXAnalysisExpressionInstance> getAnnotations() {
        return theAnnotations;
    }
}
