package elyamami;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.googleai.GoogleAiEmbeddingModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

/**
 * Le RAG facile !
 */
public class Test4 {

    interface Assistant {
        String chat(String userMessage);
    }

    public static void main(String[] args) {

        // --- Création du modèle d'embedding ---
        EmbeddingModel embeddingModel = GoogleAiEmbeddingModel.builder()
                .apiKey(System.getenv("GEMINI_KEY"))
                .modelName("text-embedding-004")
                .build();

        // --- Création du modèle de chat Gemini ---
        ChatModel model = GoogleAiGeminiChatModel.builder()
                .apiKey(System.getenv("GEMINI_KEY"))
                .modelName("gemini-2.5-flash")
                .build();

        // --- Chargement du document ---
        String nomDocument = "src/infos.txt";
        Document document = FileSystemDocumentLoader.loadDocument(nomDocument);

        // --- Base vectorielle en mémoire ---
        EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

        // --- Création de l’ingestor ---
        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .build();

        // --- Ingestion du document ---
        ingestor.ingest(document);

        // --- Création du retriever correctement configuré ---
        EmbeddingStoreContentRetriever retriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(3)
                .build();

        // --- Création de l’assistant conversationnel ---
        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(model)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .contentRetriever(retriever)
                .build();

        // --- Question à poser ---
        String question = "Comment s'appelle le chat de Pierre ?";

        // --- Réponse du modèle ---
        String reponse = assistant.chat(question);

        // --- Affichage de la réponse ---
        System.out.println(reponse);
    }
}
