package me.vaperion.plugins.rank;

import lombok.Data;

@Data
public class Rank {
    private String color, display, coloredDisplay, id;
    private int priority;

    public Rank(String id) {
        this.id = id;
        this.display = "default";
        this.color = "&f";
        this.coloredDisplay = "&fdefault";
        this.priority = -1;
    }

    public static class Builder {
        private Rank rank;

        public Builder(String id) {
            this.rank = new Rank(id);
        }

        public Builder(Rank rank) {
            this.rank = rank;
        }

        public Builder setColor(String color) {
            rank.setColor(color);
            return this;
        }

        public Builder setDisplay(String display) {
            rank.setDisplay(display);
            return this;
        }

        public Builder setColoredDisplay(String coloredDisplay) {
            rank.setColoredDisplay(coloredDisplay);
            return this;
        }

        public Builder setPriority(int priority) {
            rank.setPriority(priority);
            return this;
        }

        public Rank build() {
            return rank;
        }
    }
}
