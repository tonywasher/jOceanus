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

package io.github.tonywasher.joceanus.themis.xanalysis.solver.mapper;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisInstance;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisInstance.ThemisXAnalysisTypeInstance;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.decl.ThemisXAnalysisDeclEnum;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.type.ThemisXAnalysisType;
import io.github.tonywasher.joceanus.themis.xanalysis.solver.reflect.ThemisXAnalysisReflectBaseUtils;

import java.util.List;
import java.util.function.Predicate;

/**
 * Class to wrap self reference.
 */
public class ThemisXAnalysisMapperTypeRef
        implements ThemisXAnalysisTypeInstance {
    /**
     * The parent.
     */
    private final ThemisXAnalysisDeclEnum theParent;

    /**
     * The node.
     */
    private final ClassOrInterfaceType theType;

    /**
     * Constructor.
     *
     * @param pEnum the enum to wrap.
     */
    ThemisXAnalysisMapperTypeRef(final ThemisXAnalysisDeclEnum pEnum) {
        /* Store parent */
        theParent = pEnum;

        /* Create the classOrInterface reference */
        theType = ThemisXAnalysisReflectBaseUtils.createTypeForName(pEnum.getFullName());
    }

    @Override
    public Node getNode() {
        return theType;
    }

    @Override
    public ThemisXAnalysisId getId() {
        return ThemisXAnalysisType.CLASSINTERFACE;
    }

    @Override
    public void registerChild(final ThemisXAnalysisInstance pChild) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ThemisXAnalysisInstance getParent() {
        return theParent;
    }

    @Override
    public List<ThemisXAnalysisInstance> getChildren() {
        return List.of();
    }

    @Override
    public List<ThemisXAnalysisInstance> discoverChildren(final Predicate<ThemisXAnalysisInstance> pTest) {
        return List.of();
    }

    @Override
    public void discoverNodes(final List<ThemisXAnalysisInstance> pList,
                              final Predicate<ThemisXAnalysisInstance> pTest) {
        /* NoOp */
    }

    /**
     * Obtain the class instance.
     *
     * @return the class instance
     */
    public ThemisXAnalysisClassInstance getClassInstance() {
        return theParent;
    }
}
