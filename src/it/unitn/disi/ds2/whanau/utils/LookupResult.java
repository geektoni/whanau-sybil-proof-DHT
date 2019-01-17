package it.unitn.disi.ds2.whanau.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LookupResult {

    public String value;
    public int tries;

    public LookupResult(){}

    public LookupResult(String value,int tries)
    {
        this.value = value;
        this.tries = tries;
    }

    public static void writeOnFile(List<LookupResult> list, String filename)
    {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
            for (LookupResult r : list) {
                String outputLine = r.value == null ? "fail\n" : r.tries+"\n";
                writer.write(outputLine);
            }
        } catch (IOException e) {
            System.err.println("Impossible to write on file '"+filename+"'");
        }
    }

}
