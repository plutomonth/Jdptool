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
package net.sf.jdptool;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.sf.jdptool.config.ModuleConfig;
import net.sf.jdptool.filter.NotExcludesPredicate;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sun.jdi.ReferenceType;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;

public class SnapshotRunner implements Runner, Constants {

    private static Log log = LogFactory.getLog(SnapshotRunner.class);

    private ModuleConfig config;
    
    private VirtualMachine vm;
    
    private Recorder recorder;

    private int interval;
    
    private String[] excludes = defaultExcludes;

    /**
     * Construct a SnapshotRunner instance
     */
    public SnapshotRunner() {
    }

    //---------------------------------- Implements private methods
    /**
     * 
     */
    private void initInterval(){
        for (int i = 0; i < config.paramterSize(); i++) {
            if("interval".equals(config.getParameter(i).getName())) {
                interval = Integer.parseInt(config.getParameter(i).getValue());
                
                return;
            }
        }
    }

    /**
     * 
     */
    private void initExcludes(){
        for (int i = 0; i < config.paramterSize(); i++) {
            if("excludes".equals(config.getParameter(i).getName())) {
                excludes = config.getParameter(i).getValue().split(";");
                
                return;
            }
        }
    }

    
    //---------------------------------- Implements methods for Runnable
    
    /**
     * 
     */
    public void run() {
        try {
            while (Jdptool.vmRunning && this.interval > 0) {
                List<ThreadReference> threads = vm.allThreads();
                
                for (int i=0; i<threads.size(); i++) {
                    ThreadReference thread = threads.get(i);
                    StringBuffer sb = new StringBuffer("Thread:");
                    sb.append(thread.name());
                    sb.append(thread.toString());
                    recorder.write("snapshot", sb.toString());
                }
                
                Collection classes = CollectionUtils.select(vm.allClasses(),
                                                            new NotExcludesPredicate(excludes));
                for (Iterator it = classes.iterator(); it!=null && it.hasNext(); ) {
                    ReferenceType clazz = (ReferenceType)it.next();
                    StringBuffer sb = new StringBuffer("Class:");
                    sb.append(clazz.name());
                    sb.append("[" + clazz.classObject().referringObjects(0).size() + "]");
                    recorder.write("snapshot", sb.toString());
                }
                                
                Thread.sleep(interval);
            }
        } catch (InterruptedException ex) {
            log.error("Snapshot function error", ex);
        } 
    }

    //---------------------------------- Implements methods for Runner
    
    /**
     * 
     */
    public void destory() {
        recorder = null;
        vm = null;
        config = null;
    }

    /**
     * 
     */
    public void init(VirtualMachine vm, ModuleConfig config) {
        this.vm = vm;
        this.config = config;
        
        initInterval();
        initExcludes();
    }

    /**
     * 
     */
    public void setRecorder(Recorder recorder) {
        this.recorder = recorder;
    }
}
