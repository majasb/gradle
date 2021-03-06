/*
 * Copyright 2011 the original author or authors.
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
package org.gradle.api.internal.artifacts.ivyservice;

import org.apache.ivy.core.module.id.ModuleRevisionId;
import org.gradle.api.artifacts.ModuleVersionIdentifier;
import org.gradle.api.artifacts.UnresolvedDependency;
import org.gradle.api.internal.artifacts.DefaultModuleVersionIdentifier;
import org.gradle.util.DeprecationLogger;

public class DefaultUnresolvedDependency implements UnresolvedDependency {
    private final Throwable problem;
    private ModuleVersionIdentifier id;
    private final ModuleRevisionId revisionId;

    public DefaultUnresolvedDependency(ModuleRevisionId id, Throwable problem) {
        revisionId = id;
        this.id = new DefaultModuleVersionIdentifier(id.getOrganisation(), id.getName(), id.getRevision());
        this.problem = problem;
    }

    public String getId() {
        DeprecationLogger.nagUserOfReplacedMethod("UnresolvedDependency.getId()", "UnresolvedDependency.getIdentifier()");
        return revisionId.toString();
    }

    public ModuleVersionIdentifier getIdentifier() {
        return id;
    }

    public Throwable getProblem() {
        return problem;
    }

    public String toString() {
        return id.getGroup() + ":" + id.getName() + ":" + id.getVersion();
    }
}
