package com.coecs.bluenicheuser;

public class Review {

    private String reviewID;
    private String review;
    private String fromJobTitle;
    private User fromUser;

    private int rating;

    public Review() {
    }

    public Review(String reviewID, String review, String fromJobTitle, User fromUser, int rating) {
        this.reviewID = reviewID;
        this.review = review;
        this.fromJobTitle = fromJobTitle;
        this.fromUser = fromUser;
        this.rating = rating;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getReviewID() {
        return reviewID;
    }

    public void setReviewID(String reviewID) {
        this.reviewID = reviewID;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getFromJobTitle() {
        return fromJobTitle;
    }

    public void setFromJobTitle(String fromJobTitle) {
        this.fromJobTitle = fromJobTitle;
    }

    public User getFromUser() {
        return fromUser;
    }

    public void setFromUser(User fromUser) {
        this.fromUser = fromUser;
    }
}
