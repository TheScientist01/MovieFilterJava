
import java.util.*;

public class Search {
    private List<String> genre;
    private List<String> vote;
    private List<String> voteCount;
    private List<String> date;
    private List<String> language;

    public List<String> getGenre() {
        if(this.genre!=null){
            return this.genre;
        }
        return null;
        
    }

    public void setGenre(List<String> genre) {
        this.genre = genre;
    }

    public List<String> getVote() {
        return this.vote;
    }

    public void setVote(List<String> vote) {
        this.vote = vote;
    }

    public List<String> getVoteCount() {
        return this.voteCount;
    }

    public void setVoteCount(List<String> voteCount) {
        this.voteCount = voteCount;
    }

    public List<String> getDate() {
        return this.date;
    }

    public void setDate(List<String> date) {
        this.date = date;
    }

    public List<String> getLanguage() {
        return this.language;
    }

    public void setLanguage(List<String> language) {
        this.language = language;
    }



    
}
