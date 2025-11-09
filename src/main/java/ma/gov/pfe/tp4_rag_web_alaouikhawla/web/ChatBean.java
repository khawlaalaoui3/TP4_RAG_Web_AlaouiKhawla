package ma.gov.pfe.tp4_rag_web_alaouikhawla.web;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import ma.gov.pfe.tp4_rag_web_alaouikhawla.llm.AssistantRAG;

@Named
@SessionScoped
public class ChatBean implements Serializable {

    private String question;
    private String answer;

    private AssistantRAG assistant;

    public ChatBean() {
        try {
            assistant = new AssistantRAG();
        } catch (Exception e) {
            answer = "Erreur initialisation Assistant : " + e.getMessage();
        }
    }

    public void ask() {
        answer = assistant.ask(question);
    }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
    public String getAnswer() { return answer; }
}