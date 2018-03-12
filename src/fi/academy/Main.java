package fi.academy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {

        printtaaJunanKuva();
        System.out.println("\nTervetuloa käyttämään Ryhmäjunan aikataulusovellusta. \n \n");
        JunaJSON.lueJunanJSONData();
    }

    private static void printtaaJunanKuva(){
        try {
            Files.lines(Paths.get("junanKuva.txt")).forEach(System.out::println);
        } catch (IOException e) {
            System.out.println("Virhe kuvan lukemisessa");
        }
        System.out.println();
        System.out.println();
    }
}
