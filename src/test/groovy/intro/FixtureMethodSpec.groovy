/*
 * Copyright 2012 the original author or authors.
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

/**
 * Fixture Methods in action
 */
class FixtureMethodSpec extends Specification {
    def instanceField = 0
    @Shared def sharedField = 0

    /**
     * Run prior to the setup() fixture of first Feature Method
     * NOTE: NO ACCESS TO INSTANCE FIELDS
     */
    def setupSpec() {
        assert sharedField == 0
        sharedField++
    }

    /**
     * Run prior to feature method after setupSpec()
     */
    def setup() {
        assert instanceField == 0
        instanceField++

        assert sharedField == 1
        sharedField++
    }

    /**
     * Run after feature method prior to cleanupSpec()
     */
    def cleanup() {
        instanceField = -1 // get reinitialized for each feature method

        assert sharedField == 2
        sharedField--
    }

    /**
     * Run after feature method after to cleanup()
     * NOTE: NO ACCESS TO INSTANCE FIELDS
     */
    def cleanupSpec() {
        assert sharedField == 1
        sharedField--
    }

    def 'test 1'() {
        expect:
        instanceField == 1
        sharedField == 2
    }

    def 'test 2'() {
        expect:
        instanceField == 1
        sharedField == 2
    }
}
