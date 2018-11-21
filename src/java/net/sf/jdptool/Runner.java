/*
 * Copyright 2007 Lu Ming
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
package net.sf.jdptool;

import net.sf.jdptool.config.ModuleConfig;

import com.sun.jdi.VirtualMachine;

/**
 * <p>A <strong>Runner</strong> is a configuration wrapper for a
 * module-specific resource or service that needs to be notified about
 * application startup and application shutdown events (corresponding to when
 * the container calls <code>init</code> and <code>destroy</code> on the
 * corresponding instance). <code>Runner</code> objects can be configured
 * in the <code>jdp-config.xml</code> file, without the need to subclass
 * simply to perform application lifecycle activities.</p>
 *
 * <p>Implementations of this interface must supply a zero-argument
 * constructor for use by. Configuration can be accomplished by providing
 * standard JavaBeans property setter methods, which will all have been 
 * called before the <code>init()</code> method is invoked.</p>
 * 
 * @author Lu Ming
 */
public interface Runner extends Runnable {

    /**
     * <p>Receive notification that the specified module is being started
     * up.</p>
     * @param vm - The target JVM
     * @param config
     */
    public void init(VirtualMachine vm, ModuleConfig config);
    
    /**
     * <p>Receive notification that our owning module is being shut down.</p>
     */
    public void destory();
    
    /**
     * Set the recorder which charge of record debug information
     * @param recorder - the recorder
     */
    public void setRecorder(Recorder recorder);
}
