package it.unitn.disi.ds2.whanau.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class LookupResult {

    public String value;
    public int messages;

    public LookupResult(String value,int messages)
    {
        this.value = value;
        this.messages = messages;
    }

    /**
     * Static utility method that writes on a file the results
     * On each line of the output file there will be the number of messages used
     * or 'fail' if the query has failed
     * @param list an instance of List<LookupResult> that contains the results of the various queries
     * @param filename the name of the file on which to print the values
     */
    public static void writeOnFile(List<LookupResult> list, String filename)
    {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
            writer.write("lookup_success,messages\n");
            for (LookupResult r : list) {
                String outputLine = r.value == null ? "false" : "true";
                outputLine += ","+r.messages+"\n";
                writer.write(outputLine);
            }
            writer.close();
        } catch (IOException e) {
            System.err.println("Impossible to write on file '"+filename+"'");
        }
    }

}
