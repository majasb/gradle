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
package org.gradle.api.dsl

import org.gradle.integtests.fixtures.GradleDistribution
import org.gradle.integtests.fixtures.GradleDistributionExecuter
import org.gradle.util.TestFile
import org.junit.Rule
import org.junit.Test
import spock.lang.Issue

class DynamicObjectIntegrationTest {
    @Rule public final GradleDistribution dist = new GradleDistribution()
    @Rule public final GradleDistributionExecuter executer = new GradleDistributionExecuter()

    @Test
    public void canAddDynamicPropertiesToProject() {
        TestFile testDir = dist.getTestDir();
        testDir.file("settings.gradle").writelns("include 'child'");
        testDir.file("build.gradle").writelns(
                "ext.rootProperty = 'root'",
                "ext.sharedProperty = 'ignore me'",
                "ext.property = 'value'",
                "convention.plugins.test = new ConventionBean()",
                "task rootTask",
                "task testTask",
                "class ConventionBean { def getConventionProperty() { 'convention' } }"
        );
        testDir.file("child/build.gradle").writelns(
                "ext.childProperty = 'child'",
                "ext.sharedProperty = 'shared'",
                "task testTask << {",
                "  new Reporter().checkProperties(project)",
                "}",
                "assert 'root' == rootProperty",
                "assert 'root' == property('rootProperty')",
                "assert 'root' == properties.rootProperty",
                "assert 'child' == childProperty",
                "assert 'child' == property('childProperty')",
                "assert 'child' == properties.childProperty",
                "assert 'shared' == sharedProperty",
                "assert 'shared' == property('sharedProperty')",
                "assert 'shared' == properties.sharedProperty",
                "assert 'convention' == conventionProperty",
                // Use a separate class, to isolate Project from the script
                "class Reporter {",
                "  def checkProperties(object) {",
                "    assert 'root' == object.rootProperty",
                "    assert 'child' == object.childProperty",
                "    assert 'shared' == object.sharedProperty",
                "    assert 'convention' == object.conventionProperty",
                "    assert 'value' == object.property",
                "    assert ':child:testTask' == object.testTask.path",
                "    try { object.rootTask; fail() } catch (MissingPropertyException e) { }",
                "  }",
                "}"
        );

        executer.inDirectory(testDir).withTasks("testTask").run();
    }

    @Test
    public void canAddDynamicMethodsToProject() {
        TestFile testDir = dist.getTestDir();
        testDir.file("settings.gradle").writelns("include 'child'");
        testDir.file("build.gradle").writelns(
                "def rootMethod(p) { 'root' + p }",
                "def sharedMethod(p) { 'ignore me' }",
                "convention.plugins.test = new ConventionBean()",
                "task rootTask",
                "task testTask",
                "class ConventionBean { def conventionMethod(name) { 'convention' + name } }"
        );
        testDir.file("child/build.gradle").writelns(
                "def childMethod(p) { 'child' + p }",
                "def sharedMethod(p) { 'shared' + p }",
                "task testTask << {",
                "  new Reporter().checkMethods(project)",
                "}",
                // Use a separate class, to isolate Project from the script
                "class Reporter {",
                "  def checkMethods(object) {",
                "    assert 'rootMethod' == object.rootMethod('Method')",
                "    assert 'childMethod' == object.childMethod('Method')",
                "    assert 'sharedMethod'== object.sharedMethod('Method')",
                "    assert 'conventionMethod' == object.conventionMethod('Method')",
                "    object.testTask { assert ':child:testTask' == delegate.path }",
                "    try { object.rootTask { }; fail() } catch (MissingMethodException e) { }",
                "  }",
                "}"
        );

        executer.inDirectory(testDir).withTasks("testTask").run();
    }

    @Test
    public void canAddMixinsToProject() {
        TestFile testDir = dist.getTestDir();
        testDir.file('build.gradle') << '''
convention.plugins.test = new ConventionBean()

assert conventionProperty == 'convention'
assert conventionMethod('value') == '[value]'

class ConventionBean {
    def getConventionProperty() { 'convention' }
    def conventionMethod(String value) { "[$value]" }
}
'''

        executer.inDirectory(testDir).run();
    }

    @Test
    public void canAddExtensionsToProject() {
        TestFile testDir = dist.getTestDir();
        testDir.file('build.gradle') << '''
extensions.test = new ExtensionBean()

assert test instanceof ExtensionBean
test { it ->
    assert it == project.test
}
class ExtensionBean {
}
'''

        executer.inDirectory(testDir).run();
    }

    @Test
    public void canAddPropertiesToProjectUsingGradlePropertiesFile() {
        TestFile testDir = dist.getTestDir();
        testDir.file("settings.gradle").writelns("include 'child'");
        testDir.file("gradle.properties") << '''
global=some value
'''
        testDir.file("build.gradle") << '''
assert 'some value' == global
assert hasProperty('global')
assert 'some value' == property('global')
assert 'some value' == properties.global
assert 'some value' == project.global
assert project.hasProperty('global')
assert 'some value' == project.property('global')
assert 'some value' == project.properties.global
'''
        testDir.file("child/gradle.properties") << '''
global=overridden value
'''
        testDir.file("child/build.gradle") << '''
assert 'overridden value' == global
'''

        executer.inDirectory(testDir).run();
    }

    @Test
    public void canAddDynamicPropertiesToCoreDomainObjects() {
        TestFile testDir = dist.getTestDir();
        testDir.file('build.gradle') << '''
            class GroovyTask extends DefaultTask { }

            task defaultTask {
                ext.custom = 'value'
            }
            task javaTask(type: Copy) {
                ext.custom = 'value'
            }
            task groovyTask(type: GroovyTask) {
                ext.custom = 'value'
            }
            configurations {
                test {
                    ext.custom = 'value'
                }
            }
            dependencies {
                test('::name:') {
                    ext.custom = 'value';
                }
                test(module('::other')) {
                    ext.custom = 'value';
                }
                test(project(':')) {
                    ext.custom = 'value';
                }
                test(files('src')) {
                    ext.custom = 'value';
                }
            }
            repositories {
                ext.custom = 'repository'
            }
            defaultTask.custom = 'another value'
            javaTask.custom = 'another value'
            groovyTask.custom = 'another value'
            assert !project.hasProperty('custom')
            assert defaultTask.hasProperty('custom')
            assert defaultTask.custom == 'another value'
            assert javaTask.custom == 'another value'
            assert groovyTask.custom == 'another value'
            assert configurations.test.hasProperty('custom')
            assert configurations.test.custom == 'value'
            configurations.test.dependencies.each {
                assert it.hasProperty('custom')
                assert it.custom == 'value'
                assert it.getProperty('custom') == 'value'
            }
            assert repositories.hasProperty('custom')
            assert repositories.custom == 'repository'
            repositories {
                assert custom == 'repository'
            }
'''

        executer.inDirectory(testDir).withTasks("defaultTask").run();
    }

    @Test
    public void canAddMixInsToCoreDomainObjects() {
        TestFile testDir = dist.getTestDir();
        testDir.file('build.gradle') << '''
            class Extension { def doStuff() { 'method' } }
            class GroovyTask extends DefaultTask { }

            task defaultTask {
                convention.plugins.custom = new Extension()
            }
            task javaTask(type: Copy) {
                convention.plugins.custom = new Extension()
            }
            task groovyTask(type: GroovyTask) {
                convention.plugins.custom = new Extension()
            }
            configurations {
                test {
                    convention.plugins.custom = new Extension()
                }
            }
            dependencies {
                test('::name:') {
                    convention.plugins.custom = new Extension()
                }
                test(module('::other')) {
                    convention.plugins.custom = new Extension()
                }
                test(project(':')) {
                    convention.plugins.custom = new Extension()
                }
                test(files('src')) {
                    convention.plugins.custom = new Extension()
                }
            }
            repositories {
                convention.plugins.custom = new Extension()
            }
            assert defaultTask.doStuff() == 'method'
            assert javaTask.doStuff() == 'method'
            assert groovyTask.doStuff() == 'method'
            assert configurations.test.doStuff() == 'method'
            configurations.test.dependencies.each {
                assert it.doStuff() == 'method'
            }
            assert repositories.doStuff() == 'method'
            repositories {
                assert doStuff() == 'method'
            }
'''

        executer.inDirectory(testDir).withTasks("defaultTask").run();
    }

    @Test
    public void canAddExtensionsToCoreDomainObjects() {
        TestFile testDir = dist.getTestDir();
        testDir.file('build.gradle') << '''
            class Extension { def doStuff() { 'method' } }
            class GroovyTask extends DefaultTask { }

            task defaultTask {
                extensions.test = new Extension()
            }
            task javaTask(type: Copy) {
                extensions.test = new Extension()
            }
            task groovyTask(type: GroovyTask) {
                extensions.test = new Extension()
            }
            configurations {
                test {
                    extensions.test = new Extension()
                }
            }
            dependencies {
                test('::name:') {
                    extensions.test = new Extension()
                }
                test(module('::other')) {
                    extensions.test = new Extension()
                }
                test(project(':')) {
                    extensions.test = new Extension()
                }
                test(files('src')) {
                    extensions.test = new Extension()
                }
            }
            repositories {
                extensions.test = new Extension()
            }
            assert defaultTask.test instanceof Extension
            assert javaTask.test instanceof Extension
            assert groovyTask.test instanceof Extension
            assert configurations.test.test instanceof Extension
            configurations.test.dependencies.each {
                assert it.test instanceof Extension
            }
            assert repositories.test instanceof Extension
            repositories {
                assert test instanceof Extension
            }
'''

        executer.inDirectory(testDir).withTasks("defaultTask").run();
    }

    @Test
    public void mixesDslMethodsIntoCoreDomainObjects() {
        TestFile testDir = dist.getTestDir();
        testDir.file('build.gradle') << '''
            class GroovyTask extends DefaultTask {
                def String prop
                void doStuff(Action<Task> action) { action.execute(this) }
            }
            tasks.withType(GroovyTask) { conventionMapping.prop = { '[default]' } }
            task test(type: GroovyTask)
            assert test.prop == '[default]'
            test {
                description 'does something'
                prop 'value'
            }
            assert test.description == 'does something'
            assert test.prop == 'value'
            test.doStuff {
                prop = 'new value'
            }
            assert test.prop == 'new value'
'''

        executer.inDirectory(testDir).withTasks("test").run();
    }

    @Test
    void canAddExtensionsToDynamicExtensions() {
        TestFile testDir = dist.getTestDir();
        testDir.file('build.gradle') << '''
            class Extension {
                String name
                Extension(String name) {
                    this.name = name
                }
            }

            project.extensions.create("l1", Extension, "l1")
            project.l1.extensions.create("l2", Extension, "l2")
            project.l1.l2.extensions.create("l3", Extension, "l3")

            task test << {
                assert project.l1.name == "l1"
                assert project.l1.l2.name == "l2"
                assert project.l1.l2.l3.name == "l3"
            }
        '''

        executer.inDirectory(testDir).withTasks("test").run();
    }

    @Test
    public void canInjectMethodsFromParentProject() {
        TestFile testDir = dist.getTestDir();
        testDir.file("settings.gradle").writelns("include 'child'");
        testDir.file("build.gradle").writelns(
                "subprojects {",
                "  ext.injectedMethod = { project.name }",
                "}"
        );
        testDir.file("child/build.gradle").writelns(
                "task testTask << {",
                "   assert injectedMethod() == 'child'",
                "}"
        );

        executer.inDirectory(testDir).withTasks("testTask").run();
    }
    
    @Test void canAddNewPropertiesViaTheAdhocNamespace() {
        TestFile testDir = dist.getTestDir();
        testDir.file("build.gradle") << """
            ext {
                set "p1", 1
            }
            assert p1 == 1
            p1 = 2
            assert p1 == 2
            assert ext.p1 == 2

            task run << {
                ext {
                    set "p1", 1
                }
                assert p1 == 1
                p1 = 2
                assert p1 == 2
                assert ext.p1 == 2
            }
        """
        
        executer.withTasks("run").run()
    }

    @Issue("GRADLE-2163")
    @Test void canDecorateBooleanPrimitiveProperties() {
        TestFile testDir = dist.getTestDir();
        testDir.file("build.gradle") << """
            class CustomBean {
                boolean b
            }

            // best way to decorate right now
            extensions.create('bean', CustomBean)

            task run << {
                assert bean.b == false
                bean.conventionMapping.b = { true }
                assert bean.b == true
            }
        """

        executer.withTasks("run").run()
    }
}
