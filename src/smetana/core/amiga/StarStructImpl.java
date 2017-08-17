/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * Project Info:  http://plantuml.com
 * 
 * If you like this project or if you find it useful, you can support us at:
 * 
 * http://plantuml.com/patreon (only 1$ per month!)
 * http://plantuml.com/paypal
 * 
 * This file is part of Smetana.
 * Smetana is a partial translation of Graphviz/Dot sources from C to Java.
 *
 * (C) Copyright 2009-2017, Arnaud Roques
 *
 * This translation is distributed under the same Licence as the original C program.
 * 
 * THE ACCOMPANYING PROGRAM IS PROVIDED UNDER THE TERMS OF THIS ECLIPSE PUBLIC
 * LICENSE ("AGREEMENT"). [Eclipse Public License - v 1.0]
 * 
 * ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM CONSTITUTES
 * RECIPIENT'S ACCEPTANCE OF THIS AGREEMENT.
 * 
 * You may obtain a copy of the License at
 * 
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package smetana.core.amiga;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import smetana.core.Bucket;
import smetana.core.CFunction;
import smetana.core.CFunctionImpl;
import smetana.core.CString;
import smetana.core.HardcodedStruct;
import smetana.core.JUtils;
import smetana.core.MutableDoublePtr;
import smetana.core.OFFSET;
import smetana.core.StructureDefinition;
import smetana.core.UnsupportedC;
import smetana.core.__array_of_cstring__;
import smetana.core.__array_of_integer__;
import smetana.core.__array_of_ptr__;
import smetana.core.__array_of_struct__;
import smetana.core.__ptr__;
import smetana.core.__struct__;
import smetana.core.__struct_impl__;

public class StarStructImpl extends UnsupportedC implements StarStruct {

	public static int CPT = 0;

	private final int UID = CPT++;
	private final Map<String, Area> fields;
	private final Set<String> inlineFields = new HashSet<String>();
	private final StarStruct parent;
	private final Class theClass;

	public boolean isSameThan(StarStruct other) {
		return this.UID == ((StarStructImpl) other).UID;
	}

	@Override
	public String toString() {
		return "StarStruct " + getUID36() + " " + theClass + " " + fields.keySet() + " {parent=" + parent + "}";
	}

	public Class getRealClass() {
		return theClass;
	}

	private int getIndexOf(Area searched) {
		int i = 0;
		for (Area a : fields.values()) {
			if (a == searched) {
				return i;
			}
			i++;
		}
		throw new IllegalArgumentException();
	}

	private Area getAreaByIndex(int idx) {
		int i = 0;
		for (Area a : fields.values()) {
			if (i == idx) {
				return a;
			}
			i++;
		}
		throw new IllegalArgumentException();
	}

	public __struct__ getStruct() {
		return new __struct_impl__(this);
	}

	public StarStructImpl(Class theClass, StarStruct parent) {
		this.parent = parent;
		this.theClass = theClass;
		this.fields = new LinkedHashMap<String, Area>();
		JUtils.LOG("Creation Struct " + getUID36());
		if (getUID36().equals("2tg")) {
			JUtils.LOG("It's me");
		}
		final StructureDefinition structureDefinition = StructureDefinition.from(theClass);
		final Map<String, Bucket> bucketsMap = structureDefinition.getBucketsMap();

		JUtils.LOG("FIELDS = " + theClass + " " + bucketsMap.keySet());
		for (Map.Entry<String, Bucket> ent : bucketsMap.entrySet()) {
			// JUtils.LOG("AreaStruct entrie=" + ent.getKey());
			final Bucket bucket = ent.getValue();
			fields.put(bucket.name, BucketToAreaFactory.createArea(bucket, this));
			if (bucket.inlineStruct() && bucket.functionPointer() == false) {
				inlineFields.add(bucket.name);
			}
		}
	}

	public Area getArea(String name) {
		if (fields.containsKey(name) == false) {
			System.err.println("fields=" + fields.keySet());
			throw new IllegalArgumentException("No such field " + name);
		}
		final Area result = fields.get(name);
		return result;
	}

	public String getUID36() {
		return Integer.toString(UID, 36);
	}

	// __c__

	public String getDebug(String fieldName) {
		throw new UnsupportedOperationException();
	}

	public void setInt(String fieldName, int data) {
		final AreaInt area = (AreaInt) getArea(fieldName);
		area.setInternal(data);
	}

	public void setBoolean(String fieldName, boolean data) {
		final AreaInt area = (AreaInt) getArea(fieldName);
		area.setInternal(data ? 1 : 0);
	}

	public void setDouble(String fieldName, double data) {
		// if (fieldName.equals("x") && Math.round(data) == 54) {
		// System.err.println("setDoubleTrace 54!!");
		// }
		final AreaDouble area = (AreaDouble) getArea(fieldName);
		area.setInternal(data);
	}

	public int getInt(String fieldName) {
		final AreaInt area = (AreaInt) getArea(fieldName);
		return area.getInternal();
	}

	public double getDouble(String fieldName) {
		final AreaDouble area = (AreaDouble) getArea(fieldName);
		return area.getInternal();
	}

	public boolean getBoolean(String fieldName) {
		final AreaInt area = (AreaInt) getArea(fieldName);
		if (area == null) {
			return false;
		}
		return area.getInternal() != 0;
	}

	public __ptr__ plus(int pointerMove) {
		JUtils.LOG("******************* plus =" + pointerMove);
		JUtils.LOG("me=" + this);
		if (parent != null) {
			int idx = ((StarStructImpl) parent).getIndexOf(this);
			JUtils.LOG("idx=" + idx);
			idx += pointerMove;
			final Area result = ((StarStructImpl) parent).getAreaByIndex(idx);
			JUtils.LOG("result=" + result);
			return (__ptr__) result;
		}
		throw new UnsupportedOperationException(getClass().toString());
	}

	public CString getCString(String fieldName) {
		return (CString) getArea(fieldName);
	}

	public __array_of_struct__ getArrayOfStruct(String fieldName) {
		Area area = getArea(fieldName);
		if (area instanceof StarArrayOfStruct) {
			return ((StarArrayOfStruct) area).getInternalArray();
		}
		return (__array_of_struct__) area;
	}

	public __array_of_ptr__ getArrayOfPtr(String fieldName) {
		Area area = getArea(fieldName);
		if (area instanceof StarStar) {
			return (__array_of_ptr__) ((StarStar) area).getArea();
		}
		if (area instanceof StarArrayOfPtr) {
			return (__array_of_ptr__) ((StarArrayOfPtr) area).getInternalArray();
		}
		return (__array_of_ptr__) area;
	}

	public __array_of_cstring__ getArrayOfCString(String fieldName) {
		Area area = getArea(fieldName);
		if (area instanceof StarArrayOfCString) {
			return ((StarArrayOfCString) area).getInternalArray();
		}
		return (__array_of_cstring__) area;
	}

	public __array_of_integer__ getArrayOfInteger(String fieldName) {
		Area area = getArea(fieldName);
		if (area instanceof StarArrayOfInteger) {
			return ((StarArrayOfInteger) area).getInternalArray();
		}
		return (__array_of_integer__) area;
	}

	public __struct__ getStruct(String fieldName) {
		// if (getArea(fieldName) instanceof __array__) {
		// return ((__array__) getArea(fieldName)).getStruct(0);
		// }
		if (getArea(fieldName) != null && getArea(fieldName) instanceof __ptr__ == false) {
			throw new IllegalArgumentException("Issue in getStruct with " + fieldName + " "
					+ getArea(fieldName).getClass());
		}
		final __ptr__ area = (__ptr__) getArea(fieldName);
		// if (area instanceof StarArray) {
		// final __array__ array = ((StarArray) area).getInternalArray();
		// return new __struct__<__ptr__>((StarStruct) array.getInternal(0));
		// }
		if (area instanceof HardcodedStruct) {
			return (__struct__) area;
		}
		if (area instanceof StarStructImpl == false) {
			throw new IllegalStateException(fieldName + " " + area.getClass().toString());
		}
		if (area instanceof StarStructImpl && this.inlineFields.contains(fieldName) == false) {
			throw new IllegalArgumentException(fieldName + " is NOT inline!");
		}
		return new __struct_impl__<__ptr__>((StarStructImpl) area);
	}

	public __ptr__ getPtr(String fieldName) {
		if (getArea(fieldName) != null && getArea(fieldName) instanceof __ptr__ == false) {
			throw new IllegalArgumentException("Issue in getStruct with " + fieldName + " "
					+ getArea(fieldName).getClass());
		}
		final __ptr__ area = (__ptr__) getArea(fieldName);
		if (area == null) {
			return null;
		}
		if (area instanceof StarStruct == false && area instanceof CFunctionImpl == false
				&& area instanceof CString == false && area instanceof StarStar == false
				&& area instanceof StarArrayOfPtr == false && area instanceof StarArrayOfStruct == false
				&& area instanceof StarArrayOfInteger == false && area instanceof MutableDoublePtr == false
		/* && area instanceof AreaArray == false */) {
			throw new IllegalStateException(area.getClass().toString());
		}

		if (area instanceof StarStructImpl && this.inlineFields.contains(fieldName)) {
			throw new IllegalArgumentException(fieldName + " is inline!");
		}
		return area;
	}

	public void setStruct(String fieldName, __struct__ newData) {
		if (newData == null) {
			throw new IllegalArgumentException();
		}
		if (inlineFields.contains(fieldName) == false) {
			throw new UnsupportedOperationException("IMPOSSIBLE2 " + fieldName);
		}
		final Area area = fields.get(fieldName);
		if (area == null) {
			throw new UnsupportedOperationException("IMPOSSIBLE3 " + fieldName);
		}
		if (area instanceof HardcodedStruct) {
			((HardcodedStruct) area).copyDataFrom(newData);
		} else {
			final StarStructImpl existing = (StarStructImpl) area;
			existing.copyDataFrom(newData);
		}
	}

	public void setCString(String fieldName, CString newData) {
		if (newData == null) {
			fields.put(fieldName, null);
		} else {
			fields.put(fieldName, (Area) newData);
			((CString) newData).setMyFather(this);
		}
	}

	public __ptr__ setPtr(String fieldName, __ptr__ newData) {
		if (newData instanceof CFunction) {
			fields.put(fieldName, (Area) newData);
			return newData;
		}
		if (inlineFields.contains(fieldName)) {
			throw new UnsupportedOperationException("IMPOSSIBLE5 " + fieldName);
		}
		if (newData == null) {
			fields.put(fieldName, null);
			return null;
		}
		if (newData instanceof StarStruct) {
			if (inlineFields.contains(fieldName)) {
				throw new UnsupportedOperationException("IMPOSSIBLE1");
			} else {
				fields.put(fieldName, (Area) newData);
			}
			return newData;
		}
		if (newData instanceof CString) {
			fields.put(fieldName, (Area) newData);
			((CString) newData).setMyFather(this);
			return newData;
		}
		if (newData instanceof StarArrayOfPtr) {
			fields.put(fieldName, (Area) newData);
			return newData;
		}
		if (newData instanceof StarArrayOfStruct) {
			fields.put(fieldName, (Area) newData);
			return newData;
		}
		if (newData instanceof StarStar) {
			fields.put(fieldName, (Area) newData);
			return newData;
		}
		if (newData instanceof StarArrayOfCString) {
			fields.put(fieldName, (Area) newData);
			return newData;
		}
		if (newData instanceof MutableDoublePtr) {
			fields.put(fieldName, (Area) newData);
			return newData;
		}
		if (newData instanceof StarArrayOfInteger) {
			fields.put(fieldName, (Area) newData);
			return newData;
		}
		// if (newData instanceof AreaArray) {
		// fields.put(fieldName, (Area) newData);
		// return newData;
		// }
		throw new UnsupportedOperationException("en cours1 ! " + newData.getClass());
	}

	public void memcopyFrom(Area source) {
		if (source instanceof StarArrayOfPtr) {
			final __array_of_ptr__ array = ((StarArrayOfPtr) source).getInternalArray();
			copyDataFrom((StarStructImpl) array.getInternal(0));
		} else if (source instanceof __struct__) {
			copyDataFrom((__struct__) source);
		} else {
			copyDataFrom((StarStructImpl) source);
		}
	}

	public void copyDataFrom(__struct__ other) {
		copyDataFrom(other.getInternalData());
	}

	public void setStruct(__struct__ value) {
		copyDataFrom(value);
	}

	public void copyDataFrom(__ptr__ arg) {
		JUtils.LOG("copyDataFrom I AM " + this);
		JUtils.LOG("other=" + arg);
		JUtils.LOG("FIELDS=" + fields.keySet());
		if (arg instanceof InternalData) {
			copyDataFromInternal((InternalData) arg);
			return;
		}
		if (this.getClass() != arg.getClass()) {
			throw new UnsupportedOperationException(getClass().toString());
		}
		StarStructImpl other = (StarStructImpl) arg;
		if (fields.keySet().equals(other.fields.keySet()) == false) {
			throw new IllegalStateException();
		}
		copyDataFromInternal(other);
	}

	private void copyDataFromInternal(InternalData other) {
		for (String fieldName : new TreeSet<String>(fields.keySet())) {
			Area field = fields.get(fieldName);
			final Area otherField = other.getArea(fieldName);
			if (field == null && otherField == null) {
				continue;
			}
			if (field == null) {
				if (otherField instanceof StarStructImpl && inlineFields.contains(fieldName) == false) {
					fields.put(fieldName, otherField);
				} else if (otherField instanceof CString) {
					fields.put(fieldName, otherField);
				} else if (otherField instanceof StarStar) {
					fields.put(fieldName, otherField);
				} else if (otherField instanceof StarArrayOfPtr) {
					fields.put(fieldName, otherField);
				} else if (otherField instanceof CFunction) {
					fields.put(fieldName, otherField);
				} else if (otherField instanceof StarArrayOfStruct) {
					fields.put(fieldName, otherField);
				} else {
					System.err.println("XX otherField = " + otherField);
					throw new UnsupportedOperationException(otherField.getClass().toString());
				}
			} else if (field instanceof AreaInt) {
				field.memcopyFrom(otherField);
			} else if (field instanceof AreaDouble) {
				field.memcopyFrom(otherField);
			} else if (field instanceof CString) {
				field.memcopyFrom(otherField);
			} else if (field instanceof StarStar) {
				if (otherField == null) {
					fields.put(fieldName, null);
				} else {
					field.memcopyFrom(otherField);
				}
			} else if (field instanceof StarStructImpl && inlineFields.contains(fieldName)) {
				field.memcopyFrom(otherField);
			} else if (field instanceof StarStructImpl && inlineFields.contains(fieldName) == false) {
				fields.put(fieldName, otherField);
			} else if (field instanceof StarArrayOfPtr) {
				field.memcopyFrom(otherField);
			} else if (field instanceof HardcodedStruct) {
				field.memcopyFrom(otherField);
			} else {
				System.err.println("fieldName=" + fieldName + " " + field);
				System.err.println("otherField = " + otherField);
				throw new UnsupportedOperationException(field.getClass().toString());
			}
		}
	}

	public Object call(String fieldName, Object... args) {
		final CFunction area = (CFunction) getArea(fieldName);
		return area.exe(args);
	}

	public __ptr__ castTo(Class dest) {
		JUtils.LOG("******************* castTo =" + dest);
		JUtils.LOG("me=" + this);
		if (theClass == dest) {
			return this;
		}
		if (parent != null && ((StarStructImpl) parent).theClass == dest) {
			JUtils.LOG("IT's my father!");
			return parent;
		}

		final Area first = fields.values().iterator().next();
		JUtils.LOG("first=" + first);
		if (first instanceof StarStructImpl) {
			final StarStructImpl first2 = (StarStructImpl) first;
			JUtils.LOG("first.parent=" + first2.parent);
			if (first2.parent != this) {
				throw new IllegalStateException();
			}
			if (dest == first2.theClass) {
				return first2;
			}
		}
		throw new UnsupportedOperationException();
	}

	public Object addVirtualBytes(int virtualBytes) {
		JUtils.LOG("#### addVirtualBytes " + virtualBytes);
		if (virtualBytes == 0) {
			return this;
		}
		JUtils.LOG("this=" + toString());
		if (virtualBytes < 0) {
			final OFFSET offset = OFFSET.fromInt(-virtualBytes);
			JUtils.LOG("OFFSET1=" + offset);
			if (parent == null) {
				throw new UnsupportedOperationException("No father! How to go back?");
			}
			JUtils.LOG("father=" + parent);
			if (((StarStructImpl) parent).theClass != offset.getTheClass()) {
				throw new UnsupportedOperationException("Bad class matching1!");
			}
			final Object checking = parent.addVirtualBytes(-virtualBytes);
			JUtils.LOG("checking=" + checking);
			if (checking != this) {
				throw new UnsupportedOperationException("Cheking fail!");
			}
			return parent;
		}
		final OFFSET offset = OFFSET.fromInt(virtualBytes);
		JUtils.LOG("OFFSET2=" + offset);
		final String field = offset.getField();
		JUtils.LOG("field=" + field);
		JUtils.LOG("fields=" + fields.keySet());
		final Area result = fields.get(field);
		if (result == null && parent != null) {
			return parent.addVirtualBytes(virtualBytes);
		}
		if (result == null) {
			throw new UnsupportedOperationException();
		}
		return result;
	}

	// __c__

}
