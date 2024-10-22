package osama.partone;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.Duration;

public class CountdownController {

    @FXML
    private TextField counter;

    private Timeline timeline;
    private Duration time = Duration.minutes(2);

    public CountdownController() {
        setupTimeline();
    }

    private void setupTimeline() {
        if (timeline != null) {
            timeline.stop();
        }
        time = Duration.minutes(2);
        timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(1), event -> {
            time = time.subtract(Duration.seconds(1));
            updateLabel();
            if (time.lessThanOrEqualTo(Duration.ZERO)) {
                timeline.stop();
                
            }
        }));
    }

    public void start() {
        timeline.play();
    }

    public void stop() {
        timeline.stop();
        time = Duration.ZERO;
        updateLabel();
    }

    public void reset() {
        timeline.stop();
        setupTimeline();
        updateLabel();
    }

    private void updateLabel() {
        long seconds = (long) time.toSeconds();
        counter.setText(String.format("%02d:%02d", seconds / 60, seconds % 60));
    }
}

