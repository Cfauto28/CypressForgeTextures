package net.fmg793.cypressforgetextures.cpw.mods.fml.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Profiler {
	public static boolean profilingEnabled = false;
	private static List sectionList = new ArrayList();
	private static List timestampList = new ArrayList();
	private static String profilingSection = "";
	private static Map profilingMap = new HashMap();

	public static void clearProfiling() {
		profilingMap.clear();
	}

	public static void startSection(String string0) {
		if(profilingEnabled) {
			if(profilingSection.length() > 0) {
				profilingSection = profilingSection + ".";
			}

			profilingSection = profilingSection + string0;
			sectionList.add(profilingSection);
			timestampList.add(System.nanoTime());
		}
	}

	public static void endSection() {
		if(profilingEnabled) {
			long j0 = System.nanoTime();
			long j2 = ((Long)timestampList.remove(timestampList.size() - 1)).longValue();
			sectionList.remove(sectionList.size() - 1);
			long j4 = j0 - j2;
			if(profilingMap.containsKey(profilingSection)) {
				profilingMap.put(profilingSection, ((Long)profilingMap.get(profilingSection)).longValue() + j4);
			} else {
				profilingMap.put(profilingSection, j4);
			}

			profilingSection = sectionList.size() > 0 ? (String)sectionList.get(sectionList.size() - 1) : "";
			if(j4 > 100000000L) {
				System.out.println(profilingSection + " " + j4);
			}

		}
	}

	public static List getProfilingData(String string0) {
		if(!profilingEnabled) {
			return null;
		} else {
			long j2 = profilingMap.containsKey("root") ? ((Long)profilingMap.get("root")).longValue() : 0L;
			long j4 = profilingMap.containsKey(string0) ? ((Long)profilingMap.get(string0)).longValue() : -1L;
			ArrayList arrayList6 = new ArrayList();
			if(string0.length() > 0) {
				string0 = string0 + ".";
			}

			long j7 = 0L;
			Iterator iterator9 = profilingMap.keySet().iterator();

			while(iterator9.hasNext()) {
				String string10 = (String)iterator9.next();
				if(string10.length() > string0.length() && string10.startsWith(string0) && string10.indexOf(".", string0.length() + 1) < 0) {
					j7 += ((Long)profilingMap.get(string10)).longValue();
				}
			}

			float f19 = (float)j7;
			if(j7 < j4) {
				j7 = j4;
			}

			if(j2 < j7) {
				j2 = j7;
			}

			Iterator iterator20 = profilingMap.keySet().iterator();

			String string11;
			while(iterator20.hasNext()) {
				string11 = (String)iterator20.next();
				if(string11.length() > string0.length() && string11.startsWith(string0) && string11.indexOf(".", string0.length() + 1) < 0) {
					long j12 = ((Long)profilingMap.get(string11)).longValue();
					double d14 = (double)j12 * 100.0D / (double)j7;
					double d16 = (double)j12 * 100.0D / (double)j2;
					String string18 = string11.substring(string0.length());
					arrayList6.add(new ProfilerResult(string18, d14, d16));
				}
			}

			iterator20 = profilingMap.keySet().iterator();

			while(iterator20.hasNext()) {
				string11 = (String)iterator20.next();
				profilingMap.put(string11, ((Long)profilingMap.get(string11)).longValue() * 999L / 1000L);
			}

			if((float)j7 > f19) {
				arrayList6.add(new ProfilerResult("unspecified", (double)((float)j7 - f19) * 100.0D / (double)j7, (double)((float)j7 - f19) * 100.0D / (double)j2));
			}

			Collections.sort(arrayList6);
			arrayList6.add(0, new ProfilerResult(string0, 100.0D, (double)j7 * 100.0D / (double)j2));
			return arrayList6;
		}
	}

	public static void endStartSection(String string0) {
		endSection();
		startSection(string0);
	}
}
