package ejninsync.ejinusermanagment.datatypes;

public class ForumStats {

    private int forum_post_count;
    private int forum_votes;
    private int forum_up_votes;
    private int forum_down_votes;

    public ForumStats(){
    }

    public ForumStats(int forum_post_count, int forum_votes, int forum_up_votes, int forum_down_votes) {
        this.forum_post_count = forum_post_count;
        this.forum_votes = forum_votes;
        this.forum_up_votes = forum_up_votes;
        this.forum_down_votes = forum_down_votes;
    }

    public int getForum_post_count() {
        return forum_post_count;
    }

    public void setForum_post_count(int forum_post_count) {
        this.forum_post_count = forum_post_count;
    }

    public int getForum_votes() {
        return forum_votes;
    }

    public void setForum_votes(int forum_votes) {
        this.forum_votes = forum_votes;
    }

    public int getForum_up_votes() {
        return forum_up_votes;
    }

    public void setForum_up_votes(int forum_up_votes) {
        this.forum_up_votes = forum_up_votes;
    }

    public int getForum_down_votes() {
        return forum_down_votes;
    }

    public void setForum_down_votes(int forum_down_votes) {
        this.forum_down_votes = forum_down_votes;
    }
}
