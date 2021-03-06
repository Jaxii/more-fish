package me.elsiff.morefish.fishing.competition;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import javax.annotation.Nonnull;
import me.elsiff.morefish.dao.DaoFactory;
import me.elsiff.morefish.dao.YamlRecordDao;
import me.elsiff.morefish.fishing.Fish;
import org.bukkit.OfflinePlayer;

public final class FishingCompetition {

    @Nonnull
    private FishingCompetition.State state;

    public FishingCompetition() {
        this.state = State.DISABLED;
    }

    private void checkStateDisabled() {
        if (state != State.DISABLED) {
            throw new IllegalStateException("Fishing competition hasn't disabled");
        }
    }

    private void checkStateEnabled() {
        if (state != State.ENABLED) {
            throw new IllegalStateException("Fishing competition hasn't enabled");
        }
    }

    public final void clearRecords() {
        getRecords().clear();
    }

    public final boolean containsContestant(@Nonnull UUID contestant) {
        return getRanking().stream().anyMatch(record -> contestant.equals(record.getFisher()));
    }

    public final void disable() {
        this.checkStateEnabled();
        this.state = FishingCompetition.State.DISABLED;
    }

    public final void enable() {
        this.checkStateDisabled();
        this.state = FishingCompetition.State.ENABLED;
    }

    @Nonnull
    public final List<Record> getRanking() {
        return this.getRecords().all();
    }

    private YamlRecordDao getRecords() {
        return DaoFactory.INSTANCE.getRecords();
    }

    @Nonnull
    public final FishingCompetition.State getState() {
        return this.state;
    }

    public final void setState(@Nonnull FishingCompetition.State state) {
        this.state = state;
    }

    public final boolean isDisabled() {
        return this.state == FishingCompetition.State.DISABLED;
    }

    public final boolean isEnabled() {
        return this.state == FishingCompetition.State.ENABLED;
    }

    public final void putRecord(@Nonnull Record record) {
        checkStateEnabled();
        if (containsContestant(record.getFisher())) {
            Record oldRecord = recordOf(record.getFisher());
            if (record.getFish().getLength() > oldRecord.getFish().getLength()) {
                getRecords().update(record);
            }
        }
        else {
            getRecords().insert(record);
        }

    }

    public final int rankNumberOf(@Nonnull Record record) {
        return getRanking().indexOf(record) + 1;
    }

    @Nonnull
    public final Entry<Integer, Record> rankedRecordOf(@Nonnull OfflinePlayer contestant) {
        List<Record> records = getRanking();
        int place = 0;
        for (Record record : records) {
            if (record.getFisher().equals(contestant.getUniqueId())) {
                return new SimpleEntry<>(place, record);
            }

            place++;
        }

        throw new IllegalStateException("Record not found");
    }

    @Nonnull
    public final Record recordOf(@Nonnull UUID contestant) {
        return getRanking().stream().filter(record -> contestant.equals(record.getFisher())).findFirst().orElseThrow(() -> new IllegalStateException("Record not found"));
    }

    @Nonnull
    public final Record recordOf(int rankNumber) {
        if (rankNumber >= 1 && rankNumber <= getRanking().size()) {
            return getRanking().get(rankNumber - 1);
        }

        throw new IllegalArgumentException("Rank number is out of records size.");
    }

    @Nonnull
    public final List<Record> top(int size) {
        return getRecords().top(size);
    }

    public final boolean willBeNewFirst(@Nonnull OfflinePlayer catcher, @Nonnull Fish fish) {
        if (!getRanking().isEmpty()) {
            Record record = getRanking().get(0);
            return fish.getLength() > record.getFish().getLength() && !record.getFisher().equals(catcher.getUniqueId());
        }

        return true;
    }

    public enum State {
        ENABLED,
        DISABLED
    }
}
