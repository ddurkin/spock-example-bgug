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

import spock.lang.Specification
import intro.ExternalHelpers

/**
 * Some experiments with Mocks
 */
class FooInteractionsSpec extends Specification{
    Foo foo = Mock()

    def 'Creating Spock mocks'() {
        given:
        // IDE friendly declaration
        Foo foo1 = Mock()

        // Equivalent, but less autocomplete friendly
        def foo2 = Mock(Foo)

        expect:
        true
    }

    def 'Mocks are very loose'() {
        when:
        def silly = foo.thisMethodDoesNotExist()

        then:
        silly == null
    }

    def 'Default values (null/0/false) are returned by default'() {
        when:
        def f = foo.f()
        def g = foo.g()
        def h = foo.h()
        def x = foo.x

        then:
        f == null
        g == 0
        h == false
        x == 0
    }

    def 'Expecting one invocation of a method'() {
        when:
        foo.f()

        then:
        1 * foo.f()
    }


    def 'Expecting invocation of getters'() {
        when:
        def x1 = foo.x
        def x2 = foo.getX()

        then:
        2 * foo.x
    }

    def 'Specifying what the Mock should return'() {
        when:
        def f = foo.f()

        then:
        1 * foo.f() >> 'bar'
        f == 'bar'
    }

    def 'Optional invocation'() {
        when:
        def f = foo.f()
        null

        then:
        // return value is required for optional invocation
        foo.f() >> 4
        f == 4
    }

    def 'Specifying what the Mock should return for multiple invocations'() {
        when:
        def f1 = foo.f()
        def f2 = foo.f()

        then:
        // NB '>>>' instead of '>>'
        2 * foo.f() >>> ['bar', 'baz']
        f1 == 'bar'
        f2 == 'baz'
    }

    def 'Specifying return values with a closure'() {
        given:
        def x = 0

        when:
        def g1 = foo.g()
        def g2 = foo.g()

        then:
        2 * foo.g() >> {++x}
        g1 == 1
        g2 == 2
    }

    def 'Expecting maximum number of invocations'() {
        when:
        null

        then:
        // parens are required - will fail silently otherwise
        (_..2) * foo.f()


        when:
        foo.f()

        then:
        (_..2) * foo.f()


        when:
        foo.f()
        foo.f()

        then:
        (_..2) * foo.f()

    }

    def 'Expecting minimum number of invocations'() {
        when:
        foo.f()
        foo.f()

        then:
        (2.._) * foo.f()


        when:
        foo.f()
        foo.f()
        foo.f()

        then:
        (2.._) * foo.f()
    }

    def 'Expecting minimum and maximum invocations'() {
        when:
        foo.f()
        foo.f()
        foo.f()

        then:
        (2..4) * foo.f()
    }

    def 'Expecting invocation on any mock'() {
        given:
        Foo foo1 = Mock()
        Foo foo2 = Mock()

        when:
        foo2.f()

        then:
        1 * _.f()
    }


    def 'Expecting invocation on matched method or propety getter'() {
        when:
        foo.g()

        then:
        1 * foo./[fgh]/()
    }


    def 'Expecting any argument value'() {
        when:
        foo.fn(5)

        then:
        1 * foo.fn(_)
        true
    }

    def 'Expecting a non-null argument'() {
        when:
        foo.fo(null)
        foo.fo('bar')

        then:
        // require at one call that matches the argument constraint
        // NB not requiring that there is one call, and that it matches the constraint!
        1 * foo.fo(!null)
    }

    def 'Expecting a specific argument'() {
        when:
        foo.fn(4)
        foo.fn(5)

        then:
        1 * foo.fn(5)
    }

    def 'Expecting any other argument'() {
        when:
        foo.fn(4)
        foo.fn(5)

        then:
        1 * foo.fn(!5)
    }


    def 'Expecting any other argument - tighter version'() {
        when:
        foo.fn(4)

        then:
        0 * foo.fn(5)
        _ * foo.fn(_)
    }

    def 'Expecting a specified type of argument'() {
        when:
        foo.fo('bar')

        then:
        1 * foo.fo(_ as String)
    }

    def 'Expecting argument to meet a condition'() {
        when:
        foo.fo(2)
        foo.fo(3)
        foo.fo(4)

        then:
        2 * foo.fo({it % 2 == 0})
    }

    def 'Expecting argument to meet a condition - tighter version'() {
        when:
        foo.fo(2)
        foo.fo(4)

        then:
        2 * foo.fo({it % 2 == 0})
        0 * foo.fo({it % 2 != 0})
    }

    def ''() {
        when:
        foo.fn(4)

        then:
        1 * foo.fn(!5)
    }


    def 'Fail for unspecified method invocation'() {
        when:
        foo.fn(4)

        then:
        1 * foo.fn(!5)
        0 * _
        // or equivalently
        0 * _._
    }

    def 'Multiline interaction definitions'() {
        when:
        foo.f()
        foo.f()

        then:
        interaction {
            def n = 2
            n * foo.f()
        }
    }

    def 'Helper method interactions'() {
        when:
        foo.f()
        foo.f()
        foo.g()

        then:
        interaction {
            fooReceivesF(2)
        }
        interaction {
            // NB this doesn't work
            ExternalHelpers.fooReceivesG(foo, 5)
        }
    }

    def 'Unordered invocations'() {
        when:
        foo.g()
        foo.f()

        then:
        1 * foo.f()
        1 * foo.g()
    }

    def 'Ordered invocations'() {
        when:
        foo.g()
        foo.f()

        then:
        1 * foo.g()

        then:
        1 * foo.f()
    }

    def 'Some methods do not match wildcard'() {
        when:
        foo.hashCode()
        foo.toString()
        foo.silly()

        // this one matches
        foo.f()


        then:
        1 * foo._()
    }

    def 'Nonexistent methods can be invoked, but cannot be matched'() {
        when:
        foo.silly()

        then:
        0 * foo.silly()
        0 * foo._()
        // this one matches the call
        1 * _
    }

    def 'Order of declarations matters when multiple predicates are matched'() {
        when:
        foo.f()

        then:
        // 0 * _
        1 * foo.f()
        0 * _
    }

    def 'Global interactions'() {
        given:
        2 * foo.f()

        and:
        foo.f()
        foo.f()
    }

    def fooReceivesF(int n) {
        n * foo.f()
    }
}
