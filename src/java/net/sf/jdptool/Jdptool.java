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

import java.util.HashMap;
import java.util.Iterator;

import net.sf.jdptool.config.ConfigConstants;
import net.sf.jdptool.config.JdpConfig;
import net.sf.jdptool.config.ModuleConfig;
import net.sf.jdptool.utils.LoadHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sun.jdi.VirtualMachine;

/**
 * <p>
 * The <code>Jdptool</code> is the kernal of jdptool
 * </p>
 * 
 * @author Lu Ming
 */
public class Jdptool implements ConfigConstants {
    
    protected static boolean vmRunning = true;

    private static Log log = LogFactory.getLog(Jdptool.class);
    
    private boolean initialized = false;

    private VirtualMachine vm;

    private JdpConfig jdpConfig;

    private JvmConnector connector;

    private Recorder recorder;

    private HashMap<String, Plugin> plugins = new HashMap<String, Plugin>();
    
    private HashMap<String, Thread> runners = new HashMap<String, Thread>();

    /**
     * Constructor Jdptool instance
     * 
     * @param connector
     * @param config
     */
    public Jdptool(JvmConnector connector, JdpConfig config) {
        this.connector = connector;
        this.jdpConfig = config;
    }

    /**
     * Initialize Jdptool
     *
     */
    public synchronized void init() throws Exception {
        vm = connector.open();
        
        initRecorder();
        initRunners();
        initPlugins();
        
        initialized = true;
    }
    
    /**
     * Initialize recorder used by runners
     * @throws Exception
     */
    private void initRecorder() throws Exception {
        ModuleConfig config = jdpConfig.getRecorder();
        recorder = (Recorder) LoadHelper.createInstance(config.getClassName());
        
        recorder.init(config);
    }
    
    /**
     * Initialize runners
     * @throws Exception
     */
    private void initRunners() throws Exception {
        for (int i = 0; i < jdpConfig.runnerSize(); i++) {
            ModuleConfig config = jdpConfig.getRunner(i);
            Runner runner = (Runner) LoadHelper.createInstance(config.getClassName());
            runner.init(vm, config);
            runner.setRecorder(recorder);
            Thread thread = new Thread(runner, config.getName());
            
            runners.put(config.getName(), thread);
        }
    }
    
    /**
     * Initialize plugins
     * @throws Exception
     */
    private void initPlugins() throws Exception {
        for (int i = 0; i < jdpConfig.pluginSize(); i++) {
            ModuleConfig config = jdpConfig.getPlugin(i);
            Plugin plugin = (Plugin) LoadHelper.createInstance(config.getClassName());
            plugin.init(config);
            
            plugins.put(config.getName(), plugin);
        }
    }
    
    /**
     * Start the main program
     */
    public void start() {
        
        try {
            if (!initialized) init();
        } catch (Exception e) {
            log.error("Init() error", e);
            System.exit(1);
        }
        
        for (Iterator it = runners.values().iterator(); it.hasNext();) {
            ((Thread)it.next()).start();
        }
        vm.resume();
        
        try {
            for (Iterator it = runners.values().iterator(); it.hasNext();) {
                ((Thread)it.next()).join();
            }
        } catch (InterruptedException exc) {
            // we don't interrupt
        } finally {
            close();
        }
    }
    
    /**
     * Close the main program
     *
     */
    public void close() {
        recorder.close();
        for (Iterator it = plugins.values().iterator(); it.hasNext();) {
            ((Plugin)it.next()).destory();
        }
        
    }
}
