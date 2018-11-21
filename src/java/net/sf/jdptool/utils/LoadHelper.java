/*
 * Copyright 2006 Lu Ming
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package net.sf.jdptool.utils;

public final class LoadHelper {

    /**
     * <p>Return the <code>Class</code> object for the specified fully
     * qualified class name, from this web application's class loader.</p>
     *
     * @param className Fully qualified class name to be loaded
     * @return Class object
     * @throws ClassNotFoundException if the class cannot be found
     */
    public static Class loadClass(String className)
            throws ClassNotFoundException {
        return loadClass(className, null);
    }

    /**
     * <p>Return the <code>Class</code> object for the specified fully
     * qualified class name, from this web application's class loader.</p>
     *
     * @param className   Fully qualified class name to be loaded
     * @param classLoader The desired classloader to use
     * @return Class object
     * @throws ClassNotFoundException if the class cannot be found
     */
    public static Class loadClass(String className,
                                  ClassLoader classLoader)
        throws ClassNotFoundException {
        if (classLoader == null) {
            // Look up the class loader to be used
            classLoader = Thread.currentThread().getContextClassLoader();

            if (classLoader == null) {
                classLoader = LoadHelper.class.getClassLoader();
            }
        }

        // Attempt to load the specified class
        return (classLoader.loadClass(className));
    }

    /**
     * <p>Return a new instance of the specified fully qualified class name,
     * after loading the class from this web application's class loader. The
     * specified class <strong>MUST</strong> have a public zero-arguments
     * constructor.</p>
     *
     * @param className Fully qualified class name to use
     * @return new instance of class
     * @throws ClassNotFoundException if the class cannot be found
     * @throws IllegalAccessException if the class or its constructor is not
     *                                accessible
     * @throws InstantiationException if this class represents an abstract
     *                                class, an interface, an array class, a
     *                                primitive type, or void
     * @throws InstantiationException if this class has no zero-arguments
     *                                constructor
     */
    public static Object createInstance(String className)
        throws ClassNotFoundException, IllegalAccessException,
            InstantiationException {
        return createInstance(className, null);
    }

    /**
     * <p>Return a new instance of the specified fully qualified class name,
     * after loading the class from this web application's class loader. The
     * specified class <strong>MUST</strong> have a public zero-arguments
     * constructor.</p>
     *
     * @param className   Fully qualified class name to use
     * @param classLoader The desired classloader to use
     * @return new instance of class
     * @throws ClassNotFoundException if the class cannot be found
     * @throws IllegalAccessException if the class or its constructor is not
     *                                accessible
     * @throws InstantiationException if this class represents an abstract
     *                                class, an interface, an array class, a
     *                                primitive type, or void
     * @throws InstantiationException if this class has no zero-arguments
     *                                constructor
     */
    public static Object createInstance(String className,
        ClassLoader classLoader)
        throws ClassNotFoundException, IllegalAccessException,
            InstantiationException {
        return (loadClass(className, classLoader).newInstance());
    }
}
