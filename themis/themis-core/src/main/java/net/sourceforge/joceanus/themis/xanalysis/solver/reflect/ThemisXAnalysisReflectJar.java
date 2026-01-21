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
package net.sourceforge.joceanus.themis.xanalysis.solver.reflect;

import com.github.javaparser.ast.body.BodyDeclaration;
import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.exc.ThemisDataException;
import net.sourceforge.joceanus.themis.xanalysis.parser.ThemisXAnalysisParser;
import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisChar;
import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisInstance.ThemisXAnalysisClassInstance;
import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisInstance.ThemisXAnalysisTypeInstance;
import net.sourceforge.joceanus.themis.xanalysis.parser.proj.ThemisXAnalysisMaven.ThemisXAnalysisMavenId;
import net.sourceforge.joceanus.themis.xanalysis.parser.proj.ThemisXAnalysisProject;
import net.sourceforge.joceanus.themis.xanalysis.parser.type.ThemisXAnalysisTypeClassInterface;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Solve external class references via Jars and reflection.
 */
public class ThemisXAnalysisReflectJar
        implements AutoCloseable {
    /**
     * The Project parser.
     */
    private final ThemisXAnalysisParser theProjectParser;

    /**
     * The JarClass Loader.
     */
    private final URLClassLoader theClassLoader;

    /**
     * The External Classes map.
     */
    private Map<String, ThemisXAnalysisReflectExternal> theExternalClasses;

    /**
     * Constructor.
     *
     * @param pParser the project parser.
     * @throws OceanusException on error
     */
    public ThemisXAnalysisReflectJar(final ThemisXAnalysisParser pParser) throws OceanusException {
        /* Create URL list and create URL Loader */
        theProjectParser = pParser;
        final URL[] myUrls = determineURLList(pParser.getProject());
        theClassLoader = URLClassLoader.newInstance(myUrls);
    }

    /**
     * Process external class list.
     *
     * @param pExternalClasses the external classes.
     * @throws OceanusException on error
     */
    public void processExternalClasses(final Map<String, ThemisXAnalysisReflectExternal> pExternalClasses) throws OceanusException {
        /* Extract the values as a separate list */
        theExternalClasses = pExternalClasses;
        final List<ThemisXAnalysisReflectExternal> myExternals = new ArrayList<>(pExternalClasses.values());

        /* Loop through the list */
        for (ThemisXAnalysisReflectExternal myClass : myExternals) {
            /* Load the external class */
            final Class<?> myLoaded = loadClass(myClass.getFullName());

            /* Create a resolved class based on the loaded class */
            final BodyDeclaration<?> myResolved = buildClass(myLoaded);
            final ThemisXAnalysisClassInstance myInstance = (ThemisXAnalysisClassInstance) theProjectParser.parseDeclaration(myResolved);
            myClass.setClassInstance(myInstance);

            /* Process ancestors */
            processAncestors(myInstance);
        }
    }

    /**
     * Process external class list.
     *
     * @param pExternal the external classes.
     * @throws OceanusException on error
     */
    private void processAncestors(final ThemisXAnalysisClassInstance pExternal) throws OceanusException {
        /* Process all the extended classes */
        for (ThemisXAnalysisTypeInstance myAncestor : pExternal.getExtends()) {
            /* Process the ancestor */
            processAncestor((ThemisXAnalysisTypeClassInterface) myAncestor);
        }

        /* Process all the implemented classes */
        for (ThemisXAnalysisTypeInstance myAncestor : pExternal.getImplements()) {
            /* Process the ancestor */
            processAncestor((ThemisXAnalysisTypeClassInterface) myAncestor);
        }
    }

    /**
     * Process an ancestor.
     *
     * @param pAncestor the ancestor.
     * @throws OceanusException on error
     */
    private void processAncestor(final ThemisXAnalysisTypeClassInterface pAncestor) throws OceanusException {
        /* Access the name of the class and convert to period format */
        final String myFullName = pAncestor.getFullName().replace(ThemisXAnalysisChar.DOLLAR, ThemisXAnalysisChar.PERIOD);

        /* See whether we have seen this class before */
        ThemisXAnalysisReflectExternal myExternal = theExternalClasses.get(myFullName);
        if (myExternal == null) {
            /* Load the external class */
            final Class<?> myLoaded = loadClass(myFullName);

            /* Create a resolved class based on the loaded class */
            final BodyDeclaration<?> myResolved = buildClass(myLoaded);
            final ThemisXAnalysisClassInstance myInstance = (ThemisXAnalysisClassInstance) theProjectParser.parseDeclaration(myResolved);
            myExternal = new ThemisXAnalysisReflectExternal(myInstance);
            theExternalClasses.put(myFullName, myExternal);

            /* Process ancestors */
            processAncestors(myInstance);

            /* else known class */
        } else {
            /* Add link */
            pAncestor.setClassInstance(myExternal);
        }
    }

    /**
     * determine the URL List.
     *
     * @param pProject the project
     * @return the URL List
     * @throws OceanusException on error
     */
    private URL[] determineURLList(final ThemisXAnalysisProject pProject) throws OceanusException {
        /* Create list of URLs for the dependencies */
        final List<URL> myList = new ArrayList<>();
        for (ThemisXAnalysisMavenId myId : pProject.getDependencies()) {
            /* Protect against exceptions */
            try {
                final File myJar = myId.getMavenJarPath();
                final URL myUrl = (new URI("jar:file:/" + myJar + "!/")).toURL();
                myList.add(myUrl);

                /* Handle exceptions */
            } catch (URISyntaxException
                     | MalformedURLException e) {
                throw new ThemisDataException("Failed to build URL", e);
            }
        }

        /* Convert list to array */
        return myList.toArray(new URL[0]);
    }

    /**
     * Load a class.
     *
     * @param pClassName the class name.
     * @return the loaded class
     * @throws OceanusException on error
     */
    private Class<?> loadClass(final String pClassName) throws OceanusException {
        /* Protect against exceptions */
        try {
            return theClassLoader.loadClass(pClassName);

            /* If we failed to find the class */
        } catch (ClassNotFoundException e) {
            /* Try again with the canonical name converted to a subClass */
            final String mySubClass = trySubClass(pClassName);
            if (mySubClass != null) {
                return loadClass(mySubClass);
            }

            /* Failed to find the class */
            throw new ThemisDataException("Failed to find class " + pClassName, e);
        }
    }

    /**
     * Change class name to make last class subClass.
     *
     * @param pClassName the class name
     * @return the subClass name or null
     */
    private static String trySubClass(final String pClassName) {
        /* Swap last period for dollar */
        final int myLastIndex = pClassName.lastIndexOf(ThemisXAnalysisChar.PERIOD);
        return myLastIndex != -1
                ? pClassName.substring(0, myLastIndex) + ThemisXAnalysisChar.DOLLAR + pClassName.substring(myLastIndex + 1)
                : null;
    }

    /**
     * build class.
     *
     * @param pSource the source class
     * @return the parsed class
     * @throws OceanusException on error
     */
    private BodyDeclaration<?> buildClass(final Class<?> pSource) throws OceanusException {
        /* Build the relevant class type */
        if (pSource.isAnnotation()) {
            return new ThemisXAnalysisReflectAnnotation(pSource);
        } else if (pSource.isEnum()) {
            return new ThemisXAnalysisReflectEnum(pSource);
        } else if (pSource.isRecord()) {
            return new ThemisXAnalysisReflectRecord(pSource);
        } else {
            return new ThemisXAnalysisReflectClass(pSource);
        }
    }

    @Override
    public void close() {
        try {
            if (theClassLoader != null) {
                theClassLoader.close();
            }
        } catch (IOException e) {
            /* Do nothing */
        }
    }
}
