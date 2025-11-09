package ma.gov.pfe.tp4_rag_web_alaouikhawla.bean;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.Part;
import ma.gov.pfe.tp4_rag_web_alaouikhawla.llm.AssistantRAG;

@Named("uploadBean")
@RequestScoped
public class UploadBean {

    @Inject
    private MagasinEmbeddings magasinEmbeddings;
    @Inject
    private AssistantRAG assistantRAG;
    private Part fichier;
    private String messagePourChargementFichier;

    public Part getFichier() { return fichier; }
    public void setFichier(Part fichier) { this.fichier = fichier; }
    public String getMessagePourChargementFichier() { return messagePourChargementFichier; }

    public void upload() {
        try {
            if (fichier != null && fichier.getSubmittedFileName().endsWith(".pdf")) {
                magasinEmbeddings.ajouter(fichier.getInputStream());
                messagePourChargementFichier = " PDF indexé : " + fichier.getSubmittedFileName();
            } else {
                messagePourChargementFichier = " Sélectionnez un fichier PDF.";
            }
        } catch (Exception e) {
            e.printStackTrace();
            messagePourChargementFichier = " Erreur : " + e.getMessage();
        }
        assistantRAG.reset();
    }
}
