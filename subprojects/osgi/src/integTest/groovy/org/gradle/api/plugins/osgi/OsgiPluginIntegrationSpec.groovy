/*
 * Copyright 2012 the original author or authors.
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

package org.gradle.api.plugins.osgi

import org.gradle.integtests.fixtures.AbstractIntegrationSpec
import spock.lang.Issue

class OsgiPluginIntegrationSpec extends AbstractIntegrationSpec {

    @Issue("http://issues.gradle.org/browse/GRADLE-2237")
    def "can set modelled manifest properties with instruction"() {
        given:
        buildFile << """
            version = "1.0"
            apply plugin: "java"
            apply plugin: "osgi"
                            
            jar {
                manifest {
                    version = "3.0"
                    instructionReplace("Bundle-Version", "2.0")
                }
            }
        """
        
        and:
        file("src/main/java/Thing.java") << "public class Thing {}"
        
        when:
        run "jar"

        then:
        file("build/tmp/jar/MANIFEST.MF").text.contains("Bundle-Version: 2.0")
    }
}