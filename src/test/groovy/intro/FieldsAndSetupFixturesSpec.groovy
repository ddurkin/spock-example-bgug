/*
 * Copyright 2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package intro

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

 /**
 * See the fields section of the docs:
 * http://code.google.com/p/spock/wiki/SpockBasics *
 */
@Stepwise
class FieldsAndSetupFixturesSpec extends Specification {

    /**
     * Strongly encouraged to only use static fields for constants
     * @Shared preferred for any shared variables
     */
    static final PI = 3.141592654

    /**
     * Objects stored into instance fields are not shared between feature methods.
     * Instead, every feature method gets its own object.
     * Semantically, this is equivalent to initializing them at the very beginning of the setup() method.)
     *
     * New object for every feature
     */
    def someObject = 1
    Integer someSetupObject
    /**
     * Sometimes you w ant to share for all feature methods
     *
     * Semantically, this is equivalent to initializing the field at the very beginning of the setupSpec() method.
     *
     * One object shared for all feature methods prior to first feature method invocation
     */
    @Shared def someSharedObject = 1
    @Shared Integer someSetupSpecSharedObject

    /**
     * Invoked prior to the setup() fixture of first Feature Method
     * NOTE: NO ACCESS TO INSTANCE FIELDS
     */
    def setupSpec() {
        someSetupSpecSharedObject = 1
    }

    /**
     * example initializing a field in the setup fixture
     * run prior to feature method after setupSpec()
     */
    def setup() {
        someSetupObject = 1
        assert someSetupSpecSharedObject != null
    }

    def '1st test - Shared objects are not recreated for each test'() {
        given:
        someObject++
        someSetupObject++
        someSharedObject++
        someSetupSpecSharedObject++

        expect:
        someObject == 2
        someSetupObject == 2
        someSharedObject == 2
        someSetupSpecSharedObject == 2
    }

    def '2nd test - Shared objects are not recreated for each test'() {
        given:
        someObject++
        someSetupObject++
        someSharedObject++
        someSetupSpecSharedObject++

        expect:
        someObject == 2
        someSetupObject == 2
        someSharedObject == 3
        someSetupSpecSharedObject == 3
    }

}

