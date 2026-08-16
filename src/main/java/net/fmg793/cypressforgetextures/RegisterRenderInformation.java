package net.fmg793.cypressforgetextures;

import java.util.ArrayList;
import java.util.List;

public class RegisterRenderInformation {
	public static List<String> pathList = new ArrayList<>();

	public static void register(String path) {
        if (path != null) {
        	pathList.add(path);
        }
	}
}
