# Utility Discord Bot
Discord Bot with many utilities like base64/hex de-/encode and string hashing

# Invite the bot
Does not need any permissions (yet)
[click here](https://discord.com/api/oauth2/authorize?client_id=897819560902787133&permissions=131072&scope=bot%20applications.commands)

# found a bug?
Make a pull Request!

# wanna contribute?

**Use JDK 11!!**

- Clone the repo with intellij
- let it build the gradle stuff and maybe you gotta reload gradle
- add a Secrets.xml file with 3 tags TOKEN, CC_EMAIL and CC_PW in the root dir of the project
- code
- go to Base.Bot and at onReady() where the slashCommandManager is replace null with ur guildID 
- build it with the shadowjar task
- test the code
- replace your guildID from step 5 with null
- submit a pull request and dont publish ur secrets.xml

---
made by [ReisMiner#1111](https://reisminer.xyz)
