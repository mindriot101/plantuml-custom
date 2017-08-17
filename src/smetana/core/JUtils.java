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

import h.Agdesc_s;
import h.Agedge_s;
import h.Agedgeinfo_t;
import h.Agiddisc_s;
import h.Agmemdisc_s;
import h.Agtag_s;
import h.ST_Agdesc_s;
import h.ST_Agiddisc_s;
import h.ST_Agmemdisc_s;
import h.ST_Agtag_s;
import h.ST_arrowname_t;
import h.ST_arrowtype_t;
import h.ST_boxf;
import h.ST_dtdisc_s;
import h.ST_dtmethod_s;
import h.ST_elist;
import h.ST_nlist_t;
import h.ST_pointf;
import h.ST_port;
import h._dtdisc_s;
import h._dtmethod_s;
import h.arrowname_t;
import h.arrowtype_t;
import h.bezier;
import h.boxf;
import h.elist;
import h.nlist_t;
import h.pointf;
import h.port;
import h.splines;
import smetana.core.amiga.StarArrayOfInteger;
import smetana.core.amiga.StarArrayOfPtr;
import smetana.core.amiga.StarArrayOfStruct;
import smetana.core.amiga.StarStar;
import smetana.core.amiga.StarStruct;
import smetana.core.amiga.StarStructImpl;

// http://docs.oracle.com/javase/specs/jls/se5.0/html/expressions.html#15.7.4
// http://www.jbox.dk/sanos/source/lib/string.c.html

public class JUtils {

	public static int USHRT_MAX = 65535;

	public static size_t sizeof(Class cl) {
		return new size_t_struct(cl);
	}

	public static size_t sizeof(__ptr__ element) {
		return new size_t_of_element(element);
	}

	public static size_t sizeof(String name, int sz) {
		if (name.equals("char*")) {
			return new size_t_array_of_charstars(sz);
		}
		throw new UnsupportedOperationException();
	}

	public static size_t sizeof(Class cl, int nb) {
		return new size_t_array_of_something(cl, nb);
	}

	public static size_t size_t_array_of_integer(int nb) {
		return new size_t_array_of_integer(nb);
	}

	public static size_t sizeof_starstar_empty(Class cl, int nb) {
		return new size_t_array_of_array_of_something_empty(cl, nb);
	}

	public static int strcmp(CString s1, CString s2) {
		return s1.compareTo(s2);
	}

	public static int strncmp(CString s1, CString s2, int n) {
		return s1.compareTo(s2, n);
	}

	public static CString strstr(CString s1, CString s2) {
		throw new UnsupportedOperationException("s1=" + s1 + " s2=" + s2);
	}

	public static void strncpy(CString destination, CString source, int nb) {
		destination.copyFrom(source, nb);
	}

	public static CString strchr(CString str, char c) {
		return str.strchr(c);
	}

	public static int strtol(CString str, CString[] endptr, int base) {
		if (base != 10) {
			throw new IllegalArgumentException();
		}
		CString end = str;
		int result = Integer.parseInt(end.getContent());
		endptr[0] = end.plus(("" + result).length());
		return result;
	}

	public static double strtod(CString str, CString[] endptr) {
		final double result = Double.parseDouble(str.getContent());
		return result;
	}

	public static double atof(CString str) {
		return Double.parseDouble(str.getContent());
	}

	public static int memcmp(__ptr__ s1, __ptr__ s2, int sz) {
		throw new UnsupportedOperationException("s1=" + s1 + " s2=" + s2 + " sz=" + sz);
	}

	public static void memset(__ptr__ obj, int value, size_t nbytes) {
		if (value != 0) {
			throw new UnsupportedOperationException();
		}
	}

	public static int strlen(CString s) {
		return s.length();
	}

	public static double abs(double x) {
		return Math.abs(x);
	}

	public static double cos(double x) {
		return Math.cos(x);
	}

	public static double sin(double x) {
		return Math.sin(x);
	}

	public static double sqrt(double x) {
		return Math.sqrt(x);
	}

	public static double atan2(double a, double b) {
		return Math.atan2(a, b);
	}

	public static double pow(double a, double b) {
		return Math.pow(a, b);
	}

	public static boolean isdigit(char c) {
		return Character.isDigit(c);
	}

	public static int atoi(CString s) {
		return Integer.parseInt(s.getContent());
	}

	public static char tolower(char c) {
		return Character.toLowerCase(c);
	}

	public static CFunction function(Class codingClass, String name) {
		return CFunctionImpl.create(codingClass, name);
	}

	public static int enumAsInt(Class enumClass, String name) {
		CEnumInterpretor interpretor = new CEnumInterpretor(enumClass);
		final int result = interpretor.valueOf(name);
		JUtils.LOG("result for " + name + " is " + result);
		return result;
	}

	public static CString getenv(CString var) {
		return null;
	}

	public static void LOG(String s) {
		// System.err.println(s);
	}

	public static void LOG2(String s) {
		// System.err.println(s);
	}

	public static boolean EQ(Object o1, Object o2) {
		if (o1 == o2) {
			return true;
		}
		if (o1 == null && o2 != null) {
			return false;
		}
		if (o2 == null && o1 != null) {
			return false;
		}
		// if (o1 instanceof AreaArray && o2 instanceof AreaArray) {
		// return ((AreaArray) o1).isSameThan((AreaArray) o2);
		// }
		if (o1 instanceof StarStruct && o2 instanceof StarStruct) {
			return ((StarStruct) o1).isSameThan((StarStruct) o2);
		}
		if (o1 instanceof CString && o2 instanceof CString) {
			return ((CString) o1).isSameThan((CString) o2);
		}
		if (o1 instanceof StarArrayOfPtr && o2 instanceof StarArrayOfPtr) {
			return ((StarArrayOfPtr) o1).isSameThan((StarArrayOfPtr) o2);
		}
		if (o1 instanceof StarStar && o2 instanceof StarStruct) {
			__ptr__ o1b = ((StarStar) o1).getPtr();
			if (((StarStruct) o1b).getRealClass() != ((StarStruct) o2).getRealClass()) {
				throw new UnsupportedOperationException();
			}
			return EQ(o1b, o2);
		}
		if (o1 instanceof StarStruct && o2 instanceof StarArrayOfPtr) {
			__ptr__ o2b = ((StarArrayOfPtr) o2).getPtrForEquality();
			if (((StarStruct) o1).getRealClass() != ((StarStruct) o2b).getRealClass()) {
				throw new UnsupportedOperationException();
			}
			return EQ(o1, o2b);
		}
		if (o1 instanceof StarStruct && o2 instanceof StarArrayOfStruct) {
			__ptr__ o2b = ((StarArrayOfStruct) o2).getPtrForEquality();
			if (((StarStruct) o1).getRealClass() != ((StarStruct) o2b).getRealClass()) {
				throw new UnsupportedOperationException();
			}
			return EQ(o1, o2b);
		}
		if (o1 instanceof StarStar && o2 instanceof StarArrayOfPtr) {
			__ptr__ o1b = ((StarStar) o1).getPtr();
			__ptr__ o2b = ((StarArrayOfPtr) o2).getPtr();
			if (((StarStruct) o1b).getRealClass() != ((StarStruct) o2b).getRealClass()) {
				throw new UnsupportedOperationException();
			}
			return EQ(o1b, o2b);
		}
		if (o1 instanceof StarArrayOfStruct && o2 instanceof StarArrayOfStruct) {
			StarArrayOfStruct oo1 = (StarArrayOfStruct) o1;
			StarArrayOfStruct oo2 = (StarArrayOfStruct) o2;
			return oo1.isSameThan(oo2);
		}
		if (o1 instanceof StarArrayOfPtr && o2 instanceof StarArrayOfStruct) {
			StarArrayOfPtr oo1 = (StarArrayOfPtr) o1;
			StarArrayOfStruct oo2 = (StarArrayOfStruct) o2;
			__struct__ s1 = oo1.getStruct();
			__struct__ s2 = oo2.getStruct();
			boolean result = s1.getInternalData().isSameThan(s2.getInternalData());
			return result;
		}
		System.err.println("o1=" + o1.getClass() + " " + o1);
		System.err.println("o2=" + o2.getClass() + " " + o2);
		throw new UnsupportedOperationException();
	}

	public static boolean NEQ(Object o1, Object o2) {
		return EQ(o1, o2) == false;
	}

	public static void qsort(__ptr__ array, int nb, size_t size, CFunction compare) {
		if (nb <= 1) {
			return;
		}
		JUtils.LOG("array=" + array);
		JUtils.LOG("nb=" + nb);
		JUtils.LOG("size=" + size);
		JUtils.LOG("compare=" + compare);
		boolean change;
		do {
			change = false;
			for (int i = 0; i < nb - 1; i++) {
				__ptr__ element1 = array.plus(i);
				__ptr__ element2 = array.plus(i + 1);
				Integer cmp = (Integer) compare.exe(element1, element2);
				JUtils.LOG("cmp=" + cmp);
				if (cmp.intValue() > 0) {
					change = true;
					if (array instanceof StarArrayOfInteger) {
						((StarArrayOfInteger) array).swap(i, i + 1);
					} else {
						((StarStar) array).swap(i, i + 1);
					}
				}
			}
		} while (change);
		for (int i = 0; i < nb - 1; i++) {
			__ptr__ element1 = array.plus(i);
			__ptr__ element2 = array.plus(i + 1);
			JUtils.LOG("element1=" + element1);
			JUtils.LOG("element2=" + element2);
			Integer cmp = (Integer) compare.exe(element1, element2);
			JUtils.LOG("cmp=" + cmp);
			if (cmp.intValue() > 0) {
				throw new IllegalStateException();
			}
		}
	}

	static public int setjmp(jmp_buf jmp) {
		// if (jmp.hasBeenCalled()) {
		// throw new UnsupportedOperationException();
		// }
		jmp.saveCallingEnvironment();
		return 0;
	}

	// DEBUG

	public static void printDebugEdge(Agedge_s e) {
		System.err.println("*********** PRINT EDGE ********** ");
		final Agedgeinfo_t data = (Agedgeinfo_t) Macro.AGDATA(e).castTo(Agedgeinfo_t.class);
		final splines splines = (splines) data.getPtr("spl");
		__struct__<boxf> bb = splines.getStruct("bb");
		final bezier list = (bezier) splines.getPtr("list");
		System.err.println("splines.size=" + splines.getInt("size"));
		System.err.println("bb.LL=" + pointftoString(bb.getStruct("LL")));
		System.err.println("bb.UR=" + pointftoString(bb.getStruct("UR")));
		printDebugBezier((bezier) splines.getPtr("list").getPtr());

	}

	private static String pointftoString(__struct__<pointf> point) {
		final StringBuilder sb = new StringBuilder();
		sb.append("(");
		sb.append(point.getDouble("x"));
		sb.append(" ; ");
		sb.append(point.getDouble("y"));
		sb.append(")");
		return sb.toString();
	}

	private static void printDebugBezier(bezier bezier) {
		System.err.println("bezier.size=" + bezier.getInt("size"));
		System.err.println("bezier.sflag=" + bezier.getInt("sflag"));
		System.err.println("splines.eflag=" + bezier.getInt("eflag"));
		System.err.println("bezier.sp=" + pointftoString(bezier.getStruct("sp")));
		System.err.println("bezier.ep=" + pointftoString(bezier.getStruct("ep")));
		for (int i = 0; i < bezier.getInt("size"); i++) {
			final __ptr__ pt = bezier.getPtr("list").plus(i).getPtr();
			System.err.println("pt=" + pointftoString(pt));
		}
	}

	private static String pointftoString(__ptr__ point) {
		final StringBuilder sb = new StringBuilder();
		sb.append("(");
		sb.append(point.getDouble("x"));
		sb.append(" ; ");
		sb.append(point.getDouble("y"));
		sb.append(")");
		return sb.toString();
	}

	public static <C extends __ptr__> __struct__<C> from(Class<C> theClass) {
		if (theClass == _dtmethod_s.class) {
			return new ST_dtmethod_s();
		}
		if (theClass == _dtdisc_s.class) {
			return new ST_dtdisc_s();
		}
		if (theClass == Agdesc_s.class) {
			return new ST_Agdesc_s();
		}
		if (theClass == Agtag_s.class) {
			return new ST_Agtag_s();
		}
		if (theClass == Agiddisc_s.class) {
			return new ST_Agiddisc_s();
		}
		if (theClass == Agmemdisc_s.class) {
			return new ST_Agmemdisc_s();
		}
		if (theClass == nlist_t.class) {
			return new ST_nlist_t();
		}
		if (theClass == arrowname_t.class) {
			return new ST_arrowname_t();
		}
		if (theClass == arrowtype_t.class) {
			return new ST_arrowtype_t();
		}
		if (theClass == elist.class) {
			return new ST_elist();
		}
		if (theClass == pointf.class) {
			return new ST_pointf();
		}
		if (theClass == boxf.class) {
			return new ST_boxf();
		}
		if (theClass == port.class) {
			return new ST_port();
		}
		return new __struct_impl__<C>(theClass);
	}

	public static StarStruct create(Class theClass, StarStruct parent) {
		if (theClass == _dtmethod_s.class) {
			throw new IllegalArgumentException(theClass.toString());
		}
		if (theClass == _dtdisc_s.class) {
			return new ST_dtdisc_s(parent);
		}
		if (theClass == Agdesc_s.class) {
			return new ST_Agdesc_s(parent);
		}
		if (theClass == Agtag_s.class) {
			return new ST_Agtag_s(parent);
		}
		if (theClass == Agiddisc_s.class) {
			throw new IllegalArgumentException(theClass.toString());
		}
		if (theClass == Agmemdisc_s.class) {
			throw new IllegalArgumentException(theClass.toString());
		}
		if (theClass == nlist_t.class) {
			return new ST_nlist_t(parent);
		}
		if (theClass == arrowname_t.class) {
			throw new IllegalArgumentException(theClass.toString());
		}
		if (theClass == arrowtype_t.class) {
			throw new IllegalArgumentException(theClass.toString());
		}
		if (theClass == elist.class) {
			return new ST_elist(parent);
		}
		if (theClass == pointf.class) {
			return new ST_pointf(parent);
		}
		if (theClass == boxf.class) {
			return new ST_boxf(parent);
		}
		if (theClass == port.class) {
			return new ST_port(parent);
		}
		return new StarStructImpl(theClass, parent);
	}

}
