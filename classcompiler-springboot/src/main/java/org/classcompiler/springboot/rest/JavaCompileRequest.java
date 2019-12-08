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
package org.classcompiler.springboot.rest;

public class JavaCompileRequest {

    private String fullyQualifiedClassName;
    private String javaSource;

    public static JavaCompileRequest of(String fullyQualifiedClassName, String javaSource) {
	final JavaCompileRequest jcr = new JavaCompileRequest();
	jcr.setFullyQualifiedClassName(fullyQualifiedClassName);
	jcr.setJavaSource(javaSource);
	return jcr;
    }

    public String getFullyQualifiedClassName() {
	return fullyQualifiedClassName;
    }

    public void setFullyQualifiedClassName(String fullyQualifiedClassName) {
	this.fullyQualifiedClassName = fullyQualifiedClassName;
    }

    public String getJavaSource() {
	return javaSource;
    }

    public void setJavaSource(String javaSource) {
	this.javaSource = javaSource;
    }
}
