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

import spock.lang.Specification

/**
 * spock.lang.Specification is the base of all Specs.
 *
 * Specs can be named anything but many people have them end in Spec.
 *
 * Here we have it end in UnitSpec to be explicit
 */
class OverviewUnitSpec extends Specification {
    def 'A minimal test'() {
        expect:
        true
    }

    def 'A less minimal test'() {
        given:
        def x = 1

        when:
        x++

        then:
        x == 2
    }

    def 'You can have more optional blocks'() {

        // optional setup (or given) block
        setup:
        setSomethingUp()
        setSomethingUp()

        // optional expect blocks
        expect:
        evaluateACondition()
        evaluateACondition()

        // optional when-then* blocks
        when:
        doSomething()
        doSomething()

        then:
        evaluateACondition()
        evaluateACondition()

        then:
        evaluateACondition()
        evaluateACondition()


        when:
        doSomething()
        doSomething()

        then:
        evaluateACondition()
        evaluateACondition()

        then:
        evaluateACondition()
        evaluateACondition()

        // optional expect blocks
        expect:
        evaluateACondition()
        evaluateACondition()

        // optional cleanup block
        cleanup:
        cleanSomethingUp()
        cleanSomethingUp()

        // optional where block for data-driven tests
        where:
        a << [1, 2]
        b << [2, 4]
    }

    def 'Given is an alias for setup'() {
        given:
        def x = 1

        expect:
        x == 1
    }

    def 'Literate style'() {
        given: 'x is assigned a value'
        def x = 1

        expect: 'x takes that value'
        x == 1
    }

    def 'And blocks'() {
        given: 'x is assigned a value'
        def x = 1

        and: 'y is assigned twice x'
        def y = 2 * x

        expect: 'the sum of x and y is thrice x'
        x + y == 3 * x
    }

    def 'Checking for exceptions'() {
        when:
        throw new RuntimeException('Bad news')

        then: 'This is more IDE friendly than the second method'
        RuntimeException e1 = thrown()
        e1.message == 'Bad news'


        when:
        throw new RuntimeException('Good news')

        then: 'The IDE cannot autocomplete e here'
        def e2 = thrown(RuntimeException)
        e2.message == 'Good news'
    }

    def 'Data driven tests'() {
        expect: 'the sum of a and b is c'
        a + b == c

        where:
        a << [1, 2, 3, 4]
        b << [2, 3, 4, 5]
        c << [3, 5, 7, 9]
    }

    def 'Groovy truth'() {
        expect:
        1
        true
        [1]
        'not empty'
    }

    def 'Helper methods can be used to share test code'() {
        expect:
        helperMethod('not empty')
        ExternalHelpers.helperMethod('not empty')
    }

    // Helper methods should be 'void' and should contain assertions
    void helperMethod(x) {
        assert true
        assert 1
        assert x
    }

    private cleanSomethingUp() {
    }

    private setSomethingUp() {
    }

    private doSomething() {
    }

    private boolean evaluateACondition() {
        return true
    }

}