package engine.models;

public class Feedback {

    public static final Feedback RIGHT_FEEDBACK = new Feedback(true, "Congratulations, you're right!");
    public static final Feedback WRONG_FEEDBACK = new Feedback(false, "Wrong answer! Please, try again.");

    private final boolean success;
    private final String feedback;

    public Feedback(boolean success, String feedback) {
        this.success = success;
        this.feedback = feedback;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getFeedback() {
        return feedback;
    }
}
