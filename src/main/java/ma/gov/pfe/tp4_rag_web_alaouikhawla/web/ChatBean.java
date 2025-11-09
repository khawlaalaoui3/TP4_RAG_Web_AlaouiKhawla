package ma.gov.pfe.tp4_rag_web_alaouikhawla.web;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import ma.gov.pfe.tp4_rag_web_alaouikhawla.llm.AssistantRAG;

@Named("chatBean")
@SessionScoped
public class ChatBean implements Serializable {

    private String question;
    private String answer;

    @Inject
    private AssistantRAG assistant;

    public void ask() {
        answer = assistant.ask(question);
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
