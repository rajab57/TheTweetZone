# TheTweetZone (Simple Twitter Client)

**Build a simple Twitter client that supports viewing a Twitter timeline and composing a new tweet.**

Total Hours = 13 hours spent in total
### Features
1. [x] User can sign in to Twitter using OAuth login
3. [x] User can view the tweets from their home timeline
 * [x] User should be able to see the username, name, body and timestamp for each tweet
 * [x] User should be displayed the relative timestamp for a tweet "8m", "7h"
 * [x] User can view more tweets as they scroll with infinite pagination
 * [x] **Optional**: Links in tweets are clickable and will launch the web browser
4. [x] User can compose a new tweet
 * [x] User can click a Compose icon in the Action Bar on the top right
 * [x] User can then enter a new tweet and post this to twitter
 * [x] User is taken back to home timeline with new tweet visible in timeline
 * [x] **Optional**: User can see a counter with total number of characters left for tweet
5. [x] **Advanced**: User can refresh tweets timeline by pulling down to refresh (i.e pull-to-refresh)
6. [x] **Advanced**: User can open the twitter app offline and see last loaded tweets
7. [x] **Advanced**: User can tap a tweet to display a "detailed" view of that tweet
8. [x] **Advanced**: User can select "reply" from detail view to respond to a tweet
9. [x] **Advanced**: Improve the user interface and theme the app to feel "twitter branded"
10. [x] **Bonus**: User can see embedded image media within the tweet detail view
11. [x] **Bonus**: Compose activity is replaced with a modal overlay
12. [x] **Additional** : Attempt to make it look closer to Twitter
13. [x] **Additional** : Retweeted information is captured on the timeline.
14. [x] **Additional** : Parsing the tweet body to be similar to Twitter
15. [x] **Additional** : Captured number of retweets and favorites on the detailed Tweet View.
16. [x] **Additional** : Show indefinite progress while fetching tweets from TwitterServer/local database.


#### Incomplete
1. The reply, favorite and retweet buttons on the timeline do not have action attached to it.
2. Replying to a tweet on a detail tweet screen does not show up on the screen.
3. Replied to a tweet are not shown on the detail tweet screen.

### Walk through of all user stories:

[![Video Walkthrough](https://raw.github.com/rajab57/TheTweetZone/assets/HomeScreen.png)](https://www.dropbox.com/s/vytpnyc9u7pl9zy/tweetzone.mp4)
![Video Walkthrough] https://www.dropbox.com/s/vytpnyc9u7pl9zy/tweetzone.mp4

### Libraries and Dependencies 
1. scribe-java - Simple OAuth library for handling the authentication flow.
2. Android Async HTTP - Simple asynchronous HTTP requests with JSON parsing
3. codepath-oauth - Custom-built library for managing OAuth authentication and signing of requests
4. UniversalImageLoader - Used for async image loading and caching them in memory and on disk.
5. ActiveAndroid - Simple ORM for persisting a local SQLite database on the Android device
6. PullToRefreshView https://github.com/erikwt/PullToRefresh-ListView/
