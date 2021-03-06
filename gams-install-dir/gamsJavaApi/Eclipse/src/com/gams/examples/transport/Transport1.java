package com.gams.examples.transport;

import java.io.*;

import com.gams.api.*;

/**
 * This example shows how to create and run a GAMSJob from the simple GAMS 
 * [trnsport] model from the GAMS Model Library. It also shows how to specify 
 * the solver using GAMSOptions and run a job with a solver option.
 */ 
public class Transport1 {

    public static void main(String[] args) {
        GAMSWorkspace ws = null;
        // check workspace info from command line arguments
        if (args.length > 0) {  
            GAMSWorkspaceInfo wsInfo = new GAMSWorkspaceInfo();
            wsInfo.setSystemDirectory(args[0]);
            // create GAMSWorkspace "ws" with user-specified system directory and the default working directory 
            // (the directory named with current date and time under System.getProperty("java.io.tmpdir"))
            ws = new GAMSWorkspace(wsInfo);
        } else {
            // create GAMSWorkspace "ws" with default system directory and default working directory 
            // (the directory named with current date and time under System.getProperty("java.io.tmpdir"))
            ws = new GAMSWorkspace();
        }
 
        // create GAMSJob "t1" from "trnsport" model in GAMS Model Libraries
        GAMSJob t1 = ws.addJobFromGamsLib("trnsport");
        // run GAMSJob "t1"
        t1.run();

        // retrieve GAMSVariable "x" from GAMSJob's output databases
        System.out.println("Ran with Default:");
        GAMSVariable x = t1.OutDB().getVariable("x");
        for (GAMSVariableRecord rec :  x) {
            System.out.print("x(" + rec.getKey(0) + ", " + rec.getKey(1) + "):");
            System.out.print(", level    = " + rec.getLevel());
            System.out.println(", marginal = " + rec.getMarginal());
        }

        // create GAMSOptions "opt1"
        GAMSOptions opt1 = ws.addOptions();
        // set all model types of "opt1" for "xpress"
        opt1.setAllModelTypes("xpress");
        // run GAMSJob "t1" with GAMSOptions "opt1"
        t1.run(opt1);

        // retrieve GAMSVariable "x" from GAMSJob's output databases
        GAMSDatabase db1 = t1.OutDB();
        System.out.println("Ran with XPRESS:");
        for (GAMSVariableRecord rec : db1.getVariable("x")) {
            System.out.print("x(" + rec.getKey(0) + ", " + rec.getKey(1) + "):");
            System.out.print(", level    = " + rec.getLevel());
            System.out.println(", marginal = " + rec.getMarginal());
        }

        // write file "xpress.opt" under GAMSWorkspace's working directory
        try {
            BufferedWriter optFile = new BufferedWriter(new FileWriter(ws.workingDirectory() + GAMSGlobals.FILE_SEPARATOR + "xpress.opt"));
            optFile.write("algorithm=barrier");
            optFile.close();
         } catch(IOException e) {
              e.printStackTrace();
              System.exit(-1);
         }

        // create GAMSOptions "opt2"
        GAMSOptions opt2 = ws.addOptions();
        // set all model types of "opt2" for "xpress"
        opt2.setAllModelTypes( "xpress" );
        // for "opt2", use "xpress.opt" as solver's option file
        opt2.setOptFile(1);

        try {
           // run GAMSJob "t2" with GAMSOptions "opt2" and capture log into "transport1_xpress.log". 
           PrintStream output = new PrintStream(new File(ws.workingDirectory() + GAMSGlobals.FILE_SEPARATOR +"transport1_xpress.log")); 
           t1.run(opt2, output);
        } catch (FileNotFoundException e) {
           // run GAMSJob "t2" with GAMSOptions "opt2" and log is written to standard output
           t1.run(opt2);
        }

        // retrieve GAMSVariable "x" from GAMSJob's output databases
        GAMSDatabase db2 = t1.OutDB();
        System.out.println("Ran with XPRESS with non-default option:");
        for (GAMSVariableRecord rec : db2.getVariable("x")) {
            System.out.print("x(" + rec.getKey(0) + ", " + rec.getKey(1) + "):");
            System.out.print(", level    = " + rec.getLevel());
            System.out.println(", marginal = " + rec.getMarginal());
        }

        // dispose option and database
        opt1.dispose();
        opt2.dispose();
        db1.dispose();
        db2.dispose();
        // cleanup GAMSWorkspace's working directory
        cleanup(ws.workingDirectory());
        // terminate program
        System.exit(0);
    }

    static void cleanup(String directory)  {
        File directoryToDelete = new File(directory);
        String files[] = directoryToDelete.list();
        for (String file : files) {
            File fileToDelete = new File(directoryToDelete, file);
            try {
                 fileToDelete.delete();
            } catch(Exception e){
                 e.printStackTrace();
            }
        }
        try {
           directoryToDelete.delete();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
