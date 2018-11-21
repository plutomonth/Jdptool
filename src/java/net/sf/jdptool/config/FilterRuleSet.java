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

public class FilterRuleSet extends RuleSetBase implements ConfigConstants {

    /**
     * <p>Add the set of Rule instances defined in this RuleSet to the
     * specified <code>Digester</code> instance, associating them with our
     * namespace URI (if any).  This method should only be called by a
     * Digester instance.
     *
     * @param digester instance to which rule set  should be added.
     */
    public void addRuleInstances(Digester digester) {
        // Root XML element "jdp-filter"
        digester.addObjectCreate("jdp-filter", FilterConfig.class);
        digester.addRule("jdp-filter", new SetConfigPropertiesRule());
        
        // XML element "jdp-filter/breaks"
        digester.addObjectCreate("jdp-filter/breaks", BreaksConfig.class);
        digester.addSetNext("jdp-filter/breaks", "setBreaks", BreaksConfig.class.getName());
        
        // XML element "jdp-filter/field"
        digester.addObjectCreate("jdp-filter/field", BasicConfig.class);
        digester.addRule("jdp-filter/field", new SetConfigPropertiesRule());
        digester.addSetNext("jdp-filter/field", "addField", BasicConfig.class.getName());
        
        // XML element "jdp-filter/timing"
        digester.addObjectCreate("jdp-filter/timing", BasicConfig.class);
        digester.addRule("jdp-filter/timing", new SetConfigPropertiesRule());
        digester.addSetNext("jdp-filter/timing", "addTiming", BasicConfig.class.getName());
        
        // XML element "jdp-filter/breaks/method"
        digester.addObjectCreate("jdp-filter/breaks/method", BreakConfig.class);
        digester.addRule("jdp-filter/breaks/method", new SetConfigPropertiesRule());
        digester.addSetNext("jdp-filter/breaks/method", "addMethod", BreakConfig.class.getName());
        
        // XML element "jdp-filter/breaks/line"
        digester.addObjectCreate("jdp-filter/breaks/line", BreakConfig.class);
        digester.addRule("jdp-filter/breaks/line", new SetConfigPropertiesRule());
        digester.addSetNext("jdp-filter/breaks/line", "addLine", BreakConfig.class.getName());
        
        // XML element "jdp-filter/breaks/method/variable"
        digester.addObjectCreate("jdp-filter/breaks/method/variable",
                                 BreakConfig.class);
        digester.addRule("jdp-filter/breaks/method/variable", new SetConfigPropertiesRule());
        digester.addSetNext("jdp-filter/breaks/method/variable", "addVariable", BasicConfig.class.getName());
        
        // XML element "jdp-filter/breaks/line/variable"
        digester.addObjectCreate("jdp-filter/breaks/line/variable", BreakConfig.class);
        digester.addRule("jdp-filter/breaks/line/variable", new SetConfigPropertiesRule());
        digester.addSetNext("jdp-filter/breaks/line/variable", "addVariable", BasicConfig.class.getName());
        

    }
    
}
