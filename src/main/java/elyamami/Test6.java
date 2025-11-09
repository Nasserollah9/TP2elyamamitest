package elyamami;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.service.AiServices;
import java.util.Scanner;

public class Test6 {

    public static void main(String[] args) {

        // --- 1. Création du modèle de chat Gemini ---
        // (Assurez-vous que GEMINI_KEY est définie dans vos variables d'environnement)
        ChatModel model = GoogleAiGeminiChatModel.builder()
                .apiKey(System.getenv("GEMINI_KEY"))
                // Utilisons un modèle récent, efficace pour la détection d'outils
                .modelName("gemini-2.5-flash")
                .build();

        // --- 2. Création de l’assistant avec l'outil ---
        AssistantMeteo assistant = AiServices.builder(AssistantMeteo.class)
                .chatModel(model)
                .tools(new MeteoTool())  // <-- C'est ici qu'on ajoute l'outil !
                // Pas besoin de .chatMemory() pour ce test, mais on pourrait l'ajouter
                .build();

        // --- 3. Interaction en boucle pour les tests ---
        Scanner scanner = new Scanner(System.in);
        System.out.println("Assistant Météo prêt. (Tapez 'exit' pour quitter)");
        System.out.println("Exemples : 'Quel temps fait-il à Tokyo ?' ou 'Dois-je prendre un parapluie à Londres ?'");

        while (true) {
            System.out.print("\nUtilisateur : ");
            String question = scanner.nextLine();

            if ("exit".equalsIgnoreCase(question)) {
                break;
            }

            // --- Réponse du modèle ---
            // Le LLM va analyser la question.
            // S'il pense avoir besoin de la météo, il appellera MeteoTool.
            // Sinon, il répondra directement.
            String reponse = assistant.chat(question);

            // --- Affichage de la réponse ---
            System.out.println("Assistant : " + reponse);
        }

        scanner.close();
        System.out.println("Au revoir !");
    }
}