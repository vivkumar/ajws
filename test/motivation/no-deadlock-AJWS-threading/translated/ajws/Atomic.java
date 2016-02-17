/*
 * 
 * Vivek Kumar, (vk.aries@gmail.com)
 * http://vivkumar.github.io/
 * February 2016
 *
 * ----------------------------------------------------------------------------------------  
 * This code is writen as per the description
 * provided in the following paper:
 * 	
 *  "A Data-Centric Approach to Synchronization", 
 *  Julian Dolby, Christian Hammer, Daniel Marino, Frank Tip, Mandana Vaziri, and Jan Vitek: 
 *  In ACM Trans. Program. Lang. Syst., 34(1): pp. 4:1-4:48, May 2012.
 *
 *  Please see the section 5.1.4 in the above paper for more information.
 * ----------------------------------------------------------------------------------------  
 *
 */

package ajws;

import ajws.OrderedLock;

public interface Atomic {
  OrderedLock getLock();
}
