/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012,2020 Tony Washer
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
package net.sourceforge.joceanus.jthemis.analysis;

/**
 * Primitive.
 */
public enum ThemisAnalysisPrimitive
        implements ThemisAnalysisDataType {
    /**
     * byte.
     */
    BYTE("byte", "Byte"),

    /**
     * Char.
     */
    CHAR("char", "Character"),

    /**
     * Short.
     */
    SHORT("short", "Short"),

    /**
     * Int.
     */
    INT("int", "Integer"),

    /**
     * Long.
     */
    LONG("long", "Long"),

    /**
     * Boolean.
     */
    BOOLEAN("boolean", "Boolean"),

    /**
     * Float.
     */
    FLOAT("float", "Float"),

    /**
     * Double.
     */
    DOUBLE("double", "Double"),

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
     * Void.
     */
    VOID("void");

    /**
     * The modifier.
     */
    private final String thePrimitive;

    /**
     * The boxed.
     */
    private final String theBoxed;

    /**
     * Constructor.
     * @param pPrimitive the primitive
     */
    ThemisAnalysisPrimitive(final String pPrimitive) {
        this(pPrimitive, null);
    }

    /**
     * Constructor.
     * @param pPrimitive the primitive
     * @param pBoxed the boxed
     */
    ThemisAnalysisPrimitive(final String pPrimitive,
                            final String pBoxed) {
        thePrimitive = pPrimitive;
        theBoxed = pBoxed;
    }

    /**
     * Obtain the primitive.
     * @return the primitive
     */
    String getPrimitive() {
        return thePrimitive;
    }

    /**
     * Obtain the boxed.
     * @return the boxed
     */
    String getBoxed() {
        return theBoxed;
    }

    @Override
    public String toString() {
        return getPrimitive();
    }
}
