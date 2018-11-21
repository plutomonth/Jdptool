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
package net.sf.jdptool.config;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


public class JdpConfig extends BasicConfig {

    private ModuleConfig recorder;

    private LinkedList<ModuleConfig> runners = new LinkedList<ModuleConfig>();
    
    private LinkedList<ModuleConfig> plugins = new LinkedList<ModuleConfig>();
    
    /**
     * Return <code>ModuleConfig</code> of recorder
     * @return
     */
    public ModuleConfig getRecorder() {
        return recorder;
    }

    /**
     * Set <code>ModuleConfig</code> of recorder
     * @param snapshot
     */
    public void setRecorder(ModuleConfig recorder) {
        this.recorder = recorder;
    }
    
    /**
     * Add a <code>ModuleConfig</code> of runner into list
     * @param variable
     */
    public void addRunner(ModuleConfig runner) {
        runners.add(runner);
    }

    /**
     * Get the size of <code>ModuleConfig</code> of runners
     * @return the size of variable list
     */
    public int runnerSize() {
        return runners.size();
    }
    
    /**
     * Get all <code>ModuleConfig</code> of runner
     * @return
     */
    public List getRunners() {
        return Collections.unmodifiableList(runners);
    }

    /**
     * Returns the <code>ModuleConfig</code> of runners at the specified
     * position in this list.
     * @param index - index of the variable to return
     * @return the the <code>ModuleConfig</code> at the specified position
     */
    public ModuleConfig getRunner(int index) {
        return runners.get(index);
    }
    
    /**
     * Removes all of the <code>ModuleConfig</code> of runners from this list
     */
    public void clearRunner() {
        runners.clear();
    }
    
    /**
     * Add a <code>ModuleConfig</code> of plugin into list
     * @param variable
     */
    public void addPlugin(ModuleConfig plugin) {
        plugins.add(plugin);
    }

    /**
     * Get the size of <code>ModuleConfig</code> of plugins
     * @return the size of plugin list
     */
    public int pluginSize() {
        return plugins.size();
    }
    
    /**
     * Get all <code>ModuleConfig</code> of plugin
     * @return
     */
    public List getPlugins() {
        return Collections.unmodifiableList(plugins);
    }

    /**
     * Returns the <code>ModuleConfig</code> of runners at the specified
     * position in this list.
     * @param index - index of the variable to return
     * @return the the <code>ModuleConfig</code> at the specified position
     */
    public ModuleConfig getPlugin(int index) {
        return plugins.get(index);
    }
    
    /**
     * Removes all of the <code>ModuleConfig</code> of plugins from this list
     */
    public void clearPlugins() {
        plugins.clear();
    }
    
    
}
