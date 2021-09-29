package dumb.comfurtably.springbootproject.entities;

public class RequestArticleEntity {

    private String article;
    private boolean nsfw;

    //Getters and setters
    public String getArticle () {
        return article;
    }

    public void setArticle (String article) {
        this.article = article;
    }

    public boolean getNsfw () {
        return this.nsfw;
    }

    public void setNsfw (boolean nsfw) {
        this.nsfw = nsfw;
    }
    
}
