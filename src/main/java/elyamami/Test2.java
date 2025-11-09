package elyamami;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;

import java.util.Map;

public class Test2 {

    // Le texte que nous souhaitons traduire
    private static final String TEXTE_A_TRADUIRE = "J'aime beaucoup programmer en Java et utiliser LangChain4j pour les LLM.";

    public static void main(String[] args) {
        // 1. Initialisation du modèle de Chat (comme dans le Test 1)
        ChatModel model = GoogleAiGeminiChatModel.builder()
                .apiKey(System.getenv("GEMINI_KEY")) // Récupération de la clé API
                .modelName("gemini-2.5-flash")
                .temperature(0.7) // Configuration de la température
                // .logRequestsAndResponses(true) // Décommentez pour voir les échanges JSON
                .build();

        // 2. Création du PromptTemplate
        // Le template contient une variable nommée {{texteAtraduire}}
        PromptTemplate promptTemplate = PromptTemplate.from("Traduis le texte suivant en anglais : {{texteATraduire}}");

        // 3. Application du template pour créer le Prompt
        // Nous fournissons la valeur concrète pour la variable du template
        Prompt prompt = promptTemplate.apply(
                Map.of("texteATraduire", TEXTE_A_TRADUIRE)
        );

        // 4. Envoi du Prompt au modèle et récupération de la réponse
        // toChatMessages() convertit le Prompt en une liste de ChatMessages
        String reponse = model.chat(prompt.text());
        // 5. Affichage des résultats
        System.out.println("--- Test 2 : Traducteur avec PromptTemplate ---");
        System.out.println("Texte original à traduire : " + TEXTE_A_TRADUIRE);
        System.out.println("Réponse du LLM (Traduction) : \n" + reponse);
        System.out.println("----------------------------------------------");
    }
}