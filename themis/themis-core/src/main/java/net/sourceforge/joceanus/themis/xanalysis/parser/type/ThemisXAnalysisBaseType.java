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
package net.sourceforge.joceanus.themis.xanalysis.parser.type;

import com.github.javaparser.ast.type.Type;
import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisBaseInstance;
import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisInstance.ThemisXAnalysisTypeInstance;
import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisParserDef;

/**
 * Type Base Class.
 *
 * @param <T> the Type
 */
public abstract class ThemisXAnalysisBaseType<T extends Type>
        extends ThemisXAnalysisBaseInstance<T>
        implements ThemisXAnalysisTypeInstance {
    /**
     * The typeId.
     */
    private final ThemisXAnalysisType theId;

    /**
     * Constructor.
     *
     * @param pParser the parser
     * @param pType   the type
     * @throws OceanusException on error
     */
    ThemisXAnalysisBaseType(final ThemisXAnalysisParserDef pParser,
                            final T pType) throws OceanusException {
        super(pParser, pType);
        theId = ThemisXAnalysisType.determineType(pParser, pType);
    }

    @Override
    public ThemisXAnalysisType getId() {
        return theId;
    }
}
