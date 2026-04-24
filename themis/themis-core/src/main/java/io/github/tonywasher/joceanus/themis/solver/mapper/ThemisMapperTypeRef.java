/*
 * Themis: Java Project Framework
 * Copyright 2026. Tony Washer
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

package io.github.tonywasher.joceanus.themis.solver.mapper;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisInstance;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisInstance.ThemisTypeInstance;
import io.github.tonywasher.joceanus.themis.parser.decl.ThemisDeclEnum;
import io.github.tonywasher.joceanus.themis.parser.type.ThemisType;
import io.github.tonywasher.joceanus.themis.solver.reflect.ThemisReflectBaseUtils;

import java.util.List;
import java.util.function.Predicate;

/**
 * Class to wrap self reference.
 */
public class ThemisMapperTypeRef
        implements ThemisTypeInstance {
    /**
     * The parent.
     */
    private final ThemisDeclEnum theParent;

    /**
     * The node.
     */
    private final ClassOrInterfaceType theType;

    /**
     * Constructor.
     *
     * @param pEnum the enum to wrap.
     */
    ThemisMapperTypeRef(final ThemisDeclEnum pEnum) {
        /* Store parent */
        theParent = pEnum;

        /* Create the classOrInterface reference */
        theType = ThemisReflectBaseUtils.createTypeForName(pEnum.getFullName());
    }

    @Override
    public Node getNode() {
        return theType;
    }

    @Override
    public ThemisId getId() {
        return ThemisType.CLASSINTERFACE;
    }

    @Override
    public void registerChild(final ThemisInstance pChild) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ThemisInstance getParent() {
        return theParent;
    }

    @Override
    public List<ThemisInstance> getChildren() {
        return List.of();
    }

    @Override
    public List<ThemisInstance> discoverChildren(final Predicate<ThemisInstance> pTest) {
        return List.of();
    }

    @Override
    public void discoverNodes(final List<ThemisInstance> pList,
                              final Predicate<ThemisInstance> pTest) {
        /* NoOp */
    }

    /**
     * Obtain the class instance.
     *
     * @return the class instance
     */
    public ThemisClassInstance getClassInstance() {
        return theParent;
    }
}
