package Commands;

import Base.SlashCommand;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;


public class PingCommand extends SlashCommand {
    @Override
    public String getDescription() {
        return "Returns the ping to the Base.Bot.";
    }

    @Override
    public String getCommand() {
        return "ping";
    }

    @Override
    public void onExecute(SlashCommandEvent event) {
        long time = System.currentTimeMillis();
        event.reply("Pong!").setEphemeral(true)
                .flatMap(v ->
                        event.getHook().editOriginalFormat("Pong: %d ms",  System.currentTimeMillis() - time) // then edit original
                ).queue();
    }
}
