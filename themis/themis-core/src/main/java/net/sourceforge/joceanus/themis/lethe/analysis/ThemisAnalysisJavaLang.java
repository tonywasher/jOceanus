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
package net.sourceforge.joceanus.themis.lethe.analysis;

import net.sourceforge.joceanus.themis.lethe.analysis.ThemisAnalysisDataMap.ThemisAnalysisDataType;

/**
 * Well-known java.lang classes.
 */
public enum ThemisAnalysisJavaLang
        implements ThemisAnalysisDataType {
    /**
     * Object.
     */
    OBJECT("Object"),

    /**
     * Enum.
     */
    ENUM("Enum"),

    /**
     * String.
     */
    STRING("String"),

    /**
     * CharSequence.
     */
    CHARSEQ("CharSequence"),

    /**
     * StringBuilder.
     */
    STRINGBLDR("StringBuilder"),

    /**
     * StringBuffer.
     */
    STRINGBUFFER("StringBuffer"),

    /**
     * Comparable.
     */
    COMPARABLE("Comparable"),

    /**
     * Throwable.
     */
    THROWABLE("Throwable"),

    /**
     * Number.
     */
    NUMBER("Number"),

    /**
     * Thread.
     */
    THREAD("Thread"),

    /**
     * Runnable.
     */
    RUNNABLE("Runnable"),

    /**
     * Class.
     */
    CLASS("Class"),

    /**
     * Iterable.
     */
    ITERABLE("Iterable"),

    /**
     * Exception.
     */
    EXCEPT("Exception"),

    /**
     * RunTimeException.
     */
    RUNTIMEXCEPT("RuntimeException"),

    /**
     * StackTraceELement.
     */
    STACKTRACE("StackTraceElement"),

    /**
     * Override.
     */
    OVERRIDE("Override"),

    /**
     * SuppressWarnings.
     */
    SUPPRESSWARNINGS("SuppressWarnings"),

    /**
     * Deprecated.
     */
    DEPRECATED("Deprecated"),

    /**
     * FunctionalInterface.
     */
    FUNCTIONALINTERFACE("FunctionalInterface"),

    /**
     * AutoCloseable.
     */
    AUTOCLOSEABLE("AutoCloseable"),

    /**
     * IllegalArgumentException.
     */
    ILLEGALARGEXC("IllegalArgumentException"),

    /**
     * IllegalStateException.
     */
    ILLEGALSTATEECEPTION("IllegalStateException");

    /**
     * The class name.
     */
    private final String theName;

    /**
     * Constructor.
     * @param pName the className
     */
    ThemisAnalysisJavaLang(final String pName) {
        theName = pName;
    }

    /**
     * Obtain the className.
     * @return the name
     */
    String getName() {
        return theName;
    }

    @Override
    public String toString() {
        return getName();
    }
}
