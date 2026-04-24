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
package io.github.tonywasher.joceanus.themis.solver.reflect;

import com.github.javaparser.ast.body.BodyDeclaration;
import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.themis.exc.ThemisDataException;
import io.github.tonywasher.joceanus.themis.parser.ThemisParser;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisChar;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisInstance.ThemisClassInstance;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisInstance.ThemisTypeInstance;
import io.github.tonywasher.joceanus.themis.parser.proj.ThemisMavenId;
import io.github.tonywasher.joceanus.themis.parser.proj.ThemisProject;
import io.github.tonywasher.joceanus.themis.parser.type.ThemisTypeClassInterface;

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
public class ThemisReflectJar
        implements AutoCloseable {
    /**
     * The Project parser.
     */
    private final ThemisParser theProjectParser;

    /**
     * The JarClass Loader.
     */
    private final URLClassLoader theClassLoader;

    /**
     * The External Classes map.
     */
    private final Map<String, ThemisReflectExternal> theExternalClasses;

    /**
     * Constructor.
     *
     * @param pParser   the project parser.
     * @param pExternal classes the externalClass map.
     * @throws OceanusException on error
     */
    public ThemisReflectJar(final ThemisParser pParser,
                            final Map<String, ThemisReflectExternal> pExternal) throws OceanusException {
        /* Create URL list and create URL Loader */
        theProjectParser = pParser;
        theExternalClasses = pExternal;
        final URL[] myUrls = determineURLList(pParser.getProject());
        theClassLoader = URLClassLoader.newInstance(myUrls);
    }

    /**
     * Process external class list.
     *
     * @throws OceanusException on error
     */
    public void processExternalClasses() throws OceanusException {
        /* Extract the values as a separate list */
        final List<ThemisReflectExternal> myExternals = new ArrayList<>(theExternalClasses.values());

        /* Loop through the list */
        for (ThemisReflectExternal myClass : myExternals) {
            /* Load the external class */
            final Class<?> myLoaded = loadClass(myClass.getFullName());

            /* Create a resolved class based on the loaded class */
            final BodyDeclaration<?> myResolved = buildClass(myLoaded);
            final ThemisClassInstance myInstance = (ThemisClassInstance) theProjectParser.parseDeclaration(myResolved);
            myClass.setClassInstance(myInstance);

            /* Process ancestors */
            processAncestors(myClass);

            /* Process children */
            processChildren(myLoaded.getClasses());
        }
    }

    /**
     * Try a class as a java.lang class.
     *
     * @param pName the class name
     * @return the loaded class or null if it did not exist
     */
    public ThemisReflectExternal tryJavaLang(final String pName) {
        /* Create the javaLang name and try for the named class */
        final String myFullName = ThemisReflectExternal.JAVALANG + pName;
        return tryNamedClass(myFullName);
    }

    /**
     * Try a class as a java.lang class.
     *
     * @param pName the class name
     * @return the loaded class or null if it did not exist
     */
    public ThemisReflectExternal tryNamedClass(final String pName) {
        /* Protect against exceptions */
        try {
            /* Load the external class */
            final ThemisReflectExternal myExternal = theExternalClasses.get(pName);
            if (myExternal == null) {
                return loadNamedClass(pName);
            }
            return myExternal;

            /* If we failed, just return null */
        } catch (OceanusException e) {
            return null;
        }
    }

    /**
     * Load the class.
     *
     * @param pFullName the fullName of the class
     * @return the loaded class
     * @throws OceanusException on error
     */
    private ThemisReflectExternal loadNamedClass(final String pFullName) throws OceanusException {
        /* Load the external class */
        final Class<?> myLoaded = loadClass(pFullName);

        /* Create a resolved class based on the loaded class */
        final BodyDeclaration<?> myResolved = buildClass(myLoaded);
        final ThemisClassInstance myInstance = (ThemisClassInstance) theProjectParser.parseDeclaration(myResolved);
        final ThemisReflectExternal myExternal = new ThemisReflectExternal(myInstance);

        /* Store it as an external Class */
        theExternalClasses.put(pFullName, myExternal);

        /* Process ancestors */
        processAncestors(myExternal);

        /* Process children */
        processChildren(myLoaded.getClasses());

        /* return the class */
        return myExternal;
    }

    /**
     * Process external class list.
     *
     * @param pExternal the external classes.
     * @throws OceanusException on error
     */
    private void processAncestors(final ThemisReflectExternal pExternal) throws OceanusException {
        /* Access aunderlying instance */
        final ThemisClassInstance myInstance = pExternal.getClassInstance();

        /* Process all the extended classes */
        for (ThemisTypeInstance myAncestor : myInstance.getExtends()) {
            /* Process the ancestor */
            pExternal.addAncestor(processAncestor((ThemisTypeClassInterface) myAncestor));
        }

        /* Process all the implemented classes */
        for (ThemisTypeInstance myAncestor : myInstance.getImplements()) {
            /* Process the ancestor */
            pExternal.addAncestor(processAncestor((ThemisTypeClassInterface) myAncestor));
        }
    }

    /**
     * Process an ancestor.
     *
     * @param pAncestor the ancestor.
     * @return the resolved ancestor
     * @throws OceanusException on error
     */
    private ThemisReflectExternal processAncestor(final ThemisTypeClassInterface pAncestor) throws OceanusException {
        /* Access the name of the class and convert to period format */
        final String myFullName = pAncestor.getFullName().replace(ThemisChar.DOLLAR, ThemisChar.PERIOD);

        /* See whether we have seen this class before */
        ThemisReflectExternal myExternal = theExternalClasses.get(myFullName);
        if (myExternal == null) {
            /* Load the external class */
            myExternal = loadNamedClass(myFullName);
        }

        /* Add link */
        pAncestor.setClassInstance(myExternal);
        return myExternal;
    }

    /**
     * Process child classes.
     *
     * @param pChildren the children.
     * @throws OceanusException on error
     */
    private void processChildren(final Class<?>[] pChildren) throws OceanusException {
        /* Loop through the children */
        for (Class<?> myChild : pChildren) {
            /* Ignore private/anonymous and local classes */
            final boolean isLocalAnon = myChild.isAnonymousClass() || myChild.isLocalClass();
            final boolean isPrivate = ThemisReflectBaseUtils.isPrivate(myChild.getModifiers());
            if (!isPrivate && !isLocalAnon) {
                processChild(myChild);
            }
        }
    }

    /**
     * Process child class.
     *
     * @param pChild the child.
     * @throws OceanusException on error
     */
    private void processChild(final Class<?> pChild) throws OceanusException {
        /* Create a resolved class based on the loaded class */
        final BodyDeclaration<?> myResolved = buildClass(pChild);
        final ThemisClassInstance myInstance = (ThemisClassInstance) theProjectParser.parseDeclaration(myResolved);
        final ThemisReflectExternal myExternal = new ThemisReflectExternal(myInstance);

        /* Store it as an external Class */
        theExternalClasses.put(myExternal.getFullName(), myExternal);

        /* Process ancestors */
        processAncestors(myExternal);
    }

    /**
     * determine the URL List.
     *
     * @param pProject the project
     * @return the URL List
     * @throws OceanusException on error
     */
    private URL[] determineURLList(final ThemisProject pProject) throws OceanusException {
        /* Create list of URLs for the dependencies */
        final List<URL> myList = new ArrayList<>();
        for (ThemisMavenId myId : pProject.getDependencies()) {
            /* Protect against exceptions */
            try {
                final File myJar = myId.getMavenJarPath();
                final String myName = myJar.toString().replace("\\", "/");
                final URL myUrl = (new URI("jar:file:/" + myName + "!/")).toURL();
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
        final int myLastIndex = pClassName.lastIndexOf(ThemisChar.PERIOD);
        return myLastIndex != -1
                ? pClassName.substring(0, myLastIndex) + ThemisChar.DOLLAR + pClassName.substring(myLastIndex + 1)
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
            return new ThemisReflectAnnotation(pSource);
        } else if (pSource.isEnum()) {
            return new ThemisReflectEnum(pSource);
        } else if (pSource.isRecord()) {
            return new ThemisReflectRecord(pSource);
        } else {
            return new ThemisReflectClass(pSource);
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
