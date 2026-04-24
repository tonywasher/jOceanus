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
package io.github.tonywasher.joceanus.themis.parser.node;

import com.github.javaparser.ast.Modifier.Keyword;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisModifierList;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisInstance.ThemisNodeInstance;

import java.util.List;

/**
 * Modifier List.
 */
public class ThemisNodeModifierList
        implements ThemisModifierList {
    /**
     * The Modifier list.
     */
    private final List<ThemisNodeInstance> theModifiers;

    /**
     * Constructor.
     */
    public ThemisNodeModifierList() {
        this(null);
    }

    /**
     * Constructor.
     *
     * @param pModifiers the Modifiers.
     */
    public ThemisNodeModifierList(final List<ThemisNodeInstance> pModifiers) {
        theModifiers = pModifiers;
    }

    @Override
    public boolean isPublic() {
        return isPresent(Keyword.PUBLIC);
    }

    @Override
    public boolean isProtected() {
        return isPresent(Keyword.PROTECTED);
    }

    @Override
    public boolean isPrivate() {
        return isPresent(Keyword.PRIVATE);
    }

    @Override
    public boolean isStatic() {
        return isPresent(Keyword.STATIC);
    }

    @Override
    public boolean isFinal() {
        return isPresent(Keyword.FINAL);
    }

    @Override
    public boolean isSynchronized() {
        return isPresent(Keyword.SYNCHRONIZED);
    }

    @Override
    public boolean isVolatile() {
        return isPresent(Keyword.VOLATILE);
    }

    @Override
    public boolean isTransient() {
        return isPresent(Keyword.TRANSIENT);
    }

    @Override
    public boolean isTransitive() {
        return isPresent(Keyword.TRANSITIVE);
    }

    @Override
    public boolean isNative() {
        return isPresent(Keyword.NATIVE);
    }

    @Override
    public boolean isAbstract() {
        return isPresent(Keyword.ABSTRACT);
    }

    @Override
    public boolean isSealed() {
        return isPresent(Keyword.SEALED);
    }

    /**
     * Look for keyword in modifiers.
     *
     * @param pKeyword the keyword
     * @return true/false if present
     */
    private boolean isPresent(final Keyword pKeyword) {
        return theModifiers.stream().map(x -> (ThemisNodeModifier) x)
                .map(ThemisNodeModifier::getKeyword).anyMatch(pKeyword::equals);
    }
}
