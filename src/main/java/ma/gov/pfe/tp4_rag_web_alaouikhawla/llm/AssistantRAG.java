package ma.gov.pfe.tp4_rag_web_alaouikhawla.llm;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.enterprise.context.ApplicationScoped;
import ma.gov.pfe.tp4_rag_web_alaouikhawla.bean.MagasinEmbeddings;
import ma.gov.pfe.tp4_rag_web_alaouikhawla.llm.Assistant;

@Named
@ApplicationScoped
public class AssistantRAG {

    @Inject
    private MagasinEmbeddings magasinEmbeddings;

    private Assistant assistant;

    public void initAssistant() {

        if (assistant != null) return; // déjà initialisé

        ChatModel model = GoogleAiGeminiChatModel.builder()
                .apiKey(System.getenv("GEMINI_KEY"))
                .modelName("gemini-2.5-flash")
                .temperature(0.2)
                .logRequestsAndResponses(true)
                .build();

        ContentRetriever retriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(magasinEmbeddings.getEmbeddingStore())
                .embeddingModel(magasinEmbeddings.getEmbeddingModel())
                .maxResults(3)
                .build();

        var augmentor = DefaultRetrievalAugmentor.builder()
                .contentRetriever(retriever)
                .build();

        this.assistant = AiServices.builder(Assistant.class)
                .chatModel(model)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .retrievalAugmentor(augmentor)
                .build();
    }

    public String ask(String question) {
        initAssistant();
        return assistant.chat(question);
    }
}
