# **SinkIt!** - Documentation

![SinkIt! game logo](/src/main/resources/assets/logo.png)

## Contents
- [**SinkIt!** - Documentation](#sinkit---documentation)
  - [Contents](#contents)
    - [How to play](#how-to-play)
    - [Client-server communication](#client-server-communication)
    - [Database handler](#database-handler)
      - [Examples](#examples)

### How to play
1. **Login / Registration screen**
   ---
   
    First step is **loging in** to existing account or **registering** new account.
    
    <br>

    - **Login screen**

        Enter your username, password and press the **Login button**. If loging in was successfull, you will be redirected to **Menu screen**
    
    <br>

    - **Register screen**

        Enter your username, enter your password, enter your password again (for confirmation) and press **Register button**. If registration was successfull, you will be redirected to **Game screen**
    
    <br>
    

2. **Menu screen**
   ---

    On this screen you can *create new game*, *join existing game* or *check your statistics*.
    
    <br>

    - **Create game**

        If you want to create new game, select **game board size** in selector above this button. After selecting **game board size** this button. When is game created, you will be redirected to **Game screen**
    
    <br>

    - **Join game**

        If you want to join existing game, enter the **game id** to the input field on the left side of this button. After filling **game id** press this button. When game is joined you will be redirected to **Game screen**
    
    <br>

    - **Statistics**

        If you would like to know how many games you've won and how many games you've lost, press this button. (Statistics are updated after each finished game)
    
    <br>

    - **Logout**

        Logs out from this account and redirects yout to **Login / Registration screen**
    
    <br>

3. **Game screen**
   ---
   
   After successfully creating or joining the game, you will be redirected here. On the top of the screen you can see label containing **Game id**. After pressing **Copy button**, **Game id** will be copied to your clipboard, then you can share it with your friend. If the game was already initialized *(You placed your ships and submitted them)* you see **Running screen** otherwise **Setup screen is shown**. When player joins or leaves the game, other player is notified by popup window.
    
    <br>
   
   - **Setup screen**

        On this screen you will see board matching the size you selected in **Menu screen**. Select your favorite ship under the board. When you hover over any tile in board, some of them will lighten up. It indicates where ship will be placed. Ships can touch each other by their side, but can't cover each other. With `Left click` you can place the ship. With `Right click` you can remove the ship. After placing required amount of ships, press **Confirm button**.
        
        | Board size | Required count of placed ships |
        | ---------- | ----------------------- |
        | `Small`    | 5                       |
        | `Medium`   | 8                       |
        | `Big`      | 11                      |
    
    <br>

   - **Running screen**

        When other player setup their game starting player has been randomly chosen. In each turn you can shoot 5 times. Shooting don't cost you tokens. Besides shooting you can use as many power-ups as your token count allows you. Each power-up has different usage.

        | Power-up | My board | Enemy board | Usage |
        | -------- | -------- | ----------- | ----- |
        | Bomber   | &#10060; | &#9989;     | Reveals row or column in enemies board (You can choose between row and column by pressing `SHIFT`) |
        | Radar    | &#10060; | &#9989;     | Can be placed only on empty revealed tiles. It reveals 3x3 area on enemy board |
        | Bomb     | &#9989;  | &#10060;    | When enemy shoot your bomb, it will reveal three squares on enemies board |
        | Farm     | &#9989;  | &#10060;    | For each surrounding tile, where ship is placed, generates 1 bonus token. When part of farm is destroyed, or surrounding ship is destroyed, bonus token production is decreased |

        You Game is over, when one of the players doesn't have any tiles covered by ship left.
    
    <br>

### Client-server communication

There are two types of events **Client events** *(Sent from client)* and **Server events** *(Sent from server)*. Each event has its specific **Event class** and **Deserializer method**. Before event is sent, it is serialized to **JSON string object**. When event is received, it is parsed from **JSON string** back to its specific **Event class** using its common method for all events. Each **Event class** is constructed based on **event type** and its **properties**.

Example `GameUpdatedEvent`:
```json
{
    "type": "GAME_UPDATED",
    "currentPlayer": "Geff",
    "powerUps": {
        "my": [
            {
                "type": "BOMB",
                "position": {
                    "x": 1,
                    "y": 2
                }
            }
        ],
        "enemy": []
    },
    "shotTiles": {
        "my": [
            {
                "x": 5,
                "y": 5
            },
            {
                "x": 6,
                "y": 10
            }
        ],
        "enemy": []
    },
    "ships": [],
    "success": true
}
```
    
<br>

### Database handler

For working with users I developed my own **Database handler** based on **JSON conversion** and **File operations**. My custom handler is abstract class can work with any **Data model class** implementing my own `DataModel.java` interface. `DataModel.java` has only two methods which you need to implement. `mapToEntry` *(For working with maps)* and `deserialize` *(For constructing class from JSON string)*. My **DatabaseHandler** class supports:

- Saving one item to database
- Saving list of items to database
- Loading all items from database
- Loading all items satisfying some condition
- Loading one item satisfying some condition
- Deleting item satisfying some condition
- Updating item satisfying some condition
- Clearing database *(Deleting all items)*

#### Examples

- Saving user to database
    
    ```java
    final private Database<UserModel> usersDB = new DatabaseHandler<>("users.json", UserModel.class, false);
    
    // Previous code
    
    UsersModel userToBeSaved = new UsersModel("Geff", "heslo1");
    boolean isSaved = usersDB.save(userToBeSaved);
    
    // Next code
    ```

- Loading user with `id` equal to *123*
    
    ```java
    final private Database<UserModel> usersDB = new DatabaseHandler<>("users.json", UserModel.class, false);
    
    // Previous code
    
    UserModel user = usersDB.get("id", 123);
    
    // Next code
    ```

- Deleting user with `username` equal to "Jimmy"
    
    ```java
    final private Database<UserModel> usersDB = new DatabaseHandler<>("users.json", UserModel.class, false);
    
    // Previous code
    
    UserModel user = usersDB.get("username", "Jimmy");
    
    // Next code
    ```