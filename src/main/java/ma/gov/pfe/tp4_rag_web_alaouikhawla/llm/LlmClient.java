package ma.gov.pfe.tp4_rag_web_alaouikhawla.llm;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.data.message.SystemMessage;
import jakarta.enterprise.context.ApplicationScoped;


/**
 * Classe métier pour interagir avec le LLM Gemini via LangChain4j.
 */
@ApplicationScoped
public class LlmClient {

    /**
     * Interface Assistant : proxy généré automatiquement par LangChain4j.
     * LangChain4j fournit l'implémentation à l'exécution.
     */

    private String systemRole;
    private final ChatMemory chatMemory;
    private final Assistant assistant;

    public LlmClient() {
        String apiKey = System.getenv("GEMINI_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("La clé API Gemini (GEMINI_KEY) n'est pas définie.");
        }

        // Création du ChatModel (Gemini)
        ChatModel model = GoogleAiGeminiChatModel.builder()
                .apiKey(apiKey)
                .modelName("gemini-2.5-flash")
                .temperature(0.3)
                .build();

        // Création d'une mémoire de conversation (fenêtre glissante de 10 messages)
        this.chatMemory = MessageWindowChatMemory.withMaxMessages(10);

        // Création du service IA : LangChain4j génère une implémentation de Assistant
        this.assistant = AiServices.builder(Assistant.class)
                .chatModel(model)
                .chatMemory(chatMemory)
                .build();
    }

    /**
     * Définit le rôle système et l'enregistre comme SystemMessage dans la mémoire.
     * Vide la mémoire avant d'ajouter le nouveau SystemMessage.
     *
     * @param role le rôle système choisi
     */
    public void setSystemRole(String role) {
        this.systemRole = role;
        // Vide la mémoire (méthode disponible sur ChatMemory)
        this.chatMemory.clear();
        if (role != null && !role.isBlank()) {
            // SystemMessage.from(...) crée un message système compatible
            this.chatMemory.add(SystemMessage.from(role));
        }
    }

    /**
     * Envoie une question au LLM et retourne la réponse.
     *
     * @param prompt la question utilisateur
     * @return réponse textuelle du LLM
     */
    public String ask(String prompt) {
        if (prompt == null || prompt.isBlank()) {
            return "(La question est vide)";
        }
        return assistant.chat(prompt);
    }
}
