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

package org.gradle.api.internal.artifacts.configurations;

import org.gradle.api.artifacts.ConflictResolution;
import org.gradle.api.artifacts.DependencySet;
import org.gradle.api.artifacts.ResolutionStrategy;
import org.gradle.api.internal.artifacts.configurations.conflicts.LatestConflictResolution;
import org.gradle.api.internal.artifacts.configurations.conflicts.StrictConflictResolution;

/**
 * by Szczepan Faber, created at: 10/7/11
 */
public class DefaultResolutionStrategy implements ResolutionStrategy {

    private DependencySet forcedVersions;
    private ConflictResolution conflictResolution = new LatestConflictResolution();

    public void setForcedVersions(DependencySet forcedVersions) {
        assert forcedVersions != null : "forcedVersions cannot be null";
        this.forcedVersions = forcedVersions;
        if (conflictResolution instanceof StrictConflictResolution) {
            //TODO SF - only working for strict strategy for now. Unit test.
            //I need tests for the other before I enable it. (it it make sense)
            ((StrictConflictResolution) conflictResolution).setForcedVersions(forcedVersions);
        }
    }

    public DependencySet getForcedVersions() {
        return forcedVersions;
    }

    public ConflictResolution latest() {
        return new LatestConflictResolution();
    }

    public ConflictResolution strict() {
        return new StrictConflictResolution().setForcedVersions(forcedVersions);
    }

    public ConflictResolution getConflictResolution() {
        return this.conflictResolution;
    }

    public void setConflictResolution(ConflictResolution conflictResolution) {
        assert conflictResolution != null : "Cannot set null conflictResolution";
        this.conflictResolution = conflictResolution;
    }
}