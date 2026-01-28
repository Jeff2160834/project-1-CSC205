package com.csc205.project1;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Represents an immutable point in three-dimensional Euclidean space.
 * 
 * <p>This class follows the Value Object pattern, ensuring that once a Point3D 
 * instance is created, its coordinates cannot be modified. All transformation 
 * operations return new Point3D instances rather than modifying the existing object.
 * 
 * <p>Common operations include:
 * <ul>
 *   <li>Distance calculations (Euclidean, Manhattan, Chebyshev)</li>
 *   <li>Rotations around the X, Y, and Z axes</li>
 *   <li>Vector operations (addition, subtraction, scalar multiplication)</li>
 *   <li>Normalization and magnitude calculations</li>
 * </ul>
 * 
 * <p><b>Design Patterns Used:</b>
 * <ul>
 *   <li><b>Value Object Pattern:</b> Immutable representation of a geometric point</li>
 *   <li><b>Factory Method Pattern:</b> Static factory methods for common points (origin, unit vectors)</li>
 *   <li><b>Fluent Interface:</b> Method chaining for transformations</li>
 * </ul>
 * 
 * @author Claude
 * @version 1.0
 * @since 1.0
 */
public class Point3D {
    
    private static final Logger logger = Logger.getLogger(Point3D.class.getName());
    
    // Immutable fields representing the coordinates
    private final double x;
    private final double y;
    private final double z;
    
    // Tolerance for floating-point comparisons
    private static final double EPSILON = 1e-10;
    
    /**
     * Constructs a new Point3D with the specified coordinates.
     * 
     * <p>This is the primary constructor for creating points in 3D space. 
     * Coordinates are stored as double-precision floating-point numbers to 
     * support both integer and fractional values.
     * 
     * <p><b>Example usage:</b>
     * <pre>
     * Point3D point = new Point3D(3.0, 4.0, 5.0);
     * </pre>
     * 
     * @param x the x-coordinate of the point
     * @param y the y-coordinate of the point
     * @param z the z-coordinate of the point
     */
    public Point3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        logger.log(Level.INFO, "Created Point3D at ({0}, {1}, {2})", 
                   new Object[]{x, y, z});
    }
    
    /**
     * Factory method to create a point at the origin (0, 0, 0).
     * 
     * <p>This static factory method demonstrates the Factory Method pattern,
     * providing a convenient and semantically clear way to obtain a point at 
     * the coordinate system's origin.
     * 
     * <p><b>Example usage:</b>
     * <pre>
     * Point3D origin = Point3D.origin();
     * </pre>
     * 
     * @return a new Point3D at the origin
     */
    public static Point3D origin() {
        logger.info("Creating origin point");
        return new Point3D(0, 0, 0);
    }
    
    /**
     * Factory method to create a unit vector along the X-axis (1, 0, 0).
     * 
     * <p>Unit vectors are commonly used in linear algebra and 3D graphics
     * to represent directions without magnitude.
     * 
     * @return a new Point3D representing the X-axis unit vector
     */
    public static Point3D unitX() {
        return new Point3D(1, 0, 0);
    }
    
    /**
     * Factory method to create a unit vector along the Y-axis (0, 1, 0).
     * 
     * @return a new Point3D representing the Y-axis unit vector
     */
    public static Point3D unitY() {
        return new Point3D(0, 1, 0);
    }
    
    /**
     * Factory method to create a unit vector along the Z-axis (0, 0, 1).
     * 
     * @return a new Point3D representing the Z-axis unit vector
     */
    public static Point3D unitZ() {
        return new Point3D(0, 0, 1);
    }
    
    // Accessor methods (Getters)
    
    /**
     * Returns the x-coordinate of this point.
     * 
     * @return the x-coordinate
     */
    public double getX() {
        return x;
    }
    
    /**
     * Returns the y-coordinate of this point.
     * 
     * @return the y-coordinate
     */
    public double getY() {
        return y;
    }
    
    /**
     * Returns the z-coordinate of this point.
     * 
     * @return the z-coordinate
     */
    public double getZ() {
        return z;
    }
    
    /**
     * Calculates the Euclidean distance between this point and another point.
     * 
     * <p>The Euclidean distance is the straight-line distance between two points
     * in 3D space, calculated using the formula:
     * <pre>
     * distance = sqrt((x2-x1)² + (y2-y1)² + (z2-z1)²)
     * </pre>
     * 
     * <p>This method demonstrates the fundamental principle of geometric algorithms
     * and is essential for many applications including collision detection, 
     * nearest-neighbor searches, and spatial data structures like k-d trees.
     * 
     * <p><b>Time Complexity:</b> O(1)
     * <p><b>Space Complexity:</b> O(1)
     * 
     * <p><b>Example usage:</b>
     * <pre>
     * Point3D p1 = new Point3D(0, 0, 0);
     * Point3D p2 = new Point3D(3, 4, 0);
     * double distance = p1.distanceTo(p2); // Returns 5.0
     * </pre>
     * 
     * @param other the other point to measure distance to
     * @return the Euclidean distance between this point and the other point
     * @throws IllegalArgumentException if other is null
     */
    public double distanceTo(Point3D other) {
        if (other == null) {
            logger.log(Level.SEVERE, "Attempted to calculate distance to null point");
            throw new IllegalArgumentException("Cannot calculate distance to null point");
        }
        
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        double dz = this.z - other.z;
        
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        
        logger.log(Level.INFO, "Calculated distance from {0} to {1}: {2}", 
                   new Object[]{this, other, distance});
        
        return distance;
    }
    
    /**
     * Calculates the Manhattan distance (L1 norm) between this point and another point.
     * 
     * <p>The Manhattan distance, also known as taxicab distance, is the sum of the
     * absolute differences of the coordinates:
     * <pre>
     * manhattanDistance = |x2-x1| + |y2-y1| + |z2-z1|
     * </pre>
     * 
     * <p>This metric is useful in grid-based pathfinding algorithms (like A*) and
     * is computationally cheaper than Euclidean distance as it avoids square root
     * calculations.
     * 
     * <p><b>Time Complexity:</b> O(1)
     * <p><b>Space Complexity:</b> O(1)
     * 
     * @param other the other point to measure distance to
     * @return the Manhattan distance between this point and the other point
     * @throws IllegalArgumentException if other is null
     */
    public double manhattanDistanceTo(Point3D other) {
        if (other == null) {
            logger.log(Level.SEVERE, "Attempted to calculate Manhattan distance to null point");
            throw new IllegalArgumentException("Cannot calculate Manhattan distance to null point");
        }
        
        double distance = Math.abs(this.x - other.x) + 
                         Math.abs(this.y - other.y) + 
                         Math.abs(this.z - other.z);
        
        logger.log(Level.INFO, "Calculated Manhattan distance from {0} to {1}: {2}", 
                   new Object[]{this, other, distance});
        
        return distance;
    }
    
    /**
     * Calculates the Chebyshev distance (L∞ norm) between this point and another point.
     * 
     * <p>The Chebyshev distance is the maximum absolute difference among the coordinates:
     * <pre>
     * chebyshevDistance = max(|x2-x1|, |y2-y1|, |z2-z1|)
     * </pre>
     * 
     * <p>This metric is useful in chess-like movement algorithms and certain optimization
     * problems where diagonal movement has the same cost as orthogonal movement.
     * 
     * <p><b>Time Complexity:</b> O(1)
     * <p><b>Space Complexity:</b> O(1)
     * 
     * @param other the other point to measure distance to
     * @return the Chebyshev distance between this point and the other point
     * @throws IllegalArgumentException if other is null
     */
    public double chebyshevDistanceTo(Point3D other) {
        if (other == null) {
            logger.log(Level.SEVERE, "Attempted to calculate Chebyshev distance to null point");
            throw new IllegalArgumentException("Cannot calculate Chebyshev distance to null point");
        }
        
        double distance = Math.max(Math.abs(this.x - other.x), 
                         Math.max(Math.abs(this.y - other.y), 
                                  Math.abs(this.z - other.z)));
        
        logger.log(Level.INFO, "Calculated Chebyshev distance from {0} to {1}: {2}", 
                   new Object[]{this, other, distance});
        
        return distance;
    }
    
    /**
     * Rotates this point around the X-axis by the specified angle.
     * 
     * <p>This method performs a rotation transformation using the rotation matrix:
     * <pre>
     * | 1    0         0      |
     * | 0    cos(θ)   -sin(θ) |
     * | 0    sin(θ)    cos(θ) |
     * </pre>
     * 
     * <p>The rotation follows the right-hand rule: with the thumb pointing along
     * the positive X-axis, the fingers curl in the direction of positive rotation.
     * 
     * <p>Since this class follows the Value Object pattern, this method returns a
     * new Point3D instance rather than modifying the current object.
     * 
     * <p><b>Time Complexity:</b> O(1)
     * <p><b>Space Complexity:</b> O(1)
     * 
     * <p><b>Example usage:</b>
     * <pre>
     * Point3D point = new Point3D(0, 1, 0);
     * Point3D rotated = point.rotateX(Math.PI / 2); // 90-degree rotation
     * // rotated ≈ (0, 0, 1)
     * </pre>
     * 
     * @param angleRadians the angle to rotate in radians (positive = counter-clockwise)
     * @return a new Point3D representing the rotated point
     */
    public Point3D rotateX(double angleRadians) {
        logger.log(Level.INFO, "Rotating point {0} around X-axis by {1} radians", 
                   new Object[]{this, angleRadians});
        
        double cos = Math.cos(angleRadians);
        double sin = Math.sin(angleRadians);
        
        double newY = y * cos - z * sin;
        double newZ = y * sin + z * cos;
        
        return new Point3D(x, newY, newZ);
    }
    
    /**
     * Rotates this point around the Y-axis by the specified angle.
     * 
     * <p>This method performs a rotation transformation using the rotation matrix:
     * <pre>
     * | cos(θ)   0   sin(θ) |
     * | 0        1   0      |
     * | -sin(θ)  0   cos(θ) |
     * </pre>
     * 
     * <p>The rotation follows the right-hand rule: with the thumb pointing along
     * the positive Y-axis, the fingers curl in the direction of positive rotation.
     * 
     * <p><b>Time Complexity:</b> O(1)
     * <p><b>Space Complexity:</b> O(1)
     * 
     * @param angleRadians the angle to rotate in radians (positive = counter-clockwise)
     * @return a new Point3D representing the rotated point
     */
    public Point3D rotateY(double angleRadians) {
        logger.log(Level.INFO, "Rotating point {0} around Y-axis by {1} radians", 
                   new Object[]{this, angleRadians});
        
        double cos = Math.cos(angleRadians);
        double sin = Math.sin(angleRadians);
        
        double newX = x * cos + z * sin;
        double newZ = -x * sin + z * cos;
        
        return new Point3D(newX, y, newZ);
    }
    
    /**
     * Rotates this point around the Z-axis by the specified angle.
     * 
     * <p>This method performs a rotation transformation using the rotation matrix:
     * <pre>
     * | cos(θ)  -sin(θ)  0 |
     * | sin(θ)   cos(θ)  0 |
     * | 0        0       1 |
     * </pre>
     * 
     * <p>The rotation follows the right-hand rule: with the thumb pointing along
     * the positive Z-axis, the fingers curl in the direction of positive rotation.
     * 
     * <p><b>Time Complexity:</b> O(1)
     * <p><b>Space Complexity:</b> O(1)
     * 
     * @param angleRadians the angle to rotate in radians (positive = counter-clockwise)
     * @return a new Point3D representing the rotated point
     */
    public Point3D rotateZ(double angleRadians) {
        logger.log(Level.INFO, "Rotating point {0} around Z-axis by {1} radians", 
                   new Object[]{this, angleRadians});
        
        double cos = Math.cos(angleRadians);
        double sin = Math.sin(angleRadians);
        
        double newX = x * cos - y * sin;
        double newY = x * sin + y * cos;
        
        return new Point3D(newX, newY, z);
    }
    
    /**
     * Adds another point to this point (vector addition).
     * 
     * <p>This method treats both points as position vectors from the origin
     * and returns their vector sum. This operation is fundamental in linear
     * algebra and is used extensively in physics simulations and computer graphics.
     * 
     * <p><b>Time Complexity:</b> O(1)
     * <p><b>Space Complexity:</b> O(1)
     * 
     * <p><b>Example usage:</b>
     * <pre>
     * Point3D p1 = new Point3D(1, 2, 3);
     * Point3D p2 = new Point3D(4, 5, 6);
     * Point3D sum = p1.add(p2); // Returns Point3D(5, 7, 9)
     * </pre>
     * 
     * @param other the point to add to this point
     * @return a new Point3D representing the sum
     * @throws IllegalArgumentException if other is null
     */
    public Point3D add(Point3D other) {
        if (other == null) {
            logger.log(Level.SEVERE, "Attempted to add null point");
            throw new IllegalArgumentException("Cannot add null point");
        }
        
        logger.log(Level.INFO, "Adding {0} to {1}", new Object[]{other, this});
        return new Point3D(this.x + other.x, this.y + other.y, this.z + other.z);
    }
    
    /**
     * Subtracts another point from this point (vector subtraction).
     * 
     * <p>This method computes the displacement vector from the other point to
     * this point. The result represents the direction and distance from 'other'
     * to 'this'.
     * 
     * <p><b>Time Complexity:</b> O(1)
     * <p><b>Space Complexity:</b> O(1)
     * 
     * @param other the point to subtract from this point
     * @return a new Point3D representing the difference
     * @throws IllegalArgumentException if other is null
     */
    public Point3D subtract(Point3D other) {
        if (other == null) {
            logger.log(Level.SEVERE, "Attempted to subtract null point");
            throw new IllegalArgumentException("Cannot subtract null point");
        }
        
        logger.log(Level.INFO, "Subtracting {0} from {1}", new Object[]{other, this});
        return new Point3D(this.x - other.x, this.y - other.y, this.z - other.z);
    }
    
    /**
     * Multiplies this point by a scalar value (scalar multiplication).
     * 
     * <p>This operation scales the distance of the point from the origin by the
     * given factor. It's commonly used to scale vectors or to interpolate between
     * points.
     * 
     * <p><b>Time Complexity:</b> O(1)
     * <p><b>Space Complexity:</b> O(1)
     * 
     * <p><b>Example usage:</b>
     * <pre>
     * Point3D p = new Point3D(1, 2, 3);
     * Point3D scaled = p.multiply(2.0); // Returns Point3D(2, 4, 6)
     * </pre>
     * 
     * @param scalar the scalar value to multiply by
     * @return a new Point3D representing the scaled point
     */
    public Point3D multiply(double scalar) {
        logger.log(Level.INFO, "Multiplying {0} by scalar {1}", 
                   new Object[]{this, scalar});
        return new Point3D(this.x * scalar, this.y * scalar, this.z * scalar);
    }
    
    /**
     * Calculates the dot product of this point and another point (treating them as vectors).
     * 
     * <p>The dot product is calculated as:
     * <pre>
     * dot = x1*x2 + y1*y2 + z1*z2
     * </pre>
     * 
     * <p>The dot product is a fundamental operation in vector algebra with many
     * applications:
     * <ul>
     *   <li>Calculating the angle between vectors: cos(θ) = (a·b)/(|a||b|)</li>
     *   <li>Testing for perpendicularity: vectors are perpendicular if dot = 0</li>
     *   <li>Projecting one vector onto another</li>
     * </ul>
     * 
     * <p><b>Time Complexity:</b> O(1)
     * <p><b>Space Complexity:</b> O(1)
     * 
     * @param other the other point (vector) to compute the dot product with
     * @return the dot product of this point and the other point
     * @throws IllegalArgumentException if other is null
     */
    public double dot(Point3D other) {
        if (other == null) {
            logger.log(Level.SEVERE, "Attempted to calculate dot product with null point");
            throw new IllegalArgumentException("Cannot calculate dot product with null point");
        }
        
        double result = this.x * other.x + this.y * other.y + this.z * other.z;
        logger.log(Level.INFO, "Calculated dot product of {0} and {1}: {2}", 
                   new Object[]{this, other, result});
        
        return result;
    }
    
    /**
     * Calculates the cross product of this point and another point (treating them as vectors).
     * 
     * <p>The cross product is calculated as:
     * <pre>
     * cross = (y1*z2 - z1*y2, z1*x2 - x1*z2, x1*y2 - y1*x2)
     * </pre>
     * 
     * <p>The cross product is perpendicular to both input vectors and its magnitude
     * equals the area of the parallelogram formed by the two vectors. This operation
     * is essential for:
     * <ul>
     *   <li>Computing surface normals in 3D graphics</li>
     *   <li>Determining the orientation of three points</li>
     *   <li>Calculating torque in physics simulations</li>
     * </ul>
     * 
     * <p><b>Time Complexity:</b> O(1)
     * <p><b>Space Complexity:</b> O(1)
     * 
     * @param other the other point (vector) to compute the cross product with
     * @return a new Point3D representing the cross product
     * @throws IllegalArgumentException if other is null
     */
    public Point3D cross(Point3D other) {
        if (other == null) {
            logger.log(Level.SEVERE, "Attempted to calculate cross product with null point");
            throw new IllegalArgumentException("Cannot calculate cross product with null point");
        }
        
        double newX = this.y * other.z - this.z * other.y;
        double newY = this.z * other.x - this.x * other.z;
        double newZ = this.x * other.y - this.y * other.x;
        
        Point3D result = new Point3D(newX, newY, newZ);
        logger.log(Level.INFO, "Calculated cross product of {0} and {1}: {2}", 
                   new Object[]{this, other, result});
        
        return result;
    }
    
    /**
     * Calculates the magnitude (length) of this point when treated as a vector from the origin.
     * 
     * <p>The magnitude is calculated as:
     * <pre>
     * magnitude = sqrt(x² + y² + z²)
     * </pre>
     * 
     * <p>This is equivalent to the Euclidean distance from the origin and is used
     * in normalization, physics calculations, and determining vector lengths.
     * 
     * <p><b>Time Complexity:</b> O(1)
     * <p><b>Space Complexity:</b> O(1)
     * 
     * @return the magnitude of this point (as a vector)
     */
    public double magnitude() {
        double mag = Math.sqrt(x * x + y * y + z * z);
        logger.log(Level.INFO, "Calculated magnitude of {0}: {1}", 
                   new Object[]{this, mag});
        return mag;
    }
    
    /**
     * Returns a normalized version of this point (unit vector in the same direction).
     * 
     * <p>A normalized vector has a magnitude of 1 while maintaining the same direction
     * as the original vector. This is computed by dividing each component by the
     * vector's magnitude.
     * 
     * <p>Normalized vectors are essential in:
     * <ul>
     *   <li>Representing pure directions without magnitude</li>
     *   <li>Lighting calculations in computer graphics</li>
     *   <li>Physics simulations requiring directional vectors</li>
     * </ul>
     * 
     * <p><b>Time Complexity:</b> O(1)
     * <p><b>Space Complexity:</b> O(1)
     * 
     * @return a new Point3D representing the normalized vector
     * @throws ArithmeticException if the magnitude is zero (cannot normalize zero vector)
     */
    public Point3D normalize() {
        double mag = magnitude();
        
        if (Math.abs(mag) < EPSILON) {
            logger.log(Level.SEVERE, "Attempted to normalize zero vector: {0}", this);
            throw new ArithmeticException("Cannot normalize a zero vector");
        }
        
        Point3D result = new Point3D(x / mag, y / mag, z / mag);
        logger.log(Level.INFO, "Normalized {0} to {1}", new Object[]{this, result});
        
        return result;
    }
    
    /**
     * Calculates the midpoint between this point and another point.
     * 
     * <p>The midpoint is the point that is equidistant from both points and
     * lies on the line segment connecting them. It's calculated as the average
     * of the coordinates:
     * <pre>
     * midpoint = ((x1+x2)/2, (y1+y2)/2, (z1+z2)/2)
     * </pre>
     * 
     * <p><b>Time Complexity:</b> O(1)
     * <p><b>Space Complexity:</b> O(1)
     * 
     * @param other the other point to find the midpoint with
     * @return a new Point3D representing the midpoint
     * @throws IllegalArgumentException if other is null
     */
    public Point3D midpoint(Point3D other) {
        if (other == null) {
            logger.log(Level.SEVERE, "Attempted to calculate midpoint with null point");
            throw new IllegalArgumentException("Cannot calculate midpoint with null point");
        }
        
        Point3D mid = new Point3D(
            (this.x + other.x) / 2.0,
            (this.y + other.y) / 2.0,
            (this.z + other.z) / 2.0
        );
        
        logger.log(Level.INFO, "Calculated midpoint of {0} and {1}: {2}", 
                   new Object[]{this, other, mid});
        
        return mid;
    }
    
    /**
     * Linearly interpolates between this point and another point.
     * 
     * <p>Linear interpolation (lerp) calculates a point along the line segment
     * between two points based on a parameter t:
     * <ul>
     *   <li>t = 0 returns this point</li>
     *   <li>t = 1 returns the other point</li>
     *   <li>t = 0.5 returns the midpoint</li>
     *   <li>t can be outside [0,1] for extrapolation</li>
     * </ul>
     * 
     * <p>This operation is fundamental in animation, pathfinding, and graphics
     * rendering for smooth transitions between positions.
     * 
     * <p><b>Time Complexity:</b> O(1)
     * <p><b>Space Complexity:</b> O(1)
     * 
     * @param other the target point to interpolate towards
     * @param t the interpolation parameter (0.0 = this point, 1.0 = other point)
     * @return a new Point3D representing the interpolated point
     * @throws IllegalArgumentException if other is null
     */
    public Point3D lerp(Point3D other, double t) {
        if (other == null) {
            logger.log(Level.SEVERE, "Attempted to interpolate with null point");
            throw new IllegalArgumentException("Cannot interpolate with null point");
        }
        
        if (t < 0.0 || t > 1.0) {
            logger.log(Level.WARNING, "Interpolation parameter t={0} is outside [0,1] range", t);
        }
        
        Point3D result = new Point3D(
            this.x + t * (other.x - this.x),
            this.y + t * (other.y - this.y),
            this.z + t * (other.z - this.z)
        );
        
        logger.log(Level.INFO, "Interpolated between {0} and {1} with t={2}: {3}", 
                   new Object[]{this, other, t, result});
        
        return result;
    }
    
    /**
     * Checks if this point is equal to another object.
     * 
     * <p>Two Point3D objects are considered equal if their coordinates are equal
     * within a small tolerance (EPSILON) to account for floating-point precision errors.
     * 
     * <p>This implementation follows the contract defined by Object.equals():
     * <ul>
     *   <li>Reflexive: x.equals(x) returns true</li>
     *   <li>Symmetric: x.equals(y) returns the same as y.equals(x)</li>
     *   <li>Transitive: if x.equals(y) and y.equals(z), then x.equals(z)</li>
     *   <li>Consistent: multiple invocations return the same result</li>
     *   <li>x.equals(null) returns false</li>
     * </ul>
     * 
     * @param obj the object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Point3D other = (Point3D) obj;
        
        boolean isEqual = Math.abs(this.x - other.x) < EPSILON &&
                         Math.abs(this.y - other.y) < EPSILON &&
                         Math.abs(this.z - other.z) < EPSILON;
        
        if (isEqual) {
            logger.log(Level.INFO, "Points {0} and {1} are equal", 
                       new Object[]{this, other});
        }
        
        return isEqual;
    }
    
    /**
     * Returns a hash code for this point.
     * 
     * <p>This implementation follows the contract between equals() and hashCode():
     * if two objects are equal according to equals(), they must have the same hash code.
     * 
     * <p>The hash code is computed using the standard library's Double.hashCode()
     * method combined using a prime number multiplier to reduce collisions.
     * 
     * @return a hash code value for this point
     */
    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + Double.hashCode(x);
        result = 31 * result + Double.hashCode(y);
        result = 31 * result + Double.hashCode(z);
        return result;
    }
    
    /**
     * Returns a string representation of this point.
     * 
     * <p>The format is "Point3D(x, y, z)" which provides a clear and concise
     * representation suitable for debugging and logging.
     * 
     * @return a string representation of this point
     */
    @Override
    public String toString() {
        return String.format("Point3D(%.2f, %.2f, %.2f)", x, y, z);
    }
    
    /**
     * Demonstrates usage of the Point3D class.
     * 
     * <p>This main method serves as both documentation and a test harness,
     * showing common operations and expected results.
     */
    public static void main(String[] args) {
        logger.info("Starting Point3D demonstration");
        
        // Create some points
        Point3D p1 = new Point3D(3, 4, 0);
        Point3D p2 = new Point3D(0, 0, 0);
        Point3D p3 = Point3D.origin();
        
        // Distance calculations
        System.out.println("Distance from " + p1 + " to " + p2 + ": " + p1.distanceTo(p2));
        
        // Rotation
        Point3D rotated = p1.rotateZ(Math.PI / 2);
        System.out.println("Point " + p1 + " rotated 90° around Z-axis: " + rotated);
        
        // Vector operations
        Point3D sum = p1.add(new Point3D(1, 1, 1));
        System.out.println("Sum: " + sum);
        
        // Normalization
        Point3D normalized = p1.normalize();
        System.out.println("Normalized " + p1 + ": " + normalized);
        System.out.println("Magnitude of normalized vector: " + normalized.magnitude());
        
        logger.info("Point3D demonstration completed");
    }
}