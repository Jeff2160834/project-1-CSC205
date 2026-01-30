package com.csc205.project1;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive unit test suite for the Point3D class.
 * 
 * <p>This test suite covers:
 * <ul>
 *   <li>Normal cases: Standard operations with typical values</li>
 *   <li>Edge cases: Boundary values, zero vectors, unit vectors</li>
 *   <li>Corner cases: Extreme values, numerical precision, special angles</li>
 *   <li>Error cases: Null inputs, invalid operations, degenerate cases</li>
 * </ul>
 * 
 * <p>Test Organization:
 * <ul>
 *   <li>Construction and Factory Methods</li>
 *   <li>Distance Calculations (Euclidean, Manhattan, Chebyshev)</li>
 *   <li>Rotation Operations (X, Y, Z axes)</li>
 *   <li>Vector Operations (add, subtract, multiply, dot, cross)</li>
 *   <li>Magnitude and Normalization</li>
 *   <li>Interpolation and Midpoint</li>
 *   <li>Equality and Hash Code</li>
 * </ul>
 * 
 * @author Claude
 * @version 1.0
 */
@DisplayName("Point3D Test Suite")
public class Point3DTest {
    
    private static final double EPSILON = 1e-10;
    private static final double DELTA = 1e-9;  // For assertEquals with doubles
    
    // Test fixtures - commonly used points
    private Point3D origin;
    private Point3D unitX;
    private Point3D unitY;
    private Point3D unitZ;
    private Point3D point345;  // (3, 4, 5) - useful for distance tests
    
    @BeforeEach
    void setUp() {
        origin = new Point3D(0, 0, 0);
        unitX = new Point3D(1, 0, 0);
        unitY = new Point3D(0, 1, 0);
        unitZ = new Point3D(0, 0, 1);
        point345 = new Point3D(3, 4, 5);
    }
    
    // ============================================================================
    // Construction and Factory Methods
    // ============================================================================
    
    @Nested
    @DisplayName("Construction Tests")
    class ConstructionTests {
        
        @Test
        @DisplayName("Constructor should create point with correct coordinates")
        void testBasicConstruction() {
            Point3D point = new Point3D(1.5, 2.5, 3.5);
            
            assertEquals(1.5, point.getX(), DELTA);
            assertEquals(2.5, point.getY(), DELTA);
            assertEquals(3.5, point.getZ(), DELTA);
        }
        
        @Test
        @DisplayName("Constructor should handle zero coordinates")
        void testZeroConstruction() {
            Point3D point = new Point3D(0, 0, 0);
            
            assertEquals(0.0, point.getX(), DELTA);
            assertEquals(0.0, point.getY(), DELTA);
            assertEquals(0.0, point.getZ(), DELTA);
        }
        
        @Test
        @DisplayName("Constructor should handle negative coordinates")
        void testNegativeConstruction() {
            Point3D point = new Point3D(-1, -2, -3);
            
            assertEquals(-1.0, point.getX(), DELTA);
            assertEquals(-2.0, point.getY(), DELTA);
            assertEquals(-3.0, point.getZ(), DELTA);
        }
        
        @Test
        @DisplayName("Constructor should handle very large values")
        void testLargeValues() {
            double large = 1e100;
            Point3D point = new Point3D(large, large, large);
            
            assertEquals(large, point.getX(), DELTA);
            assertEquals(large, point.getY(), DELTA);
            assertEquals(large, point.getZ(), DELTA);
        }
        
        @Test
        @DisplayName("Constructor should handle very small values")
        void testSmallValues() {
            double small = 1e-100;
            Point3D point = new Point3D(small, small, small);
            
            assertEquals(small, point.getX(), DELTA);
            assertEquals(small, point.getY(), DELTA);
            assertEquals(small, point.getZ(), DELTA);
        }
        
        @Test
        @DisplayName("origin() factory should create point at (0,0,0)")
        void testOriginFactory() {
            Point3D origin = Point3D.origin();
            
            assertEquals(0.0, origin.getX(), DELTA);
            assertEquals(0.0, origin.getY(), DELTA);
            assertEquals(0.0, origin.getZ(), DELTA);
        }
        
        @Test
        @DisplayName("unitX() factory should create point at (1,0,0)")
        void testUnitXFactory() {
            Point3D unitX = Point3D.unitX();
            
            assertEquals(1.0, unitX.getX(), DELTA);
            assertEquals(0.0, unitX.getY(), DELTA);
            assertEquals(0.0, unitX.getZ(), DELTA);
        }
        
        @Test
        @DisplayName("unitY() factory should create point at (0,1,0)")
        void testUnitYFactory() {
            Point3D unitY = Point3D.unitY();
            
            assertEquals(0.0, unitY.getX(), DELTA);
            assertEquals(1.0, unitY.getY(), DELTA);
            assertEquals(0.0, unitY.getZ(), DELTA);
        }
        
        @Test
        @DisplayName("unitZ() factory should create point at (0,0,1)")
        void testUnitZFactory() {
            Point3D unitZ = Point3D.unitZ();
            
            assertEquals(0.0, unitZ.getX(), DELTA);
            assertEquals(0.0, unitZ.getY(), DELTA);
            assertEquals(1.0, unitZ.getZ(), DELTA);
        }
    }
    
    // ============================================================================
    // Distance Calculations
    // ============================================================================
    
    @Nested
    @DisplayName("Euclidean Distance Tests")
    class EuclideanDistanceTests {
        
        @Test
        @DisplayName("Distance from origin to (3,4,0) should be 5")
        void testPythagoreanTriple() {
            Point3D point = new Point3D(3, 4, 0);
            double distance = origin.distanceTo(point);
            
            assertEquals(5.0, distance, DELTA);
        }
        
        @Test
        @DisplayName("Distance from origin to (3,4,5) should be 5√2")
        void test3D_Distance() {
            double expected = Math.sqrt(50);  // sqrt(9 + 16 + 25)
            double distance = origin.distanceTo(point345);
            
            assertEquals(expected, distance, DELTA);
        }
        
        @Test
        @DisplayName("Distance from point to itself should be 0")
        void testDistanceToSelf() {
            double distance = point345.distanceTo(point345);
            assertEquals(0.0, distance, DELTA);
        }
        
        @Test
        @DisplayName("Distance should be symmetric")
        void testDistanceSymmetry() {
            Point3D p1 = new Point3D(1, 2, 3);
            Point3D p2 = new Point3D(4, 5, 6);
            
            assertEquals(p1.distanceTo(p2), p2.distanceTo(p1), DELTA);
        }
        
        @Test
        @DisplayName("Distance with negative coordinates")
        void testNegativeDistance() {
            Point3D p1 = new Point3D(-1, -2, -3);
            Point3D p2 = new Point3D(2, 3, 4);
            
            // sqrt((2-(-1))^2 + (3-(-2))^2 + (4-(-3))^2) = sqrt(9+25+49) = sqrt(83)
            double expected = Math.sqrt(83);
            assertEquals(expected, p1.distanceTo(p2), DELTA);
        }
        
        @Test
        @DisplayName("distanceTo should throw exception for null point")
        void testDistanceToNull() {
            assertThrows(IllegalArgumentException.class, () -> {
                origin.distanceTo(null);
            });
        }
        
        @Test
        @DisplayName("Distance between very close points should be accurate")
        void testVerySmallDistance() {
            Point3D p1 = new Point3D(0, 0, 0);
            Point3D p2 = new Point3D(1e-10, 1e-10, 1e-10);
            
            double distance = p1.distanceTo(p2);
            assertTrue(distance > 0 && distance < 1e-9);
        }
        
        @Test
        @DisplayName("Distance between very far points should not overflow")
        void testLargeDistance() {
            Point3D p1 = new Point3D(0, 0, 0);
            Point3D p2 = new Point3D(1e50, 1e50, 1e50);
            
            double distance = p1.distanceTo(p2);
            assertTrue(Double.isFinite(distance));
            assertTrue(distance > 0);
        }
    }
    
    @Nested
    @DisplayName("Manhattan Distance Tests")
    class ManhattanDistanceTests {
        
        @Test
        @DisplayName("Manhattan distance should be sum of absolute differences")
        void testBasicManhattanDistance() {
            Point3D p1 = new Point3D(1, 2, 3);
            Point3D p2 = new Point3D(4, 6, 8);
            
            // |4-1| + |6-2| + |8-3| = 3 + 4 + 5 = 12
            assertEquals(12.0, p1.manhattanDistanceTo(p2), DELTA);
        }
        
        @Test
        @DisplayName("Manhattan distance from origin to (1,1,1) should be 3")
        void testManhattanFromOrigin() {
            Point3D point = new Point3D(1, 1, 1);
            assertEquals(3.0, origin.manhattanDistanceTo(point), DELTA);
        }
        
        @Test
        @DisplayName("Manhattan distance should handle negative coordinates")
        void testNegativeManhattanDistance() {
            Point3D p1 = new Point3D(-2, -3, -4);
            Point3D p2 = new Point3D(1, 2, 3);
            
            // |-2-1| + |-3-2| + |-4-3| = 3 + 5 + 7 = 15
            assertEquals(15.0, p1.manhattanDistanceTo(p2), DELTA);
        }
        
        @Test
        @DisplayName("Manhattan distance should be symmetric")
        void testManhattanSymmetry() {
            Point3D p1 = new Point3D(1, 2, 3);
            Point3D p2 = new Point3D(4, 5, 6);
            
            assertEquals(p1.manhattanDistanceTo(p2), p2.manhattanDistanceTo(p1), DELTA);
        }
        
        @Test
        @DisplayName("Manhattan distance to self should be 0")
        void testManhattanToSelf() {
            assertEquals(0.0, point345.manhattanDistanceTo(point345), DELTA);
        }
        
        @Test
        @DisplayName("manhattanDistanceTo should throw exception for null point")
        void testManhattanDistanceToNull() {
            assertThrows(IllegalArgumentException.class, () -> {
                origin.manhattanDistanceTo(null);
            });
        }
        
        @Test
        @DisplayName("Manhattan distance should be >= Euclidean distance")
        void testManhattanVsEuclidean() {
            Point3D p1 = new Point3D(1, 2, 3);
            Point3D p2 = new Point3D(4, 5, 6);
            
            double manhattan = p1.manhattanDistanceTo(p2);
            double euclidean = p1.distanceTo(p2);
            
            assertTrue(manhattan >= euclidean - DELTA);
        }
    }
    
    @Nested
    @DisplayName("Chebyshev Distance Tests")
    class ChebyshevDistanceTests {
        
        @Test
        @DisplayName("Chebyshev distance should be maximum absolute difference")
        void testBasicChebyshevDistance() {
            Point3D p1 = new Point3D(1, 2, 3);
            Point3D p2 = new Point3D(4, 5, 10);
            
            // max(|4-1|, |5-2|, |10-3|) = max(3, 3, 7) = 7
            assertEquals(7.0, p1.chebyshevDistanceTo(p2), DELTA);
        }
        
        @Test
        @DisplayName("Chebyshev distance from origin to (1,1,1) should be 1")
        void testChebyshevFromOrigin() {
            Point3D point = new Point3D(1, 1, 1);
            assertEquals(1.0, origin.chebyshevDistanceTo(point), DELTA);
        }
        
        @Test
        @DisplayName("Chebyshev distance should handle negative coordinates")
        void testNegativeChebyshevDistance() {
            Point3D p1 = new Point3D(-5, -2, -1);
            Point3D p2 = new Point3D(3, 4, 2);
            
            // max(|3-(-5)|, |4-(-2)|, |2-(-1)|) = max(8, 6, 3) = 8
            assertEquals(8.0, p1.chebyshevDistanceTo(p2), DELTA);
        }
        
        @Test
        @DisplayName("Chebyshev distance should be symmetric")
        void testChebyshevSymmetry() {
            Point3D p1 = new Point3D(1, 2, 3);
            Point3D p2 = new Point3D(4, 5, 6);
            
            assertEquals(p1.chebyshevDistanceTo(p2), p2.chebyshevDistanceTo(p1), DELTA);
        }
        
        @Test
        @DisplayName("Chebyshev distance to self should be 0")
        void testChebyshevToSelf() {
            assertEquals(0.0, point345.chebyshevDistanceTo(point345), DELTA);
        }
        
        @Test
        @DisplayName("chebyshevDistanceTo should throw exception for null point")
        void testChebyshevDistanceToNull() {
            assertThrows(IllegalArgumentException.class, () -> {
                origin.chebyshevDistanceTo(null);
            });
        }
        
        @Test
        @DisplayName("Chebyshev distance should be <= Manhattan distance")
        void testChebyshevVsManhattan() {
            Point3D p1 = new Point3D(1, 2, 3);
            Point3D p2 = new Point3D(4, 5, 6);
            
            double chebyshev = p1.chebyshevDistanceTo(p2);
            double manhattan = p1.manhattanDistanceTo(p2);
            
            assertTrue(chebyshev <= manhattan + DELTA);
        }
    }
    
    // ============================================================================
    // Rotation Operations
    // ============================================================================
    
    @Nested
    @DisplayName("Rotation Tests")
    class RotationTests {
        
        @Test
        @DisplayName("Rotate (1,0,0) 90° around Z-axis should give (0,1,0)")
        void testRotateZ_90Degrees() {
            Point3D rotated = unitX.rotateZ(Math.PI / 2);
            
            assertEquals(0.0, rotated.getX(), DELTA);
            assertEquals(1.0, rotated.getY(), DELTA);
            assertEquals(0.0, rotated.getZ(), DELTA);
        }
        
        @Test
        @DisplayName("Rotate (0,1,0) 90° around X-axis should give (0,0,1)")
        void testRotateX_90Degrees() {
            Point3D rotated = unitY.rotateX(Math.PI / 2);
            
            assertEquals(0.0, rotated.getX(), DELTA);
            assertEquals(0.0, rotated.getY(), DELTA);
            assertEquals(1.0, rotated.getZ(), DELTA);
        }
        
        @Test
        @DisplayName("Rotate (0,0,1) 90° around Y-axis should give (1,0,0)")
        void testRotateY_90Degrees() {
            Point3D rotated = unitZ.rotateY(Math.PI / 2);
            
            assertEquals(1.0, rotated.getX(), DELTA);
            assertEquals(0.0, rotated.getY(), DELTA);
            assertEquals(0.0, rotated.getZ(), DELTA);
        }
        
        @Test
        @DisplayName("Rotate 360° should return to original position")
        void testRotate360Degrees() {
            Point3D original = new Point3D(1, 2, 3);
            Point3D rotated = original.rotateX(2 * Math.PI)
                                     .rotateY(2 * Math.PI)
                                     .rotateZ(2 * Math.PI);
            
            assertEquals(original.getX(), rotated.getX(), DELTA);
            assertEquals(original.getY(), rotated.getY(), DELTA);
            assertEquals(original.getZ(), rotated.getZ(), DELTA);
        }
        
        @Test
        @DisplayName("Rotate by 0 should not change point")
        void testRotateByZero() {
            Point3D rotated = point345.rotateX(0).rotateY(0).rotateZ(0);
            
            assertEquals(point345.getX(), rotated.getX(), DELTA);
            assertEquals(point345.getY(), rotated.getY(), DELTA);
            assertEquals(point345.getZ(), rotated.getZ(), DELTA);
        }
        
        @Test
        @DisplayName("Rotation should preserve magnitude")
        void testRotationPreservesMagnitude() {
            Point3D original = new Point3D(3, 4, 5);
            Point3D rotated = original.rotateX(0.7).rotateY(1.2).rotateZ(0.5);
            
            assertEquals(original.magnitude(), rotated.magnitude(), DELTA);
        }
        
        @Test
        @DisplayName("Rotate origin should remain at origin")
        void testRotateOrigin() {
            Point3D rotated = origin.rotateX(Math.PI / 3)
                                   .rotateY(Math.PI / 4)
                                   .rotateZ(Math.PI / 6);
            
            assertEquals(0.0, rotated.getX(), DELTA);
            assertEquals(0.0, rotated.getY(), DELTA);
            assertEquals(0.0, rotated.getZ(), DELTA);
        }
        
        @Test
        @DisplayName("Rotate 180° around X-axis should negate Y and Z")
        void testRotateX_180Degrees() {
            Point3D point = new Point3D(1, 2, 3);
            Point3D rotated = point.rotateX(Math.PI);
            
            assertEquals(1.0, rotated.getX(), DELTA);
            assertEquals(-2.0, rotated.getY(), DELTA);
            assertEquals(-3.0, rotated.getZ(), DELTA);
        }
        
        @Test
        @DisplayName("Rotate 180° around Y-axis should negate X and Z")
        void testRotateY_180Degrees() {
            Point3D point = new Point3D(1, 2, 3);
            Point3D rotated = point.rotateY(Math.PI);
            
            assertEquals(-1.0, rotated.getX(), DELTA);
            assertEquals(2.0, rotated.getY(), DELTA);
            assertEquals(-3.0, rotated.getZ(), DELTA);
        }
        
        @Test
        @DisplayName("Rotate 180° around Z-axis should negate X and Y")
        void testRotateZ_180Degrees() {
            Point3D point = new Point3D(1, 2, 3);
            Point3D rotated = point.rotateZ(Math.PI);
            
            assertEquals(-1.0, rotated.getX(), DELTA);
            assertEquals(-2.0, rotated.getY(), DELTA);
            assertEquals(3.0, rotated.getZ(), DELTA);
        }
        
        @Test
        @DisplayName("Four 90° rotations around same axis should equal 360°")
        void testFourQuarterRotations() {
            Point3D original = new Point3D(1, 2, 3);
            Point3D rotated = original.rotateX(Math.PI / 2)
                                     .rotateX(Math.PI / 2)
                                     .rotateX(Math.PI / 2)
                                     .rotateX(Math.PI / 2);
            
            assertEquals(original.getX(), rotated.getX(), DELTA);
            assertEquals(original.getY(), rotated.getY(), DELTA);
            assertEquals(original.getZ(), rotated.getZ(), DELTA);
        }
    }
    
    // ============================================================================
    // Vector Operations
    // ============================================================================
    
    @Nested
    @DisplayName("Vector Addition Tests")
    class AdditionTests {
        
        @Test
        @DisplayName("Add two positive vectors")
        void testAddPositive() {
            Point3D p1 = new Point3D(1, 2, 3);
            Point3D p2 = new Point3D(4, 5, 6);
            Point3D sum = p1.add(p2);
            
            assertEquals(5.0, sum.getX(), DELTA);
            assertEquals(7.0, sum.getY(), DELTA);
            assertEquals(9.0, sum.getZ(), DELTA);
        }
        
        @Test
        @DisplayName("Add positive and negative vectors")
        void testAddMixed() {
            Point3D p1 = new Point3D(5, -3, 2);
            Point3D p2 = new Point3D(-2, 4, -1);
            Point3D sum = p1.add(p2);
            
            assertEquals(3.0, sum.getX(), DELTA);
            assertEquals(1.0, sum.getY(), DELTA);
            assertEquals(1.0, sum.getZ(), DELTA);
        }
        
        @Test
        @DisplayName("Add zero vector should not change point")
        void testAddZero() {
            Point3D sum = point345.add(origin);
            
            assertEquals(point345.getX(), sum.getX(), DELTA);
            assertEquals(point345.getY(), sum.getY(), DELTA);
            assertEquals(point345.getZ(), sum.getZ(), DELTA);
        }
        
        @Test
        @DisplayName("Addition should be commutative")
        void testAddCommutative() {
            Point3D p1 = new Point3D(1, 2, 3);
            Point3D p2 = new Point3D(4, 5, 6);
            
            Point3D sum1 = p1.add(p2);
            Point3D sum2 = p2.add(p1);
            
            assertEquals(sum1.getX(), sum2.getX(), DELTA);
            assertEquals(sum1.getY(), sum2.getY(), DELTA);
            assertEquals(sum1.getZ(), sum2.getZ(), DELTA);
        }
        
        @Test
        @DisplayName("Addition should be associative")
        void testAddAssociative() {
            Point3D p1 = new Point3D(1, 2, 3);
            Point3D p2 = new Point3D(4, 5, 6);
            Point3D p3 = new Point3D(7, 8, 9);
            
            Point3D sum1 = p1.add(p2).add(p3);
            Point3D sum2 = p1.add(p2.add(p3));
            
            assertEquals(sum1.getX(), sum2.getX(), DELTA);
            assertEquals(sum1.getY(), sum2.getY(), DELTA);
            assertEquals(sum1.getZ(), sum2.getZ(), DELTA);
        }
        
        @Test
        @DisplayName("add should throw exception for null point")
        void testAddNull() {
            assertThrows(IllegalArgumentException.class, () -> {
                origin.add(null);
            });
        }
    }
    
    @Nested
    @DisplayName("Vector Subtraction Tests")
    class SubtractionTests {
        
        @Test
        @DisplayName("Subtract two vectors")
        void testSubtractBasic() {
            Point3D p1 = new Point3D(5, 7, 9);
            Point3D p2 = new Point3D(2, 3, 4);
            Point3D diff = p1.subtract(p2);
            
            assertEquals(3.0, diff.getX(), DELTA);
            assertEquals(4.0, diff.getY(), DELTA);
            assertEquals(5.0, diff.getZ(), DELTA);
        }
        
        @Test
        @DisplayName("Subtract point from itself should give zero vector")
        void testSubtractSelf() {
            Point3D diff = point345.subtract(point345);
            
            assertEquals(0.0, diff.getX(), DELTA);
            assertEquals(0.0, diff.getY(), DELTA);
            assertEquals(0.0, diff.getZ(), DELTA);
        }
        
        @Test
        @DisplayName("Subtract zero vector should not change point")
        void testSubtractZero() {
            Point3D diff = point345.subtract(origin);
            
            assertEquals(point345.getX(), diff.getX(), DELTA);
            assertEquals(point345.getY(), diff.getY(), DELTA);
            assertEquals(point345.getZ(), diff.getZ(), DELTA);
        }
        
        @Test
        @DisplayName("Subtraction should handle negative results")
        void testSubtractNegativeResult() {
            Point3D p1 = new Point3D(1, 2, 3);
            Point3D p2 = new Point3D(4, 5, 6);
            Point3D diff = p1.subtract(p2);
            
            assertEquals(-3.0, diff.getX(), DELTA);
            assertEquals(-3.0, diff.getY(), DELTA);
            assertEquals(-3.0, diff.getZ(), DELTA);
        }
        
        @Test
        @DisplayName("subtract should throw exception for null point")
        void testSubtractNull() {
            assertThrows(IllegalArgumentException.class, () -> {
                origin.subtract(null);
            });
        }
        
        @Test
        @DisplayName("Subtract is inverse of add")
        void testSubtractInverseOfAdd() {
            Point3D p1 = new Point3D(1, 2, 3);
            Point3D p2 = new Point3D(4, 5, 6);
            
            Point3D result = p1.add(p2).subtract(p2);
            
            assertEquals(p1.getX(), result.getX(), DELTA);
            assertEquals(p1.getY(), result.getY(), DELTA);
            assertEquals(p1.getZ(), result.getZ(), DELTA);
        }
    }
    
    @Nested
    @DisplayName("Scalar Multiplication Tests")
    class MultiplicationTests {
        
        @Test
        @DisplayName("Multiply by positive scalar")
        void testMultiplyPositive() {
            Point3D point = new Point3D(1, 2, 3);
            Point3D scaled = point.multiply(3.0);
            
            assertEquals(3.0, scaled.getX(), DELTA);
            assertEquals(6.0, scaled.getY(), DELTA);
            assertEquals(9.0, scaled.getZ(), DELTA);
        }
        
        @Test
        @DisplayName("Multiply by negative scalar")
        void testMultiplyNegative() {
            Point3D point = new Point3D(1, 2, 3);
            Point3D scaled = point.multiply(-2.0);
            
            assertEquals(-2.0, scaled.getX(), DELTA);
            assertEquals(-4.0, scaled.getY(), DELTA);
            assertEquals(-6.0, scaled.getZ(), DELTA);
        }
        
        @Test
        @DisplayName("Multiply by zero should give zero vector")
        void testMultiplyByZero() {
            Point3D scaled = point345.multiply(0.0);
            
            assertEquals(0.0, scaled.getX(), DELTA);
            assertEquals(0.0, scaled.getY(), DELTA);
            assertEquals(0.0, scaled.getZ(), DELTA);
        }
        
        @Test
        @DisplayName("Multiply by one should not change vector")
        void testMultiplyByOne() {
            Point3D scaled = point345.multiply(1.0);
            
            assertEquals(point345.getX(), scaled.getX(), DELTA);
            assertEquals(point345.getY(), scaled.getY(), DELTA);
            assertEquals(point345.getZ(), scaled.getZ(), DELTA);
        }
        
        @Test
        @DisplayName("Multiply by fraction should scale down")
        void testMultiplyByFraction() {
            Point3D point = new Point3D(4, 6, 8);
            Point3D scaled = point.multiply(0.5);
            
            assertEquals(2.0, scaled.getX(), DELTA);
            assertEquals(3.0, scaled.getY(), DELTA);
            assertEquals(4.0, scaled.getZ(), DELTA);
        }
        
        @Test
        @DisplayName("Multiply should scale magnitude")
        void testMultiplyScalesMagnitude() {
            Point3D point = new Point3D(3, 4, 0);
            double originalMag = point.magnitude();  // 5.0
            
            Point3D scaled = point.multiply(3.0);
            double scaledMag = scaled.magnitude();   // 15.0
            
            assertEquals(originalMag * 3.0, scaledMag, DELTA);
        }
    }
    
    @Nested
    @DisplayName("Dot Product Tests")
    class DotProductTests {
        
        @Test
        @DisplayName("Dot product of perpendicular vectors should be 0")
        void testDotProductPerpendicular() {
            assertEquals(0.0, unitX.dot(unitY), DELTA);
            assertEquals(0.0, unitY.dot(unitZ), DELTA);
            assertEquals(0.0, unitZ.dot(unitX), DELTA);
        }
        
        @Test
        @DisplayName("Dot product of parallel vectors")
        void testDotProductParallel() {
            Point3D p1 = new Point3D(2, 0, 0);
            assertEquals(2.0, unitX.dot(p1), DELTA);
        }
        
        @Test
        @DisplayName("Dot product should be commutative")
        void testDotProductCommutative() {
            Point3D p1 = new Point3D(1, 2, 3);
            Point3D p2 = new Point3D(4, 5, 6);
            
            assertEquals(p1.dot(p2), p2.dot(p1), DELTA);
        }
        
        @Test
        @DisplayName("Dot product calculation")
        void testDotProductCalculation() {
            Point3D p1 = new Point3D(1, 2, 3);
            Point3D p2 = new Point3D(4, 5, 6);
            
            // 1*4 + 2*5 + 3*6 = 4 + 10 + 18 = 32
            assertEquals(32.0, p1.dot(p2), DELTA);
        }
        
        @Test
        @DisplayName("Dot product with zero vector should be 0")
        void testDotProductWithZero() {
            assertEquals(0.0, point345.dot(origin), DELTA);
        }
        
        @Test
        @DisplayName("Dot product of vector with itself equals magnitude squared")
        void testDotProductWithSelf() {
            Point3D point = new Point3D(3, 4, 0);
            double dotSelf = point.dot(point);
            double magSquared = point.magnitude() * point.magnitude();
            
            assertEquals(magSquared, dotSelf, DELTA);
        }
        
        @Test
        @DisplayName("dot should throw exception for null point")
        void testDotProductNull() {
            assertThrows(IllegalArgumentException.class, () -> {
                origin.dot(null);
            });
        }
        
        @Test
        @DisplayName("Dot product with negative values")
        void testDotProductNegative() {
            Point3D p1 = new Point3D(1, 2, 3);
            Point3D p2 = new Point3D(-1, -2, -3);
            
            // 1*(-1) + 2*(-2) + 3*(-3) = -1 - 4 - 9 = -14
            assertEquals(-14.0, p1.dot(p2), DELTA);
        }
    }
    
    @Nested
    @DisplayName("Cross Product Tests")
    class CrossProductTests {
        
        @Test
        @DisplayName("Cross product of X and Y unit vectors should be Z")
        void testCrossProductXY() {
            Point3D cross = unitX.cross(unitY);
            
            assertEquals(0.0, cross.getX(), DELTA);
            assertEquals(0.0, cross.getY(), DELTA);
            assertEquals(1.0, cross.getZ(), DELTA);
        }
        
        @Test
        @DisplayName("Cross product of Y and Z unit vectors should be X")
        void testCrossProductYZ() {
            Point3D cross = unitY.cross(unitZ);
            
            assertEquals(1.0, cross.getX(), DELTA);
            assertEquals(0.0, cross.getY(), DELTA);
            assertEquals(0.0, cross.getZ(), DELTA);
        }
        
        @Test
        @DisplayName("Cross product of Z and X unit vectors should be Y")
        void testCrossProductZX() {
            Point3D cross = unitZ.cross(unitX);
            
            assertEquals(0.0, cross.getX(), DELTA);
            assertEquals(1.0, cross.getY(), DELTA);
            assertEquals(0.0, cross.getZ(), DELTA);
        }
        
        @Test
        @DisplayName("Cross product should be anti-commutative")
        void testCrossProductAntiCommutative() {
            Point3D p1 = new Point3D(1, 2, 3);
            Point3D p2 = new Point3D(4, 5, 6);
            
            Point3D cross1 = p1.cross(p2);
            Point3D cross2 = p2.cross(p1);
            
            assertEquals(cross1.getX(), -cross2.getX(), DELTA);
            assertEquals(cross1.getY(), -cross2.getY(), DELTA);
            assertEquals(cross1.getZ(), -cross2.getZ(), DELTA);
        }
        
        @Test
        @DisplayName("Cross product of parallel vectors should be zero")
        void testCrossProductParallel() {
            Point3D p1 = new Point3D(1, 2, 3);
            Point3D p2 = new Point3D(2, 4, 6);  // Parallel to p1
            
            Point3D cross = p1.cross(p2);
            
            assertEquals(0.0, cross.getX(), DELTA);
            assertEquals(0.0, cross.getY(), DELTA);
            assertEquals(0.0, cross.getZ(), DELTA);
        }
        
        @Test
        @DisplayName("Cross product of vector with itself should be zero")
        void testCrossProductWithSelf() {
            Point3D cross = point345.cross(point345);
            
            assertEquals(0.0, cross.getX(), DELTA);
            assertEquals(0.0, cross.getY(), DELTA);
            assertEquals(0.0, cross.getZ(), DELTA);
        }
        
        @Test
        @DisplayName("Cross product should be perpendicular to both inputs")
        void testCrossProductPerpendicular() {
            Point3D p1 = new Point3D(1, 2, 3);
            Point3D p2 = new Point3D(4, 5, 6);
            Point3D cross = p1.cross(p2);
            
            // Cross product should be perpendicular (dot product = 0)
            assertEquals(0.0, cross.dot(p1), DELTA);
            assertEquals(0.0, cross.dot(p2), DELTA);
        }
        
        @Test
        @DisplayName("cross should throw exception for null point")
        void testCrossProductNull() {
            assertThrows(IllegalArgumentException.class, () -> {
                origin.cross(null);
            });
        }
        
        @Test
        @DisplayName("Cross product calculation")
        void testCrossProductCalculation() {
            Point3D p1 = new Point3D(2, 3, 4);
            Point3D p2 = new Point3D(5, 6, 7);
            Point3D cross = p1.cross(p2);
            
            // (3*7 - 4*6, 4*5 - 2*7, 2*6 - 3*5) = (21-24, 20-14, 12-15) = (-3, 6, -3)
            assertEquals(-3.0, cross.getX(), DELTA);
            assertEquals(6.0, cross.getY(), DELTA);
            assertEquals(-3.0, cross.getZ(), DELTA);
        }
    }
    
    // ============================================================================
    // Magnitude and Normalization
    // ============================================================================
    
    @Nested
    @DisplayName("Magnitude Tests")
    class MagnitudeTests {
        
        @Test
        @DisplayName("Magnitude of (3,4,0) should be 5")
        void testMagnitudePythagorean() {
            Point3D point = new Point3D(3, 4, 0);
            assertEquals(5.0, point.magnitude(), DELTA);
        }
        
        @Test
        @DisplayName("Magnitude of (3,4,5) should be √50")
        void testMagnitude3D() {
            assertEquals(Math.sqrt(50), point345.magnitude(), DELTA);
        }
        
        @Test
        @DisplayName("Magnitude of unit vectors should be 1")
        void testMagnitudeUnitVectors() {
            assertEquals(1.0, unitX.magnitude(), DELTA);
            assertEquals(1.0, unitY.magnitude(), DELTA);
            assertEquals(1.0, unitZ.magnitude(), DELTA);
        }
        
        @Test
        @DisplayName("Magnitude of zero vector should be 0")
        void testMagnitudeZero() {
            assertEquals(0.0, origin.magnitude(), DELTA);
        }
        
        @Test
        @DisplayName("Magnitude should be non-negative")
        void testMagnitudeNonNegative() {
            Point3D point = new Point3D(-1, -2, -3);
            assertTrue(point.magnitude() >= 0);
        }
        
        @Test
        @DisplayName("Magnitude should equal distance from origin")
        void testMagnitudeEqualsDistanceFromOrigin() {
            assertEquals(point345.magnitude(), origin.distanceTo(point345), DELTA);
        }
    }
    
    @Nested
    @DisplayName("Normalization Tests")
    class NormalizationTests {
        
        @Test
        @DisplayName("Normalized vector should have magnitude 1")
        void testNormalizedMagnitude() {
            Point3D normalized = point345.normalize();
            assertEquals(1.0, normalized.magnitude(), DELTA);
        }
        
        @Test
        @DisplayName("Normalizing unit vector should not change it")
        void testNormalizeUnitVector() {
            Point3D normalized = unitX.normalize();
            
            assertEquals(unitX.getX(), normalized.getX(), DELTA);
            assertEquals(unitX.getY(), normalized.getY(), DELTA);
            assertEquals(unitX.getZ(), normalized.getZ(), DELTA);
        }
        
        @Test
        @DisplayName("Normalized vector should maintain direction")
        void testNormalizedDirection() {
            Point3D point = new Point3D(3, 4, 5);
            Point3D normalized = point.normalize();
            
            // Dot product with original should be positive (same direction)
            assertTrue(normalized.dot(point) > 0);
            
            // Cross product should be zero (parallel)
            Point3D cross = normalized.cross(point);
            assertEquals(0.0, cross.magnitude(), DELTA);
        }
        
        @Test
        @DisplayName("Normalizing zero vector should throw exception")
        void testNormalizeZeroVector() {
            assertThrows(ArithmeticException.class, () -> {
                origin.normalize();
            });
        }
        
        @Test
        @DisplayName("Normalize should preserve sign of components")
        void testNormalizePreservesSign() {
            Point3D point = new Point3D(-3, 4, -5);
            Point3D normalized = point.normalize();
            
            // Signs should match
            assertTrue(normalized.getX() < 0);
            assertTrue(normalized.getY() > 0);
            assertTrue(normalized.getZ() < 0);
        }
        
        @Test
        @DisplayName("Normalizing very small vector should not overflow")
        void testNormalizeSmallVector() {
            Point3D point = new Point3D(1e-100, 1e-100, 1e-100);
            Point3D normalized = point.normalize();
            
            assertEquals(1.0, normalized.magnitude(), DELTA);
        }
    }
    
    // ============================================================================
    // Interpolation and Midpoint
    // ============================================================================
    
    @Nested
    @DisplayName("Linear Interpolation Tests")
    class InterpolationTests {
        
        @Test
        @DisplayName("Lerp with t=0 should return start point")
        void testLerpAtZero() {
            Point3D start = new Point3D(1, 2, 3);
            Point3D end = new Point3D(4, 5, 6);
            Point3D result = start.lerp(end, 0.0);
            
            assertEquals(start.getX(), result.getX(), DELTA);
            assertEquals(start.getY(), result.getY(), DELTA);
            assertEquals(start.getZ(), result.getZ(), DELTA);
        }
        
        @Test
        @DisplayName("Lerp with t=1 should return end point")
        void testLerpAtOne() {
            Point3D start = new Point3D(1, 2, 3);
            Point3D end = new Point3D(4, 5, 6);
            Point3D result = start.lerp(end, 1.0);
            
            assertEquals(end.getX(), result.getX(), DELTA);
            assertEquals(end.getY(), result.getY(), DELTA);
            assertEquals(end.getZ(), result.getZ(), DELTA);
        }
        
        @Test
        @DisplayName("Lerp with t=0.5 should return midpoint")
        void testLerpAtHalf() {
            Point3D start = new Point3D(0, 0, 0);
            Point3D end = new Point3D(4, 6, 8);
            Point3D result = start.lerp(end, 0.5);
            
            assertEquals(2.0, result.getX(), DELTA);
            assertEquals(3.0, result.getY(), DELTA);
            assertEquals(4.0, result.getZ(), DELTA);
        }
        
        @Test
        @DisplayName("Lerp should interpolate linearly")
        void testLerpLinear() {
            Point3D start = new Point3D(0, 0, 0);
            Point3D end = new Point3D(10, 10, 10);
            Point3D result = start.lerp(end, 0.3);
            
            assertEquals(3.0, result.getX(), DELTA);
            assertEquals(3.0, result.getY(), DELTA);
            assertEquals(3.0, result.getZ(), DELTA);
        }
        
        @Test
        @DisplayName("Lerp with t>1 should extrapolate beyond end")
        void testLerpExtrapolateBeyondEnd() {
            Point3D start = new Point3D(0, 0, 0);
            Point3D end = new Point3D(1, 1, 1);
            Point3D result = start.lerp(end, 2.0);
            
            assertEquals(2.0, result.getX(), DELTA);
            assertEquals(2.0, result.getY(), DELTA);
            assertEquals(2.0, result.getZ(), DELTA);
        }
        
        @Test
        @DisplayName("Lerp with t<0 should extrapolate before start")
        void testLerpExtrapolateBeforeStart() {
            Point3D start = new Point3D(1, 1, 1);
            Point3D end = new Point3D(2, 2, 2);
            Point3D result = start.lerp(end, -1.0);
            
            assertEquals(0.0, result.getX(), DELTA);
            assertEquals(0.0, result.getY(), DELTA);
            assertEquals(0.0, result.getZ(), DELTA);
        }
        
        @Test
        @DisplayName("lerp should throw exception for null point")
        void testLerpNull() {
            assertThrows(IllegalArgumentException.class, () -> {
                origin.lerp(null, 0.5);
            });
        }
    }
    
    @Nested
    @DisplayName("Midpoint Tests")
    class MidpointTests {
        
        @Test
        @DisplayName("Midpoint of origin and (2,4,6) should be (1,2,3)")
        void testBasicMidpoint() {
            Point3D point = new Point3D(2, 4, 6);
            Point3D mid = origin.midpoint(point);
            
            assertEquals(1.0, mid.getX(), DELTA);
            assertEquals(2.0, mid.getY(), DELTA);
            assertEquals(3.0, mid.getZ(), DELTA);
        }
        
        @Test
        @DisplayName("Midpoint should be symmetric")
        void testMidpointSymmetry() {
            Point3D p1 = new Point3D(1, 2, 3);
            Point3D p2 = new Point3D(4, 5, 6);
            
            Point3D mid1 = p1.midpoint(p2);
            Point3D mid2 = p2.midpoint(p1);
            
            assertEquals(mid1.getX(), mid2.getX(), DELTA);
            assertEquals(mid1.getY(), mid2.getY(), DELTA);
            assertEquals(mid1.getZ(), mid2.getZ(), DELTA);
        }
        
        @Test
        @DisplayName("Midpoint of point with itself should be same point")
        void testMidpointWithSelf() {
            Point3D mid = point345.midpoint(point345);
            
            assertEquals(point345.getX(), mid.getX(), DELTA);
            assertEquals(point345.getY(), mid.getY(), DELTA);
            assertEquals(point345.getZ(), mid.getZ(), DELTA);
        }
        
        @Test
        @DisplayName("Distance from endpoints to midpoint should be equal")
        void testMidpointEqualDistance() {
            Point3D p1 = new Point3D(1, 2, 3);
            Point3D p2 = new Point3D(7, 8, 9);
            Point3D mid = p1.midpoint(p2);
            
            double dist1 = p1.distanceTo(mid);
            double dist2 = p2.distanceTo(mid);
            
            assertEquals(dist1, dist2, DELTA);
        }
        
        @Test
        @DisplayName("midpoint should throw exception for null point")
        void testMidpointNull() {
            assertThrows(IllegalArgumentException.class, () -> {
                origin.midpoint(null);
            });
        }
        
        @Test
        @DisplayName("Midpoint with negative coordinates")
        void testMidpointNegative() {
            Point3D p1 = new Point3D(-4, -6, -8);
            Point3D p2 = new Point3D(2, 4, 6);
            Point3D mid = p1.midpoint(p2);
            
            assertEquals(-1.0, mid.getX(), DELTA);
            assertEquals(-1.0, mid.getY(), DELTA);
            assertEquals(-1.0, mid.getZ(), DELTA);
        }
    }
    
    // ============================================================================
    // Equality and Hash Code
    // ============================================================================
    
    @Nested
    @DisplayName("Equality Tests")
    class EqualityTests {
        
        @Test
        @DisplayName("Point should equal itself")
        void testEqualsReflexive() {
            assertTrue(point345.equals(point345));
        }
        
        @Test
        @DisplayName("Equal points should be symmetric")
        void testEqualsSymmetric() {
            Point3D p1 = new Point3D(1, 2, 3);
            Point3D p2 = new Point3D(1, 2, 3);
            
            assertTrue(p1.equals(p2));
            assertTrue(p2.equals(p1));
        }
        
        @Test
        @DisplayName("Equality should be transitive")
        void testEqualsTransitive() {
            Point3D p1 = new Point3D(1, 2, 3);
            Point3D p2 = new Point3D(1, 2, 3);
            Point3D p3 = new Point3D(1, 2, 3);
            
            assertTrue(p1.equals(p2));
            assertTrue(p2.equals(p3));
            assertTrue(p1.equals(p3));
        }
        
        @Test
        @DisplayName("Point should not equal null")
        void testNotEqualsNull() {
            assertFalse(point345.equals(null));
        }
        
        @Test
        @DisplayName("Point should not equal object of different type")
        void testNotEqualsDifferentType() {
            assertFalse(point345.equals("not a point"));
        }
        
        @Test
        @DisplayName("Points with different coordinates should not be equal")
        void testNotEqualsDifferentCoordinates() {
            Point3D p1 = new Point3D(1, 2, 3);
            Point3D p2 = new Point3D(1, 2, 4);
            
            assertFalse(p1.equals(p2));
        }
        
        @Test
        @DisplayName("Points within epsilon should be equal")
        void testEqualsWithinEpsilon() {
            Point3D p1 = new Point3D(1.0, 2.0, 3.0);
            Point3D p2 = new Point3D(1.0 + 1e-11, 2.0, 3.0);  // Within EPSILON
            
            assertTrue(p1.equals(p2));
        }
        
        @Test
        @DisplayName("Points outside epsilon should not be equal")
        void testNotEqualsOutsideEpsilon() {
            Point3D p1 = new Point3D(1.0, 2.0, 3.0);
            Point3D p2 = new Point3D(1.0 + 1e-9, 2.0, 3.0);  // Outside EPSILON
            
            assertFalse(p1.equals(p2));
        }
    }
    
    @Nested
    @DisplayName("Hash Code Tests")
    class HashCodeTests {
        
        @Test
        @DisplayName("Equal points should have same hash code")
        void testHashCodeConsistency() {
            Point3D p1 = new Point3D(1, 2, 3);
            Point3D p2 = new Point3D(1, 2, 3);
            
            assertEquals(p1.hashCode(), p2.hashCode());
        }
        
        @Test
        @DisplayName("Hash code should be consistent across calls")
        void testHashCodeStability() {
            int hash1 = point345.hashCode();
            int hash2 = point345.hashCode();
            
            assertEquals(hash1, hash2);
        }
        
        @Test
        @DisplayName("Different points should likely have different hash codes")
        void testHashCodeDifference() {
            Point3D p1 = new Point3D(1, 2, 3);
            Point3D p2 = new Point3D(4, 5, 6);
            
            // Not guaranteed, but highly likely for good hash function
            assertNotEquals(p1.hashCode(), p2.hashCode());
        }
        
        @Test
        @DisplayName("Hash code should work in HashSet")
        void testHashCodeInHashSet() {
            java.util.HashSet<Point3D> set = new java.util.HashSet<>();
            Point3D p1 = new Point3D(1, 2, 3);
            Point3D p2 = new Point3D(1, 2, 3);
            
            set.add(p1);
            assertTrue(set.contains(p2));
        }
    }
    
    // ============================================================================
    // Edge Cases and Special Scenarios
    // ============================================================================
    
    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {
        
        @Test
        @DisplayName("Operations with very large numbers should not overflow")
        void testLargeNumbers() {
            Point3D large = new Point3D(1e100, 1e100, 1e100);
            Point3D result = large.add(large);
            
            assertTrue(Double.isFinite(result.getX()));
            assertTrue(Double.isFinite(result.getY()));
            assertTrue(Double.isFinite(result.getZ()));
        }
        
        @Test
        @DisplayName("Operations with very small numbers should maintain precision")
        void testSmallNumbers() {
            Point3D small = new Point3D(1e-100, 1e-100, 1e-100);
            Point3D result = small.add(small);
            
            assertTrue(result.getX() > 0);
            assertTrue(result.getY() > 0);
            assertTrue(result.getZ() > 0);
        }
        
        @Test
        @DisplayName("Mixed large and small numbers")
        void testMixedMagnitudes() {
            Point3D p1 = new Point3D(1e100, 1e-100, 1.0);
            Point3D p2 = new Point3D(1e-100, 1e100, 1.0);
            
            Point3D sum = p1.add(p2);
            assertTrue(Double.isFinite(sum.magnitude()));
        }
        
        @Test
        @DisplayName("Chains of operations should maintain accuracy")
        void testOperationChaining() {
            Point3D point = new Point3D(1, 1, 1);
            
            // Chain multiple operations
            Point3D result = point
                .add(new Point3D(1, 1, 1))
                .multiply(2.0)
                .subtract(new Point3D(1, 1, 1))
                .rotateZ(Math.PI / 2);
            
            assertTrue(Double.isFinite(result.magnitude()));
        }
        
        @Test
        @DisplayName("Rotation by very small angle should be nearly identity")
        void testSmallAngleRotation() {
            Point3D point = new Point3D(1, 2, 3);
            Point3D rotated = point.rotateX(1e-10);
            
            assertEquals(point.getX(), rotated.getX(), 1e-8);
            assertEquals(point.getY(), rotated.getY(), 1e-8);
            assertEquals(point.getZ(), rotated.getZ(), 1e-8);
        }
        
        @Test
        @DisplayName("Accumulated floating-point errors in rotations")
        void testAccumulatedRotationError() {
            Point3D point = new Point3D(1, 0, 0);
            Point3D rotated = point;
            
            // Rotate by small angle many times
            for (int i = 0; i < 1000; i++) {
                rotated = rotated.rotateZ(Math.PI / 500);
            }
            
            // Should complete full rotation (2π)
            // Some error expected due to floating-point accumulation
            assertEquals(point.getX(), rotated.getX(), 1e-6);
            assertEquals(point.getY(), rotated.getY(), 1e-6);
        }
    }
    
    // ============================================================================
    // String Representation
    // ============================================================================
    
    @Nested
    @DisplayName("String Representation Tests")
    class ToStringTests {
        
        @Test
        @DisplayName("toString should include all coordinates")
        void testToStringFormat() {
            Point3D point = new Point3D(1.5, 2.5, 3.5);
            String str = point.toString();
            
            assertTrue(str.contains("1.50"));
            assertTrue(str.contains("2.50"));
            assertTrue(str.contains("3.50"));
        }
        
        @Test
        @DisplayName("toString should handle negative values")
        void testToStringNegative() {
            Point3D point = new Point3D(-1, -2, -3);
            String str = point.toString();
            
            assertTrue(str.contains("-1"));
            assertTrue(str.contains("-2"));
            assertTrue(str.contains("-3"));
        }
        
        @Test
        @DisplayName("toString should be consistent")
        void testToStringConsistency() {
            String str1 = point345.toString();
            String str2 = point345.toString();
            
            assertEquals(str1, str2);
        }
    }
}