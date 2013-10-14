/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dheinrich.sandbox.meshstuff;

import darwin.geometrie.data.*;
import darwin.geometrie.data.GenericVector;
import darwin.geometrie.unpacked.*;
import darwin.util.logging.InjectLogger;
import darwin.util.math.base.vector.*;

import com.google.common.collect.*;
import javax.media.opengl.GL;
import org.slf4j.Logger;
import org.slf4j.helpers.NOPLogger;

import static darwin.geometrie.data.DataType.FLOAT;
import static darwin.geometrie.io.ModelReader.*;


/**
 *
 * @author Daniel Heinrich <dannynullzwo@gmail.com>
 */
public class NormalGenerator implements MeshModifier {

    @InjectLogger
    private Logger logger = NOPLogger.NOP_LOGGER;
    private static final Element position = new Element(new GenericVector(FLOAT, 3), POSITION_ATTRIBUTE);
    private static final Element normal = new Element(new GenericVector(FLOAT, 3), NORMAL_ATTRIBUTE);
    
    @Override
    public Mesh modifie(Mesh m) {
        VertexBuffer old = m.getVertices();
        DataLayout layout = old.layout;
        boolean hasattr = layout.hasElement(position);

        if (m.getPrimitiv_typ() != GL.GL_TRIANGLES || !hasattr) {
            logger.warn("Mesh can't be transformed,"
                        + " only Triangle Meshs allowed!");
            return m;
        }
        layout = new DataLayout(layout, normal);
        VertexBuffer vb = new VertexBuffer(layout, old.getVcount());
        vb.copyInto(0, old);

        int[] indice = m.getIndicies();

        Multimap<Integer, Integer> map = ArrayListMultimap.create(indice.length, 4);
        for (int i = 0; i < indice.length; i++) {
            map.put(indice[i], i / 3);
        }

        Vector3[] normals = new Vector3[indice.length / 3];
        for (int i = 0; i < m.getIndexCount(); i += 3) {
            Vertex v0 = vb.getVertex(indice[i]);
            Vertex v1 = vb.getVertex(indice[i + 1]);
            Vertex v2 = vb.getVertex(indice[i + 2]);
            normals[i / 3] = calcNormals(v0, v1, v2);
        }

        for (int i : indice) {
            Vertex vertex = vb.getVertex(i);

            Vector3 n = new Vector3();
            for (int t : map.get(i)) {
                n.add(normals[t]);
            }
            n.normalize();

            vertex.setAttribute(normal, float2Float(n.getCoords()));
        }

        return new Mesh(m.getIndicies(), vb, m.getPrimitiv_typ());
    }

    private Vector3 calcNormals(Vertex v0, Vertex v1, Vertex v2) {
        ImmutableVector<Vector3> p1 = getPosition(v0);
        ImmutableVector<Vector3> p2 = getPosition(v1);
        ImmutableVector<Vector3> p3 = getPosition(v2);

        Vector3 to1 = p1.clone().sub(p2);
        Vector3 to3 = p3.clone().sub(p2);

        return to3.cross(to1).normalize();
    }

    private Vector3 getPosition(Vertex v) {
        float[] pos = getFloat(v.getAttribute(position));
        return new Vector3(pos[0], pos[1], pos[2]);
    }

    private float[] getFloat(Number[] nb) {
        float[] ret = new float[nb.length];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = nb[i].floatValue();
        }
        return ret;
    }

    private Float[] float2Float(float[] arr) {
        Float[] ret = new Float[arr.length];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = arr[i];
        }
        return ret;
    }
}
