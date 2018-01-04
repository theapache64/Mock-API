package com.theah64.mock_api.lab;

import com.theah64.mock_api.utils.CodeGen;
import org.json.JSONException;

import java.io.*;

/**
 * Created by theapache64 on 4/1/18.
 */
public class Main {

    public static void main(String[] args) throws IOException, JSONException {

        final StringBuilder jsonString = new StringBuilder();
        final BufferedReader br = new BufferedReader(new FileReader(new File("sample.json")));
        String line = null;
        while ((line = br.readLine()) != null) {
            jsonString.append(line).append("\n");
        }
        br.close();

        final String output = CodeGen.getFinalCode("com.your.package", jsonString.toString(), "Sample", true);
        save(output);
    }

    private static void save(String data) {
        //Finally writing it to a file
        final File javaFile = new File("Sample.html");
        try {
            final BufferedWriter bw = new BufferedWriter(new FileWriter(javaFile));
            bw.write(data);
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
