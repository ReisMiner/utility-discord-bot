package Base;

import net.dv8tion.jda.api.interactions.commands.OptionType;

public class SlashCommandArgs {

    private final OptionType optionType;
    private final String name;
    private final String description;
    private final Boolean required;

    public SlashCommandArgs(OptionType option, String name, String description, Boolean required) {
        this.optionType = option;
        this.name = name;
        this.description = description;
        this.required = required;
    }

    public OptionType getOptionType() {
        return optionType;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Boolean isRequired() {
        return required;
    }

}
