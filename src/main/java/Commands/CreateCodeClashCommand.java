package Commands;

import Base.SlashCommand;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class CreateCodeClashCommand extends SlashCommand {
    @Override
    public String getDescription() {
        return "Creates a codeclash";
    }

    @Override
    public String getCommand() {
        return "codeclash";
    }

    @Override
    public void onExecute(SlashCommandEvent event) {
        super.onExecute(event);
    }
}
