package ma.gov.pfe.tp4_rag_web_alaouikhawla.bean;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class MagasinEmbeddings implements Serializable {

    private final EmbeddingStore<TextSegment> embeddingStore;
    private final EmbeddingModel embeddingModel;

    public MagasinEmbeddings() {
        this.embeddingStore = new InMemoryEmbeddingStore<>();
        this.embeddingModel = new AllMiniLmL6V2EmbeddingModel();
    }

    public EmbeddingStore<TextSegment> getEmbeddingStore() {
        return embeddingStore;
    }

    public EmbeddingModel getEmbeddingModel() {
        return embeddingModel;
    }

    public void ajouter(InputStream inputStream) {
        ApacheTikaDocumentParser parser = new ApacheTikaDocumentParser();
        Document document = parser.parse(inputStream);
        ajouterDocument(document);
    }

    public void ajouterDocument(Document document) {
        // Split PDF en chunks
        var splitter = DocumentSplitters.recursive(500, 50);
        List<TextSegment> segments = splitter.split(document);

        // Générer embeddings
        var embeddings = embeddingModel.embedAll(segments).content();

        // Ajouter dans le store vectoriel
        embeddingStore.addAll(embeddings, segments);
    }
}