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
package net.sourceforge.joceanus.themis.xanalysis.base;

import com.github.javaparser.ast.type.Type;
import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisInstance.ThemisXAnalysisTypeInstance;

/**
 * Type Base Class.
 * @param <T> the Type
 */
public abstract class ThemisXAnalysisBaseType<T extends Type>
        implements ThemisXAnalysisTypeInstance {
    /**
     * The type.
     */
    private final T theType;

    /**
     * Constructor.
     * @param pType the type
     */
    protected ThemisXAnalysisBaseType(final T pType) {
        theType = pType;
    }

    /**
     * Obtain the type.
     * @return the type
     */
    public T getType() {
        return theType;
    }

    @Override
    public String toString() {
        return theType.toString();
    }
}
