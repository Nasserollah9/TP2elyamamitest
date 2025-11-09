package elyamami;

import dev.langchain4j.chain.ConversationalRetrievalChain;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.splitter.DocumentByParagraphSplitter;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.model.googleai.GoogleAiEmbeddingModel;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.query.Query;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

import java.util.List;
import java.util.Scanner;

public class Test5 {

    public static void main(String[] args) {

        System.out.println("🚀 Démarrage du programme Test5...");

        // 1️⃣ Vérifier clé Gemini
        String geminiKey = System.getenv("GEMINI_KEY");
        if (geminiKey == null || geminiKey.isEmpty()) {
            System.err.println("❌ GEMINI_KEY n'est pas défini. Arrêt du programme.");
            return;
        }
        System.out.println("✅ GEMINI_KEY trouvée.");

        // 2️⃣ Charger le PDF
        String fileName = "src/langchain4j.pdf"; // Remplace par ton PDF
        System.out.println("🔹 Chargement du PDF : " + fileName);
        Document document;
        try {
            document = FileSystemDocumentLoader.loadDocument(fileName);
            System.out.println("✅ PDF chargé avec succès");
        } catch (Exception e) {
            System.err.println("❌ Erreur lors du chargement du PDF : " + e.getMessage());
            return;
        }

        // 3️⃣ Découper le document en segments
        System.out.println("🔹 Découpage du document en segments...");
        DocumentByParagraphSplitter splitter = new DocumentByParagraphSplitter(300, 50);
        List<TextSegment> segments = splitter.split(document);
        System.out.println("✅ Segments créés : " + segments.size());

        // 4️⃣ Créer le modèle d'embeddings Gemini
        System.out.println("🔹 Création du modèle d'embeddings Gemini...");
        EmbeddingModel embeddingModel = GoogleAiEmbeddingModel.builder()
                .apiKey(geminiKey)
                .modelName("text-embedding-004")
                .build();
        System.out.println("✅ Modèle d'embeddings prêt");

        // 5️⃣ Créer la base vectorielle
        System.out.println("🔹 Création de la base vectorielle en mémoire...");
        EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
        System.out.println("✅ Base vectorielle créée");

        // 6️⃣ Calculer les embeddings pour chaque segment
        System.out.println("🔹 Calcul des embeddings pour chaque segment...");
        int count = 0;
        for (TextSegment segment : segments) {
            try {
                embeddingStore.add(embeddingModel.embed(segment.text()).content(), segment);
                count++;
                if (count % 5 == 0) { // Log tous les 5 segments
                    System.out.println("Segments traités : " + count + "/" + segments.size());
                }
            } catch (Exception e) {
                System.err.println("❌ Erreur sur segment : " + segment.text().substring(0, Math.min(50, segment.text().length())) + " ...");
                System.err.println("Message d'erreur : " + e.getMessage());
            }
        }
        System.out.println("✅ Embeddings calculés et stockés : " + count + "/" + segments.size());

        // 7️⃣ Créer le retriever
        System.out.println("🔹 Création du retriever RAG...");
        EmbeddingStoreContentRetriever retriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(3)
                .build();
        System.out.println("✅ Retriever créé");

        // 8️⃣ Créer le modèle de chat Gemini
        System.out.println("🔹 Création du modèle de chat Gemini...");
        ChatModel chatModel = GoogleAiGeminiChatModel.builder()
                .apiKey(geminiKey)
                .modelName("gemini-2.5-flash")
                .build();
        System.out.println("✅ Modèle de chat prêt");

        // 9️⃣ Créer la chaîne RAG avec mémoire
        System.out.println("🔹 Création de la chaîne RAG...");
        ConversationalRetrievalChain chain = ConversationalRetrievalChain.builder()
                .chatModel(chatModel)
                .contentRetriever(retriever)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .build();
        System.out.println("✅ Chaîne RAG prête");

        // 10️⃣ Interaction utilisateur
        System.out.println("\n💬 Assistant RAG (Gemini) prêt !");
        System.out.println("Tapez 'fin' pour terminer la conversation.\n");

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.println("==================================================");
                System.out.print("Posez votre question : ");
                String question = scanner.nextLine();

                if (question.isBlank()) continue;
                if ("fin".equalsIgnoreCase(question)) {
                    System.out.println("👋 Fin de la session !");
                    break;
                }

                System.out.println("==================================================");
                System.out.println("🔹 Traitement de la question : " + question);

                try {
                    String reponse = chain.execute(question);
                    System.out.println("🤖 Assistant : " + reponse);

                    List<Content> usedSegments = retriever.retrieve(new Query(question));
                    System.out.println("\n📄 Segments utilisés pour cette réponse :");
                    for (Content seg : usedSegments) {
                        System.out.println(" - " + seg.textSegment());
                    }
                } catch (Exception e) {
                    System.err.println("❌ Erreur lors de l'exécution de la question : " + e.getMessage());
                }

                System.out.println("==================================================\n");
            }
        }
    }
}
