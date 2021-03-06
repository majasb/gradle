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

package org.gradle.internal.classpath;

import org.gradle.internal.UncheckedException;

import java.io.File;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * An immutable classpath.
 */
public class DefaultClassPath implements ClassPath, Serializable {
    private final List<File> files;

    public DefaultClassPath(Iterable<File> files) {
        this.files = new ArrayList<File>();
        for (File file : files) {
            this.files.add(file);
        }
    }
    
    public DefaultClassPath(File... files) {
        this(Arrays.asList(files));
    }

    public boolean isEmpty() {
        return files.isEmpty();
    }

    public Collection<URI> getAsURIs() {
        List<URI> urls = new ArrayList<URI>();
        for (File file : files) {
            urls.add(file.toURI());
        }
        return urls;
    }

    public Collection<File> getAsFiles() {
        return files;
    }

    public URL[] getAsURLArray() {
        Collection<URL> result = getAsURLs();
        return result.toArray(new URL[result.size()]);
    }

    public Collection<URL> getAsURLs() {
        List<URL> urls = new ArrayList<URL>();
        for (File file : files) {
            try {
                urls.add(file.toURI().toURL());
            } catch (MalformedURLException e) {
                throw UncheckedException.throwAsUncheckedException(e);
            }
        }
        return urls;
    }

    public ClassPath plus(ClassPath other) {
        if (files.isEmpty()) {
            return other;
        }
        if (other.isEmpty()) {
            return this;
        }
        return new DefaultClassPath(concat(files, other.getAsFiles()));
    }

    public ClassPath plus(Collection<File> other) {
        if (other.isEmpty()) {
            return this;
        }
        return new DefaultClassPath(concat(files, other));
    }

    private Iterable<File> concat(List<File> files1, Collection<File> files2) {
        List<File> result = new ArrayList<File>();
        result.addAll(files1);
        result.addAll(files2);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != getClass()) {
            return false;
        }
        DefaultClassPath other = (DefaultClassPath) obj;
        return files.equals(other.files);
    }

    @Override
    public int hashCode() {
        return files.hashCode();
    }
}
