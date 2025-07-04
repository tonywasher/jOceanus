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
package net.sourceforge.joceanus.themis.xanalysis.solver.reflect;

/**
 * Well-known java.lang classes.
 */
public enum ThemisXAnalysisReflectJavaLang {
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
     * Void.
     */
    VOID("Void"),

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
     * ArithmeticException.
     */
    ARITHMETICEXC("ArithmeticException"),

    /**
     * IllegalArgumentException.
     */
    ILLEGALARGEXC("IllegalArgumentException"),

    /**
     * IllegalStateException.
     */
    ILLEGALSTATEEXC("IllegalStateException"),

    /**
     * InterruptedException.
     */
    INTERRUPTEDEXC("InterruptedException"),

    /**
     * NumberFormatException.
     */
    NUMBERFORMATEXC("NumberFormatException"),

    /**
     * RunTimeException.
     */
    RUNTIMEXC("RuntimeException"),

    /**
     * UnsupportedOperationException.
     */
    UNSUPPORTEDEXC("UnsupportedOperationException"),

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
     * The class name.
     */
    private final String theName;

    /**
     * Constructor.
     * @param pName the className
     */
    ThemisXAnalysisReflectJavaLang(final String pName) {
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
