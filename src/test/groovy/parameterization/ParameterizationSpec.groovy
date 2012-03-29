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
package parameterization

import groovy.sql.Sql
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Examples of Data Driven Parameteriized Tests
 */
@Unroll
class ParameterizationSpec extends Specification {

    // Can interpolate param values in method name using #
    def 'Tabular   parameterization y== 2 * x where x:#x, y:#y'() {
        expect:
        y == 2 * x

        where:
        x | y
        1 | 2
        2 | 4
        3 | 6
    }

    def 'Iterable parameterization y== 2 * x where x:#x, y:#y'() {
        expect:
        y == 2 * x

        where:
        x << [1, 2, 3]
        y << [2, 4, 6].iterator()
    }

    def 'Derived parameterization y== 2 * x where x:#x, y:#y'() {
        expect:
        y == 2 * x

        where:
        x << [1, 2, 3]
        // y is derived - uses '=' instead of '<<'
        y = 2 * x
    }

    def 'Multi-assignment parameter ization y== 2 * x where x:#x, y:#y'() {
        expect:
        y == 2 * x

        where:
        [x, y] << [[1, 2], [2, 4], [3, 6]]
    }

    def 'Combinations trick x<y where x:#x, y:#y'() {
        expect:
        x < y

        where:
        [x, y] << [[1, 2, 3], [4, 5, 6]].combinations()
    }


    @Shared sql = Sql.newInstance("jdbc:h2:mem:", "org.h2.Driver")

    def setupSpec() {
        sql.execute("create table t (x int, y int)")
        sql.execute("insert into t values (1,2),(2,4),(3,6)")
    }

    def 'Database parameterization y == 2 * x where x:#x, y:#y'() {
        expect:
        y == 2 * x

        where:
        [x, y] << sql.rows("select x, y from t")
    }

    // can call zero arg methods on naming patter
    def 'Params can handle the truth x:#x #x.getClass()'() {
        expect:
        x

        where:
        x << [-1,1, 2.0, '  ']
    }

}
