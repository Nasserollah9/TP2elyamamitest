package elyamami;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;

public class Test1 {

    public static void main(String[] args) {

        // 1. Récupérer la clé API de Gemini à partir d'une variable d'environnement
        // Assurez-vous que la variable GEMINI_API_KEY est définie sur votre système
        String apiKey = System.getenv("GEMINI_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            System.err.println("Erreur : La variable d'environnement 'GEMINI_API_KEY' n'est pas définie.");
            return;
        }

        // 2. Créer une instance de ChatModel avec le pattern "builder"
        ChatModel model = GoogleAiGeminiChatModel
                .builder()
                .apiKey(apiKey) // Fournit la clé API
                .modelName("gemini-2.5-flash") // Spécifie le modèle à utiliser
                .temperature(0.7) // Règle la température (créativité) à 0.7
                // Optionnel : si vous voulez voir les requêtes/réponses dans les logs
                //.logRequestsAndResponses(true)
                .build();
        // 3. Définir la question à poser
        String question = "Bonjour ! Peux-tu me donner un fait intéressant sur la planète Mars ?";
        System.out.println("-> Question : " + question);

        // 4. Poser la question au LLM et obtenir la réponse
        // La méthode 'generate' envoie le message à l'API et retourne le contenu de la réponse
        String reponse = model.chat(question);

        // 5. Afficher la réponse
        System.out.println("-> Réponse de Gemini : ");
        System.out.println(reponse);
    }
}