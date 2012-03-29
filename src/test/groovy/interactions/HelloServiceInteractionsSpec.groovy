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
package interactions

import org.spockframework.mock.TooFewInvocationsError
import org.spockframework.mock.TooManyInvocationsError
import org.spockframework.mock.WrongInvocationOrderError
import spock.lang.FailsWith
import spock.lang.Specification

class HelloServiceInteractionsSpec extends Specification {
    HelloService helloService = Mock()

    def 'Creating Spock mocks, static and dynamic style'() {
        HelloService helloService1 = Mock() // IDE friendly declaration
        def helloService2 = Mock(HelloService)// Equivalent, but less autocomplete friendly
        expect:
        true
    }

    def 'Mocks are lenient and return defaults (null/0/false)'() {
        when:
        def silly = helloService.thisMethodDoesNotExist()

        then:
        silly == null
    }

    @FailsWith(TooManyInvocationsError)
    def 'Can run in non lenient mode with broad matcher'() {
        when:
        helloService.thisMethodDoesNotExist()
        // _ is shortcut for _._
        then:
        0 * _
    }

    def 'Default returned values (null/0/false)'() {
        expect:
        helloService.sayTypedHello() == null
        helloService.sayDefHello() == null
        helloService.helloActive() == false
        helloService.sayIntHello() == 0
        helloService.sayNumberHello() == null
        helloService.echo(new Object()) == null
    }

    def 'Global interactions not'() {
        helloService.helloActive() >> true

        when:
        helloService.helloActive() == true

        then:
        helloService.helloActive() == true

        when: "global interaction in place"
        helloService.helloActive() == true

        then: "can't override with local interaction"
        helloService.helloActive() >> false
    }

    def 'Local interactions'() {
        when:
        def helloActive = helloService.helloActive()

        then:
        helloService.helloActive() >> true
        helloActive == true


        when:
        def andHelloActive = helloService.helloActive()

        then: 'no local interaction so back to default'
        andHelloActive == false
    }

    // Optional vs. required interactions
    def "Optional interactions don't have a cardinality, and must have a return value"() {
        helloService.helloActive() >> true

        expect:
        helloService.sayTypedHello() == null
    }

    @FailsWith(TooFewInvocationsError)
    def "Required interactions must have a cardinality, and may have a return value."() {

        when:
        1 == 1
        then:
        1 * helloService.helloActive() >> true

    }

    @FailsWith(TooFewInvocationsError)
    def "Required interactions must have a cardinality, no return value."() {

        when:
        1 == 1
        then:
        1 * helloService.helloActive()
    }

    @FailsWith(TooManyInvocationsError)
    def "Required interactions must have a cardinality,can have ranges"() {

        when:
        2.times { helloService.helloActive() }
        then:
        (_..1) * helloService.helloActive()
    }

    def 'Specifying what the Mock should return'() {
        when:
        def msg = helloService.sayTypedHello()

        then:
        1 * helloService.sayTypedHello() >> 'hi'
        msg == 'hi'
    }

    def 'Specifying what the Mock should return for multiple invocations'() {
        when:
        def f1 = helloService.sayTypedHello()
        def f2 = helloService.sayTypedHello()

        then:
        // NOTE '>>>' instead of '>>'
        2 * helloService.sayTypedHello() >>> ['bar', 'baz']
        f1 == 'bar'
        f2 == 'baz'
    }

    def 'Specifying return values with a closure'() {
        given:
        def x = 0

        when:
        def val1 = helloService.sayIntHello()
        def val2 = helloService.sayIntHello()

        then:
        2 * helloService.sayIntHello() >> {++x}
        val1 == 1
        val2 == 2
    }

    def 'Expecting invocation on any mock, wildcard the target'() {
        given:
        HelloService helloService1 = Mock()
        HelloService helloService2 = Mock()

        when:
        helloService2.sayTypedHello()

        then:
        1 * _.sayTypedHello()
    }

    def 'Expecting any argument value, wildcard arg'() {
        when:
        helloService.echo('Bob')

        then:
        1 * helloService.echo(_)
        true
    }

    def 'Expecting a non-null argument'() {
        when:
        helloService.echo(null)
        helloService.echo('Bob')
        // NOTE: not requiring that there is one call, and that it matches the constraint!
        // require at one call that matches the argument constraint
        then:
        1 * helloService.echo(!null)
    }

    def 'Expecting a specific argument'() {
        when:
        helloService.echo(5) == null
        helloService.echo(4) == null

        //doesn't vail validation for call to echo(4) mocks are lenient
        then:
        1 * helloService.echo(5)
    }

    @FailsWith(TooManyInvocationsError)
    def 'Expecting a specific argument, fail with any other arg'() {
        when:
        helloService.echo(5) == null
        helloService.echo(4) == null

        then:
        1 * helloService.echo(5)
        0 * helloService.echo(!5)
    }

    def 'Expecting 1 any other arg, 1 specific arg'() {
        when:
        helloService.echo(5) == null
        helloService.echo(4) == null

        then:
        1 * helloService.echo(5)
        1 * helloService.echo(!5)
    }

    def 'Expecting a specified type of argument'() {
        when:
        helloService.echo('Bob')

        then:
        1 * helloService.echo(_ as String)
    }

    @FailsWith(TooManyInvocationsError)
    def 'Expecting a specified type of argument, but call on another type'() {
        when:
        helloService.echo(1)

        // NOTE: Argument constraints are like matchers,
        // if you match, you count get evaluated against
        // the cardinality constraints, otherwise, you don't impact that interaction
        then:
        1 * helloService.echo(_ as String)
        // don't believe you can
        0 * helloService.echo(!String)
    }

    def 'Expecting argument to meet a condition'() {
        when:
        helloService.echo(2)
        helloService.echo(3)

        then:
        1 * helloService.echo({it % 2 == 0})
    }


    @FailsWith(TooManyInvocationsError)
    def 'Expecting argument to meet a condition, fail otherwise'() {
        when:
        helloService.echo(2)
        helloService.echo(3)

        then:
        1 * helloService.echo({it % 2 == 0})
        0 * helloService.echo({it % 2 != 0})
    }

    def 'interaction closure when interaction depends on other code in block'() {
        when:
        helloService.sayTypedHello()
        helloService.sayTypedHello()

        then:
        interaction {
            def count = 2
            count * helloService.sayTypedHello()
        }
    }

    def 'Invocations within a single block are unordered'() {
        when:
        helloService.sayTypedHello()
        helloService.sayDefHello()

        then:
        1 * helloService.sayDefHello()
        1 * helloService.sayTypedHello()
    }

    def 'Invocations within a single block are ordered, when match multiple constraints'() {
        when:
        helloService.sayTypedHello()

        then:
        1 * helloService.sayTypedHello()
        0 * helloService._
    }

    @FailsWith(TooManyInvocationsError)
    def 'Invocations within a single block are ordered, when match multiple constraints - expect failure'() {
        when:
        helloService.sayTypedHello()

        then:
        0 * helloService._
        1 * helloService.sayTypedHello()
    }

    @FailsWith(WrongInvocationOrderError)
    def 'For ordered invocations use multiple then blocks'() {
        when:
        helloService.sayTypedHello()
        helloService.sayDefHello()

        then:
        1 * helloService.sayDefHello()
        then:
        1 * helloService.sayTypedHello()
    }

    def 'Some methods do not match wildcard'() {
        when:
        helloService.hashCode()
        helloService.toString()
        // this one matches
        helloService.thisMethodDoesNotExist()

        then:
        1 * helloService._
    }

    @FailsWith(TooFewInvocationsError)
    def 'Some methods do not match wildcard, be careful with syntax'() {
        when:
        helloService.hashCode()
        helloService.toString()

        helloService.thisMethodDoesNotExist()

        // the trailing parens result in the 'thisMethodDoesNotExist' not matching
        then:
        1 * helloService._()
    }


}