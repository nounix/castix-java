package rpi_cast.common;

import java.io.IOException;
import java.lang.Thread;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.Files;
import java.util.List;
import java.util.ArrayList;
import java.net.URI;

public class Tools {

    public static void sleep(int mSec){
        try {
            Thread.sleep(mSec);
        } catch (Exception e) { System.out.println("Sleep fnc Exception: " + e); }
    }

    public static String findFile(String path){
        List<Path> list = new ArrayList<>();
        try {
            Files.list(Paths.get(path))
            .filter(Files::isRegularFile)
            .forEach(list::add);
            return list.get(list.size() - 1).toString();
        } catch (Exception e){ return null; }
    }

    public static void runCommand(boolean quiet, String cmd){
        try {
            String[] command;
            if(quiet){ command = new String[]{ "bash", "-c", "exec " + cmd + " > /dev/null 2>&1" }; }
            else{ command = new String[]{ "bash", "-c", "exec " + cmd }; }
            Process process = Runtime.getRuntime().exec(command);
        } catch(IOException e) {
            System.out.println("Error");
        }
    }
}
