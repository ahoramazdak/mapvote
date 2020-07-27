package net.bpj.updater;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.os.Environment;

public class IO {

	public static File appRoot;
	
	public static void init(String app_name) {
		appRoot = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + app_name + "/");
		if (!appRoot.exists())
			appRoot.mkdirs();
	}
	
	
	public static ArrayList<String> readLinesFromStream(InputStream in)
			throws IOException {
		ArrayList<String> ret = new ArrayList<String>();
		BufferedReader buffer = new BufferedReader(new InputStreamReader(in,
				"UTF-8"));
		String line = "";
		while ((line = buffer.readLine()) != null)
			ret.add(line);
		return ret;
	}
	
}
