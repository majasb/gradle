/*
 * Copyright 2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gradle.build.docs.dsl.model

import spock.lang.Specification

class PropertyMetaDataTest extends Specification {
    final ClassMetaData classMetaData = Mock()
    final PropertyMetaData propertyMetaData = new PropertyMetaData('prop', classMetaData)

    def formatsSignature() {
        def type = new TypeMetaData('org.gradle.SomeClass')
        propertyMetaData.type = type

        expect:
        propertyMetaData.signature == 'org.gradle.SomeClass prop'
    }

    def usesGetterToLocateOverriddenProperty() {
        MethodMetaData getter = Mock()
        MethodMetaData overriddenGetter = Mock()
        ClassMetaData overriddenClass = Mock()
        PropertyMetaData overriddenProperty = Mock()
        propertyMetaData.getter = getter

        when:
        def p = propertyMetaData.overriddenProperty

        then:
        p == overriddenProperty
        _ * getter.overriddenMethod >> overriddenGetter
        _ * overriddenGetter.ownerClass >> overriddenClass
        _ * overriddenClass.findDeclaredProperty('prop') >> overriddenProperty
    }

    def usesSetterToLocateOverriddenPropertyWhenPropertyHasNoGetter() {
        MethodMetaData setter = Mock()
        MethodMetaData overriddenSetter = Mock()
        ClassMetaData overriddenClass = Mock()
        PropertyMetaData overriddenProperty = Mock()
        propertyMetaData.setter = setter

        when:
        def p = propertyMetaData.overriddenProperty

        then:
        p == overriddenProperty
        _ * setter.overriddenMethod >> overriddenSetter
        _ * overriddenSetter.ownerClass >> overriddenClass
        _ * overriddenClass.findDeclaredProperty('prop') >> overriddenProperty
    }

    def usesSetterToLocateOverriddenPropertyWhenGetterDoesNotOverrideAnything() {
        MethodMetaData getter = Mock()
        MethodMetaData setter = Mock()
        MethodMetaData overriddenSetter = Mock()
        ClassMetaData overriddenClass = Mock()
        PropertyMetaData overriddenProperty = Mock()
        propertyMetaData.getter = getter
        propertyMetaData.setter = setter

        when:
        def p = propertyMetaData.overriddenProperty

        then:
        p == overriddenProperty
        1 * getter.overriddenMethod >> null
        _ * setter.overriddenMethod >> overriddenSetter
        _ * overriddenSetter.ownerClass >> overriddenClass
        _ * overriddenClass.findDeclaredProperty('prop') >> overriddenProperty
    }

    def hasNoOverriddenPropertyWhenGetterDoesNotOverrideAnythingAndHasNoSetter() {
        when:
        def p = propertyMetaData.overriddenProperty

        then:
        p == null
    }

    def hasNoOverriddenPropertyWhenGetterAndSetterDoNotOverrideAnything() {
        when:
        def p = propertyMetaData.overriddenProperty

        then:
        p == null
    }

    def "is deprecated when @Deprecated is attached to property or owner is deprecated"() {
        ClassMetaData deprecatedClass = Mock()
        def notDeprecated = new PropertyMetaData('param', classMetaData)
        def deprecated = new PropertyMetaData('param', classMetaData)
        def ownerDeprecated = new PropertyMetaData('param', deprecatedClass)

        given:
        deprecated.addAnnotationTypeName(Deprecated.class.name)
        deprecatedClass.deprecated >> true

        expect:
        !notDeprecated.deprecated
        deprecated.deprecated
        ownerDeprecated.deprecated
    }

    def "is experimental when @Experimental is attached to property or owner is experimental"() {
        ClassMetaData experimentalClass = Mock()
        def notExperimental = new PropertyMetaData('param', classMetaData)
        def experimental = new PropertyMetaData('param', classMetaData)
        def ownerExperimental = new PropertyMetaData('param', experimentalClass)

        given:
        experimental.addAnnotationTypeName("org.gradle.api.Experimental")
        experimentalClass.experimental >> true

        expect:
        !notExperimental.experimental
        experimental.experimental
        ownerExperimental.experimental
    }
}

