package cz.laryngektomie.dto.forum;

import cz.laryngektomie.helper.ForumHelper;
import org.apache.commons.lang3.time.DurationFormatUtils;

import java.time.Duration;
import java.time.LocalDateTime;

public class TopicOrPost {

    private final long topicId;
    private final String topicName;
    private final LocalDateTime createDateTime;
    private final String author;
    private final String text;
    private final String categoryName;

    public TopicOrPost(long topicId, String topicName, LocalDateTime createDateTime, String author, String text, String categoryName) {
        this.topicId = topicId;
        this.topicName = topicName;
        this.createDateTime = createDateTime;
        this.author = author;
        this.text = text;
        this.categoryName = categoryName;
    }

    public long getTopicId() {
        return topicId;
    }

    public String getTopicName() {
        return topicName;
    }

    public LocalDateTime getCreateDateTime() {
        return createDateTime;
    }

    public String getAuthor() {
        return author;
    }

    public String getText() {
        return text;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getTimePassedString() {
        //Duration = čas mezi vytvořením objektu a součastností
        Duration duration = Duration.between(createDateTime, LocalDateTime.now());
        if (duration.toHours() < 24) {
            if (duration.toMinutes() < 60) {
                if (duration.toMinutes() == 1) {
                    return "před " + DurationFormatUtils.formatDuration(duration.toMillis(), "m", true) + " minutou";
                }
                return "před " + DurationFormatUtils.formatDuration(duration.toMillis(), "m", true) + " minutami";
            } else {
                if (duration.toHours() == 1) {
                    return "před " + DurationFormatUtils.formatDuration(duration.toMillis(), "H", true) + " hodinou";
                }
                return "před " + DurationFormatUtils.formatDuration(duration.toMillis(), "H", true) + " hodinami";
            }
        } else {
            if (duration.toDays() == 1) {
                return "před " + DurationFormatUtils.formatDuration(duration.toMillis(), "d", true) + " dnem";
            }
            return "před " + DurationFormatUtils.formatDuration(duration.toMillis(), "d", true) + " dny";
        }
    }

    public String getDescription() {
        if (this.text != null) {
            return ForumHelper.getDescription(this.text, 25);
        }
        return null;
    }
}
