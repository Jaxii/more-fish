package me.elsiff.morefish.hooker;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.elsiff.morefish.MoreFish;
import me.elsiff.morefish.configuration.format.Format;
import me.elsiff.morefish.fishing.competition.FishingCompetition;
import me.elsiff.morefish.fishing.competition.Record;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class PlaceholderApiHooker implements PluginHooker {

    private boolean hasHooked = false;

    @Nonnull
    public String getPluginName() {
        return "PlaceholderAPI";
    }

    public boolean hasHooked() {
        return this.hasHooked;
    }

    public void hook(@Nonnull MoreFish plugin) {
        new MoreFishPlaceholder(plugin).register();
        Format.Companion.init(this);
        setHasHooked(true);
    }

    public void setHasHooked(boolean var1) {
        this.hasHooked = var1;
    }

    @Nonnull
    public final String tryReplacing(@Nonnull String string, @Nullable Player player) {
        return PlaceholderAPI.setPlaceholders(player, string);
    }

    public static final class MoreFishPlaceholder extends PlaceholderExpansion {

        private final FishingCompetition competition;

        public MoreFishPlaceholder(@Nonnull MoreFish moreFish) {
            this.competition = moreFish.getCompetition();
        }

        @Override
        public String getIdentifier() {
            return "morefish";
        }

        @Override
        public String getAuthor() {
            return "Jaxii";
        }

        @Override
        public String getVersion() {
            return MoreFish.instance().getDescription().getVersion();
        }

        @Nullable
        public String onPlaceholderRequest(@Nullable Player player, @Nonnull String identifier) {
            if (identifier.startsWith("top_player_")) {
                int number = Integer.parseInt(identifier.replace("top_player_", ""));
                if (competition.getRanking().size() >= number) {
                    return Bukkit.getOfflinePlayer(competition.recordOf(number).getFisher()).getName();
                }

                return "No player is this rank";
            }
            if (identifier.startsWith("top_fish_length_")) {
                int number = Integer.parseInt(identifier.replace("top_fish_length_", ""));
                if (competition.getRanking().size() >= number) {
                    return String.valueOf(competition.recordOf(number).getFish().getLength());
                }

                return "0.0";
            }
            if (identifier.startsWith("top_fish_")) {
                int number = Integer.parseInt(identifier.replace("top_fish_", ""));
                if (competition.getRanking().size() >= number) {
                    return competition.recordOf(number).getFish().getType().getName();
                }

                return "No top fish";
            }
            if (player != null) {
                if (identifier.equals("rank")) {
                    if (competition.containsContestant(player.getUniqueId())) {
                        Record record = competition.recordOf(player.getUniqueId());
                        return String.valueOf(competition.rankNumberOf(record));
                    }

                    return "0";
                }
                if (identifier.equals("fish")) {
                    if (competition.containsContestant(player.getUniqueId())) {
                        return competition.recordOf(player.getUniqueId()).getFish().getType().getName();
                    }

                    return "No value";
                }
            }

            return null;
        }
    }
}
