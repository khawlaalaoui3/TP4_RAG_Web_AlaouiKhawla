package ma.gov.pfe.tp4_rag_web_alaouikhawla.bean;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.faces.view.ViewScoped;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.io.Serializable;

@Named("uploadBean")
@ViewScoped
public class UploadBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private MagasinEmbeddings magasinEmbeddings;

    private Part fichier;
    private String messagePourChargementFichier;

    public Part getFichier() {
        return fichier;
    }

    public void setFichier(Part fichier) {
        this.fichier = fichier;
    }

    public String getMessagePourChargementFichier() {
        return messagePourChargementFichier;
    }

    public void upload() {
        try {
            if (this.fichier != null && this.fichier.getSubmittedFileName() != null
                    && this.fichier.getSubmittedFileName().toLowerCase().endsWith(".pdf")) {

                // Appelle MagasinEmbeddings pour parser, découper et indexer le PDF
                magasinEmbeddings.ajouter(fichier.getInputStream());

                messagePourChargementFichier =
                        " PDF chargé et indexé : " + fichier.getSubmittedFileName();

                // optionnel : reset du Part
                this.fichier = null;

            } else {
                messagePourChargementFichier = "⚠ Veuillez choisir un fichier PDF.";
            }
        } catch (IOException e) {
            e.printStackTrace();
            messagePourChargementFichier = " Erreur lors du chargement : " + e.getMessage();
        }
    }
}
