/*******************************************************************************
 * Copyright (c) 2019, Xavier Miret Andres <xavier.mires@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *******************************************************************************/
package org.classcompiler.compiler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PackageNode {

    private String name;
    private PackageNode parent;
    private Map<String, PackageNode> children;
    private List<JavaClass> classes;

    public PackageNode(String name) {
	this.name = name;
	this.parent = null;
	this.children = new HashMap<>();
	this.classes = new ArrayList<>();
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public List<JavaClass> getClasses() {
	return classes;
    }

    public PackageNode getParent() {
	return parent;
    }

    public void setParent(PackageNode parent) {
	this.parent = parent;
    }

    public boolean hasParent() {
	return parent != null;
    }

    public void addChild(PackageNode child) {
	children.put(child.getName(), child);
    }
    
    public PackageNode getChild(String name) {
	return children.get(name);
    }

    public Collection<PackageNode> getChildren() {
	return children.values();
    }

    public boolean hasChildren() {
	return !children.isEmpty();
    }

    @Override
    public String toString() {
	return name;
    }
}
