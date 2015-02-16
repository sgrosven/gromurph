/*
  File: ThreadFactory.java

  Originally written by Doug Lea and released into the public domain.
  This may be used for any purposes whatsoever without acknowledgment.
  Thanks for the assistance and support of Sun Microsystems Labs,
  and everyone contributing, testing, and using this code.

  History:
  Date       Who                What
  30Jun1998  dl               Create public version
*/

package org.gromurph.util.swingworker;

/**
 * Interface describing any class that can generate
 * new Thread objects. Using ThreadFactories removes
 * hardwiring of calls to <code>new Thread</code>, enabling
 * applications to use special thread subclasses, default
 * prioritization settings, etc.
 * <p>
* <p>[<a href="http://gee.cs.oswego.edu/dl"> Introduction to this package. </a>]
 **/

public interface ThreadFactory {
  /** 
   * Create a new thread that will run the given command when started
   **/
  public Thread newThread(Runnable command);
}
