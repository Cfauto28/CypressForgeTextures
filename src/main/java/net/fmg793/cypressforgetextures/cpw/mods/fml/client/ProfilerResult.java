package net.fmg793.cypressforgetextures.cpw.mods.fml.client;

public final class ProfilerResult implements Comparable {
	public double sectionPercentage;
	public double globalPercentage;
	public String name;

	public ProfilerResult(String string1, double d2, double d4) {
		this.name = string1;
		this.sectionPercentage = d2;
		this.globalPercentage = d4;
	}

	public int compareProfilerResult(ProfilerResult profilerResult1) {
		return profilerResult1.sectionPercentage < this.sectionPercentage ? -1 : (profilerResult1.sectionPercentage > this.sectionPercentage ? 1 : profilerResult1.name.compareTo(this.name));
	}

	public int getDisplayColor() {
		return (this.name.hashCode() & 11184810) + 4473924;
	}

	public int compareTo(Object object1) {
		return this.compareProfilerResult((ProfilerResult)object1);
	}
}
