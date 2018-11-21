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

/**
 * <p>This config would be used by as following tag:</p>
 * <ul>
 *      <li>runner tag</li>
 *      <li>plugin tag</li>
 *      <li>recorder tag</li>
 * </ul>
 * 
 * @author Lu Ming
 */
public class ModuleConfig extends BasicConfig {

    private LinkedList<ParamConfig> params = new LinkedList<ParamConfig>();
    
    /**
     * Get the name of module, if may be recorder, runner or plugin
     * @return
     */
    public String getName() {
        return getProperty("name");
    }
    
    /**
     * Get the qualified class name of module
     * @return
     */
    public String getClassName() {
        return getProperty("class");
    }

    /**
     * Add a <code>ParamConfig</code> of parameter into list
     * @param variable
     */
    public void addParameter(ParamConfig param) {
        params.add(param);
    }

    /**
     * Get the size of <code>ParamConfig</code> of parameters
     * @return the size of variable list
     */
    public int paramterSize() {
        return params.size();
    }
    
    /**
     * Get all <code>ParamConfig</code> of parameter
     * @return
     */
    public List getParameters() {
        return Collections.unmodifiableList(params);
    }

    /**
     * Returns the <code>ParamConfig</code> of parameters at the specified
     * position in this list.
     * @param index - index of the variable to return
     * @return the the <code>BasicConfig</code> at the specified position
     */
    public ParamConfig getParameter(int index) {
        return params.get(index);
    }
    
    /**
     * Removes all of the <code>ParamConfig</code> of parameters from this list
     */
    public void clearParameter() {
        params.clear();
    }
}
