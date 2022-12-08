# Utility Discord Bot
Discord Bot with many utilities like base64/hex de-/encode and string hashing

# Invite the bot
Does need manage role perms. Its requesting admin though just for future commands.
[click here](https://discord.com/api/oauth2/authorize?client_id=897819560902787133&permissions=8&scope=bot%20applications.commands)

# found a bug?
Make a pull Request!

# wanna contribute?

**Use JDK 11!!**

- Clone the repo with intellij
- let it build the gradle stuff and maybe you gotta reload gradle
- add a secrets.xml file with 5 tags TOKEN, DB_DB and DB_HOST, DB_USER, DB_PW in the root dir of the project
- make the db with the script in ./SQL
- code
- go to Base.Bot and at onReady() where the slashCommandManager is replace null with ur guildID 
- build it with the shadowjar task
- test the code
- replace your guildID from step 5 with null
- submit a pull request and dont publish ur secrets.xml
- enjoy

# contributors

- ReisMiner - Founder and Dev
- Yathy77 - Dev
