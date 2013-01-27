/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dheinrich.sandbox;

import darwin.geometrie.data.*;
import darwin.renderer.opengl.GLSLType;
import darwin.util.math.base.vector.*;

/**
 *
 * @author Daniel Heinrich <dannynullzwo@gmail.com>
 */
public final class SphereGenerator {

    private static final Element position = new Element(GLSLType.VEC3, "Position");
    private static final float CUBE_SIDE_LENGTH = 1;
    private final int sideCount;
    private final VertexBuffer buffer;
    private final int[] indices;
    private int accIndex = 0;

    public SphereGenerator(int tessFactor) {
        if (tessFactor < 1) {
            throw new IllegalArgumentException();
        }

        sideCount = 2 + (tessFactor - 1);
        int vertexCount = sideCount * sideCount * 6;
        vertexCount -= 2 * sideCount + 12;

        buffer = new VertexBuffer(position, vertexCount);
        indices = new int[6 * tessFactor * tessFactor * 2 * 3];//tf² = number of quads, * 2 = each quad has two tris, *3=each tris has 3 indices

        createSphere();
    }

    public VertexBuffer getVertexBuffer() {
        return buffer;
    }

    public int[] getIndices() {
        return indices;
    }

    private void createSphere() {
        float half = CUBE_SIDE_LENGTH * 0.5f;
        //top
        createPlane(new Vector3(-half, half, -half), true);
        //bottom
        createPlane(new Vector3(-half, -half, -half), false);

        //front
        createSidePlaneX(new Vector3(-half, -half, half), true);
        //back
        createSidePlaneX(new Vector3(-half, -half, -half), false);

        //left
        createSidePlaneY(new Vector3(-half, -half, -half), true);
        //right
        createSidePlaneY(new Vector3(half, -half, -half), false);
    }

    private void createPlane(ImmutableVector<Vector3> offset, boolean isTop) {
        for (int i = 0; i < sideCount; i++) {
            Vector3 side = new Vector3(0, 0, CUBE_SIDE_LENGTH);
            side.mul((float) i / (sideCount - 1)).add(offset);
            createHoriziontalRow(side);
        }
        createQuadsForSide(isTop);
    }

    private void createSidePlaneX(ImmutableVector<Vector3> offset, boolean isFront) {
        for (int i = 0; i < sideCount; i++) {
            Vector3 side = new Vector3(CUBE_SIDE_LENGTH, 0, 0);
            side.mul((float) i / (sideCount - 1)).add(offset);
            createVerticalRow(side);
        }
        createQuadsForSide(isFront);
    }

    private void createSidePlaneY(ImmutableVector<Vector3> offset, boolean isLeft) {
        for (int i = 1; i < sideCount - 1; i++) {
            Vector3 side = new Vector3(0, 0, CUBE_SIDE_LENGTH);
            side.mul((float) i / (sideCount - 1)).add(offset);
            createVerticalRow(side);
        }
        createQuadsForSide(isLeft);
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

    private void createQuadsForSide(boolean isCW) {
        int created = sideCount * sideCount;
        int first = buffer.getVcount() - created;
        for (int y = 0; y < sideCount - 1; y++) {
            for (int x = 0; x < sideCount - 1; x++) {
                int o = x + y * sideCount + first;
                addQuadIndices(!isCW, o, o + 1, o + sideCount + 1, o + sideCount);
            }
        }
    }

    private void addQuadIndices(boolean isCCW, int a, int b, int c, int d) {
        if (!isCCW) {
            int tmp = c;
            c = a;
            a = tmp;
        }
        addIndex(a);
        addIndex(b);
        addIndex(c);

        addIndex(c);
        addIndex(d);
        addIndex(a);
    }

    private void addIndex(int i) {
        indices[accIndex++] = i;
    }
}
