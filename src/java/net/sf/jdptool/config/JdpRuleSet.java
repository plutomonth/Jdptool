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

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.RuleSetBase;

public class JdpRuleSet extends RuleSetBase implements ConfigConstants {

    /**
     * <p>Add the set of Rule instances defined in this RuleSet to the
     * specified <code>Digester</code> instance, associating them with our
     * namespace URI (if any).  This method should only be called by a
     * Digester instance.
     *
     * @param digester instance to which rule set  should be added.
     */
    public void addRuleInstances(Digester digester) {
        
        // Root XML element "jdp-config"
        digester.addObjectCreate("jdp-config", JdpConfig.class);
        digester.addRule("jdp-config", new SetConfigPropertiesRule());

        // XML element "jdp-config/recorder"
        digester.addObjectCreate("jdp-config/recorder", ModuleConfig.class);
        digester.addRule("jdp-config/recorder", new SetConfigPropertiesRule());
        digester.addSetNext("jdp-config/recorder", "setRecorder", ModuleConfig.class.getName());

        // XML element "jdp-config/runner"
        digester.addObjectCreate("jdp-config/runner", ModuleConfig.class);
        digester.addRule("jdp-config/runner", new SetConfigPropertiesRule());
        digester.addSetNext("jdp-config/runner", "addRunner", ModuleConfig.class.getName());
        
        // XML element "jdp-config/plugin"
        digester.addObjectCreate("jdp-config/plugin", ModuleConfig.class);
        digester.addRule("jdp-config/plugin", new SetConfigPropertiesRule());
        digester.addSetNext("jdp-config/plugin", "addPlugin", ModuleConfig.class.getName());
        
        // XML element "jdp-config/recorder/param"
        digester.addObjectCreate("jdp-config/recorder/param", ParamConfig.class);
        digester.addRule("jdp-config/recorder/param", new SetConfigPropertiesRule());
        digester.addSetNext("jdp-config/recorder/param", "addParameter", ParamConfig.class.getName());

        // XML element "jdp-config/runner/param"
        digester.addObjectCreate("jdp-config/runner/param", ParamConfig.class);
        digester.addRule("jdp-config/runner/param", new SetConfigPropertiesRule());
        digester.addSetNext("jdp-config/runner/param", "addParameter", ParamConfig.class.getName());
        
        // XML element "jdp-config/plugin/param"
        digester.addObjectCreate("jdp-config/plugin/param", ParamConfig.class);
        digester.addRule("jdp-config/plugin/param", new SetConfigPropertiesRule());
        digester.addSetNext("jdp-config/plugin/param", "addParameter", ParamConfig.class.getName());
        
        
    }

}
