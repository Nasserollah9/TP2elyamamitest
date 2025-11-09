package elyamami;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;

import dev.langchain4j.model.googleai.GoogleAiEmbeddingModel;
import dev.langchain4j.store.embedding.CosineSimilarity;

import java.time.Duration;

public class Test3 {

    public static void main(String[] args) {
        // --- 1. Définition des phrases à tester ---
        String phrase1 = "Le chat dort sur le canapé.";
        String phrase2 = "Le félin est en train de faire la sieste sur le sofa.";
        String phrase3 = "L'avion vole au-dessus des montagnes.";

        // --- 2. Initialisation du Modèle d'Embeddings ---

        EmbeddingModel embeddingModel = GoogleAiEmbeddingModel.builder()
                .apiKey(System.getenv("GEMINI_KEY"))
                .modelName("text-embedding-004")
                // CORRECTION: Utilisation de TaskType via l'interface du modèle
                .taskType(GoogleAiEmbeddingModel.TaskType.SEMANTIC_SIMILARITY)
                .timeout(Duration.ofMillis(10000))
                .build();

        System.out.println("--- Test 3 : Calcul de Similarité Cosinus (Embeddings) ---");

        try {
            // --- 3. Génération des Embeddings (Vecteurs) ---
            System.out.println("Génération des embeddings...");

            Embedding embedding1 = embeddingModel.embed(phrase1).content();
            Embedding embedding2 = embeddingModel.embed(phrase2).content();
            Embedding embedding3 = embeddingModel.embed(phrase3).content();

            System.out.println("Dimension du vecteur : " + embedding1.vector().length);

            // --- 4. Calcul de la Similarité Cosinus ---

            // Utilisation de la méthode entre deux objets Embedding (corrigée précédemment)
            double similarity1_2 = CosineSimilarity.between(embedding1, embedding2);
            double similarity1_3 = CosineSimilarity.between(embedding1, embedding3);

            // --- 5. Affichage des résultats ---
            System.out.println("\nPhrase 1 : \"" + phrase1 + "\"");
            System.out.println("Phrase 2 : \"" + phrase2 + "\"");
            System.out.println("Phrase 3 : \"" + phrase3 + "\"");

            System.out.println("\n-> Similarité Cosinus (Phrase 1 vs 2 - SIMILAIRE) : " + similarity1_2);
            System.out.println("-> Similarité Cosinus (Phrase 1 vs 3 - DIFFÉRENT) : " + similarity1_3);

        } catch (Exception e) {
            System.err.println("Une erreur est survenue : " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("------------------------------------------------------------");
    }
}