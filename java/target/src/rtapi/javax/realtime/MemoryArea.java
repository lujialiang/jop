/*
  This file is part of JOP, the Java Optimized Processor
    see <http://www.jopdesign.com/>
  This subset of javax.realtime is provided for the JSR 302
  Safety Critical Specification for Java

  Copyright (C) 2008, Martin Schoeberl (martin@jopdesign.com)

  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package javax.realtime;

public abstract class MemoryArea {

	public MemoryArea(long size) {
	}

	public void enter(Runnable logic) {
		// dummy enter
	}
	
	public long size() {
		// dummy return
		return 0L;
	}

}


// the SCJ version
//package javax.realtime;
//
//import javax.safetycritical.annotate.Allocate;
//import javax.safetycritical.annotate.MemoryAreaEncloses;
//import javax.safetycritical.annotate.SCJAllowed;
//import javax.safetycritical.annotate.SCJRestricted;
//import static javax.safetycritical.annotate.Level.INFRASTRUCTURE;;
//
///**
// * MemoryArea
// *
// */
//@SCJAllowed
//public abstract class MemoryArea implements AllocationContext
//{
//  /**
//   * 
//   */
//  @SCJAllowed(INFRASTRUCTURE)
//  protected MemoryArea() {/* ... */}
//
//  @SCJAllowed
//  @SCJRestricted(maySelfSuspend = false)
//  public static MemoryArea getMemoryArea(Object object)
//  {
//    return null;
//  }
//
//  @Override
//  @SCJAllowed(INFRASTRUCTURE)
//  @SCJRestricted(maySelfSuspend = false)
//  public abstract void enter(Runnable logic);
//
//  
//  /**
//   * TBD: This method has no object argument, so this commentary is
//   * not meaningful.
//   *
//   * Execute <code>logic</code> in the memory area containing
//   * <code>object</code>.
//   *
//   * "@param" object is the reference for determining the area in which to
//   * execute <code>logic</code>.
//   *
//   * @param logic is the runnable to execute in the memory area
//   * containing <code>object</code>.
//   */
//  @Override
//  @Allocate(sameAreaAs={"object"})
//  @MemoryAreaEncloses(inner = { "logic" }, outer = { "this" })
//  @SCJAllowed
//  @SCJRestricted(maySelfSuspend = false)
//  public void executeInArea(Runnable logic) throws InaccessibleAreaException
//  {
//  }
//
//
//  /**
//   * TBD: this method has no object argument, so this commentary is
//   * not meaningful
//   *
//   * This method creates an object of type <code>type</code> in the memory
//   * area containing <code>object</code>.
//   *
//   * "@param" object is the reference for determining the area in which to
//   * allocate the array.
//   *
//   * @param type is the type of the object returned.
//   *
//   * @return a new object of type <code>type</code>
//   *
//   * @throws IllegalAccessException
//   * @throws IllegalArgumentException
//   * @throws InstantiationException
//   * @throws OutOfMemoryError
//   * @throws ExceptionInInitializerError
//   * @throws InaccessibleAreaException
//   */
//  @Override
//  @Allocate(sameAreaAs={"this.area"})
//  @SCJAllowed
//  @SCJRestricted(maySelfSuspend = false)
//  public Object newInstance(Class type)
////    throws IllegalArgumentException, InstantiationException,
////       OutOfMemoryError, InaccessibleAreaException
//  {
//    return null; // dummy return
//  }
//
//
//  /**
//   * This method creates an object of type <code>type</code> in the memory
//   * area containing <code>object</code>.
//   *
//   * @param type is the type of the object returned.
//   *
//   * @return a new object of type <code>type</code>
//   */
//  @Override
//  @Allocate(sameAreaAs = {"this.area"})
//  @SCJAllowed
//  @SCJRestricted(maySelfSuspend = false)
//  public Object newArray(Class type, int size)
//  {
//    return null; // dummy return
//  }
//
// 
//  /**
//   * This method creates an array of type <code>type</code> in the memory
//   * area containing <code>object</code>.
//   *
//   * @param object is the reference for determining the area in which to
//   * allocate the array.
//   *
//   * @param type is the type of the array element for the returned
//   * array.
//   *
//   * @param size is the size of the array to return.
//   *
//   * @return a new array of element type <code>type</code> with size
//   * <code>size</code>.
//   */
//  @Allocate(sameAreaAs={"object"})
//  @SCJAllowed
//  @SCJRestricted(maySelfSuspend = false)
//  public Object newArrayInArea(Object object, Class type, int size)
//  {
//    return getMemoryArea(object).newArray(type, size);
//  }
//
//  @Override
//  @SCJAllowed
//  @SCJRestricted(maySelfSuspend = false)
//  public abstract long memoryConsumed();
//
//  @Override
//  @SCJAllowed
//  @SCJRestricted(maySelfSuspend = false)
//  public abstract long memoryRemaining();
//
//  @Override
//  @SCJAllowed
//  @SCJRestricted(maySelfSuspend = false)
//  public abstract long size();
//}
