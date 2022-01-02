package cz.laryngektomie.model;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@MappedSuperclass
public abstract class EntityBase implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @CreationTimestamp
    private LocalDateTime createDateTime;

    @UpdateTimestamp
    private LocalDateTime updateDateTime;

    protected EntityBase() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCreateDateTime() {
        return createDateTime;
    }

    public void setCreateDateTime(LocalDateTime createDateTime) {
        this.createDateTime = createDateTime;
    }

    public LocalDateTime getUpdateDateTime() {
        return updateDateTime;
    }

    public void setUpdateDateTime(LocalDateTime updateDateTime) {
        this.updateDateTime = updateDateTime;
    }

    public String getCreateDateTimeString() {
        return createDateTime.format(DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy"));
    }

    public String getUpdateDateTimeString() {
        return updateDateTime.format(DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy"));
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
}
