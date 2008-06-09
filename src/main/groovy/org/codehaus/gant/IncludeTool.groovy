//  Gant -- A Groovy build framework based on scripting Ant tasks.
//
//  Copyright © 2006-8 Russel Winder
//
//  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
//  compliance with the License. You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software distributed under the License is
//  distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
//  implied. See the License for the specific language governing permissions and limitations under the
//  License.

package org.codehaus.gant

/**
 *  An instance of this class is provided to each Gant script for including tools.  A tool is a class that
 *  provides Gant related facilities.  The class must have a single parameter constructor which is a
 *  <code>Map</code>.  The map contains a binding of various useful things, in particular there is always an
 *  entry 'Ant' to give access to the global static instance of <code>AntBuilder</code>.
 *
 *  @author Russel Winder <russel.winder@concertant.com>
 */
class IncludeTool extends AbstractInclude {
  IncludeTool ( GantBinding binding ) { super ( binding  ) }
  def leftShift ( Class theClass ) {
    def className = theClass.name
    if ( ! ( className in loadedClasses ) ) {
      def index = className.lastIndexOf ( '.' ) + 1
      makeBindingEntry ( className[index..-1] , createInstance ( theClass ) )
      loadedClasses << className
    }
    this
  }
  def leftShift ( File file ) {
    def className = file.name
    if ( ! ( className in loadedClasses ) ) {
      className = className[ 0 ..< className.lastIndexOf ( '.' ) ]
      def theClass = readFile ( file , true )
      makeBindingEntry ( className , createInstance ( theClass ) )
      loadedClasses << className
    }
    this
  }
  def leftShift ( String script ) {
    def className = ''
    final javaIdentifierRegexAsString = /\b\p{javaJavaIdentifierStart}(?:\p{javaJavaIdentifierPart})*\b/
    final javaQualifiedNameRegexAsString = /\b${javaIdentifierRegexAsString}(?:[.\/]${javaIdentifierRegexAsString})*\b/
    script.eachMatch ( /(?:(?:public|final))*[ \t\r\n]*class[ \t\r\n]*(${javaIdentifierRegexAsString})[ \t\r\n]*(?:extends[ \t\r\n]*${javaQualifiedNameRegexAsString})*[ \t\r\n]*\{/ ) { opening , name ->
      //  There has to be a better way of doing this.  Assume that the first instance of the class
      //  declaration is the one we want and that any later ones are not an issue.
      if ( className == '' ) { className = name }
    }
    if ( ! ( className in loadedClasses ) ) {
      loadedClasses << className
      def theClass = binding.groovyShell.evaluate ( script + " ; return ${className}" )
      makeBindingEntry ( className , createInstance ( theClass ) )
    }
    this
  }
  def multiply ( Map keywordParameters ) {
    if ( pendingClass != null ) {
      def className = pendingClass.name
      if ( ! ( className in loadedClasses ) ) {
        def index = className.lastIndexOf ( '.' ) + 1
        makeBindingEntry ( className[index..-1] , createInstance ( pendingClass , keywordParameters ) )
        loadedClasses << className
      }
      pendingClass = null
    }
    this
  }
private void makeBindingEntry ( String name , object ) {
    def initialLetter = name[0] as Character
    def transformedName = ( Character.toLowerCase ( initialLetter ) as String ) + name[1..-1]
    try {
      binding.getVariable ( transformedName )
      throw new RuntimeException ( "Attempt to redefine name " + transformedName ) 
    }
    catch ( MissingPropertyException nspe ) {
      binding.setVariable ( transformedName , object )
    }
  }
}
