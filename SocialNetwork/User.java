package SocialNetwork;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class User {
    private final String id;       // Unique identifier (could be an email, username, etc.)
    private final String name;     
    private final Set<User> friends; // Direct friends of this user

    public User(String id, String name) {
        this.id = id;
        this.name = name;
        this.friends = new HashSet<>();
    }
    
    // Accessor methods
    public String getId() { return id; }
    public String getName() { return name; }
    
    // Adds a friend (bidirectional friendship can be established here if needed)
    public void addFriend(User friend) {
        if (friend != null && !friend.equals(this)) {
            friends.add(friend);
            // Uncomment the following line if you want mutual friendship automatically:
            // friend.friends.add(this);
        }
    }
    
    // Removes a friend
    public void removeFriend(User friend) {
        friends.remove(friend);
        // Uncomment the following line if mutual friendship should be removed:
        // friend.friends.remove(this);
    }
    
    // Returns an unmodifiable view of direct friends
    public Set<User> getFriends() {
        return Collections.unmodifiableSet(friends);
    }
    
    // Returns friends of friends (excluding direct friends and self)
    public Set<User> getFriendsOfFriends() {
        Set<User> result = new HashSet<>();
        for (User friend : friends) {
            for (User fof : friend.getFriends()) {
                if (!fof.equals(this) && !friends.contains(fof)) {
                    result.add(fof);
                }
            }
        }
        return result;
    }
    
    // equals() and hashCode() based on unique id
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    // For easier debugging/printing
    @Override
    public String toString() {
        return name;
    }
    
    // --- Example Usage ---
    public static void main(String[] args) {
        // Create several users.
        User alice = new User("1", "Alice");
        User bob = new User("2", "Bob");
        User charlie = new User("3", "Charlie");
        User dave = new User("4", "Dave");
        User eve = new User("5", "Eve");
        
        // Establish direct friendships.
        alice.addFriend(bob);
        alice.addFriend(charlie);
        
        bob.addFriend(dave);
        bob.addFriend(eve);
        
        charlie.addFriend(eve);
        // Note: Dave and Eve are not direct friends of Alice.
        
        // Find and print friends of friends for Alice.
        Set<User> friendsOfFriends = alice.getFriendsOfFriends();
        System.out.println("Friends of Friends for " + alice.getName() + ": " + friendsOfFriends);
        // Expected output (order may vary): Friends of Friends for Alice: [Dave, Eve]
    }
}

