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

package org.codehaus.gant.tests

/**
 *  A test to ensure that the target listing works. 
 *
 *  @author Russel Winder <russel.winder@concertant.com>
 */
final class DryRun_Test extends GantTestCase {
  void setUp ( ) {
    super.setUp ( )
    script = '''
target ( something : "Do something." ) { echo ( message : "Did something." ) }
target ( somethingElse : "Do something else." ) { echo ( message : "Did something else." ) }
'''
  }
  void testMissingDefault ( ) {
    assertEquals ( -12 , gant.processArgs ( [ '-n' ,  '-f' ,  '-'  ] as String[] ) )
    assertEquals ( 'Target default does not exist.\n' , output )
  }
  void testMissingNamedTarget ( ) {
    assertEquals ( -11 , gant.processArgs ( [ '-n' ,  '-f' ,  '-'  , 'blah'] as String[] ) )
    assertEquals ( ''' [property] environment : 'environment'
Target blah does not exist.
''' , output ) 
  }
  void testSomething ( ) {
    assertEquals ( 0 , gant.processArgs ( [ '-n' ,  '-f' ,  '-'  , 'something'] as String[] ) )
    assertEquals ( ''' [property] environment : 'environment'
     [echo] message : 'Did something.'
''' , output ) 
  }
  void testSomethingElse ( ) {
    assertEquals ( 0 , gant.processArgs ( [ '-n' ,  '-f' ,  '-'  , 'somethingElse'] as String[] ) )
    assertEquals (  ''' [property] environment : 'environment'
     [echo] message : 'Did something else.'
''' , output ) 
  }
}
