import Base.SlashCommandManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;

public class Bot extends ListenerAdapter {

    public static JDA jda = null;
    public static SlashCommandManager slashCommandManager;

    public static void main(String[] args) throws LoginException {
        jda = JDABuilder.create(Secrets.TOKEN, GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_VOICE_STATES)
                .addEventListeners(new Bot())
                .setChunkingFilter(ChunkingFilter.ALL) // enable member chunking for all guilds
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                .build();
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        jda.getPresence().setActivity(Activity.playing("/codeclash -> Create Code Clashes"));
        slashCommandManager = new SlashCommandManager(jda, Secrets.TEST_SERVER_ID);
    }

    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) {
        slashCommandManager.runCommand(event);
    }
}