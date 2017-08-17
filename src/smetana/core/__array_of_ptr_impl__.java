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

package smetana.core;

import h.ST_boxf;

import java.util.ArrayList;
import java.util.List;

import smetana.core.amiga.Area;
import smetana.core.amiga.AreaInt;
import smetana.core.amiga.BuilderArea;
import smetana.core.amiga.StarArrayOfPtr;
import smetana.core.amiga.StarStruct;
import smetana.core.amiga.StarStructImpl;

public class __array_of_ptr_impl__ implements __array_of_ptr__ {

	private final List<Area> data;
	private final int currentPos;
	private final BuilderArea builder;

	private final int UID = StarStructImpl.CPT++;

	public String getUID36() {
		return Integer.toString(UID, 36);
	}

	public void memcopyFrom(Area source) {
		__array_of_ptr_impl__ other = (__array_of_ptr_impl__) source;
		System.err.println("sizeMe=" + this.data.size());
		System.err.println("sizeOt=" + other.data.size());

		throw new UnsupportedOperationException();
	}

	public void swap(int i, int j) {
		Area e1 = data.get(i);
		Area e2 = data.get(j);
		data.set(i, e2);
		data.set(j, e1);
	}

	@Override
	public String toString() {
		if (data.get(0) != null) {
			return "__array__ " + getUID36() + " " + currentPos + "/" + data.size() + " " + data.get(0).toString();
		}
		return "__array__ " + getUID36() + " " + currentPos + "/" + data.size();
	}

	public void realloc(int nb) {
		while (data.size() < nb + currentPos) {
			data.add(builder.createArea());
		}
	}

	public __ptr__ asPtr() {
		return new StarArrayOfPtr(this);
	}

	public int comparePointerInternal(__array_of_ptr__ other2) {
		final __array_of_ptr_impl__ other = (__array_of_ptr_impl__) other2;
		if (this.data != other.data) {
			throw new IllegalArgumentException();
		}
		return this.currentPos - other.currentPos;
	}

	public static __array_of_ptr__ malloc_allocated(final Class cl, int nb) {
		return new __array_of_ptr_impl__(nb, new BuilderArea() {
			public Area createArea() {
				return JUtils.create(cl, null);
			}
		});
	}

	public static __array_of_ptr__ malloc_empty(int nb) {
		return new __array_of_ptr_impl__(nb, new BuilderArea() {
			public Area createArea() {
				return null;
			}
		});
	}

	private __array_of_ptr_impl__(List<Area> data, int currentPos, BuilderArea builderArea) {
		this.data = data;
		this.currentPos = currentPos;
		this.builder = builderArea;
		check();
	}

	private __array_of_ptr_impl__(int size, BuilderArea builder) {
		this.data = new ArrayList<Area>();
		this.builder = builder;
		this.currentPos = 0;
		for (int i = 0; i < size; i++) {
			data.add(builder.createArea());
		}
		check();
	}

	private void check() {
		if (getUID36().equals("194")) {
			JUtils.LOG("It's me");
		}
	}

	public __array_of_ptr_impl__ move(int delta) {
		return new __array_of_ptr_impl__(data, currentPos + delta, builder);
	}

	public __array_of_ptr_impl__ plus(int delta) {
		return move(delta);
	}

	public Area getInternal(int idx) {
		return data.get(idx + currentPos);
	}

	public void setInternalByIndex(int idx, Area value) {
		if (value == this) {
			throw new IllegalArgumentException();
		}
		if (value instanceof __array_of_ptr_impl__) {
			throw new IllegalArgumentException();
		}
		data.set(idx + currentPos, value);
	}

	//

	public int getInt() {
		return ((AreaInt) getInternal(0)).getInternal();
	}

	public CString getCString() {
		return (CString) getInternal(0);
	}

	public __ptr__ getPtr() {
		if (getInternal(0) instanceof __struct__) {
			return ((__struct__) getInternal(0)).amp();
		}
		return (__ptr__) getInternal(0);
	}

	public __struct__ getStruct() {
		if (getInternal(0) instanceof __ptr__) {
			return getPtr().getStruct();
		}
		return (__struct__) getInternal(0);
	}

	public void setInt(int value) {
		((AreaInt) getInternal(0)).setInternal(value);
	}

	public void setCString(CString value) {
		setInternalByIndex(0, value);
	}

	public void setPtr(__ptr__ value) {
		setInternalByIndex(0, (Area) value);
	}

	public void setStruct(__struct__ value) {
		final Area area = getInternal(0);
		if (area instanceof StarStruct) {
			((StarStruct) area).copyDataFrom(value);
		} else if (area instanceof StarArrayOfPtr) {
			((StarArrayOfPtr) area).copyDataFrom(value);
		} else {
			((__struct__) area).___(value);
		}
	}

	public double getDouble(String fieldName) {
		final Area tmp1 = getInternal(0);
		return ((StarStruct) tmp1).getDouble(fieldName);
	}

	public void setDouble(String fieldName, double value) {
		final Area tmp1 = getInternal(0);
		if (tmp1 instanceof __struct__) {
			((__struct__) tmp1).setDouble(fieldName, value);
			return;
		}
		((StarStruct) tmp1).setDouble(fieldName, value);
	}

	public __struct__ getStruct(String fieldName) {
		final StarStruct TMP = (StarStruct) getInternal(0);
		if (TMP instanceof ST_boxf) {
			return ((ST_boxf) TMP).getStructInternal(fieldName);
		}
		return TMP.getStruct(fieldName);
	}

}
