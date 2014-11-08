StrikeDeath with PvPRatings and GearScores
------------------------------------------

StrikeDeath
-----------
As with the original StrikeDeath, this will still cause lightning to strike
when an armored kill occurs. Data of all kills are still stored in the database
for use in any stats pages. The database has been modified to handle PvPRatings.



PvPRatings
----------
PvP Ratings are calculated from the results of fights. When a player kills
another player, it looks at gear of both the killer and the victim, gets the
Gear Score for each player, and determines who should win the fight based on
gear. It also looks at the current ratings of both players, and scales the
rating gained/lost by bother players. If the person with the higher Gear Score
and/or PvP Rating wins, they will get less points than if the "underdog" won.

The database has two types of decay. The first attempts to prevent a player from
camping a specific player for points. The database only keeps the 10 most
valuable kills within the time decay limit. When a new kill occurs after there
are 10 kills in the database, it looks to see which of the 11 has the lowest
score. If it is the current kill, you are awarded 0 rating points. If it is not
the lowest, the lowest is dropped and replaced with the new one. The message
to the players shows them the difference between these two, to reflect their
total change in rating. The second decay is the time decay mentioned earlier.
The config file allows the time decay limit to be adjusted. The default is 14
days. According to the default, only kills in the past two weeks are factored
into a players rating. After the two week mark, points gained/lost from a fight
are "undone", and no longer counted in the total rating of the players involved.
Players with the 5 highest ratings have their ranking shown next to their name
in chat.

The Commands:
-------------
"/top5" -   This command displays the five players with the highest rankings.
            If someone replaces the #1 player, an announcement is made to 
            the whole server.

"/rating [playername/clantag]" - Without any arguments, this command returns the
            rating of the sender. If supplied with a Playername, it will return
            rating of that player. if supplied with a Clan Tag, it will return
            the average rating of that clan.

"/ranking [playername]" - Without any arguments, this command returns the
            ranking of the sender. If supplied with a Playername, it will return
            the ranking of that player. *Note* This will only return rankings
            for players who have killed or been killed by another player.