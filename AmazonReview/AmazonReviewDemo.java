package AmazonReview;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

// --- Customer Class ---
class Customer {
    private final String customerId;
    private final String name;

    public Customer(String customerId, String name) {
        this.customerId = customerId;
        this.name = name;
    }
    public String getCustomerId() { return customerId; }
    public String getName() { return name; }
}

// --- Review Class ---
class Review {
    private final String reviewId;
    private final String productId;
    private final String customerId;
    private final int rating; // e.g., rating out of 5
    private final String reviewText;
    private final Date timestamp;
    private final Set<String> helpfulVotes; // customer IDs who found this review helpful

    public Review(String reviewId, String productId, String customerId, int rating, String reviewText) {
        this.reviewId = reviewId;
        this.productId = productId;
        this.customerId = customerId;
        this.rating = rating;
        this.reviewText = reviewText;
        this.timestamp = new Date(); // set current time
        this.helpfulVotes = new HashSet<>();
    }

    // Accessors
    public String getReviewId() { return reviewId; }
    public String getProductId() { return productId; }
    public int getRating() { return rating; }
    public Date getTimestamp() { return timestamp; }
    public String getReviewText() { return reviewText; }

    // Add a helpful vote from a customer.
    public void addHelpfulVote(String customerId) {
        helpfulVotes.add(customerId);
    }
    
    public int getHelpfulVotesCount() {
        return helpfulVotes.size();
    }
    
    @Override
    public String toString() {
        return "Review[" + reviewId + "]: " + rating + " stars, \"" + reviewText + "\" (Helpful: " + getHelpfulVotesCount() + ")";
    }
}

// --- Product Class ---
class Product {
    private final String productId;
    private final String name;
    private final List<Review> reviews;

    public Product(String productId, String name) {
        this.productId = productId;
        this.name = name;
        this.reviews = new ArrayList<>();
    }
    
    public String getProductId() { return productId; }
    public String getName() { return name; }
    
    // Add a review to this product.
    public void addReview(Review review) {
        reviews.add(review);
    }
    
    // Retrieve all reviews.
    public List<Review> getReviews() {
        return Collections.unmodifiableList(reviews);
    }
    
    // Compute average rating.
    public double getAverageRating() {
        if (reviews.isEmpty()) return 0.0;
        int total = 0;
        for (Review r : reviews) {
            total += r.getRating();
        }
        return (double) total / reviews.size();
    }
}

// --- ReviewManager Class ---
class ReviewManager {
    // In a real system, these might be in a database. Here we use in-memory maps.
    private final Map<String, Product> products; // productId -> Product
    private final Map<String, Review> reviews;     // reviewId -> Review

    public ReviewManager() {
        this.products = new HashMap<>();
        this.reviews = new HashMap<>();
    }
    
    // Register a product in the system.
    public void addProduct(Product product) {
        products.put(product.getProductId(), product);
    }
    
    // Add a review for a product.
    public void addReview(String reviewId, String productId, String customerId, int rating, String reviewText) {
        if (!products.containsKey(productId)) {
            throw new IllegalArgumentException("Product with ID " + productId + " not found.");
        }
        Review review = new Review(reviewId, productId, customerId, rating, reviewText);
        reviews.put(reviewId, review);
        products.get(productId).addReview(review);
    }
    
    // Retrieve reviews for a given product.
    public List<Review> getReviewsForProduct(String productId) {
        if (!products.containsKey(productId)) {
            return Collections.emptyList();
        }
        return products.get(productId).getReviews();
    }
    
    // Get average rating for a product.
    public double getAverageRating(String productId) {
        if (!products.containsKey(productId)) return 0.0;
        return products.get(productId).getAverageRating();
    }
    
    // Mark a review as helpful.
    public void markReviewHelpful(String reviewId, String customerId) {
        if (reviews.containsKey(reviewId)) {
            reviews.get(reviewId).addHelpfulVote(customerId);
        }
    }
}

// --- Demo Class ---
public class AmazonReviewDemo {
    public static void main(String[] args) {
        ReviewManager reviewManager = new ReviewManager();
        
        // Create and register a product.
        Product product = new Product("P1", "Amazon Echo");
        reviewManager.addProduct(product);
        
        // Customers add reviews.
        reviewManager.addReview("R1", "P1", "C1", 5, "Amazing sound quality!");
        reviewManager.addReview("R2", "P1", "C2", 4, "Works well, but could be cheaper.");
        
        // Mark reviews as helpful.
        reviewManager.markReviewHelpful("R1", "C3");
        reviewManager.markReviewHelpful("R1", "C4");
        
        // Display reviews and average rating.
        List<Review> productReviews = reviewManager.getReviewsForProduct("P1");
        System.out.println("Reviews for " + product.getName() + ":");
        for (Review r : productReviews) {
            System.out.println(r);
        }
        System.out.printf("Average Rating: %.2f\n", reviewManager.getAverageRating("P1"));
    }
}
