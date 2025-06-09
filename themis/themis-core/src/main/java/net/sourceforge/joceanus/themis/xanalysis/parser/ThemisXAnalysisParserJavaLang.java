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
package net.sourceforge.joceanus.themis.xanalysis.parser;

import net.sourceforge.joceanus.themis.xanalysis.base.ThemisXAnalysisInstance.ThemisXAnalysisClassInstance;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Well-known java.lang classes.
 */
public enum ThemisXAnalysisParserJavaLang {
    /**
     * Boolean.
     */
    BOOLEAN("Boolean"),

    /**
     * Byte.
     */
    BYTE("Byte"),

    /**
     * Character.
     */
    CHARACTER("Character"),

    /**
     * Class.
     */
    CLASS("Class"),

    /**
     * ClassLoader.
     */
    CLASSLOADER("ClassLoader"),

    /**
     * Double.
     */
    DOUBLE("Double"),

    /**
     * Enum.
     */
    ENUM("Enum"),

    /**
     * Float.
     */
    FLOAT("Float"),

    /**
     * Integer.
     */
    INTEGER("Integer"),

    /**
     * Long.
     */
    LONG("Long"),

    /**
     * Math.
     */
    MATH("Math"),

    /**
     * Number.
     */
    NUMBER("Number"),

    /**
     * Object.
     */
    OBJECT("Object"),

    /**
     * Package.
     */
    PACKAGE("Package"),

    /**
     * Process.
     */
    PROCESS("Process"),

    /**
     * ProcessBuilder.
     */
    PROCESSBUILDER("ProcessBuilder"),

    /**
     * Runtime.
     */
    RUNTIME("Object"),

    /**
     * SecurityManager.
     */
    SECURITYMANAGER("SecurityManager"),

    /**
     * Short.
     */
    SHORT("Short"),

    /**
     * StackTraceElement.
     */
    STACKTRACEELEMENT("StackTraceElement"),

    /**
     * String.
     */
    STRING("String"),

    /**
     * StringBuffer.
     */
    STRINGBUFFER("StringBuffer"),

    /**
     * StringBuilder.
     */
    STRINGBUILDER("StringBuilder"),

    /**
     * System.
     */
    SYSTEM("System"),

    /**
     * Thread.
     */
    THREAD("Thread"),

    /**
     * ThreadGroup.
     */
    THREADGROUP("ThreadGroup"),

    /**
     * ThreadLocal.
     */
    THREADLOCAL("ThreadLocal"),

    /**
     * Throwable.
     */
    THROWABLE("Throwable"),

    /**
     * Appendable.
     */
    APPENDABLE("Appendable"),

    /**
     * AutoCloseable.
     */
    AUTOCLOSEABLE("AutoCloseable"),

    /**
     * CharSequence.
     */
    CHARSEQ("CharSequence"),

    /**
     * Cloneable.
     */
    CLONEABLE("Cloneable"),

    /**
     * Comparable.
     */
    COMPARABLE("Comparable"),

    /**
     * Iterable.
     */
    ITERABLE("Iterable"),

    /**
     * Readable.
     */
    READABLE("Readable"),

    /**
     * Runnable.
     */
    RUNNABLE("Runnable"),

    /**
     * Exception.
     */
    EXCEPT("Exception"),

    /**
     * IllegalArgumentException.
     */
    ILLEGALARGEXC("IllegalArgumentException"),

    /**
     * IllegalStateException.
     */
    ILLEGALSTATEEXCEPTION("IllegalStateException"),

    /**
     * InterruptedException.
     */
    INTERRUPTED("InterruptedException"),

    /**
     * NumberFormatException.
     */
    NUMBERFORMATEXCEPTION("NumberFormatException"),

    /**
     * RunTimeException.
     */
    RUNTIMEXCEPT("RuntimeException"),

    /**
     * UnsupportedOperationException.
     */
    UNSUPPORTEDEXCEPT("UnsupportedOperationException"),

    /**
     * Deprecated.
     */
    DEPRECATED("Deprecated"),

    /**
     * FunctionalInterface.
     */
    FUNCTIONALINTERFACE("FunctionalInterface"),

    /**
     * Override.
     */
    OVERRIDE("Override"),

    /**
     * SuppressWarnings.
     */
    SUPPRESSWARNINGS("SuppressWarnings");

    /**
     * The javaLang prefix.
     */
    private static final String JAVALANG = "java.lang.";

    /**
     * The class name.
     */
    private final String theName;

    /**
     * Constructor.
     * @param pName the className
     */
    ThemisXAnalysisParserJavaLang(final String pName) {
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

    /**
     * Obtain map of java.lang classes.
     * @return the map
     */
    public static Map<String, ThemisXAnalysisClassInstance> getClassMap() {
        final Map<String, ThemisXAnalysisClassInstance> myMap = new LinkedHashMap<>();
        for (ThemisXAnalysisParserJavaLang myLang : values()) {
            myMap.put(myLang.getName(), new ThemisXAnalysisParserExternalClass(JAVALANG, myLang.getName()));
        }
        return myMap;
    }
}
