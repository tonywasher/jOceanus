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

import com.github.javaparser.ast.type.ReferenceType;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisParser;

/**
 * Reference Type Declaration.
 * @param <T> the type
 */
public abstract class ThemisXAnalysisTypeReference<T extends ReferenceType>
        extends ThemisXAnalysisBaseType<T> {
    /**
     * The name of the class.
     */
    private final String theName;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pType the type
     */
    ThemisXAnalysisTypeReference(final ThemisXAnalysisParser pParser,
                                 final T pType) {
        super(pType);
        theName = pType.asString();
    }

    /**
     * Obtain the name.
     * @return the name
     */
    public String getName() {
        return theName;
    }
}
