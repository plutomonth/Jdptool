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

/**
 * The class <code>BaseException</code> is base exception of jdptool which
 * indicates conditions that a jdptool application might want to catch.
 * 
 * @author mlu
 */
public class BaseException extends Exception {

    /**
     * Constructs a new exception with null as its detail message.
     */
    public BaseException() {
        super();
    }
    
    /**
     * Constructs a new exception with the specified detail message.
     * @param message - the detail message
     */
    public BaseException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new exception with the specified detail message and cause.
     * @param message - the detail message 
     * @param throwable - the cause, A null value is permitted, and indicates
     *      that the cause is nonexistent or unknown.
     */
    public BaseException(String message, Throwable throwable) {
        super(message, throwable);
    }

    /**
     * Constructs a new exception with the specified cause. This constructor
     * is useful for exceptions that are little more than wrappers for other
     * throwables
     * @param throwable - the cause, A null value is permitted, and indicates
     *      that the cause is nonexistent or unknown.
     */
    public BaseException(Throwable throwable) {
        super(throwable);
    }

}
