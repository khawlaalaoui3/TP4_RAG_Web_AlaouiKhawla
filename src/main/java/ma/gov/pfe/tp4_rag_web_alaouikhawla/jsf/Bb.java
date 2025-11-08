package ma.gov.pfe.tp4_rag_web_alaouikhawla.jsf;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.model.SelectItem;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import ma.gov.pfe.tp4_rag_web_alaouikhawla.llm.LlmClient;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Backing bean JSF pour l’application Web du TP2 LangChain.
 * Portée view : garde l’état de la conversation.
 */
@Named
@ViewScoped
public class Bb implements Serializable {

    private String roleSysteme;
    private boolean roleSystemeChangeable = true;
    private List<SelectItem> listeRolesSysteme;
    private String question;
    private String reponse;
    private StringBuilder conversation = new StringBuilder();

    @Inject
    private FacesContext facesContext;

    @Inject
    private LlmClient llmClient;

    public Bb() {}

    public String getRoleSysteme() {
        return roleSysteme;
    }

    public void setRoleSysteme(String roleSysteme) {
        this.roleSysteme = roleSysteme;
    }

    public boolean isRoleSystemeChangeable() {
        return roleSystemeChangeable;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getReponse() {
        return reponse;
    }

    public void setReponse(String reponse) {
        this.reponse = reponse;
    }

    public String getConversation() {
        return conversation.toString();
    }

    public void setConversation(String conversation) {
        this.conversation = new StringBuilder(conversation);
    }

    /**
     * 🔹 Envoie la question au LLM via LlmClient.
     */
    public String envoyer() {
        if (question == null || question.isBlank()) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Texte question vide", "Veuillez saisir une question avant d'envoyer.");
            facesContext.addMessage(null, message);
            return null;
        }

        try {
            // Si c’est la première question, définir le rôle système
            if (this.conversation.isEmpty() && roleSysteme != null && !roleSysteme.isBlank()) {
                llmClient.setSystemRole(roleSysteme);
                this.roleSystemeChangeable = false;
            }

            // 🔹 Envoyer la question et recevoir la réponse du LLM
            this.reponse = llmClient.ask(question);

            // 🔹 Mise à jour de la conversation
            afficherConversation();

        } catch (Exception e) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Erreur LLM", e.getMessage());
            facesContext.addMessage(null, message);
        }

        return null; // reste sur la même page
    }

    /**
     * Pour un nouveau chat : réinitialise la vue.
     */
    public String nouveauChat() {
        return "index";
    }

    /**
     * Ajoute question/réponse à la conversation.
     */
    private void afficherConversation() {
        this.conversation
                .append("== Utilisateur:\n").append(question)
                .append("\n== Assistant:\n").append(reponse)
                .append("\n");
    }

    /**
     * Rôles système prédéfinis (identiques au cours).
     */
    public List<SelectItem> getRolesSysteme() {
        if (this.listeRolesSysteme == null) {
            this.listeRolesSysteme = new ArrayList<>();

            String role = """
                    You are a helpful assistant. You help the user to find the information they need.
                    If the user types a question, you answer it.
                    """;
            this.listeRolesSysteme.add(new SelectItem(role, "Assistant"));

            role = """
                    You are an interpreter. You translate from English to French and from French to English.
                    If the user types a French text, you translate it into English.
                    If the user types an English text, you translate it into French.
                    If the text contains only one to three words, give some examples of usage of these words in English.
                    """;
            this.listeRolesSysteme.add(new SelectItem(role, "Traducteur Anglais-Français"));

            role = """
                    You are a travel guide. If the user types the name of a country or a town,
                    you tell them what are the main places to visit there
                    and the average price of a meal.
                    """;
            this.listeRolesSysteme.add(new SelectItem(role, "Guide touristique"));
        }
        return this.listeRolesSysteme;
    }
}
