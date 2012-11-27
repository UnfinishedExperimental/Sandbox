/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dheinrich.sandbox;

import darwin.geometrie.data.Element;
import darwin.geometrie.data.VertexBuffer;
import darwin.renderer.opengl.GLSLType;
import darwin.util.math.base.vector.ImmutableVector;
import darwin.util.math.base.vector.Vector3;

/**
 *
 * @author Daniel Heinrich <dannynullzwo@gmail.com>
 */
public final class SphereGenerator {

    private static final Element position = new Element(GLSLType.VEC3, "Position");
    private static final float CUBE_SIDE_LENGTH = 1;
    private final int sideCount;
    private final VertexBuffer buffer;

    public SphereGenerator(int tessFactor) {
        if (tessFactor < 1) {
            throw new IllegalArgumentException();
        }

        sideCount = 2 + (tessFactor - 1);
        int vertexCount = sideCount * sideCount * 6;
        vertexCount -= 2 * sideCount + 12;

        buffer = new VertexBuffer(position, vertexCount);

        createSphere();
    }

    public VertexBuffer getVertexBuffer() {
        return buffer;
    }

    private void createSphere() {
        float half = CUBE_SIDE_LENGTH * 0.5f;
        //top
        createPlane(new Vector3(-half, half, -half));
        //bottom
        createPlane(new Vector3(-half, -half, -half));
        
        //front
        createSidePlaneX(new Vector3(-half, -half, half));
        //back
        createSidePlaneX(new Vector3(-half, -half, -half));
        
        //left
        createSidePlaneY(new Vector3(-half, -half, -half));
        //right
        createSidePlaneY(new Vector3(half, -half, -half));
    }

    private void createPlane(ImmutableVector<Vector3> offset) {
        for (int i = 0; i < sideCount; i++) {
            Vector3 side = new Vector3(0, 0, CUBE_SIDE_LENGTH);
            side.mul((float) i / (sideCount - 1)).add(offset);
            createHoriziontalRow(side);
        }
    }

    private void createSidePlaneX(ImmutableVector<Vector3> offset) {
        for (int i = 0; i < sideCount; i++) {
            Vector3 side = new Vector3(CUBE_SIDE_LENGTH, 0, 0);
            side.mul((float) i / (sideCount - 1)).add(offset);
            createVerticalRow(side);
        }
    }

    private void createSidePlaneY(ImmutableVector<Vector3> offset) {
        for (int i = 1; i < sideCount - 1; i++) {
            Vector3 side = new Vector3(0, 0, CUBE_SIDE_LENGTH);
            side.mul((float) i / (sideCount - 1)).add(offset);
            createVerticalRow(side);
        }
    }

    private void createHoriziontalRow(ImmutableVector<Vector3> offset) {
        for (int i = 0; i < sideCount; i++) {
            addVertex(new Vector3(CUBE_SIDE_LENGTH, 0, 0), i, offset);
        }
    }

    private void createVerticalRow(ImmutableVector<Vector3> offset) {
        for (int i = 1; i < sideCount - 1; i++) {
            addVertex(new Vector3(0, CUBE_SIDE_LENGTH, 0), i, offset);
        }
    }

    private void addVertex(Vector3 pos, int i, ImmutableVector<Vector3> offset) {
        Vector3 side = pos.mul((float) i / (sideCount - 1)).add(offset);
        side.normalize();        
        buffer.newVertex().setAttribute(position, side.getX(), side.getY(), side.getZ());
    }
}
