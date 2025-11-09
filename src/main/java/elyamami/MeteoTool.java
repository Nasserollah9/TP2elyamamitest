package elyamami;

import dev.langchain4j.agent.tool.Tool;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Scanner;

public class MeteoTool {

    @Tool("Donne la météo actuelle pour une ville spécifique. Renvoie uniquement les données météo brutes (formatées).")
    public String donneMeteo(String ville) {
        // Log console pour voir l'appel de l'outil
        System.out.println("--- APPEL INTERNE : MeteoTool pour la ville '" + ville + "' ---");

        try {
            // Construit l'URI en gérant les espaces (ex: "New York")
            URI uri = new URI("https", "wttr.in", "/" + ville, "format=3", null);
            URL url = uri.toURL();

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            // TRÈS IMPORTANT : wttr.in renvoie du HTML sans User-Agent.
            // On simule 'curl' pour obtenir le format texte brut.
            connection.setRequestProperty("User-Agent", "curl/7.64.1");
            connection.setConnectTimeout(5000); // 5 sec timeout
            connection.setReadTimeout(5000);

            int responseCode = connection.getResponseCode();

            if (responseCode == 404) {
                System.out.println("--- Outil Réponse : Ville non trouvée (404) ---");
                return "Désolé, la ville '" + ville + "' est introuvable.";
            }

            if (responseCode != 200) {
                System.out.println("--- Outil Réponse : Erreur HTTP " + responseCode + " ---");
                return "Erreur API météo, code: " + responseCode;
            }

            // Lire la réponse
            Scanner scanner = new Scanner(connection.getInputStream());
            // .trim() pour enlever les lignes vides parfois retournées
            String response = scanner.useDelimiter("\\A").next().trim();
            scanner.close();

            System.out.println("--- Outil Réponse : " + response + " ---");
            // On retourne la donnée brute, le LLM s'occupe de la formuler.
            return response;

        } catch (IOException | URISyntaxException e) {
            System.out.println("--- Outil ERREUR : " + e.getMessage() + " ---");
            return "Erreur technique lors de la récupération de la météo : " + e.getMessage();
        }
    }
}