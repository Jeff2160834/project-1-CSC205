package com.csc205.project1;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Represents an immutable line segment in three-dimensional Euclidean space.
 * 
 * <p>This class models a directed line segment defined by two endpoints in 3D space.
 * It provides operations for geometric queries, transformations, and spatial relationships
 * between lines. The class follows the Value Object pattern, ensuring immutability and
 * thread safety.
 * 
 * <p>A line segment is the shortest path between two points and has finite length.
 * This distinguishes it from infinite lines or rays. Common operations include:
 * <ul>
 *   <li>Length and midpoint calculations</li>
 *   <li>Shortest distance computations (point-to-line, line-to-line)</li>
 *   <li>Intersection testing and calculation</li>
 *   <li>Parallel and perpendicular testing</li>
 *   <li>Projection and closest point operations</li>
 * </ul>
 * 
 * <p><b>Design Patterns Used:</b>
 * <ul>
 *   <li><b>Value Object Pattern:</b> Immutable representation of a geometric line segment</li>
 *   <li><b>Composite Pattern:</b> Composed of two Point3D objects</li>
 *   <li><b>Factory Method Pattern:</b> Static factory methods for common line types</li>
 *   <li><b>Strategy Pattern:</b> Multiple distance calculation strategies</li>
 * </ul>
 * 
 * <p><b>Geometric Foundations:</b>
 * This class implements fundamental computational geometry algorithms essential for:
 * <ul>
 *   <li>Computer graphics and rendering (ray tracing, visibility testing)</li>
 *   <li>Collision detection in physics engines and games</li>
 *   <li>Geographic Information Systems (GIS) and mapping</li>
 *   <li>Robotics path planning and obstacle avoidance</li>
 *   <li>Computer-Aided Design (CAD) systems</li>
 * </ul>
 * 
 * @author Claude
 * @version 1.0
 * @since 1.0
 * @see Point3D
 */
public class Line3D {
    
    private static final Logger logger = Logger.getLogger(Line3D.class.getName());
    
    // Immutable fields representing the line segment endpoints
    private final Point3D start;
    private final Point3D end;
    
    // Tolerance for floating-point comparisons
    private static final double EPSILON = 1e-10;
    
    /**
     * Constructs a new Line3D with the specified start and end points.
     * 
     * <p>This constructor creates a directed line segment from the start point to
     * the end point. The direction matters for certain operations like determining
     * which side of a line a point lies on.
     * 
     * <p><b>Example usage:</b>
     * <pre>
     * Point3D p1 = new Point3D(0, 0, 0);
     * Point3D p2 = new Point3D(1, 1, 1);
     * Line3D line = new Line3D(p1, p2);
     * </pre>
     * 
     * @param start the starting point of the line segment
     * @param end the ending point of the line segment
     * @throws IllegalArgumentException if either point is null or if points are identical
     */
    public Line3D(Point3D start, Point3D end) {
        if (start == null || end == null) {
            logger.log(Level.SEVERE, "Attempted to create Line3D with null point(s)");
            throw new IllegalArgumentException("Start and end points cannot be null");
        }
        
        if (start.equals(end)) {
            logger.log(Level.SEVERE, "Attempted to create Line3D with identical points: {0}", start);
            throw new IllegalArgumentException("Start and end points must be different (degenerate line)");
        }
        
        this.start = start;
        this.end = end;
        
        logger.log(Level.INFO, "Created Line3D from {0} to {1}", 
                   new Object[]{start, end});
    }
    
    /**
     * Factory method to create a line segment along the X-axis.
     * 
     * <p>Creates a unit-length line segment from the origin to (1, 0, 0).
     * This is useful for establishing reference frames and coordinate system
     * transformations.
     * 
     * @return a new Line3D along the positive X-axis
     */
    public static Line3D xAxis() {
        logger.info("Creating X-axis line segment");
        return new Line3D(Point3D.origin(), Point3D.unitX());
    }
    
    /**
     * Factory method to create a line segment along the Y-axis.
     * 
     * <p>Creates a unit-length line segment from the origin to (0, 1, 0).
     * 
     * @return a new Line3D along the positive Y-axis
     */
    public static Line3D yAxis() {
        logger.info("Creating Y-axis line segment");
        return new Line3D(Point3D.origin(), Point3D.unitY());
    }
    
    /**
     * Factory method to create a line segment along the Z-axis.
     * 
     * <p>Creates a unit-length line segment from the origin to (0, 0, 1).
     * 
     * @return a new Line3D along the positive Z-axis
     */
    public static Line3D zAxis() {
        logger.info("Creating Z-axis line segment");
        return new Line3D(Point3D.origin(), Point3D.unitZ());
    }
    
    /**
     * Factory method to create a line from a point and a direction vector.
     * 
     * <p>This factory method demonstrates an alternative construction approach
     * where a line is defined by a starting point and a direction vector with
     * a specified length.
     * 
     * <p><b>Example usage:</b>
     * <pre>
     * Point3D origin = Point3D.origin();
     * Point3D direction = new Point3D(1, 1, 0).normalize();
     * double length = 5.0;
     * Line3D line = Line3D.fromDirection(origin, direction, length);
     * </pre>
     * 
     * @param start the starting point of the line
     * @param direction the direction vector (should be normalized for predictable length)
     * @param length the desired length of the line segment
     * @return a new Line3D in the specified direction with the specified length
     * @throws IllegalArgumentException if start or direction is null, or if length is non-positive
     */
    public static Line3D fromDirection(Point3D start, Point3D direction, double length) {
        if (start == null || direction == null) {
            logger.log(Level.SEVERE, "Attempted to create Line3D from direction with null parameter(s)");
            throw new IllegalArgumentException("Start point and direction cannot be null");
        }
        
        if (length <= 0) {
            logger.log(Level.SEVERE, "Attempted to create Line3D with non-positive length: {0}", length);
            throw new IllegalArgumentException("Length must be positive");
        }
        
        Point3D end = start.add(direction.multiply(length));
        logger.log(Level.INFO, "Created Line3D from point {0} in direction {1} with length {2}", 
                   new Object[]{start, direction, length});
        
        return new Line3D(start, end);
    }
    
    // Accessor methods
    
    /**
     * Returns the starting point of this line segment.
     * 
     * @return the start point
     */
    public Point3D getStart() {
        return start;
    }
    
    /**
     * Returns the ending point of this line segment.
     * 
     * @return the end point
     */
    public Point3D getEnd() {
        return end;
    }
    
    /**
     * Calculates the length of this line segment.
     * 
     * <p>The length is computed as the Euclidean distance between the start and
     * end points. This is a fundamental metric used in many geometric algorithms.
     * 
     * <p><b>Time Complexity:</b> O(1)
     * <p><b>Space Complexity:</b> O(1)
     * 
     * <p><b>Example usage:</b>
     * <pre>
     * Line3D line = new Line3D(new Point3D(0, 0, 0), new Point3D(3, 4, 0));
     * double length = line.length(); // Returns 5.0
     * </pre>
     * 
     * @return the length of the line segment
     */
    public double length() {
        double len = start.distanceTo(end);
        logger.log(Level.INFO, "Calculated length of {0}: {1}", new Object[]{this, len});
        return len;
    }
    
    /**
     * Returns the direction vector of this line segment.
     * 
     * <p>The direction vector points from start to end and is not normalized.
     * Its magnitude equals the line's length. To get a unit direction vector,
     * call {@link #getUnitDirection()}.
     * 
     * <p><b>Time Complexity:</b> O(1)
     * <p><b>Space Complexity:</b> O(1)
     * 
     * @return a Point3D representing the direction vector
     */
    public Point3D getDirection() {
        Point3D direction = end.subtract(start);
        logger.log(Level.INFO, "Calculated direction vector for {0}: {1}", 
                   new Object[]{this, direction});
        return direction;
    }
    
    /**
     * Returns the normalized direction vector (unit vector) of this line segment.
     * 
     * <p>The unit direction vector points from start to end and has a magnitude of 1.
     * This is useful for directional calculations, ray casting, and projections.
     * 
     * <p><b>Time Complexity:</b> O(1)
     * <p><b>Space Complexity:</b> O(1)
     * 
     * @return a Point3D representing the unit direction vector
     */
    public Point3D getUnitDirection() {
        Point3D unitDir = getDirection().normalize();
        logger.log(Level.INFO, "Calculated unit direction for {0}: {1}", 
                   new Object[]{this, unitDir});
        return unitDir;
    }
    
    /**
     * Calculates the midpoint of this line segment.
     * 
     * <p>The midpoint is equidistant from both endpoints and lies exactly at the
     * center of the line segment. This operation is commonly used in bisection
     * algorithms and spatial subdivision techniques.
     * 
     * <p><b>Time Complexity:</b> O(1)
     * <p><b>Space Complexity:</b> O(1)
     * 
     * @return a Point3D representing the midpoint
     */
    public Point3D midpoint() {
        Point3D mid = start.midpoint(end);
        logger.log(Level.INFO, "Calculated midpoint of {0}: {1}", new Object[]{this, mid});
        return mid;
    }
    
    /**
     * Calculates a point along the line at the specified parameter value.
     * 
     * <p>This method uses the parametric form of a line:
     * <pre>
     * P(t) = start + t * (end - start)
     * </pre>
     * 
     * <p>Where:
     * <ul>
     *   <li>t = 0 returns the start point</li>
     *   <li>t = 1 returns the end point</li>
     *   <li>t = 0.5 returns the midpoint</li>
     *   <li>t can be outside [0, 1] to extend beyond the segment</li>
     * </ul>
     * 
     * <p>This method is fundamental for line interpolation, subdivision algorithms,
     * and geometric constructions.
     * 
     * <p><b>Time Complexity:</b> O(1)
     * <p><b>Space Complexity:</b> O(1)
     * 
     * @param t the parameter value (typically 0 ≤ t ≤ 1 for points on the segment)
     * @return a Point3D at position P(t)
     */
    public Point3D pointAt(double t) {
        if (t < 0.0 || t > 1.0) {
            logger.log(Level.WARNING, "Parameter t={0} is outside [0,1] range for line {1}", 
                       new Object[]{t, this});
        }
        
        Point3D point = start.lerp(end, t);
        logger.log(Level.INFO, "Calculated point at t={0} on {1}: {2}", 
                   new Object[]{t, this, point});
        
        return point;
    }
    
    /**
     * Calculates the shortest distance from a point to this line segment.
     * 
     * <p>This method implements a fundamental computational geometry algorithm.
     * The shortest distance depends on where the perpendicular from the point
     * intersects the line:
     * <ul>
     *   <li>If the perpendicular hits within the segment, distance is to that point</li>
     *   <li>If the perpendicular hits before the start, distance is to the start point</li>
     *   <li>If the perpendicular hits after the end, distance is to the end point</li>
     * </ul>
     * 
     * <p><b>Algorithm:</b>
     * <ol>
     *   <li>Project the point onto the infinite line containing this segment</li>
     *   <li>Clamp the projection parameter to [0, 1] to stay on the segment</li>
     *   <li>Calculate distance from point to the clamped position</li>
     * </ol>
     * 
     * <p><b>Time Complexity:</b> O(1)
     * <p><b>Space Complexity:</b> O(1)
     * 
     * <p><b>Applications:</b>
     * <ul>
     *   <li>Collision detection (point-line collisions)</li>
     *   <li>Nearest-neighbor queries in spatial databases</li>
     *   <li>Proximity testing in robotics</li>
     *   <li>Distance fields for graphics rendering</li>
     * </ul>
     * 
     * @param point the point to measure distance from
     * @return the shortest distance from the point to this line segment
     * @throws IllegalArgumentException if point is null
     */
    public double distanceToPoint(Point3D point) {
        if (point == null) {
            logger.log(Level.SEVERE, "Attempted to calculate distance to null point");
            throw new IllegalArgumentException("Point cannot be null");
        }
        
        // Vector from start to point
        Point3D startToPoint = point.subtract(start);
        
        // Direction vector of the line
        Point3D direction = getDirection();
        
        // Length squared of the line segment (avoid square root)
        double lengthSquared = direction.dot(direction);
        
        // Project point onto the line: t = (P - start) · direction / |direction|²
        double t = startToPoint.dot(direction) / lengthSquared;
        
        // Clamp t to [0, 1] to stay within the segment
        t = Math.max(0.0, Math.min(1.0, t));
        
        // Find the closest point on the segment
        Point3D closestPoint = pointAt(t);
        
        // Calculate distance
        double distance = point.distanceTo(closestPoint);
        
        logger.log(Level.INFO, "Calculated distance from point {0} to line {1}: {2} (t={3})", 
                   new Object[]{point, this, distance, t});
        
        return distance;
    }
    
    /**
     * Finds the closest point on this line segment to a given point.
     * 
     * <p>This method returns the actual point on the line segment that is nearest
     * to the given point. This is the point where the perpendicular from the given
     * point intersects the line segment (or an endpoint if the perpendicular falls
     * outside the segment).
     * 
     * <p><b>Time Complexity:</b> O(1)
     * <p><b>Space Complexity:</b> O(1)
     * 
     * @param point the point to find the closest point to
     * @return the closest Point3D on this line segment to the given point
     * @throws IllegalArgumentException if point is null
     */
    public Point3D closestPointTo(Point3D point) {
        if (point == null) {
            logger.log(Level.SEVERE, "Attempted to find closest point to null point");
            throw new IllegalArgumentException("Point cannot be null");
        }
        
        Point3D startToPoint = point.subtract(start);
        Point3D direction = getDirection();
        double lengthSquared = direction.dot(direction);
        double t = startToPoint.dot(direction) / lengthSquared;
        
        t = Math.max(0.0, Math.min(1.0, t));
        Point3D closest = pointAt(t);
        
        logger.log(Level.INFO, "Found closest point on {0} to {1}: {2}", 
                   new Object[]{this, point, closest});
        
        return closest;
    }
    
    /**
     * Calculates the shortest distance between this line segment and another line segment.
     * 
     * <p>This is one of the most complex and important operations in computational geometry.
     * The shortest distance between two line segments in 3D can occur in several scenarios:
     * <ul>
     *   <li><b>Case 1:</b> Lines are parallel → distance between parallel lines</li>
     *   <li><b>Case 2:</b> Lines are skew (non-intersecting, non-parallel) → perpendicular distance</li>
     *   <li><b>Case 3:</b> Lines intersect → distance is 0</li>
     *   <li><b>Case 4:</b> Closest points are at segment endpoints → endpoint-to-segment distance</li>
     * </ul>
     * 
     * <p><b>Algorithm:</b>
     * <ol>
     *   <li>Calculate direction vectors for both lines</li>
     *   <li>Check if lines are parallel using cross product</li>
     *   <li>If parallel, calculate distance between parallel lines</li>
     *   <li>If not parallel, find closest points on infinite lines</li>
     *   <li>Clamp parameters to [0, 1] to stay within segments</li>
     *   <li>If clamping occurred, recalculate to handle endpoint cases</li>
     *   <li>Return distance between final closest points</li>
     * </ol>
     * 
     * <p><b>Time Complexity:</b> O(1)
     * <p><b>Space Complexity:</b> O(1)
     * 
     * <p><b>Applications:</b>
     * <ul>
     *   <li>Collision detection in physics engines and games</li>
     *   <li>Robot path planning and obstacle avoidance</li>
     *   <li>Computer-Aided Design (CAD) clearance checks</li>
     *   <li>Network routing and cable layout optimization</li>
     *   <li>Molecular modeling (distance between bonds)</li>
     * </ul>
     * 
     * <p><b>Mathematical Foundation:</b>
     * For infinite lines, the closest points are found by solving:
     * <pre>
     * d1 · (P1(s) - P2(t)) = 0  (perpendicular to line 1)
     * d2 · (P1(s) - P2(t)) = 0  (perpendicular to line 2)
     * </pre>
     * This yields a 2x2 linear system that can be solved analytically.
     * 
     * @param other the other line segment to measure distance to
     * @return the shortest distance between the two line segments
     * @throws IllegalArgumentException if other is null
     */
    public double shortestDistanceTo(Line3D other) {
        if (other == null) {
            logger.log(Level.SEVERE, "Attempted to calculate distance to null line");
            throw new IllegalArgumentException("Other line cannot be null");
        }
        
        logger.log(Level.INFO, "Calculating shortest distance between {0} and {1}", 
                   new Object[]{this, other});
        
        Point3D d1 = this.getDirection();
        Point3D d2 = other.getDirection();
        Point3D w0 = this.start.subtract(other.start);
        
        double a = d1.dot(d1);  // |d1|²
        double b = d1.dot(d2);  // d1 · d2
        double c = d2.dot(d2);  // |d2|²
        double d = d1.dot(w0);  // d1 · w0
        double e = d2.dot(w0);  // d2 · w0
        
        double denominator = a * c - b * b;  // Always ≥ 0
        
        double s, t;
        
        // Check if lines are parallel (denominator ≈ 0)
        if (Math.abs(denominator) < EPSILON) {
            logger.log(Level.INFO, "Lines are parallel or nearly parallel");
            
            // Lines are parallel, choose arbitrary s = 0
            s = 0.0;
            t = (b > c ? d / b : e / c);  // Avoid division by near-zero
            t = Math.max(0.0, Math.min(1.0, t));
        } else {
            // Lines are not parallel
            s = (b * e - c * d) / denominator;
            t = (a * e - b * d) / denominator;
            
            // Check if the closest points fall within the segments
            boolean sClamped = false;
            boolean tClamped = false;
            
            if (s < 0.0) {
                s = 0.0;
                sClamped = true;
            } else if (s > 1.0) {
                s = 1.0;
                sClamped = true;
            }
            
            if (t < 0.0) {
                t = 0.0;
                tClamped = true;
            } else if (t > 1.0) {
                t = 1.0;
                tClamped = true;
            }
            
            // If we clamped one parameter, recalculate the other
            if (sClamped && !tClamped) {
                t = Math.max(0.0, Math.min(1.0, (b * s - e) / c));
            } else if (tClamped && !sClamped) {
                s = Math.max(0.0, Math.min(1.0, (b * t + d) / a));
            } else if (sClamped && tClamped) {
                // Both clamped, need to check all four endpoint combinations
                logger.log(Level.INFO, "Both parameters clamped, checking endpoint combinations");
                
                double minDist = Double.MAX_VALUE;
                
                // Check all four combinations of endpoints
                minDist = Math.min(minDist, this.start.distanceTo(other.start));
                minDist = Math.min(minDist, this.start.distanceTo(other.end));
                minDist = Math.min(minDist, this.end.distanceTo(other.start));
                minDist = Math.min(minDist, this.end.distanceTo(other.end));
                
                logger.log(Level.INFO, "Shortest distance (endpoint check): {0}", minDist);
                return minDist;
            }
        }
        
        // Calculate the closest points
        Point3D point1 = this.pointAt(s);
        Point3D point2 = other.pointAt(t);
        
        double distance = point1.distanceTo(point2);
        
        logger.log(Level.INFO, "Shortest distance: {0} (s={1}, t={2})", 
                   new Object[]{distance, s, t});
        
        return distance;
    }
    
    /**
     * Checks if this line segment is parallel to another line segment.
     * 
     * <p>Two lines are parallel if their direction vectors are parallel, meaning
     * their cross product is the zero vector (or very close to it, accounting for
     * floating-point precision).
     * 
     * <p><b>Mathematical Test:</b>
     * Lines are parallel if: |d1 × d2| ≈ 0
     * 
     * <p><b>Time Complexity:</b> O(1)
     * <p><b>Space Complexity:</b> O(1)
     * 
     * @param other the other line to test for parallelism
     * @return true if the lines are parallel, false otherwise
     * @throws IllegalArgumentException if other is null
     */
    public boolean isParallelTo(Line3D other) {
        if (other == null) {
            logger.log(Level.SEVERE, "Attempted to check parallelism with null line");
            throw new IllegalArgumentException("Other line cannot be null");
        }
        
        Point3D d1 = this.getUnitDirection();
        Point3D d2 = other.getUnitDirection();
        Point3D cross = d1.cross(d2);
        
        boolean isParallel = cross.magnitude() < EPSILON;
        
        logger.log(Level.INFO, "Lines {0} and {1} are {2}parallel", 
                   new Object[]{this, other, isParallel ? "" : "not "});
        
        return isParallel;
    }
    
    /**
     * Checks if this line segment is perpendicular to another line segment.
     * 
     * <p>Two lines are perpendicular if their direction vectors are orthogonal,
     * meaning their dot product is zero (or very close to it).
     * 
     * <p><b>Mathematical Test:</b>
     * Lines are perpendicular if: d1 · d2 ≈ 0
     * 
     * <p><b>Time Complexity:</b> O(1)
     * <p><b>Space Complexity:</b> O(1)
     * 
     * @param other the other line to test for perpendicularity
     * @return true if the lines are perpendicular, false otherwise
     * @throws IllegalArgumentException if other is null
     */
    public boolean isPerpendicularTo(Line3D other) {
        if (other == null) {
            logger.log(Level.SEVERE, "Attempted to check perpendicularity with null line");
            throw new IllegalArgumentException("Other line cannot be null");
        }
        
        Point3D d1 = this.getUnitDirection();
        Point3D d2 = other.getUnitDirection();
        double dotProduct = Math.abs(d1.dot(d2));
        
        boolean isPerpendicular = dotProduct < EPSILON;
        
        logger.log(Level.INFO, "Lines {0} and {1} are {2}perpendicular", 
                   new Object[]{this, other, isPerpendicular ? "" : "not "});
        
        return isPerpendicular;
    }
    
    /**
     * Calculates the angle between this line and another line in radians.
     * 
     * <p>The angle is calculated using the dot product formula:
     * <pre>
     * cos(θ) = (d1 · d2) / (|d1| |d2|)
     * </pre>
     * 
     * <p>The result is always in the range [0, π/2] since we use the absolute
     * value of the dot product to return the acute angle between the lines.
     * 
     * <p><b>Time Complexity:</b> O(1)
     * <p><b>Space Complexity:</b> O(1)
     * 
     * @param other the other line to measure the angle with
     * @return the angle between the lines in radians [0, π/2]
     * @throws IllegalArgumentException if other is null
     */
    public double angleTo(Line3D other) {
        if (other == null) {
            logger.log(Level.SEVERE, "Attempted to calculate angle with null line");
            throw new IllegalArgumentException("Other line cannot be null");
        }
        
        Point3D d1 = this.getUnitDirection();
        Point3D d2 = other.getUnitDirection();
        
        double dotProduct = Math.abs(d1.dot(d2));
        
        // Clamp to [-1, 1] to handle floating-point errors
        dotProduct = Math.max(-1.0, Math.min(1.0, dotProduct));
        
        double angle = Math.acos(dotProduct);
        
        logger.log(Level.INFO, "Angle between {0} and {1}: {2} radians ({3} degrees)", 
                   new Object[]{this, other, angle, Math.toDegrees(angle)});
        
        return angle;
    }
    
    /**
     * Checks if this line segment contains a specific point.
     * 
     * <p>A point is on the line segment if:
     * <ol>
     *   <li>It's collinear with the line (distance to line ≈ 0)</li>
     *   <li>It's between the start and end points (projection parameter ∈ [0, 1])</li>
     * </ol>
     * 
     * <p><b>Time Complexity:</b> O(1)
     * <p><b>Space Complexity:</b> O(1)
     * 
     * @param point the point to test
     * @return true if the point lies on the line segment, false otherwise
     * @throws IllegalArgumentException if point is null
     */
    public boolean contains(Point3D point) {
        if (point == null) {
            logger.log(Level.SEVERE, "Attempted to check if line contains null point");
            throw new IllegalArgumentException("Point cannot be null");
        }
        
        double distance = distanceToPoint(point);
        boolean isOnLine = distance < EPSILON;
        
        logger.log(Level.INFO, "Line {0} {1} point {2}", 
                   new Object[]{this, isOnLine ? "contains" : "does not contain", point});
        
        return isOnLine;
    }
    
    /**
     * Reverses the direction of this line segment.
     * 
     * <p>Returns a new Line3D with the start and end points swapped. This operation
     * is useful when you need to reverse the direction of a path or when working
     * with directed graphs.
     * 
     * <p><b>Time Complexity:</b> O(1)
     * <p><b>Space Complexity:</b> O(1)
     * 
     * @return a new Line3D with reversed direction
     */
    public Line3D reverse() {
        Line3D reversed = new Line3D(end, start);
        logger.log(Level.INFO, "Reversed line {0} to {1}", new Object[]{this, reversed});
        return reversed;
    }
    
    /**
     * Extends or shrinks this line segment by a specified factor.
     * 
     * <p>A scale factor > 1 extends the line, while 0 < factor < 1 shrinks it.
     * The line is scaled from its midpoint, maintaining its center position.
     * 
     * <p><b>Example usage:</b>
     * <pre>
     * Line3D line = new Line3D(new Point3D(0, 0, 0), new Point3D(2, 0, 0));
     * Line3D doubled = line.scale(2.0);  // Length = 4
     * Line3D halved = line.scale(0.5);   // Length = 1
     * </pre>
     * 
     * <p><b>Time Complexity:</b> O(1)
     * <p><b>Space Complexity:</b> O(1)
     * 
     * @param factor the scale factor (must be positive)
     * @return a new Line3D scaled by the specified factor
     * @throws IllegalArgumentException if factor is non-positive
     */
    public Line3D scale(double factor) {
        if (factor <= 0) {
            logger.log(Level.SEVERE, "Attempted to scale line by non-positive factor: {0}", factor);
            throw new IllegalArgumentException("Scale factor must be positive");
        }
        
        Point3D center = midpoint();
        Point3D direction = getDirection();
        Point3D scaledDirection = direction.multiply(factor / 2.0);
        
        Point3D newStart = center.subtract(scaledDirection);
        Point3D newEnd = center.add(scaledDirection);
        
        Line3D scaled = new Line3D(newStart, newEnd);
        logger.log(Level.INFO, "Scaled line {0} by factor {1}: {2}", 
                   new Object[]{this, factor, scaled});
        
        return scaled;
    }
    
    /**
     * Translates this line segment by a specified displacement vector.
     * 
     * <p>Both the start and end points are moved by the same displacement,
     * maintaining the line's length and direction.
     * 
     * <p><b>Time Complexity:</b> O(1)
     * <p><b>Space Complexity:</b> O(1)
     * 
     * @param displacement the vector to translate by
     * @return a new Line3D translated by the displacement
     * @throws IllegalArgumentException if displacement is null
     */
    public Line3D translate(Point3D displacement) {
        if (displacement == null) {
            logger.log(Level.SEVERE, "Attempted to translate line by null displacement");
            throw new IllegalArgumentException("Displacement cannot be null");
        }
        
        Point3D newStart = start.add(displacement);
        Point3D newEnd = end.add(displacement);
        
        Line3D translated = new Line3D(newStart, newEnd);
        logger.log(Level.INFO, "Translated line {0} by {1}: {2}", 
                   new Object[]{this, displacement, translated});
        
        return translated;
    }
    
    /**
     * Checks if this line is equal to another object.
     * 
     * <p>Two Line3D objects are considered equal if their start and end points
     * are equal. Note that this is a strict equality - lines with reversed
     * direction are not considered equal by this method.
     * 
     * @param obj the object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Line3D other = (Line3D) obj;
        
        boolean isEqual = this.start.equals(other.start) && this.end.equals(other.end);
        
        if (isEqual) {
            logger.log(Level.INFO, "Lines {0} and {1} are equal", 
                       new Object[]{this, other});
        }
        
        return isEqual;
    }
    
    /**
     * Returns a hash code for this line.
     * 
     * <p>The hash code is computed from the hash codes of the start and end points
     * using prime number multiplication to reduce collisions.
     * 
     * @return a hash code value for this line
     */
    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + start.hashCode();
        result = 31 * result + end.hashCode();
        return result;
    }
    
    /**
     * Returns a string representation of this line.
     * 
     * <p>The format is "Line3D[start → end]" which clearly shows the direction
     * and endpoints of the line segment.
     * 
     * @return a string representation of this line
     */
    @Override
    public String toString() {
        return String.format("Line3D[%s → %s]", start, end);
    }
    
    /**
     * Demonstrates usage of the Line3D class.
     * 
     * <p>This main method serves as both documentation and a test harness,
     * showing common operations and expected results.
     */
    public static void main(String[] args) {
        logger.info("Starting Line3D demonstration");
        
        // Create some lines
        Line3D line1 = new Line3D(new Point3D(0, 0, 0), new Point3D(4, 3, 0));
        Line3D line2 = new Line3D(new Point3D(0, 0, 1), new Point3D(4, 3, 1));
        
        System.out.println("Line 1: " + line1);
        System.out.println("Length: " + line1.length());
        System.out.println("Midpoint: " + line1.midpoint());
        
        // Distance calculations
        Point3D testPoint = new Point3D(2, 2, 0);
        System.out.println("\nDistance from " + testPoint + " to " + line1 + ": " 
                          + line1.distanceToPoint(testPoint));
        System.out.println("Closest point: " + line1.closestPointTo(testPoint));
        
        // Line-to-line distance
        System.out.println("\nShortest distance between lines: " + line1.shortestDistanceTo(line2));
        
        // Parallel and perpendicular tests
        Line3D line3 = new Line3D(new Point3D(0, 0, 0), new Point3D(0, 0, 5));
        System.out.println("\nLine 1 parallel to Line 2: " + line1.isParallelTo(line2));
        System.out.println("Line 1 perpendicular to Line 3: " + line1.isPerpendicularTo(line3));
        
        // Transformations
        Line3D scaled = line1.scale(2.0);
        System.out.println("\nOriginal line: " + line1);
        System.out.println("Scaled line (2x): " + scaled);
        
        logger.info("Line3D demonstration completed");
    }
}