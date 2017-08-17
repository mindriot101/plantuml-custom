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

import static smetana.core.JUtils.function;
import gen.lib.cdt.dttree__c;
import gen.lib.cgraph.attr__c;
import gen.lib.cgraph.edge__c;
import gen.lib.cgraph.graph__c;
import gen.lib.cgraph.id__c;
import gen.lib.cgraph.mem__c;
import gen.lib.cgraph.node__c;
import gen.lib.cgraph.utils__c;
import gen.lib.common.arrows__c;
import gen.lib.common.shapes__c;
import gen.lib.dotgen.dotsplines__c;
import h.Agedge_s;
import h.Agnode_s;
import h.Agraph_s;
import h.Agsubnode_s;
import h.Agsym_s;
import h.Agtag_s;
import h.ST_Agdesc_s;
import h.ST_Agiddisc_s;
import h.ST_Agmemdisc_s;
import h.ST_arrowname_t;
import h.ST_arrowtype_t;
import h.ST_dtdisc_s;
import h.ST_dtmethod_s;
import h.ST_elist;
import h.ST_nlist_t;
import h.ST_pointf;
import h.ST_port;
import h._dt_s;
import h._dtmethod_s;
import h.arrowname_t;
import h.arrowtype_t;
import h.boxf;
import h.deque_t;
import h.pointf;
import h.pointnlink_t;
import h.polygon_t;
import h.refstr_t;
import h.shape_desc;
import h.shape_functions;
import h.splineInfo;
import h.textfont_t;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Z {

	private static final ThreadLocal<LinkedList<Z>> instances2 = new ThreadLocal<LinkedList<Z>>();

	public final Map<Integer, CString> all = new HashMap<Integer, CString>();

	public final ST_dtmethod_s _Dttree = new ST_dtmethod_s();
	public final _dtmethod_s Dttree = _Dttree.amp();

	public final ST_dtdisc_s AgDataDictDisc = new ST_dtdisc_s();

	public final ST_Agdesc_s ProtoDesc = new ST_Agdesc_s();

	public Agraph_s ProtoGraph;

	public final __struct__<Agtag_s> Tag = JUtils.from(Agtag_s.class);

	public final ST_dtdisc_s Ag_mainedge_seq_disc = new ST_dtdisc_s();

	public final ST_dtdisc_s Ag_subedge_seq_disc = new ST_dtdisc_s();

	public final ST_dtdisc_s Ag_subedge_id_disc = new ST_dtdisc_s();

	public final ST_dtdisc_s Ag_subgraph_id_disc = new ST_dtdisc_s();

	public final ST_Agiddisc_s AgIdDisc = new ST_Agiddisc_s();

	public final ST_Agmemdisc_s AgMemDisc = new ST_Agmemdisc_s();

	public final ST_dtdisc_s Ag_subnode_id_disc = new ST_dtdisc_s();

	public final ST_dtdisc_s Ag_subnode_seq_disc = new ST_dtdisc_s();

	public int HTML_BIT;

	public int CNT_BITS;

	public final ST_dtdisc_s Refstrdisc = new ST_dtdisc_s();

	public _dt_s Refdict_default;

	public Agraph_s Ag_dictop_G;

	public final __array_of_struct__ Arrowsynonyms = __array_of_struct__.malloc(arrowname_t.class, 1);

	public final __array_of_struct__ Arrownames = __array_of_struct__.malloc(arrowname_t.class, 3);

	public final __array_of_struct__ Arrowmods = __array_of_struct__.malloc(arrowname_t.class, 1);

	public final __array_of_struct__ Arrowtypes = __array_of_struct__.malloc(arrowtype_t.class, 9);

	public __ptr__ Show_boxes;

	public int CL_type;

	public boolean Concentrate;

	public int MaxIter;

	public int State;

	public int EdgeLabelsDone;

	public double Initial_dist;

	public Agsym_s G_activepencolor, G_activefillcolor, G_selectedpencolor, G_selectedfillcolor, G_visitedpencolor,
			G_visitedfillcolor, G_deletedpencolor, G_deletedfillcolor, G_ordering, G_peripheries, G_penwidth,
			G_gradientangle, G_margin;

	public Agsym_s N_height, N_width, N_shape, N_color, N_fillcolor, N_activepencolor, N_activefillcolor,
			N_selectedpencolor, N_selectedfillcolor, N_visitedpencolor, N_visitedfillcolor, N_deletedpencolor,
			N_deletedfillcolor, N_fontsize, N_fontname, N_fontcolor, N_margin, N_label, N_xlabel, N_nojustify, N_style,
			N_showboxes, N_sides, N_peripheries, N_ordering, N_orientation, N_skew, N_distortion, N_fixed,
			N_imagescale, N_layer, N_group, N_comment, N_vertices, N_z, N_penwidth, N_gradientangle;

	public Agsym_s E_weight, E_minlen, E_color, E_fillcolor, E_activepencolor, E_activefillcolor, E_selectedpencolor,
			E_selectedfillcolor, E_visitedpencolor, E_visitedfillcolor, E_deletedpencolor, E_deletedfillcolor,
			E_fontsize, E_fontname, E_fontcolor, E_label, E_xlabel, E_dir, E_style, E_decorate, E_showboxes, E_arrowsz,
			E_constr, E_layer, E_comment, E_label_float, E_samehead, E_sametail, E_arrowhead, E_arrowtail, E_headlabel,
			E_taillabel, E_labelfontsize, E_labelfontname, E_labelfontcolor, E_labeldistance, E_labelangle, E_tailclip,
			E_headclip, E_penwidth;

	public int N_nodes, N_edges;

	public int Minrank, Maxrank;

	public int S_i;

	public int Search_size;

	public final ST_nlist_t Tree_node = new ST_nlist_t();

	public final ST_elist Tree_edge = new ST_elist();

	public Agedge_s Enter;

	public int Low, Lim, Slack;

	public int Rankdir;

	public boolean Flip;

	public final ST_pointf Offset = new ST_pointf();

	public int nedges, nboxes;

	public int routeinit;

	public __ptr__ ps;

	public int maxpn;

	public __ptr__ polypoints;

	public int polypointn;

	public __ptr__ edges;

	public int edgen;

	public __array_of_struct__ boxes = __array_of_struct__.malloc(boxf.class, 1000);

	public int MinQuit;

	public double Convergence;

	public Agraph_s Root;

	public int GlobalMinRank, GlobalMaxRank;

	public boolean ReMincross;

	public __ptr__ TE_list;

	public __ptr__ TI_list;

	public Agnode_s Last_node_decomp;
	public Agnode_s Last_node_rank;

	public char Cmark;

	public int trin, tril;

	public __ptr__ tris;

	public int pnln, pnll;

	public pointnlink_t pnls;
	public __ptr__ pnlps;

	public final ST_port Center = new ST_port();

	public final __struct__<polygon_t> p_ellipse = JUtils.from(polygon_t.class);

	public final __struct__<polygon_t> p_box = JUtils.from(polygon_t.class);

	public final __struct__<shape_functions> poly_fns = JUtils.from(shape_functions.class);

	public __ptr__ tnas;
	public int tnan;

	public final shape_desc Shapes[] = { __Shapes__("box", poly_fns.amp(), p_box.amp()),
			__Shapes__("ellipse", poly_fns.amp(), p_ellipse.amp()), __Shapes__(null, null, null) };

	public final ST_dtdisc_s Ag_mainedge_id_disc = new ST_dtdisc_s();

	public final __struct__<deque_t> dq = JUtils.from(deque_t.class);

	public final ST_Agdesc_s Agdirected = new ST_Agdesc_s();

	public final __struct__<splineInfo> sinfo = JUtils.from(splineInfo.class);

	public Agnode_s lastn; /* last node argument */
	public polygon_t poly;
	public int last, outp, sides;
	public final __struct__<pointf> O = JUtils.from(pointf.class); /* point (0,0) */
	public pointf vertex;
	public double xsize, ysize, scalex, scaley, box_URx, box_URy;

	public final __struct__<textfont_t> tf = JUtils.from(textfont_t.class);

	public __ptr__ pointfs;
	public __ptr__ pointfs2;
	public int numpts;
	public int numpts2;

	public __ptr__ Count;
	public int C;

	public int ctr = 1;

	public __struct__<Agsubnode_s> template = JUtils.from(Agsubnode_s.class);
	public __struct__<Agnode_s> dummy = JUtils.from(Agnode_s.class);

	public Agraph_s G_ns;
	public Agraph_s G_decomp;

	public int opl;

	public int opn_route;
	public int opn_shortest;

	public __ptr__ ops_route;
	public __ptr__ ops_shortest;

	public static Z z() {
		return instances2.get().peekFirst();
	}

	public static void open() {
		LinkedList<Z> list = instances2.get();
		if (list == null) {
			list = new LinkedList<Z>();
			instances2.set(list);
		}
		list.addFirst(new Z());
	}

	public static void close() {
		instances2.get().removeFirst();
	}

	private shape_desc __Shapes__(String s, shape_functions shape_functions, polygon_t polygon) {
		shape_desc result = (shape_desc) Memory.malloc(shape_desc.class);
		result.setPtr("name", s == null ? null : new CString(s));
		result.setPtr("fns", shape_functions);
		result.setPtr("polygon", polygon);
		return result;
	}

	private final static __struct__ createArrowtypes(int type, double lenfact, CFunction function) {
		final ST_arrowtype_t result = new ST_arrowtype_t();
		result.type = type;
		result.lenfact = lenfact;
		result.gen = function;
		return result;
	}

	private final static __struct__ create_arrowname_t(String name, int type) {
		final ST_arrowname_t result = new ST_arrowname_t();
		result.name = name == null ? null : new CString(name);
		result.type = type;
		return result;
	}

	private Z() {

		_Dttree.searchf = function(dttree__c.class, "dttree");
		_Dttree.type = 0000004;

		AgDataDictDisc.key = OFFSET.create(Agsym_s.class, "name").toInt();
		AgDataDictDisc.size = -1;
		AgDataDictDisc.link = OFFSET.create(Agsym_s.class, "link").toInt();
		AgDataDictDisc.makef = null;
		AgDataDictDisc.freef = function(attr__c.class, "freesym");
		AgDataDictDisc.comparf = null;
		AgDataDictDisc.hashf = null;

		ProtoDesc.directed = 1;
		ProtoDesc.strict = 0;
		ProtoDesc.no_loop = 1;
		ProtoDesc.maingraph = 0;
		ProtoDesc.flatlock = 1;
		ProtoDesc.no_write = 1;

		Ag_mainedge_seq_disc.key = 0;
		Ag_mainedge_seq_disc.size = 0;
		Ag_mainedge_seq_disc.link = OFFSET.create(Agedge_s.class, "seq_link").toInt(); // seq_link is the third
																						// field in Agedge_t
		Ag_mainedge_seq_disc.makef = null;
		Ag_mainedge_seq_disc.freef = null;
		Ag_mainedge_seq_disc.comparf = function(edge__c.class, "agedgeseqcmpf");
		Ag_mainedge_seq_disc.hashf = null;
		Ag_mainedge_seq_disc.memoryf = function(utils__c.class, "agdictobjmem");
		Ag_mainedge_seq_disc.eventf = null;

		Ag_subedge_seq_disc.key = 0;
		Ag_subedge_seq_disc.size = 0;
		Ag_subedge_seq_disc.link = -1;
		Ag_subedge_seq_disc.makef = null;
		Ag_subedge_seq_disc.freef = null;
		Ag_subedge_seq_disc.comparf = function(edge__c.class, "agedgeseqcmpf");
		Ag_subedge_seq_disc.hashf = null;
		Ag_subedge_seq_disc.memoryf = function(utils__c.class, "agdictobjmem");
		Ag_subedge_seq_disc.eventf = null;

		Ag_subedge_id_disc.key = 0;
		Ag_subedge_id_disc.size = 0;
		Ag_subedge_id_disc.link = -1;
		Ag_subedge_id_disc.makef = null;
		Ag_subedge_id_disc.freef = null;
		Ag_subedge_id_disc.comparf = function(edge__c.class, "agedgeidcmpf");
		Ag_subedge_id_disc.hashf = null;
		Ag_subedge_id_disc.memoryf = function(utils__c.class, "agdictobjmem");
		Ag_subedge_id_disc.eventf = null;

		Ag_subgraph_id_disc.key = 0;
		Ag_subgraph_id_disc.size = 0;
		Ag_subgraph_id_disc.link = OFFSET.create(Agraph_s.class, "link").toInt(); // link is the third field in
																					// Agraph_t
		Ag_subgraph_id_disc.makef = null;
		Ag_subgraph_id_disc.freef = null;
		Ag_subgraph_id_disc.comparf = function(graph__c.class, "agraphidcmpf");
		Ag_subgraph_id_disc.hashf = null;
		Ag_subgraph_id_disc.memoryf = function(utils__c.class, "agdictobjmem");
		Ag_subgraph_id_disc.eventf = null;

		AgIdDisc.open = function(id__c.class, "idopen");
		AgIdDisc.map = function(id__c.class, "idmap");
		AgIdDisc.alloc = function(id__c.class, "idalloc");
		AgIdDisc.free = function(id__c.class, "idfree");
		AgIdDisc.print = function(id__c.class, "idprint");
		AgIdDisc.close = function(id__c.class, "idclose");
		AgIdDisc.idregister = function(id__c.class, "idregister");

		AgMemDisc.open = function(mem__c.class, "memopen");
		AgMemDisc.alloc = function(mem__c.class, "memalloc");
		AgMemDisc.resize = function(mem__c.class, "memresize");
		AgMemDisc.free = function(mem__c.class, "memfree");
		AgMemDisc.close = null;

		Ag_subnode_id_disc.key = 0;
		Ag_subnode_id_disc.size = 0;
		Ag_subnode_id_disc.link = OFFSET.create(Agsubnode_s.class, "id_link").toInt(); // id_link is the second
																						// field in Agsubnode_t
		Ag_subnode_id_disc.makef = null;
		Ag_subnode_id_disc.freef = null;
		Ag_subnode_id_disc.comparf = function(node__c.class, "agsubnodeidcmpf");
		Ag_subnode_id_disc.hashf = null;
		Ag_subnode_id_disc.memoryf = function(utils__c.class, "agdictobjmem");
		Ag_subnode_id_disc.eventf = null;

		Ag_subnode_seq_disc.key = 0;
		Ag_subnode_seq_disc.size = 0;
		Ag_subnode_seq_disc.link = OFFSET.create(Agsubnode_s.class, "seq_link").toInt(); // link is the first
																							// field in
																							// Agsubnode_t
		Ag_subnode_seq_disc.makef = null;
		Ag_subnode_seq_disc.freef = function(node__c.class, "free_subnode");
		Ag_subnode_seq_disc.comparf = function(node__c.class, "agsubnodeseqcmpf");
		Ag_subnode_seq_disc.hashf = null;
		Ag_subnode_seq_disc.memoryf = function(utils__c.class, "agdictobjmem");
		Ag_subnode_seq_disc.eventf = null;

		Refstrdisc.key = OFFSET.create(refstr_t.class, "s").toInt(); // *s is the third field in refstr_t
		Refstrdisc.size = -1;
		Refstrdisc.link = 0;
		Refstrdisc.makef = null;
		Refstrdisc.freef = function(utils__c.class, "agdictobjfree");
		Refstrdisc.comparf = null;
		Refstrdisc.hashf = null;
		Refstrdisc.memoryf = function(utils__c.class, "agdictobjmem");
		Refstrdisc.eventf = null;

		Arrowsynonyms.plus(0).setStruct(create_arrowname_t(null, 0));

		Arrownames.plus(0).setStruct(create_arrowname_t("normal", 1));
		Arrownames.plus(1).setStruct(create_arrowname_t("none", 8));
		Arrownames.plus(2).setStruct(create_arrowname_t(null, 0));

		Arrowmods.plus(0).setStruct(create_arrowname_t(null, 0));

		Arrowtypes.plus(0).setStruct(createArrowtypes(1, 1.0, function(arrows__c.class, "arrow_type_normal")));
		Arrowtypes.plus(1).setStruct(createArrowtypes(2, 1.0, function(arrows__c.class, "arrow_type_crow")));
		Arrowtypes.plus(2).setStruct(createArrowtypes(3, 0.5, function(arrows__c.class, "arrow_type_tee")));
		Arrowtypes.plus(3).setStruct(createArrowtypes(4, 1.0, function(arrows__c.class, "arrow_type_box")));
		Arrowtypes.plus(4).setStruct(createArrowtypes(5, 1.2, function(arrows__c.class, "arrow_type_diamond")));
		Arrowtypes.plus(5).setStruct(createArrowtypes(6, 0.8, function(arrows__c.class, "arrow_type_dot")));
		Arrowtypes.plus(6).setStruct(createArrowtypes(7, 1.0, function(arrows__c.class, "arrow_type_curve")));
		Arrowtypes.plus(7).setStruct(createArrowtypes(8, 0.5, function(arrows__c.class, "arrow_type_gap")));
		Arrowtypes.plus(8).setStruct(createArrowtypes(0, 0.0, null));

		Center.p.x = 0;
		Center.p.y = 0;
		Center.theta = -1;
		Center.bp = null;
		Center.defined = 0;
		Center.constrained = 0;
		Center.clip = 1;
		Center.dyna = 0;
		Center.order = 0;
		Center.side = 0;

		p_ellipse.setInt("regular", 0);
		p_ellipse.setInt("peripheries", 1);
		p_ellipse.setInt("sides", 1);
		p_ellipse.setDouble("orientation", 0.);
		p_ellipse.setDouble("distortion", 0.);
		p_ellipse.setDouble("skew", 0.);

		p_box.setInt("regular", 0);
		p_box.setInt("peripheries", 1);
		p_box.setInt("sides", 4);
		p_box.setDouble("orientation", 0.);
		p_box.setDouble("distortion", 0.);
		p_box.setDouble("skew", 0.);

		poly_fns.setPtr("initfn", function(shapes__c.class, "poly_init"));
		poly_fns.setPtr("freefn", function(shapes__c.class, "poly_free"));
		poly_fns.setPtr("portfn", function(shapes__c.class, "poly_port"));
		poly_fns.setPtr("insidefn", function(shapes__c.class, "poly_inside"));
		poly_fns.setPtr("pboxfn", function(shapes__c.class, "poly_path"));
		poly_fns.setPtr("codefn", function(shapes__c.class, "poly_gencode"));

		Ag_mainedge_id_disc.key = 0;
		Ag_mainedge_id_disc.size = 0;
		Ag_mainedge_id_disc.link = OFFSET.create(Agedge_s.class, "id_link").toInt(); // id_link is the second
																						// field in Agedge_t
		Ag_mainedge_id_disc.makef = null;
		Ag_mainedge_id_disc.freef = null;
		Ag_mainedge_id_disc.comparf = function(edge__c.class, "agedgeidcmpf");
		Ag_mainedge_id_disc.hashf = null;
		Ag_mainedge_id_disc.memoryf = function(utils__c.class, "agdictobjmem");
		Ag_mainedge_id_disc.eventf = null;

		Agdirected.directed = 1;
		Agdirected.strict = 0;
		Agdirected.no_loop = 0;
		Agdirected.maingraph = 1;

		sinfo.setPtr("swapEnds", function(dotsplines__c.class, "swap_ends_p"));
		sinfo.setPtr("splineMerge", function(dotsplines__c.class, "spline_merge"));

	}

}
