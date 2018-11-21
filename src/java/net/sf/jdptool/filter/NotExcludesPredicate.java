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
package net.sf.jdptool.filter;

import java.util.regex.Pattern;

import org.apache.commons.collections.Predicate;

import com.sun.jdi.ReferenceType;

public class NotExcludesPredicate implements Predicate {

    private String[] excludes;
    
    public NotExcludesPredicate(String[] excludes) {
        this.excludes = excludes;
    }
    
    public boolean evaluate(Object reference) {
        String className = null;
        if (reference instanceof ReferenceType) {
            className = ((ReferenceType)reference).name();
        } else {
            return false;
        }
        
        for (int i = 0; i < excludes.length; i++) {
            if (Pattern.matches(excludes[i], className)) {
                return false;
            }
        }
        return true;
    }

}
