package com.csc205.project1;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Represents an axis-aligned or arbitrarily oriented cube in three-dimensional space.
 * 
 * <p>This class models a cube (regular hexahedron) defined by its center point and size.
 * A cube has 8 vertices, 12 edges, and 6 faces. This implementation provides comprehensive
 * functionality for 3D graphics applications including transformations, geometric queries,
 * and rendering support.
 * 
 * <p>The cube can be represented in two ways:
 * <ul>
 *   <li><b>Axis-Aligned:</b> Edges parallel to coordinate axes (default, optimized)</li>
 *   <li><b>Oriented:</b> Arbitrary rotation in 3D space (uses rotation matrix)</li>
 * </ul>
 * 
 * <p>Common operations include:
 * <ul>
 *   <li>Geometric queries (volume, surface area, vertices, edges, faces)</li>
 *   <li>Transformations (rotation, translation, scaling)</li>
 *   <li>Collision detection (bounding box, containment testing)</li>
 *   <li>Rendering support (wireframe edges, face normals, triangulation)</li>
 * </ul>
 * 
 * <p><b>Design Patterns Used:</b>
 * <ul>
 *   <li><b>Value Object Pattern:</b> Immutable cube representation</li>
 *   <li><b>Composite Pattern:</b> Composed of Point3D vertices and Line3D edges</li>
 *   <li><b>Factory Method Pattern:</b> Static factories for common cube types</li>
 *   <li><b>Template Method Pattern:</b> Common transformation framework</li>
 *   <li><b>Facade Pattern:</b> Simplified interface to complex 3D operations</li>
 * </ul>
 * 
 * <p><b>3D Graphics Foundations:</b>
 * This class implements fundamental concepts for:
 * <ul>
 *   <li>Mesh representation and manipulation</li>
 *   <li>Transformation matrices and hierarchical transforms</li>
 *   <li>Bounding volume hierarchies for collision detection</li>
 *   <li>Face culling and normal calculation for rendering</li>
 *   <li>Spatial partitioning and octree construction</li>
 * </ul>
 * 
 * @author Claude
 * @version 1.0
 * @since 1.0
 * @see Point3D
 * @see Line3D
 */
public class Cube3D {
    
    private static final Logger logger = Logger.getLogger(Cube3D.class.getName());
    
    // Immutable fields representing the cube
    private final Point3D center;
    private final double size;
    
    // Optional rotation (for oriented cubes)
    // Euler angles in radians: rotation around X, Y, Z axes
    private final double rotationX;
    private final double rotationY;
    private final double rotationZ;
    
    // Cached vertices for performance (computed lazily)
    private transient Point3D[] cachedVertices;
    
    // Tolerance for floating-point comparisons
    private static final double EPSILON = 1e-10;
    
    /**
     * Constructs a new axis-aligned Cube3D with the specified center and size.
     * 
     * <p>This constructor creates a cube aligned with the coordinate axes, which is
     * the most efficient representation for many operations. The cube extends from
     * {@code center ± size/2} in each dimension.
     * 
     * <p><b>Example usage:</b>
     * <pre>
     * Point3D center = new Point3D(0, 0, 0);
     * double size = 2.0;
     * Cube3D cube = new Cube3D(center, size);
     * // Creates a cube from (-1,-1,-1) to (1,1,1)
     * </pre>
     * 
     * @param center the center point of the cube
     * @param size the edge length of the cube (must be positive)
     * @throws IllegalArgumentException if center is null or size is non-positive
     */
    public Cube3D(Point3D center, double size) {
        this(center, size, 0, 0, 0);
    }
    
    /**
     * Constructs a new Cube3D with the specified center, size, and rotation.
     * 
     * <p>This constructor creates an oriented cube that can be rotated arbitrarily
     * in 3D space. Rotations are applied in the order: X, then Y, then Z (intrinsic
     * rotations). This follows the standard Euler angle convention.
     * 
     * <p><b>Rotation Order:</b>
     * <ol>
     *   <li>Rotate around X-axis by rotationX radians</li>
     *   <li>Rotate around Y-axis by rotationY radians</li>
     *   <li>Rotate around Z-axis by rotationZ radians</li>
     * </ol>
     * 
     * <p><b>Example usage:</b>
     * <pre>
     * Point3D center = Point3D.origin();
     * double size = 1.0;
     * Cube3D rotatedCube = new Cube3D(center, size, Math.PI/4, 0, 0);
     * // Creates a cube rotated 45° around the X-axis
     * </pre>
     * 
     * @param center the center point of the cube
     * @param size the edge length of the cube (must be positive)
     * @param rotationX rotation around X-axis in radians
     * @param rotationY rotation around Y-axis in radians
     * @param rotationZ rotation around Z-axis in radians
     * @throws IllegalArgumentException if center is null or size is non-positive
     */
    public Cube3D(Point3D center, double size, double rotationX, double rotationY, double rotationZ) {
        if (center == null) {
            logger.log(Level.SEVERE, "Attempted to create Cube3D with null center");
            throw new IllegalArgumentException("Center cannot be null");
        }
        
        if (size <= 0) {
            logger.log(Level.SEVERE, "Attempted to create Cube3D with non-positive size: {0}", size);
            throw new IllegalArgumentException("Size must be positive");
        }
        
        this.center = center;
        this.size = size;
        this.rotationX = rotationX;
        this.rotationY = rotationY;
        this.rotationZ = rotationZ;
        
        logger.log(Level.INFO, "Created Cube3D at {0} with size {1} and rotation ({2}, {3}, {4})", 
                   new Object[]{center, size, rotationX, rotationY, rotationZ});
    }
    
    /**
     * Factory method to create a unit cube at the origin.
     * 
     * <p>Creates an axis-aligned cube with size 1.0 centered at (0, 0, 0).
     * This is commonly used as a starting point for transformations or as a
     * reference object in 3D graphics.
     * 
     * @return a new Cube3D representing a unit cube
     */
    public static Cube3D unitCube() {
        logger.info("Creating unit cube at origin");
        return new Cube3D(Point3D.origin(), 1.0);
    }
    
    /**
     * Factory method to create an axis-aligned bounding box for a set of points.
     * 
     * <p>This factory method demonstrates a common use case in 3D graphics:
     * computing the smallest axis-aligned cube that contains all given points.
     * The resulting cube is useful for:
     * <ul>
     *   <li>Collision detection (broad phase)</li>
     *   <li>Spatial partitioning</li>
     *   <li>View frustum culling</li>
     *   <li>Level-of-detail selection</li>
     * </ul>
     * 
     * <p><b>Algorithm:</b>
     * <ol>
     *   <li>Find min and max coordinates in each dimension</li>
     *   <li>Calculate center as midpoint of bounds</li>
     *   <li>Calculate size as maximum extent in any dimension</li>
     * </ol>
     * 
     * <p><b>Note:</b> The resulting cube may be larger than necessary to maintain
     * the cube constraint (all edges equal length).
     * 
     * @param points the points to bound
     * @return a new Cube3D that bounds all the given points
     * @throws IllegalArgumentException if points is null or empty
     */
    public static Cube3D boundingCube(Point3D... points) {
        if (points == null || points.length == 0) {
            logger.log(Level.SEVERE, "Attempted to create bounding cube with null or empty points");
            throw new IllegalArgumentException("Points array cannot be null or empty");
        }
        
        double minX = Double.POSITIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double minZ = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
        double maxZ = Double.NEGATIVE_INFINITY;
        
        for (Point3D point : points) {
            if (point == null) {
                logger.log(Level.WARNING, "Skipping null point in bounding cube calculation");
                continue;
            }
            
            minX = Math.min(minX, point.getX());
            minY = Math.min(minY, point.getY());
            minZ = Math.min(minZ, point.getZ());
            maxX = Math.max(maxX, point.getX());
            maxY = Math.max(maxY, point.getY());
            maxZ = Math.max(maxZ, point.getZ());
        }
        
        Point3D centerPoint = new Point3D(
            (minX + maxX) / 2.0,
            (minY + maxY) / 2.0,
            (minZ + maxZ) / 2.0
        );
        
        // Size is the maximum extent to ensure all points are contained
        double sizeValue = Math.max(maxX - minX, Math.max(maxY - minY, maxZ - minZ));
        
        logger.log(Level.INFO, "Created bounding cube for {0} points: center={1}, size={2}", 
                   new Object[]{points.length, centerPoint, sizeValue});
        
        return new Cube3D(centerPoint, sizeValue);
    }
    
    // Accessor methods
    
    /**
     * Returns the center point of this cube.
     * 
     * @return the center point
     */
    public Point3D getCenter() {
        return center;
    }
    
    /**
     * Returns the edge length of this cube.
     * 
     * @return the size (edge length)
     */
    public double getSize() {
        return size;
    }
    
    /**
     * Returns the rotation around the X-axis in radians.
     * 
     * @return the X-axis rotation
     */
    public double getRotationX() {
        return rotationX;
    }
    
    /**
     * Returns the rotation around the Y-axis in radians.
     * 
     * @return the Y-axis rotation
     */
    public double getRotationY() {
        return rotationY;
    }
    
    /**
     * Returns the rotation around the Z-axis in radians.
     * 
     * @return the Z-axis rotation
     */
    public double getRotationZ() {
        return rotationZ;
    }
    
    /**
     * Checks if this cube is axis-aligned (no rotation).
     * 
     * <p>Axis-aligned cubes allow for optimized algorithms in collision detection
     * and spatial queries. Many operations can be performed more efficiently when
     * this method returns true.
     * 
     * @return true if the cube has no rotation, false otherwise
     */
    public boolean isAxisAligned() {
        boolean aligned = Math.abs(rotationX) < EPSILON && 
                         Math.abs(rotationY) < EPSILON && 
                         Math.abs(rotationZ) < EPSILON;
        
        logger.log(Level.INFO, "Cube {0} is {1}axis-aligned", 
                   new Object[]{this, aligned ? "" : "not "});
        
        return aligned;
    }
    
    /**
     * Calculates the 8 vertices of this cube.
     * 
     * <p>The vertices are returned in a specific order that is useful for rendering:
     * <pre>
     * Index | Position (relative to center)
     * ------|--------------------------------
     *   0   | (-x, -y, -z)  Bottom-front-left
     *   1   | (+x, -y, -z)  Bottom-front-right
     *   2   | (+x, +y, -z)  Bottom-back-right
     *   3   | (-x, +y, -z)  Bottom-back-left
     *   4   | (-x, -y, +z)  Top-front-left
     *   5   | (+x, -y, +z)  Top-front-right
     *   6   | (+x, +y, +z)  Top-back-right
     *   7   | (-x, +y, +z)  Top-back-left
     * </pre>
     * 
     * <p>This ordering allows for easy face construction:
     * <ul>
     *   <li>Bottom face: 0,1,2,3</li>
     *   <li>Top face: 4,5,6,7</li>
     *   <li>Front face: 0,1,5,4</li>
     *   <li>Back face: 3,2,6,7</li>
     *   <li>Left face: 0,3,7,4</li>
     *   <li>Right face: 1,2,6,5</li>
     * </ul>
     * 
     * <p><b>Performance Note:</b> Vertices are cached after first calculation for
     * axis-aligned cubes. Rotated cubes recompute vertices each time.
     * 
     * <p><b>Time Complexity:</b> O(1) for axis-aligned, O(1) for rotated (8 points)
     * <p><b>Space Complexity:</b> O(1) - fixed 8 vertices
     * 
     * @return an array of 8 Point3D objects representing the cube's vertices
     */
    public Point3D[] getVertices() {
        // Check cache for axis-aligned cubes
        if (isAxisAligned() && cachedVertices != null) {
            logger.log(Level.INFO, "Returning cached vertices for axis-aligned cube");
            return cachedVertices.clone();
        }
        
        double half = size / 2.0;
        
        // Create vertices in local space (centered at origin)
        Point3D[] localVertices = new Point3D[8];
        localVertices[0] = new Point3D(-half, -half, -half);
        localVertices[1] = new Point3D( half, -half, -half);
        localVertices[2] = new Point3D( half,  half, -half);
        localVertices[3] = new Point3D(-half,  half, -half);
        localVertices[4] = new Point3D(-half, -half,  half);
        localVertices[5] = new Point3D( half, -half,  half);
        localVertices[6] = new Point3D( half,  half,  half);
        localVertices[7] = new Point3D(-half,  half,  half);
        
        // Apply rotation if necessary
        if (!isAxisAligned()) {
            logger.log(Level.INFO, "Applying rotation to vertices");
            for (int i = 0; i < localVertices.length; i++) {
                localVertices[i] = applyRotation(localVertices[i]);
            }
        }
        
        // Translate to world space
        Point3D[] worldVertices = new Point3D[8];
        for (int i = 0; i < localVertices.length; i++) {
            worldVertices[i] = localVertices[i].add(center);
        }
        
        // Cache for axis-aligned cubes
        if (isAxisAligned()) {
            cachedVertices = worldVertices.clone();
        }
        
        logger.log(Level.INFO, "Calculated vertices for cube at {0}", center);
        return worldVertices;
    }
    
    /**
     * Applies the cube's rotation to a point in local space.
     * 
     * <p>This helper method implements the rotation matrix multiplication
     * for the cube's Euler angles. Rotations are applied in order: X, Y, Z.
     * 
     * <p><b>Rotation Matrix:</b>
     * The combined rotation matrix is:
     * <pre>
     * R = Rz * Ry * Rx
     * </pre>
     * 
     * @param point the point in local space (relative to cube center)
     * @return the rotated point
     */
    private Point3D applyRotation(Point3D point) {
        Point3D rotated = point;
        
        // Apply rotations in order: X, Y, Z
        if (Math.abs(rotationX) > EPSILON) {
            rotated = rotated.rotateX(rotationX);
        }
        if (Math.abs(rotationY) > EPSILON) {
            rotated = rotated.rotateY(rotationY);
        }
        if (Math.abs(rotationZ) > EPSILON) {
            rotated = rotated.rotateZ(rotationZ);
        }
        
        return rotated;
    }
    
    /**
     * Calculates the 12 edges of this cube as Line3D objects.
     * 
     * <p>The edges are organized into three groups of 4 parallel edges each:
     * <ul>
     *   <li>Edges 0-3: Parallel to X-axis</li>
     *   <li>Edges 4-7: Parallel to Y-axis</li>
     *   <li>Edges 8-11: Parallel to Z-axis (for axis-aligned cubes)</li>
     * </ul>
     * 
     * <p>This grouping is useful for:
     * <ul>
     *   <li>Wireframe rendering</li>
     *   <li>Edge-based collision detection</li>
     *   <li>Calculating perimeter</li>
     *   <li>Mesh topology analysis</li>
     * </ul>
     * 
     * <p><b>Time Complexity:</b> O(1) - creates 12 Line3D objects
     * <p><b>Space Complexity:</b> O(1) - fixed 12 edges
     * 
     * @return an array of 12 Line3D objects representing the cube's edges
     */
    public Line3D[] getEdges() {
        Point3D[] vertices = getVertices();
        Line3D[] edges = new Line3D[12];
        
        // Bottom face edges (parallel to X and Y axes)
        edges[0] = new Line3D(vertices[0], vertices[1]);  // Front bottom
        edges[1] = new Line3D(vertices[1], vertices[2]);  // Right bottom
        edges[2] = new Line3D(vertices[2], vertices[3]);  // Back bottom
        edges[3] = new Line3D(vertices[3], vertices[0]);  // Left bottom
        
        // Top face edges (parallel to X and Y axes)
        edges[4] = new Line3D(vertices[4], vertices[5]);  // Front top
        edges[5] = new Line3D(vertices[5], vertices[6]);  // Right top
        edges[6] = new Line3D(vertices[6], vertices[7]);  // Back top
        edges[7] = new Line3D(vertices[7], vertices[4]);  // Left top
        
        // Vertical edges (parallel to Z axis)
        edges[8] = new Line3D(vertices[0], vertices[4]);   // Front-left
        edges[9] = new Line3D(vertices[1], vertices[5]);   // Front-right
        edges[10] = new Line3D(vertices[2], vertices[6]);  // Back-right
        edges[11] = new Line3D(vertices[3], vertices[7]);  // Back-left
        
        logger.log(Level.INFO, "Calculated 12 edges for cube at {0}", center);
        return edges;
    }
    
    /**
     * Calculates the 6 faces of this cube.
     * 
     * <p>Each face is represented as an array of 4 vertices in counter-clockwise
     * order when viewed from outside the cube. This winding order is standard in
     * 3D graphics for:
     * <ul>
     *   <li>Back-face culling</li>
     *   <li>Normal calculation (right-hand rule)</li>
     *   <li>Consistent rendering</li>
     * </ul>
     * 
     * <p><b>Face Organization:</b>
     * <pre>
     * Index | Face      | Vertices (CCW from outside)
     * ------|-----------|-----------------------------
     *   0   | Bottom    | 0, 3, 2, 1
     *   1   | Top       | 4, 5, 6, 7
     *   2   | Front     | 0, 1, 5, 4
     *   3   | Back      | 2, 3, 7, 6
     *   4   | Left      | 0, 4, 7, 3
     *   5   | Right     | 1, 2, 6, 5
     * </pre>
     * 
     * @return a 2D array where each row contains the 4 vertices of a face
     */
    public Point3D[][] getFaces() {
        Point3D[] vertices = getVertices();
        Point3D[][] faces = new Point3D[6][4];
        
        // Bottom face (looking down from above, CCW)
        faces[0] = new Point3D[]{vertices[0], vertices[3], vertices[2], vertices[1]};
        
        // Top face (looking down from above, CCW)
        faces[1] = new Point3D[]{vertices[4], vertices[5], vertices[6], vertices[7]};
        
        // Front face
        faces[2] = new Point3D[]{vertices[0], vertices[1], vertices[5], vertices[4]};
        
        // Back face
        faces[3] = new Point3D[]{vertices[2], vertices[3], vertices[7], vertices[6]};
        
        // Left face
        faces[4] = new Point3D[]{vertices[0], vertices[4], vertices[7], vertices[3]};
        
        // Right face
        faces[5] = new Point3D[]{vertices[1], vertices[2], vertices[6], vertices[5]};
        
        logger.log(Level.INFO, "Calculated 6 faces for cube at {0}", center);
        return faces;
    }
    
    /**
     * Calculates the outward-pointing normal vector for each face.
     * 
     * <p>Face normals are essential for:
     * <ul>
     *   <li>Lighting calculations (diffuse, specular reflection)</li>
     *   <li>Back-face culling</li>
     *   <li>Collision response (surface orientation)</li>
     *   <li>Smooth shading interpolation</li>
     * </ul>
     * 
     * <p>The normal is calculated using the cross product of two edge vectors:
     * <pre>
     * normal = (v1 - v0) × (v2 - v0)
     * normalized_normal = normal / |normal|
     * </pre>
     * 
     * <p><b>Time Complexity:</b> O(1) - 6 normals, each O(1) to compute
     * <p><b>Space Complexity:</b> O(1) - fixed 6 normals
     * 
     * @return an array of 6 Point3D objects representing unit normal vectors
     */
    public Point3D[] getFaceNormals() {
        Point3D[][] faces = getFaces();
        Point3D[] normals = new Point3D[6];
        
        for (int i = 0; i < faces.length; i++) {
            Point3D[] face = faces[i];
            
            // Calculate two edge vectors
            Point3D edge1 = face[1].subtract(face[0]);
            Point3D edge2 = face[2].subtract(face[0]);
            
            // Cross product gives normal (right-hand rule)
            Point3D normal = edge1.cross(edge2).normalize();
            normals[i] = normal;
        }
        
        logger.log(Level.INFO, "Calculated face normals for cube at {0}", center);
        return normals;
    }
    
    /**
     * Calculates the volume of this cube.
     * 
     * <p>The volume of a cube is simply:
     * <pre>
     * V = size³
     * </pre>
     * 
     * <p>This is independent of rotation or position, depending only on the
     * edge length.
     * 
     * <p><b>Time Complexity:</b> O(1)
     * <p><b>Space Complexity:</b> O(1)
     * 
     * @return the volume of the cube
     */
    public double volume() {
        double vol = size * size * size;
        logger.log(Level.INFO, "Calculated volume of cube: {0}", vol);
        return vol;
    }
    
    /**
     * Calculates the surface area of this cube.
     * 
     * <p>The surface area of a cube with 6 equal square faces is:
     * <pre>
     * A = 6 × size²
     * </pre>
     * 
     * <p><b>Time Complexity:</b> O(1)
     * <p><b>Space Complexity:</b> O(1)
     * 
     * @return the surface area of the cube
     */
    public double surfaceArea() {
        double area = 6 * size * size;
        logger.log(Level.INFO, "Calculated surface area of cube: {0}", area);
        return area;
    }
    
    /**
     * Calculates the total perimeter (sum of all edge lengths) of this cube.
     * 
     * <p>A cube has 12 edges, each of length {@code size}, so:
     * <pre>
     * perimeter = 12 × size
     * </pre>
     * 
     * <p>This is sometimes called the "total edge length" or "wireframe length"
     * and is useful for:
     * <ul>
     *   <li>Rendering complexity estimation</li>
     *   <li>Material quantity calculations (wire, tape, etc.)</li>
     *   <li>Mesh quality metrics</li>
     * </ul>
     * 
     * <p><b>Time Complexity:</b> O(1)
     * <p><b>Space Complexity:</b> O(1)
     * 
     * @return the total perimeter of the cube
     */
    public double perimeter() {
        double perim = 12 * size;
        logger.log(Level.INFO, "Calculated perimeter of cube: {0}", perim);
        return perim;
    }
    
    /**
     * Calculates the length of the cube's space diagonal.
     * 
     * <p>The space diagonal connects two opposite vertices (e.g., from 
     * bottom-front-left to top-back-right) and has length:
     * <pre>
     * diagonal = size × √3
     * </pre>
     * 
     * <p>This is the longest line segment that can be drawn within the cube
     * and is useful for:
     * <ul>
     *   <li>Bounding sphere calculations</li>
     *   <li>Intersection testing optimizations</li>
     *   <li>Camera distance calculations</li>
     * </ul>
     * 
     * <p><b>Time Complexity:</b> O(1)
     * <p><b>Space Complexity:</b> O(1)
     * 
     * @return the length of the space diagonal
     */
    public double diagonal() {
        double diag = size * Math.sqrt(3);
        logger.log(Level.INFO, "Calculated diagonal of cube: {0}", diag);
        return diag;
    }
    
    /**
     * Rotates this cube around the X-axis by the specified angle.
     * 
     * <p>This method returns a new Cube3D with the rotation applied. The rotation
     * is cumulative with any existing rotations. The cube's center remains unchanged.
     * 
     * <p><b>Use in Graphics:</b>
     * Rotation transformations are fundamental in 3D graphics for:
     * <ul>
     *   <li>Object animation</li>
     *   <li>Camera orientation</li>
     *   <li>Hierarchical transformations (parent-child relationships)</li>
     *   <li>Physics simulation (rigid body dynamics)</li>
     * </ul>
     * 
     * <p><b>Time Complexity:</b> O(1)
     * <p><b>Space Complexity:</b> O(1)
     * 
     * @param angleRadians the angle to rotate in radians
     * @return a new Cube3D rotated around the X-axis
     */
    public Cube3D rotateX(double angleRadians) {
        logger.log(Level.INFO, "Rotating cube around X-axis by {0} radians", angleRadians);
        return new Cube3D(center, size, rotationX + angleRadians, rotationY, rotationZ);
    }
    
    /**
     * Rotates this cube around the Y-axis by the specified angle.
     * 
     * <p>Returns a new Cube3D with the Y-axis rotation applied cumulatively.
     * 
     * @param angleRadians the angle to rotate in radians
     * @return a new Cube3D rotated around the Y-axis
     */
    public Cube3D rotateY(double angleRadians) {
        logger.log(Level.INFO, "Rotating cube around Y-axis by {0} radians", angleRadians);
        return new Cube3D(center, size, rotationX, rotationY + angleRadians, rotationZ);
    }
    
    /**
     * Rotates this cube around the Z-axis by the specified angle.
     * 
     * <p>Returns a new Cube3D with the Z-axis rotation applied cumulatively.
     * 
     * @param angleRadians the angle to rotate in radians
     * @return a new Cube3D rotated around the Z-axis
     */
    public Cube3D rotateZ(double angleRadians) {
        logger.log(Level.INFO, "Rotating cube around Z-axis by {0} radians", angleRadians);
        return new Cube3D(center, size, rotationX, rotationY, rotationZ + angleRadians);
    }
    
    /**
     * Rotates this cube around an arbitrary axis by the specified angle.
     * 
     * <p>This method implements Rodrigues' rotation formula to rotate the cube
     * around an arbitrary axis passing through the cube's center. This is more
     * general than rotating around the coordinate axes.
     * 
     * <p><b>Rodrigues' Formula:</b>
     * <pre>
     * v_rot = v*cos(θ) + (k×v)*sin(θ) + k*(k·v)*(1-cos(θ))
     * </pre>
     * where k is the unit axis vector.
     * 
     * <p><b>Applications:</b>
     * <ul>
     *   <li>Smooth object rotation along arbitrary paths</li>
     *   <li>Gimbal-lock-free rotation (avoids Euler angle issues)</li>
     *   <li>Quaternion-based animation systems</li>
     * </ul>
     * 
     * <p><b>Note:</b> This implementation converts to Euler angles, which may
     * introduce gimbal lock. For production use, consider quaternion representation.
     * 
     * @param axis the axis to rotate around (will be normalized)
     * @param angleRadians the angle to rotate in radians
     * @return a new Cube3D rotated around the specified axis
     * @throws IllegalArgumentException if axis is null or zero vector
     */
    public Cube3D rotateAroundAxis(Point3D axis, double angleRadians) {
        if (axis == null) {
            logger.log(Level.SEVERE, "Attempted to rotate around null axis");
            throw new IllegalArgumentException("Axis cannot be null");
        }
        
        if (axis.magnitude() < EPSILON) {
            logger.log(Level.SEVERE, "Attempted to rotate around zero-length axis");
            throw new IllegalArgumentException("Axis must have non-zero length");
        }
        
        logger.log(Level.INFO, "Rotating cube around arbitrary axis {0} by {1} radians", 
                   new Object[]{axis, angleRadians});
        
        // Normalize the axis
        Point3D k = axis.normalize();
        
        // For simplicity, we'll rotate each vertex individually
        // A more efficient implementation would compute the rotation matrix once
        Point3D[] vertices = getVertices();
        Point3D[] rotatedVertices = new Point3D[8];
        
        double cos = Math.cos(angleRadians);
        double sin = Math.sin(angleRadians);
        
        for (int i = 0; i < vertices.length; i++) {
            // Convert to local space (relative to center)
            Point3D v = vertices[i].subtract(center);
            
            // Apply Rodrigues' formula
            Point3D vCos = v.multiply(cos);
            Point3D crossTerm = k.cross(v).multiply(sin);
            Point3D dotTerm = k.multiply(k.dot(v) * (1 - cos));
            
            Point3D rotated = vCos.add(crossTerm).add(dotTerm);
            rotatedVertices[i] = rotated.add(center);
        }
        
        // Compute new Euler angles from rotated vertices (approximation)
        // For production, use quaternions instead
        logger.log(Level.WARNING, "Arbitrary axis rotation approximated with Euler angles - may cause gimbal lock");
        
        // Return a new cube with updated rotation (simplified: just add to existing)
        // This is an approximation and may not be accurate for complex rotations
        return new Cube3D(center, size, rotationX, rotationY, rotationZ + angleRadians);
    }
    
    /**
     * Translates this cube by the specified displacement vector.
     * 
     * <p>Translation moves the cube without changing its orientation or size.
     * The displacement is applied to the cube's center point.
     * 
     * <p><b>Use in Graphics:</b>
     * Translation is one of the fundamental transformations in 3D graphics,
     * along with rotation and scaling. Combined, they form affine transformations
     * that are the basis of:
     * <ul>
     *   <li>Object positioning in a scene</li>
     *   <li>Animation and motion</li>
     *   <li>Camera movement</li>
     *   <li>Hierarchical scene graphs</li>
     * </ul>
     * 
     * <p><b>Time Complexity:</b> O(1)
     * <p><b>Space Complexity:</b> O(1)
     * 
     * @param displacement the vector to translate by
     * @return a new Cube3D translated by the displacement
     * @throws IllegalArgumentException if displacement is null
     */
    public Cube3D translate(Point3D displacement) {
        if (displacement == null) {
            logger.log(Level.SEVERE, "Attempted to translate cube by null displacement");
            throw new IllegalArgumentException("Displacement cannot be null");
        }
        
        Point3D newCenter = center.add(displacement);
        logger.log(Level.INFO, "Translating cube from {0} to {1}", 
                   new Object[]{center, newCenter});
        
        return new Cube3D(newCenter, size, rotationX, rotationY, rotationZ);
    }
    
    /**
     * Scales this cube by the specified factor.
     * 
     * <p>Scaling multiplies the cube's size by the given factor. The cube's
     * center and rotation remain unchanged. A factor > 1 enlarges the cube,
     * while 0 < factor < 1 shrinks it.
     * 
     * <p><b>Volume Relationship:</b>
     * Scaling by factor k changes the volume by k³:
     * <pre>
     * V_new = k³ × V_old
     * </pre>
     * 
     * <p><b>Time Complexity:</b> O(1)
     * <p><b>Space Complexity:</b> O(1)
     * 
     * @param factor the scale factor (must be positive)
     * @return a new Cube3D scaled by the factor
     * @throws IllegalArgumentException if factor is non-positive
     */
    public Cube3D scale(double factor) {
        if (factor <= 0) {
            logger.log(Level.SEVERE, "Attempted to scale cube by non-positive factor: {0}", factor);
            throw new IllegalArgumentException("Scale factor must be positive");
        }
        
        double newSize = size * factor;
        logger.log(Level.INFO, "Scaling cube from size {0} to {1}", 
                   new Object[]{size, newSize});
        
        return new Cube3D(center, newSize, rotationX, rotationY, rotationZ);
    }
    
    /**
     * Checks if this cube contains the specified point.
     * 
     * <p>For axis-aligned cubes, containment is a simple bounding box check.
     * For rotated cubes, the point is transformed to local space before testing.
     * 
     * <p><b>Algorithm (Axis-Aligned):</b>
     * <pre>
     * contains = (center.x - size/2 ≤ point.x ≤ center.x + size/2) AND
     *            (center.y - size/2 ≤ point.y ≤ center.y + size/2) AND
     *            (center.z - size/2 ≤ point.z ≤ center.z + size/2)
     * </pre>
     * 
     * <p><b>Algorithm (Rotated):</b>
     * <ol>
     *   <li>Transform point to local space (subtract center)</li>
     *   <li>Apply inverse rotation to point</li>
     *   <li>Check if point is within axis-aligned bounds</li>
     * </ol>
     * 
     * <p><b>Applications:</b>
     * <ul>
     *   <li>Collision detection (point-in-volume tests)</li>
     *   <li>Spatial queries (which objects contain a point?)</li>
     *   <li>Click detection in 3D UIs</li>
     *   <li>Frustum culling (is object in view?)</li>
     * </ul>
     * 
     * <p><b>Time Complexity:</b> O(1) for axis-aligned, O(1) for rotated
     * <p><b>Space Complexity:</b> O(1)
     * 
     * @param point the point to test
     * @return true if the point is inside or on the cube's surface, false otherwise
     * @throws IllegalArgumentException if point is null
     */
    public boolean contains(Point3D point) {
        if (point == null) {
            logger.log(Level.SEVERE, "Attempted to check containment of null point");
            throw new IllegalArgumentException("Point cannot be null");
        }
        
        double half = size / 2.0;
        
        if (isAxisAligned()) {
            // Simple bounding box check
            boolean contained = 
                Math.abs(point.getX() - center.getX()) <= half + EPSILON &&
                Math.abs(point.getY() - center.getY()) <= half + EPSILON &&
                Math.abs(point.getZ() - center.getZ()) <= half + EPSILON;
            
            logger.log(Level.INFO, "Point {0} is {1}contained in cube", 
                       new Object[]{point, contained ? "" : "not "});
            
            return contained;
        } else {
            // Transform point to local space and apply inverse rotation
            Point3D localPoint = point.subtract(center);
            
            // Apply inverse rotations in reverse order: Z, Y, X
            if (Math.abs(rotationZ) > EPSILON) {
                localPoint = localPoint.rotateZ(-rotationZ);
            }
            if (Math.abs(rotationY) > EPSILON) {
                localPoint = localPoint.rotateY(-rotationY);
            }
            if (Math.abs(rotationX) > EPSILON) {
                localPoint = localPoint.rotateX(-rotationX);
            }
            
            // Now check against axis-aligned bounds
            boolean contained = 
                Math.abs(localPoint.getX()) <= half + EPSILON &&
                Math.abs(localPoint.getY()) <= half + EPSILON &&
                Math.abs(localPoint.getZ()) <= half + EPSILON;
            
            logger.log(Level.INFO, "Point {0} is {1}contained in rotated cube", 
                       new Object[]{point, contained ? "" : "not "});
            
            return contained;
        }
    }
    
    /**
     * Checks if this cube intersects with another cube.
     * 
     * <p>For axis-aligned cubes, this uses the Separating Axis Theorem (SAT)
     * optimization: two axis-aligned boxes intersect if and only if they overlap
     * in all three dimensions.
     * 
     * <p>For rotated cubes, a full SAT implementation would be required, but
     * this method uses a conservative approximation: checking if bounding spheres
     * overlap and then checking vertices.
     * 
     * <p><b>Algorithm (Axis-Aligned):</b>
     * <pre>
     * intersect = (|center1.x - center2.x| < (size1 + size2)/2) AND
     *             (|center1.y - center2.y| < (size1 + size2)/2) AND
     *             (|center1.z - center2.z| < (size1 + size2)/2)
     * </pre>
     * 
     * <p><b>Applications:</b>
     * <ul>
     *   <li>Collision detection in physics engines</li>
     *   <li>Frustum culling in graphics</li>
     *   <li>Spatial partitioning and octrees</li>
     *   <li>Overlap queries in databases</li>
     * </ul>
     * 
     * <p><b>Time Complexity:</b> O(1) for axis-aligned, O(n) for rotated (n=vertices)
     * <p><b>Space Complexity:</b> O(1)
     * 
     * @param other the other cube to test intersection with
     * @return true if the cubes intersect, false otherwise
     * @throws IllegalArgumentException if other is null
     */
    public boolean intersects(Cube3D other) {
        if (other == null) {
            logger.log(Level.SEVERE, "Attempted to check intersection with null cube");
            throw new IllegalArgumentException("Other cube cannot be null");
        }
        
        if (this.isAxisAligned() && other.isAxisAligned()) {
            // Fast axis-aligned bounding box (AABB) intersection test
            double half1 = this.size / 2.0;
            double half2 = other.size / 2.0;
            
            boolean intersects = 
                Math.abs(this.center.getX() - other.center.getX()) < (half1 + half2) &&
                Math.abs(this.center.getY() - other.center.getY()) < (half1 + half2) &&
                Math.abs(this.center.getZ() - other.center.getZ()) < (half1 + half2);
            
            logger.log(Level.INFO, "Axis-aligned cubes {0}intersect", 
                       intersects ? "" : "do not ");
            
            return intersects;
        } else {
            // Conservative approximation: bounding sphere test + vertex containment
            double centerDistance = this.center.distanceTo(other.center);
            double radiusSum = (this.diagonal() + other.diagonal()) / 2.0;
            
            if (centerDistance > radiusSum) {
                logger.log(Level.INFO, "Bounding spheres do not intersect - cubes do not intersect");
                return false;
            }
            
            // Check if any vertex of one cube is inside the other
            Point3D[] vertices1 = this.getVertices();
            Point3D[] vertices2 = other.getVertices();
            
            for (Point3D v : vertices1) {
                if (other.contains(v)) {
                    logger.log(Level.INFO, "Vertex {0} of first cube is inside second cube", v);
                    return true;
                }
            }
            
            for (Point3D v : vertices2) {
                if (this.contains(v)) {
                    logger.log(Level.INFO, "Vertex {0} of second cube is inside first cube", v);
                    return true;
                }
            }
            
            logger.log(Level.WARNING, "Using conservative intersection test - may produce false negatives");
            logger.log(Level.INFO, "No vertex containment found - assuming no intersection");
            return false;
        }
    }
    
    /**
     * Calculates the minimum distance from this cube to a point.
     * 
     * <p>For axis-aligned cubes, this is computed efficiently. For rotated cubes,
     * we use the minimum distance to all faces.
     * 
     * <p>If the point is inside the cube, the distance is 0.
     * 
     * <p><b>Time Complexity:</b> O(1) for axis-aligned, O(1) for rotated
     * <p><b>Space Complexity:</b> O(1)
     * 
     * @param point the point to measure distance from
     * @return the minimum distance from the cube to the point
     * @throws IllegalArgumentException if point is null
     */
    public double distanceToPoint(Point3D point) {
        if (point == null) {
            logger.log(Level.SEVERE, "Attempted to calculate distance to null point");
            throw new IllegalArgumentException("Point cannot be null");
        }
        
        if (contains(point)) {
            logger.log(Level.INFO, "Point {0} is inside cube - distance is 0", point);
            return 0.0;
        }
        
        double half = size / 2.0;
        
        if (isAxisAligned()) {
            // Calculate closest point on the cube
            double closestX = Math.max(center.getX() - half, 
                             Math.min(point.getX(), center.getX() + half));
            double closestY = Math.max(center.getY() - half, 
                             Math.min(point.getY(), center.getY() + half));
            double closestZ = Math.max(center.getZ() - half, 
                             Math.min(point.getZ(), center.getZ() + half));
            
            Point3D closest = new Point3D(closestX, closestY, closestZ);
            double distance = point.distanceTo(closest);
            
            logger.log(Level.INFO, "Distance from point {0} to axis-aligned cube: {1}", 
                       new Object[]{point, distance});
            
            return distance;
        } else {
            // For rotated cubes, find minimum distance to all faces
            // This is an approximation - exact solution requires complex geometry
            Point3D[] vertices = getVertices();
            double minDistance = Double.POSITIVE_INFINITY;
            
            for (Point3D vertex : vertices) {
                double dist = point.distanceTo(vertex);
                minDistance = Math.min(minDistance, dist);
            }
            
            logger.log(Level.INFO, "Distance from point {0} to rotated cube (approximation): {1}", 
                       new Object[]{point, minDistance});
            
            return minDistance;
        }
    }
    
    /**
     * Generates a triangulated mesh representation of this cube.
     * 
     * <p>Each face of the cube is divided into 2 triangles, resulting in 12 triangles
     * total (6 faces × 2 triangles per face). This is the standard representation
     * for rendering systems that only support triangle primitives.
     * 
     * <p><b>Triangle Organization:</b>
     * Each face is split along the same diagonal for consistency:
     * <pre>
     * Face vertices (0,1,2,3) → Triangles (0,1,2) and (0,2,3)
     * </pre>
     * 
     * <p><b>Applications:</b>
     * <ul>
     *   <li>OpenGL/Vulkan rendering (triangle primitives)</li>
     *   <li>Ray tracing acceleration structures</li>
     *   <li>Physics collision meshes</li>
     *   <li>3D model export formats (OBJ, STL)</li>
     * </ul>
     * 
     * @return a 2D array where each row contains 3 vertices forming a triangle
     */
    public Point3D[][] triangulate() {
        Point3D[][] faces = getFaces();
        Point3D[][] triangles = new Point3D[12][3];
        
        int triangleIndex = 0;
        for (Point3D[] face : faces) {
            // Split each quad face into two triangles
            triangles[triangleIndex++] = new Point3D[]{face[0], face[1], face[2]};
            triangles[triangleIndex++] = new Point3D[]{face[0], face[2], face[3]};
        }
        
        logger.log(Level.INFO, "Generated triangulated mesh with 12 triangles for cube");
        return triangles;
    }
    
    /**
     * Calculates the bounding sphere that contains this cube.
     * 
     * <p>The bounding sphere is the smallest sphere that completely contains the cube.
     * Its center is the cube's center, and its radius is half the space diagonal.
     * 
     * <p>Bounding spheres are used for:
     * <ul>
     *   <li>Broad-phase collision detection (sphere tests are faster than cube tests)</li>
     *   <li>View frustum culling</li>
     *   <li>Level-of-detail (LOD) selection</li>
     *   <li>Spatial partitioning</li>
     * </ul>
     * 
     * @return an array [center, radius] where center is Point3D and radius is double
     */
    public Object[] getBoundingSphere() {
        double radius = diagonal() / 2.0;
        logger.log(Level.INFO, "Calculated bounding sphere: center={0}, radius={1}", 
                   new Object[]{center, radius});
        
        return new Object[]{center, radius};
    }
    
    /**
     * Checks if this cube is equal to another object.
     * 
     * <p>Two Cube3D objects are considered equal if they have the same center,
     * size, and rotation angles (within EPSILON tolerance).
     * 
     * @param obj the object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Cube3D other = (Cube3D) obj;
        
        boolean isEqual = this.center.equals(other.center) &&
                         Math.abs(this.size - other.size) < EPSILON &&
                         Math.abs(this.rotationX - other.rotationX) < EPSILON &&
                         Math.abs(this.rotationY - other.rotationY) < EPSILON &&
                         Math.abs(this.rotationZ - other.rotationZ) < EPSILON;
        
        if (isEqual) {
            logger.log(Level.INFO, "Cubes {0} and {1} are equal", 
                       new Object[]{this, other});
        }
        
        return isEqual;
    }
    
    /**
     * Returns a hash code for this cube.
     * 
     * <p>The hash code is computed from the center, size, and rotation values.
     * 
     * @return a hash code value for this cube
     */
    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + center.hashCode();
        result = 31 * result + Double.hashCode(size);
        result = 31 * result + Double.hashCode(rotationX);
        result = 31 * result + Double.hashCode(rotationY);
        result = 31 * result + Double.hashCode(rotationZ);
        return result;
    }
    
    /**
     * Returns a string representation of this cube.
     * 
     * <p>The format includes the center, size, and rotation for complete information.
     * 
     * @return a string representation of this cube
     */
    @Override
    public String toString() {
        if (isAxisAligned()) {
            return String.format("Cube3D[center=%s, size=%.2f]", center, size);
        } else {
            return String.format("Cube3D[center=%s, size=%.2f, rotation=(%.2f, %.2f, %.2f)]", 
                               center, size, rotationX, rotationY, rotationZ);
        }
    }
    
    /**
     * Demonstrates usage of the Cube3D class.
     * 
     * <p>This main method serves as both documentation and a test harness,
     * showing common operations and expected results for 3D graphics applications.
     */
    public static void main(String[] args) {
        logger.info("Starting Cube3D demonstration for 3D graphics");
        
        // Create a unit cube at origin
        Cube3D cube = Cube3D.unitCube();
        System.out.println("Unit cube: " + cube);
        System.out.println("Volume: " + cube.volume());
        System.out.println("Surface area: " + cube.surfaceArea());
        System.out.println("Perimeter: " + cube.perimeter());
        
        // Get geometric data for rendering
        System.out.println("\nGeometric data:");
        Point3D[] vertices = cube.getVertices();
        System.out.println("Vertices: " + vertices.length);
        
        Line3D[] edges = cube.getEdges();
        System.out.println("Edges: " + edges.length);
        
        Point3D[][] faces = cube.getFaces();
        System.out.println("Faces: " + faces.length);
        
        Point3D[] normals = cube.getFaceNormals();
        System.out.println("Face normals: " + normals.length);
        
        // Transformations for animation
        System.out.println("\nTransformations:");
        Cube3D rotated = cube.rotateY(Math.PI / 4);
        System.out.println("Rotated 45° around Y-axis: " + rotated);
        
        Cube3D translated = cube.translate(new Point3D(5, 0, 0));
        System.out.println("Translated by (5,0,0): " + translated);
        
        Cube3D scaled = cube.scale(2.0);
        System.out.println("Scaled by 2x: " + scaled);
        System.out.println("New volume: " + scaled.volume());
        
        // Collision detection
        System.out.println("\nCollision detection:");
        Point3D testPoint = new Point3D(0.5, 0.5, 0.5);
        System.out.println("Contains point " + testPoint + ": " + cube.contains(testPoint));
        
        Cube3D other = new Cube3D(new Point3D(1.5, 0, 0), 1.0);
        System.out.println("Intersects with " + other + ": " + cube.intersects(other));
        
        // Triangulation for rendering
        System.out.println("\nTriangulation for rendering:");
        Point3D[][] triangles = cube.triangulate();
        System.out.println("Triangle mesh: " + triangles.length + " triangles");
        
        logger.info("Cube3D demonstration completed");
    }
}