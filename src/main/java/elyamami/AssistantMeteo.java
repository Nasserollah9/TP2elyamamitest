package elyamami;

public interface AssistantMeteo {

    /**
     * Interagit avec l'assistant.
     * @param userMessage Le message de l'utilisateur.
     * @return La réponse de l'assistant.
     */
    String chat(String userMessage);
}