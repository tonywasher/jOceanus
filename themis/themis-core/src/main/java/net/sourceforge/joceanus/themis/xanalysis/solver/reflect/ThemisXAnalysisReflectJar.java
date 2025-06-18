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

import com.github.javaparser.ast.Node;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.themis.exc.ThemisDataException;
import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisChar;
import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisInstance.ThemisXAnalysisClassInstance;
import net.sourceforge.joceanus.themis.xanalysis.parser.proj.ThemisXAnalysisMaven.ThemisXAnalysisMavenId;
import net.sourceforge.joceanus.themis.xanalysis.parser.proj.ThemisXAnalysisProject;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
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
     * The JARClass Loader.
     */
    private final URLClassLoader theClassLoader;

    /**
     * Constructor.
     * @param pProject the underlying project.
     * @throws OceanusException on error
     */
    public ThemisXAnalysisReflectJar(final ThemisXAnalysisProject pProject) throws OceanusException {
        /* Create URL list and create URL Loader */
        final URL[] myUrls = determineURLList(pProject);
        theClassLoader = URLClassLoader.newInstance(myUrls);
    }

    /**
     * Process external class list.
     * @param pExternalClasses the external classes.
     * @throws OceanusException on error
     */
    public void processExternalClasses(final Map<String, ThemisXAnalysisClassInstance> pExternalClasses) throws OceanusException {
        /* Extract the values as a separate list */
        final List<ThemisXAnalysisClassInstance> myExternals = new ArrayList<>(pExternalClasses.values());
        for (ThemisXAnalysisClassInstance myClass : myExternals) {
            final Class<?> myLoaded = loadClass(myClass.getFullName());
            if (myLoaded != null) {
                final Node myResolved = buildClass(myLoaded);
                int i = 0;
            }
        }
    }

    /**
     * determine the URL List.
     * @param pProject the project
     * @throws OceanusException on error
     */
    private URL[] determineURLList(final ThemisXAnalysisProject pProject) throws OceanusException {
        /* Create list of URLs for the dependencies */
        final List<URL> myList = new ArrayList<>();
        for (ThemisXAnalysisMavenId myId : pProject.getDependencies()) {
            /* Protect against exceptions */
            try {
                final File myJar = myId.getMavenJarPath();
                final URL myUrl = new URL("jar:file:/" + myJar + "!/");
                myList.add(myUrl);

                /* Handle exceptions */
            } catch (MalformedURLException e) {
                throw new ThemisDataException("Failed to build URL", e);
            }
        }

        /* Convert list to array */
        return myList.toArray(new URL[0]);
    }

    /**
     * Load a class.
     * @param pClassName the class name.
     * @return the loaded class
     * @throws OceanusException on error
     */
    private Class<?> loadClass(final String pClassName) throws OceanusException {
        try {
            return theClassLoader.loadClass(pClassName);
        } catch (ClassNotFoundException e) {
            final String mySubClass = trySubClass(pClassName);
            if (mySubClass != null) {
                return loadClass(mySubClass);
            }
            System.out.println("Failed to find class " + pClassName);
            return null;
        }
    }

    /**
     * Change class name to make last class subClass
     * @param pClassName the class name
     * @return the subClass name or null
     */
    private static String trySubClass(final String pClassName) {
        /* Swap last period for dollar */
        int myLastIndex = pClassName.lastIndexOf(ThemisXAnalysisChar.PERIOD);
        return myLastIndex != -1
                ? pClassName.substring(0, myLastIndex) + ThemisXAnalysisChar.DOLLAR + pClassName.substring(myLastIndex + 1)
                : null;
    }

    /**
     * build class.
     * @param pSource the source class
     * @return the parsed class
     */
    private Node buildClass(final Class<?> pSource) {
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
