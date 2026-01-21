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
package io.github.tonywasher.joceanus.themis.xanalysis.parser.base;

/**
 * Modifier node.
 */
public interface ThemisXAnalysisModifierList {
    /**
     * Is Public?
     *
     * @return true/false
     */
    boolean isPublic();

    /**
     * Is Private?
     *
     * @return true/false
     */
    boolean isPrivate();

    /**
     * Is Protected?
     *
     * @return true/false
     */
    boolean isProtected();

    /**
     * Is Static?
     *
     * @return true/false
     */
    boolean isStatic();

    /**
     * Is Final?
     *
     * @return true/false
     */
    boolean isFinal();

    /**
     * Is Synchronized?
     *
     * @return true/false
     */
    boolean isSynchronized();

    /**
     * Is Volatile?
     *
     * @return true/false
     */
    boolean isVolatile();

    /**
     * Is Transient?
     *
     * @return true/false
     */
    boolean isTransient();

    /**
     * Is Transitive?
     *
     * @return true/false
     */
    boolean isTransitive();

    /**
     * Is Native?
     *
     * @return true/false
     */
    boolean isNative();

    /**
     * Is Abstract?
     *
     * @return true/false
     */
    boolean isAbstract();

    /**
     * Is Sealed?
     *
     * @return true/false
     */
    boolean isSealed();
}
