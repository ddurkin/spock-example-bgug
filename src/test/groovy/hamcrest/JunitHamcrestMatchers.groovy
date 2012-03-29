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

package hamcrest

import static org.hamcrest.CoreMatchers.equalTo;


import static org.junit.Assert.assertThat
import org.junit.Test

import static spock.util.matcher.HamcrestMatchers.closeTo;
/**
 * Some examples of JUnit use of Hamcrest Matchers
 */
class JunitHamcrestMatchers {

    @Test
    public void comparingTwoDecimalNumbers() {
        def myPi = 3.14
        assertThat(myPi, closeTo(Math.PI, 0.01))
    }

    @Test
    public void shouldBeTheSamePerson() {
        Object me = new Dog(name: "Ralf");
        Object theOther = new Dog(name: "Ralf");
        assertThat(me, equalTo(theOther))
    }

    @Test
    public void shouldHaveFixedSizeNumbers() {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        assertThat(numbers.size(), equalTo(5));
    }


}

